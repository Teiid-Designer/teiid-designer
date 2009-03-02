/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.loader;

import java.util.Properties;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.common.config.xml.XMLElementNames;
import com.metamatrix.vdb.internal.def.VDBDefPropertyNames;


/**
 */
public class TestVDBXMLHelper extends TestCase {

    private static final String V_3_1 = "3.1";//$NON-NLS-1$

    private static final String V_4_1 = "4.1";//$NON-NLS-1$
    private static final String V_4_2 = "4.2";//$NON-NLS-1$
    private static final String V_4_3 = "4.3";//$NON-NLS-1$

    private static final String C_4_1 = "4.1";//$NON-NLS-1$
    private static final String C_4_2 = "4.2";//$NON-NLS-1$
    private static final String C_4_3 = "4.3";//$NON-NLS-1$

    public TestVDBXMLHelper(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
    }
    
    /**
     * These test when the VDBVersion property is specified in the header 
     * @throws Exception
     * @since 4.2
     */
    public void testVDBCompatibility() throws Exception {
        Properties props = new Properties();
        
        props.setProperty(VDBDefPropertyNames.VDB_EXPORTER_VERSION, V_4_1);
        
        if (!VDBDefnXMLHelper.is41Compatible(props)) {
            fail("Version " + V_4_1 + " should have been 4.1 compatible");//$NON-NLS-1$ //$NON-NLS-2$
            
        }
        
        props = new Properties();
        
        props.setProperty(VDBDefPropertyNames.VDB_EXPORTER_VERSION, V_4_2);
        
        if (!VDBDefnXMLHelper.is41Compatible(props)) {
            fail("Version " + V_4_2 + " should have been 4.1 compatible");//$NON-NLS-1$ //$NON-NLS-2$
            
        }
        
        props = new Properties();
        
        props.setProperty(VDBDefPropertyNames.VDB_EXPORTER_VERSION, V_4_3);
        
        if (!VDBDefnXMLHelper.is41Compatible(props)) {
            fail("Version " + V_4_3 + " should have been 4.1 compatible");//$NON-NLS-1$ //$NON-NLS-2$
            
        }        
        
    } 
    
    /**
     * These test when the VDBVersion property is NOT specified in the header,
     * but rely on the Configuration version 
     * @throws Exception
     * @since 4.2
     */
    public void testVDBConfigurationCompatibility() throws Exception {
        
        Properties props = new Properties();
        
        props.setProperty(XMLElementNames.Header.ConfigurationVersion.ELEMENT, C_4_2);
        
        if (!VDBDefnXMLHelper.is41Compatible(props)) {
            fail("Configuration Version " + C_4_2 + " should have been 4.1 compatible");//$NON-NLS-1$ //$NON-NLS-2$
            
        }
        
        props = new Properties();
        
        props.setProperty(XMLElementNames.Header.ConfigurationVersion.ELEMENT, C_4_3);
        
        if (!VDBDefnXMLHelper.is41Compatible(props)) {
            fail("Configuration Version " + C_4_3 + " should have been 4.1 compatible");//$NON-NLS-1$ //$NON-NLS-2$
            
        }        
        
    }    
    
    /**
     * These test when the VDBVersion property is NOT specified in the header,
     * but rely on the Configuration version 
     * @throws Exception
     * @since 4.2
     */
    public void testVDBConfigurationInCompatibility() throws Exception {
        
        Properties props = new Properties();
        
        props.setProperty(XMLElementNames.Header.ConfigurationVersion.ELEMENT, C_4_1);
        
        if (VDBDefnXMLHelper.is41Compatible(props)) {
            fail("Configuration Version " + V_4_1 + " should have not been 4.1 compatible");//$NON-NLS-1$ //$NON-NLS-2$
            
        }
        
    }     
    
    
    public void testVDBInCompatibility() throws Exception {
        Properties props = new Properties();
        
        props.setProperty(VDBDefPropertyNames.VDB_EXPORTER_VERSION, V_3_1);
        
        if (VDBDefnXMLHelper.is41Compatible(props)) {
            fail("Version " + V_3_1 + " should have not been 4.1 compatible");//$NON-NLS-1$ //$NON-NLS-2$
            
        }
        
      
        
    }      
    

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);

    }

    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestVDBXMLHelper.class);

        return suite;
    }
}

