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
public final class TestValidationResultImpl extends TestCase {
    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    /**
     * Constructor for TestJDBCRepositoryWriter.
     * @param name
     */
    public TestValidationResultImpl(String name) {
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
        final TestSuite suite = new TestSuite(TestValidationResultImpl.class);
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

    // =========================================================================
    //                 S E T   U P   A N D   T E A R   D O W N
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
    //                         H E L P E R    M E T H O D S
    // =========================================================================
    private ValidationProblem helpCreateProblem1(){
        ValidationProblemImpl problem = new ValidationProblemImpl(-1, IStatus.ERROR, "My ERROR message"); //$NON-NLS-1$
        return problem;
    }
    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    /**
     * @since 4.0
     */
    public void testConstruction() {
        final ValidationResultImpl test = new ValidationResultImpl(new Object());
        assertNotNull(test);
        assertNotNull(test.getProblems() );      
    }

    public void testAddProblem(){
        final ValidationResultImpl test = new ValidationResultImpl(new Object());
        final int count = test.getProblems().length;

        test.addProblem(helpCreateProblem1() );
        final int problems = test.getProblems().length;

        if(problems != (count + 1) ){
            fail("Expected " + (count + 1) + " problems, but found " + problems);  //$NON-NLS-1$//$NON-NLS-2$
        } 
    }

    public void testIsFatalObject(){
        Object obj = new String("XYZ"); //$NON-NLS-1$
        final ValidationResultImpl test = new ValidationResultImpl(obj);

        if(test.isFatalObject(obj) ){
            fail("expected initial isFatal to be false, but it was true"); //$NON-NLS-1$
        }

        test.addProblem(helpCreateProblem1());

        if(!test.isFatalObject(obj)){
            fail("expected initial isFatal to be true, but it was false"); //$NON-NLS-1$
        }
    }

    public void testIsFatalResource(){
        final ValidationResultImpl test = new ValidationResultImpl(new Object());

        if(test.isFatalResource() ){
            fail("expected initial isFatal to be false, but it was true"); //$NON-NLS-1$
        }

        test.setFatalResource(true);

        if(!test.isFatalResource() ){
            fail("expected initial isFatal to be true, but it was false"); //$NON-NLS-1$
        }
    }

}
