/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.URIUtil;
import org.junit.Ignore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

@Ignore
public class SmartTestDesignerSuite extends TestSuite {

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
    private static final String GLOBAL_TEST_DATA_SUBDIR = "/org.teiid.core.designer.test/testdata/"; //$NON-NLS-1$
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

    public SmartTestDesignerSuite( String projectName,
                           String testSuiteName ) {
        super(testSuiteName);

        // set test source and data directories
        projectName = projectName.endsWith(".test") ? projectName : projectName + ".test"; //$NON-NLS-1$ //$NON-NLS-2$
        String projectPath = getProjectPath(projectName);
        String testDataPath = projectPath + '/' + SmartTestDesignerSuite.DEFAULT_TESTDATA_PATH;
        File testDataPathFile = new File(testDataPath);
        if (!testDataPathFile.exists()) {
            testDataPath = getUserDefinedProjectPath(projectName) + '/' + SmartTestDesignerSuite.DEFAULT_TESTDATA_PATH;
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

    public SmartTestDesignerSuite( String testdataPath,
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
        addTest(new SmartTestDesignerSuite(testdataPath, testClass));
    }

    protected String getTestSourcePath() {
        return this.testSourcePath;
    }

    protected void setTestDataPath( final String testdataPath ) {
        this.testdataPath = testdataPath;
    }

    protected void setTestSourcePath( final String thePath ) {
        File file = new File(thePath);
        this.testSourcePath = file.getPath();
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
     * Given a relative path, derive its absolute path from the given class.
     * 
     * @param testClass
     * @param path
     * @return
     */
    private static String getBundlePath(Class<?> testClass, Path path) {
        Bundle bundle = FrameworkUtil.getBundle(testClass);
        File file = null;
        try {
            URL url = FileLocator.find(bundle, path, null);
            URL fileURL = FileLocator.toFileURL(url);
            file = URIUtil.toFile(URIUtil.toURI(fileURL));
        }
        catch (Exception ex) {
            Assert.fail("Unable to open the data file \"" + path + "\""); //$NON-NLS-1$ //$NON-NLS-2$
            ex.printStackTrace();
        }
        
        return file.getAbsolutePath();
    }
    
    /**
     * Obtain a {@link File}for the file name in the test data directory (given by {@link #getTestDataPath()}).
     * 
     * @param testClass A class that should be used to find the correct bundle containing the testdata file. In
     *                          most cases this should be the test class calling this method.
     * @param fileName A path and name relative to the test data directory; for example, "MyFile.txt" if the file is in the test
     *        data directory, or "subfolder/MyFile.txt" if the file is in "subfolder".
     * @return The File referencing the file with the specified fileName within the test data directory
     */
    public static File getTestDataFile(Class<?> testClass, String fileName) {
        return new File(getTestDataPath(testClass), fileName);
    }

    /**
     * Obtain the file path to the root of the test data file tree. This first checks the property TEST_DATA_ROOT_PROPERTY
     * , and if that is not set to a valid path, uses the current directory.
     * 
     * @return File path, never null
     */
    public static final String getTestDataPath(Class<?> testClass) {
        String filePath = System.getProperty(TEST_DATA_ROOT_PROPERTY);
        if (filePath == null) {
            filePath = DEFAULT_TESTDATA_PATH;
        }
        
        return getBundlePath(testClass, new Path(filePath));
    }

    /**
     * Obtain the file path to a scratch area where files may be created during testing. This first checks the property
     * TEST_DATA_SCRATCH_PROPERTY. If that is not set to a valid path, it then checks the "java.io.tmpdir" property. If
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
    
    public static String getProjectPath( Class clazz ) {
        return getBundlePath(clazz, new Path("/")); //$NON-NLS-1$
    }
    
    /**
     * When declaring a new plugin in a junit suite method and initialising a platform logger,
     * an exception can occur since the plugin has not been started (this assigns the bundle
     * instance to the plugin's private bundle field). This mocks BundleContext and Bundle and
     * so starts the plugin.
     * 
     * @param plugin
     * @param pluginId
     */
    public static void mockStartBundle(Plugin plugin, String pluginId) {
        Bundle bundle  = mock(Bundle.class);
        when(bundle.getSymbolicName()).thenReturn(pluginId);
        
        BundleContext context = mock(BundleContext.class);
        when(context.getBundle()).thenReturn(bundle);
        
        try {
            plugin.start(context);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
