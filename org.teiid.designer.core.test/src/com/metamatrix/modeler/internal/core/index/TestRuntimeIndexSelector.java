/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.internal.core.index.Index;

/**
 * TestRuntimeIndexSelector
 */
public class TestRuntimeIndexSelector extends TestCase {

    private static final String TEST_INDEX_FILE_NAME = SmartTestSuite.getTestDataPath() + File.separator + "dummy.INDEX"; //$NON-NLS-1$
    private static final String TEST_INDEX_FOLDER_NAME = SmartTestSuite.getTestDataPath();
    private static final String TEST_ZIP_FILE_NAME = SmartTestSuite.getTestDataPath() + File.separator + "TestPartsSupplier.vdb"; //$NON-NLS-1$
    private static final String TEST_ZIP_FILE_NAME2 = SmartTestSuite.getTestDataPath() + File.separator
                                                      + "TestPartsSupplierUpperCaseExtension.VDB"; //$NON-NLS-1$

    /**
     * Constructor for TestRuntimeIndexSelector.
     * 
     * @param name
     */
    public TestRuntimeIndexSelector( String name ) {
        super(name);
    }

    public void testCreateWithNonExistentPath() {
        System.out.println("\nTestRuntimeIndexSelector.testCreateWithNonExistentPath()"); //$NON-NLS-1$
        String filepath = SmartTestSuite.getTestDataPath() + File.separator + "nonExistentFile"; //$NON-NLS-1$
        try {
            new RuntimeIndexSelector(filepath);
            fail("Expected failure but got success"); //$NON-NLS-1$
        } catch (Throwable e) {
            // expected
        }
    }

    public void testCreateWithModelUpperCaseExtension() {
        System.out.println("\nTestRuntimeIndexSelector.testCreateWithModelUpperCaseExtension()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME2;
        new RuntimeIndexSelector(filepath);
    }

    public void testCreate3() {
        System.out.println("\nTestRuntimeIndexSelector.testCreate3()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        new RuntimeIndexSelector(filepath);
    }

    public void testLoadIndexesFromFile() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testLoadIndexesFromFile()"); //$NON-NLS-1$
        String filepath = TEST_INDEX_FILE_NAME;
        RuntimeIndexSelector selector = new RuntimeIndexSelector(filepath);
        Index[] indexes = selector.loadIndexesFromFile(new File(filepath));
        assertEquals(1, indexes.length);
        File testFile = new File(TEST_INDEX_FILE_NAME);
        assertEquals(testFile.getCanonicalPath(), indexes[0].getIndexFile().getCanonicalPath());
    }

    public void testLoadModelFromFolder() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testLoadModelFromFolder()"); //$NON-NLS-1$
        RuntimeIndexSelector selector = new RuntimeIndexSelector(TEST_INDEX_FOLDER_NAME);
        Index[] indexes = selector.loadIndexesFromFolder(new File(TEST_INDEX_FOLDER_NAME));
        assertEquals(1, indexes.length);
        File testFile = new File(TEST_INDEX_FILE_NAME);
        assertEquals(testFile.getCanonicalPath(), indexes[0].getIndexFile().getCanonicalPath());
    }

    public void testLoadModelsFromZip() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testLoadModelsFromZip()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        RuntimeIndexSelector selector = new RuntimeIndexSelector(filepath);
        Index[] indexes = selector.loadIndexesFromZip(new File(filepath));
        assertEquals(15, indexes.length);
        File indexDirectory = new File(selector.getIndexDirectoryPath());
        File[] files = indexDirectory.listFiles();
        assertEquals(4, files.length);
    }

    public void testClearVDB() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testClearVDB()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        RuntimeIndexSelector selector = new RuntimeIndexSelector(filepath);
        selector.loadIndexesFromZip(new File(filepath));
        File directory = new File(selector.getIndexDirectoryPath());
        assertTrue(directory.exists());
        selector.clearVDB();
        assertTrue(!directory.exists());
    }

    public void testGetFileContentAsString1() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testGetFileContentAsString1()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        RuntimeIndexSelector selector = new RuntimeIndexSelector(filepath);
        selector.loadIndexesFromZip(new File(filepath));
        File directory = new File(selector.getIndexDirectoryPath());
        assertTrue(directory.exists());
        String modelContents = selector.getFileContentAsString(File.separator
                                                               + "PartsSupplier" + File.separator + "PartsSupplierOracle.xmi"); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(modelContents);
    }

    public void testGetFileContentAsString2() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testGetFileContentAsString2()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        RuntimeIndexSelector selector = new RuntimeIndexSelector(filepath);
        selector.loadIndexesFromZip(new File(filepath));
        File directory = new File(selector.getIndexDirectoryPath());
        assertTrue(directory.exists());
        List paths = new ArrayList(2);
        paths.add(File.separator + "PartsSupplier" + File.separator + "PartsSupplierOracle.xmi"); //$NON-NLS-1$ //$NON-NLS-2$
        paths.add(File.separator + "PartsSupplier" + File.separator + "PartsSupplierVirtual.xmi"); //$NON-NLS-1$ //$NON-NLS-2$
        List contents = selector.getFileContentsAsString(paths);
        assertEquals(2, contents.size());
    }

    public void testGetFileContent1() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testGetFileContent1()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        RuntimeIndexSelector selector = new RuntimeIndexSelector(filepath);
        selector.loadIndexesFromZip(new File(filepath));
        File directory = new File(selector.getIndexDirectoryPath());
        assertTrue(directory.exists());
        InputStream modelContents = selector.getFileContent(File.separator
                                                            + "PartsSupplier" + File.separator + "PartsSupplierOracle.xmi"); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(modelContents);
    }

    public void testGetFileContent2() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testGetFileContent2()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        RuntimeIndexSelector selector = new RuntimeIndexSelector(filepath);
        selector.loadIndexesFromZip(new File(filepath));
        File directory = new File(selector.getIndexDirectoryPath());
        assertTrue(directory.exists());
        String[] tokens = {"xml"}; //$NON-NLS-1$
        String[] tokenReplacements = {"html"}; //$NON-NLS-1$
        InputStream modelContents = selector.getFileContent(File.separator
                                                            + "PartsSupplier" + File.separator + "PartsSupplierOracle.xmi", tokens, tokenReplacements); //$NON-NLS-1$ //$NON-NLS-2$
        assertNotNull(modelContents);
    }

    public void testGetFilePaths() throws Exception {
        System.out.println("\nTestRuntimeIndexSelector.testGetFilePaths()"); //$NON-NLS-1$
        String filepath = TEST_ZIP_FILE_NAME;
        RuntimeIndexSelector selector = new RuntimeIndexSelector(filepath);
        selector.loadIndexesFromZip(new File(filepath));
        File directory = new File(selector.getIndexDirectoryPath());
        assertTrue(directory.exists());
        String[] filePaths = selector.getFilePaths();
        assertNotNull(filePaths);
        assertEquals(19, filePaths.length);
    }

}
