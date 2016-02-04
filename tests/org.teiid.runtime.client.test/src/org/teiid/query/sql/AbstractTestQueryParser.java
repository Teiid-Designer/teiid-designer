/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.sql.lang.ISPParameter.ParameterInfo;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.language.SQLConstants.NonReserved;
import org.teiid.language.SQLConstants.Reserved;
import org.teiid.language.SortSpecification;
import org.teiid.query.parser.ParseInfo;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.lang.ArrayTable;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CacheHint;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NamespaceItem;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.lang.ProjectedColumn;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetClause;
import org.teiid.query.sql.lang.SetClauseList;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria.PredicateQuantifier;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.TextColumn;
import org.teiid.query.sql.lang.TextTable;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.lang.XMLColumn;
import org.teiid.query.sql.lang.XMLTable;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement;
import org.teiid.query.sql.proc.BranchingStatement.BranchingMode;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.proc.WhileStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.DerivedColumn;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.SearchedCaseExpression;
import org.teiid.query.sql.symbol.TextLine;
import org.teiid.query.sql.symbol.WindowFunction;
import org.teiid.query.sql.symbol.WindowSpecification;
import org.teiid.query.sql.symbol.XMLElement;
import org.teiid.query.sql.symbol.XMLForest;
import org.teiid.query.sql.symbol.XMLParse;
import org.teiid.query.sql.symbol.XMLQuery;
import org.teiid.query.sql.symbol.XMLSerialize;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class AbstractTestQueryParser extends AbstractTest<Command> {

    /**
     * @param teiidVersion 
     */
    public AbstractTestQueryParser(Version teiidVersion) {
        super(teiidVersion);
    }

    protected void helpTest(String sql, String expectedString, Command expectedCommand, boolean designerCommand) {
        helpTest(sql, expectedString, expectedCommand, new ParseInfo(), true);
    }

    protected void helpTest(String sql, String expectedString, Command expectedCommand) {
        helpTest(sql, expectedString, expectedCommand, new ParseInfo(), false);
    }

    protected void helpTest(String sql, String expectedString, Command expectedCommand, ParseInfo info, boolean designerCommand) {
        Command actualCommand = null;
        String actualString = null;

        try {
            if (designerCommand)
                actualCommand = parser.parseDesignerCommand(sql);
            else
                actualCommand = parser.parseCommand(sql, info);

            actualString = actualCommand.toString();
        } catch (Throwable e) {
            fail(e.getMessage());
        }

        if (expectedCommand != null)
            assertEquals("Command objects do not match: ", expectedCommand, actualCommand);

        if (expectedString != null)
            assertEquals("SQL strings do not match: ", expectedString, actualString);
    }

    protected void helpTestLiteral(Boolean expected, Class<?> expectedType, String sql, String expectedSql) {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(expected, expectedType)));

        Query query = getFactory().newQuery();
        query.setSelect(select);

        helpTest(sql, expectedSql, query);
    }

    protected void helpCriteriaTest(String crit, String expectedString, Criteria expectedCrit) {
        Criteria actualCriteria;
        String actualString;

        try {
            actualCriteria = parser.parseCriteria(crit);
            actualString = actualCriteria.toString();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        assertEquals("Criteria does not match: ", expectedCrit, actualCriteria);
        assertEquals("SQL strings do not match: ", expectedString, actualString);
    }

    protected void helpException(String sql) {
        helpException(sql, null);
    }
    
    protected CacheHint helpGetCacheHint(String sql) {
    	return parser.getTeiidParser(sql).getQueryCacheOption(sql);
    }

    protected void helpException(String sql, String expected) {
        try {
            parser.parseCommand(sql);
            fail("Expected exception for parsing " + sql);
        } catch (Exception e) {
            if (expected != null) {
                assertEquals(expected, e.getMessage());
            }
        } catch (AssertionError e) {
            throw e;
        } catch (Error e) {
            if (expected != null) {
                assertEquals(expected, e.getMessage());
            }
        }
    }

    protected void helpTestExpression(String sql, String expectedString, Expression expected) throws Exception {
        Expression actual = parser.parseExpression(sql);
        String actualString = actual.toString();
        if (expected != null)
            assertEquals("Command objects do not match: ", expected, actual);

        assertEquals("SQL strings do not match: ", expectedString, actualString);
    }

    protected void helpStmtTest(String stmt, String expectedString, Statement expectedStmt) throws Exception {
        Statement actualStmt = parser.getTeiidParser(stmt).statement(new ParseInfo());
        String actualString = actualStmt.toString();

        if (expectedStmt != null)
            assertEquals("Language objects do not match: ", expectedStmt, actualStmt);

        assertEquals("SQL strings do not match: ", expectedString, actualString);
    }

 // ======================== Joins ===============================================

    /** SELECT * FROM g1 inner join g2 on g1.a1=g2.a2 */
    @Test
    public void testInnerJoin() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");

        CompareCriteria jcrit = getFactory().newCompareCriteria("g1.a1", Operator.EQ, "g2.a2");
        List<Criteria> crits = new ArrayList<Criteria>();
        crits.add(jcrit);

        JoinPredicate jp = getFactory().newJoinPredicate(g1, g2, JoinType.Types.JOIN_INNER, crits);

        From from = getFactory().newFrom();
        from.addClause(jp);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM g1 inner join g2 on g1.a1=g2.a2", "SELECT * FROM g1 INNER JOIN g2 ON g1.a1 = g2.a2", query);
    }

    /** SELECT * FROM g1 cross join g2 */
    @Test
    public void testCrossJoin() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");

        JoinPredicate jp = getFactory().newJoinPredicate(g1, g2, JoinType.Types.JOIN_CROSS);
        From from = getFactory().newFrom();
        from.addClause(jp);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM g1 cross join g2", "SELECT * FROM g1 CROSS JOIN g2", query);
    }

    /** SELECT * FROM (g1 cross join g2), g3 */
    @Test
    public void testFromClauses() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");

        JoinPredicate jp = getFactory().newJoinPredicate(g1, g2, JoinType.Types.JOIN_CROSS);
        From from = getFactory().newFrom();
        from.addClause(jp);

        UnaryFromClause g3 = getFactory().newUnaryFromClause("g3");
        from.addClause(g3);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM (g1 cross join g2), g3", "SELECT * FROM g1 CROSS JOIN g2, g3", query);
    }

    /** SELECT * FROM (g1 cross join g2) cross join g3 */
    @Test
    public void testMultiCrossJoin() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");

        JoinPredicate jp = getFactory().newJoinPredicate(g1, g2, JoinType.Types.JOIN_CROSS);
        UnaryFromClause g3 = getFactory().newUnaryFromClause("g3");
        JoinPredicate jp2 = getFactory().newJoinPredicate(jp, g3, JoinType.Types.JOIN_CROSS);
        From from = getFactory().newFrom();
        from.addClause(jp2);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM (g1 cross join g2) cross join g3", "SELECT * FROM (g1 CROSS JOIN g2) CROSS JOIN g3", query);
    }

    /** SELECT * FROM (g1 cross join g2) cross join (g3 cross join g4) */
    @Test
    public void testMultiCrossJoin2() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");

        JoinPredicate jp = getFactory().newJoinPredicate(g1, g2, JoinType.Types.JOIN_CROSS);
        UnaryFromClause g3 = getFactory().newUnaryFromClause("g3");
        UnaryFromClause g4 = getFactory().newUnaryFromClause("g4");
        JoinPredicate jp2 = getFactory().newJoinPredicate(g3, g4, JoinType.Types.JOIN_CROSS);
        JoinPredicate jp3 = getFactory().newJoinPredicate(jp, jp2, JoinType.Types.JOIN_CROSS);
        From from = getFactory().newFrom();
        from.addClause(jp3);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM (g1 cross join g2) cross join (g3 cross join g4)",
                 "SELECT * FROM (g1 CROSS JOIN g2) CROSS JOIN (g3 CROSS JOIN g4)",
                 query);
    }

    /** SELECT * FROM g1 cross join (g2 cross join g3) */
    @Test
    public void testMultiCrossJoin3() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");
        UnaryFromClause g3 = getFactory().newUnaryFromClause("g3");

        JoinPredicate jp = getFactory().newJoinPredicate(g2, g3, JoinType.Types.JOIN_CROSS);
        JoinPredicate jp2 = getFactory().newJoinPredicate(g1, jp, JoinType.Types.JOIN_CROSS);
        From from = getFactory().newFrom();
        from.addClause(jp2);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM g1 cross join (g2 cross join g3)", "SELECT * FROM g1 CROSS JOIN (g2 CROSS JOIN g3)", query);
    }

    /** SELECT * FROM g1 cross join (g2 cross join g3), g4 */
    @Test
    public void testMixedJoin() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");
        UnaryFromClause g3 = getFactory().newUnaryFromClause("g3");

        JoinPredicate jp = getFactory().newJoinPredicate(g2, g3, JoinType.Types.JOIN_CROSS);
        JoinPredicate jp2 = getFactory().newJoinPredicate(g1, jp, JoinType.Types.JOIN_CROSS);
        From from = getFactory().newFrom();
        from.addClause(jp2);
        UnaryFromClause g4 = getFactory().newUnaryFromClause("g4");
        from.addClause(g4);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM g1 cross join (g2 cross join g3), g4",
                 "SELECT * FROM g1 CROSS JOIN (g2 CROSS JOIN g3), g4",
                 query);
    }

    /** SELECT * FROM g1 cross join (g2 cross join g3), g4, g5 cross join g6 */
    @Test
    public void testMixedJoin2() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");
        UnaryFromClause g3 = getFactory().newUnaryFromClause("g3");
        UnaryFromClause g4 = getFactory().newUnaryFromClause("g4");
        UnaryFromClause g5 = getFactory().newUnaryFromClause("g5");
        UnaryFromClause g6 = getFactory().newUnaryFromClause("g6");

        JoinPredicate jp = getFactory().newJoinPredicate(g2, g3, JoinType.Types.JOIN_CROSS);
        JoinPredicate jp2 = getFactory().newJoinPredicate(g1, jp, JoinType.Types.JOIN_CROSS);
        JoinPredicate jp3 = getFactory().newJoinPredicate(g5, g6, JoinType.Types.JOIN_CROSS);
        From from = getFactory().newFrom();
        from.addClause(jp2);
        from.addClause(g4);
        from.addClause(jp3);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM g1 cross join (g2 cross join g3), g4, g5 cross join g6",
                 "SELECT * FROM g1 CROSS JOIN (g2 CROSS JOIN g3), g4, g5 CROSS JOIN g6",
                 query);
    }

    /** SELECT * FROM g1, g2 inner join g3 on g2.a=g3.a */
    @Test
    public void testMixedJoin3() {
        UnaryFromClause g1 = getFactory().newUnaryFromClause("g1");
        UnaryFromClause g2 = getFactory().newUnaryFromClause("g2");
        UnaryFromClause g3 = getFactory().newUnaryFromClause("g3");

        CompareCriteria jcrit = getFactory().newCompareCriteria("g2.a", Operator.EQ, "g3.a");

        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(jcrit);
        JoinPredicate jp = getFactory().newJoinPredicate(g2, g3, JoinType.Types.JOIN_INNER, crits);
        From from = getFactory().newFrom();
        from.addClause(g1);
        from.addClause(jp);

        Select select = getFactory().newSelectWithMultileElementSymbol();

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM g1, g2 inner join g3 on g2.a=g3.a", "SELECT * FROM g1, g2 INNER JOIN g3 ON g2.a = g3.a", query);
    }

    /** Select myG.a myA, myH.b from g myG right outer join h myH on myG.x=myH.x */
    @Test
    public void testRightOuterJoinWithAliases() {
        UnaryFromClause g = getFactory().newUnaryFromClause("myG", "g");
        UnaryFromClause h = getFactory().newUnaryFromClause("myH", "h");

        CompareCriteria jcrit = getFactory().newCompareCriteria("myG.x", Operator.EQ, "myH.x");
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(jcrit);
        JoinPredicate jp = getFactory().newJoinPredicate(g, h, JoinType.Types.JOIN_RIGHT_OUTER, crits);
        From from = getFactory().newFrom();
        from.addClause(jp);

        AliasSymbol as = getFactory().newAliasSymbolWithElementSymbol("myA", "myG.a");
        Select select = getFactory().newSelect();
        select.addSymbol(as);
        select.addSymbol(getFactory().newElementSymbol("myH.b"));

        Query query = getFactory().newQuery(select, from);

        helpTest("Select myG.a myA, myH.b from g myG right outer join h myH on myG.x=myH.x",
                 "SELECT myG.a AS myA, myH.b FROM g AS myG RIGHT OUTER JOIN h AS myH ON myG.x = myH.x",
                 query);
    }

    /** Select myG.x myX, myH.y from g myG right join h myH on myG.x=myH.x */
    @Test
    public void testRightJoinWithAliases() {
        UnaryFromClause g = getFactory().newUnaryFromClause("myG", "g");
        UnaryFromClause h = getFactory().newUnaryFromClause("myH", "h");

        CompareCriteria jcrit = getFactory().newCompareCriteria("myG.x", Operator.EQ, "myH.x");
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(jcrit);
        JoinPredicate jp = getFactory().newJoinPredicate(g, h, JoinType.Types.JOIN_RIGHT_OUTER, crits);
        From from = getFactory().newFrom();
        from.addClause(jp);

        AliasSymbol as = getFactory().newAliasSymbolWithElementSymbol("myA", "myG.a");
        Select select = getFactory().newSelect();
        select.addSymbol(as);
        select.addSymbol(getFactory().newElementSymbol("myH.b"));

        Query query = getFactory().newQuery(select, from);

        helpTest("Select myG.a myA, myH.b from g myG right join h myH on myG.x=myH.x",
                 "SELECT myG.a AS myA, myH.b FROM g AS myG RIGHT OUTER JOIN h AS myH ON myG.x = myH.x",
                 query);
    }

    /** Select myG.a myA, myH.b from g myG left outer join h myH on myG.x=myH.x */
    @Test
    public void testLeftOuterJoinWithAliases() {
        UnaryFromClause g = getFactory().newUnaryFromClause("myG", "g");
        UnaryFromClause h = getFactory().newUnaryFromClause("myH", "h");

        CompareCriteria jcrit = getFactory().newCompareCriteria("myG.x", Operator.EQ, "myH.x");
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(jcrit);
        JoinPredicate jp = getFactory().newJoinPredicate(g, h, JoinType.Types.JOIN_LEFT_OUTER, crits);
        From from = getFactory().newFrom();
        from.addClause(jp);

        AliasSymbol as = getFactory().newAliasSymbolWithElementSymbol("myA", "myG.a");
        Select select = getFactory().newSelect();
        select.addSymbol(as);
        select.addSymbol(getFactory().newElementSymbol("myH.b"));

        Query query = getFactory().newQuery(select, from);

        helpTest("Select myG.a myA, myH.b from g myG left outer join h myH on myG.x=myH.x",
                 "SELECT myG.a AS myA, myH.b FROM g AS myG LEFT OUTER JOIN h AS myH ON myG.x = myH.x",
                 query);
    }

    /** Select myG.a myA, myH.b from g myG left join h myH on myG.x=myH.x */
    @Test
    public void testLeftJoinWithAliases() {
        UnaryFromClause g = getFactory().newUnaryFromClause("myG", "g");
        UnaryFromClause h = getFactory().newUnaryFromClause("myH", "h");

        CompareCriteria jcrit = getFactory().newCompareCriteria("myG.x", Operator.EQ, "myH.x");
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(jcrit);
        JoinPredicate jp = getFactory().newJoinPredicate(g, h, JoinType.Types.JOIN_LEFT_OUTER, crits);
        From from = getFactory().newFrom();
        from.addClause(jp);

        AliasSymbol as = getFactory().newAliasSymbolWithElementSymbol("myA", "myG.a");
        Select select = getFactory().newSelect();
        select.addSymbol(as);
        select.addSymbol(getFactory().newElementSymbol("myH.b"));

        Query query = getFactory().newQuery(select, from);

        helpTest("Select myG.a myA, myH.b from g myG left join h myH on myG.x=myH.x",
                 "SELECT myG.a AS myA, myH.b FROM g AS myG LEFT OUTER JOIN h AS myH ON myG.x = myH.x",
                 query);
    }

    /** Select myG.a myA, myH.b from g myG full outer join h myH on myG.x=myH.x */
    @Test
    public void testFullOuterJoinWithAliases() {
        UnaryFromClause g = getFactory().newUnaryFromClause("myG", "g");
        UnaryFromClause h = getFactory().newUnaryFromClause("myH", "h");

        CompareCriteria jcrit = getFactory().newCompareCriteria("myG.x", Operator.EQ, "myH.x");
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(jcrit);
        JoinPredicate jp = getFactory().newJoinPredicate(g, h, JoinType.Types.JOIN_FULL_OUTER, crits);
        From from = getFactory().newFrom();
        from.addClause(jp);

        AliasSymbol as = getFactory().newAliasSymbolWithElementSymbol("myA", "myG.a");
        Select select = getFactory().newSelect();
        select.addSymbol(as);
        select.addSymbol(getFactory().newElementSymbol("myH.b"));

        Query query = getFactory().newQuery(select, from);

        helpTest("Select myG.a myA, myH.b from g myG full outer join h myH on myG.x=myH.x",
                 "SELECT myG.a AS myA, myH.b FROM g AS myG FULL OUTER JOIN h AS myH ON myG.x = myH.x",
                 query);
    }

    /** Select g.a, h.b from g full join h on g.x=h.x */
    @Test
    public void testFullJoin() {
        UnaryFromClause g = getFactory().newUnaryFromClause("g");
        UnaryFromClause h = getFactory().newUnaryFromClause("h");

        CompareCriteria jcrit = getFactory().newCompareCriteria("g.x", Operator.EQ, "h.x");
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(jcrit);
        JoinPredicate jp = getFactory().newJoinPredicate(g, h, JoinType.Types.JOIN_FULL_OUTER, crits);
        From from = getFactory().newFrom();
        from.addClause(jp);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("g.a"));
        select.addSymbol(getFactory().newElementSymbol("h.b"));

        Query query = getFactory().newQuery(select, from);

        helpTest("Select g.a, h.b from g full join h on g.x=h.x", "SELECT g.a, h.b FROM g FULL OUTER JOIN h ON g.x = h.x", query);
    }

    // ======================= Convert ==============================================

    /** SELECT CONVERT(a, string) FROM g */
    @Test
    public void testConversionFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("CONVERT", getFactory().newElementSymbol("a"), getFactory().newConstant("string"));
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT CONVERT(a, string) FROM g", "SELECT CONVERT(a, string) FROM g", query);
    }

    /** SELECT CONVERT(CONVERT(a, timestamp), string) FROM g */
    @Test
    public void testConversionFunction2() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("CONVERT", getFactory().newElementSymbol("a"), getFactory().newConstant("timestamp"));
        Function f2 = getFactory().newFunction("CONVERT", f, getFactory().newConstant("string"));
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f2));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT CONVERT(CONVERT(a, timestamp), string) FROM g",
                 "SELECT CONVERT(CONVERT(a, timestamp), string) FROM g",
                 query);
    }

    // ======================= Functions ==============================================

    /** SELECT 5 + length(concat(a, 'x')) FROM g */
    @Test
    public void testMultiFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("concat", new Expression[] {getFactory().newElementSymbol("a"), getFactory().newConstant("x")});
        Function f2 = getFactory().newFunction("length", new Expression[] {f});
        Function f3 = getFactory().newFunction("+", new Expression[] {getFactory().newConstant(new Integer(5)), f2});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f3));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 5 + length(concat(a, 'x')) FROM g", "SELECT (5 + length(concat(a, 'x'))) FROM g", query);
    }

    /** SELECT REPLACE(a, 'x', 'y') AS y FROM g */
    @Test
    public void testAliasedFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("REPLACE", new Expression[] {getFactory().newElementSymbol("a"), getFactory().newConstant("x"), getFactory().newConstant("y")});
        AliasSymbol as = getFactory().newAliasSymbol("y", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT REPLACE(a, 'x', 'y') AS y FROM g", "SELECT REPLACE(a, 'x', 'y') AS y FROM g", query);
    }

    /** SELECT cast(a as string) FROM g */
    @Test
    public void testCastFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("cast", new Expression[] {getFactory().newElementSymbol("a"), getFactory().newConstant("string")});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT cast(a as string) FROM g", "SELECT cast(a AS string) FROM g", query);
    }

    /** SELECT cast(cast(a as timestamp) as string) FROM g */
    @Test
    public void testMultiCastFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("cast", new Expression[] {getFactory().newElementSymbol("a"), getFactory().newConstant("timestamp")});
        Function f2 = getFactory().newFunction("cast", new Expression[] {f, getFactory().newConstant("string")});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f2));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT cast(cast(a as timestamp) as string) FROM g",
                 "SELECT cast(cast(a AS timestamp) AS string) FROM g",
                 query);
    }

    /** SELECT left(fullname, 3) as x FROM sys.groups */
    @Test
    public void testLeftFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("sys.groups");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("left", new Expression[] {getFactory().newElementSymbol("fullname"), getFactory().newConstant(new Integer(3))});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT left(fullname, 3) as x FROM sys.groups", "SELECT left(fullname, 3) AS x FROM sys.groups", query);
    }

    /** SELECT right(fullname, 3) as x FROM sys.groups */
    @Test
    public void testRightFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("sys.groups");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("right", new Expression[] {getFactory().newElementSymbol("fullname"), getFactory().newConstant(new Integer(3))});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT right(fullname, 3) as x FROM sys.groups", "SELECT right(fullname, 3) AS x FROM sys.groups", query);
    }

    /** SELECT char('x') AS x FROM sys.groups */
    @Test
    public void testCharFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("sys.groups");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("char", new Expression[] {getFactory().newConstant("x")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT char('x') AS x FROM sys.groups", "SELECT char('x') AS x FROM sys.groups", query);
    }

    /** SELECT insert('x', 1, 'a') as x FROM sys.groups */
    @Test
    public void testInsertFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("sys.groups");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("insert", new Expression[] {getFactory().newConstant("x"), getFactory().newConstant(new Integer(1)), getFactory().newConstant("a")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT insert('x', 1, 'a') AS x FROM sys.groups", "SELECT insert('x', 1, 'a') AS x FROM sys.groups", query);
    }

    @Test
    public void testInsertIntoSelect() {
        GroupSymbol g = getFactory().newGroupSymbol("sys.groups");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Insert insert = getFactory().newNode(ASTNodes.INSERT);
        GroupSymbol groupSymbol = getFactory().newGroupSymbol("tempA");
        insert.setGroup(groupSymbol);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(new Integer(1))));

        Query query = getFactory().newQuery();
        query.setSelect(select);

        insert.setQueryExpression(query);

        helpTest("insert into tempA SELECT 1", "INSERT INTO tempA SELECT 1", insert);
    }

    /** SELECT translate('x', 'x', 'y') FROM sys.groups */
    @Test
    public void testTranslateFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("sys.groups");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("translate", new Expression[] {getFactory().newConstant("x"), getFactory().newConstant("x"), getFactory().newConstant("y")});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT translate('x', 'x', 'y') FROM sys.groups", "SELECT translate('x', 'x', 'y') FROM sys.groups", query);
    }

    /** SELECT timestampadd(SQL_TSI_FRAC_SECOND, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionFracSecond() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampadd", new Expression[] {getFactory().newConstant("SQL_TSI_FRAC_SECOND"),
            getFactory().newConstant(new Integer(10)), getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampadd(SQL_TSI_FRAC_SECOND, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampadd(SQL_TSI_FRAC_SECOND, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT timestampadd(SQL_TSI_SECOND, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionSecond() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampadd", new Expression[] {getFactory().newConstant("SQL_TSI_SECOND"), getFactory().newConstant(new Integer(10)),
            getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampadd(SQL_TSI_SECOND, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampadd(SQL_TSI_SECOND, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT timestampadd(SQL_TSI_MINUTE, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionMinute() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampadd", new Expression[] {getFactory().newConstant("SQL_TSI_MINUTE"), getFactory().newConstant(new Integer(10)),
            getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampadd(SQL_TSI_MINUTE, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampadd(SQL_TSI_MINUTE, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT timestampadd(SQL_TSI_HOUR, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionHour() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampadd", new Expression[] {getFactory().newConstant("SQL_TSI_HOUR"), getFactory().newConstant(new Integer(10)),
            getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampadd(SQL_TSI_HOUR, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampadd(SQL_TSI_HOUR, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT timestampadd(SQL_TSI_DAY, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionDay() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampadd", new Expression[] {getFactory().newConstant("SQL_TSI_DAY"), getFactory().newConstant(new Integer(10)),
            getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampadd(SQL_TSI_DAY, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampadd(SQL_TSI_DAY, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT timestampadd(SQL_TSI_WEEK, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionWeek() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampadd", new Expression[] {getFactory().newConstant("SQL_TSI_WEEK"), getFactory().newConstant(new Integer(10)),
            getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampadd(SQL_TSI_WEEK, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampadd(SQL_TSI_WEEK, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT timestampadd(SQL_TSI_QUARTER, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionQuarter() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampadd", new Expression[] {getFactory().newConstant("SQL_TSI_QUARTER"), getFactory().newConstant(new Integer(10)),
            getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampadd(SQL_TSI_QUARTER, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampadd(SQL_TSI_QUARTER, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT timestampadd(SQL_TSI_YEAR, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionYear() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampadd", new Expression[] {getFactory().newConstant("SQL_TSI_YEAR"), getFactory().newConstant(new Integer(10)),
            getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampadd(SQL_TSI_YEAR, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampadd(SQL_TSI_YEAR, 10, '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT timestampdiff(SQL_TSI_FRAC_SECOND, '2003-05-01 10:20:10', '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampdiffFunctionFracSecond() {
        GroupSymbol g = getFactory().newGroupSymbol("my.group1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("timestampdiff", new Expression[] {getFactory().newConstant("SQL_TSI_FRAC_SECOND"),
            getFactory().newConstant("2003-05-01 10:20:10"), getFactory().newConstant("2003-05-01 10:20:30")});
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().wrapExpression(f));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT timestampdiff(SQL_TSI_FRAC_SECOND, '2003-05-01 10:20:10', '2003-05-01 10:20:30') AS x FROM my.group1",
                 "SELECT timestampdiff(SQL_TSI_FRAC_SECOND, '2003-05-01 10:20:10', '2003-05-01 10:20:30') AS x FROM my.group1",
                 query);
    }

    /** SELECT 5 + 2 + 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence1() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("+", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant(new Integer(2))});
        Function f2 = getFactory().newFunction("+", new Expression[] {f, getFactory().newConstant(new Integer(3))});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f2));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 5 + 2 + 3 FROM g", "SELECT ((5 + 2) + 3) FROM g", query);
    }

    /** SELECT 5 + 2 - 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence2() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("+", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant(new Integer(2))});
        Function f2 = getFactory().newFunction("-", new Expression[] {f, getFactory().newConstant(new Integer(3))});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f2));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 5 + 2 - 3 FROM g", "SELECT ((5 + 2) - 3) FROM g", query);
    }

    /** SELECT 5 + 2 * 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence3() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(new Integer(2)), getFactory().newConstant(new Integer(3))});
        Function f2 = getFactory().newFunction("+", new Expression[] {getFactory().newConstant(new Integer(5)), f});

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f2));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 5 + 2 * 3 FROM g", "SELECT (5 + (2 * 3)) FROM g", query);
    }

    /** SELECT 5 * 2 + 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence4() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant(new Integer(2))});
        Function f2 = getFactory().newFunction("+", new Expression[] {f, getFactory().newConstant(new Integer(3))});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f2));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 5 * 2 + 3 FROM g", "SELECT ((5 * 2) + 3) FROM g", query);
    }

    /** SELECT 5 * 2 * 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence5() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant(new Integer(2))});
        Function f2 = getFactory().newFunction("*", new Expression[] {f, getFactory().newConstant(new Integer(3))});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f2));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 5 * 2 * 3 FROM g", "SELECT ((5 * 2) * 3) FROM g", query);
    }

    /** SELECT 1 + 2 * 3 + 4 * 5 FROM g */
    @Test
    public void testArithmeticOperatorPrecedenceMixed1() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(new Integer(2)), getFactory().newConstant(new Integer(3))});
        Function f2 = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(new Integer(4)), getFactory().newConstant(new Integer(5))});
        Function f3 = getFactory().newFunction("+", new Expression[] {getFactory().newConstant(new Integer(1)), f});
        Function f4 = getFactory().newFunction("+", new Expression[] {f3, f2});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f4));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 1 + 2 * 3 + 4 * 5 FROM g", "SELECT ((1 + (2 * 3)) + (4 * 5)) FROM g", query);
    }

    /** SELECT 1 * 2 + 3 * 4 + 5 FROM g */
    @Test
    public void testArithmeticOperatorPrecedenceMixed2() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(new Integer(1)), getFactory().newConstant(new Integer(2))});
        Function f2 = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(new Integer(3)), getFactory().newConstant(new Integer(4))});
        Function f3 = getFactory().newFunction("+", new Expression[] {f, f2});
        Function f4 = getFactory().newFunction("+", new Expression[] {f3, getFactory().newConstant(new Integer(5))});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f4));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 1 * 2 + 3 * 4 + 5 FROM g", "SELECT (((1 * 2) + (3 * 4)) + 5) FROM g", query);
    }

    /** SELECT 5 - 4 - 3 - 2 FROM g --> SELECT ((5 - 4) - 3) - 2 FROM g */
    @Test
    public void testLeftAssociativeExpressions1() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("-", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant(new Integer(4))});
        Function f2 = getFactory().newFunction("-", new Expression[] {f, getFactory().newConstant(new Integer(3))});
        Function f3 = getFactory().newFunction("-", new Expression[] {f2, getFactory().newConstant(new Integer(2))});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f3));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 5 - 4 - 3 - 2 FROM g", "SELECT (((5 - 4) - 3) - 2) FROM g", query);
    }

    /** SELECT 5 / 4 / 3 / 2 FROM g --> SELECT ((5 / 4) / 3) / 2 FROM g */
    @Test
    public void testLeftAssociativeExpressions2() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("/", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant(new Integer(4))});
        Function f2 = getFactory().newFunction("/", new Expression[] {f, getFactory().newConstant(new Integer(3))});
        Function f3 = getFactory().newFunction("/", new Expression[] {f2, getFactory().newConstant(new Integer(2))});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f3));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 5 / 4 / 3 / 2 FROM g", "SELECT (((5 / 4) / 3) / 2) FROM g", query);
    }

    /** SELECT 'a' || 'b' || 'c' FROM g */
    @Test
    public void testConcatOperator1() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("||", new Expression[] {getFactory().newConstant("a"), getFactory().newConstant("b")});
        Function f2 = getFactory().newFunction("||", new Expression[] {f, getFactory().newConstant("c")});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f2));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 'a' || 'b' || 'c' FROM g", "SELECT (('a' || 'b') || 'c') FROM g", query);
    }

    /** SELECT 2 + 3 || 5 + 1 * 2 FROM g */
    @Test
    public void testMixedOperators1() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(new Integer(1)), getFactory().newConstant(new Integer(2))});
        Function f2 = getFactory().newFunction("+", new Expression[] {getFactory().newConstant(new Integer(5)), f});
        Function f3 = getFactory().newFunction("+", new Expression[] {getFactory().newConstant(new Integer(2)), getFactory().newConstant(new Integer(3))});
        Function f4 = getFactory().newFunction("||", new Expression[] {f3, f2});
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(f4));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT 2 + 3 || 5 + 1 * 2 FROM g", "SELECT ((2 + 3) || (5 + (1 * 2))) FROM g", query);
    }

    // ======================= Group By ==============================================

    /** SELECT a FROM m.g GROUP BY b, c */
    @Test
    public void testGroupBy() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        GroupBy groupBy = getFactory().newGroupBy(getFactory().newElementSymbol("b"), getFactory().newElementSymbol("c"));

        Query query = getFactory().newQuery(select, from);
        query.setGroupBy(groupBy);
        helpTest("SELECT a FROM m.g GROUP BY b, c", "SELECT a FROM m.g GROUP BY b, c", query);
    }

    /** SELECT a FROM m.g GROUP BY b, c HAVING b=5*/
    @Test
    public void testGroupByHaving() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        GroupBy groupBy = getFactory().newGroupBy(getFactory().newElementSymbol("b"), getFactory().newElementSymbol("c"));

        CompareCriteria having = getFactory().newCompareCriteria(getFactory().newElementSymbol("b"), Operator.EQ, getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        helpTest("SELECT a FROM m.g GROUP BY b, c HAVING b=5", "SELECT a FROM m.g GROUP BY b, c HAVING b = 5", query);
    }

    /** SELECT COUNT(a) AS c FROM m.g */
    @Test
    public void testAggregateFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newAliasSymbol("c", getFactory().newAggregateSymbol("COUNT", false, getFactory().newElementSymbol("a"))));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT COUNT(a) AS c FROM m.g", "SELECT COUNT(a) AS c FROM m.g", query);
    }

    /** SELECT (COUNT(a)) AS c FROM m.g - this kind of query is generated by ODBC sometimes */
    @Test
    public void testAggregateFunctionWithParens() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newAliasSymbol("c", getFactory().newAggregateSymbol("COUNT", false, getFactory().newElementSymbol("a"))));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT (COUNT(a)) AS c FROM m.g", "SELECT COUNT(a) AS c FROM m.g", query);
    }

    /** SELECT a FROM m.g GROUP BY a HAVING COUNT(b) > 0*/
    @Test
    public void testHavingFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        GroupBy groupBy = getFactory().newGroupBy(getFactory().newElementSymbol("a"));

        Criteria having = getFactory().newCompareCriteria(getFactory().newAggregateSymbol("COUNT", false, getFactory().newElementSymbol("b")),
                                             Operator.GT,
                                             getFactory().newConstant(new Integer(0)));

        Query query = getFactory().newQuery(select, from);
        query.setGroupBy(groupBy);
        query.setHaving(having);

        helpTest("SELECT a FROM m.g GROUP BY a HAVING COUNT(b) > 0", "SELECT a FROM m.g GROUP BY a HAVING COUNT(b) > 0", query);
    }

    /** SELECT a FROM m.g GROUP BY a, b HAVING COUNT(b) > 0 AND b+5 > 0 */
    @Test
    public void testCompoundHaving() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        GroupBy groupBy = getFactory().newGroupBy(getFactory().newElementSymbol("a"), getFactory().newElementSymbol("b"));

        CompoundCriteria having = getFactory().newCompoundCriteria(CompoundCriteria.AND,
                                                      getFactory().newCompareCriteria(getFactory().newAggregateSymbol("COUNT", false, getFactory().newElementSymbol("b")),
                                                                         Operator.GT,
                                                                         getFactory().newConstant(new Integer(0))),
                                                      getFactory().newCompareCriteria(getFactory().newFunction("+", new Expression[] {
                                                                             getFactory().newElementSymbol("b"), getFactory().newConstant(new Integer(5))}),
                                                                         Operator.GT,
                                                                         getFactory().newConstant(new Integer(0))));

        Query query = getFactory().newQuery(select, from);
        query.setGroupBy(groupBy);
        query.setHaving(having);

        helpTest("SELECT a FROM m.g GROUP BY a, b HAVING COUNT(b) > 0 AND b+5 > 0",
                 "SELECT a FROM m.g GROUP BY a, b HAVING (COUNT(b) > 0) AND ((b + 5) > 0)",
                 query);
    }

    @Test
    public void testFunctionOfAggregates() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        AggregateSymbol agg1 = getFactory().newAggregateSymbol("COUNT", false, getFactory().newElementSymbol("a"));
        AggregateSymbol agg2 = getFactory().newAggregateSymbol("SUM", false, getFactory().newElementSymbol("a"));
        Function f = getFactory().newFunction("*", new Expression[] {agg1, agg2});
        AliasSymbol alias = getFactory().newAliasSymbol("c", getFactory().wrapExpression(f));
        select.addSymbol(alias);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT COUNT(a) * SUM(a) AS c FROM m.g", "SELECT (COUNT(a) * SUM(a)) AS c FROM m.g", query);

    }

    /** SELECT 5-null, a.g1.c1 FROM a.g1 */
    @Test
    public void testArithmeticNullFunction() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newFunction("-", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant(null)})));
        select.addSymbol(getFactory().newElementSymbol("a.g1.c1"));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 5-null, a.g1.c1 FROM a.g1", "SELECT (5 - null), a.g1.c1 FROM a.g1", query);
    }

    /** SELECT 'abc' FROM a.g1 */
    @Test
    public void testStringLiteral() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant("abc")));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 'abc' FROM a.g1", "SELECT 'abc' FROM a.g1", query);
    }

    /** SELECT 'O''Leary' FROM a.g1 */
    @Test
    public void testStringLiteralEscapedTick() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant("O'Leary")));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 'O''Leary' FROM a.g1", "SELECT 'O''Leary' FROM a.g1", query);
    }

    /** SELECT '''abc''' FROM a.g1 */
    @Test
    public void testStringLiteralEscapedTick2() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant("'abc'")));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT '''abc''' FROM a.g1", "SELECT '''abc''' FROM a.g1", query);
    }

    /** SELECT 'a''b''c' FROM a.g1 */
    @Test
    public void testStringLiteralEscapedTick3() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant("a'b'c")));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 'a''b''c' FROM a.g1", "SELECT 'a''b''c' FROM a.g1", query);
    }

    /** SELECT " "" " FROM a.g1 */
    @Test
    public void testStringLiteralEscapedTick4() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol(" \" "));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT \" \"\" \" FROM a.g1", "SELECT \" \"\" \" FROM a.g1", query);
    }

    /** SELECT 123456789012 FROM a.g1 */
    @Test
    public void testLongLiteral() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(new Long(123456789012L))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 123456789012 FROM a.g1", "SELECT 123456789012 FROM a.g1", query);
    }

    /** SELECT 1000000000000000000000000 FROM a.g1 */
    @Test
    public void testBigIntegerLiteral() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(new BigInteger("1000000000000000000000000"))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 1000000000000000000000000 FROM a.g1", "SELECT 1000000000000000000000000 FROM a.g1", query);
    }

    /** SELECT {d'2002-10-02'} FROM m.g1 */
    @Test
    public void testDateLiteral1() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(java.sql.Date.valueOf("2002-10-02"))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT {d'2002-10-02'} FROM m.g1", "SELECT {d'2002-10-02'} FROM m.g1", query);
    }

    /** SELECT {d'2002-9-1'} FROM m.g1 */
    @Test
    public void testDateLiteral2() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(java.sql.Date.valueOf("2002-09-01"))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT {d'2002-09-01'} FROM m.g1", "SELECT {d'2002-09-01'} FROM m.g1", query);
    }

    /** SELECT {t '11:10:00' } FROM m.g1 */
    @Test
    public void testTimeLiteral1() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(java.sql.Time.valueOf("11:10:00"))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT {t '11:10:00' } FROM m.g1", "SELECT {t'11:10:00'} FROM m.g1", query);
    }

    /** SELECT {t '5:10:00'} FROM m.g1 */
    @Test
    public void testTimeLiteral2() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(java.sql.Time.valueOf("5:10:00"))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT {t '05:10:00'} FROM m.g1", "SELECT {t'05:10:00'} FROM m.g1", query);
    }

    /** SELECT {ts'2002-10-02 19:00:02.50'} FROM m.g1 */
    @Test
    public void testTimestampLiteral() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(java.sql.Timestamp.valueOf("2002-10-02 19:00:02.50"))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT {ts'2002-10-02 19:00:02.50'} FROM m.g1", "SELECT {ts'2002-10-02 19:00:02.5'} FROM m.g1", query);
    }

    /** SELECT {b'true'} FROM m.g1 */
    @Test
    public void testBooleanLiteralTrue() {
        Boolean expected = Boolean.TRUE;
        Class<?> expectedType = DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass();
        String sql = "SELECT {b'true'}";
        String expectedSql = "SELECT TRUE";

        helpTestLiteral(expected, expectedType, sql, expectedSql);
    }

    /** SELECT TRUE FROM m.g1 */
    @Test
    public void testBooleanLiteralTrue2() {
        Boolean expected = Boolean.TRUE;
        Class<?> expectedType = DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass();
        String sql = "SELECT TRUE";
        String expectedSql = "SELECT TRUE";

        helpTestLiteral(expected, expectedType, sql, expectedSql);
    }

    /** SELECT {b'false'} FROM m.g1 */
    @Test
    public void testBooleanLiteralFalse() {
        Boolean expected = Boolean.FALSE;
        Class<?> expectedType = DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass();
        String sql = "SELECT {b'false'}";
        String expectedSql = "SELECT FALSE";

        helpTestLiteral(expected, expectedType, sql, expectedSql);
    }

    /** SELECT FALSE FROM m.g1 */
    @Test
    public void testBooleanLiteralFalse2() {
        Boolean expected = Boolean.FALSE;
        Class<?> expectedType = DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass();
        String sql = "SELECT {b'false'}";
        String expectedSql = "SELECT FALSE";

        helpTestLiteral(expected, expectedType, sql, expectedSql);
    }

    @Test
    public void testBooleanLiteralUnknown() {
        Boolean expected = null;
        Class<?> expectedType = DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass();
        String sql = "SELECT {b'unknown'}";
        String expectedSql = "SELECT UNKNOWN";

        helpTestLiteral(expected, expectedType, sql, expectedSql);
    }

    @Test
    public void testBooleanLiteralUnknown2() {
        Boolean expected = null;
        Class<?> expectedType = DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass();
        String sql = "SELECT UNKNOWN";
        String expectedSql = "SELECT UNKNOWN";

        helpTestLiteral(expected, expectedType, sql, expectedSql);
    }

    /** SELECT DISTINCT a FROM g */
    @Test
    public void testSelectDistinct() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));
        select.setDistinct(true);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT DISTINCT a FROM g", "SELECT DISTINCT a FROM g", query);
    }

    /** SELECT ALL a FROM g */
    @Test
    public void testSelectAll() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));
        select.setDistinct(false);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT ALL a FROM g", "SELECT a FROM g", query);
    }

    //=========================Aliasing==============================================

    /** SELECT a AS myA, b FROM g */
    @Test
    public void testAliasInSelect() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        AliasSymbol as = getFactory().newAliasSymbol("myA", getFactory().newElementSymbol("a"));
        Select select = getFactory().newSelect();
        select.addSymbol(as);
        select.addSymbol(getFactory().newElementSymbol("b"));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT a AS myA, b FROM g", "SELECT a AS myA, b FROM g", query);
    }

    /** SELECT a myA, b FROM g, h */
    @Test
    public void testAliasInSelect2() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        GroupSymbol h = getFactory().newGroupSymbol("h");
        From from = getFactory().newFrom();
        from.addGroup(g);
        from.addGroup(h);

        AliasSymbol as = getFactory().newAliasSymbol("myA", getFactory().newElementSymbol("a"));
        Select select = getFactory().newSelect();
        select.addSymbol(as);
        select.addSymbol(getFactory().newElementSymbol("b"));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT a myA, b FROM g, h", "SELECT a AS myA, b FROM g, h", query);
    }

    /** SELECT myG.a FROM g AS myG */
    @Test
    public void testAliasInFrom() {
        GroupSymbol g = getFactory().newGroupSymbol("myG", "g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("myG.a"));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT myG.a FROM g AS myG", "SELECT myG.a FROM g AS myG", query);
    }

    /** SELECT myG.*, myH.b FROM g AS myG, h AS myH */
    @Test
    public void testAliasesInFrom() {
        GroupSymbol g = getFactory().newGroupSymbol("myG", "g");
        GroupSymbol h = getFactory().newGroupSymbol("myH", "h");
        From from = getFactory().newFrom();
        from.addGroup(g);
        from.addGroup(h);

        Select select = getFactory().newSelect();
        MultipleElementSymbol myG = getFactory().newMultipleElementSymbol("myG");
        select.addSymbol(myG);
        select.addSymbol(getFactory().newElementSymbol("myH.b"));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT myG.*, myH.b FROM g AS myG, h AS myH", "SELECT myG.*, myH.b FROM g AS myG, h AS myH", query);
    }

    /** SELECT myG.a, myH.b FROM g myG, h myH */
    @Test
    public void testHiddenAliasesInFrom() {
        GroupSymbol g = getFactory().newGroupSymbol("myG", "g");
        GroupSymbol h = getFactory().newGroupSymbol("myH", "h");
        From from = getFactory().newFrom();
        from.addGroup(g);
        from.addGroup(h);

        Select select = getFactory().newSelect();
        MultipleElementSymbol myG = getFactory().newMultipleElementSymbol("myG");
        select.addSymbol(myG);
        select.addSymbol(getFactory().newElementSymbol("myH.b"));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT myG.*, myH.b FROM g myG, h myH", "SELECT myG.*, myH.b FROM g AS myG, h AS myH", query);
    }

    // ======================= Misc ==============================================

    /** Select a From db.g Where a IS NULL */
    @Test
    public void testIsNullCriteria1() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Criteria crit = getFactory().newIsNullCriteria(a);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("Select a From db.g Where a IS NULL", "SELECT a FROM db.g WHERE a IS NULL", query);
    }

    /** Select a From db.g Where a IS NOT NULL */
    @Test
    public void testIsNullCriteria2() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        IsNullCriteria crit = getFactory().newIsNullCriteria(a);
        crit.setNegated(true);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("Select a From db.g Where a IS NOT NULL", "SELECT a FROM db.g WHERE a IS NOT NULL", query);
    }

    /** Select a From db.g Where Not a IS NULL */
    @Test
    public void testNotIsNullCriteria() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Criteria crit = getFactory().newNotCriteria(getFactory().newIsNullCriteria(a));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("Select a From db.g Where Not a IS NULL", "SELECT a FROM db.g WHERE NOT (a IS NULL)", query);
    }

    /** SELECT a from db.g where a <> "value" */
    @Test
    public void testStringNotEqualDoubleTicks() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression ex = getFactory().newElementSymbol("value");
        Criteria crit = getFactory().newCompareCriteria(a, Operator.NE, ex);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a <> \"value\"", "SELECT a FROM db.g WHERE a <> \"value\"", query);
    }

    /** SELECT a from db.g where a != "value" */
    @Test
    public void testNotEquals2() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression constant = getFactory().newConstant("value");
        Criteria crit = getFactory().newCompareCriteria(a, Operator.NE, constant);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a != 'value'", "SELECT a FROM db.g WHERE a <> 'value'", query);
    }

    /** SELECT a from db."g" where a = 5 */
    @Test
    public void testPartlyQuotedGroup() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Criteria crit = getFactory().newCompareCriteria(a, Operator.EQ, getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.\"g\" where a = 5", "SELECT a FROM db.g WHERE a = 5", query);
    }

    /** SELECT a from "db"."g" where a = 5 */
    @Test
    public void testFullyQuotedGroup() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Criteria crit = getFactory().newCompareCriteria(a, Operator.EQ, getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from \"db\".\"g\" where a = 5", "SELECT a FROM db.g WHERE a = 5", query);
    }

    /** SELECT "db".g.a from db.g */
    @Test
    public void testPartlyQuotedElement1() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("db.g.a");
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT \"db\".g.a from db.g", "SELECT db.g.a FROM db.g", query);
    }

    /** SELECT "db"."g".a from db.g */
    @Test
    public void testPartlyQuotedElement2() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("db.g.a");
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT \"db\".\"g\".a from db.g", "SELECT db.g.a FROM db.g", query);
    }

    /** SELECT "db"."g"."a" from db.g */
    @Test
    public void testPartlyQuotedElement3() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("db.g.a");
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT \"db\".\"g\".\"a\" from db.g", "SELECT db.g.a FROM db.g", query);
    }

    /** SELECT ""g"".""a" from db.g */
    @Test
    public void testStringLiteralLikeQuotedElement() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("g\".\"a"));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT \"g\"\".\"\"a\" from g", "SELECT \"g\"\"\".\"\"\"a\" FROM g", query);
    }

    /** SELECT ""g"".""a" from db.g */
    @Test
    public void testStringLiteralLikeQuotedElement1() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant("g\".\"a")));

        Query query = getFactory().newQuery(select, from);
        ParseInfo info = new ParseInfo();
        info.setAnsiQuotedIdentifiers(false);
        helpTest("SELECT \"g\"\".\"\"a\" from g", "SELECT 'g\".\"a' FROM g", query, info, false);
    }

    /** SELECT g.x AS "select" FROM g */
    @Test
    public void testQuotedAlias() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        AliasSymbol a = getFactory().newAliasSymbol("select", getFactory().newElementSymbol("g.x"));
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT g.x AS \"select\" FROM g", "SELECT g.x AS \"select\" FROM g", query);
    }

    /** SELECT g.x AS year FROM g */
    @Test
    public void testQuotedAlias2() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        AliasSymbol a = getFactory().newAliasSymbol("year", getFactory().newElementSymbol("g.x"));
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT g.x AS \"year\" FROM g", "SELECT g.x AS \"year\" FROM g", query);
    }

    @Test
    public void testQuotedAlias3() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        AliasSymbol a = getFactory().newAliasSymbol("some year", getFactory().newElementSymbol("g.x"));
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT g.x AS \"some year\" FROM g", "SELECT g.x AS \"some year\" FROM g", query);
    }

    /** SELECT g."select" FROM g */
    @Test
    public void testReservedWordElement1() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("g.select");
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT g.\"select\" FROM g", "SELECT g.\"select\" FROM g", query);
    }

    /** SELECT newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet.x FROM newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet */
    @Test
    public void testReservedWordElement2() {
        GroupSymbol g = getFactory().newGroupSymbol("newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet.x");
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet.x FROM newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet",
                 "SELECT newModel5.ResultSetDocument.MappingClasses.\"from\".\"from\".Query1InputSet.x FROM newModel5.ResultSetDocument.MappingClasses.\"from\".\"from\".Query1InputSet",
                 query);
    }

    /** SELECT * FROM newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet  */
    @Test
    public void testReservedWordGroup1() {
        GroupSymbol g = getFactory().newGroupSymbol("newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet",
                 "SELECT * FROM newModel5.ResultSetDocument.MappingClasses.\"from\".\"from\".Query1InputSet",
                 query);
    }

    /** SELECT * FROM newModel5."ResultSetDocument.MappingClasses.from.from.Query1InputSet"  */
    @Test
    public void testReservedWordGroup2() {
        GroupSymbol g = getFactory().newGroupSymbol("newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT * FROM newModel5.\"ResultSetDocument.MappingClasses.from.from.Query1InputSet\"",
                 "SELECT * FROM newModel5.ResultSetDocument.MappingClasses.\"from\".\"from\".Query1InputSet",
                 query);
    }

    /** SELECT * FROM model.doc WHERE ab.cd.@ef = 'abc' */
    @Test
    public void testXMLCriteriaWithAttribute() {
        GroupSymbol g = getFactory().newGroupSymbol("model.doc");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());

        Query query = getFactory().newQuery(select, from);

        ElementSymbol elem = getFactory().newElementSymbol("ab.cd.@ef");
        query.setCriteria(getFactory().newCompareCriteria(elem, Operator.EQ, getFactory().newConstant("abc")));

        helpTest("SELECT * FROM model.doc WHERE ab.cd.@ef = 'abc'", "SELECT * FROM model.doc WHERE ab.cd.@ef = 'abc'", query);
    }

    /** SELECT a from db.g where a <> 'value' */
    @Test
    public void testStringNotEqual() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression constant = getFactory().newConstant("value");
        Criteria crit = getFactory().newCompareCriteria(a, Operator.NE, constant);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a <> 'value'", "SELECT a FROM db.g WHERE a <> 'value'", query);
    }

    /** SELECT a from db.g where a BETWEEN 1000 AND 2000 */
    @Test
    public void testBetween1() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression constant1 = getFactory().newConstant(new Integer(1000));
        Expression constant2 = getFactory().newConstant(new Integer(2000));
        Criteria crit = getFactory().newBetweenCriteria(a, constant1, constant2);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a BETWEEN 1000 AND 2000", "SELECT a FROM db.g WHERE a BETWEEN 1000 AND 2000", query);
    }

    /** SELECT a from db.g where a NOT BETWEEN 1000 AND 2000 */
    @Test
    public void testBetween2() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression constant1 = getFactory().newConstant(new Integer(1000));
        Expression constant2 = getFactory().newConstant(new Integer(2000));
        BetweenCriteria crit = getFactory().newBetweenCriteria(a, constant1, constant2);
        crit.setNegated(true);
        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a NOT BETWEEN 1000 AND 2000",
                 "SELECT a FROM db.g WHERE a NOT BETWEEN 1000 AND 2000",
                 query);
    }

    /** SELECT a from db.g where a < 1000 */
    @Test
    public void testCompareLT() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression constant = getFactory().newConstant(new Integer(1000));
        Criteria crit = getFactory().newCompareCriteria(a, Operator.LT, constant);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a < 1000", "SELECT a FROM db.g WHERE a < 1000", query);
    }

    /** SELECT a from db.g where a > 1000 */
    @Test
    public void testCompareGT() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression constant = getFactory().newConstant(new Integer(1000));
        Criteria crit = getFactory().newCompareCriteria(a, Operator.GT, constant);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a > 1000", "SELECT a FROM db.g WHERE a > 1000", query);
    }

    /** SELECT a from db.g where a <= 1000 */
    @Test
    public void testCompareLE() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression constant = getFactory().newConstant(new Integer(1000));
        Criteria crit = getFactory().newCompareCriteria(a, Operator.LE, constant);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a <= 1000", "SELECT a FROM db.g WHERE a <= 1000", query);
    }

    /** SELECT a from db.g where a >= 1000 */
    @Test
    public void testCompareGE() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression constant = getFactory().newConstant(new Integer(1000));
        Criteria crit = getFactory().newCompareCriteria(a, Operator.GE, constant);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where a >= 1000", "SELECT a FROM db.g WHERE a >= 1000", query);
    }

    /** SELECT a from db.g where b = x and a = 1000 */
    @Test
    public void testCompoundCompare1() {
        helpTestCompoundCompare("SELECT a from db.g where b = x and a = 1000");
    }

    /** SELECT a from db.g where (b = x and a = 1000) */
    @Test
    public void testCompoundCompare2() {
        helpTestCompoundCompare("SELECT a from db.g where (b = x and a = 1000)");
    }

    /** SELECT a from db.g where ((b = x) and (a = 1000)) */
    @Test
    public void testCompoundCompare3() {
        helpTestCompoundCompare("SELECT a from db.g where ((b = x) and (a = 1000))");
    }

    /** SELECT a from db.g where (((b = x) and (a = 1000))) */
    @Test
    public void testCompoundCompare4() {
        helpTestCompoundCompare("SELECT a from db.g where (((b = x) and (a = 1000)))");
    }

    /** SELECT a FROM db.g WHERE (b = x) AND (a = 1000) */
    protected void helpTestCompoundCompare(String testSQL) {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Criteria crit1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("b"), Operator.EQ, getFactory().newElementSymbol("x"));
        Expression constant = getFactory().newConstant(new Integer(1000));
        Criteria crit2 = getFactory().newCompareCriteria(a, Operator.EQ, constant);
        Criteria crit = getFactory().newCompoundCriteria(CompoundCriteria.AND, crit1, crit2);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest(testSQL, "SELECT a FROM db.g WHERE (b = x) AND (a = 1000)", query);
    }

    /** SELECT a FROM db.g WHERE b IN (1000,5000)*/
    @Test
    public void testSetCriteria0() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        Expression constant1 = getFactory().newConstant(new Integer(1000));
        Expression constant2 = getFactory().newConstant(new Integer(5000));
        List<Expression> constants = new ArrayList<Expression>(2);
        constants.add(constant1);
        constants.add(constant2);
        Criteria crit = getFactory().newSetCriteria(getFactory().newElementSymbol("b"), constants);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a FROM db.g WHERE b IN (1000,5000)", "SELECT a FROM db.g WHERE b IN (1000, 5000)", query);
    }

    /** SELECT a FROM db.g WHERE b NOT IN (1000,5000)*/
    @Test
    public void testSetCriteria1() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        Expression constant1 = getFactory().newConstant(new Integer(1000));
        Expression constant2 = getFactory().newConstant(new Integer(5000));
        List<Expression> constants = new ArrayList<Expression>(2);
        constants.add(constant1);
        constants.add(constant2);
        SetCriteria crit = getFactory().newSetCriteria(getFactory().newElementSymbol("b"), constants);
        crit.setNegated(true);
        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a FROM db.g WHERE b NOT IN (1000,5000)", "SELECT a FROM db.g WHERE b NOT IN (1000, 5000)", query);
    }

    // ================================== order by ==================================

    /** SELECT a FROM db.g WHERE b = aString order by c*/
    @Test
    public void testOrderBy() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Criteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("b"), Operator.EQ, getFactory().newElementSymbol("aString"));

        ArrayList elements = new ArrayList();
        elements.add(getFactory().newElementSymbol("c"));
        OrderBy orderBy = getFactory().newOrderBy(elements);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        query.setOrderBy(orderBy);
        helpTest("SELECT a FROM db.g WHERE b = aString ORDER BY c", "SELECT a FROM db.g WHERE b = aString ORDER BY c", query);
    }

    /** SELECT a FROM db.g WHERE b = aString order by c desc*/
    @Test
    public void testOrderByDesc() {
        List<Expression> elements = new ArrayList<Expression>();
        elements.add(getFactory().newElementSymbol("c"));
        List<Boolean> orderTypes = new ArrayList<Boolean>();
        orderTypes.add(Boolean.FALSE);
        OrderBy orderBy = getFactory().newOrderBy(elements, orderTypes);

        Query query = getOrderByQuery(orderBy);
        helpTest("SELECT a FROM db.g WHERE b = aString ORDER BY c desc",
                 "SELECT a FROM db.g WHERE b = aString ORDER BY c DESC",
                 query);
    }

    protected Query getOrderByQuery(OrderBy orderBy) {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Criteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("b"), Operator.EQ, getFactory().newElementSymbol("aString"));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        query.setOrderBy(orderBy);
        return query;
    }

    /** SELECT a FROM db.g WHERE b = aString order by c,d*/
    @Test
    public void testOrderBys() {
        ArrayList elements = new ArrayList();
        elements.add(getFactory().newElementSymbol("c"));
        elements.add(getFactory().newElementSymbol("d"));
        OrderBy orderBy = getFactory().newOrderBy(elements);

        Query query = getOrderByQuery(orderBy);
        helpTest("SELECT a FROM db.g WHERE b = aString ORDER BY c,d", "SELECT a FROM db.g WHERE b = aString ORDER BY c, d", query);
    }

    /** SELECT a FROM db.g WHERE b = aString order by c desc,d desc*/
    @Test
    public void testOrderBysDesc() {
        List<ElementSymbol> elements = new ArrayList<ElementSymbol>();
        elements.add(getFactory().newElementSymbol("c"));
        elements.add(getFactory().newElementSymbol("d"));
        List<Boolean> orderTypes = new ArrayList<Boolean>();
        orderTypes.add(Boolean.FALSE);
        orderTypes.add(Boolean.FALSE);
        OrderBy orderBy = getFactory().newOrderBy(elements, orderTypes);

        Query query = getOrderByQuery(orderBy);
        helpTest("SELECT a FROM db.g WHERE b = aString ORDER BY c desc,d desc",
                 "SELECT a FROM db.g WHERE b = aString ORDER BY c DESC, d DESC",
                 query);
    }

    /** SELECT a FROM db.g WHERE b = aString order by c desc,d*/
    @Test
    public void testMixedOrderBys() {
        ArrayList<ElementSymbol> elements = new ArrayList<ElementSymbol>();
        elements.add(getFactory().newElementSymbol("c"));
        elements.add(getFactory().newElementSymbol("d"));
        ArrayList<Boolean> orderTypes = new ArrayList<Boolean>();
        orderTypes.add(Boolean.FALSE);
        orderTypes.add(Boolean.TRUE);
        OrderBy orderBy = getFactory().newOrderBy(elements, orderTypes);

        Query query = getOrderByQuery(orderBy);
        helpTest("SELECT a FROM db.g WHERE b = aString ORDER BY c desc,d",
                 "SELECT a FROM db.g WHERE b = aString ORDER BY c DESC, d",
                 query);
    }

    @Test
    public void testOrderByNullOrdering() {
        OrderBy orderBy = getFactory().newOrderBy();
        OrderByItem item = getFactory().newOrderByItem(getFactory().newElementSymbol("c"), true);
        item.setNullOrdering(SortSpecification.NullOrdering.FIRST);
        orderBy.getOrderByItems().add(item);
        item = getFactory().newOrderByItem(getFactory().newElementSymbol("d"), false);
        item.setNullOrdering(SortSpecification.NullOrdering.LAST);
        orderBy.getOrderByItems().add(item);

        Query query = getOrderByQuery(orderBy);
        helpTest("SELECT a FROM db.g WHERE b = aString ORDER BY c NULLS FIRST,d desc nulls last",
                 "SELECT a FROM db.g WHERE b = aString ORDER BY c NULLS FIRST, d DESC NULLS LAST",
                 query);
    }

    // ================================== match ====================================

    /** SELECT a FROM db.g WHERE b LIKE 'aString'*/
    @Test
    public void testLike0() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression string1 = getFactory().newConstant("aString");
        Criteria crit = getFactory().newMatchCriteria(getFactory().newElementSymbol("b"), string1);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a FROM db.g WHERE b LIKE 'aString'", "SELECT a FROM db.g WHERE b LIKE 'aString'", query);
    }

    /** SELECT a FROM db.g WHERE b NOT LIKE 'aString'*/
    @Test
    public void testLike1() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression string1 = getFactory().newConstant("aString");
        MatchCriteria crit = getFactory().newMatchCriteria(getFactory().newElementSymbol("b"), string1);
        crit.setNegated(true);
        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a FROM db.g WHERE b NOT LIKE 'aString'", "SELECT a FROM db.g WHERE b NOT LIKE 'aString'", query);
    }

    /** SELECT a from db.g where b like '#String' escape '#'*/
    @Test
    public void testLikeWithEscape() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression string1 = getFactory().newConstant("#String");
        Criteria crit = getFactory().newMatchCriteria(getFactory().newElementSymbol("b"), string1, '#');

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where b like '#String' escape '#'",
                 "SELECT a FROM db.g WHERE b LIKE '#String' ESCAPE '#'",
                 query);
    }

    /** SELECT "date"."time" from db.g */
    @Test
    public void testReservedWordsInElement() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("date.time");
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT \"date\".\"time\" from db.g", "SELECT \"date\".\"time\" FROM db.g", query);

    }

    /** SELECT a */
    @Test
    public void testNoFromClause() {
        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        Expression b = getFactory().wrapExpression(getFactory().newConstant(new Integer(5), Integer.class));
        select.addSymbol(a);
        select.addSymbol(b);
        Query query = getFactory().newQuery();
        query.setSelect(select);
        helpTest("SELECT a, 5", "SELECT a, 5", query);
    }

    // ==================== misc queries that should fail ===========================

    /** SELECT a or b from g */
    @Test
    public void testOrInSelect() {
        Query query = getFactory().newQuery();
        CompoundCriteria compoundCriteria = getFactory().newCompoundCriteria(CompoundCriteria.OR,
                                                                getFactory().newExpressionCriteria(getFactory().newElementSymbol("a")),
                                                                getFactory().newExpressionCriteria(getFactory().newElementSymbol("b")));
        query.setSelect(getFactory().newSelect(Arrays.asList(getFactory().wrapExpression(compoundCriteria))));
        helpTest("select a or b", "SELECT (a) OR (b)", query);
    }

    /** SELECT a FROM g WHERE a LIKE x*/
    @Test
    public void testLikeWOConstant() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        ElementSymbol x = getFactory().newElementSymbol("x");
        Criteria crit = getFactory().newMatchCriteria(a, x);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a FROM g WHERE a LIKE x", "SELECT a FROM g WHERE a LIKE x", query);
    }

    /** Test reusability of parser */
    @Test
    public void testReusabilityOfParserObject() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT a FROM m.g", "SELECT a FROM m.g", query);

        helpTest("SELECT a FROM m.g", "SELECT a FROM m.g", query);
    }

    /** SELECT a from db.g where b LIKE ? */
    @Test
    public void testParameter1() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Reference ref1 = getFactory().newReference(0);
        Criteria crit = getFactory().newMatchCriteria(getFactory().newElementSymbol("b"), ref1);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a from db.g where b LIKE ?", "SELECT a FROM db.g WHERE b LIKE ?", query);
    }

    /** SELECT a from db.g where b LIKE ? */
    @Test
    public void testParameter2() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        Reference ref0 = getFactory().newReference(0);
        Expression expr = getFactory().wrapExpression(ref0);
        select.addSymbol(expr);

        Reference ref1 = getFactory().newReference(1);
        Criteria crit = getFactory().newMatchCriteria(getFactory().newElementSymbol("b"), ref1);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT ? from db.g where b LIKE ?", "SELECT ? FROM db.g WHERE b LIKE ?", query);
    }

    /** SELECT a, b FROM (SELECT c FROM m.g) AS y */
    @Test
    public void testSubquery1() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol symbol = getFactory().newElementSymbol("c");
        select.addSymbol(symbol);

        Query query = getFactory().newQuery(select, from);

        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("y", query);
        From from2 = getFactory().newFrom();
        from2.addClause(sfc);

        Select select2 = getFactory().newSelect();
        select2.addSymbol(getFactory().newElementSymbol("a"));
        select2.addSymbol(getFactory().newElementSymbol("b"));

        Query query2 = getFactory().newQuery();
        query2.setSelect(select2);
        query2.setFrom(from2);

        helpTest("SELECT a, b FROM (SELECT c FROM m.g) AS y", "SELECT a, b FROM (SELECT c FROM m.g) AS y", query2);
    }

    /** SELECT a, b FROM ((SELECT c FROM m.g)) AS y */
    @Test
    public void testSubquery1a() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol symbol = getFactory().newElementSymbol("c");
        select.addSymbol(symbol);

        Query query = getFactory().newQuery(select, from);

        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("y", query);
        From from2 = getFactory().newFrom();
        from2.addClause(sfc);

        Select select2 = getFactory().newSelect();
        select2.addSymbol(getFactory().newElementSymbol("a"));
        select2.addSymbol(getFactory().newElementSymbol("b"));

        Query query2 = getFactory().newQuery();
        query2.setSelect(select2);
        query2.setFrom(from2);

        helpTest("SELECT a, b FROM ((SELECT c FROM m.g)) AS y", "SELECT a, b FROM (SELECT c FROM m.g) AS y", query2);
    }

    /** SELECT a, b FROM m.g1 JOIN (SELECT c FROM m.g2) AS y ON m.g1.a = y.c */
    @Test
    public void testSubquery2() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g2");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol symbol = getFactory().newElementSymbol("c");
        select.addSymbol(symbol);

        Query query = getFactory().newQuery(select, from);

        UnaryFromClause ufc = getFactory().newUnaryFromClause("m.g1");
        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("y", query);
        CompareCriteria join = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g1.a"), Operator.EQ, getFactory().newElementSymbol("y.c"));
        List crits = new ArrayList();
        crits.add(join);
        JoinPredicate jp = getFactory().newJoinPredicate(ufc, sfc, JoinType.Types.JOIN_INNER, crits);
        From from2 = getFactory().newFrom();
        from2.addClause(jp);

        Select select2 = getFactory().newSelect();
        select2.addSymbol(getFactory().newElementSymbol("a"));
        select2.addSymbol(getFactory().newElementSymbol("b"));

        Query query2 = getFactory().newQuery();
        query2.setSelect(select2);
        query2.setFrom(from2);

        helpTest("SELECT a, b FROM m.g1 JOIN (SELECT c FROM m.g2) AS y ON m.g1.a = y.c",
                 "SELECT a, b FROM m.g1 INNER JOIN (SELECT c FROM m.g2) AS y ON m.g1.a = y.c",
                 query2);
    }

    /** INSERT INTO m.g (a) VALUES (?) */
    @Test
    public void testInsertWithReference() {
        Insert insert = getFactory().newInsert();
        insert.setGroup(getFactory().newGroupSymbol("m.g"));
        List vars = new ArrayList();
        vars.add(getFactory().newElementSymbol("a"));
        insert.setVariables(vars);
        List values = new ArrayList();
        values.add(getFactory().newReference(0));
        insert.setValues(values);
        helpTest("INSERT INTO m.g (a) VALUES (?)", "INSERT INTO m.g (a) VALUES (?)", insert);
    }

    @Test
    public void testStoredQueryWithNoParameter() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setProcedureName("proc1");
        helpTest("exec proc1()", "EXEC proc1()", storedQuery);
        helpTest("execute proc1()", "EXEC proc1()", storedQuery);
    }

    @Test
    public void testStoredQueryWithNoParameter2() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setProcedureName("proc1");

        From from = getFactory().newFrom();
        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("X", storedQuery);
        from.addClause(sfc);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("X.A"));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT X.A FROM (exec proc1()) AS X", "SELECT X.A FROM (EXEC proc1()) AS X", query);
    }

    @Test
    public void testStoredQuery() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setProcedureName("proc1");
        SPParameter parameter = getFactory().newSPParameter(1, getFactory().newConstant("param1"));
        parameter.setParameterType(ParameterInfo.IN);
        storedQuery.setParameter(parameter);
        helpTest("Exec proc1('param1')", "EXEC proc1('param1')", storedQuery);
        helpTest("execute proc1('param1')", "EXEC proc1('param1')", storedQuery);
    }

    @Test
    public void testStoredQuery2() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setProcedureName("proc1");
        SPParameter parameter = getFactory().newSPParameter(1, getFactory().newConstant("param1"));
        storedQuery.setParameter(parameter);
        From from = getFactory().newFrom();
        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("X", storedQuery);
        from.addClause(sfc);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("X.A"));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT X.A FROM (exec proc1('param1')) AS X", "SELECT X.A FROM (EXEC proc1('param1')) AS X", query);
    }

    @Test
    public void testStoredQuery2SanityCheck() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setProcedureName("proc1");
        SPParameter parameter = getFactory().newSPParameter(1, getFactory().newConstant("param1"));
        storedQuery.setParameter(parameter);
        From from = getFactory().newFrom();
        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("x", storedQuery);
        from.addClause(sfc);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("x.a"));

        helpTest("exec proc1('param1')", "EXEC proc1('param1')", storedQuery);
    }

    @Test
    public void testIfStatement() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        String shortType = new String("short");
        Statement ifStmt = getFactory().newDeclareStatement(a, shortType);

        ElementSymbol b = getFactory().newElementSymbol("b");
        Statement elseStmt = getFactory().newDeclareStatement(b, shortType);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(ifStmt);

        Block elseBlock = getFactory().newBlock();
        elseBlock.addStatement(elseStmt);

        ElementSymbol c = getFactory().newElementSymbol("c");
        Criteria crit = getFactory().newCompareCriteria(c, Operator.EQ, getFactory().newConstant(new Integer(5)));

        IfStatement stmt = getFactory().newIfStatement(crit, ifBlock);
        stmt.setElseBlock(elseBlock);

        helpStmtTest("IF(c = 5) BEGIN DECLARE short a; END ELSE BEGIN DECLARE short b; END", "IF(c = 5)" + "\n" + "BEGIN" + "\n"
                                                                                             + "DECLARE short a;" + "\n" + "END"
                                                                                             + "\n" + "ELSE" + "\n" + "BEGIN"
                                                                                             + "\n" + "DECLARE short b;" + "\n"
                                                                                             + "END", stmt);
    }

    @Test
    public void testAssignStatement() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ, getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        Expression expr = getFactory().newConstant("aString");

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(a, query);
        AssignmentStatement exprStmt = getFactory().newAssignmentStatement(a, expr);

        helpStmtTest("a = SELECT a1 FROM g WHERE a2 = 5;", "a = (SELECT a1 FROM g WHERE a2 = 5);", queryStmt);

        helpStmtTest("a = 'aString';", "a = 'aString';", exprStmt);
    }

    @Test
    public void testDeclareStatement() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        String type = new String("short");
        DeclareStatement stmt = getFactory().newDeclareStatement(a, type);

        helpStmtTest("DECLARE short a;", "DECLARE short a;", stmt);
    }

    @Test
    public void testDeclareStatementWithAssignment() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        String type = new String("short");
        DeclareStatement stmt = getFactory().newDeclareStatement(a, type, getFactory().newConstant(null));

        helpStmtTest("DECLARE short a = null;", "DECLARE short a = null;", stmt);
    }

    @Test
    public void testDeclareStatementWithAssignment1() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        String type = new String("string");
        DeclareStatement stmt = getFactory().newDeclareStatement(a, type, getFactory().newScalarSubquery(sampleQuery()));

        helpStmtTest("DECLARE string a = SELECT a1 FROM g WHERE a2 = 5;",
                     "DECLARE string a = (SELECT a1 FROM g WHERE a2 = 5);",
                     stmt);
    }

    @Test
    public void testStatement() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        String type = new String("short");
        DeclareStatement declStmt = getFactory().newDeclareStatement(a, type);
        Statement stmt = declStmt;

        helpStmtTest("DECLARE short a;", "DECLARE short a;", stmt);
    }

    @Test
    public void testCommandStatement() throws Exception {
        Query query = sampleQuery();

        Command sqlCmd = query;
        CommandStatement cmdStmt = getFactory().newCommandStatement(sqlCmd);

        helpStmtTest("SELECT a1 FROM g WHERE a2 = 5;", "SELECT a1 FROM g WHERE a2 = 5;", cmdStmt);
    }

    protected Query sampleQuery() {
        List<ElementSymbol> symbols = new ArrayList<ElementSymbol>();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ, getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);
        return query;
    }

    @Test
    public void testDynamicCommandStatement() throws Exception {
        List symbols = new ArrayList();

        ElementSymbol a1 = getFactory().newElementSymbol("a1");
        a1.setType(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());
        symbols.add(a1);

        DynamicCommand sqlCmd = getFactory().newDynamicCommand();
        Expression sql = getFactory().newConstant("SELECT a1 FROM g WHERE a2 = 5");

        sqlCmd.setSql(sql);
        sqlCmd.setAsColumns(symbols);
        sqlCmd.setAsClauseSet(true);

        sqlCmd.setIntoGroup(getFactory().newGroupSymbol("#g"));

        CommandStatement cmdStmt = getFactory().newCommandStatement(sqlCmd);

        helpStmtTest("exec string 'SELECT a1 FROM g WHERE a2 = 5' as a1 string into #g;",
                     "EXECUTE IMMEDIATE 'SELECT a1 FROM g WHERE a2 = 5' AS a1 string INTO #g;",
                     cmdStmt);
    }

    //sql is a variable, also uses the as, into, and update clauses
    @Test
    public void testDynamicCommandStatement1() throws Exception {
        List<ElementSymbol> symbols = new ArrayList<ElementSymbol>();

        ElementSymbol a1 = getFactory().newElementSymbol("a1");
        a1.setType(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());
        symbols.add(a1);

        ElementSymbol a2 = getFactory().newElementSymbol("a2");
        a2.setType(DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass());
        symbols.add(a2);

        DynamicCommand sqlCmd = getFactory().newDynamicCommand();
        Expression sql = getFactory().newElementSymbol("z");

        sqlCmd.setSql(sql);
        sqlCmd.setAsColumns(symbols);
        sqlCmd.setAsClauseSet(true);

        sqlCmd.setIntoGroup(getFactory().newGroupSymbol("#g"));

        sqlCmd.setUpdatingModelCount(1);

        CommandStatement cmdStmt = getFactory().newCommandStatement(sqlCmd);

        helpStmtTest("execute IMMEDIATE z as a1 string, a2 integer into #g update 1;",
                     "EXECUTE IMMEDIATE z AS a1 string, a2 integer INTO #g UPDATE 1;",
                     cmdStmt);
    }

    @Test
    public void testDynamicCommandStatementWithUsing() throws Exception {
        SetClauseList using = getFactory().newSetClauseList();

        ElementSymbol a = getFactory().newElementSymbol("a");
        SetClause setClause = getFactory().newSetClause(a, getFactory().newElementSymbol("b"));
        using.addClause(setClause);

        DynamicCommand sqlCmd = getFactory().newDynamicCommand();
        Expression sql = getFactory().newElementSymbol("z");

        sqlCmd.setSql(sql);
        sqlCmd.setUsing(using);

        CommandStatement cmdStmt = getFactory().newCommandStatement(sqlCmd);

        helpStmtTest("execute immediate z using a=b;", "EXECUTE IMMEDIATE z USING a = b;", cmdStmt);
    }

    @Test
    public void testSubquerySetCriteria0() {
        //test wrap up command with subquerySetCriteria
        Query outer = exampleIn(false);

        helpTest("SELECT a FROM db.g WHERE b IN (SELECT a FROM db.g WHERE a2 = 5)",
                 "SELECT a FROM db.g WHERE b IN (SELECT a FROM db.g WHERE a2 = 5)",
                 outer);
    }

    protected Query exampleIn(boolean semiJoin) {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        Expression expr = getFactory().newElementSymbol("b");

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ, getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);
        SubquerySetCriteria subCrit = getFactory().newSubquerySetCriteria(expr, query);
        subCrit.getSubqueryHint().setMergeJoin(semiJoin);
        Query outer = getFactory().newQuery();
        outer.setSelect(select);
        outer.setFrom(from);
        outer.setCriteria(subCrit);
        return outer;
    }

    @Test
    public void testSubquerySetCriteria1() {

        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        Expression expr = getFactory().newElementSymbol("b");

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ, getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);
        SubquerySetCriteria subCrit = getFactory().newSubquerySetCriteria(expr, query);
        subCrit.setNegated(true);
        Query outer = getFactory().newQuery();
        outer.setSelect(select);
        outer.setFrom(from);
        outer.setCriteria(subCrit);

        helpTest("SELECT a FROM db.g WHERE b NOT IN (SELECT a FROM db.g WHERE a2 = 5)",
                 "SELECT a FROM db.g WHERE b NOT IN (SELECT a FROM db.g WHERE a2 = 5)",
                 outer);
    }

    @Test
    public void testSubquerySetCriteriaWithExec() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        Expression expr = getFactory().newElementSymbol("b");

        StoredProcedure exec = getFactory().newStoredProcedure();
        exec.setProcedureName("m.sq1");
        Query query = getFactory().newQuery(getFactory().newSelect(Arrays.asList(getFactory().newMultipleElementSymbol())),
                               getFactory().newFrom(Arrays.asList(getFactory().newSubqueryFromClause("x", exec))));
        SubquerySetCriteria subCrit = getFactory().newSubquerySetCriteria(expr, query);

        Query outer = getFactory().newQuery();
        outer.setSelect(select);
        outer.setFrom(from);
        outer.setCriteria(subCrit);

        helpTest("SELECT a FROM db.g WHERE b IN (EXEC m.sq1())",
                 "SELECT a FROM db.g WHERE b IN (SELECT * FROM (EXEC m.sq1()) AS x)",
                 outer);
    }

    @Test
    public void testSubquerySetCriteriaWithUnion() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("a"));

        Expression expr = getFactory().newElementSymbol("b");

        Query u1 = getFactory().newQuery();
        Select u1s = getFactory().newSelect();
        u1s.addSymbol(getFactory().newElementSymbol("x1"));
        u1.setSelect(u1s);
        From u1f = getFactory().newFrom();
        u1f = getFactory().newFrom();
        u1f.addClause(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("db.g2")));
        u1.setFrom(u1f);

        Query u2 = getFactory().newQuery();
        Select u2s = getFactory().newSelect();
        u2s.addSymbol(getFactory().newElementSymbol("x2"));
        u2.setSelect(u2s);
        From u2f = getFactory().newFrom();
        u2f = getFactory().newFrom();
        u2f.addClause(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("db.g3")));
        u2.setFrom(u2f);

        SetQuery union = getFactory().newSetQuery(u1, SetQuery.Operation.UNION, u2, true);

        SubquerySetCriteria subCrit = getFactory().newSubquerySetCriteria(expr, union);

        Query outer = getFactory().newQuery();
        outer.setSelect(select);
        outer.setFrom(from);
        outer.setCriteria(subCrit);

        helpTest("SELECT a FROM db.g WHERE b IN (SELECT x1 FROM db.g2 UNION ALL SELECT x2 FROM db.g3)",
                 "SELECT a FROM db.g WHERE b IN (SELECT x1 FROM db.g2 UNION ALL SELECT x2 FROM db.g3)",
                 outer);
    }

    @Test
    public void testVariablesInExec() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setProcedureName("proc1");
        SPParameter parameter = getFactory().newSPParameter(1, getFactory().newElementSymbol("param1"));
        parameter.setParameterType(ParameterInfo.IN);
        storedQuery.setParameter(parameter);
        helpTest("Exec proc1(param1)", "EXEC proc1(param1)", storedQuery);
        helpTest("execute proc1(param1)", "EXEC proc1(param1)", storedQuery);
    }

    @Test
    public void testExecSubquery() {
        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();
        from.addClause(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("newModel2.Table1")));
        StoredProcedure subquery = getFactory().newStoredProcedure();
        subquery.setProcedureName("NewVirtual.StoredQuery");
        from.addClause(getFactory().newSubqueryFromClause("a", subquery));
        query.setFrom(from);

        helpTest("SELECT * FROM newModel2.Table1, (EXEC NewVirtual.StoredQuery()) AS a",
                 "SELECT * FROM newModel2.Table1, (EXEC NewVirtual.StoredQuery()) AS a",
                 query);
    }

    @Test
    public void testUnicode1() {
        try {
            byte[] data = {(byte)0xd0, (byte)0x9c, (byte)0xd0, (byte)0xbe, (byte)0xd1, (byte)0x81, (byte)0xd0, (byte)0xba,
                (byte)0xd0, (byte)0xb2, (byte)0xd0, (byte)0xb0};

            String string = new String(data, "UTF-8");
            String sql = "SELECT * FROM TestDocument.TestDocument WHERE Subject='" + string + "'";

            Query query = getFactory().newQuery();
            Select select = getFactory().newSelect();
            select.addSymbol(getFactory().newMultipleElementSymbol());
            query.setSelect(select);
            From from = getFactory().newFrom();
            from.addGroup(getFactory().newGroupSymbol("TestDocument.TestDocument"));
            query.setFrom(from);
            CompareCriteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("Subject"), Operator.EQ, getFactory().newConstant(string));
            query.setCriteria(crit);

            helpTest(sql, query.toString(), query);

        } catch (UnsupportedEncodingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnicode2() {
        String sql = "SELECT * FROM TestDocument.TestDocument WHERE Subject='\u0041\u005a'";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("TestDocument.TestDocument"));
        query.setFrom(from);
        CompareCriteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("Subject"), Operator.EQ, getFactory().newConstant("AZ"));
        query.setCriteria(crit);

        helpTest(sql, query.toString(), query);
    }

    @Test
    public void testUnicode3() {
        String sql = "SELECT '\u05e0'";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        Constant c = getFactory().newConstant("\u05e0");
        select.addSymbol(getFactory().wrapExpression(c));
        query.setSelect(select);

        helpTest(sql, query.toString(), query);
    }

    @Test
    public void testUnicode4() {
        String sql = "SELECT \u05e0 FROM g";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        ElementSymbol e = getFactory().newElementSymbol("\u05e0");
        select.addSymbol(e);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));
        query.setSelect(select);
        query.setFrom(from);

        helpTest(sql, query.toString(), query);
    }

    @Test
    public void testEscapedFunction1() {
        String sql = "SELECT * FROM a.thing WHERE e1 = {fn concat('a', 'b')}";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("a.thing"));
        query.setFrom(from);
        Function function = getFactory().newFunction("concat", new Expression[] {getFactory().newConstant("a"), getFactory().newConstant("b")});
        CompareCriteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, function);
        query.setCriteria(crit);

        helpTest(sql, "SELECT * FROM a.thing WHERE e1 = concat('a', 'b')", query);
    }

    @Test
    public void testEscapedFunction2() {
        String sql = "SELECT * FROM a.thing WHERE e1 = {fn convert(5, string)}";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("a.thing"));
        query.setFrom(from);
        Function function = getFactory().newFunction("convert", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant("string")});
        CompareCriteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, function);
        query.setCriteria(crit);

        helpTest(sql, "SELECT * FROM a.thing WHERE e1 = convert(5, string)", query);
    }

    @Test
    public void testEscapedFunction3() {
        String sql = "SELECT * FROM a.thing WHERE e1 = {fn cast(5 as string)}";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("a.thing"));
        query.setFrom(from);
        Function function = getFactory().newFunction("cast", new Expression[] {getFactory().newConstant(new Integer(5)), getFactory().newConstant("string")});
        CompareCriteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, function);
        query.setCriteria(crit);

        helpTest(sql, "SELECT * FROM a.thing WHERE e1 = cast(5 AS string)", query);
    }

    @Test
    public void testEscapedFunction4() {
        String sql = "SELECT * FROM a.thing WHERE e1 = {fn concat({fn concat('a', 'b')}, 'c')}";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("a.thing"));
        query.setFrom(from);
        Function func1 = getFactory().newFunction("concat", new Expression[] {getFactory().newConstant("a"), getFactory().newConstant("b")});
        Function func2 = getFactory().newFunction("concat", new Expression[] {func1, getFactory().newConstant("c")});
        CompareCriteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, func2);
        query.setCriteria(crit);

        helpTest(sql, "SELECT * FROM a.thing WHERE e1 = concat(concat('a', 'b'), 'c')", query);
    }

    @Test
    public void testFunctionWithUnderscore() {
        String sql = "SELECT yowza_yowza() FROM a.thing";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        Function func1 = getFactory().newFunction("yowza_yowza", new Expression[] {});
        Expression expr = getFactory().wrapExpression(func1);
        select.addSymbol(expr);
        query.setSelect(select);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("a.thing"));
        query.setFrom(from);

        helpTest(sql, "SELECT yowza_yowza() FROM a.thing", query);
    }

    @Test
    public void testManyInnerJoins1() {
        String sql = "SELECT * " + "FROM SQL1.dbo.Customers INNER JOIN SQL1.dbo.Orders "
                     + "ON SQL1.dbo.Customers.CustomerID = SQL1.dbo.Orders.CustomerID " + "INNER JOIN SQL1.dbo.order_details "
                     + "ON SQL1.dbo.Orders.OrderID = SQL1.dbo.order_details.OrderID";

        String sqlExpected = "SELECT * " + "FROM (SQL1.dbo.Customers INNER JOIN SQL1.dbo.Orders "
                             + "ON SQL1.dbo.Customers.CustomerID = SQL1.dbo.Orders.CustomerID) "
                             + "INNER JOIN SQL1.dbo.order_details "
                             + "ON SQL1.dbo.Orders.OrderID = SQL1.dbo.order_details.OrderID";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();

        GroupSymbol g1 = getFactory().newGroupSymbol("SQL1.dbo.Customers");
        GroupSymbol g2 = getFactory().newGroupSymbol("SQL1.dbo.Orders");
        GroupSymbol g3 = getFactory().newGroupSymbol("SQL1.dbo.order_details");

        ElementSymbol e1 = getFactory().newElementSymbol("SQL1.dbo.Customers.CustomerID");
        ElementSymbol e2 = getFactory().newElementSymbol("SQL1.dbo.Orders.CustomerID");
        ElementSymbol e3 = getFactory().newElementSymbol("SQL1.dbo.Orders.OrderID");
        ElementSymbol e4 = getFactory().newElementSymbol("SQL1.dbo.order_details.OrderID");

        List jcrits1 = new ArrayList();
        jcrits1.add(getFactory().newCompareCriteria(e1, Operator.EQ, e2));
        List jcrits2 = new ArrayList();
        jcrits2.add(getFactory().newCompareCriteria(e3, Operator.EQ, e4));

        JoinPredicate jp1 = getFactory().newJoinPredicate(getFactory().newUnaryFromClause(g1), getFactory().newUnaryFromClause(g2), JoinType.Types.JOIN_INNER, jcrits1);
        JoinPredicate jp2 = getFactory().newJoinPredicate(jp1, getFactory().newUnaryFromClause(g3), JoinType.Types.JOIN_INNER, jcrits2);

        from.addClause(jp2);
        query.setFrom(from);

        helpTest(sql, sqlExpected, query);
    }

    @Test
    public void testManyInnerJoins2() {
        String sql = "SELECT * " + "FROM A INNER JOIN (B RIGHT OUTER JOIN C ON b1 = c1) " + "ON a1 = b1 " + "INNER JOIN D "
                     + "ON a1 = d1";

        String sqlExpected = "SELECT * " + "FROM (A INNER JOIN (B RIGHT OUTER JOIN C ON b1 = c1) " + "ON a1 = b1) "
                             + "INNER JOIN D " + "ON a1 = d1";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();

        UnaryFromClause g1 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("A"));
        UnaryFromClause g2 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("B"));
        UnaryFromClause g3 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("C"));
        UnaryFromClause g4 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("D"));

        ElementSymbol e1 = getFactory().newElementSymbol("a1");
        ElementSymbol e2 = getFactory().newElementSymbol("b1");
        ElementSymbol e3 = getFactory().newElementSymbol("c1");
        ElementSymbol e4 = getFactory().newElementSymbol("d1");

        List jcrits1 = new ArrayList();
        jcrits1.add(getFactory().newCompareCriteria(e1, Operator.EQ, e2));
        List jcrits2 = new ArrayList();
        jcrits2.add(getFactory().newCompareCriteria(e2, Operator.EQ, e3));
        List jcrits3 = new ArrayList();
        jcrits3.add(getFactory().newCompareCriteria(e1, Operator.EQ, e4));

        JoinPredicate jp1 = getFactory().newJoinPredicate(g2, g3, JoinType.Types.JOIN_RIGHT_OUTER, jcrits2);
        JoinPredicate jp2 = getFactory().newJoinPredicate(g1, jp1, JoinType.Types.JOIN_INNER, jcrits1);
        JoinPredicate jp3 = getFactory().newJoinPredicate(jp2, g4, JoinType.Types.JOIN_INNER, jcrits3);

        from.addClause(jp3);
        query.setFrom(from);

        helpTest(sql, sqlExpected, query);
    }

    @Test
    public void testManyInnerJoins3() {
        String sql = "SELECT * " + "FROM A INNER JOIN " + "(B RIGHT OUTER JOIN C ON b1 = c1 " + "CROSS JOIN D) " + "ON a1 = d1";

        String sqlExpected = "SELECT * " + "FROM A INNER JOIN " + "((B RIGHT OUTER JOIN C ON b1 = c1) " + "CROSS JOIN D) "
                             + "ON a1 = d1";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        query.setSelect(select);
        From from = getFactory().newFrom();

        UnaryFromClause g1 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("A"));
        UnaryFromClause g2 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("B"));
        UnaryFromClause g3 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("C"));
        UnaryFromClause g4 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("D"));

        ElementSymbol e1 = getFactory().newElementSymbol("a1");
        ElementSymbol e2 = getFactory().newElementSymbol("b1");
        ElementSymbol e3 = getFactory().newElementSymbol("c1");
        ElementSymbol e4 = getFactory().newElementSymbol("d1");

        List jcrits1 = new ArrayList();
        jcrits1.add(getFactory().newCompareCriteria(e2, Operator.EQ, e3));
        List jcrits2 = new ArrayList();
        jcrits2.add(getFactory().newCompareCriteria(e1, Operator.EQ, e4));

        JoinPredicate jp1 = getFactory().newJoinPredicate(g2, g3, JoinType.Types.JOIN_RIGHT_OUTER, jcrits1);
        JoinPredicate jp2 = getFactory().newJoinPredicate(jp1, g4, JoinType.Types.JOIN_CROSS);
        JoinPredicate jp3 = getFactory().newJoinPredicate(g1, jp2, JoinType.Types.JOIN_INNER, jcrits2);

        from.addClause(jp3);
        query.setFrom(from);

        helpTest(sql, sqlExpected, query);
    }

    @Test
    public void testLoopStatement() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol c1 = getFactory().newElementSymbol("c1");
        select.addSymbol(c1);
        select.addSymbol(getFactory().newElementSymbol("c2"));

        Query query = getFactory().newQuery(select, from);

        ElementSymbol x = getFactory().newElementSymbol("x");
        String intType = new String("integer");
        Statement dStmt = getFactory().newDeclareStatement(x, intType);
        c1 = getFactory().newElementSymbol("mycursor.c1");
        Statement assignmentStmt = getFactory().newAssignmentStatement(x, c1);
        Block block = getFactory().newBlock();
        block.addStatement(dStmt);
        block.addStatement(assignmentStmt);

        String cursor = "mycursor";

        LoopStatement loopStmt = getFactory().newLoopStatement(block, query, cursor);

        helpStmtTest("LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor BEGIN DECLARE integer x; x=mycursor.c1; END",
                     "LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor" + "\n" + "BEGIN" + "\n" + "DECLARE integer x;" + "\n"
                     + "x = mycursor.c1;" + "\n" + "END",
                     loopStmt);
    }

    @Test
    public void testLoopStatementWithOrderBy() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol c1 = getFactory().newElementSymbol("c1");
        select.addSymbol(c1);
        select.addSymbol(getFactory().newElementSymbol("c2"));

        OrderBy orderBy = getFactory().newOrderBy();
        orderBy.addVariable(c1);

        Query query = getFactory().newQuery(select, from);
        query.setOrderBy(orderBy);

        ElementSymbol x = getFactory().newElementSymbol("x");
        String intType = new String("integer");
        Statement dStmt = getFactory().newDeclareStatement(x, intType);
        c1 = getFactory().newElementSymbol("mycursor.c1");
        Statement assignmentStmt = getFactory().newAssignmentStatement(x, c1);
        Block block = getFactory().newBlock();
        block.addStatement(dStmt);
        block.addStatement(assignmentStmt);

        String cursor = "mycursor";

        LoopStatement loopStmt = getFactory().newLoopStatement(block, query, cursor);

        helpStmtTest("LOOP ON (SELECT c1, c2 FROM m.g ORDER BY c1) AS mycursor BEGIN DECLARE integer x; x=mycursor.c1; END",
                     "LOOP ON (SELECT c1, c2 FROM m.g ORDER BY c1) AS mycursor" + "\n" + "BEGIN" + "\n" + "DECLARE integer x;"
                     + "\n" + "x = mycursor.c1;" + "\n" + "END",
                     loopStmt);
    }

    @Test
    public void testWhileStatement() throws Exception {
        ElementSymbol x = getFactory().newElementSymbol("x");
        Function f = getFactory().newFunction("+", new Expression[] {x, getFactory().newConstant(new Integer(1))});
        Statement assignmentStmt = getFactory().newAssignmentStatement(x, f);
        Block block = getFactory().newBlock();
        block.addStatement(assignmentStmt);
        Criteria crit = getFactory().newCompareCriteria(x, Operator.LT, getFactory().newConstant(new Integer(100)));
        WhileStatement whileStmt = getFactory().newWhileStatement(crit, block);
        helpStmtTest("WHILE (x < 100) BEGIN x=x+1; END",
                     "WHILE(x < 100)" + "\n" + "BEGIN" + "\n" + "x = (x + 1);" + "\n" + "END",
                     whileStmt);
    }

    @Test
    public void testWhileStatement1() throws Exception {
        ElementSymbol x = getFactory().newElementSymbol("x");
        Function f = getFactory().newFunction("+", new Expression[] {x, getFactory().newConstant(new Integer(1))});
        Statement assignmentStmt = getFactory().newAssignmentStatement(x, f);
        Block block = getFactory().newBlock();
        block.setAtomic(true);
        block.setLabel("1y");
        block.addStatement(assignmentStmt);
        BranchingStatement bs = getFactory().newBranchingStatement(BranchingMode.CONTINUE);
        bs.setLabel("1y");
        block.addStatement(bs);
        Criteria crit = getFactory().newCompareCriteria(x, Operator.LT, getFactory().newConstant(new Integer(100)));
        WhileStatement whileStmt = getFactory().newWhileStatement(crit, block);
        helpStmtTest("WHILE (x < 100) \"1y\": BEGIN ATOMIC x=x+1; CONTINUE \"1y\"; END", "WHILE(x < 100)" + "\n"
                                                                                         + "\"1y\" : BEGIN ATOMIC" + "\n"
                                                                                         + "x = (x + 1);\nCONTINUE \"1y\";"
                                                                                         + "\n" + "END", whileStmt);
    }

    @Test
    public void testBreakStatement() throws Exception {
        Statement breakStmt = getFactory().newBranchingStatement();
        helpStmtTest("break;", "BREAK;", breakStmt);
    }

    @Test
    public void testContinueStatement() throws Exception {
        BranchingStatement contStmt = getFactory().newBranchingStatement(BranchingMode.CONTINUE);
        helpStmtTest("continue;", "CONTINUE;", contStmt);
    }

    @Test
    public void testContinueStatement1() throws Exception {
        BranchingStatement contStmt = getFactory().newBranchingStatement(BranchingMode.CONTINUE);
        contStmt.setLabel("x");
        helpStmtTest("continue x;", "CONTINUE x;", contStmt);
    }

    @Test
    public void testScalarSubqueryExpressionInSelect() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        s2.addSymbol(getFactory().wrapExpression(getFactory().newScalarSubquery(q1)));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        helpTest("SELECT e1, (SELECT e1 FROM m.g1) FROM m.g2", "SELECT e1, (SELECT e1 FROM m.g1) FROM m.g2", q2);
    }

    @Test
    public void testScalarSubqueryExpressionInSelect2() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().wrapExpression(getFactory().newScalarSubquery(q1)));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        helpTest("SELECT (SELECT e1 FROM m.g1) FROM m.g2", "SELECT (SELECT e1 FROM m.g1) FROM m.g2", q2);
    }

    @Test
    public void testScalarSubqueryExpressionInSelect3() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().wrapExpression(getFactory().newScalarSubquery(q1)));
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        helpTest("SELECT (SELECT e1 FROM m.g1), e1 FROM m.g2", "SELECT (SELECT e1 FROM m.g1), e1 FROM m.g2", q2);
    }

    @Test
    public void testScalarSubqueryExpressionWithAlias() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        s2.addSymbol(getFactory().newAliasSymbol("X", getFactory().wrapExpression(getFactory().newScalarSubquery(q1))));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        helpTest("SELECT e1, (SELECT e1 FROM m.g1) as X FROM m.g2", "SELECT e1, (SELECT e1 FROM m.g1) AS X FROM m.g2", q2);
    }

    @Test
    public void testScalarSubqueryExpressionInComplexExpression() throws Exception {
        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));

        s2.addSymbol(getFactory().newAliasSymbol("X", getFactory().wrapExpression(parser.parseExpression("(SELECT e1 FROM m.g1) + 2"))));

        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        helpTest("SELECT e1, ((SELECT e1 FROM m.g1) + 2) as X FROM m.g2",
                 "SELECT e1, ((SELECT e1 FROM m.g1) + 2) AS X FROM m.g2",
                 q2);
    }

    @Test
    public void testScalarSubqueryExpressionInComplexExpression2() throws Exception {
        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));

        s2.addSymbol(getFactory().newAliasSymbol("X", getFactory().wrapExpression(parser.parseExpression("3 + (SELECT e1 FROM m.g1)"))));

        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        helpTest("SELECT e1, (3 + (SELECT e1 FROM m.g1)) as X FROM m.g2",
                 "SELECT e1, (3 + (SELECT e1 FROM m.g1)) AS X FROM m.g2",
                 q2);
    }

    @Test
    public void testScalarSubqueryExpressionInComplexExpression3() throws Exception {
        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));

        s2.addSymbol(getFactory().newAliasSymbol("X", getFactory().wrapExpression(parser.parseExpression("(SELECT e1 FROM m.g1) + (SELECT e3 FROM m.g3)"))));

        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        helpTest("SELECT e1, ((SELECT e1 FROM m.g1) + (SELECT e3 FROM m.g3)) as X FROM m.g2",
                 "SELECT e1, ((SELECT e1 FROM m.g1) + (SELECT e3 FROM m.g3)) AS X FROM m.g2",
                 q2);
    }

    @Test
    public void testScalarSubqueryExpressionInFunction() throws Exception {
        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));

        s2.addSymbol(getFactory().newAliasSymbol("X", getFactory().wrapExpression(parser.parseExpression("length((SELECT e1 FROM m.g1))"))));

        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Query q2 = getFactory().newQuery(s2, f2);

        helpTest("SELECT e1, length((SELECT e1 FROM m.g1)) as X FROM m.g2",
                 "SELECT e1, length((SELECT e1 FROM m.g1)) AS X FROM m.g2",
                 q2);
    }

    @Test
    public void testExistsPredicateCriteria() {

        Query q2 = exampleExists(false);

        helpTest("SELECT e1 FROM m.g2 WHERE Exists (SELECT e1 FROM m.g1)",
                 "SELECT e1 FROM m.g2 WHERE EXISTS (SELECT e1 FROM m.g1)",
                 q2);
    }

    protected Query exampleExists(boolean semiJoin) {
        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        ExistsCriteria existsCrit = getFactory().newExistsCriteria(q1);
        existsCrit.getSubqueryHint().setMergeJoin(semiJoin);
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);
        q2.setCriteria(existsCrit);
        return q2;
    }

    @Test
    public void testAnyQuantifierSubqueryComparePredicate() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Criteria left = getFactory().newSubqueryCompareCriteria(getFactory().newElementSymbol("e3"), q1, Operator.GE, PredicateQuantifier.ANY);
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);
        q2.setCriteria(left);

        helpTest("SELECT e1 FROM m.g2 WHERE e3 >= ANY (SELECT e1 FROM m.g1)",
                 "SELECT e1 FROM m.g2 WHERE e3 >= ANY (SELECT e1 FROM m.g1)",
                 q2);

    }

    @Test
    public void testSomeQuantifierSubqueryComparePredicate() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Criteria left = getFactory().newSubqueryCompareCriteria(getFactory().newElementSymbol("e3"), q1, Operator.GT, PredicateQuantifier.SOME);
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);
        q2.setCriteria(left);

        helpTest("SELECT e1 FROM m.g2 WHERE e3 > some (SELECT e1 FROM m.g1)",
                 "SELECT e1 FROM m.g2 WHERE e3 > SOME (SELECT e1 FROM m.g1)",
                 q2);

    }

    @Test
    public void testAllQuantifierSubqueryComparePredicate() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Criteria left = getFactory().newSubqueryCompareCriteria(getFactory().newElementSymbol("e3"), q1, Operator.EQ, PredicateQuantifier.ALL);
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);
        q2.setCriteria(left);

        helpTest("SELECT e1 FROM m.g2 WHERE e3 = all (SELECT e1 FROM m.g1)",
                 "SELECT e1 FROM m.g2 WHERE e3 = ALL (SELECT e1 FROM m.g1)",
                 q2);

    }

    @Test
    public void testScalarSubqueryComparePredicate() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Criteria left = getFactory().newCompareCriteria(getFactory().newElementSymbol("e3"), Operator.LT, getFactory().newScalarSubquery(q1));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);
        q2.setCriteria(left);

        helpTest("SELECT e1 FROM m.g2 WHERE e3 < (SELECT e1 FROM m.g1)",
                 "SELECT e1 FROM m.g2 WHERE e3 < (SELECT e1 FROM m.g1)",
                 q2);

    }

    @Test
    public void testSelectInto() {
        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol c1 = getFactory().newElementSymbol("c1");
        select.addSymbol(c1);
        select.addSymbol(getFactory().newElementSymbol("c2"));

        Into into = getFactory().newInto(getFactory().newGroupSymbol("#temp"));
        Query q = getFactory().newQuery();
        q.setSelect(select);
        q.setFrom(from);
        q.setInto(into);
        helpTest("SELECT c1, c2 INTO #temp FROM m.g", "SELECT c1, c2 INTO #temp FROM m.g", q);
    }

    @Test
    public void testAndOrPrecedence_1575() {
        Select s = getFactory().newSelect();
        s.addSymbol(getFactory().newMultipleElementSymbol());
        From f = getFactory().newFrom();
        f.addGroup(getFactory().newGroupSymbol("m.g1"));
        CompareCriteria c1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, getFactory().newConstant(new Integer(0)));
        CompareCriteria c2 = getFactory().newCompareCriteria(getFactory().newElementSymbol("e2"), Operator.EQ, getFactory().newConstant(new Integer(1)));
        CompareCriteria c3 = getFactory().newCompareCriteria(getFactory().newElementSymbol("e3"), Operator.EQ, getFactory().newConstant(new Integer(3)));
        CompoundCriteria cc1 = getFactory().newCompoundCriteria(CompoundCriteria.AND, c2, c3);
        CompoundCriteria cc2 = getFactory().newCompoundCriteria(CompoundCriteria.OR, c1, cc1);
        Query q = getFactory().newQuery();
        q.setSelect(s);
        q.setFrom(f);
        q.setCriteria(cc2);

        helpTest("SELECT * FROM m.g1 WHERE e1=0 OR e2=1 AND e3=3",
                 "SELECT * FROM m.g1 WHERE (e1 = 0) OR ((e2 = 1) AND (e3 = 3))",
                 q);
    }

    @Test
    public void testAndOrPrecedence2_1575() {
        Select s = getFactory().newSelect();
        s.addSymbol(getFactory().newMultipleElementSymbol());
        From f = getFactory().newFrom();
        f.addGroup(getFactory().newGroupSymbol("m.g1"));
        CompareCriteria c1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, getFactory().newConstant(new Integer(0)));
        CompareCriteria c2 = getFactory().newCompareCriteria(getFactory().newElementSymbol("e2"), Operator.EQ, getFactory().newConstant(new Integer(1)));
        CompareCriteria c3 = getFactory().newCompareCriteria(getFactory().newElementSymbol("e3"), Operator.EQ, getFactory().newConstant(new Integer(3)));
        CompoundCriteria cc1 = getFactory().newCompoundCriteria(CompoundCriteria.AND, c1, c2);
        CompoundCriteria cc2 = getFactory().newCompoundCriteria(CompoundCriteria.OR, cc1, c3);
        Query q = getFactory().newQuery();
        q.setSelect(s);
        q.setFrom(f);
        q.setCriteria(cc2);

        helpTest("SELECT * FROM m.g1 WHERE e1=0 AND e2=1 OR e3=3",
                 "SELECT * FROM m.g1 WHERE ((e1 = 0) AND (e2 = 1)) OR (e3 = 3)",
                 q);
    }

    protected void helpTestCompoundNonJoinCriteria(String sqlPred, Criteria predCrit) {
        Select s = getFactory().newSelect();
        s.addSymbol(getFactory().newMultipleElementSymbol());
        From f = getFactory().newFrom();

        CompareCriteria c1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, getFactory().newConstant(new Integer(0)));
        CompoundCriteria cc1 = getFactory().newCompoundCriteria(CompoundCriteria.AND, c1, predCrit);
        JoinPredicate jp = getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g1")),
                                            getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g2")),
                                            JoinType.Types.JOIN_INNER,
                                            Collections.singletonList(cc1));
        f.addClause(jp);

        Query q = getFactory().newQuery();
        q.setSelect(s);
        q.setFrom(f);

        helpTest("SELECT * FROM m.g1 JOIN m.g2 ON e1=0 AND " + sqlPred, "SELECT * FROM m.g1 INNER JOIN m.g2 ON e1 = 0 AND "
                                                                        + sqlPred, q);

    }

    @Test
    public void testCompoundNonJoinCriteriaInFromWithComparisonCriteria() {
        CompareCriteria c2 = getFactory().newCompareCriteria(getFactory().newElementSymbol("e2"), Operator.EQ, getFactory().newConstant(new Integer(1)));
        helpTestCompoundNonJoinCriteria("e2 = 1", c2);
    }

    @Test
    public void testCompoundNonJoinCriteriaInFromWithIsNull() {
        helpTestCompoundNonJoinCriteria("e2 IS NULL", getFactory().newIsNullCriteria(getFactory().newElementSymbol("e2")));
    }

    @Test
    public void testCompoundNonJoinCriteriaInFromUWithIN() {
        List<Expression> values = new ArrayList<Expression>();
        values.add(getFactory().newConstant(new Integer(0)));
        values.add(getFactory().newConstant(new Integer(1)));
        SetCriteria crit = getFactory().newSetCriteria(getFactory().newElementSymbol("e2"), values);
        helpTestCompoundNonJoinCriteria("e2 IN (0, 1)", crit);
    }

    @Test
    public void testCompoundNonJoinCriteriaInFromUWithLIKE() {
        MatchCriteria crit = getFactory().newMatchCriteria(getFactory().newElementSymbol("e2"), getFactory().newConstant("%"));
        helpTestCompoundNonJoinCriteria("e2 LIKE '%'", crit);
    }

    @Test
    public void testCompoundNonJoinCriteria_defect15167_1() throws Exception {
        parser.parseCommand("SELECT A.alert_id, A.primary_entity_name, A.primary_entity_level_code, A.alert_description, A.create_date, A.alert_risk_score, S.scenario_name, A.alert_status_code, A.process_id, A.actual_values_text, S.SCENARIO_CATEGORY_DESC, A.primary_entity_number, A.scenario_id, A.primary_entity_key FROM (FSK_ALERT AS A LEFT OUTER JOIN FSK_SCENARIO AS S ON A.scenario_id = S.scenario_id) INNER JOIN FSC_ACCOUNT_DIM AS C ON A.primary_entity_key = C.ACCOUNT_KEY  AND ((S.current_ind = 'Y') OR (S.current_ind IS NULL)) WHERE (A.primary_entity_level_code = 'ACC') AND (C.ACCOUNT_KEY = 23923) AND (A.logical_delete_ind = 'N')");
    }

    @Test
    public void testCompoundNonJoinCriteria_defect15167_2() throws Exception {
        parser.parseCommand("SELECT A.alert_id, A.primary_entity_name, A.primary_entity_level_code, A.alert_description, A.create_date, A.alert_risk_score, S.scenario_name, A.alert_status_code, A.process_id, A.actual_values_text, S.SCENARIO_CATEGORY_DESC, A.primary_entity_number, A.scenario_id, A.primary_entity_key FROM (FSK_ALERT AS A LEFT OUTER JOIN FSK_SCENARIO AS S ON A.scenario_id = S.scenario_id) INNER JOIN FSC_ACCOUNT_DIM AS C ON A.primary_entity_key = C.ACCOUNT_KEY  AND (S.current_ind = 'Y' OR S.current_ind IS NULL) WHERE (A.primary_entity_level_code = 'ACC') AND (C.ACCOUNT_KEY = 23923) AND (A.logical_delete_ind = 'N')");
    }

    @Test
    public void testCompoundNonJoinCriteria_defect15167_3() throws Exception {
        parser.parseCommand("SELECT A.alert_id, A.primary_entity_name, A.primary_entity_level_code, A.alert_description, A.create_date, A.alert_risk_score, S.scenario_name, A.alert_status_code, A.process_id, A.actual_values_text, S.SCENARIO_CATEGORY_DESC, A.primary_entity_number, A.scenario_id, A.primary_entity_key FROM (FSK_ALERT AS A LEFT OUTER JOIN FSK_SCENARIO AS S ON A.scenario_id = S.scenario_id) INNER JOIN FSC_ACCOUNT_DIM AS C ON (A.primary_entity_key = C.ACCOUNT_KEY AND (S.current_ind = 'Y' OR S.current_ind IS NULL)) WHERE (A.primary_entity_level_code = 'ACC') AND (C.ACCOUNT_KEY = 23923) AND (A.logical_delete_ind = 'N')");
    }

    @Test
    public void testCompoundNonJoinCriteria_defect15167_4() throws Exception {
        parser.parseCommand("SELECT A.alert_id, A.primary_entity_name, A.primary_entity_level_code, A.alert_description, A.create_date, A.alert_risk_score, S.scenario_name, A.alert_status_code, A.process_id, A.actual_values_text, S.SCENARIO_CATEGORY_DESC, A.primary_entity_number, A.scenario_id, A.primary_entity_key FROM (FSK_ALERT AS A LEFT OUTER JOIN FSK_SCENARIO AS S ON A.scenario_id = S.scenario_id) INNER JOIN FSC_ACCOUNT_DIM AS C ON (A.primary_entity_key = C.ACCOUNT_KEY AND S.current_ind = 'Y' OR S.current_ind IS NULL) WHERE (A.primary_entity_level_code = 'ACC') AND (C.ACCOUNT_KEY = 23923) AND (A.logical_delete_ind = 'N')");
    }

    @Test
    public void testFunctionInGroupBy() throws Exception {
        parser.parseCommand("SELECT SUM(s), elem+1 FROM m.g GROUP BY elem+1");
    }

    @Test
    public void testCaseInGroupBy() throws Exception {
        parser.parseCommand("SELECT SUM(elem+1), CASE elem WHEN 0 THEN 1 ELSE 2 END AS c FROM m.g GROUP BY CASE elem WHEN 0 THEN 1 ELSE 2 END");
    }

    @Test
    public void testNationCharString() throws Exception {
        Query query = (Query)parser.parseCommand("SELECT N'blah' FROM m.g");
        Select select = query.getSelect();
        ExpressionSymbol s = (ExpressionSymbol)select.getSymbol(0);
        Constant c = (Constant)s.getExpression();
        assertEquals(c, getFactory().newConstant("blah"));
    }

    @Test
    public void testNationCharString2() throws Exception {
        Query query = (Query)parser.parseCommand("SELECT DISTINCT TABLE_QUALIFIER, NULL AS TABLE_OWNER, NULL AS TABLE_NAME, NULL AS TABLE_TYPE, NULL AS REMARKS FROM ATIODBCSYSTEM.OA_TABLES  WHERE TABLE_QUALIFIER LIKE N'%'  ESCAPE '\\'  ORDER BY TABLE_QUALIFIER  ");
        MatchCriteria matchCrit = (MatchCriteria)query.getCriteria();
        Constant c = (Constant)matchCrit.getRightExpression();
        assertEquals(c, getFactory().newConstant("%"));
    }

    @Test
    public void testScalarSubquery() throws Exception {
        parser.parseCommand("SELECT (SELECT 1) FROM x");
    }

    @Test
    public void testElementInDoubleQuotes() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("x");
        From from = getFactory().newFrom();
        from.addGroup(g);

        ElementSymbol e = getFactory().newElementSymbol("foo");
        Select select = getFactory().newSelect();
        select.addSymbol(e);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT \"foo\" FROM x", "SELECT foo FROM x", query);
    }

    @Test
    public void testElementInDoubleQuotes_Insert() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("x");
        From from = getFactory().newFrom();
        from.addGroup(g);

        ElementSymbol e = getFactory().newElementSymbol("foo");

        Insert query = getFactory().newInsert();
        query.setGroup(g);
        query.addVariable(e);
        query.addValue(getFactory().newConstant("bar", String.class));

        helpTest("insert into x (\"foo\") values ('bar')", "INSERT INTO x (foo) VALUES ('bar')", query);
    }

    @Test
    public void testElementInDoubleQuotes_Update() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("x");
        From from = getFactory().newFrom();
        from.addGroup(g);

        ElementSymbol e = getFactory().newElementSymbol("foo");
        Update query = getFactory().newUpdate();
        query.setGroup(g);
        query.addChange(e, getFactory().newConstant("bar", String.class));

        helpTest("update x set \"foo\"='bar'", "UPDATE x SET foo = 'bar'", query);
    }

    @Test
    public void testElementInDoubleQuotes_delete() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("x");
        From from = getFactory().newFrom();
        from.addGroup(g);

        ElementSymbol e = getFactory().newElementSymbol("foo");
        CompareCriteria c = getFactory().newCompareCriteria(e, Operator.EQ, getFactory().newConstant("bar", String.class));
        Delete query = getFactory().newDelete(g, c);

        helpTest("delete from x where \"foo\"='bar'", "DELETE FROM x WHERE foo = 'bar'", query);
    }

    @Test
    public void testAliasInDoubleQuotes() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("x");
        From from = getFactory().newFrom();
        from.addGroup(g);

        AliasSymbol as = getFactory().newAliasSymbol("fooAlias", getFactory().newElementSymbol("fooKey"));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT fooKey AS \"fooAlias\" FROM x", "SELECT fooKey AS fooAlias FROM x", query);
    }

    @Test
    public void testAliasInDoubleQuotesWithQuotedGroup() throws Exception {

        GroupSymbol g = getFactory().newGroupSymbol("x.y.z");
        From from = getFactory().newFrom();
        from.addGroup(g);

        AliasSymbol as = getFactory().newAliasSymbol("fooAlias", getFactory().newElementSymbol("fooKey"));
        Select select = getFactory().newSelect();
        select.addSymbol(as);

        ElementSymbol a = getFactory().newElementSymbol("x.y.z.id");
        Constant c = getFactory().newConstant(new Integer(10));
        Criteria crit = getFactory().newCompareCriteria(a, Operator.EQ, c);

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);

        helpTest("SELECT fooKey AS \"fooAlias\" FROM \"x.y\".z where x.\"y.z\".id = 10",
                 "SELECT fooKey AS fooAlias FROM x.y.z WHERE x.y.z.id = 10",
                 query);
    }

    @Test
    public void testSingleQuotedConstant() throws Exception {

        GroupSymbol g = getFactory().newGroupSymbol("x.y.z");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Constant as = getFactory().newConstant("fooString");
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(as));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 'fooString' FROM \"x.y.z\"", "SELECT 'fooString' FROM x.y.z", query);
    }

    /** QUERY Tool Format*/
    @Test
    public void testQueryWithQuotes_MSQuery() throws Exception {
        parser.parseCommand("SELECT \"PART_COLOR\", \"PART_ID\", \"PART_NAME\", \"PART_WEIGHT\" FROM \"VirtualParts.base\".\"Parts\"");
    }

    /** MS Access Format**/
    @Test
    public void testQueryWithQuotes_MSAccess() throws Exception {
        parser.parseCommand("SELECT \"PART_COLOR\" ,\"PART_ID\" ,\"PART_NAME\" ,\"PART_WEIGHT\"  FROM \"parts_oracle.DEV_RRAMESH\".\"PARTS\"");
    }

    /** BO Business View Manager**/
    @Test
    public void testQueryWithQuotes_BODesigner() throws Exception {
        parser.parseCommand("SELECT DISTINCT \"PARTS\".\"PART_NAME\" FROM   \"parts_oracle.DEV_RRAMESH\".\"PARTS\" \"PARTS\"");
    }

    /** Crystal Reports **/
    @Test
    public void testQueryWithQuotes_CrystalReports() throws Exception {
        parser.parseCommand("SELECT \"Oracle_PARTS\".\"PART_COLOR\", \"Oracle_PARTS\".\"PART_ID\", \"Oracle_PARTS\".\"PART_NAME\", \"Oracle_PARTS\".\"PART_WEIGHT\", \"SQL_PARTS\".\"PART_COLOR\", \"SQL_PARTS\".\"PART_ID\", \"SQL_PARTS\".\"PART_NAME\", \"SQL_PARTS\".\"PART_WEIGHT\" FROM   \"parts_oracle.DEV_RRAMESH\".\"PARTS\" \"Oracle_PARTS\", \"parts_sqlserver.dv_rreddy.dv_rreddy\".\"PARTS\" \"SQL_PARTS\" WHERE  (\"Oracle_PARTS\".\"PART_ID\"=\"SQL_PARTS\".\"PART_ID\")");
    }

    @Test
    public void testOrderByWithNumbers_InQuotes() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("z");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("x"));
        select.addSymbol(getFactory().newElementSymbol("y"));

        OrderBy orderby = getFactory().newOrderBy();
        orderby.addVariable(getFactory().newElementSymbol("1"), true);

        Query query = getFactory().newQuery(select, from);
        query.setOrderBy(orderby);

        helpTest("SELECT x, y from z order by \"1\"", "SELECT x, y FROM z ORDER BY \"1\"", query);
    }

    @Test
    public void testOrderByWithNumbers_AsInt() throws Exception {
        GroupSymbol g = getFactory().newGroupSymbol("z");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("x"));
        select.addSymbol(getFactory().newElementSymbol("y"));

        OrderBy orderby = getFactory().newOrderBy();
        orderby.addVariable(getFactory().wrapExpression(getFactory().newConstant(1)), true);

        Query query = getFactory().newQuery(select, from);
        query.setOrderBy(orderby);

        helpTest("SELECT x, y FROM z order by 1", "SELECT x, y FROM z ORDER BY 1", query);
    }

    @Test
    public void testEmptyAndNullInputsGiveSameErrorMessage() throws Exception {
        String emptyMessage = null;
        try {
            parser.parseCommand("");
            fail("Expected exception for parsing empty string");
        } catch (Exception e) {
            emptyMessage = e.getMessage();
        }

        String nullMessage = null;
        try {
            parser.parseCommand(null);
            fail("Expected exception for parsing null string");
        } catch (Exception e) {
            nullMessage = e.getMessage();
        }

        assertTrue("Expected same message for empty and null cases", emptyMessage.equals(nullMessage));
    }

    @Test
    public void testCase3281NamedVariable() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setDisplayNamedParameters(true);
        storedQuery.setProcedureName("proc1");
        SPParameter parameter = getFactory().newSPParameter(1, getFactory().newConstant("paramValue1"));
        parameter.setName("param1");
        parameter.setParameterType(ParameterInfo.IN);
        storedQuery.setParameter(parameter);
        helpTest("Exec proc1(param1 = 'paramValue1')", "EXEC proc1(param1 => 'paramValue1')", storedQuery);
        helpTest("execute proc1(param1 = 'paramValue1')", "EXEC proc1(param1 => 'paramValue1')", storedQuery);
    }

    @Test
    public void testCase3281NamedVariables() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setDisplayNamedParameters(true);
        storedQuery.setProcedureName("proc1");
        SPParameter param1 = getFactory().newSPParameter(1, getFactory().newConstant("paramValue1"));
        param1.setName("param1");
        param1.setParameterType(ParameterInfo.IN);
        storedQuery.setParameter(param1);
        SPParameter param2 = getFactory().newSPParameter(2, getFactory().newConstant("paramValue2"));
        param2.setName("param2");
        param2.setParameterType(ParameterInfo.IN);
        storedQuery.setParameter(param2);
        helpTest("Exec proc1(param1 = 'paramValue1', param2 = 'paramValue2')",
                 "EXEC proc1(param1 => 'paramValue1', param2 => 'paramValue2')",
                 storedQuery);
        helpTest("execute proc1(param1 = 'paramValue1', param2 = 'paramValue2')",
                 "EXEC proc1(param1 => 'paramValue1', param2 => 'paramValue2')",
                 storedQuery);
    }

    @Test
    public void testCase3281QuotedNamedVariableFails2() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setProcedureName("proc1");
        SPParameter param1 = getFactory().newSPParameter(1, getFactory().newCompareCriteria(getFactory().newConstant("a"), Operator.EQ, getFactory().newConstant("b")));
        param1.setParameterType(ParameterInfo.IN);
        storedQuery.setParameter(param1);
        helpTest("Exec proc1('a' = 'b')", "EXEC proc1(('a' = 'b'))", storedQuery);
    }

    /** Test what happens if the name of a parameter is a reserved word.  It must be quoted (double-ticks). */
    @Test
    public void testCase3281NamedVariablesReservedWords() {
        StoredProcedure storedQuery = getFactory().newStoredProcedure();
        storedQuery.setDisplayNamedParameters(true);
        storedQuery.setProcedureName("proc1");
        SPParameter param1 = getFactory().newSPParameter(1, getFactory().newConstant("paramValue1"));
        param1.setName("in"); //<---RESERVED WORD
        param1.setParameterType(ParameterInfo.IN);
        storedQuery.setParameter(param1);
        SPParameter param2 = getFactory().newSPParameter(2, getFactory().newConstant("paramValue2"));
        param2.setName("in2");
        param2.setParameterType(ParameterInfo.IN);
        storedQuery.setParameter(param2);
        helpTest("Exec proc1(\"in\" = 'paramValue1', in2 = 'paramValue2')",
                 "EXEC proc1(\"in\" => 'paramValue1', in2 => 'paramValue2')",
                 storedQuery);
        helpTest("execute proc1(\"in\" = 'paramValue1', in2 = 'paramValue2')",
                 "EXEC proc1(\"in\" => 'paramValue1', in2 => 'paramValue2')",
                 storedQuery);
    }

    @Test
    public void testExceptionMessageWithLocation() {
        try {
            parser.parseCommand("SELECT FROM");
            fail("Should not be able to parse invalid sql");
        } catch (Exception e) {
            // Exception should be thrown
        }
    }

    @Test
    public void testEscapedOuterJoin() {
        String sql = "SELECT * FROM {oj A LEFT OUTER JOIN B ON (A.x=B.x)}";
        String expected = "SELECT * FROM A LEFT OUTER JOIN B ON A.x = B.x";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        query.setSelect(select);
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom();
        query.setFrom(from);
        Criteria compareCriteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("A.x"), Operator.EQ, getFactory().newElementSymbol("B.x"));
        FromClause f1 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("A"));
        FromClause f2 = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("B"));
        JoinPredicate jp = getFactory().newJoinPredicate(f1,
                                            f2,
                                            JoinType.Types.JOIN_LEFT_OUTER,
                                            Arrays.asList(new Criteria[] {compareCriteria}));
        from.addClause(jp);

        helpTest(sql, expected, query);
    }

    @Test
    public void testNameSpacedFunctionName() {
        String sql = "select a.x()";

        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        Function func1 = getFactory().newFunction("a.x", new Expression[] {});
        Expression expr = getFactory().wrapExpression(func1);
        select.addSymbol(expr);
        query.setSelect(select);

        helpTest(sql, "SELECT a.x()", query);
    }

    @Test
    public void testUnionJoin() {
        String sql = "select * from pm1.g1 union join pm1.g2 where g1.e1 = 1";
        String expected = "SELECT * FROM pm1.g1 UNION JOIN pm1.g2 WHERE g1.e1 = 1";

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());

        From from = getFactory().newFrom();
        from.addClause(getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("pm1.g1")),
                                        getFactory().newUnaryFromClause(getFactory().newGroupSymbol("pm1.g2")),
                                        JoinType.Types.JOIN_UNION));

        Criteria crit = getFactory().newCompareCriteria(getFactory().newElementSymbol("g1.e1"), Operator.EQ, getFactory().newConstant(new Integer(1)));

        Query command = getFactory().newQuery(select, from);
        command.setCriteria(crit);
        helpTest(sql, expected, command);
    }

    @Test
    public void testCommandWithSemicolon() throws Exception {
        helpTest("select * from pm1.g1;", "SELECT * FROM pm1.g1", parser.parseCommand("select * from pm1.g1"));
    }

    @Test
    public void testLOBTypes() throws Exception {
        Function convert = getFactory().newFunction("convert", new Expression[] {getFactory().newConstant(null), getFactory().newConstant("blob")});
        Function convert1 = getFactory().newFunction("convert", new Expression[] {getFactory().newConstant(null), getFactory().newConstant("clob")});
        Function convert2 = getFactory().newFunction("convert", new Expression[] {getFactory().newConstant(null), getFactory().newConstant("xml")});
        Select select = getFactory().newSelect(Arrays.asList(getFactory().wrapExpression(convert, "expr"), getFactory().wrapExpression(convert1, "expr1"), getFactory().wrapExpression(convert2, "expr2")));
        Query query = getFactory().newQuery();
        query.setSelect(select);

        helpTest("select convert(null, blob), convert(null, clob), convert(null, xml)",
                 "SELECT convert(null, blob), convert(null, clob), convert(null, xml)",
                 query);
    }

    @Test
    public void testInsertWithoutColumns() {
        Insert insert = getFactory().newInsert();
        insert.setGroup(getFactory().newGroupSymbol("m.g"));
        insert.addValue(getFactory().newConstant("a"));
        insert.addValue(getFactory().newConstant("b"));
        helpTest("INSERT INTO m.g VALUES ('a', 'b')", "INSERT INTO m.g VALUES ('a', 'b')", insert);
    }

    @Test
    public void testXmlElement() throws Exception {
        XMLElement f = getFactory().newXMLElement("table", Arrays.asList((Expression)getFactory().newConstant("x")));
        helpTestExpression("xmlelement(name \"table\", 'x')", "XMLELEMENT(NAME \"table\", 'x')", f);
    }

    @Test
    public void testXmlElement1() throws Exception {
        XMLElement f = getFactory().newXMLElement("table", Arrays.asList((Expression)getFactory().newConstant("x")));
        helpTestExpression("xmlelement(\"table\", 'x')", "XMLELEMENT(NAME \"table\", 'x')", f);
    }

    @Test
    public void testXmlElementWithAttributes() throws Exception {
        XMLElement f = getFactory().newXMLElement("y", new ArrayList<Expression>());
        f.setAttributes(getFactory().newXMLAttributes(Arrays.asList(getFactory().newDerivedColumn("val", getFactory().newConstant("a")))));
        helpTestExpression("xmlelement(y, xmlattributes('a' as val))", "XMLELEMENT(NAME y, XMLATTRIBUTES('a' AS val))", f);
    }

    @Test
    public void testXmlForest() throws Exception {
        XMLForest f = getFactory().newXMLForest(Arrays.asList(getFactory().newDerivedColumn("table", getFactory().newElementSymbol("a"))));
        helpTestExpression("xmlforest(a as \"table\")", "XMLFOREST(a AS \"table\")", f);
    }

    @Test
    public void testXmlPi() throws Exception {
        Function f = getFactory().newFunction("xmlpi", new Expression[] {getFactory().newConstant("a"), getFactory().newElementSymbol("val")});
        helpTestExpression("xmlpi(NAME a, val)", "xmlpi(NAME a, val)", f);
    }

    @Test
    public void testXmlNamespaces() throws Exception {
        XMLForest f = getFactory().newXMLForest(Arrays.asList(getFactory().newDerivedColumn("table", getFactory().newElementSymbol("a"))));
        f.setNamespaces(getFactory().newXMLNamespaces(Arrays.asList(new NamespaceItem(), new NamespaceItem("http://foo", "x"))));
        helpTestExpression("xmlforest(xmlnamespaces(no default, 'http://foo' as x), a as \"table\")",
                           "XMLFOREST(XMLNAMESPACES(NO DEFAULT, 'http://foo' AS x), a AS \"table\")",
                           f);
    }

    @Test
    public void testXmlAggWithOrderBy() throws Exception {
        String sql = "SELECT xmlAgg(1 order by e2)";
        AggregateSymbol as = getFactory().newAggregateSymbol(Reserved.XMLAGG, false, getFactory().newConstant(1));
        as.setOrderBy(getFactory().newOrderBy(Arrays.asList(getFactory().newElementSymbol("e2"))));
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(as)));
        helpTest(sql, "SELECT XMLAGG(1 ORDER BY e2)", query);
    }

    @Test
    public void testTextAggWithOrderBy() throws Exception {
        List<DerivedColumn> expressions = new ArrayList<DerivedColumn>();
        expressions.add(getFactory().newDerivedColumn("col1", getFactory().newElementSymbol("e1")));
        expressions.add(getFactory().newDerivedColumn("col2", getFactory().newElementSymbol("e2")));

        TextLine tf = getFactory().newTextLine();
        tf.setExpressions(expressions);
        tf.setDelimiter(new Character(','));
        tf.setIncludeHeader(true);

        AggregateSymbol as = getFactory().newAggregateSymbol(NonReserved.TEXTAGG, false, tf);
        as.setOrderBy(getFactory().newOrderBy(Arrays.asList(getFactory().newElementSymbol("e2"))));

        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(as)));

        String sql = "SELECT TextAgg(FOR e1 as col1, e2 as col2 delimiter ',' header order by e2)";
        helpTest(sql, "SELECT TEXTAGG(FOR e1 AS col1, e2 AS col2 DELIMITER ',' HEADER ORDER BY e2)", query);
    }

    @Test
    public void testArrayAggWithOrderBy() throws Exception {
        String sql = "SELECT array_agg(1 order by e2)";
        AggregateSymbol as = getFactory().newAggregateSymbol(Reserved.ARRAY_AGG, false, getFactory().newConstant(1));
        as.setOrderBy(getFactory().newOrderBy(Arrays.asList(getFactory().newElementSymbol("e2"))));
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(as)));
        helpTest(sql, "SELECT ARRAY_AGG(1 ORDER BY e2)", query);
    }

    @Test
    public void testArrayAggWithIndexing() throws Exception {
        String sql = "SELECT (array_agg(1))[1]";
        AggregateSymbol as = getFactory().newAggregateSymbol(Reserved.ARRAY_AGG, false, getFactory().newConstant(1));
        Expression expr = getFactory().wrapExpression(getFactory().newFunction("array_get", new Expression[] {as, getFactory().newConstant(1)}));
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(expr)));
        helpTest(sql, "SELECT array_get(ARRAY_AGG(1), 1)", query);
    }

    @Test
    public void testNestedTable() throws Exception {
        String sql = "SELECT * from TABLE(exec foo()) as x";
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(getFactory().newMultipleElementSymbol())));
        StoredProcedure sp = getFactory().newStoredProcedure();
        sp.setProcedureName("foo");
        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("x", sp);
        sfc.setTable(true);
        query.setFrom(getFactory().newFrom(Arrays.asList(sfc)));
        helpTest(sql, "SELECT * FROM TABLE(EXEC foo()) AS x", query);
    }

    @Test
    public void testTextTable() throws Exception {
        String sql = "SELECT * from texttable(file columns x string WIDTH 1, y date width 10 skip 10) as x";
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(getFactory().newMultipleElementSymbol())));
        TextTable tt = getFactory().newTextTable();
        tt.setFile(getFactory().newElementSymbol("file"));
        List<TextColumn> columns = new ArrayList<TextColumn>();
        columns.add(getFactory().newTextColumn("x", "string", 1));
        columns.add(getFactory().newTextColumn("y", "date", 10));
        tt.setColumns(columns);
        tt.setSkip(10);
        tt.setName("x");
        query.setFrom(getFactory().newFrom(Arrays.asList(tt)));
        helpTest(sql, "SELECT * FROM TEXTTABLE(file COLUMNS x string WIDTH 1, y date WIDTH 10 SKIP 10) AS x", query);

        sql = "SELECT * from texttable(file columns x string, y date delimiter ',' escape '\"' header skip 10) as x";
        tt.setDelimiter(',');
        tt.setQuote('"');
        tt.setEscape(true);
        tt.setHeader(1);
        for (TextColumn textColumn : columns) {
            textColumn.setWidth(null);
        }
        helpTest(sql,
                 "SELECT * FROM TEXTTABLE(file COLUMNS x string, y date DELIMITER ',' ESCAPE '\"' HEADER SKIP 10) AS x",
                 query);
    }

    @Test
    public void testXMLTable() throws Exception {
        String sql = "SELECT * from xmltable(xmlnamespaces(no default), '/' columns x for ordinality, y date default {d'2000-01-01'} path '@date') as x";
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(getFactory().newMultipleElementSymbol())));
        XMLTable xt = getFactory().newXMLTable();
        xt.setName("x");
        xt.setNamespaces(getFactory().newXMLNamespaces(Arrays.asList(new NamespaceItem())));
        xt.setXquery("/");
        List<XMLColumn> columns = new ArrayList<XMLColumn>();
        columns.add(getFactory().newXMLColumn("x", true));
        XMLColumn c2 = getFactory().newXMLColumn("y", false);
        c2.setType("date");
        c2.setPath("@date");
        c2.setDefaultExpression(getFactory().newConstant(Date.valueOf("2000-01-01")));
        columns.add(c2);
        xt.setColumns(columns);
        query.setFrom(getFactory().newFrom(Arrays.asList(xt)));
        helpTest(sql,
                 "SELECT * FROM XMLTABLE(XMLNAMESPACES(NO DEFAULT), '/' COLUMNS x FOR ORDINALITY, y date DEFAULT {d'2000-01-01'} PATH '@date') AS x",
                 query);
    }

    @Test
    public void testXmlSerialize() throws Exception {
        XMLSerialize f = getFactory().newXMLSerialize();
        f.setDocument(true);
        f.setExpression(getFactory().newElementSymbol("x"));
        f.setTypeString("CLOB");
        helpTestExpression("xmlserialize(document x as CLOB)", "XMLSERIALIZE(DOCUMENT x AS CLOB)", f);
    }

    @Test
    public void testXmlQuery() throws Exception {
        XMLQuery f = getFactory().newXMLQuery();
        f.setXquery("/x");
        f.setEmptyOnEmpty(false);
        DerivedColumn derivedColumn = getFactory().newDerivedColumn(null, getFactory().newElementSymbol("foo"));
        derivedColumn.setPropagateName(false);
        f.setPassing(Arrays.asList(derivedColumn));
        helpTestExpression("xmlquery('/x' passing foo null on empty)", "XMLQUERY('/x' PASSING foo NULL ON EMPTY)", f);
    }

    @Test
    public void testXmlParse() throws Exception {
        XMLParse f = getFactory().newXMLParse();
        f.setDocument(true);
        f.setExpression(getFactory().newElementSymbol("x"));
        f.setWellFormed(true);
        helpTestExpression("xmlparse(document x wellformed)", "XMLPARSE(DOCUMENT x WELLFORMED)", f);
    }

    @Test
    public void testXmlSerialize1() throws Exception {
        XMLSerialize f = getFactory().newXMLSerialize();
        f.setExpression(getFactory().newElementSymbol("x"));
        f.setTypeString("CLOB");
        helpTestExpression("xmlserialize(x as CLOB)", "XMLSERIALIZE(x AS CLOB)", f);
    }

    @Test
    public void testExpressionCriteria() throws Exception {
        SearchedCaseExpression sce = getFactory().newSearchedCaseExpression(Arrays.asList(getFactory().newExpressionCriteria(getFactory().newElementSymbol("x"))),
                                                               Arrays.asList(getFactory().newElementSymbol("y")));
        helpTestExpression("case when x then y end", "CASE WHEN x THEN y END", sce);
    }

    @Test
    public void testExpressionCriteria1() throws Exception {
        SearchedCaseExpression sce = getFactory().newSearchedCaseExpression(Arrays.asList(getFactory().newNotCriteria(getFactory().newExpressionCriteria(getFactory().newElementSymbol("x")))),
                                                               Arrays.asList(getFactory().newElementSymbol("y")));
        helpTestExpression("case when not x then y end", "CASE WHEN NOT (x) THEN y END", sce);
    }

    @Test
    public void testWithClause() throws Exception {
        Query query = getOrderByQuery(null);
        query.setWith(Arrays.asList(getFactory().newWithQueryCommand(getFactory().newGroupSymbol("x"), getOrderByQuery(null))));
        helpTest("WITH x AS (SELECT a FROM db.g WHERE b = aString) SELECT a FROM db.g WHERE b = aString",
                 "WITH x AS (SELECT a FROM db.g WHERE b = aString) SELECT a FROM db.g WHERE b = aString",
                 query);
    }

    @Test
    public void testExplicitTable() throws Exception {
        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect();
        query.setSelect(select);
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom(Arrays.asList(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("X"))));
        query.setFrom(from);
        helpTest("TABLE X", "SELECT * FROM X", query);
    }

    @Test
    public void testArrayTable() throws Exception {
        String sql = "SELECT * from arraytable(null columns x string, y date) as x";
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(getFactory().newMultipleElementSymbol())));
        ArrayTable tt = getFactory().newArrayTable();
        tt.setArrayValue(getFactory().newConstant(null, DataTypeManagerService.DefaultDataTypes.NULL.getTypeClass()));
        List<ProjectedColumn> columns = new ArrayList<ProjectedColumn>();
        columns.add(getFactory().newProjectedColumn("x", "string"));
        columns.add(getFactory().newProjectedColumn("y", "date"));
        tt.setColumns(columns);
        tt.setName("x");
        query.setFrom(getFactory().newFrom(Arrays.asList(tt)));
        helpTest(sql, "SELECT * FROM ARRAYTABLE(null COLUMNS x string, y date) AS x", query);
    }

    @Test
    public void testPositionalReference() throws Exception {
        String sql = "select $1";
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(getFactory().wrapExpression(getFactory().newReference(0)))));
        helpTest(sql, "SELECT ?", query);
    }

    @Test
    public void testNonReserved() throws Exception {
        String sql = "select count";
        Query query = getFactory().newQuery();
        query.setSelect(getFactory().newSelect(Arrays.asList(getFactory().newElementSymbol("count"))));
        helpTest(sql, "SELECT count", query);
    }

    @Test
    public void testAggFilter() throws Exception {
        String sql = "select count(*) filter (where x = 1) from g";
        Query query = getFactory().newQuery();
        AggregateSymbol aggregateSymbol = getFactory().newAggregateSymbol(AggregateSymbol.Type.COUNT.name(), false, null);
        aggregateSymbol.setCondition(getFactory().newCompareCriteria(getFactory().newElementSymbol("x"), Operator.EQ, getFactory().newConstant(1)));
        query.setSelect(getFactory().newSelect(Arrays.asList(aggregateSymbol)));
        query.setFrom(getFactory().newFrom(Arrays.asList(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("g")))));
        helpTest(sql, "SELECT COUNT(*) FILTER(WHERE x = 1) FROM g", query);
    }

    @Test
    public void testWindowFunction() throws Exception {
        String sql = "select row_number() over (partition by x order by y) from g";
        Query query = getFactory().newQuery();
        WindowFunction wf = getFactory().newWindowFunction("win_row_number");
        wf.setFunction(getFactory().newAggregateSymbol("ROW_NUMBER", false, null));
        WindowSpecification ws = getFactory().newWindowSpecification();
        ws.setPartition(new ArrayList<Expression>(Arrays.asList(getFactory().newElementSymbol("x"))));
        ws.setOrderBy(getFactory().newOrderBy(Arrays.asList(getFactory().newElementSymbol("y"))));
        wf.setWindowSpecification(ws);
        query.setSelect(getFactory().newSelect(Arrays.asList(wf)));
        query.setFrom(getFactory().newFrom(Arrays.asList(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("g")))));
        helpTest(sql, "SELECT ROW_NUMBER() OVER (PARTITION BY x ORDER BY y) FROM g", query);
    }

    @Test
    public void testSubString() {
        String sql = "select substring(RTRIM(MED.BATDAT), 4, 4) from FCC.MEDMAS as MED";

        UnaryFromClause ufc = getFactory().newUnaryFromClause("MED", "FCC.MEDMAS");
        List<FromClause> clauses = new ArrayList<FromClause>();
        clauses.add(ufc);
        From from = getFactory().newFrom(clauses);

        ElementSymbol e = getFactory().newElementSymbol("MED.BATDAT");
        Expression[] f2args = new Expression[] { e };
        Function f2 = getFactory().newFunction("RTRIM", f2args);
        Constant c1 = getFactory().newConstant(4);
        Constant c2 = getFactory().newConstant(4);
        Expression[] f1args = new Expression[] { f2, c1, c2 };
        Function f1 = getFactory().newFunction("substring", f1args);

        ExpressionSymbol es = getFactory().newNode(ASTNodes.EXPRESSION_SYMBOL);
        
        /*
         * Annoying that I have to be so explicit but any other way is far more
         * loquacious and not really worth it.
         */
        String name;
        if (parser.getTeiidParser().getVersion().isSevenServer())
            name = "expr";
        else
            name = "expr1";

        es.setName(name);
        es.setExpression(f1);

        Select select = getFactory().newSelect(Arrays.asList(es));
        Query query = getFactory().newQuery(select, from);
        
        helpTest(sql, "SELECT substring(RTRIM(MED.BATDAT), 4, 4) FROM FCC.MEDMAS AS MED", query);
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

    // #####################  COMMENTS ###############################

    private void printIndexes(String text) {
        int i = 0;
        while(i < text.length()) {
            StringBuffer indexes = new StringBuffer();
            StringBuffer textBuffer= new StringBuffer();

            int lineLength = 20;
            for (int j = i; j <= (i + lineLength) && j < text.length(); ++j) {
                char c = text.charAt(j);
                indexes.append(j).append("\t");
                textBuffer.append("'").append(c).append("'").append("\t");
            }

            textBuffer.append(NEW_LINE);

            System.out.println(indexes);
            System.out.println(textBuffer);

            i += lineLength + 1;
            
        }
    }

    /** SELECT * FROM g1 cross join g2 */
    @Test
    public void testCrossJoinWithCommentsWithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM g1 cross join g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM g1 CROSS JOIN g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM (g1 cross join g2), g3 */
    @Test
    public void testFromClausesWithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM (g1 cross join g2), g3 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM g1 CROSS JOIN g2, g3 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM (g1 cross join g2) cross join g3 */
    @Test
    public void testMultiCrossJoinWithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM (g1 cross join g2) cross join g3 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM (g1 CROSS JOIN g2) CROSS JOIN g3 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM (g1 cross join g2) cross join (g3 cross join g4) */
    @Test
    public void testMultiCrossJoin2WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM (g1 cross join g2) cross join (g3 cross join g4) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM (g1 CROSS JOIN g2) CROSS JOIN (g3 CROSS JOIN g4) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM g1 cross join (g2 cross join g3) */
    @Test
    public void testMultiCrossJoin3WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM g1 cross join (g2 cross join g3) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM g1 CROSS JOIN (g2 CROSS JOIN g3) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM g1 cross join (g2 cross join g3), g4 */
    @Test
    public void testMixedJoinWithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM g1 cross join (g2 cross join g3), g4 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM g1 CROSS JOIN (g2 CROSS JOIN g3), g4 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM g1 cross join (g2 cross join g3), g4, g5 cross join g6 */
    @Test
    public void testMixedJoin2WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM g1 cross join (g2 cross join g3), g4, g5 cross join g6 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM g1 CROSS JOIN (g2 CROSS JOIN g3), g4, g5 CROSS JOIN g6 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM g1, g2 inner join g3 on g2.a=g3.a */
    @Test
    public void testMixedJoin3WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM g1, g2 inner join g3 on g2.a=g3.a /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM g1, g2 INNER JOIN g3 ON g2.a = g3.a /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Select myG.a myA, myH.b from g myG right outer join h myH on myG.x=myH.x */
    @Test
    public void testRightOuterJoinWithAliasesWithComments() {
        String sql = "/* Leading Comment */ Select myG.a myA, myH.b from g myG right outer join h myH on myG.x=myH.x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT myG.a AS myA, myH.b FROM g AS myG RIGHT OUTER JOIN h AS myH ON myG.x = myH.x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Select myG.x myX, myH.y from g myG right join h myH on myG.x=myH.x */
    @Test
    public void testRightJoinWithAliasesWithComments() {
        String sql = "/* Leading Comment */ Select myG.a myA, myH.b from g myG right join h myH on myG.x=myH.x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT myG.a AS myA, myH.b FROM g AS myG RIGHT OUTER JOIN h AS myH ON myG.x = myH.x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Select myG.a myA, myH.b from g myG left outer join h myH on myG.x=myH.x */
    @Test
    public void testLeftOuterJoinWithAliasesWithComments() {
        String sql = "/* Leading Comment */ Select myG.a myA, myH.b from g myG left outer join h myH on myG.x=myH.x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT myG.a AS myA, myH.b FROM g AS myG LEFT OUTER JOIN h AS myH ON myG.x = myH.x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Select myG.a myA, myH.b from g myG left join h myH on myG.x=myH.x */
    @Test
    public void testLeftJoinWithAliasesWithComments() {
        String sql = "/* Leading Comment */ Select myG.a myA, myH.b from g myG left join h myH on myG.x=myH.x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT myG.a AS myA, myH.b FROM g AS myG LEFT OUTER JOIN h AS myH ON myG.x = myH.x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Select myG.a myA, myH.b from g myG full outer join h myH on myG.x=myH.x */
    @Test
    public void testFullOuterJoinWithAliasesWithComments() {
        String sql = "/* Leading Comment */ Select myG.a myA, myH.b from g myG full outer join h myH on myG.x=myH.x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT myG.a AS myA, myH.b FROM g AS myG FULL OUTER JOIN h AS myH ON myG.x = myH.x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Select g.a, h.b from g full join h on g.x=h.x */
    @Test
    public void testFullJoinWithComments() {
        String sql = "/* Leading Comment */ Select g.a, h.b from g full join h on g.x=h.x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT g.a, h.b FROM g FULL OUTER JOIN h ON g.x = h.x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    // ======================= Convert ==============================================

    /** SELECT CONVERT(a, string) FROM g */
    @Test
    public void testConversionFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT CONVERT(a, string) FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT CONVERT(a, string) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT CONVERT(CONVERT(a, timestamp), string) FROM g */
    @Test
    public void testConversionFunction2WithComments() {
        String sql = "/* Leading Comment */ SELECT CONVERT(CONVERT(a, timestamp), string) FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT CONVERT(CONVERT(a, timestamp), string) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    // ======================= Functions ==============================================

    /** SELECT 5 + length(concat(a, 'x')) FROM g */
    @Test
    public void testMultiFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT 5 + length(concat(a, 'x')) FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (5 + length(concat(a, 'x'))) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT REPLACE(a, 'x', 'y') AS y FROM g */
    @Test
    public void testAliasedFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT REPLACE(a, 'x', 'y') AS y FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT REPLACE(a, 'x', 'y') AS y FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT cast(a as string) FROM g */
    @Test
    public void testCastFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT cast(a as string) FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT cast(a AS string) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT cast(cast(a as timestamp) as string) FROM g */
    @Test
    public void testMultiCastFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT cast(cast(a as timestamp) as string) FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT cast(cast(a AS timestamp) AS string) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT left(fullname, 3) as x FROM sys.groups */
    @Test
    public void testLeftFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT left(fullname, 3) as x FROM sys.groups /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT left(fullname, 3) AS x FROM sys.groups /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT right(fullname, 3) as x FROM sys.groups */
    @Test
    public void testRightFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT right(fullname, 3) as x FROM sys.groups /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT right(fullname, 3) AS x FROM sys.groups /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT char('x') AS x FROM sys.groups */
    @Test
    public void testCharFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT char('x') AS x FROM sys.groups /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT char('x') AS x FROM sys.groups /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT insert('x', 1, 'a') as x FROM sys.groups */
    @Test
    public void testInsertFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT insert('x', 1, 'a') AS x FROM sys.groups /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT insert('x', 1, 'a') AS x FROM sys.groups /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testInsertIntoSelectWithComments() {
        String sql = "insert into tempA SELECT 1 /* Trailing Comment */";
        String expectedSql = "INSERT INTO tempA SELECT 1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT translate('x', 'x', 'y') FROM sys.groups */
    @Test
    public void testTranslateFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT translate('x', 'x', 'y') FROM sys.groups /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT translate('x', 'x', 'y') FROM sys.groups /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampadd(SQL_TSI_FRAC_SECOND, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionFracSecondWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_FRAC_SECOND, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_FRAC_SECOND, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampadd(SQL_TSI_SECOND, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionSecondWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_SECOND, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_SECOND, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampadd(SQL_TSI_MINUTE, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionMinuteWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_MINUTE, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_MINUTE, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampadd(SQL_TSI_HOUR, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionHourWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_HOUR, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_HOUR, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampadd(SQL_TSI_DAY, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionDayWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_DAY, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_DAY, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampadd(SQL_TSI_WEEK, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionWeekWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_WEEK, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_WEEK, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampadd(SQL_TSI_QUARTER, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionQuarterWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_QUARTER, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_QUARTER, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampadd(SQL_TSI_YEAR, 10, '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampaddFunctionYearWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_YEAR, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampadd(SQL_TSI_YEAR, 10, '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT timestampdiff(SQL_TSI_FRAC_SECOND, '2003-05-01 10:20:10', '2003-05-01 10:20:30') as x FROM my.group1 */
    @Test
    public void testTimestampdiffFunctionFracSecondWithComments() {
        String sql = "/* Leading Comment */ SELECT timestampdiff(SQL_TSI_FRAC_SECOND, '2003-05-01 10:20:10', '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT timestampdiff(SQL_TSI_FRAC_SECOND, '2003-05-01 10:20:10', '2003-05-01 10:20:30') AS x FROM my.group1 /* Trailing Comment */";
        helpTest(sql,
                 expectedSql,
                 null);
    }

    /** SELECT 5 + 2 + 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence1WithComments() {
        String sql = "/* Leading Comment */ SELECT 5 + 2 + 3 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ((5 + 2) + 3) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 5 + 2 - 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence2WithComments() {
        String sql = "/* Leading Comment */ SELECT 5 + 2 - 3 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ((5 + 2) - 3) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 5 + 2 * 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence3WithComments() {
        String sql = "/* Leading Comment */ SELECT 5 + 2 * 3 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (5 + (2 * 3)) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 5 * 2 + 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence4WithComments() {
        String sql = "/* Leading Comment */ SELECT 5 * 2 + 3 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ((5 * 2) + 3) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 5 * 2 * 3 FROM g */
    @Test
    public void testArithmeticOperatorPrecedence5WithComments() {
        String sql = "/* Leading Comment */ SELECT 5 * 2 * 3 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ((5 * 2) * 3) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 1 + 2 * 3 + 4 * 5 FROM g */
    @Test
    public void testArithmeticOperatorPrecedenceMixed1WithComments() {
        String sql = "/* Leading Comment */ SELECT 1 + 2 * 3 + 4 * 5 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ((1 + (2 * 3)) + (4 * 5)) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 1 * 2 + 3 * 4 + 5 FROM g */
    @Test
    public void testArithmeticOperatorPrecedenceMixed2WithComments() {
        String sql = "/* Leading Comment */ SELECT 1 * 2 + 3 * 4 + 5 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (((1 * 2) + (3 * 4)) + 5) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 5 - 4 - 3 - 2 FROM g --> SELECT ((5 - 4) - 3) - 2 FROM g */
    @Test
    public void testLeftAssociativeExpressions1WithComments() {
        String sql = "/* Leading Comment */ SELECT 5 - 4 - 3 - 2 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (((5 - 4) - 3) - 2) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 5 / 4 / 3 / 2 FROM g --> SELECT ((5 / 4) / 3) / 2 FROM g */
    @Test
    public void testLeftAssociativeExpressions2WithComments() {
        String sql = "/* Leading Comment */ SELECT 5 / 4 / 3 / 2 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (((5 / 4) / 3) / 2) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 'a' || 'b' || 'c' FROM g */
    @Test
    public void testConcatOperator1WithComments() {
        String sql = "/* Leading Comment */ SELECT 'a' || 'b' || 'c' FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (('a' || 'b') || 'c') FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 2 + 3 || 5 + 1 * 2 FROM g */
    @Test
    public void testMixedOperators1WithComments() {
        String sql = "/* Leading Comment */ SELECT 2 + 3 || 5 + 1 * 2 FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ((2 + 3) || (5 + (1 * 2))) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    // ======================= Group By ==============================================

    /** SELECT a FROM m.g GROUP BY b, c */
    @Test
    public void testGroupByWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM m.g GROUP BY b, c /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM m.g GROUP BY b, c /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM m.g GROUP BY b, c HAVING b=5*/
    @Test
    public void testGroupByHavingWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM m.g GROUP BY b, c HAVING b=5 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM m.g GROUP BY b, c HAVING b = 5 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT COUNT(a) AS c FROM m.g */
    @Test
    public void testAggregateFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT COUNT(a) AS c FROM m.g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT COUNT(a) AS c FROM m.g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT (COUNT(a)) AS c FROM m.g - this kind of query is generated by ODBC sometimes */
    @Test
    public void testAggregateFunctionWithParensWithComments() {
        String sql = "/* Leading Comment */ SELECT (COUNT(a)) AS c FROM m.g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT COUNT(a) AS c FROM m.g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM m.g GROUP BY a HAVING COUNT(b) > 0*/
    @Test
    public void testHavingFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM m.g GROUP BY a HAVING COUNT(b) > 0 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM m.g GROUP BY a HAVING COUNT(b) > 0 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM m.g GROUP BY a, b HAVING COUNT(b) > 0 AND b+5 > 0 */
    @Test
    public void testCompoundHavingWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM m.g GROUP BY a, b HAVING COUNT(b) > 0 AND b+5 > 0 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM m.g GROUP BY a, b HAVING (COUNT(b) > 0) AND ((b + 5) > 0) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testFunctionOfAggregatesWithComments() {
        String sql = "/* Leading Comment */ SELECT COUNT(a) * SUM(a) AS c FROM m.g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (COUNT(a) * SUM(a)) AS c FROM m.g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);

    }

    /** SELECT 5-null, a.g1.c1 FROM a.g1 */
    @Test
    public void testArithmeticNullFunctionWithComments() {
        String sql = "/* Leading Comment */ SELECT 5-null, a.g1.c1 FROM a.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (5 - null), a.g1.c1 FROM a.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 'abc' FROM a.g1 */
    @Test
    public void testStringLiteralWithComments() {
        String sql = "/* Leading Comment */ SELECT 'abc' FROM a.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT 'abc' FROM a.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 'O''Leary' FROM a.g1 */
    @Test
    public void testStringLiteralEscapedTickWithComments() {
        String sql = "/* Leading Comment */ SELECT 'O''Leary' FROM a.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT 'O''Leary' FROM a.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT '''abc''' FROM a.g1 */
    @Test
    public void testStringLiteralEscapedTick2WithComments() {
        String sql = "/* Leading Comment */ SELECT '''abc''' FROM a.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT '''abc''' FROM a.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 'a''b''c' FROM a.g1 */
    @Test
    public void testStringLiteralEscapedTick3WithComments() {
        String sql = "/* Leading Comment */ SELECT 'a''b''c' FROM a.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT 'a''b''c' FROM a.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT " "" " FROM a.g1 */
    @Test
    public void testStringLiteralEscapedTick4WithComments() {
        String sql = "/* Leading Comment */ SELECT \" \"\" \" FROM a.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT \" \"\" \" FROM a.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 123456789012 FROM a.g1 */
    @Test
    public void testLongLiteralWithComments() {
        String sql = "/* Leading Comment */ SELECT 123456789012 FROM a.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT 123456789012 FROM a.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT 1000000000000000000000000 FROM a.g1 */
    @Test
    public void testBigIntegerLiteralWithComments() {
        String sql = "/* Leading Comment */ SELECT 1000000000000000000000000 FROM a.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT 1000000000000000000000000 FROM a.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT {d'2002-10-02'} FROM m.g1 */
    @Test
    public void testDateLiteral1WithComments() {
        String sql = "/* Leading Comment */ SELECT {d'2002-10-02'} FROM m.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT {d'2002-10-02'} FROM m.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT {d'2002-9-1'} FROM m.g1 */
    @Test
    public void testDateLiteral2WithComments() {
        String sql = "/* Leading Comment */ SELECT {d'2002-09-01'} FROM m.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT {d'2002-09-01'} FROM m.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT {t '11:10:00' } FROM m.g1 */
    @Test
    public void testTimeLiteral1WithComments() {
        String sql = "/* Leading Comment */ SELECT {t '11:10:00' } FROM m.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT {t'11:10:00'} FROM m.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT {t '5:10:00'} FROM m.g1 */
    @Test
    public void testTimeLiteral2WithComments() {
        String sql = "/* Leading Comment */ SELECT {t '05:10:00'} FROM m.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT {t'05:10:00'} FROM m.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT {ts'2002-10-02 19:00:02.50'} FROM m.g1 */
    @Test
    public void testTimestampLiteralWithComments() {
        String sql = "/* Leading Comment */ SELECT {ts'2002-10-02 09:00:02.50'} FROM m.g1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT {ts'2002-10-02 09:00:02.5'} FROM m.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT {b'true'} FROM m.g1 */
    @Test
    public void testBooleanLiteralTrueWithComments() {
        String sql = "/* Leading Comment */ SELECT {b'true'} /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT TRUE /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT TRUE FROM m.g1 */
    @Test
    public void testBooleanLiteralTrue2WithComments() {
        String sql = "/* Leading Comment */ SELECT TRUE /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT TRUE /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT {b'false'} FROM m.g1 */
    @Test
    public void testBooleanLiteralFalseWithComments() {
        String sql = "/* Leading Comment */ SELECT {b'false'} /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT FALSE /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT FALSE FROM m.g1 */
    @Test
    public void testBooleanLiteralFalse2WithComments() {
        String sql = "/* Leading Comment */ SELECT {b'false'} /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT FALSE /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testBooleanLiteralUnknownWithComments() {
        String sql = "/* Leading Comment */ SELECT {b'unknown'} /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT UNKNOWN /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testBooleanLiteralUnknown2WithComments() {
        String sql = "/* Leading Comment */ SELECT UNKNOWN /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT UNKNOWN /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT DISTINCT a FROM g */
    @Test
    public void testSelectDistinctWithComments() {
        String sql = "/* Leading Comment */ SELECT DISTINCT a /* Pre-From Comment */ FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT DISTINCT a /* Pre-From Comment */ FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT ALL a FROM g */
    @Test
    public void testSelectAllWithComments() {
        String sql = "/* Leading Comment */ SELECT ALL a /* Pre-From Comment */ FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    //=========================Aliasing==============================================

    /** SELECT a AS myA, b FROM g */
    @Test
    public void testAliasInSelectWithComments() {
        String sql = "/* Leading Comment */ SELECT a AS myA, b FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a AS myA, b FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a myA, b FROM g, h */
    @Test
    public void testAliasInSelect2WithComments() {
        String sql = "/* Leading Comment */ SELECT a myA, b FROM g, h /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a AS myA, b FROM g, h /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT myG.a FROM g AS myG */
    @Test
    public void testAliasInFromWithComments() {
        String sql = "/* Leading Comment */ SELECT myG.a FROM g AS myG /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT myG.a FROM g AS myG /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT myG.*, myH.b FROM g AS myG, h AS myH */
    @Test
    public void testAliasesInFromWithComments() {
        String sql = "/* Leading Comment */ SELECT myG.*, myH.b FROM g AS myG, h AS myH /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT myG.*, myH.b FROM g AS myG, h AS myH /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT myG.a, myH.b FROM g myG, h myH */
    @Test
    public void testHiddenAliasesInFromWithComments() {
        String sql = "/* Leading Comment */ SELECT myG.*, myH.b FROM g myG, h myH /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT myG.*, myH.b FROM g AS myG, h AS myH /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    // ======================= Misc ==============================================

    /** Select a From db.g Where a IS NULL */
    @Test
    public void testIsNullCriteria1WithComments() {
        String sql = "/* Leading Comment */ Select a /* Pre-From Comment */ From db.g Where a IS NULL /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a IS NULL /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Select a From db.g Where a IS NOT NULL */
    @Test
    public void testIsNullCriteria2WithComments() {
        String sql = "/* Leading Comment */ Select a /* Pre-From Comment */ From db.g Where a IS NOT NULL /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a IS NOT NULL /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Select a From db.g Where Not a IS NULL */
    @Test
    public void testNotIsNullCriteriaWithComments() {
        String sql = "/* Leading Comment */ Select a /* Pre-From Comment */ From db.g Where Not a IS NULL /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE NOT (a IS NULL) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a <> "value" */
    @Test
    public void testStringNotEqualDoubleTicksWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a <> \"value\" /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a <> \"value\" /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a != "value" */
    @Test
    public void testNotEquals2WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a != 'value' /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a <> 'value' /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db."g" where a = 5 */
    @Test
    public void testPartlyQuotedGroupWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.\"g\" where a = 5 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a = 5 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from "db"."g" where a = 5 */
    @Test
    public void testFullyQuotedGroupWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from \"db\".\"g\" where a = 5 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a = 5 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT "db".g.a from db.g */
    @Test
    public void testPartlyQuotedElement1WithComments() {
        String sql = "/* Leading Comment */ SELECT \"db\".g.a from db.g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT db.g.a FROM db.g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT "db"."g".a from db.g */
    @Test
    public void testPartlyQuotedElement2WithComments() {
        String sql = "/* Leading Comment */ SELECT \"db\".\"g\".a from db.g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT db.g.a FROM db.g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT "db"."g"."a" from db.g */
    @Test
    public void testPartlyQuotedElement3WithComments() {
        String sql = "/* Leading Comment */ SELECT \"db\".\"g\".\"a\" from db.g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT db.g.a FROM db.g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT ""g"".""a" from db.g */
    @Test
    public void testStringLiteralLikeQuotedElementWithComments() {
        String sql = "/* Leading Comment */ SELECT \"g\"\".\"\"a\" from g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT \"g\"\"\".\"\"\"a\" FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT g.x AS "select" FROM g */
    @Test
    public void testQuotedAliasWithComments() {
        String sql = "/* Leading Comment */ SELECT g.x AS \"select\" FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT g.x AS \"select\" FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT g.x AS year FROM g */
    @Test
    public void testQuotedAlias2WithComments() {
        String sql = "/* Leading Comment */ SELECT g.x AS \"year\" FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT g.x AS \"year\" FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testQuotedAlias3WithComments() {
        String sql = "/* Leading Comment */ SELECT g.x AS \"some year\" FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT g.x AS \"some year\" FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT g."select" FROM g */
    @Test
    public void testReservedWordElement1WithComments() {
        String sql = "/* Leading Comment */ SELECT g.\"select\" FROM g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT g.\"select\" FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet.x FROM newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet */
    @Test
    public void testReservedWordElement2WithComments() {
        String sql = "/* Leading Comment */ SELECT newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet.x FROM newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT newModel5.ResultSetDocument.MappingClasses.\"from\".\"from\".Query1InputSet.x FROM newModel5.ResultSetDocument.MappingClasses.\"from\".\"from\".Query1InputSet /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet  */
    @Test
    public void testReservedWordGroup1WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM newModel5.ResultSetDocument.MappingClasses.from.from.Query1InputSet /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM newModel5.ResultSetDocument.MappingClasses.\"from\".\"from\".Query1InputSet /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM newModel5."ResultSetDocument.MappingClasses.from.from.Query1InputSet"  */
    @Test
    public void testReservedWordGroup2WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM newModel5.\"ResultSetDocument.MappingClasses.from.from.Query1InputSet\" /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM newModel5.ResultSetDocument.MappingClasses.\"from\".\"from\".Query1InputSet /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT * FROM model.doc WHERE ab.cd.@ef = 'abc' */
    @Test
    public void testXMLCriteriaWithAttributeWithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM model.doc WHERE ab.cd.@ef = 'abc' /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM model.doc WHERE ab.cd.@ef = 'abc' /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a <> 'value' */
    @Test
    public void testStringNotEqualWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a <> 'value' /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a <> 'value' /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a BETWEEN 1000 AND 2000 */
    @Test
    public void testBetween1WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a BETWEEN 1000 AND 2000 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a BETWEEN 1000 AND 2000 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a NOT BETWEEN 1000 AND 2000 */
    @Test
    public void testBetween2WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a NOT BETWEEN 1000 AND 2000 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a NOT BETWEEN 1000 AND 2000 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a < 1000 */
    @Test
    public void testCompareLTWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a < 1000 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a < 1000 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a > 1000 */
    @Test
    public void testCompareGTWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a > 1000 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a > 1000 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a <= 1000 */
    @Test
    public void testCompareLEWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a <= 1000 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a <= 1000 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where a >= 1000 */
    @Test
    public void testCompareGEWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where a >= 1000 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE a >= 1000 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM db.g WHERE b IN (1000,5000)*/
    @Test
    public void testSetCriteria0WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b IN (1000,5000) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b IN (1000, 5000) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM db.g WHERE b NOT IN (1000,5000)*/
    @Test
    public void testSetCriteria1WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b NOT IN (1000,5000) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b NOT IN (1000, 5000) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    // ================================== order by ==================================

    /** SELECT a FROM db.g WHERE b = aString order by c*/
    @Test
    public void testOrderByWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM db.g WHERE b = aString order by c desc*/
    @Test
    public void testOrderByDescWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c desc /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c DESC /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM db.g WHERE b = aString order by c,d*/
    @Test
    public void testOrderBysWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c,d /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c, d /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM db.g WHERE b = aString order by c desc,d desc*/
    @Test
    public void testOrderBysDescWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c desc,d desc /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c DESC, d DESC /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM db.g WHERE b = aString order by c desc,d*/
    @Test
    public void testMixedOrderBysWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c desc,d /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c DESC, d /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testOrderByNullOrderingWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c NULLS FIRST,d desc nulls last /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString ORDER BY c NULLS FIRST, d DESC NULLS LAST /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    // ================================== match ====================================

    /** SELECT a FROM db.g WHERE b LIKE 'aString'*/
    @Test
    public void testLike0WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b LIKE 'aString' /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b LIKE 'aString' /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM db.g WHERE b NOT LIKE 'aString'*/
    @Test
    public void testLike1WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b NOT LIKE 'aString' /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b NOT LIKE 'aString' /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where b like '#String' escape '#'*/
    @Test
    public void testLikeWithEscapeWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where b like '#String' escape '#' /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b LIKE '#String' ESCAPE '#' /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT "date"."time" from db.g */
    @Test
    public void testReservedWordsInElementWithComments() {
        String sql = "/* Leading Comment */ SELECT \"date\".\"time\" from db.g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT \"date\".\"time\" FROM db.g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);

    }

    /** SELECT a */
    @Test
    public void testNoFromClauseWithComments() {
        String sql = "/* Leading Comment */ SELECT a, 5 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a, 5 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a or b from g */
    @Test
    public void testOrInSelectWithComments() {
        String sql = "/* Leading Comment */ select a or b /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (a) OR (b) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a FROM g WHERE a LIKE x*/
    @Test
    public void testLikeWOConstantWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM g WHERE a LIKE x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM g WHERE a LIKE x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where b LIKE ? */
    @Test
    public void testParameter1WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ from db.g where b LIKE ? /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b LIKE ? /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a from db.g where b LIKE ? */
    @Test
    public void testParameter2WithComments() {
        String sql = "/* Leading Comment */ SELECT ? from db.g where b LIKE ? /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ? FROM db.g WHERE b LIKE ? /* Trailing Comment */";
        printIndexes(sql);
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a, b FROM (SELECT c FROM m.g) AS y */
    @Test
    public void testSubquery1WithComments() {
        String sql = "/* Leading Comment */ SELECT a, b FROM (SELECT c /* Pre-From Comment */ FROM m.g) AS y /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a, b FROM (SELECT c /* Pre-From Comment */ FROM m.g) AS y /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a, b FROM ((SELECT c FROM m.g)) AS y */
    @Test
    public void testSubquery1aWithComments() {
        String sql = "/* Leading Comment */ SELECT a, b FROM ((SELECT c /* Pre-From Comment */ FROM m.g)) AS y /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a, b FROM (SELECT c /* Pre-From Comment */ FROM m.g) AS y /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** SELECT a, b FROM m.g1 JOIN (SELECT c FROM m.g2) AS y ON m.g1.a = y.c */
    @Test
    public void testSubquery2WithComments() {
        String sql = "/* Leading Comment */ SELECT a, b FROM m.g1 JOIN (SELECT c /* Pre-From Comment */ FROM m.g2) AS y ON m.g1.a = y.c /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a, b FROM m.g1 INNER JOIN (SELECT c /* Pre-From Comment */ FROM m.g2) AS y ON m.g1.a = y.c /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** INSERT INTO m.g (a) VALUES (?) */
    @Test
    public void testInsertWithReferenceWithComments() {
        String sql = "INSERT INTO m.g (a) VALUES (?) /* Trailing Comment */";
        String expectedSql = "INSERT INTO m.g (a) VALUES (?) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testStoredQueryWithNoParameterWithComments() {
        String sql = "exec proc1() /* Trailing Comment */";
        String expectedSql = "EXEC proc1() /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testStoredQueryWithNoParameter2WithComments() {
        String sql = "/* Leading Comment */ SELECT X.A FROM (exec proc1()) AS X /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT X.A FROM (EXEC proc1()) AS X /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testStoredQueryWithComments() {
        String sql = "Exec proc1('param1') /* Trailing Comment */";
        String expectedSql = "EXEC proc1('param1') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testStoredQuery2WithComments() {
        String sql = "/* Leading Comment */ SELECT X.A FROM (exec proc1('param1')) AS X /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT X.A FROM (EXEC proc1('param1')) AS X /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testStoredQuery2SanityCheckWithComments() {
        String sql = "exec proc1('param1') /* Trailing Comment */";
        String expectedSql = "EXEC proc1('param1') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testIfStatementWithComments() throws Exception {

        helpStmtTest("IF(c = 5) BEGIN DECLARE short a; END ELSE BEGIN DECLARE short b; END",
                     "IF(c = 5)" + "" + NEW_LINE + "BEGIN" + "" + NEW_LINE + "DECLARE short a;" + "" + NEW_LINE + "END" + ""
                                                                                             + NEW_LINE + "ELSE" + "" + NEW_LINE
                                                                                             + "BEGIN" + "" + NEW_LINE
                                                                                             + "DECLARE short b;" + "\n" + "END",
                     null);
    }

    @Test
    public void testAssignStatementWithComments() throws Exception {

        helpStmtTest("a = SELECT a1 FROM g WHERE a2 = 5;", "a = (SELECT a1 FROM g WHERE a2 = 5);", null);

        helpStmtTest("a = 'aString';", "a = 'aString';", null);
    }

    @Test
    public void testDeclareStatementWithComments() throws Exception {

        helpStmtTest("DECLARE short a;", "DECLARE short a;", null);
    }

    @Test
    public void testDeclareStatementWithAssignmentWithComments() throws Exception {

        helpStmtTest("DECLARE short a = null;", "DECLARE short a = null;", null);
    }

    @Test
    public void testDeclareStatementWithAssignment1WithComments() throws Exception {

        helpStmtTest("DECLARE string a = SELECT a1 FROM g WHERE a2 = 5;",
                     "DECLARE string a = (SELECT a1 FROM g WHERE a2 = 5);",
                     null);
    }

    @Test
    public void testStatementWithComments() throws Exception {
        helpStmtTest("DECLARE short a;", "DECLARE short a;", null);
    }

    @Test
    public void testCommandStatementWithComments() throws Exception {
        helpStmtTest("SELECT a1 FROM g WHERE a2 = 5;", "SELECT a1 FROM g WHERE a2 = 5;", null);
    }

    @Test
    public void testDynamicCommandStatementWithComments() throws Exception {
        helpStmtTest("exec string 'SELECT a1 FROM g WHERE a2 = 5' as a1 string into #g;",
                     "EXECUTE IMMEDIATE 'SELECT a1 FROM g WHERE a2 = 5' AS a1 string INTO #g;",
                     null);
    }

    //sql is a variable, also uses the as, into, and update clauses
    @Test
    public void testDynamicCommandStatement1WithComments() throws Exception {
        helpStmtTest("execute IMMEDIATE z as a1 string, a2 integer into #g update 1;",
                     "EXECUTE IMMEDIATE z AS a1 string, a2 integer INTO #g UPDATE 1;",
                     null);
    }

    @Test
    public void testDynamicCommandStatementWithUsingWithComments() throws Exception {
        helpStmtTest("execute immediate z using a=b;", "EXECUTE IMMEDIATE z USING a = b;", null);
    }

    @Test
    public void testSubquerySetCriteria0WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b IN (SELECT a /* Pre-From Comment */ FROM db.g WHERE a2 = 5) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b IN (SELECT a /* Pre-From Comment */ FROM db.g WHERE a2 = 5) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testSubquerySetCriteria1WithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b NOT IN (SELECT a /* Pre-From Comment */ FROM db.g WHERE a2 = 5) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b NOT IN (SELECT a /* Pre-From Comment */ FROM db.g WHERE a2 = 5) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testSubquerySetCriteriaWithExecWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b IN (EXEC m.sq1()) /* Trailing Comment */";
        // TODO
        // Not a truly trailing comment since the as x gets inserted after the comment.
        // However taking into account the 'as x' is nigh on impossible since it would require
        // accounting for the IN, EXEC/CALL/EXECUTE keywords in the parser/token manager
        // as well as trying to handle everything that comes after the EXEC keyword as well as
        // the opening and closing brackets. Just not worth it at this stage for a mere comment!
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b IN (SELECT * FROM (EXEC m.sq1()) /* Trailing Comment */ AS x)";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testSubquerySetCriteriaWithUnionWithComments() {
        String sql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b IN (SELECT x1 FROM db.g2 UNION ALL SELECT x2 FROM db.g3) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a /* Pre-From Comment */ FROM db.g WHERE b IN (SELECT x1 FROM db.g2 UNION ALL SELECT x2 FROM db.g3) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testVariablesInExecWithComments() {
        String sql = "Exec proc1(param1) /* Trailing Comment */";
        String expectedSql = "EXEC proc1(param1) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testExecSubqueryWithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM newModel2.Table1, (EXEC NewVirtual.StoredQuery()) AS a /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM newModel2.Table1, (EXEC NewVirtual.StoredQuery()) AS a /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testEscapedFunction1WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM a.thing WHERE e1 = {fn concat('a', 'b')} /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM a.thing WHERE e1 = concat('a', 'b') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testEscapedFunction2WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM a.thing WHERE e1 = {fn convert(5, string)} /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM a.thing WHERE e1 = convert(5, string) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testEscapedFunction3WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM a.thing WHERE e1 = {fn cast(5 as string)} /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM a.thing WHERE e1 = cast(5 AS string) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testEscapedFunction4WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM a.thing WHERE e1 = {fn concat({fn concat('a', 'b')}, 'c')} /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM a.thing WHERE e1 = concat(concat('a', 'b'), 'c') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testFunctionWithUnderscoreWithComments() {
        String sql = "/* Leading Comment */ SELECT yowza_yowza() FROM a.thing /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT yowza_yowza() FROM a.thing /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testManyInnerJoins1WithComments() {
        String sql = "/* Leading Comment */ SELECT * " + "FROM SQL1.dbo.Customers INNER JOIN SQL1.dbo.Orders "
                     + "ON SQL1.dbo.Customers.CustomerID = SQL1.dbo.Orders.CustomerID " + "INNER JOIN SQL1.dbo.order_details "
                     + "ON SQL1.dbo.Orders.OrderID = SQL1.dbo.order_details.OrderID";

        String sqlExpected = "/* Leading Comment */ SELECT * " + "FROM (SQL1.dbo.Customers INNER JOIN SQL1.dbo.Orders "
                             + "ON SQL1.dbo.Customers.CustomerID = SQL1.dbo.Orders.CustomerID) "
                             + "INNER JOIN SQL1.dbo.order_details "
                             + "ON SQL1.dbo.Orders.OrderID = SQL1.dbo.order_details.OrderID";
        helpTest(sql, sqlExpected, null);
    }

    @Test
    public void testManyInnerJoins2WithComments() {
        String sql = "/* Leading Comment */ SELECT * " + "FROM A INNER JOIN (B RIGHT OUTER JOIN C ON b1 = c1) " + "ON a1 = b1 "
                     + "INNER JOIN D " + "ON a1 = d1";

        String sqlExpected = "/* Leading Comment */ SELECT * " + "FROM (A INNER JOIN (B RIGHT OUTER JOIN C ON b1 = c1) " + "ON a1 = b1) "
                             + "INNER JOIN D " + "ON a1 = d1";
        helpTest(sql, sqlExpected, null);
    }

    @Test
    public void testManyInnerJoins3WithComments() {
        String sql = "/* Leading Comment */ SELECT * " + "FROM A INNER JOIN " + "(B RIGHT OUTER JOIN C ON b1 = c1 "
                     + "CROSS JOIN D) " + "ON a1 = d1";

        String sqlExpected = "/* Leading Comment */ SELECT * " + "FROM A INNER JOIN " + "((B RIGHT OUTER JOIN C ON b1 = c1) " + "CROSS JOIN D) "
                             + "ON a1 = d1";
        helpTest(sql, sqlExpected, null);
    }

    @Test
    public void testLoopStatementWithComments() throws Exception {
        helpStmtTest("LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor BEGIN DECLARE integer x; x=mycursor.c1; END",
                     "LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor" + "" + NEW_LINE + "BEGIN" + "" + NEW_LINE + "DECLARE integer x;"
                                                                                                                 + "\n"
                                                                                                                 + "x = mycursor.c1;"
                                                                                                                 + "" + NEW_LINE
                                                                                                                 + "END",
                     null);
    }

    @Test
    public void testLoopStatementWithOrderByWithComments() throws Exception {
        helpStmtTest("LOOP ON (SELECT c1, c2 FROM m.g ORDER BY c1) AS mycursor BEGIN DECLARE integer x; x=mycursor.c1; END",
                     "LOOP ON (SELECT c1, c2 FROM m.g ORDER BY c1) AS mycursor" + "" + NEW_LINE + "BEGIN" + "" + NEW_LINE + "DECLARE integer x;"
                                                                                                                             + "\n"
                                                                                                                             + "x = mycursor.c1;"
                                                                                                                             + "\n"
                                                                                                                             + "END",
                     null);
    }

    @Test
    public void testWhileStatementWithComments() throws Exception {
        helpStmtTest("WHILE (x < 100) BEGIN x=x+1; END",
                     "WHILE(x < 100)" + "" + NEW_LINE + "BEGIN" + "" + NEW_LINE + "x = (x + 1);" + "" + NEW_LINE + "END",
                     null);
    }

    @Test
    public void testWhileStatement1WithComments() throws Exception {
        helpStmtTest("WHILE (x < 100) \"1y\": BEGIN ATOMIC x=x+1; CONTINUE \"1y\"; END",
                     "WHILE(x < 100)" + "" + NEW_LINE + "\"1y\" : BEGIN ATOMIC" + "" + NEW_LINE + "x = (x + 1);\nCONTINUE \"1y\";"
                                                                                         + "" + NEW_LINE + "END",
                     null);
    }

    @Test
    public void testBreakStatementWithComments() throws Exception {
        helpStmtTest("break;", "BREAK;", null);
    }

    @Test
    public void testContinueStatementWithComments() throws Exception {
        helpStmtTest("continue;", "CONTINUE;", null);
    }

    @Test
    public void testContinueStatement1WithComments() throws Exception {
        helpStmtTest("continue x;", "CONTINUE x;", null);
    }

    @Test
    public void testScalarSubqueryExpressionInSelectWithComments() {
        String sql = "/* Leading Comment */ SELECT e1, (SELECT e1 FROM m.g1) FROM m.g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1, (SELECT e1 FROM m.g1) FROM m.g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryExpressionInSelect2WithComments() {
        String sql = "/* Leading Comment */ SELECT (SELECT e1 FROM m.g1) FROM m.g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (SELECT e1 FROM m.g1) FROM m.g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryExpressionInSelect3WithComments() {
        String sql = "/* Leading Comment */ SELECT (SELECT e1 FROM m.g1), e1 FROM m.g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT (SELECT e1 FROM m.g1), e1 FROM m.g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryExpressionWithAliasWithComments() {
        String sql = "/* Leading Comment */ SELECT e1, (SELECT e1 FROM m.g1) as X FROM m.g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1, (SELECT e1 FROM m.g1) AS X FROM m.g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryExpressionInComplexExpressionWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT e1, ((SELECT e1 FROM m.g1) + 2) as X FROM m.g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1, ((SELECT e1 FROM m.g1) + 2) AS X FROM m.g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryExpressionInComplexExpression2WithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT e1, (3 + (SELECT e1 FROM m.g1)) as X FROM m.g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1, (3 + (SELECT e1 FROM m.g1)) AS X FROM m.g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryExpressionInComplexExpression3WithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT e1, ((SELECT e1 FROM m.g1) + (SELECT e3 FROM m.g3)) as X FROM m.g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1, ((SELECT e1 FROM m.g1) + (SELECT e3 FROM m.g3)) AS X FROM m.g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryExpressionInFunctionWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT e1, length((SELECT e1 FROM m.g1)) as X FROM m.g2 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1, length((SELECT e1 FROM m.g1)) AS X FROM m.g2 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testExistsPredicateCriteriaWithComments() {
        String sql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE Exists (SELECT e1 FROM m.g1) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE EXISTS (SELECT e1 FROM m.g1) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testAnyQuantifierSubqueryComparePredicateWithComments() {
        String sql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE e3 >= ANY (SELECT e1 FROM m.g1) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE e3 >= ANY (SELECT e1 FROM m.g1) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testSomeQuantifierSubqueryComparePredicateWithComments() {
        String sql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE e3 > some (SELECT e1 FROM m.g1) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE e3 > SOME (SELECT e1 FROM m.g1) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testAllQuantifierSubqueryComparePredicateWithComments() {
        String sql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE e3 = all (SELECT e1 FROM m.g1) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE e3 = ALL (SELECT e1 FROM m.g1) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryComparePredicateWithComments() {
        String sql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE e3 < (SELECT e1 FROM m.g1) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT e1 FROM m.g2 WHERE e3 < (SELECT e1 FROM m.g1) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);

    }

    @Test
    public void testSelectIntoWithComments() {
        String sql = "/* Leading Comment */ SELECT c1, c2 INTO #temp FROM m.g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT c1, c2 INTO #temp FROM m.g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testAndOrPrecedence_1575WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM m.g1 WHERE e1=0 OR e2=1 AND e3=3 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM m.g1 WHERE (e1 = 0) OR ((e2 = 1) AND (e3 = 3)) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testAndOrPrecedence2_1575WithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM m.g1 WHERE e1=0 AND e2=1 OR e3=3 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM m.g1 WHERE ((e1 = 0) AND (e2 = 1)) OR (e3 = 3) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testCompoundNonJoinCriteria_defect15167_3WithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT A.alert_id, A.primary_entity_name, A.primary_entity_level_code, A.alert_description, A.create_date, A.alert_risk_score, S.scenario_name, A.alert_status_code, A.process_id, A.actual_values_text, S.SCENARIO_CATEGORY_DESC, A.primary_entity_number, A.scenario_id, A.primary_entity_key FROM (FSK_ALERT AS A LEFT OUTER JOIN FSK_SCENARIO AS S ON A.scenario_id = S.scenario_id) INNER JOIN FSC_ACCOUNT_DIM AS C ON (A.primary_entity_key = C.ACCOUNT_KEY AND (S.current_ind = 'Y' OR S.current_ind IS NULL)) WHERE (A.primary_entity_level_code = 'ACC') AND (C.ACCOUNT_KEY = 23923) AND (A.logical_delete_ind = 'N') /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT A.alert_id, A.primary_entity_name, A.primary_entity_level_code, A.alert_description, A.create_date, A.alert_risk_score, S.scenario_name, A.alert_status_code, A.process_id, A.actual_values_text, S.SCENARIO_CATEGORY_DESC, A.primary_entity_number, A.scenario_id, A.primary_entity_key FROM (FSK_ALERT AS A LEFT OUTER JOIN FSK_SCENARIO AS S ON A.scenario_id = S.scenario_id) INNER JOIN FSC_ACCOUNT_DIM AS C ON A.primary_entity_key = C.ACCOUNT_KEY AND ((S.current_ind = 'Y') OR (S.current_ind IS NULL)) WHERE (A.primary_entity_level_code = 'ACC') AND (C.ACCOUNT_KEY = 23923) AND (A.logical_delete_ind = 'N') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testCompoundNonJoinCriteria_defect15167_4WithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT A.alert_id, A.primary_entity_name, A.primary_entity_level_code, A.alert_description, A.create_date, A.alert_risk_score, S.scenario_name, A.alert_status_code, A.process_id, A.actual_values_text, S.SCENARIO_CATEGORY_DESC, A.primary_entity_number, A.scenario_id, A.primary_entity_key FROM (FSK_ALERT AS A LEFT OUTER JOIN FSK_SCENARIO AS S ON A.scenario_id = S.scenario_id) INNER JOIN FSC_ACCOUNT_DIM AS C ON (A.primary_entity_key = C.ACCOUNT_KEY AND S.current_ind = 'Y' OR S.current_ind IS NULL) WHERE (A.primary_entity_level_code = 'ACC') AND (C.ACCOUNT_KEY = 23923) AND (A.logical_delete_ind = 'N') /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT A.alert_id, A.primary_entity_name, A.primary_entity_level_code, A.alert_description, A.create_date, A.alert_risk_score, S.scenario_name, A.alert_status_code, A.process_id, A.actual_values_text, S.SCENARIO_CATEGORY_DESC, A.primary_entity_number, A.scenario_id, A.primary_entity_key FROM (FSK_ALERT AS A LEFT OUTER JOIN FSK_SCENARIO AS S ON A.scenario_id = S.scenario_id) INNER JOIN FSC_ACCOUNT_DIM AS C ON (((A.primary_entity_key = C.ACCOUNT_KEY) AND (S.current_ind = 'Y')) OR (S.current_ind IS NULL)) WHERE (A.primary_entity_level_code = 'ACC') AND (C.ACCOUNT_KEY = 23923) AND (A.logical_delete_ind = 'N') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testScalarSubqueryWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT (SELECT 1) FROM x /* Trailing Comment */";
        helpTest(sql, sql, null);
    }

    @Test
    public void testElementInDoubleQuotesWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT \"foo\" /* Pre-From Comment */ FROM x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT foo /* Pre-From Comment */ FROM x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testElementInDoubleQuotes_InsertWithComments() throws Exception {
        String sql = "insert into x (\"foo\") values ('bar') /* Trailing Comment */";
        String expectedSql = "INSERT INTO x (foo) VALUES ('bar') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testElementInDoubleQuotes_UpdateWithComments() throws Exception {
        String sql = "update x set \"foo\"='bar' /* Trailing Comment */";
        String expectedSql = "UPDATE x SET foo = 'bar' /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testElementInDoubleQuotes_deleteWithComments() throws Exception {
        String sql = "delete from x where \"foo\"='bar' /* Trailing Comment */";
        String expectedSql = "DELETE FROM x WHERE foo = 'bar' /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testAliasInDoubleQuotesWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT fooKey AS \"fooAlias\" /* Pre-From Comment */ FROM x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT fooKey AS fooAlias /* Pre-From Comment */ FROM x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testAliasInDoubleQuotesWithQuotedGroupWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT fooKey AS \"fooAlias\" /* Pre-From Comment */ FROM \"x.y\".z where x.\"y.z\".id = 10 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT fooKey AS fooAlias /* Pre-From Comment */ FROM x.y.z WHERE x.y.z.id = 10 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testSingleQuotedConstantWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT 'fooString' FROM \"x.y.z\" /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT 'fooString' FROM x.y.z /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testOrderByWithNumbers_InQuotesWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT x, y from z order by \"1\" /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT x, y FROM z ORDER BY \"1\" /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testOrderByWithNumbers_AsIntWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT x, y FROM z order by 1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT x, y FROM z ORDER BY 1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testCase3281NamedVariableWithComments() {
        String sql = "Exec proc1(param1 = 'paramValue1') /* Trailing Comment */";
        String expectedSql = "EXEC proc1(param1 => 'paramValue1') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testCase3281NamedVariablesWithComments() {
        String sql = "Exec proc1(param1 = 'paramValue1', param2 = 'paramValue2') /* Trailing Comment */";
        String expectedSql = "EXEC proc1(param1 => 'paramValue1', param2 => 'paramValue2') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testCase3281QuotedNamedVariableFails2WithComments() {
        String sql = "Exec proc1('a' = 'b') /* Trailing Comment */";
        String expectedSql = "EXEC proc1(('a' = 'b')) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    /** Test what happens if the name of a parameter is a reserved word.  It must be quoted (double-ticks). */
    @Test
    public void testCase3281NamedVariablesReservedWordsWithComments() {
        String sql = "Exec proc1(\"in\" = 'paramValue1', in2 = 'paramValue2') /* Trailing Comment */";
        String expectedSql = "EXEC proc1(\"in\" => 'paramValue1', in2 => 'paramValue2') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testEscapedOuterJoinWithComments() {
        String sql = "/* Leading Comment */ SELECT * FROM {oj A LEFT OUTER JOIN B ON (A.x=B.x)} /* Trailing Comment */";
        String expected = "/* Leading Comment */ SELECT * FROM A LEFT OUTER JOIN B ON A.x = B.x /* Trailing Comment */";
        helpTest(sql, expected, null);
    }

    @Test
    public void testNameSpacedFunctionNameWithComments() {
        String sql = "/* Leading Comment */ select a.x() /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT a.x() /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testUnionJoinWithComments() {
        String sql = "/* Leading Comment */ select * from pm1.g1 union join pm1.g2 where g1.e1 = 1 /* Trailing Comment */";
        String expected = "/* Leading Comment */ SELECT * FROM pm1.g1 UNION JOIN pm1.g2 WHERE g1.e1 = 1 /* Trailing Comment */";
        helpTest(sql, expected, null);
    }

    @Test
    public void testCommandWithSemicolonWithComments() throws Exception {
        String sql = "/* Leading Comment */ select * from pm1.g1; /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM pm1.g1 /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testLOBTypesWithComments() throws Exception {
        String sql = "/* Leading Comment */ select convert(null, blob), convert(null, clob), convert(null, xml) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT convert(null, blob), convert(null, clob), convert(null, xml) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testInsertWithoutColumnsWithComments() {
        String sql = "INSERT INTO m.g VALUES ('a', 'b') /* Trailing Comment */";
        String expectedSql = "INSERT INTO m.g VALUES ('a', 'b') /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testXmlElementWithComments() throws Exception {
        helpTestExpression("xmlelement(name \"table\", 'x')", "XMLELEMENT(NAME \"table\", 'x')", null);
    }

    @Test
    public void testXmlElement1WithComments() throws Exception {
        helpTestExpression("xmlelement(\"table\", 'x')", "XMLELEMENT(NAME \"table\", 'x')", null);
    }

    @Test
    public void testXmlElementWithAttributesWithComments() throws Exception {
        helpTestExpression("xmlelement(y, xmlattributes('a' as val))", "XMLELEMENT(NAME y, XMLATTRIBUTES('a' AS val))", null);
    }

    @Test
    public void testXmlForestWithComments() throws Exception {
        helpTestExpression("xmlforest(a as \"table\")", "XMLFOREST(a AS \"table\")", null);
    }

    @Test
    public void testXmlPiWithComments() throws Exception {
        helpTestExpression("xmlpi(NAME a, val)", "xmlpi(NAME a, val)", null);
    }

    @Test
    public void testXmlNamespacesWithComments() throws Exception {
        helpTestExpression("xmlforest(xmlnamespaces(no default, 'http://foo' as x), a as \"table\")",
                           "XMLFOREST(XMLNAMESPACES(NO DEFAULT, 'http://foo' AS x), a AS \"table\")",
                           null);
    }

    @Test
    public void testXmlAggWithOrderByWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT xmlAgg(1 order by e2) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT XMLAGG(1 ORDER BY e2) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testTextAggWithOrderByWithComments() throws Exception {

        String sql = "/* Leading Comment */ SELECT TextAgg(FOR e1 as col1, e2 as col2 delimiter ',' header order by e2) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT TEXTAGG(FOR e1 AS col1, e2 AS col2 DELIMITER ',' HEADER ORDER BY e2) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testArrayAggWithOrderByWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT array_agg(1 order by e2) /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ARRAY_AGG(1 ORDER BY e2) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testArrayAggWithIndexingWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT (array_agg(1))[1] /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT array_get(ARRAY_AGG(1), 1) /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testNestedTableWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT * from TABLE(exec foo()) as x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM TABLE(EXEC foo()) AS x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testTextTableWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT * from texttable(file columns x string WIDTH 1, y date width 10 skip 10) as x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM TEXTTABLE(file COLUMNS x string WIDTH 1, y date WIDTH 10 SKIP 10) AS x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);

        sql = "/* Leading Comment */ SELECT * from texttable(file columns x string, y date delimiter ',' escape '\"' header skip 10) as x";
        expectedSql = "/* Leading Comment */ SELECT * FROM TEXTTABLE(file COLUMNS x string, y date DELIMITER ',' ESCAPE '\"' HEADER SKIP 10) AS x";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testXMLTableWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT * from xmltable(xmlnamespaces(no default), '/' columns x for ordinality, y date default {d'2000-01-01'} path '@date') as x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM XMLTABLE(XMLNAMESPACES(NO DEFAULT), '/' COLUMNS x FOR ORDINALITY, y date DEFAULT {d'2000-01-01'} PATH '@date') AS x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testXmlSerializeWithComments() throws Exception {
        helpTestExpression("xmlserialize(document x as CLOB)", "XMLSERIALIZE(DOCUMENT x AS CLOB)", null);
    }

    @Test
    public void testXmlQueryWithComments() throws Exception {
        helpTestExpression("xmlquery('/x' passing foo null on empty)", "XMLQUERY('/x' PASSING foo NULL ON EMPTY)", null);
    }

    @Test
    public void testXmlParseWithComments() throws Exception {
        helpTestExpression("xmlparse(document x wellformed)", "XMLPARSE(DOCUMENT x WELLFORMED)", null);
    }

    @Test
    public void testXmlSerialize1WithComments() throws Exception {
        helpTestExpression("xmlserialize(x as CLOB)", "XMLSERIALIZE(x AS CLOB)", null);
    }

    @Test
    public void testExpressionCriteriaWithComments() throws Exception {
        helpTestExpression("case when x then y end", "CASE WHEN x THEN y END", null);
    }

    @Test
    public void testExpressionCriteria1WithComments() throws Exception {
        helpTestExpression("case when not x then y end", "CASE WHEN NOT (x) THEN y END", null);
    }

    @Test
    public void testWithClauseWithComments() throws Exception {
        String sql = "WITH x AS (SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString) SELECT a /* Pre-From Comment 2 */ FROM db.g WHERE b = aString /* Trailing Comment */";
        String expectedSql = "WITH x AS (SELECT a /* Pre-From Comment */ FROM db.g WHERE b = aString) SELECT a /* Pre-From Comment 2 */ FROM db.g WHERE b = aString /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testExplicitTableWithComments() throws Exception {
        String sql = "/* Leading Comment */ TABLE X /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM X /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testArrayTableWithComments() throws Exception {
        String sql = "/* Leading Comment */ SELECT * from arraytable(null columns x string, y date) as x /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT * FROM ARRAYTABLE(null COLUMNS x string, y date) AS x /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testPositionalReferenceWithComments() throws Exception {
        String sql = "/* Leading Comment */ select $1 /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ? /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testNonReservedWithComments() throws Exception {
        String sql = "/* Leading Comment */ select count /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT count /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testAggFilterWithComments() throws Exception {
        String sql = "/* Leading Comment */ select count(*) filter (where x = 1) from g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT COUNT(*) FILTER(WHERE x = 1) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testWindowFunctionWithComments() throws Exception {
        String sql = "/* Leading Comment */ select row_number() over (partition by x order by y) from g /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT ROW_NUMBER() OVER (PARTITION BY x ORDER BY y) FROM g /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testSubStringWithComments() {
        String sql = "/* Leading Comment */ select substring(RTRIM(MED.BATDAT), 4, 4) from FCC.MEDMAS as MED /* Trailing Comment */";
        String expectedSql = "/* Leading Comment */ SELECT substring(RTRIM(MED.BATDAT), 4, 4) FROM FCC.MEDMAS AS MED /* Trailing Comment */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testCommentsSimple() {
        String sql = "/* Comment 1 */ SELECT * FROM TABLE_A";
        String expectedSql = "/* Comment 1 */ SELECT * FROM TABLE_A";
        helpTest(sql, expectedSql, null);
    }
}
