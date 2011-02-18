/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * ModelResourceIndexSelector finds all index files associated with any projects and resources within the model workspace
 */
public class ModelWorkspaceSearchIndexSelector extends TargetLocationIndexSelector {

    private static final Index[] EMPTY_INDEX_ARRAY = new Index[0];

    private Collection resources = null;
    IProgressMonitor monitor;

    // if this is set to true, we were constructed with a set of resources, which
    // implies that we have been handed a 'selection' scope for a real search.
    // We need to know this so that we can handle the alternate case:
    // if no resources are provided, default to 'workspace' scope.
    // This is the treatment required by classes like FindRelatedObjectsCommandImpl.
    private final boolean bCreatedWithResources;
    private boolean indexesForCreatedResourcesAreSet = false;
    private Index[] cachedIndexes = null;

    /**
     * Construct an instance of ModelResourceIndexSelector
     */
    public ModelWorkspaceSearchIndexSelector() {
        super(IndexUtil.INDEX_PATH);

        bCreatedWithResources = false;
    }

    /**
     * Construct an instance of ModelResourceIndexSelector
     */
    public ModelWorkspaceSearchIndexSelector( IProgressMonitor monitor ) {
        super(IndexUtil.INDEX_PATH);
        this.monitor = monitor;
        bCreatedWithResources = false;
    }

    /**
     * Construct an instance of ModelResourceIndexSelector
     * 
     * @param resources The collection of ModelResources to be indexed
     */
    public ModelWorkspaceSearchIndexSelector( Collection resources ) {

        super(IndexUtil.INDEX_PATH);
        this.resources = resources;
        bCreatedWithResources = true;
    }

    /**
     * Construct an instance of ModelResourceIndexSelector Defect 22774 - This constructor allows ModelEditorImpl the ability to
     * set the search parameters (i.e. resources) so the search to find objects related to deleted/refactored objects can be
     * narrowed based on the object types (i.e. resource types) slated to be deleted.
     * 
     * @param resources The collection of ModelResources to be indexed
     * @param monitor The progress monitor
     */
    public ModelWorkspaceSearchIndexSelector( Collection resources,
                                              IProgressMonitor monitor ) {

        super(IndexUtil.INDEX_PATH);
        this.resources = resources;
        this.monitor = monitor;
        bCreatedWithResources = true;
    }

    private boolean updateIndexes() {

        Collection nonIndexedResources = null;
        try {
            if (this.resources == null) {

                nonIndexedResources = ModelWorkspaceManager.getModelWorkspaceManager().getNonIndexedResources(ModelResource.SEARCH_INDEXED);
            } else {
                nonIndexedResources = new LinkedList();
                for (Iterator rscIter = this.resources.iterator(); rscIter.hasNext();) {
                    if (monitor != null) {
                        monitor.worked(1);
                    }
                    ModelResource mResource = (ModelResource)rscIter.next();
                    if (mResource.getIndexType() != ModelResource.INDEXED
                        && mResource.getIndexType() != ModelResource.SEARCH_INDEXED) {
                        nonIndexedResources.add(mResource.getResource());
                    }
                }
            }
            if (nonIndexedResources != null && !nonIndexedResources.isEmpty()) {
                final Collection resourcesToIndex = new ArrayList(nonIndexedResources);
                // If there are models with unsaved changes create index files
                final TransactionRunnable runnable = new TransactionRunnable() {
                    public Object run( final UnitOfWork uow ) {
                        ModelBuildUtil.createSearchIndexes(monitor, resourcesToIndex);
                        return null;
                    }
                };
                // Execute the indexing within a transaction as this operation may open resources
                // and create new EObjects
                ModelerCore.getModelEditor().executeAsTransaction(runnable, "Updating ModelIndexes", false, false, this); //$NON-NLS-1$
                return true;
            }
        } catch (CoreException e) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 e,
                                 ModelerCore.Util.getString("ModelWorkspaceSearchIndexSelector.Error_trying_to_index_update_search_indexes___1") + e.getMessage()); //$NON-NLS-1$
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.index.IndexSelector#getIndexes()
     */
    @Override
    public Index[] getIndexes() throws IOException {
        // update indexes if needed
        boolean indexesUpdated = updateIndexes();

        // jh Defect 18513 fix: I removed code that returned here if the indexes did not have to be updated

        // if no resource selection is provided and if indexes are not updated
        // return the cached indexes (or buld index objects for whole workspce and return those)
        if (!bCreatedWithResources && !indexesUpdated) {
            return super.getIndexes();
        }

        // clear the cached indexes
        setIndexes(null);

        // if resource selection is available, build index objects for the selected resources
        // index cache may not need to change but rebuild the index objects for the selection any way,
        // the supers getIndex() assumes whole workspce when the cache is empty regardless of the selection
        // so need to make sure cache is properly set

        // --------------------------------
        // Defect 22774 - refactored this a bit so we cache up the index (i.e. only create them once here.
        // --------------------------------
        if (this.resources != null) {
            if (!indexesForCreatedResourcesAreSet || indexesUpdated) {

                ArrayList tmp = new ArrayList();
                for (Iterator rscIter = this.resources.iterator(); rscIter.hasNext();) {

                    String resourceFileName = null;

                    ModelResource mResource = (ModelResource)rscIter.next();

                    try {
                        resourceFileName = mResource.getEmfResource().getURI().lastSegment();
                    } catch (ModelWorkspaceException theException) {
                        ModelerCore.Util.log(theException);
                    }

                    String fileName = IndexUtil.getSearchIndexFileName(mResource);
                    String path = IndexUtil.INDEX_PATH + fileName;
                    if (IndexUtil.indexFileExists(path)) {
                        Index theIndex = IndexUtil.getIndexFile(fileName, path, resourceFileName);
                        if (theIndex != null) {
                            tmp.add(theIndex);
                        }
                    }

                }
                cachedIndexes = new Index[tmp.size()];
                tmp.toArray(cachedIndexes);

                indexesForCreatedResourcesAreSet = true;
            }
            // cache the indexes
            super.setIndexes(cachedIndexes);

            return cachedIndexes;
        }

        // index cache is set by this point in which case just resurn the cached objects,
        // else build objects for all indexes in the workspace and return those.
        return super.getIndexes();
    }

    public void setMonitor( IProgressMonitor monitor ) {
        this.monitor = monitor;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(100);
        sb.append("ModelWorkspaceSearchIndexSelector ["); //$NON-NLS-1$
        Index[] indexes = EMPTY_INDEX_ARRAY;
        try {
            indexes = getIndexes();
        } catch (IOException e) {
            // do nothing
        }
        for (int i = 0; i < indexes.length; i++) {
            if (i > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(indexes[i].getIndexFile());
        }
        sb.append("]"); //$NON-NLS-1$
        return sb.toString();
    }

    @Override
    protected FilenameFilter getIndexFileFilter() {
        return IndexFilter.FILTER_INSTANCE;
    }

    public static class IndexFilter implements FilenameFilter {
        public static IndexFilter FILTER_INSTANCE = new IndexFilter();

        public boolean accept( File dir,
                               String name ) {
            IPath path = new Path(name);
            String extension = path.getFileExtension();
            if (extension != null && extension.equals(IndexConstants.SEARCH_INDEX_EXT)) {
                return true;
            }
            return false;
        }
    }
}
