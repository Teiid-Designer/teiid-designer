/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.custom;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/** 
 * @since 4.3
 */
public class TestExcelDatabaseMetaDataHandler  extends TestCase{
    public TestExcelDatabaseMetaDataHandler(String name) {
        super(name);
    }
    
    public static Test suite() {
        final TestSuite suite = new TestSuite(TestExcelDatabaseMetaDataHandler.class);
        return new TestSetup(suite);
    }
    
    public void testLoadExcelDocument1() throws Exception{
        File excelTestFile = new File("testdata/Book1.xls");//$NON-NLS-1$
        ExcelDatabaseMetaDataHandler handler = new ExcelDatabaseMetaDataHandler(null, excelTestFile);
        handler.loadExcelDocument();
        assertTrue(handler.tables.next());
        assertEquals("Test Table1", handler.tables.getString(3));//$NON-NLS-1$
        assertTrue(handler.tables.next());
        assertEquals("TestTable2", handler.tables.getString(3));//$NON-NLS-1$     
        assertTrue(!handler.tables.next());
        
        assertNotNull(handler.columns.get("Test Table1".toUpperCase()));//$NON-NLS-1$
        assertNotNull(handler.columns.get("TestTable2".toUpperCase()));//$NON-NLS-1$
        assertEquals(2, handler.columns.size());
    }
    
    public void testResultSetMetadata() throws Exception{
        File excelTestFile = new File("testdata/Book1.xls");//$NON-NLS-1$
        ExcelDatabaseMetaDataHandler handler = new ExcelDatabaseMetaDataHandler(null, excelTestFile);
        handler.loadExcelDocument();
        ResultSetMetaData rm = ((ResultSet)handler.columns.get("Test Table1".toUpperCase())).getMetaData();//$NON-NLS-1$
        assertEquals("TABLE_CAT", rm.getColumnName(1));//$NON-NLS-1$
        assertEquals("TABLE_CAT", rm.getColumnLabel(1));//$NON-NLS-1$     
    }
}
