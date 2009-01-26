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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.junit.Ignore;
import com.metamatrix.core.CoreConstants;

@Ignore
public class SmartTestSuite extends TestSuite {

    public static final String DEFAULT_TESTDATA_PATH = "testdata"; //$NON-NLS-1$

    /**
     * This property is obtained from the System properties to find the location of test data files. For the safest use, use
     * {@link #getTestDataPath} .
     */
    private static final String TEST_DATA_ROOT_PROPERTY = "test.data.root"; //$NON-NLS-1$

    /**
     * This property is obtained from the System properties to find the location of a scratch area during testing. For the safest
     * use, use {@link #getTestScratchPath} .
     */
    private static final String TEST_DATA_SCRATCH_PROPERTY = "test.data.scratch"; //$NON-NLS-1$

    /**
     * This property is obtained from the System properties to find the location of test source files. For the safest use, use
     * {@link #getTestSourcePath} .
     */
    private static final String TEST_SOURCE_ROOT_PROPERTY = "test.source.root"; //$NON-NLS-1$

    static {
        String root = System.getProperty("plugins.root"); //$NON-NLS-1$
        if (root == null) {
            String userdir = System.getProperty("user.dir") + "/.."; //$NON-NLS-1$ //$NON-NLS-2$
            File f = new File(userdir);
            String pluginroot = f.getAbsolutePath();
            pluginroot = pluginroot.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
            System.setProperty("plugins.root", pluginroot); //$NON-NLS-1$
        }
    }

    protected String testdataPath;
    private String testSourcePath;

    private static final String PROJECT_ROOT_PROPERTY_NAME = "projectRoot"; //$NON-NLS-1$ 
    private static final String GLOBAL_TEST_DATA_SUBDIR = "/com.metamatrix.core.modeler.test/testdata/"; //$NON-NLS-1$
    private static final String DEFAULT_TESTSOURCE_PATH = "testsrc"; //$NON-NLS-1$

    private static String projectRootDir = null;
    private static String userDefinedProjectRootDir = null;
    private static String globalTestDataDir = null;

    private static synchronized void initPaths() {
        if (projectRootDir == null) {
            userDefinedProjectRootDir = "."; //$NON-NLS-1$
            try {
                File userDir = new File(System.getProperty("user.dir") + "/.."); //$NON-NLS-1$ //$NON-NLS-2$
                String rootDir = userDir.getCanonicalPath();
                projectRootDir = System.getProperty(PROJECT_ROOT_PROPERTY_NAME, rootDir);
                globalTestDataDir = projectRootDir + GLOBAL_TEST_DATA_SUBDIR;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getGlobalTestDataPath() {
        initPaths();
        return globalTestDataDir;
    }

    public static String getProjectPath( String projectName ) {
        initPaths();
        return projectRootDir + File.separator + projectName;
    }

    public static String getUserDefinedProjectPath( String projectName ) {
        initPaths();
        return userDefinedProjectRootDir + File.separator + projectName;
    }

    public SmartTestSuite( String projectName,
                           String testSuiteName ) {
        super(testSuiteName);

        // set test source and data directories
        projectName = projectName.endsWith(".test") ? projectName : projectName + ".test"; //$NON-NLS-1$ //$NON-NLS-2$
        String projectPath = getProjectPath(projectName);
        String testDataPath = projectPath + '/' + SmartTestSuite.DEFAULT_TESTDATA_PATH;
        File testDataPathFile = new File(testDataPath);
        if (!testDataPathFile.exists()) {
            testDataPath = getUserDefinedProjectPath(projectName) + '/' + SmartTestSuite.DEFAULT_TESTDATA_PATH;
        }
        setTestDataPath(testDataPath);

        // Set the test source path
        String testSrcPath = projectPath + '/' + DEFAULT_TESTSOURCE_PATH;
        File testSrcPathFile = new File(testSrcPath);
        if (!testSrcPathFile.exists()) {
            testSrcPath = getUserDefinedProjectPath(projectName) + '/' + DEFAULT_TESTSOURCE_PATH;
        }
        setTestSourcePath(testSrcPath);
    }

    public SmartTestSuite( String testdataPath,
                           Class<Test> testClass ) {
        super(testClass);
        this.testdataPath = testdataPath;
    }

    /* 
     * @see junit.framework.TestSuite#runTest(junit.framework.Test, junit.framework.TestResult)
     */
    @Override
    public void runTest( Test test,
                         TestResult result ) {
        setTestDataPath(testdataPath);
        super.runTest(test, result);
    }

    /* 
     * @see junit.framework.TestSuite#addTestSuite(java.lang.Class)
     */
    @Override
    public void addTestSuite( Class testClass ) {
        addTest(new SmartTestSuite(testdataPath, testClass));
    }

    protected String getTestSourcePath() {
        return this.testSourcePath;
    }

    protected void setTestDataPath( final String testdataPath ) {
        this.testdataPath = testdataPath;
        System.getProperties().setProperty(SmartTestSuite.TEST_DATA_ROOT_PROPERTY, testdataPath);
    }

    protected void setTestSourcePath( final String thePath ) {
        File file = new File(thePath);
        this.testSourcePath = file.getPath();
        System.getProperties().setProperty(SmartTestSuite.TEST_SOURCE_ROOT_PROPERTY, this.testSourcePath);
    }

    /**
     * Calls {@link Assert#fail(String) fail}, passing the specified exception's stack trace, converted to a string, as the
     * message argument.
     * 
     * @param error The caught exception
     * @since 3.0
     * @deprecated simply throw the exception
     */
    @Deprecated
    public static void fail( final Throwable error ) {
        final StringWriter trace = new StringWriter();
        error.printStackTrace(new PrintWriter(trace));
        Assert.fail(trace.toString());
    }

    /**
     * Calls {@link Assert#fail(String) fail}, passing the specified exception's stack trace, converted to a string, as the
     * message argument.
     * 
     * @param error The caught exception
     * @since 3.0
     * @deprecated simply throw the exception
     */
    @Deprecated
    public static void fail( final Throwable error,
                             final String message ) {
        final StringWriter trace = new StringWriter();
        error.printStackTrace(new PrintWriter(trace));
        Assert.fail(message + ":" + trace.toString()); //$NON-NLS-1$
    }

    /**
     * Obtain a {@link File}for the file name in the test data directory (given by {@link #getTestDataPath()}).
     * 
     * @param fileName A path and name relative to the test data directory; for example, "MyFile.txt" if the file is in the test
     *        data directory, or "subfolder/MyFile.txt" if the file is in "subfolder".
     * @return The File referencing the file with the specified fileName within the test data directory
     */
    public static File getTestDataFile( String fileName ) {
        return new File(getTestDataPath(), fileName);
    }

    public static File getTestScratchFile( String fileName ) {
        // Create the input stream ...
        String path = getTestScratchPath();
        File scratchDirectory = new File(path);
        if (!scratchDirectory.exists()) {
            scratchDirectory.mkdir();
        }
        File file = new File(scratchDirectory, fileName);
        return file;
    }

    /**
     * Obtain a {@link java.io.FileInputStream FileInputStream} to a data file in the test data directory (given by
     * {@link #getTestDataPath()}). This method causes the test case to fail if the file could not be found or opened.
     * 
     * @param filename the path and name of the file relative to the test data directory; for example, "MyFile.txt" if the file is
     *        in the test data directory, or "subfolder/MyFile.txt" if the file is in "subfolder".
     * @return the FileInputStream to the file, which should be closed by the caller when finished
     */
    public static final FileOutputStream getTestScratchFileOutputStream( String filename ) {
        FileOutputStream stream = null;
        try {
            File file = getTestScratchFile(filename);
            stream = new FileOutputStream(file);
        } catch (IOException e) {
            Assert.fail("Unable to open the scratch output file \"" + filename + "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return stream;
    }

    public static void setDefaultProperties() {
        System.getProperties().setProperty(CoreConstants.NO_CONFIGURATION, "true"); //$NON-NLS-1$ 
    }

    /**
     * Obtain the file path to the root of the test data file tree. This first checks the property {@link TEST_DATA_ROOT_PROPERTY}
     * , and if that is not set to a valid path, uses the current directory.
     * 
     * @return File path, never null
     */
    public static final String getTestDataPath() {
        String filePath = System.getProperty(TEST_DATA_ROOT_PROPERTY);
        if (filePath == null) {
            filePath = DEFAULT_TESTDATA_PATH;
        }
        return filePath;
    }

    /**
     * Obtain the file path to a scratch area where files may be created during testing. This first checks the property
     * {@link TEST_DATA_SCRATCH_PROPERTY}. If that is not set to a valid path, it then checks the "java.io.tmpdir" property. If
     * that is not set, it uses the current directory.
     * 
     * @return File path, never null
     */
    public static final String getTestScratchPath() {
        String filePath = System.getProperty(TEST_DATA_SCRATCH_PROPERTY);
        if (filePath == null) {
            filePath = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$

            if (filePath == null) {
                filePath = "."; //$NON-NLS-1$
            }
        }

        return filePath;
    }

    /**
     * This should be used for the expected results in a file; with different style of line separator characters.
     * 
     * @param expected
     * @param actual
     */
    public static void assertEqualsMultiLineString( String expected,
                                                    String actual ) {
        BufferedReader eReader = new BufferedReader(new StringReader(expected));
        BufferedReader aReader = new BufferedReader(new StringReader(actual));
        int line = 0;
        try {
            String aLine = aReader.readLine();
            String eLine = eReader.readLine();

            while (aLine != null) {
                Assert.assertEquals(eLine, aLine);
                aLine = aReader.readLine();
                eLine = eReader.readLine();
                line++;
            }
            if (eLine != null) {
                Assert.fail("More lines expected; missing from=" + eLine); //$NON-NLS-1$
            }
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail("IOException caught"); //$NON-NLS-1$
        }
    }

}
