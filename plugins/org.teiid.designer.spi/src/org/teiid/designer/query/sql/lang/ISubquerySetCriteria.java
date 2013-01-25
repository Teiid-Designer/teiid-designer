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
public interface ISubquerySetCriteria<E extends IExpression, LV extends ILanguageVisitor, C extends ICommand>
    extends IPredicateCriteria<LV>, ISubqueryContainer<C> {

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
     * Sets the negation flag for this criteria.
     * 
     * @param negationFlag true if this criteria contains a NOT; 
     *                  false otherwise
     */
    public void setNegated(boolean negationFlag);
}
