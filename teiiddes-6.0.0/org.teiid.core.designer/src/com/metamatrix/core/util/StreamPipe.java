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
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import com.metamatrix.core.MetaMatrixRuntimeException;

/**
 * The StreamPipe class provides the ability for an information consumer to read via an
 * {@link InputStream} information that is being supplied by another component through an 
 * {@link OutputStream}.  To use, this class is simply instantiated, the 
 * {@link #getOutputStream() output stream} is given to the supplier, and the
 * {@link #getInputStream() input stream} is given to the consumer.  The
 * read methods of the {@link InputStream) block until there is something to read
 * or until the end of the stream is reached.
 * <p>
 * The reader can cancel the operation by closing its {@link getInputStream() InputStream}.  When this
 * occurs while the writer is still supplying information to its {@link getOutputStream() OutputStream},
 * the writer receives an {@link java.io.IOException}.
 * </p>
 * <p>
 * Similarly, the writer can terminate the operation by closing its {@link getOutputStream() OutputStream}.
 * When this occurs, the reader simply sees the termination as a normal termination of the stream.
 * </p>
 */
public class StreamPipe {
    
    private PipedInputStream istream;
    private PipedOutputStream ostream;

    /**
     * Construct an instance of StreamPipe.
     */
    public StreamPipe() {
        this.istream = new PipedInputStream();
        try {
			this.ostream = new PipedOutputStream(istream);
		} catch (IOException e) {
			throw new MetaMatrixRuntimeException(e);
		}
    }
    
    /**
     * Obtain the {@link InputStream) from which information can be read. 
     * Calling {@link InputStream#close() close()} on this stream will result
     * in the cancellation of the piping process, and the writer will
     * receive an {@link IOException}.
     * @return the input stream from which information can be read; never null
     */
    public InputStream getInputStream() {
        return istream;
    }
    
    /**
     * Obtain the {@link OutputStream) to which the information is to be written.
     * Calling {@link OutputStream#close() close()} on this stream will result
     * in the termination of the piping process; the caller will <i>not</i>
     * receive an {@link IOException} but will instead simply be allowed to process
     * the information that was submitted by the writer until the 
     * {@link OutputStream#close() close()} was called.
     * @return the output stream to which information is to be written; never null
     */
    public OutputStream getOutputStream() {
        return ostream;
    }
    
}

