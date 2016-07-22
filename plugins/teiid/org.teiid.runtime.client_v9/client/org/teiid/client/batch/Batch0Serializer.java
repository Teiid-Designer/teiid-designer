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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamConstants;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.teiid.client.BatchSerializer;
import org.teiid.client.ResizingArrayList;
import org.teiid.core.types.BlobType;
import org.teiid.core.types.ClobType;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.types.XMLType;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.Messages;



/** 
 * @since 4.2
 */
public class Batch0Serializer extends BatchSerializer {

    protected final Map<String, ColumnSerializer[]> serializers = new HashMap<String, ColumnSerializer[]>(128);

    protected final Map<String, ColumnSerializer> version1serializers = new HashMap<String, ColumnSerializer>(128);

    protected Batch0Serializer(ITeiidServerVersion teiidVersion, byte version) {
        super(teiidVersion, version);

        initDateNormalizer();

        serializers.put(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL.getId(),  new ColumnSerializer[] { new BigDecimalColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.BIG_INTEGER.getId(),  new ColumnSerializer[] { new BigIntegerColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId(),         new ColumnSerializer[] { new BooleanColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.BYTE.getId(),                  new ColumnSerializer[] { new ByteColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.CHAR.getId(),                 new ColumnSerializer[] { new CharColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.DATE.getId(),                  new ColumnSerializer[] { new DateColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.DOUBLE.getId(),            new ColumnSerializer[] { new DoubleColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.FLOAT.getId(),                new ColumnSerializer[] { new FloatColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),           new ColumnSerializer[] { new IntColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.LONG.getId(),                new ColumnSerializer[] { new LongColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.SHORT.getId(),               new ColumnSerializer[] { new ShortColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.TIME.getId(),                  new ColumnSerializer[] { new TimeColumnSerializer() });
        serializers.put(DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getId(),      new ColumnSerializer[] { new TimestampColumnSerializer() });

        version1serializers.put(DataTypeManagerService.DefaultDataTypes.DATE.getId(),          new DateColumnSerializer1());
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.TIME.getId(),          new TimeColumnSerializer1());
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.STRING.getId(),     	new StringColumnSerializer1());
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.CLOB.getId(),     		new ClobColumnSerializer1());
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.BLOB.getId(),     		new BlobColumnSerializer1());
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.XML.getId(),     		new XmlColumnSerializer1());
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.NULL.getId(),     		new NullColumnSerializer1());
    	version1serializers.put(DataTypeManagerService.DefaultDataTypes.OBJECT.getId(),     	new ObjectColumnSerializer1());
    }

    /**
     * @param teiidVersion
     */
    public Batch0Serializer(ITeiidServerVersion teiidVersion) {
        this(teiidVersion, (byte) 0);
    }

    protected class ObjectColumnSerializer1 extends ColumnSerializer {
    	
    	@Override
    	public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version)
    			throws IOException {
    	    DefaultDataTypes dataType =  getDataTypeManager().getDataType(obj.getClass());
            int code = dataType.ordinal();
    		out.writeByte((byte)code);
    		if (code == DataTypeManagerService.DefaultDataTypes.BOOLEAN.ordinal()) {
    			if (Boolean.TRUE.equals(obj)) {
    				out.write((byte)1);
    			} else {
    				out.write((byte)0);
    			}
    		} else if (code != DataTypeManagerService.DefaultDataTypes.OBJECT.ordinal()) {
    		    dataType = DataTypeManagerService.DefaultDataTypes.valueOf(getTeiidVersion(), code);
    			ColumnSerializer s = getSerializer(dataType.getId(), (byte)1);
    			s.writeObject(out, obj, cache, version);
    		} else {
    			super.writeObject(out, obj, cache, version);
    		}
    	}
    	

        @Override
      public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
          int code = in.readByte();
          return readObject(in, code, cache, version);
      }

      public Object readObject(ObjectInput in, int code, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
          if (code == DataTypeManagerService.DefaultDataTypes.BOOLEAN.ordinal()) {
              if (in.readByte() == (byte)0) {
                  return Boolean.FALSE;
              }
              return Boolean.TRUE;
          }
          if (code != DataTypeManagerService.DefaultDataTypes.OBJECT.ordinal()) {
              DefaultDataTypes dataType = DataTypeManagerService.DefaultDataTypes.valueOf(getTeiidVersion(), code);
              ColumnSerializer s = getSerializer(dataType.getId(), (byte) 1);
              return s.readObject(in, cache, version);
          }
          return super.readObject(in, cache, version);
      }
    }
    
    protected final static int MAX_UTF = 0xFFFF/3; //this is greater than the expected max length of Teiid Strings
    
    protected class StringColumnSerializer1 extends ColumnSerializer {
    	@Override
    	public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
    		String str = (String)obj;
        	if (str.length() <= MAX_UTF) {
        		//skip object serialization if we have a short string
        	    out.writeByte(ObjectStreamConstants.TC_STRING);
        	    out.writeUTF(str);
        	} else {
        		out.writeByte(ObjectStreamConstants.TC_LONGSTRING);
        		out.writeObject(obj);
        	}
        }
    	
    	@Override
    	public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException,
    			ClassNotFoundException {
    		if (in.readByte() == ObjectStreamConstants.TC_STRING) {
    			return in.readUTF();
    		}
    		return super.readObject(in, cache, version);
    	}
    }

    protected class NullColumnSerializer1 extends ColumnSerializer {
    	@Override
    	public void writeColumn(ObjectOutput out, int col,
    			List<? extends List<?>> batch, Map<Object, Integer> cache, byte version) {
    	    // Nothing Required
    	}

    	@Override
    	public void readColumn(ObjectInput in, int col,
    			List<List<Object>> batch, byte[] isNull, List<Object> cache, byte version) {
            // Nothing Required
    	}
    }
    
    protected class ClobColumnSerializer1 extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
        	((Externalizable)obj).writeExternal(out);
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
        	ClobType ct = new ClobType();
        	ct.readExternal(in);
            return ct;
        }
    }

    protected class BlobColumnSerializer1 extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
        	((Externalizable)obj).writeExternal(out);
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
        	BlobType bt = new BlobType();
        	bt.readExternal(in);
            return bt;
        }
    }

    protected class XmlColumnSerializer1 extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
        	((Externalizable)obj).writeExternal(out);
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
        	XMLType xt = new XMLType();
        	xt.readExternal(in);
            return xt;
        }
    }
    
    protected class IntColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeInt(((Integer)obj).intValue());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return Integer.valueOf(in.readInt());
        }
    }
    
    protected class LongColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeLong(((Long)obj).longValue());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return Long.valueOf(in.readLong());
        }
    }
    
    protected class FloatColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeFloat(((Float)obj).floatValue());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return new Float(in.readFloat());
        }
    }
    
    protected class DoubleColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeDouble(((Double)obj).doubleValue());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return new Double(in.readDouble());
        }
    }
    
    protected class ShortColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeShort(((Short)obj).shortValue());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return Short.valueOf(in.readShort());
        }
    }
    
    protected class BooleanColumnSerializer extends ColumnSerializer {
        /* This implementation compacts the isNull and boolean data for non-null values into a byte[]
         * by using a 8 bit mask that is bit-shifted to mask each value.
         */
    	@Override
        public void writeColumn(ObjectOutput out, int col, List<? extends List<?>> batch, Map<Object, Integer> cache, byte version) throws IOException {
            int currentByte = 0;
            int mask = 0x80;
            Object obj;
            for (int row = 0; row < batch.size(); row++) {
                // Write the isNull value
                obj = batch.get(row).get(col);
                if (obj == null ) {
                    currentByte |= mask;
                }
                mask >>= 1; // Shift the mask to the next bit
                if (mask == 0) {
                    // If the current byte has been used up, write it and reset.
                    out.write(currentByte);
                    currentByte = 0;
                    mask = 0x80;
                }
                if (obj != null) {
                    // Write the boolean value if it's not null
                    if (((Boolean)obj).booleanValue()) {
                        currentByte |= mask;
                    }
                    mask >>= 1;
                    if (mask == 0) {
                        out.write(currentByte);
                        currentByte = 0;
                        mask = 0x80;
                    }
                }
            }
            // Invariant mask != 0
            // If we haven't reached the eight-row mark then the loop would not have written this byte
            // Write the final byte containing data for th extra rows, if it exists.
            if (mask != 0x80) {
                out.write(currentByte);
            }
        }
        
        @Override
        public void readColumn(ObjectInput in, int col,
        		List<List<Object>> batch, byte[] isNull, List<Object> cache, byte version) throws IOException {
            int currentByte = 0, mask = 0; // Initialize the mask so that it is reset in the loop
            boolean isNullVal;
            for (int row = 0; row < batch.size(); row++) {
                if (mask == 0) {
                    // If we used up the byte, read the next one, and reset the mask
                    currentByte = in.read();
                    mask = 0x80;
                }
                isNullVal = (currentByte & mask) != 0;
                mask >>= 1; // Shift the mask to the next bit
                if (!isNullVal) {
                    if (mask == 0) {
                        currentByte = in.read();
                        mask = 0x80;
                    }
                    batch.get(row).set(col, ((currentByte & mask) == 0) ? Boolean.FALSE : Boolean.TRUE);
                    mask >>= 1;
                }
            }
        }
    }
    
    protected class ByteColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeByte(((Byte)obj).byteValue());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return Byte.valueOf(in.readByte());
        }
    }
    
    protected class CharColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeChar(((Character)obj).charValue());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return Character.valueOf(in.readChar());
        }
    }
    
    protected class BigIntegerColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            BigInteger val = (BigInteger)obj;
            byte[] bytes = val.toByteArray();
            out.writeInt(bytes.length);
            out.write(bytes);
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            int length = in.readInt();
            byte[] bytes = new byte[length];
            in.readFully(bytes);
            return new BigInteger(bytes);
        }
    }
    
    protected class BigDecimalColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            BigDecimal val = (BigDecimal)obj;
            out.writeInt(val.scale());
            BigInteger unscaled = val.unscaledValue();
            byte[] bytes = unscaled.toByteArray();
            out.writeInt(bytes.length);
            out.write(bytes);
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            int scale = in.readInt();
            int length = in.readInt();
            byte[] bytes = new byte[length];
            in.readFully(bytes);
            return new BigDecimal(new BigInteger(bytes), scale);
        }
    }
    
    protected class DateColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeLong(((java.sql.Date)obj).getTime());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return new java.sql.Date(in.readLong());
        }
    }
    
    protected class TimeColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeLong(((Time)obj).getTime());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return new Time(in.readLong());
        }
    }
    
    int dateNormalizer = -1;

	protected void initDateNormalizer() {
	    if (dateNormalizer == -1) {
	        Calendar c = Calendar.getInstance();
	        c.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
	        c.set(1900, 0, 1, 0, 0, 0);
	        c.set(Calendar.MILLISECOND, 0);
	        dateNormalizer = -(int)(c.getTime().getTime()/60000); //support a 32 bit range starting at this value
	    }
	}

    protected class DateColumnSerializer1 extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeInt((int)(((java.sql.Date)obj).getTime()/60000) + dateNormalizer);
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return new java.sql.Date(((in.readInt()&0xffffffffL) - dateNormalizer)*60000);
        }
    }
    
    protected class TimeColumnSerializer1 extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeInt((int)(((Time)obj).getTime()/1000));
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            return new Time((in.readInt()&0xffffffffL)*1000);
        }
    }
    
    protected class TimestampColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            Timestamp ts =  (Timestamp)obj;
            out.writeLong(ts.getTime());
            out.writeInt(ts.getNanos());
        }
        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException {
            Timestamp ts = new Timestamp(in.readLong());
            ts.setNanos(in.readInt());
            return ts;
        }
    }
        
    protected ColumnSerializer getSerializer(String type, byte version) {
    	ColumnSerializer cs = null;
    	if (version == 1) {
    		cs = version1serializers.get((type == null) ? DataTypeManagerService.DefaultDataTypes.OBJECT : type);
    	}
    	if (cs == null) {
    	    ColumnSerializer[] serializerSet = serializers.get((type == null) ? DataTypeManagerService.DefaultDataTypes.OBJECT : type);
    	    if (serializerSet != null && serializerSet.length > 0)
    	        cs = serializerSet[0];
    	}
        if (cs == null) {
        	return defaultSerializer;
        }
        return cs;
    }

    @Override
    public void writeBatch(ObjectOutput out, String[] types, List<? extends List<?>> batch) throws IOException {
    	writeBatch(out, types, batch, getCurrentVersion());
    }
    
    @Override
    public void writeBatch(ObjectOutput out, String[] types, List<? extends List<?>> batch, byte version) throws IOException {
        if (batch == null) {
            out.writeInt(-1);
        } else {
            if (version > 0 && batch.size() > 0) {
                out.writeInt(-batch.size() -1);
                out.writeByte(version);
            } else {
                out.writeInt(batch.size());
            }
            if (batch.size() > 0) {
	            int columns = types.length;
	            out.writeInt(columns);
	            Map<Object, Integer> cache = null;
	            for(int i = 0; i < columns; i++) {
	            	ColumnSerializer serializer = getSerializer(types[i], version);

	            	if (cache == null && serializer.usesCache(version)) {
                        cache = new HashMap<Object, Integer>();
                    }
	            	try {
	                    serializer.writeColumn(out, i, batch, cache, version);
	                } catch (ClassCastException e) {
	                    Object obj = null;
	                    String objectClass = null;
	                    objectSearch: for (int row = 0; row < batch.size(); row++) {
	                        obj = batch.get(row).get(i);
	                        if (obj != null) {
	                            objectClass = obj.getClass().getName();
	                            break objectSearch;
	                        }
	                    }
	                    throw new RuntimeException(Messages.gs(Messages.TEIID.TEIID20001, new Object[] {types[i], new Integer(i), objectClass}), e);
	                }
	            }
            }
        }
    }
    
    @Override
    public List<List<Object>> readBatch(ObjectInput in, String[] types) throws IOException, ClassNotFoundException {
    	return readBatch(in, types, (byte)0);
    }
    
    private List<List<Object>> readBatch(ObjectInput in, String[] types, byte version) throws IOException, ClassNotFoundException {
        int rows = in.readInt();
        if (rows == 0) {
            return new ArrayList<List<Object>>(0);
        } else if (rows > 0) {
            int columns = in.readInt();
            List<List<Object>> batch = new ResizingArrayList<List<Object>>(rows);
            int numBytes = rows/8;
            int extraRows = rows % 8;
            for (int currentRow = 0; currentRow < rows; currentRow++) {
                batch.add(currentRow, Arrays.asList(new Object[columns]));
            }
            byte[] isNullBuffer = new byte[(extraRows > 0) ? numBytes + 1: numBytes];
            for (int col = 0; col < columns; col++) {
                getSerializer(types[col], version).readColumn(in, col, batch, isNullBuffer, null, version);
            }
            return batch;
        }
        return null;
    }
}
