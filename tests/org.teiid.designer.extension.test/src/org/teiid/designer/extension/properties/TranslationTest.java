/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Constants;

/**
 * 
 */
public class TranslationTest {

    private Translation translation;

    @Before
    public void beforeEach() {
        this.translation = new Translation(Constants.DEFAULT_LOCALES[0], Constants.DEFAULT_TRANSLATION_TEXTS[0]);
    }

    @Test
    public void cloneShouldBeEquals() {
        assertEquals(this.translation, this.translation.clone());
    }

    @Test
    public void cloneShouldHaveSameHashCode() {
        assertEquals(this.translation.hashCode(), this.translation.clone().hashCode());
    }

    @Test
    public void cloneShouldNotBeExactlyEquals() {
        assertTrue(this.translation != this.translation.clone());
    }

    @Test
    public void shouldSetLocale() {
        Locale expected = Constants.DEFAULT_LOCALES[1];
        this.translation.setLocale(expected);
        assertEquals(expected, this.translation.getLocale());
    }

    @Test
    public void shouldSetLocaleAtConstruction() {
        assertEquals(Constants.DEFAULT_LOCALES[0], this.translation.getLocale());
    }

    @Test
    public void shouldSetText() {
        String expected = Constants.DEFAULT_TRANSLATION_TEXTS[1];
        this.translation.setTranslation(expected);
        assertEquals(expected, this.translation.getTranslation());
    }

    @Test
    public void shouldSetTextAtConstruction() {
        assertEquals(Constants.DEFAULT_TRANSLATION_TEXTS[0], this.translation.getTranslation());
    }

}
