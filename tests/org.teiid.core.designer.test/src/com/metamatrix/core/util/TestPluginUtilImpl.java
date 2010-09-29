/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * TestPluginUtilImpl
 */
public class TestPluginUtilImpl extends TestCase {

    public static final String PLUGIN_ID = CoreModelerPlugin.PLUGIN_ID;
    public static final String I18N_NAME = "com.metamatrix.core.util.testResourceBundleUtil"; //$NON-NLS-1$

    public static String KEY1 = "key1"; //$NON-NLS-1$
    public static String KEY2 = "key2"; //$NON-NLS-1$
    public static String KEY_WITH_ONE_PARAM = "key_with_one_param"; //$NON-NLS-1$
    public static String KEY_WITH_TWO_PARAMS = "key_with_two_params"; //$NON-NLS-1$
    public static String KEY_FOR_DOUBLE_QUOTES = "key_for_string_with_double_quotes"; //$NON-NLS-1$

    public static String ACTUAL_FOR_KEY1 = "This is the string for key1"; //$NON-NLS-1$
    public static String ACTUAL_FOR_KEY2 = "This is the string for key2"; //$NON-NLS-1$
    public static String ACTUAL_FOR_KEY_FOR_DOUBLE_QUOTES = "This is the 'string' that contains double quotes: "; //$NON-NLS-1$

    public static final Object PARAM1 = new Integer(1);
    public static final Object PARAM2 = "Param2"; //$NON-NLS-1$
    public static final Object PARAM3 = new Character('c');
    public static final List LIST_WITH_ONE_PARAM = Arrays.asList(new Object[] {PARAM1});
    public static final List LIST_WITH_TWO_PARAMS = Arrays.asList(new Object[] {PARAM1, PARAM2});
    public static final List LIST_WITH_THREE_PARAMS = Arrays.asList(new Object[] {PARAM1, PARAM2, PARAM3});
    public static final List LIST_WITH_TWO_COMPLEX_PARAMS = Arrays.asList(new Object[] {KEY1, PARAM2});

    public static final Object OBJECT_WITH_TWO_PARAMS = new Object[] {PARAM1, PARAM2};
    public static final Object[] OBJECT_ARRAY_WITH_TWO_PARAMS = new Object[] {PARAM1, PARAM2};
    
    private PluginUtilImpl util;

    /**
     * Constructor for TestPluginUtilImpl.
     * 
     * @param name
     */
    public TestPluginUtilImpl( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));
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
        TestSuite suite = new TestSuite("TestPluginUtilImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestPluginUtilImpl.class);
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
    // H E L P E R M E T H O D S
    // =========================================================================

    public void helpTestGetString( final String key,
                                   final String expected ) {
        final String actual = util.getString(key);
        if (actual == null && expected == null) {
            return;
        }
        if ((actual != null && !actual.equals(expected)) || (expected != null && !expected.equals(actual))) {
            fail("Expected compare to return \"" + expected + //$NON-NLS-1$
                 "\" but actual was \"" + actual + //$NON-NLS-1$
                 "\""); //$NON-NLS-1$
        }
    }

    public void helpTestGetString( final String key,
                                   final List params,
                                   final String expected ) {
        final String actual = util.getString(key, params);
        if (actual == null && expected == null) {
            return;
        }
        if ((actual != null && !actual.equals(expected)) || (expected != null && !expected.equals(actual))) {
            fail("Expected compare to return \"" + expected + //$NON-NLS-1$
                 "\" but actual was \"" + actual + //$NON-NLS-1$
                 "\""); //$NON-NLS-1$
        }
    }
    
    public void helpTestGetString( final String key,
            final Object params,
            final String expected ) {
			final String actual = util.getString(key, params);
				if (actual == null && expected == null) {
				return;
				}
				if ((actual != null && !actual.equals(expected)) || (expected != null && !expected.equals(actual))) {
				fail("Expected compare to return \"" + expected + //$NON-NLS-1$
				"\" but actual was \"" + actual + //$NON-NLS-1$
				"\""); //$NON-NLS-1$
				}
			}

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testGetStringWithNullKey() {
        helpTestGetString(null, "<No message available>"); //$NON-NLS-1$
    }

    public void testGetStringWithNullKeyAndNullList() {
        helpTestGetString(null, (List)null, "<No message available>"); //$NON-NLS-1$
    }

    public void testGetStringWithKey1AndNullList() {
        helpTestGetString(KEY1, null, ACTUAL_FOR_KEY1);
    }

    public void testGetStringWithKey2AndNullList() {
        helpTestGetString(KEY2, null, ACTUAL_FOR_KEY2);
    }

    public void testGetStringWithDoubleQuotesAndListOfOneParam() {
        helpTestGetString(KEY_FOR_DOUBLE_QUOTES, LIST_WITH_ONE_PARAM, ACTUAL_FOR_KEY_FOR_DOUBLE_QUOTES + "'" + //$NON-NLS-1$
                                                                      PARAM1 + "'"); //$NON-NLS-1$
    }

    public void testGetStringWithKey3AndNullList() {
        helpTestGetString(KEY_WITH_ONE_PARAM, null, "This string has 1 parameter: {0}"); //$NON-NLS-1$
    }

    public void testGetStringWithKey4AndNullList() {
        helpTestGetString(KEY_WITH_TWO_PARAMS, null, "This string has 2 parameters: {0} and {1}"); //$NON-NLS-1$
    }

    public void testGetStringWithKey3AndListOfOneParam() {
        helpTestGetString(KEY_WITH_ONE_PARAM, LIST_WITH_ONE_PARAM, "This string has 1 parameter: " + PARAM1); //$NON-NLS-1$
    }

    public void testGetStringWithKey4AndListOfOneParam() {
        helpTestGetString(KEY_WITH_TWO_PARAMS, LIST_WITH_ONE_PARAM, "This string has 2 parameters: " + PARAM1 + //$NON-NLS-1$
                                                                    " and {1}"); //$NON-NLS-1$
    }

    public void testGetStringWithKey4AndListOfTwoParams() {
        helpTestGetString(KEY_WITH_TWO_PARAMS, LIST_WITH_TWO_PARAMS, "This string has 2 parameters: " + PARAM1 + //$NON-NLS-1$
                                                                     " and " + PARAM2); //$NON-NLS-1$
    }

    public void testGetStringWithKey4AndListOfTooManyParams() {
        helpTestGetString(KEY_WITH_TWO_PARAMS, LIST_WITH_THREE_PARAMS, "This string has 2 parameters: " + PARAM1 + //$NON-NLS-1$
                                                                       " and " + PARAM2); //$NON-NLS-1$
    }

    /**
     * This method tests that the getString does <i>not</i> localize parameters before insertion.
     */
    public void testGetStringWithKey4AndListOfTwoComplexParams() {
        helpTestGetString(KEY_WITH_TWO_PARAMS, LIST_WITH_TWO_COMPLEX_PARAMS, "This string has 2 parameters: " + KEY1 + //$NON-NLS-1$
                                                                             " and " + PARAM2); //$NON-NLS-1$
    }

    public void testGetStringWithArrayAsObject() {
        helpTestGetString(KEY_WITH_TWO_PARAMS, OBJECT_WITH_TWO_PARAMS, "This string has 2 parameters: " + PARAM1 + //$NON-NLS-1$
                                                                     " and " + PARAM2); //$NON-NLS-1$
    }
    
    public void testGetStringWithArrayAsObjectArray() {
        helpTestGetString(KEY_WITH_TWO_PARAMS, OBJECT_ARRAY_WITH_TWO_PARAMS, "This string has 2 parameters: " + PARAM1 + //$NON-NLS-1$
                                                                     " and " + PARAM2); //$NON-NLS-1$
    }
}
