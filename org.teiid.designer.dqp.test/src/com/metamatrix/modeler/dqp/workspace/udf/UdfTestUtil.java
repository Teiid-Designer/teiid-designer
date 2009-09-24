package com.metamatrix.modeler.dqp.workspace.udf;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.junit.Assert;
import com.metamatrix.core.modeler.util.FileUtil.Extensions;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.modeler.core.ModelerCore;

public class UdfTestUtil {

    /**
     * the "udf" folder under the "testdata" directory
     */
    public static final String UDF_TEST_DATA_FOLDER = "udf"; //$NON-NLS-1$

    /**
     * an absolute path to a workspace UDF jar that does not have the .jar file extension
     */
    public static final String WS_UDF_JAR_BAD_EXTENSION = SmartTestSuite.getTestDataFile(UDF_TEST_DATA_FOLDER
                                                                                         + File.separatorChar
                                                                                         + "udfJarBadExtension.zip").getAbsolutePath(); //$NON-NLS-1$

    /**
     * a valid zip file to use for import (has jars that don't conflict with the good workspace UDF jars)
     */
    public static final File VALID_IMPORT_ZIP = SmartTestSuite.getTestDataFile(UDF_TEST_DATA_FOLDER + File.separatorChar
                                                                               + "importGoodNoJarConflicts.zip"); //$NON-NLS-1$

    public static final String[] VALID_IMPORT_ZIP_JARS = new String[] {"badconn.jar", "TestUDF.jar", "XSDFileConnector.jar"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public static final String IMPORT_FILE_MISSING_UDF_MODEL = SmartTestSuite.getTestDataFile(UDF_TEST_DATA_FOLDER
                                                                                              + File.separatorChar
                                                                                              + "importWithMissingModel.zip").getAbsolutePath(); //$NON-NLS-1$

    public static final String IMPORT_FILE_HAS_OTHER_FILES = SmartTestSuite.getTestDataFile(UDF_TEST_DATA_FOLDER
                                                                                            + File.separatorChar
                                                                                            + "importWithInvalidFile.zip").getAbsolutePath(); //$NON-NLS-1$

    /**
     * a path that has an invalid file extension (should be .zip but isn't)
     */
    public static final String BAD_IMPORT_FILE_EXTENSION_FILE = SmartTestSuite.getTestScratchPath() + File.separatorChar + "file.jar"; //$NON-NLS-1$

    /**
     * a path to a workspace UDF model that doesn't exist
     */
    public static final String WS_UDF_MODEL_THAT_DOES_NOT_EXIST = "doesNotExist.xmi"; //$NON-NLS-1$

    /**
     * 3 valid (right extension, exist) workspace UDF jars (udf1.jar, udf2.jar, udf3.jar)
     */
    public static final Set<String> WS_VALID_UDF_JARS;

    /**
     * invalid (bad extension, does not exist) workspace UDF jars
     */
    public static final Set<String> WS_INVALID_UDF_JARS;

    /**
     * Path to a valid, existing UDF model
     */
    public static final String VALID_WS_UDF_MODEL;

    // ===========================================================================================================================
    // Class Initializer
    // ===========================================================================================================================

    static {
        WS_VALID_UDF_JARS = new HashSet<String>(3);

        File udfJar = SmartTestSuite.getTestDataFile(UDF_TEST_DATA_FOLDER + File.separatorChar + "udf1.jar"); //$NON-NLS-1$
        WS_VALID_UDF_JARS.add(udfJar.getAbsolutePath());

        udfJar = SmartTestSuite.getTestDataFile(UDF_TEST_DATA_FOLDER + File.separatorChar + "udf2.jar"); //$NON-NLS-1$
        WS_VALID_UDF_JARS.add(udfJar.getAbsolutePath());

        udfJar = SmartTestSuite.getTestDataFile(UDF_TEST_DATA_FOLDER + File.separatorChar + "udf3.jar"); //$NON-NLS-1$
        WS_VALID_UDF_JARS.add(udfJar.getAbsolutePath());

        WS_INVALID_UDF_JARS = new HashSet<String>(3);
        WS_INVALID_UDF_JARS.add("doesNotExist.jar"); //$NON-NLS-1$
        File badExtFile = SmartTestSuite.getTestDataFile(UDF_TEST_DATA_FOLDER + File.separatorChar + "udfJarBadExtension.zip"); //$NON-NLS-1$
        WS_INVALID_UDF_JARS.add(badExtFile.getAbsolutePath()); // exists but bad file extension

        File udfModel = SmartTestSuite.getTestDataFile(UdfTestUtil.UDF_TEST_DATA_FOLDER + File.separatorChar
                                                       + ModelerCore.UDF_MODEL_NAME);
        VALID_WS_UDF_MODEL = udfModel.getAbsolutePath();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    public static void assertErrorStatus( IStatus status ) {
        Assert.assertEquals(status.getMessage(), IStatus.ERROR, status.getSeverity());
    }

    public static void assertOkStatus( IStatus status ) {
        Assert.assertEquals(status.getMessage(), IStatus.OK, status.getSeverity());
    }

    /**
     * This file path can be used by either the importer or exporter. If the file is actually created it should be deleted at the
     * end of the test.
     * 
     * @return a file path with the right file extension but the actual file does not exist
     */
    public static String createValidNonExistingFilePath() {
        Random random = new Random(System.currentTimeMillis());
        String parentDir = SmartTestSuite.getTestScratchPath();
        File file = new File(parentDir, String.valueOf(random.nextLong()) + Extensions.ZIP);

        while (file.exists()) {
            file = new File(parentDir, String.valueOf(random.nextLong()) + Extensions.ZIP);
        }

        return file.getAbsolutePath();
    }

    public static IWorkspaceUdfProvider createValidUdfProviderWithoutJars() {
        return new FakeUdfPublisher(VALID_WS_UDF_MODEL);
    }

    public static IWorkspaceUdfProvider createValidUdfProviderWithValidUdfJars() {
        FakeUdfPublisher udfProvider = new FakeUdfPublisher(VALID_WS_UDF_MODEL);
        udfProvider.setUdfModelArchivePaths(WS_VALID_UDF_JARS);
        return udfProvider;
    }

    public static IWorkspaceUdfPublisher createValidUdfPublisherWithValidUdfJars() {
        FakeUdfPublisher udfPublisher = new FakeUdfPublisher(VALID_WS_UDF_MODEL);
        udfPublisher.setUdfModelArchivePaths(WS_VALID_UDF_JARS);
        return udfPublisher;
    }

    public static String getFileNameFromPath( String path ) {
        int index = path.lastIndexOf(File.separatorChar);

        if (index == -1) {
            return path;
        }

        return path.substring(index + 1);
    }

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    private UdfTestUtil() {
        // don't allow construction
    }

}
