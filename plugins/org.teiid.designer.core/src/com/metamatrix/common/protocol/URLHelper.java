/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.common.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.teiid.core.util.Base64;

import com.metamatrix.core.io.FileUrl;

/**
 * Helper class to build the URL objects from the strings. Since as an application we do not know if we are embedded or we are in
 * our own server, we can not install the "URLStreamHandlers" in the VM, as they can be only installed once per VM, as an
 * alternative, the stream handler must be specified at the time URL it is constructed. This class will help us to this code at one
 * place. Here inspect the given string and build the correct type of URL with correct handler.
 * 
 * @since 4.4
 */
public class URLHelper {

    /**
     * Construct the URL based on the String
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     * @since 4.4
     */
    public static URL buildURL( String url ) throws MalformedURLException {

        if (url == null) throw new MalformedURLException();

        url = convertBackSlashes(url);

        final String filename = extractFileName(url);
        if (filename != null) {
            return new File(url).toURI().toURL();
        }
        return new URL(url);
    }

    public static URL buildURL( final URL url ) {
        try {
            return buildURL(url.toExternalForm());
        } catch (final MalformedURLException e) {
            // since it came as url it should not have any issues with this
        }
        return null;
    }

    static String convertBackSlashes( final String str ) {
        return str.replaceAll("\\\\", "/"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * Given an {@link URLConnection}, set its authorization property using the 
     * given username and password.
     * 
     * @param urlConn
     * @param userName
     * @param password
     */
    public static void setCredentials(URLConnection urlConn, String userName, String password) {
        if (userName == null || password == null)
            return;
        
        if (! (urlConn instanceof HttpURLConnection)) {
            // Should not be authenticating on other types of url connection
            return;
        }
        
        urlConn.setRequestProperty("Authorization", "Basic " + Base64.encodeBytes((userName + ':' + password).getBytes())); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Build a {@link java.io.File} from a {@link java.net.URL} object.
     * 
     * @param url
     * @param fileNamePrefix
     * @param fileNameSuffix
     * @return File
     * @since 5.1
     */
    public static File createFileFromUrl( final URL url,
                                          final String fileNamePrefix,
                                          final String fileNameSuffix ) throws MalformedURLException, IOException {

        return createFileFromUrlInternal(url, FileUrl.createTempFile(fileNamePrefix, fileNameSuffix), null, null, true);
    }
    
    public static File createFileFromUrl (final URL url,
                                          final String fileNamePrefix,
                                          final String fileNameSuffix,
                                          final String userName,
                                          final String password) throws MalformedURLException, IOException {

        return createFileFromUrl(url, FileUrl.createTempFile(fileNamePrefix, fileNameSuffix).getAbsolutePath(), userName, password, true);
    }

    /**
     * Download the content from the given URL and save it into the specified file.
     * 
     * @param url URL of the file to be saved
     * @param filePath the full path of the file name
     * @param userName user name if authentication is required
     * @param password password if authentication is required
     * @param verifyHostname whether to verify hostname for HTTPS connection
     * @return the file created
     * @throws MalformedURLException
     * @throws IOException
     * @since 5.5
     */
    public static File createFileFromUrl( final URL url,
                                          final String filePath,
                                          final String userName,
                                          final String password,
                                          final boolean verifyHostname ) throws MalformedURLException, IOException {
        File file = null;
        final String tempDir = System.getProperty("java.io.tmpdir");//$NON-NLS-1$
        if (filePath.indexOf("/") != -1 || filePath.indexOf("\\") != -1) {//$NON-NLS-1$//$NON-NLS-2$

            int lastPart = filePath.lastIndexOf("/");//$NON-NLS-1$
            if (lastPart == -1) lastPart = filePath.lastIndexOf("\\");//$NON-NLS-1$
            final String relativeDir = filePath.substring(0, lastPart);
            final File dir = new File(new File(tempDir), relativeDir);
            if (!dir.exists()) dir.mkdirs();
            file = new File(dir, filePath.substring(lastPart + 1));
        } else file = new File(new File(tempDir), filePath);
        return createFileFromUrlInternal(url, new FileUrl(file.toURI()), userName, password, verifyHostname);
    }

    private static File createFileFromUrlInternal( final URL url,
                                                   final File file,
                                                   final String userName,
                                                   final String password,
                                                   final boolean verifyHostname ) throws MalformedURLException, IOException {
        String nextLine;
        URLConnection urlConn = null;
        InputStreamReader inStream = null;
        BufferedReader buff = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            file.deleteOnExit();
            ((FileUrl)file).setOriginalUrlString(url.toString());
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            urlConn = url.openConnection();
            setCredentials(urlConn, userName, password);
            if (!verifyHostname && urlConn instanceof HttpsURLConnection) ((HttpsURLConnection)urlConn).setHostnameVerifier(new HostnameVerifier() {
                public boolean verify( final String arg,
                                       final SSLSession session ) {
                    return true;
                }
            });
            inStream = new InputStreamReader(urlConn.getInputStream());
            buff = new BufferedReader(inStream);

            // Read and print the lines from index.html
            while (true) {
                nextLine = buff.readLine();
                if (nextLine != null) bw.write(nextLine);
                else break;
            }
        } finally {
            if (inStream != null) inStream.close();
            if (bw != null) bw.close();
        }

        return file;
    }

    static String extractFileName( String file ) {

        if (file.matches("^(\\w){2,}:.*")) // Handles URLs - No conversion necessary //$NON-NLS-1$
        // http://lib/foo.txt - currently do not support, converts to local
        // host with absolute path
        // file://lib/foo.txt
        // file:///c:/lib/foo.txt
        return null;
        else if (file.matches("^\\/.*")) // Handles absolute paths- it can be file or URL depending upon //$NON-NLS-1$
        // context Conversion needed
        // /lib/foo.txt
        return file;
        else if (file.matches("^\\w:[\\\\,\\/].*")) { //$NON-NLS-1$
            // Handles windows absolute path - no conversion needed
            // c:\\lib\\foo.txt
            // c:/lib.foo.txt
            file = file.replaceAll("\\\\", "\\/"); //$NON-NLS-1$ //$NON-NLS-2$
            return "/" + file; //$NON-NLS-1$
        } else if (file.matches("^(\\.)+\\/.*|^\\w+\\/.*|^\\w+.*")) // Handles relative paths - these can be URLs or files - //$NON-NLS-1$
        // conversion necessary
        // ./lib/foo.txt
        // ../lib/foo.txt
        // lib/foo.txt
        return file;
        return null;
    }

    /**
     * Determines whether a URL object resolves to a valid url. This will work for any protocol (file, HTTP, etc.).
     * 
     * @param url
     * @return resolved boolean
     * @throws MalformedURLException, IOException
     * @since 5.1
     */
    public static boolean resolveUrl( final URL url ) throws MalformedURLException, IOException {
        return resolveUrl(url, true);
    }

    /**
     * Determines whether a URL object resolves to a valid url. This will work for any protocol (file, HTTP, etc.).
     * 
     * @param url
     * @param verifyHostname whether to verify hostname for HTTPS connection
     * @return resolved boolean
     * @throws MalformedURLException, IOException
     * @since 5.5
     */
    public static boolean resolveUrl( final URL url,
                                      final boolean verifyHostname ) throws MalformedURLException, IOException {
        return resolveUrl(url, null, null, verifyHostname);
    }

    /**
     * Determines whether a URL object resolves to a valid url. This will work for any protocol (file, HTTP, etc.).
     * 
     * @param url
     * @param userName
     * @param password
     * @param verifyHostname whether to verify hostname for HTTPS connection
     * @return resolved boolean
     * @throws MalformedURLException, IOException
     * @since 5.1
     */
    public static boolean resolveUrl( final URL url,
                                      final String userName,
                                      final String password,
                                      final boolean verifyHostname ) throws MalformedURLException, IOException {
        boolean resolved = true;
        if (url == null) return resolved;
        String nextLine;
        URLConnection urlConn = null;
        InputStreamReader inStream = null;
        BufferedReader buff = null;
        // Add a time-out here....
        final long timeOut = 30000;

        final long startTime = System.currentTimeMillis();
        long deltaTime = 0;
        try {
            urlConn = url.openConnection();
            setCredentials(urlConn, userName, password);
            if (!verifyHostname && urlConn instanceof HttpsURLConnection) ((HttpsURLConnection)urlConn).setHostnameVerifier(new HostnameVerifier() {
                public boolean verify( final String arg,
                                       final SSLSession session ) {
                    return true;
                }
            });
            
            inStream = new InputStreamReader(urlConn.getInputStream());
            buff = new BufferedReader(inStream);
            boolean keepReading = true;
            // Read and print the lines from index.html
            while (keepReading) {
                nextLine = buff.readLine();
                if (nextLine != null) {

                } else break;
                deltaTime = System.currentTimeMillis() - startTime;
                if (deltaTime > timeOut) {
                    keepReading = false;
                    resolved = false;
                }
            }
        } finally {
            if (inStream != null) inStream.close();
        }

        return resolved;
    }
}
