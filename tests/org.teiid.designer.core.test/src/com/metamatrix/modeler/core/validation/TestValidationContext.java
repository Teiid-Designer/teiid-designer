/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * @since 4.0
 */
public final class TestValidationContext extends TestCase {
    /**
     * Constructor for TestJDBCRepositoryWriter.
     * 
     * @param name
     */
    public TestValidationContext( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        final TestSuite suite = new TestSuite(TestValidationContext.class);
        return new TestSetup(suite) {
            @Override
            protected void setUp() throws Exception {
                oneTimeSetUp();
            }

            @Override
            protected void tearDown() throws Exception {
                oneTimeTearDown();
            }

            public void oneTimeSetUp() {
            }

            public void oneTimeTearDown() {
            }

        };
    }

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    // =========================================================================
    // S E T U P A N D T E A R D O W N
    // =========================================================================

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static void oneTimeSetUp() {
    }

    public static void oneTimeTearDown() {
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    private ValidationContext helpCreateValidationContext() {
        return new ValidationContext();
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    // public void testgetPreferences(){
    // final StringBuffer failures = new StringBuffer();
    // final ValidationContext context = helpCreateValidationContext(new Preferences(), new ModelpathEntry[0]);
    //        
    // if(context.getPreferences() == null){
    //            failures.append("Invalid controls initial state"); //$NON-NLS-1$
    // }
    //        
    //        context.addControl("testing", this); //$NON-NLS-1$
    //        if(context.getControlValue("testing") != this ){ //$NON-NLS-1$
    //            failures.append("\nAdd control / get control value did not return correct value"); //$NON-NLS-1$
    // }
    //
    // if(failures.length() > 0 ){
    // fail(failures.toString() );
    // }
    // }

    public void testAddNullResults() {
        final ValidationContext context = helpCreateValidationContext();
        try {
            context.addResult(null);
        } catch (RuntimeException e) {
            // expected
            return;
        }

        fail("Was able to add null result object"); //$NON-NLS-1$
    }

    public void testAddResults() {
        final StringBuffer failures = new StringBuffer();
        final ValidationContext context = helpCreateValidationContext();
        Object target = new String("target"); //$NON-NLS-1$
        final ValidationResult result1 = new ValidationResultImpl(target);
        final ValidationResult result2 = new ValidationResultImpl(target);
        // These results don't have problems, so they won't be 'added'
        try {
            context.addResult(result1);
            if (context.hasResults()) {
                failures.append("Expected 0 result, but got " + context.getValidationResults().size()); //$NON-NLS-1$
            }

            context.addResult(result2);

            if (context.hasResults()) {
                failures.append("Expected 0 results, but got " + context.getValidationResults().size()); //$NON-NLS-1$
            }

            if (context.getLastResult() != null) {
                failures.append("Get last result did not return expected object"); //$NON-NLS-1$
            }

        } catch (Exception e) {
            failures.append("Unexpected exception : " + e.getMessage()); //$NON-NLS-1$
        }

        if (failures.length() > 0) {
            fail(failures.toString());
        }
    }

}
