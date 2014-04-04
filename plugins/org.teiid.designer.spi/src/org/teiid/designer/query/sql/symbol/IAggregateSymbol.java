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
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;



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

        @Since(Version.TEIID_8_0)
        JSONARRAY_AGG,

        @Since(Version.TEIID_8_0)
        STRING_AGG,

        @Since(Version.TEIID_8_0)
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
