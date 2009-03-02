/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.lds;

import java.util.Properties;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.webservice.WebServicePlugin;

/**
 * @since 4.2
 */
public class TestWebArchiveBuilder extends TestCase {

    /**
     * The WebArchiveBuilder to use during the tests.
     */
    private WebArchiveBuilder builder = null;

    /**
     * Constructor for TestWebArchiveBuilder.
     * 
     * @param name
     */
    public TestWebArchiveBuilder(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        builder = WebArchiveBuilderFactory.create();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
        builder = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {

        TestSuite suite = new SmartTestSuite("com.metamatrix.modeler.webservice", "TestWebArchiveBuilder"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestWebArchiveBuilder.class);

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
    // T E S T C A S E S
    // =========================================================================

    /**
     * Test the construction of the Web archive Builder.
     */
    public void testConstruction() {
        assertNotNull(builder);
    }

    /**
     * The the content name validation.
     * 
     * @since 4.4
     */
    public void testContextNameValidation() {
        
        IStatus status = null;

        // These should succeed        
        status = builder.validateContextName("myapp"); //$NON-NLS-1$
        assertEquals(IStatus.OK, status.getSeverity());
        assertEquals(WebServicePlugin.PLUGIN_ID, status.getPlugin());
        assertEquals(WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_SUCCEEDED, status.getCode());
        assertEquals("Context name is valid", status.getMessage()); //$NON-NLS-1$ 
        assertNull(status.getException());
        
        assertEquals(IStatus.OK, builder.validateContextName("myApp").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("my-app").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("my_app").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("myapp!@#$%^&*").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("myapp1234567890").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("1myapp").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("$myapp").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("1").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("_").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("-").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("$").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("?").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("#").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("index.html").getSeverity()); //$NON-NLS-1$        
        assertEquals(IStatus.OK, builder.validateContextName("index.html?#").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.OK, builder.validateContextName("index.html?abc#").getSeverity()); //$NON-NLS-1$

        // These should fail, because of empty context
        status = builder.validateContextName(StringUtil.Constants.EMPTY_STRING);
        assertEquals(IStatus.ERROR, status.getSeverity());
        assertEquals(WebServicePlugin.PLUGIN_ID, status.getPlugin());
        assertEquals(WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, status.getCode());
        assertEquals("Context name cannot be empty", status.getMessage()); //$NON-NLS-1$
        assertNull(status.getException());

        // These should fail, because of forward slash        
        status = builder.validateContextName("/myapp"); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, status.getSeverity());
        assertEquals(WebServicePlugin.PLUGIN_ID, status.getPlugin());
        assertEquals(WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, status.getCode());
        assertEquals("Context name contains an invalid character: /", status.getMessage()); //$NON-NLS-1$
        assertNull(status.getException());
        
        assertEquals(IStatus.ERROR, builder.validateContextName("myapp/").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, builder.validateContextName("my/app").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, builder.validateContextName("myapp/index.html").getSeverity()); //$NON-NLS-1$

        // These should fail, because of back slash        
        status = builder.validateContextName("\\myapp"); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, status.getSeverity());
        assertEquals(WebServicePlugin.PLUGIN_ID, status.getPlugin());
        assertEquals(WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, status.getCode());
        assertEquals("Context name contains an invalid character: \\", status.getMessage()); //$NON-NLS-1$
        assertNull(status.getException());      
        
        assertEquals(IStatus.ERROR, builder.validateContextName("my\\app").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, builder.validateContextName("myapp\\").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, builder.validateContextName("myapp\\index.html").getSeverity()); //$NON-NLS-1$

        // These should fail, because of space
        status = builder.validateContextName("my app"); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, status.getSeverity()); 
        assertEquals(WebServicePlugin.PLUGIN_ID, status.getPlugin()); 
        assertEquals(WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, status.getCode());
        assertEquals("Context name cannot contain a space", status.getMessage()); //$NON-NLS-1$
        assertNull(status.getException());
        
        assertEquals(IStatus.ERROR, builder.validateContextName(" ").getSeverity()); //$NON-NLS-1$        

        // These should fail, because of some other reason
        status = builder.validateContextName("myapp("); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, status.getSeverity());
        assertEquals(WebServicePlugin.PLUGIN_ID, status.getPlugin());
        assertEquals(WebArchiveBuilderConstants.STATUS_CODE_CONTEXT_NAME_VALIDATION_FAILED, status.getCode());
        assertEquals("Context name is invalid", status.getMessage()); //$NON-NLS-1$
        assertNull(status.getException());
        
        assertEquals(IStatus.ERROR, builder.validateContextName("myapp)").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, builder.validateContextName("myapp[").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, builder.validateContextName("myapp]").getSeverity()); //$NON-NLS-1$
        assertEquals(IStatus.ERROR, builder.validateContextName("my|app").getSeverity()); //$NON-NLS-1$                    
    }

    public void testCreateAddlPropertiesString() {
        Properties props = new Properties();
        props.setProperty("key1", "value1"); //$NON-NLS-1$ //$NON-NLS-2$
        props.setProperty("key1>", "value1"); //$NON-NLS-1$ //$NON-NLS-2$
        props.setProperty("key13 & this", "value1 < other"); //$NON-NLS-1$ //$NON-NLS-2$
        props.setProperty("key2", "value2"); //$NON-NLS-1$ //$NON-NLS-2$
        
        String propString = ((DefaultWebArchiveBuilderImpl)builder).createAddlPropertiesString(props);
        assertTrue(propString.indexOf("key1=value1;")>-1); //$NON-NLS-1$
        assertTrue(propString.indexOf("key1&gt;=value1;")>-1); //$NON-NLS-1$
        assertTrue(propString.indexOf("key13 &amp; this=value1 &lt; other;")>-1); //$NON-NLS-1$
        assertTrue(propString.indexOf("key2=value2")>-1); //$NON-NLS-1$
        System.out.print(propString);
        
    }

}
