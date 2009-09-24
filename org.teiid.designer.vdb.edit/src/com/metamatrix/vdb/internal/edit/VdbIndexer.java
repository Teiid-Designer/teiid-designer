/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.index.IDocument;
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.index.IIndexerOutput;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.index.VDBDocument;
import com.metamatrix.modeler.core.util.ModelObjectCollector;
import com.metamatrix.modeler.internal.core.index.WordEntryComparator;
import com.metamatrix.modeler.internal.core.metadata.runtime.RuntimeAdapter;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * VdbIndexer
 */
public class VdbIndexer implements IIndexer {

    /** If true the contents of the index files will be printed to System.out */
    public static boolean PRINT_INDEX_CONTENTS = false;

    private static final String[] FILE_TYPES = new String[] {ModelUtil.EXTENSION_XMI, ModelUtil.EXTENSION_XSD,
        ModelUtil.EXTENSION_VDB};

    // sort the lists alphabetically (for better query performance) using a comporator
    private static final WordEntryComparator wComparator = new WordEntryComparator();

    // map of index name to collection of words entries
    private Map indexToCollectionMap = new HashMap();

    // boolean to indicate all te required words have been collected
    // already
    private boolean wordsCollected = false;

    /* (non-Javadoc)
     * @see com.metamatrix.core.index.IIndexer#getFileTypes()
     */
    public String[] getFileTypes() {
        return FILE_TYPES;
    }

    private IndexingContext context;

    public VdbIndexer( final IndexingContext context ) {
        ArgCheck.isNotNull(context);
        this.context = context;
    }

    public VdbIndexer() {
    }

    /**
     * The indexer iterated through the contents of a EmfResource and collects word entries for differrent entities. It sorts
     * these entries alphabetically before writing to an output that would update entity specific index files.
     */
    public void index( final IDocument document,
                       final IIndexerOutput output ) {
        ArgCheck.isInstanceOf(VDBDocument.class, document);
        ArgCheck.isNotNull(output);

        // get the indexName from the given ModelDocument
        final VDBDocument vdbDocument = (VDBDocument)document;
        final String indexName = vdbDocument.getIndexName();

        // collect the WordEntries only if they have not already been collected
        if (!this.wordsCollected) {
            final Collection wordEntries = new HashSet();
            // Iterate througth the contents of an EmfResources collecting WordEntries
            final Collection resources = vdbDocument.getResources();
            for (final Iterator resourceIter = resources.iterator(); resourceIter.hasNext();) {
                Resource resource = (Resource)resourceIter.next();
                if (resource == null) {
                    continue;
                }
                String modelPath = null;
                if (ModelUtil.isModelFile(resource)) {
                    modelPath = vdbDocument.getModelPath(resource.getURI());
                }
                if (StringUtil.isEmpty(modelPath)) {
                    final URI uri = resource.getURI();
                    if (uri != null) {
                        modelPath = uri.toString();
                    }
                }

                // Collect all the EObject instances in the EMF resource using the
                // ModelObjectCollector class to avoid a ConcurrentModificationException
                // that may occur when using the TreeIterator (emfResource.getAllContents())
                final ModelObjectCollector moc = new ModelObjectCollector(resource);
                final List eObjects = moc.getEObjects();

                // Iterate througth the contents of the EmfResource collecting WordEntries
                for (final Iterator iter = eObjects.iterator(); iter.hasNext();) {
                    final EObject eObject = (EObject)iter.next();
                    // Add the appropriate word entries for the given EObject
                    RuntimeAdapter.addIndexWord(eObject, context, modelPath, wordEntries);
                }
            }

            // collect the entries in collections specific to
            // entity type
            collectEntityWords(wordEntries);

            this.wordsCollected = true;

            if (PRINT_INDEX_CONTENTS) {
                printIndexContents(System.out);
            }
        }

        // addEnties to indexes
        addEntries(document, output, indexName);
    }

    /*
     * @see com.metamatrix.core.index.IIndexer#setFileTypes(java.lang.String[])
     */
    public void setFileTypes( final String[] fileTypes ) {
    }

    /*
     * @see com.metamatrix.core.index.IIndexer#shouldIndex(com.metamatrix.core.index.IDocument)
     */
    public boolean shouldIndex( final IDocument document ) {
        return false;
    }

    // ==================================================================================
    // P R I V A T E M E T H O D S
    // ==================================================================================

    private void printIndexContents( final PrintStream stream ) {
        for (final Iterator iter1 = this.indexToCollectionMap.keySet().iterator(); iter1.hasNext();) {
            String indexName = (String)iter1.next();
            stream.println("\n" + indexName); //$NON-NLS-1$
            Collection wordEntries = (Collection)this.indexToCollectionMap.get(indexName);
            if (wordEntries != null && !wordEntries.isEmpty()) {
                for (Iterator iter2 = wordEntries.iterator(); iter2.hasNext();) {
                    WordEntry entry = (WordEntry)iter2.next();
                    stream.println(entry.toString());
                }
            }
        }
    }

    /**
     * Add word entries to indexoutput.
     */
    private void addEntries( final IDocument document,
                             final IIndexerOutput output,
                             final String indexName ) {
        List entries = (List)this.indexToCollectionMap.get(indexName);
        if (entries != null && entries.size() > 0) {
            output.addDocument(document);
            Collections.sort(entries, wComparator);
            for (Iterator entryIter = entries.iterator(); entryIter.hasNext();) {
                WordEntry entry = (WordEntry)entryIter.next();
                output.addRef(entry.getWord());
            }
        }

        // clean up no longer need this collection
        this.indexToCollectionMap.remove(indexName);
    }

    private void collectEntityWords( final Collection wordEntries ) {
        // Iterate througth the list of WordEntry instances
        // collect WordEntries in entity specific lists
        for (Iterator iter = wordEntries.iterator(); iter.hasNext();) {
            WordEntry entry = (WordEntry)iter.next();
            if (entry == null) {
                continue;
            }

            // if the word starts with a record continuation charachter
            // then the second charachter gives the record type
            char recordType = entry.getWord()[0];
            if (recordType == IndexConstants.RECORD_TYPE.RECORD_CONTINUATION) {
                recordType = entry.getWord()[1];
            }
            switch (recordType) {
                case IndexConstants.RECORD_TYPE.COLUMN:
                    addIndexWord(IndexConstants.INDEX_NAME.COLUMNS_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.TABLE:
                    addIndexWord(IndexConstants.INDEX_NAME.TABLES_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.FOREIGN_KEY:
                case IndexConstants.RECORD_TYPE.PRIMARY_KEY:
                case IndexConstants.RECORD_TYPE.INDEX:
                case IndexConstants.RECORD_TYPE.ACCESS_PATTERN:
                case IndexConstants.RECORD_TYPE.UNIQUE_KEY:
                    addIndexWord(IndexConstants.INDEX_NAME.KEYS_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.VDB_ARCHIVE:
                    addIndexWord(IndexConstants.INDEX_NAME.VDBS_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.MODEL:
                    addIndexWord(IndexConstants.INDEX_NAME.MODELS_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.DATATYPE:
                    addIndexWord(IndexConstants.INDEX_NAME.DATATYPES_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.SELECT_TRANSFORM:
                    addIndexWord(IndexConstants.INDEX_NAME.SELECT_TRANSFORM_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.INSERT_TRANSFORM:
                    addIndexWord(IndexConstants.INDEX_NAME.INSERT_TRANSFORM_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.DELETE_TRANSFORM:
                    addIndexWord(IndexConstants.INDEX_NAME.DELETE_TRANSFORM_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM:
                    addIndexWord(IndexConstants.INDEX_NAME.UPDATE_TRANSFORM_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.CALLABLE:
                case IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER:
                case IndexConstants.RECORD_TYPE.RESULT_SET:
                    addIndexWord(IndexConstants.INDEX_NAME.PROCEDURES_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.PROC_TRANSFORM:
                    addIndexWord(IndexConstants.INDEX_NAME.PROC_TRANSFORM_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM:
                    addIndexWord(IndexConstants.INDEX_NAME.MAPPING_TRANSFORM_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.ANNOTATION:
                    addIndexWord(IndexConstants.INDEX_NAME.ANNOTATION_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.PROPERTY:
                    addIndexWord(IndexConstants.INDEX_NAME.PROPERTIES_INDEX, entry);
                    break;
                case IndexConstants.RECORD_TYPE.FILE:
                    // there is no Index file for this record type, these
                    // records get constructed dynamically in the RuntimeIndexSelector
                    // and are not looked up from an Index file
                    break;
                default:
                    break;
            }
        }
    }

    private void addIndexWord( final String indexName,
                               final WordEntry entry ) {
        if (this.indexToCollectionMap == null) {
            this.indexToCollectionMap = new HashMap();
        }
        List words = (List)this.indexToCollectionMap.get(indexName);
        if (words == null) {
            words = new ArrayList();
            this.indexToCollectionMap.put(indexName, words);
        }
        words.add(entry);
    }

}
