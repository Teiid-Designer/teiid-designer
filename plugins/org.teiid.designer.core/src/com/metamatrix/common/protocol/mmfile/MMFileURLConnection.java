/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.protocol.mmfile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;
import com.metamatrix.common.protocol.MMURLConnection;
import com.metamatrix.common.protocol.URLHelper;

/**
 * Metamatrix's own implementation of the "file:" URL handler. The reason for a different handler is to support the "output stream"
 * as the sun supplied one does not handle writing to it. Strings are not externalized because of the fact that we have huge
 * dependencies with our plugin stuff to eclipse.
 * 
 * @since 4.4
 */
public class MMFileURLConnection extends MMURLConnection {
    public static String PROTOCOL = "mmfile"; //$NON-NLS-1$
    File file;
    File deleted;
    public static String DELETED = ".deleted"; //$NON-NLS-1$
    boolean readOnly = false;

    /**
     * @param u - URL to open the connection to
     */
    public MMFileURLConnection( final URL u ) {
        this(u, false);
    }

    public MMFileURLConnection( final URL u,
                                final boolean readOnly ) {
        super(u);

        final String path = url.getPath();
        file = new File(path.replace('/', File.separatorChar).replace('|', ':'));
        deleted = new File(file.getAbsolutePath() + DELETED);
        doOutput = false;
        this.readOnly = readOnly;
    }

    /**
     * Marks that connected
     */
    @Override
    public void connect() throws IOException {
        connected = true;

        final String action = getAction();
        if (action.equals(READ) || action.equals(LIST)) {
            if (!file.exists()) throw new FileNotFoundException(file.getPath());

            // we know original file exists, but check if there is any .deleted file
            // which also says that this file is no longer accessible.
            if (deleted.exists()) throw new FileNotFoundException(file.getPath());

            doOutput = false;
            doInput = true;
        } else if (action.equals(WRITE)) {
            if (!this.readOnly) if (!file.exists() || (file.exists() && deleted.exists())) {
                // if there is deleted file remove it first.
                deleted.delete();

                // now write the file.
                final File parent = file.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();
            }
            doOutput = true;
            doInput = false;
        } else if (action.equals(DELETE)) if (!this.readOnly) if (file.exists()) {
            file.delete();

            if (file.exists()) try {
                final FileWriter fw = new FileWriter(deleted);
                fw.write("failed to delete file:\"" + url + "\"; this is marker to note this file has been deleted"); //$NON-NLS-1$ //$NON-NLS-2$
                fw.close();
            } catch (final IOException e) {
                throw new IOException("failed to delete file:" + url); //$NON-NLS-1$
            }
        }
    }

    /**
     * Returns the underlying file for this connection.
     */
    public File getFile() {
        return file;
    }

    /**
     * Provides support for returning the value for the <tt>last-modified</tt> header.
     */
    @Override
    public String getHeaderField( final String name ) {
        String headerField = null;
        if (name.equalsIgnoreCase("last-modified")) //$NON-NLS-1$
        headerField = String.valueOf(getLastModified());
        else if (name.equalsIgnoreCase("content-length")) //$NON-NLS-1$
        headerField = String.valueOf(file.length());
        else if (name.equalsIgnoreCase("content-type")) { //$NON-NLS-1$
            headerField = getFileNameMap().getContentTypeFor(file.getName());
            if (headerField == null) try {
                final InputStream is = getInputStream();
                final BufferedInputStream bis = new BufferedInputStream(is);
                headerField = URLConnection.guessContentTypeFromStream(bis);
                bis.close();
            } catch (final IOException e) {
            }
        } else if (name.equalsIgnoreCase(DATE)) headerField = String.valueOf(file.lastModified());
        else // This always returns null currently
        headerField = super.getHeaderField(name);
        return headerField;
    }

    /**
     * @see java.net.URLConnection#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!connected) connect();

        // If the action was to read
        if (getAction().equals(LIST)) {
            // Construct a filter;
            final String filter = getProperties().getProperty("filter"); //$NON-NLS-1$                
            final FileFilter fileFilter = new FileFilter() {

                public boolean accept( final File pathname ) {
                    final StringTokenizer st = new StringTokenizer(filter, ","); //$NON-NLS-1$
                    while (st.hasMoreTokens()) {
                        final String token = st.nextToken().trim().toLowerCase();
                        if (pathname.getPath().toLowerCase().endsWith(token)) return true;
                    }
                    return false;
                }
            };

            final File[] matchedFiles = file.listFiles(fileFilter);
            final String[] urls = new String[matchedFiles.length];

            final String sort = getProperties().getProperty(FILE_LIST_SORT, DATE);
            if (sort.equals(DATE)) Arrays.sort(matchedFiles, new Comparator<File>() {
                // ## JDBC4.0-begin ##
                @Override
                // ## JDBC4.0-end ##
                public int compare( final File o1,
                                    final File o2 ) {
                    return Long.valueOf(o2.lastModified()).compareTo(o1.lastModified()); // latest first.
                }
            });
            else if (sort.equals(ALPHA)) Arrays.sort(matchedFiles, new Comparator<File>() {
                // ## JDBC4.0-begin ##
                @Override
                // ## JDBC4.0-end ##
                public int compare( final File o1,
                                    final File o2 ) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            else if (sort.equals(REVERSEALPHA)) Arrays.sort(matchedFiles, new Comparator<File>() {
                // ## JDBC4.0-begin ##
                @Override
                // ## JDBC4.0-end ##
                public int compare( final File o1,
                                    final File o2 ) {
                    return o2.getName().compareTo(o1.getName());
                }
            });

            for (int i = 0; i < matchedFiles.length; i++)
                urls[i] = URLHelper.buildURL(url, matchedFiles[i].getName()).toString();

            // Build input stream from the object
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(urls);
            oos.close();
            final byte[] content = out.toByteArray();
            out.close();
            return new ByteArrayInputStream(content);
        }

        // make sure we only return the stream for non-deleted files
        if (!deleted.exists() && file.exists()) return new FileInputStream(file);
        return null;
    }

    /**
     * Returns the last modified time of the underlying file.
     */
    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    /**
     * @see java.net.URLConnection#getOutputStream()
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!connected) connect();
        if (getAction().equals(WRITE)) {

            // make sure we are not read only, if it is serve dummy stream
            if (this.readOnly) return new NullOutputStream();

            // this not readonly, so go ahead write..
            final SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                // Check for write access
                final FilePermission p = new FilePermission(file.getPath(), "write"); //$NON-NLS-1$
                sm.checkPermission(p);
            }
            return new FileOutputStream(file);
        }
        throw new IOException("Writing to the file \"" + url + "\" is not allowed"); //$NON-NLS-1$ //$NON-NLS-2$        
    }

    /**
     * Return a permission for reading of the file
     */
    @Override
    public Permission getPermission() {
        return new FilePermission(file.getPath(), "read"); //$NON-NLS-1$
    }

    /**
     * a no land output stream..
     */
    static class NullOutputStream extends OutputStream {
        @Override
        public void write( final int b ) {
            // ha ha I do nothing..
        }
    }
}
