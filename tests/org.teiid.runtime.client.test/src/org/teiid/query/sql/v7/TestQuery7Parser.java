/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.sql.v7;

import static org.junit.Assert.assertEquals;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.parser.v7.Teiid7Parser;
import org.teiid.query.sql.AbstractTestQueryParser;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.CriteriaSelector;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.HasCriteria;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.lang.TranslateCriteria;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.BranchingStatement.BranchingMode;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.LoopStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 * Unit testing for the Query Parser for teiid version 7
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestQuery7Parser extends AbstractTestQueryParser {

    private Test7Factory factory;

    /**
     *
     */
    public TestQuery7Parser() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected Test7Factory getFactory() {
        if (factory == null)
            factory = new Test7Factory(parser);

        return factory;
    }

    private void helpCriteriaSelectorTest(String selector, String expectedString, CriteriaSelector expectedSelector)
        throws Exception {
        // Don't use query parser as criteriaSelector is not part of the interface
        Teiid7Parser teiid7Parser = new Teiid7Parser(new StringReader(selector));
        CriteriaSelector actualSelector = teiid7Parser.criteriaSelector();
        assertEquals("CriteriaSelector does not match: ", expectedSelector, actualSelector);
    }

    /** SELECT 1.3e8 FROM a.g1 */
    @Test
    public void testFloatWithE() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(new Double(1.3e8))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT 1.3e8 FROM a.g1",
                 "SELECT 1.3E8 FROM a.g1",
                 query);
    }

    /** SELECT -1.3e-6 FROM a.g1 */
    @Test
    public void testFloatWithMinusE() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(new Double(-1.3e-6))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT -1.3e-6 FROM a.g1",
                 "SELECT -1.3E-6 FROM a.g1",
                 query);
    }

    /** SELECT -1.3e+8 FROM a.g1 */
    @Test
    public void testFloatWithPlusE() {
        GroupSymbol g = getFactory().newGroupSymbol("a.g1");
        From from = getFactory().newFrom();
        from.addGroup(g);

        Select select = getFactory().newSelect();
        select.addSymbol(getFactory().wrapExpression(getFactory().newConstant(new Double(-1.3e+8))));

        Query query = getFactory().newQuery(select, from);

        helpTest("SELECT -1.3e+8 FROM a.g1",
                 "SELECT -1.3E8 FROM a.g1",
                 query);
    }

    @Test
    public void testLikeWithEscapeException() {
        helpException("SELECT a from db.g where b like '#String' escape '#1'", null);
    }

    @Test
    public void testFailsWildcardInSelect1() {
        helpException("SELECT % from xx.yy", null);
    }

    @Test
    public void testInvalidToken() {
        helpException("%", null);
    }

    @Test
    public void testErrorStatement() throws Exception {
        RaiseErrorStatement errStmt = getFactory().newNode(ASTNodes.RAISE_ERROR_STATEMENT);
        errStmt.setExpression(getFactory().newConstant("Test only"));

        helpStmtTest("ERROR 'Test only';", "ERROR 'Test only';",
                     errStmt);
    }

    @Test
    public void testCriteriaSelector0() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.IS_NULL);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("IS NULL CRITERIA ON (a)", "IS NULL CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector1() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.EQ);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("= CRITERIA ON (a)", "= CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector2() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.NE);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("<> CRITERIA ON (a)", "<> CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector3() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.LT);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("< CRITERIA ON (a)", "< CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector4() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.GT);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("> CRITERIA ON (a)", "> CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector5() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.GE);
        critSelector.addElement(a);

        helpCriteriaSelectorTest(">= CRITERIA ON (a)", ">= CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector6() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.LE);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("<= CRITERIA ON (a)", "<= CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector7() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.LIKE);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("LIKE CRITERIA ON (a)", "LIKE CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector8() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.IN);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("IN CRITERIA ON (a)", "IN CRITERIA ON (a)", critSelector);
    }

    @Test
    public void testCriteriaSelector9() throws Exception {
        //ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        //critSelector.setSelectorType(Operator.IS_NULL);
        //critSelector.addElement(a);        

        helpCriteriaSelectorTest("CRITERIA", "CRITERIA", critSelector);
    }

    @Test
    public void testCriteriaSelector10() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.BETWEEN);
        critSelector.addElement(a);

        helpCriteriaSelectorTest("BETWEEN CRITERIA ON (a)", "BETWEEN CRITERIA ON (a)", critSelector);
    }

    /**HAS IS NULL CRITERIA ON (a)*/
    @Test
    public void testHasIsNullCriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.IS_NULL);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS IS NULL CRITERIA ON (a)", "HAS IS NULL CRITERIA ON (a)",
                         hasSelector);
    }

    /**HAS LIKE CRITERIA ON (a)*/
    @Test
    public void testHasLikeCriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.LIKE);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS LIKE CRITERIA ON (a)", "HAS LIKE CRITERIA ON (a)",
                         hasSelector);
    }

    @Test
    public void testHasEQCriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List<ElementSymbol> elements = new ArrayList<ElementSymbol>();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.EQ);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS = CRITERIA ON (a)", "HAS = CRITERIA ON (a)",
                         hasSelector);
    }

    @Test
    public void testHasNECriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.NE);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS <> CRITERIA ON (a)", "HAS <> CRITERIA ON (a)",
                         hasSelector);
    }

    /**HAS IN CRITERIA ON (a)*/
    @Test
    public void testHasInCriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List<ElementSymbol> elements = new ArrayList<ElementSymbol>();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.IN);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS IN CRITERIA ON (a)", "HAS IN CRITERIA ON (a)",
                         hasSelector);
    }

    /**HAS COMPARE_LT CRITERIA ON (a)*/
    @Test
    public void testHasLTCriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List<ElementSymbol> elements = new ArrayList<ElementSymbol>();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.LT);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS < CRITERIA ON (a)", "HAS < CRITERIA ON (a)",
                         hasSelector);
    }

    /**HAS COMPARE_LE CRITERIA ON (a)*/
    @Test
    public void testHasLECriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List<ElementSymbol> elements = new ArrayList<ElementSymbol>();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.LE);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS <= CRITERIA ON (a)", "HAS <= CRITERIA ON (a)",
                         hasSelector);
    }

    /**HAS COMPARE_GT CRITERIA ON (a)*/
    @Test
    public void testHasGTCriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.GT);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS > CRITERIA ON (a)", "HAS > CRITERIA ON (a)",
                         hasSelector);
    }

    /**HAS COMPARE_GE CRITERIA ON (a)*/
    @Test
    public void testHasGECriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.GE);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS >= CRITERIA ON (a)", "HAS >= CRITERIA ON (a)",
                         hasSelector);
    }

    /**HAS BETWEEN CRITERIA ON (a)*/
    @Test
    public void testHasBetweenCriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.BETWEEN);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        helpCriteriaTest("HAS BETWEEN CRITERIA ON (a)", "HAS BETWEEN CRITERIA ON (a)",
                         hasSelector);
    }

    @Test
    public void testTranslateCriteria() throws Exception {
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        Criteria crit = getFactory().newCompareCriteria(a, Operator.EQ, getFactory().newConstant(new Integer(5)));
        List critList = new ArrayList();
        critList.add(crit);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.IS_NULL);
        critSelector.setElements(elements);

        TranslateCriteria transCriteria = getFactory().newTranslateCriteria(critSelector, critList);

        helpCriteriaTest("TRANSLATE IS NULL CRITERIA ON (a) WITH (a = 5)",
                         "TRANSLATE IS NULL CRITERIA ON (a) WITH (a = 5)",
                         transCriteria);

        //helpCriteriaTest("TRANSLATE IS NULL CRITERIA ON (a) USING transFuncEQ (a)",
        //"TRANSLATE IS NULL CRITERIA ON (a) USING transFuncEQ (a)",
        //transCriteria);

    }

    /** original test */
    @Test
    public void testCreateUpdateProcedureCommand() {
        helpTestCreateUpdateProcedureCommandCase3025("CREATE PROCEDURE\nBEGIN\nDECLARE short var1;" +
                                                     "IF(HAS IS NULL CRITERIA ON (a))\nBEGIN\nvar1 = (SELECT a1 FROM g WHERE a2 = 5);\nEND\n"
                                                     +
                                                     "ELSE\nBEGIN\nDECLARE short var2;\nvar2 = (SELECT b1 FROM g WHERE a2 = 5);\nEND\n"
                                                     +
                                                     " END");

    }

    @Test
    public void testCreateUpdateProcedureCommandCase3025_1() {

        helpTestCreateUpdateProcedureCommandCase3025("CREATE PROCEDURE\nBEGIN\nDECLARE short var1;" +
                                                     "IF(HAS IS NULL CRITERIA ON (a))\nBEGIN\nvar1 = (SELECT a1 FROM g WHERE a2 = 5);\nEND\n"
                                                     +
                                                     "ELSE\nBEGIN\nDECLARE short var2;\nvar2 = (SELECT b1 FROM g WHERE a2 = 5);\nEND\n"
                                                     +
                                                     " END"); 

    }

    @Test
    public void testCreateUpdateProcedureCommandCase3025_2() {
        helpTestCreateUpdateProcedureCommandCase3025("CREATE PROCEDURE\nBEGIN\nDECLARE short var1;" +
                                                     "IF(HAS IS NULL CRITERIA ON (a))\nBEGIN\nvar1 = ((SELECT a1 FROM g WHERE a2 = 5) );\nEND\n"
                                                     +
                                                     "ELSE\nBEGIN\nDECLARE short var2;\nvar2 = (SELECT b1 FROM g WHERE a2 = 5);\nEND\n"
                                                     +
                                                     " END"); 
    }

    private void helpTestCreateUpdateProcedureCommandCase3025(String procedureString) {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List<ElementSymbol> symbols = new ArrayList<ElementSymbol>();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        List<ElementSymbol> elseSymbols = new ArrayList<ElementSymbol>();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);
        elseQuery.setCriteria(criteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        //has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.IS_NULL);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector);

        Block block = getFactory().newBlock();

        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest(procedureString, "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" + 
                                  "IF(HAS IS NULL CRITERIA ON (a))"
                                  + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                                  "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                                  "var2 = (SELECT b1 FROM g WHERE a2 = 5);" + "\n" + "END" + "\n" + "END", cmd);

    }

    /** test an expression in parentheses in an assignment statement */
    @Test
    public void testCreateUpdateProcedureCommandCase3025_3() {

        String procedureString = "CREATE PROCEDURE\nBEGIN\nDECLARE short var1;" +
                                 "IF(HAS IS NULL CRITERIA ON (a))\nBEGIN\nvar1 = (concat('x', 'y') );\nEND\n" +
                                 "ELSE\nBEGIN\nDECLARE short var2;\nvar2 = (SELECT b1 FROM g WHERE a2 = 5);\nEND\n" +
                                 " END";

        helpTestCreateUpdateProcedureCommandCase3025_Expression(procedureString);
    }

    /** test an expression in parentheses in an assignment statement */
    @Test
    public void testCreateUpdateProcedureCommandCase3025_4() {

        String procedureString = "CREATE PROCEDURE\nBEGIN\nDECLARE short var1;" +
                                 "IF(HAS IS NULL CRITERIA ON (a))\nBEGIN\nvar1 = ((concat('x', 'y') ));\nEND\n" +
                                 "ELSE\nBEGIN\nDECLARE short var2;\nvar2 = (SELECT b1 FROM g WHERE a2 = 5);\nEND\n" +
                                 " END";

        helpTestCreateUpdateProcedureCommandCase3025_Expression(procedureString);
    }

    /** test an expression without parentheses in an assignment statement */
    @Test
    public void testCreateUpdateProcedureCommandCase3025_5() {

        String procedureString = "CREATE PROCEDURE\nBEGIN\nDECLARE short var1;" +
                                 "IF(HAS IS NULL CRITERIA ON (a))\nBEGIN\nvar1 = concat('x', 'y') ;\nEND\n" +
                                 "ELSE\nBEGIN\nDECLARE short var2;\nvar2 = (SELECT b1 FROM g WHERE a2 = 5);\nEND\n" +
                                 " END";

        helpTestCreateUpdateProcedureCommandCase3025_Expression(procedureString);
    }

    /** test an expression in parentheses in an assignment statement */
    private void helpTestCreateUpdateProcedureCommandCase3025_Expression(String procedureString) {
        String expectedString = "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" + 
                                "IF(HAS IS NULL CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = concat('x', 'y');" + "\n" +
                                "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                                "var2 = (SELECT b1 FROM g WHERE a2 = 5);" + "\n" + "END" + "\n" + "END";

        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        Expression[] args = new Expression[] {getFactory().newConstant("x"), getFactory().newConstant("y")};
        Function function = getFactory().newFunction("concat", args);
        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, function);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        List<ElementSymbol> elseSymbols = new ArrayList<ElementSymbol>();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query elseQuery = getFactory().newQuery(elseSelect, from);
        elseQuery.setCriteria(criteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        //has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.IS_NULL);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector);

        Block block = getFactory().newBlock();

        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest(procedureString, expectedString, cmd);
    }

    /**IF statement with has criteria */
    @Test
    public void testCreateUpdateProcedureCommand1() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);
        elseQuery.setCriteria(criteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        //has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE a2 = 5; END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE a2 = 5);" + "\n" + "END" + "\n" + "END", cmd);
    }

    @Test
    public void testCreateUpdateProcedureCommand0() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);
        elseQuery.setCriteria(criteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        //has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        //critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS CRITERIA) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE a2 = 5; END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS CRITERIA)" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE a2 = 5);" + "\n" + "END" + "\n" + "END", cmd);
    }

    /**IF statement with has LIKE criteria */
    @Test
    public void testCreateUpdateProcedureCommand2() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);
        elseQuery.setCriteria(criteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        //has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.LIKE);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS LIKE CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE a2 = 5; END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS LIKE CRITERIA ON (a))"
                         + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE a2 = 5);" + "\n" + "END" + "\n" + "END", cmd);
    }

    /**IF statement with has IN criteria */
    @Test
    public void testCreateUpdateProcedureCommand3() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);
        elseQuery.setCriteria(criteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        //has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.IN);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS IN CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE a2 = 5; END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS IN CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE a2 = 5);" + "\n" + "END" + "\n" + "END", cmd);
    }

    /**IF statement with has <> criteria */
    @Test
    public void testCreateUpdateProcedureCommand4() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);
        elseQuery.setCriteria(criteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        //has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        critSelector.setSelectorType(Operator.NE);
        critSelector.setElements(elements);

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS <> CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE a2 = 5; END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS <> CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE a2 = 5);" + "\n" + "END" + "\n" + "END", cmd);
    }

    /**Has criteria in WHERE clause*/
    @Test
    public void testCreateUpdateProcedureCommand5() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        //element for has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);

        CriteriaSelector critSelector2 = getFactory().newCriteriaSelector();
        //critSelector2.setSelectorType(Operator.NE);
        critSelector2.setElements(elements);

        HasCriteria hasSelector2 = getFactory().newHasCriteria(critSelector2);
        //has criteria for else block
        elseQuery.setCriteria(hasSelector2);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        CriteriaSelector critSelector1 = getFactory().newCriteriaSelector();
        critSelector1.setSelectorType(Operator.NE);
        critSelector1.setElements(elements);

        HasCriteria hasSelector1 = getFactory().newHasCriteria(critSelector1);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector1);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS <> CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE HAS CRITERIA ON (a); END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS <> CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE HAS CRITERIA ON (a));" + "\n" + "END" + "\n" + "END", cmd);
    }

    /** Translate criteria (empty criteriaSelector in WHERE clause*/
    @Test
    public void testCreateUpdateProcedureCommand7() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        //element for has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);

        Criteria crit = getFactory().newCompareCriteria(a, Operator.EQ, getFactory().newConstant(new Integer(5)));
        List critList = new ArrayList();
        critList.add(crit);

        CriteriaSelector critSelector2 = getFactory().newCriteriaSelector();
        //critSelector2.setSelectorType(Operator.IS_NULL);
        critSelector2.setElements(elements);

        TranslateCriteria transCriteria = getFactory().newTranslateCriteria(critSelector2, critList);
        elseQuery.setCriteria(transCriteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        CriteriaSelector critSelector1 = getFactory().newCriteriaSelector();
        critSelector1.setSelectorType(Operator.NE);
        critSelector1.setElements(elements);

        HasCriteria hasSelector1 = getFactory().newHasCriteria(critSelector1);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector1);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS <> CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = (SELECT b1 FROM g WHERE TRANSLATE CRITERIA ON (a) WITH (a = 5)); END" +
                 " END",
                 "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                 "IF(HAS <> CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                 "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                 "var2 = (SELECT b1 FROM g WHERE TRANSLATE CRITERIA ON (a) WITH (a = 5));" + "\n" + "END" + "\n" + "END", cmd);
    }

    /** Translate criteria (is null criteriaSelector in WHERE clause*/
    @Test
    public void testCreateUpdateProcedureCommand9() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        //element for has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);

        Criteria crit = getFactory().newCompareCriteria(a, Operator.EQ, getFactory().newConstant(new Integer(5)));
        List critList = new ArrayList();
        critList.add(crit);

        CriteriaSelector critSelector2 = getFactory().newCriteriaSelector();
        critSelector2.setSelectorType(Operator.IS_NULL);
        critSelector2.setElements(elements);

        TranslateCriteria transCriteria = getFactory().newTranslateCriteria(critSelector2, critList);
        elseQuery.setCriteria(transCriteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        CriteriaSelector critSelector1 = getFactory().newCriteriaSelector();
        critSelector1.setSelectorType(Operator.NE);
        critSelector1.setElements(elements);

        HasCriteria hasSelector1 = getFactory().newHasCriteria(critSelector1);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector1);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS <> CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END"
                 +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE TRANSLATE IS NULL CRITERIA ON (a) WITH (a = 5); END"
                 +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS <> CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE TRANSLATE IS NULL CRITERIA ON (a) WITH (a = 5));"
                         + "\n" + "END" + "\n" + "END", cmd);
    }

    /** Translate criteria ( only with WHERE clause) */
    @Test
    public void testCreateUpdateProcedureCommand10() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        //element for has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);

        Criteria crit = getFactory().newCompareCriteria(a, Operator.EQ, getFactory().newConstant(new Integer(5)));
        List critList = new ArrayList();
        critList.add(crit);

        TranslateCriteria transCriteria = getFactory().newTranslateCriteria();
        CriteriaSelector critSelector2 = getFactory().newCriteriaSelector();
        transCriteria.setTranslations(critList);
        transCriteria.setSelector(critSelector2);

        elseQuery.setCriteria(transCriteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        CriteriaSelector critSelector1 = getFactory().newCriteriaSelector();
        critSelector1.setSelectorType(Operator.NE);
        critSelector1.setElements(elements);

        HasCriteria hasSelector1 = getFactory().newHasCriteria(critSelector1);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector1);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS <> CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE TRANSLATE CRITERIA WITH (a = 5); END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS <> CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE TRANSLATE CRITERIA WITH (a = 5));" + "\n" + "END" + "\n" + "END", cmd);
    }

    /** Translate criteria ( only with WHERE clause) */
    @Test
    public void testCreateUpdateProcedureCommand12() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        //element for has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);

        Criteria crit1 = getFactory().newCompareCriteria(a, Operator.EQ, getFactory().newConstant(new Integer(5)));
        ElementSymbol m = getFactory().newElementSymbol("m");
        Criteria crit2 = getFactory().newCompareCriteria(m, Operator.EQ, getFactory().newConstant(new Integer(6)));
        List critList = new ArrayList();
        critList.add(crit1);
        critList.add(crit2);

        TranslateCriteria transCriteria = getFactory().newTranslateCriteria();
        CriteriaSelector critSelector2 = getFactory().newCriteriaSelector();
        transCriteria.setTranslations(critList);
        transCriteria.setSelector(critSelector2);

        elseQuery.setCriteria(transCriteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        CriteriaSelector critSelector1 = getFactory().newCriteriaSelector();
        critSelector1.setSelectorType(Operator.NE);
        critSelector1.setElements(elements);

        HasCriteria hasSelector1 = getFactory().newHasCriteria(critSelector1);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector1);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS <> CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE TRANSLATE CRITERIA WITH (a = 5, m = 6); END" +
                 " END",
                 "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                 "IF(HAS <> CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                 "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                 "var2 = (SELECT b1 FROM g WHERE TRANSLATE CRITERIA WITH (a = 5, m = 6));" + "\n" + "END" + "\n" + "END", cmd);

    }

    /** Translate criteria (with only Criteria in WHERE clause) */
    @Test
    public void testCreateUpdateProcedureCommand11() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        //element for has criteria
        ElementSymbol a = getFactory().newElementSymbol("a");
        List elements = new ArrayList();
        elements.add(a);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);

        Criteria crit = getFactory().newCompareCriteria(a, Operator.EQ, getFactory().newConstant(new Integer(5)));
        List critList = new ArrayList();
        critList.add(crit);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();
        TranslateCriteria transCrit = getFactory().newTranslateCriteria();
        transCrit.setSelector(critSelector);

        elseQuery.setCriteria(transCrit);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        CriteriaSelector critSelector1 = getFactory().newCriteriaSelector();
        critSelector1.setSelectorType(Operator.NE);
        critSelector1.setElements(elements);

        HasCriteria hasSelector1 = getFactory().newHasCriteria(critSelector1);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector1);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS <> CRITERIA ON (a)) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE TRANSLATE CRITERIA; END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS <> CRITERIA ON (a))" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE TRANSLATE CRITERIA);" + "\n" + "END" + "\n" + "END", cmd);
    }

    /**IF statement with has criteria no on */
    @Test
    public void testCreateUpdateProcedureCommand8() {
        //declare var1
        ElementSymbol var1 = getFactory().newElementSymbol("var1");
        String shortType = new String("short");
        Statement declStmt = getFactory().newDeclareStatement(var1, shortType);

        //ifblock
        List symbols = new ArrayList();
        symbols.add(getFactory().newElementSymbol("a1"));
        Select select = getFactory().newSelect(symbols);

        From from = getFactory().newFrom();
        from.addGroup(getFactory().newGroupSymbol("g"));

        Criteria criteria = getFactory().newCompareCriteria(getFactory().newElementSymbol("a2"), Operator.EQ,
                                               getFactory().newConstant(new Integer(5)));

        Query query = getFactory().newQuery(select, from);
        query.setCriteria(criteria);

        AssignmentStatement queryStmt = getFactory().newAssignmentStatement(var1, query);

        Block ifBlock = getFactory().newBlock();
        ifBlock.addStatement(queryStmt);

        //else block 
        ElementSymbol var2 = getFactory().newElementSymbol("var2");
        Statement elseDeclStmt = getFactory().newDeclareStatement(var2, shortType);

        List elseSymbols = new ArrayList();
        elseSymbols.add(getFactory().newElementSymbol("b1"));
        Select elseSelect = getFactory().newSelect(elseSymbols);

        Query elseQuery = getFactory().newQuery(elseSelect, from);
        elseQuery.setCriteria(criteria);

        AssignmentStatement elseQueryStmt = getFactory().newAssignmentStatement(var2, elseQuery);

        Block elseBlock = getFactory().newBlock();
        List elseStmts = new ArrayList();
        elseStmts.add(elseDeclStmt);
        elseStmts.add(elseQueryStmt);

        elseBlock.setStatements(elseStmts);

        CriteriaSelector critSelector = getFactory().newCriteriaSelector();

        HasCriteria hasSelector = getFactory().newHasCriteria(critSelector);

        IfStatement stmt = getFactory().newIfStatement(ifBlock, elseBlock, hasSelector);

        Block block = getFactory().newBlock();
        block.addStatement(declStmt);
        block.addStatement(stmt);

        CreateUpdateProcedureCommand cmd = getFactory().newCreateUpdateProcedureCommand();
        cmd.setBlock(block);

        helpTest("CREATE PROCEDURE BEGIN DECLARE short var1;" +
                 " IF(HAS CRITERIA) BEGIN var1 = SELECT a1 FROM g WHERE a2 = 5; END" +
                 " ELSE BEGIN DECLARE short var2; var2 = SELECT b1 FROM g WHERE a2 = 5; END" +
                 " END", "CREATE PROCEDURE" + "\n" + "BEGIN" + "\n" + "DECLARE short var1;" + "\n" +
                         "IF(HAS CRITERIA)" + "\n" + "BEGIN" + "\n" + "var1 = (SELECT a1 FROM g WHERE a2 = 5);" + "\n" +
                         "END" + "\n" + "ELSE" + "\n" + "BEGIN" + "\n" + "DECLARE short var2;" + "\n" +
                         "var2 = (SELECT b1 FROM g WHERE a2 = 5);" + "\n" + "END" + "\n" + "END", cmd);
    }

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

        CreateUpdateProcedureCommand virtualProcedureCommand = getFactory().newCreateUpdateProcedureCommand();
        virtualProcedureCommand.setBlock(block);
        virtualProcedureCommand.setUpdateProcedure(false);

        helpTest("CREATE VIRTUAL PROCEDURE BEGIN DECLARE integer x; LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor BEGIN x=mycursor.c1; IF(x > 5) BEGIN CONTINUE; END END SELECT c1, c2 FROM m.g; END",
                 "CREATE VIRTUAL PROCEDURE\nBEGIN\nDECLARE integer x;\n"
                 + "LOOP ON (SELECT c1, c2 FROM m.g) AS mycursor\nBEGIN\n"
                 + "x = mycursor.c1;\nIF(x > 5)\nBEGIN\nCONTINUE;\nEND\nEND\n" + "SELECT c1, c2 FROM m.g;\nEND",
                 virtualProcedureCommand);

    }

    @Test
    public void testDropTable() {
        Drop drop = getFactory().newNode(ASTNodes.DROP);
        drop.setTable(getFactory().newGroupSymbol("tempTable"));
        helpTest("DROP table tempTable", "DROP TABLE tempTable", drop);
    }

    @Test
    public void testBadCreate() {
        helpException("create insert");
    }

    @Test
    public void testIfElseWithoutBeginEnd() {
        String sql = "CREATE PROCEDURE BEGIN IF (x > 1) select 1; IF (x > 1) select 1; ELSE select 1; END"; //$NON-NLS-1$
        String expected = "CREATE PROCEDURE\nBEGIN\nIF(x > 1)\nBEGIN\nSELECT 1;\nEND\nIF(x > 1)\nBEGIN\nSELECT 1;\nEND\nELSE\nBEGIN\nSELECT 1;\nEND\nEND"; //$NON-NLS-1$

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
        
        CreateUpdateProcedureCommand command = getFactory().newCreateUpdateProcedureCommand();
        command.setBlock(block3);
        helpTest(sql, expected, command);
    }

    @Test
    public void testCommentsSimple1() {
        String sql = "/*+ cache(ttl:300000) */ " + // 25 
                     "/* Comment 1 */ " + // 41
                     "SELECT " + // 48
                     "/*+ sh */ " + // 70 - note the space between the + and sh - this is parseable but removed!
                     "* " + // 72
                     "/* Comment 2 /* Comment 2.5 */ */ " + // 106
                     "FROM " + // 111
                     "/* Comment 3 */ " + "g1 INNER JOIN /*+ MAKEDEP */ g2 ON g1.a1 = g2.a2 " + "/* Comment 4 */";
        String expectedSql = "/*+ cache(ttl:300000) */ " + // 25 
                                            "/* Comment 1 */ " + // 41
                                            "SELECT " + // 48
                                            "/*+sh */ " + // 70
                                            "* " + // 72
                                            "/* Comment 2 /* Comment 2.5 */ */ " + // 106
                                            "FROM " + // 111
                                            "/* Comment 3 */ " + "g1 INNER JOIN /*+ MAKEDEP */ g2 ON g1.a1 = g2.a2 " + "/* Comment 4 */";
        helpTest(sql, expectedSql, null);
    }

    @Test
    public void testCommentsSimple2() {
        String sql = "/*+ cache(ttl:300000) */ " + "/* Comment 1 */ " + "SELECT " + "/*+sh */ " + "a1 "
                     + "/* Comment 2 */ " + "FROM " + "/* Comment 3 */ " + "g1 INNER JOIN /*+ MAKEDEP */ g2 ON g1.a1 = g2.a2 "
                     + "/* Comment 4 */";
        helpTest(sql, sql, null);
    }
}
