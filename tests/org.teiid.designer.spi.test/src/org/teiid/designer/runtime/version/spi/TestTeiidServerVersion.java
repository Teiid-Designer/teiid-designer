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

        assertFalse(version("8.0.x").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isGreaterThan(version("8.0.x"))); //$NON-NLS-1$ //$NON-NLS-2$

        assertFalse(version("8.x.0").isGreaterThan(version("8.0.0"))); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse(version("8.0.0").isGreaterThan(version("8.x.0"))); //$NON-NLS-1$ //$NON-NLS-2$
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
    }
}
