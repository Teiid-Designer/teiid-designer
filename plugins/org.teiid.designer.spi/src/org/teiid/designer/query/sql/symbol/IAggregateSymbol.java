/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.query.sql.symbol;

import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;



/**
 *
 */
public interface IAggregateSymbol<LV extends ILanguageVisitor>
    extends IExpression<LV> {

    public enum Type {        
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

        @Since("8.0.0")
        JSONARRAY_AGG,

        @Since("8.0.0")
        STRING_AGG,

        @Since("8.0.0")
        USER_DEFINED;
    }

    /**
     * Get the aggregate function type - this will map to one of the reserved words
     * for the aggregate functions.
     * 
     * @return Aggregate function type
     */
    Type getAggregateFunction();
    
    /**
     * Set the aggregate function.  If the aggregate function is an invalid value, an
     * IllegalArgumentException is thrown.
     * 
     * @param aggregateFunction Aggregate function type
     */
    void setAggregateFunction(Type aggregateFunction);
    
}
