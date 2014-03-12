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
import org.teiid.client.batch.Batch0Serializer;
import org.teiid.client.batch.Batch1Serializer;
import org.teiid.client.batch.Batch2Serializer;
import org.teiid.client.batch.Batch3Serializer;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;

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
public abstract class BatchSerializer {

    protected static final ITeiidServerVersion TEIID_8_0 = TeiidServerVersion.TEIID_8_SERVER;

    protected static final ITeiidServerVersion TEIID_8_2 = new TeiidServerVersion(
                                                                                                                      ITeiidServerVersion.EIGHT,
                                                                                                                      ITeiidServerVersion.TWO,
                                                                                                                      ITeiidServerVersion.ZERO);

    protected static final ITeiidServerVersion TEIID_8_6 = TeiidServerVersion.TEIID_8_6_SERVER;

    private final ITeiidServerVersion teiidVersion;

    /**
     * @param teiidVersion
     */
    public BatchSerializer(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
    }

    protected ITeiidServerVersion getTeiidVersion() {
        return this.teiidVersion;
    }

    protected DataTypeManagerService getDataTypeManager() {
        return DataTypeManagerService.getInstance(getTeiidVersion());
    }

    /**
     * @param teiidVersion
     * @return correct version of batch serializer according to teiid version
     */
    public static BatchSerializer getInstance(ITeiidServerVersion teiidVersion) {
        if (teiidVersion.isLessThan(TEIID_8_0))
            return new Batch0Serializer(teiidVersion);
        else if (teiidVersion.isLessThan(TEIID_8_2))
            return new Batch1Serializer(teiidVersion);
        else if (teiidVersion.isLessThan(TEIID_8_6))
            return new Batch2Serializer(teiidVersion);
        else
            return new Batch3Serializer(teiidVersion);
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
