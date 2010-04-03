/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.common.xmi.XMIHeader;
import com.metamatrix.core.index.IDocument;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.index.ModelDocument;
import com.metamatrix.modeler.core.index.ResourceDocument;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.search.runtime.SearchRuntimeAdapter;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ModelSearchIndexer
 */
public class ModelSearchIndexer extends ModelIndexer {

    private static String INDEX_TYPES = ModelerCore.Util.getString("ModelSearchIndexer.Search_Indexes_1"); //$NON-NLS-1$

    public ModelSearchIndexer() {
        super();
    }

    // ==================================================================================
    // O V E R R I D D E N M E T H O D S
    // ==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.internal.core.index.IIndexer#shouldIndex(com.metamatrix.internal.core.index.IDocument)
     */
    @Override
    public boolean shouldIndex( final IDocument document ) {
        if (document instanceof ResourceDocument) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.index.ResourceIndexer#getIndexType()
     */
    @Override
    public String getIndexType() {
        return INDEX_TYPES;
    }

    /**
     * Set the indexType on the modelResource, each indexer is responsible for setting the appropriate Type.
     * 
     * @param resource The modelResource whose index type is set
     */
    @Override
    protected void setIndexType( ModelResource resource ) {
        ArgCheck.isNotNull(resource);
        if (resource.getIndexType() == ModelResource.NOT_INDEXED) {
            resource.setIndexType(ModelResource.SEARCH_INDEXED);
        } else if (resource.getIndexType() == ModelResource.METADATA_INDEXED) {
            resource.setIndexType(ModelResource.INDEXED);
        }
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a EObject within a resource. This
     * resulting WordEntry is of the form: uuid|metamodelURI#EClass|modelPath|
     * 
     * @see com.metamatrix.modeler.internal.core.index.ModelIndexer#addIndexWord(org.eclipse.emf.ecore.EObject, java.lang.String,
     *      java.util.List)
     */
    protected void addIndexWord( final EObject eObject,
                                 final String modelPath,
                                 final List wordEntries ) {
        ArgCheck.isNotNull(eObject);
        ArgCheck.isNotNull(wordEntries);

        // add all search words for the EObject
        SearchRuntimeAdapter.addObjectSearchWords(eObject, modelPath, wordEntries);
    }

    @Override
    protected void addIndexWord( final EObject eObject,
                                 IndexingContext context,
                                 final String modelPath,
                                 final List wordEntries ) {
        addIndexWord(eObject, modelPath, wordEntries);
    }

    /**
     * @see com.metamatrix.modeler.internal.core.index.ModelIndexer#addResourceWordEntries(com.metamatrix.internal.core.index.IDocument,
     *      java.util.List)
     */
    @Override
    protected void addResourceWordEntries( final IDocument document,
                                           final List wordEntries ) {

        if (document instanceof ModelDocument) {
            final ModelDocument modelDocument = (ModelDocument)document;
            // get the emfResource
            Resource resource = modelDocument.getResource();
            try {
                // find the model resource
                ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(resource);

                // find the primary meta model URI for the resource
                String primaryMetamodelURI = null;
                if (modelResource != null) {
                    MetamodelDescriptor descriptor = modelResource.getPrimaryMetamodelDescriptor();
                    if (descriptor != null) {
                        primaryMetamodelURI = descriptor.getNamespaceURI();
                    }
                }
                // add search word for the resource
                SearchRuntimeAdapter.addResourceSearchWords(modelResource.getUuid(),
                                                            modelDocument.getIResource().getFullPath(),
                                                            resource.getURI().toString(),
                                                            primaryMetamodelURI,
                                                            modelResource.getModelType().getName(),
                                                            wordEntries);
            } catch (ModelerCoreException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                return;
            }
        } else if (document instanceof ResourceDocument) {
            final ResourceDocument resourceDocument = (ResourceDocument)document;
            IResource resourceFile = resourceDocument.getIResource();
            XMIHeader header = ModelUtil.getXmiHeader(resourceFile);
            if (header != null) {
                // add search word for the resource
                SearchRuntimeAdapter.addResourceSearchWords(header.getUUID(),
                                                            resourceDocument.getIResource().getFullPath(),
                                                            URI.createFileURI(resourceFile.getLocation().toString()),
                                                            header.getPrimaryMetamodelURI(),
                                                            ModelType.VDB_ARCHIVE_LITERAL.getName(),
                                                            wordEntries);
            }
        }
    }

    /**
     * Get all the files names that would contain index information for the given resource.
     * 
     * @param resource The resource whose index files names are returned
     * @return The index file names for this resource
     */
    @Override
    protected String getIndexFileName( IPath path ) {
        return IndexUtil.getIndexFileName(path.toString(), IndexConstants.SEARCH_INDEX_EXT);
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

}
