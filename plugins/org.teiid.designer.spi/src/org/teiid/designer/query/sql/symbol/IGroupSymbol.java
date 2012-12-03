/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;


/**
 *
 */
public interface IGroupSymbol extends ISymbol {


    /**
     * Set the name of the group symbol
     * 
     * @param newName
     */
    void setName(String newName);
    
    /**
     * @return
     */
    String getDefinition();
    
    /**
     * @param newName
     */
    void setDefinition(String newName);

    /**
     * @return
     */
    boolean isProcedure();
    
    /**
     * Get the metadata ID that this group symbol resolves to.  If
     * the group symbol has not been resolved yet, this will be null.
     * If the symbol has been resolved, this will never be null.
     * 
     * @return Metadata ID object
     */
    Object getMetadataID();
    
    /**
     * Set the metadata ID that this group symbol resolves to.  It cannot
     * be null.
     * 
     * @param meatdataID Metadata ID object
     * 
     * @throws IllegalArgumentException If metadataID is null
     */
    void setMetadataID(Object metadataID);

}
