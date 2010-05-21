/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.transformation.metadata;

import java.util.Collection;
import com.metamatrix.modeler.core.index.IndexSelector;


/** 
 * This class contains all the information needed for lookup of metadata.
 * @since 4.2
 */
public class QueryMetadataContext {

    // 	The index selector to use for index file queries    
    private IndexSelector indexSelector;

    // collection of EMF resources to search for metadata
    private Collection eResources;    

    // restrict the search to dependent resources
    private boolean restrictedSearch;

    /** 
     * QueryMetadataContext
     * @param indexSelector The indexSelector to set.
     * @since 4.2
     */
    public QueryMetadataContext(final IndexSelector indexSelector) {
        this.indexSelector = indexSelector;
    }
    
    /** 
     * QueryMetadataContext
     * @param indexSelector The indexSelector to set.
     * @since 4.2
     */
    public QueryMetadataContext() {
    }

    /**
     * Get the index selector to use for index file queries.
     * Never null
     */
    public IndexSelector getIndexSelector() {
        return this.indexSelector;
    }

    /**
     * @param indexSelector The indexSelector to set.
     * @since 4.2
     */
    public void setIndexSelector(IndexSelector indexSelector) {
        this.indexSelector = indexSelector;
    }

    /** 
     * Check if the search needs to be restricted to dependent resources
     * or search among resources to which there are model imports.
     * @return Returns the restrictedSearch.
     * @since 4.2
     */
    public boolean isRestrictedSearch() {
        return this.restrictedSearch;
    }

    /** 
     * @param restrictedSearch The restrictedSearch to set.
     * @since 4.2
     */
    public void setRestrictedSearch(boolean restrictedSearch) {
        this.restrictedSearch = restrictedSearch;
    }

    /** 
     * Get resources to look up metadata in.
     * @return Returns the eResources.
     * @since 4.2
     */
    public Collection getResources() {
        return this.eResources;
    }

    /** 
     * @param resources The eResources to set.
     * @since 4.2
     */
    public void setResources(Collection resources) {
        this.eResources = resources;
    }    
}