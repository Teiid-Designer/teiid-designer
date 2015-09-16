/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v84;

import java.util.Arrays;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement.BranchingMode;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.v8.TestQuery8Parser;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestQuery84Parser extends TestQuery8Parser {

    protected TestQuery84Parser(Version teiidVersion) {
        super(teiidVersion);
    }

    public TestQuery84Parser() {
        this(Version.TEIID_8_4);
    }

    /**
     * Drops the CREATE VIRTUAL PROCEDURE prefix
     */
    @Override
    @Test
    public void testVirtualProcedure() {
        ElementSymbol x = getFactory().newElementSymbol("x");
        String intType = new String("integer");
        Statement dStmt = getFactory().newDeclareStatement(x, intType);

        GroupSymbol g = getFactory().newGroupSymbol("m.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol c1 = getFactory().newElementSymbol("c1");
        select.addSymbol(c1);
        select.addSymbol(getFactory().newElementSymbol("c2"));

        Query query = getFactory().newQuery(select, from);

        x = getFactory().newElementSymbol("x");
        c1 = getFactory().newElementSymbol("mycursor.c1");
        Statement assignmentStmt = getFactory().newAssignmentStatement(x, c1);
        Block block = getFactory().newBlock();
        block.addStatement(assignmentStmt);

        Block ifBlock = getFactory().newBlock();
        Statement continueStmt = getFactory().newBranchingStatement(BranchingMode.CONTINUE);
        ifBlock.addStatement(continueStmt);
        Criteria crit = getFactory().newCompareCriteria(x, Operator.GT, getFactory().newConstant(new Integer(5)));
        IfStatement ifStmt = getFactory().newIfStatement(crit, ifBlock);
        block.addStatement(ifStmt);

        String cursor = "mycursor";
        LoopStatement loopStmt = getFactory().newLoopStatement(block, query, cursor);

        block = getFactory().newBlock();
        block.addStatement(dStmt);
        block.addStatement(loopStmt);
        CommandStatement cmdStmt = getFactory().newCommandStatement(query);
        block.addStatement(cmdStmt);

        CreateProcedureCommand virtualProcedureCommand = getFactory().newCreateProcedureCommand();
        virtualProcedureCommand.setBlock(block);

        helpTest("BEGIN DECLARE integer x; LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor BEGIN x=mycursor.c1; IF(x > 5) BEGIN CONTINUE; END END SELECT c1, c2 FROM m.g; END",
                 "BEGIN\nDECLARE integer x;\n" + "LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor\nBEGIN\n"
                 + "x = mycursor.c1;\nIF(x > 5)\nBEGIN\nCONTINUE;\nEND\nEND\n" + "SELECT c1, c2 FROM m.g;\nEND",
                 virtualProcedureCommand);
    }

    @Override
    @Test
    public void testIfElseWithoutBeginEnd() {
        String sql = "BEGIN IF (x > 1) select 1; IF (x > 1) select 1; ELSE select 1; END"; //$NON-NLS-1$
        String expected = "BEGIN\nIF(x > 1)\nBEGIN\nSELECT 1;\nEND\nIF(x > 1)\nBEGIN\nSELECT 1;\nEND\nELSE\nBEGIN\nSELECT 1;\nEND\nEND"; //$NON-NLS-1$

        Query query = getFactory().newQuery();
        Expression expr = getFactory().wrapExpression(getFactory().newConstant(1));
        query.setSelect(getFactory().newSelect(Arrays.asList(expr))); //$NON-NLS-1$

        CommandStatement commandStmt = getFactory().newCommandStatement(query);
        CompareCriteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("x"), CriteriaOperator.Operator.GT, getFactory().newConstant(1)); //$NON-NLS-1$
        Block block = getFactory().newBlock();
        block.addStatement(commandStmt);

        IfStatement ifStmt = getFactory().newIfStatement(criteria, block);
        IfStatement ifStmt1 = ifStmt.clone();

        Block block2 = getFactory().newBlock();
        block2.addStatement(commandStmt);
        ifStmt1.setElseBlock(block2);
        Block block3 = getFactory().newBlock();
        block3.addStatement(ifStmt);
        block3.addStatement(ifStmt1);

        CreateProcedureCommand command = getFactory().newCreateProcedureCommand();
        command.setBlock(block3);
        helpTest(sql, expected, command);
    }

    @Override
    @Test
    public void testIfElseWithoutBeginAndWithoutCreateVirtualProcedurePrefix() {
        String sql = "BEGIN IF (x > 1) select 1; IF (x > 1) select 1; ELSE select 1; END"; //$NON-NLS-1$
        String expected = "BEGIN\nIF(x > 1)\nBEGIN\nSELECT 1;\nEND\nIF(x > 1)\nBEGIN\nSELECT 1;\nEND\nELSE\nBEGIN\nSELECT 1;\nEND\nEND"; //$NON-NLS-1$

        Query query = getFactory().newQuery();
        Expression expr = getFactory().wrapExpression(getFactory().newConstant(1));
        query.setSelect(getFactory().newSelect(Arrays.asList(expr))); //$NON-NLS-1$

        CommandStatement commandStmt = getFactory().newCommandStatement(query);
        CompareCriteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("x"), CriteriaOperator.Operator.GT, getFactory().newConstant(1)); //$NON-NLS-1$
        Block block = getFactory().newBlock();
        block.addStatement(commandStmt);

        IfStatement ifStmt = getFactory().newIfStatement(criteria, block);
        IfStatement ifStmt1 = ifStmt.clone();

        Block block2 = getFactory().newBlock();
        block2.addStatement(commandStmt);
        ifStmt1.setElseBlock(block2);
        Block block3 = getFactory().newBlock();
        block3.addStatement(ifStmt);
        block3.addStatement(ifStmt1);

        CreateProcedureCommand command = getFactory().newCreateProcedureCommand();
        command.setBlock(block3);
        helpTest(sql, expected, command);
    }
}
