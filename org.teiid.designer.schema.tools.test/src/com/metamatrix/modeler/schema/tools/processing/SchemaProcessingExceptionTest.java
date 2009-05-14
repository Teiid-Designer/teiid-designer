/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.processing;

import junit.framework.TestCase;

public class SchemaProcessingExceptionTest extends TestCase {

    Exception testException;
    private static final String TEST_EXECPTION_MESSAGE = "Colorless green dreams sleep furiously"; //$NON-NLS-1$
    private static final String SPE_MESSAGE = "get Some"; //$NON-NLS-1$

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testException = new Exception(TEST_EXECPTION_MESSAGE);
    }

    public void testDefaultCtor() {
        SchemaProcessingException ex = new SchemaProcessingException();
        assertNull("This message should be null", ex.getMessage()); //$NON-NLS-1$
    }

    public void testStringCtor() {
        SchemaProcessingException ex = new SchemaProcessingException(SPE_MESSAGE);
        assertEquals("These messages should be the same", SPE_MESSAGE, ex.getMessage()); //$NON-NLS-1$
    }

    public void testThrowableCtor() {
        SchemaProcessingException ex = new SchemaProcessingException(testException);
        assertEquals("These throwables should be the same", testException, ex.getCause()); //$NON-NLS-1$
    }

    public void testStringThrowableCtor() {
        SchemaProcessingException ex = new SchemaProcessingException(SPE_MESSAGE, testException);
        assertEquals("These messages should be the same", SPE_MESSAGE, ex.getMessage()); //$NON-NLS-1$
        assertEquals("These throwables should be the same", testException, ex.getCause()); //$NON-NLS-1$
    }

}
