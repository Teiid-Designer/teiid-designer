/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.sql;

import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.lang.ParseInfo;
import org.teiid.runtime.client.lang.ast.Criteria;
import org.teiid.runtime.client.lang.ast.Expression;
import org.teiid.runtime.client.lang.ast.LanguageObject;
import org.teiid.runtime.client.lang.ast.Query;
import org.teiid.runtime.client.lang.ast.Select;
import org.teiid.runtime.client.lang.ast.Statement;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class AbstractTestCloning extends AbstractTest<LanguageObject> {

    /**
     * @param teiidVersion 
     */
    public AbstractTestCloning(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    protected void helpTest(String sql, LanguageObject expectedNode) {
        helpTest(sql, sql, expectedNode);
    }

    @Override
    protected void helpTest(String sql, String expectedSql, LanguageObject expectedNode) {
        helpTest(sql, expectedSql, expectedNode, new ParseInfo());
    }

    @Override
    protected void helpTest(String sql, String expectedSql, LanguageObject expectedNode, ParseInfo info) {
        LanguageObject clonedNode = null;
        try {
            clonedNode = expectedNode.clone();
        } catch (Throwable e) {
            fail(e.getClass().getSimpleName() + ": " + e.getMessage());
        }

        assertEquals("Cloned object does not match: ", expectedNode, clonedNode);
    }

    @Override
    protected void helpTestLiteral(Boolean expected, Class<?> expectedType, String sql, String expectedSql) {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(expected, expectedType)));

        Query query = getFactory().newQuery();
        query.setSelect(select);

        helpTest(sql, expectedSql, query);
    }

    protected void helpCriteriaTest(String crit, String expectedString, Criteria expectedCrit) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void helpTestExpression(String sql, String expectedString, Expression expected) throws Exception {
        helpTest(sql, expectedString, expected);
    }

    @Override
    protected void helpStmtTest(String stmt, String expectedString, Statement expectedStmt) throws Exception {
        helpTest(stmt, expectedString, expectedStmt);
    }
}
