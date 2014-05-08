/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.Vdb.Event.MODEL_TRANSLATOR;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_VISIBLE;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ResourceFinder;
import org.teiid.designer.core.index.Index;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.core.translators.TranslatorPropertyDefinition;
import org.teiid.designer.core.util.VdbHelper;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.metamodels.function.FunctionPlugin;
import org.teiid.designer.metamodels.function.ScalarFunction;
import org.teiid.designer.metamodels.function.extension.FunctionModelExtensionConstants;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalPlugin;
import org.teiid.designer.metamodels.relational.extension.RelationalModelExtensionConstants;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.ProblemElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.Severity;
import org.teiid.designer.vdb.manifest.SourceElement;


/**
 *
 *
 * @since 8.0
 */
@ThreadSafe
public final class VdbModelEntry extends VdbEntry {

    private static final String INDEX_FOLDER = "runtime-inf/"; //$NON-NLS-1$
    private static final String EMPTY_STR = StringUtilities.EMPTY_STRING;
    
    /**
     * @param path the model path (may not be <code>null</code>)
     * @return the default name to use as the source name (never <code>null</code>)
     */
    public static String createDefaultSourceName(IPath path) {
        return path.removeFileExtension().lastSegment();
    }

    private final String indexName;
    private String modelUuid;
    private final Set<Problem> problems = new HashSet<Problem>();
    private final AtomicBoolean visible = new AtomicBoolean(true);
    private final CopyOnWriteArraySet<VdbEntry> imports = new CopyOnWriteArraySet<VdbEntry>();
    private final CopyOnWriteArraySet<VdbModelEntry> importedBy = new CopyOnWriteArraySet<VdbModelEntry>();
    private final CopyOnWriteArraySet<String> importVdbNames = new CopyOnWriteArraySet<String>();
    private final CopyOnWriteArraySet<VdbFileEntry> udfJars = new CopyOnWriteArraySet<VdbFileEntry>();
    private final String modelClass;
    private final boolean builtIn;
    private final String type;
    private final VdbSourceInfo sourceInfo;
    private transient ModelElement element;

    /**
     * Constructs a model entry and adds it to the specified VDB. <strong>Callers of this method should call
     * {@link #synchronizeModelEntry(IProgressMonitor)} immediately after constructing the model entry.</strong>
     * 
     * @param vdb the VDB where the resource is be added to (may not be <code>null</code>)
     * @param name the resource path (may not be <code>null</code>)
     * @param monitor the progress monitor or <code>null</code>
     */
    VdbModelEntry( final Vdb vdb,
                   final IPath name,
                   final IProgressMonitor monitor ) {
        super(vdb, name, monitor);
        indexName = IndexUtil.getRuntimeIndexFileName(findFileInWorkspace());
        final Resource model = findModel();
        builtIn = getFinder().isBuiltInResource(model);
        modelClass = findModelClass(model);
        sourceInfo = new VdbSourceInfo(vdb);
        if (ModelUtil.isXmiFile(model)) {
        	final ModelResource mr = ModelerCore.getModelEditor().findModelResource(model);
            final EmfResource emfModel = (EmfResource)model;
            type = emfModel.getModelType().getName();
            modelUuid = ModelUtil.getUuidString(mr);

            // TODO: Backing out the auto-set visibility to FALSE for physical models (Preview won't work)
            // visible.set(false);
            // TODO: re-visit in 7.1
            // For now, we're removing the assumption that the user will want to seed the VDB model entry with the model's
            // Description. From a UI standpoint, if the description contains multiple lines, then the row height
            // is way too high. User can always copy/paste from model to VDB AND the description for a model is always
            // available in the model itself.
            // if (emfModel.getModelAnnotation().getDescription() != null)
            // description.set(emfModel.getModelAnnotation().getDescription());
            if (ModelUtil.isPhysical(model)) {
                final String defaultName = createDefaultSourceName(name);
                final ConnectionInfoHelper helper = new ConnectionInfoHelper();
                String translator = helper.getTranslatorName(mr);
                if( translator == null ) translator = EMPTY_STR;
                
                // Jndi defaults to source name, unless the property is found in the model.
                String jndiName = defaultName;
                String jndiProp = helper.getJndiProperty(mr);
                if(!CoreStringUtil.isEmpty(jndiProp)) {
                	jndiName = jndiProp;
                }

                sourceInfo.add(defaultName, jndiName, translator);
                Properties translatorProps = helper.getTranslatorOverrideProperties(mr);
                if( !translatorProps.isEmpty() ) {
                	updateTranslatorOverrides(translatorProps);
                }
            }
            // TODO: Backing out the auto-set visibility to FALSE for physical models (Preview won't work)
            // if( ModelUtil.isVirtual(emfModel) ) {
            visible.set(true);
            // }
        } else {
        	type = VdbUtil.OTHER;
        }
        // TODO: Fix This ???
        /*
        if (this.translator.get() == null) {
            this.translator.set(EMPTY_STR);
        }
        */
        if (this.description.get() == null) {
            this.description.set(EMPTY_STR);
        }
    }

    private void updateUdfJars(Resource model) {
        if(model==null) return;

        final ModelResource mdlResrc = ModelerCore.getModelEditor().findModelResource(model);
        if (mdlResrc == null) return;

        udfJars.clear();
        
        // Find available udf jar resources in the project
        IProject project = mdlResrc.getModelProject().getProject();
        List<IResource> jarResources = VdbHelper.getUdfJarResources(project);
        
        // Get all scalar functions in the Model, then update the jarPaths
        List<EObject> children = model.getContents();
        for(EObject eObj: children) {
            // Look for ScalarFunctions and Procedure in View Model with function=true
            String udfJarPath = null;
            if(eObj instanceof ScalarFunction) {
                udfJarPath = getUdfJarPath((ScalarFunction)eObj);
            } else if(eObj instanceof Procedure && ((Procedure)eObj).isFunction()) {
                udfJarPath = getUdfJarPath((Procedure)eObj);
            }
            if(udfJarPath!=null && udfJarPath.trim().length()!=0) {
                // Go thru available jar resources
                for(IResource jarResource: jarResources) {
                    if(jarResource instanceof IFile) {
                        IPath path = ((IFile)jarResource).getProjectRelativePath();
                        if(path.toString().equals(udfJarPath)) {
                            udfJars.add(new VdbFileEntry(getVdb(), jarResource.getFullPath(), VdbFileEntry.FileEntryType.UDFJar, new NullProgressMonitor()));
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Get the Udf jarPath property from the supplied ScalarFunction
     * @param scalarFunc the supplied ScalarFunction
     * @return the Udf jarPath property value
     */
    public static String getUdfJarPath(final ScalarFunction scalarFunc) {
        String udfJarPath = null;
        ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(FunctionModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix());
        if(assistant!=null) {
            try {
                udfJarPath = assistant.getPropertyValue(scalarFunc, FunctionModelExtensionConstants.PropertyIds.UDF_JAR_PATH);
            } catch (Exception ex) {
                ModelerCore.Util.log(IStatus.ERROR,ex,FunctionPlugin.Util.getString("FunctionUtil.ErrorGettingJarPath", scalarFunc.getName())); //$NON-NLS-1$
            }
        }
        return udfJarPath;
    }
    
    /**
     * Get the Udf jarPath property from the supplied ScalarFunction
     * @param proc the supplied Procedure
     * @return the Udf jarPath property value
     */
    public static String getUdfJarPath(final Procedure proc) {
        String udfJarPath = null;
        ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(RelationalModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix());
        if(assistant!=null) {
            try {
                udfJarPath = assistant.getPropertyValue(proc, RelationalModelExtensionConstants.PropertyIds.UDF_JAR_PATH);
            } catch (Exception ex) {
                String msg = RelationalPlugin.Util.getString("ProcedureVirtualFunctionRule.errorGettingJarPath", proc.getName());  //$NON-NLS-1$
                RelationalPlugin.Util.log(IStatus.ERROR,ex,msg);
            }
        }
        return udfJarPath;
    }
    
    VdbModelEntry( final Vdb vdb,
                   final ModelElement element,
                   final IProgressMonitor monitor ) {
        super(vdb, element, monitor);
        this.element = element;
        type =  element.getType();
        visible.set(element.isVisible());
        sourceInfo = new VdbSourceInfo(vdb);
        if (element.getSources() != null && !element.getSources().isEmpty()) {
            for (final SourceElement source : element.getSources()) {
            	sourceInfo.add(source.getName(), source.getJndiName(), 
            			source.getTranslatorName() == null ? StringUtilities.EMPTY_STRING : source.getTranslatorName());
            }
        }
        if(VdbUtil.FUNCTION.equals(type) || VdbUtil.VIRTUAL.equals(type) || VdbUtil.PHYSICAL.equals(type)) {
            updateUdfJars(findModel());
        }
        for (final ProblemElement problem : element.getProblems())
            problems.add(new Problem(problem));
        boolean builtIn = false;
        String indexName = null;
        String modelClass = null;
        for (final PropertyElement property : element.getProperties()) {
            final String name = property.getName();
            if (ModelElement.BUILT_IN.equals(name)) builtIn = Boolean.parseBoolean(property.getValue());
            else if (ModelElement.INDEX_NAME.equals(name)) indexName = property.getValue();
            else if (ModelElement.MODEL_CLASS.equals(name)) modelClass = property.getValue();
            else if (ModelElement.MODEL_UUID.equals(name)) modelUuid = property.getValue();
            else if (ModelElement.IMPORT_VDB_REFERENCE.equals(name)) {
            	importVdbNames.add(property.getValue());
            } else if( ModelElement.SUPPORTS_MULTI_SOURCE.equals(name)) {
            	sourceInfo.setIsMultiSource(Boolean.parseBoolean(property.getValue()));
            } else if( ModelElement.MULTI_SOURCE_ADD_COLUMN.equals(name)) {
            	sourceInfo.setAddColumn(Boolean.parseBoolean(property.getValue()));
            } else if( ModelElement.MULTI_SOURCE_COLUMN_ALIAS.equals(name)) {
            	sourceInfo.setColumnAlias(property.getValue());
            }
        }
        this.builtIn = builtIn;
        this.indexName = indexName;
        this.modelClass = modelClass;
        
        getVdb().registerImportVdbs(importVdbNames, this.getName().toString(), monitor);
        
        getVdb().synchronizeUdfJars(udfJars);
    }

    private void clean() {
        // Clear problems
        problems.clear();
        // Clear set of imports and inverse relationships
        for (final VdbEntry entry : imports) {
            if (entry instanceof VdbModelEntry) {
                VdbModelEntry vdbModelEntry = (VdbModelEntry) entry;
                vdbModelEntry.importedBy.remove(this);

                if (vdbModelEntry.isBuiltIn())
                    entry.dispose();
            }
        }
        imports.clear();
        getIndexFile().delete();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.VdbEntry#dispose()
     */
    @Override
    final void dispose() {
        super.dispose();

        // remove the imported by models
        Collection<VdbModelEntry> importedByModels = new ArrayList<VdbModelEntry>(importedBy);

        for (final VdbModelEntry entry : importedByModels) {
            importedBy.remove(entry);
            getVdb().removeEntry(entry);
        }

        clean();
    }

    private Resource findModel() {
        IResource resource = ModelerCore.getWorkspace().getRoot().findMember(getName());

        // model not found in workspace
        if (resource == null) {
            return null;
        }

        Resource emfResource = getFinder().findByURI(URI.createFileURI(resource.getLocation().toString()), false);

        // as a last resort force loading the resource
        if (emfResource == null) {
            try {
                emfResource = ModelerCore.getModelContainer().getResource(URI.createFileURI(resource.getLocation().toString()), true);
            } catch (CoreException e) {
                throw CoreModelerPlugin.toRuntimeException(e);
            }
        }

        return emfResource;
    }
    
    private String findModelClass(Resource resource) {
    	try {
			return ModelUtil.getModelClass(resource);
		} catch (ModelWorkspaceException e) {
			throw CoreModelerPlugin.toRuntimeException(e);
		}
    }

    private ResourceFinder getFinder() {
        try {
            return ModelerCore.getModelContainer().getResourceFinder();
        } catch (final Exception error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
    }

    /**
     * Determine if the resource for this entry contains any User-Defined functions.  Currently this includes:
     * 1) FunctionModel with ScalarFunctions and 2) Relational Procedures where function=true
     * @return 'true' if the Model resource contains a User-Defined function, 'false' if not.
     */
    public final boolean containsUdf() {
        boolean hasUdf = false;
        
        // If its a FunctionModel it has ScalarFunctions/Udfs in it
        if(VdbUtil.FUNCTION.equals(getType())) {
            hasUdf = true;
        // If its a relational View Model, see if it has any procedures with function=true
        } else {
            Resource modelResc = findModel();
            boolean isRelational = false;
            try {
                isRelational = ModelUtil.getModelClass(modelResc).equals(ModelUtil.MODEL_CLASS_RELATIONAL);
            } catch (ModelWorkspaceException ex) {
            }
            if(isRelational) {
                try {
                    final ModelResource mr = ModelerCore.getModelEditor().findModelResource(modelResc);
                    @SuppressWarnings("unchecked")
					List<EObject> eObjs = mr.getEObjects();
                    for(EObject eObj: eObjs) {
                        if(eObj instanceof Procedure && ((Procedure)eObj).isFunction()) {
                            hasUdf = true;
                            break;
                        }
                    }
                } catch (ModelWorkspaceException ex) {
                }
            }
        }

        return hasUdf;
    }
    
    /**
     * @return the immutable set of model entries that import this model entry
     */
    public final Set<VdbModelEntry> getImportedBy() {
        return Collections.unmodifiableSet(importedBy);
    }

    /**
     * @return the immutable set of model entries imported by this model entry
     */
    public final Set<? extends VdbEntry> getImports() {
        return Collections.unmodifiableSet(imports);
    }
    
    /**
     * @return the immutable set of VDB name strings referenced by this model entry
     */
    public final Set<String> getImportVdbNames() {
        return Collections.unmodifiableSet(importVdbNames);
    }
    
    /**
     * @return the immutable set of VDB Udf File entries used by this model entry
     */
    public final Set<VdbFileEntry> getUdfJars() {
        return Collections.unmodifiableSet(udfJars);
    }


    File getIndexFile() {
        return new File(getVdb().getFolder(), INDEX_FOLDER + indexName);
    }

    /**
     * @return indexName
     */
    public String getIndexName() {
        return indexName;
    }
    
    /**
     * @return the <code>VdbSourceInfo</code> object
     */
	public VdbSourceInfo getSourceInfo() {
    	return this.sourceInfo;
    }

    /**
     * @return the immutable set of problems associated with this model entry
     */
    public final Set<Problem> getProblems() {
        return Collections.unmodifiableSet(problems);
    }

    /**
     * @return type
     */
    public final String getType() {
        return type;
    }

    void initializeImports() {
        for (final PropertyElement property : element.getProperties())
            if (ModelElement.IMPORTS.equals(property.getName())) for (final VdbModelEntry entry : getVdb().modelEntries)
                if (property.getValue().equals(entry.getName().toString())) {
                    entry.importedBy.add(this);
                    imports.add(entry);
                    break;
                }
        element = null;
    }
    
    /**
     * @return model uuid
     */
    public final String getModelUuid() {
        return modelUuid;
    }
    
    /**
     * @return model class
     */
    public final String getModelClass() {
        return modelClass;
    }

    /**
     * @return <code>true</code> if the associated model is a hidden built-in model.
     */
    public final boolean isBuiltIn() {
        return builtIn;
    }

    /**
     * @return <code>true</code> if the associated model will be directly accessible to users.
     */
    public final boolean isVisible() {
        return visible.get();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.VdbEntry#save(java.util.zip.ZipOutputStream, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    final void save( final ZipOutputStream out,
                     final IProgressMonitor monitor ) {
        super.save(out, monitor);
        // Save model index
        save(out, new ZipEntry(INDEX_FOLDER + indexName), getIndexFile(), monitor);

        if (!getVdb().isPreview()) {
            try {
                // Convert problems for this model entry to markers on the VDB file
                final IFile vdbFile = getVdb().getFile();
                for (final Problem problem : problems) {
                    final IMarker marker = vdbFile.createMarker(IMarker.PROBLEM);
                    marker.setAttribute(IMarker.SEVERITY, problem.getSeverity());
                    marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
                    marker.setAttribute(IMarker.LOCATION, getName().toString() + '/' + problem.getLocation());
                }
            } catch (final Exception error) {
                throw CoreModelerPlugin.toRuntimeException(error);
            }
        }
    }

    /**
     * @param index
     * @param name 
     */
    public void setJndiName( int index, String name ) {
    	CoreArgCheck.isTrue(index < sourceInfo.getSourceCount(), "index out of range"); //$NON-NLS-1$
    	
    	if (StringUtilities.isEmpty(name)) name = null;
    	String oldName = sourceInfo.getSource(index).getJndiName();
        if (StringUtilities.equals(name, oldName)) return;
        sourceInfo.getSource(index).setJndiName(name);
    }

    /**
     * @param index
     * @param name
     */
    public final void setSourceName(  int index, String name ) {
    	CoreArgCheck.isTrue(index < sourceInfo.getSourceCount(), "index out of range"); //$NON-NLS-1$
    	
    	if (StringUtilities.isEmpty(name)) name = null;
    	String oldName = sourceInfo.getSource(index).getName();
        if (StringUtilities.equals(name, oldName)) return;
        sourceInfo.getSource(index).setName(name);
    }

    /**
     * @param index
     * @param name
     */
    public final void setTranslatorName(  int index, String name ) {
    	CoreArgCheck.isTrue(index < sourceInfo.getSourceCount(), "index out of range"); //$NON-NLS-1$
    	if (StringUtilities.isEmpty(name)) name = null;
    	String oldName = sourceInfo.getSource(index).getTranslatorName();
        if (StringUtilities.equals(name, oldName)) return;
        sourceInfo.getSource(index).setTranslatorName(name);
    }
    
    /**
     * Returns the current <code>TranslatorOverride</code> for this model
     * @return translator override. May be null.
     */
    public final TranslatorOverride getTranslatorOverride() {
    	if( !this.sourceInfo.isEmpty() ) {
        	Set<TranslatorOverride> overrides = getVdb().getTranslators();
        	for( TranslatorOverride to : overrides) {
        		for( VdbSource source : this.sourceInfo.getSources() ) {
        			String translatorName = source.getTranslatorName();
	        		if( translatorName != null && translatorName.toString().equalsIgnoreCase(to.getName()) ) {
	        			return to;
	        		}
        		}
        	}
    	}
    	
    	return null;
    }
    
    
    /*
     * Only called by synchronize method or vdb creation. Intent is to update the TO for a given model based on 
     * injected translator properties.
     * 
     * 1) if matching property found, set the new value
     * 2) if no matching property found, add a new one
     * 3) No way to tell if an OLD property needs to get removed though
     */
    void updateTranslatorOverrides(Properties props) {
    	// TODO: Update for Multi-Sources bindings
    	if( this.sourceInfo.isMultiSource() ) {
    		return;
    	}
    	
        // If only ONE property and it's "name", then ignore

        if( props.size() == 1 && ((String)props.keySet().toArray()[0]).equalsIgnoreCase(VdbConstants.Translator.NAME_KEY) ) {
            return;
        }
    	TranslatorOverride to = getTranslatorOverride();
    	String oldTranslator = this.sourceInfo.getSource(0).getTranslatorName();
    	String newTranslator = null;
    	if( to == null ) {
    		String toName = null;
    		if( !oldTranslator.startsWith(this.sourceInfo.getSource(0).getName()) ) {
    			toName = this.sourceInfo.getSource(0).getName() + '_' + oldTranslator;
    		}
    		to = new TranslatorOverride(getVdb(), toName, oldTranslator, null);
    		newTranslator = toName;
    		this.sourceInfo.getSource(0).setTranslatorName(toName);
    		getVdb().addTranslator(to, new NullProgressMonitor());
    	}
    	
    	TranslatorOverrideProperty[] toProps = to.getProperties();
    	
        Set<Object> keys = props.keySet();
        for (Object nextKey : keys) {
        	boolean existing = "name".equals(nextKey); //$NON-NLS-1$
        	// Look through current TO props to see if already defined
    		for( TranslatorOverrideProperty toProp : toProps ) {
    			if( toProp.getDefinition().getId().equals(nextKey) ) {

    				// This is an override case
    				toProp.setValue(props.getProperty((String)nextKey));
    				existing = true;
    				break;
    			}
    		}

    		if( !existing ) {
    			to.addProperty(new TranslatorOverrideProperty(new TranslatorPropertyDefinition((String) nextKey, "dummy"), props.getProperty((String)nextKey))); //$NON-NLS-1$
    		}
    	}
        getVdb().setModified(this, MODEL_TRANSLATOR, oldTranslator, newTranslator);
    }

    /**
     * @param visible <code>true</code> if the associated model will be directly accessible to users.
     */
    public final void setVisible( final boolean visible ) {
        final boolean oldVisible = isVisible();
        if (oldVisible == visible) return;
        this.visible.set(visible);
        getVdb().setModified(this, MODEL_VISIBLE, oldVisible, visible);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.VdbEntry#synchronize(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public final void synchronize( final IProgressMonitor monitor ) {
        if (getSynchronization() != Synchronization.NotSynchronized) return;
        synchronizeModelEntry(monitor);
        super.synchronize(monitor);
    }

    void synchronizeModelEntry( final IProgressMonitor monitor ) {
        final IFile workspaceFile = findFileInWorkspace();
        if (workspaceFile == null) return;
        clean();
        try {
            final Resource model = findModel();
            if (ModelUtil.isPhysical(model)) {
            	if( !this.getSourceInfo().isMultiSource() ) {
	                final ModelResource mr = ModelerCore.getModelEditor().findModelResource(workspaceFile);
	                final ConnectionInfoHelper helper = new ConnectionInfoHelper();
	                
	                final String translatorName = this.sourceInfo.getSource(0).getTranslatorName();
	                final String resourceTranslatorName = helper.getTranslatorName(mr);
	                if (!CoreStringUtil.isEmpty(resourceTranslatorName) && !CoreStringUtil.equals(translatorName, resourceTranslatorName)) {
	                    this.sourceInfo.getSource(0).setTranslatorName(resourceTranslatorName == null ? EMPTY_STR : resourceTranslatorName);
	                }
	                
	                Properties translatorProps = helper.getTranslatorProperties(mr);
	                if( !translatorProps.isEmpty() ) {
	                	updateTranslatorOverrides(translatorProps);
	                }
	                
	                final String jndiName = this.sourceInfo.getSource(0).getJndiName();
	                final String resourceJndiName = helper.getJndiProperty(mr);
	                if (!CoreStringUtil.isEmpty(resourceJndiName) && !CoreStringUtil.equals(jndiName, resourceJndiName)) {
	                    this.sourceInfo.getSource(0).setJndiName(resourceJndiName == null ? EMPTY_STR : resourceJndiName);
	                }
            	}
                
            }
            if (containsUdf()) {
                updateUdfJars(model);
            }

            // Build model if necessary
            // Get Index File and check time/date to see if we need to rebuild or not
            IPath indexPath = new Path(IndexUtil.INDEX_PATH + indexName); //
            File indexFile = indexPath.toFile();
            long indexDate = -1;
            if( indexFile.exists() ) {
            	indexDate = indexFile.lastModified();
            	
            }
            if( workspaceFile.getLocalTimeStamp() > indexDate ) {
            	// Note that this will index and validate the model in the workspace
            	getVdb().getBuilder().buildResources(monitor, Collections.singleton(workspaceFile), ModelerCore.getModelContainer(), false);
            }
            problems.clear();
            // Synchronize model problems
            for (final IMarker marker : workspaceFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
                Object attr = marker.getAttribute(IMarker.SEVERITY);
                if (attr == null) {
                    continue;
                }
                // Asserting attr is an Integer...
                final int severity = ((Integer)attr).intValue();
                if (severity == IMarker.SEVERITY_ERROR || severity == IMarker.SEVERITY_WARNING) {
                    problems.add(new Problem(marker));
                }
            }
            // Also add imported models if not a preview
            if (!getVdb().isPreview()) {
            	importVdbNames.clear();
                Resource[] refs = getFinder().findReferencesFrom(model, true, false);
                if (refs != null) {
                    for (final Resource importedModel : refs) {
                    	java.net.URI uri = java.net.URI.create(importedModel.getURI().toString());
                        IFile[] modelFiles = ModelerCore.getWorkspace().getRoot().findFilesForLocationURI(uri);
                        final IPath name = modelFiles[0].getFullPath();
                        
                        // Check Model File to see if it contains a vdb name property
                        final String importVdbName = ModelUtil.getModelAnnotationPropertyValue(modelFiles[0], VdbConstants.VDB_NAME_KEY);
                        if( importVdbName != null ) {
                        	importVdbNames.add(importVdbName);
                        } else {
	                        VdbModelEntry importedEntry = null;
	
	                        for (final VdbModelEntry entry : getVdb().getModelEntries()) {
	                            if (name.equals(entry.getName())) {
	                                importedEntry = entry;
	                                break;
	                            }
	                        }
	
	                        if (importedEntry == null) importedEntry = getVdb().addModelEntry(name, monitor);
	                        imports.add(importedEntry);
	                        importedEntry.importedBy.add(this);
                        }
                    }
                }
                
                // Process for any import VDBs
                // if list is empty, then there may be import VDB's that need to get removed from the VDB
                getVdb().registerImportVdbs(importVdbNames, this.getName().toString(), monitor);
                
                getVdb().synchronizeUdfJars(udfJars);
            }
            // Copy snapshot of workspace file index to VDB folder
            // TODO: If index name of workspace file can change (?), we have to delete the old index and update our index name
            final Index index = IndexUtil.getIndexFile(indexName, IndexUtil.INDEX_PATH + indexName, getName().lastSegment());
            FileUtils.copy(index.getIndexFile(), getIndexFile().getParentFile(), true);
        } catch (final Exception error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
    }


    /**
     * Replaces the given old entry with the new entry in this entry's
     * imports collection
     *
     * @param oldEntry
     * @param newEntry
     */
    void replaceImport(VdbEntry oldEntry, VdbEntry newEntry) {
        imports.remove(oldEntry);
        imports.add(newEntry);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.VdbEntry#toString(java.lang.StringBuilder)
     */
    @Override
    protected void toString( final StringBuilder builder ) {
        builder.append(", type="); //$NON-NLS-1$
        builder.append(type);
        builder.append(", visible?="); //$NON-NLS-1$
        builder.append(visible);
        builder.append(", built-in?="); //$NON-NLS-1$
        builder.append(builtIn);
        for( VdbSource source : sourceInfo.getSources() ) {
	        builder.append(", source="); //$NON-NLS-1$
	        builder.append(source);
        }
        builder.append(", index="); //$NON-NLS-1$
        builder.append(indexName);
        builder.append(", problems?="); //$NON-NLS-1$
        builder.append(!problems.isEmpty());
        builder.append(", imports=["); //$NON-NLS-1$
        for (final Iterator<VdbEntry> iter = imports.iterator(); iter.hasNext();) {
            builder.append(iter.next().getName());
            if (iter.hasNext()) builder.append(", "); //$NON-NLS-1$
        }
        builder.append(']');
    }

    /**
     * 
     */
    public class Problem {

        private final int severity;
        private final String message;
        private final String location;

        Problem( final IMarker marker ) {
            this.severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            this.message = marker.getAttribute(IMarker.MESSAGE, null);
            this.location = marker.getAttribute(IMarker.LOCATION, null);
        }

        Problem( final ProblemElement problem ) {
            this.severity = problem.getSeverity() == Severity.ERROR ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING;
            this.message = problem.getMessage();
            this.location = problem.getLocation();
        }

        /**
         * @return location
         */
        public String getLocation() {
            return location;
        }

        /**
         * @return message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @return severity
         */
        public int getSeverity() {
            return severity;
        }
    }
}
