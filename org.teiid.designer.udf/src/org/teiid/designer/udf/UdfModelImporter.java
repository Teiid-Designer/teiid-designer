/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.modeler.util.FileUtil;
import com.metamatrix.core.modeler.util.FileUtil.Extensions;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.ModelerCore;

public class UdfModelImporter {

    /**
     * A prefix for I18n message keys.
     * 
     * @since 6.0.0
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(UdfModelImporter.class);

    /**
     * The full path to the zip file being imported.
     * 
     * @since 6.0.0
     */
    private String sourcePath;

    /**
     * Provides the locations of the UDF model and the UDF jars currently in the workspace.
     * 
     * @since 6.0.0
     */
    private final IWorkspaceUdfPublisher udfPublisher;

    /**
     * Indicates if the import file is a valid UDF import file.
     * 
     * @since 6.0.0
     */
    private IStatus sourceStatus = UdfPlugin.createErrorStatus(PREFIX + "missingImportArchivePath"); //$NON-NLS-1$;

    /**
     * @param udfPublisher the object responsible for knowing where the UDF model and UDF jars are located in the workspace (never
     *        <code>null</code>)
     * @throws RuntimeException if the UDF publisher is <code>null</code>
     * @since 6.0.0
     */
    public UdfModelImporter( IWorkspaceUdfPublisher udfPublisher ) {
        if (udfPublisher == null) {
            throw new RuntimeException(UdfPlugin.UTIL.getString(PREFIX + "missingUdfPublisher")); //$NON-NLS-1$
        }

        this.udfPublisher = udfPublisher;
    }

    /**
     * @return a status indicating if all the import pre-conditions have been satisfied
     * @since 6.0.0
     */
    public IStatus canImport() {
        // make sure all workspace UDF model information is available
        IStatus status = getUdfPublisherStatus();

        if (!status.isOK()) {
            return status;
        }

        // make sure imported zip file is valid
        return isValidZipFile();
    }

    /**
     * Perform the import.
     * 
     * @param monitor the progress monitor used during the import (can be <code>null</code>)
     * @return a status indicating if the import was successful
     * @since 6.0.0
     * @see #canImport()
     */
    public IStatus doImport( IProgressMonitor monitor ) {
        IStatus status = canImport();

        if (status.isOK()) {
            if (monitor == null) {
                monitor = new NullProgressMonitor();
            }

            ZipFile zipFile = null;

            try {
                if (!monitor.isCanceled()) {
                    zipFile = new ZipFile(this.sourcePath);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    // set the total work in the monitor
                    monitor.beginTask(UdfPlugin.UTIL.getString(PREFIX + "importTaskName"), (zipFile.size())); //$NON-NLS-1$
                    monitor.setTaskName(UdfPlugin.UTIL.getString(PREFIX + "importTaskName")); //$NON-NLS-1$

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();
                        String name = entry.getName();

                        if (!FileUtil.isArchiveFileName(name, false)) {
                            // must be the UDF model
                            assert entry.getName().endsWith(ModelerCore.UDF_MODEL_NAME);
                            this.udfPublisher.replaceUdfModel(zipFile.getInputStream(entry));
                        }

                        monitor.worked(1);
                    }
                }
            } catch (Exception e) {
                UdfPlugin.UTIL.log(e);
                return UdfPlugin.createErrorStatus(PREFIX + "errorReadingImportArchive", this.sourcePath); //$NON-NLS-1$
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        UdfPlugin.UTIL.log(e);
                    }
                }
            }
        }

        return status;
    }

    /**
     * @return the absolute path to the file being imported or <code>null</code> if not set
     * @since 6.0.0
     */
    public String getSourcePath() {
        return this.sourcePath;
    }

    private IStatus getUdfPublisherStatus() {
        String udfModelPath = this.udfPublisher.getUdfModelPath();

        // make sure the path to the UDF model has been set
        if (udfModelPath == null) {
            return UdfPlugin.createErrorStatus(PREFIX + "missingWorkspaceUdfModelPath"); //$NON-NLS-1$
        }

        // make sure it is the UDF model
        if (!ModelerCore.UDF_MODEL_NAME.equals(new File(udfModelPath).getName())) {
            return UdfPlugin.createErrorStatus(PREFIX + "invalidWorkspaceUdfModelName", //$NON-NLS-1$
                                               udfModelPath,
                                               ModelerCore.UDF_MODEL_NAME);
        }

        // make sure UDF model exists
        if (!new File(udfModelPath).exists()) {
            return UdfPlugin.createErrorStatus(PREFIX + "udfModelDoesNotExist", udfModelPath); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

    /**
     * @return a status indicating if the selected import zip file is a valid to use when importing a UDF model
     * @since 6.0.0
     */
    public IStatus isValidZipFile() {
        return this.sourceStatus;
    }

    private boolean isValidJarFileEntry( ZipEntry entry ) {
        String name = entry.getName();

        // valid if a jar file
        if ((name != null) && FileUtil.isArchiveFileName(name, false)) {
            return true;
        }

        // valid if the UDF model
        if (ModelerCore.UDF_MODEL_NAME.equals(name)) {
            return true;
        }

        return false;
    }

    /**
     * @param sourcePath the new absolute path of the file being imported (can be <code>null</code>)
     * @since 6.0.0
     */
    public void setSourcePath( String sourcePath ) {
        this.sourcePath = sourcePath;
        updateState();
    }

    private void updateState() {
        this.sourceStatus = null;

        if (StringUtil.isEmpty(this.sourcePath)) {
            this.sourceStatus = UdfPlugin.createErrorStatus(PREFIX + "missingImportArchivePath"); //$NON-NLS-1$;
        } else {
            File importZip = new File(this.sourcePath);

            if (!importZip.exists()) {
                this.sourceStatus = UdfPlugin.createErrorStatus(PREFIX + "importArchiveDoesNotExist", this.sourcePath); //$NON-NLS-1$
            } else if (!FileUtil.isZipFileName(importZip.getName())) {
                this.sourceStatus = UdfPlugin.createErrorStatus(PREFIX + "invalidImportFileExtension", //$NON-NLS-1$
                                                                this.sourcePath,
                                                                Extensions.ZIP);
            } else {
                boolean foundUdfModel = false;
                ZipFile zipFile = null;

                // need to make sure that contents consist only of FunctionDefinitions.xml and jar files
                try {
                    zipFile = new ZipFile(this.sourcePath);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();

                        if (isValidJarFileEntry(entry)) {
                            String entryName = entry.getName();

                            if (ModelerCore.UDF_MODEL_NAME.equals(entryName)) {
                                foundUdfModel = true;
                            }
                        } else {
                            this.sourceStatus = UdfPlugin.createErrorStatus(PREFIX + "invalidImportArchive", //$NON-NLS-1$
                                                                            this.sourcePath,
                                                                            ModelerCore.UDF_MODEL_NAME);
                            break; // don't process zip any more
                        }
                    }

                    // must have found the UDF model
                    if ((this.sourceStatus == null) && !foundUdfModel) {
                        this.sourceStatus = UdfPlugin.createErrorStatus(PREFIX + "invalidImportArchive", //$NON-NLS-1$
                                                                        this.sourcePath,
                                                                        ModelerCore.UDF_MODEL_NAME);
                    }

                    // all good
                    if (this.sourceStatus == null) {
                        this.sourceStatus = Status.OK_STATUS;
                    }
                } catch (IOException e) {
                    UdfPlugin.UTIL.log(e);
                    this.sourceStatus = UdfPlugin.createErrorStatus(PREFIX + "errorReadingImportArchive", this.sourcePath); //$NON-NLS-1$
                } finally {
                    if (zipFile != null) {
                        try {
                            zipFile.close();
                        } catch (IOException e) {
                            UdfPlugin.UTIL.log(e);
                        }
                    }
                }
            }
        }
    }

}
