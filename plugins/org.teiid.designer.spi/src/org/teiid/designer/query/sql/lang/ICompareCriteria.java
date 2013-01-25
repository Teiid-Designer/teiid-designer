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
public interface ICompareCriteria<E extends IExpression, LV extends ILanguageVisitor>
    extends IPredicateCriteria<LV> {

    /** Constant indicating the two operands are equal. */
    int EQ = 1;

    /** Constant indicating the two operands are not equal. */
    int NE = 2;

    /** Constant indicating the first operand is less than the second. */
    int LT = 3;

    /** Constant indicating the first operand is greater than the second. */
    int GT = 4;

    /** Constant indicating the first operand is less than or equal to the second. */
    int LE = 5;

    /** Constant indicating the first operand is greater than or equal to the second. */
    int GE = 6;

    /**
     * Returns the operator.
     * @return The operator
     */
    int getOperator();

    /**
     * Set the operator
     * 
     * @param operator
     */
    void setOperator(int operator);
    
    /**
     * Get the string version of the operator
     * 
     * @return operator string
     */
    String getOperatorAsString();
    
    /**
     * Get left expression.
     * @return Left expression
     */
    E getLeftExpression();

    /**
     * Set the left expression
     * 
     * @param expression
     */
    void setLeftExpression(E expression);
    
    /**
     * Get right expression.
     * 
     * @return Right expression
     */
    E getRightExpression();

    /**
     * Set the right expression
     * 
     * @param expression
     */
    void setRightExpression(E expression);
    
}
