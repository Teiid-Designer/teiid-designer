/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.transformation.metadata;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;

/**
 * TransformationMetadataFactory
 *
 * @since 8.0
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
    public IQueryMetadataInterface getServerMetadata(final IndexSelector selector) {
        QueryMetadataContext context = new QueryMetadataContext(selector);
        return getServerMetadata(context);
    }    

    /**
     * Return a reference to a {@link QueryMetadataInterface} implementation, the metadata
     * is assumed not to change.
     * @param context Object containing the info needed to lookup metadta.
     * @return the QueryMetadataInterface implementation; never null
     */
    IQueryMetadataInterface getServerMetadata(final QueryMetadataContext context) {
        CoreArgCheck.isNotNull(context);
        // Create the QueryMetadataInterface implementation to use
        // for query validation and resolution
        return new ServerRuntimeMetadata(context);
    }

}