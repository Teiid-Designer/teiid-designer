/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.sql.v8;

import java.util.Arrays;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.QueryParser;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.AbstractTestQueryParser;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement.BranchingMode;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateProcedureCommand;
import org.teiid.query.sql.proc.ExceptionExpression;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.RaiseStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;
import org.teiid.query.sql.symbol.JSONObject;
import org.teiid.query.sql.symbol.XMLSerialize;

/**
 * Unit testing for the Query Parser for teiid version 8
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestQuery8Parser extends AbstractTestQueryParser {

    private Test8Factory factory;

    protected TestQuery8Parser(Version teiidVersion) {
        super(teiidVersion);
    }

    public TestQuery8Parser() {
        this(Version.TEIID_8_0);
    }

    @Override
    protected Test8Factory getFactory() {
        if (factory == null)
            factory = new Test8Factory(parser);

        return factory;
    }

    @Test
    public void testSignedExpression() {
        GroupSymbol g = getFactory().newGroupSymbol("g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Function f = getFactory().newFunction("*", new Expression[] {getFactory().newConstant(-1), getFactory().newElementSymbol("x")});
        Select select = getFactory().newSelect();
        select.addSymbol(f);
        select.addSymbol(getFactory().newElementSymbol("x"));
        select.addSymbol(getFactory().newConstant(5));

        Query query = getFactory().newQuery(select, from);
        helpTest("SELECT -x, +x, +5 FROM g",
                 "SELECT (-1 * x), x, 5 FROM g",
                 query);
    }

    @Test
    public void testFloatWithE() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().newConstant(new Double(1.3e8)));
        select.addSymbol(getFactory().newConstant(new Double(-1.3e+8)));
        select.addSymbol(getFactory().newConstant(new Double(+1.3e-8)));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 1.3e8, -1.3e+8, +1.3e-8 FROM a.g1",
                 "SELECT 1.3E8, -1.3E8, 1.3E-8 FROM a.g1",
                 query);
    }

    @Test
    public void testPgLike() {
        GroupSymbol g = getFactory().newGroupSymbol("db.g");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("a");
        select.addSymbol(a);

        Expression string1 = getFactory().newConstant("\\_aString");
        MatchCriteria crit = getFactory().newMatchCriteria(getFactory().newElementSymbol("b"), string1, '\\');

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(crit);
        helpTest("SELECT a FROM db.g WHERE b LIKE E'\\\\_aString'",
                 "SELECT a FROM db.g WHERE b LIKE '\\_aString' ESCAPE '\\'",
                 query);
    }

    @Test
    public void testLikeWithEscapeException() {
//        helpException("SELECT a from db.g where b like '#String' escape '#1'", "TEIID31100 Parsing error: Encountered \"like '#String' escape [*]'#1'[*]\" at line 1, column 50.\nTEIID30398 LIKE/SIMILAR TO ESCAPE value must be a single character: [#1].");
        helpException("SELECT a from db.g where b like '#String' escape '#1'", null);
    }

    @Test
    public void testErrorStatement() throws Exception {
        ExceptionExpression ee = getFactory().newExceptionExpression();
        ee.setMessage(getFactory().newConstant("Test only"));
        RaiseStatement errStmt = getFactory().newNode(ASTNodes.RAISE_STATEMENT);
        errStmt.setExpression(ee);

        helpStmtTest("ERROR 'Test only';", "RAISE SQLEXCEPTION 'Test only';",
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

        helpStmtTest("RAISE SQLWARNING SQLEXCEPTION 'Test only' SQLSTATE '100' chain e;", "RAISE SQLWARNING SQLEXCEPTION 'Test only' SQLSTATE '100' CHAIN e;",
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
        helpTestExpression("xmlserialize(x as BLOB encoding \"UTF-8\" version '1.0' INCLUDING xmldeclaration)",
                           "XMLSERIALIZE(x AS BLOB ENCODING \"UTF-8\" VERSION '1.0' INCLUDING XMLDECLARATION)",
                           f);
    }

    @Test
    public void testWindowedExpression() {
        String sql = "SELECT foo(x, y) over ()";
        helpException(sql);
    }

    @Test
    public void testInvalidLimit() {
        helpException("SELECT * FROM pm1.g1 LIMIT -5");
    }

    @Test
    public void testInvalidLimit_Offset() {
        helpException("SELECT * FROM pm1.g1 LIMIT -1, 100");
    }

    @Test
    public void testTextTableNegativeWidth() {
        helpException("SELECT * from texttable(null columns x string width -1) as x");
    }

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
        errStmt.setExpression(ee);
        Block b = getFactory().newBlock();
        b.setExceptionGroup("e");
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt, true);
        helpStmtTest("BEGIN\nselect * from x;\na = 1;\nexception e\nERROR 'My Error';\nEND", "BEGIN\nSELECT * FROM x;\na = 1;\nEXCEPTION e\nRAISE SQLEXCEPTION 'My Error';\nEND", b);
    }

    @Test
    public void testJSONObject() throws Exception {
        JSONObject f = getFactory().newJSONObject(Arrays.asList(getFactory().newDerivedColumn("table", getFactory().newElementSymbol("a"))));
        helpTestExpression("jsonObject(a as \"table\")", "JSONOBJECT(a AS \"table\")", f);
    }

    @Test
    public void testVirtualProcedure(){        
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
        
        helpTest("CREATE VIRTUAL PROCEDURE BEGIN DECLARE integer x; LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor BEGIN x=mycursor.c1; IF(x > 5) BEGIN CONTINUE; END END SELECT c1, c2 FROM m.g; END",
        "CREATE VIRTUAL PROCEDURE\nBEGIN\nDECLARE integer x;\n"
        + "LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor\nBEGIN\n"
        + "x = mycursor.c1;\nIF(x > 5)\nBEGIN\nCONTINUE;\nEND\nEND\n"
        + "SELECT c1, c2 FROM m.g;\nEND", virtualProcedureCommand);

    }

    @Test
    public void testIfElseWithoutBeginEnd() {
        String sql = "CREATE VIRTUAL PROCEDURE BEGIN IF (x > 1) select 1; IF (x > 1) select 1; ELSE select 1; END"; //$NON-NLS-1$
        String expected = "CREATE VIRTUAL PROCEDURE\nBEGIN\nIF(x > 1)\nBEGIN\nSELECT 1;\nEND\nIF(x > 1)\nBEGIN\nSELECT 1;\nEND\nELSE\nBEGIN\nSELECT 1;\nEND\nEND"; //$NON-NLS-1$

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

    @Test
    public void testIfElseWithoutBeginAndWithoutCreateVirtualProcedurePrefix() {
        String sql = "BEGIN IF (x > 1) select 1; IF (x > 1) select 1; ELSE select 1; END"; //$NON-NLS-1$
        /* CREATE VIRTUAL PROCEDURE is a required prefix for version 8.0 - 8.4 */
        helpException(sql);

        this.teiidVersion = Version.TEIID_8_1.get();
        this.parser = new QueryParser(teiidVersion);
        helpException(sql);

        this.teiidVersion = Version.TEIID_8_2.get();
        this.parser = new QueryParser(teiidVersion);
        helpException(sql);

        this.teiidVersion = Version.TEIID_8_3.get();
        this.parser = new QueryParser(teiidVersion);
        helpException(sql);
    }
    
    @Test
    public void testCacheHint() {
    	String sql 		= "/*+ cache(ttl:180000) */ SELECT column_1 FROM model_a.table_1";
    	String expected = "/*+ cache(ttl:180000) */ SELECT column_1 FROM model_a.table_1";
    	
    	GroupSymbol g = getFactory().newGroupSymbol("model_a.table_1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        ElementSymbol a = getFactory().newElementSymbol("column_1");
        select.addSymbol(a);

        Query query = getFactory().newQuery(select, from);
        query.setCacheHint(helpGetCacheHint(sql));
        
        helpTest(sql, expected, query);
    }

    @Test
    public void testCommentsSimple1() {
        String sql = "/*+ cache(ttl:300000) */ " + // 25 
                     "/* Comment 1 */ " + // 41
                     "SELECT " + // 48
                     "/*+ sh KEEP ALIASES */ " + // 70 - note the space between the + and sh - this is parseable but removed!
                     "* " + // 72
                     "/* Comment 2 /* Comment 2.5 */ */ " + // 106
                     "FROM " + // 111
                     "/* Comment 3 */ " + "g1 INNER JOIN /*+ MAKEDEP */ g2 ON g1.a1 = g2.a2 " + "/* Comment 4 */";
        String expectedSql = "/*+ cache(ttl:300000) */ " + // 25 
                                            "/* Comment 1 */ " + // 41
                                            "SELECT " + // 48
                                            "/*+sh KEEP ALIASES */ " + // 70
                                            "* " + // 72
                                            "/* Comment 2 /* Comment 2.5 */ */ " + // 106
                                            "FROM " + // 111
                                            "/* Comment 3 */ " + "g1 INNER JOIN /*+ MAKEDEP */ g2 ON g1.a1 = g2.a2 " + "/* Comment 4 */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testCommentsSimple2() {
        String sql = "/*+ cache(ttl:300000) */ " + "/* Comment 1 */ " + "SELECT " + "/*+sh KEEP ALIASES */ " + "a1 "
                     + "/* Comment 2 */ " + "FROM " + "/* Comment 3 */ " + "g1 INNER JOIN /*+ MAKEDEP */ g2 ON g1.a1 = g2.a2 "
                     + "/* Comment 4 */";
        helpTest(sql, sql, null);
    }
}
