/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.Vdb.Event.MODEL_JNDI_NAME;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_SOURCE_NAME;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_TRANSLATOR;
import static org.teiid.designer.vdb.Vdb.Event.MODEL_VISIBLE;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.jcip.annotations.ThreadSafe;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.datatools.connection.ConnectionInfoHelper;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.ProblemElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.Severity;
import org.teiid.designer.vdb.manifest.SourceElement;

import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 *
 */
@ThreadSafe
public final class VdbModelEntry extends VdbEntry {

    private static final String INDEX_FOLDER = "runtime-inf/"; //$NON-NLS-1$
    private static final String EMPTY_STR = StringUtilities.EMPTY_STRING;
    
    /**
     * @param path the model path (may not be <code>null</code>)
     * @return the default name to use as the JNDI name (never <code>null</code>)
     */
    public static String createDefaultJndiName(IPath path) {
        return path.removeFileExtension().lastSegment();
    }

    private final String indexName;
    private final Set<Problem> problems = new HashSet<Problem>();
    private final AtomicBoolean visible = new AtomicBoolean(true);
    private final CopyOnWriteArraySet<VdbModelEntry> imports = new CopyOnWriteArraySet<VdbModelEntry>();
    private final CopyOnWriteArraySet<VdbModelEntry> importedBy = new CopyOnWriteArraySet<VdbModelEntry>();
    private final String modelClass;
    private final boolean builtIn;
    private final String type;
    private final AtomicReference<String> translator = new AtomicReference<String>();
    private final AtomicReference<String> source = new AtomicReference<String>();
    private final AtomicReference<String> jndiName = new AtomicReference<String>();
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
        if (ModelUtil.isXmiFile(model)) {
            final EmfResource emfModel = (EmfResource)model;
            type = emfModel.getModelType().getName();

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
                final String defaultName = createDefaultJndiName(name);
                source.set(defaultName);
                final ModelResource mr = ModelerCore.getModelEditor().findModelResource(model);
                final ConnectionInfoHelper helper = new ConnectionInfoHelper();
                final String translator = helper.getTranslatorName(mr);
                this.translator.set(translator == null ? EMPTY_STR : translator);
                jndiName.set(defaultName);
                Properties translatorProps = helper.getTranslatorProperties(mr);
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
        if (this.translator.get() == null) {
            this.translator.set(EMPTY_STR);
        }
        if (this.description.get() == null) {
            this.description.set(EMPTY_STR);
        }
    }

    VdbModelEntry( final Vdb vdb,
                   final ModelElement element,
                   final IProgressMonitor monitor ) {
        super(vdb, element, monitor);
        this.element = element;
        type =  element.getType();
        visible.set(element.isVisible());
        if (element.getSources() != null && !element.getSources().isEmpty()) {
            for (final SourceElement source : element.getSources()) {
                this.source.set(source.getName());
                this.translator.set(source.getTranslatorName() == null ? StringUtilities.EMPTY_STRING : source.getTranslatorName());
                this.jndiName.set(source.getJndiName());
                break; // TODO: support multi-source bindings
            }
        } else {
            this.translator.set(EMPTY_STR);
            // this.jndiName.set(EMPTY_STR);
            // this.source.set(EMPTY_STR);
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
        }
        this.builtIn = builtIn;
        this.indexName = indexName;
        this.modelClass = modelClass;
    }

    private void clean() {
        // Clear problems
        problems.clear();
        // Clear set of imports and inverse relationships
        for (final VdbModelEntry entry : imports) {
            entry.importedBy.remove(this);
            if (entry.isBuiltIn()) entry.dispose();
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
        IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(getName());

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
     * @return the immutable set of model entries that import this model entry
     */
    public final Set<VdbModelEntry> getImportedBy() {
        return Collections.unmodifiableSet(importedBy);
    }

    /**
     * @return the immutable set of model entries imported by this model entry
     */
    public final Set<VdbModelEntry> getImports() {
        return Collections.unmodifiableSet(imports);
    }

    private File getIndexFile() {
        return new File(getVdb().getFolder(), INDEX_FOLDER + indexName);
    }

    /**
     * @return indexName
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * @return jndiName
     */
    public String getJndiName() {
        return jndiName.get();
    }

    /**
     * @return the immutable set of problems associated with this model entry
     */
    public final Set<Problem> getProblems() {
        return Collections.unmodifiableSet(problems);
    }

    /**
     * @return source
     */
    public final String getSourceName() {
        return source.get();
    }

    /**
     * @return translator
     */
    public String getTranslator() {
        return translator.get();
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
     * @return <code>true</code> if the associated model is a hidden built-in model.
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
     * @param name
     */
    public void setJndiName( String name ) {
        if (StringUtilities.isEmpty(name)) name = null;
        final String oldName = getJndiName();
        if (StringUtilities.equals(name, oldName)) return;
        this.jndiName.set(name);
        getVdb().setModified(this, MODEL_JNDI_NAME, oldName, name);
    }

    /**
     * @param name
     */
    public final void setSourceName( String name ) {
        if (StringUtilities.isEmpty(name)) name = null;
        final String oldName = getSourceName();
        if (StringUtilities.equals(name, oldName)) return;
        this.source.set(name);
        getVdb().setModified(this, MODEL_SOURCE_NAME, oldName, name);
    }

    /**
     * @param translator
     */
    public final void setTranslator( String translator ) {
        final String oldTranslator = getTranslator();
        if (StringUtilities.equals(translator, oldTranslator)) return;
        this.translator.set(translator);
        getVdb().setModified(this, MODEL_TRANSLATOR, oldTranslator, translator);
    }
    
    /**
     * Returns the current <code>TranslatorOverride</code> for this model
     * @return translator override. May be null.
     */
    public final TranslatorOverride getTranslatorOverride() {
    	if( this.translator != null ) {
        	Set<TranslatorOverride> overrides = getVdb().getTranslators();
        	for( TranslatorOverride to : overrides) {
        		if( this.translator.toString().equalsIgnoreCase(to.getName()) ) {
        			return to;
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
        // If only ONE property and it's "name", then ignore
        if( props.size() == 1 && ((String)props.keySet().toArray()[0]).equalsIgnoreCase(VdbConstants.Translator.NAME_KEY) ) {
            return;
        }
    	TranslatorOverride to = getTranslatorOverride();
    	String oldTranslator = getTranslator();
    	if( to == null ) {
    		String toName = null;
    		if( !getTranslator().startsWith(getSourceName()) ) {
    			toName = getSourceName() + '_' + getTranslator();
    		}
    		to = new TranslatorOverride(getVdb(), toName, getTranslator(), null);
    		setTranslator(toName);
    		getVdb().addTranslator(to, new NullProgressMonitor());
    	}
    	
    	TranslatorOverrideProperty[] toProps = to.getProperties();
    	
        Set<Object> keys = props.keySet();
        for (Object nextKey : keys) {
        	boolean existing = "name".equals((String)nextKey); //$NON-NLS-1$
        	// Look through current TO props to see if already defined
    		for( TranslatorOverrideProperty toProp : toProps ) {
    			if( toProp.getDefinition().getId().equals((String)nextKey) ) {

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
        getVdb().setModified(this, MODEL_TRANSLATOR, oldTranslator, translator);
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
                final ModelResource mr = ModelerCore.getModelEditor().findModelResource(workspaceFile);
                final ConnectionInfoHelper helper = new ConnectionInfoHelper();
                
                if (CoreStringUtil.isEmpty(this.translator.get())) {
                    final String translator = helper.getTranslatorName(mr);
                    this.translator.set(translator == null ? EMPTY_STR : translator);
                }
                
                Properties translatorProps = helper.getTranslatorProperties(mr);
                if( !translatorProps.isEmpty() ) {
                	updateTranslatorOverrides(translatorProps);
                }
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
                Resource[] refs = getFinder().findReferencesFrom(model, true, false);

                if (refs != null) {
                    for (final Resource importedModel : refs) {
                    	java.net.URI uri = java.net.URI.create(importedModel.getURI().toString());
                        IFile[] modelFiles = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri);
                        final IPath name = modelFiles[0].getFullPath();
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
            // Copy snapshot of workspace file index to VDB folder
            // TODO: If index name of workspace file can change (?), we have to delete the old index and update our index name
            final Index index = IndexUtil.getIndexFile(indexName, IndexUtil.INDEX_PATH + indexName, getName().lastSegment());
            FileUtils.copy(index.getIndexFile(), getIndexFile().getParentFile(), true);
        } catch (final Exception error) {
            throw CoreModelerPlugin.toRuntimeException(error);
        }
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
        builder.append(", source="); //$NON-NLS-1$
        builder.append(source);
        builder.append(", index="); //$NON-NLS-1$
        builder.append(indexName);
        builder.append(", problems?="); //$NON-NLS-1$
        builder.append(!problems.isEmpty());
        builder.append(", imports=["); //$NON-NLS-1$
        for (final Iterator<VdbModelEntry> iter = imports.iterator(); iter.hasNext();) {
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
