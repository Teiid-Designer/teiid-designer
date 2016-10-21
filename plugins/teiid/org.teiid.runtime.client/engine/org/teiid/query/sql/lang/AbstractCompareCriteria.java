/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.lang;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.runtime.client.Messages;

/**
 *
 */
public abstract class AbstractCompareCriteria extends Criteria implements PredicateCriteria, CriteriaOperator {

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
    protected Operator operator = Operator.EQ;

    /**
     * @param p
     * @param id
     */
    public AbstractCompareCriteria(ITeiidServerVersion p, int id) {
        super(p, id);
    }

    /**
     * Returns the operator.
     * @return The operator
     */
    public int getOperator() {
        return this.operator.getIndex();
    }

    /**
     * @return string representation of operator
     */
    public String getOperatorAsString() {
        return this.operator.toString();
    }

    /**
     * Sets the operator.
     * @param operator
     */
    public void setOperator( Operator operator ) {
        if (operator.isLessThan(Operator.EQ) || operator.isGreaterThan(Operator.GE)) {
            throw new IllegalArgumentException(Messages.getString(Messages.ERR.ERR_015_010_0001, operator));
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

    /**
     * @return right expression
     */
    public abstract Expression getRightExpression();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.leftExpression == null) ? 0 : this.leftExpression.hashCode());
        result = prime * result + ((this.operator == null) ? 0 : this.operator.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        AbstractCompareCriteria other = (AbstractCompareCriteria)obj;
        if (this.leftExpression == null) {
            if (other.leftExpression != null) return false;
        } else if (!this.leftExpression.equals(other.leftExpression)) return false;
        if (this.operator != other.operator) return false;
        return true;
    }
}
