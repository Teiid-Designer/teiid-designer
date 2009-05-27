/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.loader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jdom.Element;
import com.metamatrix.common.util.ByteArrayHelper;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.common.vdb.api.VDBStreamImpl;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.vdb.VDBStatus;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;
import com.metamatrix.vdb.runtime.BasicVDBDefn;

/**
 */
public class TestVDBWriter extends TestCase {

    // private BasicConfigurationObjectEditor editor = new BasicConfigurationObjectEditor(false);

    public TestVDBWriter( String name ) {
        super(name);
    }

    public void testWritingExportVDBDefn() throws Exception {

        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$

        String testDataPath = SmartTestSuite.getGlobalTestDataPath();

        VDBWriter.exportVDBDefn(vdbDefn, "VDBDefnFileName.DEF", "TestVDB.vdb", testDataPath, new Properties());//$NON-NLS-1$ //$NON-NLS-2$

    }

    // VAH - this is no longer an issue because we're setting a predefined name

    // public void testInvalidWritingExportVDBDefn() throws Exception {
    //       
    //        
    // try {
    //            BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
    //            
    // String testDataPath = getGlobalTestDataPath();
    //            
    //            VDBWriter.exportVDBDefn(vdbDefn, "", "TestVDB.vdb", testDataPath, null);//$NON-NLS-1$ //$NON-NLS-2$
    //            
    //            fail("The exporting of the VDB should have failed, its missing the .DEF file name");//$NON-NLS-1$
    // } catch (Exception e) {
    //            
    // }
    //        
    // try {
    //            BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
    //            
    // String testDataPath = getGlobalTestDataPath();
    //            
    //            VDBWriter.exportVDBDefn(vdbDefn, "VDBDefnFileName.DEF", "", testDataPath, null);//$NON-NLS-1$ //$NON-NLS-2$
    //            
    //            fail("The exporting of the VDB should have failed, its missing the .DEF file name");//$NON-NLS-1$
    // } catch (Exception e) {
    //            
    // }
    //       
    // }

    public void testWritingEmptyVDBDefn() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB"); //$NON-NLS-1$
            vdbDefn.setVersion("2");//$NON-NLS-1$
            vdbDefn.setFileName("TestFileName");//$NON-NLS-1$

            VDBWriter.writeVDBDefn(outputStream, vdbDefn, new Properties());

            outputStream.flush();
            System.out.println(outputStream.toString());
        } finally {
            outputStream.close();

        }
    }

    public void testWritingVDBDefn() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB");//$NON-NLS-1$
            vdbDefn.setVersion("2");//$NON-NLS-1$
            vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
            vdbDefn.setCreatedBy("TestVDBExport");//$NON-NLS-1$
            vdbDefn.setDateCreated(DateUtil.getCurrentDate());
            vdbDefn.setDescription("VDB Description");//$NON-NLS-1$
            vdbDefn.setHasWSDLDefined(false);
            vdbDefn.setStatus(VDBStatus.ACTIVE);
            vdbDefn.setUUID("mmuuid");//$NON-NLS-1$

            VDBWriter.writeVDBDefn(outputStream, vdbDefn, new Properties());

            outputStream.flush();
            System.out.println(outputStream.toString());
        } finally {
            outputStream.close();

        }

    }

    /**
     * Exception: java.lang.NullPointerException at com.metamatrix.vdb.edit.loader.VDBWriter.writeVDBDefn(VDBWriter.java:143) at
     * com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper.saveDefn(VdbDefnHelper.java:454) is caused because the model had a
     * ConnectorBinding UUID for which the binding did not exist at the VDBDefn level.
     * 
     * @throws Exception
     * @since 4.2
     */
    public void testDef18613() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB");//$NON-NLS-1$
            vdbDefn.setVersion("2");//$NON-NLS-1$
            vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
            vdbDefn.setCreatedBy("TestVDBExport");//$NON-NLS-1$
            vdbDefn.setDateCreated(DateUtil.getCurrentDate());
            vdbDefn.setDescription("VDB Description");//$NON-NLS-1$
            vdbDefn.setHasWSDLDefined(false);
            vdbDefn.setStatus(VDBStatus.ACTIVE);
            vdbDefn.setUUID("mmuuid");//$NON-NLS-1$

            BasicVDBModelDefn mdefn = new BasicVDBModelDefn("Model1");//$NON-NLS-1$
            mdefn.addConnectorBindingByName("BingName1");//$NON-NLS-1$
            mdefn.setDescription("ModelDesc");//$NON-NLS-1$
            mdefn.setIsVisible(true);
            mdefn.setModelType(ModelType.PHYSICAL);
            mdefn.setUuid("ModelUUID");//$NON-NLS-1$

            vdbDefn.addModelInfo(mdefn);

            VDBWriter.writeVDBDefn(outputStream, vdbDefn, new Properties());

            outputStream.flush();
            System.out.println(outputStream.toString());
        } finally {
            outputStream.close();

        }

    }

    public void testWriteVDBArchiveWithDef() throws Exception {

        String testDataPath = SmartTestSuite.getGlobalTestDataPath();

        String filePath = FileUtils.buildDirectoryPath(new String[] {testDataPath, "/global/Books/Books.vdb"});//$NON-NLS-1$

        File f = new File(filePath);

        FileInputStream fis = new FileInputStream(f);

        // read in a vdb into byte[] so it can be used to export
        byte[] vdbBytes = ByteArrayHelper.toByteArray(fis);

        String scratchPath = SmartTestSuite.getTestScratchPath();

        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB");//$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        vdbDefn.setCreatedBy("TestVDBExport");//$NON-NLS-1$
        vdbDefn.setDateCreated(DateUtil.getCurrentDate());
        vdbDefn.setDescription("VDB Description");//$NON-NLS-1$
        vdbDefn.setHasWSDLDefined(false);
        vdbDefn.setStatus(VDBStatus.ACTIVE);
        vdbDefn.setUUID("mmuuid");//$NON-NLS-1$
        vdbDefn.setVDBStream(new VDBStreamImpl(vdbBytes));

        BasicVDBModelDefn mdefn = new BasicVDBModelDefn("Model1");//$NON-NLS-1$
        mdefn.addConnectorBindingByName("BindingName1");//$NON-NLS-1$
        mdefn.setDescription("ModelDesc");//$NON-NLS-1$
        mdefn.setIsVisible(true);
        mdefn.setModelType(ModelType.PHYSICAL);
        mdefn.setUuid("ModelUUID");//$NON-NLS-1$

        vdbDefn.addModelInfo(mdefn);

        String archiveFileName = "TestArchive.vdb";//$NON-NLS-1$

        String archivePath = FileUtils.buildDirectoryPath(new String[] {scratchPath, archiveFileName});

        VDBWriter.exportVDBArchive(archivePath, vdbDefn, new Properties());

        if (!VDBReader.isValidVDBDefFileIncluded(archivePath)) {
            fail("VDB Definition was not added to the archive when exported");//$NON-NLS-1$
        }

    }

    public void testWriteVDBArchiveWithDefToOutputStream() throws Exception {

        String testDataPath = SmartTestSuite.getGlobalTestDataPath();

        String filePath = FileUtils.buildDirectoryPath(new String[] {testDataPath, "/global/Books/Books.vdb"});//$NON-NLS-1$

        File f = new File(filePath);

        FileInputStream fis = new FileInputStream(f);

        // read in a vdb into byte[] so it can be used to export
        byte[] vdbBytes = ByteArrayHelper.toByteArray(fis);

        String scratchPath = SmartTestSuite.getTestScratchPath();

        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB");//$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        vdbDefn.setCreatedBy("TestVDBExport");//$NON-NLS-1$
        vdbDefn.setDateCreated(DateUtil.getCurrentDate());
        vdbDefn.setDescription("VDB Description");//$NON-NLS-1$
        vdbDefn.setHasWSDLDefined(false);
        vdbDefn.setStatus(VDBStatus.ACTIVE);
        vdbDefn.setUUID("mmuuid");//$NON-NLS-1$
        vdbDefn.setVDBStream(new VDBStreamImpl(vdbBytes));

        BasicVDBModelDefn mdefn = new BasicVDBModelDefn("Model1");//$NON-NLS-1$
        mdefn.addConnectorBindingByName("BindingName1");//$NON-NLS-1$
        mdefn.setDescription("ModelDesc");//$NON-NLS-1$
        mdefn.setIsVisible(true);
        mdefn.setModelType(ModelType.PHYSICAL);
        mdefn.setUuid("ModelUUID");//$NON-NLS-1$

        vdbDefn.addModelInfo(mdefn);

        String archiveFileName = "TestArchive.vdb";//$NON-NLS-1$

        String archivePath = FileUtils.buildDirectoryPath(new String[] {scratchPath, archiveFileName});

        new File(archivePath).delete();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {

            VDBWriter.exportVDBArchive(bos, vdbDefn, new Properties());
        } finally {
            bos.close();
        }

        FileUtils.write(bos.toByteArray(), new File(archivePath));

        if (!VDBReader.isValidVDBDefFileIncluded(archivePath)) {
            fail("VDB Definition was not added to the archive when exported");//$NON-NLS-1$
        }

    }

    public void testInvalidWriteVDBArchiveWithDef() throws Exception {

        String scratchPath = SmartTestSuite.getTestScratchPath();

        BasicVDBDefn vdbDefn = new BasicVDBDefn("TestVDB");//$NON-NLS-1$
        vdbDefn.setVersion("2");//$NON-NLS-1$
        vdbDefn.setFileName("TestFileName");//$NON-NLS-1$
        vdbDefn.setCreatedBy("TestVDBExport");//$NON-NLS-1$
        vdbDefn.setDateCreated(DateUtil.getCurrentDate());
        vdbDefn.setDescription("VDB Description");//$NON-NLS-1$
        vdbDefn.setHasWSDLDefined(false);
        vdbDefn.setStatus(VDBStatus.ACTIVE);
        vdbDefn.setUUID("mmuuid");//$NON-NLS-1$

        String archiveFileName = "TestArchive.vdb";//$NON-NLS-1$

        String archivePath = FileUtils.buildDirectoryPath(new String[] {scratchPath, archiveFileName});

        try {
            VDBWriter.exportVDBArchive(archivePath, vdbDefn, new Properties());
            fail("Should have failed, did not contain the vdb archive bytes");//$NON-NLS-1$
        } catch (Throwable t) {

        }

    }

    public void testWriteVDBArchive() throws Exception {

        String testDataPath = SmartTestSuite.getGlobalTestDataPath();

        String filePath = FileUtils.buildDirectoryPath(new String[] {testDataPath, "/global/Books/Books.vdb"});//$NON-NLS-1$

        File f = new File(filePath);

        FileInputStream fis = new FileInputStream(f);

        // read in a vdb into byte[] so it can be used to export
        byte[] vdbBytes = ByteArrayHelper.toByteArray(fis);

        String scratchPath = SmartTestSuite.getTestScratchPath();

        VDBWriter.writeVDBArchive(scratchPath, "TestVDB.vdb", new VDBStreamImpl(vdbBytes));//$NON-NLS-1$

    }

    // this is no longer an issue because we're setting the archive name if one is not specified

    public void testUpdatingVDBName() throws Exception {
        String testDataPath = SmartTestSuite.getTestDataPath();
        String testScratchPath = SmartTestSuite.getTestScratchPath();
        String vdbDefPath = FileUtils.buildDirectoryPath(new String[] {testDataPath, "/vdbloader/PartsSupplier.DEF"});//$NON-NLS-1$

        String tempVDB = FileUtils.buildDirectoryPath(new String[] {testScratchPath, "/vdbloader/Updated.DEF"});//$NON-NLS-1$ $NON-NLS-2$

        FileUtils.copy(vdbDefPath, tempVDB);

        File f = new File(tempVDB);
        if (!f.exists()) {
            fail(vdbDefPath + " was not copied to " + tempVDB);//$NON-NLS-1$
        }

        String updatedVDBName = "UpdateVDBName";//$NON-NLS-1$

        Properties props = new Properties();
        props.setProperty("txnAutoWrap", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        VDBWriter.updateConfigDefFile(f, updatedVDBName, props);

        VDBDefnXMLHelper helper = new VDBDefnXMLHelper();
        Element e = helper.getRoot(f);
        if (e == null) {
            fail("Did not get the root element from file " + f);//$NON-NLS-1$
        }
        VDBDefn vdbDefn = helper.createVDBDefn(e, null);

        if (!vdbDefn.getName().equalsIgnoreCase(updatedVDBName)) {
            fail("VDB Name was not updated");//$NON-NLS-1$
        }

        // assertTrue(Boolean.valueOf(vdbDefn.getExecutionProperties().getProperty("txnAutoWrap").booleanValue());
    }

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);

    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestVDBWriter"); //$NON-NLS-1$
        suite.addTestSuite(TestVDBWriter.class);

        return new TestSetup(suite) { // junit.extensions package

            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                ModelerCore.testLoadModelContainer();
            }

            @Override
            public void tearDown() {
            }
        };
    }
}
