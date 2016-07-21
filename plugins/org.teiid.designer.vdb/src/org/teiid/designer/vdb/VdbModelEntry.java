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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
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
import org.teiid.designer.vdb.manifest.SourceElement;

import net.jcip.annotations.ThreadSafe;


/**
 *
 *
 * @since 8.0
 */
@ThreadSafe
public class VdbModelEntry extends VdbIndexedEntry {

    /**
     * @param path the model path (may not be <code>null</code>)
     * @return the default name to use as the source name (never <code>null</code>)
     */
    public static String createDefaultSourceName(IPath path) {
        return path.removeFileExtension().lastSegment();
    }

    private String modelUuid;
    private final AtomicBoolean visible = new AtomicBoolean(true);
    final CopyOnWriteArraySet<VdbEntry> imports = new CopyOnWriteArraySet<VdbEntry>();
    final CopyOnWriteArraySet<VdbModelEntry> importedBy = new CopyOnWriteArraySet<VdbModelEntry>();
    final CopyOnWriteArraySet<String> importVdbNames = new CopyOnWriteArraySet<String>();
    final CopyOnWriteArraySet<VdbFileEntry> udfJars = new CopyOnWriteArraySet<VdbFileEntry>();
    private final String modelClass;
    private final boolean builtIn;
    private final String type;
    final VdbSourceInfo sourceInfo;
    private transient ModelElement element;
    private final String schemaText;
    private final String metadataType;

    /**
     * Constructs a model entry and adds it to the specified VDB. <strong>Callers of this method should call
     * {@link #synchronizeModelEntry()} immediately after constructing the model entry.</strong>
     * 
     * @param vdb the VDB where the resource is be added to (may not be <code>null</code>)
     * @param path the resource path (may not be <code>null</code>)
     * @throws Exception
     */
    public VdbModelEntry( final XmiVdb vdb, final IPath path) throws Exception {
        super(vdb, path);
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
                final String defaultName = createDefaultSourceName(path);
                final ConnectionInfoHelper helper = new ConnectionInfoHelper();
                String translator = helper.getTranslatorName(mr);
                if( translator == null ) translator = EMPTY_STRING;
                
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

        schemaText = null;
        metadataType = null;
    }

    private void updateUdfJars(Resource model) throws Exception {
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
                            udfJars.add(new VdbFileEntry(getVdb(), jarResource.getFullPath(), VdbFileEntry.FileEntryType.UDFJar));
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
    
    /**
     * @param vdb
     * @param element
     * @throws Exception
     */
    public VdbModelEntry( final XmiVdb vdb,
                   final ModelElement element ) throws Exception {
        super(vdb, element);
        this.element = element;
        type =  element.getType();
        visible.set(element.isVisible());
        sourceInfo = new VdbSourceInfo(vdb);
        if( element.getMetadata() != null && !element.getMetadata().isEmpty()) {
        	schemaText = element.getMetadata().get(0).getSchemaText();
        	metadataType = element.getMetadata().get(0).getType();
        } else {
        	schemaText = null;
            metadataType = null;
        }
        if (element.getSources() != null && !element.getSources().isEmpty()) {
            for (final SourceElement source : element.getSources()) {
            	sourceInfo.add(source.getName(), source.getJndiName(), 
            			source.getTranslatorName() == null ? EMPTY_STRING : source.getTranslatorName());
            }
        }

        if(VdbUtil.FUNCTION.equals(type) || VdbUtil.VIRTUAL.equals(type) || VdbUtil.PHYSICAL.equals(type)) {
            updateUdfJars(findModel());
        }
        for (final ProblemElement problem : element.getProblems())
            addProblem(new Problem(problem));
        boolean builtIn = false;
        String modelClass = null;
        for (final PropertyElement property : element.getProperties()) {
            final String name = property.getName();
            if (ModelElement.BUILT_IN.equals(name)) builtIn = Boolean.parseBoolean(property.getValue());
            else if (ModelElement.MODEL_CLASS.equals(name)) modelClass = property.getValue();
            else if (ModelElement.MODEL_UUID.equals(name)) modelUuid = property.getValue();
            else if (ModelElement.IMPORT_VDB_REFERENCE.equals(name)) {
            	importVdbNames.add(property.getValue());
            } else if( ModelElement.SUPPORTS_MULTI_SOURCE.equals(name) || ModelElement.MULTI_SOURCE.equals(name) ) {
            	sourceInfo.setIsMultiSource(Boolean.parseBoolean(property.getValue()));
            } else if( ModelElement.MULTI_SOURCE_ADD_COLUMN.equals(name)) {
            	sourceInfo.setAddColumn(Boolean.parseBoolean(property.getValue()));
            } else if( ModelElement.MULTI_SOURCE_COLUMN_ALIAS.equals(name)) {
            	sourceInfo.setColumnAlias(property.getValue());
            } else {
            	setProperty(name, property.getValue());
            }
        }
        this.builtIn = builtIn;
        this.modelClass = modelClass;
        
        getVdb().synchronizeUdfJars(udfJars);
    }

    @Override
    protected void clean() {
        super.clean();

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
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.VdbEntry#dispose()
     */
    @Override
    public final void dispose() {
        super.dispose();

        // remove the imported by models
        Collection<VdbModelEntry> importedByModels = new ArrayList<VdbModelEntry>(importedBy);

        for (final VdbModelEntry entry : importedByModels) {
            importedBy.remove(entry);
            getVdb().removeEntry(entry);
        }

        clean();
    }

    private String findModelClass(Resource resource) throws Exception {
        return ModelUtil.getModelClass(resource);
    }

    /**
     * Determine if the resource for this entry contains any User-Defined functions.  Currently this includes:
     * 1) FunctionModel with ScalarFunctions and 2) Relational Procedures where function=true
     * @return 'true' if the Model resource contains a User-Defined function, 'false' if not.
     * @throws Exception
     */
    public final boolean containsUdf() throws Exception {
        boolean hasUdf = false;
        
        // If its a FunctionModel it has ScalarFunctions/Udfs in it
        if(VdbUtil.FUNCTION.equals(getType())) {
            hasUdf = true;
        // If its a relational View Model, see if it has any procedures with function=true
        } else {
            Resource modelResc = findModel();
            boolean isRelational = false;
            isRelational = ModelUtil.getModelClass(modelResc).equals(ModelUtil.MODEL_CLASS_RELATIONAL);
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
                    // Nothing to do
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


    /**
     * @return the <code>VdbSourceInfo</code> object
     */
	public VdbSourceInfo getSourceInfo() {
    	return this.sourceInfo;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     */
    public void initializeImports() {
        for (final PropertyElement property : element.getProperties()) {
            if (ModelElement.IMPORTS.equals(property.getName())) {
            	for (final VdbEntry entry : getVdb().getModelEntries()) {
	                if (property.getValue().equals(entry.getPath().toOSString())) {
	                    ((VdbModelEntry)entry).importedBy.add(this);
	                    imports.add(entry);
	                    break;
	                }
            	}
            }
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
     * @see org.teiid.designer.vdb.VdbEntry#save(java.util.zip.ZipOutputStream)
     */
    @Override
    public final void save( final ZipOutputStream out) throws Exception {
        super.save(out);
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
     * @return schema text
     */
    public String getSchemaText() {
    	return schemaText;
    }
    
    /**
     * @return metadata type
     */
    public final String getMetadataType() {
    	return metadataType;
    }
    
    /**
     * Returns the current <code>TranslatorOverride</code> for this model
     * @return translator override. May be null.
     */
    public final TranslatorOverride getTranslatorOverride() {
    	if( !this.sourceInfo.isEmpty() ) {
        	Collection<TranslatorOverride> overrides = getVdb().getTranslators();
        	for( TranslatorOverride to : overrides) {
        		for( VdbSource source : this.sourceInfo.getSources() ) {
        			String translatorName = source.getTranslatorName();
	        		if( translatorName != null && translatorName.toString().equalsIgnoreCase(to.getType()) ) {
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

        if( props.size() == 1 && ((String)props.keySet().toArray()[0]).equalsIgnoreCase(VdbConstants.TRANSLATOR_NAME_KEY) ) {
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
    		getVdb().addTranslator(to);
    	}
    	
    	TranslatorOverrideProperty[] toProps = to.getOverrideProperties();
    	
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
     * @see org.teiid.designer.vdb.VdbEntry#synchronize()
     */
    @Override
    public void synchronize() throws Exception {
        if (getSynchronization() != Synchronization.NotSynchronized)
            return;
        synchronizeModelEntry();
        super.synchronize();
    }

    /**
     * @throws Exception
     */
    public void synchronizeModelEntry() throws Exception {
        final IFile workspaceFile = findFileInWorkspace();
        if (workspaceFile == null)
            return;

        clean();

        final Resource model = findModel();
        if (ModelUtil.isPhysical(model)) {
            if (!this.getSourceInfo().isMultiSource()) {
                final ModelResource mr = ModelerCore.getModelEditor().findModelResource(model);
                final ConnectionInfoHelper helper = new ConnectionInfoHelper();

                final String translatorName = this.sourceInfo.getSource(0).getTranslatorName();
                final String resourceTranslatorName = helper.getTranslatorName(mr);
                if (!CoreStringUtil.isEmpty(resourceTranslatorName)
                    && !CoreStringUtil.equals(translatorName, resourceTranslatorName)) {
                    this.sourceInfo.getSource(0).setTranslatorName(resourceTranslatorName == null ? EMPTY_STRING : resourceTranslatorName);
                }

                Properties translatorProps = helper.getTranslatorProperties(mr);
                if (!translatorProps.isEmpty()) {
                    updateTranslatorOverrides(translatorProps);
                }

                final String jndiName = this.sourceInfo.getSource(0).getJndiName();
                final String resourceJndiName = helper.getJndiProperty(mr);
                if (!CoreStringUtil.isEmpty(resourceJndiName) && !CoreStringUtil.equals(jndiName, resourceJndiName)) {
                    this.sourceInfo.getSource(0).setJndiName(resourceJndiName == null ? EMPTY_STRING : resourceJndiName);
                }
            }
        }

        if (containsUdf()) {
            updateUdfJars(model);
        }

        synchronizeIndex();

        // Also add imported models if not a preview
        if (!getVdb().isPreview()) {
            importVdbNames.clear();
            Resource[] refs = getFinder().findReferencesFrom(model, true, false);
        	
            Collection<VdbImportInfo> vdbImports = new ArrayList<VdbImportInfo>();
        	
            if (refs != null) {
            	// Need to look in each imported model.. if it's a "view" model, then we can ignore it
            	// If it's a "source" model, then we need to look for the properties in the model
            	
                for (final Resource importedModel : refs) {
                    java.net.URI uri = java.net.URI.create(importedModel.getURI().toString());
                    IFile[] modelFiles = ModelerCore.getWorkspace().getRoot().findFilesForLocationURI(uri);
                    final IPath name = modelFiles[0].getFullPath();

                    // Check Model File to see if it contains a vdb name property
                    final String importVdbName = ModelUtil.getModelAnnotationPropertyValue(modelFiles[0],
                                                                                           VdbConstants.VDB_NAME_KEY);

                    if (importVdbName != null) {
                        String versionStr = ModelUtil.getModelAnnotationPropertyValue(modelFiles[0],
								VdbConstants.VDB_VERSION_KEY);
                        
                        int vdbVersion = 1;
                        if( ! StringUtilities.isEmpty(versionStr) ) {
        					try {
        	                    int versionValue = Integer.parseInt(versionStr);
        	                    if (versionValue > 0) {
        	                    	vdbVersion = versionValue;
        						}
        					} catch (NumberFormatException ex) {
        						VdbPlugin.UTIL.log(ex);
        					}
                        }
                        importVdbNames.add(importVdbName);
                        vdbImports.add(new VdbImportInfo(importVdbName, vdbVersion));
                    } else {
                        VdbEntry importedEntry = null;

                        for (final VdbEntry entry : getVdb().getModelEntries()) {
                            if (name.equals(entry.getPath())) {
                                importedEntry = entry;
                                break;
                            }
                        }

                        if (importedEntry == null)
                            importedEntry = getVdb().addEntry(name);
                        imports.add(importedEntry);

                        if (importedEntry instanceof VdbModelEntry)
                            ((VdbModelEntry)importedEntry).importedBy.add(this);
                    }
                }
            }

            // Process for any import VDBs
            // if list is empty, then there may be import VDB's that need to get removed from the VDB
            getVdb().registerImportVdbs(vdbImports, this.getPath().toString());

            getVdb().synchronizeUdfJars(udfJars);
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
        builder.append(getIndexName());
        builder.append(", problems?="); //$NON-NLS-1$
        builder.append(!getProblems().isEmpty());
        builder.append(", imports=["); //$NON-NLS-1$
        for (final Iterator<VdbEntry> iter = imports.iterator(); iter.hasNext();) {
            builder.append(iter.next().getName());
            if (iter.hasNext()) builder.append(", "); //$NON-NLS-1$
        }
        builder.append(']');
    }

    @Override
    public VdbModelEntry clone() {
        try {
            VdbModelEntry clone = new VdbModelEntry(getVdb(), getPath());
            cloneVdbObject(clone);
            return clone;
        } catch (Exception ex) {
            VdbPlugin.UTIL.log(ex);
            return null;
        }
    }
}
