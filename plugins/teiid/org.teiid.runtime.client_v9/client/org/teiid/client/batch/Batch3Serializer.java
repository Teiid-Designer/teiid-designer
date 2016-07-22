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

package org.teiid.client.batch;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamConstants;
import java.util.List;
import java.util.Map;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;



/**
 * @since 4.2
 *
 * <ul>
 * <li>version 0: starts with 7.1 and uses simple serialization too broadly
 * <li>version 1: starts with 8.0 uses better string, blob, clob, xml, etc.
 *   add varbinary support.
 *   however was possibly silently truncating date/time values that were
 *   outside of jdbc allowed values
 * <li>version 2: starts with 8.2 and adds better array serialization and
 *   uses a safer date/time serialization
 * </ul>
 */
public class Batch3Serializer extends Batch2Serializer {

    protected Batch3Serializer(ITeiidServerVersion teiidVersion, byte version) {
        super(teiidVersion, version);

        serializers.put(DataTypeManagerService.DefaultDataTypes.STRING.getId(),     	new ColumnSerializer[] {defaultSerializer, new StringColumnSerializer1(), new StringColumnSerializer1(), new StringColumnSerializer3()});
    }

    /**
     * @param teiidVersion
     */
    public Batch3Serializer(ITeiidServerVersion teiidVersion) {
        this(teiidVersion, (byte) 3);
    }

    protected class StringColumnSerializer3 extends StringColumnSerializer1 {
    	private int MAX_INLINE_STRING_LENGTH = 5;
    	private byte REPEATED_STRING = 0;
    	@Override
    	public Object readObject(ObjectInput in, List<Object> cache, byte version)
    			throws IOException, ClassNotFoundException {
    		byte b = in.readByte();
    		if (b == ObjectStreamConstants.TC_STRING) {
    			String val = in.readUTF();
    			if (val.length() > MAX_INLINE_STRING_LENGTH) {
    				cache.add(val);
    			}
    			return val;
    		}
    		if (b == REPEATED_STRING) {
    			Integer val = in.readInt();
    			return cache.get(val);
    		}
    		String val = (String) in.readObject();
    		if (val.length() > MAX_INLINE_STRING_LENGTH) {
				cache.add(val);
			}
    		return val;
    	}
    	
    	@Override
    	public void writeObject(ObjectOutput out, Object obj,
    			Map<Object, Integer> cache, byte version) throws IOException {
    		String str = (String)obj;
    		Integer val = cache.get(str);
    		if (val != null) {
    			out.writeByte(REPEATED_STRING);
    			out.writeInt(val);
    			return;
    		} 
    		if (str.length() > MAX_INLINE_STRING_LENGTH) {
    			cache.put(str, cache.size());
    		}
    		super.writeObject(out, obj, cache, version);
    	}
    	
    	@Override
    	public boolean usesCache(byte version) {
    		return true;
    	}
    }

}
