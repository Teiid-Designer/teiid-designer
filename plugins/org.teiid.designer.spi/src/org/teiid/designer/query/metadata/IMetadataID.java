/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.metadata;

import java.util.List;

/**
 *
 */
public interface IMetadataID {
    
    /**
     * Get ID value 
     * 
     * @return ID value
     */
    String getID();
    
    /** 
     * @return Returns the originalMetadataID.
     */
    Object getOriginalMetadataID();
    
    /**
     * Get elements - only valid for groups
     * 
     * @return List of TempMetadataID for groups, null for elements
     */
    List<IMetadataID> getElements();
    
    /**
     * Get type - only valid for elements
     *
     * @return Type for elements, null for groups
     */
    Class<?> getType();
}
