/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.io.IOException;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.core.util.SmartTestDesignerSuite;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * TestResourceFileIndexSelector
 */
public class TestResourceFileIndexSelector extends TestCase {

    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------
    private static final String TEST_MODEL_FILE_NAME = SmartTestDesignerSuite.getTestDataPath() + File.separator
                                                       + "PartsSupplierOracle100.xmi"; //$NON-NLS-1$
    private static final String TEST_MODEL_FOLDER_NAME = SmartTestDesignerSuite.getTestDataPath();
    private static final String TEST_ZIP_FILE_NAME = SmartTestDesignerSuite.getTestDataPath() + File.separator + "builtInDatatypes.zip"; //$NON-NLS-1$

    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------

    // =========================================================================
    // F R A M E W O R K
    // =========================================================================

    /**
     * Constructor for TestResourceFileIndexSelector.
     * 
     * @param name
     */
    public TestResourceFileIndexSelector( String name ) {
        super(name);
    }

    // =========================================================================
    // T E S T C O N T R O L
    // =========================================================================

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestResourceFileIndexSelector"); //$NON-NLS-1$
        suite.addTestSuite(TestResourceFileIndexSelector.class);

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

    static String userDir = System.getProperty("user.dir"); //$NON-NLS-1$

    public static void oneTimeSetUp() {
        System.setProperty("user.dir", SmartTestDesignerSuite.getTestScratchPath()); //$NON-NLS-1$
        ModelerCore plugin = new ModelerCore();
        ((PluginUtilImpl)ModelerCore.Util).initializePlatformLogger(plugin);
    }

    public static void oneTimeTearDown() {
        System.setProperty("user.dir", userDir); //$NON-NLS-1$
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    public static File[] helpLoadModelsFromFile( final ResourceFileIndexSelector selector,
                                                 final String filepath ) {
        File file = new File(filepath);
        return selector.loadModelsFromFile(file);
    }

    public static File[] helpLoadModelsFromFolder( final ResourceFileIndexSelector selector,
                                                   final String filepath ) {
        File folder = new File(filepath);
        return selector.loadModelsFromFolder(folder);
    }

    public static File[] helpLoadModelsFromZip( final ResourceFileIndexSelector selector,
                                                final String filepath ) throws IOException {
        File zipFile = new File(filepath);
        return selector.loadModelsFromZip(zipFile);
    }

    public static Resource[] helpLoadResources( final ResourceFileIndexSelector selector,
                                                final File[] modelFiles ) throws CoreException {
        return selector.loadResources(modelFiles);
    }

    public static Index[] helpIndexResources( final ResourceFileIndexSelector selector,
                                              final Resource[] models ) throws CoreException {
        return selector.indexResources(models);
    }

    public void testCreate() {
        System.out.println("\nTestResourceFileIndexSelector.testCreate()"); //$NON-NLS-1$
        try {
            new ResourceFileIndexSelector(null);
            fail("Expected failure but got success"); //$NON-NLS-1$
        } catch (Throwable e) {
            // expected
        }
    }

    public void testCreate2() {
        System.out.println("\nTestResourceFileIndexSelector.testCreate2()"); //$NON-NLS-1$
        String filepath = SmartTestDesignerSuite.getTestDataPath() + File.separator + "nonExistentFile"; //$NON-NLS-1$
        try {
            new ResourceFileIndexSelector(filepath);
            fail("Expected failure but got success"); //$NON-NLS-1$
        } catch (Throwable e) {
            // expected
        }
    }

    public void testCreate3() throws Exception {
        System.out.println("\nTestResourceFileIndexSelector.testCreate3()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        new ResourceFileIndexSelector(filepath);
    }

    public void testLoadModelFromFile() throws Exception {
        System.out.println("\nTestResourceFileIndexSelector.testLoadModelFromFile()"); //$NON-NLS-1$
        String filepath = TEST_MODEL_FILE_NAME;
        ResourceFileIndexSelector selector = new ResourceFileIndexSelector(filepath);
        File[] modelFiles = selector.loadModelsFromFile(new File(filepath));
        assertEquals(1, modelFiles.length);
    }

    public void testLoadModelFromFolder() throws Exception {
        System.out.println("\nTestResourceFileIndexSelector.testLoadModelFromFolder()"); //$NON-NLS-1$
        String filepath = TEST_MODEL_FOLDER_NAME;
        ResourceFileIndexSelector selector = new ResourceFileIndexSelector(filepath);
        File[] modelFiles = selector.loadModelsFromFolder(new File(filepath));
        assertTrue(modelFiles.length > 1);
    }

    public void testLoadModelsFromZip() throws Exception {
        System.out.println("\nTestResourceFileIndexSelector.testLoadModelsFromZip()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        ResourceFileIndexSelector selector = new ResourceFileIndexSelector(filepath);
        File[] modelFiles = selector.loadModelsFromZip(new File(filepath));
        assertEquals(1, modelFiles.length);
    }

}
