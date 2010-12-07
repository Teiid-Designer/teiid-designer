/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.dialogs;

import java.io.File;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.teiid.core.util.EquivalenceUtil;
import com.metamatrix.core.util.SmartTestSuite;

/**
 * @since 5.0.1
 */
public final class TestFileUiUtils extends TestCase {

    private static File TEST_DIR;

    private static final FileUiUtils UTILS = FileUiUtils.INSTANCE;

    public static Test suite() {
        TestSuite result = new TestSuite("TestFileUiUtils"); //$NON-NLS-1$
        result.addTestSuite(TestFileUiUtils.class);

        return result;
    }

    static {
        TEST_DIR = new File(SmartTestSuite.getTestDataPath());
    }

    public TestFileUiUtils( String theTestName ) {
        super(theTestName);
    }

    /**
     * Make sure null input throws exception
     */
    public void testGetExistingCaseVariantFileName1() {
        try {
            UTILS.getExistingCaseVariantFileName((String)null);
            fail("Null input did not throw exception"); //$NON-NLS-1$
        } catch (IllegalArgumentException theException) {
            // expected result
        } catch (Exception theException) {
            fail("Expected AssertionException when input is null and got " + theException.getClass()); //$NON-NLS-1$
        }
    }

    /**
     * Make sure empty input throws exception
     */
    public void testGetExistingCaseVariantFileName2() {
        try {
            UTILS.getExistingCaseVariantFileName(""); //$NON-NLS-1$
            fail("Empty input did not throw exception"); //$NON-NLS-1$
        } catch (IllegalArgumentException theException) {
            // expected result
        } catch (Exception theException) {
            fail("Expected AssertionException when input is empty and got " + theException.getClass()); //$NON-NLS-1$
        }
    }

    /**
     * Make sure input containing all spaces throws exception
     */
    public void testGetExistingCaseVariantFileName3() {
        try {
            UTILS.getExistingCaseVariantFileName("          "); //$NON-NLS-1$
            fail("Input consisting of all spaces did not throw exception"); //$NON-NLS-1$
        } catch (IllegalArgumentException theException) {
            // expected result
        } catch (Exception theException) {
            fail("Expected AssertionException when input is empty and got " + theException.getClass()); //$NON-NLS-1$
        }
    }

    /**
     * Make sure return value is the same as the input value when a file doesn't exist
     */
    public void testGetExistingCaseVariantFileName4() {
        File temp = new File(TEST_DIR, "thisFileShouldNotExist"); //$NON-NLS-1$
        String name = temp.getAbsolutePath();
        assertEquals("A non-existent file did not return the input value", //$NON-NLS-1$
                     name,
                     UTILS.getExistingCaseVariantFileName(name));
    }

    /**
     * Make sure return value is the same as the input value when a file does exist having the same case
     */
    public void testGetExistingCaseVariantFileName5() {
        File temp = new File(TEST_DIR, "MyVdb.vdb"); //$NON-NLS-1$
        String name = temp.getAbsolutePath();
        assertEquals("Did not find file with same name and same case", //$NON-NLS-1$
                     name,
                     UTILS.getExistingCaseVariantFileName(name));
    }

    /**
     * Make sure leading/trailing spaces are ignored
     */
    public void testGetExistingCaseVariantFileName7() {
        File temp = new File(TEST_DIR, "   myvdb.vdb "); //$NON-NLS-1$
        String name = temp.getAbsolutePath();
        assertFalse("Did not find file with same name but case was different and had leading/trailing spaces", //$NON-NLS-1$
                    EquivalenceUtil.areEqual(name, UTILS.getExistingCaseVariantFileName(name)));
    }

}
