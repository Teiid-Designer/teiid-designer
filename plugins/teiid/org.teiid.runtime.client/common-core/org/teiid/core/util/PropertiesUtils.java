/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.core.util;

import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Performs a correct deep clone of the properties object by capturing
     * all properties in the default(s) and placing them directly into the
     * new Properties object.  If the input is an instance of
     * <code>UnmodifiableProperties</code>, this method returns an
     * <code>UnmodifiableProperties</code> instance around a new (flattened)
     * copy of the underlying Properties object.
     */
    public static Properties clone( Properties props ) {
        return clone(props, null, false);
    }

    /**
     * Performs a correct deep clone of the properties object by capturing
     * all properties in the default(s) and placing them directly into the
     * new Properties object.  If the input is an instance of
     * <code>UnmodifiableProperties</code>, this method returns an
     * <code>UnmodifiableProperties</code> instance around a new (flattened)
     * copy of the underlying Properties object.
     */
    public static Properties clone( Properties props, Properties defaults, boolean deepClone ) {
        Properties result = null;
        if ( defaults != null ) {
            if ( deepClone ) {
                defaults = clone(defaults);
            }
            result = new Properties(defaults);
        } else {
            result = new Properties();
        }
        
        putAll(result, props);
        
        return result;
    }

    /**
     * <p>This method is intended to replace the use of the <code>putAll</code>
     * method of <code>Properties</code> inherited from <code>java.util.Hashtable</code>.
     * The problem with that method is that, since it is inherited from
     * <code>Hashtable</code>, <i>default</i> properties are lost.
     * </p>
     * <p>For example, the following code
     * <pre><code>
     * Properties a;
     * Properties b;
     * //initialize ...
     * a.putAll(b);
     * </code></pre>
     * will fail <i>if</i> <code>b</code> had been constructed with a default
     * <code>Properties</code> object.  Those defaults would be lost and
     * not added to <code>a</code>.</p>
     *
     * <p>The above code could be correctly performed with this method,
     * like this:
     * <pre><code>
     * Properties a;
     * Properties b;
     * //initialize ...
     * PropertiesUtils.putAll(a,b);
     * </code></pre>
     * In the above example, <code>a</code> is modified - properties are added to
     * it (note that if <code>a</code> has defaults they will remain unaffected.)
     * The properties from <code>b</code>, <i>including defaults</i>, will be
     * added to <code>a</code> using its <code>setProperty</code> method -
     * these new properties will overwrite any pre-existing ones of the same
     * name.
     * </p>
     *
     * @param addToThis This Properties object is modified; the properties
     * of the other parameter are added to this.  The added property values
     * will replace any current property values of the same names.
     * @param withThese The properties (including defaults) of this
     * object are added to the "addToThis" parameter.
     */
    public static void putAll(Properties addToThis,
                              Properties withThese) {
        if ( withThese != null && addToThis != null ) {
            Enumeration enumeration = withThese.propertyNames();
            while ( enumeration.hasMoreElements() ) {
                String propName = (String) enumeration.nextElement();
                Object propValue = withThese.get(propName);
                if ( propValue == null ) {
                    //defaults can only be retrieved as strings
                    propValue = withThese.getProperty(propName);
                }
                if ( propValue != null ) {
                    addToThis.put(propName, propValue);
                }
            }
        }
    }

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

    public static void toHex(StringBuilder sb, InputStream is) throws IOException {
        int i = 0;
        while ((i = is.read()) != -1) {
            byte b = (byte)i;
            sb.append(toHex(b >>> 4));
            sb.append(toHex(b));
        }
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
