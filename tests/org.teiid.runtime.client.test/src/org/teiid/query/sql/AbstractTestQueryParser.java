/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql;

import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.query.parser.ParseInfo;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class AbstractTestQueryParser extends AbstractTest<Command> {

    /**
     * @param teiidVersion 
     */
    public AbstractTestQueryParser(ITeiidServerVersion teiidVersion) {
        super(teiidVersion);
    }

    @Override
    protected void helpTest(String sql, String expectedString, Command expectedCommand) {
        helpTest(sql, expectedString, expectedCommand, new ParseInfo());
    }

    @Override
    protected void helpTest(String sql, String expectedString, Command expectedCommand, ParseInfo info) {
        Command actualCommand = null;

        try {
            actualCommand = parser.parseCommand(sql, info);
        } catch (Throwable e) {
            fail(e.getMessage());
        }

        assertEquals("Command objects do not match: ", expectedCommand, actualCommand);
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
        Criteria actualCriteria;

        try {
            actualCriteria = parser.parseCriteria(crit);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        assertEquals("Criteria does not match: ", expectedCrit, actualCriteria);
    }

    protected void helpException(String sql) {
        helpException(sql, null);
    }

    protected void helpException(String sql, String expected) {
        try {
            parser.parseCommand(sql);
            fail("Expected exception for parsing " + sql);
        } catch (Exception e) {
            if (expected != null) {
                assertEquals(expected, e.getMessage());
            }
        } catch (Error e) {
            if (expected != null) {
                assertEquals(expected, e.getMessage());
            }
        }
    }

    @Override
    protected void helpTestExpression(String sql, String expectedString, Expression expected) throws Exception {
        Expression actual = parser.parseExpression(sql);
        assertEquals("Command objects do not match: ", expected, actual);
    }

    @Override
    protected void helpStmtTest(String stmt, String expectedString, Statement expectedStmt) throws Exception {
        Statement actualStmt = parser.getTeiidParser(stmt).statement(new ParseInfo());
        assertEquals("Language objects do not match: ", expectedStmt, actualStmt);
    }

    /** SELECT * FROM g1 inner join g2 */
    @Test
    public void testInvalidInnerJoin() {
        helpException("SELECT * FROM g1 inner join g2");
    }

    /** SELECT a FROM m.g GROUP BY a, b HAVING COUNT(AVG(b)) */
    @Test
    public void testFailNestedAggregateInHaving() {
        helpException("SELECT a FROM m.g GROUP BY a, b HAVING COUNT(b) AS x = 5");
    }

    /** SELECT a FROM m.g GROUP BY a, b AS x */
    @Test
    public void testFailAliasInHaving() {
        helpException("SELECT a FROM m.g GROUP BY a, b AS x");
    }

    @Test
    public void testExceptionLength() {
        String sql = "SELECT * FROM Customer where Customer.Name = (select lastname from CUSTOMER where acctid = 9";
        helpException(sql);
    }

    /** SELECT {d'bad'} FROM m.g1 */
    @Test
    public void testDateLiteralFail() {
        helpException("SELECT {d'bad'} FROM m.g1");
    }

    /** SELECT {t 'xyz'} FROM m.g1 */
    @Test
    public void testTimeLiteralFail() {
        helpException("SELECT {t 'xyz'} FROM m.g1");
    }

    /** SELECT a AS or FROM g */
    @Test
    public void testAliasInSelectUsingKeywordFails() {
        helpException("SELECT a AS or FROM g");
    }

    /** SELECT or.a FROM g AS or */
    @Test
    public void testAliasInFromUsingKeywordFails() {
        helpException("SELECT or.a FROM g AS or");
    }

    /** FROM g WHERE a = 'aString' */
    @Test
    public void testFailsNoSelectClause() {
        helpException("FROM g WHERE a = 'aString'");
    }

    /** SELECT a WHERE a = 'aString' */
    @Test
    public void testFailsNoFromClause() {
        helpException("SELECT a WHERE a = 'aString'");
    }

    /** SELECT xx.yy%.a from xx.yy */
    @Test
    public void testFailsWildcardInSelect() {
        helpException("SELECT xx.yy%.a from xx.yy");
    }

    /** SELECT a from g ORDER BY b DSC*/
    @Test
    public void testFailsDSCMisspelled() {
        helpException("SELECT a from g ORDER BY b DSC");
    }

    /** SELECT a, b FROM (SELECT c FROM m.g2) */
    @Test
    public void testSubqueryInvalid() {
        helpException("SELECT a, b FROM (SELECT c FROM m.g2)");
    }

    //as clause should use short names
    @Test
    public void testDynamicCommandStatement2() {
        helpException("create virtual procedure begin execute string z as variables.a1 string, a2 integer into #g; end");
    }

    //using clause should use short names
    @Test
    public void testDynamicCommandStatement3() {
        helpException("create virtual procedure begin execute string z as a1 string, a2 integer into #g using variables.x=variables.y; end");
    }

    //into clause requires as clause
    @Test
    public void testDynamicCommandStatement4() {
        helpException("create virtual procedure begin execute string z into #g using x=variables.y; end");
    }

    @Test
    public void testBadScalarSubqueryExpression() {
        helpException("SELECT e1, length(SELECT e1 FROM m.g1) as X FROM m.g2");
    }

    @Test
    public void testAliasInSingleQuotes() throws Exception {

        GroupSymbol g = getFactory().newGroupSymbol("x.y.z");
        From from = getFactory().newFrom();
        from.addGroup(g);

        AliasSymbol as = getFactory().newAliasSymbol("fooAlias", getFactory().newElementSymbol("fooKey"));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        helpException("SELECT fooKey 'fooAlias' FROM x.\"y\".z");
    }

    @Test
    public void testOrderByWithNumbers_AsNegitiveInt() {
        helpException("SELECT x, y FROM z order by -1");
    }

    @Test
    public void testEmptyOuterJoinCriteria() {
        helpException("select a from b left outer join c on ()");
    }

    @Test
    public void testBadAlias() {
        String sql = "select a as a.x from foo";

        helpException(sql);
    }

    @Test
    public void testUnionJoin1() {
        String sql = "select * from pm1.g1 union all join pm1.g2 where g1.e1 = 1";

        helpException(sql);
    }

    @Test
    public void testTextTableColumns() throws Exception {
        helpException("SELECT * from texttable(foo x string)");
    }

    @Test
    public void testTrim1() {
        helpException("select trim('xy' from e1) from pm1.g1");
    }
}
