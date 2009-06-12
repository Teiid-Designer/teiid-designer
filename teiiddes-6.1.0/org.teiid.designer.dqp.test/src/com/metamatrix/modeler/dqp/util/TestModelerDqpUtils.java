/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.dqp.internal.config.FakeVdbEditingContext;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;

/**
 * @since 4.3
 */
public final class TestModelerDqpUtils extends TestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite("TestModelerDqpUtils"); //$NON-NLS-1$
        suite.addTestSuite(TestModelerDqpUtils.class);

        return suite;
    }

    public TestModelerDqpUtils( String theTestName ) {
        super(theTestName);
    }

    /**
     * Make sure exception thrown when VdbEditingContext is null
     */
    public void testGetModelImportSource1() {
        try {
            ModelerDqpUtils.getModelImportSource((VdbEditingContext)null, new BasicVDBModelDefn("test")); //$NON-NLS-1$
            fail("Did not get expected IllegalArgumentException for null VdbEditingContext"); //$NON-NLS-1$
        } catch (IllegalArgumentException theException) {
            // expected
        }
    }

    /**
     * Make sure exception thrown when ModelInfo is null
     */
    public void testGetModelImportSource2() {
        try {
            ModelerDqpUtils.getModelImportSource(new FakeVdbEditingContext(), null);
            fail("Did not get expected IllegalArgumentException for null ModelInfo"); //$NON-NLS-1$
        } catch (IllegalArgumentException theException) {
            // expected
        }
    }

    /**
     * Make sure no exceptions when non-null inputs
     */
    public void testGetModelImportSource3() {
        ModelerDqpUtils.getModelImportSource(new FakeVdbEditingContext(), new BasicVDBModelDefn("test")); //$NON-NLS-1$
    }

    /**
     * Make sure null name returns the right status code.
     */
    public void testIsValidBindingName1() {
        IStatus status = ModelerDqpUtils.isValidBindingName(null);
        assertEquals("IStatus returned the wrong code for null name", ModelerDqpUtils.BINDING_NAME_EMPTY_ERROR, status.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure empty name returns the right status code.
     */
    public void testIsValidBindingName2() {
        IStatus status = ModelerDqpUtils.isValidBindingName(""); //$NON-NLS-1$
        assertEquals("IStatus returned the wrong code for empty name", ModelerDqpUtils.BINDING_NAME_EMPTY_ERROR, status.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure a name that is too long returns the right status code.
     */
    public void testIsValidBindingName3() {
        // construct long name
        StringBuffer longName = new StringBuffer();
        for (int i = 0; i <= ModelerDqpUtils.BINDING_NAME_MAX_LENGTH; ++i) {
            longName.append('a');
        }

        IStatus status = ModelerDqpUtils.isValidBindingName(longName.toString());
        assertEquals("IStatus returned the wrong code for name max length error", ModelerDqpUtils.BINDING_NAME_MAX_LENGTH_ERROR, status.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure name with an invalid last character returns the right status code.
     */
    public void testIsValidBindingName4() {
        IStatus status = ModelerDqpUtils.isValidBindingName("BAD#"); //$NON-NLS-1$
        assertEquals("IStatus returned the wrong code for invalid last character in name", ModelerDqpUtils.BINDING_NAME_INVALID_CHAR_ERROR, status.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure name with an invalid first character returns the right status code.
     */
    public void testIsValidBindingName5() {
        IStatus status = ModelerDqpUtils.isValidBindingName("#BAD"); //$NON-NLS-1$
        assertEquals("IStatus returned the wrong code for invalid first character in name", ModelerDqpUtils.BINDING_NAME_INVALID_CHAR_ERROR, status.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure name with an invalid interior characters returns the right status code.
     */
    public void testIsValidBindingName6() {
        IStatus status = ModelerDqpUtils.isValidBindingName("BA#$D"); //$NON-NLS-1$
        assertEquals("IStatus returned the wrong code for invalid interior characters in name", ModelerDqpUtils.BINDING_NAME_INVALID_CHAR_ERROR, status.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure name beginning with a space returns the right status code.
     */
    public void testIsValidBindingName7() {
        IStatus status = ModelerDqpUtils.isValidBindingName(" name"); //$NON-NLS-1$
        assertEquals("IStatus returned the wrong code for name beginning with a space", ModelerDqpUtils.BINDING_NAME_WHITESPACE_ERROR, status.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure name ending with a space returns the right status code.
     */
    public void testIsValidBindingName8() {
        IStatus status = ModelerDqpUtils.isValidBindingName("name "); //$NON-NLS-1$
        assertEquals("IStatus returned the wrong code for name ending with a space", ModelerDqpUtils.BINDING_NAME_WHITESPACE_ERROR, status.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure name having consecutive spaces returns the right status code.
     */
    public void testIsValidBindingName9() {
        IStatus status = ModelerDqpUtils.isValidBindingName("na  me"); //$NON-NLS-1$
        assertEquals("IStatus returned the wrong code for name with a consecutive spaces", ModelerDqpUtils.BINDING_NAME_WHITESPACE_ERROR, status.getCode()); //$NON-NLS-1$
    }

}
