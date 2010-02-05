/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.util.SmartTestSuite;

/**
 * @since 4.2
 */
public class TestURLClassLoaderRegistry extends TestCase {

    private URLClassLoaderRegistry registry;

    /**
     * Constructor for TestURLClassLoaderRegistry.
     * 
     * @param name
     */
    public TestURLClassLoaderRegistry( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        registry = new URLClassLoaderRegistry();

    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new SmartTestSuite(CoreModelerPlugin.PLUGIN_ID, "TestURLClassLoaderRegistry"); //$NON-NLS-1$ 
        suite.addTestSuite(TestURLClassLoaderRegistry.class);
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

    public void helpLoadClass( final URLClassLoaderRegistry reg,
                               final URL[] urls,
                               final String className,
                               final boolean shouldFind ) throws Exception {
        assertNotNull(reg);
        assertNotNull(urls);

        // Find the class loader with the supplied URLs ...
        URLClassLoader loader = reg.getClassLoader(urls);
        assertNotNull(loader);
        helpLoadClass(loader, className, shouldFind);

        // Build the array of Strings ...
        final String[] urlStrings = new String[urls.length];
        for (int i = 0; i < urlStrings.length; i++) {
            String urlString = urls[i].toString();
            urlStrings[i] = urlString;
        }

        // Find the class loader with the supplied URLs ...
        loader = reg.getClassLoader(urlStrings);
        assertNotNull(loader);
        helpLoadClass(loader, className, shouldFind);

    }

    public void helpLoadClass( final URLClassLoader loader,
                               final String className,
                               final boolean shouldFind ) throws Exception {
        try {
            // Try to load the class ...
            final Class clazz = loader.loadClass(className);
            if (shouldFind) {
                assertNotNull(clazz);
            }
        } catch (ClassNotFoundException err) {
            if (shouldFind) {
                throw err;
            }
            // Else expected ...
        }
    }

    public void testSetup() {
        assertNotNull(registry);
    }

    public void testGetClassLoaderWithNullURLArgument() {
        try {
            registry.getClassLoader((URL[])null);
            fail("Was expecting exception with null URL arg"); //$NON-NLS-1$
        } catch (IllegalArgumentException err) {
            // expected
        }
    }

    public void testGetClassLoaderWithNullURLArgumentWithNullParent() {
        try {
            registry.getClassLoader((URL[])null, null);
            fail("Was expecting exception with null URL arg"); //$NON-NLS-1$
        } catch (IllegalArgumentException err) {
            // expected
        }
    }

    public void testGetClassLoaderWithNullStringArrayArgument() throws Exception {
        try {
            registry.getClassLoader((String[])null);
            fail("Was expecting exception with null String arg"); //$NON-NLS-1$
        } catch (IllegalArgumentException err) {
            // expected
        }
    }

    public void testGetClassLoaderWithNullStringArrayArgumentWithNullParent() throws Exception {
        try {
            registry.getClassLoader((String[])null, null);
            fail("Was expecting exception with null String arg"); //$NON-NLS-1$
        } catch (IllegalArgumentException err) {
            // expected
        }
    }

    public void testGetClassLoaderWithEmptyURLArgument() {
        registry.getClassLoader(new URL[] {});
    }

    public void testGetClassLoaderWithEmptyURLArgumentWithNullParent() {
        registry.getClassLoader(new URL[] {}, null);
    }

    public void testGetClassLoaderWithEmptyStringArrayArgument() throws Exception {
        registry.getClassLoader(new String[] {}, null);
    }

    public void testGetClassLoaderWithEmptyStringArrayArgumentWithNullParent() throws Exception {
        registry.getClassLoader(new String[] {}, null);
    }
}
