/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import junit.framework.TestCase;

/**<p>
 * </p>
 * @since 4.0
 */
public final class TestI18nUtil extends TestCase
implements I18nUtil.Constants {
    //============================================================================================================================
    // Constants

    private static final String I18N_PROPERTY_CLASS_PREFIX =
        TestI18nUtil.class.getSimpleName() + PROPERTY_NAME_SEPARATOR_CHAR;

	//============================================================================================================================
	// Test Methods

    /**<p>
     * </p>
     * @since 4.0
     */
    public void testGetPropertyPrefix() {
        assertEquals(I18N_PROPERTY_CLASS_PREFIX, I18nUtil.getPropertyPrefix(getClass()));
        try {
            I18nUtil.getPropertyPrefix((Class)null);
        } catch (final IllegalArgumentException expected) {
        }
        assertEquals(I18N_PROPERTY_CLASS_PREFIX, I18nUtil.getPropertyPrefix(this));
        try {
            I18nUtil.getPropertyPrefix(null);
        } catch (final IllegalArgumentException expected) {
        }
    }
}
