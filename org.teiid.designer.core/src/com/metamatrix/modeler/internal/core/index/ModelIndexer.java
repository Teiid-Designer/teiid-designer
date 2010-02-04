/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.index.IDocument;
import com.metamatrix.core.index.IIndex;
import com.metamatrix.core.index.IIndexerOutput;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.DuplicateResourceException;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.index.ModelDocument;
import com.metamatrix.modeler.core.index.ResourceIndexer;
import com.metamatrix.modeler.core.util.ModelObjectCollector;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.metadata.runtime.RuntimeAdapter;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * An <code>ModelIndexer</code> indexes ONE document at each time. It adds the document names and the words references to an
 * IIndex. Each IIndexer can index certain types of document, and should not index the other files.
 */
public class ModelIndexer implements ResourceIndexer {

    /** If true the contents of the index files will be printed to System.out */
    public static boolean PRINT_INDEX_CONTENTS = false;

    private static final String[] FILE_TYPES = new String[] {"model"}; //$NON-NLS-1$

    private static String INDEX_TYPES = ModelerCore.Util.getString("ModelIndexer.Metadata_Indexes_1"); //$NON-NLS-1$

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    public ModelIndexer() {
    }

    private IndexingContext createQueryContext() {
        Collection resources;
        Container container;
        try {
            // assume model container...modler metadata is for workspace
            container = ModelerCore.getModelContainer();
            ModelWorkspace workspace = ModelerCore.getModelWorkspace();
            if (workspace.isOpen()) {
                resources = Arrays.asList(workspace.getEmfResources());
            } else {
                resources = container.getResources();
            }
            IndexingContext context = new IndexingContext();
            context.setResourcesInContext(resources);
            return context;
        } catch (CoreException e) {
            ModelerCore.Util.log(e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.index.ResourceIndexer#getIndexType()
     */
    public String getIndexType() {
        return INDEX_TYPES;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.internal.core.index.IIndexer#getFileTypes()
     */
    public String[] getFileTypes() {
        return FILE_TYPES;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.internal.core.index.IIndexer#setFileTypes(java.lang.String[])
     */
    public void setFileTypes( final String[] fileTypes ) {
    }

    /* (non-Javadoc)
     * @see com.metamatrix.internal.core.index.IIndexer#shouldIndex(com.metamatrix.internal.core.index.IDocument)
     */
    public boolean shouldIndex( final IDocument document ) {
        if (document instanceof ModelDocument) {
            return true;
        }
        return false;
    }

    /**
     * Indexes the given emf resource, adding the resource name and the word references to this resource to the given
     * <code>ModelIndex</code>.The caller should use <code>shouldIndex()</code> first to determine whether this indexer handles
     * the given type of EmfResource, and only call this method if so.
     */
    public final void index( final IDocument document,
                             final IIndexerOutput output ) {
        ArgCheck.isNotNull(document);
        ArgCheck.isNotNull(output);

        if (!this.shouldIndex(document)) {
            return;
        }

        // ------------------------------------
        // Defect 22774 - (BML 10/3/06) added ability to set the initial index array size prior to creating the index words.
        // This should improve performance a little for little models and a little more for large models with many indexes.
        // ------------------------------------
        int initialIndexSize = 10;
        List eObjects = Collections.EMPTY_LIST;
        String modelPath = null;

        if (document instanceof ModelDocument) {
            // Get the indexName from the given ModelDocument
            final ModelDocument modelDocument = (ModelDocument)document;
            modelPath = modelDocument.getIResource().getFullPath().toString();
            // get the emf resource
            Resource emfResource = modelDocument.getResource();

            final ModelObjectCollector moc = new ModelObjectCollector(emfResource);
            eObjects = moc.getEObjects();
            initialIndexSize += eObjects.size();
        }

        // list of words to be added to the index
        List indexWords = new ArrayList(initialIndexSize);

        // Add any WordEntry instances for the resource being indexed
        this.addResourceWordEntries(document, indexWords);
        // ModelerCore.Util.log(IStatus.INFO, "    -------------- ModelIndexer.index() START ----------------------");
        // ModelerCore.Util.log(IStatus.INFO, "        >> Indexing emfResource = " + document.getName());
        // ModelerCore.Util.log(IStatus.INFO, "        >>           # EObjects = " + eObjects.size());
        if (document instanceof ModelDocument) {

            IndexingContext context = createQueryContext();
            for (final Iterator iter = eObjects.iterator(); iter.hasNext();) {
                final EObject eObject = (EObject)iter.next();
                // Add the appropriate word entries for the given EObject
                this.addIndexWord(eObject, context, modelPath, indexWords);
            }

        }

        // Preprocess the WordEntry list prior to creating the index file
        this.sortWordEntries(indexWords);

        // Add the entries to the index file
        output.addDocument(document);
        addEntries(output, indexWords);

        if (PRINT_INDEX_CONTENTS && !(this instanceof ModelSearchIndexer)) {
            printWordEntryList(indexWords, System.out);
        }

        // free up memory, clear the index words
        indexWords = null;
        // ModelerCore.Util.log(IStatus.INFO, "    -------Finished Indexing END  -----------------------\n");
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.index.ResourceIndexer#indexResource(org.eclipse.core.runtime.IPath, boolean, boolean)
     */
    public void indexResource( final IResource resource,
                               final boolean reuseExistingFile,
                               final boolean addResource ) throws ModelerCoreException {
        ArgCheck.isNotNull(resource);
        ModelWorkspace workspace = ModelerCore.getModelWorkspace();
        ModelResource mResource = workspace.findModelResource(resource);

        // Find the EMF resource for it ...
        Resource emfResource = null;
        if (mResource != null) {
            try {
                emfResource = mResource.getEmfResource(); // will cause opening if not already open; may be problesm opening
            } catch (ModelWorkspaceException err) {
                final Throwable wrapped = err.getException();
                if (wrapped instanceof DuplicateResourceException) {
                    // Don't do anything about this
                } else {
                    throw err;
                }
            }
        }

        // relative path in workspace
        IPath path = resource.getFullPath();
        try {

            String fileName = this.getIndexFileName(path);
            // runtime index path
            String indexFilePath = IndexUtil.getIndexFilePath(IndexUtil.INDEX_PATH, fileName);

            if (ModelerCore.DEBUG || ModelerCore.DEBUG_PROJECT_BUILDER) {
                final Object[] params = new Object[] {fileName, path};
                final String msg = ModelerCore.Util.getString("IndexUtil.DEBUG.Creating_index_file_0_for_resource_1_1", params); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.INFO, msg);
            }

            IDocument document = null;
            if (emfResource != null) {
                if (mResource != null) {
                    // model document
                    document = new ModelDocumentImpl(path.toFile(), resource, emfResource);
                } else {
                    // file document
                    document = new ResourceDocumentImpl(path.toFile(), resource);
                }
            }

            if (document != null) {
                // create the index
                IIndex runtimeIndex = IndexUtil.getNewIndexFile(fileName, indexFilePath, resource.getName());
                if (addResource) {
                    runtimeIndex.add(document, this);
                } else {
                    runtimeIndex.remove(document.getName());
                }
                runtimeIndex.save();
            }

            // if this is a model resource, mark it as indexed
            if (mResource != null) {
                this.setIndexType(mResource);
            }
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, IStatus.OK,
                                        ModelerCore.Util.getString("ModelBuilder.IO_Error_trying_to_index_an_EmfResource_2"), e); //$NON-NLS-1$
            throw new ModelerCoreException(status);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.index.ResourceIndexer#indexResource(org.eclipse.core.resources.IResource, boolean, boolean)
     */
    public void indexResource( IPath path,
                               boolean reuseExistingFile,
                               boolean addResource ) throws ModelerCoreException {
        ArgCheck.isNotNull(path);
        IResource resource = WorkspaceResourceFinderUtil.findIResourceByPath(path);
        indexResource(resource, reuseExistingFile, addResource);
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Set the indexType on the modelResource, each indexer is responsible for setting the appropriate Type.
     * 
     * @param resource The modelResource whose index type is set
     */
    protected void setIndexType( final ModelResource resource ) {
        ArgCheck.isNotNull(resource);
        if (resource.getIndexType() == ModelResource.NOT_INDEXED) {
            resource.setIndexType(ModelResource.METADATA_INDEXED);
        } else if (resource.getIndexType() == ModelResource.SEARCH_INDEXED) {
            resource.setIndexType(ModelResource.INDEXED);
        }
    }

    /**
     * Get the files name that would contain index information for the given resource.
     * 
     * @param path The path to the resource whose index files names are returned
     * @return The index file name for this resource
     */
    protected String getIndexFileName( final IPath path ) {
        return IndexUtil.getIndexFileName(path.toString(), IndexConstants.INDEX_EXT);
    }

    /**
     * Create the {@link com.metamatrix.internal.core.index.impl.WordEntry} instance(s) to be used as the index file record(s) for
     * this EObject instance. The word entries are added to the list provided by the calling method.
     * 
     * @param EObject
     * @param modelPath path to the model within the workspace
     * @param wordEntries the list to which WordEntry instances are added
     * @return
     */
    protected void addIndexWord( final EObject eObject,
                                 IndexingContext context,
                                 final String modelPath,
                                 final List wordEntries ) {
        RuntimeAdapter.addIndexWord(eObject, context, modelPath, wordEntries, true);
    }

    /**
     * Sort the list of WordEntry instances prior to adding the entries to the IIndexerOutput.
     * 
     * @param wordEntries the list to be sorted
     */
    protected void sortWordEntries( final List wordEntries ) {
        // sort the lists alphabetically (for better query performance) using a comparator
        WordEntryComparator wComparator = new WordEntryComparator();
        Collections.sort(wordEntries, wComparator);
    }

    /**
     * Add {@link com.metamatrix.internal.core.index.impl.WordEntry} instance(s) for the resource that is being indexed.
     * 
     * @param document
     * @param wordEntries
     */
    protected void addResourceWordEntries( final IDocument document,
                                           final List wordEntries ) {
        // do nothing by default.
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    /**
     * Add word entries to indexoutput.
     */
    private void addEntries( final IIndexerOutput output,
                             final List entries ) {
        Iterator entryIter = entries.iterator();
        while (entryIter.hasNext()) {
            WordEntry entry = (WordEntry)entryIter.next();
            output.addRef(entry.getWord());
        }
    }

    private void printWordEntryList( final List wordEntries,
                                     final PrintStream stream ) {
        for (Iterator iter = wordEntries.iterator(); iter.hasNext();) {
            WordEntry entry = (WordEntry)iter.next();
            stream.println(entry.toString());
        }
    }

}
