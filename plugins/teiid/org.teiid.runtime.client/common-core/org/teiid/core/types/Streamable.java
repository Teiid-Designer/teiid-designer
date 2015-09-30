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

package org.teiid.core.types;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teiid.core.types.InputStreamFactory.StorageMode;
import org.teiid.core.util.MultiArrayOutputStream;



/**
 * A large value object which can be streamable in chunks of data each time
 * 
 * <p>A reference stream id is tuple source id for a Streamble object where the 
 * object is in buffer manager, but the contents will never be written to disk;
 * this is the ID that client needs to reference to get the chunk of data.
 */
public abstract class Streamable<T> implements Externalizable {
	
	private static final Logger logger = Logger.getLogger(Streamable.class.getName());

	private static final long serialVersionUID = -8252488562134729374L;
	
	private static AtomicLong counter = new AtomicLong();
	
	public static final String ENCODING = "UTF-8"; //$NON-NLS-1$
	public static final Charset CHARSET = Charset.forName(ENCODING);
    public static final int STREAMING_BATCH_SIZE_IN_BYTES = 102400; // 100K

    private String referenceStreamId = String.valueOf(counter.getAndIncrement());
    protected transient volatile T reference;
	protected long length = -1;
    
    public Streamable() {
    	
	}
    
    public Streamable(T reference) {
        if (reference == null) {
            throw new IllegalArgumentException();
        }

    	this.reference = reference;
    }
    
    /**
     * Returns the cached length.  May be binary or character based.
     * @return
     */
    public long getLength() {
		return length;
	}
    
    abstract long computeLength() throws SQLException;
    
    public long length() throws SQLException {
    	if (length == -1) {
    		if (InputStreamFactory.getStorageMode(this.reference) == StorageMode.FREE) {
    			throw new SQLException("Already freed or streaming"); //$NON-NLS-1$ 
    		}
    		length = computeLength();
    	}
    	return length;
    }
    
    public T getReference() {
		return reference;
	}
    
    public void setReference(T reference) {
		this.reference = reference;
	}
    
    public String getReferenceStreamId() {
        return this.referenceStreamId;
    }
    
    public void setReferenceStreamId(String id) {
        this.referenceStreamId = id;
    }
    
    @Override
    public String toString() {
    	if (reference == null) {
    		return getClass().getName() + " " + this.referenceStreamId; //$NON-NLS-1$
    	}
        return reference.toString();
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException,
    		ClassNotFoundException {
    	length = in.readLong();
    	this.referenceStreamId = (String)in.readObject();
    	if (referenceStreamId == null) {
    		//we expect the data inline
    		readReference(in);
    	}
    }
    
    protected abstract void readReference(ObjectInput in) throws IOException;
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
		try {
			length();
		} catch (SQLException e) {
		}
    	out.writeLong(length);
    	boolean writeBuffer = false;
    	MultiArrayOutputStream baos = null;
    	if (referenceStreamId == null) {
    		//TODO: detect when this buffering is not necessary
    	    if (length > Integer.MAX_VALUE) {
                throw new AssertionError("Should not inline a lob of length " + length); //$NON-NLS-1$
            }
            if (length > 0) {
                baos = new MultiArrayOutputStream((int)length);
            } else {
                baos = new MultiArrayOutputStream(256);
            }
    		DataOutputStream dataOutput = new DataOutputStream(baos);
    		try {
    			writeReference(dataOutput);
    			dataOutput.close();
    			writeBuffer = true;
    		} catch (IOException e) {
    			logger.log(Level.WARNING, e.getMessage());
    			referenceStreamId = "error"; //$NON-NLS-1$
    		}
    	}
    	out.writeObject(referenceStreamId);
		if (writeBuffer) {
			baos.writeTo(out);
		}
    }

    protected boolean isBinary() {
        return true;
    }

    protected abstract void writeReference(DataOutput out) throws IOException;
    
}
