/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * ModelResourceIndexSelector finds all index files associated with any projects and resources within the model workspace
 */
public class ModelWorkspaceIndexSelector extends TargetLocationIndexSelector {

    /**
     * Construct an instance of ModelResourceIndexSelector
     */
    public ModelWorkspaceIndexSelector() {
        super(IndexUtil.INDEX_PATH);
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.index.IndexSelector#getIndexes()
     */
    @Override
    public Index[] getIndexes() throws IOException {
        // update indexes if needed
        boolean indexesUpdated = updateIndexes();
        if (indexesUpdated) {
            setIndexes(null);
        }
        return super.getIndexes();
    }

    /**
     * Update the indexes by indexing any models in the workspace that need to be indexed.
     * 
     * @since 4.2
     */
    private boolean updateIndexes() {
        try {
            // Gather a list of all ModelResource instance in the workspace
            // that have unsaved changes
            final Collection nonIndexedResources = ModelWorkspaceManager.getModelWorkspaceManager().getNonIndexedResources(ModelResource.METADATA_INDEXED);
            if (nonIndexedResources != null && !nonIndexedResources.isEmpty()) {
                // If there are models with unsaved changes create temporary index files
                // for use in query validation and resolution
                final TransactionRunnable runnable = new TransactionRunnable() {
                    public Object run( final UnitOfWork uow ) {
                        ModelBuildUtil.createModelIndexes(null, nonIndexedResources);
                        return null;
                    }
                };
                // Execute the indexing within a transaction as this operation may open resources
                // and create new EObjects
                ModelerCore.getModelEditor().executeAsTransaction(runnable, "Updating ModelIndexes", false, false, this); //$NON-NLS-1$

                return true;
            }
        } catch (CoreException e) {
            ModelerCore.Util.log(IStatus.ERROR, e, ModelerCore.Util.getString("ModelWorkspaceIndexSelector.0")); //$NON-NLS-1$
        }
        return false;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(100);
        sb.append("ModelWorkspaceIndexSelector ["); //$NON-NLS-1$
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

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    @Override
    protected FilenameFilter getIndexFileFilter() {
        return IndexFilter.FILTER_INSTANCE;
    }

    // ==================================================================================
    // I N N E R C L A S S
    // ==================================================================================

    public static class IndexFilter implements FilenameFilter {
        public static IndexFilter FILTER_INSTANCE = new IndexFilter();

        public boolean accept( File dir,
                               String name ) {
            IPath path = new Path(name);
            String extension = path.getFileExtension();
            if (extension != null && extension.equals(IndexConstants.INDEX_EXT)) {
                return true;
            }
            return false;
        }
    }
}
