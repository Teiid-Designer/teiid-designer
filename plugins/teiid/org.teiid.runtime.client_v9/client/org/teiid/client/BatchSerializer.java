/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.client;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import org.teiid.client.batch.Batch0Serializer;
import org.teiid.client.batch.Batch1Serializer;
import org.teiid.client.batch.Batch2Serializer;
import org.teiid.client.batch.Batch3Serializer;
import org.teiid.client.batch.Batch4Serializer;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

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
public abstract class BatchSerializer {

    protected final byte currentVersion;

    private final ITeiidServerVersion teiidVersion;

    protected ColumnSerializer defaultSerializer = new ColumnSerializer();

    /**
     * Base Column Serializer
     */
    public class ColumnSerializer {

        /**
         * @param out
         * @param col
         * @param batch
         * @param cache
         * @param version
         * @throws IOException
         */
        public void writeColumn(ObjectOutput out, int col, List<? extends List<?>> batch, Map<Object, Integer> cache, byte version) throws IOException {
            writeIsNullData(out, col, batch);
            Object obj = null;
            for (int i = 0; i < batch.size(); i++) {
                obj = batch.get(i).get(col);
                if (obj != null) {
                    writeObject(out, obj, cache, version);
                }
            }
        }

        /**
         * @param in
         * @param col
         * @param batch
         * @param isNull
         * @param cache
         * @param version 
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public void readColumn(ObjectInput in, int col, List<List<Object>> batch, byte[] isNull, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
            readIsNullData(in, isNull);
            for (int i = 0; i < batch.size(); i++) {
                if (!isNullObject(isNull, i)) {
                    batch.get(i).set(col, readObject(in, cache, version));
                }
            }
        }
        
        /**
         * @param out
         * @param obj
         * @param cache
         * @param version
         * @throws IOException
         */
        public void writeObject(ObjectOutput out, Object obj, Map<Object, Integer> cache, byte version) throws IOException {
            out.writeObject(obj);
        }

        /**
         * @param in
         * @param cache
         * @param version
         * @return read object
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public Object readObject(ObjectInput in, List<Object> cache, byte version) throws IOException, ClassNotFoundException {
            return in.readObject();
        }

        /**
         * @param version
         * @return true if serializer should cache, false by default
         */
        public boolean usesCache(byte version) {
            return false;
        }
    }

    /**
     * @param teiidVersion
     */
    protected BatchSerializer(ITeiidServerVersion teiidVersion, byte version) {
        this.teiidVersion = teiidVersion;
        this.currentVersion = version;
    }

    protected ITeiidServerVersion getTeiidVersion() {
        return this.teiidVersion;
    }

    /**
     * @return the currentVersion
     */
    protected byte getCurrentVersion() {
        return this.currentVersion;
    }

    protected DataTypeManagerService getDataTypeManager() {
        return DataTypeManagerService.getInstance(getTeiidVersion());
    }

    /**
     * @param teiidVersion
     * @return correct version of batch serializer according to teiid version
     */
    public static BatchSerializer getInstance(ITeiidServerVersion teiidVersion) {
            return new Batch4Serializer(teiidVersion);
    }

    protected void writeIsNullData(ObjectOutput out, int offset, Object[] batch) throws IOException {
        int currentByte = 0;
        for (int mask = 0x80; offset < batch.length; offset++, mask >>= 1) {
            if (batch[offset] == null) {
                currentByte |= mask;
            }
        }
        out.write(currentByte);
    }

    /**
     * Packs the (boolean) information about whether data values in the column are null
     * into bytes so that we send ~n/8 instead of n bytes.
     * @param out
     * @param col
     * @param batch
     * @throws IOException
     * @since 4.2
     */
    protected void writeIsNullData(ObjectOutput out, int col, List<? extends List<?>> batch) throws IOException {
        int numBytes = batch.size() / 8, row = 0, currentByte = 0;
        for (int byteNum = 0; byteNum < numBytes; byteNum++, row += 8) {
            currentByte = (batch.get(row).get(col) == null) ? 0x80 : 0;
            if (batch.get(row + 1).get(col) == null) {
                currentByte |= 0x40;
            }
            if (batch.get(row + 2).get(col) == null) {
                currentByte |= 0x20;
            }
            if (batch.get(row + 3).get(col) == null) {
                currentByte |= 0x10;
            }
            if (batch.get(row + 4).get(col) == null) {
                currentByte |= 0x08;
            }
            if (batch.get(row + 5).get(col) == null) {
                currentByte |= 0x04;
            }
            if (batch.get(row + 6).get(col) == null) {
                currentByte |= 0x02;
            }
            if (batch.get(row + 7).get(col) == null) {
                currentByte |= 0x01;
            }
            out.write(currentByte);
        }
        if (batch.size() % 8 > 0) {
            currentByte = 0;
            for (int mask = 0x80; row < batch.size(); row++, mask >>= 1) {
                if (batch.get(row).get(col) == null) {
                    currentByte |= mask;
                }
            }
            out.write(currentByte);
        }
    }


    /**
     * Reads the isNull data into a byte array
     * @param in
     * @param isNullBytes
     * @throws IOException
     * @since 4.2
     */
    protected void readIsNullData(ObjectInput in, byte[] isNullBytes) throws IOException {
        for (int i = 0; i < isNullBytes.length; i++) {
            isNullBytes[i] = in.readByte();
        }
    }

    /**
     * Gets whether a data value is null based on a packed byte array containing boolean data
     * @param isNull
     * @param row
     * @return
     * @since 4.2
     */
    protected boolean isNullObject(byte[] isNull, int row) {
        //              byte number           mask     bits to shift mask
        return (isNull[row / 8] & (0x01 << (7 - (row % 8)))) != 0;
    }

    protected boolean isNullObject(int row, byte b) {
        return (b & (0x01 << (7 - (row % 8)))) != 0;
    }

    /**
     * @param in
     * @param types
     * @return batch of results
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    public abstract List<List<Object>> readBatch(ObjectInput in, String[] types) throws IOException, ClassNotFoundException;

    /**
     * @param out
     * @param types
     * @param batch
     * @throws IOException
     */
    public abstract void writeBatch(ObjectOutput out, String[] types, List<? extends List<?>> batch) throws IOException;

    /**
     * @param out
     * @param types
     * @param batch
     * @param version
     * @throws IOException
     */
    public abstract void writeBatch(ObjectOutput out, String[] types, List<? extends List<?>> batch, byte version) throws IOException;
}
