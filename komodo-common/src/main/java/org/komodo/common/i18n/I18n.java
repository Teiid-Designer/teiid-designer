/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.i18n;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.komodo.common.CommonI18n;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.Precondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that can be subclassed and used to internationalize property files. All public, static, and non-final strings
 * in the subclass will be set to the default locale's value in the associated property file value. The placeholders used in 
 * the messages are those that are used by {@link Formatter}.
 */
public abstract class I18n {

    private class I18nProperties extends Properties {

        private static final long serialVersionUID = 297271848138379264L;

        private final Class<I18n> clazz;
        private final Collection<String> errors;
        private final Map<String, Field> fields;

        I18nProperties(final Map<String, Field> fields,
                       final Class<I18n> clazz,
                       final Collection<String> errors) {
            this.clazz = clazz;
            this.fields = fields;
            this.errors = errors;
        }

        /**
         * {@inheritDoc}
         *
         * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
         */
        @Override
        public synchronized Object put(final Object key,
                                       final Object value) {
            if (this.fields.containsKey(key)) {
                try {
                    final Field field = this.clazz.getDeclaredField((String)key);
                    field.set(null, value);
                    this.fields.remove(key);
                } catch (final Exception e) {
                    this.errors.add(I18n.bind(CommonI18n.problemAccessingI18Field, key, this.clazz.getName()));
                }
            } else {
                this.errors.add(I18n.bind(CommonI18n.missingI18Field, key, this.clazz.getName()));
            }

            return super.put(key, value);
        }

    }

    /**
     * @param pattern the message pattern (cannot be <code>null</code> or empty)
     * @param args the arguments being used to replace placeholders in the message (can be <code>null</code> or empty)
     * @return the localized message (never <code>null</code>)
     */
    public static String bind(final String pattern,
                              final Object... args) {
        Precondition.notEmpty(pattern, "pattern"); //$NON-NLS-1$
        return String.format(pattern, args);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Should be called in a <code>static</code> block to load the properties file and assign values to the class string fields.
     * 
     * @throws IllegalStateException if there is a problem reading the I8n class file or properties file
     */
    protected void initialize() {
        final Map<String, Field> fields = new HashMap<String, Field>();

        // collect all public, static, non-final, string fields
        try {
            for (final Field field : getClass().getDeclaredFields()) {
                final int modifiers = field.getModifiers();

                if ((field.getType() == String.class) && ((modifiers & Modifier.PUBLIC) == Modifier.PUBLIC)
                    && ((modifiers & Modifier.STATIC) == Modifier.STATIC) && ((modifiers & Modifier.FINAL) != Modifier.FINAL)) {
                    fields.put(field.getName(), field);
                }
            }
        } catch (final Exception e) {
            throw new IllegalStateException(I18n.bind(CommonI18n.problemLoadingI18nClass, getClass().getName()), e);
        }

        // return if nothing to do
        if (CollectionUtil.isEmpty(fields)) {
            return;
        }

        // load properties file
        InputStream stream = null;
        IllegalStateException problem = null;

        try {
            final Class thisClass = getClass();
            final String bundleName = thisClass.getName().replaceAll("\\.", "/").concat(".properties"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            final URL url = thisClass.getClassLoader().getResource(bundleName);
            stream = url.openStream();

            final Collection<String> errors = new ArrayList<String>();
            final Properties props = new I18nProperties(fields, thisClass, errors);
            props.load(stream);

            // log errors for any properties keys that don't have fields
            for (final String error : errors) {
                if (problem == null) {
                    problem = new IllegalStateException(error);
                }

                this.logger.error(error);
            }

            // log errors for any fields that don't have properties
            for (final String fieldName : fields.keySet()) {
                final String error = I18n.bind(CommonI18n.missingPropertiesKey, fieldName, getClass().getName());

                if (problem == null) {
                    problem = new IllegalStateException(error);
                }

                this.logger.error(error);
            }
        } catch (final Exception e) {
            throw new IllegalStateException(I18n.bind(CommonI18n.problemLoadingI18nProperties, getClass().getName()), e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (final Exception e) {
                } finally {
                    stream = null;
                }
            }

            if (problem != null) {
                throw problem;
            }
        }
    }

}
