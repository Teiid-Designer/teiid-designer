/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.designer.vdb.connections.ConnectionFinderExtensionManager;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.ProblemElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.Severity;
import org.teiid.designer.vdb.manifest.SourceElement;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 *
 */
@ThreadSafe
public final class VdbModelEntry extends VdbEntry {

    private static final String INDEX_FOLDER = "runtime-inf/"; //$NON-NLS-1$

    private final String indexName;
    private final Set<Problem> problems = new HashSet<Problem>();
    private final AtomicBoolean visible = new AtomicBoolean(true);
    private final CopyOnWriteArraySet<VdbModelEntry> imports = new CopyOnWriteArraySet<VdbModelEntry>();
    private final CopyOnWriteArraySet<VdbModelEntry> importedBy = new CopyOnWriteArraySet<VdbModelEntry>();
    private final boolean builtIn;
    private final ModelType type;
    private final AtomicReference<String> source = new AtomicReference<String>();
    private transient ModelElement element;

    VdbModelEntry( final Vdb vdb,
                   final IPath name,
                   final IProgressMonitor monitor ) {
        super(vdb, name, monitor);
        indexName = IndexUtil.getRuntimeIndexFileName(findFileInWorkspace());
        synchronizeModelEntry(monitor);
        if( name.getFileExtension().equalsIgnoreCase(ModelUtil.EXTENSION_XMI)) {
	        final EmfResource model = (EmfResource)findModel();
	        builtIn = getFinder().isBuiltInResource(model);
	        type = model.getModelType();
	        if( model.getModelAnnotation().getDescription() != null ) {
	        	description.set(model.getModelAnnotation().getDescription());
	        } else {
	        	description.set(StringUtilities.EMPTY_STRING);
	        }
	        
	        if( ModelUtil.isPhysical(model) ) {
	        	String connectionName = ConnectionFinderExtensionManager.findConnectionName(model, name.removeFileExtension().lastSegment());
	        	source.set(connectionName);
	        }
        } else if( name.getFileExtension().equalsIgnoreCase(ModelUtil.EXTENSION_XSD)) {
	        final XSDResourceImpl model = (XSDResourceImpl)findModel();
	        builtIn = getFinder().isBuiltInResource(model);
	        type = ModelType.UNKNOWN_LITERAL;
	        description.set(StringUtilities.EMPTY_STRING);
	        source.set(StringUtilities.EMPTY_STRING);
        } else {
	        builtIn = false;
	        type = ModelType.UNKNOWN_LITERAL;
	        description.set(StringUtilities.EMPTY_STRING);
	        source.set(StringUtilities.EMPTY_STRING);
        }
    }

    VdbModelEntry( final Vdb vdb,
                   final ModelElement element,
                   final String description,
                   final IProgressMonitor monitor ) {
        super(vdb, element, description, monitor);
        this.element = element;
        type = ModelType.get(element.getType());
        visible.set(element.isVisible());
        for (final SourceElement source : element.getSources()) {
            this.source.set(source.getName());
            break; // TODO: support multi-source bindings
        }
        for (final ProblemElement problem : element.getProblems())
            problems.add(new Problem(problem));
        boolean builtIn = false;
        String indexName = null;
        for (final PropertyElement property : element.getProperties()) {
            final String name = property.getName();
            if (ModelElement.BUILT_IN.equals(name)) builtIn = Boolean.parseBoolean(property.getValue());
            else if (ModelElement.INDEX_NAME.equals(name)) indexName = property.getValue();
        }
        this.builtIn = builtIn;
        this.indexName = indexName;
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
        for (final VdbModelEntry entry : importedBy)
            getVdb().removeEntry(entry);
        clean();
    }

    private Resource findModel() {
        return getFinder().findByURI(URI.createFileURI(ResourcesPlugin.getWorkspace().getRoot().getLocation().append(getName()).toString()),
                                     false);
    }

    /**
     * @return source
     */
    public final String getDataSource() {
        return source.get();
    }

    private ResourceFinder getFinder() {
        try {
            return ModelerCore.getModelContainer().getResourceFinder();
        } catch (final Exception error) {
            CoreModelerPlugin.throwRuntimeException(error);
            return null;
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
     * @return the immutable set of problems associated with this model entry
     */
    public final Set<Problem> getProblems() {
        return Collections.unmodifiableSet(problems);
    }

    /**
     * @return type
     */
    public final ModelType getType() {
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
            CoreModelerPlugin.throwRuntimeException(error);
        }
    }

    /**
     * @param source
     */
    public final void setDataSource( final String source ) {
        final String oldSource = getDataSource();
        if( StringUtilities.areSame(source, oldSource, false) ) return;
        this.source.set(source);
        getVdb().setModified(this, Vdb.DATA_SOURCE, oldSource, source);
    }

    /**
     * @param visible <code>true</code> if the associated model will be directly accessible to users.
     */
    public final void setVisible( final boolean visible ) {
        final boolean oldVisible = isVisible();
        if (oldVisible == visible) return;
        this.visible.set(visible);
        getVdb().setModified(this, Vdb.VISIBLE, oldVisible, visible);
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

    private void synchronizeModelEntry( final IProgressMonitor monitor ) {
        final IFile workspaceFile = findFileInWorkspace();
        if (workspaceFile == null) return;
        clean();
        try {
            // Build model if necessary
            ModelBuildUtil.buildResources(monitor, Collections.singleton(workspaceFile), ModelerCore.getModelContainer(), false);
            // Synchronize model problems
            for (final IMarker marker : workspaceFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE))
                problems.add(new Problem(marker));
            // Also add imported models
            final Resource model = findModel();
            final IPath workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation();
            for (final Resource importedModel : getFinder().findReferencesFrom(model, true, false)) {
                final IPath name = Path.fromPortableString(importedModel.getURI().toFileString()).makeRelativeTo(workspace).makeAbsolute();
                VdbModelEntry importedEntry = null;
                for (final VdbModelEntry entry : getVdb().getModelEntries())
                    if (name.equals(entry.getName())) {
                        importedEntry = entry;
                        break;
                    }
                if (importedEntry == null) importedEntry = getVdb().addModelEntry(name, monitor);
                imports.add(importedEntry);
                importedEntry.importedBy.add(this);
            }
            // Copy snapshot of workspace file index to VDB folder
            // TODO: If index name of workspace file can change (?), we have to delete the old index and update our index name
            final Index index = IndexUtil.getIndexFile(indexName, IndexUtil.INDEX_PATH + indexName, getName().lastSegment());
            FileUtils.copy(index.getIndexFile(), getIndexFile().getParentFile(), true);
        } catch (final Exception error) {
            CoreModelerPlugin.throwRuntimeException(error);
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
