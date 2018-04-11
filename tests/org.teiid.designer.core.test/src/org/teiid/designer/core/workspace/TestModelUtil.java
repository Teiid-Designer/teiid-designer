/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import java.io.File;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.designer.common.xmi.XMIHeader;



/**
 * TestModelUtil
 */
public class TestModelUtil extends TestCase {

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite("TestModelUtil"); //$NON-NLS-1$
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

    /**
     * Constructor for TestModelUtil.
     * 
     * @param name
     */
    public TestModelUtil( final String name ) {
        super(name);
    }

    public void testAlmostEmptyModelFileNameWithAllCapsXMLExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName(".XML")); //$NON-NLS-1$
    }

    public void testAlmostEmptyModelFileNameWithWrongExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName(".xyz")); //$NON-NLS-1$
    }

    public void testAlmostEmptyModelFileNameWithXMLExtension() {
        assertTrue(true == ModelUtil.isValidModelFileName(".xml")); //$NON-NLS-1$
    }

    public void testGetFileExtensionForFileWithExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "testFileWithExtension.abc"); //$NON-NLS-1$
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

    public void testGetXmiHeaderWith0200File() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "partsSupplierOracle_v0200.xml"); //$NON-NLS-1$
        final XMIHeader header = ModelFileUtil.getXmiHeader(f);
        assertTrue(null != header);
        assertTrue(header.getXmiVersion().startsWith("1.")); //$NON-NLS-1$
        assertTrue(null == header.getUUID());
    }

    public void testGetXmiHeaderWith0300File() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "VirtualNorthwind.xml"); //$NON-NLS-1$
        final XMIHeader header = ModelFileUtil.getXmiHeader(f);
        assertTrue(null != header);
        assertTrue(header.getXmiVersion().startsWith("1.")); //$NON-NLS-1$
        assertTrue(null == header.getUUID());
    }

    public void testGetXmiHeaderWithEmptyModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "emptyModel.xmi"); //$NON-NLS-1$
        final XMIHeader header = ModelFileUtil.getXmiHeader(f);
        assertTrue(null != header);
        assertTrue(header.getXmiVersion().startsWith("2.")); //$NON-NLS-1$
        assertTrue(null != header.getUUID());
    }

    public void testGetXmiHeaderWithMsWordFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "sampleMSWord.doc"); //$NON-NLS-1$
        assertTrue(null == ModelFileUtil.getXmiHeader(f));
    }

    public void testGetXmiHeaderWithNonEmptyModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonEmptyModel.xmi"); //$NON-NLS-1$
        final XMIHeader header = ModelFileUtil.getXmiHeader(f);
        assertTrue(null != header);
        assertTrue(header.getXmiVersion().startsWith("2.")); //$NON-NLS-1$
        assertTrue(null != header.getUUID());
    }

    public void testGetXmiHeaderWithNonExistentFile() {
        assertTrue(null == ModelFileUtil.getXmiHeader(new File(""))); //$NON-NLS-1$
    }

    public void testGetXmiHeaderWithNonModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonModelFile.txt"); //$NON-NLS-1$
        assertTrue(null == ModelFileUtil.getXmiHeader(f));
    }

    public void testGetXmiHeaderWithXsdFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "Books.xsd"); //$NON-NLS-1$
        assertTrue(null != ModelFileUtil.getXmiHeader(f));
    }

    public void testGetXmiHeaderWithZipFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(null == ModelFileUtil.getXmiHeader(f));
    }

    public void testGetXsdHeaderWithEmptyModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "emptyModel.xmi"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithMsWordFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "sampleMSWord.doc"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithNonEmptyModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonEmptyModel.xmi"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithNonExistentFile() {
        assertTrue(null == ModelUtil.getXsdHeader(new File(""))); //$NON-NLS-1$
    }

    public void testGetXsdHeaderWithNonModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonModelFile.txt"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithVdbArchive() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplierVirtual.vdb"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithXsdFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "Books.xsd"); //$NON-NLS-1$
        assertTrue(null != ModelUtil.getXsdHeader(f));
    }

    public void testGetXsdHeaderWithZipFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(null == ModelUtil.getXsdHeader(f));
    }

    public void testIsModelFileWith0200File() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "partsSupplierOracle_v0200.xml"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWith0300File() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "VirtualNorthwind.xml"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithEmptyModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "emptyModel.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithMsWordFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "sampleMSWord.doc"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithNonEmptyModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonEmptyModel.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithNonExistentFile() {
        assertTrue(false == ModelFileUtil.isModelFile(new File(""))); //$NON-NLS-1$
    }

    public void testIsModelFileWithNonModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonModelFile.txt"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithVdbArchive() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplierVirtual.vdb"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithXmiFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplier_Oracle.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithXmiFileAndMixedCaseExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplier_Oracle.xMI"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithXmiFileAndUpperCaseExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplier_Oracle.XMI"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithXsdFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "Books.xsd"); //$NON-NLS-1$
        assertTrue(true == ModelFileUtil.isModelFile(f));
    }

    public void testIsModelFileWithZipFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isModelFile(f));
    }

    public void testIsVdbArchiveFileWithNonExistentFile() {
        assertTrue(false == ModelFileUtil.isVdbArchiveFile(new File(""))); //$NON-NLS-1$
    }

    public void testIsVdbArchiveFileWithNonVdbFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isVdbArchiveFile(f));
    }

    public void testIsVdbArchiveFileWithVdbFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplierVirtual.vdb"); //$NON-NLS-1$
        assertTrue(true == ModelFileUtil.isVdbArchiveFile(f));
    }

    public void testIsVdbArchiveFileWithVdbFileAndMixedCaseExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplierVirtual.vDb"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isVdbArchiveFile(f));
    }

    public void testIsVdbArchiveFileWithVdbFileAndUpperCaseExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplierVirtual.VDB"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isVdbArchiveFile(f));
    }

    public void testIsXmiFileWith0200File() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "partsSupplierOracle_v0200.xml"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWith0300File() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "VirtualNorthwind.xml"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithEmptyModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "emptyModel.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithMsWordFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "sampleMSWord.doc"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithNonEmptyModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonEmptyModel.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithNonExistentFile() {
        assertTrue(false == ModelUtil.isXmiFile(new File(""))); //$NON-NLS-1$
    }

    public void testIsXmiFileWithNonModelFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonModelFile.txt"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithVdbArchive() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplierVirtual.vdb"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithXmiFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplier_Oracle.xmi"); //$NON-NLS-1$
        assertTrue(true == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithXmiFileAndMixedCaseExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplier_Oracle.xMI"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithXmiFileAndUpperCaseExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "PartSupplier_Oracle.XMI"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithXsdFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "Books.xsd"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXmiFileWithZipFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "builtInDatatypes.zip"); //$NON-NLS-1$
        assertTrue(false == ModelUtil.isXmiFile(f));
    }

    public void testIsXsdFileWithNonExistentFile() {
        assertTrue(false == ModelFileUtil.isXsdFile(new File(""))); //$NON-NLS-1$
    }

    public void testIsXsdFileWithNonXsdFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "nonModelFile.txt"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isXsdFile(f));
    }

    public void testIsXsdFileWithXsdFile() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "Books.xsd"); //$NON-NLS-1$
        assertTrue(true == ModelFileUtil.isXsdFile(f));
    }

    public void testIsXsdFileWithXsdFileAndMixedCaseExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "Books.xsD"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isXsdFile(f));
    }

    public void testIsXsdFileWithXsdFileAndUpperCaseExtension() {
        final File f = SmartTestDesignerSuite.getTestDataFile(getClass(), "Books.XSD"); //$NON-NLS-1$
        assertTrue(false == ModelFileUtil.isXsdFile(f));
    }

    public void testModelFileNameWithAllCapsVDBExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.VDB")); //$NON-NLS-1$
    }

    public void testModelFileNameWithAllCapsXMIExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.XMI")); //$NON-NLS-1$
    }

    public void testModelFileNameWithAllCapsXMLExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.XML")); //$NON-NLS-1$
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

    public void testModelFileNameWithWrongExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.xyz")); //$NON-NLS-1$
    }

    public void testModelFileNameWithXMLExtension() {
        assertTrue(true == ModelUtil.isValidModelFileName("SomeModel.xml")); //$NON-NLS-1$
    }

    public void testNullModelFileName() {
        assertTrue(false == ModelUtil.isValidModelFileName(null));
    }

    public void testNullPackageName() {
        assertTrue(false == ModelUtil.isValidFolderNameForPackage(null));
    }

    public void testValidModelFileNameWithSpaces() {
        assertTrue(true == ModelUtil.isValidModelFileName("Model file with spaces.xml")); //$NON-NLS-1$
    }

    public void testValidPackageName() {
        assertTrue(true == ModelUtil.isValidFolderNameForPackage("ThisIsAValidPackage")); //$NON-NLS-1$
    }

    public void testValidPackageNameWithExtension() {
        assertTrue(true == ModelUtil.isValidFolderNameForPackage("This Is A Valid Package.txt")); //$NON-NLS-1$
    }

    public void testValidPackageNameWithSpaces() {
        assertTrue(true == ModelUtil.isValidFolderNameForPackage("This Is A Valid Package")); //$NON-NLS-1$
    }

    public void testValidPackageNameWithXMLExtension() {
        assertTrue(true == ModelUtil.isValidFolderNameForPackage("This Is A Valid Package.xml")); //$NON-NLS-1$
    }

    public void testZeroLengthModelFileName() {
        assertTrue(false == ModelUtil.isValidModelFileName("")); //$NON-NLS-1$
    }

    public void testZeroLengthPackageName() {
        assertTrue(false == ModelUtil.isValidFolderNameForPackage("")); //$NON-NLS-1$
    }

    public void tstAlmostEmptyModelFileNameWithAllCapsMMMExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName(".MMM")); //$NON-NLS-1$
    }

    public void tstAlmostEmptyModelFileNameWithMMMExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName(".mmm")); //$NON-NLS-1$
    }

    public void tstModelFileNameWithAllCapsMMMExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.MMM")); //$NON-NLS-1$
    }

    public void tstModelFileNameWithMMMExtension() {
        assertTrue(false == ModelUtil.isValidModelFileName("SomeModel.mmm")); //$NON-NLS-1$
    }

}
