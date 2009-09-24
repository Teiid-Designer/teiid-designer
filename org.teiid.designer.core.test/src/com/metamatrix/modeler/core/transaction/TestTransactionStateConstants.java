/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.transaction;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestTransactionStateConstants
 */
public class TestTransactionStateConstants extends TestCase {

    /**
     * Constructor for TestTransactionStateConstants.
     * @param name
     */
    public TestTransactionStateConstants(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestTransactionStateConstants"); //$NON-NLS-1$
        suite.addTestSuite(TestTransactionStateConstants.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            }
            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================

    public void helpTestDisplayValue( final int code, final String expectedDisplayString ) {
        final String actual = TransactionStateConstants.getDisplayValue(code);
        assertEquals(expectedDisplayString, actual);
		assertSame(expectedDisplayString, actual);
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testGetDisplayValue1() {
        helpTestDisplayValue( TransactionStateConstants.UNINITIALIZED,
                              TransactionStateConstants.UNINITIALIZED_STRING );
    }

    public void testGetDisplayValue2() {
        helpTestDisplayValue( TransactionStateConstants.STARTED,
                              TransactionStateConstants.STARTED_STRING );
    }

    public void testGetDisplayValue3() {
        helpTestDisplayValue( TransactionStateConstants.COMMITTING,
                              TransactionStateConstants.COMMITTING_STRING );
    }

    public void testGetDisplayValue4() {
        helpTestDisplayValue( TransactionStateConstants.ROLLING_BACK,
                              TransactionStateConstants.ROLLING_BACK_STRING );
    }

    public void testGetDisplayValue5() {
        helpTestDisplayValue( TransactionStateConstants.COMPLETE,
                              TransactionStateConstants.COMPLETE_STRING );
    }

    public void testGetDisplayValue6() {
        helpTestDisplayValue( TransactionStateConstants.FAILED,
                              TransactionStateConstants.FAILED_STRING );
    }

    public void testGetDisplayValueTooSmall() {
        helpTestDisplayValue( 0, TransactionStateConstants.UNKNOWN_STRING );
    }

    public void testGetDisplayValueTooBig() {
        helpTestDisplayValue( 6, TransactionStateConstants.UNKNOWN_STRING );
    }


}
