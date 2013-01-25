/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.lang;

import java.util.Collection;
import org.teiid.designer.query.sql.ILanguageVisitor;

/**
 *
 */
public interface ISetCriteria<E extends IExpression, LV extends ILanguageVisitor>
    extends IPredicateCriteria<LV> {

    /**
     * Get the expression
     * 
     * @return expression
     */
    E getExpression();

    /**
     * Set the expression
     * 
     * @param expression
     */
    void setExpression(E expression);

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
