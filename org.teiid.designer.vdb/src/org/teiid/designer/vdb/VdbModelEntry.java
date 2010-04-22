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
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.plugin.VdbPlugin;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.ResourceFinder;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 *
 */
@ThreadSafe
public final class VdbModelEntry extends VdbEntry {

    private static final String INDEX_FOLDER = "runtime-inf/"; //$NON-NLS-1$

    private final String indexName;
    private final Set<IMarker> problems = new HashSet<IMarker>();
    private final AtomicBoolean visible = new AtomicBoolean(true);
    private final CopyOnWriteArraySet<VdbModelEntry> dependsUpon = new CopyOnWriteArraySet<VdbModelEntry>();
    private final CopyOnWriteArraySet<VdbModelEntry> dependentOf = new CopyOnWriteArraySet<VdbModelEntry>();
    private final boolean builtIn;
    private final ModelType type;
    private final AtomicReference<String> source = new AtomicReference<String>();

    VdbModelEntry( final IPath name,
                   final Vdb vdb,
                   final IProgressMonitor monitor ) {
        super(name, vdb, monitor);
        indexName = IndexUtil.getRuntimeIndexFileName(findFileInWorkspace());
        synchronizeModelEntry(monitor);
        final Resource model = findModel();
        builtIn = (model == null ? false : getFinder().isBuiltInResource(model));
        type = ((EmfResource)model).getModelType();
    }

    VdbModelEntry( final ModelElement model,
                   final Vdb vdb,
                   final IProgressMonitor monitor ) {
        super(Path.fromPortableString(model.getPath()), vdb, monitor);
        type = ModelType.get(model.getType());
        visible.set(model.isVisible());
        boolean builtIn = false;
        String indexName = null;
        for (final PropertyElement property : model.getProperties()) {
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
        // Clear set of dependents and inverse relationships
        for (final VdbModelEntry entry : dependsUpon) {
            entry.dependentOf.remove(this);
            if (entry.isBuiltIn()) entry.dispose();
        }
        dependsUpon.clear();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.VdbEntry#dispose()
     */
    @Override
    final void dispose() {
        super.dispose();
        for (final VdbModelEntry entry : dependentOf)
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

    /**
     * @return the immutable set of model entries that depend upon this model entry
     */
    public final Set<VdbModelEntry> getDependentOf() {
        return Collections.unmodifiableSet(dependentOf);
    }

    /**
     * @return the immutable set of model entries upon which this model entry depends
     */
    public final Set<VdbModelEntry> getDependsUpon() {
        return Collections.unmodifiableSet(dependsUpon);
    }

    private ResourceFinder getFinder() {
        try {
            return ModelerCore.getModelContainer().getResourceFinder();
        } catch (final Exception error) {
            VdbPlugin.throwRuntimeExeption(error);
            return null;
        }
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
    public final Set<IMarker> getProblems() {
        return Collections.unmodifiableSet(problems);
    }

    /**
     * @return type
     */
    public final ModelType getType() {
        return type;
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
    }

    /**
     * @param source
     */
    public final void setDataSource( final String source ) {
        final String oldSource = getDataSource();
        if (oldSource != null && oldSource.equals(source)) return;
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
            Collections.addAll(problems, workspaceFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE));
            // Also add dependent models
            final Resource model = findModel();
            for (final Resource dependentModel : getFinder().findReferencesFrom(model, true, false)) {
                final String name = dependentModel.getURI().toString();
                VdbModelEntry dependentEntry = null;
                for (final VdbModelEntry existingEntry : getVdb().getModelEntries())
                    if (name.equals(existingEntry.getName().toString())) {
                        dependentEntry = existingEntry;
                        break;
                    }
                if (dependentEntry == null) dependentEntry = getVdb().addModelEntry(Path.fromPortableString(name), monitor);
                dependsUpon.add(dependentEntry);
                dependentEntry.dependentOf.add(this);
            }
            // Copy snapshot of workspace file index to VDB folder
            final Index index = IndexUtil.getIndexFile(indexName, IndexUtil.INDEX_PATH + indexName, getName().lastSegment());
            FileUtils.copy(index.getIndexFile(), getIndexFile().getParentFile(), true);
        } catch (final Exception error) {
            VdbPlugin.throwRuntimeExeption(error);
        }
    }
}
