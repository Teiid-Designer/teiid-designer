/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestClassLoaderUtil
 */
public class TestClassLoaderUtil extends TestCase {

    private static final String[] JAR_FILE_NAMES = new String[] {"concurrent.jar"}; //$NON-NLS-1$

    private ClassLoaderUtil loaderUtil;

    /**
     * Constructor for TestClassLoaderUtil.
     * 
     * @param name
     */
    public TestClassLoaderUtil( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            final URL[] urls = new URL[JAR_FILE_NAMES.length];
            for (int i = 0; i < JAR_FILE_NAMES.length; i++) {
                final String filename = JAR_FILE_NAMES[i];
                final File file = SmartTestSuite.getTestDataFile(File.separator + filename);
                urls[i] = file.toURI().toURL();
            }
            final URLClassLoader loader = new URLClassLoader(urls, this.getClass().getClassLoader());
            this.loaderUtil = new ClassLoaderUtil(loader);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.loaderUtil = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestClassLoaderUtil"); //$NON-NLS-1$
        suite.addTestSuite(TestClassLoaderUtil.class);
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

    final class SomeNonPublicClass {
    }

    final class SomeClassWithoutNoArgConstructor {
        protected SomeClassWithoutNoArgConstructor( String s ) {
        }
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    public void helpPrintClassArray( final Class[] classes ) {
        for (int i = 0; i < classes.length; i++) {
            final Class c = classes[i];
            System.out.println("  " + c.getName() + " (" + (c.isInterface() ? "interface" : "class") + ")"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$
        }
    }

    public void helpCheckArray( final Class[] classes,
                                final int expectedSize,
                                final String desc ) {
        if (classes.length != expectedSize) {
            final String text = "Expected " + expectedSize + " classes; actual was " + classes.length; //$NON-NLS-1$//$NON-NLS-2$
            fail(text);
        }
    }

    public void testIsPublic1() {
        final Class clazz = List.class;
        if (!this.loaderUtil.isPublic(clazz)) {
            fail("The " + clazz.getName() + " class should be public"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public void testIsPublic2() {
        final Class clazz = String.class;
        if (!this.loaderUtil.isPublic(clazz)) {
            fail("The " + clazz.getName() + " class should be public"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public void testIsPublic3() {
        final Class clazz = SomeNonPublicClass.class;
        if (this.loaderUtil.isPublic(clazz)) {
            fail("The " + clazz.getName() + " class should not be public"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public void testHasNoArgConstructor1() {
        final Class clazz = String.class;
        if (!this.loaderUtil.hasAZeroArgConstructor(clazz)) {
            fail("The " + clazz.getName() + " class does have a no-arg constructor"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public void testHasNoArgConstructor2() {
        final Class clazz = ArrayList.class;
        if (!this.loaderUtil.hasAZeroArgConstructor(clazz)) {
            fail("The " + clazz.getName() + " class does have a no-arg constructor"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public void testHasNoArgConstructor3() {
        final Class clazz = SomeClassWithoutNoArgConstructor.class;
        if (this.loaderUtil.hasAZeroArgConstructor(clazz)) {
            fail("The " + clazz.getName() + " class does not have a no-arg constructor"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }
}
