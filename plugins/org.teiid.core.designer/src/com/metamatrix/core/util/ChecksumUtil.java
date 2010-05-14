/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * This utility class provides mechanisms for computing the checksum.
 */
public class ChecksumUtil {

    protected static final int BUFFER_SIZE = 1024;

    /**
     * Compute and return the checksum (using the default CRC-32 algorithm) of the contents on the specified stream. This method
     * closes the stream upon completion.
     * 
     * @param stream the stream containing the contents for which the checksum is to be computed; may not be null
     * @return the Checksum for the contents
     * @throws AssertionError if <code>stream</code> is null
     * @throws IOException if there is an error reading the stream
     */
    public static Checksum computeChecksum( InputStream stream ) throws IOException {
        Checksum checksum = new CRC32();
        computeChecksum(stream, checksum);
        return checksum;
    }

    /**
     * Compute the checksum of the contents on the specified stream using the supplied Checksum algorithm, and modify that
     * Checksum instance with the checksum value. This method closes the stream upon completion.
     * 
     * @param stream the stream containing the contents for which the checksum is to be computed; may not be null
     * @param algorithm the checksum algorithm to be used.
     * @return the number of bytes from <code>stream</code> that were processed
     * @throws AssertionError if <code>stream</code> or <code>algorithm</code> is null
     * @throws IOException if there is an error reading the stream
     */
    public static long computeChecksum( InputStream stream,
                                        Checksum algorithm ) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        long sizeInBytes = 0;

        // Compute the checksum ...
        IOException ioe = null;
        try {
            while ((n = stream.read(buffer)) > -1) {
                algorithm.update(buffer, 0, n);
                sizeInBytes += n;
            }
        } catch (IOException e) {
            ioe = e;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // Throw this only if there was no IOException from processing above
                if (ioe == null) {
                    ioe = e;
                }
            }
        }
        if (ioe != null) {
            throw ioe;
        }
        return sizeInBytes;
    }

}
