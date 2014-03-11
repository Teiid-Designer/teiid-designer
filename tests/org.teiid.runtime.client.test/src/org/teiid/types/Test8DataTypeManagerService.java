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
import java.util.Set;
import org.junit.Test;
import org.teiid.core.types.BinaryType;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class Test8DataTypeManagerService extends AbstractTestDataTypeManagerService {

    public Test8DataTypeManagerService() {
        super(TeiidServerVersion.TEIID_8_SERVER);
    }

    @Test
    public void testGetDefaultDataType1() {
        assertSame(DefaultDataTypes.VARBINARY.getId(), dataTypeManager.getDefaultDataType(DataTypeName.VARBINARY));
    }

    @Test
    public void testGetDataType1() {
        assertSame(DefaultDataTypes.VARBINARY, dataTypeManager.getDataType("varbinary"));
    }

    @Test
    public void testGetDataTypeClass1() {
        assertSame(BinaryType.class, dataTypeManager.getDataTypeClass("varbinary"));
    }

    @Test
    public void testGetDefaultDataTypeClass1() {
        assertSame(BinaryType.class, dataTypeManager.getDefaultDataClass(DataTypeName.VARBINARY));
    }

    @Test
    public void testGetDataType4Class1() {
        assertSame(DefaultDataTypes.VARBINARY, dataTypeManager.getDataType(BinaryType.class));
    }

    @Test
    public void testGetAllDataTypeNames() {
        Set<String> names = dataTypeManager.getAllDataTypeNames();
        assertTrue(! names.isEmpty());
        assertTrue(names.contains(DefaultDataTypes.DECIMAL.getId()));
        assertTrue(names.contains(DefaultDataTypes.STRING.getId()));
        assertTrue(names.contains(DefaultDataTypes.VARBINARY.getId()));
    }

    @Test
    public void testGetDataSourceType1() {
        assertEquals(DataSourceTypes.SALESFORCE.id(), dataTypeManager.getDataSourceType(DataSourceTypes.SALESFORCE));
        assertEquals(DataSourceTypes.LDAP.id(), dataTypeManager.getDataSourceType(DataSourceTypes.LDAP));
        assertEquals(DataSourceTypes.FILE.id(), dataTypeManager.getDataSourceType(DataSourceTypes.FILE));
        assertEquals(DataSourceTypes.WS.id(), dataTypeManager.getDataSourceType(DataSourceTypes.WS));
    }
}
