/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import org.eclipse.emf.ecore.EObject;

/**
 * SqlModelAspect is used to get the differrent properties on a model for runtime metadata.
 */
public interface SqlModelAspect extends SqlAspect {
    
    /**
     * Return the URI string for the primary metamodel 
     * @param eObject The <code>EObject</code> to use
     * @return String
     */
    String getPrimaryMetamodelUri(EObject eObject);
    
    /**
     * Check if OrderBys are supported by these
     * @param eObject The <code>EObject</code> for which orderby prop is checked  
     * @return true if orderBys are supported
     */
    boolean supportsOrderBy(EObject eObject);
    
    /**
     * Check if model supports outer joins
     * @param eObject The <code>EObject</code> for which outer joins prop is checked 
     * @return true if outer joins are supported
     */
    boolean supportsOuterJoin(EObject eObject);
    
    /**
     * Check if full table scans are supported
     * @param eObject The <code>EObject</code> for which table scans prop is checked 
     * @return true if full table scans are supported
     */
    boolean supportsWhereAll(EObject eObject);
    
    /**
     * Check if distinct are supported
     * @param eObject The <code>EObject</code> for which distinct prop is checked 
     * @return true if distinct is supported
     */
    boolean supportsDistinct(EObject eObject);
    
    /**
     * Check if joins are supported on this model
     * @param eObject The <code>EObject</code> for which join prop is checked 
     * @return true if joins are supported
     */
    boolean supportsJoin(EObject eObject);
    
    /**
     * Check if the model is visible
     * @param eObject The <code>EObject</code> for which visibility prop is checked 
     * @return
     */
    boolean isVisible(EObject eObject);
    
    /**
     * Get the maxSet size allowed
     * @param eObject The <code>EObject</code> for which max allowed setsize prop is checked 
     * @return maximum allowed size in a SET criteria
     */
    int getMaxSetSize(EObject eObject);    
    
    /**
     * Return integer indicating the type of Model it is. 
     * @return int
     *
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.MODEL_TYPES
     */
    int getModelType(EObject eObject);

}
