/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.util;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import com.metamatrix.modeler.core.util.UriPathConverter;

/**
 * BasicUriPathConverterTest
 */
public class TestBasicUriPathConverter extends TestCase {

    private static final String EXCEPTION_EXPECTED_RESULT = "Exception"; //$NON-NLS-1$

    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------

    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------

    // =========================================================================
    // F R A M E W O R K
    // =========================================================================

    /**
     * Constructor for BasicUriPathConverterTest.
     * 
     * @param name
     */
    public TestBasicUriPathConverter( String name ) {
        super(name);
    }

    // =========================================================================
    // T E S T C O N T R O L
    // =========================================================================

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestBasicUriPathConverter"); //$NON-NLS-1$
        suite.addTestSuite(TestBasicUriPathConverter.class);

        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }

            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }

    // =========================================================================
    // M A I N
    // =========================================================================

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    // =========================================================================
    // S E T U P A N D T E A R D O W N
    // =========================================================================

    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
    }

    /**
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
    }

    public static void oneTimeSetUp() {
    }

    public static void oneTimeTearDown() {
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    public UriPathConverter helpCreatePathConverter() {
        UriPathConverter converter = new BasicUriPathConverter();
        assertNotNull(converter);
        return converter;
    }

    public void helpTestMakeAbsolute( UriPathConverter converter,
                                      String relativePath,
                                      String basePath,
                                      String expectedResult ) throws Exception {
        String result = null;
        try {
            result = converter.makeAbsolute(relativePath, basePath);
        } catch (Exception e) {
            if (!EXCEPTION_EXPECTED_RESULT.equals(expectedResult)) {
                throw e;
            }
            return;
        }
        System.out.println("\nrelativePath = " + relativePath + //$NON-NLS-1$
                           "\nbasePath     = " + basePath + //$NON-NLS-1$
                           "\nabsolutePath = " + result); //$NON-NLS-1$
        if (expectedResult != null) {
            assertEquals(expectedResult, result);
        }
    }

    public void helpTestMakeAbsolute( UriPathConverter converter,
                                      URI relativeURI,
                                      URI baseURI,
                                      String expectedResult ) throws Exception {
        URI result = null;
        try {
            result = converter.makeAbsolute(relativeURI, baseURI);
        } catch (Exception e) {
            if (!EXCEPTION_EXPECTED_RESULT.equals(expectedResult)) {
                throw e;
            }
            return;
        }
        System.out.println("\nrelativeURI = " + relativeURI + //$NON-NLS-1$
                           "\nbaseURI     = " + baseURI + //$NON-NLS-1$
                           "\nabsoluteURI = " + result); //$NON-NLS-1$
        if (expectedResult != null) {
            assertEquals(expectedResult, result.toString());
        }
    }

    public void helpTestMakeRelative( UriPathConverter converter,
                                      String absolutePath,
                                      String basePath,
                                      String expectedResult ) throws Exception {
        String result = null;
        try {
            result = converter.makeRelative(absolutePath, basePath);
        } catch (Exception e) {
            if (!EXCEPTION_EXPECTED_RESULT.equals(expectedResult)) {
                throw e;
            }
            return;
        }
        System.out.println("\nabsolutePath = " + absolutePath + //$NON-NLS-1$
                           "\nbasePath     = " + basePath + //$NON-NLS-1$
                           "\nrelativePath = " + result); //$NON-NLS-1$
        if (expectedResult != null) {
            assertEquals(expectedResult, result);
        }
    }

    public void helpTestMakeRelative( UriPathConverter converter,
                                      URI absoluteURI,
                                      URI baseURI,
                                      String expectedResult ) throws Exception {
        URI result = null;
        try {
            result = converter.makeRelative(absoluteURI, baseURI);
        } catch (Exception e) {
            if (!EXCEPTION_EXPECTED_RESULT.equals(expectedResult)) {
                throw e;
            }
            return;
        }
        System.out.println("\nabsoluteURI = " + absoluteURI + //$NON-NLS-1$
                           "\nbaseURI     = " + baseURI + //$NON-NLS-1$
                           "\nrelativeURI = " + result); //$NON-NLS-1$
        if (expectedResult != null) {
            assertEquals(expectedResult, result.toString());
        }
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    // public void testDataPathSupplied() {
    // UnitTestUtil.assertTestDataPathSet();
    // }

    public void testBasicUriPathConverter() {
        helpCreatePathConverter();
    }

    /*
     * Test for String makeAbsolute(String, String)
     */
    public void testMakeAbsoluteStringString() throws Exception {
        final UriPathConverter converter = helpCreatePathConverter();
        helpTestMakeAbsolute(converter, null, "/a/b.xmi", EXCEPTION_EXPECTED_RESULT); //$NON-NLS-1$ 
        helpTestMakeAbsolute(converter, "/a/b.xmi", null, "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTestMakeAbsolute(converter, "/a/b.xmi", "", "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeAbsolute(converter, "/a/c/e.xmi", "/a/c/d.xmi", "/a/c/e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, "/a/b.xmi", "/a/c/d.xmi", "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, "/x/y/z.xmi", "/a/c/d.xmi", "/x/y/z.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeAbsolute(converter, "./e.xmi", "/a/c/d.xmi", "/a/c/e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, "e.xmi", "/a/c/d.xmi", "/a/c/e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, "../b.xmi", "/a/c/d.xmi", "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, "../../x/y/z.xmi", "/a/c/d.xmi", "/x/y/z.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeAbsolute(converter, "e.xmi", "file:/C:/a/c/d.xmi", "file:/C:/a/c/e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /*
     * Test for URI makeAbsolute(URI, URI)
     */
    public void testMakeAbsoluteURIURI() throws Exception {
        final UriPathConverter converter = helpCreatePathConverter();
        helpTestMakeAbsolute(converter, null, URI.createURI("/a/b.xmi"), EXCEPTION_EXPECTED_RESULT); //$NON-NLS-1$ 
        helpTestMakeAbsolute(converter, URI.createURI("/a/b.xmi"), null, "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$
        helpTestMakeAbsolute(converter, URI.createURI("/a/b.xmi"), URI.createURI(""), "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeAbsolute(converter, URI.createURI("/a/c/e.xmi"), URI.createURI("/a/c/d.xmi"), "/a/c/e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, URI.createURI("/a/b.xmi"), URI.createURI("/a/c/d.xmi"), "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, URI.createURI("/x/y/z.xmi"), URI.createURI("/a/c/d.xmi"), "/x/y/z.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeAbsolute(converter, URI.createURI("./e.xmi"), URI.createURI("/a/c/d.xmi"), "/a/c/e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, URI.createURI("e.xmi"), URI.createURI("/a/c/d.xmi"), "/a/c/e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, URI.createURI("../b.xmi"), URI.createURI("/a/c/d.xmi"), "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeAbsolute(converter, URI.createURI("../../x/y/z.xmi"), URI.createURI("/a/c/d.xmi"), "/x/y/z.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeAbsolute(converter, URI.createURI("e.xmi"), URI.createURI("file:/C:/a/c/d.xmi"), "file:/C:/a/c/e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /*
     * Test for String makeRelative(String, String)
     */
    public void testMakeRelativeStringString() throws Exception {
        final UriPathConverter converter = helpCreatePathConverter();
        helpTestMakeRelative(converter, null, "/a/b.xmi", EXCEPTION_EXPECTED_RESULT); //$NON-NLS-1$ 
        helpTestMakeRelative(converter, "/a/b.xmi", null, EXCEPTION_EXPECTED_RESULT); //$NON-NLS-1$ 
        helpTestMakeRelative(converter, "/a/b.xmi", "", "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeRelative(converter, "/a/c/e.xmi", "/a/c/d.xmi", "e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeRelative(converter, "/a/b.xmi", "/a/c/d.xmi", "../b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeRelative(converter, "/x/y/z.xmi", "/a/c/d.xmi", "../../x/y/z.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeRelative(converter, "/a/c/e.xmi", "/a/c/d.xmi", "e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeRelative(converter, "/a/b.xmi", "/a/c/d.xmi", "../b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeRelative(converter, "/x/y/z.xmi", "/a/c/d.xmi", "../../x/y/z.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    /*
     * Test for URI makeRelative(URI, URI)
     */
    public void testMakeRelativeURIURI() throws Exception {
        final UriPathConverter converter = helpCreatePathConverter();
        helpTestMakeRelative(converter, null, URI.createURI("/a/b.xmi"), EXCEPTION_EXPECTED_RESULT); //$NON-NLS-1$ 
        helpTestMakeRelative(converter, URI.createURI("/a/b.xmi"), null, EXCEPTION_EXPECTED_RESULT); //$NON-NLS-1$ 
        helpTestMakeRelative(converter, URI.createURI("/a/b.xmi"), URI.createURI(""), "/a/b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeRelative(converter, URI.createURI("/a/c/e.xmi"), URI.createURI("/a/c/d.xmi"), "e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeRelative(converter, URI.createURI("/a/b.xmi"), URI.createURI("/a/c/d.xmi"), "../b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeRelative(converter, URI.createURI("/x/y/z.xmi"), URI.createURI("/a/c/d.xmi"), "../../x/y/z.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        helpTestMakeRelative(converter, URI.createURI("/a/c/e.xmi"), URI.createURI("/a/c/d.xmi"), "e.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeRelative(converter, URI.createURI("/a/b.xmi"), URI.createURI("/a/c/d.xmi"), "../b.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        helpTestMakeRelative(converter, URI.createURI("/x/y/z.xmi"), URI.createURI("/a/c/d.xmi"), "../../x/y/z.xmi"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
