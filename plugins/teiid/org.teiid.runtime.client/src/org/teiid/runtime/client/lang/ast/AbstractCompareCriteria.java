/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.ast;

import org.teiid.runtime.client.lang.parser.TeiidParser;

/**
 *
 */
public abstract class AbstractCompareCriteria extends Criteria implements CriteriaOperator {

    /** The left-hand expression. */
    private Expression leftExpression;
    /**
     * The operator used in the clause.
     * @see #Operator.EQ
     * @see #Operator.NE
     * @see #Operator.LT
     * @see #Operator.GT
     * @see #Operator.LE
     * @see #Operator.GE
     */
    private Operator operator = Operator.EQ;

    /**
     * @param id
     */
    public AbstractCompareCriteria(int id) {
        super(id);
    }

    /**
     * @param p
     * @param id
     */
    public AbstractCompareCriteria(TeiidParser p, int id) {
        super(p, id);
    }

    /**
     * Returns the operator.
     * @return The operator
     */
    public Operator getOperator() {
        return this.operator;
    }

    /**
     * Sets the operator.
     * @param operator
     */
    public void setOperator( Operator operator ) {
        if (operator.isLessThan(Operator.EQ) || operator.isGreaterThan(Operator.GE)) {
            throw new IllegalArgumentException();
        }
        this.operator = operator;
    }

    /**
     * Get left expression.
     * @return Left expression
     */
    public Expression getLeftExpression() {
        return this.leftExpression;
    }

    /**
     * Set left expression.
     * @param expression Left expression
     */
    public void setLeftExpression(Expression expression) {
        this.leftExpression = expression;
    }

    public abstract Expression getRightExpression();
}
