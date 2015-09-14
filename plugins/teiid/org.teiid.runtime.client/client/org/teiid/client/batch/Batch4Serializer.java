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
import java.sql.Array;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.teiid.core.types.ArrayImpl;
import org.teiid.core.types.BlobType;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.types.GeometryType;
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
 * <li>version 3: starts with 8.6 and adds better repeated string performance
 * <li>version 4: starts with 8.10 and adds the geometry type
 * </ul>
 */
public class Batch4Serializer extends Batch3Serializer {

    private static final byte VERSION_GEOMETRY = (byte)4;

    /**
     * @param teiidVersion
     */
    protected Batch4Serializer(ITeiidServerVersion teiidVersion, byte version) {
        super(teiidVersion, version);

        serializers.put(DataTypeManagerService.DefaultDataTypes.GEOMETRY.getId(),       new ColumnSerializer[] {defaultSerializer, new GeometryColumnSerializer()});
        serializers.put(DataTypeManagerService.DefaultDataTypes.OBJECT.getId(),     	new ColumnSerializer[] {defaultSerializer, new ObjectColumnSerializer_B4((byte)1)});
    }

    /**
     * @param teiidVersion
     */
    public Batch4Serializer(ITeiidServerVersion teiidVersion) {
        this(teiidVersion, VERSION_GEOMETRY);
    }

    @Override
    protected ColumnSerializer getArrayColumnSerializer2() {
        return new ArrayColumnSerializer2(new ObjectColumnSerializer_B4((byte)2));
    }

    protected class ArrayColumnSerializer2_B4 extends ArrayColumnSerializer2 {

        public ArrayColumnSerializer2_B4(ObjectColumnSerializer_B4 ser) {
            super(ser);
        }

        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            Object[] values = null;
            try {
                values = (Object[])((Array)obj).getArray();
            } catch (SQLException e) {
                out.writeInt(-1);
                return;
            }
            out.writeInt(values.length);
            DefaultDataTypes dataType = getDataTypeManager().getDataType(values.getClass().getComponentType());
            int code = dataType.ordinal();
            if (code == DefaultDataTypes.GEOMETRY.ordinal() && version < VERSION_GEOMETRY) {
                code = DefaultDataTypes.BLOB.ordinal();
            }
            out.writeByte((byte)code);
            for (int i = 0; i < values.length;) {
                writeIsNullData(out, i, values);
                int end = Math.min(values.length, i + 8);
                for (; i < end; i++) {
                    if (values[i] != null) {
                        this.ser.writeObject(out, values[i], code, cache, version);
                    }
                }
            }
            out.writeBoolean((obj instanceof ArrayImpl && ((ArrayImpl)obj).isZeroBased()));
        }
    }

    protected class ObjectColumnSerializer_B4 extends ObjectColumnSerializer1_B1 {

        byte defaultVersion;

        public ObjectColumnSerializer_B4(byte version) {
            super();
            this.defaultVersion = version;
        }

        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            DefaultDataTypes dataType = getDataTypeManager().getDataType(obj.getClass());
            int code = dataType.ordinal();
            if (code == DefaultDataTypes.GEOMETRY.ordinal() && version < VERSION_GEOMETRY) {
                code = DefaultDataTypes.BLOB.ordinal();
            }
            out.writeByte((byte)code);
            writeObject(out, obj, code, cache, version < VERSION_GEOMETRY ? this.defaultVersion : version);
        }

        @Override
        public void writeObject(ObjectOutput out, Object obj, int code, Map<Object, Integer> cache, byte effectiveVersion)
            throws IOException {
            if (code == DefaultDataTypes.BOOLEAN.ordinal()) {
                if (Boolean.TRUE.equals(obj)) {
                    out.write((byte)1);
                } else {
                    out.write((byte)0);
                }
            } else if (code == DefaultDataTypes.OBJECT.ordinal()) {
                super.writeObject(out, obj, cache, effectiveVersion);
            } else {
                String name = getDataTypeManager().getDataTypeName(obj.getClass());
                ColumnSerializer s = getSerializer(name, effectiveVersion);
                s.writeObject(out, obj, cache, effectiveVersion);
            }
        }

        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
            int code = in.readByte();
            return readObject(in, code, cache, version < VERSION_GEOMETRY ? this.defaultVersion : version);
        }

        @Override
        public boolean usesCache(byte version) {
            return version >= 3;
        }

    }

    protected class GeometryColumnSerializer extends ColumnSerializer {
        @Override
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            ((Externalizable)obj).writeExternal(out);
        }

        @Override
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
            if (version < 4) {
                BlobType bt = new BlobType();
                bt.readExternal(in);
                return bt;
            }
            GeometryType bt = new GeometryType();
            bt.readExternal(in);
            return bt;
        }
    }
}
