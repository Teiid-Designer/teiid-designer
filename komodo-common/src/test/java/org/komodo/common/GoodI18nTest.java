/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komodo.common.i18n.I18n;

/**
 * A test class for {@link I18n}.
 */
@SuppressWarnings( {"javadoc", "nls"} )
public class GoodI18nTest extends I18n {

    public static String field;
    public static String patternOneArg;

    @BeforeClass
    public static void setup() {
        final GoodI18nTest i18n = new GoodI18nTest();
        i18n.initialize();
    }

    @Test
    public void shouldSetFieldWithNoArg() {
        assertThat(GoodI18nTest.field, is("test field"));
    }

    @Test
    public void shouldSetFieldWithOneArg() {
        final String arg = "argOne";
        assertThat(I18n.bind(GoodI18nTest.patternOneArg, arg), is("test pattern " + arg));
    }

}
