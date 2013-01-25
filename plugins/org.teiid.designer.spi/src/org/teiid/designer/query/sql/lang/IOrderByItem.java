/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import org.teiid.designer.query.sql.ILanguageVisitor;


/**
 *
 */
public interface IOrderByItem<E extends IExpression, LV extends ILanguageVisitor> 
    extends ILanguageObject<LV> {

    /**
     * Get the symbol value
     * 
     * @return symbol value
     */
    E getSymbol();
    
    /**
     * Set the symbol value
     * 
     * @param symbol
     */
    void setSymbol(E symbol);

    /**
     * Is the order ascending
     * 
     * @return true if ascending
     */
    boolean isAscending();
}
