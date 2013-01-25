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
public interface IExpressionSymbol<E extends IExpression, LV extends ILanguageVisitor>
    extends ISymbol<LV>, IExpression<LV> {

    /**
     * Get the expression represented by this symbol
     * 
     * @return expression
     */
    E getExpression();
    
    /**
     * Set the expression represented by this symbol.
     * 
     * @param expression Expression for this expression symbol
     */
    void setExpression(E expression);

}
