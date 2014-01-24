/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.ast;

import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.runtime.client.lang.parser.LanguageVisitor;

/**
 *
 */
@SuppressWarnings( "unused" )
public interface AggregateSymbol extends Node, SingleElementSymbol, Expression, IAggregateSymbol<LanguageVisitor> {

    /**
     * @return name
     */
    String getName();

    /**
     * @param name
     */
    void setName(String name);

    /**
     * @return distinct
     */
    boolean isDistinct();

    /**
     * @param isDistinct
     */
    void setDistinct(boolean isDistinct);

    /**
     * Get function arguments
     *
     * @return Get function arguments
     */
    @Since("8.0.0")
    Expression[] getArgs();

    /**
     * @param arguments
     */
    @Since("8.0.0")
    void setArgs(Expression[] arguments);

    /**
     * @return order by
     */
    public OrderBy getOrderBy();

    /**
     * @param orderBy
     */
    public void setOrderBy(OrderBy orderBy);

    /**
     * @return condition
     */
    public Expression getCondition();
    
    /**
     * @param condition
     */
    void setCondition(Expression condition);
    
    /**
     * @return isWindowed
     */
    boolean isWindowed();
    
    /**
     * @param isWindowed
     */
    void setWindowed(boolean isWindowed);

    /**
     * @return canonicalName
     */
    @Removed("8.0.0")
    String getCanonicalName();

    /**
     * @param canonicalName
     */
    @Removed("8.0.0")
    void setCanonicalName(String canonicalName);

    /**
     * Get the aggregate function type - this will map to one of the reserved words
     * for the aggregate functions.
     * @return Aggregate function type
     */
    Type getAggregateFunction();

    /**
     * @param aggregateFunction
     */
    void setAggregateFunction(String aggregateFunction);

    /**
     * Set the aggregate function.  If the aggregate function is an invalid value, an
     * IllegalArgumentException is thrown.
     * @param aggregateFunction Aggregate function type
     * @see org.teiid.runtime.client.lang.SQLConstants.NonReserved#COUNT
     * @see org.teiid.runtime.client.lang.SQLConstants.NonReserved#SUM
     * @see org.teiid.runtime.client.lang.SQLConstants.NonReserved#AVG
     * @see org.teiid.runtime.client.lang.SQLConstants.NonReserved#MIN
     * @see org.teiid.runtime.client.lang.SQLConstants.NonReserved#MAX
     */
    void setAggregateFunction(Type aggregateFunction);

    /**
     * Get the expression for this symbol
     * @return Expression for this symbol
     */
    @Removed("8.0.0")
    Expression getExpression();

    /**
     * Set the expression represented by this symbol.
     * @param expression Expression for this expression symbol
     */
    @Removed("8.0.0")
    void setExpression(Expression expression);

    /**
     * Clone this aggregate symbol
     */
    @Override
    AggregateSymbol clone();
}
