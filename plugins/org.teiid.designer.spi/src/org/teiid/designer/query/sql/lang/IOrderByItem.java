/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;


/**
 *
 */
public interface IOrderByItem extends ILanguageObject {

    /**
     * Get the symbol value
     * 
     * @return symbol value
     */
    IExpression getSymbol();
    
    /**
     * Set the symbol value
     * 
     * @param symbol
     */
    void setSymbol(IExpression symbol);

    /**
     * Is the order ascending
     * 
     * @return true if ascending
     */
    boolean isAscending();
}
