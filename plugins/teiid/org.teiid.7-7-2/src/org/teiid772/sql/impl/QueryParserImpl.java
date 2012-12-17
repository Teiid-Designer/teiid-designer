/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl;

import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class QueryParserImpl implements IQueryParser {

    QueryParser queryParser = new QueryParser();
    SyntaxFactory factory = new SyntaxFactory();

    @Override
    public ICriteria parseCriteria(String criteriaString) throws Exception {
        Criteria criteria = queryParser.parseCriteria(criteriaString);
        return (ICriteria) factory.createExpression(criteria);
    }

    @Override
    public ICommand parseCommand(String commandString) throws Exception {
        Command command = queryParser.parseCommand(commandString);
        return (ICommand) factory.createLanguageObject(command);
    }

    @Override
    public ICommand parseDesignerCommand(String commandString) throws Exception {
        Command command = queryParser.parseDesignerCommand(commandString);
        return (ICommand) factory.createLanguageObject(command);
    }

    @Override
    public IExpression parseExpression(String expressionString) throws Exception {
        Expression expression = queryParser.parseExpression(expressionString);
        return factory.createExpression(expression);
    }
    
}
