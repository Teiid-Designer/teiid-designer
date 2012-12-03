/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import org.teiid.designer.query.sql.lang.IExpression;

/**
 *
 */
public interface IAliasSymbol extends ISymbol, IExpression {

    /**
     * Get the underlying symbol
     * 
     * @return Underlying symbol
     */
    IExpression getSymbol();

    /**
     * Set the underlying symbol
     * 
     * @param symbol New symbol
     */
    void setSymbol(IExpression symbol);

}
