/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.connector.metadata.internal;

import java.util.Collection;
import java.util.Map;

public interface IObjectSource {

    /**
     * Return a collection that is results given the groupName(indexName) and search criteria.
     * 
     * @param groupName The name of table/index to search.
     * @param criteria The map of metadata field name to MetadataSearchCriteria
     * @return The collection that is the MetadataRecord objects
     * @since 4.3
     */
    Collection getObjects( String groupName,
                           Map criteria );
}
