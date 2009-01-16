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

package com.metamatrix.modeler.internal.ui.wizards;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * Exports resources to a .zip file
 */
public class ModelerZipExporter {

    private ZipOutputStream outputStream;
    private boolean useCompression = true;

    // constants

    /**
     * Create an instance of this class.
     * 
     * @param filename
     *            java.lang.String
     * @param compress
     *            boolean
     * @param includeManifestFile
     *            boolean
     * @exception java.io.IOException
     */
    public ModelerZipExporter(String filename,
                              boolean compress) throws IOException {
        outputStream = new ZipOutputStream(new FileOutputStream(filename));
        useCompression = compress;
    }


    /**
     * Do all required cleanup now that we're finished with the currently-open .zip
     * 
     * @exception java.io.IOException
     */
    public void finished() throws IOException {
        outputStream.close();
    }

    /**
     * Create a new ZipEntry with the passed pathname and contents, and write it to the current archive
     * 
     * @param pathname
     *            java.lang.String
     * @param contents
     *            byte[]
     * @exception java.io.IOException
     */
    protected void write(String pathname,
                         byte[] contents) throws IOException {
        ZipEntry newEntry = new ZipEntry(pathname);

        // if the contents are being compressed then we get the below for free.
        if (!useCompression) {
            newEntry.setMethod(ZipEntry.STORED);
            newEntry.setSize(contents.length);
            CRC32 checksumCalculator = new CRC32();
            checksumCalculator.update(contents);
            newEntry.setCrc(checksumCalculator.getValue());
        }

        outputStream.putNextEntry(newEntry);
        outputStream.write(contents);
        outputStream.closeEntry();
    }

    /**
     * Write the passed resource to the current archive
     * 
     * @param resource
     *            org.eclipse.core.resources.IFile
     * @param destinationPath
     *            java.lang.String
     * @exception java.io.IOException
     * @exception org.eclipse.core.runtime.CoreException
     */
    public void write(IFile resource,
                      String destinationPath) throws IOException,
                                             CoreException {
        ByteArrayOutputStream output = null;
        InputStream contentStream = null;

        try {
            output = new ByteArrayOutputStream();
            contentStream = resource.getContents(false);
            int chunkSize = contentStream.available();
            byte[] readBuffer = new byte[chunkSize];
            int n = contentStream.read(readBuffer);

            while (n > 0) {
                output.write(readBuffer);
                n = contentStream.read(readBuffer);
            }
        } finally {
            if (output != null)
                output.close();
            if (contentStream != null)
                contentStream.close();
        }

        write(destinationPath, output.toByteArray());
    }

}

