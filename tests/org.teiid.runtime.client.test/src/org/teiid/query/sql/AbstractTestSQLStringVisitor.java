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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISetQuery.Operation;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.resolver.QueryResolver;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
import org.teiid.query.sql.lang.LanguageObject;
import org.teiid.query.sql.lang.Limit;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.NotCriteria;
import org.teiid.query.sql.lang.Option;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.lang.SetQuery;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryCompareCriteria.PredicateQuantifier;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.CaseExpression;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.ScalarSubquery;
import org.teiid.query.sql.symbol.SearchedCaseExpression;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import org.teiid.query.unittest.RealMetadataFactory;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class AbstractTestSQLStringVisitor extends AbstractTest<Command> {

    private RealMetadataFactory metadataFactory;

    /**
     * @param teiidVersion 
     */
    public AbstractTestSQLStringVisitor(Version teiidVersion) {
        super(teiidVersion);
        metadataFactory = new RealMetadataFactory(this.teiidVersion);
    }

    // ################################## TEST HELPERS ################################ 

    protected void helpTest(LanguageObject obj, String expectedStr) {
        String actualStr = SQLStringVisitor.getSQLString(obj);
        assertEquals("Expected and actual strings don't match: ", expectedStr, actualStr);
    }

    protected Expression helpTestExpression(String sql, String expected) throws Exception {
        Expression expr = parser.parseExpression(sql);
        helpTest(expr, expected);
        return expr;
    }

    protected List getWhenExpressions(int expressions) {
        return getWhenExpressions(expressions, -1, false);
    }
    
    protected List getWhenExpressions(int expressions, int nullIndex, boolean includeNull) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < expressions; i++) {
            if(includeNull && i == nullIndex) {
                list.add(getFactory().newConstant(null) );
            }else {
                list.add(getFactory().newConstant(String.valueOf((char)('a' + i))));
            }
        }
        return list;
    }
    
    protected List getThenExpressions(int expressions) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < expressions; i++) {
            list.add(getFactory().newConstant(new Integer(i)));
        }
        return list;
    }

    protected CaseExpression example(int whens) {
        ElementSymbol x = getFactory().newElementSymbol("x");
        CaseExpression caseExpr = getFactory().newCaseExpression(x, getWhenExpressions(whens), getThenExpressions(whens));
        caseExpr.setElseExpression(getFactory().newConstant(new Integer(9999)));
        return caseExpr;
    }
    
    protected CaseExpression caseExample(int whens, int nullIndex, boolean includeNull) {
        assertTrue("Null Index must be less than the number of When expressions", nullIndex < whens);
        ElementSymbol x = getFactory().newElementSymbol("x");
        CaseExpression caseExpr = getFactory().newCaseExpression(x, getWhenExpressions(whens, nullIndex, includeNull), getThenExpressions(whens));
        caseExpr.setElseExpression(getFactory().newConstant(new Integer(9999)));
        return caseExpr;
    }

    protected List getWhenCriteria(int criteria) {
        ArrayList list = new ArrayList();
        ElementSymbol x = getFactory().newElementSymbol("x");
        for (int i = 0; i < criteria; i++) {
            list.add(getFactory().newCompareCriteria(x, Operator.EQ, getFactory().newConstant(new Integer(i))));
        }
        return list;
    }

    protected List getAlphaWhenCriteria(int criteria) {
        ArrayList list = new ArrayList();
        ElementSymbol x = getFactory().newElementSymbol("x");
        for (int i = 0; i < criteria; i++) {
            list.add(getFactory().newCompareCriteria(x, Operator.EQ, getFactory().newConstant(String.valueOf((char)('a' + i)))));
        }
        return list;
    }

    protected SearchedCaseExpression searchedCaseExample(int whens) {
        SearchedCaseExpression caseExpr = getFactory().newSearchedCaseExpression(getWhenCriteria(whens), getThenExpressions(whens));
        caseExpr.setElseExpression(getFactory().newConstant(new Integer(9999)));
        return caseExpr;
    }

    protected SearchedCaseExpression searchedCaseExample2(int whens) {
        SearchedCaseExpression caseExpr = getFactory().newSearchedCaseExpression(getAlphaWhenCriteria(whens), getThenExpressions(whens));
        caseExpr.setElseExpression(getFactory().newConstant(new Integer(9999)));
        return caseExpr;
    }

    // ################################## ACTUAL TESTS ################################

    @Test
    public void testNull() {
        String sql = SQLStringVisitor.getSQLString(null);
        assertEquals("Incorrect string for null object", SQLStringVisitor.UNDEFINED, sql);
    }

    @Test
    public void testBetweenCriteria1() {
        BetweenCriteria bc = getFactory().newBetweenCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             getFactory().newConstant(new Integer(1000)),
                                                             getFactory().newConstant(new Integer(2000)));
        helpTest(bc, "m.g.c1 BETWEEN 1000 AND 2000");
    }

    @Test
    public void testBetweenCriteria2() {
        BetweenCriteria bc = getFactory().newBetweenCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             getFactory().newConstant(new Integer(1000)),
                                                             getFactory().newConstant(new Integer(2000)));
        bc.setNegated(true);
        helpTest(bc, "m.g.c1 NOT BETWEEN 1000 AND 2000");
    }

    @Test
    public void testCompareCriteria1() {
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             Operator.EQ,
                                                             getFactory().newConstant("abc"));

        helpTest(cc, "m.g.c1 = 'abc'");
    }

    @Test
    public void testCompareCriteria2() {
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             Operator.NE,
                                                             getFactory().newConstant("abc"));

        helpTest(cc, "m.g.c1 <> 'abc'");
    }

    @Test
    public void testCompareCriteria3() {
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             Operator.GT,
                                                             getFactory().newConstant("abc"));

        helpTest(cc, "m.g.c1 > 'abc'");
    }

    @Test
    public void testCompareCriteria4() {
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             Operator.GE,
                                                             getFactory().newConstant("abc"));

        helpTest(cc, "m.g.c1 >= 'abc'");
    }

    @Test
    public void testCompareCriteria5() {
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             Operator.LT,
                                                             getFactory().newConstant("abc"));

        helpTest(cc, "m.g.c1 < 'abc'");
    }

    @Test
    public void testCompareCriteria6() {
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             Operator.LE,
                                                             getFactory().newConstant("abc"));

        helpTest(cc, "m.g.c1 <= 'abc'");
    }

    @Test
    public void testCompareCriteria7() {
        CompareCriteria cc = getFactory().newCompareCriteria((Expression)null, Operator.EQ, (Expression)null);

        helpTest(cc, "<undefined> = <undefined>");
    }

    @Test
    public void testCompoundCriteria1() {
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                             Operator.EQ,
                                                             getFactory().newConstant("abc"));
        List<Criteria> crits = new ArrayList<Criteria>();
        crits.add(cc);
        CompoundCriteria comp = getFactory().newNode(ASTNodes.COMPOUND_CRITERIA);
        comp.setOperator(CompoundCriteria.AND);
        comp.setCriteria(crits);

        helpTest(comp, "m.g.c1 = 'abc'");
    }

    @Test
    public void testCompoundCriteria2() {
        CompareCriteria cc1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                              Operator.EQ,
                                                              getFactory().newConstant("abc"));
        CompareCriteria cc2 = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c2"),
                                                              Operator.EQ,
                                                              getFactory().newConstant("abc"));
        List<Criteria> crits = new ArrayList<Criteria>();
        crits.add(cc1);
        crits.add(cc2);
        CompoundCriteria comp = getFactory().newNode(ASTNodes.COMPOUND_CRITERIA);
        comp.setOperator(CompoundCriteria.AND);
        comp.setCriteria(crits);

        helpTest(comp, "(m.g.c1 = 'abc') AND (m.g.c2 = 'abc')");
    }

    @Test
    public void testCompoundCriteria3() {
        CompareCriteria cc1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                              Operator.EQ,
                                                              getFactory().newConstant("abc"));
        CompareCriteria cc2 = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c2"),
                                                              Operator.EQ,
                                                              getFactory().newConstant("abc"));
        CompareCriteria cc3 = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c3"),
                                                              Operator.EQ,
                                                              getFactory().newConstant("abc"));
        List<Criteria> crits = new ArrayList<Criteria>();
        crits.add(cc1);
        crits.add(cc2);
        crits.add(cc3);
        CompoundCriteria comp = getFactory().newNode(ASTNodes.COMPOUND_CRITERIA);
        comp.setOperator(CompoundCriteria.OR);
        comp.setCriteria(crits);

        helpTest(comp, "(m.g.c1 = 'abc') OR (m.g.c2 = 'abc') OR (m.g.c3 = 'abc')");
    }

    @Test
    public void testCompoundCriteria4() {
        CompareCriteria cc1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                              Operator.EQ,
                                                              getFactory().newConstant("abc"));
        List<Criteria> crits = new ArrayList<Criteria>();
        crits.add(cc1);
        crits.add(null);
        CompoundCriteria comp = getFactory().newNode(ASTNodes.COMPOUND_CRITERIA);
        comp.setOperator(CompoundCriteria.OR);
        comp.setCriteria(crits);

        helpTest(comp, "(m.g.c1 = 'abc') OR (<undefined>)");
    }

    @Test
    public void testCompoundCriteria5() {
        CompareCriteria cc1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                              Operator.EQ,
                                                              getFactory().newConstant("abc"));
        List<Criteria> crits = new ArrayList<Criteria>();
        crits.add(null);
        crits.add(cc1);
        CompoundCriteria comp = getFactory().newNode(ASTNodes.COMPOUND_CRITERIA);
        comp.setOperator(CompoundCriteria.OR);
        comp.setCriteria(crits);

        helpTest(comp, "(<undefined>) OR (m.g.c1 = 'abc')");
    }

    @Test
    public void testCompoundCriteria6() {
        CompareCriteria cc1 = getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                              Operator.EQ,
                                                              getFactory().newConstant("abc"));
        List<Criteria> crits = new ArrayList<Criteria>();
        crits.add(cc1);
        crits.add(null);
        CompoundCriteria comp = getFactory().newNode(ASTNodes.COMPOUND_CRITERIA);
        comp.setOperator(CompoundCriteria.OR);
        comp.setCriteria(crits);

        helpTest(comp, "(m.g.c1 = 'abc') OR (<undefined>)");
    }

    @Test
    public void testDelete1() {
        Delete delete = getFactory().newNode(ASTNodes.DELETE);
        delete.setGroup(getFactory().newGroupSymbol("m.g"));

        helpTest(delete, "DELETE FROM m.g");
    }

    @Test
    public void testDelete2() {
        Delete delete = getFactory().newNode(ASTNodes.DELETE);
        delete.setGroup(getFactory().newGroupSymbol("m.g"));
        delete.setCriteria(getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g.c1"),
                                                           Operator.EQ,
                                                           getFactory().newConstant("abc")));

        helpTest(delete, "DELETE FROM m.g WHERE m.g.c1 = 'abc'");
    }

    @Test
    public void testFrom1() {
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("m.g1"));
        from.addGroup(getFactory().newGroupSymbol("m.g2"));

        helpTest(from, "FROM m.g1, m.g2");
    }

    @Test
    public void testFrom2() {
        From from = getFactory().newFrom();
        from.addClause(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g1")));
        from.addClause(getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g2")),
                                                     getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g3")),
                                                     JoinType.Types.JOIN_CROSS));

        helpTest(from, "FROM m.g1, m.g2 CROSS JOIN m.g3");
    }

    @Test
    public void testGroupBy1() {
        GroupBy gb = getFactory().newGroupBy();
        gb.addSymbol(getFactory().newElementSymbol("m.g.e1"));

        helpTest(gb, "GROUP BY m.g.e1");
    }

    @Test
    public void testGroupBy2() {
        GroupBy gb = getFactory().newGroupBy();
        gb.addSymbol(getFactory().newElementSymbol("m.g.e1"));
        gb.addSymbol(getFactory().newElementSymbol("m.g.e2"));
        gb.addSymbol(getFactory().newElementSymbol("m.g.e3"));

        helpTest(gb, "GROUP BY m.g.e1, m.g.e2, m.g.e3");
    }

    @Test
    public void testInsert1() {
        Insert insert = getFactory().newInsert();
        insert.setGroup(getFactory().newGroupSymbol("m.g1"));

        List<ElementSymbol> vars = new ArrayList<ElementSymbol>();
        vars.add(getFactory().newElementSymbol("e1"));
        vars.add(getFactory().newElementSymbol("e2"));
        insert.setVariables(vars);
        List<Constant> values = new ArrayList<Constant>();
        values.add(getFactory().newConstant(new Integer(5)));
        values.add(getFactory().newConstant("abc"));
        insert.setValues(values);

        helpTest(insert, "INSERT INTO m.g1 (e1, e2) VALUES (5, 'abc')");
    }

    @Test
    public void testIsNullCriteria1() {
        IsNullCriteria inc = getFactory().newNode(ASTNodes.IS_NULL_CRITERIA);
        inc.setExpression(getFactory().newConstant("abc"));

        helpTest(inc, "'abc' IS NULL");
    }

    @Test
    public void testIsNullCriteria2() {
        IsNullCriteria inc = getFactory().newNode(ASTNodes.IS_NULL_CRITERIA);
        inc.setExpression(getFactory().newElementSymbol("m.g.e1"));

        helpTest(inc, "m.g.e1 IS NULL");
    }

    @Test
    public void testIsNullCriteria3() {
        IsNullCriteria inc = getFactory().newNode(ASTNodes.IS_NULL_CRITERIA);
        helpTest(inc, "<undefined> IS NULL");
    }

    @Test
    public void testIsNullCriteria4() {
        IsNullCriteria inc = getFactory().newNode(ASTNodes.IS_NULL_CRITERIA);
        inc.setExpression(getFactory().newElementSymbol("m.g.e1"));
        inc.setNegated(true);
        helpTest(inc, "m.g.e1 IS NOT NULL");
    }

    @Test
    public void testJoinPredicate1() {
        JoinPredicate jp = getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g2")),
                                                         getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g3")),
                                                         JoinType.Types.JOIN_CROSS);

        helpTest(jp, "m.g2 CROSS JOIN m.g3");
    }

    @Test
    public void testOptionalJoinPredicate1() {
        JoinPredicate jp = getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g2")),
                                                         getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g3")),
                                                         JoinType.Types.JOIN_CROSS);
        jp.setOptional(true);
        helpTest(jp, "/*+ optional */ (m.g2 CROSS JOIN m.g3)");
    }

    @Test
    public void testJoinPredicate2() {
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g2.e1"), Operator.EQ, getFactory().newElementSymbol("m.g3.e1")));
        JoinPredicate jp = getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g2")),
                                                         getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g3")),
                                                         JoinType.Types.JOIN_INNER,
                                                         crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1");
    }

    @Test
    public void testJoinPredicate3() {
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g2.e1"), Operator.EQ, getFactory().newElementSymbol("m.g3.e1")));
        crits.add(getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g2.e2"), Operator.EQ, getFactory().newElementSymbol("m.g3.e2")));
        JoinPredicate jp = getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g2")),
                                                         getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g3")),
                                                         JoinType.Types.JOIN_INNER,
                                                         crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1 AND m.g2.e2 = m.g3.e2");
    }

    @Test
    public void testJoinPredicate4() {
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g2.e1"), Operator.EQ, getFactory().newElementSymbol("m.g3.e1")));
        JoinPredicate jp = getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g2")),
                                                         getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g3")),
                                                         JoinType.Types.JOIN_INNER,
                                                         crits);

        JoinPredicate jp2 = getFactory().newJoinPredicate(jp,
                                                          getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g1")),
                                                          JoinType.Types.JOIN_CROSS);

        helpTest(jp2, "(m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1) CROSS JOIN m.g1");
    }

    @Test
    public void testJoinPredicate5() {
        ArrayList<Criteria> crits = new ArrayList<Criteria>();
        crits.add(getFactory().newNotCriteria(getFactory().newCompareCriteria(getFactory().newElementSymbol("m.g2.e1"), Operator.EQ, getFactory().newElementSymbol("m.g3.e1"))));
        JoinPredicate jp = getFactory().newJoinPredicate(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g2")),
                                                         getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g3")),
                                                         JoinType.Types.JOIN_INNER,
                                                         crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON NOT (m.g2.e1 = m.g3.e1)");
    }

    @Test
    public void testJoinType1() {
        helpTest(getFactory().newJoinType(JoinType.Types.JOIN_CROSS), "CROSS JOIN");
    }

    @Test
    public void testJoinType2() {
        helpTest(getFactory().newJoinType(JoinType.Types.JOIN_INNER), "INNER JOIN");
    }

    @Test
    public void testJoinType3() {
        helpTest(getFactory().newJoinType(JoinType.Types.JOIN_RIGHT_OUTER), "RIGHT OUTER JOIN");
    }

    @Test
    public void testJoinType4() {
        helpTest(getFactory().newJoinType(JoinType.Types.JOIN_LEFT_OUTER), "LEFT OUTER JOIN");
    }

    @Test
    public void testJoinType5() {
        helpTest(getFactory().newJoinType(JoinType.Types.JOIN_FULL_OUTER), "FULL OUTER JOIN");
    }

    @Test
    public void testMatchCriteria1() {
        MatchCriteria mc = getFactory().newNode(ASTNodes.MATCH_CRITERIA);
        mc.setLeftExpression(getFactory().newElementSymbol("m.g.e1"));
        mc.setRightExpression(getFactory().newConstant("abc"));

        helpTest(mc, "m.g.e1 LIKE 'abc'");
    }

    @Test
    public void testMatchCriteria2() {
        MatchCriteria mc = getFactory().newNode(ASTNodes.MATCH_CRITERIA);
        mc.setLeftExpression(getFactory().newElementSymbol("m.g.e1"));
        mc.setRightExpression(getFactory().newConstant("%"));
        mc.setEscapeChar('#');

        helpTest(mc, "m.g.e1 LIKE '%' ESCAPE '#'");
    }

    @Test
    public void testMatchCriteria3() {
        MatchCriteria mc = getFactory().newNode(ASTNodes.MATCH_CRITERIA);
        mc.setLeftExpression(getFactory().newElementSymbol("m.g.e1"));
        mc.setRightExpression(getFactory().newConstant("abc"));
        mc.setNegated(true);
        helpTest(mc, "m.g.e1 NOT LIKE 'abc'");
    }

    @Test
    public void testNotCriteria1() {
        NotCriteria not = getFactory().newNotCriteria(getFactory().newIsNullCriteria(getFactory().newElementSymbol("m.g.e1")));
        helpTest(not, "NOT (m.g.e1 IS NULL)");
    }

    @Test
    public void testNotCriteria2() {
        NotCriteria not = getFactory().newNode(ASTNodes.NOT_CRITERIA);
        helpTest(not, "NOT (<undefined>)");
    }

    @Test
    public void testOption1() {
        Option option = getFactory().newNode(ASTNodes.OPTION);
        helpTest(option, "OPTION");
    }

    @Test
    public void testOption5() {
        Option option = getFactory().newNode(ASTNodes.OPTION);
        option.addDependentGroup("abc");
        option.addDependentGroup("def");
        option.addDependentGroup("xyz");
        helpTest(option, "OPTION MAKEDEP abc, def, xyz");
    }

    @Test
    public void testOption6() {
        Option option = getFactory().newNode(ASTNodes.OPTION);
        option.addDependentGroup("abc");
        option.addDependentGroup("def");
        option.addDependentGroup("xyz");
        helpTest(option, "OPTION MAKEDEP abc, def, xyz");
    }

    @Test
    public void testOption8() {
        Option option = getFactory().newNode(ASTNodes.OPTION);
        option.addNoCacheGroup("abc");
        option.addNoCacheGroup("def");
        option.addNoCacheGroup("xyz");
        helpTest(option, "OPTION NOCACHE abc, def, xyz");
    }

    //  related to defect 14423
    @Test
    public void testOption9() {
        Option option = getFactory().newNode(ASTNodes.OPTION);
        option.setNoCache(true);
        helpTest(option, "OPTION NOCACHE");
    }

    @Test
    public void testOrderBy1() {
        OrderBy ob = getFactory().newOrderBy();
        ob.addVariable(getFactory().newElementSymbol("e1"));

        helpTest(ob, "ORDER BY e1");
    }

    @Test
    public void testOrderBy2() {
        OrderBy ob = getFactory().newOrderBy();
        ob.addVariable(getFactory().newElementSymbol("e1"));
        ob.addVariable(getFactory().newAliasSymbol("x", getFactory().newElementSymbol("e2")));

        helpTest(ob, "ORDER BY e1, x");
    }

    @Test
    public void testOrderBy3() {
        OrderBy ob = getFactory().newOrderBy();
        ob.addVariable(getFactory().newElementSymbol("e1"), IOrderBy.DESC);
        ob.addVariable(getFactory().newElementSymbol("x"), IOrderBy.DESC);

        helpTest(ob, "ORDER BY e1 DESC, x DESC");
    }

    @Test
    public void testQuery1() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("m.g"));
        Query query = getFactory().newQuery();
        query.setSelect(select);
        query.setFrom(from);

        helpTest(query, "SELECT * FROM m.g");
    }

    @Test
    public void testQuery2() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("m.g"));
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, getFactory().newConstant(new Integer(5)));
        GroupBy groupBy = getFactory().newGroupBy();
        groupBy.addSymbol(getFactory().newElementSymbol("e1"));
        CompareCriteria having = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.GT, getFactory().newConstant(new Integer(0)));
        OrderBy orderBy = getFactory().newOrderBy();
        orderBy.addVariable(getFactory().newElementSymbol("e1"));

        Query query = getFactory().newQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT * FROM m.g WHERE e1 = 5 GROUP BY e1 HAVING e1 > 0 ORDER BY e1");
    }

    @Test
    public void testQuery3() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("m.g"));
        GroupBy groupBy = getFactory().newGroupBy();
        groupBy.addSymbol(getFactory().newElementSymbol("e1"));
        CompareCriteria having = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.GT, getFactory().newConstant(new Integer(0)));
        OrderBy orderBy = getFactory().newOrderBy();
        orderBy.addVariable(getFactory().newElementSymbol("e1"));

        Query query = getFactory().newQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT * FROM m.g GROUP BY e1 HAVING e1 > 0 ORDER BY e1");
    }

    @Test
    public void testQuery4() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("m.g"));
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, getFactory().newConstant(new Integer(5)));
        CompareCriteria having = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.GT, getFactory().newConstant(new Integer(0)));
        OrderBy orderBy = getFactory().newOrderBy();
        orderBy.addVariable(getFactory().newElementSymbol("e1"));

        Query query = getFactory().newQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT * FROM m.g WHERE e1 = 5 HAVING e1 > 0 ORDER BY e1");
    }

    @Test
    public void testQuery5() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("m.g"));
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, getFactory().newConstant(new Integer(5)));
        GroupBy groupBy = getFactory().newGroupBy();
        groupBy.addSymbol(getFactory().newElementSymbol("e1"));
        OrderBy orderBy = getFactory().newOrderBy();
        orderBy.addVariable(getFactory().newElementSymbol("e1"));

        Query query = getFactory().newQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT * FROM m.g WHERE e1 = 5 GROUP BY e1 ORDER BY e1");
    }

    @Test
    public void testQuery6() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("m.g"));
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, getFactory().newConstant(new Integer(5)));
        GroupBy groupBy = getFactory().newGroupBy();
        groupBy.addSymbol(getFactory().newElementSymbol("e1"));
        CompareCriteria having = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.GT, getFactory().newConstant(new Integer(0)));

        Query query = getFactory().newQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);

        helpTest(query, "SELECT * FROM m.g WHERE e1 = 5 GROUP BY e1 HAVING e1 > 0");
    }

    @Test
    public void testQuery7() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newMultipleElementSymbol());
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("m.g"));
        CompareCriteria cc = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.EQ, getFactory().newConstant(new Integer(5)));
        GroupBy groupBy = getFactory().newGroupBy();
        groupBy.addSymbol(getFactory().newElementSymbol("e1"));
        CompareCriteria having = getFactory().newCompareCriteria(getFactory().newElementSymbol("e1"), Operator.GT, getFactory().newConstant(new Integer(0)));
        OrderBy orderBy = getFactory().newOrderBy();
        orderBy.addVariable(getFactory().newElementSymbol("e1"));

        Query query = getFactory().newQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT * FROM m.g WHERE e1 = 5 GROUP BY e1 HAVING e1 > 0 ORDER BY e1");
    }

    @Test
    public void testSelect1() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("e1"));

        helpTest(select, "e1");
    }

    @Test
    public void testSelect2() {
        Select select = getFactory().newSelect();
        select.setDistinct(true);
        select.addSymbol(getFactory().newElementSymbol("e1"));

        helpTest(select, "DISTINCT e1");
    }

    @Test
    public void testSelect3() {
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("e1"));
        select.addSymbol(getFactory().newElementSymbol("e2"));

        helpTest(select, "e1, e2");
    }

    @Test
    public void testSetCriteria1() {
        SetCriteria sc = getFactory().newNode(ASTNodes.SET_CRITERIA);
        sc.setExpression(getFactory().newElementSymbol("e1"));
        sc.setValues(new ArrayList<Expression>());

        helpTest(sc, "e1 IN ()");
    }

    @Test
    public void testSetCriteria2() {
        SetCriteria sc = getFactory().newNode(ASTNodes.SET_CRITERIA);
        sc.setExpression(getFactory().newElementSymbol("e1"));
        ArrayList<Expression> values = new ArrayList<Expression>();
        values.add(getFactory().newElementSymbol("e2"));
        values.add(getFactory().newConstant("abc"));
        sc.setValues(values);

        helpTest(sc, "e1 IN (e2, 'abc')");
    }

    @Test
    public void testSetCriteria3() {
        SetCriteria sc = getFactory().newNode(ASTNodes.SET_CRITERIA);
        sc.setExpression(getFactory().newElementSymbol("e1"));
        ArrayList<Expression> values = new ArrayList<Expression>();
        values.add(null);
        values.add(getFactory().newConstant("b"));
        sc.setValues(values);

        helpTest(sc, "e1 IN (<undefined>, 'b')");
    }

    @Test
    public void testSetCriteria4() {
        SetCriteria sc = getFactory().newNode(ASTNodes.SET_CRITERIA);
        sc.setExpression(getFactory().newElementSymbol("e1"));
        ArrayList<Expression> values = new ArrayList<Expression>();
        values.add(getFactory().newElementSymbol("e2"));
        values.add(getFactory().newConstant("abc"));
        sc.setValues(values);
        sc.setNegated(true);
        helpTest(sc, "e1 NOT IN (e2, 'abc')");
    }

    @Test
    public void testSetQuery1() {
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
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        SetQuery sq = getFactory().newSetQuery(q1, Operation.UNION, q2, false);

        helpTest(sq, "SELECT e1 FROM m.g1 UNION SELECT e1 FROM m.g2");
    }

    @Test
    public void testSetQuery2() {
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
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        SetQuery sq = getFactory().newSetQuery(q1, Operation.UNION, q2, true);

        helpTest(sq, "SELECT e1 FROM m.g1 UNION ALL SELECT e1 FROM m.g2");
    }

    @Test
    public void testSetQuery3() {
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
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        OrderBy orderBy = getFactory().newOrderBy();
        orderBy.addVariable(getFactory().newElementSymbol("e1"));

        SetQuery sq = getFactory().newSetQuery(q1, Operation.UNION, q2, false);
        sq.setOrderBy(orderBy);

        helpTest(sq, "SELECT e1 FROM m.g1 UNION SELECT e1 FROM m.g2 ORDER BY e1");
    }

    @Test
    public void testSetQuery4() {
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
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        SetQuery sq = getFactory().newSetQuery(q1, Operation.UNION, q2, false);

        helpTest(sq, "SELECT e1 FROM m.g1 UNION SELECT e1 FROM m.g2");
    }

    @Test
    public void testSetQuery5() {
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
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        Select s3 = getFactory().newSelect();
        s3.addSymbol(getFactory().newElementSymbol("e3"));
        From f3 = getFactory().newFrom();
        f3.addGroup(getFactory().newGroupSymbol("m.g3"));
        Query q3 = getFactory().newQuery();
        q3.setSelect(s3);
        q3.setFrom(f3);

        SetQuery sq = getFactory().newSetQuery(q1, Operation.UNION, q2, false);

        SetQuery sq2 = getFactory().newSetQuery(q3, Operation.UNION, sq, true);

        helpTest(sq2, "SELECT e3 FROM m.g3 UNION ALL (SELECT e1 FROM m.g1 UNION SELECT e1 FROM m.g2)");
    }

    @Test
    public void testSubqueryFromClause1() {
        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("temp", q1);
        helpTest(sfc, "(SELECT e1 FROM m.g1) AS temp");
    }

    @Test
    public void testOptionalSubqueryFromClause1() {
        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        SubqueryFromClause sfc = getFactory().newSubqueryFromClause("temp", q1);
        sfc.setOptional(true);
        helpTest(sfc, "/*+ optional */ (SELECT e1 FROM m.g1) AS temp");
    }

    @Test
    public void testSubquerySetCriteria1() {
        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ElementSymbol expr = getFactory().newElementSymbol("e2");

        SubquerySetCriteria ssc = getFactory().newSubquerySetCriteria(expr, q1);
        helpTest(ssc, "e2 IN (SELECT e1 FROM m.g1)");
    }

    @Test
    public void testSubquerySetCriteria2() {
        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ElementSymbol expr = getFactory().newElementSymbol("e2");

        SubquerySetCriteria ssc = getFactory().newSubquerySetCriteria(expr, q1);
        ssc.setNegated(true);
        helpTest(ssc, "e2 NOT IN (SELECT e1 FROM m.g1)");
    }

    @Test
    public void testUnaryFromClause() {
        helpTest(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g1")), "m.g1");
    }

    @Test
    public void testOptionalUnaryFromClause() {
        UnaryFromClause unaryFromClause = getFactory().newUnaryFromClause(getFactory().newGroupSymbol("m.g1"));//$NON-NLS-1$
        unaryFromClause.setOptional(true);
        helpTest(unaryFromClause, "/*+ optional */ m.g1"); 
    }

    @Test
    public void testUpdate1() {
        Update update = getFactory().newUpdate();
        update.setGroup(getFactory().newGroupSymbol("m.g1"));
        update.addChange(getFactory().newElementSymbol("e1"), getFactory().newConstant("abc"));

        helpTest(update, "UPDATE m.g1 SET e1 = 'abc'");
    }

    @Test
    public void testUpdate2() {
        Update update = getFactory().newUpdate();
        update.setGroup(getFactory().newGroupSymbol("m.g1"));
        update.addChange(getFactory().newElementSymbol("e1"), getFactory().newConstant("abc"));
        update.addChange(getFactory().newElementSymbol("e2"), getFactory().newConstant("xyz"));

        helpTest(update, "UPDATE m.g1 SET e1 = 'abc', e2 = 'xyz'");
    }

    @Test
    public void testUpdate3() {
        Update update = getFactory().newUpdate();
        update.setGroup(getFactory().newGroupSymbol("m.g1"));
        update.addChange(getFactory().newElementSymbol("e1"), getFactory().newConstant("abc"));
        update.setCriteria(getFactory().newCompareCriteria(getFactory().newElementSymbol("e2"),
                                                           Operator.EQ,
                                                           getFactory().newConstant("abc")));

        helpTest(update, "UPDATE m.g1 SET e1 = 'abc' WHERE e2 = 'abc'");
    }

    @Test
    public void testAliasSymbol1() {
        AliasSymbol as = getFactory().newAliasSymbol("x", getFactory().newElementSymbol("y"));
        helpTest(as, "y AS x");
    }

    // Test alias symbol with reserved word 
    @Test
    public void testAliasSymbol2() {
        AliasSymbol as = getFactory().newAliasSymbol("select", getFactory().newElementSymbol("y"));
        helpTest(as, "y AS \"select\"");
    }

    @Test
    public void testAllSymbol() {
        helpTest(getFactory().newMultipleElementSymbol(), "*");
    }

    @Test
    public void testAllInGroupSymbol() {
        helpTest(getFactory().newMultipleElementSymbol("m.g"), "m.g.*");
    }

    @Test
    public void testConstantNull() {
        helpTest(getFactory().newConstant(null), "null");
    }

    @Test
    public void testConstantString() {
        helpTest(getFactory().newConstant("abc"), "'abc'");
    }

    @Test
    public void testConstantInteger() {
        helpTest(getFactory().newConstant(new Integer(5)), "5");
    }

    @Test
    public void testConstantBigDecimal() {
        helpTest(getFactory().newConstant(new BigDecimal("5.4")), "5.4");
    }

    @Test
    public void testConstantStringWithTick() {
        helpTest(getFactory().newConstant("O'Leary"), "'O''Leary'");
    }

    @Test
    public void testConstantStringWithTicks() {
        helpTest(getFactory().newConstant("'abc'"), "'''abc'''");
    }

    @Test
    public void testConstantStringWithMoreTicks() {
        helpTest(getFactory().newConstant("a'b'c"), "'a''b''c'");
    }

    @Test
    public void testConstantStringWithDoubleTick() {
        helpTest(getFactory().newConstant("group=\"x\""), "'group=\"x\"'");
    }

    @Test
    public void testConstantBooleanTrue() {
        helpTest(getFactory().newConstant(Boolean.TRUE), "TRUE");
    }

    @Test
    public void testConstantBooleanFalse() {
        helpTest(getFactory().newConstant(Boolean.FALSE), "FALSE");
    }

    @Test
    public void testConstantDate() {
        helpTest(getFactory().newConstant(java.sql.Date.valueOf("2002-10-02")), "{d'2002-10-02'}");
    }

    @Test
    public void testConstantTime() {
        helpTest(getFactory().newConstant(java.sql.Time.valueOf("5:00:00")), "{t'05:00:00'}");
    }

    @Test
    public void testConstantTimestamp() {
        helpTest(getFactory().newConstant(java.sql.Timestamp.valueOf("2002-10-02 17:10:35.0234")), "{ts'2002-10-02 17:10:35.0234'}");
    }

    @Test
    public void testElementSymbol1() {
        ElementSymbol es = getFactory().newElementSymbol("elem");
        helpTest(es, "elem");
    }

    @Test
    public void testElementSymbol2() {
        ElementSymbol es = getFactory().newElementSymbol("elem");
        es.setDisplayFullyQualified(false);
        es.setGroupSymbol(getFactory().newGroupSymbol("m.g"));
        helpTest(es, "elem");
    }

    @Test
    public void testElementSymbol3() {
        ElementSymbol es = getFactory().newElementSymbol("m.g.elem");
        es.setDisplayFullyQualified(true);
        es.setGroupSymbol(getFactory().newGroupSymbol("m.g"));
        helpTest(es, "m.g.elem");
    }

    @Test
    public void testElementSymbol4() {
        ElementSymbol es = getFactory().newElementSymbol("vdb.m.g.elem");
        es.setDisplayFullyQualified(true);
        helpTest(es, "vdb.m.g.elem");
    }

    @Test
    public void testElementSymbol5() {
        ElementSymbol es = getFactory().newElementSymbol("m.g.select");
        es.setDisplayFullyQualified(false);
        es.setGroupSymbol(getFactory().newGroupSymbol("m.g"));
        helpTest(es, "\"select\"");
    }

    @Test
    public void testExpressionSymbol1() {
        Expression expr = getFactory().wrapExpression(getFactory().newConstant("abc"), "abc");
        helpTest(expr, "'abc'");
    }

    @Test public void testFunction1() {
            Function func = getFactory().newFunction("concat", new Expression[] {
                getFactory().newConstant("a"), null    
            });
            helpTest(func, "concat('a', <undefined>)");
        }

    @Test public void testFunction2() {
            Function func = getFactory().newFunction("now", new Expression[] {});
            helpTest(func, "now()");
        }

    @Test public void testFunction3() {
            Function func = getFactory().newFunction("concat", new Expression[] {null, null});
            helpTest(func, "concat(<undefined>, <undefined>)");
        }

    @Test public void testFunction4() {
            Function func1 = getFactory().newFunction("power", new Expression[] {
                getFactory().newConstant(new Integer(5)), 
                getFactory().newConstant(new Integer(3)) });
            Function func2 = getFactory().newFunction("power", new Expression[] {
                func1, 
                getFactory().newConstant(new Integer(3)) });            
            Function func3 = getFactory().newFunction("+", new Expression[] {
                getFactory().newConstant(new Integer(1000)),
                func2 });
            helpTest(func3, "(1000 + power(power(5, 3), 3))");
        }

    @Test public void testFunction5() {
            Function func1 = getFactory().newFunction("concat", new Expression[] {
                getFactory().newElementSymbol("elem2"),
                null });
            Function func2 = getFactory().newFunction("concat", new Expression[] {
                getFactory().newElementSymbol("elem1"),
                func1 });            
            helpTest(func2, "concat(elem1, concat(elem2, <undefined>))");
        }

    @Test public void testConvertFunction1() {
            Function func = getFactory().newFunction("convert", new Expression[] {
                getFactory().newConstant("5"), 
                getFactory().newConstant("integer")    
            });
            helpTest(func, "convert('5', integer)");
        }

    @Test public void testConvertFunction2() {
            Function func = getFactory().newFunction("convert", new Expression[] {
                null, 
                getFactory().newConstant("integer")    
            });
            helpTest(func, "convert(<undefined>, integer)");
        }

    @Test public void testConvertFunction3() {
            Function func = getFactory().newFunction("convert", new Expression[] {
                getFactory().newConstant(null), 
                getFactory().newConstant("integer")    
            });
            helpTest(func, "convert(null, integer)");
        }

    @Test public void testConvertFunction4() {
            Function func = getFactory().newFunction("convert", new Expression[] {
                getFactory().newConstant("abc"), 
                null    
            });
            helpTest(func, "convert('abc', <undefined>)");
        }

    @Test
    public void testConvertFunction5() {
        Function func = getFactory().newFunction("convert");
        helpTest(func, "convert()");
    }

    @Test
    public void testConvertFunction6() {
        Function func = getFactory().newFunction("convert", new Expression[0]);
        helpTest(func, "convert()");
    }

    @Test public void testConvertFunction7() {
            Function func = getFactory().newFunction("convert", new Expression[] {getFactory().newConstant("abc")});
            helpTest(func, "convert('abc', <undefined>)");
        }

    @Test public void testCastFunction1() {
            Function func = getFactory().newFunction("cast", new Expression[] {
                getFactory().newConstant("5"), 
                getFactory().newConstant("integer")    
            });
            helpTest(func, "cast('5' AS integer)");
        }

    @Test public void testCastFunction2() {
            Function func = getFactory().newFunction("cast", new Expression[] {
                null, 
                getFactory().newConstant("integer")    
            });
            helpTest(func, "cast(<undefined> AS integer)");
        }

    @Test public void testCastFunction3() {
            Function func = getFactory().newFunction("cast", new Expression[] {
                getFactory().newConstant(null), 
                getFactory().newConstant("integer")    
            });
            helpTest(func, "cast(null AS integer)");
        }

    @Test public void testCastFunction4() {
            Function func = getFactory().newFunction("cast", new Expression[] {
                getFactory().newConstant("abc"), 
                null    
            });
            helpTest(func, "cast('abc' AS <undefined>)");
        }

    @Test public void testArithemeticFunction1() { 
            Function func = getFactory().newFunction("-", new Expression[] { 
                getFactory().newConstant(new Integer(-2)),
                getFactory().newConstant(new Integer(-1))});
            helpTest(func, "(-2 - -1)");    
        }

    @Test
    public void testGroupSymbol1() {
        GroupSymbol gs = getFactory().newGroupSymbol("g");
        helpTest(gs, "g");
    }

    @Test
    public void testGroupSymbol2() {
        GroupSymbol gs = getFactory().newGroupSymbol("x", "g");
        helpTest(gs, "g AS x");
    }

    @Test
    public void testGroupSymbol3() {
        GroupSymbol gs = getFactory().newGroupSymbol("vdb.g");
        helpTest(gs, "vdb.g");
    }

    @Test
    public void testGroupSymbol4() {
        GroupSymbol gs = getFactory().newGroupSymbol("x", "vdb.g");
        helpTest(gs, "vdb.g AS x");
    }

    @Test
    public void testGroupSymbol5() {
        GroupSymbol gs = getFactory().newGroupSymbol("from", "m.g");
        helpTest(gs, "m.g AS \"from\"");
    }

    @Test
    public void testGroupSymbol6() {
        GroupSymbol gs = getFactory().newGroupSymbol("x", "on.select");
        helpTest(gs, "\"on\".\"select\" AS x");
    }

    @Test
    public void testExecNoParams() {
        StoredProcedure proc = getFactory().newStoredProcedure();
        proc.setProcedureName("myproc");
        helpTest(proc, "EXEC myproc()");
    }

    @Test
    public void testExecInputParam() {
        StoredProcedure proc = getFactory().newStoredProcedure();
        proc.setProcedureName("myproc");
        SPParameter param = getFactory().newSPParameter(1, getFactory().newReference(0));
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc(?)");
    }

    @Test
    public void testExecInputOutputParam() {
        StoredProcedure proc = getFactory().newStoredProcedure();
        proc.setProcedureName("myproc");
        SPParameter param1 = getFactory().newSPParameter(1, getFactory().newConstant(new Integer(5)));
        param1.setParameterType(ISPParameter.ParameterInfo.IN);
        proc.setParameter(param1);

        SPParameter param2 = getFactory().newSPParameter(2, ISPParameter.ParameterInfo.OUT, "x");
        proc.setParameter(param2);

        helpTest(proc, "EXEC myproc(5)");
    }

    @Test
    public void testExecOutputInputParam() {
        StoredProcedure proc = getFactory().newStoredProcedure();
        proc.setProcedureName("myproc");

        SPParameter param2 = getFactory().newSPParameter(2, ISPParameter.ParameterInfo.OUT, "x");
        proc.setParameter(param2);

        SPParameter param1 = getFactory().newSPParameter(1, getFactory().newConstant(new Integer(5)));
        param1.setParameterType(ISPParameter.ParameterInfo.IN);
        proc.setParameter(param1);

        helpTest(proc, "EXEC myproc(5)");
    }

    @Test
    public void testExecReturnParam() {
        StoredProcedure proc = getFactory().newStoredProcedure();
        proc.setProcedureName("myproc");

        SPParameter param = getFactory().newSPParameter(1, ISPParameter.ParameterInfo.RETURN_VALUE, "ret");
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc()");
    }

    @Test
    public void testExecNamedParam() {
        StoredProcedure proc = getFactory().newStoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc");
        SPParameter param = getFactory().newSPParameter(1, getFactory().newReference(0));
        param.setName("p1");//$NON-NLS-1$
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc(p1 => ?)");
    }

    @Test
    public void testExecNamedParams() {
        StoredProcedure proc = getFactory().newStoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc");
        SPParameter param = getFactory().newSPParameter(1, getFactory().newReference(0));
        param.setName("p1");//$NON-NLS-1$
        proc.setParameter(param);
        SPParameter param2 = getFactory().newSPParameter(2, getFactory().newReference(0));
        param2.setName("p2");//$NON-NLS-1$
        proc.setParameter(param2);
        helpTest(proc, "EXEC myproc(p1 => ?, p2 => ?)");
    }

    /**
     * Test when a parameter's name is a reserved word.
     * (Note: parameters should always have short names, not
     * multiple period-delimited name components.) 
     * 
     * @since 4.3
     */
    @Test
    public void testExecNamedParamsReservedWord() {
        StoredProcedure proc = getFactory().newStoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc");
        SPParameter param = getFactory().newSPParameter(1, getFactory().newReference(0));
        param.setName("in");//$NON-NLS-1$
        proc.setParameter(param);
        SPParameter param2 = getFactory().newSPParameter(2, getFactory().newReference(0));
        param2.setName("in2");//$NON-NLS-1$
        proc.setParameter(param2);
        helpTest(proc, "EXEC myproc(\"in\" => ?, in2 => ?)");
    }

    // Test methods for Update Procedure Language Objects

    @Test
    public void testDeclareStatement() {
        DeclareStatement dclStmt = getFactory().newDeclareStatement(getFactory().newElementSymbol("a"), "String");
        helpTest(dclStmt, "DECLARE String a;");
    }

    @Test
    public void testAssignmentStatement1() {
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1)));
        helpTest(assigStmt, "a = 1;");
    }

    @Test
    public void testAssignmentStatement2() {
        Query q1 = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("x"));
        q1.setSelect(select);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));
        q1.setFrom(from);

        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), q1);
        helpTest(assigStmt, "a = (SELECT x FROM g);");
    }

    @Test
    public void testCommandStatement1() {
        Query q1 = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("x"));
        q1.setSelect(select);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));
        q1.setFrom(from);

        CommandStatement cmdStmt = getFactory().newCommandStatement(q1);
        helpTest(cmdStmt, "SELECT x FROM g;");
    }

    @Test
    public void testCommandStatement2() {
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g"));
        CommandStatement cmdStmt = getFactory().newCommandStatement(d1);
        helpTest(cmdStmt, "DELETE FROM g;");
    }

    @Test
    public void testSubqueryCompareCriteria1() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ElementSymbol expr = getFactory().newElementSymbol("e2");

        SubqueryCompareCriteria scc = getFactory().newSubqueryCompareCriteria(expr,
                                                                              q1,
                                                                              Operator.EQ,
                                                                              PredicateQuantifier.ANY);

        helpTest(scc, "e2 = ANY (SELECT e1 FROM m.g1)");
    }

    @Test
    public void testSubqueryCompareCriteria2() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ElementSymbol expr = getFactory().newElementSymbol("e2");

        SubqueryCompareCriteria scc = getFactory().newSubqueryCompareCriteria(expr,
                                                                              q1,
                                                                              Operator.LE,
                                                                              PredicateQuantifier.SOME);

        helpTest(scc, "e2 <= SOME (SELECT e1 FROM m.g1)");
    }

    @Test
    public void testExistsCriteria1() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ExistsCriteria ec = getFactory().newExistsCriteria(q1);

        helpTest(ec, "EXISTS (SELECT e1 FROM m.g1)");
    }

    @Test
    public void testDynamicCommand() {
        List<ElementSymbol> symbols = new ArrayList<ElementSymbol>();

        ElementSymbol a1 = getFactory().newElementSymbol("a1");
        a1.setType(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());
        symbols.add(a1);

        DynamicCommand obj = getFactory().newDynamicCommand();
        Expression sql = getFactory().newConstant("SELECT a1 FROM g WHERE a2 = 5");

        obj.setSql(sql);
        obj.setAsColumns(symbols);
        obj.setAsClauseSet(true);
        obj.setIntoGroup(getFactory().newGroupSymbol("#g"));

        helpTest(obj, "EXECUTE IMMEDIATE 'SELECT a1 FROM g WHERE a2 = 5' AS a1 string INTO #g");
    }

    @Test
    public void testScalarSubquery() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ScalarSubquery obj = getFactory().newScalarSubquery(q1);

        helpTest(obj, "(SELECT e1 FROM m.g1)");
    }

    @Test
    public void testNewSubqueryObjects() {

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newElementSymbol("e1"));
        From f1 = getFactory().newFrom();
        f1.addGroup(getFactory().newGroupSymbol("m.g1"));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newElementSymbol("e1"));
        s2.addSymbol(getFactory().wrapExpression(getFactory().newScalarSubquery(q1), "blargh"));
        From f2 = getFactory().newFrom();
        f2.addGroup(getFactory().newGroupSymbol("m.g2"));
        Criteria left = getFactory().newSubqueryCompareCriteria(getFactory().newElementSymbol("e3"), q1, Operator.GE, PredicateQuantifier.ANY);
        Criteria right = getFactory().newExistsCriteria(q1);
        Criteria outer = getFactory().newCompoundCriteria(CompoundCriteria.AND, left, right);
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);
        q2.setCriteria(outer);

        helpTest(q2,
                 "SELECT e1, (SELECT e1 FROM m.g1) FROM m.g2 WHERE (e3 >= ANY (SELECT e1 FROM m.g1)) AND (EXISTS (SELECT e1 FROM m.g1))");
    }

    @Test
    public void testCaseExpression1() {
        helpTest(example(2), "CASE x WHEN 'a' THEN 0 WHEN 'b' THEN 1 ELSE 9999 END");
    }

    @Test
    public void testCaseExpression2() {
        CaseExpression example = example(2);
        example.setElseExpression(null);
        helpTest(example, "CASE x WHEN 'a' THEN 0 WHEN 'b' THEN 1 END");
    }

    @Test
    public void testCaseExpression3() {
        CaseExpression example = caseExample(3, 0, true);
        helpTest(example, "CASE x WHEN null THEN 0 WHEN 'b' THEN 1 WHEN 'c' THEN 2 ELSE 9999 END");
    }

    @Test
    public void testCaseExpression4() {
        CaseExpression example = caseExample(3, 2, true);
        example.setElseExpression(null);
        helpTest(example, "CASE x WHEN 'a' THEN 0 WHEN 'b' THEN 1 WHEN null THEN 2 END");
    }

    @Test
    public void testSearchedCaseExpression1() {
        helpTest(searchedCaseExample(2), "CASE WHEN x = 0 THEN 0 WHEN x = 1 THEN 1 ELSE 9999 END");

    }

    @Test
    public void testSearchedCaseExpression2() {
        SearchedCaseExpression example = searchedCaseExample(2);
        example.setElseExpression(null);
        helpTest(example, "CASE WHEN x = 0 THEN 0 WHEN x = 1 THEN 1 END");

    }

    /**  
     * For some reason this test was outputting
     * SELECT 'A' AS FOO UNION SELECT 'A' AS FOO
     */
    @Test
    public void testSetQueryUnionOfLiteralsCase3102() {

        String expected = "SELECT 'A' AS FOO UNION SELECT 'B' AS FOO";

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newAliasSymbol("FOO", getFactory().wrapExpression(getFactory().newConstant("A"), "xxx")));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newAliasSymbol("FOO", getFactory().wrapExpression(getFactory().newConstant("B"), "xxx")));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);

        SetQuery sq = getFactory().newSetQuery(q1, Operation.UNION, q2, false);

        helpTest(sq, expected);
    }

    /**  
     * For some reason this test was outputting
     * SELECT 'A' AS FOO UNION SELECT 'A' AS FOO
     * Same as above except that ExpressionSymbols' internal names (which aren't visible
     * in the query) are different
     */
    @Test
    public void testSetQueryUnionOfLiteralsCase3102a() {

        String expected = "SELECT 'A' AS FOO UNION SELECT 'B' AS FOO";

        Select s1 = getFactory().newSelect();
        s1.addSymbol(getFactory().newAliasSymbol("FOO", getFactory().wrapExpression(getFactory().newConstant("A"), "xxx")));
        Query q1 = getFactory().newQuery();
        q1.setSelect(s1);

        Select s2 = getFactory().newSelect();
        s2.addSymbol(getFactory().newAliasSymbol("FOO", getFactory().wrapExpression(getFactory().newConstant("B"), "yyy")));
        Query q2 = getFactory().newQuery();
        q2.setSelect(s2);

        SetQuery sq = getFactory().newSetQuery(q1, Operation.UNION, q2, false);

        helpTest(sq, expected);
    }

    @Test
    public void testLimit() {
        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect(Arrays.asList(getFactory().newMultipleElementSymbol()));
        From from = getFactory().newFrom(Arrays.asList(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("a"))));
        query.setSelect(select);
        query.setFrom(from);
        
        Limit limit = getFactory().newNode(ASTNodes.LIMIT);
        limit.setRowLimit(getFactory().newConstant(new Integer(100)));
        query.setLimit(limit);
        helpTest(query, "SELECT * FROM a LIMIT 100");
    }

    @Test
    public void testLimitWithOffset() {
        Query query = getFactory().newQuery();
        Select select = getFactory().newSelect(Arrays.asList(getFactory().newMultipleElementSymbol()));
        From from = getFactory().newFrom(Arrays.asList(getFactory().newUnaryFromClause(getFactory().newGroupSymbol("a"))));
        query.setSelect(select);
        query.setFrom(from);

        Limit limit = getFactory().newNode(ASTNodes.LIMIT);
        limit.setOffset(getFactory().newConstant(new Integer(50)));
        limit.setRowLimit(getFactory().newConstant(new Integer(100)));
        query.setLimit(limit);
        helpTest(query, "SELECT * FROM a LIMIT 50, 100"); 
    }

    @Test
    public void testUnionOrderBy() throws Exception {
        Command command = parser.parseCommand("select pm1.g1.e1 from pm1.g1 union select e2 from pm1.g2 order by e1");
        QueryResolver queryResolver = new QueryResolver(parser);
        queryResolver.resolveCommand(command, metadataFactory.example1Cached());
        helpTest(command, "SELECT pm1.g1.e1 FROM pm1.g1 UNION SELECT e2 FROM pm1.g2 ORDER BY e1");
    }

    @Test
    public void testUnionBranchOrderBy() throws Exception {
        Command command = parser.parseCommand("select pm1.g1.e1 from pm1.g1 union (select e2 from pm1.g2 order by e1)");
        QueryResolver queryResolver = new QueryResolver(parser);
        queryResolver.resolveCommand(command, metadataFactory.example1Cached());
        helpTest(command, "SELECT pm1.g1.e1 FROM pm1.g1 UNION (SELECT e2 FROM pm1.g2 ORDER BY e1)");
    }

    @Test
    public void testAliasedOrderBy() throws Exception {
        Command command = parser.parseCommand("select pm1.g1.e1 as a from pm1.g1 order by a");
        QueryResolver queryResolver = new QueryResolver(parser);
        queryResolver.resolveCommand(command, metadataFactory.example1Cached());
        helpTest(command, "SELECT pm1.g1.e1 AS a FROM pm1.g1 ORDER BY a");
    }

    @Test
    public void testNumberOrderBy() throws Exception {
        Command command = parser.parseCommand("select pm1.g1.e1 as a from pm1.g1 order by 1");
        QueryResolver queryResolver = new QueryResolver(parser);
        queryResolver.resolveCommand(command, metadataFactory.example1Cached());
        helpTest(command, "SELECT pm1.g1.e1 AS a FROM pm1.g1 ORDER BY 1");
    }

    @Test
    public void testLikeRegex() throws Exception {
        helpTestExpression("x like_regex 'b'", "x LIKE_REGEX 'b'");
    }

    @Test
    public void testSimilar() throws Exception {
        helpTestExpression("x similar to 'b' escape 'c'", "x SIMILAR TO 'b' ESCAPE 'c'");
    }

    @Test
    public void testTextTable() throws Exception {
        String sql = "SELECT * from texttable(file columns x string WIDTH 1 NO TRIM NO ROW DELIMITER) as x";
        helpTest(parser.parseCommand(sql),
                 "SELECT * FROM TEXTTABLE(file COLUMNS x string WIDTH 1 NO TRIM NO ROW DELIMITER) AS x");
    }

}
