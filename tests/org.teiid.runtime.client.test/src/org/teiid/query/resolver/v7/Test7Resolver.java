/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
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
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.v7.Test7Factory;

/**
 *
 */
@SuppressWarnings( {"nls" , "javadoc"})
public class Test7Resolver extends AbstractTestResolver {

    private Test7Factory factory;

    /**
     *
     */
    public Test7Resolver() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test7Factory(getQueryParser());

        return factory;
    }

    @Test
    public void testSelectExpressions() {
        Query resolvedQuery = (Query)helpResolve("SELECT e1, concat(e1, 's'), concat(e1, 's') as c FROM pm1.g1"); //$NON-NLS-1$
        helpCheckFrom(resolvedQuery, new String[] {"pm1.g1"}); //$NON-NLS-1$
        helpCheckSelect(resolvedQuery, new String[] {"pm1.g1.e1", "expr", "c"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpCheckElements(resolvedQuery.getSelect(), new String[] {"pm1.g1.e1", "pm1.g1.e1", "pm1.g1.e1"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                          new String[] {"pm1.g1.e1", "pm1.g1.e1", "pm1.g1.e1"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Test
    public void testSelectCountStar() {
        Query resolvedQuery = (Query)helpResolve("SELECT count(*) FROM pm1.g1"); //$NON-NLS-1$
        helpCheckFrom(resolvedQuery, new String[] {"pm1.g1"}); //$NON-NLS-1$
        helpCheckSelect(resolvedQuery, new String[] {"count"}); //$NON-NLS-1$
        helpCheckElements(resolvedQuery.getSelect(), new String[] {}, new String[] {});
    }

    @Test
    public void testConversionNotPossible() {
        helpResolveException("SELECT dayofmonth('2002-01-01') FROM pm1.g1", "Error Code:ERR.015.008.0040 Message:The function 'dayofmonth('2002-01-01')' is a valid function form, but the arguments do not match a known type signature and cannot be converted using implicit type conversions."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testResolveParameters() throws Exception {
        List bindings = new ArrayList();
        bindings.add("pm1.g2.e1"); //$NON-NLS-1$
        bindings.add("pm1.g2.e2"); //$NON-NLS-1$

        Query resolvedQuery = (Query)helpResolveWithBindings("SELECT pm1.g1.e1, ? FROM pm1.g1 WHERE pm1.g1.e1 = ?", metadata, bindings); //$NON-NLS-1$

        helpCheckFrom(resolvedQuery, new String[] {"pm1.g1"}); //$NON-NLS-1$
        helpCheckSelect(resolvedQuery, new String[] {"pm1.g1.e1", "expr"}); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckElements(resolvedQuery.getCriteria(), new String[] {"pm1.g1.e1", "pm1.g2.e2"}, //$NON-NLS-1$
                          new String[] {"pm1.g1.e1", "pm1.g2.e2"}); //$NON-NLS-1$

    }

    @Test
    public void testStoredQuery1() {
        StoredProcedure proc = (StoredProcedure)helpResolve("EXEC pm1.sq2('abc')"); //$NON-NLS-1$

        // Check number of resolved parameters
        Collection<SPParameter> params = proc.getParameters();
        assertEquals("Did not get expected parameter count", 2, proc.getParameterCount()); //$NON-NLS-1$

        // Check resolved parameters
        Iterator<SPParameter> iterator = params.iterator();
        SPParameter param1 = iterator.next();
        helpCheckParameter(param1,
                           SPParameter.IN,
                           1,
                           "pm1.sq2.in", DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass(), getFactory().newConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$

        SPParameter param2 = iterator.next();
        helpCheckParameter(param2, SPParameter.RESULT_SET, 2, "pm1.sq2.ret", java.sql.ResultSet.class, null); //$NON-NLS-1$
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
        Collection<SPParameter> params = proc.getParameters();
        assertEquals("Did not get expected parameter count", 3, params.size()); //$NON-NLS-1$

        // Check resolved parameters
        Iterator<SPParameter> parameters = params.iterator();
        SPParameter param1 = parameters.next();
        helpCheckParameter(param1,
                           SPParameter.IN,
                           1,
                           "pm1.sq3a.in", DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass(), getFactory().newConstant("abc")); //$NON-NLS-1$ //$NON-NLS-2$

        SPParameter param2 = parameters.next();
        helpCheckParameter(param2,
                           SPParameter.IN,
                           2,
                           "pm1.sq3a.in2", DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(), getFactory().newConstant(new Integer(123))); //$NON-NLS-1$
    }

    @Test
    public void testInputToInputsConversion() throws Exception {
        String procedure = "CREATE PROCEDURE  "; //$NON-NLS-1$
        procedure = procedure + "BEGIN\n"; //$NON-NLS-1$
        procedure = procedure + "DECLARE integer var1;\n"; //$NON-NLS-1$
        procedure = procedure + "ROWS_UPDATED = (Select pm1.g1.e2 from pm1.g1 where e2=INPUTS.e2);\n"; //$NON-NLS-1$
        procedure = procedure + "END\n"; //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e2=40"; //$NON-NLS-1$

        Command command = helpResolveUpdateProcedure(procedure, userUpdateStr);
        assertEquals("CREATE PROCEDURE\nBEGIN\nDECLARE integer var1;\nROWS_UPDATED = (SELECT pm1.g1.e2 FROM pm1.g1 WHERE e2 = INPUTS.e2);\nEND",
                     command.toString());
    }

    @Test
    public void testCaseOverInlineView() throws Exception {
        String sql = "SELECT CASE WHEN x > 0 THEN 1.0 ELSE 2.0 END FROM (SELECT e2 AS x FROM pm1.g1) AS g"; //$NON-NLS-1$
        Command c = helpResolve(sql);
        assertEquals(sql, c.toString());
        verifyProjectedTypes(c, new Class[] {Double.class});
    }

    @Test
    public void testXMLQueryWithVariable() {
        String sql = "CREATE VIRTUAL PROCEDURE " //$NON-NLS-1$
                     + "BEGIN " //$NON-NLS-1$
                     + "declare string x = '1'; " //$NON-NLS-1$
                     + "select * from xmltest.doc1 where node1 = x; " //$NON-NLS-1$
                     + "end "; //$NON-NLS-1$

        CreateUpdateProcedureCommand command = (CreateUpdateProcedureCommand)helpResolve(sql);

        CommandStatement cmdStmt = (CommandStatement)command.getBlock().getStatements().get(1);

        CompareCriteria criteria = (CompareCriteria)((Query)cmdStmt.getCommand()).getCriteria();

        assertEquals(ProcedureReservedWords.VARIABLES,
                     ((ElementSymbol)criteria.getRightExpression()).getGroupSymbol().getCanonicalName());
    }

    @Test
    public void testPowerWithLong_Fails() throws Exception {
        String sql = "SELECT power(10, 999999999999)"; //$NON-NLS-1$

        helpResolveException(sql);
    }

    @Test
    public void testUpdateError() {
        String userUpdateStr = "UPDATE vm1.g2 SET e1='x'"; //$NON-NLS-1$

        helpResolveException(userUpdateStr,
                             metadata,
                             "Error Code:ERR.015.008.0009 Message:Update is not allowed on the view vm1.g2: a procedure must be defined to handle the Update."); //$NON-NLS-1$
    }

    @Test
    public void testInsertError() {
        String userUpdateStr = "INSERT into vm1.g2 (e1) values ('x')"; //$NON-NLS-1$

        helpResolveException(userUpdateStr,
                             metadata,
                             "Error Code:ERR.015.008.0009 Message:Insert is not allowed on the view vm1.g2: a procedure must be defined to handle the Insert."); //$NON-NLS-1$
    }

    @Test
    public void testDeleteError() {
        String userUpdateStr = "DELETE from vm1.g2 where e1='x'"; //$NON-NLS-1$

        helpResolveException(userUpdateStr,
                             metadata,
                             "Error Code:ERR.015.008.0009 Message:Delete is not allowed on the view vm1.g2: a procedure must be defined to handle the Delete."); //$NON-NLS-1$
    }

    @Test
    public void testImplicitTempInsertWithNoColumns() {
        StringBuffer proc = new StringBuffer("CREATE VIRTUAL PROCEDURE") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  create local temporary table #matt (x integer);") //$NON-NLS-1$
        .append("\n  insert into #matt values (1);") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        Command cmd = helpResolve(proc.toString());

        String sExpected = "CREATE VIRTUAL PROCEDURE\nBEGIN\nCREATE LOCAL TEMPORARY TABLE #matt (x integer);\nINSERT INTO #matt (#matt.x) VALUES (1);\nEND\n\tCREATE LOCAL TEMPORARY TABLE #matt (x integer)\n\tINSERT INTO #matt (#matt.x) VALUES (1)\n"; //$NON-NLS-1$
        String sActual = cmd.printCommandTree();
        assertEquals(sExpected, sActual);
    }

    //return should be first, then out
    @Test
    public void testParamOrder() {
        Query resolvedQuery = (Query)helpResolve("SELECT * FROM (exec pm4.spRetOut()) as a", getMetadataFactory().exampleBQTCached()); //$NON-NLS-1$

        List<Expression> projectedSymbols = resolvedQuery.getProjectedSymbols();
        assertFalse(projectedSymbols.isEmpty());
        Expression symbol = projectedSymbols.get(0);
        assertTrue(symbol instanceof Symbol);
        assertEquals("a.ret", ((Symbol) symbol).getName());
    }
}
