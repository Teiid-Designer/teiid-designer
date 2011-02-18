/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import junit.framework.TestCase;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.metamodels.core.util.UriValidator;


/**
 * Since most of the class being tested is just a pass through, if a value is valid or not is
 * generally not tested. Most of the tests are checking preconditions (like arguments are null) or
 * if the correct status code is returned. 
 * @since 4.3
 */
public final class TestUriValidator extends TestCase
                                    implements UriValidator.StatusCodes {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValid(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Make sure null value is OK
     */
    public void testIsValid1() {
        assertTrue("Null value should be valid", UriValidator.isValid(null)); //$NON-NLS-1$
    }

    /**
     * Make sure empty value is OK
     */
    public void testIsValid2() {
        assertTrue("Null value should be valid", UriValidator.isValid("")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validate(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure null value is OK
     */
    public void testValidate1() {
        UriValidator.validate(null);
    }

    /**
     * Make sure empty value is OK
     */
    public void testValidate2() {
        UriValidator.validate(""); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for good URI value.
     */
    public void testValidate3() {
        // valid URI
        String uri = "scheme://user@host:1234/segment1/segment2/segment3?query#fragment"; //$NON-NLS-1$
        
        IStatus result = UriValidator.validate(uri);
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid URI", VALID_URI, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for bad URI scheme component value.
     */
//    public void testValidate4() {
//        // invalid scheme
//        String uri = "sc/heme://user@host:1234/segment1/segment2/segment3?query#fragment"; //$NON-NLS-1$ // scheme can't start with number
//
//        IStatus result = UriValidator.validate(uri); // scheme can't contain a ?
//        assertNotNull("Status should never be null", result); //$NON-NLS-1$
//        assertEquals("Incorrect status code for an invalid URI scheme component", INVALID_SCHEME, result.getCode()); //$NON-NLS-1$
//    }

    /**
     * Make sure right status code for bad URI jar authority component value.
     */
//    public void testValidate5() {
//        // invalid jar authority
//        String uri = "jar:!//user@host:1234/segment1/segment2/segment3?query#fragment"; //$NON-NLS-1$
//
//        IStatus result = UriValidator.validate(uri); //$NON-NLS-1$ // jar authority can't contain a ?
//        assertNotNull("Status should never be null", result); //$NON-NLS-1$
//        assertEquals("Incorrect status code for an invalid URI jar authority component", INVALID_JAR_AUTHORITY, result.getCode()); //$NON-NLS-1$
//    }

    /**
     * Make sure right status code for bad URI authority component value.
     */
//    public void testValidate6() {
//        // invalid authority
//        String uri = "scheme://us?er@host:1234/segment1/segment2/segment3?query#fragment"; //$NON-NLS-1$
//
//        IStatus result = UriValidator.validate(uri); // authority can't contain a ?
//        assertNotNull("Status should never be null", result); //$NON-NLS-1$
//        assertEquals("Incorrect status code for an invalid URI authority component", INVALID_AUTHORITY, result.getCode()); //$NON-NLS-1$
//    }

    /**
     * Make sure right status code for bad URI segment array component value.
     */
//    public void testValidate7() {
//        // invalid segment array
//        String uri = "scheme://user@host:1234/segment1/segm?ent2/segment3?query#fragment"; //$NON-NLS-1$
//
//        IStatus result = UriValidator.validate(uri); // segment can't contain a ?
//        assertNotNull("Status should never be null", result); //$NON-NLS-1$
//        assertEquals("Incorrect status code for an invalid URI segment array component", INVALID_SEGMENTS, result.getCode()); //$NON-NLS-1$
//    }

    /**
     * Make sure right status code for bad URI query component value.
     */
//    public void testValidate8() {
//        // invalid query
//        String uri = "scheme://user@host:1234/segment1/segment2/segment3?que#ry#fragment"; //$NON-NLS-1$
//
//        IStatus result = UriValidator.validate(uri); // fragment can't contain a #
//        assertNotNull("Status should never be null", result); //$NON-NLS-1$
//        assertEquals("Incorrect status code for an invalid URI query component", INVALID_QUERY, result.getCode()); //$NON-NLS-1$
//    }

    /**
     * Make sure right status code for bad URI opaque part component value.
     */
//    public void testValidate9() {
//        // invalid opaque part
//        String uri = "J/ohn.Doe@example.com"; //$NON-NLS-1$
//
//        IStatus result = UriValidator.validate(uri); // opaque part can't contain a +
//        assertNotNull("Status should never be null", result); //$NON-NLS-1$
//        assertEquals("Incorrect status code for an invalid URI opaque part component", INVALID_OPAQUE_PART, result.getCode()); //$NON-NLS-1$
//    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidAuthority(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    // no need to test as this method is just a pass through to URI method.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateAuthority(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Make sure right status code for good authority value.
     */
    public void testValidateAuthority1() {
        // valid authority
        IStatus result = UriValidator.validateAuthority("abc"); //$NON-NLS-1$
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid authority", VALID_AUTHORITY, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for bad authority value.
     */
    public void testValidateAuthority2() {
        // invalid authority
        IStatus result = UriValidator.validateAuthority("?abc"); //$NON-NLS-1$ // authority can't contain a ?
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for an invalid authority", INVALID_AUTHORITY, result.getCode()); //$NON-NLS-1$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidDevice(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    // no need to test as this method is just a pass through to URI method.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateDevice(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure right status code for good device value.
     */
    public void testValidateDevice1() {
        // valid device
        IStatus result = UriValidator.validateDevice("abc:"); //$NON-NLS-1$
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid device", VALID_DEVICE, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for bad device value.
     */
    public void testValidateDevice2() {
        // invalid device
        IStatus result = UriValidator.validateDevice("abc"); //$NON-NLS-1$ // device must end in a colon
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for an invalid device", INVALID_DEVICE, result.getCode()); //$NON-NLS-1$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidFragment(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    // no need to test as this method is just a pass through to URI method.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateFragment(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure right status code for good fragment value.
     */
    public void testValidateFragment1() {
        // valid fragment
        IStatus result = UriValidator.validateFragment("abc"); //$NON-NLS-1$
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid fragment", VALID_FRAGMENT, result.getCode()); //$NON-NLS-1$
    }
    
    /**
     * No need to test an invalid fragment since all possible values are valid. 
     */

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidJarAuthority(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    // no need to test as this method is just a pass through to URI method.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateJarAuthority(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure right status code for good jar authority value.
     */
//    public void testValidateJarAuthority1() {
//        // valid jar authority
//        IStatus result = UriValidator.validateJarAuthority("jar:embeddedzipfile:file:/home/cprowse/data.zip?lib/config.jar!/"); //$NON-NLS-1$
//        assertNotNull("Status should never be null", result); //$NON-NLS-1$
//        assertEquals("Incorrect status code for valid jar authority", VALID_JAR_AUTHORITY, result.getCode()); //$NON-NLS-1$
//    }

    /**
     * Make sure right status code for bad jar authority value.
     */
    public void testValidateJarAuthority2() {
        // invalid jar authority
        IStatus result = UriValidator.validateArchiveAuthority("abc"); //$NON-NLS-1$ // jar authority must start with "jar"
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for an invalid jar authority", INVALID_ARCHIVE_AUTHORITY, result.getCode()); //$NON-NLS-1$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidOpaquePart(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    // no need to test as this method is just a pass through to URI method.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateOpaquePart(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure right status code for good opaque part value.
     */
    public void testValidateOpaquePart1() {
        // valid opaque part
        IStatus result = UriValidator.validateOpaquePart("abc"); //$NON-NLS-1$
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid opaque part", VALID_OPAQUE_PART, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for bad opaque part value.
     */
    public void testValidateOpaquePart2() {
        // invalid opaque part
        IStatus result = UriValidator.validateOpaquePart(null); // opaque part can't be null
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for an invalid opaque part", INVALID_OPAQUE_PART, result.getCode()); //$NON-NLS-1$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidQuery(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    // no need to test as this method is just a pass through to URI method.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateQuery(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure right status code for good query value.
     */
    public void testValidateQuery1() {
        // valid query
        IStatus result = UriValidator.validateQuery("abc"); //$NON-NLS-1$
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid query", VALID_QUERY, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for bad scheme value.
     */
    public void testValidateQuery2() {
        // invalid query
        IStatus result = UriValidator.validateQuery("abc#"); //$NON-NLS-1$ // query can't contain a #
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for an invalid query", INVALID_QUERY, result.getCode()); //$NON-NLS-1$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidScheme(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    // no need to test as this method is just a pass through to URI method.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateScheme(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure right status code for good scheme value.
     */
    public void testValidateScheme1() {
        // valid scheme
        IStatus result = UriValidator.validateScheme("abc"); //$NON-NLS-1$
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid scheme", VALID_SCHEME, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for bad scheme value.
     */
    public void testValidateScheme2() {
        // invalid scheme
        IStatus result = UriValidator.validateScheme("abc?"); //$NON-NLS-1$ // scheme can't contain a ?
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for an invalid scheme", INVALID_SCHEME, result.getCode()); //$NON-NLS-1$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidSegment(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    // no need to test as this method is just a pass through to URI method.
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateSegment(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure right status code for good segment value.
     */
    public void testValidateSegment1() {
        // valid segment
        IStatus result = UriValidator.validateSegment("abc"); //$NON-NLS-1$
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid segment", VALID_SEGMENT, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for bad segment value.
     */
    public void testValidateSegment2() {
        // invalid segment
        IStatus result = UriValidator.validateSegment("abc?"); //$NON-NLS-1$ // segment can't contain a ?
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for an invalid segment", INVALID_SEGMENT, result.getCode()); //$NON-NLS-1$
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // isValidSegments(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure exception thrown when segment array is null.
     */
    public void testIsValidSegments() {
        // null segment array
        try {
            UriValidator.isValidSegments(null);
            fail("AssertionException was not thrown for null segment array"); //$NON-NLS-1$
        } catch (IllegalArgumentException theException) {
            // pass
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // validateSegments(String) TEST CASES
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Make sure right status code for good segment array value.
     */
    public void testValidateSegments1() {
        // valid segment array
        IStatus result = UriValidator.validateSegments(new String[] {"abc", "def", "ghi"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for valid segment array", VALID_SEGMENTS, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure right status code for bad segment array value.
     */
    public void testValidateSegments2() {
        // invalid segment array
        IStatus result = UriValidator.validateSegments(new String[] {"abc", "def?", "ghi"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ // segment can't contain a ?
        assertNotNull("Status should never be null", result); //$NON-NLS-1$
        assertEquals("Incorrect status code for an invalid segment array", INVALID_SEGMENTS, result.getCode()); //$NON-NLS-1$
    }

    /**
     * Make sure exception thrown when segment array is null.
     */
    public void testValidateSegments3() {
        // null segment array
        try {
            UriValidator.validateSegments(null);
            fail("AssertionException was not thrown for null segment array"); //$NON-NLS-1$
        } catch (IllegalArgumentException theException) {
            // pass
        }
    }

}
