/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.TransactionRunnable;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.util.Util;

/**
 * ModelResourceIndexSelector finds all index files associated with any projects and resources within the model workspace
 */
public class ModelResourceIndexSelector extends AbstractIndexSelector {

    private static final Index[] EMPTY_INDEX_ARRAY = new Index[0];

    private Resource resource;

    // private Index[] indexes;

    /**
     * Construct an instance of ModelResourceIndexSelector
     */
    public ModelResourceIndexSelector( final Resource resource ) {
        ArgCheck.isNotNull(resource);
        this.resource = resource;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.index.IndexSelector#getIndexes()
     */
    @Override
    public Index[] getIndexes() {
        // update indexes if needed
        updateIndexes();
        // boolean indexesUpdated = updateIndexes();
        // if(indexesUpdated ) {
        // ArrayList tmp = new ArrayList();

        List modelImports = this.getModelImports();
        File[] indexFiles = this.getIndexFiles(modelImports);

        return IndexUtil.getExistingIndexes(indexFiles);
        // }
        // for (int i = 0; i < indexFiles.length; i++) {
        // final File indexFile = indexFiles[i];
        // if (IndexUtil.indexFileExists(indexFile.getAbsolutePath())) {
        // Index theIndex = IndexUtil.getIndexFile(indexFile.getName(), indexFile.getAbsolutePath(),
        // this.resource.getURI().lastSegment());
        // if(theIndex != null ) {
        // tmp.add(theIndex);
        // }
        // }
        // }
        //
        // this.indexes = new Index[tmp.size()];
        // tmp.copyInto(this.indexes);
        // }
        //
        // return this.indexes;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * Return all the imports to resources that are being looked up for index info.
     * 
     * @return List The list of model imports.
     */
    public List getModelImports() {
        ModelAnnotation model = Util.getModelAnnotation(this.resource);
        if (model != null) {
            return model.getModelImports();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Return all the imports to resources that are being looked up for index info.
     * 
     * @return List The list of model imports.
     */
    public Collection getNonIndexedResources() {
        List imports = getModelImports();
        Collection iResources = new ArrayList(imports.size() + 1);
        for (final Iterator importIter = imports.iterator(); importIter.hasNext();) {
            ModelImport mdlImport = (ModelImport)importIter.next();
            String importedResourcePath = mdlImport.getPath();
            if (importedResourcePath != null) {
                ModelResource importResource = ModelerCore.getModelWorkspace().findModelResource(new Path(importedResourcePath));
                if (importResource != null && importResource.getIndexType() != ModelResource.INDEXED
                    && importResource.getIndexType() != ModelResource.METADATA_INDEXED) {
                    iResources.add(importResource.getResource());
                }
            }
        }
        ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(this.getResource());
        if (modelResource.getIndexType() != ModelResource.INDEXED
            && modelResource.getIndexType() != ModelResource.METADATA_INDEXED) {
            iResources.add(modelResource.getResource());
        }
        return iResources;
    }

    /**
     * Update the indexes by indexing any dependent models in the workspace that need to be indexed.
     * 
     * @since 4.2
     */
    private boolean updateIndexes() {
        try {
            // Gather a list of all ModelResource instance in the workspace
            // that have unsaved changes
            final Collection nonIndexedResources = getNonIndexedResources();
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
            ModelerCore.Util.log(IStatus.ERROR, e, ModelerCore.Util.getString("ModelResourceIndexSelector.0")); //$NON-NLS-1$
        }
        return false;
    }

    /**
     * Return the reference to the EMF resource used to construct this IndexSelector
     * 
     * @return Resource
     */
    public Resource getResource() {
        return this.resource;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(100);
        sb.append("ModelResourceIndexSelector ["); //$NON-NLS-1$
        Index[] indexes = EMPTY_INDEX_ARRAY;
        indexes = getIndexes();
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
    // P R I V A T E M E T H O D S
    // ==================================================================================

    private File[] getIndexFiles( final List modelImports ) {
        ArgCheck.isNotNull(modelImports);
        ArrayList tmp = new ArrayList();

        // get the index files name for the imports followed by
        // the index files for the resource itsels. (Defect 13933)
        for (Iterator iter = modelImports.iterator(); iter.hasNext();) {
            final ModelImport modelImport = (ModelImport)iter.next();
            if (modelImport != null) {
                File indexFile = getIndexFile(modelImport);
                if (indexFile != null && indexFile.exists()) {
                    tmp.add(indexFile);
                }
            }
        }

        File rsrcIndexFile = new File(IndexUtil.INDEX_PATH, IndexUtil.getRuntimeIndexFileName(this.resource));
        if (rsrcIndexFile.exists()) {
            tmp.add(rsrcIndexFile);
        }

        File[] result = new File[tmp.size()];
        tmp.toArray(result);

        return result;
    }

    private File getIndexFile( final ModelImport modelImport ) {
        String indexFileName = null;
        try {
            indexFileName = IndexUtil.getRuntimeIndexFileName(modelImport.getPath());
        } catch (Exception e) {
            // Exception here means that the index file for the import wasnt found
            // Result is that the returned file will be null
        }
        if (indexFileName != null) {
            return new File(IndexUtil.INDEX_PATH, indexFileName);
        }
        return null;
    }

}
