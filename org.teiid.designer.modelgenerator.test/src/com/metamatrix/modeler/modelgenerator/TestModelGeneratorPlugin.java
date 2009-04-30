/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator;

import java.util.Date;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * TestModelGeneratorPlugin
 */
public class TestModelGeneratorPlugin extends TestCase {

    /**
     * Constructor for TestModelGeneratorPlugin.
     * 
     * @param name
     */
    public TestModelGeneratorPlugin( String name ) {
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
        TestSuite suite = new TestSuite("TestModelGeneratorPlugin"); //$NON-NLS-1$
        suite.addTestSuite(TestModelGeneratorPlugin.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
                ModelGeneratorPlugin plugin = new ModelGeneratorPlugin();
                ((PluginUtilImpl)ModelGeneratorPlugin.Util).initializePlatformLogger(plugin);
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
        final String actualValue = ModelGeneratorPlugin.Util.getString(key); // may throw exception
        if (actualValue == null) {
            fail("ModelGeneratorPlugin.Util.getString(\"" + key + //$NON-NLS-1$
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

    // public void testDataPathSupplied() {
    // UnitTestUtil.assertTestDataPathSet();
    // }

    // /*
    // * Test for ModelGenerator createUml2RelationalGenerator(ModelResource, ModelResource, ModelResource, ModelResource)
    // */
    // public void testCreateUml2RelationalGeneratorWithNullModelResources() {
    //        final ModelResource uml2Resource = new FakeModelResource("/project/MyUml2Model.xmi"); //$NON-NLS-1$
    //        final ModelResource relationalResource = new FakeModelResource("/project/MyRelationalModel.xmi"); //$NON-NLS-1$
    //        final ModelResource datatypeResource = new FakeModelResource("/project/MySDTModel.xsd"); //$NON-NLS-1$
    //        final ModelResource relationResource = new FakeModelResource("/project/MyRelationModel.xmi"); //$NON-NLS-1$
    // final ModelResource nullResource = null;
    //
    // // Try null uml2 ...
    // try {
    // ModelGeneratorPlugin.createUml2RelationalGenerator(nullResource,relationalResource,datatypeResource,relationResource);
    //            fail("Failed to catch null UML2 resource"); //$NON-NLS-1$
    // } catch ( IllegalArgumentException e ) {
    // // expected
    // } catch ( Throwable t ) {
    // UnitTestUtil.fail(t);
    // }
    //
    // // Try null relational ...
    // try {
    // ModelGeneratorPlugin.createUml2RelationalGenerator(uml2Resource,nullResource,datatypeResource,relationResource);
    //            fail("Failed to catch null UML2 resource"); //$NON-NLS-1$
    // } catch ( IllegalArgumentException e ) {
    // // expected
    // } catch ( Throwable t ) {
    // UnitTestUtil.fail(t);
    // }
    //
    // // Try null datatype ...
    // try {
    // ModelGenerator gen =
    // ModelGeneratorPlugin.createUml2RelationalGenerator(uml2Resource,relationalResource,nullResource,relationResource);
    // UnitTestUtil.assertNotNull( gen.getDatatypeSelector() );
    // UnitTestUtil.assertTrue( gen.getDatatypeSelector() instanceof BuiltInDatatypeFinder );
    // UnitTestUtil.assertNotNull( gen.getRelationTracker() );
    // UnitTestUtil.assertTrue( gen.getRelationTracker() instanceof RelationTrackerImpl );
    // } catch ( CoreException t ) {
    // UnitTestUtil.fail(t);
    // }
    //
    // try {
    // ModelGenerator gen =
    // ModelGeneratorPlugin.createUml2RelationalGenerator(uml2Resource,relationalResource,datatypeResource,nullResource);
    // UnitTestUtil.assertNotNull( gen.getDatatypeSelector() );
    // UnitTestUtil.assertTrue( gen.getDatatypeSelector() instanceof XsdDatatypeFinder );
    // UnitTestUtil.assertNotNull( gen.getRelationTracker() );
    // UnitTestUtil.assertTrue( gen.getRelationTracker() instanceof RelationTrackerImpl );
    // } catch ( CoreException t ) {
    // UnitTestUtil.fail(t);
    // }
    // }
    //
    // /*
    // * Test for ModelGenerator createUml2RelationalGenerator(Resource, Resource, Resource, Resource)
    // */
    // public void testCreateUml2RelationalGeneratorResourceResourceResourceResource() {
    //        final Resource uml2Resource = new FakeResource("platform://project/MyUml2Model.xmi"); //$NON-NLS-1$
    //        final Resource relationalResource = new FakeResource("platform://project/MyRelationalModel.xmi"); //$NON-NLS-1$
    //        final Resource datatypeResource = new FakeResource("platform://project/MySDTModel.xsd"); //$NON-NLS-1$
    //        final Resource relationResource = new FakeResource("platform://project/MyRelationModel.xmi"); //$NON-NLS-1$
    // final Resource nullResource = null;
    //
    // // Try null uml2 ...
    // try {
    // ModelGeneratorPlugin.createUml2RelationalGenerator(nullResource,relationalResource,datatypeResource,relationResource);
    //            fail("Failed to catch null UML2 resource"); //$NON-NLS-1$
    // } catch ( IllegalArgumentException e ) {
    // // expected
    // } catch ( Throwable t ) {
    // UnitTestUtil.fail(t);
    // }
    //
    // // Try null relational ...
    // try {
    // ModelGeneratorPlugin.createUml2RelationalGenerator(uml2Resource,nullResource,datatypeResource,relationResource);
    //            fail("Failed to catch null UML2 resource"); //$NON-NLS-1$
    // } catch ( IllegalArgumentException e ) {
    // // expected
    // } catch ( Throwable t ) {
    // UnitTestUtil.fail(t);
    // }
    //
    // // Try null datatype ...
    // try {
    // ModelGenerator gen =
    // ModelGeneratorPlugin.createUml2RelationalGenerator(uml2Resource,relationalResource,nullResource,relationResource);
    // UnitTestUtil.assertNotNull( gen.getDatatypeSelector() );
    // UnitTestUtil.assertTrue( gen.getDatatypeSelector() instanceof BuiltInDatatypeFinder );
    // UnitTestUtil.assertNotNull( gen.getRelationTracker() );
    // UnitTestUtil.assertTrue( gen.getRelationTracker() instanceof RelationTrackerImpl );
    // } catch ( CoreException t ) {
    // UnitTestUtil.fail(t);
    // }
    //
    // try {
    // ModelGenerator gen =
    // ModelGeneratorPlugin.createUml2RelationalGenerator(uml2Resource,relationalResource,datatypeResource,nullResource);
    // UnitTestUtil.assertNotNull( gen.getDatatypeSelector() );
    // UnitTestUtil.assertTrue( gen.getDatatypeSelector() instanceof XsdDatatypeFinder );
    // UnitTestUtil.assertNotNull( gen.getRelationTracker() );
    // UnitTestUtil.assertTrue( gen.getRelationTracker() instanceof RelationTrackerImpl );
    // } catch ( CoreException t ) {
    // UnitTestUtil.fail(t);
    // }
    // }
    //
    public void testNonPlatformGetStringWithNullKey() {
        final String key = null;
        final String expectedValue = "<No message available>"; //$NON-NLS-1$
        helpTestGetString(key, expectedValue);
    }

    public void testNonPlatformGetStringWithZeroLengthKey() {
        final String key = ""; //$NON-NLS-1$
        final String expectedValue = "<Missing message for key \"\" in: com.metamatrix.modeler.modelgenerator.i18n>"; //$NON-NLS-1$
        helpTestGetString(key, expectedValue);
    }

    // public void testNonPlatformGetStringThatShouldBeFoundWithTrailingSpace() {
    //        final String key = "ReflectionUtil.>>_ERROR_invoking_method__7"; //$NON-NLS-1$
    //        final String expectedValue = "ERROR invoking method "; //$NON-NLS-1$
    // helpTestGetString(key,expectedValue);
    // }

    public void testNonPlatformGetStringThatShouldBeFound() {
        final String key = "UserCancelledException.User_cancelled_operation_msg"; //$NON-NLS-1$
        final String expectedValue = "User cancelled operation"; //$NON-NLS-1$
        helpTestGetString(key, expectedValue);
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
        ModelGeneratorPlugin.Util.log(status);
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
        ModelGeneratorPlugin.Util.log(status);
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
        ModelGeneratorPlugin.Util.log(status);
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
        ModelGeneratorPlugin.Util.log(status);
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

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogStringObject() {
        final Object obj = "This is a string object that is output"; //$NON-NLS-1$
        ModelGeneratorPlugin.Util.log(obj);
    }

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogDateObject() {
        final Object obj = new Date();
        ModelGeneratorPlugin.Util.log(obj);
    }

    /*
     * Test for void log(Object)
     */
    public void testNonPlatformLogNullObject() {
        final Object obj = null;
        ModelGeneratorPlugin.Util.log(obj);
    }

    /*
     * Test for void log(Throwable)
     */
    public void testNonPlatformLogThrowable() {
        final Throwable t = new Throwable("This is the throwable"); //$NON-NLS-1$
        t.fillInStackTrace();
        ModelGeneratorPlugin.Util.log(t);
    }

}
