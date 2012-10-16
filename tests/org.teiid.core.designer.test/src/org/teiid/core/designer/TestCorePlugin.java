/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.core.util.SmartTestDesignerSuite;

/**
 * TestCorePlugin
 */
public class TestCorePlugin extends TestCase {

    /**
     * Constructor for TestCorePlugin.Util.
     * 
     * @param name
     */
    public TestCorePlugin( String name ) {
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
        CoreModelerPlugin plugin = new CoreModelerPlugin();
        SmartTestDesignerSuite.mockStartBundle(plugin, CoreModelerPlugin.PLUGIN_ID);
        ((PluginUtilImpl)CoreModelerPlugin.Util).initializePlatformLogger(plugin);
        // logger!
        TestSuite suite = new TestSuite("TestCorePlugin"); //$NON-NLS-1$
        suite.addTestSuite(TestCorePlugin.class);
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

    public void helpTestGetString( final String key,
                                   final String expectedValue ) {
        final String actualValue = CoreModelerPlugin.Util.getString(key); // may throw exception
        if (actualValue == null) {
            fail("CoreModelerPlugin.Util.getString(\"" + key + //$NON-NLS-1$
                 "\") should not return null"); //$NON-NLS-1$
        }
        if (!actualValue.equals(expectedValue)) {
            fail("Expected \"" + expectedValue + //$NON-NLS-1$
                 "\" but got \"" + actualValue + //$NON-NLS-1$
                 "\""); //$NON-NLS-1$
        }
    }

    public void testNonPlatformGetStringWithNullKey() {
        final String key = null;
        final String expectedValue = "<No message available>"; //$NON-NLS-1$
        helpTestGetString(key, expectedValue);
    }

    public void testNonPlatformGetStringWithZeroLengthKey() {
        final String key = ""; //$NON-NLS-1$
        final String expectedValue = "<Missing message for key \"\" in: " + //$NON-NLS-1$
                                     CoreModelerPlugin.PLUGIN_ID + ".i18n>"; //$NON-NLS-1$
        helpTestGetString(key, expectedValue);
    }

    public void testNonPlatformGetStringThatShouldBeFound() throws Exception {
        
        String pkgName = CoreModelerPlugin.class.getPackage().getName().replaceAll("\\.", "/"); //$NON-NLS-1$ //$NON-NLS-2$
        InputStream propStream = CoreModelerPlugin.class.getClassLoader()
                                                        .getResourceAsStream(pkgName + "/i18n.properties"); //$NON-NLS-1$
        
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new InputStreamReader(propStream));
            String line = null;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) //$NON-NLS-1$
                    continue;
                
                String[] entry = line.split("="); //$NON-NLS-1$
                if (entry.length != 2)
                    continue;
                
                // May skip properties using "=" in the value but enough for a sample
                final String key = entry[0];
                final String expectedValue = entry[1]
                                                                .replaceAll("\\\\", ""); //$NON-NLS-1$ //$NON-NLS-2$
                
                
                helpTestGetString(key, expectedValue);
            }        
        }
        finally {
            if (reader != null) 
                reader.close();
            else if (propStream != null)
                propStream.close();
        }
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
        final IStatus status = new Status(severity, pluginID, code, message, t);
        CoreModelerPlugin.Util.log(status);
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
        final IStatus embeddedStatus1 = new Status(embeddedSeverity1, pluginID, code + 1, embeddedMessage1, t);
        final IStatus embeddedStatus2 = new Status(embeddedSeverity2, pluginID, code, embeddedMessage2, t);
        final IStatus[] embedded = new IStatus[] {embeddedStatus1, embeddedStatus2};
        final IStatus status = new MultiStatus(pluginID, code, embedded, message, t);
        CoreModelerPlugin.Util.log(status);
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
        final IStatus status = new Status(severity, pluginID, code, message, t);
        CoreModelerPlugin.Util.log(status);
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
        final IStatus embeddedStatus1 = new Status(embeddedSeverity1, pluginID, code + 1, embeddedMessage1, t);
        final IStatus embeddedStatus2 = new Status(embeddedSeverity2, pluginID, code, embeddedMessage2, t);
        final IStatus[] embedded = new IStatus[] {embeddedStatus1, embeddedStatus2};
        final IStatus status = new MultiStatus(pluginID, code, embedded, message, t);
        CoreModelerPlugin.Util.log(status);
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
        new Status(severity, pluginID, code, message, t);
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
        new Status(severity, pluginID, code, message, t);
    }

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogStringObject() {
        final Object obj = "This is a string object that is output"; //$NON-NLS-1$
        CoreModelerPlugin.Util.log(obj);
    }

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogDateObject() {
        final Object obj = new Date();
        CoreModelerPlugin.Util.log(obj);
    }

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogNullObject() {
        final Object obj = null;
        CoreModelerPlugin.Util.log(obj);
    }
}
