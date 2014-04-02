/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.ParseInfo;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.Expression;

/**
 * @param <T>
 */
public abstract class AbstractTest<T extends LanguageObject> {

    protected ITeiidServerVersion teiidVersion;

    protected QueryParser parser;

    /**
     * @param teiidVersion
     */
    public AbstractTest(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
        this.parser = new QueryParser(teiidVersion);
    }

    protected abstract AbstractTestFactory getFactory();

    protected abstract void helpTest(String sql, String expectedSql, T expectedNode);

    protected abstract void helpTest(String sql, String expectedSql, T expectedNode, ParseInfo info);

    protected abstract void helpTestLiteral(Boolean expected, Class<?> expectedType, String sql, String expectedSql);

    protected abstract void helpTestExpression(String sql, String expectedString, Expression expected) throws Exception;

    protected abstract void helpStmtTest(String stmt, String expectedString, Statement expectedStmt) throws Exception;
}
