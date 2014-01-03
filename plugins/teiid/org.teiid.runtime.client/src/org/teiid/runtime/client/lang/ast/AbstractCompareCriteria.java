/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.teiid.core.util.Assertion;
import org.teiid.runtime.client.lang.parser.TeiidParser;

/**
 *
 */
public abstract class AbstractCompareCriteria extends Criteria {

    public enum Operator {

        /** Constant indicating the two operands are equal. */
        EQ("="), //$NON-NLS-1$
    
        /** Constant indicating the two operands are not equal. */
        NE("<>", "!="), //$NON-NLS-1$ //$NON-NLS-2$
    
        /** Constant indicating the first operand is less than the second. */
        LT("<"), //$NON-NLS-1$
    
        /** Constant indicating the first operand is greater than the second. */
        GT(">"), //$NON-NLS-1$

        /** Constant indicating the first operand is less than or equal to the second. */
        LE("<="), //$NON-NLS-1$
    
        /** Constant indicating the first operand is greater than or equal to the second. */
        GE(">="); //$NON-NLS-1$

        private Collection<String> symbols = new ArrayList<String>();

        private Operator(String... symbols) {
            this.symbols.addAll(Arrays.asList(symbols));
        }

        public int getIndex() {
            return ordinal() + 1;
        }

        /**
         * Operators can have more than one symbol
         * representing them
         *
         * @return collection of symbols delineating the operator
         */
        public Collection<String> getSymbols() {
            return symbols;
        }

        /**
         * @param other
         *
         * @return this operator's index is less than other's
         */
        public boolean isLessThan(Operator other) {
            return this.getIndex() < other.getIndex();
        }

        /**
         * @param other
         *
         * @return this operator's index is greater than other's
         */
        public boolean isGreaterThan(Operator other) {
            return this.getIndex() > other.getIndex();
        }

        /**
         * @param symbol
         *
         * @return the {@link Operator} for the given string representation
         */
        public static Operator getOperator(String symbol) {
            for (Operator operator : Operator.values()) {
                if (operator.equals(symbol))
                    return operator;
            }
   
            Assertion.failed("unknown operator"); //$NON-NLS-1$
            return null;
        }
    }

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
