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
public interface ISubquerySetCriteria extends IPredicateCriteria, ISubqueryContainer<IQueryCommand> {

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
     * Set the subquery command (either a SELECT or a procedure execution).
     *
     * @param command Command to execute to get the values for the criteria
     */
    void setCommand(IQueryCommand command);

    /**
     * Sets the negation flag for this criteria.
     * 
     * @param negationFlag true if this criteria contains a NOT; 
     *                  false otherwise
     */
    public void setNegated(boolean negationFlag);
}
