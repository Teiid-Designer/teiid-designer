/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
 
package org.teiid.common.buffer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.PhantomReference;
import java.nio.charset.Charset;
import org.teiid.common.buffer.FileStore.FileStoreOutputStream;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.InputStreamFactory;

public final class FileStoreInputStreamFactory extends InputStreamFactory {
	private final FileStore lobBuffer;
	private FileStoreOutputStream fsos;
	private String encoding;
	private PhantomReference<Object> cleanup;
	private Writer writer;

	public FileStoreInputStreamFactory(FileStore lobBuffer, String encoding) {
		this.encoding = encoding;
		this.lobBuffer = lobBuffer;
		cleanup = AutoCleanupUtil.setCleanupReference(this, lobBuffer);
	}

	@Override
	public InputStream getInputStream() {
		return getInputStream(0, -1);
	}
	
	public InputStream getInputStream(long start, long len) {
		if (fsos != null && !fsos.bytesWritten()) {
			if (start > Integer.MAX_VALUE) {
				throw new AssertionError("Invalid start " + start); //$NON-NLS-1$
			}
			int s = (int)start;
			int intLen = fsos.getCount() - s;
            if (len >= 0) {
                intLen = (int)Math.min(len, len);
            }
            return new ByteArrayInputStream(fsos.getBuffer(), s, intLen);
        }
        return lobBuffer.createInputStream(start, len);
    }
    
    public byte[] getMemoryBytes() {
        if (fsos != null && !fsos.bytesWritten() && fsos.getBuffer().length == fsos.getCount()) {
            return fsos.getBuffer();
        }
        throw new IllegalStateException("In persistent mode or not closed for writing"); //$NON-NLS-1$
    }
	
	@Override
	public Reader getCharacterStream() throws IOException {
		return new InputStreamReader(getInputStream(), Charset.forName(encoding).newDecoder());
	}

	@Override
	public long getLength() {
		if (fsos != null && !fsos.bytesWritten()) {
			return fsos.getCount();
		}
		return lobBuffer.getLength();
	}

	/**
	 * Returns a new writer instance that is backed by the shared output stream.
	 * Closing a writer will prevent further writes.
	 * @return
	 */
	public Writer getWriter() {
		if (writer == null) {
			writer = new OutputStreamWriter(getOuputStream(), Charset.forName(encoding));
		}
		return writer;
	}
	
	/**
	 * The returned output stream is shared among all uses.
	 * Once closed no further writing can occur
	 * @return output stream
	 */
	public FileStoreOutputStream getOuputStream() {
	    return getOuputStream(DataTypeManagerService.MAX_LOB_MEMORY_BYTES);
    }
    
    /**
     * The returned output stream is shared among all uses.
     * Once closed no further writing can occur
     * @param maxMemorySize
     * @return output stream
     */
    public FileStoreOutputStream getOuputStream(int maxMemorySize) {
        if (fsos == null) {
            fsos = lobBuffer.createOutputStream(maxMemorySize);
        }
        return fsos;
    }

	@Override
	public void free() {
		fsos = null;
		lobBuffer.remove();
		AutoCleanupUtil.removeCleanupReference(cleanup);
		cleanup = null;
	}
	
	@Override
	public StorageMode getStorageMode() {
		if (fsos == null || fsos.bytesWritten()) {
			return StorageMode.PERSISTENT;
		}
		return StorageMode.MEMORY;
	}
}