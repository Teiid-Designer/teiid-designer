/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.core.util;

import java.util.Properties;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;

/**
 *
 */
public class PropertiesUtils {

    public static boolean getBooleanProperty(Properties props, String propName, boolean defaultValue) {
        String stringVal = props.getProperty(propName);
        if (stringVal == null) {
            return defaultValue;
        }
        stringVal = stringVal.trim();
        if (stringVal.length() == 0) {
            return defaultValue;
        }
        try {
            return Boolean.valueOf(stringVal);
        } catch (NumberFormatException e) {
            String msg = Messages.getString(Messages.InvalidPropertyException.message,
                                            propName,
                                            stringVal,
                                            Float.class.getSimpleName());
            throw new RuntimeException(new TeiidClientException(e, msg));
        }
    }

    public static int getIntProperty(Properties props, String propName, int defaultValue) {
        String stringVal = props.getProperty(propName);
        if(stringVal == null) {
            return defaultValue;
        }
        stringVal = stringVal.trim();
        if (stringVal.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(stringVal);
        } catch(NumberFormatException e) {
            String msg = Messages.getString(Messages.InvalidPropertyException.message,
                                            propName,
                                            stringVal,
                                            Integer.class.getSimpleName());
            throw new RuntimeException(new TeiidClientException(e, msg));
        }
    }

    /** A table of hex digits */
    private static final char[] hexDigit = { '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F' };

    /**
     * Convert a nibble to a hex character
     * @param   nibble  the nibble to convert.
     */
    public static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(toHex(b >>> 4));
            sb.append(toHex(b));
        }
        return sb.toString();
    }
}
