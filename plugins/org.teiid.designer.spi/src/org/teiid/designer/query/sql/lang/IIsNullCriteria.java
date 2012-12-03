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
public interface IIsNullCriteria extends IPredicateCriteria {

    /**
     * Get the expression
     * 
     * @return expression
     */
    IExpression getExpression();

    /**
     * Set the expression
     * 
     * @param expression
     */
    void setExpression(IExpression expression);

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

}
