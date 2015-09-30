/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.resolver.AbstractTestResolver;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.ProcedureReservedWords;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.v8.Test8Factory;

/**
 *
 */
@SuppressWarnings( {"nls" , "javadoc"})
public class Test8Resolver extends AbstractTestResolver {

    private Test8Factory factory;

    protected Test8Resolver(Version teiidVersion) {
        super(teiidVersion);
    }
   
    public Test8Resolver() {
        this(Version.TEIID_8_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test8Factory(getQueryParser());

        return factory;
    }

    @Test
    public void testSelectExpressions() {
        Query resolvedQuery = (Query)helpResolve("SELECT e1, concat(e1, 's'), concat(e1, 's') as c FROM pm1.g1"); //$NON-NLS-1$
        helpCheckFrom(resolvedQuery, new String[] {"pm1.g1"}); //$NON-NLS-1$
        helpCheckSelect(resolvedQuery, new String[] {"pm1.g1.e1", "expr2", "c"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpCheckElements(resolvedQuery.getSelect(), new String[] {"pm1.g1.e1", "pm1.g1.e1", "pm1.g1.e1"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                          new String[] {"pm1.g1.e1", "pm1.g1.e1", "pm1.g1.e1"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Test
    public void testSelectCountStar() {
        Query resolvedQuery = (Query)helpResolve("SELECT count(*) FROM pm1.g1"); //$NON-NLS-1$
        helpCheckFrom(resolvedQuery, new String[] {"pm1.g1"}); //$NON-NLS-1$
        helpCheckSelect(resolvedQuery, new String[] {"expr1"}); //$NON-NLS-1$
        helpCheckElements(resolvedQuery.getSelect(), new String[] {}, new String[] {});
    }

    @Test
    public void testConversionPossible() {
        helpResolve("SELECT dayofmonth('2002-01-01') FROM pm1.g1"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testResolveParameters() throws Exception {
        List bindings = new ArrayList();
        bindings.add("pm1.g2.e1"); //$NON-NLS-1$
        bindings.add("pm1.g2.e2"); //$NON-NLS-1$

        Query resolvedQuery = (Query)helpResolveWithBindings("SELECT pm1.g1.e1, ? FROM pm1.g1 WHERE pm1.g1.e1 = ?", metadata, bindings); //$NON-NLS-1$

        helpCheckFrom(resolvedQuery, new String[] {"pm1.g1"}); //$NON-NLS-1$
        helpCheckSelect(resolvedQuery, new String[] {"pm1.g1.e1", "expr2"}); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckElements(resolvedQuery.getCriteria(), new String[] {"pm1.g1.e1", "pm1.g2.e2"}, //$NON-NLS-1$ //$NON-NLS-2$
                          new String[] {"pm1.g1.e1", "pm1.g2.e2"}); //$NON-NLS-1$ //$NON-NLS-2$

    }

    @Test
    public void testStoredQuery1() {
        StoredProcedure proc = (StoredProcedure)helpResolve("EXEC pm1.sq2('abc')"); //$NON-NLS-1$

        // Check number of resolved parameters
        assertEquals("Did not get expected parameter count", 2, proc.getParameterCount()); //$NON-NLS-1$

        // Check resolved parameters
        SPParameter param1 = proc.getParameter(2);
        helpCheckParameter(param1, SPParameter.RESULT_SET, 2, "pm1.sq2.ret", java.sql.ResultSet.class, null); //$NON-NLS-1$

        SPParameter param2 = proc.getParameter(1);
        helpCheckParameter(param2,
                           SPParameter.IN,
                           1,
                           "pm1.sq2.in", DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass(), getFactory().newConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * per defect 8211 - Input params do not have to be numbered sequentially in metadata.  For example,
     * the first input param can be #1 and the second input param can be #3.  (This occurs in 
     * QueryBuilder's metadata where the return param happens to be created in between the two
     * input params and is numbered #2, but is not loaded into QueryBuilder's runtime env).  
     * When the user's query is parsed and resolved, the placeholder
     * input params are numbered #1 and #2.  This test tests that this disparity in ordering should not
     * be a problem as long as RELATIVE ordering is in synch.
     */
    @Test
    public void testStoredQueryParamOrdering_8211() {
        StoredProcedure proc = (StoredProcedure)helpResolve("EXEC pm1.sq3a('abc', 123)"); //$NON-NLS-1$

        // Check number of resolved parameters
        assertEquals("Did not get expected parameter count", 3, proc.getParameterCount()); //$NON-NLS-1$

        // Check resolved parameters
        SPParameter param1 = proc.getParameter(1);
        helpCheckParameter(param1,
                           SPParameter.IN,
                           1,
                           "pm1.sq3a.in", DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass(), getFactory().newConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$

        SPParameter param2 = proc.getParameter(2);
        helpCheckParameter(param2,
                           SPParameter.IN,
                           2,
                           "pm1.sq3a.in2", DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(), getFactory().newConstant(new Integer(123))); //$NON-NLS-1$
    }

    @Test
    public void testCaseOverInlineView() throws Exception {
        String sql = "SELECT CASE WHEN x > 0 THEN 1.0 ELSE 2.0 END FROM (SELECT e2 AS x FROM pm1.g1) AS g"; //$NON-NLS-1$
        Command c = helpResolve(sql);
        assertEquals(sql, c.toString());
        verifyProjectedTypes(c, new Class[] {BigDecimal.class});
    }

    @Test
    public void testXMLQueryWithVariable() {
        String sql = "CREATE VIRTUAL PROCEDURE " //$NON-NLS-1$
                     + "BEGIN " //$NON-NLS-1$
                     + "declare string x = '1'; " //$NON-NLS-1$
                     + "select * from xmltest.doc1 where node1 = x; " //$NON-NLS-1$
                     + "end "; //$NON-NLS-1$

        CreateProcedureCommand command = (CreateProcedureCommand)helpResolve(sql);

        CommandStatement cmdStmt = (CommandStatement)command.getBlock().getStatements().get(1);

        CompareCriteria criteria = (CompareCriteria)((Query)cmdStmt.getCommand()).getCriteria();

        assertEquals(ProcedureReservedWords.VARIABLES, ((ElementSymbol)criteria.getRightExpression()).getGroupSymbol().getName());
    }

    @Test
    public void testPowerWithLong() throws Exception {
        String sql = "SELECT power(10, 999999999999)"; //$NON-NLS-1$

        helpResolve(sql);
    }

    @Test
    public void testImplicitTempInsertWithNoColumns() {
        StringBuffer proc = new StringBuffer("CREATE VIRTUAL PROCEDURE") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  create local temporary table #matt (x integer);") //$NON-NLS-1$
        .append("\n  insert into #matt values (1);") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        Command cmd = helpResolve(proc.toString());

        String sExpected = "CREATE VIRTUAL PROCEDURE\nBEGIN\nCREATE LOCAL TEMPORARY TABLE #matt (x integer);\nINSERT INTO #matt (x) VALUES (1);\nEND\n\tCREATE LOCAL TEMPORARY TABLE #matt (x integer)\n\tINSERT INTO #matt (x) VALUES (1)\n"; //$NON-NLS-1$
        String sActual = cmd.printCommandTree();
        assertEquals(sExpected, sActual);
    }

    //return should be first, then out
    @Test
    public void testParamOrder() {
        Query resolvedQuery = (Query)helpResolve("SELECT * FROM (exec pm4.spRetOut()) as a", getMetadataFactory().exampleBQTCached()); //$NON-NLS-1$

        assertEquals("a.ret", resolvedQuery.getProjectedSymbols().get(0).toString()); //$NON-NLS-1$
    }

    @Test
    public void testObjectTableWithParam() {
        helpResolve("select * from objecttable('x + 1' passing ? as x columns obj OBJECT '') as y"); //$NON-NLS-1$
    }

    @Test
    public void testArrayCase() {
        Command c = helpResolve("select case when e1 is null then array_agg(e4) when e2 is null then array_agg(e4+1) end from pm1.g1 group by e1, e2"); //$NON-NLS-1$
        assertTrue(c.getProjectedSymbols().get(0).getType().isArray());
    }

    @Test
    public void testArrayCase1() {
        Command c = helpResolve("select case when e1 is null then array_agg(e1) when e2 is null then array_agg(e4+1) end from pm1.g1 group by e1, e2"); //$NON-NLS-1$
        assertTrue(c.getProjectedSymbols().get(0).getType().isArray());
    }

    @Test
    public void testForeignTempInvalidModel() {
        String sql = "create foreign temporary table x (y string) on x"; //$NON-NLS-1$
        helpResolveException(sql, "TEIID31134 Could not create foreign temporary table, since schema x does not exist."); //$NON-NLS-1$
    }

    @Test
    public void testForeignTempInvalidModel1() {
        String sql = "create foreign temporary table x (y string) on vm1"; //$NON-NLS-1$
        helpResolveException(sql, "TEIID31135 Could not create foreign temporary table, since schema vm1 is not physical."); //$NON-NLS-1$ 
    }

}
