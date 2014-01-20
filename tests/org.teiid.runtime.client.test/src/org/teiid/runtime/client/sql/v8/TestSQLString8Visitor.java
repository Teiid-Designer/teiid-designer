/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.runtime.client.sql.v8;

import java.util.Arrays;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.runtime.client.lang.TeiidNodeFactory.ASTNodes;
import org.teiid.runtime.client.lang.ast.AssignmentStatement;
import org.teiid.runtime.client.lang.ast.Block;
import org.teiid.runtime.client.lang.ast.BranchingStatement.BranchingMode;
import org.teiid.runtime.client.lang.ast.CommandStatement;
import org.teiid.runtime.client.lang.ast.CreateProcedureCommand;
import org.teiid.runtime.client.lang.ast.Criteria;
import org.teiid.runtime.client.lang.ast.CriteriaOperator.Operator;
import org.teiid.runtime.client.lang.ast.ElementSymbol;
import org.teiid.runtime.client.lang.ast.ExceptionExpression;
import org.teiid.runtime.client.lang.ast.Expression;
import org.teiid.runtime.client.lang.ast.From;
import org.teiid.runtime.client.lang.ast.Function;
import org.teiid.runtime.client.lang.ast.GroupSymbol;
import org.teiid.runtime.client.lang.ast.IfStatement;
import org.teiid.runtime.client.lang.ast.JSONObject;
import org.teiid.runtime.client.lang.ast.LoopStatement;
import org.teiid.runtime.client.lang.ast.MatchCriteria;
import org.teiid.runtime.client.lang.ast.Query;
import org.teiid.runtime.client.lang.ast.RaiseStatement;
import org.teiid.runtime.client.lang.ast.Select;
import org.teiid.runtime.client.lang.ast.Statement;
import org.teiid.runtime.client.lang.ast.XMLSerialize;
import org.teiid.runtime.client.sql.AbstractTestSQLStringVisitor;

/**
 * Unit testing for the SQLStringVisitor for teiid version 8
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestSQLString8Visitor extends AbstractTestSQLStringVisitor {

    private Test8Factory factory;

    /**
     *
     */
    public TestSQLString8Visitor() {
        super(new TeiidServerVersion("8.0.0")); //$NON-NLS-1$
    }

    @Override
    protected Test8Factory getFactory() {
        if (factory == null)
            factory = new Test8Factory(parser);

        return factory;
    }

    @Test
    public void testSignedExpression() {
        GroupSymbol g = getFactory().newGroupSymbol("g"); //$NON-NLS-1$
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(-1), getFactory().newElementSymbol("x")});
        Select select = getFactory().newSelect();
        select.addSymbol(f);
        select.addSymbol(getFactory().newElementSymbol("x"));
        select.addSymbol(getFactory().newConstant(5));

        Query query = getFactory().newQuery(select, from);
        helpTest(
                 "SELECT (-1 * x), x, 5 FROM g", //$NON-NLS-1$
                 query);
    }

    @Test
    public void testFloatWithE() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1"); //$NON-NLS-1$
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newConstant(new Double(1.3e8))); //$NON-NLS-1$
        select.addSymbol(getFactory().newConstant(new Double(-1.3e+8))); //$NON-NLS-1$
        select.addSymbol(getFactory().newConstant(new Double(+1.3e-8))); //$NON-NLS-1$

        Query query = getFactory().newQuery(select, from);

        helpTest(
                 "SELECT 1.3E8, -1.3E8, 1.3E-8 FROM a.g1", //$NON-NLS-1$
                 query);
    }

    @Test
    public void testPgLike() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g"); //$NON-NLS-1$
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a"); //$NON-NLS-1$
        select.addSymbol(a);

        Expression string1 = getFactory().newConstant("\\_aString"); //$NON-NLS-1$
        MatchCriteria crit = getFactory().newMatchCriteria(getFactory().newElementSymbol("b"), string1, '\\');

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a FROM db.g WHERE b LIKE '\\_aString' ESCAPE '\\'", //$NON-NLS-1$
                 query);
    }

//    @Test
//    public void testLikeWithEscapeException() {
////        helpException("SELECT a from db.g where b like '#String' escape '#1'", "TEIID31100 Parsing error: Encountered \"like '#String' escape [*]'#1'[*]\" at line 1, column 50.\nTEIID30398 LIKE/SIMILAR TO ESCAPE value must be a single character: [#1]."); //$NON-NLS-1$ //$NON-NLS-2$
//        helpException("SELECT a from db.g where b like '#String' escape '#1'", null);
//    }

    @Test
    public void testErrorStatement() throws Exception {
        ExceptionExpression ee = getFactory().newExceptionExpression();
        ee.setMessage(getFactory().newConstant("Test only"));
        RaiseStatement errStmt = getFactory().newNode(ASTNodes.RAISE_STATEMENT);
        errStmt.setExpression(ee);

        helpTest("RAISE SQLEXCEPTION 'Test only';", //$NON-NLS-1$ //$NON-NLS-2$
                     errStmt);
    }

    @Test
    public void testRaiseErrorStatement() throws Exception {
        ExceptionExpression ee = getFactory().newExceptionExpression();
        ee.setMessage(getFactory().newConstant("Test only"));
        ee.setSqlState(getFactory().newConstant("100"));
        ee.setParent(getFactory().newElementSymbol("e"));
        RaiseStatement errStmt = getFactory().newNode(ASTNodes.RAISE_STATEMENT);
        errStmt.setExpression(ee);
        errStmt.setWarning(true);

        helpTest("RAISE SQLWARNING SQLEXCEPTION 'Test only' SQLSTATE '100' CHAIN e;", //$NON-NLS-1$ //$NON-NLS-2$
                     errStmt);
    }

    @Test
    public void testXmlSerialize2() throws Exception {
        XMLSerialize f = getFactory().newXMLSerialize();
        f.setExpression(getFactory().newElementSymbol("x"));
        f.setTypeString("BLOB");
        f.setDeclaration(Boolean.TRUE);
        f.setVersion("1.0");
        f.setEncoding("UTF-8");
        helpTest("XMLSERIALIZE(x AS BLOB ENCODING \"UTF-8\" VERSION '1.0' INCLUDING XMLDECLARATION)",
                           f);
    }

//    @Test
//    public void testWindowedExpression() {
//        String sql = "SELECT foo(x, y) over ()";
//        helpException(sql);
//    }
//
//    @Test
//    public void testInvalidLimit() {
//        helpException("SELECT * FROM pm1.g1 LIMIT -5");
//    }
//
//    @Test
//    public void testInvalidLimit_Offset() {
//        helpException("SELECT * FROM pm1.g1 LIMIT -1, 100");
//    }
//
//    @Test
//    public void testTextTableNegativeWidth() {
//        helpException("SELECT * from texttable(null columns x string width -1) as x");
//    }

    @Test
    public void testBlockExceptionHandling() throws Exception {
        Select select = getFactory().newSelectWithMultileElementSymbol();
        From from = getFactory().newFrom();
        from.setClauses(Arrays.asList(getFactory().newUnaryFromClause("x")));
        Query query = getFactory().newQuery(select, from);
        CommandStatement cmdStmt = getFactory().newCommandStatement(query);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1)));
        RaiseStatement errStmt = getFactory().newNode(ASTNodes.RAISE_STATEMENT);
        ExceptionExpression ee = getFactory().newExceptionExpression();
        ee.setMessage(getFactory().newConstant("My Error"));
        errStmt.setExpression(ee); //$NON-NLS-1$
        Block b = getFactory().newBlock();
        b.setExceptionGroup("e");
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt, true);
        helpTest("BEGIN\nSELECT * FROM x;\na = 1;\nEXCEPTION e\nRAISE SQLEXCEPTION 'My Error';\nEND", b); //$NON-NLS-1$
    }

    @Test
    public void testJSONObject() throws Exception {
        JSONObject f = getFactory().newJSONObject(Arrays.asList(getFactory().newDerivedColumn("table", getFactory().newElementSymbol("a"))));
        helpTest("JSONOBJECT(a AS \"table\")", f);
    }

    @Test public void testVirtualProcedure(){        
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
        Criteria crit = getFactory().newCompareCriteria(x, Operator.GT,  getFactory().newConstant(new Integer(5)));
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
        
        helpTest("CREATE VIRTUAL PROCEDURE\nBEGIN\nDECLARE integer x;\n" //$NON-NLS-1$
        + "LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor\nBEGIN\n" //$NON-NLS-1$
        + "x = mycursor.c1;\nIF(x > 5)\nBEGIN\nCONTINUE;\nEND\nEND\n" //$NON-NLS-1$
        + "SELECT c1, c2 FROM m.g;\nEND", virtualProcedureCommand); //$NON-NLS-1$

    }
}
