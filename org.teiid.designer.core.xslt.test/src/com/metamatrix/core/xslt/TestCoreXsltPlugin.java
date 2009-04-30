/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.xslt;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * TestCoreXsltPlugin
 */
public class TestCoreXsltPlugin extends TestCase {

    public final static String[] REAL_KEYS = new String[] {"StyleFromResource.Unable_to_load_XSLT_stylesheet_from_URI", //$NON-NLS-1$
        "CoreXsltPlugin.Error_loading_the_XSLT_transform" //$NON-NLS-1$
    };
    public final static String[] REAL_VALUES = new String[] {"Unable to load XSLT stylesheet from {0}", //$NON-NLS-1$
        "Error loading the XSLT transform" //$NON-NLS-1$
    };

    /**
     * Constructor for TestCoreXsltPlugin.Util.
     * 
     * @param name
     */
    public TestCoreXsltPlugin( String name ) {
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
        TestSuite suite = new TestSuite("TestCoreXsltPlugin"); //$NON-NLS-1$
        suite.addTestSuite(TestCoreXsltPlugin.class);
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
                                   final String expectedValue ) {
        final String actualValue = CoreXsltPlugin.Util.getString(key); // may throw exception
        if (actualValue == null) {
            fail("CoreXsltPlugin.Util.getString(\"" + key + //$NON-NLS-1$
                 "\") should not return null"); //$NON-NLS-1$
        }
        if (!actualValue.equals(expectedValue)) {
            fail("Expected \"" + expectedValue + //$NON-NLS-1$
                 "\" but got \"" + actualValue + //$NON-NLS-1$
                 "\""); //$NON-NLS-1$
        }
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testNonPlatformGetStringWithNullKey() {
        final String key = null;
        final String expectedValue = "<No message available>"; //$NON-NLS-1$
        helpTestGetString(key, expectedValue);
    }

    public void testNonPlatformGetStringWithZeroLengthKey() {
        final String key = ""; //$NON-NLS-1$
        final String expectedValue = "<Missing message for key \"\" in: com.metamatrix.core.xslt.i18n>"; //$NON-NLS-1$
        helpTestGetString(key, expectedValue);
    }

    public void testNonPlatformGetStringThatShouldBeFound() {
        for (int i = 0; i != REAL_KEYS.length; ++i) {
            final String key = REAL_KEYS[i];
            final String expectedValue = REAL_VALUES[i];
            helpTestGetString(key, expectedValue);
        }
    }

    /*
     * Test for void log(IStatus)
     */
    public void testNonPlatformLogIStatus_ErrorWithNullMessageWithoutThrowable() {
        final int severity = IStatus.ERROR;
        final int code = 100;
        final String message = null;
        final Throwable t = null;
        final String pluginID = "my.plugin.id"; //$NON-NLS-1$
        try {
            new Status(severity, pluginID, code, message, t);
        } catch (IllegalArgumentException e) {
            fail("Should have been able to create a Status " + //$NON-NLS-1$
                 "with a null message"); //$NON-NLS-1$
        }
    }

    /*
     * Test for void log(IStatus)
     */
    public void testNonPlatformLogIStatus_ErrorWithoutMessageWithThrowable() {
        final int severity = IStatus.ERROR;
        final int code = 100;
        final String message = null;
        final Throwable t = new Throwable("This is the throwable"); //$NON-NLS-1$
        t.fillInStackTrace();
        final String pluginID = "my.plugin.id"; //$NON-NLS-1$
        try {
            new Status(severity, pluginID, code, message, t);
        } catch (IllegalArgumentException e) {
            fail("Should have been able to create a Status " + //$NON-NLS-1$
                 "with a null message"); //$NON-NLS-1$
        }
    }

}
