/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.core.util;

import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
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

    public static void setBeanProperties(Object bean, Properties props, String prefix) {
        setBeanProperties(bean, props, prefix, false);
    }
    
    public static void setBeanProperties(Object bean, Properties props, String prefix, boolean caseSensitive) {
        // Move all prop names to lower case so we can use reflection to get
        // method names and look them up in the connection props.
        Map<?, ?> map = props;
        if (!caseSensitive) {
            map = caseInsensitiveProps(props);          
        }
        final Method[] methods = bean.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            final String methodName = method.getName();
            // If setter ...
            if (! methodName.startsWith("set") || method.getParameterTypes().length != 1 ) { //$NON-NLS-1$
                continue;
            }
            // Get the property name
            String propertyName = methodName.substring(3);    // remove the "set"
            if (prefix != null) {
                if (caseSensitive) {
                    propertyName = prefix + "." + Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1, propertyName.length()); //$NON-NLS-1$
                } else {
                    propertyName = prefix + "." + propertyName; //$NON-NLS-1$
                }
            }
            Object propertyValue = map.get(propertyName);
            if (propertyValue != null || map.containsKey(propertyName)) {
                setProperty(bean, propertyValue, method, propertyName);
            }
        }
    }
    
    public static void setBeanProperty(Object bean, String name, Object value) {
        final Method[] methods = bean.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            final Method method = methods[i];
            final String methodName = method.getName();
            // If setter ...
            if (! methodName.startsWith("set") || method.getParameterTypes().length != 1 || !StringUtil.endsWithIgnoreCase(methodName, name)) { //$NON-NLS-1$
                continue;
            }
            // Get the property name
            final String propertyName = methodName.substring(3);    // remove the "set"
            setProperty(bean, value, method, propertyName);
        }
    }

    private static Class<?> setProperty(Object bean, Object value,
            final Method method, final String propertyName) {
        final Class<?> argType = method.getParameterTypes()[0];
        try {
            Object[] params = new Object[] {value};
            if (value != null && !argType.isAssignableFrom(value.getClass())) {
                params = new Object[] {StringUtil.valueOf(value.toString(), argType)};
            }
            method.invoke(bean, params);
        } catch (Throwable e) {
            String msg = Messages.getString(Messages.InvalidPropertyException.message,
                                            propertyName,
                                            value,
                                            argType);
            throw new RuntimeException(new TeiidClientException(e, msg));
        }
        return argType;
    }

    private static TreeMap<String, String> caseInsensitiveProps(final Properties connectionProps) {
        final TreeMap<String, String> caseInsensitive = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        final Enumeration<?> itr = connectionProps.propertyNames();
        while ( itr.hasMoreElements() ) {
            final String name = (String) itr.nextElement();
            String propValue = connectionProps.getProperty(name);
            if (propValue != null || connectionProps.containsKey(name)) {
                caseInsensitive.put(name, propValue);
            } 
        }
        return caseInsensitive;
    }
}
