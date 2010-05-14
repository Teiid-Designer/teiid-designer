/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.sql.lang.Command;
import com.metamatrix.query.sql.navigator.DeepPreOrderNavigator;

/**
 */
public class TestInputSetPramReplacementVisitor extends TestCase {

    /**
     * Constructor for TestInputSetPramReplacementVisitor.
     * @param name
     */
    public TestInputSetPramReplacementVisitor(String name) {
        super(name);
    }

    // =========================================================================
    //                        T E S T   C O N T R O L
    // =========================================================================

    /** 
     * Construct the test suite, which uses a one-time setup call
     * and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestInputSetPramReplacementVisitor"); //$NON-NLS-1$
        suite.addTestSuite(TestInputSetPramReplacementVisitor.class);
        //suite.addTest(new TestInputSetPramReplacementVisitor("testGetCharacterVDBResource")); //$NON-NLS-1$
        //suite.addTest(new TestInputSetPramReplacementVisitor("testGetElementIDsInKey13760")); //$NON-NLS-1$

        return new TestSetup(suite);
    }

    public Command parseCommand(String sql) throws Exception {
        QueryParser parser = new QueryParser();
        return parser.parseCommand(sql);
    }

    public void helpTestVisitor(String sqlIn, String sqlOut) throws Exception {
        InputSetPramReplacementVisitor visitor = new InputSetPramReplacementVisitor();
        Command c = parseCommand(sqlIn);
        DeepPreOrderNavigator.doVisit(c, visitor);
        
        assertEquals("Command not properly modified", sqlOut, c.toString()); //$NON-NLS-1$
    }
    
    public void testNoChanges() throws Exception {
        helpTestVisitor("SELECT e FROM g", "SELECT e FROM g");         //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testElementInSelect() throws Exception {
        helpTestVisitor("SELECT INPUT.e FROM g", "SELECT ? FROM g");         //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testAliasedElementInSelect() throws Exception {
        helpTestVisitor("SELECT INPUT.e AS x FROM g", "SELECT ? AS x FROM g");         //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testElementInCompareCriteria1() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE e = INPUT.x", //$NON-NLS-1$
            "SELECT e FROM g WHERE e = ?");         //$NON-NLS-1$ 
    }

    public void testElementInCompareCriteria2() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE INPUT.x = e", //$NON-NLS-1$
            "SELECT e FROM g WHERE ? = e");         //$NON-NLS-1$ 
    }

    public void testElementInIsNullCriteria() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE INPUT.x IS NULL", //$NON-NLS-1$
            "SELECT e FROM g WHERE ? IS NULL");         //$NON-NLS-1$ 
    }

    public void testElementInLikeCriteria1() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE e LIKE INPUT.x", //$NON-NLS-1$
            "SELECT e FROM g WHERE e LIKE ?");         //$NON-NLS-1$ 
    }

    public void testElementInLikeCriteria2() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE INPUT.x LIKE e", //$NON-NLS-1$
            "SELECT e FROM g WHERE ? LIKE e");         //$NON-NLS-1$ 
    }

    public void testElementInSetCriteria1() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE INPUT.x IN (e)", //$NON-NLS-1$
            "SELECT e FROM g WHERE ? IN (e)");         //$NON-NLS-1$ 
    }

    public void testElementInSetCriteria2() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE e IN (1, INPUT.x, 2, INPUT.y)", //$NON-NLS-1$
            "SELECT e FROM g WHERE e IN (1, ?, 2, ?)");         //$NON-NLS-1$ 
    }

    public void testElementInBetweenCriteria1() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE INPUT.x BETWEEN e AND f", //$NON-NLS-1$
            "SELECT e FROM g WHERE ? BETWEEN e AND f");         //$NON-NLS-1$ 
    }

    public void testElementInBetweenCriteria2() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE e BETWEEN f AND INPUT.x", //$NON-NLS-1$
            "SELECT e FROM g WHERE e BETWEEN f AND ?");         //$NON-NLS-1$ 
    }

    public void testElementInBetweenCriteria3() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE e BETWEEN INPUT.x AND f", //$NON-NLS-1$
            "SELECT e FROM g WHERE e BETWEEN ? AND f");         //$NON-NLS-1$ 
    }

    public void testElementInFunction1() throws Exception {
        helpTestVisitor(
            "SELECT concat(e, INPUT.x) FROM g", //$NON-NLS-1$
            "SELECT concat(e, ?) FROM g");         //$NON-NLS-1$ 
    }

    public void testElementInFunction2() throws Exception {
        helpTestVisitor(
            "SELECT concat(INPUT.x, e) FROM g", //$NON-NLS-1$
            "SELECT concat(?, e) FROM g");         //$NON-NLS-1$ 
    }

    public void testElementInFunction3() throws Exception {
        helpTestVisitor(
            "SELECT concat(e, length(INPUT.x)) FROM g", //$NON-NLS-1$
            "SELECT concat(e, length(?)) FROM g");         //$NON-NLS-1$ 
    }
    
    public void testCase1() throws Exception {
        helpTestVisitor(
            "SELECT CASE WHEN INPUT.x=0 THEN 1 END FROM g", //$NON-NLS-1$
            "SELECT CASE WHEN ? = 0 THEN 1 END FROM g");         //$NON-NLS-1$         
    }

    public void testCase2() throws Exception {
        helpTestVisitor(
            "SELECT CASE WHEN e=INPUT.x THEN 1 END FROM g", //$NON-NLS-1$
            "SELECT CASE WHEN e = ? THEN 1 END FROM g");         //$NON-NLS-1$         
    }

    public void testCase3() throws Exception {
        helpTestVisitor(
            "SELECT CASE WHEN e=0 THEN INPUT.x END FROM g", //$NON-NLS-1$
            "SELECT CASE WHEN e = 0 THEN ? END FROM g");         //$NON-NLS-1$         
    }

    public void testCase4() throws Exception {
        helpTestVisitor(
            "SELECT CASE WHEN e=0 THEN 1 ELSE INPUT.x END FROM g", //$NON-NLS-1$
            "SELECT CASE WHEN e = 0 THEN 1 ELSE ? END FROM g");         //$NON-NLS-1$         
    }

    public void testCase5() throws Exception {
        helpTestVisitor(
            "SELECT CASE WHEN INPUT.x = 0 THEN 1 END FROM g", //$NON-NLS-1$
            "SELECT CASE WHEN ? = 0 THEN 1 END FROM g");         //$NON-NLS-1$         
    }

    public void testCase6() throws Exception {
        helpTestVisitor(
            "SELECT CASE WHEN INPUT.x = 0 THEN 1 ELSE INPUT.y END FROM g", //$NON-NLS-1$
            "SELECT CASE WHEN ? = 0 THEN 1 ELSE ? END FROM g");         //$NON-NLS-1$         
    }

    public void testElementInExistsCriteria() throws Exception {
        helpTestVisitor(
            "SELECT e FROM g WHERE EXISTS(SELECT INPUT.x FROM g2)", //$NON-NLS-1$
            "SELECT e FROM g WHERE EXISTS (SELECT ? FROM g2)");         //$NON-NLS-1$ 
    }
    
    public void testElementInProcParam() throws Exception {
        helpTestVisitor(
            "SELECT e from (exec proc(INPUT.x)) x", //$NON-NLS-1$
            "SELECT e FROM (EXEC proc(?)) AS x");         //$NON-NLS-1$ 
    }

}
