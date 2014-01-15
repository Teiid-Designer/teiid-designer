/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client.util;

import java.util.Properties;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;

/**
 *
 */
public class PropertiesUtil {

    public static boolean getBooleanProperty(Properties props, String propName, boolean defaultValue) {
        String stringVal = props.getProperty(propName);
        if(stringVal == null) {
            return defaultValue;
        }
        stringVal = stringVal.trim();
        if (stringVal.length() == 0) {
            return defaultValue;
        }
        try {
            return Boolean.valueOf(stringVal);
        } catch(NumberFormatException e) {
            String msg = Messages.getString(Messages.InvalidPropertyException.message, propName, stringVal, Float.class.getSimpleName());
            throw new RuntimeException(new TeiidClientException(e, msg));
        }
    }
}
