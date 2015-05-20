/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.version.spi;

import junit.framework.TestCase;

/**
 *
 */
public class TestTeiidServerVersion extends TestCase {

    private ITeiidServerVersion version(String versionId) {
        return new TeiidServerVersion(versionId);
    }

    /**
     * Test {@link TeiidServerVersion#compareTo(ITeiidServerVersion)}
     */
    public void testCompareTo() {
        assertTrue(version("8.0.0").compareTo(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.0").compareTo(version("8.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(version("8.0.0").compareTo(version("8.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(version("8.0.0").compareTo(version("8.x.x"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.x").compareTo(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(version("8.x.0").compareTo(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(version("8.x.x").compareTo(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertFalse(version("8.0.0").compareTo(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").compareTo(version("8.1.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").compareTo(version("8.x.1"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").compareTo(version("9.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertFalse(version("7.0.0").compareTo(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.1.0").compareTo(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.x.1").compareTo(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("9.0.0").compareTo(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test {@link TeiidServerVersion#getMaximumVersion()}
     */
    public void testGetMaximum() {
        assertEquals(version("8.0.0"), version("8.0.0").getMaximumVersion()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(version("8.9.0"), version("8.x.0").getMaximumVersion()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(version("8.0.9"), version("8.0.x").getMaximumVersion()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(version("8.9.9"), version("8.x.x").getMaximumVersion()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test {@link TeiidServerVersion#getMinimumVersion()}
     */
    public void testGetMinimum() {
        assertEquals(version("8.0.0"), version("8.0.0").getMinimumVersion()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(version("8.0.0"), version("8.x.0").getMinimumVersion()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(version("8.0.0"), version("8.0.x").getMinimumVersion()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(version("8.0.0"), version("8.x.x").getMinimumVersion()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test {@link TeiidServerVersion#isGreaterThan(ITeiidServerVersion)}
     */
    public void testIsGreaterThan() {
        assertFalse(version("8.0.0").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.0").isGreaterThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.0.0").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.0").isGreaterThan(version("7.7.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.7.0").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.0").isGreaterThan(version("7.0.1"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.0.1").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.0").isGreaterThan(version("7.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.0.x").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.0").isGreaterThan(version("7.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.x.0").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.0").isGreaterThan(version("7.x.x"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.x.x").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.x.0").isGreaterThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.0.0").isGreaterThan(version("8.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.x").isGreaterThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.0.0").isGreaterThan(version("8.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.x.x").isGreaterThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.0.0").isGreaterThan(version("8.x.x"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.x").isGreaterThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.0.0").isGreaterThan(version("8.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.x.0").isGreaterThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("7.0.0").isGreaterThan(version("8.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.0.x").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isGreaterThan(version("8.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("8.x.0").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isGreaterThan(version("8.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        // silly micro version should be ignored since minor versions should be enough for the comparison
        assertTrue(version("8.1.extendedmicroversionid").isGreaterThan(version("8.0.0")));  //$NON-NLS-1$//$NON-NLS-2$
        
        // same minor versions up until 1 and 2
        assertTrue(version("8.designer-2.0").isGreaterThan(version("8.designer-1.0")));  //$NON-NLS-1$//$NON-NLS-2$

        // Comparing 1 and 10
        assertTrue(version("8.designer-10.0").isGreaterThan(version("8.designer-1.0")));  //$NON-NLS-1$//$NON-NLS-2$

        // 20 < 18 but designer > teiidteiid
        assertTrue(version("8.teiidteiid-18.0").isGreaterThan(version("8.designer-20.0")));  //$NON-NLS-1$//$NON-NLS-2$
        assertFalse(version("8.designer-20.0").isGreaterThan(version("8.teiidteiid-18.0")));  //$NON-NLS-1$//$NON-NLS-2$
        
        assertTrue(version("8.11.0").isGreaterThan(version("8.8.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(version("8.10.0").isGreaterThan(version("8.8.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertTrue(version("8.10.1").isGreaterThan(version("8.10.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.10.1").isGreaterThan(version("8.11.0"))); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Test {@link TeiidServerVersion#isLessThan(ITeiidServerVersion)}
     */
    public void testIsLessThan() {
        assertFalse(version("8.0.0").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.0.0").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isLessThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.7.0").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isLessThan(version("7.7.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.0.1").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isLessThan(version("7.0.1"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.0.x").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isLessThan(version("7.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.x.0").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isLessThan(version("7.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.x.x").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isLessThan(version("7.x.x"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.0.0").isLessThan(version("8.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.x.0").isLessThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.0.0").isLessThan(version("8.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.x").isLessThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.0.0").isLessThan(version("8.x.x"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.x.x").isLessThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.0.0").isLessThan(version("8.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.x").isLessThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertTrue(version("7.0.0").isLessThan(version("8.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.x.0").isLessThan(version("7.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertFalse(version("8.0.0").isLessThan(version("8.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.x").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertFalse(version("8.0.0").isLessThan(version("8.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.x.0").isLessThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        
        // silly micro version should be ignored since minor versions should be enough for the comparison
        assertTrue(version("8.0.0").isLessThan(version("8.1.extendedmicroversionid")));  //$NON-NLS-1$//$NON-NLS-2$
        
        // same minor versions up until 1 and 2
        assertTrue(version("8.designer-1.0").isLessThan(version("8.designer-2.0")));  //$NON-NLS-1$//$NON-NLS-2$

        // Comparing 1 and 10
        assertTrue(version("8.designer-1.0").isLessThan(version("8.designer-10.0")));  //$NON-NLS-1$//$NON-NLS-2$

        // 20 > 18 but designer < teiidteiid
        assertTrue(version("8.designer-20.0").isLessThan(version("8.teiidteiid-18.0")));  //$NON-NLS-1$//$NON-NLS-2$
        assertFalse(version("8.teiidteiid-18.0").isLessThan(version("8.designer-20.0")));  //$NON-NLS-1$//$NON-NLS-2$
        
	    assertTrue(version("8.7.0").isLessThan(version("8.7.1"))); //$NON-NLS-1$ //$NON-NLS-2$
	    assertTrue(version("8.8.0").isLessThan(version("8.10.0"))); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
