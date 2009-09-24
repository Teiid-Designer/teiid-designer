/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * TestCoreValidationRulesUtil
 */
public class TestCoreValidationRulesUtil extends TestCase {

    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    /**
     * Constructor for TestJDBCRepositoryWriter.
     * @param name
     */
    public TestCoreValidationRulesUtil(String name) {
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
        final TestSuite suite = new TestSuite(TestCoreValidationRulesUtil.class);
        return new TestSetup(suite) {
            @Override
            protected void setUp() throws Exception {
                oneTimeSetUp();
            }
            @Override
            protected void tearDown() throws Exception {
                oneTimeTearDown();
            }

            public void oneTimeSetUp() {}

            public void oneTimeTearDown() {}

        };
    }

    // =========================================================================
    //                                 M A I N
    // =========================================================================

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    // =========================================================================
    //                 S E T   U P   A N D   T E A R   D O W N
    // =========================================================================

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void oneTimeSetUp() {}

    public static void oneTimeTearDown() {}

    // =========================================================================
    //                         H E L P E R    M E T H O D S
    // =========================================================================
    private ValidationResult helpCreateValidationResult() {
        return new ValidationResultImpl("target"); //$NON-NLS-1$
    }
    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    
    public void testValidateStringLength1() {
        ValidationResult result = helpCreateValidationResult();
        CoreValidationRulesUtil.validateStringLength(result, 10, "hkdsfhksdf"); //$NON-NLS-1$
        assertTrue(!result.hasProblems());
    }

    public void testValidateStringLength2() {
        ValidationResult result = helpCreateValidationResult();
        CoreValidationRulesUtil.validateStringLength(result, 5, "hkdsfhksdf"); //$NON-NLS-1$
        assertTrue(result.hasProblems());
    }
    
    public void testValidateStringName1() {
        ValidationResult result = helpCreateValidationResult();
        CoreValidationRulesUtil.validateStringNameChars(result, "sdfksdbfkj", null); //$NON-NLS-1$
        assertTrue(!result.hasProblems());        
    }

    public void testValidateStringName2() {
        ValidationResult result = helpCreateValidationResult();
        CoreValidationRulesUtil.validateStringNameChars(result, "s!dfksdbfkj", null); //$NON-NLS-1$
        assertTrue(result.hasProblems());
    }

    public void testValidateStringName3() {
        ValidationResult result = helpCreateValidationResult();
        CoreValidationRulesUtil.validateStringNameChars(result, "1sdfksdbfkj", null); //$NON-NLS-1$
        assertTrue(result.hasProblems());
    }

    public void testValidateStringName4() {
        ValidationResult result = helpCreateValidationResult();
        char[] invalidChars = {'s'};
        CoreValidationRulesUtil.validateStringNameChars(result, "sdfksdbfkj", invalidChars); //$NON-NLS-1$
        assertTrue(result.hasProblems());
    }
    
    public void testGetValidString1() {
        String validString = CoreValidationRulesUtil.getValidString("sdfksdbfkj", null, 2); //$NON-NLS-1$
        assertEquals("sd", validString); //$NON-NLS-1$
    }

    public void testGetValidString2() {
        String validString = CoreValidationRulesUtil.getValidString("s@#dfksdbfkj", null, 50); //$NON-NLS-1$
        assertEquals("s__dfksdbfkj", validString); //$NON-NLS-1$
    }

    public void testGetValidString3() {
        char[] invalidChars = {'s'};        
        String validString = CoreValidationRulesUtil.getValidString("s@#dfksdbfkj", invalidChars, 50); //$NON-NLS-1$
        assertEquals("dfk_dbfkj", validString); //$NON-NLS-1$
    }

    public void testGetValidString4() {
        char[] invalidChars = {'s'};        
        String validString = CoreValidationRulesUtil.getValidString("s@#dfksdbfkj", invalidChars, 5); //$NON-NLS-1$
        assertEquals("dfk_d", validString); //$NON-NLS-1$
    }

}
