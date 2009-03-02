/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation.rules;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalFactory;


/** 
 * @since 5.0.2
 */
public class TestColumnNativeTypeRule extends TestCase {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final ColumnNativeTypeRule RULE = new ColumnNativeTypeRule();
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public static Test suite() {
        TestSuite suite = new TestSuite("TestColumnNativeTypeRule"); //$NON-NLS-1$
        suite.addTestSuite(TestColumnNativeTypeRule.class);
        
        return suite;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Column column;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public TestColumnNativeTypeRule(String theTestName) {
        super(theTestName);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see junit.framework.TestCase#setUp()
     * @since 5.0.2
     */
    @Override
    protected void setUp() throws Exception {
        this.column = RelationalFactory.eINSTANCE.createColumn();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // TESTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Make sure a null native type does not cause a validation error.
     */
    public void testValidate1() {
        this.column.setNativeType(null);
        assertNull("Null native type should not have a validation result.", RULE.validate(column)); //$NON-NLS-1$
    }

    /**
     * Make sure a valid native type does not cause a validation error.
     */
    public void testValidate2() {
        this.column.setNativeType("VARCHAR2"); //$NON-NLS-1$
        assertNull("An alphanumeric native type should not have a validation result.", RULE.validate(column)); //$NON-NLS-1$
    }

    /**
     * Make sure empty string causes a validation error. 
     */
    public void testValidate3() {
        this.column.setNativeType(""); //$NON-NLS-1$
        assertNotNull("Empty native type should have been a validation error.", RULE.validate(column)); //$NON-NLS-1$
    }

    /**
     * Make sure a string with only spaces causes a validation error. 
     */
    public void testValidate4() {
        this.column.setNativeType("    "); //$NON-NLS-1$
        assertNotNull("A native type consisting of all spaces should have been a validation error.", RULE.validate(column)); //$NON-NLS-1$
    }

//    /**
//     * Make sure a string with non-alphanumeric characters causes a validation error. 
//     */
//    public void testValidate5() {
//        this.column.setNativeType("VAR-CHAR"); //$NON-NLS-1$
//        assertNotNull("A native type consisting of invalid characters should have been a validation error.", RULE.validate(column)); //$NON-NLS-1$
//    }
//
//    /**
//     * Make sure a string with a leading space causes a validation error. 
//     */
//    public void testValidate6() {
//        this.column.setNativeType(" VARCHAR2"); //$NON-NLS-1$
//        assertNotNull("A native type with a beginning space should have been a validation error.", RULE.validate(column)); //$NON-NLS-1$
//    }
//
//    /**
//     * Make sure a string with a trailing space causes a validation error. 
//     */
//    public void testValidate7() {
//        this.column.setNativeType("VARCHAR2 "); //$NON-NLS-1$
//        assertNotNull("A native type with a trailing space should have been a validation error.", RULE.validate(column)); //$NON-NLS-1$
//    }
//
//    /**
//     * Make sure a string with valid special characters does not cause a validation error. 
//     */
//    public void testValidate8() {
//        this.column.setNativeType(ColumnNativeTypeRule.VALID_SPECIAL_CHARS);
//        assertNull("A native type with parens should not have been a validation error.", RULE.validate(column)); //$NON-NLS-1$
//    }
//
}
