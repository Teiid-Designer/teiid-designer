/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;


/**
 *
 */
public interface IElementSymbol<G extends IGroupSymbol, LV extends ILanguageVisitor>
    extends ISymbol<LV>, IExpression<LV> {

    public enum DisplayMode {

        // symbol name
        FULLY_QUALIFIED,

        // default
        OUTPUT_NAME,

        // short name
        SHORT_OUTPUT_NAME
    }
    
    /**
     * @return
     */
    G getGroupSymbol();

    /**
     * @param groupSymbol
     */
    void setGroupSymbol(G groupSymbol);

    /**
     * @return
     */
    boolean isExternalReference();
    
    /**
     * @param value
     */
    void setDisplayFullyQualified(boolean value);
    
    /**
     * 
     * @return
     */
    DisplayMode getDisplayMode();
    
    /**
     * @param outputName
     */
    void setDisplayMode(DisplayMode outputName);
    
    /**
     * @param targetType
     */
    void setType(Class<Object> targetType);
    
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
     * @param metadataID Metadata ID object
     * 
     */
    void setMetadataID(Object metadataID);

}
