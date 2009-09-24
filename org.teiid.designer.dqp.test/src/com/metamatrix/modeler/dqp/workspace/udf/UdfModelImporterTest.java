package com.metamatrix.modeler.dqp.workspace.udf;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.core.runtime.IStatus;
import org.junit.AfterClass;
import org.junit.Test;

public class UdfModelImporterTest {

    // ===========================================================================================================================
    // Class Fields
    // ===========================================================================================================================

    private static Set<File> tempFiles = new HashSet<File>();

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    @AfterClass
    public static void cleanup() {
        for (File file : tempFiles) {
            file.delete();
        }
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    private void assertErrorStatus( UdfModelImporter importer ) {
        IStatus status = importer.canImport();
        UdfTestUtil.assertErrorStatus(status);

        status = importer.doImport(null);
        UdfTestUtil.assertErrorStatus(status);
    }

    private void assertOkStatus( UdfModelImporter importer ) {
        IStatus status = importer.canImport();
        UdfTestUtil.assertOkStatus(status);

        status = importer.doImport(null);
        UdfTestUtil.assertOkStatus(status);
    }

    private UdfModelImporter createValidImporter() {
        UdfModelImporter importer = new UdfModelImporter(UdfTestUtil.createValidUdfPublisherWithValidUdfJars());
        importer.setSourcePath(UdfTestUtil.VALID_IMPORT_ZIP.getAbsolutePath());

        return importer;
    }

    // ===========================================================================================================================
    // Tests
    // ===========================================================================================================================

    @Test
    public void shouldNotAllowNullImportFile() {
        UdfModelImporter importer = createValidImporter();
        importer.setSourcePath(null);
        assertErrorStatus(importer);
    }

    @Test
    public void shouldNotAllowEmptyImportFile() {
        UdfModelImporter importer = createValidImporter();
        importer.setSourcePath(""); //$NON-NLS-1$
        assertErrorStatus(importer);
    }

    @Test
    public void shouldNotAllowImportFileToNotExist() {
        UdfModelImporter importer = createValidImporter();
        String path = UdfTestUtil.createValidNonExistingFilePath();
        tempFiles.add(new File(path));
        importer.setSourcePath(path);
        assertErrorStatus(importer);
    }

    @Test
    public void shouldNotAllowImportFileWithIncorrectFileExtension() {
        UdfModelImporter importer = createValidImporter();
        importer.setSourcePath(UdfTestUtil.VALID_WS_UDF_MODEL);
        assertErrorStatus(importer);
    }

    @Test
    public void shouldNotAllowImportFileToNotHaveUdfModel() {
        UdfModelImporter importer = createValidImporter();
        importer.setSourcePath(UdfTestUtil.IMPORT_FILE_MISSING_UDF_MODEL);
        assertErrorStatus(importer);
    }

    @Test
    public void shouldNotAllowImportFileToHaveOtherFiles() {
        UdfModelImporter importer = createValidImporter();
        importer.setSourcePath(UdfTestUtil.IMPORT_FILE_HAS_OTHER_FILES);
        assertErrorStatus(importer);
    }

    @Test
    public void shouldNotImportUnselectedJars() {
        FakeUdfPublisher udfPublisher = (FakeUdfPublisher)UdfTestUtil.createValidUdfProviderWithValidUdfJars();
        UdfModelImporter importer = new UdfModelImporter(udfPublisher);
        importer.setSourcePath(UdfTestUtil.VALID_IMPORT_ZIP.getAbsolutePath());
        importer.selectJarFile(UdfTestUtil.VALID_IMPORT_ZIP_JARS[0], false);
        Assert.assertFalse("'" + UdfTestUtil.VALID_IMPORT_ZIP_JARS[0] + "' should not be being imported.",
                           importer.isJarFileBeingImported(UdfTestUtil.VALID_IMPORT_ZIP_JARS[0]));
        Assert.assertFalse("Jar file should not have been added to workspace: " + UdfTestUtil.VALID_IMPORT_ZIP_JARS[0],
                           udfPublisher.hasJarBeenImported(UdfTestUtil.VALID_IMPORT_ZIP_JARS[0]));
    }

    @Test
    public void shouldImportJarByDefault() {
        FakeUdfPublisher udfPublisher = (FakeUdfPublisher)UdfTestUtil.createValidUdfProviderWithValidUdfJars();
        UdfModelImporter importer = new UdfModelImporter(udfPublisher);
        importer.setSourcePath(UdfTestUtil.VALID_IMPORT_ZIP.getAbsolutePath());
        Assert.assertTrue("'" + UdfTestUtil.VALID_IMPORT_ZIP_JARS[0] + "' should be being imported.",
                          importer.isJarFileBeingImported(UdfTestUtil.VALID_IMPORT_ZIP_JARS[0]));
    }

    @Test
    public void shouldAllowValidImportFile() {
        IWorkspaceUdfPublisher udfPublisher = UdfTestUtil.createValidUdfPublisherWithValidUdfJars();
        UdfModelImporter importer = new UdfModelImporter(udfPublisher);
        importer.setSourcePath(UdfTestUtil.VALID_IMPORT_ZIP.getAbsolutePath());
        assertOkStatus(importer);
        Assert.assertTrue("UDF model was not replaced", ((FakeUdfPublisher)udfPublisher).isUdfModelReplaced());
        
        for (String jarName : UdfTestUtil.VALID_IMPORT_ZIP_JARS) {
            Assert.assertTrue("Jar file was not added to workspace: " + jarName,
                              ((FakeUdfPublisher)udfPublisher).hasJarBeenImported(jarName));
        }
    }

}
