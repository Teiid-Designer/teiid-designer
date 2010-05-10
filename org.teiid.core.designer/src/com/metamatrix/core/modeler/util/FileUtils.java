/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.modeler.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.modeler.util.OperationUtil.Unreliable;
import com.metamatrix.core.util.ChecksumUtil;
import com.metamatrix.core.util.CoreArgCheck;

public class FileUtils {

    public static int DEFAULT_BUFFER_SIZE = 8092;
    public static String TEMP_DIRECTORY;

    public final static String JAVA_IO_TEMP_DIR = "java.io.tmpdir";//$NON-NLS-1$

    private static final String TEMP_FILE = "delete.me"; //$NON-NLS-1$
    private static final String TEMP_FILE_RENAMED = "delete.me.old"; //$NON-NLS-1$

    static {
        final String tempDirPath = System.getProperty(JAVA_IO_TEMP_DIR);
        TEMP_DIRECTORY = (tempDirPath.endsWith(File.separator) ? tempDirPath : tempDirPath + File.separator);
    }

    /**
     * Copy a file. Overwrites the destination file if it exists.
     * 
     * @param fromFileName
     * @param toFileName
     * @throws Exception
     * @since 4.3
     */
    public static File copy( final File fromFile,
                             final File destDirectory,
                             final boolean overwrite ) throws IOException {
        final File toFile = new File(destDirectory, fromFile.getName());

        if (toFile.exists()) if (overwrite) toFile.delete();
        else {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.File_already_exists", toFile.getName()); //$NON-NLS-1$            
            throw new IOException(msg);
        }

        if (!fromFile.exists()) throw new FileNotFoundException(
                                                                CoreModelerPlugin.Util.getString("FileUtils.File_does_not_exist._1", fromFile.getName())); //$NON-NLS-1$

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fromFile);
            write(fis, toFile);
        } finally {
            if (fis != null) fis.close();
        }
        return toFile;
    }

    public static void copy( final File source,
                             final OutputStream destination ) {
        OperationUtil.perform(new UnreliableCopy(source, destination));
    }

    public static void copy( final InputStream source,
                             final File destination ) {
        OperationUtil.perform(new UnreliableCopy(source, destination));
    }

    public static void copy( final InputStream source,
                             final OutputStream destination ) {
        try {
            final byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
            for (int len = source.read(buf); len >= 0; len = source.read(buf))
                destination.write(buf, 0, len);
        } catch (final IOException error) {
            CoreModelerPlugin.throwRuntimeException(error);
        }
    }

    /**
     * Copy a file. Overwrites the destination file if it exists.
     * 
     * @param fromFileName
     * @param toFileName
     * @throws Exception
     * @since 4.3
     */
    public static void copy( final String fromFileName,
                             final String toFileName ) throws IOException {
        copy(fromFileName, toFileName, true);
    }

    /**
     * Copy a file
     * 
     * @param fromFileName
     * @param toFileName
     * @param overwrite whether to overwrite the destination file if it exists.
     * @throws MetaMatrixCoreException
     * @since 4.3
     */
    public static void copy( final String fromFileName,
                             final String toFileName,
                             final boolean overwrite ) throws IOException {
        final File toFile = new File(toFileName);

        if (toFile.exists()) if (overwrite) toFile.delete();
        else {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.File_already_exists", toFileName); //$NON-NLS-1$            
            throw new IOException(msg);
        }

        final File fromFile = new File(fromFileName);
        if (!fromFile.exists()) throw new FileNotFoundException(
                                                                CoreModelerPlugin.Util.getString("FileUtils.File_does_not_exist._1", //$NON-NLS-1$
                                                                                                 fromFileName));

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fromFile);
            write(fis, toFileName);
        } finally {
            if (fis != null) fis.close();
        }
    }

    /**
     * Copy recursively the <code>sourceDirectory</code> and all its contents to the <code>targetDirectory</code>. If
     * <code>targetDirectory</code> does not exist, it will be created.
     * 
     * @param sourceDirectory The source directory to copy
     * @param targetDirectory The target directory to copy to
     * @throws Exception If the source directory does not exist.
     * @since 4.3
     */
    public static void copyDirectoriesRecursively( final File sourceDirectory,
                                                   final File targetDirectory,
                                                   final FilenameFilter filter ) throws Exception {
        copyRecursively(sourceDirectory, targetDirectory, filter, true);
    }

    /**
     * Copy file from originating directory to the destination directory.
     * 
     * @param orginDirectory
     * @param destDirectory
     * @param fileName
     * @throws Exception
     * @since 4.4
     */
    public static void copyFile( final String orginDirectory,
                                 final String destDirectory,
                                 final String fileName ) throws Exception {

        copyFile(orginDirectory, fileName, destDirectory, fileName);
    }

    /**
     * Copy file from originating directory to the destination directory.
     * 
     * @param orginDirectory
     * @param orginFileName
     * @param destDirectory
     * @param destFileName
     * @throws Exception
     * @since 4.4
     */
    public static void copyFile( final String orginDirectory,
                                 final String orginFileName,
                                 final String destDirectory,
                                 final String destFileName ) throws Exception {

        FileUtils.copy(orginDirectory + File.separator + orginFileName, destDirectory + File.separator + destFileName);
    }

    /**
     * Copy recursively from the <code>sourceDirectory</code> all its contents to the <code>targetDirectory</code>. if
     * <code>includeSourceRoot<code> == <code>true</code>, copy <code>sourceDirectory</code> itself, else only copy
     * <code>sourceDirectory</code>'s contents. If <code>targetDirectory</code> does not exist, it will be created.
     * 
     * @param sourceDirectory
     * @param targetDirectory
     * @param filteredFiles - files which should not be copied
     * @param includeSourceRoot
     * @throws FileNotFoundException
     * @throws Exception
     * @since 4.3
     */
    public static void copyRecursively( final File sourceDirectory,
                                        final File targetDirectory,
                                        final FilenameFilter filter,
                                        final boolean includeSourceRoot ) throws FileNotFoundException, Exception {
        if (!sourceDirectory.exists()) throw new FileNotFoundException(
                                                                       CoreModelerPlugin.Util.getString("FileUtils.File_does_not_exist._1", //$NON-NLS-1$
                                                                                                        sourceDirectory));

        if (!sourceDirectory.isDirectory()) throw new FileNotFoundException(
                                                                            CoreModelerPlugin.Util.getString("FileUtils.Not_a_directory", //$NON-NLS-1$
                                                                                                             sourceDirectory));

        File targetDir = new File(targetDirectory.getAbsolutePath() + File.separatorChar + sourceDirectory.getName());
        if (includeSourceRoot) // copy source directory
        targetDir.mkdir();
        else // copy only source directory contents
        targetDir = new File(targetDirectory.getAbsolutePath() + File.separatorChar);

        File[] sourceFiles = null;
        if (filter != null) sourceFiles = sourceDirectory.listFiles(filter);
        else sourceFiles = sourceDirectory.listFiles();

        for (final File srcFile : sourceFiles)
            if (srcFile.isDirectory()) {
                final File childTargetDir = new File(targetDir.getAbsolutePath());
                copyRecursively(srcFile, childTargetDir, filter, true);
            } else copy(srcFile.getAbsolutePath(), targetDir.getAbsolutePath() + File.separatorChar + srcFile.getName());
    }

    /**
     * Compute checksum for the given file.
     * 
     * @param f The file for which checksum needs to be computed
     * @return The checksum
     * @since 4.3
     */
    public static long getCheckSum( final File f ) throws Exception {
        CoreArgCheck.isNotNull(f);
        FileInputStream is = null;
        try {
            is = new FileInputStream(f);
            return ChecksumUtil.computeChecksum(is).getValue();
        } finally {
            if (is != null) try {
                is.close();
            } catch (final IOException err1) {
            }
        }
    }

    /**
     * @param string
     * @return
     */
    public static String getFilenameWithoutExtension( final String filename ) {
        if (filename == null || filename.length() == 0) return filename;
        final int extensionIndex = filename.lastIndexOf('.');
        if (extensionIndex == -1) return filename; // not found
        if (extensionIndex == 0) return ""; //$NON-NLS-1$
        return filename.substring(0, extensionIndex);
    }

    public static String normalizeFileName( final String theFileName ) {
        if (theFileName == null) return null;
        if (theFileName.length() == 0) return theFileName;

        try {
            return URLDecoder.decode(theFileName, "UTF-8"); //$NON-NLS-1$
        } catch (final UnsupportedEncodingException e) {
            return theFileName;
        }
    }

    public static void removeChildrenRecursively( final File directory ) {
        final File[] files = directory.listFiles();
        if (files != null) for (final File file2 : files) {
            final File file = file2;
            if (file.isDirectory()) removeDirectoryAndChildren(file);
            else if (!file.delete()) file.deleteOnExit();
        }
    }

    public static void removeDirectoryAndChildren( final File directory ) {
        removeChildrenRecursively(directory);
        if (!directory.delete()) directory.deleteOnExit();
    }

    /**
     * Test whether it's possible to read and write files in the specified directory.
     * 
     * @param dirPath Name of the directory to test
     * @throws MetaMatrixCoreException
     * @since 4.3
     */
    public static void testDirectoryPermissions( final String dirPath ) throws MetaMatrixCoreException {

        // try to create a file
        final File tmpFile = new File(dirPath + File.separatorChar + TEMP_FILE);
        boolean success = false;
        try {
            success = tmpFile.createNewFile();
        } catch (final IOException e) {
        }
        if (!success) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_create_file_in", dirPath); //$NON-NLS-1$            
            throw new MetaMatrixCoreException(msg);
        }

        // test if file can be written to
        if (!tmpFile.canWrite()) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_write_file_in", dirPath); //$NON-NLS-1$            
            throw new MetaMatrixCoreException(msg);
        }

        // test if file can be read
        if (!tmpFile.canRead()) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_read_file_in", dirPath); //$NON-NLS-1$            
            throw new MetaMatrixCoreException(msg);
        }

        // test if file can be renamed
        final File newFile = new File(dirPath + File.separatorChar + TEMP_FILE_RENAMED);
        success = false;
        try {
            success = tmpFile.renameTo(newFile);
        } catch (final Exception e) {
        }
        if (!success) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_rename_file_in", dirPath); //$NON-NLS-1$            
            throw new MetaMatrixCoreException(msg);
        }

        // test if file can be deleted
        success = false;
        try {
            success = newFile.delete();
        } catch (final Exception e) {
        }
        if (!success) {
            final String msg = CoreModelerPlugin.Util.getString("FileUtils.Unable_to_delete_file_in", dirPath); //$NON-NLS-1$            
            throw new MetaMatrixCoreException(msg);
        }
    }

    /**
     * Write an InputStream to a file.
     */
    public static void write( final InputStream is,
                              final File f ) throws IOException {
        write(is, f, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Write an InputStream to a file.
     */
    public static void write( final InputStream is,
                              final File f,
                              final int bufferSize ) throws IOException {
        f.delete();
        final File parentDir = f.getParentFile();
        if (parentDir != null) parentDir.mkdirs();

        FileOutputStream fio = null;
        BufferedOutputStream bos = null;
        try {
            fio = new FileOutputStream(f);
            bos = new BufferedOutputStream(fio);
            if (bufferSize > 0) {
                final byte[] buff = new byte[bufferSize];
                int bytesRead;

                // Simple read/write loop.
                while (-1 != (bytesRead = is.read(buff, 0, buff.length)))
                    bos.write(buff, 0, bytesRead);
            }
            bos.flush();
        } finally {
            if (bos != null) bos.close();
            if (fio != null) fio.close();
        }
    }

    /**
     * Write an InputStream to a file.
     */
    public static void write( final InputStream is,
                              final String fileName ) throws IOException {
        final File f = new File(fileName);
        write(is, f);
    }

    private FileUtils() {
    }

    static class UnreliableCopy implements Unreliable {

        private File sourceFile;
        private InputStream source;
        private File destinationFile;
        private OutputStream destination;

        UnreliableCopy( final File source,
                        final OutputStream destination ) {
            sourceFile = source;
            this.destination = destination;
        }

        UnreliableCopy( final InputStream source,
                        final File destination ) {
            this.source = source;
            destinationFile = destination;
        }

        @Override
        public void doIfFails() {
        }

        @Override
        public void finallyDo() throws Exception {
            if (destinationFile != null) destination.close();
            if (sourceFile != null) source.close();
        }

        @Override
        public void tryToDo() throws Exception {
            if (source == null) source = new FileInputStream(sourceFile);
            if (destination == null) {
                final File folder = destinationFile.getParentFile();
                if (folder != null) folder.mkdirs();
                destination = new FileOutputStream(destinationFile);
            }
            copy(source, destination);
        }
    }
}
