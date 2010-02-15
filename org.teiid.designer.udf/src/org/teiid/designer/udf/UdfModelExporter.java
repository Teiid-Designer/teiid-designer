/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.modeler.util.FileUtil.Extensions;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;

public class UdfModelExporter {

    /**
     * A prefix for I18n message keys.
     * 
     * @since 6.0.0
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(UdfModelExporter.class);

    /**
     * Number of bytes to read when adding files to the zip file.
     * 
     * @since 6.0.0
     */
    private static final int DATA_BLOCK_SIZE = 16384;

    /**
     * The full path to the file being created by the export.
     * 
     * @since 6.0.0
     */
    private String exportZipFilePath;

    /**
     * A flag indicating if existing files should be overwritten.
     * 
     * @since 6.0.0
     */
    private boolean overwriteMode;

    /**
     * Provides the locations of the UDF model and the UDF jars being exported.
     * 
     * @since 6.0.0
     */
    private final IWorkspaceUdfProvider udfProvider;

    /**
     * @param udfProvider the object responsible for knowing where the UDF model and UDF jars are located (never <code>null</code>
     *        )
     * @throws RuntimeException if the UDF provider is <code>null</code>
     * @since 6.0.0
     */
    public UdfModelExporter( IWorkspaceUdfProvider udfProvider ) {
        if (udfProvider == null) {
            throw new RuntimeException(UdfPlugin.UTIL.getString(PREFIX + "missingUdfProvider")); //$NON-NLS-1$
        }

        this.udfProvider = udfProvider;
    }

    /**
     * @return a status indicating if all the export pre-conditions have been satisfied
     * @since 6.0.0
     */
    public IStatus canExport() {
        // make sure all workspace UDF model information is available
        IStatus status = getUdfProviderStatus();

        if (!status.isOK()) {
            return status;
        }

        // make sure export file path has been set
        if (StringUtil.isEmpty(this.exportZipFilePath)) {
            return UdfPlugin.createErrorStatus(PREFIX + "missingExportFile"); //$NON-NLS-1$
        }

        // make sure export file directory exists
        File exportZip = new File(this.exportZipFilePath);
        File exportDir = exportZip.getParentFile();

        if ((exportDir != null) && !exportDir.exists()) {
            return UdfPlugin.createErrorStatus(PREFIX + "exportDirectoryDoesNotExist", this.exportZipFilePath); //$NON-NLS-1$
        }

        // make sure file extension is correct
        if (!FileUtil.isZipFileName(exportZip.getName())) {
            return UdfPlugin.createErrorStatus(PREFIX + "invalidExportFileExtension", //$NON-NLS-1$
                                               this.exportZipFilePath,
                                               Extensions.ZIP);
        }

        // make sure if export file already exists that overwrite mode has been set
        if (!this.overwriteMode && exportZip.exists()) {
            return UdfPlugin.createErrorStatus(PREFIX + "exportFileExistsOverwriteModeOff", this.exportZipFilePath); //$NON-NLS-1$
        }

        // ready to export
        return Status.OK_STATUS;
    }

    /**
     * Perform the export.
     * 
     * @param monitor the progress monitor used during the export (can be <code>null</code>)
     * @return a status indicating if the export was successful
     * @since 6.0.0
     * @see #canExport()
     */
    public IStatus doExport( IProgressMonitor monitor ) {
        IStatus status = canExport();

        if (status.isOK()) {
            if (monitor == null) {
                monitor = new NullProgressMonitor();
            }

            // set the total work in the monitor
            monitor.beginTask(UdfPlugin.UTIL.getString(PREFIX + "exportTaskName"), 1); //$NON-NLS-1$
            monitor.setTaskName(UdfPlugin.UTIL.getString(PREFIX + "exportTaskName")); //$NON-NLS-1$

            FileOutputStream fos = null;
            ZipOutputStream targetStream = null;

            try {
                if (!monitor.isCanceled()) {
                    // create a list of paths that includes UDF model and jars
                    List<String> allFiles = new ArrayList<String>(1);
                    allFiles.add(this.udfProvider.getUdfModelPath());

                    // create zip file
                    fos = new FileOutputStream(this.exportZipFilePath);
                    targetStream = new ZipOutputStream(fos);
                    FileInputStream fis = null;
                    BufferedInputStream sourceStream = null;

                    // add all the files
                    for (String path : allFiles) {
                        try {
                            // add UDF model
                            fis = new FileInputStream(path);
                            sourceStream = new BufferedInputStream(fis);
                            ZipEntry entry = new ZipEntry(new File(path).getName());
                            targetStream.putNextEntry(entry);

                            // read file and add to zip
                            byte[] data = new byte[DATA_BLOCK_SIZE];
                            int count;

                            while ((count = sourceStream.read(data)) != -1) {
                                targetStream.write(data, 0, count);
                            }

                            monitor.worked(1);

                            if (monitor.isCanceled()) {
                                break;
                            }
                        } finally {
                            sourceStream.close();
                            targetStream.closeEntry();
                        }
                    }
                }
            } catch (Exception e) {
                UdfPlugin.UTIL.log(e);
                return UdfPlugin.createErrorStatus(PREFIX + "errorCreatingExportArchive", this.exportZipFilePath); //$NON-NLS-1$
            } finally {
                if (targetStream != null) {
                    try {
                        targetStream.close();
                    } catch (IOException e) {
                        UdfPlugin.UTIL.log(e);
                    }
                }
            }
        }

        return status;
    }

    /**
     * @return the absolute path of the export zip file or <code>null</code> if not set
     * @since 6.0.0
     */
    public String getExportZipFilePath() {
        return this.exportZipFilePath;
    }

    private IStatus getUdfProviderStatus() {
        String udfModelPath = this.udfProvider.getUdfModelPath();

        // make sure the path to the UDF model has been set
        if (udfModelPath == null) {
            return UdfPlugin.createErrorStatus(PREFIX + "missingUdfExportModel"); //$NON-NLS-1$
        }

        // make sure it is the UDF model
        if (!ModelerCore.UDF_MODEL_NAME.equals(new File(udfModelPath).getName())) {
            return UdfPlugin.createErrorStatus(PREFIX + "invalidUdfExportModel", udfModelPath, ModelerCore.UDF_MODEL_NAME); //$NON-NLS-1$
        }

        // make sure UDF model exists
        if (!new File(udfModelPath).exists()) {
            return UdfPlugin.createErrorStatus(PREFIX + "udfModelDoesNotExist", udfModelPath); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

    /**
     * @return <code>true</code> if existing files will be overwritten
     * @since 6.0.0
     */
    public boolean isOverwriteMode() {
        return this.overwriteMode;
    }

    /**
     * @return <code>true</code> if an existing file exists at selected location and must be overwritten
     * @since 6.0.0
     */
    public boolean isOverwriteRequired() {
        if (this.exportZipFilePath != null) {
            return new File(this.exportZipFilePath).exists();
        }

        return false;
    }

    /**
     * @param exportPath the new absolute path of the exported zip file (can be <code>null</code>)
     * @since 6.0.0
     */
    public void setExportZipFilePath( String exportPath ) {
        this.exportZipFilePath = exportPath;
    }

    /**
     * @param overwriteMode a flag indicating if existing files should be overwritten
     * @since 6.0.0
     */
    public void setOverwriteMode( boolean overwriteMode ) {
        this.overwriteMode = overwriteMode;
    }
}
