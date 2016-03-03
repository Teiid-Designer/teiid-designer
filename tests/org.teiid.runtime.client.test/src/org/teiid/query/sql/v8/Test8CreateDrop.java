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

package org.teiid.query.sql.v8;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.metadata.BaseColumn.NullType;
import org.teiid.query.sql.AbstractSqlTest;
import org.teiid.query.sql.AbstractTestFactory;
import org.teiid.query.sql.lang.Create;
import org.teiid.query.sql.lang.Drop;
import org.teiid.query.sql.symbol.ElementSymbol;

@SuppressWarnings( {"nls", "javadoc"} )
public class Test8CreateDrop extends AbstractSqlTest {

    private Test8Factory factory;

    protected Test8CreateDrop(Version teiidVersion) {
        super(teiidVersion);
    }

    public Test8CreateDrop() {
        this(Version.TEIID_8_0);
    }

    @Override
    protected AbstractTestFactory getFactory() {
        if (factory == null)
            factory = new Test8Factory(parser);

        return factory;
    }

    @Test
    public void testCreateTempTable1() {
        Create create = getFactory().newCreate();
        create.setTable(getFactory().newGroupSymbol("tempTable")); //$NON-NLS-1$
        List<ElementSymbol> columns = new ArrayList<ElementSymbol>();
        ElementSymbol column = getFactory().newElementSymbol("c1");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
        columns.add(column);
        column = getFactory().newElementSymbol("c2");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BYTE.getTypeClass());
        columns.add(column);
        create.setElementSymbolsAsColumns(columns);
        helpTest("Create local TEMPORARY table tempTable (c1 boolean, c2 byte)", "CREATE LOCAL TEMPORARY TABLE tempTable (c1 boolean, c2 byte)", create); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testCreateTempTable2() {
        Create create = getFactory().newCreate();
        create.setTable(getFactory().newGroupSymbol("tempTable")); //$NON-NLS-1$
        List<ElementSymbol> columns = new ArrayList<ElementSymbol>();
        ElementSymbol column = getFactory().newElementSymbol("c1");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
        columns.add(column);
        column = getFactory().newElementSymbol("c2");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BYTE.getTypeClass());
        columns.add(column);
        create.setElementSymbolsAsColumns(columns);
        create.getColumns().get(0).setNullType(NullType.No_Nulls);
        helpTest("Create local TEMPORARY table tempTable(c1 boolean not null, c2 byte)", "CREATE LOCAL TEMPORARY TABLE tempTable (c1 boolean NOT NULL, c2 byte)", create); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testCreateTempTable3() {
        helpException("Create TEMPORARY table tempTable (c1 boolean, c2 byte)"); //$NON-NLS-1$ 
    }

    @Test
    public void testCreateTempTable4() {
        helpException("Create table tempTable (c1 boolean, c2 byte)"); //$NON-NLS-1$ 
    }

    @Test
    public void testCreateTempTable5() {
        helpException("Create  local TEMPORARY table tempTable (c1 boolean primary, c2 byte)"); //$NON-NLS-1$ 
    }

    @Test
    public void testCreateTempTable7() {
        helpException("Create local TEMPORARY table tempTable (c1.x boolean, c2 byte)"); //$NON-NLS-1$ //$NON-NLS-2$ 
    }

    @Test
    public void testCreateTempTableWithPrimaryKey() {
        Create create = getFactory().newCreate();
        create.setTable(getFactory().newGroupSymbol("tempTable")); //$NON-NLS-1$
        List<ElementSymbol> columns = new ArrayList<ElementSymbol>();
        ElementSymbol column = getFactory().newElementSymbol("c1");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BOOLEAN.getTypeClass());
        columns.add(column);
        column = getFactory().newElementSymbol("c2");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BYTE.getTypeClass());
        columns.add(column);
        create.setElementSymbolsAsColumns(columns);
        create.getPrimaryKey().add(column);
        helpTest("Create local TEMPORARY table tempTable(c1 boolean, c2 byte, primary key (c2))", "CREATE LOCAL TEMPORARY TABLE tempTable (c1 boolean, c2 byte, PRIMARY KEY(c2))", create); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Test
    public void testDropTable() {
        Drop drop = getFactory().newDrop();
        drop.setTable(getFactory().newGroupSymbol("tempTable")); //$NON-NLS-1$
        helpTest("DROP table tempTable", "DROP TABLE tempTable", drop); //$NON-NLS-1$ //$NON-NLS-2$
    }

    //    @Test public void testForeignTemp() {
    //
    //        Datatype strDatatype = new Datatype();
    //        strDatatype.setUUID("mmuuid:bf6c34c0-c442-1e24-9b01-c8207cd53eb7");
    //        strDatatype.setBasetypeName("anySimpleType");
    //        strDatatype.setCaseSensitive(true);
    //        strDatatype.setJavaClassName("java.lang.String");
    //        strDatatype.setLength(4000);
    //        strDatatype.setName("string");
    //        strDatatype.setNameInSource("string");
    //        strDatatype.setPrecision(0);
    //        strDatatype.setRuntimeTypeName("string");
    //        strDatatype.setScale(0);
    //        strDatatype.setSearchType(Column.SearchType.Searchable);
    //        strDatatype.setType(Datatype.Type.Basic);
    //        strDatatype.setVarietyType(Datatype.Variety.Atomic);
    //
    //        Datatype decDatatype = new Datatype();
    //        decDatatype.setUUID("mmuuid:569dfa00-c456-1e24-9b01-c8207cd53eb7");
    //        decDatatype.setBasetypeName("anySimpleType");
    //        decDatatype.setCaseSensitive(false);
    //        decDatatype.setJavaClassName("java.math.BigDecimal");
    //        decDatatype.setLength(2147483647);
    //        decDatatype.setName("decimal");
    //        decDatatype.setNameInSource("decimal");
    //        decDatatype.setPrecision(2147483647);
    //        decDatatype.setRadix(10);
    //        decDatatype.setRuntimeTypeName("bigdecimal");
    //        decDatatype.setScale(2147483647);
    //        decDatatype.setSearchType(Column.SearchType.Searchable);
    //        decDatatype.setSigned(true);
    //        decDatatype.setType(Datatype.Type.Basic);
    //        decDatatype.setVarietyType(Datatype.Variety.Atomic);
    //
    //        Create create = getFactory().newCreate();
    //        create.setTable(getFactory().newGroupSymbol("tempTable")); //$NON-NLS-1$
    //        create.setOn("source");
    //        Table t = new Table();
    //        t.setName("tempTable");
    //        t.setUUID("tid:0");
    //        Column c = new Column();
    //        c.setName("x");
    //        c.setUUID("tid:0");
    //        c.setDatatype(strDatatype, true);
    //        t.addColumn(c);
    //        c = new Column();
    //        c.setName("y");
    //        c.setUUID("tid:0");
    //        c.setDatatype(decDatatype, true);
    //        t.addColumn(c);
    //        t.setCardinality(10000);
    //        create.setTableMetadata(t);
    //        helpTest("create foreign temporary table tempTable (x string, y decimal) options (cardinality 10000) on source", "CREATE FOREIGN TEMPORARY TABLE tempTable (\n	x string,\n	y bigdecimal\n) OPTIONS (CARDINALITY 10000) ON 'source'", create); //$NON-NLS-1$ //$NON-NLS-2$
    //    }

    @Test
    public void testBadCreate() {
        helpException("create insert"); //$NON-NLS-1$
    }

    @Test
    public void testTypeAliases() {
        Create create = getFactory().newCreate();
        create.setTable(getFactory().newGroupSymbol("tempTable")); //$NON-NLS-1$
        List<ElementSymbol> columns = new ArrayList<ElementSymbol>();
        ElementSymbol column = getFactory().newElementSymbol("c1");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass());
        columns.add(column);
        column = getFactory().newElementSymbol("c2");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BYTE.getTypeClass());
        columns.add(column);
        column = getFactory().newElementSymbol("c3");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.SHORT.getTypeClass());
        columns.add(column);
        column = getFactory().newElementSymbol("c4");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.FLOAT.getTypeClass());
        columns.add(column);
        column = getFactory().newElementSymbol("c5");//$NON-NLS-1$
        column.setType(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL.getTypeClass());
        columns.add(column);
        create.setElementSymbolsAsColumns(columns);
        helpTest("Create local TEMPORARY table tempTable (c1 varchar, c2 tinyint, c3 smallint, c4 real, c5 decimal)", "CREATE LOCAL TEMPORARY TABLE tempTable (c1 varchar, c2 tinyint, c3 smallint, c4 real, c5 decimal)", create); //$NON-NLS-1$ 
    }

}
