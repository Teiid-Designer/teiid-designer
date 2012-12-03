/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;


/**
 *
 */
public interface IAggregateSymbol extends IFunction {

    public enum AggregateType {
        COUNT,
        SUM,
        AVG,
        MIN,
        MAX,
        XMLAGG,
        TEXTAGG,
        ARRAY_AGG,
        ANY,
        SOME,
        EVERY,
        STDDEV_POP,
        STDDEV_SAMP,
        VAR_POP,
        VAR_SAMP,
        RANK,
        DENSE_RANK,
        ROW_NUMBER,
        USER_DEFINED;
    }

    /**
     * Get the aggregate function type - this will map to one of the reserved words
     * for the aggregate functions.
     * 
     * @return Aggregate function type
     */
    AggregateType getAggregateFunction();
    
    /**
     * Set the aggregate function.  If the aggregate function is an invalid value, an
     * IllegalArgumentException is thrown.
     * 
     * @param aggregateFunction Aggregate function type
     */
    void setAggregateFunction(AggregateType aggregateFunction);
    
}
