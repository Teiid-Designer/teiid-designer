/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.reader;

import java.io.InputStream;

/**
 *
 */
public interface ZipReaderCallback {

    /**
     * Process the given {@link InputStream}.
     *
     * It is not necessary to close the stream since the parent reader owns the stream.
     *
     * @param inputStream
     *
     * @throws Exception
     */
    void process(InputStream inputStream) throws Exception;

}
