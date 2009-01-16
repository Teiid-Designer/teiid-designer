/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.vdb.internal.edit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.ChecksumUtil;
import com.metamatrix.vdb.edit.VdbEditPlugin;

/**
 * VdbFileWriter
 */
public class VdbFileWriter {

    /**
     * Specifies that the VDB file that is to be created should be a JAR file.
     */
    public static final int FORM_JAR = 1;

    /**
     * Specifies that the VDB file that is to be created should be a ZIP file.
     */
    public static final int FORM_ZIP = 2;

    /**
     * The default size of the buffer used when adding files to the VDB file is 4096 bytes.
     */
    public static final int DEFAULT_BUFFER_SIZE = 4096;

    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method indicating that writing the
     * archive was successful with no warnings and errors
     */
    public static final int WRITING_ARCHIVE_SUCCESSFUL = 1001;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method indicating that writing the
     * archive was successful but with 1 or more warnings
     */
    public static final int WRITING_ARCHIVE_WITH_WARNINGS = 1002;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method indicating that writing the
     * archive was successful but with 1 or more errors
     */
    public static final int WRITING_ARCHIVE_WITH_ERRORS = 1003;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method indicating that writing the
     * archive was successful but with errors and warnings
     */
    public static final int WRITING_ARCHIVE_WITH_ERRORS_AND_WARNINGS = 1004;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method
     */
    public static final int UNABLE_TO_ADD_FOLDER_TO_ARCHIVE = 1005;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method
     */
    public static final int UNEXPECTED_EXCEPTION = 1006;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method
     */
    public static final int ERROR_COMPUTING_CHECKSUM = 1007;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method
     */
    public static final int ERROR_CLOSING_CONTENT_STREAM = 1008;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method
     */
    public static final int UNABLE_TO_DELETE_TEMP_CONTENTS_FILE = 1009;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method
     */
    public static final int CONTENTS_NOT_FOUND = 1010;
    /**
     * One of the codes in the status(es) returned by the {@link #write(IProgressMonitor)}method
     */
    public static final int ERROR_WRITING_CONTENTS = 1011;

    private static final String PLUGIN_ID = VdbEditPlugin.PLUGIN_ID;

    protected static final int AMOUNT_OF_WORK_PER_ENTRY = 100;

    private final IPath path;
    private final int bufferSize;
    private final List archiveEntryInfos;
    private ZipOutputStream archiveStream;
    private final int form;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance by specifying the path to the archive file and the form of the archive. The default buffer size is
     * {@link #DEFAULT_BUFFER_SIZE}.
     * 
     * @param pathToArchive the path to the archive file; may not be null
     * @param form the form of the archive; one of {@link #FORM_JAR}or {@link #FORM_ZIP}).
     */
    public VdbFileWriter( final IPath pathToArchive,
                          final int form ) {
        this(pathToArchive, form, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Construct an instance by specifying the path to the archive file, the form of the archive, and the size of the buffer.
     * 
     * @param pathToArchive the path to the archive file; may not be null
     * @param form the form of the archive; one of {@link #FORM_JAR}or {@link #FORM_ZIP}).
     * @param bufferSize the size of the buffer; must be positive
     */
    public VdbFileWriter( final IPath pathToArchive,
                          final int form,
                          final int bufferSize ) {
        ArgCheck.isNotNull(pathToArchive);
        ArgCheck.isPositive(bufferSize);
        argCheckForm(form);

        // Ensure the path references an existing file or doesn't reference an existing folder
        this.path = pathToArchive;
        this.bufferSize = bufferSize;
        this.archiveEntryInfos = new ArrayList();
        this.form = form;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * Helper method to validate archive form value
     */
    public static void argCheckForm( final int form ) {
        if (form != FORM_JAR && form != FORM_ZIP) {
            final Object[] params = new Object[] {new Integer(form)};
            final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.invalid_form", params); //$NON-NLS-1$
            ArgCheck.isTrue(false, msg);
        }
    }

    /**
     * Return the path to the archive file
     * 
     * @return
     * @since 5.0
     */
    public IPath getPath() {
        return this.path;
    }

    /**
     * Open the archive output stream
     * 
     * @throws IOException
     * @since 5.0
     */
    public void open() throws IOException {
        if (this.archiveStream == null) {
            this.archiveStream = openArchiveOutputStream(this.path);
        }
    }

    /**
     * Add an entry to the archive writer
     * 
     * @param pathInArchive the path within the archive for the contents
     * @param contents the file contents to add to the archive
     * @throws IOException
     * @since 5.0
     */
    public void addEntry( final IPath pathInArchive,
                          final File contents ) throws IOException {
        addEntry(pathInArchive, contents, null, false);
    }

    /**
     * Add an entry to the archive writer
     * 
     * @param pathInArchive the path within the archive for the contents
     * @param contents the file contents to add to the archive
     * @param comment the comment to associate with the entry
     * @param isContentsFileTemporary if true the contents will be deleted after it is successfully added to the archive
     * @throws IOException
     * @since 5.0
     */
    public void addEntry( final IPath pathInArchive,
                          final File contents,
                          final String comment,
                          final boolean isContentsFileTemporary ) throws IOException {
        ArgCheck.isNotNull(pathInArchive);
        ArgCheck.isNotNull(contents);
        if (!checkDuplicateEntry(pathInArchive)) {
            final IPath contentsPath = new Path(contents.getCanonicalPath());
            final ArchiveEntryInfo entry = new ArchiveEntryInfo(pathInArchive, contentsPath, comment, isContentsFileTemporary);
            getArchiveEntryInfos().add(entry);
        }
    }

    /**
     * Create the archive file
     * 
     * @param progressMonitor the ProgressMonitor
     * @return
     * @since 5.0
     */
    public IStatus write( final IProgressMonitor progressMonitor ) {
        assertWriterIsOpen();

        // If no progress monitor was specified then create a NullProgressMonitor so
        // that we do not have to check for null everywhere within this method
        final IProgressMonitor monitor = (progressMonitor != null ? progressMonitor : new NullProgressMonitor());

        // Start the monitor
        final Object[] taskParams = new Object[] {this.path};
        final String taskName = VdbEditPlugin.Util.getString("VdbFileWriter.taskName", taskParams); //$NON-NLS-1$
        final int totalWork = (getArchiveEntryInfos().size()) * AMOUNT_OF_WORK_PER_ENTRY * 2;
        monitor.beginTask(taskName, totalWork);

        final List problems = new ArrayList();
        try {
            // Iterate over each of the entry infos ...
            final Iterator iter = getArchiveEntryInfos().iterator();
            while (iter.hasNext()) {
                final ArchiveEntryInfo entryInfo = (ArchiveEntryInfo)iter.next();
                // Find the file referenced by the info ...
                writeToArchive(entryInfo, problems, monitor);
            }
        } catch (Throwable e) {
            final int code = UNEXPECTED_EXCEPTION;
            final Object[] params = new Object[] {this.path};
            final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Unexpected_exception_while_writing_vdb_file", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, code, msg, null);
            problems.add(status);
        } finally {
            // stop the monitor ...
            monitor.done();
        }

        // Convert the problems to the result ...
        return createSingleIStatus((IStatus[])problems.toArray(new IStatus[problems.size()]));
    }

    /**
     * Close the archive output stream
     * 
     * @throws IOException
     * @since 5.0
     */
    public void close() throws IOException {
        if (this.archiveStream != null) {
            this.archiveStream.close();
        }
    }

    // =========================================================================
    // Implementation methods
    // =========================================================================

    protected List getArchiveEntryInfos() {
        return this.archiveEntryInfos;
    }

    protected void assertWriterIsOpen() {
        if (this.archiveStream == null) {
            final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Writer_must_be_open_before_calling"); //$NON-NLS-1$
            throw new IllegalStateException(msg);
        }
    }

    protected void writeToArchive( final ArchiveEntryInfo info,
                                   final List problems,
                                   final IProgressMonitor monitor ) {
        assertWriterIsOpen();

        final File entryContents = info.pathToContents.toFile();
        if (entryContents.isDirectory()) {
            final Object[] params = new Object[] {info.pathToContents, this.path};
            final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Contents_path_is_directory", params); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, UNABLE_TO_ADD_FOLDER_TO_ARCHIVE, msg, null);
            problems.add(status);
            return;
        }

        InputStream stream = null;
        try {
            // Open the stream to the file ...
            try {
                stream = new FileInputStream(entryContents);
            } catch (FileNotFoundException e1) {
                final Object[] params = new Object[] {info.pathToContents, this.path};
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Contents_not_found", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, CONTENTS_NOT_FOUND, msg, e1);
                problems.add(status);
                return;
            }
            stream = new BufferedInputStream(stream);

            // Compute CRC of input stream
            long sizeInBytes = 0;
            CRC32 crc32 = new CRC32();
            try {
                sizeInBytes = ChecksumUtil.computeChecksum(stream, crc32);
            } catch (Exception e) {
                final Object[] params = new Object[] {info.pathToContents, this.path};
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Error_computing_checksum", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, ERROR_COMPUTING_CHECKSUM, msg, e);
                problems.add(status);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    final Object[] params = new Object[] {info.pathToContents, this.path};
                    final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Error_closing_content_stream_for_crc", params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, ERROR_CLOSING_CONTENT_STREAM, msg, e);
                    problems.add(status);
                }
            }

            // Construct the archive entry for this file and set the relevant information ...
            final ZipEntry entry = createArchiveEntry(info.pathInArchive.toString());
            entry.setCrc(crc32.getValue());
            entry.setSize(sizeInBytes);
            entry.setTime(entryContents.lastModified());
            if (info.comment != null && info.comment.trim().length() != 0) {
                entry.setComment(info.comment);
            }

            // Add the work for the first read ...
            monitor.worked(AMOUNT_OF_WORK_PER_ENTRY);

            // Open the stream to the file ...
            try {
                stream = new FileInputStream(entryContents);
            } catch (FileNotFoundException e1) {
                final Object[] params = new Object[] {info.pathToContents, this.path};
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Contents_not_found_for_adding", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, CONTENTS_NOT_FOUND, msg, e1);
                problems.add(status);
                return;
            }
            stream = new BufferedInputStream(stream);

            // Put the contents into the archive stream ...
            final byte[] buffer = new byte[this.bufferSize];
            int n = 0;
            try {
                this.archiveStream.putNextEntry(entry);
                while ((n = stream.read(buffer)) > -1) {
                    this.archiveStream.write(buffer, 0, n);
                }
            } catch (IOException ex) {
                final Object[] params = new Object[] {info.pathToContents, this.path};
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Error_writing_contents", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, ERROR_WRITING_CONTENTS, msg, ex);
                problems.add(status);
            } catch (Throwable ex) {
                final Object[] params = new Object[] {info.pathToContents, this.path};
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Unexpected_error_writing_contents", params); //$NON-NLS-1$
                final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, ERROR_WRITING_CONTENTS, msg, ex);
                problems.add(status);
            } finally {
                try {
                    this.archiveStream.closeEntry();
                } catch (IOException e2) {
                    final Object[] params = new Object[] {info.pathToContents, this.path};
                    final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Error_closing_archive_entry", params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, ERROR_WRITING_CONTENTS, msg, e2);
                    problems.add(status);
                }
            }
            // Let the next finally block close the stream ...

            // Add the work for the second read ...
            monitor.worked(AMOUNT_OF_WORK_PER_ENTRY);

        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    final Object[] params = new Object[] {info.pathToContents, this.path};
                    final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Error_closing_content_stream", params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, ERROR_CLOSING_CONTENT_STREAM, msg, e);
                    problems.add(status);
                }
            }
            // If the file is temporary, then clean it up ...
            if (info.isTemporaryContentsFile) {
                final boolean success = entryContents.delete();
                if (!success) {
                    final Object[] params = new Object[] {info.pathToContents, this.path};
                    final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Unable_to_delete_contents_file", params); //$NON-NLS-1$
                    final IStatus status = new Status(IStatus.WARNING, PLUGIN_ID, UNABLE_TO_DELETE_TEMP_CONTENTS_FILE, msg, null);
                    problems.add(status);
                }
            }
        }
    }

    protected IStatus createSingleIStatus( final IStatus[] problems ) {
        IStatus result = null;
        if (problems.length == 0) {
            final int code = WRITING_ARCHIVE_SUCCESSFUL;
            final Object[] params = new Object[] {this.path};
            final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Writing_vdb_file_completed", params); //$NON-NLS-1$
            result = new Status(IStatus.OK, PLUGIN_ID, code, msg, null);
        } else if (problems.length == 1) {
            result = problems[0];
        } else {
            // There were problems, so determine whether there were warnings and errors ...
            int numErrors = 0;
            int numWarnings = 0;
            for (int i = 0; i < problems.length; i++) {
                if (problems[i].getSeverity() == IStatus.WARNING) {
                    ++numWarnings;
                } else if (problems[i].getSeverity() == IStatus.ERROR) {
                    ++numErrors;
                }
            }

            // Create the final status ...
            if (numWarnings != 0 && numErrors == 0) {
                final Object[] params = new Object[] {new Integer(numWarnings)};
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Writing_vdb_file_completed_with_warnings", params); //$NON-NLS-1$
                result = new MultiStatus(PLUGIN_ID, WRITING_ARCHIVE_WITH_WARNINGS, problems, msg, null);

            } else if (numWarnings == 0 && numErrors != 0) {
                final Object[] params = new Object[] {new Integer(numErrors)};
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Writing_vdb_file_resulted_in_errors", params); //$NON-NLS-1$
                result = new MultiStatus(PLUGIN_ID, WRITING_ARCHIVE_WITH_ERRORS, problems, msg, null);

            } else if (numWarnings != 0 && numErrors != 0) {
                final Object[] params = new Object[] {new Integer(numWarnings), new Integer(numErrors)};
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Writing_vdb_file_resulted_in_warnings_and_errors", params); //$NON-NLS-1$
                result = new MultiStatus(PLUGIN_ID, WRITING_ARCHIVE_WITH_ERRORS_AND_WARNINGS, problems, msg, null);

            } else {
                final String msg = VdbEditPlugin.Util.getString("VdbFileWriter.Writing_vdb_file_completed_with_no_warnings_or_errors"); //$NON-NLS-1$
                result = new MultiStatus(PLUGIN_ID, WRITING_ARCHIVE_SUCCESSFUL, problems, msg, null);
            }
        }
        return result;
    }

    // =========================================================================
    // Override these methods to specialize behavior
    // =========================================================================

    protected ZipOutputStream openArchiveOutputStream( final IPath archiveFilePath ) throws IOException {
        final File archiveFile = new File(archiveFilePath.toOSString());
        if (!archiveFile.exists()) {
            archiveFile.createNewFile();
        }
        OutputStream ostream = new FileOutputStream(archiveFile);
        ostream = new BufferedOutputStream(ostream);
        if (this.form == FORM_ZIP) {
            return new ZipOutputStream(ostream);
        }
        final Manifest manifest = new Manifest();
        final Attributes attribs = manifest.getMainAttributes();
        attribs.put(Attributes.Name.MANIFEST_VERSION, "1.0"); //$NON-NLS-1$
        return new JarOutputStream(ostream, manifest);
    }

    protected ZipEntry createArchiveEntry( final String name ) {
        if (this.form == FORM_ZIP) {
            return new ZipEntry(name);
        }
        return new JarEntry(name);
    }

    protected boolean checkDuplicateEntry( final IPath pathInArchive ) {
        if (getArchiveEntryInfos() != null) {
            for (final Iterator i = getArchiveEntryInfos().iterator(); i.hasNext();) {
                ArchiveEntryInfo entryInfo = (ArchiveEntryInfo)i.next();
                if (entryInfo.pathInArchive.equals(pathInArchive)) {
                    return true;
                }
                IPath potentialPath = pathInArchive.makeAbsolute();
                IPath existingPath = entryInfo.pathInArchive.makeAbsolute();
                if (existingPath.equals(potentialPath)) {
                    return true;
                }
                potentialPath = pathInArchive.makeRelative();
                existingPath = entryInfo.pathInArchive.makeRelative();
                if (existingPath.equals(potentialPath)) {
                    return true;
                }
            }
        }

        return false;
    }

    // ==================================================================================
    // I N N E R C L A S S
    // ==================================================================================

    protected class ArchiveEntryInfo {

        protected final IPath pathInArchive;
        protected final IPath pathToContents;
        protected final String comment;
        protected final boolean isTemporaryContentsFile;

        protected ArchiveEntryInfo( final IPath pathInArchive,
                                    final IPath pathToContents,
                                    final String comment,
                                    final boolean tempContents ) {
            this.pathInArchive = pathInArchive;
            this.pathToContents = pathToContents;
            this.comment = comment;
            this.isTemporaryContentsFile = tempContents;
        }
    }

}
