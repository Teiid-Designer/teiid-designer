/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.util.Date;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

/**
 * TestModelerCore
 */
public class TestModelerCore extends TestCase {

    /**
     * Constructor for TestModelerCore.
     * @param name
     */
    public TestModelerCore(String name) {
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
        TestSuite suite = new TestSuite("TestModelerCore"); //$NON-NLS-1$
        suite.addTestSuite(TestModelerCore.class);
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

    public void helpTestGetString( final String key, final String expectedValue ) {
        final String actualValue = ModelerCore.Util.getString(key);   // may throw exception
        if ( actualValue == null ) {
            fail("ModelerCore.Util.getString(\"" + key + //$NON-NLS-1$
                                  "\") should not return null");  //$NON-NLS-1$
        }
        if ( !actualValue.equals(expectedValue) ) {
            fail("Expected \"" + expectedValue + //$NON-NLS-1$
                                  "\" but got \"" + actualValue + //$NON-NLS-1$
                                  "\""); //$NON-NLS-1$
        }
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testNonPlatformGetStringWithNullKey() {
        final String key = null;
        final String expectedValue = "<No message available>"; //$NON-NLS-1$
        helpTestGetString(key,expectedValue);
    }

    public void testNonPlatformGetStringWithZeroLengthKey() {
        final String key = ""; //$NON-NLS-1$
        final String expectedValue = "<Missing message for key \"\" in: " +  //$NON-NLS-1$
                                      ModelerCore.PLUGIN_ID + ".i18n>"; //$NON-NLS-1$
        helpTestGetString(key,expectedValue);
    }

    public void testNonPlatformGetStringThatShouldBeFoundWithTrailingSpace() {
        final String key = "ReflectionUtil.>>_ERROR_invoking_method__7"; //$NON-NLS-1$
        final String expectedValue = "ERROR invoking method "; //$NON-NLS-1$
        helpTestGetString(key,expectedValue);
    }

    public void testNonPlatformGetStringThatShouldBeFound() {
        final String key = "ContainerInterceptorImpl.Container_1"; //$NON-NLS-1$
        final String expectedValue = "Container may not be null"; //$NON-NLS-1$
        helpTestGetString(key,expectedValue);
    }

    /*
     * Test for void log(IStatus)
     */
    public void testNonPlatformLogIStatus_ErrorWithMessageWithoutThrowable() {
        final int severity = IStatus.ERROR;
        final int code = 100;
        final String message = "This is an error message"; //$NON-NLS-1$
        final Throwable t = null;
        final String pluginID = "my.plugin.id"; //$NON-NLS-1$
        final IStatus status = new Status(severity,pluginID,code,message,t);
        ModelerCore.Util.log(status);
    }

    /*
     * Test for void log(IStatus)
     */
    public void testNonPlatformLogIStatus_ErrorMultiStatusWithMessageWithThrowable() {
        final int embeddedSeverity1 = IStatus.WARNING;
        final int embeddedSeverity2 = IStatus.ERROR;
        final int code = 100;
        final String embeddedMessage1 = "This is the embedded warning message 1"; //$NON-NLS-1$
        final String embeddedMessage2 = "This is the embedded error message 2"; //$NON-NLS-1$
        final String message = "This is the message for the outter multistatus"; //$NON-NLS-1$
        final Throwable t = new Throwable("This is the throwable"); //$NON-NLS-1$
        t.fillInStackTrace();
        final String pluginID = "my.plugin.id"; //$NON-NLS-1$
        final IStatus embeddedStatus1 = new Status(embeddedSeverity1,pluginID,code+1,embeddedMessage1,t);
        final IStatus embeddedStatus2 = new Status(embeddedSeverity2,pluginID,code,embeddedMessage2,t);
        final IStatus[] embedded = new IStatus[]{embeddedStatus1,embeddedStatus2};
        final IStatus status = new MultiStatus(pluginID,code,embedded,message,t);
        ModelerCore.Util.log(status);
    }

    /*
     * Test for void log(IStatus)
     */
    public void testNonPlatformLogIStatus_WarningWithMessageWithoutThrowable() {
        final int severity = IStatus.WARNING;
        final int code = 100;
        final String message = "This is a warning message"; //$NON-NLS-1$
        final Throwable t = null;
        final String pluginID = "my.plugin.id"; //$NON-NLS-1$
        final IStatus status = new Status(severity,pluginID,code,message,t);
        ModelerCore.Util.log(status);
    }

    /*
     * Test for void log(IStatus)
     */
    public void testNonPlatformLogIStatus_WarningMultiStatusWithMessageWithThrowable() {
        final int embeddedSeverity1 = IStatus.INFO;
        final int embeddedSeverity2 = IStatus.WARNING;
        final int code = 100;
        final String embeddedMessage1 = "This is the embedded info message 1"; //$NON-NLS-1$
        final String embeddedMessage2 = "This is the embedded warning message 2"; //$NON-NLS-1$
        final String message = "This is the message for the outter multistatus"; //$NON-NLS-1$
        final Throwable t = new Throwable("This is the throwable"); //$NON-NLS-1$
        t.fillInStackTrace();
        final String pluginID = "my.plugin.id"; //$NON-NLS-1$
        final IStatus embeddedStatus1 = new Status(embeddedSeverity1,pluginID,code+1,embeddedMessage1,t);
        final IStatus embeddedStatus2 = new Status(embeddedSeverity2,pluginID,code,embeddedMessage2,t);
        final IStatus[] embedded = new IStatus[]{embeddedStatus1,embeddedStatus2};
        final IStatus status = new MultiStatus(pluginID,code,embedded,message,t);
        ModelerCore.Util.log(status);
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
            new Status(severity,pluginID,code,message,t);
        } catch ( IllegalArgumentException e ) {
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
            new Status(severity,pluginID,code,message,t);
        } catch ( IllegalArgumentException e ) {
        	fail("Should have been able to create a Status " + //$NON-NLS-1$
            "with a null message"); //$NON-NLS-1$
        }
    }

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogStringObject() {
        final Object obj = "This is a string object that is output"; //$NON-NLS-1$
        ModelerCore.Util.log(obj);
    }

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogDateObject() {
        final Object obj = new Date();
        ModelerCore.Util.log(obj);
    }

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogNullObject() {
        final Object obj = null;
        ModelerCore.Util.log(obj);
    }

    /*
     * Test for void log(Throwable)
     */
    public void testNonPlatformLogThrowable() {
        final Throwable t = new Throwable("This is the throwable"); //$NON-NLS-1$
        t.fillInStackTrace();
        ModelerCore.Util.log(t);
    }

//    public void testGetMetamodels() {
//        final Metamodels metamodels = ModelerCore.getMetamodels();
//        if ( metamodels == null ) {
	//            fail("Unable to obtain the Metamodels reference from ModelerCore"); //$NON-NLS-1$
//        } else {
//            System.out.println("Number of metamodels: " + metamodels.getMetamodelDescriptors().size()); //$NON-NLS-1$
//        }
//    }

    public void testGetRegistry() {
        if ( ModelerCore.getRegistry() == null ) {
            fail("Registry should never be null"); //$NON-NLS-1$
        }
    }


}
