/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import com.metamatrix.internal.core.index.IIndexConstants;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * This class contains static utilities to load and save resources.
 * @since 3.1
 * @version 3.1
 * @author <a href="mailto:jverhaeg@metamatrix.com">John P. A. Verhaeg</a>
 */
public class IoUtilities {
    
    private static final int DEFAULT_READING_SIZE = IIndexConstants.BLOCK_SIZE;

    //############################################################################################################################
	//# Static Methods                                                                                                           #
	//############################################################################################################################
    
    /**
     * Returns the given file's contents as a byte array.
     */
    public static byte[] getResourceContentsAsByteArray(IFile file) throws IOException, ModelWorkspaceException {
        InputStream stream= null;
        try {
            stream = new BufferedInputStream(file.getContents(true));
        } catch (CoreException e) {
            throw new ModelWorkspaceException(e);
        }
        try {
            return getInputStreamAsByteArray(stream, -1);
        } finally {
                stream.close();
        }
    }
    
    /**
     * Returns the given file's contents as a byte array.
     */
    public static byte[] getResourceContentsAsByteArray(File file) throws IOException {
        InputStream stream= new FileInputStream(file);
        byte[] bytes = getInputStreamAsByteArray(stream, -1);
        stream.close();
        return bytes;
    }    
    
    /**
     * Returns the given input stream's contents as a byte array.
     * If a length is specified (ie. if length != -1), only length bytes
     * are returned. Otherwise all bytes in the stream are returned.
     * Note this doesn't close the stream.
     * @throws IOException if a problem occured reading the stream.
     */
    public static byte[] getInputStreamAsByteArray(InputStream stream, int length)
        throws IOException {
        byte[] contents;
        if (length == -1) {
            contents = new byte[0];
            int contentsLength = 0;
            int amountRead = -1;
            do {
                int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE);  // read at least 8K
                
                // resize contents if needed
                if (contentsLength + amountRequested > contents.length) {
                    System.arraycopy(
                        contents,
                        0,
                        contents = new byte[contentsLength + amountRequested],
                        0,
                        contentsLength);
                }

                // read as many bytes as possible
                amountRead = stream.read(contents, contentsLength, amountRequested);

                if (amountRead > 0) {
                    // remember length of contents
                    contentsLength += amountRead;
                }
            } while (amountRead != -1); 

            // resize contents if necessary
            if (contentsLength < contents.length) {
                System.arraycopy(
                    contents,
                    0,
                    contents = new byte[contentsLength],
                    0,
                    contentsLength);
            }
        } else {
            contents = new byte[length];
            int len = 0;
            int readSize = 0;
            while ((readSize != -1) && (len != length)) {
                // See PR 1FMS89U
                // We record first the read size. In this case len is the actual read size.
                len += readSize;
                readSize = stream.read(contents, len, length - len);
            }
        }

        return contents;
    }
}
