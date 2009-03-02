/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import junit.framework.Test;
import org.junit.Ignore;

/**
 * The <code>SmartAllTestsSuite</code> class is a {@link junit.framework.TestSuite} that uses reflection to find the test classes
 * to run.
 * 
 * @since 4.2
 */
@Ignore
public class SmartAllTestsSuite extends SmartTestSuite {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Place the character '#' in front of lines in the excluded test file to indicate the line is a comment.
     */
    private static final String COMMENT_PREFIX = "#"; //$NON-NLS-1$

    /**
     * The name of the file that includes the test classes that should not be run. Each line of the file consists of the full
     * class name of the test class that should not be run.
     */
    protected static final String EXCLUDED_TESTS_FILE_NAME = ".excludedTests"; //$NON-NLS-1$

    /**
     * The name of the standard test class <code>suite()</code> method.
     */
    protected static final String SUITE_METHOD_NAME = "suite"; //$NON-NLS-1$

    /**
     * The common prefix or class names of test classes.
     */
    protected static final String TEST_FILE_NAME_PREFIX = "Test"; //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The class loader used to load the test classes. If one is not set, the system loader is used. <strong>This field should not
     * be accessed directly as it is lazily created.</strong>
     * 
     * @see #getClassLoader()
     */
    private ClassLoader classLoader;

    /**
     * The collection of full test class names of the tests that should not be run. <strong>This field should not be accessed
     * directly as it is lazily created.</strong>
     * 
     * @see #getExcludedTests()
     */
    private Collection excludedTests;

    /**
     * The index used when stripping off the test source path directory in order to find the test class name.
     * 
     * @see #getClassName(String)
     */
    protected int testClassNameStartIndex;

    /**
     * Indicates if directories will be recursed when finding test classes.
     */
    protected boolean recurse = true;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a <code>SmartAllTestsSuite</code>.
     * 
     * @param theProjectName the project name
     * @param theTestSuiteName the name of this suite
     */
    public SmartAllTestsSuite( String theProjectName,
                               String theTestSuiteName ) {
        super(theProjectName, theTestSuiteName);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds a test to the collection of test that will <strong>NOT</strong> be executed.
     * 
     * @param theTestClassName the full class name of the test class
     * @since 4.2
     */
    protected void addExcludedTest( String theTestClassName ) {
        getExcludedTests().add(theTestClassName);
    }

    /**
     * Collects all tests and adds them to this suite. Must be called before running the this suite.
     * 
     * @since 4.2
     */
    public void collectTests() {
        String sourcePath = getTestSourcePath();
        this.testClassNameStartIndex = sourcePath.length() + 1;

        try {
            Collection tests = collectTests(sourcePath);
            processTests(tests);
        } catch (IOException theException) {
            theException.printStackTrace();
        }
    }

    /**
     * Collects all test class names whose tests should be run. All tests that have been identified as being excluded will not be
     * returned.
     * 
     * @param theDirectoryName the name of the directory where tests should be collected
     * @return the collection of test class names that should be run (never <code>null</code>)
     * @throws AssertionError if the parameter is not a directory or if the directory does not exist
     * @throws IOException if an exception occurs when accessing the file system
     * @since 4.2
     */
    protected Collection collectTests( String theDirectoryName ) throws IOException {
        File directory = new File(theDirectoryName);
        Assertion.assertTrue(directory.exists(), "Test source path <" + theDirectoryName + "> does not exist"); //$NON-NLS-1$ //$NON-NLS-2$
        Assertion.assertTrue(directory.isDirectory(), "Parameter <" + theDirectoryName + "> is not a director"); //$NON-NLS-1$ //$NON-NLS-2$

        Collection result = new ArrayList();
        File[] files = directory.listFiles();

        if (files.length > 0) {
            String sourcePath = getTestSourcePath();

            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory() && this.recurse) {
                    result.addAll(collectTests(files[i].getCanonicalPath()));
                } else if (files[i].isFile()) {
                    String name = files[i].getName();

                    if (StringUtil.startsWithIgnoreCase(name, TEST_FILE_NAME_PREFIX) && FileUtils.isJavaFileName(name)) {
                        String path = files[i].getCanonicalPath();

                        if (StringUtil.startsWithIgnoreCase(path, sourcePath)) {
                            String testClassName = getClassName(path);

                            if (!getExcludedTests().contains(testClassName)) {
                                result.add(testClassName);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Obtains the {@link ClassLoader} to use load test classes. If one is not set the system loader is used.
     * 
     * @return the class loader (never <code>null</code>)
     * @since 4.2
     */
    protected ClassLoader getClassLoader() {
        if (this.classLoader == null) {
            this.classLoader = ClassLoader.getSystemClassLoader();
        }

        return this.classLoader;
    }

    /**
     * Obtains the full test class name for the specified full file path. The parameter must not be <code>null</code> and must be
     * at least as long as the source path string plus 2.
     * 
     * @param theFullFilePath the full file system path
     * @return the full test class name
     * @since 4.2
     */
    protected String getClassName( String theFullFilePath ) {
        String temp = theFullFilePath.substring(this.testClassNameStartIndex);
        temp = temp.replace(File.separatorChar, '.');

        return FileUtils.getFilenameWithoutExtension(temp);
    }

    /**
     * Obtains all the full test class names of the tests that will be excluded.
     * 
     * @return the excluded tests (never <code>null</code>)
     * @since 4.2
     */
    protected Collection getExcludedTests() {
        // initialize excluded tests by first reading in the excluded tests file
        if (this.excludedTests == null) {
            this.excludedTests = new HashSet();
            String fileName = getTestSourcePath() + File.separatorChar + EXCLUDED_TESTS_FILE_NAME;

            try {
                FileReader fileReader = new FileReader(fileName);
                BufferedReader in = new BufferedReader(fileReader);
                String excludedTest = in.readLine();

                if (excludedTest != null) {
                    do {
                        if (!excludedTest.startsWith(COMMENT_PREFIX)) {
                            this.excludedTests.add(excludedTest);
                        }

                        excludedTest = in.readLine();
                    } while (excludedTest != null);
                }
            } catch (FileNotFoundException theException) {
                // file not found - OK
                this.excludedTests = Collections.EMPTY_LIST;
            } catch (IOException theException) {
                // problem reading file
                this.excludedTests = Collections.EMPTY_LIST;
            }
        }

        return this.excludedTests;
    }

    /**
     * Processes the tests by adding them to this suite. If the test has a <code>suite()</code> method it is added.
     * 
     * @param theTests a collection of test class full names
     * @since 4.2
     */
    protected void processTests( Collection theTests ) {
        ClassLoader loader = getClassLoader();
        Iterator itr = theTests.iterator();

        while (itr.hasNext()) {
            String className = (String)itr.next();

            try {
                Class testClass = loader.loadClass(className);
                boolean added = false;

                // either add suite or class
                try {
                    Method method = testClass.getMethod(SUITE_METHOD_NAME);

                    if (Modifier.isStatic(method.getModifiers())) {
                        try {
                            this.addTest((Test)method.invoke(method));
                            added = true;
                        } catch (Exception theException) {
                            System.out.println("Problem executing suite() for class <" + className + ">"); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    }
                } catch (NoSuchMethodException theException) {
                    // static suite() method not found - OK
                }

                // no static suite() method found
                if (!added) {
                    this.addTestSuite(testClass);
                }
            } catch (ClassNotFoundException theException) {
                System.out.println("Could not load class <" + className + ">"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
    }

    /**
     * Sets the <code>ClassLoader</code> to use when loading test classes.
     * 
     * @param theClassLoader the class loader
     * @since 4.2
     */
    protected void setClassLoader( ClassLoader theClassLoader ) {
        this.classLoader = theClassLoader;
    }

}
