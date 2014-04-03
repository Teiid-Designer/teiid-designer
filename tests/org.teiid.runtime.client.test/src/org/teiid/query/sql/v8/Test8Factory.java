/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v8;

import org.teiid.query.parser.QueryParser;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.WindowFunction;

/**
 *
 */
public class Test8Factory extends AbstractTestFactory {

    /**
     * @param parser
     */
    public Test8Factory(QueryParser parser) {
        super(parser);
    }

    @Override
    public Expression wrapExpression(Expression expr, String... exprName) {
        // Expression are no longer wrapped in ExpressionSymbols. Purely a version 7 concept
        return expr;
    }

    @Override
    public AggregateSymbol newAggregateSymbol(String name, boolean isDistinct, Expression expression) {
        AggregateSymbol as = newNode(ASTNodes.AGGREGATE_SYMBOL);
        as.setName(name);
        as.setDistinct(isDistinct);
        if (expression == null)
            as.setArgs(null);
        else
            as.setArgs(new Expression[] {expression});
        return as;
    }

    @Override
    public WindowFunction newWindowFunction(String name) {
        WindowFunction windowFunction = newNode(ASTNodes.WINDOW_FUNCTION);
        // window function no longer uses name
        return windowFunction;
    }

    @Override
    public RaiseStatement newRaiseStatement(Expression expr) {
        RaiseStatement raiseStatement = newNode(ASTNodes.RAISE_STATEMENT);
        raiseStatement.setExpression(expr);
        return raiseStatement;
    }

}
