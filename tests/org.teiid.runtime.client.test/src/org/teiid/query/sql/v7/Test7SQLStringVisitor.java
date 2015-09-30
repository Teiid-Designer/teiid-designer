/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.query.sql.v7;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.language.SQLConstants.NonReserved;
import org.teiid.query.parser.TeiidNodeFactory.ASTNodes;
import org.teiid.query.sql.AbstractTestSQLStringVisitor;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.CriteriaOperator.Operator;
import org.teiid.query.sql.lang.CriteriaSelector;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.proc.AssignmentStatement;
import org.teiid.query.sql.proc.Block;
import org.teiid.query.sql.proc.CommandStatement;
import org.teiid.query.sql.proc.CreateUpdateProcedureCommand;
import org.teiid.query.sql.proc.IfStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;
import org.teiid.query.sql.proc.Statement;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.ElementSymbol;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class Test7SQLStringVisitor extends AbstractTestSQLStringVisitor {

    private Test7Factory factory;

    public Test7SQLStringVisitor() {
        super(Version.TEIID_7_7);
    }

    @Override
    protected Test7Factory getFactory() {
        if (factory == null)
            factory = new Test7Factory(parser);

        return factory;
    }

    @Test
    public void testAggregateSymbol1() {
        AggregateSymbol agg = getFactory().newAggregateSymbol("abc", NonReserved.COUNT, false, getFactory().newConstant("abc"));
        helpTest(agg, "COUNT('abc')");
    }

    @Test
    public void testAggregateSymbol2() {
        AggregateSymbol agg = getFactory().newAggregateSymbol("abc", NonReserved.COUNT, true, getFactory().newConstant("abc"));
        helpTest(agg, "COUNT(DISTINCT 'abc')");
    }

    @Test
    public void testAggregateSymbol3() {
        AggregateSymbol agg = getFactory().newAggregateSymbol("abc", NonReserved.COUNT, false, null);
        helpTest(agg, "COUNT(*)");
    }

    @Test
    public void testAggregateSymbol4() {
        AggregateSymbol agg = getFactory().newAggregateSymbol("abc", NonReserved.AVG, false, getFactory().newConstant("abc"));
        helpTest(agg, "AVG('abc')");
    }

    @Test
    public void testAggregateSymbol5() {
        AggregateSymbol agg = getFactory().newAggregateSymbol("abc", NonReserved.SUM, false, getFactory().newConstant("abc"));
        helpTest(agg, "SUM('abc')");
    }

    @Test
    public void testAggregateSymbol6() {
        AggregateSymbol agg = getFactory().newAggregateSymbol("abc", NonReserved.MIN, false, getFactory().newConstant("abc"));
        helpTest(agg, "MIN('abc')");
    }

    @Test
    public void testAggregateSymbol7() {
        AggregateSymbol agg = getFactory().newAggregateSymbol("abc", NonReserved.MAX, false, getFactory().newConstant("abc"));
        helpTest(agg, "MAX('abc')");
    }

    @Test
    public void testRaiseErrorStatement() {
        Statement errStmt = getFactory().newRaiseStatement(getFactory().newConstant("My Error"));
        helpTest(errStmt, "ERROR 'My Error';");
    }

    @Test
    public void testRaiseErrorStatementWithExpression() {
        Statement errStmt = getFactory().newRaiseStatement(getFactory().newElementSymbol("a"));
        helpTest(errStmt, "ERROR a;");
    }

    @Test public void testHasCriteria1() {
        ElementSymbol sy1 = getFactory().newElementSymbol("x"); //$NON-NLS-1$
        ElementSymbol sy2 = getFactory().newElementSymbol("y"); //$NON-NLS-1$
        ElementSymbol sy3 = getFactory().newElementSymbol("z"); //$NON-NLS-1$
        List elmnts = new ArrayList(3);
        elmnts.add(sy1);
        elmnts.add(sy2);
        elmnts.add(sy3);                
        CriteriaSelector cs = getFactory().newCriteriaSelector();
        cs.setElements(elmnts);
        cs.setSelectorType(Operator.LIKE);
        helpTest(getFactory().newHasCriteria(cs), "HAS LIKE CRITERIA ON (x, y, z)"); //$NON-NLS-1$
    }
    
    @Test public void testHasCriteria2() {
        ElementSymbol sy1 = getFactory().newElementSymbol("x"); //$NON-NLS-1$
        ElementSymbol sy2 = getFactory().newElementSymbol("y"); //$NON-NLS-1$
        ElementSymbol sy3 = getFactory().newElementSymbol("z"); //$NON-NLS-1$
        List elmnts = new ArrayList(3);
        elmnts.add(sy1);
        elmnts.add(sy2);
        elmnts.add(sy3);                
        CriteriaSelector cs = getFactory().newCriteriaSelector();
        cs.setElements(elmnts);
        cs.setSelectorType(Operator.LIKE);
        helpTest(getFactory().newHasCriteria(cs), "HAS LIKE CRITERIA ON (x, y, z)"); //$NON-NLS-1$
    }
    
    @Test public void testHasCriteria3() {
        ElementSymbol sy1 = getFactory().newElementSymbol("x"); //$NON-NLS-1$
        ElementSymbol sy2 = getFactory().newElementSymbol("y"); //$NON-NLS-1$
        ElementSymbol sy3 = getFactory().newElementSymbol("z"); //$NON-NLS-1$
        List elmnts = new ArrayList(3);
        elmnts.add(sy1);
        elmnts.add(sy2);
        elmnts.add(sy3);                
        CriteriaSelector cs = getFactory().newCriteriaSelector();
        cs.setElements(elmnts);
        cs.setSelectorType(Operator.BETWEEN);
        helpTest(getFactory().newHasCriteria(cs), "HAS BETWEEN CRITERIA ON (x, y, z)"); //$NON-NLS-1$
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
        helpTest(b, "BEGIN\nDELETE FROM g;\na = 1;\nERROR 'My Error';\nEND");
    }    
    
    @Test public void testBlock2() {        
        // construct If statement

        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt =  getFactory().newCommandStatement(d1);
        Block ifblock = getFactory().newBlock(cmdStmt);
        // construct If criteria        
        ElementSymbol sy1 = getFactory().newElementSymbol("x"); //$NON-NLS-1$
        List elmnts = new ArrayList(1);
        elmnts.add(sy1);
        CriteriaSelector cs = getFactory().newCriteriaSelector();
        cs.setElements(elmnts);
        cs.setSelectorType(Operator.LIKE); 
        Criteria crit = getFactory().newHasCriteria(cs);        
        IfStatement ifStmt = getFactory().newIfStatement(crit, ifblock);        
        
        // other statements
        RaiseErrorStatement errStmt =   getFactory().newRaiseStatement(getFactory().newConstant("My Error")); //$NON-NLS-1$
        Block b = getFactory().newBlock();
        b.addStatement(cmdStmt);
        b.addStatement(ifStmt);
        b.addStatement(errStmt);        

        helpTest(b, "BEGIN\nDELETE FROM g;\nIF(HAS LIKE CRITERIA ON (x))\nBEGIN\nDELETE FROM g;\nEND\nERROR 'My Error';\nEND"); //$NON-NLS-1$
    } 
    
    @Test public void testIfStatement1() {
        // construct If block
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt =  getFactory().newCommandStatement(d1);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1))); //$NON-NLS-1$
        RaiseErrorStatement errStmt =   getFactory().newRaiseStatement(getFactory().newConstant("My Error")); //$NON-NLS-1$
        Block ifblock = getFactory().newBlock();
        ifblock.addStatement(cmdStmt);
        ifblock.addStatement(assigStmt);
        ifblock.addStatement(errStmt);

        // construct If criteria        
        ElementSymbol sy1 = getFactory().newElementSymbol("x"); //$NON-NLS-1$
        List elmnts = new ArrayList(1);
        elmnts.add(sy1);
        CriteriaSelector cs = getFactory().newCriteriaSelector();
        cs.setElements(elmnts);
        cs.setSelectorType(Operator.LIKE); 
        Criteria crit = getFactory().newHasCriteria(cs);
        
        IfStatement ifStmt = getFactory().newIfStatement(crit, ifblock);
        helpTest(ifStmt, "IF(HAS LIKE CRITERIA ON (x))\nBEGIN\nDELETE FROM g;\na = 1;\nERROR 'My Error';\nEND"); //$NON-NLS-1$
    }

    @Test public void testIfStatement2() {
        // construct If block
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt =  getFactory().newCommandStatement(d1);
        Block ifblock = getFactory().newBlock(cmdStmt);

        // construct If criteria        
        ElementSymbol sy1 = getFactory().newElementSymbol("x"); //$NON-NLS-1$
        List elmnts = new ArrayList(1);
        elmnts.add(sy1);
        CriteriaSelector cs = getFactory().newCriteriaSelector();
        cs.setElements(elmnts);
        cs.setSelectorType(Operator.LIKE); 
        Criteria crit = getFactory().newHasCriteria(cs);
        
        IfStatement ifStmt = getFactory().newIfStatement(crit, ifblock);
        helpTest(ifStmt, "IF(HAS LIKE CRITERIA ON (x))\nBEGIN\nDELETE FROM g;\nEND"); //$NON-NLS-1$
    }

    @Test public void testIfStatement3() {
        // construct If block
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g")); //$NON-NLS-1$
        CommandStatement cmdStmt =  getFactory().newCommandStatement(d1);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1))); //$NON-NLS-1$
        RaiseErrorStatement errStmt =   getFactory().newRaiseStatement(getFactory().newConstant("My Error")); //$NON-NLS-1$
        Block ifblock = getFactory().newBlock();
        ifblock.addStatement(cmdStmt);
        ifblock.addStatement(assigStmt);
        ifblock.addStatement(errStmt);

        // construct If criteria        
        ElementSymbol sy1 = getFactory().newElementSymbol("x"); //$NON-NLS-1$
        List elmnts = new ArrayList(1);
        elmnts.add(sy1);
        CriteriaSelector cs = getFactory().newCriteriaSelector();
        cs.setElements(elmnts);
        cs.setSelectorType(Operator.LIKE);     
        Criteria crit = getFactory().newHasCriteria(cs);
        
        Block elseblock = getFactory().newBlock();
        elseblock.addStatement(cmdStmt);
        
        IfStatement ifStmt = getFactory().newIfStatement(crit, ifblock);
        ifStmt.setElseBlock(elseblock);
        helpTest(ifStmt, "IF(HAS LIKE CRITERIA ON (x))\nBEGIN\nDELETE FROM g;\na = 1;\nERROR 'My Error';\nEND\nELSE\nBEGIN\nDELETE FROM g;\nEND"); //$NON-NLS-1$
    }

    @Test
    public void testCreateUpdateProcedure1() {
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g"));
        CommandStatement cmdStmt = getFactory().newCommandStatement(d1);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1)));
        RaiseErrorStatement errStmt = getFactory().newRaiseStatement(getFactory().newConstant("My Error"));
        Block b = getFactory().newBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        CreateUpdateProcedureCommand cup = getFactory().newCreateUpdateProcedureCommand(b);
        helpTest(cup, "CREATE PROCEDURE\nBEGIN\nDELETE FROM g;\na = 1;\nERROR 'My Error';\nEND");
    }

    @Test
    public void testCreateUpdateProcedure2() {
        Delete d1 = getFactory().newNode(ASTNodes.DELETE);
        d1.setGroup(getFactory().newGroupSymbol("g"));
        CommandStatement cmdStmt = getFactory().newCommandStatement(d1);
        AssignmentStatement assigStmt = getFactory().newAssignmentStatement(getFactory().newElementSymbol("a"), getFactory().newConstant(new Integer(1)));
        RaiseErrorStatement errStmt = getFactory().newRaiseStatement(getFactory().newConstant("My Error"));
        Block b = getFactory().newBlock();
        b.addStatement(cmdStmt);
        b.addStatement(assigStmt);
        b.addStatement(errStmt);
        CreateUpdateProcedureCommand cup = getFactory().newCreateUpdateProcedureCommand(b);
        helpTest(cup, "CREATE PROCEDURE\nBEGIN\nDELETE FROM g;\na = 1;\nERROR 'My Error';\nEND");
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
        CreateUpdateProcedureCommand cup = getFactory().newCreateUpdateProcedureCommand(b);
        helpTest(cup, "CREATE PROCEDURE\nBEGIN\nDELETE FROM g;\na = 1;\nERROR 'My Error';\nEND");
    }
}
