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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.teiid.client.ResizingArrayList;
import org.teiid.core.types.BinaryType;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.types.XMLType;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;



/** 
 * @since 4.2
 */
public class Batch1Serializer extends Batch0Serializer{

    protected Batch1Serializer(ITeiidServerVersion teiidVersion, byte version) {
        super(teiidVersion, version);

        serializers.put(DataTypeManagerService.DefaultDataTypes.VARBINARY.getId(),     new ColumnSerializer[] { new BinaryColumnSerializer() });

        version1serializers.put(DataTypeManagerService.DefaultDataTypes.XML.getId(),            new XmlColumnSerializer1_B1());
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.OBJECT.getId(),     	new ObjectColumnSerializer1_B1(DataTypeManagerService.DefaultDataTypes.VARBINARY.ordinal()));
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.VARBINARY.getId(),     new BinaryColumnSerializer1());
    }

    /**
     * @param teiidVersion
     */
    public Batch1Serializer(ITeiidServerVersion teiidVersion) {
        this(teiidVersion, (byte) 1);
    }

    protected class BinaryColumnSerializer1 extends ColumnSerializer {
		@Override
		public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
			byte[] bytes = ((BinaryType)obj).getBytes();
			out.writeInt(bytes.length); //in theory this could be a short, but we're not strictly enforcing the length
			out.write(bytes);
		}

		@Override
		public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
			int length = in.readInt();
			byte[] bytes = new byte[length];
			in.readFully(bytes);
			return new BinaryType(bytes);
		}
	}
    
    protected class BinaryColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            //uses object serialization for compatibility with legacy clients
            super.writeObject(out, ((BinaryType)obj).getBytesDirect(), cache, version);
        }

        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
            //won't actually be used
            byte[] bytes = (byte[])super.readObject(in, cache, version);
            return new BinaryType(bytes);
        }
    }

    protected class ObjectColumnSerializer1_B1 extends ObjectColumnSerializer1 {

        private int highestKnownCode;

        public ObjectColumnSerializer1_B1() {
            this.highestKnownCode = -1;
        }

        public ObjectColumnSerializer1_B1(int highestKnownCode) {
            this.highestKnownCode = highestKnownCode;
        }

        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            DefaultDataTypes dataType = getDataTypeManager().getDataType(obj.getClass());
            int code = dataType.ordinal();
            out.writeByte((byte)code);
            writeObject(out, obj, code, cache, version);
        }

		public void writeObject(ObjectOutput out, Object obj, int code, Map<Object, Integer> cache, byte version)
            throws IOException {
		    if (code == DefaultDataTypes.BOOLEAN.ordinal()) {
		        if (Boolean.TRUE.equals(obj)) {
		            out.write((byte)1);
		        } else {
		            out.write((byte)0);
		        }
		    } else if (code <= this.highestKnownCode && code != DefaultDataTypes.OBJECT.ordinal()) {
		        DefaultDataTypes dataType = DefaultDataTypes.valueOf(getTeiidVersion(), code);
		        ColumnSerializer s = getSerializer(dataType.getId(), version);
		        s.writeObject(out, obj, cache, version);
		    } else {
		        super.writeObject(out, obj, cache, version);
		    }
		}

		@Override
        public boolean usesCache(byte version) {
            return version >= 3;
        }
	}

    private class XmlColumnSerializer1_B1 extends XmlColumnSerializer1 {

        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            ((XMLType)obj).writeExternal(out, (byte) 1);
        }

        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
        	XMLType xt = new XMLType();
        	xt.readExternal(in, (byte) 1);
            return xt;
        }
    }

    @Override
    public List<List<Object>> readBatch(ObjectInput in, String[] types) throws IOException, ClassNotFoundException {
        int rows = 0;
        try {
            rows = in.readInt();
        } catch (IOException e) {
            //7.4 compatibility
            if (types == null || types.length == 0) {
                List<Object>[] result = (List[])in.readObject();
                ArrayList<List<Object>> batch = new ArrayList<List<Object>>();
                batch.addAll(Arrays.asList(result));
                return batch;
            }
            throw e;
        }
        if (rows == 0) {
            return new ArrayList<List<Object>>(0);
        }
        if (rows == -1) {
        	return null;
        }
        byte version = (byte)0;
        if (rows < 0) {
        	rows = -(rows+1);
        	version = in.readByte();
        } 
        int columns = in.readInt();
        List<List<Object>> batch = new ResizingArrayList<List<Object>>(rows);
        int numBytes = rows/8;
        int extraRows = rows % 8;
        for (int currentRow = 0; currentRow < rows; currentRow++) {
            batch.add(currentRow, Arrays.asList(new Object[columns]));
        }
        byte[] isNullBuffer = new byte[(extraRows > 0) ? numBytes + 1: numBytes];
        List<Object> cache = null;
        for (int col = 0; col < columns; col++) {
            ColumnSerializer serializer = getSerializer(types[col], version);
            if (cache == null && serializer.usesCache(version)) {
                cache = new ArrayList<Object>();
            }
            serializer.readColumn(in, col, batch, isNullBuffer, cache, version);
        }
        return batch;
    }
}
