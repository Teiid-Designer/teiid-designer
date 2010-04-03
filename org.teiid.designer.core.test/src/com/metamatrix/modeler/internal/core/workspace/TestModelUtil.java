/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.common.vdb.VdbHeader;
import com.metamatrix.common.vdb.VdbModelInfo;
import com.metamatrix.core.util.SmartTestSuite;

/**
 * TestModelUtil
 */
public class TestModelUtil extends TestCase {

    /**
     * Constructor for TestModelUtil.
     * 
     * @param name
     */
    public TestModelUtil( String name ) {
        super(name);
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestModelUtil"); //$NON-NLS-1$
        suite.addTestSuite(TestModelUtil.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
                // do nothing
            }

            @Override
            public void tearDown() {
                // do nothing
            }
        };
    }

    public void testZeroLengthPackageName() {
        assertTrue(false == ModelUtil.isValidFolderNameForPackage("")); //$NON-NLS-1$
    }

    public void testNullPackageName() {
        assertTrue(false == ModelUtil.isValidFolderNameForPackage(null));
    }

    public void testValidPackageName() {
        assertTrue(true == ModelUtil.isValidFolderNameForPackage("ThisIsAValidPackage")); //$NON-NLS-1$
    }

    public void testValidPackageNameWithSpaces() {
        assertTrue(true == ModelUtil.isValidFolderNameForPackage("This Is A Valid Package")); //$NON-NLS-1$
    }

    public void testValidPackageNameWithExtension() {
        assertTrue(true == ModelUtil.isValidFolderNameForPackage("This Is A Valid Package.txt")); //$NON-NLS-1$
    }

    public void testValidPackageNameWithXMLExtension() {
        assertTrue(true == ModelUtil.isValidFolderNameForPackage("This Is A Valid Package.xml")); //$NON-NLS-1$
    }

    public void testZeroLengthModelFileName() {
        assertTrue(false == ModelUtil.isValidModelFileName("")); //$NON-NLS-1$
    }

    public void testNullModelFileName() {
        assertTrue(false == ModelUtil.isValidModelFileName(null));
    }

    public void testAlmostEmptyModelFileNameWithWrongExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName(".xyz")); //$NON-NLS-1$
    }

    public void testAlmostEmptyModelFileNameWithXMLExtension() {
        assertTrue(true == ModelUtil.isValidModelFileName(".xml")); //$NON-NLS-1$
    }

    public void tstAlmostEmptyModelFileNameWithMMMExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName(".mmm")); //$NON-NLS-1$
    }

    public void testAlmostEmptyModelFileNameWithAllCapsXMLExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName(".XML")); //$NON-NLS-1$
    }

    public void tstAlmostEmptyModelFileNameWithAllCapsMMMExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName(".MMM")); //$NON-NLS-1$
    }

    public void testModelFileNameWithWrongExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.xyz")); //$NON-NLS-1$
    }

    public void testModelFileNameWithXMLExtension() {
        assertTrue(true == ModelUtil.isValidModelFileName("SomeModel.xml")); //$NON-NLS-1$
    }

    public void tstModelFileNameWithMMMExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.mmm")); //$NON-NLS-1$
    }

    public void testModelFileNameWithAllCapsXMLExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.XML")); //$NON-NLS-1$
    }

    public void tstModelFileNameWithAllCapsMMMExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.MMM")); //$NON-NLS-1$
    }

    public void testModelFileNameWithAllCapsXMIExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.XMI")); //$NON-NLS-1$
    }

    public void testModelFileNameWithAllCapsXSDExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.XSD")); //$NON-NLS-1$
    }

    public void testModelFileNameWithMixedCaseXMIExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.xMi")); //$NON-NLS-1$
    }

    public void testModelFileNameWithMixedCaseXSDExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.xSd")); //$NON-NLS-1$
    }

    public void testModelFileNameWithVdbExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.vdb")); //$NON-NLS-1$
    }

    public void testModelFileNameWithAllCapsVDBExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.VDB")); //$NON-NLS-1$
    }

    public void testValidModelFileNameWithSpaces() {
        assertTrue(true == ModelUtil.isValidModelFileName("Model file with spaces.xml")); //$NON-NLS-1$
    }

    public void testIsXmiFileWithNonExistentFile() {
        assertTrue(false == ModelUtil.isXmiFile(new File(""))); //$NON-NLS-1$
    }

    public void testIsXmiFileWithXsdFile() {
        File f = SmartTestSuite.getTestDataFile("Books.xsd"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithXmiFile() {
        File f = SmartTestSuite.getTestDataFile("PartSupplier_Oracle.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithXmiFileAndUpperCaseExtension() {
        File f = SmartTestSuite.getTestDataFile("PartSupplier_Oracle.XMI"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithXmiFileAndMixedCaseExtension() {
        File f = SmartTestSuite.getTestDataFile("PartSupplier_Oracle.xMI"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithEmptyModelFile() {
        File f = SmartTestSuite.getTestDataFile("emptyModel.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithNonEmptyModelFile() {
        File f = SmartTestSuite.getTestDataFile("nonEmptyModel.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithNonModelFile() {
        File f = SmartTestSuite.getTestDataFile("nonModelFile.txt"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithMsWordFile() {
        File f = SmartTestSuite.getTestDataFile("sampleMSWord.doc"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithVdbManifestFile() {
        File f = SmartTestSuite.getTestDataFile("MetaMatrix-VdbManifestModel.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithVdbArchive() {
        File f = SmartTestSuite.getTestDataFile("PartSupplierVirtual.vdb"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithZipFile() {
        File f = SmartTestSuite.getTestDataFile("builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWith0200File() {
        File f = SmartTestSuite.getTestDataFile("partsSupplierOracle_v0200.xml"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWith0300File() {
        File f = SmartTestSuite.getTestDataFile("VirtualNorthwind.xml"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testGetXsdHeaderWithNonExistentFile() {
        assertTrue(null == ModelUtil.getXsdHeader(new File(""))); //$NON-NLS-1$
    }

    public void testGetXsdHeaderWithXsdFile() {
        File f = SmartTestSuite.getTestDataFile("Books.xsd"); //$NON-NLS-1$
        assertTrue(null != ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithEmptyModelFile() {
        File f = SmartTestSuite.getTestDataFile("emptyModel.xmi"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithNonEmptyModelFile() {
        File f = SmartTestSuite.getTestDataFile("nonEmptyModel.xmi"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithNonModelFile() {
        File f = SmartTestSuite.getTestDataFile("nonModelFile.txt"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithMsWordFile() {
        File f = SmartTestSuite.getTestDataFile("sampleMSWord.doc"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithVdbManifestFile() {
        File f = SmartTestSuite.getTestDataFile("MetaMatrix-VdbManifestModel.xmi"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithVdbArchive() {
        File f = SmartTestSuite.getTestDataFile("PartSupplierVirtual.vdb"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithZipFile() {
        File f = SmartTestSuite.getTestDataFile("builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetVdbHeaderForNonVdbArchive() {
        File f = SmartTestSuite.getTestDataFile("emptyModel.xmi"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getVdbHeader(f));
    }

    public void testGetVdbHeaderForNullArgument() {
        assertTrue(null == ModelUtil.getVdbHeader(null));
    }

    public void testGetVdbHeaderWithZipFile() {
        File f = SmartTestSuite.getTestDataFile("builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getVdbHeader(f));
    }

    public void testGetVdbHeaderForVdbArchive() {
        File f = SmartTestSuite.getTestDataFile("builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getVdbHeader(f));
    }

    public void testGetVdbHeaderForBooksVdbArchive() {
        String testDataPath = SmartTestSuite.getGlobalTestDataPath() + "global/baselinevdbs/books/BooksXML_VDB.vdb"; //$NON-NLS-1$
        File f = new File(testDataPath);
        assertTrue(f.exists());
        VdbHeader header = ModelUtil.getVdbHeader(f);
        assertNotNull(header);
        assertEquals(5, header.getModelInfos().length);
        assertEquals(0, header.getNonModelInfos().length);
        assertEquals("mmuuid:89582d80-0e17-1eec-8518-c32201e76066", header.getUUID()); //$NON-NLS-1$
    }

    public void testGetVdbHeaderForPartsVdbArchive() {
        String testDataPath = SmartTestSuite.getGlobalTestDataPath() + "global/baselinevdbs/partssupplier/PartsSupplier_VDB.vdb"; //$NON-NLS-1$
        File f = new File(testDataPath);
        assertTrue(f.exists());
        VdbHeader header = ModelUtil.getVdbHeader(f);
        assertNotNull(header);
        assertEquals(3, header.getModelInfos().length);
        assertEquals(0, header.getNonModelInfos().length);
        assertEquals("mmuuid:155720c0-14df-1eec-8518-c32201e76066", header.getUUID()); //$NON-NLS-1$

        VdbModelInfo info = header.getModelInfos()[0];
        assertEquals("PartSupplier_Oracle.xmi", info.getName()); //$NON-NLS-1$
        assertEquals("/Parts Project/PartSupplier_Oracle.xmi", info.getPath()); //$NON-NLS-1$
        assertEquals("mmuuid:579b2e80-1274-1eec-8518-c32201e76066", info.getUUID()); //$NON-NLS-1$
        assertEquals("PHYSICAL", info.getModelType()); //$NON-NLS-1$
        assertEquals("http://www.metamatrix.com/metamodels/Relational", info.getPrimaryMetamodelURI()); //$NON-NLS-1$

        info = header.getModelInfos()[2];
        assertEquals("PartsVirtual.xmi", info.getName()); //$NON-NLS-1$
        assertEquals("/Parts Project/PartsVirtual.xmi", info.getPath()); //$NON-NLS-1$
        assertEquals("mmuuid:fb52cb80-128a-1eec-8518-c32201e76066", info.getUUID()); //$NON-NLS-1$
        assertEquals("VIRTUAL", info.getModelType()); //$NON-NLS-1$
        assertEquals("http://www.metamatrix.com/metamodels/Relational", info.getPrimaryMetamodelURI()); //$NON-NLS-1$
    }

    public void testGetFileExtensionForFileWithNoExtension() {
        File f = SmartTestSuite.getTestDataFile("testFileWithNoExtension"); //$NON-NLS-1$
        assertEquals("", ModelUtil.getFileExtension(f)); //$NON-NLS-1$
    }

    public void testGetFileExtensionForFileWithNoExtension2() {
        File f = SmartTestSuite.getTestDataFile("testFileWithNoExtension."); //$NON-NLS-1$
        assertEquals("", ModelUtil.getFileExtension(f)); //$NON-NLS-1$
    }

    public void testGetFileExtensionForFileWithExtension() {
        File f = SmartTestSuite.getTestDataFile("testFileWithExtension.abc"); //$NON-NLS-1$
        assertEquals("abc", ModelUtil.getFileExtension(f)); //$NON-NLS-1$
    }

    public void testGetRelativePath() {
        IPath s = new Path("/a/b/c/x.xsd");//$NON-NLS-1$
        IPath b = new Path("/a/b");//$NON-NLS-1$
        assertEquals("c/x.xsd", ModelUtil.getRelativePath(s, b));//$NON-NLS-1$

        s = new Path("/a/b/c/x.xsd");//$NON-NLS-1$
        b = new Path("/a/b/y.xsd");//$NON-NLS-1$
        assertEquals("c/x.xsd", ModelUtil.getRelativePath(s, b));//$NON-NLS-1$

        s = new Path("/a/b/x.xsd");//$NON-NLS-1$
        b = new Path("/a/b/c/y.xsd");//$NON-NLS-1$
        assertEquals("../x.xsd", ModelUtil.getRelativePath(s, b));//$NON-NLS-1$

        s = new Path("/a/b/x.xsd");//$NON-NLS-1$
        b = new Path("/a/b/c");//$NON-NLS-1$
        assertEquals("../x.xsd", ModelUtil.getRelativePath(s, b));//$NON-NLS-1$

        s = new Path("/a/b/x.xsd");//$NON-NLS-1$
        b = new Path("a/b/c");//$NON-NLS-1$
        assertEquals("../x.xsd", ModelUtil.getRelativePath(s, b));//$NON-NLS-1$

        s = new Path("/a/b/x.xsd");//$NON-NLS-1$
        b = new Path("/b/c");//$NON-NLS-1$
        assertEquals("../../a/b/x.xsd", ModelUtil.getRelativePath(s, b));//$NON-NLS-1$

        s = new Path("a/b/c/x.xsd");//$NON-NLS-1$
        b = new Path("a/b/y.xsd");//$NON-NLS-1$
        assertEquals("c/x.xsd", ModelUtil.getRelativePath(s, b));//$NON-NLS-1$

        s = new Path("/a/b/c/x.xsd");//$NON-NLS-1$
        b = new Path("/a/b/d/y.xsd");//$NON-NLS-1$
        assertEquals("../c/x.xsd", ModelUtil.getRelativePath(s, b));//$NON-NLS-1$

    }
    // Currently only works when run inside Eclipse environment ...
    // public void testModelFileNameWithInvalidCharacters() {
    //        assertTrue( false == ModelUtil.isValidModelFileName("Model / file with invalid ? characters.xml") ); //$NON-NLS-1$
    // }

}
