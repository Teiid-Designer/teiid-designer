package org.komodo.common;

import org.komodo.common.i18n.I18n;

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/

/**
 * Localized messages of the komodo-common module.
 */
@SuppressWarnings( {"javadoc"} )
public class CommonI18n extends I18n {

    public static String missingI18Field;
    public static String missingPropertiesKey;
    public static String problemAccessingI18Field;
    public static String problemLoadingI18nClass;
    public static String problemLoadingI18nProperties;

    public static String objectIsNotInstanceOf;
    public static String objectIsNotNull;
    public static String stringsDoNotMatchExactly;
    public static String collectionIsEmpty;
    public static String mapIsEmpty;
    public static String stringIsEmpty;
    public static String objectIsNull;

    static {
        final CommonI18n i18n = new CommonI18n();
        i18n.initialize();
    }

    /**
     * Don't allow public construction.
     */
    private CommonI18n() {
        // nothing to do
    }
}
