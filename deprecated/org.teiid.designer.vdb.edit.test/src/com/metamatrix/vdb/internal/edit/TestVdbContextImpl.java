/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.ZipFile;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.xsd.XSDPackage;
import org.jdom.Document;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.ObjectConverterUtil;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.util.TempDirectory;
import com.metamatrix.internal.core.xml.JdomHelper;
import com.metamatrix.internal.core.xml.vdb.VdbHeader;
import com.metamatrix.metamodels.core.impl.ModelAnnotationImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.vdb.edit.VdbContext;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.Severity;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl;

/**
 * @since 5.0
 */
public class TestVdbContextImpl extends TestCase {

    private static final String NEW_VDB_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + "newVdb.vdb"; //$NON-NLS-1$
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
        SmartTestSuite.getTestDataFile("projects/Northwind/Northwind.xmi"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/BQT/BQT_SQLServer_Output.xsd"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("projects/BQT/TestBQT.vdb"), //$NON-NLS-1$
        SmartTestSuite.getTestDataFile("BooksWebService_VDB_040303.vdb")}; //$NON-NLS-1$

    /**
     * Constructor for TestVdbContextImpl.
     * 
     * @param name
     */
    public TestVdbContextImpl( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestVdbContextImpl"); //$NON-NLS-1$
        //        suite.addTest(new TestVdbContextImpl("testOpenWithBooksWebServiceVdb_040303")); //$NON-NLS-1$
        suite.addTestSuite(TestVdbContextImpl.class);

        return new TestSetup(suite) { // junit.extensions package

            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                ModelerCore.testLoadModelContainer();
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
        File f = new File(NEW_VDB_FILE_PATH);
        if (f.exists()) {
            f.delete();
        }
        FileUtils.removeChildrenRecursively(VDB_WORKING_FOLDER);
    }

    public VdbContextImpl helpCreateContext( final String pathToVdb ) {
        VdbContextImpl context = new VdbContextImpl(new File(pathToVdb), VDB_WORKING_FOLDER);
        return context;
    }

    public VdbContextImpl helpCreateContext( final File f ) {
        VdbContextImpl context = new VdbContextImpl(f, VDB_WORKING_FOLDER);
        return context;
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

    public boolean helpAssertFileInArray( final File[] files,
                                          String fileName ) {
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public File helpGetFileInArray( final File[] files,
                                    String fileName ) {
        for (File file : files) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }

        return null;
    }

    public void testCreateContextWithNullFile() throws Exception {
        try {
            new VdbContextImpl(null, VDB_WORKING_FOLDER);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testCreateWithNonExistentFile() throws Exception {

        new VdbContextImpl(new File(NEW_VDB_FILE_PATH), VDB_WORKING_FOLDER);

    }

    public void testCreateWithNullWorkingFolder() throws Exception {
        try {
            new VdbContextImpl(new File(NEW_VDB_FILE_PATH), null);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testCreateWithNonExistentWorkingFolder() throws Exception {
        try {
            new VdbContextImpl(new File(NEW_VDB_FILE_PATH), new File(SmartTestSuite.getTestDataPath() + File.pathSeparator
                                                                     + "nonExistentFolder")); //$NON-NLS-1$
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testCreateContext() throws Exception {

        helpCreateContext(NEW_VDB_FILE_PATH);

    }

    public void testFileMkDir() throws Exception {
        FileUtils.removeChildrenRecursively(VDB_WORKING_FOLDER);
        try {
            File vdbWorkingFolder = VDB_WORKING_FOLDER;
            if (!vdbWorkingFolder.exists()) {
                vdbWorkingFolder.mkdirs();
            }
            // Create 20 more temp directories
            int size = 20;
            for (int i = 0; i < size; i++) {
                String tempDirName = "folder_" + i; //$NON-NLS-1$
                File f = new File(VDB_WORKING_FOLDER, tempDirName);
                f.mkdir();
            }
            assertEquals(size, VDB_WORKING_FOLDER.listFiles().length);

        } finally {
            FileUtils.removeChildrenRecursively(VDB_WORKING_FOLDER);
        }
    }

    public void testCreateTempDirectory() throws Exception {
        FileUtils.removeChildrenRecursively(VDB_WORKING_FOLDER);
        try {
            // Instantiating the context will create one temp directory
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            // Create 20 more temp directories
            int size = 20;
            for (int i = 0; i < size; i++) {
                context.createTempDirectory(VDB_WORKING_FOLDER);
            }
            assertEquals(size + 1, VDB_WORKING_FOLDER.listFiles().length);

        } finally {
            FileUtils.removeChildrenRecursively(VDB_WORKING_FOLDER);
        }
    }

    public void testGetSeverityWhenClosed() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        assertEquals(Severity.WARNING_LITERAL, context.getSeverity());

    }

    public void testGetSeverityWhenOpen() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);
            assertEquals(Severity.WARNING_LITERAL, context.getSeverity());

        } finally {
            context.dispose();
        }
    }

    public void testPeekAtContentsWithNonExistentFile() throws Exception {

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
        VdbHeader header = context.peekAtContents();
        assertNull(header);

    }

    public void testPeekAtContentsWithNonVdbFile() throws Exception {
        File f = helpFindTestModelByName("Books.xsd"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        VdbHeader header = context.peekAtContents();
        assertNull(header);

    }

    public void testPeekAtContents() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        VdbHeader header = context.peekAtContents();
        assertNotNull(header);
        assertEquals(false, context.isOpen());
        assertEquals("This virtual database (VDB) definition file contains the models required to deploy the BooksXML Web Service project.", header.getDescription());//$NON-NLS-1$
        assertEquals("BooksWebService_VDB", header.getName());//$NON-NLS-1$
        assertEquals("WARNING", header.getSeverity());//$NON-NLS-1$
        assertEquals("mmuuid:6d179c02-f639-1f59-a112-e0eac778cc3c", header.getUUID());//$NON-NLS-1$

        assertEquals(7, header.getModelInfos().length);
        assertEquals("/Books Project/BookDatatypes.xsd", header.getModelInfos()[0].getLocation());//$NON-NLS-1$
        assertEquals("TYPE", header.getModelInfos()[0].getModelType());//$NON-NLS-1$
        assertEquals("BookDatatypes.xsd", header.getModelInfos()[0].getName());//$NON-NLS-1$
        assertEquals(XSDPackage.eNS_URI, header.getModelInfos()[0].getPrimaryMetamodelURI());
        assertEquals(null, header.getModelInfos()[0].getUUID());

        assertEquals("/Books Project/BooksXML.xmi", header.getModelInfos()[4].getLocation());//$NON-NLS-1$
        assertEquals("VIRTUAL", header.getModelInfos()[4].getModelType());//$NON-NLS-1$
        assertEquals("BooksXML.xmi", header.getModelInfos()[4].getName());//$NON-NLS-1$
        assertEquals("http://www.metamatrix.com/metamodels/XmlDocument", header.getModelInfos()[4].getPrimaryMetamodelURI());//$NON-NLS-1$
        assertEquals("mmuuid:f96d8dc0-0dc9-1eec-8518-c32201e76066", header.getModelInfos()[4].getUUID());//$NON-NLS-1$

        assertEquals("/Books Project/Books_SourceB.xmi", header.getModelInfos()[6].getLocation());//$NON-NLS-1$
        assertEquals("PHYSICAL", header.getModelInfos()[6].getModelType());//$NON-NLS-1$
        assertEquals("Books_SourceB.xmi", header.getModelInfos()[6].getName());//$NON-NLS-1$
        assertEquals("http://www.metamatrix.com/metamodels/Relational", header.getModelInfos()[6].getPrimaryMetamodelURI());//$NON-NLS-1$
        assertEquals("mmuuid:5bad5100-0db6-1eec-8518-c32201e76066", header.getModelInfos()[6].getUUID());//$NON-NLS-1$

        assertEquals(2, header.getNonModelInfos().length);
        assertEquals("BooksWebService_VDB.DEF", header.getNonModelInfos()[0].getName());//$NON-NLS-1$
        assertEquals("BooksWebService_VDB.DEF", header.getNonModelInfos()[0].getPath());//$NON-NLS-1$

        assertEquals("ConfigurationInfo.def", header.getNonModelInfos()[1].getName());//$NON-NLS-1$
        assertEquals("ConfigurationInfo.def", header.getNonModelInfos()[1].getPath());//$NON-NLS-1$

    }

    public void testIsEmptyWithNonExistent() throws Exception {

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
        assertEquals(true, context.isEmpty());

    }

    public void testIsEmptyWithEmptyFile() throws Exception {

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
        File f = new File(NEW_VDB_FILE_PATH);
        f.createNewFile();
        assertEquals(true, f.exists());
        assertEquals(true, context.isEmpty());

    }

    public void testIsEmptyWithNonEmptyFile() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        assertEquals(true, f.exists());
        assertEquals(false, context.isEmpty());

    }

    public void testIsOpen() throws Exception {

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
        context.open(null);
        assertEquals(true, context.isOpen());

    }

    public void testIsOpenWhenClosed() throws Exception {

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
        assertEquals(false, context.isOpen());

    }

    public void testOpenWithNewVdb() throws Exception {
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(NEW_VDB_FILE_PATH);
            context.open(null);
            assertEquals(true, context.isOpen());
            assertNotNull(context.getTempDirectory());

        } finally {
            context.dispose();
        }
    }

    public void testOpenWithBooksWebServiceVdb() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            File tempFolder = new File(context.getTempDirectory().getPath());
            File[] files = tempFolder.listFiles();
            assertEquals(3, files.length);

            assertTrue(helpAssertFileInArray(files, "Books Project")); //$NON-NLS-1$
            assertTrue(helpAssertFileInArray(files, "BooksWebService_VDB.DEF")); //$NON-NLS-1$
            assertTrue(helpAssertFileInArray(files, "ConfigurationInfo.def")); //$NON-NLS-1$

            assertTrue(helpGetFileInArray(files, "BooksWebService_VDB.DEF").length() > 0); //$NON-NLS-1$
            assertTrue(helpGetFileInArray(files, "ConfigurationInfo.def").length() > 0); //$NON-NLS-1$
            files = helpGetFileInArray(files, "Books Project").listFiles(); //$NON-NLS-1$
            assertEquals(7, files.length);
            assertTrue(helpAssertFileInArray(files, "Books.xsd")); //$NON-NLS-1$
            assertTrue(helpGetStringContent(helpGetFileInArray(files, "Books.xsd")).indexOf("schemaLocation=\"BookDatatypes.xsd") > 0); //$NON-NLS-1$ //$NON-NLS-2$ 

        } finally {
            context.dispose();
        }
    }

    public void testOpenWithBooksWebServiceVdb_040303() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB_040303.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            File tempFolder = new File(context.getTempDirectory().getPath());
            File[] files = tempFolder.listFiles();
            assertEquals(2, files.length);
            assertTrue(helpAssertFileInArray(files, "Books Project")); //$NON-NLS-1$
            assertTrue(helpAssertFileInArray(files, "BooksWebService_VDB.DEF")); //$NON-NLS-1$
            files = helpGetFileInArray(files, "Books Project").listFiles(); //$NON-NLS-1$
            assertEquals(7, files.length);
            assertTrue(helpAssertFileInArray(files, "Books.xsd")); //$NON-NLS-1$
            assertTrue(helpGetStringContent(helpGetFileInArray(files, "Books.xsd")).indexOf("schemaLocation=\"BookDatatypes.xsd") > 0); //$NON-NLS-1$ //$NON-NLS-2$

        } finally {
            context.dispose();
        }
    }

    public void testGetModelReferenceByPathWhenClosed() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            context.getModelReference("/fakePath"); //$NON-NLS-1$
        } catch (IllegalStateException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetModelReferenceByUuidWhenClosed() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            context.getModelReference(IDGenerator.getInstance().getFactory(UUID.PROTOCOL).create());
        } catch (IllegalStateException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetNonModelReferenceByPathWhenClosed() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            context.getNonModelReference("/fakePath"); //$NON-NLS-1$
        } catch (IllegalStateException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetModelReferenceByPathWithNull() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            String pathInArchive = null;
            context.getModelReference(pathInArchive);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetModelReferenceByUuidWithNull() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            UUID uuid = null;
            context.getModelReference(uuid);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetModelReferenceByFileWithNull() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            File file = null;
            context.getModelReference(file);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetNonModelReferenceByPathWithNull() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            String pathInArchive = null;
            context.getNonModelReference(pathInArchive);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetNonModelReferenceByFileWithNull() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            File file = null;
            context.getNonModelReference(file);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetModelReferenceByPath() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            assertNotNull(context.getModelReference("/Books Project/Books_SourceA.xmi")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("/Books Project/Books_SourceB.xmi")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("/Books Project/BooksWebService.xmi")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("/Books Project/BooksXML.xmi")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("/Books Project/Books.xsd")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("/Books Project/BookDatatypes.xsd")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("/Books Project/BooksInput.xsd")); //$NON-NLS-1$

            assertNotNull(context.getModelReference("/BOOKS PROJECT/BOOKSXML.XMI")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("/books project/booksxml.xmi")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("BOOKS PROJECT/BOOKSXML.XMI")); //$NON-NLS-1$
            assertNotNull(context.getModelReference("books project/booksxml.xmi")); //$NON-NLS-1$

        } finally {
            context.dispose();
        }
    }

    public void testGetModelReferenceByUuid() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            assertNotNull(context.getModelReference(IDGenerator.getInstance().stringToObject("mmuuid:98384100-0dae-1eec-8518-c32201e76066", UUID.PROTOCOL))); //$NON-NLS-1$
            assertNotNull(context.getModelReference(IDGenerator.getInstance().stringToObject("mmuuid:5bad5100-0db6-1eec-8518-c32201e76066", UUID.PROTOCOL))); //$NON-NLS-1$
            assertNotNull(context.getModelReference(IDGenerator.getInstance().stringToObject("mmuuid:f96d8dc0-0dc9-1eec-8518-c32201e76066", UUID.PROTOCOL))); //$NON-NLS-1$

        } finally {
            context.dispose();
        }
    }

    public void testGetModelReferenceByFile() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            File tempDirFolder = new File(context.getTempDirectory().getPath());
            assertNotNull(context.getModelReference(new File(tempDirFolder, "/Books Project/Books_SourceB.xmi"))); //$NON-NLS-1$
            assertNotNull(context.getModelReference(new File(tempDirFolder, "/Books Project/Books_SourceB.xmi"))); //$NON-NLS-1$
            assertNotNull(context.getModelReference(new File(tempDirFolder, "/Books Project/BooksWebService.xmi"))); //$NON-NLS-1$
            assertNotNull(context.getModelReference(new File(tempDirFolder, "/Books Project/BooksXML.xmi"))); //$NON-NLS-1$
            assertNotNull(context.getModelReference(new File(tempDirFolder, "/Books Project/Books.xsd"))); //$NON-NLS-1$
            assertNotNull(context.getModelReference(new File(tempDirFolder, "/Books Project/BookDatatypes.xsd"))); //$NON-NLS-1$
            assertNotNull(context.getModelReference(new File(tempDirFolder, "/Books Project/BooksInput.xsd"))); //$NON-NLS-1$

        } finally {
            context.dispose();
        }
    }

    public void testGetNonModelReferenceByPath() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            assertNotNull(context.getNonModelReference("ConfigurationInfo.def")); //$NON-NLS-1$
            assertNotNull(context.getNonModelReference("CONFIGURATIONINFO.DEF")); //$NON-NLS-1$
            assertNotNull(context.getNonModelReference("configurationinfo.def")); //$NON-NLS-1$
            assertNotNull(context.getNonModelReference("/CONFIGURATIONINFO.DEF")); //$NON-NLS-1$
            assertNotNull(context.getNonModelReference("/configurationinfo.def")); //$NON-NLS-1$

        } finally {
            context.dispose();
        }
    }

    public void testGetNonModelReferenceByFile() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            File tempDirFolder = new File(context.getTempDirectory().getPath());
            assertNotNull(context.getNonModelReference(new File(tempDirFolder, "ConfigurationInfo.def"))); //$NON-NLS-1$

        } finally {
            context.dispose();
        }
    }

    public void testCloseWhenNotOpened() throws Exception {

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
        assertEquals(false, context.isOpen());
        context.close(null);
        assertEquals(false, context.isOpen());
        assertNotNull(context.getTempDirectory());

    }

    public void testCloseWithNonExistentVdb() throws Exception {
        File f = new File(NEW_VDB_FILE_PATH);

        VdbContextImpl context = helpCreateContext(f);
        context.open(null);
        assertEquals(true, context.isOpen());
        File tempDir = new File(context.getTempDirectory().getPath());
        context.close(null);
        assertEquals(false, context.isOpen());
        assertEquals(false, f.exists());
        assertEquals(true, tempDir.exists());
        assertNotNull(context.getTempDirectory());
        assertEquals(0, tempDir.listFiles().length);

    }

    public void testCloseWithEmptyVdb() throws Exception {
        File f = new File(NEW_VDB_FILE_PATH);

        f.createNewFile();
        assertEquals(true, f.exists());
        assertEquals(0, f.length());

        VdbContextImpl context = helpCreateContext(f);
        context.open(null);
        assertEquals(true, context.isOpen());
        File tempDir = new File(context.getTempDirectory().getPath());
        context.close(null);
        assertEquals(false, context.isOpen());
        assertEquals(true, f.exists());
        assertEquals(0, f.length());
        assertEquals(true, tempDir.exists());
        assertNotNull(context.getTempDirectory());
        assertEquals(0, tempDir.listFiles().length);

    }

    public void testCloseBooksWebServiceVdb() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        context.open(null);
        assertEquals(true, context.isOpen());
        File tempDir = new File(context.getTempDirectory().getPath());
        assertEquals(3, tempDir.listFiles().length);

        context.close(null);
        assertEquals(false, context.isOpen());
        assertEquals(true, f.exists());
        assertEquals(true, tempDir.exists());
        assertNotNull(context.getTempDirectory());
        assertEquals(0, tempDir.listFiles().length);

    }

    public void testCloseWithVeto() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        VetoableChangeListener listener = new TestVetoableChangeListener();
        context.addVetoableChangeListener(listener);
        context.open(null);
        assertEquals(true, context.isOpen());
        File tempDir = new File(context.getTempDirectory().getPath());
        assertEquals(3, tempDir.listFiles().length);

        context.close(null);
        assertEquals(true, context.isOpen());
        assertEquals(true, f.exists());
        assertEquals(true, tempDir.exists());

        // Remove VetoableChangeListener - should be able to close now
        context.removeVetoableChangeListener(listener);
        context.close(null);
        assertEquals(false, context.isOpen());
        assertEquals(true, f.exists());
        assertEquals(true, tempDir.exists());
        assertNotNull(context.getTempDirectory());
        assertEquals(0, tempDir.listFiles().length);

    }

    public void testCloseCalledMultipleTimes() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        context.open(null);
        assertEquals(true, context.isOpen());
        File tempDir = new File(context.getTempDirectory().getPath());
        assertEquals(3, tempDir.listFiles().length);

        context.close(null);
        context.close(null);
        context.close(null);
        assertEquals(false, context.isOpen());
        assertEquals(true, f.exists());
        assertEquals(true, tempDir.exists());
        assertNotNull(context.getTempDirectory());
        assertEquals(0, tempDir.listFiles().length);

    }

    public void testDispose() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        File tempDir = new File(context.getTempDirectory().getPath());
        assertEquals(true, f.exists());
        assertEquals(true, tempDir.exists());
        context.dispose();
        assertEquals(true, f.exists());
        assertEquals(false, tempDir.exists());
        assertNull(context.getTempDirectory());

    }

    public void testDisposeCalledMultipleTimes() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        File tempDir = new File(context.getTempDirectory().getPath());
        assertEquals(true, f.exists());
        assertEquals(true, tempDir.exists());
        context.dispose();
        context.dispose();
        context.dispose();
        assertEquals(true, f.exists());
        assertEquals(false, tempDir.exists());
        assertNull(context.getTempDirectory());

    }

    public void testGetVirtualDatabaseContextClosed() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            context.getVirtualDatabase();
        } catch (IllegalStateException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testGetVirtualDatabase() throws Exception {
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(NEW_VDB_FILE_PATH);
            context.open(null);
            VirtualDatabase vdb = context.getVirtualDatabase();
            assertNotNull(vdb);
            assertEquals(2, vdb.eResource().getContents().size());
            assertEquals(ModelAnnotationImpl.class, vdb.eResource().getContents().get(0).getClass());
            assertEquals(VirtualDatabaseImpl.class, vdb.eResource().getContents().get(1).getClass());

        } finally {
            context.dispose();
        }
    }

    public void testBuildDocument() throws Exception {
        File f = helpFindTestModelByName("Books.xsd"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);

        Document doc = context.buildDocument(new FileInputStream(f));
        assertNotNull(doc);
        assertNotNull(doc.getRootElement());

    }

    public void testBuildDocumentWithNullInputStream() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);

            Document doc = context.buildDocument(null);
            assertNotNull(doc);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testEncodeXsdSchemaDirectivePathsForBooksXsd() throws Exception {
        File f = helpFindTestModelByName("Books.xsd"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);

        Document doc = context.buildDocument(new FileInputStream(f));
        context.encodeXsdSchemaDirectivePaths(doc, "/Books Project/Books.xsd", new File(SmartTestSuite.getTestDataPath())); //$NON-NLS-1$

        String result = JdomHelper.write(doc);
        assertTrue(result.indexOf("schemaLocation=\"http://vdb.metamatrix.com/Books%20Project/BookDatatypes.xsd?vdbToken=true") > 0); //$NON-NLS-1$

    }

    public void testEncodeXsdSchemaDirectivePathsForBqtOutputXsd() throws Exception {
        File f = helpFindTestModelByName("BQT_SQLServer_Output.xsd"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);

        Document doc = context.buildDocument(new FileInputStream(f));
        context.encodeXsdSchemaDirectivePaths(doc, "/BQT/BQT_SQLServer_Output.xsd", new File(SmartTestSuite.getTestDataPath())); //$NON-NLS-1$

        String result = JdomHelper.write(doc);
        assertTrue(result.indexOf("schemaLocation=\"http://vdb.metamatrix.com/builtInDataTypes.xsd?vdbToken=true") > 0); //$NON-NLS-1$

    }

    public void testDecodeXsdSchemaDirectivePathsForBooksXsd() throws Exception {
        File f = helpFindTestModelByName("Books.xsd"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);

        Document doc = context.buildDocument(new FileInputStream(f));
        context.decodeXsdSchemaDirectivePaths(doc, "/Books Project/Books.xsd", new File(SmartTestSuite.getTestDataPath())); //$NON-NLS-1$
        String result = JdomHelper.write(doc);
        assertTrue(result.indexOf("schemaLocation=\"BookDatatypes.xsd") > 0); //$NON-NLS-1$

        context.encodeXsdSchemaDirectivePaths(doc, "/Books Project/Books.xsd", new File(SmartTestSuite.getTestDataPath())); //$NON-NLS-1$
        context.decodeXsdSchemaDirectivePaths(doc, "/Books Project/Books.xsd", new File(SmartTestSuite.getTestDataPath())); //$NON-NLS-1$
        result = JdomHelper.write(doc);
        assertTrue(result.indexOf("schemaLocation=\"BookDatatypes.xsd") > 0); //$NON-NLS-1$

    }

    public void testDecodeXsdSchemaDirectivePathsForBqtOutputXsd() throws Exception {
        File f = helpFindTestModelByName("BQT_SQLServer_Output.xsd"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);

        Document doc = context.buildDocument(new FileInputStream(f));
        context.decodeXsdSchemaDirectivePaths(doc, "/BQT/BQT_SQLServer_Output.xsd", new File(SmartTestSuite.getTestDataPath())); //$NON-NLS-1$
        String result = JdomHelper.write(doc);
        assertTrue(result.indexOf("schemaLocation=\"http://www.metamatrix.com/metamodels/SimpleDatatypes-instance") > 0); //$NON-NLS-1$

        context.encodeXsdSchemaDirectivePaths(doc, "/BQT/BQT_SQLServer_Output.xsd", new File(SmartTestSuite.getTestDataPath())); //$NON-NLS-1$
        context.decodeXsdSchemaDirectivePaths(doc, "/BQT/BQT_SQLServer_Output.xsd", new File(SmartTestSuite.getTestDataPath())); //$NON-NLS-1$
        result = JdomHelper.write(doc);
        assertTrue(result.indexOf("schemaLocation=\"http://www.metamatrix.com/metamodels/SimpleDatatypes-instance") > 0); //$NON-NLS-1$

    }

    public void testCopyTempDirectoryEntryForSaveWithBooksWebServiceVdb() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        TempDirectory targetDir = null;
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);
            assertEquals(true, context.isOpen());
            targetDir = context.createTempDirectory(VDB_WORKING_FOLDER);

            File targetFile = context.copyTempDirectoryEntryForSave("/Books Project/Books.xsd", context.getTempDirectory(), targetDir); //$NON-NLS-1$
            assertEquals(true, targetFile.exists());
            String s = helpGetStringContent(targetFile);
            assertTrue(s.indexOf("schemaLocation=\"http://vdb.metamatrix.com/Books%20Project/BookDatatypes.xsd?vdbToken=true") > 0); //$NON-NLS-1$

            targetFile = context.copyTempDirectoryEntryForSave("/Books Project/BooksWebService.xmi", context.getTempDirectory(), targetDir); //$NON-NLS-1$
            assertEquals(true, targetFile.exists());
            s = helpGetStringContent(targetFile);
            assertTrue(s.indexOf("modelLocation=\"BooksInput.xsd") > 0); //$NON-NLS-1$
            assertTrue(s.indexOf("modelLocation=\"BooksXML.xmi") > 0); //$NON-NLS-1$

        } finally {
            if (targetDir != null) {
                targetDir.remove();
            }
            context.dispose();
        }
    }

    public void testCreateNormalizedPathWithNull() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            context.createNormalizedPath(null);
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testCreateNormalizedPathWithEmptyString() throws Exception {
        try {
            VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
            context.createNormalizedPath(""); //$NON-NLS-1$
        } catch (IllegalArgumentException expected) {
            return;

        }
        fail("Expected failure but got success"); //$NON-NLS-1$
    }

    public void testCreateNormalizedPath() throws Exception {

        VdbContextImpl context = helpCreateContext(NEW_VDB_FILE_PATH);
        assertEquals("abc", context.createNormalizedPath("abc").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("abc", context.createNormalizedPath("/abc").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("abc", context.createNormalizedPath("/abc/").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("/abc/def", context.createNormalizedPath("abc/def").toString()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("/abc/def", context.createNormalizedPath("/abc/def").toString()); //$NON-NLS-1$ //$NON-NLS-2$

    }

    public void testGetInputStreamForModelReference() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            ModelReference ref = context.getModelReference("/Books Project/Books_SourceA.xmi"); //$NON-NLS-1$
            assertNotNull(ref);
            assertNotNull(context.getInputStream(ref));

        } finally {
            context.dispose();
        }
    }

    public void testGetInputStreamForNonModelReference() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);

            assertEquals(true, context.isOpen());
            NonModelReference ref = context.getNonModelReference("ConfigurationInfo.def"); //$NON-NLS-1$
            assertNotNull(ref);
            assertNotNull(context.getInputStream(ref));

        } finally {
            context.dispose();
        }
    }

    public void testWriteArchiveEntryToTempDirectoryWithBooksWebServiceVdb() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$

        VdbContextImpl context = helpCreateContext(f);
        ZipFile vdbArchive = new ZipFile(context.getVdbFile());

        File file = context.writeArchiveEntryToTempDirectory("/Books Project/Books.xsd", vdbArchive, context.getTempDirectory()); //$NON-NLS-1$
        Document doc = context.buildDocument(new FileInputStream(file));
        String result = JdomHelper.write(doc);
        assertTrue(result.indexOf("schemaLocation=\"BookDatatypes.xsd") > 0); //$NON-NLS-1$

        file = context.writeArchiveEntryToTempDirectory("/Books Project/BooksWebService.xmi", vdbArchive, context.getTempDirectory()); //$NON-NLS-1$
        doc = context.buildDocument(new FileInputStream(file));
        result = JdomHelper.write(doc);
        assertTrue(result.indexOf("modelLocation=\"BooksInput.xsd") > 0); //$NON-NLS-1$
        assertTrue(result.indexOf("modelLocation=\"BooksXML.xmi") > 0); //$NON-NLS-1$

    }

    public void testGetDescriptionWhenCosed() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            assertEquals("This virtual database (VDB) definition file contains the models required to deploy the BooksXML Web Service project.", context.getDescription()); //$NON-NLS-1$

        } finally {
            context.dispose();
        }
    }

    public void testGetDescription() throws Exception {
        File f = helpFindTestModelByName("BooksWebService_VDB.vdb"); //$NON-NLS-1$
        VdbContextImpl context = null;
        try {
            context = helpCreateContext(f);
            context.open(null);
            assertEquals("This virtual database (VDB) definition file contains the models required to deploy the BooksXML Web Service project.", context.getDescription()); //$NON-NLS-1$

        } finally {
            context.dispose();
        }
    }

    class TestVetoableChangeListener implements VetoableChangeListener {
        public void vetoableChange( PropertyChangeEvent evt ) throws PropertyVetoException {
            if (VdbContext.CLOSING_EVENT_NAME.equals(evt.getPropertyName())) {
                throw new PropertyVetoException("VDB in use", evt); //$NON-NLS-1$
            }
        }
    }
}
