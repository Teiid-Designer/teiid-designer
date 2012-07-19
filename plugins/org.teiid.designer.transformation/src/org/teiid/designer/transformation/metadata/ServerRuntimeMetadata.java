/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.transformation.metadata;

import org.teiid.core.TeiidComponentException;
import org.teiid.designer.core.index.IEntryResult;
import org.teiid.designer.core.index.Index;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.SimpleIndexUtil;
import org.teiid.designer.transformation.TransformationPlugin;

/**
 * Metadata implementation used by server to resolve queries.
 * 
 */
public class ServerRuntimeMetadata extends TransformationMetadata {

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
     * ServerRuntimeMetadata constructor
     * @param context Object containing the info needed to lookup metadta.
     */    
    public  ServerRuntimeMetadata(final QueryMetadataContext context) {
        super(context);
    }

    //==================================================================================
    //                   O V E R R I D D E N   M E T H O D S
    //==================================================================================

    /**
     * Return the array of MtkIndex instances representing core indexes for the
     * specified record type
     * @param recordType The type of record to loop up the indexes that conyains it
     * @param selector The indexselector that has access to indexes
     * @return The array if indexes
     * @throws QueryMetadataException
     */
    @Override
    protected Index[] getIndexes(final char recordType, final IndexSelector selector) throws TeiidComponentException {
        // The the index file name for the record type
        try {
            final String indexName = SimpleIndexUtil.getIndexFileNameForRecordType(recordType);
            return SimpleIndexUtil.getIndexes(indexName, selector);            
        } catch(Exception e) {
            throw new TeiidComponentException(e, TransformationPlugin.Util.getString("TransformationMetadata.Error_trying_to_obtain_index_file_using_IndexSelector_1",selector)); //$NON-NLS-1$
        }
    }

    /** 
     * @see org.teiid.designer.transformation.metadata.TransformationMetadata#queryIndex(org.teiid.designer.core.index.core.index.impl.Index[], char[], boolean, boolean)
     * @since 4.2
     */
    @Override
    protected IEntryResult[] queryIndex(final Index[] indexes,
                                        char[] pattern,
                                        boolean isPrefix,
                                        boolean returnFirstMatch) throws TeiidComponentException {
        try {
            return super.queryIndex(indexes, pattern, isPrefix, returnFirstMatch);
        } catch(TeiidComponentException e) {
            if(!this.getIndexSelector().isValid()) {
                throw new TeiidComponentException(TransformationPlugin.Util.getString("ServerRuntimeMetadata.invalid_selector")); //$NON-NLS-1$
            }
            throw e;
        }
    }
}