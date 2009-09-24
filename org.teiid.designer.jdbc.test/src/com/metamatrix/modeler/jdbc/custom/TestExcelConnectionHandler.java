/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.custom;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/** 
 * @since 4.3
 */
public class TestExcelConnectionHandler extends TestCase{
    public TestExcelConnectionHandler(String name) {
        super(name);
    }
    
    public static Test suite() {
        final TestSuite suite = new TestSuite(TestExcelConnectionHandler.class);
        return new TestSetup(suite);
    }
    
    public void testGetFilePath1() {
        String url="\"jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};DBQ=testdata/db1.mdb\"";//$NON-NLS-1$
        assertEquals("testdata/db1.mdb", ExcelConnectionHandler.getFilePath(url));//$NON-NLS-1$
    }
    
    public void testGetFilePath2() {
        String url="\"jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};dbq=testdata/db1.mdb\"";//$NON-NLS-1$
        assertEquals("testdata/db1.mdb", ExcelConnectionHandler.getFilePath(url));//$NON-NLS-1$
    }
    
    public void testGetFilePath3() {
        String url="\"jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};DBQ=e:/testdata/db1.mdb\"";//$NON-NLS-1$
        assertEquals("e:/testdata/db1.mdb", ExcelConnectionHandler.getFilePath(url));//$NON-NLS-1$
    }
    
    public void testGetFilePath4() {
        String url="\"jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};DBQ=e:/testdata/db1.mdb;anotherproperty=a\"";//$NON-NLS-1$;
        assertEquals("e:/testdata/db1.mdb", ExcelConnectionHandler.getFilePath(url));//$NON-NLS-1$
    }
    
    public void testGetFilePath5() {
        try {
            ExcelConnectionHandler.getFilePath( "\"jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)}\"");//$NON-NLS-1$
        }catch(IllegalArgumentException e) {
            //expected
            return;
        }
        fail("Expected exception, but did not get");//$NON-NLS-1$
    }
}
