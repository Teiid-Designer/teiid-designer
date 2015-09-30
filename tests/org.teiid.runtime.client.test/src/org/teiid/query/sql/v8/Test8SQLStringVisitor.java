/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v8;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.language.SQLConstants.NonReserved;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.AbstractTestSQLStringVisitor;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.Array;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class Test8SQLStringVisitor extends AbstractTestSQLStringVisitor {

    private Test8Factory factory;

    protected Test8SQLStringVisitor(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8SQLStringVisitor() {
        this(Version.TEIID_8_0);
    }

    @Override
    protected Test8Factory getFactory() {
        if (factory == null)
            factory = new Test8Factory(parser);

        return factory;
    }


    @Test
    public void testMerge1() {
        Insert insert = getFactory().newInsert();
        insert.setMerge(true);
        insert.setGroup(getFactory().newGroupSymbol("m.g1"));

        List<ElementSymbol> vars = new ArrayList<ElementSymbol>();
        vars.add(getFactory().newElementSymbol("e1"));
        vars.add(getFactory().newElementSymbol("e2"));
        insert.setVariables(vars);
        List<Constant> values = new ArrayList<Constant>();
        values.add(getFactory().newConstant(new Integer(5)));
        values.add(getFactory().newConstant("abc"));
        insert.setValues(values);

        helpTest(insert, "MERGE INTO m.g1 (e1, e2) VALUES (5, 'abc')");
    }

    @Test
    public void testAggregateSymbol1() {
        AggregateSymbol agg = getFactory().newAggregateSymbol(NonReserved.COUNT, false, getFactory().newConstant("abc"));
        helpTest(agg, "COUNT('abc')");
    }

    @Test
    public void testAggregateSymbol2() {
        AggregateSymbol agg = getFactory().newAggregateSymbol(NonReserved.COUNT, true, getFactory().newConstant("abc"));
        helpTest(agg, "COUNT(DISTINCT 'abc')");
    }

    @Test
    public void testAggregateSymbol3() {
        AggregateSymbol agg = getFactory().newAggregateSymbol(NonReserved.COUNT, false, null);
        helpTest(agg, "COUNT(*)");
    }

    @Test
    public void testAggregateSymbol4() {
        AggregateSymbol agg = getFactory().newAggregateSymbol(NonReserved.AVG, false, getFactory().newConstant("abc"));
        helpTest(agg, "AVG('abc')");
    }

    @Test
    public void testAggregateSymbol5() {
        AggregateSymbol agg = getFactory().newAggregateSymbol(NonReserved.SUM, false, getFactory().newConstant("abc"));
        helpTest(agg, "SUM('abc')");
    }

    @Test
    public void testAggregateSymbol6() {
        AggregateSymbol agg = getFactory().newAggregateSymbol(NonReserved.MIN, false, getFactory().newConstant("abc"));
        helpTest(agg, "MIN('abc')");
    }

    @Test
    public void testAggregateSymbol7() {
        AggregateSymbol agg = getFactory().newAggregateSymbol(NonReserved.MAX, false, getFactory().newConstant("abc"));
        helpTest(agg, "MAX('abc')");
    }

    @Test
    public void testRaiseErrorStatement() {
        Statement errStmt = getFactory().newRaiseStatement(getFactory().newConstant("My Error"));
        helpTest(errStmt, "RAISE 'My Error';");
    }

    @Test
    public void testRaiseErrorStatementWithExpression() {
        Statement errStmt = getFactory().newRaiseStatement(getFactory().newElementSymbol("a"));
        helpTest(errStmt, "RAISE a;");
    }

    @Test
    public void testCommandStatement1a() {
        Query q1 = getFactory().newQuery();
        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newElementSymbol("x"));
        q1.setSelect(select);
        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));
        q1.setFrom(from);

        CommandStatement cmdStmt = getFactory().newCommandStatement(q1);
        cmdStmt.setReturnable(false);
        helpTest(cmdStmt, "SELECT x FROM g WITHOUT RETURN;");
    }

    @Test
    public void testBlock1() {
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g"));
        CommandStatement cmdStmt = getFactory().newCommandStatement(d1);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1)));
        Statement errStmt = getFactory().newRaiseStatement(getFactory().newConstant("My Error"));
        Block b = getFactory().newBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        helpTest(b, "BEGIN\nDELETE FROM g;\na = 1;\nRAISE 'My Error';\nEND");
    }

    @Test
    public void testCreateUpdateProcedure1() {
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g"));
        CommandStatement cmdStmt = getFactory().newCommandStatement(d1);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1)));
        RaiseStatement errStmt = getFactory().newRaiseStatement(getFactory().newConstant("My Error"));
        Block b = getFactory().newBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        CreateProcedureCommand cup = getFactory().newCreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\nDELETE FROM g;\na = 1;\nRAISE 'My Error';\nEND");
    }

    @Test
    public void testCreateUpdateProcedure2() {
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g"));
        CommandStatement cmdStmt = getFactory().newCommandStatement(d1);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1)));
        RaiseStatement errStmt = getFactory().newRaiseStatement(getFactory().newConstant("My Error"));
        Block b = getFactory().newBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        CreateProcedureCommand cup = getFactory().newCreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\nDELETE FROM g;\na = 1;\nRAISE 'My Error';\nEND");
    }

    @Test
    public void testCreateUpdateProcedure3() {
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g"));
        CommandStatement cmdStmt = getFactory().newCommandStatement(d1);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1)));
        Statement errStmt = getFactory().newRaiseStatement(getFactory().newConstant("My Error"));
        Block b = getFactory().newBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        CreateProcedureCommand cup = getFactory().newCreateProcedureCommand(b);
        helpTest(cup, "CREATE VIRTUAL PROCEDURE\nBEGIN\nDELETE FROM g;\na = 1;\nRAISE 'My Error';\nEND");
    }

    @Test
    public void testArray() {
        List<Expression> expr = new ArrayList<Expression>();
        expr.add(getFactory().newElementSymbol("e1"));
        expr.add(getFactory().newConstant(1));
        Array array = getFactory().newArray(
                                            DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass(),
                                            expr);
        helpTest(array, "(e1, 1)");
    }

    @Test
    public void testReturnStatement() throws Exception {
        helpTest(parser.parseProcedure("begin if (true) return 1; return; end", false),
                 "CREATE VIRTUAL PROCEDURE\nBEGIN\nIF(TRUE)\nBEGIN\nRETURN 1;\nEND\nRETURN;\nEND");
    }

    @Test
    public void testConditionNesting() throws Exception {
        String sql = "select (intkey = intnum) is null, (intkey < intnum) in (true, false) from bqt1.smalla";

        helpTest(parser.parseCommand(sql),
                 "SELECT (intkey = intnum) IS NULL, (intkey < intnum) IN (TRUE, FALSE) FROM bqt1.smalla");
    }

    @Test
    public void testSubqueryNameEscaping() throws Exception {
        helpTest(getFactory().newSubqueryFromClause("user", parser.parseCommand("select 1")),
                 "(SELECT 1) AS \"user\"");
    }

}
