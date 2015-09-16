/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.types;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.types.NullType;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class AbstractTestDataTypeManagerService {

    protected final Map<ITeiidServerVersion, DataTypeManagerService> dataTypeManagerCache = new HashMap<ITeiidServerVersion, DataTypeManagerService>();

    /**
     * @param teiidVersion
     */
    public AbstractTestDataTypeManagerService(Version... teiidVersions) {
        for (Version teiidVersion : teiidVersions) {
            dataTypeManagerCache.put(teiidVersion.get(), DataTypeManagerService.getInstance(teiidVersion.get()));
        }
    }

    @Test
    public void testCachedInstance() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            ITeiidServerVersion teiidVersion = entry.getKey();
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(dataTypeManager, DataTypeManagerService.getInstance(teiidVersion));

            // Using old teiid version as unlikely ever to match
            assertNotSame(dataTypeManager, DataTypeManagerService.getInstance(new TeiidServerVersion("6.0.0"))); //$NON-NLS-1$
        }
    }

    @Test
    public void testGetDefaultDataType() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            // Test for null
            try {
                dataTypeManager.getDefaultDataType(null);
                fail("Should not allow null data type name");
            } catch (IllegalArgumentException ex) {
                // should throw an exception
            }

            // Top of the list
            assertSame(DefaultDataTypes.BIG_DECIMAL.getId(), dataTypeManager.getDefaultDataType(DataTypeName.BIGDECIMAL));

            // Middle of the list
            assertSame(DefaultDataTypes.BLOB.getId(), dataTypeManager.getDefaultDataType(DataTypeName.BLOB));
        }
    }

    @Test
    public void testGetDataType() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(DefaultDataTypes.NULL, dataTypeManager.getDataType((String)null));

            assertSame(DefaultDataTypes.STRING, dataTypeManager.getDataType("string"));
            assertSame(DefaultDataTypes.STRING, dataTypeManager.getDataType("STRING"));
            assertSame(DefaultDataTypes.STRING, dataTypeManager.getDataType("String[]"));

            assertSame(DefaultDataTypes.OBJECT, dataTypeManager.getDataType("NoSuchObject"));
        }
    }

    @Test
    public void testGetDataTypeClass() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(NullType.class, dataTypeManager.getDataTypeClass((String)null));

            assertSame(String.class, dataTypeManager.getDataTypeClass("string"));
            assertSame(String.class, dataTypeManager.getDataTypeClass("STRING"));
            assertSame(String[].class, dataTypeManager.getDataTypeClass("String[]"));

            assertSame(Object.class, dataTypeManager.getDataTypeClass("NoSuchObject"));
        }
    }

    @Test
    public void testGetDefaultDataTypeClass() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            try {
                dataTypeManager.getDefaultDataClass(null);
                fail("Should not allow null data type name");
            } catch (IllegalArgumentException ex) {
                // should throw an exception
            }

            assertSame(String.class, dataTypeManager.getDefaultDataClass(DataTypeName.STRING));
            assertSame(Object.class, dataTypeManager.getDefaultDataClass(DataTypeName.OBJECT));
        }
    }

    @Test
    public void testGetDataType4Class() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            try {
                dataTypeManager.getDataType((Class<?>)null);
                fail("Should not allow null class parameter");
            } catch (IllegalArgumentException ex) {
                // should throw an exception
            }

            assertSame(DefaultDataTypes.LONG, dataTypeManager.getDataType(Long.class));
            assertSame(DefaultDataTypes.STRING, dataTypeManager.getDataType(String.class));
        }
    }

    @Test
    public void testGetDataSourceType() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(DataSourceTypes.JDBC.id(), dataTypeManager.getDataSourceType(DataSourceTypes.JDBC));
            assertSame(DataSourceTypes.UNKNOWN.id(), dataTypeManager.getDataSourceType(DataSourceTypes.UNKNOWN));
        }
    }
}
