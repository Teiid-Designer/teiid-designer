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
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.types.NullType;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.type.IDataTypeManagerService.DataSourceTypes;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;

/**
 *
 */
@SuppressWarnings( {"javadoc", "nls"} )
public abstract class AbstractTestDataTypeManagerService {

    protected final ITeiidServerVersion teiidVersion;

    protected final DataTypeManagerService dataTypeManager;

    /**
     * @param teiidVersion
     */
    public AbstractTestDataTypeManagerService(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
        this.dataTypeManager = DataTypeManagerService.getInstance(teiidVersion);
    }

    @Test
    public void testCachedInstance() {
        assertSame(dataTypeManager, DataTypeManagerService.getInstance(teiidVersion));

        // Using old teiid version as unlikely ever to match
        assertNotSame(dataTypeManager, DataTypeManagerService.getInstance(new TeiidServerVersion("6.0.0"))); //$NON-NLS-1$
    }

    @Test
    public void testGetDefaultDataType() {
        // Test for null
        try {
            dataTypeManager.getDefaultDataType(null);
            fail("Should not allow null data type name");
        } catch (IllegalArgumentException ex) {
            // should throw an exception
        }

        // Top of the list
        assertSame(DefaultDataTypes.BIG_DECIMAL.getId(), dataTypeManager.getDefaultDataType(DataTypeName.BIG_DECIMAL));
        
        // Middle of the list
        assertSame(DefaultDataTypes.BLOB.getId(), dataTypeManager.getDefaultDataType(DataTypeName.BLOB));
    }   

    @Test
    public void testGetDataType() {
        assertSame(DefaultDataTypes.NULL, dataTypeManager.getDataType((String) null));

        assertSame(DefaultDataTypes.STRING, dataTypeManager.getDataType("string"));
        assertSame(DefaultDataTypes.STRING, dataTypeManager.getDataType("STRING"));
        assertSame(DefaultDataTypes.STRING, dataTypeManager.getDataType("String[]"));

        assertSame(DefaultDataTypes.OBJECT, dataTypeManager.getDataType("NoSuchObject"));
    }

    @Test
    public void testGetDataTypeClass() {
        assertSame(NullType.class, dataTypeManager.getDataTypeClass((String) null));

        assertSame(String.class, dataTypeManager.getDataTypeClass("string"));
        assertSame(String.class, dataTypeManager.getDataTypeClass("STRING"));
        assertSame(String[].class, dataTypeManager.getDataTypeClass("String[]"));

        assertSame(Object.class, dataTypeManager.getDataTypeClass("NoSuchObject"));
    }

    @Test
    public void testGetDefaultDataTypeClass() {
        try {
            dataTypeManager.getDefaultDataClass(null);
            fail("Should not allow null data type name");
        } catch (IllegalArgumentException ex) {
            // should throw an exception
        }

        assertSame(String.class, dataTypeManager.getDefaultDataClass(DataTypeName.STRING));
        assertSame(Object.class, dataTypeManager.getDefaultDataClass(DataTypeName.OBJECT));
    }

    @Test
    public void testGetDataType4Class() {
        try {
            dataTypeManager.getDataType((Class<?>) null);
            fail("Should not allow null class parameter");
        } catch (IllegalArgumentException ex) {
            // should throw an exception
        }

        assertSame(DefaultDataTypes.BIGINT, dataTypeManager.getDataType(Long.class));
        assertSame(DefaultDataTypes.STRING, dataTypeManager.getDataType(String.class));
    }

    @Test
    public void testGetDataSourceType() {
        assertSame(DataSourceTypes.JDBC.id(), dataTypeManager.getDataSourceType(DataSourceTypes.JDBC));
        assertSame(DataSourceTypes.UNKNOWN.id(), dataTypeManager.getDataSourceType(DataSourceTypes.UNKNOWN));
    }
}
