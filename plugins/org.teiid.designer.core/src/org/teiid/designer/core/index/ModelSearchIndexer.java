/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.index;

import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.index.IDocument;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.search.runtime.SearchRuntimeAdapter;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.xmi.XMIHeader;
import org.teiid.designer.metamodels.core.ModelType;


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

    @Override
    protected void addIndexWord( final EObject eObject,
                                 final IndexingContext context,
                                 final String modelPath,
                                 final List wordEntries ) {
        addIndexWord(eObject, modelPath, wordEntries);
    }

    /**
     * Create a {@link org.teiid.designer.core.index.index.impl.WordEntry} instance representing a EObject within a resource. This
     * resulting WordEntry is of the form: uuid|metamodelURI#EClass|modelPath|
     * 
     * @see org.teiid.designer.core.index.ModelIndexer#addIndexWord(org.eclipse.emf.ecore.EObject, java.lang.String,
     *      java.util.List)
     */
    protected void addIndexWord( final EObject eObject,
                                 final String modelPath,
                                 final List wordEntries ) {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isNotNull(wordEntries);

        // add all search words for the EObject
        SearchRuntimeAdapter.addObjectSearchWords(eObject, modelPath, wordEntries);
    }

    /**
     * @see org.teiid.designer.core.index.ModelIndexer#addResourceWordEntries(org.teiid.core.index.IDocument,
     *      java.util.List)
     */
    @Override
    protected void addResourceWordEntries( final IDocument document,
                                           final List wordEntries ) {

        if (document instanceof ModelDocument) {
            final ModelDocument modelDocument = (ModelDocument)document;
            // get the emfResource
            final Resource resource = modelDocument.getResource();
            try {
                // find the model resource
                final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource(resource);

                // find the primary meta model URI for the resource
                String primaryMetamodelURI = null;
                if (modelResource != null) {
                    final MetamodelDescriptor descriptor = modelResource.getPrimaryMetamodelDescriptor();
                    if (descriptor != null) primaryMetamodelURI = descriptor.getNamespaceURI();
                }
                // add search word for the resource
                SearchRuntimeAdapter.addResourceSearchWords(modelResource.getUuid(),
                                                            modelDocument.getIResource().getFullPath(),
                                                            resource.getURI().toString(),
                                                            primaryMetamodelURI,
                                                            modelResource.getModelType().getName(),
                                                            wordEntries);
            } catch (final ModelerCoreException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                return;
            }
        } else if (document instanceof ResourceDocument) {
            final ResourceDocument resourceDocument = (ResourceDocument)document;
            final IResource resourceFile = resourceDocument.getIResource();
            final XMIHeader header = ModelUtil.getXmiHeader(resourceFile);
            if (header != null) // add search word for the resource
            SearchRuntimeAdapter.addResourceSearchWords(header.getUUID(),
                                                        resourceDocument.getIResource().getFullPath(),
                                                        URI.createFileURI(resourceFile.getLocation().toString()),
                                                        header.getPrimaryMetamodelURI(),
                                                        ModelType.VDB_ARCHIVE_LITERAL.getName(),
                                                        wordEntries);
        }
    }

    /**
     * Get all the files names that would contain index information for the given resource.
     * 
     * @param resource The resource whose index files names are returned
     * @return The index file names for this resource
     */
    @Override
    protected String getIndexFileName( final IPath path ) {
        return IndexUtil.getIndexFileName(path.toString(), IndexConstants.SEARCH_INDEX_EXT);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.index.ResourceIndexer#getIndexType()
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
    protected void setIndexType( final ModelResource resource ) {
        CoreArgCheck.isNotNull(resource);
        if (resource.getIndexType() == ModelResource.NOT_INDEXED) resource.setIndexType(ModelResource.SEARCH_INDEXED);
        else if (resource.getIndexType() == ModelResource.METADATA_INDEXED) resource.setIndexType(ModelResource.INDEXED);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.core.index.IIndexer#shouldIndex(org.teiid.designer.core.index.IDocument)
     */
    @Override
    public boolean shouldIndex( final IDocument document ) {
        if (document instanceof ResourceDocument) return true;
        return false;
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

}
