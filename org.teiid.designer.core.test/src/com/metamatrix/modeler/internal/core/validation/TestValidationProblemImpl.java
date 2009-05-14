/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.validation;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.core.validation.ValidationProblem;

/**
 * @since 4.0
 */
public final class TestValidationProblemImpl extends TestCase {
    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    /**
     * Constructor for TestJDBCRepositoryWriter.
     * @param name
     */
    public TestValidationProblemImpl(String name) {
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
        final TestSuite suite = new TestSuite(TestValidationProblemImpl.class);
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

    // =========================================================================
    //                                 M A I N
    // =========================================================================

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    public static void oneTimeSetUp() {
    }

    public static void oneTimeTearDown() {
    }

    // =========================================================================
    //                         H E L P E R    M E T H O D S
    // =========================================================================
    protected ValidationProblem helpCreateProblem(final int code, final int severity, final String message){
        ValidationProblem problem = new ValidationProblemImpl(code, severity, message);
        
        return problem;
    }
    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    /**
     * @since 4.0
     */
    public void testConstruction() {
        StringBuffer failures = new StringBuffer();
        ValidationProblem problem = null;
        //test valid construction 1
        try {
            problem = helpCreateProblem(-1, IStatus.ERROR , "test"); //$NON-NLS-1$
        } catch (Exception e) {
            failures.append("\nUnexpected exception creating valid construction 1"); //$NON-NLS-1$
        }
        
        //test valid construction 1 (null target is originally being considered valid)
        try {
            problem = helpCreateProblem(-1, IStatus.ERROR , "test"); //$NON-NLS-1$
        } catch (Exception e) {
            failures.append("\nUnexpected exception creating valid construction 2"); //$NON-NLS-1$
        }
        
        //test invalid construction 1 (invalid severity)
        try {
            problem = null;
            problem = helpCreateProblem(-1, -4 , "test"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            //expected
        }
        
        if(problem != null){
            failures.append("\nInvalid construction 2 failure : Expected invalid construction"); //$NON-NLS-1$
        }
        
        if(failures.length() > 0 ){
            fail(failures.toString() );
        }
    }
    
    public void testToString(){
        StringBuffer failures = new StringBuffer();
        ValidationProblem problem = helpCreateProblem(-1, IStatus.ERROR, "My Message");  //$NON-NLS-1$
        if(!problem.toString().equals("Error - My Message") ){ //$NON-NLS-1$
            failures.append("Expected toString \"Error My Message\" but got " + problem.toString() ); //$NON-NLS-1$
        }
        
        problem = helpCreateProblem(-1, IStatus.INFO, "My Message");  //$NON-NLS-1$
        if(!problem.toString().equals("Info - My Message") ){ //$NON-NLS-1$
            failures.append("Expected toString \"Info My Message\" but got " + problem.toString() ); //$NON-NLS-1$
        }

        problem = helpCreateProblem(-1, IStatus.OK, "My Message");  //$NON-NLS-1$
        if(!problem.toString().equals("OK - My Message") ){ //$NON-NLS-1$
            failures.append("Expected toString \"Info My Message\" but got " + problem.toString() ); //$NON-NLS-1$
        }

        problem = helpCreateProblem(-1, IStatus.WARNING, "My Message");  //$NON-NLS-1$
        if(!problem.toString().equals("Warning - My Message") ){ //$NON-NLS-1$
            failures.append("Expected toString \"Info My Message\" but got " + problem.toString() ); //$NON-NLS-1$
        }

        problem = helpCreateProblem(-1, IStatus.WARNING, "My Message");  //$NON-NLS-1$ 
        if(!problem.toString().equals("Warning - My Message") ){ //$NON-NLS-1$
            failures.append("Expected toString \"Info My Message\" but got " + problem.toString() ); //$NON-NLS-1$
        }
        
        if(failures.length() > 0 ){
            fail(failures.toString() );
        }
    }
}
