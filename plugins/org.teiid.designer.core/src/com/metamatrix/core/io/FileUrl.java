/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import com.metamatrix.core.util.CoreStringUtil;

/**
 * This class allows for the original URL of a URL object that was used to create a File object to be saved. The File object is
 * created from the URL by saving the InputStream from the URL off to a local file.
 */
public class FileUrl extends File {

    private static final long serialVersionUID = 1L;

    /* (non-Javadoc)
     * @see java.io.File#createTempFile(java.lang.String, java.lang.String, java.io.File)
     */
    public static File createTempFile( final String prefix,
                                       final String suffix ) throws IOException {
        FileUrl fileUrl = null;
        File file = File.createTempFile(prefix, suffix);

        fileUrl = new FileUrl(file.toURI());

        file = null;

        return fileUrl;
    }

    /*
     * This is the original URL of the InputStream that was
     * used to create this File object. 
     */
    private String originalUrlString = CoreStringUtil.Constants.EMPTY_STRING;

    public FileUrl( final String pathname ) {
        super(pathname);
    }

    public FileUrl( final String parent,
                    final String child ) {
        super(parent, child);
    }

    public FileUrl( final URI uri ) {
        super(uri);
    }

    /**
     * @return originalUrlString The original URL used to create this File object
     */
    public String getOriginalUrlString() {
        return originalUrlString;
    }

    /**
     * @param originalUrlString
     */
    public void setOriginalUrlString( final String originalUrlString ) {
        this.originalUrlString = originalUrlString;
    }

}
