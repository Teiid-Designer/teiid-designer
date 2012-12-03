/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.Collection;

/**
 *
 */
public interface ISetCriteria extends IPredicateCriteria {

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
     * Returns the set of values.  Returns an empty collection if there are
     * currently no values.
     * 
     * @return The collection of Expression values
     */
    Collection<Object> getValues();
      
    /**
     * Sets the values in the set.
     * 
     * @param values The set of value Expressions
     */
    void setValues(Collection<Object> values);

    /**
     * Inverse the set criteria
     * 
     * @param value
     */
    void setNegated(boolean value);

}
