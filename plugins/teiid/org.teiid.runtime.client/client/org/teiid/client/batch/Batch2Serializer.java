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
import java.sql.Array;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.teiid.core.types.ArrayImpl;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.Messages;




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
public class Batch2Serializer extends Batch1Serializer {

    protected Batch2Serializer(ITeiidServerVersion teiidVersion, byte version) {
        super(teiidVersion, version);

        serializers.put(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL.getId(),   new ColumnSerializer[] {new BigDecimalColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.BIG_INTEGER.getId(),   new ColumnSerializer[] {new BigIntegerColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId(),       new ColumnSerializer[] {new BooleanColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.BYTE.getId(),          new ColumnSerializer[] {new ByteColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.CHAR.getId(),          new ColumnSerializer[] {new CharColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.DATE.getId(),          new ColumnSerializer[] {new DateColumnSerializer(), new DateColumnSerializer1_B2(), new DateColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.DOUBLE.getId(),        new ColumnSerializer[] {new DoubleColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.FLOAT.getId(),         new ColumnSerializer[] {new FloatColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.INTEGER.getId(),       new ColumnSerializer[] {new IntColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.LONG.getId(),          new ColumnSerializer[] {new LongColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.SHORT.getId(),         new ColumnSerializer[] {new ShortColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.TIME.getId(),          new ColumnSerializer[] {new TimeColumnSerializer(), new TimeColumnSerializer1_B2(), new TimeColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getId(),     new ColumnSerializer[] {new TimestampColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.STRING.getId(),     	new ColumnSerializer[] {defaultSerializer, new StringColumnSerializer1()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.CLOB.getId(),  	   	new ColumnSerializer[] {defaultSerializer, new ClobColumnSerializer1()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.BLOB.getId(),     		new ColumnSerializer[] {defaultSerializer, new BlobColumnSerializer1()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.XML.getId(),     		new ColumnSerializer[] {defaultSerializer, new XmlColumnSerializer1()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.NULL.getId(),     		new ColumnSerializer[] {defaultSerializer, new NullColumnSerializer1()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.OBJECT.getId(),     	new ColumnSerializer[] {defaultSerializer, new ObjectColumnSerializer1_B1(DataTypeManagerService.DefaultDataTypes.VARBINARY.ordinal())});
        serializers.put(DataTypeManagerService.DefaultDataTypes.VARBINARY.getId(),    	new ColumnSerializer[] {new BinaryColumnSerializer(), new BinaryColumnSerializer1()});
    }

    /**
     * @param teiidVersion
     */
    public Batch2Serializer(ITeiidServerVersion teiidVersion) {
        this(teiidVersion, (byte) 2);
    }

    protected ColumnSerializer arrayColumnSerializer = new ColumnSerializer() {
    	
    	@Override
    	public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version)
    			throws IOException {
    		try {
				super.writeObject(out, ((java.sql.Array)obj).getArray(), cache, version);
			} catch (SQLException e) {
				throw new IOException(e);
			}
    	}
    	
    	@Override
    	public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException,
    			ClassNotFoundException {
    		return new ArrayImpl(getTeiidVersion(), (Object[]) in.readObject());
    	}
    	
    };

    protected ColumnSerializer getArrayColumnSerializer2() {
        return new ArrayColumnSerializer2(new ObjectColumnSerializer1_B1(DataTypeManagerService.DefaultDataTypes.VARBINARY.ordinal()));
    }

	protected class ArrayColumnSerializer2 extends ColumnSerializer {

		ObjectColumnSerializer1_B1 ser;
		
		public ArrayColumnSerializer2(ObjectColumnSerializer1_B1 ser) {
			this.ser = ser;
		}
		
		@Override
    	public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version)
    			throws IOException {
			Object[] values = null;
    		try {
    			values = (Object[]) ((Array)obj).getArray();
    		} catch (SQLException e) {
    			out.writeInt(-1);
    			return;
    		}
			out.writeInt(values.length);
			DefaultDataTypes dataType = getDataTypeManager().getDataType(values.getClass().getComponentType());
            int code = dataType.ordinal();
    		out.writeByte((byte)code);
    		for (int i = 0; i < values.length;) {
    			writeIsNullData(out, i, values);
    			int end = Math.min(values.length, i+8);
    			for (; i < end; i++) {
    				if (values[i] != null) {
						ser.writeObject(out, values[i], code, cache, version);
					}        				
    			}
    		}
    		out.writeBoolean((obj instanceof ArrayImpl && ((ArrayImpl)obj).isZeroBased()));
    	}

		@Override
    	public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException,
    			ClassNotFoundException {
    		int length = in.readInt();
    		if (length == -1) {
        		return new ArrayImpl(getTeiidVersion(), (Object[]) null);
    		}
    		int code = in.readByte();
    		DefaultDataTypes dataType = DataTypeManagerService.DefaultDataTypes.valueOf(getTeiidVersion(), code);
    		Object[] vals = (Object[])java.lang.reflect.Array.newInstance(dataType.getTypeClass(), length);
    		for (int i = 0; i < length;) {
    			byte b = in.readByte();
    			int end = Math.min(length, i+8);
    			for (; i < end; i++) {
					if (!isNullObject(i, b)) {
						vals[i] = ser.readObject(in, code, cache, version);
					}
    			}
    		}
    		ArrayImpl result = new ArrayImpl(getTeiidVersion(), vals);
    		result.setZeroBased(in.readBoolean());
    		return result;
    	}

		@Override
        public boolean usesCache(byte version) {
            return version >= 3;
        }
	}

    private static long MIN_DATE_32;
    private static long MAX_DATE_32;
    private static long MIN_TIME_32;
    private static long MAX_TIME_32;

    @Override
    protected void initDateNormalizer() {
        if (dateNormalizer == -1) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		c.set(1900, 0, 1, 0, 0, 0);
		c.set(Calendar.MILLISECOND, 0);
		MIN_DATE_32 = c.getTimeInMillis();
		MAX_DATE_32 = MIN_DATE_32 + ((1l<<32)-1)*60000;
		dateNormalizer = -(int)(MIN_DATE_32/60000); //support a 32 bit range starting at this value
		MAX_TIME_32 = Integer.MAX_VALUE*1000l;
		MIN_TIME_32 = Integer.MIN_VALUE*1000l;
        }
	}

    private class DateColumnSerializer1_B2 extends DateColumnSerializer1 {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            long time = ((java.sql.Date)obj).getTime();
            if (time < MIN_DATE_32 || time > MAX_DATE_32) {
                throw new IOException(Messages.gs(Messages.TEIID.TEIID20029, obj.getClass().getName()));
            }
			out.writeInt((int)(time/60000) + dateNormalizer);
        }
    }
    
    protected class TimeColumnSerializer1_B2 extends TimeColumnSerializer1 {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            long time = ((Time)obj).getTime();
            if (time < MIN_TIME_32 || time > MAX_TIME_32) {
                throw new IOException(Messages.gs(Messages.TEIID.TEIID20029, obj.getClass().getName()));
            }
			out.writeInt((int)(time/1000));
        }
    }

    @Override
    protected ColumnSerializer getSerializer(String type, byte version) {
    	ColumnSerializer[] sers = serializers.get(type);
    	if (sers == null) {
    		if (DataTypeManagerService.isArrayType(type)) {
    			if (version < 2) {
    				return arrayColumnSerializer; 
    			}
    			return getArrayColumnSerializer2();
    		}
    		return defaultSerializer;
    	}
    	return sers[Math.min(version, sers.length - 1)];
    }
}
