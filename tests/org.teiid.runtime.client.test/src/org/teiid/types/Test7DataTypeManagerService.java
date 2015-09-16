/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
public class Test7DataTypeManagerService extends AbstractTestDataTypeManagerService {

    public Test7DataTypeManagerService() {
        super(Version.TEIID_7_7);
    }

    @Test
    public void testGetDefaultDataType1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            try {
                dataTypeManager.getDefaultDataType(DataTypeName.VARBINARY);
                fail("VARBINARY should be not applicable");
            } catch (IllegalArgumentException ex) {
                // pass
            }
        }
    }

    @Test
    public void testGetDataType1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(DefaultDataTypes.OBJECT, dataTypeManager.getDataType("varbinary"));
        }
    }

    @Test
    public void testGetDataTypeClass1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(Object.class, dataTypeManager.getDataTypeClass("varbinary"));
        }
    }

    @Test
    public void testGetDefaultDataTypeClass1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            try {
                dataTypeManager.getDefaultDataClass(DataTypeName.VARBINARY);
                fail("VARBINARY should be not applicable");
            } catch (IllegalArgumentException ex) {
                // should throw an exception
            }
        }
    }

    @Test
    public void testGetDataType4Class1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            assertSame(DefaultDataTypes.OBJECT, dataTypeManager.getDataType(BinaryType.class));
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
            assertFalse(names.contains(DefaultDataTypes.VARBINARY.getId()));
        }
    }

    @Test
    public void testGetDataSourceType1() {
        for (Entry<ITeiidServerVersion, DataTypeManagerService> entry : dataTypeManagerCache.entrySet()) {
            DataTypeManagerService dataTypeManager = entry.getValue();

            // Should retrieve the old value from the Updated annotation
            assertEquals("connector-salesforce", dataTypeManager.getDataSourceType(DataSourceTypes.SALESFORCE));
            assertEquals("connector-ldap", dataTypeManager.getDataSourceType(DataSourceTypes.LDAP));
            assertEquals("connector-file", dataTypeManager.getDataSourceType(DataSourceTypes.FILE));
            assertEquals("connector-ws", dataTypeManager.getDataSourceType(DataSourceTypes.WS));
        }
    }
}
