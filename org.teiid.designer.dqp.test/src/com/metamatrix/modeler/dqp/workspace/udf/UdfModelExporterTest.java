package com.metamatrix.modeler.dqp.workspace.udf;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.junit.AfterClass;
import org.junit.Test;
import com.metamatrix.core.util.SmartTestSuite;

public class UdfModelExporterTest {

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

    private void assertErrorStatus( UdfModelExporter exporter ) {
        IStatus status = exporter.canExport();
        UdfTestUtil.assertErrorStatus(status);

        status = exporter.doExport(null);
        UdfTestUtil.assertErrorStatus(status);
    }

    private void assertOkStatus( UdfModelExporter exporter ) {
        IStatus status = exporter.canExport();
        UdfTestUtil.assertOkStatus(status);

        status = exporter.doExport(null);
        UdfTestUtil.assertOkStatus(status);
    }

    private UdfModelExporter createExporterHavingNonExistentExportFileDirectory() {
        UdfModelExporter exporter = createValidExporterWithJars();
        exporter.setExportZipFilePath("/bad/path/to/export/file.zip"); //$NON-NLS-1$
        return exporter;
    }

    private UdfModelExporter createExporterHavingUdfJarPathsWithIncorrectFileExtensions() {
        FakeUdfPublisher udfProvider = (FakeUdfPublisher)UdfTestUtil.createValidUdfProviderWithoutJars();

        // create some bad paths and set on provider
        Set<String> invalidUdfModelArchivePaths = new HashSet<String>(1);
        invalidUdfModelArchivePaths.add(UdfTestUtil.WS_UDF_JAR_BAD_EXTENSION);
        udfProvider.setUdfModelArchivePaths(invalidUdfModelArchivePaths);

        UdfModelExporter exporter = new UdfModelExporter(udfProvider);
        String path = UdfTestUtil.createValidNonExistingFilePath();
        tempFiles.add(new File(path));
        exporter.setExportZipFilePath(path);

        return exporter;
    }

    private UdfModelExporter createExporterInOverwriteModeWithExistingExportFile() throws IOException {
        UdfModelExporter exporter = createExporterNotInOverwriteModeWithExistingExportFile();
        exporter.setOverwriteMode(true);
        return exporter;
    }

    private UdfModelExporter createExporterNotInOverwriteModeWithExistingExportFile() throws IOException {
        UdfModelExporter exporter = createValidExporterWithoutJars();
        exporter.setExportZipFilePath(createValidExistingOutputFilePath());
        return exporter;
    }

    private UdfModelExporter createExporterWithEmptyExportFilePath() {
        UdfModelExporter exporter = createValidExporterWithoutJars();
        exporter.setExportZipFilePath(""); //$NON-NLS-1$
        return exporter;
    }

    private UdfModelExporter createExporterWithEmptyUdfModelPath() {
        FakeUdfPublisher udfProvider = new FakeUdfPublisher(""); //$NON-NLS-1$
        return new UdfModelExporter(udfProvider);
    }

    private UdfModelExporter createExporterWithExportFileHavingInvalidFileExtension() {
        UdfModelExporter exporter = createValidExporterWithoutJars();
        exporter.setExportZipFilePath(UdfTestUtil.BAD_IMPORT_FILE_EXTENSION_FILE);
        return exporter;
    }

    private UdfModelExporter createExporterWithExportFileNotExistingAndNotInOverwriteMode() {
        UdfModelExporter exporter = createValidExporterWithoutJars();
        String path = UdfTestUtil.createValidNonExistingFilePath();
        tempFiles.add(new File(path));
        exporter.setExportZipFilePath(path);
        return exporter;
    }

    private UdfModelExporter createExporterWithExportFileNotExistingAndInOverwriteMode() {
        UdfModelExporter exporter = createExporterWithExportFileNotExistingAndNotInOverwriteMode();
        exporter.setOverwriteMode(true);
        return exporter;
    }

    private UdfModelExporter createExporterWithInvalidUdfModelName() {
        FakeUdfPublisher udfProvider = new FakeUdfPublisher(UdfTestUtil.WS_UDF_MODEL_THAT_DOES_NOT_EXIST);
        return new UdfModelExporter(udfProvider);
    }

    private UdfModelExporter createExporterWithNullExportFilePath() {
        UdfModelExporter exporter = createValidExporterWithJars();
        exporter.setExportZipFilePath(null);
        return exporter;
    }

    private UdfModelExporter createExporterWithNullUdfModelPath() {
        FakeUdfPublisher udfProvider = new FakeUdfPublisher(null);
        return new UdfModelExporter(udfProvider);
    }

    private UdfModelExporter createExporterWithNullUdfProvider() {
        return new UdfModelExporter(null);
    }

    private UdfModelExporter createExporterWithUdfJarPathsThatDoNotExist() {
        FakeUdfPublisher udfProvider = (FakeUdfPublisher)UdfTestUtil.createValidUdfProviderWithoutJars();

        // create some bad paths and set on provider
        udfProvider.setUdfModelArchivePaths(UdfTestUtil.WS_INVALID_UDF_JARS);

        UdfModelExporter exporter = new UdfModelExporter(udfProvider);
        String path = UdfTestUtil.createValidNonExistingFilePath();
        tempFiles.add(new File(path));
        exporter.setExportZipFilePath(path);

        return exporter;
    }

    private UdfModelExporter createValidExporterWithJars() {
        UdfModelExporter exporter = new UdfModelExporter(UdfTestUtil.createValidUdfProviderWithValidUdfJars());
        String path = UdfTestUtil.createValidNonExistingFilePath();
        tempFiles.add(new File(path));
        exporter.setExportZipFilePath(path);
        return exporter;
    }

    private UdfModelExporter createValidExporterWithoutJars() {
        UdfModelExporter exporter = new UdfModelExporter(UdfTestUtil.createValidUdfProviderWithoutJars());
        String path = UdfTestUtil.createValidNonExistingFilePath();
        tempFiles.add(new File(path));
        exporter.setExportZipFilePath(path);
        return exporter;
    }

    private String createValidExistingOutputFilePath() throws IOException {
        File file = new File(SmartTestSuite.getTestScratchPath() + File.separatorChar + "file.zip"); //$NON-NLS-1$

        if (!file.exists()) {
            file.createNewFile();
            file.deleteOnExit();
        }

        // add to collection of temp files that will be cleaned up at end of these tests
        tempFiles.add(file);

        return file.getAbsolutePath();
    }

    // ===========================================================================================================================
    // Tests
    // ===========================================================================================================================

    @Test( expected = RuntimeException.class )
    public void shouldNotAllowNullProvider() {
        createExporterWithNullUdfProvider();
    }

    @Test
    public void shouldNotAllowNullUdfModelPath() {
        assertErrorStatus(createExporterWithNullUdfModelPath());
    }

    @Test
    public void shouldNotAllowEmptyUdfModelPath() {
        assertErrorStatus(createExporterWithEmptyUdfModelPath());
    }

    @Test
    public void shouldNotAllowIncorrectUdfModelName() {
        assertErrorStatus(createExporterWithInvalidUdfModelName());
    }

    @Test
    public void shouldNotAllowNullExportFilePath() {
        assertErrorStatus(createExporterWithNullExportFilePath());
    }

    @Test
    public void shouldNotAllowEmptyExportFilePath() {
        assertErrorStatus(createExporterWithEmptyExportFilePath());
    }

    @Test
    public void shouldNotAllowNonExistentExportFileDirectory() {
        assertErrorStatus(createExporterHavingNonExistentExportFileDirectory());
    }

    @Test
    public void shouldNotAllowExportFileWithInvalidFileExtension() {
        assertErrorStatus(createExporterWithExportFileHavingInvalidFileExtension());
    }

    @Test
    public void shouldNotAllowExportFileToExistWhenNotInOverwriteMode() throws IOException {
        assertErrorStatus(createExporterNotInOverwriteModeWithExistingExportFile());
    }

    @Test
    public void shouldAllowExportFileToExistWhenInOverwriteMode() throws IOException {
        assertOkStatus(createExporterInOverwriteModeWithExistingExportFile());
    }

    @Test
    public void shouldAllowExportFileToNotExistWhenNotInOverwriteMode() {
        assertOkStatus(createExporterWithExportFileNotExistingAndNotInOverwriteMode());
    }

    @Test
    public void shouldAllowExportFileToNotExistWhenInOverwriteMode() {
        assertOkStatus(createExporterWithExportFileNotExistingAndInOverwriteMode());
    }

    @Test
    public void shouldAllowJars() {
        assertOkStatus(createValidExporterWithJars());
    }

    @Test
    public void shouldAllowNoJars() {
        assertOkStatus(createValidExporterWithoutJars());
    }

    @Test
    public void shouldNotAllowUdfJarPathsToNotExist() {
        assertErrorStatus(createExporterWithUdfJarPathsThatDoNotExist());
    }

    @Test
    public void shouldNotAllowUdfJarPathsWithIncorrectFileExtensions() {
        assertErrorStatus(createExporterHavingUdfJarPathsWithIncorrectFileExtensions());
    }

}
