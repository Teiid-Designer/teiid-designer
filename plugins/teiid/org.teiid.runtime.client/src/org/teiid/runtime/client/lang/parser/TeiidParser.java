/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.lang.parser;

import java.io.Reader;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.lang.ParseInfo;
import org.teiid.runtime.client.lang.TeiidNodeFactory.ASTNodes;
import org.teiid.runtime.client.lang.ast.Command;
import org.teiid.runtime.client.lang.ast.Criteria;
import org.teiid.runtime.client.lang.ast.Expression;
import org.teiid.runtime.client.lang.ast.LanguageObject;
import org.teiid.runtime.client.lang.ast.Statement;
import org.teiid.runtime.client.types.DataTypeManagerService;

/**
 *
 */
public interface TeiidParser {

    /**
     * @return teiid version of this parser
     */
    ITeiidServerVersion getVersion();

    /**
     * @return dataTypeManagerService
     */
    DataTypeManagerService getDataTypeService();

    /**
     * Reinitialise the parser against the new sql reader
     *
     * @param sql
     */
    void ReInit(Reader sql);

    /**
     * @param nodeType
     * 
     * @return instance of commonly used node
     */
    <T extends LanguageObject> T createASTNode(ASTNodes nodeType);

    /**
     * Parse an expression
     *
     * @param info
     * @return the expression
     * @throws Exception
     */
    Expression expression(ParseInfo info) throws Exception;

    /**
     * Parse a command
     *
     * @param parseInfo
     * @return the command
     * @throws Exception
     */
    Command command(ParseInfo parseInfo) throws Exception;

    /**
     * Parse a designer command
     *
     * @param parseInfo
     * @return the command
     * @throws Exception
     */
    Command designerCommand(ParseInfo parseInfo) throws Exception;

    /**
     * Parse a criteria
     *
     * @param parseInfo
     * @return the criteria
     * @throws Exception
     */
    Criteria criteria(ParseInfo parseInfo) throws Exception;

    /**
     * Parse a statement
     *
     * @param info
     * @return the statement
     * @throws Exception
     */
    Statement statement(ParseInfo info) throws Exception;

}
