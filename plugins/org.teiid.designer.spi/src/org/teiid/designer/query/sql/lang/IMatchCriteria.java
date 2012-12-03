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
public interface IMatchCriteria extends IPredicateCriteria {

    /** The internal null escape character */
    public static final char NULL_ESCAPE_CHAR = 0;

    public enum Mode {
        LIKE,
        SIMILAR,
        /**
         * The escape char is typically not used in regex mode.
         */
        REGEX
    }
    
    /**
     * Get the left expression
     * 
     * @return expression
     */
    IExpression getLeftExpression();
    
    /**
     * Set the left expression
     * 
     * @param expression
     */
    void setLeftExpression(IExpression expression);

    /**
     * Get the right expression
     * 
     * @return expression
     */
    IExpression getRightExpression();
    
    /**
     * Set the right expression
     * 
     * @param expression
     */
    void setRightExpression(IExpression expression);

    /**
     * Get the escape character
     * 
     * @return escape character
     */
    char getEscapeChar();
    
    /**
     * Set the escape character
     * 
     * @param escapeChar
     */
    void setEscapeChar(char escapeChar);

    /**
     * Has this been negated
     * 
     * @return true if negated
     */
    boolean isNegated();
    
    /**
     * Inverse the match
     * 
     * @param value
     */
    void setNegated(boolean value);

    /**
     * Get the mode
     * 
     * @return mode
     */
    Mode getMode();
    
}
