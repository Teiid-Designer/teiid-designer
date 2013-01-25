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
public interface IBetweenCriteria<LV extends ILanguageVisitor> extends IPredicateCriteria<LV> {

    /**
     * Has this been negated
     * 
     * @return true if negated
     */
    boolean isNegated();
    
    /**
     * Inverse the negation
     * 
     * @param value
     */
    void setNegated(boolean value);

    /**
     * Get expression.
     * 
     * @return Expression to compare
     */
    IExpression getExpression();

    /**
     * Get the lower expression.
     * 
     * @return the lower expression
     */
    IExpression getLowerExpression();
    
    /**
     * Get the upper expression.
     * 
     * @return the upper expression
     */
    IExpression getUpperExpression();

}
