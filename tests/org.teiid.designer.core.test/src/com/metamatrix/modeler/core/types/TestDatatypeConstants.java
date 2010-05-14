/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.types;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.id.UUIDFactory;

/**
 * DatatypeConstantsTest
 */
public class TestDatatypeConstants extends TestCase {

    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------
    
    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------
    
    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    
    /**
     * Constructor for DatatypeConstantsTest.
     * @param name
     */
    public TestDatatypeConstants(String name) {
        super(name);
    }
    
    // =========================================================================
    //                        T E S T   C O N T R O L
    // =========================================================================
    
    /** 
     * Construct the test suite, which uses a one-time setup call
     * and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestDatatypeConstants"); //$NON-NLS-1$
        suite.addTestSuite(TestDatatypeConstants.class);
    
        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }
            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }
    
    // =========================================================================
    //                                 M A I N
    // =========================================================================
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }
    
    // =========================================================================
    //                 S E T   U P   A N D   T E A R   D O W N
    // =========================================================================
    
    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
    }
    
    /**
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
    }
    
    public static void oneTimeSetUp() {
    }
    
    public static void oneTimeTearDown() {
    }
    
    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    
    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    
//    public void testDataPathSupplied() {
//        UnitTestUtil.assertTestDataPathSet();
//    }
    
    public void testGetDatatypeNamefromRuntimeType() {
        String result = DatatypeConstants.getDatatypeNamefromRuntimeType(DatatypeConstants.RuntimeTypeNames.INTEGER);
        assertEquals(DatatypeConstants.BuiltInNames.INT,result);
    }
    
    public void testGetUuidParts() {
        UUID uuid = (UUID) DatatypeConstants.BUILTIN_DATATYPES_MODEL_UUID;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'BUILTIN_DATATYPES_MODEL'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_SCHEMA_UUID_1999;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_SCHEMA_UUID_1999'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_MAGIC_SCHEMA_UUID_1999;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_MAGIC_SCHEMA_UUID_1999'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_SCHEMA_INSTANCE_UUID_1999;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_SCHEMA_INSTANCE_UUID_1999'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_SCHEMA_UUID_2000_10;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_SCHEMA_UUID_2000_10'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_MAGIC_SCHEMA_UUID_2000_10;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_MAGIC_SCHEMA_UUID_2000_10'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_SCHEMA_INSTANCE_UUID_2000_10;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_SCHEMA_INSTANCE_UUID_2000_10'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_SCHEMA_UUID_2001;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_SCHEMA_UUID_2001'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_MAGIC_SCHEMA_UUID_2001;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_MAGIC_SCHEMA_UUID_2001'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) DatatypeConstants.XML_SCHEMA_INSTANCE_UUID_2001;
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'XML_SCHEMA_INSTANCE_UUID_2001'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        UUIDFactory factory = new UUIDFactory();
        uuid = (UUID) factory.create();
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", 'www.w3.org'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) factory.create();
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", '1999'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) factory.create();
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", '2000'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) factory.create();
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", '10'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

        uuid = (UUID) factory.create();
        System.out.println(uuid+", "+UUID.getPart1(uuid)+", "+UUID.getPart2(uuid)+", '2001'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

    }
    
}
