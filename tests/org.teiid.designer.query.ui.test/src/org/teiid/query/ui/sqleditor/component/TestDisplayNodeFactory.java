/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.sqleditor.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.teiid.client.metadata.ParameterInfo;
import org.teiid.core.types.DataTypeManager;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.AbstractCompareCriteria;
import org.teiid.query.sql.lang.BetweenCriteria;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.DynamicCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.lang.JoinPredicate;
import org.teiid.query.sql.lang.JoinType;
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
import org.teiid.query.sql.lang.SetQuery.Operation;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;
import org.teiid.query.sql.lang.SubqueryFromClause;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.lang.Update;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.DeclareStatement;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;
import org.teiid.query.sql.symbol.Reference;
import org.teiid.query.sql.symbol.ScalarSubquery;


public class TestDisplayNodeFactory extends TestCase {

    // ################################## FRAMEWORK ################################

    public TestDisplayNodeFactory( String name ) {
        super(name);
    }

    // ################################## TEST HELPERS ################################

    private void helpTest( LanguageObject obj,
                           String expectedStr ) {
        DisplayNode displayNode = DisplayNodeFactory.createDisplayNode(null, obj);

        String actualStr = displayNode.toString();
        assertEquals("Expected and actual strings don't match: ", expectedStr, actualStr); //$NON-NLS-1$
    }

    // ################################## ACTUAL TESTS ################################

    public void testBetweenCriteria1() {
        BetweenCriteria bc = new BetweenCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 new Constant(new Integer(1000)), new Constant(new Integer(2000)));
        helpTest(bc, "m.g.c1 BETWEEN 1000 AND 2000"); //$NON-NLS-1$
    }

    public void testBetweenCriteria2() {
        BetweenCriteria bc = new BetweenCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 new Constant(new Integer(1000)), new Constant(new Integer(2000)));
        bc.setNegated(true);
        helpTest(bc, "m.g.c1 NOT BETWEEN 1000 AND 2000"); //$NON-NLS-1$
    }

    public void testCompareCriteria1() {
        CompareCriteria cc = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 = 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria2() {
        CompareCriteria cc = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 AbstractCompareCriteria.NE, new Constant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 <> 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria3() {
        CompareCriteria cc = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 AbstractCompareCriteria.GT, new Constant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 > 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria4() {
        CompareCriteria cc = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 AbstractCompareCriteria.GE, new Constant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 >= 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria5() {
        CompareCriteria cc = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 AbstractCompareCriteria.LT, new Constant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 < 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria6() {
        CompareCriteria cc = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 AbstractCompareCriteria.LE, new Constant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 <= 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria7() {
        CompareCriteria cc = new CompareCriteria(null, AbstractCompareCriteria.EQ, null);

        helpTest(cc, "<undefined> = <undefined>"); //$NON-NLS-1$
    }

    public void testCompoundCriteria1() {
        CompareCriteria cc = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        List crits = new ArrayList();
        crits.add(cc);
        CompoundCriteria comp = new CompoundCriteria(CompoundCriteria.AND, crits);

        helpTest(comp, "m.g.c1 = 'abc'"); //$NON-NLS-1$
    }

    public void testCompoundCriteria2() {
        CompareCriteria cc1 = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        CompareCriteria cc2 = new CompareCriteria(new ElementSymbol("m.g.c2"), //$NON-NLS-1$
                                                  AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        List crits = new ArrayList();
        crits.add(cc1);
        crits.add(cc2);
        CompoundCriteria comp = new CompoundCriteria(CompoundCriteria.AND, crits);

        helpTest(comp, "(m.g.c1 = 'abc') AND (m.g.c2 = 'abc')"); //$NON-NLS-1$
    }

    public void testCompoundCriteria3() {
        CompareCriteria cc1 = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        CompareCriteria cc2 = new CompareCriteria(new ElementSymbol("m.g.c2"), //$NON-NLS-1$
                                                  AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        CompareCriteria cc3 = new CompareCriteria(new ElementSymbol("m.g.c3"), //$NON-NLS-1$
                                                  AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        List crits = new ArrayList();
        crits.add(cc1);
        crits.add(cc2);
        crits.add(cc3);
        CompoundCriteria comp = new CompoundCriteria(CompoundCriteria.OR, crits);

        helpTest(comp, "(m.g.c1 = 'abc') OR (m.g.c2 = 'abc') OR (m.g.c3 = 'abc')"); //$NON-NLS-1$
    }

    public void testCompoundCriteria4() {
        CompareCriteria cc1 = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        List crits = new ArrayList();
        crits.add(cc1);
        crits.add(null);
        CompoundCriteria comp = new CompoundCriteria(CompoundCriteria.OR, crits);

        helpTest(comp, "(m.g.c1 = 'abc') OR (<undefined>)"); //$NON-NLS-1$
    }

    public void testCompoundCriteria5() {
        CompareCriteria cc1 = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        List crits = new ArrayList();
        crits.add(null);
        crits.add(cc1);
        CompoundCriteria comp = new CompoundCriteria(CompoundCriteria.OR, crits);

        helpTest(comp, "(<undefined>) OR (m.g.c1 = 'abc')"); //$NON-NLS-1$
    }

    public void testCompoundCriteria6() {
        CompareCriteria cc1 = new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  AbstractCompareCriteria.EQ, new Constant("abc")); //$NON-NLS-1$
        List crits = new ArrayList();
        crits.add(cc1);
        crits.add(null);
        CompoundCriteria comp = new CompoundCriteria(CompoundCriteria.OR, crits);

        helpTest(comp, "(m.g.c1 = 'abc') OR (<undefined>)"); //$NON-NLS-1$
    }

    public void testDelete1() {
        Delete delete = new Delete();
        delete.setGroup(new GroupSymbol("m.g")); //$NON-NLS-1$

        helpTest(delete, "DELETE FROM m.g"); //$NON-NLS-1$
    }

    public void testDelete2() {
        Delete delete = new Delete();
        delete.setGroup(new GroupSymbol("m.g")); //$NON-NLS-1$
        delete.setCriteria(new CompareCriteria(new ElementSymbol("m.g.c1"), //$NON-NLS-1$
                                               AbstractCompareCriteria.EQ, new Constant("abc"))); //$NON-NLS-1$

        helpTest(delete, "DELETE FROM m.g\nWHERE\n\tm.g.c1 = 'abc'"); //$NON-NLS-1$
    }

    public void testFrom1() {
        From from = new From();
        from.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        from.addGroup(new GroupSymbol("m.g2")); //$NON-NLS-1$

        helpTest(from, "FROM\n\tm.g1, m.g2"); //$NON-NLS-1$
    }

    public void testFrom2() {
        From from = new From();
        from.addClause(new UnaryFromClause(new GroupSymbol("m.g1"))); //$NON-NLS-1$
        from.addClause(new JoinPredicate(new UnaryFromClause(new GroupSymbol("m.g2")), //$NON-NLS-1$
                                         new UnaryFromClause(new GroupSymbol("m.g3")), //$NON-NLS-1$
                                         JoinType.JOIN_CROSS));

        helpTest(from, "FROM\n\tm.g1, m.g2 CROSS JOIN m.g3"); //$NON-NLS-1$
    }

    public void testGroupBy1() {
        GroupBy gb = new GroupBy();
        gb.addSymbol(new ElementSymbol("m.g.e1")); //$NON-NLS-1$

        helpTest(gb, "GROUP BY m.g.e1"); //$NON-NLS-1$
    }

    public void testGroupBy2() {
        GroupBy gb = new GroupBy();
        gb.addSymbol(new ElementSymbol("m.g.e1")); //$NON-NLS-1$
        gb.addSymbol(new ElementSymbol("m.g.e2")); //$NON-NLS-1$
        gb.addSymbol(new ElementSymbol("m.g.e3")); //$NON-NLS-1$

        helpTest(gb, "GROUP BY m.g.e1, m.g.e2, m.g.e3"); //$NON-NLS-1$
    }

    public void testInsert1() {
        Insert insert = new Insert();
        insert.setGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$

        List vars = new ArrayList();
        vars.add(new ElementSymbol("e1")); //$NON-NLS-1$
        vars.add(new ElementSymbol("e2")); //$NON-NLS-1$
        insert.setVariables(vars);
        List values = new ArrayList();
        values.add(new Constant(new Integer(5)));
        values.add(new Constant("abc")); //$NON-NLS-1$
        insert.setValues(values);

        helpTest(insert, "INSERT INTO m.g1\n\t\t(e1, e2)\n\tVALUES\n\t\t(5, 'abc')"); //$NON-NLS-1$
    }

    public void testIsNullCriteria1() {
        IsNullCriteria inc = new IsNullCriteria();
        inc.setExpression(new Constant("abc")); //$NON-NLS-1$

        helpTest(inc, "'abc' IS NULL"); //$NON-NLS-1$
    }

    public void testIsNullCriteria2() {
        IsNullCriteria inc = new IsNullCriteria();
        inc.setExpression(new ElementSymbol("m.g.e1")); //$NON-NLS-1$

        helpTest(inc, "m.g.e1 IS NULL"); //$NON-NLS-1$
    }

    public void testIsNullCriteria3() {
        IsNullCriteria inc = new IsNullCriteria();
        helpTest(inc, "<undefined> IS NULL"); //$NON-NLS-1$
    }

    public void testIsNullCriteria4() {
        IsNullCriteria inc = new IsNullCriteria();
        inc.setExpression(new ElementSymbol("m.g.e1")); //$NON-NLS-1$
        inc.setNegated(true);
        helpTest(inc, "m.g.e1 IS NOT NULL"); //$NON-NLS-1$
    }

    public void testJoinPredicate1() {
        JoinPredicate jp = new JoinPredicate(new UnaryFromClause(new GroupSymbol("m.g2")), //$NON-NLS-1$
                                             new UnaryFromClause(new GroupSymbol("m.g3")), //$NON-NLS-1$
                                             JoinType.JOIN_CROSS);

        helpTest(jp, "m.g2 CROSS JOIN m.g3"); //$NON-NLS-1$
    }

    public void testOptionalJoinPredicate1() {
        JoinPredicate jp = new JoinPredicate(new UnaryFromClause(new GroupSymbol("m.g2")), //$NON-NLS-1$
                                             new UnaryFromClause(new GroupSymbol("m.g3")), //$NON-NLS-1$
                                             JoinType.JOIN_CROSS);
        jp.setOptional(true);
        helpTest(jp, "/*+ optional */ (m.g2 CROSS JOIN m.g3)"); //$NON-NLS-1$
    }

    public void testJoinPredicate2() {
        ArrayList crits = new ArrayList();
        crits.add(new CompareCriteria(new ElementSymbol("m.g2.e1"), AbstractCompareCriteria.EQ, new ElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
        JoinPredicate jp = new JoinPredicate(new UnaryFromClause(new GroupSymbol("m.g2")), //$NON-NLS-1$
                                             new UnaryFromClause(new GroupSymbol("m.g3")), //$NON-NLS-1$
                                             JoinType.JOIN_INNER, crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1"); //$NON-NLS-1$
    }

    public void testJoinPredicate3() {
        ArrayList crits = new ArrayList();
        crits.add(new CompareCriteria(new ElementSymbol("m.g2.e1"), AbstractCompareCriteria.EQ, new ElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
        crits.add(new CompareCriteria(new ElementSymbol("m.g2.e2"), AbstractCompareCriteria.EQ, new ElementSymbol("m.g3.e2"))); //$NON-NLS-1$ //$NON-NLS-2$
        JoinPredicate jp = new JoinPredicate(new UnaryFromClause(new GroupSymbol("m.g2")), //$NON-NLS-1$
                                             new UnaryFromClause(new GroupSymbol("m.g3")), //$NON-NLS-1$
                                             JoinType.JOIN_INNER, crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1 AND m.g2.e2 = m.g3.e2"); //$NON-NLS-1$
    }

    public void testJoinPredicate4() {
        ArrayList crits = new ArrayList();
        crits.add(new CompareCriteria(new ElementSymbol("m.g2.e1"), AbstractCompareCriteria.EQ, new ElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
        JoinPredicate jp = new JoinPredicate(new UnaryFromClause(new GroupSymbol("m.g2")), //$NON-NLS-1$
                                             new UnaryFromClause(new GroupSymbol("m.g3")), //$NON-NLS-1$
                                             JoinType.JOIN_INNER, crits);

        JoinPredicate jp2 = new JoinPredicate(jp, new UnaryFromClause(new GroupSymbol("m.g1")), //$NON-NLS-1$
                                              JoinType.JOIN_CROSS);

        helpTest(jp2, "(m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1) CROSS JOIN m.g1"); //$NON-NLS-1$
    }

    public void testJoinPredicate5() {
        ArrayList crits = new ArrayList();
        crits.add(new NotCriteria(
                                  new CompareCriteria(
                                                      new ElementSymbol("m.g2.e1"), AbstractCompareCriteria.EQ, new ElementSymbol("m.g3.e1")))); //$NON-NLS-1$ //$NON-NLS-2$
        JoinPredicate jp = new JoinPredicate(new UnaryFromClause(new GroupSymbol("m.g2")), //$NON-NLS-1$
                                             new UnaryFromClause(new GroupSymbol("m.g3")), //$NON-NLS-1$
                                             JoinType.JOIN_INNER, crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON NOT (m.g2.e1 = m.g3.e1)"); //$NON-NLS-1$
    }
    
    public void testJoinPredicate6() {
	    ArrayList crits = new ArrayList();
	    CompareCriteria comprCrit1 = new CompareCriteria(new ElementSymbol("m.g2.e1"), AbstractCompareCriteria.EQ, new ElementSymbol("m.g3.e1")); //$NON-NLS-1$ //$NON-NLS-2$
	    CompareCriteria comprCrit2 = new CompareCriteria(new ElementSymbol("m.g2.e2"), AbstractCompareCriteria.EQ, new ElementSymbol("m.g3.e2")); //$NON-NLS-1$ //$NON-NLS-2$
  		IsNullCriteria inc = new IsNullCriteria();
  		inc.setExpression(new ElementSymbol("m.g.e1")); //$NON-NLS-1$
	    
  		crits.add(inc);
  		crits.add(comprCrit2);
  		
        CompoundCriteria compCrit = new CompoundCriteria(CompoundCriteria.OR, crits);

        ArrayList crits2 = new ArrayList();
        crits2.add(comprCrit1);
        crits2.add(compCrit);
        
        JoinPredicate jp = new JoinPredicate(
    		new UnaryFromClause(new GroupSymbol("m.g2")), //$NON-NLS-1$
    		new UnaryFromClause(new GroupSymbol("m.g3")), //$NON-NLS-1$
    		JoinType.JOIN_LEFT_OUTER,
    		crits2 );
    		
    	helpTest(jp, "m.g2 LEFT OUTER JOIN m.g3 ON m.g2.e1 = m.g3.e1 AND ((m.g.e1 IS NULL) OR (m.g2.e2 = m.g3.e2))"); //$NON-NLS-1$
	}
    public void testJoinType1() {
        helpTest(JoinType.JOIN_CROSS, "CROSS JOIN"); //$NON-NLS-1$
    }

    public void testJoinType2() {
        helpTest(JoinType.JOIN_INNER, "INNER JOIN"); //$NON-NLS-1$
    }

    public void testJoinType3() {
        helpTest(JoinType.JOIN_RIGHT_OUTER, "RIGHT OUTER JOIN"); //$NON-NLS-1$
    }

    public void testJoinType4() {
        helpTest(JoinType.JOIN_LEFT_OUTER, "LEFT OUTER JOIN"); //$NON-NLS-1$
    }

    public void testJoinType5() {
        helpTest(JoinType.JOIN_FULL_OUTER, "FULL OUTER JOIN"); //$NON-NLS-1$
    }

    public void testMatchCriteria1() {
        MatchCriteria mc = new MatchCriteria();
        mc.setLeftExpression(new ElementSymbol("m.g.e1")); //$NON-NLS-1$
        mc.setRightExpression(new Constant("abc")); //$NON-NLS-1$

        helpTest(mc, "m.g.e1 LIKE 'abc'"); //$NON-NLS-1$
    }

    public void testMatchCriteria2() {
        MatchCriteria mc = new MatchCriteria();
        mc.setLeftExpression(new ElementSymbol("m.g.e1")); //$NON-NLS-1$
        mc.setRightExpression(new Constant("%")); //$NON-NLS-1$
        mc.setEscapeChar('#');

        helpTest(mc, "m.g.e1 LIKE '%' ESCAPE '#'"); //$NON-NLS-1$
    }

    public void testMatchCriteria3() {
        MatchCriteria mc = new MatchCriteria();
        mc.setLeftExpression(new ElementSymbol("m.g.e1")); //$NON-NLS-1$
        mc.setRightExpression(new Constant("abc")); //$NON-NLS-1$
        mc.setNegated(true);
        helpTest(mc, "m.g.e1 NOT LIKE 'abc'"); //$NON-NLS-1$
    }

    public void testNotCriteria1() {
        NotCriteria not = new NotCriteria(new IsNullCriteria(new ElementSymbol("m.g.e1"))); //$NON-NLS-1$
        helpTest(not, "NOT (m.g.e1 IS NULL)"); //$NON-NLS-1$
    }

    public void testNotCriteria2() {
        NotCriteria not = new NotCriteria();
        helpTest(not, "NOT (<undefined>)"); //$NON-NLS-1$
    }

    public void testOption1() {
        Option option = new Option();
        helpTest(option, "OPTION"); //$NON-NLS-1$
    }

    public void testOrderBy1() {
        OrderBy ob = new OrderBy();
        ob.addVariable(new ElementSymbol("e1")); //$NON-NLS-1$

        helpTest(ob, "ORDER BY e1"); //$NON-NLS-1$
    }

    public void testOrderBy2() {
        OrderBy ob = new OrderBy();
        ob.addVariable(new ElementSymbol("e1")); //$NON-NLS-1$
        ob.addVariable(new AliasSymbol("x", new ElementSymbol("e2"))); //$NON-NLS-1$ //$NON-NLS-2$

        helpTest(ob, "ORDER BY e1, x"); //$NON-NLS-1$
    }

    public void testOrderBy3() {
        OrderBy ob = new OrderBy();
        ob.addVariable(new ElementSymbol("e1"), OrderBy.DESC); //$NON-NLS-1$
        ob.addVariable(new ElementSymbol("x"), OrderBy.DESC); //$NON-NLS-1$

        helpTest(ob, "ORDER BY e1 DESC, x DESC"); //$NON-NLS-1$
    }

    public void testQuery1() {
        Select select = new Select();
        select.addSymbol(new MultipleElementSymbol());
        From from = new From();
        from.addGroup(new GroupSymbol("m.g")); //$NON-NLS-1$
        Query query = new Query();
        query.setSelect(select);
        query.setFrom(from);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g"); //$NON-NLS-1$
    }

    public void testQuery2() {
        Select select = new Select();
        select.addSymbol(new MultipleElementSymbol());
        From from = new From();
        from.addGroup(new GroupSymbol("m.g")); //$NON-NLS-1$
        CompareCriteria cc = new CompareCriteria(
                                                 new ElementSymbol("e1"), AbstractCompareCriteria.EQ, new Constant(new Integer(5))); //$NON-NLS-1$
        GroupBy groupBy = new GroupBy();
        groupBy.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        CompareCriteria having = new CompareCriteria(
                                                     new ElementSymbol("e1"), AbstractCompareCriteria.GT, new Constant(new Integer(0))); //$NON-NLS-1$
        OrderBy orderBy = new OrderBy();
        orderBy.addVariable(new ElementSymbol("e1")); //$NON-NLS-1$

        Query query = new Query();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testQuery3() {
        Select select = new Select();
        select.addSymbol(new MultipleElementSymbol());
        From from = new From();
        from.addGroup(new GroupSymbol("m.g")); //$NON-NLS-1$
        GroupBy groupBy = new GroupBy();
        groupBy.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        CompareCriteria having = new CompareCriteria(
                                                     new ElementSymbol("e1"), AbstractCompareCriteria.GT, new Constant(new Integer(0))); //$NON-NLS-1$
        OrderBy orderBy = new OrderBy();
        orderBy.addVariable(new ElementSymbol("e1")); //$NON-NLS-1$

        Query query = new Query();
        query.setSelect(select);
        query.setFrom(from);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testQuery4() {
        Select select = new Select();
        select.addSymbol(new MultipleElementSymbol());
        From from = new From();
        from.addGroup(new GroupSymbol("m.g")); //$NON-NLS-1$
        CompareCriteria cc = new CompareCriteria(
                                                 new ElementSymbol("e1"), AbstractCompareCriteria.EQ, new Constant(new Integer(5))); //$NON-NLS-1$
        CompareCriteria having = new CompareCriteria(
                                                     new ElementSymbol("e1"), AbstractCompareCriteria.GT, new Constant(new Integer(0))); //$NON-NLS-1$
        OrderBy orderBy = new OrderBy();
        orderBy.addVariable(new ElementSymbol("e1")); //$NON-NLS-1$

        Query query = new Query();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testQuery5() {
        Select select = new Select();
        select.addSymbol(new MultipleElementSymbol());
        From from = new From();
        from.addGroup(new GroupSymbol("m.g")); //$NON-NLS-1$
        CompareCriteria cc = new CompareCriteria(
                                                 new ElementSymbol("e1"), AbstractCompareCriteria.EQ, new Constant(new Integer(5))); //$NON-NLS-1$
        GroupBy groupBy = new GroupBy();
        groupBy.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        OrderBy orderBy = new OrderBy();
        orderBy.addVariable(new ElementSymbol("e1")); //$NON-NLS-1$

        Query query = new Query();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testQuery6() {
        Select select = new Select();
        select.addSymbol(new MultipleElementSymbol());
        From from = new From();
        from.addGroup(new GroupSymbol("m.g")); //$NON-NLS-1$
        CompareCriteria cc = new CompareCriteria(
                                                 new ElementSymbol("e1"), AbstractCompareCriteria.EQ, new Constant(new Integer(5))); //$NON-NLS-1$
        GroupBy groupBy = new GroupBy();
        groupBy.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        CompareCriteria having = new CompareCriteria(
                                                     new ElementSymbol("e1"), AbstractCompareCriteria.GT, new Constant(new Integer(0))); //$NON-NLS-1$

        Query query = new Query();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0"); //$NON-NLS-1$
    }

    public void testQuery7() {
        Select select = new Select();
        select.addSymbol(new MultipleElementSymbol());
        From from = new From();
        from.addGroup(new GroupSymbol("m.g")); //$NON-NLS-1$
        CompareCriteria cc = new CompareCriteria(
                                                 new ElementSymbol("e1"), AbstractCompareCriteria.EQ, new Constant(new Integer(5))); //$NON-NLS-1$
        GroupBy groupBy = new GroupBy();
        groupBy.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        CompareCriteria having = new CompareCriteria(
                                                     new ElementSymbol("e1"), AbstractCompareCriteria.GT, new Constant(new Integer(0))); //$NON-NLS-1$
        OrderBy orderBy = new OrderBy();
        orderBy.addVariable(new ElementSymbol("e1")); //$NON-NLS-1$

        Query query = new Query();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
    }

    // The "Select" display node was basically retired with Teiid 7.7 code-base. It's been replaced by the concept that the SQL Statement
    // is already a select.
//    public void testSelect1() {
//        Select select = new Select();
//        select.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
//
//        helpTest(select, "SELECT\n\t\te1"); //$NON-NLS-1$
//    }
//
//    public void testSelect2() {
//        Select select = new Select();
//        select.setDistinct(true);
//        select.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
//
//        helpTest(select, "SELECT DISTINCT\n\t\te1"); //$NON-NLS-1$
//    }
//
//    public void testSelect3() {
//        Select select = new Select();
//        select.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
//        select.addSymbol(new ElementSymbol("e2")); //$NON-NLS-1$
//
//        helpTest(select, "SELECT\n\t\te1, e2"); //$NON-NLS-1$
//    }

    public void testSetCriteria1() {
        SetCriteria sc = new SetCriteria();
        sc.setExpression(new ElementSymbol("e1")); //$NON-NLS-1$
        sc.setValues(new ArrayList());

        helpTest(sc, "e1 IN ()"); //$NON-NLS-1$
    }

    public void testSetCriteria2() {
        SetCriteria sc = new SetCriteria();
        sc.setExpression(new ElementSymbol("e1")); //$NON-NLS-1$
        ArrayList values = new ArrayList();
        values.add(new ElementSymbol("e2")); //$NON-NLS-1$
        values.add(new Constant("abc")); //$NON-NLS-1$
        sc.setValues(values);

        helpTest(sc, "e1 IN (e2, 'abc')"); //$NON-NLS-1$
    }

    public void testSetCriteria3() {
        SetCriteria sc = new SetCriteria();
        sc.setExpression(new ElementSymbol("e1")); //$NON-NLS-1$
        ArrayList values = new ArrayList();
        values.add(null);
        values.add(new Constant("b")); //$NON-NLS-1$
        sc.setValues(values);

        helpTest(sc, "e1 IN (<undefined>, 'b')"); //$NON-NLS-1$
    }

    public void testSetCriteria4() {
        SetCriteria sc = new SetCriteria();
        sc.setExpression(new ElementSymbol("e1")); //$NON-NLS-1$
        ArrayList values = new ArrayList();
        values.add(new ElementSymbol("e2")); //$NON-NLS-1$
        values.add(new Constant("abc")); //$NON-NLS-1$
        sc.setValues(values);
        sc.setNegated(true);
        helpTest(sc, "e1 NOT IN (e2, 'abc')"); //$NON-NLS-1$
    }

    public void testSetQuery1() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = new Select();
        s2.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f2 = new From();
        f2.addGroup(new GroupSymbol("m.g2")); //$NON-NLS-1$
        Query q2 = new Query();
        q2.setSelect(s2);
        q2.setFrom(f2);

        SetQuery sq = new SetQuery(Operation.UNION);
        sq.setAll(false);
        sq.setLeftQuery(q1);
        sq.setRightQuery(q2);

        helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
    }

    public void testSetQuery2() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = new Select();
        s2.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f2 = new From();
        f2.addGroup(new GroupSymbol("m.g2")); //$NON-NLS-1$
        Query q2 = new Query();
        q2.setSelect(s2);
        q2.setFrom(f2);

        SetQuery sq = new SetQuery(Operation.UNION);
        sq.setLeftQuery(q1);
        sq.setRightQuery(q2);

        helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION ALL\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
    }

    public void testSetQuery3() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = new Select();
        s2.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f2 = new From();
        f2.addGroup(new GroupSymbol("m.g2")); //$NON-NLS-1$
        Query q2 = new Query();
        q2.setSelect(s2);
        q2.setFrom(f2);

        OrderBy orderBy = new OrderBy();
        orderBy.addVariable(new ElementSymbol("e1")); //$NON-NLS-1$

        SetQuery sq = new SetQuery(Operation.UNION, false, q1, q2);
        sq.setOrderBy(orderBy);

        helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2\nORDER BY e1"); //$NON-NLS-1$
    }

    public void testSetQuery4() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = new Select();
        s2.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f2 = new From();
        f2.addGroup(new GroupSymbol("m.g2")); //$NON-NLS-1$
        Query q2 = new Query();
        q2.setSelect(s2);
        q2.setFrom(f2);

        SetQuery sq = new SetQuery(Operation.UNION, false, q1, q2);

        helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
    }

    public void testSetQuery5() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = new Select();
        s2.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f2 = new From();
        f2.addGroup(new GroupSymbol("m.g2")); //$NON-NLS-1$
        Query q2 = new Query();
        q2.setSelect(s2);
        q2.setFrom(f2);

        Select s3 = new Select();
        s3.addSymbol(new ElementSymbol("e3")); //$NON-NLS-1$
        From f3 = new From();
        f3.addGroup(new GroupSymbol("m.g3")); //$NON-NLS-1$
        Query q3 = new Query();
        q3.setSelect(s3);
        q3.setFrom(f3);

        SetQuery sq = new SetQuery(Operation.UNION, false, q1, q2);

        SetQuery sq2 = new SetQuery(Operation.UNION, true, q3, sq);

        helpTest(sq2,
                 "SELECT\n\t\te3\n\tFROM\n\t\tm.g3\nUNION ALL\n(SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2)"); //$NON-NLS-1$
    }

    public void testSubqueryFromClause1() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        SubqueryFromClause sfc = new SubqueryFromClause("temp", q1); //$NON-NLS-1$
        helpTest(sfc, "(SELECT e1 FROM m.g1) AS temp"); //$NON-NLS-1$
    }

    public void testOptionalSubqueryFromClause1() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        SubqueryFromClause sfc = new SubqueryFromClause("temp", q1); //$NON-NLS-1$
        sfc.setOptional(true);
        helpTest(sfc, "/*+ optional */ (SELECT e1 FROM m.g1) AS temp"); //$NON-NLS-1$
    }

    public void testSubquerySetCriteria1() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ElementSymbol expr = new ElementSymbol("e2"); //$NON-NLS-1$

        SubquerySetCriteria ssc = new SubquerySetCriteria(expr, q1);
        helpTest(ssc, "e2 IN (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testSubquerySetCriteria2() {
        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ElementSymbol expr = new ElementSymbol("e2"); //$NON-NLS-1$

        SubquerySetCriteria ssc = new SubquerySetCriteria(expr, q1);
        ssc.setNegated(true);
        helpTest(ssc, "e2 NOT IN (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testUnaryFromClause() {
        helpTest(new UnaryFromClause(new GroupSymbol("m.g1")), "m.g1"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testOptionalUnaryFromClause() {
        UnaryFromClause unaryFromClause = new UnaryFromClause(new GroupSymbol("m.g1"));//$NON-NLS-1$
        unaryFromClause.setOptional(true);
        helpTest(unaryFromClause, "/*+ optional */ m.g1"); //$NON-NLS-1$ 
    }

    public void testUpdate1() {
        Update update = new Update();
        update.setGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        update.addChange(new ElementSymbol("e1"), new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$

        helpTest(update, "UPDATE m.g1\n\tSET\n\t\te1 = 'abc'"); //$NON-NLS-1$
    }

    public void testUpdate2() {
        Update update = new Update();
        update.setGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        update.addChange(new ElementSymbol("e1"), new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        update.addChange(new ElementSymbol("e2"), new Constant("xyz")); //$NON-NLS-1$ //$NON-NLS-2$

        helpTest(update, "UPDATE m.g1\n\tSET\n\t\te1 = 'abc', e2 = 'xyz'"); //$NON-NLS-1$
    }

    public void testUpdate3() {
        Update update = new Update();
        update.setGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        update.addChange(new ElementSymbol("e1"), new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        update.setCriteria(new CompareCriteria(new ElementSymbol("e2"), //$NON-NLS-1$
                                               AbstractCompareCriteria.EQ, new Constant("abc"))); //$NON-NLS-1$

        helpTest(update, "UPDATE m.g1\n\tSET\n\t\te1 = 'abc'\n\tWHERE\n\t\te2 = 'abc'"); //$NON-NLS-1$
    }

    public void testAggregateSymbol1() {
        AggregateSymbol agg = new AggregateSymbol("abc", false, new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(AggregateSymbol.Type.COUNT);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }

    public void testAggregateSymbol2() {
        AggregateSymbol agg = new AggregateSymbol("abc", true, new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(AggregateSymbol.Type.COUNT);
        helpTest(agg, "abc(DISTINCT 'abc')"); //$NON-NLS-1$
    }

    public void testAggregateSymbol3() {
        AggregateSymbol agg = new AggregateSymbol("abc", false, null); //$NON-NLS-1$
        agg.setAggregateFunction(AggregateSymbol.Type.COUNT);
        helpTest(agg, "abc(*)"); //$NON-NLS-1$
    }

    public void testAggregateSymbol4() {
        AggregateSymbol agg = new AggregateSymbol("abc", false, new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(AggregateSymbol.Type.AVG);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }

    public void testAggregateSymbol5() {
        AggregateSymbol agg = new AggregateSymbol("abc", false, new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(AggregateSymbol.Type.SUM);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }

    public void testAggregateSymbol6() {
        AggregateSymbol agg = new AggregateSymbol("abc", false, new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(AggregateSymbol.Type.MIN);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }

    public void testAggregateSymbol7() {
        AggregateSymbol agg = new AggregateSymbol("abc", false, new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(AggregateSymbol.Type.MAX);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }

    public void testAliasSymbol1() {
        AliasSymbol as = new AliasSymbol("x", new ElementSymbol("y")); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(as, "y AS x"); //$NON-NLS-1$
    }

    // Test alias symbol with reserved word
    public void testAliasSymbol2() {
        AliasSymbol as = new AliasSymbol("select", new ElementSymbol("y")); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(as, "y AS \"select\""); //$NON-NLS-1$
    }

    public void testMultipleElementSymbol() {
        helpTest(new MultipleElementSymbol(), "*"); //$NON-NLS-1$
    }

    public void testConstantNull() {
        helpTest(new Constant(null), "null"); //$NON-NLS-1$
    }

    public void testConstantString() {
        helpTest(new Constant("abc"), "'abc'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantInteger() {
        helpTest(new Constant(new Integer(5)), "5"); //$NON-NLS-1$
    }

    public void testConstantBigDecimal() {
        helpTest(new Constant(new BigDecimal("5.4")), "5.4"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantStringWithTick() {
        helpTest(new Constant("O'Leary"), "'O''Leary'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantStringWithTicks() {
        helpTest(new Constant("'abc'"), "'''abc'''"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantStringWithMoreTicks() {
        helpTest(new Constant("a'b'c"), "'a''b''c'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantStringWithDoubleTick() {
        helpTest(new Constant("group=\"x\""), "'group=\"x\"'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantBooleanTrue() {
        helpTest(new Constant(Boolean.TRUE), "TRUE"); //$NON-NLS-1$
    }

    public void testConstantBooleanFalse() {
        helpTest(new Constant(Boolean.FALSE), "FALSE"); //$NON-NLS-1$
    }

    public void testConstantDate() {
        helpTest(new Constant(java.sql.Date.valueOf("2002-10-02")), "{d'2002-10-02'}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantTime() {
        helpTest(new Constant(java.sql.Time.valueOf("5:00:00")), "{t'05:00:00'}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantTimestamp() {
        helpTest(new Constant(java.sql.Timestamp.valueOf("2002-10-02 17:10:35.0234")), "{ts'2002-10-02 17:10:35.0234'}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testElementSymbol1() {
        ElementSymbol es = new ElementSymbol("elem"); //$NON-NLS-1$
        helpTest(es, "elem"); //$NON-NLS-1$
    }

    public void testElementSymbol2() {
        ElementSymbol es = new ElementSymbol("elem", false); //$NON-NLS-1$
        es.setGroupSymbol(new GroupSymbol("m.g")); //$NON-NLS-1$
        helpTest(es, "elem"); //$NON-NLS-1$
    }

    public void testElementSymbol3() {
        ElementSymbol es = new ElementSymbol("m.g.elem", true); //$NON-NLS-1$
        es.setGroupSymbol(new GroupSymbol("m.g")); //$NON-NLS-1$
        helpTest(es, "m.g.elem"); //$NON-NLS-1$
    }

    public void testElementSymbol4() {
        ElementSymbol es = new ElementSymbol("elem", true); //$NON-NLS-1$
        es.setGroupSymbol(new GroupSymbol("m.g")); //$NON-NLS-1$
        helpTest(es, "m.g.elem"); //$NON-NLS-1$
    }

    public void testElementSymbol5() {
        ElementSymbol es = new ElementSymbol("m.g.select", false); //$NON-NLS-1$
        es.setGroupSymbol(new GroupSymbol("m.g")); //$NON-NLS-1$
        helpTest(es, "\"select\""); //$NON-NLS-1$
    }

    public void testExpressionSymbol1() {
        ExpressionSymbol expr = new ExpressionSymbol("abc", new Constant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(expr, "'abc'"); //$NON-NLS-1$
    }

    public void testFunction1() {
        Function func = new Function("concat", new Expression[] { //$NON-NLS-1$
                                     new Constant("a"), null //$NON-NLS-1$
                                     });
        helpTest(func, "concat('a', <undefined>)"); //$NON-NLS-1$
    }

    public void testFunction2() {
        Function func = new Function("now", new Expression[] {}); //$NON-NLS-1$
        helpTest(func, "now()"); //$NON-NLS-1$
    }

    public void testFunction3() {
        Function func = new Function("concat", new Expression[] {null, null}); //$NON-NLS-1$
        helpTest(func, "concat(<undefined>, <undefined>)"); //$NON-NLS-1$
    }

    public void testFunction4() {
        Function func1 = new Function("power", new Expression[] { //$NON-NLS-1$
                                      new Constant(new Integer(5)), new Constant(new Integer(3))});
        Function func2 = new Function("power", new Expression[] { //$NON-NLS-1$
                                      func1, new Constant(new Integer(3))});
        Function func3 = new Function("+", new Expression[] { //$NON-NLS-1$
                                      new Constant(new Integer(1000)), func2});
        helpTest(func3, "(1000 + power(power(5, 3), 3))"); //$NON-NLS-1$
    }

    public void testFunction5() {
        Function func1 = new Function("concat", new Expression[] { //$NON-NLS-1$
                                      new ElementSymbol("elem2"), //$NON-NLS-1$
                                          null});
        Function func2 = new Function("concat", new Expression[] { //$NON-NLS-1$
                                      new ElementSymbol("elem1"), //$NON-NLS-1$
                                          func1});
        helpTest(func2, "concat(elem1, concat(elem2, <undefined>))"); //$NON-NLS-1$
    }

    public void testConvertFunction1() {
        Function func = new Function("convert", new Expression[] { //$NON-NLS-1$
                                     new Constant("5"), //$NON-NLS-1$
                                         new Constant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "convert('5', integer)"); //$NON-NLS-1$
    }

    public void testConvertFunction2() {
        Function func = new Function("convert", new Expression[] { //$NON-NLS-1$
                                     null, new Constant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "convert(<undefined>, integer)"); //$NON-NLS-1$
    }

    public void testConvertFunction3() {
        Function func = new Function("convert", new Expression[] { //$NON-NLS-1$
                                     new Constant(null), new Constant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "convert(null, integer)"); //$NON-NLS-1$
    }

    public void testConvertFunction5() {
        Function func = new Function("convert", null); //$NON-NLS-1$
        helpTest(func, "convert()"); //$NON-NLS-1$
    }

    public void testConvertFunction6() {
        Function func = new Function("convert", new Expression[0]); //$NON-NLS-1$
        helpTest(func, "convert()"); //$NON-NLS-1$
    }

    public void testCastFunction1() {
        Function func = new Function("cast", new Expression[] { //$NON-NLS-1$
                                     new Constant("5"), //$NON-NLS-1$
                                         new Constant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "cast('5' AS integer)"); //$NON-NLS-1$
    }

    public void testCastFunction2() {
        Function func = new Function("cast", new Expression[] { //$NON-NLS-1$
                                     null, new Constant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "cast(<undefined> AS integer)"); //$NON-NLS-1$
    }

    public void testCastFunction3() {
        Function func = new Function("cast", new Expression[] { //$NON-NLS-1$
                                     new Constant(null), new Constant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "cast(null AS integer)"); //$NON-NLS-1$
    }

    public void testArithemeticFunction1() {
        Function func = new Function("-", new Expression[] { //$NON-NLS-1$
                                     new Constant(new Integer(-2)), new Constant(new Integer(-1))});
        helpTest(func, "(-2 - -1)"); //$NON-NLS-1$
    }

    public void testGroupSymbol1() {
        GroupSymbol gs = new GroupSymbol("g"); //$NON-NLS-1$
        helpTest(gs, "g"); //$NON-NLS-1$
    }

    public void testGroupSymbol2() {
        GroupSymbol gs = new GroupSymbol("x", "g"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(gs, "g AS x"); //$NON-NLS-1$
    }

    public void testGroupSymbol3() {
        GroupSymbol gs = new GroupSymbol("vdb.g"); //$NON-NLS-1$
        helpTest(gs, "vdb.g"); //$NON-NLS-1$
    }

    public void testGroupSymbol4() {
        GroupSymbol gs = new GroupSymbol("x", "vdb.g"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(gs, "vdb.g AS x"); //$NON-NLS-1$
    }

    public void testGroupSymbol5() {
        GroupSymbol gs = new GroupSymbol("from", "m.g"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(gs, "m.g AS \"from\""); //$NON-NLS-1$
    }

    public void testGroupSymbol6() {
        GroupSymbol gs = new GroupSymbol("x", "on.select"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(gs, "\"on\".\"select\" AS x"); //$NON-NLS-1$
    }

    public void testExecNoParams() {
        StoredProcedure proc = new StoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        helpTest(proc, "EXEC myproc()"); //$NON-NLS-1$
    }

    public void testExecInputParam() {
        StoredProcedure proc = new StoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        SPParameter param = new SPParameter(1, new Reference(0));
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc(?)"); //$NON-NLS-1$
    }

    public void testExecInputOutputParam() {
        StoredProcedure proc = new StoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        SPParameter param1 = new SPParameter(1, new Constant(new Integer(5)));
        param1.setParameterType(ParameterInfo.IN);
        proc.setParameter(param1);

        SPParameter param2 = new SPParameter(2, ParameterInfo.OUT, "x"); //$NON-NLS-1$
        proc.setParameter(param2);

        helpTest(proc, "EXEC myproc(5)"); //$NON-NLS-1$
    }

    public void testExecOutputInputParam() {
        StoredProcedure proc = new StoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$

        SPParameter param2 = new SPParameter(2, ParameterInfo.OUT, "x"); //$NON-NLS-1$
        proc.setParameter(param2);

        SPParameter param1 = new SPParameter(1, new Constant(new Integer(5)));
        param1.setParameterType(ParameterInfo.IN);
        proc.setParameter(param1);

        helpTest(proc, "EXEC myproc(5)"); //$NON-NLS-1$
    }

    public void testExecReturnParam() {
        StoredProcedure proc = new StoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$

        SPParameter param = new SPParameter(1, ParameterInfo.RETURN_VALUE, "ret"); //$NON-NLS-1$
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc()"); //$NON-NLS-1$
    }

    public void testExecNamedParam() {
        StoredProcedure proc = new StoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        SPParameter param = new SPParameter(1, new Reference(0));
        param.setName("p1");//$NON-NLS-1$
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc(p1 => ?)"); //$NON-NLS-1$
    }

    public void testExecNamedParams() {
        StoredProcedure proc = new StoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        SPParameter param = new SPParameter(1, new Reference(0));
        param.setName("p1");//$NON-NLS-1$
        proc.setParameter(param);
        SPParameter param2 = new SPParameter(2, new Reference(0));
        param2.setName("p2");//$NON-NLS-1$
        proc.setParameter(param2);
        helpTest(proc, "EXEC myproc(p1 => ?, p2 => ?)"); //$NON-NLS-1$
    }

    /**
     * Test when a parameter's name is a reserved word. (Note: parameters should always have short names, not multiple
     * period-delimited name components.)
     * 
     * @since 4.3
     */
    public void testExecNamedParamsReservedWord() {
        StoredProcedure proc = new StoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        SPParameter param = new SPParameter(1, new Reference(0));
        param.setName("in");//$NON-NLS-1$
        proc.setParameter(param);
        SPParameter param2 = new SPParameter(2, new Reference(0));
        param2.setName("in2");//$NON-NLS-1$
        proc.setParameter(param2);
        helpTest(proc, "EXEC myproc(\"in\" => ?, in2 => ?)"); //$NON-NLS-1$
    }

    // Test methods for Update Procedure Language Objects

    public void testDeclareStatement() {
        DeclareStatement dclStmt = new DeclareStatement(new ElementSymbol("a"), "String"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(dclStmt, "DECLARE String a;"); //$NON-NLS-1$
    }

    public void testRaiseStatement() {
        RaiseStatement errStmt = new RaiseStatement(new Constant("My Error")); //$NON-NLS-1$
        helpTest(errStmt, "RAISE 'My Error';"); //$NON-NLS-1$
    }

    public void testRaiseStatementWithExpression() {
        RaiseStatement errStmt = new RaiseStatement(new ElementSymbol("a")); //$NON-NLS-1$
        helpTest(errStmt, "RAISE a;"); //$NON-NLS-1$
    }

    public void testAssignmentStatement1() {
        AssignmentStatement assigStmt = new AssignmentStatement(new ElementSymbol("a"), new Constant(new Integer(1))); //$NON-NLS-1$
        helpTest(assigStmt, "a = 1;"); //$NON-NLS-1$
    }

    public void FAILINGtestAssignmentStatement2() {
        // TODO fix this test
        Query q1 = new Query();
        Select select = new Select();
        select.addSymbol(new ElementSymbol("x")); //$NON-NLS-1$
        q1.setSelect(select);
        From from = new From();
        from.addGroup(new GroupSymbol("g")); //$NON-NLS-1$
        q1.setFrom(from);

        AssignmentStatement assigStmt = new AssignmentStatement(new ElementSymbol("a"), q1); //$NON-NLS-1$
        helpTest(assigStmt, "a = SELECT x FROM g;"); //$NON-NLS-1$
    }

 
    public void testCommandStatement1() {
        Query q1 = new Query();
        Select select = new Select();
        select.addSymbol(new ElementSymbol("x")); //$NON-NLS-1$
        q1.setSelect(select);
        From from = new From();
        from.addGroup(new GroupSymbol("g")); //$NON-NLS-1$
        q1.setFrom(from);

        CommandStatement cmdStmt = new CommandStatement(q1);
        helpTest(cmdStmt, "SELECT x FROM g;"); //$NON-NLS-1$
    }

    public void testCommandStatement2() {
        Delete d1 = new Delete();
        d1.setGroup(new GroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt = new CommandStatement(d1);
        helpTest(cmdStmt, "DELETE FROM g;"); //$NON-NLS-1$
    }

    public void testBlock1() {
        Delete d1 = new Delete();
        d1.setGroup(new GroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt = new CommandStatement(d1);
        AssignmentStatement assigStmt = new AssignmentStatement(new ElementSymbol("a"), new Constant(new Integer(1))); //$NON-NLS-1$
        RaiseStatement errStmt = new RaiseStatement(new Constant("My Error")); //$NON-NLS-1$
        Block b = new Block();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        helpTest(b, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$
    }

    public void testCreateUpdateProcedure1() {
        Delete d1 = new Delete();
        d1.setGroup(new GroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt = new CommandStatement(d1);
        AssignmentStatement assigStmt = new AssignmentStatement(new ElementSymbol("a"), new Constant(new Integer(1))); //$NON-NLS-1$
        RaiseStatement errStmt = new RaiseStatement(new Constant("My Error")); //$NON-NLS-1$
        Block b = new Block();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        CreateProcedureCommand cup = new CreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$
    }

    public void testCreateUpdateProcedure2() {
        Delete d1 = new Delete();
        d1.setGroup(new GroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt = new CommandStatement(d1);
        AssignmentStatement assigStmt = new AssignmentStatement(new ElementSymbol("a"), new Constant(new Integer(1))); //$NON-NLS-1$
        RaiseStatement errStmt = new RaiseStatement(new Constant("My Error")); //$NON-NLS-1$
        Block b = new Block();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        CreateProcedureCommand cup = new CreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$
    }

    public void testCreateUpdateProcedure3() {
        Delete d1 = new Delete();
        d1.setGroup(new GroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt = new CommandStatement(d1);
        AssignmentStatement assigStmt = new AssignmentStatement(new ElementSymbol("a"), new Constant(new Integer(1))); //$NON-NLS-1$
        RaiseStatement errStmt = new RaiseStatement(new Constant("My Error")); //$NON-NLS-1$
        Block b = new Block();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        CreateProcedureCommand cup = new CreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$
    }

    // Test Insert
    public void FAILINGtestCreateUpdateProcedure6() {
        // TODO fix this test
        Insert insert = new Insert();
        insert.setGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$

        List vars = new ArrayList();
        vars.add(new ElementSymbol("e1")); //$NON-NLS-1$
        vars.add(new ElementSymbol("e2")); //$NON-NLS-1$
        insert.setVariables(vars);
        List values = new ArrayList();
        values.add(new Constant(new Integer(5)));
        values.add(new Constant("abc")); //$NON-NLS-1$
        insert.setValues(values);
        AssignmentStatement assigStmt = new AssignmentStatement(new ElementSymbol("VARIABLES.ROWS_UPDATED"), insert); //$NON-NLS-1$
        Block b = new Block();
        b.addStatement(assigStmt);
        CreateProcedureCommand cup = new CreateProcedureCommand(b);
        helpTest(cup,
                 "CREATE PROCEDURE\nBEGIN\n\tVARIABLES.ROWS_UPDATED = INSERT INTO m.g1 (e1, e2) VALUES (5, 'abc');\nEND"); //$NON-NLS-1$
    }

    public void testSubqueryCompareCriteria1() {

        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ElementSymbol expr = new ElementSymbol("e2"); //$NON-NLS-1$

        SubqueryCompareCriteria scc = new SubqueryCompareCriteria(expr, q1, AbstractCompareCriteria.EQ,
                                                                  SubqueryCompareCriteria.ANY);

        helpTest(scc, "e2 = ANY (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testSubqueryCompareCriteria2() {

        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ElementSymbol expr = new ElementSymbol("e2"); //$NON-NLS-1$

        SubqueryCompareCriteria scc = new SubqueryCompareCriteria(expr, q1, AbstractCompareCriteria.LE,
                                                                  SubqueryCompareCriteria.SOME);

        helpTest(scc, "e2 <= SOME (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testExistsCriteria1() {

        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ExistsCriteria ec = new ExistsCriteria(q1);

        helpTest(ec, "EXISTS (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void FAILINGtestDynamicCommand() {
        // TODO fix this test
        List symbols = new ArrayList();

        ElementSymbol a1 = new ElementSymbol("a1"); //$NON-NLS-1$
        a1.setType(DataTypeManager.DefaultDataClasses.STRING);
        symbols.add(a1);

        DynamicCommand obj = new DynamicCommand();
        Expression sql = new Constant("SELECT a1 FROM g WHERE a2 = 5"); //$NON-NLS-1$

        obj.setSql(sql);
        obj.setAsColumns(symbols);
        obj.setAsClauseSet(true);
        obj.setIntoGroup(new GroupSymbol("#g")); //$NON-NLS-1$

        helpTest(obj, "EXECUTE STRING 'SELECT a1 FROM g WHERE a2 = 5'\n\tAS a1 string\n\tINTO #g"); //$NON-NLS-1$
    }

    public void testScalarSubquery() {

        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ScalarSubquery obj = new ScalarSubquery(q1);

        helpTest(obj, "(SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testNewSubqueryObjects() {

        Select s1 = new Select();
        s1.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        From f1 = new From();
        f1.addGroup(new GroupSymbol("m.g1")); //$NON-NLS-1$
        Query q1 = new Query();
        q1.setSelect(s1);
        q1.setFrom(f1);

        Select s2 = new Select();
        s2.addSymbol(new ElementSymbol("e1")); //$NON-NLS-1$
        s2.addSymbol(new ExpressionSymbol("blargh", new ScalarSubquery(q1))); //$NON-NLS-1$
        From f2 = new From();
        f2.addGroup(new GroupSymbol("m.g2")); //$NON-NLS-1$
        Criteria left = new SubqueryCompareCriteria(
                                                    new ElementSymbol("e3"), q1, AbstractCompareCriteria.GE, SubqueryCompareCriteria.ANY); //$NON-NLS-1$
        Criteria right = new ExistsCriteria(q1);
        Criteria outer = new CompoundCriteria(CompoundCriteria.AND, left, right);
        Query q2 = new Query();
        q2.setSelect(s2);
        q2.setFrom(f2);
        q2.setCriteria(outer);

        helpTest(q2,
                 "SELECT\n\t\te1, (SELECT e1 FROM m.g1)\n\tFROM\n\t\tm.g2\n\tWHERE\n\t\t(e3 >= ANY (SELECT e1 FROM m.g1)) AND (EXISTS (SELECT e1 FROM m.g1))"); //$NON-NLS-1$
    }

    /**
     * For some reason this test was outputting SELECT 'A' AS FOO UNION SELECT 'A' AS FOO
     */
    public void testSetQueryUnionOfLiteralsCase3102() {

        String expected = "SELECT\n\t\t'A' AS FOO\nUNION\nSELECT\n\t\t'B' AS FOO"; //$NON-NLS-1$

        Select s1 = new Select();
        s1.addSymbol(new AliasSymbol("FOO", new ExpressionSymbol("xxx", new Constant("A")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Query q1 = new Query();
        q1.setSelect(s1);

        Select s2 = new Select();
        s2.addSymbol(new AliasSymbol("FOO", new ExpressionSymbol("xxx", new Constant("B")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Query q2 = new Query();
        q2.setSelect(s2);

        SetQuery sq = new SetQuery(Operation.UNION, false, q1, q2);

        helpTest(sq, expected);
    }

    /**
     * For some reason this test was outputting SELECT 'A' AS FOO UNION SELECT 'A' AS FOO Same as above except that
     * ExpressionSymbols' internal names (which aren't visible in the query) are different
     */
    public void testSetQueryUnionOfLiteralsCase3102a() {

        String expected = "SELECT\n\t\t'A' AS FOO\nUNION\nSELECT\n\t\t'B' AS FOO"; //$NON-NLS-1$

        Select s1 = new Select();
        s1.addSymbol(new AliasSymbol("FOO", new ExpressionSymbol("xxx", new Constant("A")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Query q1 = new Query();
        q1.setSelect(s1);

        Select s2 = new Select();
        s2.addSymbol(new AliasSymbol("FOO", new ExpressionSymbol("yyy", new Constant("B")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        Query q2 = new Query();
        q2.setSelect(s2);

        SetQuery sq = new SetQuery(Operation.UNION, false, q1, q2);

        helpTest(sq, expected);
    }

    public void FAILINGtestNullExpressionInNamedParameter() {
        // TODO fix this test
        String expected = "EXEC sp1(PARAM => sp1.PARAM)"; //$NON-NLS-1$

        StoredProcedure sp = new StoredProcedure();
        sp.setDisplayNamedParameters(true);
        sp.setProcedureName("sp1"); //$NON-NLS-1$

        SPParameter param = new SPParameter(0, ParameterInfo.IN, "sp1.PARAM"); //$NON-NLS-1$
        sp.setParameter(param);

        helpTest(sp, expected);
    }

    public void testLimit() {
        Query query = new Query();
        Select select = new Select(Arrays.asList(new MultipleElementSymbol()));
        From from = new From(Arrays.asList(new UnaryFromClause(new GroupSymbol("a")))); //$NON-NLS-1$
        query.setSelect(select);
        query.setFrom(from);
        query.setLimit(new Limit(null, new Constant(new Integer(100))));
        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\ta\n\tLIMIT 100"); //$NON-NLS-1$
    }

    public void testLimitWithOffset() {
        Query query = new Query();
        Select select = new Select(Arrays.asList(new MultipleElementSymbol()));
        From from = new From(Arrays.asList(new UnaryFromClause(new GroupSymbol("a")))); //$NON-NLS-1$
        query.setSelect(select);
        query.setFrom(from);
        query.setLimit(new Limit(new Constant(new Integer(50)), new Constant(new Integer(100))));
        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\ta\n\tLIMIT 50, 100"); //$NON-NLS-1$ 
    }

    public void testQueryWithMakeDep() {
        Query query = new Query();
        Select select = new Select(Arrays.asList(new MultipleElementSymbol()));
        FromClause fromClause = new UnaryFromClause(new GroupSymbol("a")); //$NON-NLS-1$
        fromClause.setMakeDep(true);
        From from = new From(Arrays.asList(fromClause));
        query.setSelect(select);
        query.setFrom(from);
        String expectedSQL = "SELECT\n\t\t*\n\tFROM\n\t\t/*+ MAKEDEP */ a";  //$NON-NLS-1$ 
        helpTest(query, expectedSQL);
    }

    public void testQueryWithJoinPredicateMakeDep() {
        Query query = new Query();
        Select select = new Select(Arrays.asList(new MultipleElementSymbol()));
        FromClause fromClause = new UnaryFromClause(new GroupSymbol("a")); //$NON-NLS-1$
        fromClause.setMakeNotDep(true);
        FromClause fromClause1 = new UnaryFromClause(new GroupSymbol("b")); //$NON-NLS-1$
        fromClause1.setMakeDep(true);
        From from = new From(Arrays.asList(new JoinPredicate(fromClause, fromClause1, JoinType.JOIN_CROSS)));
        query.setSelect(select);
        query.setFrom(from);
        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\t/*+ MAKENOTDEP */ a CROSS JOIN /*+ MAKEDEP */ b"); //$NON-NLS-1$ 
    }

    public void testQueryWithNestedJoinPredicateMakeDep() throws Exception {
        Query query = (Query)QueryParser.getQueryParser().parseCommand("Select a From (db.g1 JOIN db.g2 ON a = b) makedep LEFT OUTER JOIN db.g3 ON a = c"); //$NON-NLS-1$
        helpTest(query, "SELECT\n\t\ta\n\tFROM\n\t\t/*+ MAKEDEP */ (db.g1 INNER JOIN db.g2 ON a = b) LEFT OUTER JOIN db.g3 ON a = c"); //$NON-NLS-1$
    }
    
    public void testCast() throws Exception {
        Expression ex = QueryParser.getQueryParser().parseExpression("cast(x as integer)"); //$NON-NLS-1$
        helpTest(ex, "cast(x AS integer)"); //$NON-NLS-1$
    }
    
    public void testXMLPi() throws Exception {
        Expression ex = QueryParser.getQueryParser().parseExpression("xmlpi(name foo, 'bar')"); //$NON-NLS-1$
        helpTest(ex, "xmlpi(NAME foo, 'bar')"); //$NON-NLS-1$
    }
    
    public void testXMLPi1() throws Exception {
        Expression ex = QueryParser.getQueryParser().parseExpression("xmlpi(name \"table\", 'bar')"); //$NON-NLS-1$
        helpTest(ex, "xmlpi(NAME \"table\", 'bar')"); //$NON-NLS-1$
    }
    
    public void testTimestampAdd() throws Exception {
        Expression ex = QueryParser.getQueryParser().parseExpression("timestampadd(SQL_TSI_DAY, x, y)"); //$NON-NLS-1$
        helpTest(ex, "timestampadd(SQL_TSI_DAY, x, y)"); //$NON-NLS-1$
    }
    
    public void testXMLAgg() throws Exception {
        LanguageObject ex = QueryParser.getQueryParser().parseCommand("select xmlagg(x order by y)"); //$NON-NLS-1$
        helpTest(ex, "SELECT\n\t\tXMLAGG(x ORDER BY y)"); //$NON-NLS-1$
    }

    public void testXMLElement() throws Exception {
        LanguageObject ex = QueryParser.getQueryParser().parseExpression("xmlelement(name y, xmlattributes('x' as foo), q)"); //$NON-NLS-1$
        helpTest(ex, "XMLELEMENT(NAME y, XMLATTRIBUTES('x' AS foo), q)"); //$NON-NLS-1$
    }
    
    public void testCacheHint() throws Exception {
        LanguageObject ex = QueryParser.getQueryParser().parseCommand("/*+ cache(pref_mem) */ select * from db.g2"); //$NON-NLS-1$
        helpTest(ex, "/*+ cache(pref_mem) */\nSELECT\n\t\t*\n\tFROM\n\t\tdb.g2"); //$NON-NLS-1$
    }
    
    // ################################## TEST SUITE ################################

    /**
     * This suite of all tests could be defined in another class but it seems easier to maintain it here.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestSQLStringVisitor"); //$NON-NLS-1$
        suite.addTestSuite(TestDisplayNodeFactory.class);
        // suite.addTest(new TestSQLStringVisitor("testSetQueryUnionOfLiteralsCase3102"));
        return suite;
    }

}
