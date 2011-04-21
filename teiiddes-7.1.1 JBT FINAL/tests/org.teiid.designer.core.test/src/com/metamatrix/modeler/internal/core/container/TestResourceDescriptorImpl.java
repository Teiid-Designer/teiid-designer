/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * TestResourceDescriptorImpl
 */
public class TestResourceDescriptorImpl extends TestCase {

    private static final String FACTORY_CLASS_NAME = TestResourceFactory.class.getName();
    private static final String DEFAULT_UNIQUE_ID = "com.metamatrix.myResourceDescriptor"; //$NON-NLS-1$

    private ResourceDescriptorImpl descriptor;

    /**
     * Constructor for TestResourceDescriptorImpl.
     * 
     * @param name
     */
    public TestResourceDescriptorImpl( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.descriptor = new ResourceDescriptorImpl(DEFAULT_UNIQUE_ID);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.descriptor = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestResourceDescriptorImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestResourceDescriptorImpl.class);
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

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testDefaultConstructor() {
        if (this.descriptor.getExtensions() == null) {
            fail("getExtensions() should never return null"); //$NON-NLS-1$
        }
        if (this.descriptor.getExtensions().size() != 0) {
            fail("getExtensions() should be empty for default construction"); //$NON-NLS-1$
        }
        if (this.descriptor.getProtocols() == null) {
            fail("getProtocols() should never return null"); //$NON-NLS-1$
        }
        if (this.descriptor.getProtocols().size() != 0) {
            fail("getProtocols() should be empty for default construction"); //$NON-NLS-1$
        }
        if (this.descriptor.getUniqueIdentifier() != DEFAULT_UNIQUE_ID) { // check for same reference!
            fail("The unique identifier didn't match the Object passed in"); //$NON-NLS-1$
        }
        if (this.descriptor.toString() == null || this.descriptor.toString().length() == 0) {
            fail("toString() should not be empty or zero-length"); //$NON-NLS-1$
        }
        // if ( this.descriptor.getClassLoader() != this.getClass().getClassLoader() ) {
        // fail("The default class loader should be the same as the class loader for this class"); //$NON-NLS-1$
        // }
    }

    public void testInvalidConstructorWithNullUniqueID() {
        try {
            new ResourceDescriptorImpl(null);
            fail("Failed to catch null unique identifier"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testInvalidConstructorWithZeroLengthUniqueID() {
        try {
            new ResourceDescriptorImpl(""); //$NON-NLS-1$
            fail("Failed to catch null unique identifier"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testProtocolsIsModifiable() {
        try {
            this.descriptor.getProtocols().add("SomeProtocol"); //$NON-NLS-1$
        } catch (Throwable e) {
            fail("The protocols list is not modifiable"); //$NON-NLS-1$
        }
    }

    public void testExtensionsIsModifiable() {
        try {
            this.descriptor.getExtensions().add("SomeProtocol"); //$NON-NLS-1$
        } catch (Throwable e) {
            fail("The extensions list is not modifiable"); //$NON-NLS-1$
        }
    }

    public void testSettingResourceFactoryClass() {
        this.descriptor.setResourceFactoryClass(FACTORY_CLASS_NAME);
        if (!this.descriptor.getResourceFactoryClassName().equals(FACTORY_CLASS_NAME)) {
            fail("getResourceFactoryClassName didn't return value passed to setResourceFactoryClass(String)"); //$NON-NLS-1$
        }
    }

    public void testSettingResourceFactoryClassWithNullClassLoader() {
        this.descriptor.setResourceFactoryClass(FACTORY_CLASS_NAME, null);
        if (!this.descriptor.getResourceFactoryClassName().equals(FACTORY_CLASS_NAME)) {
            fail("getResourceFactoryClassName didn't return value passed to setResourceFactoryClass(String,ClassLoader)"); //$NON-NLS-1$
        }
        // if ( this.descriptor.getClassLoader() != this.getClass().getClassLoader() ) {
        // fail("The class loader should be the default"); //$NON-NLS-1$
        // }
    }

    //
    // public void testSettingResourceFactoryClassWithClassLoader() {
    // this.descriptor.setResourceFactoryClass(FACTORY_CLASS_NAME,NON_FUNCTIONING_CLASS_LOADER);
    // if ( !this.descriptor.getResourceFactoryClassName().equals(FACTORY_CLASS_NAME) ) {
    // fail("getResourceFactoryClassName didn't return value passed to setResourceFactoryClass(String,ClassLoader)");
    // //$NON-NLS-1$
    // }
    // if ( this.descriptor.getClassLoader() != NON_FUNCTIONING_CLASS_LOADER ) {
    // fail("The class loader should be same as passed into setResourceFactoryClass(String,ClassLoader)"); //$NON-NLS-1$
    // }
    // }

    public void testGetResourceFactoryWithoutFactoryClass() throws Exception {
        try {
            this.descriptor.getResourceFactory();
            fail("Should not have been able to create a factory"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // expected
        }
    }

    public void testGetResourceFactory() throws Exception {
        this.descriptor.setResourceFactoryClass(FACTORY_CLASS_NAME, null);
        try {
            this.descriptor.getResourceFactory();
            fail("Should not have been able to create a factory"); //$NON-NLS-1$
        } catch (NullPointerException e) {
            // expected
        }
    }
}

class TestResourceFactory implements Resource.Factory {
    public Resource createResource( URI uri ) {
        return null;
    }
}
