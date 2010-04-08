/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelResourceFilter;
import com.metamatrix.modeler.internal.core.workspace.ModelResourceImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * ResourceRefactorCommand is an abstract base class for RefactorCommands that need to operate on <code>IResource</code> objects.
 * It accomplishes many tasks common to most RefactorCommand implementations, such as finding dependent models, checking for
 * read-only resources that must be modified, and rebuilding the list of model imports.
 */
public abstract class ResourceRefactorCommand implements RefactorCommand {

    private static final ModelResourceFilter RESOURCE_FILTER = new ModelResourceFilter();

    /** IStatus code indicating that no target Resource has been set for this command before calling canExecute */
    public static final int ERROR_MISSING_RESOURCE = 1100;

    /** IStatus code indicating a model resource was not found */
    public static final int WARNING_REBUILD_IMPORTS = 1101;

    /** IStatus code indicating an exception occurred obtaining the model resource */
    public static final int ERROR_REBUILD_IMPORTS = 1102;

    /** IStatus code indicating that no target Resource has been set for this command before calling canExecute */
    public static final int REBUILD_IMPORTS_COMPLETE = 1103;

    /** IStatus code indicating that the required indexes could not build to search for the depenedent resources */
    public static final int ERROR_BUILD_INDEXES = 1104;

    /** IStatus code indicating that the required indexes to search for the depenedent resources succeeded */
    public static final int BUILD_INDEXES_COMPLETE = 1105;

    /** IStatus code indicating an exception occurred obtaining the model resource */
    public static final int ERROR_RENAMING_VDB = 1106;

    private static final boolean UNDO_REQUEST = true;

    private static final boolean NOT_UNDO_REQUEST = false;

    public static final String PID = ModelerCore.PLUGIN_ID;
    protected static final IStatus[] EMPTY_ISTATUS = new IStatus[0];

    // MyDefect : Added
    private String nameAfterRename;
    private String label;
    private IResource resource;
    private IResource modifiedResource;
    Collection dependentResources;
    private Collection dependencies;
    private List problems = new ArrayList();
    private OrganizeImportHandler handler;

    /**
     * Construct an instance of ResourceRefactorCommand.
     */
    public ResourceRefactorCommand( final String label ) {
        this.label = label;
        dependencies = Collections.EMPTY_LIST;
        dependentResources = Collections.EMPTY_LIST;
    }

    public void setResource( final IResource resource ) {
        this.resource = resource;
    }

    protected void setModifiedResource( final IResource resource ) {
        this.modifiedResource = resource;
    }

    protected IResource getResource() {
        return this.resource;
    }

    protected IResource getModifiedResource() {
        return this.modifiedResource;
    }

    public void setImportHandler( OrganizeImportHandler handler ) {
        this.handler = handler;
    }

    /**
     * Set the label of this command. Useful when the label contains the name of the resource that has been modified, which may
     * not be known at construction time.
     * 
     * @param label the label of this command, which may be pre-pended with "Undo " or "Redo " by the UI.
     */
    public void setLabel( final String label ) {
        this.label = label;
    }

    // MyDefect : 17255 Refactored,
    /**
     * This method moved from ResourceRenameCommand to base class so all the extending classes have access to it.
     * 
     * @since 4.2
     */
    public void setNewName( String name ) {
        this.nameAfterRename = name;
    }

    public String getNewName() {
        return this.nameAfterRename;
    }

    /**
     * This version of canExecute checks that the argument of <code>setResource</code> has been set and is not read-only. Either
     * of these conditions will generate an <code>IStatus</code> of severity <code>IStatus.ERROR</code>. If these conditions pass,
     * the return value will have severity of <code>IStatus.OK</code>
     * 
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#canExecute()
     * @return an IStatus
     */
    final public IStatus canExecute() {
        if (this.resource == null) {
            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.No_resource_has_been_selected"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_MISSING_RESOURCE, msg, null);
        } else if (ModelUtil.isIResourceReadOnly(resource)) {
            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Selection_is_read_only"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, ERROR_READONLY_RESOURCE, msg, null);
        }
        IStatus result = getCanExecuteStatus();
        if (result != null) {
            return result;
        }
        final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Ready_to_execute"); //$NON-NLS-1$
        return new Status(IStatus.OK, PID, CAN_EXECUTE, msg, null);
    }

    /**
     * Determine if all conditions necessary for this command to execute have been satisfied.
     * 
     * @return an IStatus with the appropriate severity and message for the pre-execution state of this command. The
     *         implementation may return null if it is okay to execute.
     */
    abstract protected IStatus getCanExecuteStatus();

    /* (non-Javadoc)
     * Subclasses must not implement behavior.
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    final public IStatus execute( IProgressMonitor monitor ) {

        problems.clear();

        try {
            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Execution_complete"); //$NON-NLS-1$
            IStatus result = new Status(IStatus.OK, PID, EXECUTE_SUCCEEDED, msg, null);
            final IStatus okayStatus = result;

            // To check the dependent resources, the index files of all the model resources
            // in the workspace are to be searched. So, generate the index files.
            // result = buildIndexes(monitor);

            if (result.getSeverity() == IStatus.ERROR) {
                return result;
            }

            // check the dependent resources
            int severity = checkDependentResources(NOT_UNDO_REQUEST);

            // see if we should modify the resource
            if (severity < IStatus.ERROR) {

                // tell the subclass to modify the resource
                result = modifyResource(this.getResource(), monitor);

                // if modification succeeded, refactor the dependent files
                if (result == null || result.getSeverity() < IStatus.ERROR) {
                    // Get the map of the refactored paths, that is old path mapped to the new refactored path
                    final Map refactoredPaths = getMovedResourcePathMap(false);
                    result = refactorDependentResources(monitor, refactoredPaths);
                }

            } else {
                final String msg2 = ModelerCore.Util.getString("ResourceRefactorCommand.Dependent_resource_error"); //$NON-NLS-1$
                return new Status(severity, PID, ERROR_READONLY_RESOURCE, msg2, null);
            }

            if (result.isOK()) {
                return okayStatus;
            }
            return result;
        } finally {
            if (monitor != null) monitor.done();
        }

    }

    /**
     * Perform the necessary modifications to the selected resource
     * 
     * @return an IStatus with the appropriate severity and message for the pre-execution state of this command.
     */
    abstract protected IStatus modifyResource( IResource resource,
                                               IProgressMonitor monitor );

    /**
     * checkDependentResources
     * 
     * @param isUndo
     * @return int
     * @since 4.3
     */
    protected int checkDependentResources( final boolean isUndo ) {

        // Determine dependent resource
        Collection searchResults = dependentResources(isUndo);
        ResourceStatusList statusList = new ResourceStatusList(searchResults);
        this.dependentResources = statusList.getResourceList();

        // Determine dependencies
        searchResults = dependencyResources(isUndo);
        statusList = new ResourceStatusList(searchResults, IStatus.OK);
        this.dependencies = statusList.getImportedResourceList();

        this.problems.addAll(statusList.getProblems());
        return statusList.getHighestSeverity();
    }

    /**
     * calculateDependentResources
     * 
     * @param isUndo
     * @return Collection
     * @since 4.3
     */
    private Collection dependentResources( final boolean isUndo ) {

        // search the workspace for any models that import anything beneath the path that is moving
        IResource res = resource;
        if (isUndo) {
            res = modifiedResource;
        }

        Collection dependentResource = dependentResources(res);

        // MyDefect : 17647 added to make sure no duplicate dependent resources.
        return checkForDuplicate(dependentResource);
    }

    /**
     * calculateDependentResources
     * 
     * @param res
     * @return Collection
     * @since 4.3
     */
    private static Collection dependentResources( IResource res ) {

        Collection dependentResources = new ArrayList();

        try {
            if (res instanceof IContainer) {
                // sometimes this is getting called with a nonexistent resourse...
                // see defect 18558 for more details
                if (res.exists()) {
                    IContainer folder = (IContainer)res;
                    IResource[] resources = folder.members();
                    dependentResources = new LinkedList();

                    for (int idx = 0; idx < resources.length; idx++) {
                        dependentResources.addAll(dependentResources(resources[idx]));
                    }
                } // endif
            } else {
                WorkspaceResourceFinderUtil.getResourcesThatUseRecursive(res, RESOURCE_FILTER, dependentResources);
            }
        } catch (CoreException ce) {
            ModelerCore.Util.log(ce);
            dependentResources = Collections.EMPTY_LIST;
        }

        return dependentResources;
    }

    /**
     * calculateDependencyResources
     * 
     * @param isUndo
     * @return Collection
     * @since 4.3
     */
    private Collection dependencyResources( final boolean isUndo ) {

        IResource res = resource;
        if (isUndo) {
            res = modifiedResource;
        }

        return dependencyResources(res);
    }

    /**
     * calculateDependencyResources
     * 
     * @param res
     * @return Collection
     * @since 4.3
     */
    private static Collection dependencyResources( IResource res ) {

        Collection dependencyResource = Collections.EMPTY_LIST;

        try {
            if (res instanceof IContainer) {
                IContainer folder = (IContainer)res;
                IResource[] resources = folder.members();
                dependencyResource = new LinkedList();

                for (int idx = 0; idx < resources.length; idx++) {
                    dependencyResource.addAll(dependencyResources(resources[idx]));
                }

            } else {
                dependencyResource = Arrays.asList(WorkspaceResourceFinderUtil.getDependentResources(res));
                // ModelWorkspaceSearch search = new ModelWorkspaceSearch();
                // String path = res.getFullPath().toString();
                // dependencyResource = search.getResourcesImportedByModel(path);

            }
        } catch (CoreException ce) {
            ModelerCore.Util.log(ce);
            dependencyResource = Collections.EMPTY_LIST;
        }

        return dependencyResource;
    }

    /**
     * @param dependentResource
     * @return Collection
     * @since 4.3
     */
    private Collection checkForDuplicate( Collection dependentResource ) {

        if (dependentResource.size() <= 1) return dependentResource;

        Set uniqueSet = new HashSet();

        for (Iterator iter = dependentResource.iterator(); iter.hasNext();) {
            IResource nextResource = (IResource)iter.next();
            uniqueSet.add(nextResource);
        }

        return new ArrayList(uniqueSet);
    }

    /**
     * Obtain a list of resources that are dependent upon the target resource for this object.
     * 
     * @return
     */
    protected Collection getModifiedDependentResources() {
        return dependentResources(getModifiedResource());
    }

    /**
     * Obtain a list of resources that are dependent upon the target resource for this object.
     * 
     * @return
     */
    // protected static Collection getDependentResources(IResource res) {
    // Collection rv;
    //
    // if (res instanceof IContainer) {
    // // defect 12382 - if a folder type, we need to scan all resources underneath:
    // IContainer cont = (IContainer) res;
    // IResource[] mems = EMPTY_IRESOURCE_ARRAY;
    // try {
    // mems = cont.members();
    // } catch (CoreException ex) {
    //                String message = ModelerCore.Util.getString("ResourceRefactorCommand.getDependentResourcesProblemMessage", res.getFullPath());  //$NON-NLS-1$
    // ModelerCore.Util.log(IStatus.ERROR, ex, message);
    // } // endtry
    //            
    // if (mems.length > 0) {
    // rv = new HashSet();
    // for (int i = 0; i < mems.length; i++) {
    // IResource subres = mems[i];
    // rv.addAll(getDependentResources(subres));
    // } // endfor
    // } else {
    // // no children, or exception:
    // rv = Collections.EMPTY_SET;
    // } // endif
    // } else {
    // // search the workspace for any models that import anything beneath the path that is moving
    // Collection colDependentResources = WorkspaceResourceFinderUtil.getResourcesThatUse(res, RESOURCE_FILTER);
    //            
    // // build a ResourceStatusList from the search results
    // ResourceStatusList statusList = new ResourceStatusList(colDependentResources);
    // rv = statusList.getResourceList();
    // } // endif
    //        
    // return rv;
    // }
    /**
     * Rebuilds the model import list and fixes broken references for all model files that use this command's resource.
     * 
     * @param monitor
     * @return
     */
    protected IStatus refactorDependentResources( IProgressMonitor monitor,
                                                  final Map refactoredPaths ) {
        Collection errorList = new ArrayList();
        int severity = IStatus.OK;

        final ModelEditor editor = ModelerCore.getModelEditor();
        // defect 16804 - make sure we need to rebuild imports:
        if (shouldRebuildImports()) {
            for (final Iterator iter = this.dependentResources.iterator(); iter.hasNext();) {
                IFile modelFile = (IFile)iter.next();
                try {
                    ModelResource modelResource = editor.findModelResource(modelFile);
                    if (modelResource != null) {
                        if (modelResource.getResource() == null || !modelResource.getResource().exists()) {
                            continue;
                        }
                        // defect 16804 - should not try to refactor read-only dependent resources:
                        if (!modelResource.isReadOnly()) {
                            rebuildImports(modelResource, monitor, refactoredPaths);
                            regenerateUserSql(modelResource, monitor, refactoredPaths);
                            modelResource.save(null, false);

                            // Send notification for transformation roots to invalidate any transformation cache
                            final Resource resrc = modelResource.getEmfResource();
                            if (resrc instanceof EmfResource) {
                                final List xformations = ((EmfResource)resrc).getModelContents().getTransformations();
                                for (final Iterator rootIter = xformations.iterator(); rootIter.hasNext();) {
                                    final TransformationMappingRoot root = (TransformationMappingRoot)rootIter.next();
                                    final Notification notification = new ENotificationImpl(
                                                                                            (InternalEObject)root,
                                                                                            Notification.SET,
                                                                                            TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TARGET,
                                                                                            refactoredPaths.keySet(),
                                                                                            refactoredPaths.values());
                                    root.eNotify(notification);
                                }
                            }
                        } // endif -- readonly
                    } else {
                        if (severity < IStatus.WARNING) {
                            severity = IStatus.WARNING;
                        }
                        final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Model_resource_not_in_ws", modelFile.getName()); //$NON-NLS-1$
                        errorList.add(new Status(IStatus.WARNING, PID, WARNING_REBUILD_IMPORTS, msg, null));
                    }

                } catch (ModelWorkspaceException e) {
                    severity = IStatus.ERROR;
                    final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Exception_finding_model_resource", modelFile.getName()); //$NON-NLS-1$
                    errorList.add(new Status(IStatus.ERROR, PID, ERROR_REBUILD_IMPORTS, msg, e));
                }
            }
        } // endif

        // Added as partial fix to Defect 15916
        validateDependentResources();

        // Update imports within model being refactored.
        final Map map = new HashMap();
        for (final Iterator iter = this.dependencies.iterator(); iter.hasNext();) {
            final IFile file = (IFile)iter.next();
            final String path = file.getFullPath().toString();
            map.put(path, path);
        }
        if (!map.isEmpty()) {
            try {
                ModelResource model;

                // defect 18462 - sometimes this resource is a folder, not a file.
                if (resource instanceof IFile) {
                    model = editor.findModelResource((IFile)this.resource);
                } else {
                    // we were not a file, so no modelRes exists:
                    model = null;
                } // endif

                if (model != null && model.isXsd()) {
                    final Resource oldEmfResrc = model.getEmfResource();
                    // MyDefect : 16368
                    final XSDSchema xsdSchema = ((XSDResourceImpl)oldEmfResrc).getSchema();
                    if (xsdSchema != null) {
                        final Iterator oldIter = xsdSchema.getContents().iterator();
                        model = editor.findModelResource((IFile)this.modifiedResource);
                        final Resource emfResrc = model.getEmfResource();
                        final Iterator iter = ((XSDResourceImpl)emfResrc).getSchema().getContents().iterator();
                        while (oldIter.hasNext()) {
                            // For each of the EObjects, get the import Aspect
                            final EObject oldEObj = (EObject)oldIter.next();
                            final EObject eObj = (EObject)iter.next();
                            // Skip annotations
                            if (oldEObj instanceof XSDAnnotation) {
                                continue;
                            }
                            // Break if named component found since all imports, includes, and redefines must appear before these
                            if (oldEObj instanceof XSDNamedComponent) {
                                break;
                            }
                            final ImportsAspect importsAspect = AspectManager.getModelImportsAspect(oldEObj);
                            if (importsAspect != null) {

                                if (oldEObj instanceof XSDSchemaDirective) {
                                    Container cntr = ModelerCore.getContainer(emfResrc);
                                    Resource refResource = cntr.getResourceFinder().findByImport((XSDSchemaDirective)oldEObj,
                                                                                                 true);
                                    if (refResource != null) {
                                        importsAspect.setModelLocation(eObj, refResource.getURI());
                                        emfResrc.setModified(true);
                                    }

                                }

                                // // If imports Aspect is not null, get the import path
                                // final IPath importPath = importsAspect.getModelPath(oldEObj);
                                // if (importPath != null) {
                                // final String refactoredPath = (String)map.get(importPath.makeAbsolute().toString());
                                // // If the import path has been changed
                                // if (refactoredPath != null) {
                                // // unload the old refactored model resource (since its not unloaed during refactoring of the
                                // physical file)
                                // importsAspect.setModelPath(eObj, new Path(refactoredPath));
                                // emfResrc.setModified(true);
                                // }
                                // }
                            }
                        }
                    }
                }
            } catch (final ModelWorkspaceException err) {
                severity = IStatus.ERROR;
                final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Exception_finding_model_resource", //$NON-NLS-1$
                                                              this.resource.getName());
                errorList.add(new Status(IStatus.ERROR, PID, ERROR_REBUILD_IMPORTS, msg, err));
            }
        }

        // defect 16076 - display the correct text on completion, and display all errors
        String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Execution_complete"); //$NON-NLS-1$
        MultiStatus multiStatus = new MultiStatus(PID, REBUILD_IMPORTS_COMPLETE, (IStatus[])errorList.toArray(EMPTY_ISTATUS),
                                                  msg, null);
        if (!multiStatus.isOK()) {
            msg = ModelerCore.Util.getString("ResourceRefactorCommand.Dependent_resource_error"); //$NON-NLS-1$
            multiStatus = new MultiStatus(PID, ERROR_REBUILD_IMPORTS, (IStatus[])errorList.toArray(EMPTY_ISTATUS), msg, null);
        }
        return multiStatus;
    }

    /*
     * This method calls the appropriate validate method to re-index the dependent resources.
     * Though the resources are saved, some modeler components will think it's Dirty, for instance. DEFECT 15916 partial fix.
     */
    protected void validateDependentResources() {
        if (!dependentResources.isEmpty()) {
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) {
                    Container cont = null;
                    try {
                        cont = ModelerCore.getModelContainer();
                    } catch (CoreException err) {
                        String message = ModelerCore.Util.getString("ResourceRefactorCommand.doGetContainerProblemMessage"); //$NON-NLS-1$
                        ModelerCore.Util.log(IStatus.ERROR, err, message);
                    }
                    ModelBuildUtil.validateResources(null, dependentResources, cont, false);
                    return null;
                }
            };
            // Execute the validation within a transaction as this operation may open resources
            // and create new EObjects
            try {
                ModelerCore.getModelEditor().executeAsTransaction(runnable, "Updating ModelIndexes", false, false, this); //$NON-NLS-1$
            } catch (CoreException err) {
                ModelerCore.Util.log(err);
            }
        }
    }

    protected IStatus rebuildImports( ModelResource modelResource,
                                      IProgressMonitor monitor,
                                      Map refactoredPaths ) throws ModelWorkspaceException {
        final OrganizeImportCommand importCommand = new OrganizeImportCommand();
        importCommand.setResource(modelResource.getEmfResource());

        // The OrganizeImportCommand has to be provided with a Map of old and new paths
        // of resources that are affected by refactoring.
        importCommand.setRefactoredPaths(refactoredPaths);
        importCommand.setHandler(this.handler);
        IStatus status = importCommand.canExecute();
        if (status.isOK()) {
            status = importCommand.execute(monitor);
        }
        return status;
    }

    protected IStatus regenerateUserSql( ModelResource modelResource,
                                         IProgressMonitor monitor,
                                         Map refactoredPaths ) throws ModelWorkspaceException {
        final Resource r = modelResource.getEmfResource();
        // If the model resource being represents a virtual model with transformations ...
        if (r instanceof EmfResource && ((EmfResource)r).getModelType() == ModelType.VIRTUAL_LITERAL) {

            // Ensure that this model resource is loaded so that we can retrieve and update its contents
            if (!r.isLoaded()) {
                Map options = (r.getResourceSet() != null ? r.getResourceSet().getLoadOptions() : Collections.EMPTY_MAP);
                try {
                    r.load(options);
                } catch (IOException e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                    return new Status(IStatus.ERROR, PID, 0, e.getLocalizedMessage(), e);
                }
            }

            // Process all transformations in the TransformationContainer
            final List transformations = ((EmfResource)r).getModelContents().getTransformations();
            for (Iterator i = transformations.iterator(); i.hasNext();) {
                EObject eObj = (EObject)i.next();
                if (eObj instanceof SqlTransformationMappingRoot) {
                    SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)eObj;
                    SqlTransformation helper = (SqlTransformation)mappingRoot.getHelper();
                    SqlTransformation nested = null;
                    if (helper != null) {
                        for (Iterator j = helper.getNested().iterator(); j.hasNext();) {
                            eObj = (EObject)j.next();
                            if (eObj instanceof SqlTransformation) {
                                nested = (SqlTransformation)eObj;
                            }
                        }
                    }
                    if (nested != null) {
                        try {
                            // Use all resources in the workspace container to
                            List eResources = new ArrayList(ModelerCore.getModelContainer().getResources());

                            // Convert select SQL
                            String uuidFormSql = helper.getSelectSql();
                            String convertedSql = null;
                            if (!CoreStringUtil.isEmpty(uuidFormSql)) {
                                convertedSql = SqlStringConverter.convertUUIDsToFullNames(uuidFormSql, eResources);
                                nested.setSelectSql(convertedSql);
                            }

                            // Convert insert SQL
                            uuidFormSql = helper.getInsertSql();
                            if (!CoreStringUtil.isEmpty(uuidFormSql)) {
                                convertedSql = SqlStringConverter.convertUUIDsToFullNames(uuidFormSql, eResources);
                                nested.setInsertSql(convertedSql);
                            }

                            // Convert update SQL
                            uuidFormSql = helper.getUpdateSql();
                            if (!CoreStringUtil.isEmpty(uuidFormSql)) {
                                convertedSql = SqlStringConverter.convertUUIDsToFullNames(uuidFormSql, eResources);
                                nested.setUpdateSql(convertedSql);
                            }

                            // Convert delete SQL
                            uuidFormSql = helper.getDeleteSql();
                            if (!CoreStringUtil.isEmpty(uuidFormSql)) {
                                convertedSql = SqlStringConverter.convertUUIDsToFullNames(uuidFormSql, eResources);
                                nested.setDeleteSql(convertedSql);
                            }
                        } catch (CoreException e) {
                            ModelerCore.Util.log(IStatus.ERROR, e, e.getLocalizedMessage());
                            return new Status(IStatus.ERROR, PID, 0, e.getLocalizedMessage(), e);
                        }
                    }
                }
            }
        }
        final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.user_sql_string_regenerated_successfully"); //$NON-NLS-1$
        return new Status(IStatus.OK, PID, 0, msg, null);
    }

    /* (non-Javadoc)
     * Default implementation returns true.  Override to return false.
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#canUndo()
     */
    public boolean canUndo() {
        if (anyResourceReadOnly()) return false;

        return true;
    }

    /* (non-Javadoc)
     * Default implementation returns true.  Override to return false.
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#canRedo()
     */
    public boolean canRedo() {
        if (anyResourceReadOnly()) return false;

        return true;
    }

    /* (non-Javadoc)
     * Returns an empty list.  Subclasses may override to implement
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getResult()
     */
    public Collection getResult() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * Returns an empty list.  Subclasses may override to implement
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getAffectedObjects()
     */
    public Collection getAffectedObjects() {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getLabel()
     */
    public String getLabel() {
        return label;
    }

    /* (non-Javadoc)
     * Returns the label property.  Subclasses may override to implement.
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getDescription()
     */
    public String getDescription() {
        return label;
    }

    /* (non-Javadoc)
     * Returns an empty list.  Subclasses may override to implement
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getPostExecuteMessages()
     */
    public Collection getPostExecuteMessages() {
        return this.problems;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#redo(org.eclipse.core.runtime.IProgressMonitor)
     */
    final public IStatus redo( IProgressMonitor monitor ) {
        problems.clear();

        int severity = checkDependentResources(NOT_UNDO_REQUEST);
        if (severity >= IStatus.ERROR) {
            final String msg2 = ModelerCore.Util.getString("ResourceRefactorCommand.Dependent_resource_error"); //$NON-NLS-1$
            return new Status(severity, PID, ERROR_READONLY_RESOURCE, msg2, null);
        }

        redoResourceModification(monitor);
        final Map refactoredPaths = getMovedResourcePathMap(false);
        return refactorDependentResources(monitor, refactoredPaths);
    }

    /**
     * Re-apply the resource modification. Typically called after undo. Subclasses that do not support redo may no-op.
     * 
     * @param monitor
     */
    abstract protected IStatus redoResourceModification( IProgressMonitor monitor );

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#undo(org.eclipse.core.runtime.IProgressMonitor)
     */
    final public IStatus undo( IProgressMonitor monitor ) {
        problems.clear();

        int severity = checkDependentResources(UNDO_REQUEST);
        if (severity >= IStatus.ERROR) {
            final String msg2 = ModelerCore.Util.getString("ResourceRefactorCommand.Dependent_resource_error"); //$NON-NLS-1$
            return new Status(severity, PID, ERROR_READONLY_RESOURCE, msg2, null);
        }

        undoResourceModification(monitor);
        final Map refactoredPaths = getMovedResourcePathMap(true);
        return refactorDependentResources(monitor, refactoredPaths);
    }

    /**
     * Undo the resource modification. Can be called either after execute or redo. Subclasses that do not support undo may no-op.
     * 
     * @param monitor
     */
    abstract protected IStatus undoResourceModification( IProgressMonitor monitor );

    /**
     * Allow subclasses to add problems to the list
     * 
     * @param problem
     */
    protected void addProblem( IStatus problem ) {
        this.problems.add(problem);
        // swjTODO: figure out what the severity is and, if ERROR, return ERROR from execute's IStatus.
    }

    protected void unloadModelResources( IResource resource ) throws CoreException {
        // Collect all IResources within all IProjects
        ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();
        resource.accept(visitor);
        for (Iterator iter = visitor.getModelResources().iterator(); iter.hasNext();) {
            ModelResource mResource = (ModelResource)iter.next();

            // MyDefect : 16368
            // if (!mResource.isXsd()) {
            // Do not unload xsd model resources, as they are used for getting the imports aspect
            mResource.unload();
            mResource.close();
            if (mResource instanceof ModelResourceImpl) {
                ((ModelResourceImpl)mResource).removeEmfResource();
            }
            // }
        }

        // The resources move/rename will trigger the event that will actually remove and create
        // the corresponding resources, since these too are workspace management events
        // they are processed after the refactoring is done. But since we need the index files at
        // the old path to be deleted and the index files at the new path to be created,
        // we do it explicitly.

        // Delete the index files corresponding to the model resource at the old path
        for (Iterator iter = visitor.getResources().iterator(); iter.hasNext();) {
            IResource tmpResource = (IResource)iter.next();

            if (ModelUtil.isModelFile(tmpResource) && tmpResource.getLocation() != null) {
                // Remove the runtime index file associated with the resource being removed
                String runtimeIndexFileName = IndexUtil.getRuntimeIndexFileName(tmpResource);
                File runtimeIndexFile = new File(IndexUtil.INDEX_PATH, runtimeIndexFileName);
                if (!runtimeIndexFile.delete()) {
                    runtimeIndexFile.deleteOnExit();
                }

                // Remove the search index file associated with the resource being removed
                String searchIndexFileName = IndexUtil.getSearchIndexFileName(tmpResource);
                File searchIndexFile = new File(IndexUtil.INDEX_PATH, searchIndexFileName);
                if (!searchIndexFile.delete()) {
                    searchIndexFile.deleteOnExit();
                }
            }
        }
    }

    protected IStatus buildIndexes( final IProgressMonitor monitor,
                                    final Collection iResources ) {
        try {
            // If there are models with unsaved changes create temporary index files
            // for use in query validation and resolution
            final TransactionRunnable runnable = new TransactionRunnable() {
                public Object run( final UnitOfWork uow ) {
                    ModelBuildUtil.indexResources(null, iResources);
                    return null;
                }
            };
            // Execute the indexing within a transaction as this operation may open resources
            // and create new EObjects
            ModelerCore.getModelEditor().executeAsTransaction(runnable, "Updating ModelIndexes", false, false, this); //$NON-NLS-1$            
        } catch (Exception e) {
            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Exception_building_indexes"); //$NON-NLS-1$            
            return new Status(IStatus.ERROR, PID, ERROR_REBUILD_IMPORTS, msg, e);
        }
        final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Building_indexes_complete"); //$NON-NLS-1$
        return new Status(IStatus.OK, PID, BUILD_INDEXES_COMPLETE, msg, null);
    }

    protected IStatus buildIndexes( final IProgressMonitor monitor,
                                    IResource iResource ) {
        Collection iResources = new ArrayList(1);
        iResources.add(iResource);
        return buildIndexes(monitor, iResources);
    }

    protected boolean anyResourceReadOnly() {
        return !getReadOnlyDependentResources().isEmpty();
    }

    public List getReadOnlyDependentResources() {
        // TODO: consider caching this value as long as the resource hasn't changed
        IResource res = getModifiedResource();
        if (res == null) {
            res = getResource();
        } // endif
        return getReadOnlyDependentResources(res);
    }

    protected static List getReadOnlyDependentResources( IResource res ) {
        // We need to check all resources to see if any have been changed to read-only
        List rv = new ArrayList();
        ModelResource mr = null;

        try {
            mr = ModelUtil.getModel(res);
        } catch (Exception err) {
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
        }

        if (mr != null && mr.isReadOnly()) {
            rv.add(mr);
        }

        // Iterator iter = getDependentResources(res).iterator();
        Iterator iter = dependentResources(res).iterator();
        while (iter.hasNext()) {
            IResource dres = (IResource)iter.next();
            try {
                mr = ModelUtil.getModel(dres);
                if (mr != null && mr.isReadOnly()) {
                    rv.add(dres);
                }
            } catch (ModelWorkspaceException err) {
                ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
            }
        }

        return rv;
    }

    /**
     * Obtain a map containing the path of any ModelResource that is being changed by this operation. The key values shall be the
     * String path of the ModelResource before the move, thus enabling lookup for Model Imports that need to change. This method
     * will be called following <code>modifyResource</code>, and <code>redoResourceModification</code>. The method will also be
     * called following <code>undoResourceModification</code>, in which case the key/value entries in the map must be the reverse
     * of the map returned following <code>modifyResource</code>.
     * 
     * @param isUndo true if this operation is being undone.
     * @return
     */
    protected abstract Map getMovedResourcePathMap( boolean isUndo );

    /** Indicate whether we should attempt to rebuild imports */
    protected boolean shouldRebuildImports() {
        return true;
    }

    /**
     * @since 4.2
     */
    public Collection getDependentResources() {
        return this.dependentResources;
    }
}
