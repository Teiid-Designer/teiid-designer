/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.id.ObjectID;
import org.teiid.core.designer.plugin.PluginUtilities;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.Stopwatch;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.container.DuplicateResourceException;
import org.teiid.designer.core.index.ModelIndexer;
import org.teiid.designer.core.index.ModelSearchIndexer;
import org.teiid.designer.core.index.ResourceIndexer;
import org.teiid.designer.core.refactor.OrganizeImportCommand;
import org.teiid.designer.core.refactor.OrganizeImportHandler;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspace;
import org.teiid.designer.core.workspace.ModelWorkspaceException;


/**
 * This class was derived from ModelBuildUtil class in order to streamline the validation of models that are specifically part of of a Vdb.
 * 
 * In particular, a VDB doesn't care about Search indexes, so the ModelSearchIndexer isn't needed and is not added to INDEXERS list.
 * 
 * Also, a synchronizing flag is accessible via start() and stop() methods so a VDB can notify the builder that indexed or validated resources should
 * be cached so any attempt to re-index or re-validate will be ignored. This can be a performance hog if not enabled in the VDB's synchronize ALL process.
 *
 * @since 8.0
 */
@SuppressWarnings("javadoc")
public class VdbModelBuilder {
    
	public static final int MONITOR_TASK_NAME_MAX_LENGTH = 200;
    public static final String TASK_NAME_TRUNCTATION_SUFFIX = ModelerCore.Util.getString("ModelBuildUtil.taskNameTruncationSuffix"); //$NON-NLS-1$	

    public static final String MONITOR_RESOURCE_VALIDATION_MSG = ModelerCore.Util.getString("ModelBuildUtil.Validating_Resource__1"); //$NON-NLS-1$
    public static final String MONITOR_OBJECT_VALIDATION_MSG = ModelerCore.Util.getString("ModelBuildUtil.Validating__2"); //$NON-NLS-1$	

    private static Collection VALIDATORS;

    private static Collection INDEXERS;
    
    private Collection<IResource> indexedResources;
    private Collection<IResource> builtResources;
    private boolean synchronizing = false;

    static {
        // initialize the validators collection
        initValidators();
        // initialize the indexers collection
        initIndexers();
    }
    

    public VdbModelBuilder() {
    	super();
    	this.indexedResources = new ArrayList<IResource>();
    	this.builtResources = new ArrayList<IResource>();
    }
    
    public void start() {
    	this.synchronizing = true;
    	this.indexedResources.clear();
    	this.builtResources.clear();
    }
    
    public void stop() {
    	this.synchronizing = false;
    	this.indexedResources.clear();
    	this.builtResources.clear();
    }

    /**
     * Index and validate the collection of IResources being passed. Validation of the resources can be within the context of the
     * resources being passed in. The validation rules that use the context may use this information.
     * 
     * @param monitor The progressMonitor, may be null
     * @param resources Collection of IResouurces
     * @param validateInContext If true the validationContext is updated with the collection of resources.
     * @since 4.2
     */
    public void buildResources( final IProgressMonitor monitor,
                                       final Collection iResources,
                                       final Container container,
                                       final boolean validateInContext ) {

        // Get the list of "modified" EMF resources within the model container
        // prior to performing the build. Since indexing and validation are
        // considered read-only operations we must make sure that any resources
        // that are loaded as a result of these operations have a status of
        // not modified
        final List modifiedResources = getModifiedResources();


        // index all the resources
        indexResources(monitor, iResources);

        // Reset the modified state of all EMF resources to what it was
        // prior to indexing. This needs to be done since indexing may
        // mark an XSD resource as modified causing the XSDResourceValidator
        // to not validate it.
        setModifiedResources(modifiedResources);

        // validate all the resources
        validateResources(monitor, iResources, container, validateInContext);
//        validateResources(monitor, dependentModels, container, validateInContext);

        // Reset the modified state of all EMF resources to what it was prior to validating
        setModifiedResources(modifiedResources);
    }

    // ############################################################################################################################
    // # Validation Methods #
    // ############################################################################################################################

    /**
     * Deletes all markers for the supplied list of IResources
     * 
     * @param iResources
     * @since 5.0.2
     */
    public void clearResourceMarkers( final Collection iResources ) {
        try {
            // clear all markers on this resource
            final Iterator itr = iResources.iterator();

            while (itr.hasNext()) {
                final IResource iResource = (IResource)itr.next();
                if (iResource.exists()) {
                    iResource.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
                } // endif
            }
        } catch (final CoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    /**
     * Deletes all markers for the supplied IResource
     * 
     * @param iResource
     * @since 5.0.2
     */
    public void clearResourceMarkers( final IResource iResource ) {
        try {
            // clear all markers on this resource, as we are
            // going to create fresh markets
            // defect 16537 - make sure resource exists before deleting markers
            if (iResource.exists()) {
                iResource.deleteMarkers(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
            } // endif
        } catch (final CoreException e) {
            ModelerCore.Util.log(e);
        }
    }

    public void createModelIndexes( final IProgressMonitor monitor,
                                           final Collection resources ) {
        final ModelIndexer modelIndexer = new ModelIndexer();
        indexResources(monitor, resources, modelIndexer);
    }

    /**
     * Create a new instance of ValidationContext to use during validation
     */
    public ValidationContext createValidationContext() {
        ValidationContext context = null;
        final Plugin corePlugin = ModelerCore.getPlugin();
        if (corePlugin != null && !ModelerCore.ignoreValidationPreferencesOnBuild()) {
            context = new ValidationContext(ModelerCore.PLUGIN_ID);
        } else {
            // non plugin environment
            context = new ValidationContext();
        }
        return context;
    }

    public List getModifiedResources() {
        try {
            return getModifiedResources(ModelerCore.getModelContainer().getResources());
        } catch (final CoreException e) {
            // do nothing - the method will return an empty list
        }
        return Collections.EMPTY_LIST;
    }

    public List getModifiedResources( final List eResources ) {
        final List result = new ArrayList();
        if (eResources != null) {
            for (final Iterator iter = eResources.iterator(); iter.hasNext();) {
                final Resource resource = (Resource)iter.next();
                if (resource.isModified()) {
                    result.add(resource);
                }
            }
        }
        return result;
    }

    /**
     * @param workspace
     * @return
     * @throws CoreException
     * @since 4.3
     */
    private Resource[] getWorkspaceResourcesInScope( final ModelWorkspace workspace ) throws CoreException {
        final Resource[] wsEmfResources = workspace.getEmfResources();
        final Collection resourcesInScope = new ArrayList(Arrays.asList(wsEmfResources));

        // MyCode : 18565
        updateResource(resourcesInScope);

        return (Resource[])resourcesInScope.toArray(new Resource[resourcesInScope.size()]);
    }

    public void indexResource( IProgressMonitor monitor,
                                      final IResource iResource,
                                      final ResourceIndexer indexer ) {
    	if( this.synchronizing && this.indexedResources.contains(iResource) ) {
    		return;
    	}
    	
    	//System.out.println("    >>> VdbModelBuilder.indexResource() = " + iResource.getName());
    	// create a monitor if needed
        monitor = monitor != null ? monitor : new NullProgressMonitor();
        if (monitor.isCanceled()) {
            return;
        }
        final String sTask = ModelerCore.Util.getString("ModelBuildUtil.Creating_{0}_for_{1}_1", indexer.getIndexType(), iResource.getFullPath()); //$NON-NLS-1$
        //System.out.println("[ModelBuildUtil.indexResource TaskName is: " + sTask ); //$NON-NLS-1$
        monitor.setTaskName(sTask);
        try {
        	this.indexedResources.add(iResource);
            final Stopwatch totalWatch = new Stopwatch();
            totalWatch.start();
            indexer.indexResource(iResource, false, true);
            totalWatch.stop();
        } catch (final Throwable e) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 e,
                                 ModelerCore.Util.getString("ModelBuilder.Error_indexing_model_resource_3", iResource.getFullPath())); //$NON-NLS-1$
        }
    }

    public void indexResources( IProgressMonitor monitor,
                                       final Collection resources ) {
        // create a monitor if needed
        monitor = monitor != null ? monitor : new NullProgressMonitor();
        // get all indexers and index
        for (final Iterator indexerIter = INDEXERS.iterator(); indexerIter.hasNext();) {
            final ResourceIndexer indexer = (ResourceIndexer)indexerIter.next();
            indexResources(monitor, resources, indexer);
        }
    }

    public void indexResources( final IProgressMonitor monitor,
                                       final Collection resources,
                                       final ResourceIndexer indexer ) {
        for (final Iterator rsourceIter = resources.iterator(); rsourceIter.hasNext();) {
            final IResource resource = (IResource)rsourceIter.next();
            indexResource(monitor, resource, indexer);
            if (monitor != null) {
                monitor.worked(1);
            }
        }
    }

    private static void initIndexers() {
        // Find all extensions of the notifiers extension point
        final String id = ModelerCore.EXTENSION_POINT.RESOURCE_INDEXER.UNIQUE_ID;
        final IExtension[] extensions = PluginUtilities.getExtensions(id);
        // initialize the indexers array
        INDEXERS = new ArrayList(extensions.length);
        for (int i = 0; i < extensions.length; ++i) {
            final IExtension extension = extensions[i];
            final String element = ModelerCore.EXTENSION_POINT.RESOURCE_INDEXER.ELEMENTS.CLASS;
            final String attribute = ModelerCore.EXTENSION_POINT.RESOURCE_INDEXER.ATTRIBUTES.NAME;
            try {
                final Object instance = PluginUtilities.createExecutableExtension(extension, element, attribute);
                if (instance instanceof ResourceIndexer ) {
                	if( !(instance instanceof ModelSearchIndexer) ) {
                		INDEXERS.add(instance);
                	}
                } else {
                    final String message = ModelerCore.Util.getString("ModelBuildUtil.Extension_class_not_instance_of_IIndexer_1"); //$NON-NLS-1$
                    ModelerCore.Util.log(message);
                }
            } catch (final CoreException e) {
                ModelerCore.Util.log(e);
            }
        }
    }

    // ############################################################################################################################
    // # Indexing Methods #
    // ############################################################################################################################

    private static void initValidators() {
        // Find all extensions of the notifiers extension point
        final String id = ModelerCore.EXTENSION_POINT.RESOURCE_VALIDATOR.UNIQUE_ID;
        final IExtension[] extensions = PluginUtilities.getExtensions(id);

        // initialize the validators array
        VALIDATORS = new ArrayList(extensions.length);
        for (int i = 0; i < extensions.length; ++i) {
            final IExtension extension = extensions[i];
            final String element = ModelerCore.EXTENSION_POINT.RESOURCE_VALIDATOR.ELEMENTS.CLASS;
            final String attribute = ModelerCore.EXTENSION_POINT.RESOURCE_VALIDATOR.ATTRIBUTES.NAME;
            try {
                final Object instance = PluginUtilities.createExecutableExtension(extension, element, attribute);
                if (instance instanceof ResourceValidator) {
                    VALIDATORS.add(instance);
                } else {
                    final String message = ModelerCore.Util.getString("ModelBuildUtil.Extension_class_not_instance_of_ResourceValidator_1"); //$NON-NLS-1$
                    ModelerCore.Util.log(message);
                }
            } catch (final CoreException e) {
                ModelerCore.Util.log(e);
            }
        }
    }

    private void internalValidateResource( final IProgressMonitor monitor,
                                                  final IResource iResource,
                                                  final ResourceValidator validator,
                                                  final ValidationContext context,
                                                  final boolean clearMarkers ) {
    	if( this.synchronizing && this.builtResources.contains(iResource) ) {
    		return;
    	}
    	
    	//System.out.println("    >>> VdbModelBuilder.internalValidateResource() = " + iResource.getName());
        // create a monitor if needed
        final IProgressMonitor progresssMonitor = (monitor != null ? monitor : new NullProgressMonitor());

        if (progresssMonitor.isCanceled() || !validator.isValidatorForObject(iResource)) {
            return;
        }

        // path to the resource in the workspace
        progresssMonitor.setTaskName(MONITOR_RESOURCE_VALIDATION_MSG + iResource.getFullPath());

        // See if the model was marked as a duplicate ...
        Object duplicateOfModel = null;
        try {
            duplicateOfModel = iResource.getSessionProperty(ModelerCore.DUPLICATE_MODEL_OF_IPATH_KEY);
        } catch (final CoreException err) {
            // Do nothing; treat as tho not a duplicate ...
        }

        if (clearMarkers) {
            clearResourceMarkers(iResource);
        }

        // Try to validate; if this is the first time this resource is opened, the duplicate model
        // session property might not be assigned, and a ModelWorkspaceException may be thrown when
        // opening the model
        if (duplicateOfModel == null) {
        	this.builtResources.add(iResource);
            try {
                // Validate the model ...
                final ModelWorkspace workspace = ModelerCore.getModelWorkspace();
                final ModelResource mResource = workspace.findModelResource(iResource);

                // Find the Resource for the given IResource (unless IResource is VDB Resource)
                // VDB IResources do not have a corresponding Emf Resource.
                Resource resource = null;
                if (!ModelUtil.isVdbArchiveFile(iResource)) {
                    try {
                        if (mResource != null && mResource.getEmfResource() != null) {
                            resource = mResource.getEmfResource();
                        } else {
                            // Force a load if it not already loaded.
                            if (iResource.getRawLocation() != null) {
                                final URI uri = URI.createFileURI(iResource.getRawLocation().toString());
                                resource = ModelerCore.getModelContainer().getResource(uri, true);
                                if (resource != null) {
                                    resource.setModified(false);
                                }
                            }
                        }
                    } catch (final Exception e) {
                        // Do nothing. IResources that do not wrap an emf Resource will throw an exception here...
                        // Let the validator decide what to do if no emf Resource can be found.
                    }
                }
                final Object objToValidate = (resource != null ? (Object)resource : (Object)iResource);

                final Stopwatch totalWatch = new Stopwatch();
                totalWatch.start();
                validator.validate(progresssMonitor, objToValidate, context);
                totalWatch.stop();

                validator.addMarkers(context, iResource);
                context.clearResults();
            } catch (final ModelerCoreException e) {
                final Throwable underlyingException = e.getException();
                if (underlyingException instanceof DuplicateResourceException) {
                    // Look again for the duplicate of model path ...
                    try {
                        duplicateOfModel = iResource.getSessionProperty(ModelerCore.DUPLICATE_MODEL_OF_IPATH_KEY);
                    } catch (final CoreException err) {
                        ModelerCore.Util.log(err);
                    }
                } else {
                    ModelerCore.Util.log(e);
                }
            }
        }

        // Now handle the case when this is a duplicate model ...

        if (duplicateOfModel != null) {
            try {
                final Object[] params = new Object[] {duplicateOfModel};
                final String msg = ModelerCore.Util.getString("ModelBuildUtil.ModelDuplicateOf_0", params); //$NON-NLS-1$
                // The Model is a duplicate, so don't validate ...
                final IMarker marker = iResource.createMarker(IMarker.PROBLEM);
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.LOCATION, duplicateOfModel);
                marker.setAttribute(ModelerCore.MARKER_URI_PROPERTY, null);
                marker.setAttribute(ModelerCore.TARGET_MARKER_URI_PROPERTY, null);
                marker.setAttribute(IMarker.MESSAGE, msg);
            } catch (final CoreException e) {
                ModelerCore.Util.log(e);
            }
        }
    }

    /**
     * Basic Update Imports utility method.
     * 
     * @param modelResource
     * @param includeDiagramReferences
     * @return
     * @throws ModelWorkspaceException
     * @since 5.0.2
     */
    public boolean rebuildImports( final Resource resource,
                                          final boolean includeDiagramReferences ) {
        if (resource != null) {
            final ModelResource mr = ModelerCore.getModelEditor().findModelResource(resource);

            if (mr != null && !mr.isReadOnly()) {
                final OrganizeImportHandler handler = new OrganizeImportHandler() {
                    @Override
					public Object choose( final List options ) {
                        return null;
                    }
                };
                final OrganizeImportCommand importCommand = new OrganizeImportCommand();
                importCommand.setResource(resource);
                importCommand.setHandler(handler);
                importCommand.setIncludeDiagramReferences(includeDiagramReferences);
                final IProgressMonitor monitor = null; // for now
                final IStatus status = importCommand.canExecute();
                if (status.isOK()) {
                    final IStatus runStatus = importCommand.execute(monitor);

                    if (!runStatus.isOK()) {
                        ModelerCore.Util.log(runStatus);
                        return false;
                    }
                    return true;
                }
                ModelerCore.Util.log(status);
            }
        }
        return false;
    }

    public void setModifiedResources( final List modifiedResources ) {
        try {
            final Container container = ModelerCore.getModelContainer();

            for (final Object element : container.getResources()) {
                final Resource resource = (Resource)element;
                if (modifiedResources != null && modifiedResources.contains(resource)) {
                    resource.setModified(true);
                } else {
                    resource.setModified(false);
                }
            }
        } catch (final CoreException theException) {
            ModelerCore.Util.log(IStatus.ERROR, theException, theException.getMessage());
        }
    }

    /**
     * updates the collection if the systemVdbResources are not already in the collection
     * 
     * @param resourcesInScope
     * @since 4.3
     */
    private void updateResource( final Collection resourcesInScope ) {
        final Resource[] systemVdbResources = ModelerCore.getSystemVdbResources();
        for (final Resource systemVdbResource : systemVdbResources) {
            if (systemVdbResource instanceof EmfResource) {
                final ObjectID objectID = ((EmfResource)systemVdbResource).getUuid();
                try {
                    final Resource resrc = ModelerCore.getModelContainer().getResourceFinder().findByUUID(objectID, false);
                    if (resrc != null && !resourcesInScope.contains(resrc)) {
                        resourcesInScope.add(systemVdbResource);
                    }
                } catch (final CoreException err) {
                    ModelerCore.Util.log(err);
                }
            } else {
                resourcesInScope.add(systemVdbResource);
            }
        }
    }

    public void validateResource( final IProgressMonitor monitor,
                                         final IResource iResource,
                                         final ResourceValidator validator,
                                         final ValidationContext context ) {
        internalValidateResource(monitor, iResource, validator, context, true);
    }

    /**
     * Validate the {@link org.eclipse.emf.ecore.resource.Resource} using the specified
     * 
     * @param monitor
     * @param eResource
     * @param validator
     * @param context
     * @since 4.2
     */
    private void validateResource( final IProgressMonitor monitor,
                                          final Resource eResource,
                                          final ResourceValidator validator,
                                          final ValidationContext context ) {
        CoreArgCheck.isNotNull(eResource);
        CoreArgCheck.isNotNull(validator);
        CoreArgCheck.isNotNull(context);

        // create a monitor if needed
        final IProgressMonitor progresssMonitor = (monitor != null ? monitor : new NullProgressMonitor());
        if (monitor.isCanceled() || !validator.isValidatorForObject(eResource)) {
            return;
        }

        monitor.setTaskName(MONITOR_RESOURCE_VALIDATION_MSG + eResource.getURI().lastSegment());
        final boolean isModified = eResource.isModified();
        try {
            final Stopwatch totalWatch = new Stopwatch();
            totalWatch.start();
            validator.validate(progresssMonitor, eResource, context);
            totalWatch.stop();
        } catch (final ModelerCoreException e) {
            ModelerCore.Util.log(e);
        } finally {
            // Restore the "modified" state to what is was prior to validation
            eResource.setModified(isModified);
        }
    }

    /**
     * Validate the {@link org.eclipse.emf.ecore.resource.Resource} returning the result in the supplied ValidationContext instance.
     * After validation is complete the user should clear the ValidationContext to free up memory.
     * 
     * @param monitor The progressMonitor, may be null
     * @param eResource the resource to validate
     * @param context the validation context to use
     * @since 4.2
     */
    public void validateResource( final IProgressMonitor monitor,
                                         final Resource eResource,
                                         final ValidationContext context ) {
        CoreArgCheck.isNotNull(eResource);
        CoreArgCheck.isNotNull(context);

        // get all validators and validate
        for (final Iterator validateIter = VALIDATORS.iterator(); validateIter.hasNext();) {
            final ResourceValidator validator = (ResourceValidator)validateIter.next();
            validateResource(monitor, eResource, validator, context);
        }
    }

    /**
     * Validate the collection of IResources being passed. Validation of the resources can be within the context of the resources
     * being passed in. The validation rules that use the context may use this information.
     * 
     * @param monitor The progressMonitor, may be null
     * @param resources Collection of IResouurces
     * @param validateInContext If true the validationContext is updated with the collection of resources.
     * @since 4.2
     */
    public void validateResources( final IProgressMonitor monitor,
                                          final Collection iResources,
                                          final Container container,
                                          final boolean validateInContext ) {
        // create a monitor if needed
        final IProgressMonitor progresssMonitor = (monitor != null ? monitor : new NullProgressMonitor());

        // Create the validation context to use
        final ValidationContext context = createValidationContext();

        // set the resource scope (all model resources in open model projects)
        final ModelWorkspace workspace = ModelerCore.getModelWorkspace();

        try {
            final Resource[] resourcesInScope = getWorkspaceResourcesInScope(workspace);
            context.setResourcesInScope(resourcesInScope);

        } catch (final CoreException theException) {
            ModelerCore.Util.log(theException);
        }

        // set the resources on the context since validation
        // is in context of the resources.
        if (validateInContext && (iResources != null)) {
            final List temp = new ArrayList();
            final Iterator itr = iResources.iterator();

            while (itr.hasNext()) {
                final IResource iResource = (IResource)itr.next();

                if (ModelUtil.isModelFile(iResource)) {
                    try {
                        final ModelResource modelResource = ModelerCore.getModelEditor().findModelResource((IFile)iResource);

                        if (modelResource != null) {
                            temp.add(modelResource.getEmfResource());
                        }
                    } catch (final ModelWorkspaceException theException) {
                        ModelerCore.Util.log(theException);
                    }
                }
            }

            context.setResourcesToValidate((Resource[])temp.toArray(new Resource[temp.size()]));
        }

        context.setResourceContainer(container); // may be null

        clearResourceMarkers(iResources);

        // get all validators and validate
        for (final Iterator validateIter = VALIDATORS.iterator(); validateIter.hasNext();) {
            final ResourceValidator validator = (ResourceValidator)validateIter.next();
            validator.validationStarted(iResources, context);
            try {
                for (final Iterator rsourceIter = iResources.iterator(); rsourceIter.hasNext();) {
                    final IResource resource = (IResource)rsourceIter.next();
                    internalValidateResource(progresssMonitor, resource, validator, context, false);
                }
            } finally {
                validator.validationEnded(context);
            }
        }

        // clear the context after validation to free up memory
        context.clearState();
    }

    /**
     * Validate the collection of {@link org.eclipse.emf.ecore.resource.Resource} instances returning the result in the supplied
     * ValidationContext instance. After validation is complete the user should clear the ValidationContext to free up memory.
     * 
     * @param monitor The progressMonitor, may be null
     * @param eResources the collection of resources to validate
     * @param context the validation context to use
     * @since 4.2
     */
    public void validateResources( final IProgressMonitor monitor,
                                          final Resource[] eResources,
                                          final ValidationContext context ) {
        CoreArgCheck.isNotNull(eResources);
        CoreArgCheck.isNotNull(context);

        // create a monitor if needed
        final IProgressMonitor progresssMonitor = (monitor != null ? monitor : new NullProgressMonitor());

        // set the resources on the context since validation is in context of the resources.
        context.setResourcesToValidate(eResources);

        // get all validators and validate
        for (int i = 0; i < eResources.length; ++i) {
            validateResource(progresssMonitor, eResources[i], context);
        }
    }

    // #############################################################################
    // # Protected methods
    // #############################################################################

}

