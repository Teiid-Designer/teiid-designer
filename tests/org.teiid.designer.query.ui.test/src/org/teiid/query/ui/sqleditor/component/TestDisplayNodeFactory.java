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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryParser;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.designer.query.sql.lang.IStoredProcedure;
import org.teiid.designer.query.sql.lang.ISubqueryCompareCriteria;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.proc.IAssignmentStatement;
import org.teiid.designer.query.sql.proc.IBlock;
import org.teiid.designer.query.sql.proc.ICommandStatement;
import org.teiid.designer.query.sql.proc.ICreateProcedureCommand;
import org.teiid.designer.query.sql.proc.IDeclareStatement;
import org.teiid.designer.query.sql.proc.IRaiseStatement;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;


public class TestDisplayNodeFactory extends TestCase {

    private IQueryFactory factory;
    
    private IQueryParser parser;
    
    // ################################## FRAMEWORK ################################

    public TestDisplayNodeFactory( String name ) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        factory = queryService.createQueryFactory();
        parser = queryService.getQueryParser();
    }

    // ################################## TEST HELPERS ################################

    private void helpTest( ILanguageObject obj,
                           String expectedStr ) {
        DisplayNode displayNode = DisplayNodeFactory.createDisplayNode(null, obj);

        String actualStr = displayNode.toString();
        assertEquals("Expected and actual strings don't match: ", expectedStr, actualStr); //$NON-NLS-1$
    }

    // ################################## ACTUAL TESTS ################################

    public void testBetweenCriteria1() {
        IBetweenCriteria bc = factory.createBetweenCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 factory.createConstant(new Integer(1000)), factory.createConstant(new Integer(2000)));
        helpTest(bc, "m.g.c1 BETWEEN 1000 AND 2000"); //$NON-NLS-1$
    }

    public void testBetweenCriteria2() {
        IBetweenCriteria bc = factory.createBetweenCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 factory.createConstant(new Integer(1000)), factory.createConstant(new Integer(2000)));
        bc.setNegated(true);
        helpTest(bc, "m.g.c1 NOT BETWEEN 1000 AND 2000"); //$NON-NLS-1$
    }

    public void testCompareCriteria1() {
        ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 = 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria2() {
        ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 ICompareCriteria.NE, factory.createConstant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 <> 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria3() {
        ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 ICompareCriteria.GT, factory.createConstant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 > 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria4() {
        ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 ICompareCriteria.GE, factory.createConstant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 >= 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria5() {
        ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 ICompareCriteria.LT, factory.createConstant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 < 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria6() {
        ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 ICompareCriteria.LE, factory.createConstant("abc")); //$NON-NLS-1$

        helpTest(cc, "m.g.c1 <= 'abc'"); //$NON-NLS-1$
    }

    public void testCompareCriteria7() {
        ICompareCriteria cc = factory.createCompareCriteria(null, ICompareCriteria.EQ, null);

        helpTest(cc, "<undefined> = <undefined>"); //$NON-NLS-1$
    }

    public void testCompoundCriteria1() {
        ICompareCriteria cc = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                 ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.LogicalOperator.AND, cc);

        helpTest(comp, "m.g.c1 = 'abc'"); //$NON-NLS-1$
    }

    public void testCompoundCriteria2() {
        ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompareCriteria cc2 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c2"), //$NON-NLS-1$
                                                  ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.LogicalOperator.AND, cc1, cc2);

        helpTest(comp, "(m.g.c1 = 'abc') AND (m.g.c2 = 'abc')"); //$NON-NLS-1$
    }

    public void testCompoundCriteria3() {
        ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompareCriteria cc2 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c2"), //$NON-NLS-1$
                                                  ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompareCriteria cc3 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c3"), //$NON-NLS-1$
                                                  ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.LogicalOperator.OR, cc1, cc2, cc3);

        helpTest(comp, "(m.g.c1 = 'abc') OR (m.g.c2 = 'abc') OR (m.g.c3 = 'abc')"); //$NON-NLS-1$
    }

    public void testCompoundCriteria4() {
        ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.LogicalOperator.OR, cc1, null);

        helpTest(comp, "(m.g.c1 = 'abc') OR (<undefined>)"); //$NON-NLS-1$
    }

    public void testCompoundCriteria5() {
        ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.LogicalOperator.OR, null, cc1);

        helpTest(comp, "(<undefined>) OR (m.g.c1 = 'abc')"); //$NON-NLS-1$
    }

    public void testCompoundCriteria6() {
        ICompareCriteria cc1 = factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                                  ICompareCriteria.EQ, factory.createConstant("abc")); //$NON-NLS-1$
        ICompoundCriteria comp = factory.createCompoundCriteria(ICompoundCriteria.LogicalOperator.OR, cc1, null);

        helpTest(comp, "(m.g.c1 = 'abc') OR (<undefined>)"); //$NON-NLS-1$
    }

    public void testDelete1() {
        IDelete delete = factory.createDelete();
        delete.setGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$

        helpTest(delete, "DELETE FROM m.g"); //$NON-NLS-1$
    }

    public void testDelete2() {
        IDelete delete = factory.createDelete();
        delete.setGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        delete.setCriteria(factory.createCompareCriteria(factory.createElementSymbol("m.g.c1"), //$NON-NLS-1$
                                               ICompareCriteria.EQ, factory.createConstant("abc"))); //$NON-NLS-1$

        helpTest(delete, "DELETE FROM m.g\nWHERE\n\tm.g.c1 = 'abc'"); //$NON-NLS-1$
    }

    public void testFrom1() {
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        from.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$

        helpTest(from, "FROM\n\tm.g1, m.g2"); //$NON-NLS-1$
    }

    public void testFrom2() {
        IFrom from = factory.createFrom();
        from.addClause(factory.createUnaryFromClause(factory.createGroupSymbol("m.g1"))); //$NON-NLS-1$
        from.addClause(factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                         factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                         IJoinPredicate.JoinType.JOIN_CROSS));

        helpTest(from, "FROM\n\tm.g1, m.g2 CROSS JOIN m.g3"); //$NON-NLS-1$
    }

    public void testGroupBy1() {
        IGroupBy gb = factory.createGroupBy();
        gb.addSymbol(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$

        helpTest(gb, "GROUP BY m.g.e1"); //$NON-NLS-1$
    }

    public void testGroupBy2() {
        IGroupBy gb = factory.createGroupBy();
        gb.addSymbol(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
        gb.addSymbol(factory.createElementSymbol("m.g.e2")); //$NON-NLS-1$
        gb.addSymbol(factory.createElementSymbol("m.g.e3")); //$NON-NLS-1$

        helpTest(gb, "GROUP BY m.g.e1, m.g.e2, m.g.e3"); //$NON-NLS-1$
    }

    public void testInsert1() {
        IInsert insert = factory.createInsert();
        insert.setGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$

        List vars = new ArrayList();
        vars.add(factory.createElementSymbol("e1")); //$NON-NLS-1$
        vars.add(factory.createElementSymbol("e2")); //$NON-NLS-1$
        insert.setVariables(vars);
        List values = new ArrayList();
        values.add(factory.createConstant(new Integer(5)));
        values.add(factory.createConstant("abc")); //$NON-NLS-1$
        insert.setValues(values);

        helpTest(insert, "INSERT INTO m.g1\n\t\t(e1, e2)\n\tVALUES\n\t\t(5, 'abc')"); //$NON-NLS-1$
    }

    public void testIsNullCriteria1() {
        IIsNullCriteria inc = factory.createIsNullCriteria();
        inc.setExpression(factory.createConstant("abc")); //$NON-NLS-1$

        helpTest(inc, "'abc' IS NULL"); //$NON-NLS-1$
    }

    public void testIsNullCriteria2() {
        IIsNullCriteria inc = factory.createIsNullCriteria();
        inc.setExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$

        helpTest(inc, "m.g.e1 IS NULL"); //$NON-NLS-1$
    }

    public void testIsNullCriteria3() {
        IIsNullCriteria inc = factory.createIsNullCriteria();
        helpTest(inc, "<undefined> IS NULL"); //$NON-NLS-1$
    }

    public void testIsNullCriteria4() {
        IIsNullCriteria inc = factory.createIsNullCriteria();
        inc.setExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
        inc.setNegated(true);
        helpTest(inc, "m.g.e1 IS NOT NULL"); //$NON-NLS-1$
    }

    public void testJoinPredicate1() {
        IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                             factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                             IJoinPredicate.JoinType.JOIN_CROSS);

        helpTest(jp, "m.g2 CROSS JOIN m.g3"); //$NON-NLS-1$
    }

    public void testOptionalJoinPredicate1() {
        IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                             factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                             IJoinPredicate.JoinType.JOIN_CROSS);
        jp.setOptional(true);
        helpTest(jp, "/*+ optional */ (m.g2 CROSS JOIN m.g3)"); //$NON-NLS-1$
    }

    public void testJoinPredicate2() {
        ArrayList<ICriteria> crits = new ArrayList<ICriteria>();
        crits.add(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
        IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                             factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                             IJoinPredicate.JoinType.JOIN_INNER, crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1"); //$NON-NLS-1$
    }

    public void testJoinPredicate3() {
        ArrayList<ICriteria> crits = new ArrayList<ICriteria>();
        crits.add(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
        crits.add(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e2"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e2"))); //$NON-NLS-1$ //$NON-NLS-2$
        IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                             factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                             IJoinPredicate.JoinType.JOIN_INNER, crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1 AND m.g2.e2 = m.g3.e2"); //$NON-NLS-1$
    }

    public void testJoinPredicate4() {
        ArrayList<ICriteria> crits = new ArrayList<ICriteria>();
        crits.add(factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1"))); //$NON-NLS-1$ //$NON-NLS-2$
        IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                             factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                             IJoinPredicate.JoinType.JOIN_INNER, crits);

        IJoinPredicate jp2 = factory.createJoinPredicate(jp, factory.createUnaryFromClause(factory.createGroupSymbol("m.g1")), //$NON-NLS-1$
                                              IJoinPredicate.JoinType.JOIN_CROSS);

        helpTest(jp2, "(m.g2 INNER JOIN m.g3 ON m.g2.e1 = m.g3.e1) CROSS JOIN m.g1"); //$NON-NLS-1$
    }

    public void testJoinPredicate5() {
        ArrayList<ICriteria> crits = new ArrayList<ICriteria>();
        crits.add(factory.createNotCriteria(
                                  factory.createCompareCriteria(
                                                      factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1")))); //$NON-NLS-1$ //$NON-NLS-2$
        IJoinPredicate jp = factory.createJoinPredicate(factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
                                             factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
                                             IJoinPredicate.JoinType.JOIN_INNER, crits);

        helpTest(jp, "m.g2 INNER JOIN m.g3 ON NOT (m.g2.e1 = m.g3.e1)"); //$NON-NLS-1$
    }
    
    public void testJoinPredicate6() {
        ICompareCriteria comprCrit1 = factory.createCompareCriteria(factory.createElementSymbol("m.g2.e1"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e1")); //$NON-NLS-1$ //$NON-NLS-2$
        ICompareCriteria comprCrit2 = factory.createCompareCriteria(factory.createElementSymbol("m.g2.e2"), ICompareCriteria.EQ, factory.createElementSymbol("m.g3.e2")); //$NON-NLS-1$ //$NON-NLS-2$
        IIsNullCriteria inc = factory.createIsNullCriteria();
        inc.setExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
        
        ICompoundCriteria compCrit = factory.createCompoundCriteria(ICompoundCriteria.LogicalOperator.OR, inc, comprCrit2);

        ArrayList crits2 = new ArrayList();
        crits2.add(comprCrit1);
        crits2.add(compCrit);
        
        IJoinPredicate jp = factory.createJoinPredicate(
            factory.createUnaryFromClause(factory.createGroupSymbol("m.g2")), //$NON-NLS-1$
            factory.createUnaryFromClause(factory.createGroupSymbol("m.g3")), //$NON-NLS-1$
            IJoinPredicate.JoinType.JOIN_LEFT_OUTER,
            crits2 );
            
        helpTest(jp, "m.g2 LEFT OUTER JOIN m.g3 ON m.g2.e1 = m.g3.e1 AND ((m.g.e1 IS NULL) OR (m.g2.e2 = m.g3.e2))"); //$NON-NLS-1$
    }

    public void testMatchCriteria1() {
        IMatchCriteria mc = factory.createMatchCriteria();
        mc.setLeftExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
        mc.setRightExpression(factory.createConstant("abc")); //$NON-NLS-1$

        helpTest(mc, "m.g.e1 LIKE 'abc'"); //$NON-NLS-1$
    }

    public void testMatchCriteria2() {
        IMatchCriteria mc = factory.createMatchCriteria();
        mc.setLeftExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
        mc.setRightExpression(factory.createConstant("%")); //$NON-NLS-1$
        mc.setEscapeChar('#');

        helpTest(mc, "m.g.e1 LIKE '%' ESCAPE '#'"); //$NON-NLS-1$
    }

    public void testMatchCriteria3() {
        IMatchCriteria mc = factory.createMatchCriteria();
        mc.setLeftExpression(factory.createElementSymbol("m.g.e1")); //$NON-NLS-1$
        mc.setRightExpression(factory.createConstant("abc")); //$NON-NLS-1$
        mc.setNegated(true);
        helpTest(mc, "m.g.e1 NOT LIKE 'abc'"); //$NON-NLS-1$
    }

    public void testINotCriteria1() {
        INotCriteria not = factory.createNotCriteria(factory.createIsNullCriteria(factory.createElementSymbol("m.g.e1"))); //$NON-NLS-1$
        helpTest(not, "NOT (m.g.e1 IS NULL)"); //$NON-NLS-1$
    }

    public void testINotCriteria2() {
        INotCriteria not = factory.createNotCriteria();
        helpTest(not, "NOT (<undefined>)"); //$NON-NLS-1$
    }

    public void testOption1() {
        IOption option = factory.createOption();
        helpTest(option, "OPTION"); //$NON-NLS-1$
    }

    public void testOrderBy1() {
        IOrderBy ob = factory.createOrderBy();
        ob.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

        helpTest(ob, "ORDER BY e1"); //$NON-NLS-1$
    }

    public void testOrderBy2() {
        IOrderBy ob = factory.createOrderBy();
        ob.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$
        ob.addVariable(factory.createAliasSymbol("x", factory.createElementSymbol("e2"))); //$NON-NLS-1$ //$NON-NLS-2$

        helpTest(ob, "ORDER BY e1, x"); //$NON-NLS-1$
    }

    public void testOrderBy3() {
        IOrderBy ob = factory.createOrderBy();
        ob.addVariable(factory.createElementSymbol("e1"), IOrderBy.DESC); //$NON-NLS-1$
        ob.addVariable(factory.createElementSymbol("x"), IOrderBy.DESC); //$NON-NLS-1$

        helpTest(ob, "ORDER BY e1 DESC, x DESC"); //$NON-NLS-1$
    }

    public void testQuery1() {
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createMultipleElementSymbol());
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        IQuery query = factory.createQuery();
        query.setSelect(select);
        query.setFrom(from);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g"); //$NON-NLS-1$
    }

    public void testQuery2() {
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createMultipleElementSymbol());
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        ICompareCriteria cc = factory.createCompareCriteria(
                                                 factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
        IGroupBy groupBy = factory.createGroupBy();
        groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        ICompareCriteria having = factory.createCompareCriteria(
                                                     factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$
        IOrderBy orderBy = factory.createOrderBy();
        orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

        IQuery query = factory.createQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testQuery3() {
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createMultipleElementSymbol());
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        IGroupBy groupBy = factory.createGroupBy();
        groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        ICompareCriteria having = factory.createCompareCriteria(
                                                     factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$
        IOrderBy orderBy = factory.createOrderBy();
        orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

        IQuery query = factory.createQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testQuery4() {
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createMultipleElementSymbol());
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        ICompareCriteria cc = factory.createCompareCriteria(
                                                 factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
        ICompareCriteria having = factory.createCompareCriteria(
                                                     factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$
        IOrderBy orderBy = factory.createOrderBy();
        orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

        IQuery query = factory.createQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testQuery5() {
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createMultipleElementSymbol());
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        ICompareCriteria cc = factory.createCompareCriteria(
                                                 factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
        IGroupBy groupBy = factory.createGroupBy();
        groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IOrderBy orderBy = factory.createOrderBy();
        orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

        IQuery query = factory.createQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testQuery6() {
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createMultipleElementSymbol());
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        ICompareCriteria cc = factory.createCompareCriteria(
                                                 factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
        IGroupBy groupBy = factory.createGroupBy();
        groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        ICompareCriteria having = factory.createCompareCriteria(
                                                     factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$

        IQuery query = factory.createQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0"); //$NON-NLS-1$
    }

    public void testQuery7() {
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createMultipleElementSymbol());
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        ICompareCriteria cc = factory.createCompareCriteria(
                                                 factory.createElementSymbol("e1"), ICompareCriteria.EQ, factory.createConstant(new Integer(5))); //$NON-NLS-1$
        IGroupBy groupBy = factory.createGroupBy();
        groupBy.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        ICompareCriteria having = factory.createCompareCriteria(
                                                     factory.createElementSymbol("e1"), ICompareCriteria.GT, factory.createConstant(new Integer(0))); //$NON-NLS-1$
        IOrderBy orderBy = factory.createOrderBy();
        orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

        IQuery query = factory.createQuery();
        query.setSelect(select);
        query.setFrom(from);
        query.setCriteria(cc);
        query.setGroupBy(groupBy);
        query.setHaving(having);
        query.setOrderBy(orderBy);

        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\tm.g\n\tWHERE\n\t\te1 = 5\n\tGROUP BY e1\n\tHAVING\n\t\te1 > 0\n\tORDER BY e1"); //$NON-NLS-1$
    }

    public void testSetCriteria1() {
        ISetCriteria sc = factory.createSetCriteria();
        sc.setExpression(factory.createElementSymbol("e1")); //$NON-NLS-1$
        sc.setValues(new ArrayList());

        helpTest(sc, "e1 IN ()"); //$NON-NLS-1$
    }

    public void testSetCriteria2() {
        ISetCriteria sc = factory.createSetCriteria();
        sc.setExpression(factory.createElementSymbol("e1")); //$NON-NLS-1$
        ArrayList values = new ArrayList();
        values.add(factory.createElementSymbol("e2")); //$NON-NLS-1$
        values.add(factory.createConstant("abc")); //$NON-NLS-1$
        sc.setValues(values);

        helpTest(sc, "e1 IN (e2, 'abc')"); //$NON-NLS-1$
    }

    public void testSetCriteria3() {
        ISetCriteria sc = factory.createSetCriteria();
        sc.setExpression(factory.createElementSymbol("e1")); //$NON-NLS-1$
        ArrayList values = new ArrayList();
        values.add(null);
        values.add(factory.createConstant("b")); //$NON-NLS-1$
        sc.setValues(values);

        helpTest(sc, "e1 IN (<undefined>, 'b')"); //$NON-NLS-1$
    }

    public void testSetCriteria4() {
        ISetCriteria sc = factory.createSetCriteria();
        sc.setExpression(factory.createElementSymbol("e1")); //$NON-NLS-1$
        ArrayList values = new ArrayList();
        values.add(factory.createElementSymbol("e2")); //$NON-NLS-1$
        values.add(factory.createConstant("abc")); //$NON-NLS-1$
        sc.setValues(values);
        sc.setNegated(true);
        helpTest(sc, "e1 NOT IN (e2, 'abc')"); //$NON-NLS-1$
    }

    public void testSetQuery1() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ISelect s2 = factory.createSelect();
        s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f2 = factory.createFrom();
        f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
        IQuery q2 = factory.createQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION);
        sq.setAll(false);
        sq.setLeftQuery(q1);
        sq.setRightQuery(q2);

        helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
    }

    public void testSetQuery2() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ISelect s2 = factory.createSelect();
        s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f2 = factory.createFrom();
        f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
        IQuery q2 = factory.createQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION);
        sq.setLeftQuery(q1);
        sq.setRightQuery(q2);

        helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION ALL\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
    }

    public void testSetQuery3() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ISelect s2 = factory.createSelect();
        s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f2 = factory.createFrom();
        f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
        IQuery q2 = factory.createQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        IOrderBy orderBy = factory.createOrderBy();
        orderBy.addVariable(factory.createElementSymbol("e1")); //$NON-NLS-1$

        ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);
        sq.setOrderBy(orderBy);

        helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2\nORDER BY e1"); //$NON-NLS-1$
    }

    public void testSetQuery4() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ISelect s2 = factory.createSelect();
        s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f2 = factory.createFrom();
        f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
        IQuery q2 = factory.createQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);

        helpTest(sq, "SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2"); //$NON-NLS-1$
    }

    public void testSetQuery5() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ISelect s2 = factory.createSelect();
        s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f2 = factory.createFrom();
        f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
        IQuery q2 = factory.createQuery();
        q2.setSelect(s2);
        q2.setFrom(f2);

        ISelect s3 = factory.createSelect();
        s3.addSymbol(factory.createElementSymbol("e3")); //$NON-NLS-1$
        IFrom f3 = factory.createFrom();
        f3.addGroup(factory.createGroupSymbol("m.g3")); //$NON-NLS-1$
        IQuery q3 = factory.createQuery();
        q3.setSelect(s3);
        q3.setFrom(f3);

        ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);

        ISetQuery sq2 = factory.createSetQuery(ISetQuery.Operation.UNION, true, q3, sq);

        helpTest(sq2,
                 "SELECT\n\t\te3\n\tFROM\n\t\tm.g3\nUNION ALL\n(SELECT\n\t\te1\n\tFROM\n\t\tm.g1\nUNION\nSELECT\n\t\te1\n\tFROM\n\t\tm.g2)"); //$NON-NLS-1$
    }

    public void testSubqueryFromClause1() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ISubqueryFromClause sfc = factory.createSubqueryFromClause("temp", q1); //$NON-NLS-1$
        helpTest(sfc, "(SELECT e1 FROM m.g1) AS temp"); //$NON-NLS-1$
    }

    public void testOptionalSubqueryFromClause1() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ISubqueryFromClause sfc = factory.createSubqueryFromClause("temp", q1); //$NON-NLS-1$
        sfc.setOptional(true);
        helpTest(sfc, "/*+ optional */ (SELECT e1 FROM m.g1) AS temp"); //$NON-NLS-1$
    }

    public void testSubquerySetCriteria1() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        IElementSymbol expr = factory.createElementSymbol("e2"); //$NON-NLS-1$

        ISubquerySetCriteria ssc = factory.createSubquerySetCriteria(expr, q1);
        helpTest(ssc, "e2 IN (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testSubquerySetCriteria2() {
        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        IElementSymbol expr = factory.createElementSymbol("e2"); //$NON-NLS-1$

        ISubquerySetCriteria ssc = factory.createSubquerySetCriteria(expr, q1);
        ssc.setNegated(true);
        helpTest(ssc, "e2 NOT IN (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testUnaryFromClause() {
        helpTest(factory.createUnaryFromClause(factory.createGroupSymbol("m.g1")), "m.g1"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testOptionalUnaryFromClause() {
        IUnaryFromClause unaryFromClause = factory.createUnaryFromClause(factory.createGroupSymbol("m.g1"));//$NON-NLS-1$
        unaryFromClause.setOptional(true);
        helpTest(unaryFromClause, "/*+ optional */ m.g1"); //$NON-NLS-1$ 
    }
    
    public void testIAggregateSymbol1() {
        IAggregateSymbol agg = factory.createAggregateSymbol("abc", false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(IAggregateSymbol.AggregateType.COUNT);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }
        
    public void testIAggregateSymbol2() {
        IAggregateSymbol agg = factory.createAggregateSymbol("abc", true, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(IAggregateSymbol.AggregateType.COUNT);
        helpTest(agg, "abc(DISTINCT 'abc')"); //$NON-NLS-1$
    }
        
    public void testIAggregateSymbol3() {
        IAggregateSymbol agg = factory.createAggregateSymbol("abc", false, null); //$NON-NLS-1$
        agg.setAggregateFunction(IAggregateSymbol.AggregateType.COUNT);
        helpTest(agg, "abc(*)"); //$NON-NLS-1$
    }
        
    public void testIAggregateSymbol4() {
        IAggregateSymbol agg = factory.createAggregateSymbol("abc", false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(IAggregateSymbol.AggregateType.AVG);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }
        
    public void testIAggregateSymbol5() {
        IAggregateSymbol agg = factory.createAggregateSymbol("abc", false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(IAggregateSymbol.AggregateType.SUM);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }
        
    public void testIAggregateSymbol6() {
        IAggregateSymbol agg = factory.createAggregateSymbol("abc", false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(IAggregateSymbol.AggregateType.MIN);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }
        
    public void testIAggregateSymbol7() {
        IAggregateSymbol agg = factory.createAggregateSymbol("abc", false, factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        agg.setAggregateFunction(IAggregateSymbol.AggregateType.MAX);
        helpTest(agg, "abc('abc')"); //$NON-NLS-1$
    }

    public void testAliasSymbol1() {
        IAliasSymbol as = factory.createAliasSymbol("x", factory.createElementSymbol("y")); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(as, "y AS x"); //$NON-NLS-1$
    }

    // Test alias symbol with reserved word
    public void testAliasSymbol2() {
        IAliasSymbol as = factory.createAliasSymbol("select", factory.createElementSymbol("y")); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(as, "y AS \"select\""); //$NON-NLS-1$
    }

    public void testMultipleElementSymbol() {
        helpTest(factory.createMultipleElementSymbol(), "*"); //$NON-NLS-1$
    }

    public void testConstantNull() {
        helpTest(factory.createConstant(null), "null"); //$NON-NLS-1$
    }

    public void testConstantString() {
        helpTest(factory.createConstant("abc"), "'abc'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantInteger() {
        helpTest(factory.createConstant(new Integer(5)), "5"); //$NON-NLS-1$
    }

    public void testConstantBigDecimal() {
        helpTest(factory.createConstant(new BigDecimal("5.4")), "5.4"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantStringWithTick() {
        helpTest(factory.createConstant("O'Leary"), "'O''Leary'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantStringWithTicks() {
        helpTest(factory.createConstant("'abc'"), "'''abc'''"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantStringWithMoreTicks() {
        helpTest(factory.createConstant("a'b'c"), "'a''b''c'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantStringWithDoubleTick() {
        helpTest(factory.createConstant("group=\"x\""), "'group=\"x\"'"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantBooleanTrue() {
        helpTest(factory.createConstant(Boolean.TRUE), "TRUE"); //$NON-NLS-1$
    }

    public void testConstantBooleanFalse() {
        helpTest(factory.createConstant(Boolean.FALSE), "FALSE"); //$NON-NLS-1$
    }

    public void testConstantDate() {
        helpTest(factory.createConstant(java.sql.Date.valueOf("2002-10-02")), "{d'2002-10-02'}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantTime() {
        helpTest(factory.createConstant(java.sql.Time.valueOf("5:00:00")), "{t'05:00:00'}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testConstantTimestamp() {
        helpTest(factory.createConstant(java.sql.Timestamp.valueOf("2002-10-02 17:10:35.0234")), "{ts'2002-10-02 17:10:35.0234'}"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testElementSymbol1() {
        IElementSymbol es = factory.createElementSymbol("elem"); //$NON-NLS-1$
        helpTest(es, "elem"); //$NON-NLS-1$
    }

    public void testElementSymbol2() {
        IElementSymbol es = factory.createElementSymbol("elem", false); //$NON-NLS-1$
        es.setGroupSymbol(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        helpTest(es, "elem"); //$NON-NLS-1$
    }

    public void testElementSymbol3() {
        IElementSymbol es = factory.createElementSymbol("m.g.elem", true); //$NON-NLS-1$
        es.setGroupSymbol(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        helpTest(es, "m.g.elem"); //$NON-NLS-1$
    }

    public void testElementSymbol4() {
        IElementSymbol es = factory.createElementSymbol("elem", true); //$NON-NLS-1$
        es.setGroupSymbol(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        helpTest(es, "m.g.elem"); //$NON-NLS-1$
    }

    public void testElementSymbol5() {
        IElementSymbol es = factory.createElementSymbol("m.g.select", false); //$NON-NLS-1$
        es.setGroupSymbol(factory.createGroupSymbol("m.g")); //$NON-NLS-1$
        helpTest(es, "\"select\""); //$NON-NLS-1$
    }

    public void testExpressionSymbol1() {
        IExpressionSymbol expr = factory.createExpressionSymbol("abc", factory.createConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(expr, "'abc'"); //$NON-NLS-1$
    }

    public void testFunction1() {
        IFunction func = factory.createFunction("concat", new IExpression[] { //$NON-NLS-1$
                                     factory.createConstant("a"), null //$NON-NLS-1$
                                     });
        helpTest(func, "concat('a', <undefined>)"); //$NON-NLS-1$
    }

    public void testFunction2() {
        IFunction func = factory.createFunction("now", new IExpression[] {}); //$NON-NLS-1$
        helpTest(func, "now()"); //$NON-NLS-1$
    }

    public void testFunction3() {
        IFunction func = factory.createFunction("concat", new IExpression[] {null, null}); //$NON-NLS-1$
        helpTest(func, "concat(<undefined>, <undefined>)"); //$NON-NLS-1$
    }

    public void testFunction4() {
        IFunction func1 = factory.createFunction("power", new IExpression[] { //$NON-NLS-1$
                                      factory.createConstant(new Integer(5)), factory.createConstant(new Integer(3))});
        IFunction func2 = factory.createFunction("power", new IExpression[] { //$NON-NLS-1$
                                      func1, factory.createConstant(new Integer(3))});
        IFunction func3 = factory.createFunction("+", new IExpression[] { //$NON-NLS-1$
                                      factory.createConstant(new Integer(1000)), func2});
        helpTest(func3, "(1000 + power(power(5, 3), 3))"); //$NON-NLS-1$
    }

    public void testFunction5() {
        IFunction func1 = factory.createFunction("concat", new IExpression[] { //$NON-NLS-1$
                                      factory.createElementSymbol("elem2"), //$NON-NLS-1$
                                          null});
        IFunction func2 = factory.createFunction("concat", new IExpression[] { //$NON-NLS-1$
                                      factory.createElementSymbol("elem1"), //$NON-NLS-1$
                                          func1});
        helpTest(func2, "concat(elem1, concat(elem2, <undefined>))"); //$NON-NLS-1$
    }

    public void testConvertFunction1() {
        IFunction func = factory.createFunction("convert", new IExpression[] { //$NON-NLS-1$
                                     factory.createConstant("5"), //$NON-NLS-1$
                                         factory.createConstant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "convert('5', integer)"); //$NON-NLS-1$
    }

    public void testConvertFunction2() {
        IFunction func = factory.createFunction("convert", new IExpression[] { //$NON-NLS-1$
                                     null, factory.createConstant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "convert(<undefined>, integer)"); //$NON-NLS-1$
    }

    public void testConvertFunction3() {
        IFunction func = factory.createFunction("convert", new IExpression[] { //$NON-NLS-1$
                                     factory.createConstant(null), factory.createConstant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "convert(null, integer)"); //$NON-NLS-1$
    }

    public void testConvertFunction5() {
        IFunction func = factory.createFunction("convert", null); //$NON-NLS-1$
        helpTest(func, "convert()"); //$NON-NLS-1$
    }

    public void testConvertFunction6() {
        IFunction func = factory.createFunction("convert", new IExpression[0]); //$NON-NLS-1$
        helpTest(func, "convert()"); //$NON-NLS-1$
    }

    public void testCastFunction1() {
        IFunction func = factory.createFunction("cast", new IExpression[] { //$NON-NLS-1$
                                     factory.createConstant("5"), //$NON-NLS-1$
                                         factory.createConstant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "cast('5' AS integer)"); //$NON-NLS-1$
    }

    public void testCastFunction2() {
        IFunction func = factory.createFunction("cast", new IExpression[] { //$NON-NLS-1$
                                     null, factory.createConstant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "cast(<undefined> AS integer)"); //$NON-NLS-1$
    }

    public void testCastFunction3() {
        IFunction func = factory.createFunction("cast", new IExpression[] { //$NON-NLS-1$
                                     factory.createConstant(null), factory.createConstant("integer") //$NON-NLS-1$
                                     });
        helpTest(func, "cast(null AS integer)"); //$NON-NLS-1$
    }

    public void testArithemeticFunction1() {
        IFunction func = factory.createFunction("-", new IExpression[] { //$NON-NLS-1$
                                     factory.createConstant(new Integer(-2)), factory.createConstant(new Integer(-1))});
        helpTest(func, "(-2 - -1)"); //$NON-NLS-1$
    }

    public void testGroupSymbol1() {
        IGroupSymbol gs = factory.createGroupSymbol("g"); //$NON-NLS-1$
        helpTest(gs, "g"); //$NON-NLS-1$
    }

    public void testGroupSymbol2() {
        IGroupSymbol gs = factory.createGroupSymbol("x", "g"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(gs, "g AS x"); //$NON-NLS-1$
    }

    public void testGroupSymbol3() {
        IGroupSymbol gs = factory.createGroupSymbol("vdb.g"); //$NON-NLS-1$
        helpTest(gs, "vdb.g"); //$NON-NLS-1$
    }

    public void testGroupSymbol4() {
        IGroupSymbol gs = factory.createGroupSymbol("x", "vdb.g"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(gs, "vdb.g AS x"); //$NON-NLS-1$
    }

    public void testGroupSymbol5() {
        IGroupSymbol gs = factory.createGroupSymbol("from", "m.g"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(gs, "m.g AS \"from\""); //$NON-NLS-1$
    }

    public void testGroupSymbol6() {
        IGroupSymbol gs = factory.createGroupSymbol("x", "on.select"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(gs, "\"on\".\"select\" AS x"); //$NON-NLS-1$
    }

    public void testExecNoParams() {
        IStoredProcedure proc = factory.createStoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        helpTest(proc, "EXEC myproc()"); //$NON-NLS-1$
    }

    public void testExecInputParam() {
        IStoredProcedure proc = factory.createStoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        ISPParameter param = factory.createSPParameter(1, factory.createReference(0));
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc(?)"); //$NON-NLS-1$
    }

    public void testExecInputOutputParam() {
        IStoredProcedure proc = factory.createStoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        ISPParameter param1 = factory.createSPParameter(1, factory.createConstant(new Integer(5)));
        param1.setParameterType(ISPParameter.ParameterInfo.IN);
        proc.setParameter(param1);

        ISPParameter param2 = factory.createSPParameter(2, ISPParameter.ParameterInfo.OUT, "x"); //$NON-NLS-1$
        proc.setParameter(param2);

        helpTest(proc, "EXEC myproc(5)"); //$NON-NLS-1$
    }

    public void testExecOutputInputParam() {
        IStoredProcedure proc = factory.createStoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$

        ISPParameter param2 = factory.createSPParameter(2, ISPParameter.ParameterInfo.OUT, "x"); //$NON-NLS-1$
        proc.setParameter(param2);

        ISPParameter param1 = factory.createSPParameter(1, factory.createConstant(new Integer(5)));
        param1.setParameterType(ISPParameter.ParameterInfo.IN);
        proc.setParameter(param1);

        helpTest(proc, "EXEC myproc(5)"); //$NON-NLS-1$
    }

    public void testExecReturnParam() {
        IStoredProcedure proc = factory.createStoredProcedure();
        proc.setProcedureName("myproc"); //$NON-NLS-1$

        ISPParameter param = factory.createSPParameter(1, ISPParameter.ParameterInfo.RETURN_VALUE, "ret"); //$NON-NLS-1$
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc()"); //$NON-NLS-1$
    }

    public void testExecNamedParam() {
        IStoredProcedure proc = factory.createStoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        ISPParameter param = factory.createSPParameter(1, factory.createReference(0));
        param.setName("p1");//$NON-NLS-1$
        proc.setParameter(param);
        helpTest(proc, "EXEC myproc(p1 => ?)"); //$NON-NLS-1$
    }

    public void testExecNamedParams() {
        IStoredProcedure proc = factory.createStoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        ISPParameter param = factory.createSPParameter(1, factory.createReference(0));
        param.setName("p1");//$NON-NLS-1$
        proc.setParameter(param);
        ISPParameter param2 = factory.createSPParameter(2, factory.createReference(0));
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
        IStoredProcedure proc = factory.createStoredProcedure();
        proc.setDisplayNamedParameters(true);
        proc.setProcedureName("myproc"); //$NON-NLS-1$
        ISPParameter param = factory.createSPParameter(1, factory.createReference(0));
        param.setName("in");//$NON-NLS-1$
        proc.setParameter(param);
        ISPParameter param2 = factory.createSPParameter(2, factory.createReference(0));
        param2.setName("in2");//$NON-NLS-1$
        proc.setParameter(param2);
        helpTest(proc, "EXEC myproc(\"in\" => ?, in2 => ?)"); //$NON-NLS-1$
    }

    // Test methods for Update Procedure Language Objects

    public void testDeclareStatement() {
        IDeclareStatement dclStmt = factory.createDeclareStatement(factory.createElementSymbol("a"), "String"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTest(dclStmt, "DECLARE String a;"); //$NON-NLS-1$
    }

    public void testRaiseStatement() {
        IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
        helpTest(errStmt, "RAISE 'My Error';"); //$NON-NLS-1$
    }

    public void testRaiseStatementWithExpression() {
        IRaiseStatement errStmt = factory.createRaiseStatement(factory.createElementSymbol("a")); //$NON-NLS-1$
        helpTest(errStmt, "RAISE a;"); //$NON-NLS-1$
    }

    public void testAssignmentStatement1() {
        IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
        helpTest(assigStmt, "a = 1;"); //$NON-NLS-1$
    }

    public void FAILINGtestAssignmentStatement2() {
        // TODO fix this test
        IQuery q1 = factory.createQuery();
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createElementSymbol("x")); //$NON-NLS-1$
        q1.setSelect(select);
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
        q1.setFrom(from);

        IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), q1); //$NON-NLS-1$
        helpTest(assigStmt, "a = SELECT x FROM g;"); //$NON-NLS-1$
    }

 
    public void testCommandStatement1() {
        IQuery q1 = factory.createQuery();
        ISelect select = factory.createSelect();
        select.addSymbol(factory.createElementSymbol("x")); //$NON-NLS-1$
        q1.setSelect(select);
        IFrom from = factory.createFrom();
        from.addGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
        q1.setFrom(from);

        ICommandStatement cmdStmt = factory.createCommandStatement(q1);
        helpTest(cmdStmt, "SELECT x FROM g;"); //$NON-NLS-1$
    }

    public void testCommandStatement2() {
        IDelete d1 = factory.createDelete();
        d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
        ICommandStatement cmdStmt = factory.createCommandStatement(d1);
        helpTest(cmdStmt, "DELETE FROM g;"); //$NON-NLS-1$
    }

    public void testBlock1() {
        IDelete d1 = factory.createDelete();
        d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
        ICommandStatement cmdStmt = factory.createCommandStatement(d1);
        IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
        IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
        IBlock b = factory.createBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        helpTest(b, "BEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$
    }

    public void testCreateUpdateProcedure1() {
        IDelete d1 = factory.createDelete();
        d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
        ICommandStatement cmdStmt = factory.createCommandStatement(d1);
        IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
        IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
        IBlock b = factory.createBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        ICreateProcedureCommand cup = factory.createCreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$
    }

    public void testCreateUpdateProcedure2() {
        IDelete d1 = factory.createDelete();
        d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
        ICommandStatement cmdStmt = factory.createCommandStatement(d1);
        IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
        IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
        IBlock b = factory.createBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        ICreateProcedureCommand cup = factory.createCreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$
    }

    public void testCreateUpdateProcedure3() {
        IDelete d1 = factory.createDelete();
        d1.setGroup(factory.createGroupSymbol("g")); //$NON-NLS-1$
        ICommandStatement cmdStmt = factory.createCommandStatement(d1);
        IAssignmentStatement assigStmt = factory.createAssignmentStatement(factory.createElementSymbol("a"), factory.createConstant(new Integer(1))); //$NON-NLS-1$
        IRaiseStatement errStmt = factory.createRaiseStatement(factory.createConstant("My Error")); //$NON-NLS-1$
        IBlock b = factory.createBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        ICreateProcedureCommand cup = factory.createCreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\n\tDELETE FROM g;\n\ta = 1;\n\tRAISE 'My Error';\nEND"); //$NON-NLS-1$
    }

    public void testSubqueryCompareCriteria1() {

        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        IElementSymbol expr = factory.createElementSymbol("e2"); //$NON-NLS-1$

        ISubqueryCompareCriteria scc = factory.createSubqueryCompareCriteria(expr, q1, ICompareCriteria.EQ,
                                                                  ISubqueryCompareCriteria.ANY);

        helpTest(scc, "e2 = ANY (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testSubqueryCompareCriteria2() {

        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        IElementSymbol expr = factory.createElementSymbol("e2"); //$NON-NLS-1$

        ISubqueryCompareCriteria scc = factory.createSubqueryCompareCriteria(expr, q1, ICompareCriteria.LE,
                                                                  ISubqueryCompareCriteria.SOME);

        helpTest(scc, "e2 <= SOME (SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testScalarSubquery() {

        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        IScalarSubquery obj = factory.createScalarSubquery(q1);

        helpTest(obj, "(SELECT e1 FROM m.g1)"); //$NON-NLS-1$
    }

    public void testNewSubqueryObjects() {

        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        IFrom f1 = factory.createFrom();
        f1.addGroup(factory.createGroupSymbol("m.g1")); //$NON-NLS-1$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);
        q1.setFrom(f1);

        ISelect s2 = factory.createSelect();
        s2.addSymbol(factory.createElementSymbol("e1")); //$NON-NLS-1$
        s2.addSymbol(factory.createExpressionSymbol("blargh", factory.createScalarSubquery(q1))); //$NON-NLS-1$
        IFrom f2 = factory.createFrom();
        f2.addGroup(factory.createGroupSymbol("m.g2")); //$NON-NLS-1$
        ICriteria left = factory.createSubqueryCompareCriteria(
                                                    factory.createElementSymbol("e3"), q1, ICompareCriteria.GE, ISubqueryCompareCriteria.ANY); //$NON-NLS-1$
        ICriteria right = factory.createExistsCriteria(q1);
        ICriteria outer = factory.createCompoundCriteria(ICompoundCriteria.LogicalOperator.AND, left, right);
        IQuery q2 = factory.createQuery();
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

        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createAliasSymbol("FOO", factory.createExpressionSymbol("xxx", factory.createConstant("A")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);

        ISelect s2 = factory.createSelect();
        s2.addSymbol(factory.createAliasSymbol("FOO", factory.createExpressionSymbol("xxx", factory.createConstant("B")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        IQuery q2 = factory.createQuery();
        q2.setSelect(s2);

        ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);

        helpTest(sq, expected);
    }

    /**
     * For some reason this test was outputting SELECT 'A' AS FOO UNION SELECT 'A' AS FOO Same as above except that
     * ExpressionSymbols' internal names (which aren't visible in the query) are different
     */
    public void testSetQueryUnionOfLiteralsCase3102a() {

        String expected = "SELECT\n\t\t'A' AS FOO\nUNION\nSELECT\n\t\t'B' AS FOO"; //$NON-NLS-1$

        ISelect s1 = factory.createSelect();
        s1.addSymbol(factory.createAliasSymbol("FOO", factory.createExpressionSymbol("xxx", factory.createConstant("A")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        IQuery q1 = factory.createQuery();
        q1.setSelect(s1);

        ISelect s2 = factory.createSelect();
        s2.addSymbol(factory.createAliasSymbol("FOO", factory.createExpressionSymbol("yyy", factory.createConstant("B")))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        IQuery q2 = factory.createQuery();
        q2.setSelect(s2);

        ISetQuery sq = factory.createSetQuery(ISetQuery.Operation.UNION, false, q1, q2);

        helpTest(sq, expected);
    }

    public void FAILINGtestNullExpressionInNamedParameter() {
        // TODO fix this test
        String expected = "EXEC sp1(PARAM => sp1.PARAM)"; //$NON-NLS-1$

        IStoredProcedure sp = factory.createStoredProcedure();
        sp.setDisplayNamedParameters(true);
        sp.setProcedureName("sp1"); //$NON-NLS-1$

        ISPParameter param = factory.createSPParameter(0, ISPParameter.ParameterInfo.IN, "sp1.PARAM"); //$NON-NLS-1$
        sp.setParameter(param);

        helpTest(sp, expected);
    }

    public void testQueryWithMakeDep() {
        IQuery query = factory.createQuery();
        ISelect select = factory.createSelect(Arrays.asList(factory.createMultipleElementSymbol()));
        IFromClause fromClause = factory.createUnaryFromClause(factory.createGroupSymbol("a")); //$NON-NLS-1$
        fromClause.setMakeDep(true);
        IFrom from = factory.createFrom(Arrays.asList(fromClause));
        query.setSelect(select);
        query.setFrom(from);
        String expectedSQL = "SELECT\n\t\t*\n\tFROM\n\t\t/*+ MAKEDEP */ a";  //$NON-NLS-1$ 
        helpTest(query, expectedSQL);
    }

    public void testQueryWithJoinPredicateMakeDep() {
        IQuery query = factory.createQuery();
        ISelect select = factory.createSelect(Arrays.asList(factory.createMultipleElementSymbol()));
        IFromClause fromClause = factory.createUnaryFromClause(factory.createGroupSymbol("a")); //$NON-NLS-1$
        fromClause.setMakeNotDep(true);
        IFromClause fromClause1 = factory.createUnaryFromClause(factory.createGroupSymbol("b")); //$NON-NLS-1$
        fromClause1.setMakeDep(true);
        IFrom from = factory.createFrom(Arrays.asList(factory.createJoinPredicate(fromClause, fromClause1, IJoinPredicate.JoinType.JOIN_CROSS)));
        query.setSelect(select);
        query.setFrom(from);
        helpTest(query, "SELECT\n\t\t*\n\tFROM\n\t\t/*+ MAKENOTDEP */ a CROSS JOIN /*+ MAKEDEP */ b"); //$NON-NLS-1$ 
    }

    public void testQueryWithNestedJoinPredicateMakeDep() throws Exception {
        IQuery query = (IQuery)parser.parseCommand("Select a From (db.g1 JOIN db.g2 ON a = b) makedep LEFT OUTER JOIN db.g3 ON a = c"); //$NON-NLS-1$
        helpTest(query, "SELECT\n\t\ta\n\tFROM\n\t\t/*+ MAKEDEP */ (db.g1 INNER JOIN db.g2 ON a = b) LEFT OUTER JOIN db.g3 ON a = c"); //$NON-NLS-1$
    }
    
    public void testCast() throws Exception {
        IExpression ex = parser.parseExpression("cast(x as integer)"); //$NON-NLS-1$
        helpTest(ex, "cast(x AS integer)"); //$NON-NLS-1$
    }
    
    public void testXMLPi() throws Exception {
        IExpression ex = parser.parseExpression("xmlpi(name foo, 'bar')"); //$NON-NLS-1$
        helpTest(ex, "xmlpi(NAME foo, 'bar')"); //$NON-NLS-1$
    }
    
    public void testXMLPi1() throws Exception {
        IExpression ex = parser.parseExpression("xmlpi(name \"table\", 'bar')"); //$NON-NLS-1$
        helpTest(ex, "xmlpi(NAME \"table\", 'bar')"); //$NON-NLS-1$
    }
    
    public void testTimestampAdd() throws Exception {
        IExpression ex = parser.parseExpression("timestampadd(SQL_TSI_DAY, x, y)"); //$NON-NLS-1$
        helpTest(ex, "timestampadd(SQL_TSI_DAY, x, y)"); //$NON-NLS-1$
    }
    
    public void testXMLAgg() throws Exception {
        ILanguageObject ex = parser.parseCommand("select xmlagg(x order by y)"); //$NON-NLS-1$
        helpTest(ex, "SELECT\n\t\tXMLAGG(x ORDER BY y)"); //$NON-NLS-1$
    }

    public void testXMLElement() throws Exception {
        ILanguageObject ex = parser.parseExpression("xmlelement(name y, xmlattributes('x' as foo), q)"); //$NON-NLS-1$
        helpTest(ex, "XMLELEMENT(NAME y, XMLATTRIBUTES('x' AS foo), q)"); //$NON-NLS-1$
    }
    
    public void testCacheHint() throws Exception {
        ILanguageObject ex = parser.parseCommand("/*+ cache(pref_mem) */ select * from db.g2"); //$NON-NLS-1$
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
