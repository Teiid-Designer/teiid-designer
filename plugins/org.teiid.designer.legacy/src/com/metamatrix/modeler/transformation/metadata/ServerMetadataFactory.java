/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.transformation.metadata;

import org.teiid.core.util.ArgCheck;
import com.metamatrix.modeler.core.index.IndexSelector;
import org.teiid.query.metadata.QueryMetadataInterface;

/**
 * TransformationMetadataFactory
 */
public class ServerMetadataFactory {

    private static final ServerMetadataFactory INSTANCE = new ServerMetadataFactory();

    protected ServerMetadataFactory() {}

    public static ServerMetadataFactory getInstance() {
        return INSTANCE;
    }

	/**
     * Return a reference to a {@link QueryMetadataInterface} implementation, the metadata
     * is assumed not to change.
     * @param context Object containing the info needed to lookup metadta.
     * @return the QueryMetadataInterface implementation; never null
     */
    public QueryMetadataInterface getServerMetadata(final IndexSelector selector) {
        QueryMetadataContext context = new QueryMetadataContext(selector);
        return getServerMetadata(context);
    }    

    /**
     * Return a reference to a {@link QueryMetadataInterface} implementation, the metadata
     * is assumed not to change.
     * @param context Object containing the info needed to lookup metadta.
     * @return the QueryMetadataInterface implementation; never null
     */
    QueryMetadataInterface getServerMetadata(final QueryMetadataContext context) {
        ArgCheck.isNotNull(context);
        // Create the QueryMetadataInterface implementation to use
        // for query validation and resolution
        return new ServerRuntimeMetadata(context);
    }
    
    /**
     * Create a {@link QueryMetadataInterface} implementation that maintains a local cache
     * of metadata. For server the state of the metadata should not change anyway.
     * @param selector The indexselector used to lookup index files.
     * @return a new QueryMetadataInterface implementation; never null
     */
    public QueryMetadataInterface createCachingServerMetadata(final IndexSelector selector) {
        QueryMetadataContext context = new QueryMetadataContext(selector);
        return createCachingServerMetadata(context);
    }    

    /**
     * Create a {@link QueryMetadataInterface} implementation that maintains a local cache
     * of metadata. For server the state of the metadata should not change anyway.
     * @param context Object containing the info needed to lookup metadta.
     * @return a new QueryMetadataInterface implementation; never null
     */
    QueryMetadataInterface createCachingServerMetadata(final QueryMetadataContext context) {
        final TransformationMetadata metadata = (TransformationMetadata)getServerMetadata(context);
        return new TransformationMetadataFacade(metadata);
    }

}