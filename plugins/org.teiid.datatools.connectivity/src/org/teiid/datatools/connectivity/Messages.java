/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.datatools.connectivity;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 *
 */
public class Messages {

    private static final String BUNDLE_NAME = "org.teiid.datatools.connectivity.messages"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private static final String DOT = "."; //$NON-NLS-1$

    @SuppressWarnings( "javadoc" )
    public enum ConnectivityUtil {
        noTeiidDriverDefinitionFound,
        errorGettingProfile,
        errorGettingProfileProperties,
        noTeiidDriverFound;

        @Override
        public String toString() {
            return getEnumName(this) + DOT + name();
        }
    }

    @SuppressWarnings( "javadoc" )
    public enum TeiidJDBCConnection {
        noDriverFound, 
        invalidUserPassword;

        @Override
        public String toString() {
            return getEnumName(this) + DOT + name();
        }
    }

    private static String getEnumName(Enum<?> enumValue) {
        String className = enumValue.getClass().getName();
        String[] components = className.split("\\$"); //$NON-NLS-1$
        return components[components.length - 1];
    }
    
    private Messages() {
    }

    /**
     * Get message string
     *
     * @param key
     *
     * @return i18n string
     */
    private static String getString(Enum<?> key) {
        try {
            return RESOURCE_BUNDLE.getString(key.toString());
        } catch (final Exception err) {
            String msg;

            if (err instanceof NullPointerException) {
                msg = "<No message available>"; //$NON-NLS-1$
            } else if (err instanceof MissingResourceException) {
                msg = "<Missing message for key \"" + key + "\" in: " + BUNDLE_NAME + '>'; //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                msg = err.getLocalizedMessage();
            }

            return msg;
        }
    }

    /**
     * Get message string with parameters
     *
     * @param key
     * @param parameters
     *
     * @return i18n string
     */
    public static String getString(Enum<?> key, Object... parameters) {
        String text = getString(key);

        // Check the trivial cases ...
        if (text == null) {
            return '<' + key.toString() + '>';
        }
        if (parameters == null || parameters.length == 0) {
            return text;
        }

        return MessageFormat.format(text, parameters);
    }
}
