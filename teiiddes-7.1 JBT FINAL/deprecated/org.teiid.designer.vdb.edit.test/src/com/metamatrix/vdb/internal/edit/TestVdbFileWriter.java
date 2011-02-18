/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.ObjectConverterUtil;
import com.metamatrix.core.util.SmartTestSuite;

/**
 * @since 5.0
 */
public class TestVdbFileWriter extends TestCase {

    private static final IPath NEW_VDB_FILE_PATH = new Path(SmartTestSuite.getTestDataPath() + File.separator + "newVdb.vdb"); //$NON-NLS-1$
    private static final File VDB_WORKING_FOLDER = new File(SmartTestSuite.getTestDataPath() + File.separator
                                                            + "vdbWorkingFolder"); //$NON-NLS-1$

    private static final File[] TEST_MODELS = new File[] {
        SmartTestSuite.getTestDataFile("projects/Books Project/Books_Oracle.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Books Project/Books_SQLServer.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Books Project/Books.xsd"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Books Project/BookDatatypes.xsd"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Books Project/BooksWebService_VDB.vdb"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Parts Project/PartsSupplier_SQLServer.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Parts Project/PartSupplier_Oracle.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Parts Project/PartsSupplier_VDB.vdb"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/Northwind/Northwind.xmi")}; //$NON-NLS-1$

    /**
     * Constructor for TestVdbFileWriter.
     * 
     * @param name
     */
    public TestVdbFileWriter( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestVdbFileWriter"); //$NON-NLS-1$
        suite.addTestSuite(TestVdbFileWriter.class);

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

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    public static void oneTimeSetUp() {
        if (!VDB_WORKING_FOLDER.exists()) {
            VDB_WORKING_FOLDER.mkdir();
        }
    }

    public static void oneTimeTearDown() {
        FileUtils.removeDirectoryAndChildren(VDB_WORKING_FOLDER);
        System.gc();
        Thread.yield();
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
        File f = NEW_VDB_FILE_PATH.toFile();
        if (f.exists()) {
            f.delete();
        }
        FileUtils.removeChildrenRecursively(VDB_WORKING_FOLDER);
    }

    public File helpFindTestModelByName( final String name ) {
        for (int i = 0; i != TEST_MODELS.length; ++i) {
            File f = TEST_MODELS[i];
            if (f.getName().equalsIgnoreCase(name)) {
                return f;
            }
        }
        return null;
    }

    public String helpGetStringContent( final File f ) throws Exception {
        assertNotNull(f);
        String s = null;
        if (f.getName().endsWith("xmi") || f.getName().endsWith("xsd")) { //$NON-NLS-1$ //$NON-NLS-2$
            s = ObjectConverterUtil.convertFileToString(f);
        }
        return s;
    }

    public void testCreateWriterWithNullPath() throws Exception {
        try {
            new VdbFileWriter(null, VdbFileWriter.FORM_ZIP);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testCreateWriterWithBadForm() throws Exception {
        try {
            new VdbFileWriter(NEW_VDB_FILE_PATH, 0);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testCreateWriterWithBadBufferSize() throws Exception {
        try {
            new VdbFileWriter(NEW_VDB_FILE_PATH, 0, -1028);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testCreateWriter() throws Exception {

        new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);

    }

    public void testArgCheckForm() throws Exception {

        VdbFileWriter.argCheckForm(VdbFileWriter.FORM_JAR);
        VdbFileWriter.argCheckForm(VdbFileWriter.FORM_ZIP);

    }

    public void testGetPath() throws Exception {

        VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
        assertEquals(NEW_VDB_FILE_PATH, writer.getPath());

    }

    public void testAddEntry() throws Exception {
        File f = helpFindTestModelByName("BookDatatypes.xsd"); //$NON-NLS-1$

        VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
        writer.addEntry(new Path("/Books Project/BookDatatypes.xsd"), f); //$NON-NLS-1$
        assertEquals(1, writer.getArchiveEntryInfos().size());

    }

    public void testAddDuplicateEntry() throws Exception {
        File f = helpFindTestModelByName("BookDatatypes.xsd"); //$NON-NLS-1$

        VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
        writer.addEntry(new Path("/Books Project/BookDatatypes.xsd"), f); //$NON-NLS-1$
        writer.addEntry(new Path("Books Project/BookDatatypes.xsd"), f); //$NON-NLS-1$
        writer.addEntry(new Path("/Books Project/BookDatatypes.xsd"), f); //$NON-NLS-1$
        assertEquals(1, writer.getArchiveEntryInfos().size());

        writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
        writer.addEntry(new Path(f.getAbsolutePath()), f);
        writer.addEntry(new Path(f.getAbsolutePath()), f);
        assertEquals(1, writer.getArchiveEntryInfos().size());

    }

    public void testCreateArchiveEntryWithJarForm() throws Exception {

        VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
        ZipEntry entry = writer.createArchiveEntry("name"); //$NON-NLS-1$
        assertEquals(JarEntry.class, entry.getClass());

    }

    public void testCreateArchiveEntryWithZipForm() throws Exception {

        VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_ZIP);
        ZipEntry entry = writer.createArchiveEntry("name"); //$NON-NLS-1$
        assertEquals(ZipEntry.class, entry.getClass());

    }

    public void testWriteWithoutOpening() throws Exception {
        File f = helpFindTestModelByName("BookDatatypes.xsd"); //$NON-NLS-1$
        try {
            VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
            writer.addEntry(new Path("/Books Project/BookDatatypes.xsd"), f); //$NON-NLS-1$
            writer.write(null);
        } catch (IllegalStateException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testWriteWithoutClosing() throws Exception {
        File f = helpFindTestModelByName("BookDatatypes.xsd"); //$NON-NLS-1$
        try {
            VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
            writer.addEntry(new Path("/Books Project/BookDatatypes.xsd"), f); //$NON-NLS-1$
            writer.open();
            IStatus status = writer.write(null);
            assertTrue(status.isOK());
            assertTrue(NEW_VDB_FILE_PATH.toFile().exists());
            new ZipFile(NEW_VDB_FILE_PATH.toFile()); // will throw exception
        } catch (ZipException expected) {
            return;

        }
    }

    public void testWrite() throws Exception {
        File f = helpFindTestModelByName("BookDatatypes.xsd"); //$NON-NLS-1$

        VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
        writer.addEntry(new Path("/Books Project/BookDatatypes.xsd"), f); //$NON-NLS-1$
        writer.open();
        IStatus status = writer.write(null);
        writer.close();
        assertTrue(status.isOK());
        assertTrue(NEW_VDB_FILE_PATH.toFile().exists());
        ZipFile archive = new ZipFile(NEW_VDB_FILE_PATH.toFile());
        ZipEntry zipEntry = archive.getEntry("/Books Project/BookDatatypes.xsd"); //$NON-NLS-1$
        assertNotNull(zipEntry);
        String expected = helpGetStringContent(f);
        String actual = ObjectConverterUtil.convertToString(archive.getInputStream(zipEntry));
        assertEquals(expected, actual);
        archive.close();

    }

    public void testOverWriteExisting() throws Exception {
        File f1 = helpFindTestModelByName("BookDatatypes.xsd"); //$NON-NLS-1$
        File f2 = helpFindTestModelByName("Books_Oracle.xmi"); //$NON-NLS-1$
        File f3 = helpFindTestModelByName("Books_SQLServer.xmi"); //$NON-NLS-1$

        VdbFileWriter writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
        writer.addEntry(new Path("/Books Project/BookDatatypes.xsd"), f1); //$NON-NLS-1$
        writer.open();
        IStatus status = writer.write(null);
        writer.close();
        assertTrue(status.isOK());
        assertTrue(NEW_VDB_FILE_PATH.toFile().exists());
        ZipFile archive = new ZipFile(NEW_VDB_FILE_PATH.toFile());
        assertNotNull(archive.getEntry("/Books Project/BookDatatypes.xsd")); //$NON-NLS-1$
        archive.close();

        writer = new VdbFileWriter(NEW_VDB_FILE_PATH, VdbFileWriter.FORM_JAR);
        writer.addEntry(new Path("/Books Project/Books_Oracle.xmi"), f2); //$NON-NLS-1$
        writer.addEntry(new Path("/Books Project/Books_SQLServer.xmi"), f3); //$NON-NLS-1$
        writer.open();
        status = writer.write(null);
        writer.close();
        assertTrue(status.isOK());
        assertTrue(NEW_VDB_FILE_PATH.toFile().exists());
        archive = new ZipFile(NEW_VDB_FILE_PATH.toFile());
        assertNotNull(archive.getEntry("/Books Project/Books_Oracle.xmi")); //$NON-NLS-1$
        assertNotNull(archive.getEntry("/Books Project/Books_SQLServer.xmi")); //$NON-NLS-1$

        String expected = helpGetStringContent(f2);
        String actual = ObjectConverterUtil.convertToString(archive.getInputStream(archive.getEntry("/Books Project/Books_Oracle.xmi"))); //$NON-NLS-1$
        assertEquals(expected, actual);
        expected = helpGetStringContent(f3);
        actual = ObjectConverterUtil.convertToString(archive.getInputStream(archive.getEntry("/Books Project/Books_SQLServer.xmi"))); //$NON-NLS-1$
        assertEquals(expected, actual);

    }
}
