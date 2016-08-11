/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.resolver.v9;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.Table;
import org.teiid.query.metadata.TransformationMetadata;
import org.teiid.query.resolver.AbstractTestProcedureResolving;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.lang.SPParameter;
import org.teiid.query.sql.lang.StoredProcedure;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.TriggerAction;
import org.teiid.query.sql.symbol.Array;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Symbol;
import org.teiid.query.sql.v9.Test9Factory;
import org.teiid.query.unittest.RealMetadataFactory;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class Test9ProcedureResolving extends AbstractTestProcedureResolving {

    private static final String NEW_LINE = "\n";

    private Test9Factory factory;

    protected Test9ProcedureResolving(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test9ProcedureResolving() {
        this(Version.TEIID_9_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test9Factory(getQueryParser());

        return factory;
    }

    @Override
    protected TriggerAction helpResolveUpdateProcedure(String procedure, String userUpdateStr, Table.TriggerEvent procedureType)
        throws Exception {
        IQueryMetadataInterface metadata = getMetadataFactory().exampleUpdateProc(procedureType, procedure);
        return (TriggerAction)resolveProcedure(userUpdateStr, metadata);
    }

    @Test
    public void testAmbigousInput() {
        String procedure = "FOR EACH ROW "; //$NON-NLS-1$
        procedure = procedure + "BEGIN ATOMIC\n"; //$NON-NLS-1$
        procedure = procedure + "select e1;\n"; //$NON-NLS-1$
        procedure = procedure + "END\n"; //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpFailUpdateProcedure(procedure,
                                userUpdateStr,
                                Table.TriggerEvent.UPDATE,
                                "TEIID31117 Element \"e1\" is ambiguous and should be qualified, at a single scope it exists in [CHANGING, \"NEW\", \"OLD\"]"); //$NON-NLS-1$
    }

    @Test
    public void testLoopRedefinition() {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  declare string var1;") //$NON-NLS-1$
        .append("\n  LOOP ON (SELECT pm1.g1.e1 FROM pm1.g1) AS loopCursor") //$NON-NLS-1$
        .append("\n  BEGIN") //$NON-NLS-1$
        .append("\n    LOOP ON (SELECT pm1.g2.e1 FROM pm1.g2 WHERE loopCursor.e1 = pm1.g2.e1) AS loopCursor") //$NON-NLS-1$
        .append("\n    BEGIN") //$NON-NLS-1$
        .append("\n      var1 = CONCAT(var1, CONCAT(' ', loopCursor.e1));") //$NON-NLS-1$
        .append("\n    END") //$NON-NLS-1$
        .append("\n  END") //$NON-NLS-1$
        .append("\n  END"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpFailUpdateProcedure(proc.toString(),
                                userUpdateStr,
                                Table.TriggerEvent.UPDATE,
                                "TEIID30124 Loop cursor or exception group name loopCursor already exists."); //$NON-NLS-1$
    }

    @Test
    public void testTempGroupElementShouldNotBeResolable() {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  select 1 as a into #temp;") //$NON-NLS-1$
        .append("\n  select #temp.a from pm1.g1;") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpFailUpdateProcedure(proc.toString(),
                                userUpdateStr,
                                Table.TriggerEvent.UPDATE,
                                "TEIID31119 Symbol #temp.a is specified with an unknown group context"); //$NON-NLS-1$
    }

    @Test
    public void testTempGroupElementShouldNotBeResolable1() {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  select 1 as a into #temp;") //$NON-NLS-1$
        .append("\n  insert into #temp (a) values (#temp.a);") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpFailUpdateProcedure(proc.toString(),
                                userUpdateStr,
                                Table.TriggerEvent.UPDATE,
                                "TEIID31119 Symbol #temp.a is specified with an unknown group context"); //$NON-NLS-1$
    }

    @Test
    public void testProcedureCreate() throws Exception {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  create local temporary table t1 (e1 string);") //$NON-NLS-1$
        .append("\n  select e1 from t1;") //$NON-NLS-1$
        .append("\n  create local temporary table t1 (e1 string, e2 integer);") //$NON-NLS-1$
        .append("\n  select e2 from t1;") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpResolveUpdateProcedure(proc.toString(), userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testTEIDDES2345() throws Exception {
        String proc = "FOR EACH ROW" + NEW_LINE +
                            "BEGIN ATOMIC" + NEW_LINE +
                            "DECLARE integer VARIABLES.ROWS_UPDATED;" + NEW_LINE +
                            "UPDATE vm1.g1 SET e1='x';" + NEW_LINE +
                            "VARIABLES.ROWS_UPDATED = VARIABLES.ROWCOUNT;" + NEW_LINE +
                            "END";

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x1'"; //$NON-NLS-1$

        helpResolveUpdateProcedure(proc, userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    /**
     * it is not ok to redefine the loopCursor 
     */
    @Test
    public void testProcedureCreate1() {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  LOOP ON (SELECT pm1.g1.e1 FROM pm1.g1) AS loopCursor") //$NON-NLS-1$
        .append("\n  BEGIN") //$NON-NLS-1$
        .append("\n  create local temporary table loopCursor (e1 string);") //$NON-NLS-1$
        .append("\nEND") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpFailUpdateProcedure(proc.toString(),
                                userUpdateStr,
                                Table.TriggerEvent.UPDATE,
                                "TEIID30118 Cannot create temporary table \"loopCursor\". An object with the same name already exists."); //$NON-NLS-1$
    }

    @Test
    public void testProcedureCreateDrop() {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n drop table t1;") //$NON-NLS-1$
        .append("\n  create local temporary table t1 (e1 string);") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpFailUpdateProcedure(proc.toString(), userUpdateStr, Table.TriggerEvent.UPDATE, "Group does not exist: t1"); //$NON-NLS-1$
    }

    @Test
    public void testProcedureCreateDrop1() throws Exception {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  create local temporary table t1 (e1 string);") //$NON-NLS-1$
        .append("\n  drop table t1;") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpResolveUpdateProcedure(proc.toString(), userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testCreateAfterImplicitTempTable() throws Exception {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  select e1 into #temp from pm1.g1;") //$NON-NLS-1$
        .append("\n  create local temporary table #temp (e1 string);") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpResolveUpdateProcedure(proc.toString(), userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    @Test
    public void testInsertAfterCreate() throws Exception {
        StringBuffer proc = new StringBuffer("FOR EACH ROW") //$NON-NLS-1$
        .append("\nBEGIN") //$NON-NLS-1$
        .append("\n  create local temporary table #temp (e1 string, e2 string);") //$NON-NLS-1$
        .append("\n  insert into #temp (e1) values ('a');") //$NON-NLS-1$
        .append("\nEND"); //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpResolveUpdateProcedure(proc.toString(), userUpdateStr, Table.TriggerEvent.UPDATE);
    }

    /**
     * delete procedures should not reference input or changing vars.
     */
    @Test
    public void testDefect16451() {
        String procedure = "FOR EACH ROW "; //$NON-NLS-1$
        procedure += "BEGIN ATOMIC\n"; //$NON-NLS-1$
        procedure += "Select pm1.g1.e2 from pm1.g1 where e1 = NEW.e1;\n"; //$NON-NLS-1$
        procedure += "END\n"; //$NON-NLS-1$

        String userUpdateStr = "delete from vm1.g1 where e1='x'"; //$NON-NLS-1$

        helpFailUpdateProcedure(procedure,
                                userUpdateStr,
                                Table.TriggerEvent.DELETE,
                                "TEIID31119 Symbol \"NEW\".e1 is specified with an unknown group context"); //$NON-NLS-1$
    }

    @Test
    public void testInvalidVirtualProcedure3() throws Exception {
        helpResolveException("EXEC pm1.vsp18()", getMetadataFactory().example1Cached(), "Group does not exist: temptable"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    // variable resolution, variable compared against
    // different datatype element for which there is no implicit transformation)
    @Test
    public void testCreateUpdateProcedure2() {
        String procedure = "FOR EACH ROW "; //$NON-NLS-1$
        procedure += "BEGIN\n"; //$NON-NLS-1$
        procedure += "DECLARE boolean var1;\n"; //$NON-NLS-1$
        procedure += "ROWS_UPDATED = UPDATE pm1.g1 SET pm1.g1.e4 = convert(var1, string), pm1.g1.e1 = var1;\n"; //$NON-NLS-1$
        procedure += "END\n"; //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1=1"; //$NON-NLS-1$

        helpFailUpdateProcedure(procedure,
                                userUpdateStr,
                                Table.TriggerEvent.UPDATE,
                                "Cannot set symbol 'pm1.g1.e4' with expected type double to expression 'convert(var1, string)'"); //$NON-NLS-1$
    }

    // special variable INPUT compared against invalid type
    @Test
    public void testInvalidInputInUpdate() {
        String procedure = "FOR EACH ROW "; //$NON-NLS-1$
        procedure += "BEGIN ATOMIC\n"; //$NON-NLS-1$
        procedure += "DECLARE integer var1;\n"; //$NON-NLS-1$
        procedure += "Select pm1.g1.e2, new.e2 from pm1.g1;\n"; //$NON-NLS-1$
        procedure += "UPDATE pm1.g1 SET pm1.g1.e1 = new.e1, pm1.g1.e2 = new.e1;\n"; //$NON-NLS-1$
        procedure += "END\n"; //$NON-NLS-1$

        String userUpdateStr = "UPDATE vm1.g1 SET e1='x'"; //$NON-NLS-1$

        helpFailUpdateProcedure(procedure,
                                userUpdateStr,
                                Table.TriggerEvent.UPDATE,
                                "Cannot set symbol 'pm1.g1.e2' with expected type integer to expression '\"new\".e1'"); //$NON-NLS-1$
    }

    @Test
    public void testOptionalParams() throws Exception {
        String ddl = "create foreign procedure proc (x integer, y string);\n";
        TransformationMetadata tm = createMetadata(ddl);

        String sql = "call proc (1)"; //$NON-NLS-1$

        StoredProcedure sp = (StoredProcedure)helpResolve(sql, tm);

        assertEquals(getFactory().newConstant(null, DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass()),
                     sp.getParameter(2).getExpression());

        sql = "call proc (1, 'a')"; //$NON-NLS-1$

        sp = (StoredProcedure)helpResolve(sql, tm);

        assertEquals(getFactory().newConstant("a", DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass()),
                     sp.getParameter(2).getExpression());
    }

    public TransformationMetadata createMetadata(String ddl) throws Exception {
        return getMetadataFactory().fromDDL(ddl, "test", "test");
    }

    @Test
    public void testOptionalParams1() throws Exception {
        String ddl = "create foreign procedure proc (x integer, y string NOT NULL, z integer);\n";
        TransformationMetadata tm = createMetadata(ddl);

        String sql = "call proc (1, 'a')"; //$NON-NLS-1$

        StoredProcedure sp = (StoredProcedure)helpResolve(sql, tm);

        assertEquals(getFactory().newConstant("a", DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass()),
                     sp.getParameter(2).getExpression());
    }

    @Test
    public void testVarArgs() throws Exception {
        String ddl = "create foreign procedure proc (x integer, VARIADIC z integer) returns (x string);\n";
        TransformationMetadata tm = createMetadata(ddl);
        String sql = "call proc (1, 2, 3)"; //$NON-NLS-1$

        StoredProcedure sp = (StoredProcedure)helpResolve(sql, tm);
        assertEquals("EXEC proc(1, 2, 3)", sp.toString());
        assertEquals(getFactory().newConstant(1), sp.getParameter(1).getExpression());
        Array expectedArray = getFactory().newArray(DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(),
                                                    Arrays.asList((Expression)getFactory().newConstant(2),
                                                                  getFactory().newConstant(3)));
        expectedArray.setImplicit(true);
        assertEquals(expectedArray, sp.getParameter(2).getExpression());
        assertEquals(SPParameter.RESULT_SET, sp.getParameter(3).getParameterType());
    }

    @Test
    public void testLoopRedefinition2() throws Exception {
        helpResolveException("EXEC pm1.vsp11()", getMetadataFactory().example1Cached(), "TEIID30124 Loop cursor or exception group name mycursor already exists."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testVarArgs1() throws Exception {
        String ddl = "create foreign procedure proc (VARIADIC z integer) returns (x string);\n";
        TransformationMetadata tm = createMetadata(ddl);

        String sql = "call proc ()"; //$NON-NLS-1$
        StoredProcedure sp = (StoredProcedure)helpResolve(sql, tm);
        assertEquals("EXEC proc()", sp.toString());
        Array expected = getFactory().newArray(DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(),
                                           new ArrayList<Expression>(0));
        expected.setImplicit(true);
        assertEquals(expected,
                     sp.getParameter(1).getExpression());
    }

    @Test public void testVarArgs2() throws Exception {
    	String ddl = "create foreign procedure proc (VARIADIC z object) returns (x string);\n";
    	TransformationMetadata tm = createMetadata(ddl);    	

    	String sql = "call proc ()"; //$NON-NLS-1$
        StoredProcedure sp = (StoredProcedure) helpResolve(sql, tm);
        assertEquals("EXEC proc()", sp.toString());
        Array expected = getFactory().newArray(DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass(), 
        									new ArrayList<Expression>(0));
        expected.setImplicit(true);
        assertEquals(expected, sp.getParameter(1).getExpression());
               
        sql = "call proc (1, (2, 3))"; //$NON-NLS-1$
        sp = (StoredProcedure)helpResolve(sql, tm);
        assertEquals("EXEC proc(1, (2, 3))", sp.toString());
        ArrayList<Expression> expressions = new ArrayList<Expression>();
        Constant constant_1 = getFactory().newConstant(1, DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass());
        Constant constant_2 = getFactory().newConstant(2, DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass());
        Constant constant_3 = getFactory().newConstant(3, DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass());
        expressions.add(constant_1); //new Constant(1));
        Array expression2 = getFactory().newArray(DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(), Arrays.asList((Expression)constant_2, constant_3));
        expressions.add(expression2);
        Array expected2 = getFactory().newArray(DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass(), expressions);
        expected2.setImplicit(true);
        assertEquals(expected2, sp.getParameter(1).getExpression());
    }
    
    @Test public void testAnonBlock() throws Exception {
    	String sql = "begin select 1 as something; end"; //$NON-NLS-1$
    	RealMetadataFactory rmf = new RealMetadataFactory(getTeiidVersion());
        CreateProcedureCommand sp = (CreateProcedureCommand) helpResolve(sql, rmf.example1Cached());
        assertEquals(1, sp.getResultSetColumns().size());
        assertEquals("something", Symbol.getName(sp.getResultSetColumns().get(0)));
        assertEquals(1, sp.getProjectedSymbols().size());
        assertTrue(sp.returnsResultSet());
    }
    
    @Test public void testAnonBlockNoResult() throws Exception {
    	String sql = "begin select 1 as something without return; end"; //$NON-NLS-1$
    	RealMetadataFactory rmf = new RealMetadataFactory(getTeiidVersion());
        CreateProcedureCommand sp = (CreateProcedureCommand)helpResolve(sql, rmf.example1Cached());
        assertEquals(0, sp.getProjectedSymbols().size());
        assertFalse(sp.returnsResultSet());
    }
    
    @Test public void testReturnAndResultSet() throws Exception {
    	String ddl = "CREATE FOREIGN PROCEDURE proc (OUT param STRING RESULT) RETURNS TABLE (a INTEGER, b STRING);"; //$NON-NLS-1$
    	RealMetadataFactory rmf = new RealMetadataFactory(getTeiidVersion());
    	TransformationMetadata tm = rmf.fromDDL(ddl, "x", "y");
    	StoredProcedure sp = (StoredProcedure)helpResolve("exec proc()", tm);
        assertEquals(2, sp.getProjectedSymbols().size());
        String actualValue = sp.getProjectedSymbols().get(1).toString();
        String expectedValue = "y.proc.RSParam.b";
        assertEquals(expectedValue, actualValue);
        assertTrue(sp.returnsResultSet());
        
        sp.setCallableStatement(true);
        assertEquals(3, sp.getProjectedSymbols().size());
        assertEquals("y.proc.param", sp.getProjectedSymbols().get(2).toString());
        
        CreateProcedureCommand cpc = (CreateProcedureCommand)helpResolve("begin exec proc(); end", tm);
        assertEquals(2, cpc.getProjectedSymbols().size());
        assertEquals(2, ((CommandStatement)cpc.getBlock().getStatements().get(0)).getCommand().getProjectedSymbols().size());
        assertTrue(cpc.returnsResultSet());
        
        helpValidate("begin declare string var; var = exec proc(); select var; end", new String[] {"SELECT var;"}, tm);
    }
}
