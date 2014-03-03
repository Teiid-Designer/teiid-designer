/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.metamodels.relational.aspects.validation;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 *
 */
public class TestRelationalStringNameValidator extends TestCase {

    private RelationalStringNameValidator validator;

    /**
     * Constructor for TestJdbcTableImpl.
     * 
     * @param name
     */
    public TestRelationalStringNameValidator() {
        super();
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        validator = new RelationalStringNameValidator();
    }
    
    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestRelationalStringNameValidator"); //$NON-NLS-1$
        suite.addTestSuite(TestRelationalStringNameValidator.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            }

            @Override
            public void tearDown() {
            }
        };
    }
    
    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testSimpleTableNameIgnoreRestriction() {
    	validator.setUp(true, false);
    	
    	String result = validator.checkValidName("ValidTableName"); //$NON-NLS-1$
    	assertNull(result);
    }
    
    public void testInvalidSimpleTableNameIgnoreRestriction_1() {
    	validator.setUp(true, false);
    	
    	String result = validator.checkValidName("Valid.TableName"); //$NON-NLS-1$
    	assertNotNull(result);
    }
    
    public void testComplexTableNameIgnoreRestriction_2() {
    	validator.setUp(true, false);
    	
    	String result = validator.checkValidName("Valid$(%$(%*$%&TableName"); //$NON-NLS-1$
    	assertNull(result);
    }
    
    public void testInvalidSimpleTableNameIgnoreRestriction_3() {
    	validator.setUp(true, false);
    	
    	String result = validator.checkValidName("Invalid TableName"); //$NON-NLS-1$
    	assertNotNull(result);
    }
    
    public void testDoubleQuotedTableNameIgnoreRestriction() {
    	validator.setUp(true, false);
    	
    	String result = validator.checkValidName("\"Valid.Table.Name\""); //$NON-NLS-1$
    	assertNull(result);
    }
    
    public void testDoubleQuotedTableNameWithRestriction() {
    	validator.setUp(true, true);
    	
    	String result = validator.checkValidName("\"Valid.Table.Name\""); //$NON-NLS-1$
    	assertNull(result);
    }
    
    public void testInvalidComplexTableNameWithRestriction_2() {
    	validator.setUp(true, true);
    	
    	String result = validator.checkValidName("Invalid$(%$(%*$%&TableName"); //$NON-NLS-1$
    	assertNotNull(result);
    }
    
    public void testInvalidSimpleTableNameWithRestriction_3() {
    	validator.setUp(true, true);
    	
    	String result = validator.checkValidName("Invalid TableName"); //$NON-NLS-1$
    	assertNotNull(result);
    }
    
    public void testSimpleNameIgnoreRestriction() {
    	validator.setUp(false, false);
    	
    	String result = validator.checkValidName("ValidTableName"); //$NON-NLS-1$
    	assertNull(result);
    }
    
    public void testInvalidSimpleNameIgnoreRestriction_1() {
    	validator.setUp(false, false);
    	
    	String result = validator.checkValidName("Valid.TableName"); //$NON-NLS-1$
    	assertNotNull(result);
    }
    
    public void testComplexNameIgnoreRestriction_2() {
    	validator.setUp(false, false);
    	
    	String result = validator.checkValidName("Valid$(%$(%*$%&TableName"); //$NON-NLS-1$
    	assertNull(result);
    }
    
    public void testInvalidSimpleNameIgnoreRestriction_3() {
    	validator.setUp(false, false);
    	
    	String result = validator.checkValidName("Invalid TableName"); //$NON-NLS-1$
    	assertNotNull(result);
    }
    
    public void testDoubleQuotedNameIgnoreRestriction() {
    	validator.setUp(false, false);
    	
    	String result = validator.checkValidName("\"Valid.Table.Name\""); //$NON-NLS-1$
    	assertNull(result);
    }
    
    public void testDoubleQuotedNameWithRestriction() {
    	validator.setUp(false, true);
    	
    	String result = validator.checkValidName("\"Valid.Table.Name\""); //$NON-NLS-1$
    	assertNull(result);
    }
    
    public void testInvalidComplexNameWithRestriction_2() {
    	validator.setUp(false, true);
    	
    	String result = validator.checkValidName("Invalid$(%$(%*$%&TableName"); //$NON-NLS-1$
    	assertNotNull(result);
    }
    
    public void testInvalidSimpleNameWithRestriction_3() {
    	validator.setUp(false, true);
    	
    	String result = validator.checkValidName("Invalid TableName"); //$NON-NLS-1$
    	assertNotNull(result);
    }

}
