/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Test;
import org.teiid.core.types.BinaryType;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class Test8DataTypeManagerService extends AbstractTestDataTypeManagerService {

    public Test8DataTypeManagerService() {
        super(Version.TEIID_8_0, Version.TEIID_8_1, Version.TEIID_8_2, Version.TEIID_8_3,
                    Version.TEIID_8_4, Version.TEIID_8_5, Version.TEIID_8_6, Version.TEIID_8_7);
    }

    @Test
    public void testGetDefaultDataType1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(DefaultDataTypes.VARBINARY.getId(), dataTypeManager.getDefaultDataType(DataTypeName.VARBINARY));
        }
    }

    @Test
    public void testGetDataType1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(DefaultDataTypes.VARBINARY, dataTypeManager.getDataType("varbinary"));
        }
    }

    @Test
    public void testGetDataTypeClass1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(BinaryType.class, dataTypeManager.getDataTypeClass("varbinary"));
        }
    }

    @Test
    public void testGetDefaultDataTypeClass1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(BinaryType.class, dataTypeManager.getDefaultDataClass(DataTypeName.VARBINARY));
        }
    }

    @Test
    public void testGetDataType4Class1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(DefaultDataTypes.VARBINARY, dataTypeManager.getDataType(BinaryType.class));
        }
    }

    @Test
    public void testGetAllDataTypeNames() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            Set<String> names = dataTypeManager.getAllDataTypeNames();
            assertTrue(!names.isEmpty());
            assertTrue(names.contains(DefaultDataTypes.BIG_DECIMAL.getId()));
            assertTrue(names.contains(DefaultDataTypes.STRING.getId()));
            assertTrue(names.contains(DefaultDataTypes.VARBINARY.getId()));
        }
    }

    @Test
    public void testGetDataSourceType1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertEquals(DataSourceTypes.SALESFORCE.id(), dataTypeManager.getDataSourceType(DataSourceTypes.SALESFORCE));
            assertEquals(DataSourceTypes.LDAP.id(), dataTypeManager.getDataSourceType(DataSourceTypes.LDAP));
            assertEquals(DataSourceTypes.FILE.id(), dataTypeManager.getDataSourceType(DataSourceTypes.FILE));
            assertEquals(DataSourceTypes.WS.id(), dataTypeManager.getDataSourceType(DataSourceTypes.WS));
        }
    }
}
