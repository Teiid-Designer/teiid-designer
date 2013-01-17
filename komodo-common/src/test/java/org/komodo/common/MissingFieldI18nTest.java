/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common;

import org.junit.Test;
import org.komodo.common.i18n.I18n;

/**
 * A test class for {@link I18n}.
 */
@SuppressWarnings( {"javadoc"} )
public class MissingFieldI18nTest extends I18n {

    public static String field;

    @Test( expected = IllegalStateException.class )
    public void shouldNotInitialize() {
        final MissingFieldI18nTest i18n = new MissingFieldI18nTest();
        i18n.initialize();
    }

}
