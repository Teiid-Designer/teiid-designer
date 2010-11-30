/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools;

import junit.framework.TestCase;

public class NameUtilTest extends TestCase {

    private static String dots;
    private static String parens;
    private static String trailingUnderscore;
    private static String duplicate;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dots = new String("foo.bar"); //$NON-NLS-1$
        parens = new String("foo(bar)"); //$NON-NLS-1$
        trailingUnderscore = new String("foo_bar_"); //$NON-NLS-1$
        duplicate = new String("foo_bar(foo_bar)"); //$NON-NLS-1$
    }

    /*
     * Test method for 'com.metamatrix.modeler.schema.tools.NameUtil.normalizeNameForRelationalTable(String)'
     */
    public void testDots() {
        assertEquals("foo_bar", NameUtil.normalizeName(dots)); //$NON-NLS-1$
    }

    public void testParens() {
        assertEquals("foo_bar", NameUtil.normalizeName(parens)); //$NON-NLS-1$
    }

    public void testUnderscore() {
        assertEquals("foo_bar", NameUtil.normalizeName(trailingUnderscore)); //$NON-NLS-1$
    }

    public void testDuplicate() {
        assertEquals("foo_bar", NameUtil.normalizeName(duplicate)); //$NON-NLS-1$
    }

}
