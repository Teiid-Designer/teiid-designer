/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.io;

import java.io.Writer;

/**
 * A writer that ignores all characters.
 */
public class NullWriter extends Writer {

    /**
     * The shared null writer.
     */
    public static final NullWriter SHARED = new NullWriter();

    /**
     * Don't allow public construction.
     */
    private NullWriter() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#append(char)
     */
    @Override
    public Writer append(final char c) {
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#append(java.lang.CharSequence)
     */
    @Override
    public Writer append(final CharSequence csq) {
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#append(java.lang.CharSequence, int, int)
     */
    @Override
    public Writer append(final CharSequence csq,
                         final int start,
                         final int end) {
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#close()
     */
    @Override
    public void close() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#write(char[])
     */
    @Override
    public void write(final char[] cbuf) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(final char[] chars,
                      final int offset,
                      final int length) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#write(int)
     */
    @Override
    public void write(final int c) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#write(java.lang.String)
     */
    @Override
    public void write(final String str) {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Writer#write(java.lang.String, int, int)
     */
    @Override
    public void write(final String str,
                      final int off,
                      final int len) {
        // nothing to do
    }

}
