/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

/**
 * ModelRecord
 */
public interface ModelRecord extends MetadataRecord {
    
    /**
     * Return the URI string for the primary metamodel
     * @return
     */
    String getPrimaryMetamodelUri();
    
    /**
     * Check if OrderBys are supported by these
     * @return true if orderBys are supported
     */
    boolean supportsOrderBy();
    
    /**
     * Check if model supports outer joins
     * @return true if outer joins are supported
     */
    boolean supportsOuterJoin();
    
    /**
     * Check if full table scans are supported
     * @return true if full table scans are supported
     */
    boolean supportsWhereAll();
    
    /**
     * Check if distinct are supported
     * @return true if distinct is supported
     */
    boolean supportsDistinct();
    
    /**
     * Check if joins are supported on this model
     * @return true if joins are supported
     */
    boolean supportsJoin();
    
    /**
     * Check if the model is visible
     * @return
     */
    boolean isVisible();
    
    /**
     * Get the maxSet size allowed
     * @return maximum allowed size in a SET criteria
     */
    int getMaxSetSize();    
    
    /**
     * Check if the model represents a physical model
     * @return
     */
    boolean isPhysical();
    
    /**
     * Return integer indicating the type of Model it is. 
     * @return int
     *
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.MODEL_TYPES
     */
    int getModelType();

}
