/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * <p>
 * This is the sole implementation for the {@link Debugger} interface. The static methods in this class are intended for use by
 * the {@link com.metamatrix.internal.ui.preferences.DebugPreferencePage}.
 * </p>
 * <p>
 * This class is thread-safe.
 * </p>
 * 
 * @since 4.0
 */
public final class DebuggerImpl implements ClassUtil.Constants, Debugger, Debugger.Constants {
    // ============================================================================================================================
    // Constants

    // Must not use ClassUtil to get I18n prefix due to class loader issues
    private static final String I18N_PREFIX = "DebuggerImpl."; //$NON-NLS-1$

    private static final String CONFLICT_WARNING = I18N_PREFIX + "conflictWarning"; //$NON-NLS-1$

    static final String MESSAGE_SEPARATOR = ": "; //$NON-NLS-1$
    static final char HASHCODE_SEPARATOR = '@';

    private static final String ENTERED_MESSAGE_PREFIX = "Entered "; //$NON-NLS-1$
    private static final String EXITED_MESSAGE_PREFIX = "Exited "; //$NON-NLS-1$

    private static final DateFormat TIMESTAMPER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); //$NON-NLS-1$

    private static final Map INDENTS = new HashMap(0);
    private static final String INDENT = "  "; //$NON-NLS-1$

    // ============================================================================================================================
    // Static Variables

    static Properties props;
    static Map propDescs, propVals;
    static boolean debugEnabled, traceEnabled;
    static boolean printThread, printTimestamp, indentMsg;
    static String output;

    // ============================================================================================================================
    // Static Methods

    /**
     * <p>
     * </p>
     * Loads all debug properties from the file system, overwriting any existing properties.
     * 
     * @since 4.0
     */
    public static void loadProperties() {
        final Properties installProps = new Properties();
        final Map propPlugins = new HashMap();
        final Map newPropDescs = new HashMap();
        final Map newPropVals = new HashMap();

        final Bundle[] bundles = CoreModelerPlugin.getDefault().getBundle().getBundleContext().getBundles();
        for (int bundleNdx = bundles.length; --bundleNdx >= 0;) {
            final Bundle bundle = bundles[bundleNdx];
            final String pluginId = bundle.getSymbolicName();
            try {
                final URL url = bundle.getEntry(FILENAME);
                final File file = (url == null ? null : new File(FileLocator.resolve(url).getFile()));
                if (file != null && file.exists()) {
                    final Properties props = new Properties();
                    props.load(file.toURI().toURL().openStream());
                    // Load properties, logging warnings for any name conflicts
                    for (final Iterator iter = props.entrySet().iterator(); iter.hasNext();) {
                        final Entry entry = (Entry)iter.next();
                        final String key = (String)entry.getKey();
                        final String val = (String)entry.getValue();
                        if (key.endsWith(DESCRIPTION_SUFFIX)) {
                            newPropDescs.put(getStoredKey(key.substring(0, key.length() - DESCRIPTION_SUFFIX.length())), val);
                        } else if (key.endsWith(VALUES_SUFFIX)) {
                            newPropVals.put(getStoredKey(key.substring(0, key.length() - VALUES_SUFFIX.length())), val);
                        } else if (TRACE_FILTERS_PROPERTY.equals(key)) {
                            installProps.put(key, val);
                        } else if (installProps.containsKey(key)) {
                            CoreModelerPlugin.Util.log(IStatus.WARNING, CoreModelerPlugin.Util.getString(CONFLICT_WARNING,
                                                                                                         key,
                                                                                                         pluginId,
                                                                                                         propPlugins.get(key)));
                        } else {
                            installProps.put(getStoredKey(key), val);
                            propPlugins.put(key, pluginId);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                // Ignore, This is now thrown if .debug does not exist.
            } catch (final IOException err) {
                CoreModelerPlugin.Util.log(err);
            }
        }

        // Load installed properties as both values and defaults
        final Properties newProps = new Properties(installProps);
        newProps.putAll(installProps);
        // Process existing properties
        final File file = getDebugFile();
        if (file.exists()) {
            try {
                // Load existing properties
                newProps.load(file.toURI().toURL().openStream());
                // Remove obsolete properties
                for (final Iterator iter = newProps.entrySet().iterator(); iter.hasNext();) {
                    final Entry entry = (Entry)iter.next();
                    final String key = (String)entry.getKey();
                    if (key.startsWith(TRACE_CLASSES_PREFIX)) {
                    } else if (!installProps.containsKey(key)) {
                        // Remove context
                        iter.remove();
                    }
                }
            } catch (final IOException err) {
                CoreModelerPlugin.Util.log(err);
            }
        }
        // Initialize option properties
        debugEnabled = Boolean.valueOf(newProps.getProperty(DEBUG_ENABLED_PROPERTY)).booleanValue();
        traceEnabled = Boolean.valueOf(newProps.getProperty(TRACING_PROPERTY)).booleanValue();
        output = newProps.getProperty(OUTPUT_DESTINATION_PROPERTY, SYSTEM_OUT);
        printThread = Boolean.valueOf(newProps.getProperty(PRINT_THREAD_PROPERTY)).booleanValue();
        printTimestamp = Boolean.valueOf(newProps.getProperty(PRINT_TIMESTAMP_PROPERTY)).booleanValue();
        indentMsg = Boolean.valueOf(newProps.getProperty(INDENT_MESSAGE_PROPERTY)).booleanValue();
        // Save properties
        props = newProps;
        propDescs = newPropDescs;
        propVals = newPropVals;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private static File getDebugFile() {
        final Bundle plugin = Platform.getBundle(CoreModelerPlugin.PLUGIN_ID);
        final IPath path = Platform.getStateLocation(plugin).append(FILENAME);
        final File file = path.toFile();
        return file;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private static void ensurePropertiesLoaded() {
        if (props == null || props.isEmpty()) {
            loadProperties();
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private static Properties getProperties() {
        ensurePropertiesLoaded();
        return props;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private static String getStoredKey( final String key ) {
        if (key.startsWith(RESERVED_PREFIX)) {
            final StringBuffer buf = new StringBuffer(key.length());
            for (final StringTokenizer iter = new StringTokenizer(key, RESERVED_PREFIX); iter.hasMoreTokens();) {
                buf.append(iter.nextToken());
            }
            return buf.toString();
        }
        return CONTEXTS_PREFIX + key;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private static boolean isPropertyEnabledRecursively( final String name ) {
        final int ndx = name.lastIndexOf(SEPARATOR);
        if (ndx > 0 && !isPropertyEnabledRecursively(name.substring(0, ndx))) {
            return false;
        }
        return isPropertyTrue(name);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static String getProperty( final String name ) {
        if (name == null) {
            return EMPTY_STRING;
        }
        return getProperties().getProperty(name);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static String getPropertyDescription( final String name ) {
        if (name == null) {
            return EMPTY_STRING;
        }
        ensurePropertiesLoaded();
        return (String)propDescs.get(name);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static String getPropertyValues( final String name ) {
        if (name == null) {
            return EMPTY_STRING;
        }
        ensurePropertiesLoaded();
        return (String)propVals.get(name);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static Iterator getPropertyIterator() {
        return getProperties().entrySet().iterator();
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static void setProperty( final String name,
                                    final String value ) {
        if (name == null) {
            final String msg = CoreModelerPlugin.Util.getString("DebuggerImpl.The_name_may_not_be_null"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        if (value == null) {
            final String msg = CoreModelerPlugin.Util.getString("DebuggerImpl.The_value_may_not_be_null"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        getProperties().setProperty(name, value);
        if (DEBUG_ENABLED_PROPERTY.equals(name)) {
            debugEnabled = Boolean.valueOf(value).booleanValue();
        } else if (TRACING_PROPERTY.equals(name)) {
            traceEnabled = Boolean.valueOf(value).booleanValue();
        } else if (OUTPUT_DESTINATION_PROPERTY.equals(name)) {
            output = value;
        } else if (PRINT_THREAD_PROPERTY.equals(name)) {
            printThread = Boolean.valueOf(value).booleanValue();
        } else if (PRINT_TIMESTAMP_PROPERTY.equals(name)) {
            printTimestamp = Boolean.valueOf(value).booleanValue();
        } else if (INDENT_MESSAGE_PROPERTY.equals(name)) {
            indentMsg = Boolean.valueOf(value).booleanValue();
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static boolean isPropertyBoolean( final String name ) {
        return (getPropertyValues(name) == null);
    }

    /**
     * <p>
     * </p>
     * Returns whether debugging is enabled for the specified property.
     * 
     * @return True if debugging is enabled for the specified property.
     * @since 4.0
     */
    public static boolean isPropertyTrue( final String name ) {
        if (name == null) {
            return false;
        }
        return (!isPropertyBoolean(name) || Boolean.valueOf(getProperty(name)).booleanValue());
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static boolean isPropertyEnabled( final String name ) {
        if (name == null) {
            return false;
        }
        boolean isEnabled = false;
        try {
            ensurePropertiesLoaded();
        } catch (RuntimeException e) {
            // If we can't load the properties.... return false;
            return isEnabled;
        }

        return (debugEnabled && isPropertyEnabledRecursively(name));
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static void saveProperties() throws IOException {
        // Remove all class-level properties with false values to conserve disk space.
        for (final Iterator iter = getPropertyIterator(); iter.hasNext();) {
            final Entry entry = (Entry)iter.next();
            final String key = (String)entry.getKey();
            if (key.startsWith(TRACE_CLASSES_PREFIX) && !Boolean.valueOf((String)entry.getValue()).booleanValue()) {
                iter.remove();
            }
        }
        getProperties().store(new FileOutputStream(getDebugFile()), null);
    }

    // ============================================================================================================================
    // Variables

    private String pluginId;

    /** Key = timer ID, value = start time (Double) */
    private Map timerIdStartMap;

    // ============================================================================================================================
    // Constructors

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    DebuggerImpl( final String id ) {
        this.pluginId = id;
        timerIdStartMap = new HashMap();
    }

    // ============================================================================================================================
    // Implemented Methods

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#debug(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void debug( final String context,
                       final String message ) {
        if (isDebugEnabled(context)) {
            print(context, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#getDebugContextProperty(java.lang.String)
     * @since 4.0
     */
    public String getDebugContextProperty( final String property ) {
        if (property == null) {
            return EMPTY_STRING;
        }
        return getProperty(CONTEXTS_PREFIX + property);
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#getTraceContextProperty(java.lang.String)
     * @since 4.0
     */
    public String getTraceContextProperty( final String property ) {
        if (property == null) {
            return EMPTY_STRING;
        }
        return getProperty(TRACE_CONTEXTS_PREFIX + property);
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#isDebugEnabled(java.lang.String)
     * @since 4.0
     */
    public boolean isDebugEnabled( final String context ) {
        return isPropertyEnabled(CONTEXTS_PREFIX + context);
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#isTraceEnabled(java.lang.String)
     * @since 4.0
     */
    public boolean isTraceEnabled( final String context ) {
        return isPropertyEnabled(TRACE_CONTEXTS_PREFIX + context);
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#isTraceEnabled(java.lang.Class)
     * @since 4.0
     */
    public boolean isTraceEnabled( final Class clazz ) {
        if (clazz == null) {
            return false;
        }
        return isPropertyEnabled(TRACE_CLASSES_PREFIX + this.pluginId + SEPARATOR
                                 + clazz.getName().replace(PACKAGE_SEPARATOR_CHAR, SEPARATOR_CHAR));
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#isTraceEnabled(java.lang.Object)
     * @since 4.0
     */
    public boolean isTraceEnabled( final Object object ) {
        if (object == null) {
            return false;
        }
        return isTraceEnabled(object.getClass());
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#print(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void print( final String context,
                       final String message ) {
        print(format(context, message));
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#print(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void print( final Class clazz,
                       final String message ) {
        if (traceEnabled) {
            print(format(clazz, message));
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#print(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void print( final Object object,
                       final String message ) {
        if (traceEnabled) {
            print(format(object, message));
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#printEntered(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void printEntered( final String context,
                              final String message ) {
        if (!traceEnabled) {
            return;
        }
        if (indentMsg) {
            final Thread thread = Thread.currentThread();
            String indent = (String)INDENTS.get(thread);
            indent = (indent == null) ? EMPTY_STRING : indent;
            print(indent + format(context, ENTERED_MESSAGE_PREFIX + message));
            INDENTS.put(thread, indent + INDENT);
        } else {
            print(context, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#printEntered(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void printEntered( final Class clazz,
                              final String message ) {
        if (!traceEnabled) {
            return;
        }
        if (indentMsg) {
            final Thread thread = Thread.currentThread();
            String indent = (String)INDENTS.get(thread);
            indent = (indent == null) ? EMPTY_STRING : indent;
            print(indent + format(clazz, ENTERED_MESSAGE_PREFIX + message));
            INDENTS.put(thread, indent + INDENT);
        } else {
            print(clazz, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#printEntered(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void printEntered( final Object object,
                              final String message ) {
        if (!traceEnabled) {
            return;
        }
        if (indentMsg) {
            final Thread thread = Thread.currentThread();
            String indent = (String)INDENTS.get(thread);
            indent = (indent == null) ? EMPTY_STRING : indent;
            print(indent + format(object, ENTERED_MESSAGE_PREFIX + message));
            INDENTS.put(thread, indent + INDENT);
        } else {
            print(object, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#printExited(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void printExited( final String context,
                             final String message ) {
        if (!traceEnabled) {
            return;
        }
        if (indentMsg) {
            final Thread thread = Thread.currentThread();
            String indent = (String)INDENTS.get(thread);
            indent = (indent == null) ? EMPTY_STRING : indent.substring(INDENT.length());
            print(indent + format(context, EXITED_MESSAGE_PREFIX + message));
            INDENTS.put(thread, indent);
        } else {
            print(context, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#printExited(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void printExited( final Class clazz,
                             final String message ) {
        if (!traceEnabled) {
            return;
        }
        if (indentMsg) {
            final Thread thread = Thread.currentThread();
            String indent = (String)INDENTS.get(thread);
            indent = (indent == null) ? EMPTY_STRING : indent.substring(INDENT.length());
            print(indent + format(clazz, EXITED_MESSAGE_PREFIX + message));
            INDENTS.put(thread, indent);
        } else {
            print(clazz, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#printExited(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void printExited( final Object object,
                             final String message ) {
        if (!traceEnabled) {
            return;
        }
        if (indentMsg) {
            final Thread thread = Thread.currentThread();
            String indent = (String)INDENTS.get(thread);
            indent = (indent == null) ? EMPTY_STRING : indent.substring(INDENT.length());
            print(indent + format(object, EXITED_MESSAGE_PREFIX + message));
            INDENTS.put(thread, indent);
        } else {
            print(object, message);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.core.util.Debugger#start(java.lang.String)
     */
    public void start( String theContextId ) {
        start(theContextId, theContextId);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.core.util.Debugger#start(java.lang.String, java.lang.String)
     */
    public void start( String theTimerId,
                       String theContextId ) {
        start(theTimerId, theContextId, null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.core.util.Debugger#start(java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void start( String theTimerId,
                       String theContextId,
                       Object[] theInfo ) {
        ArgCheck.isNotNull(theContextId);

        if (isDebugEnabled(theContextId)) {
            double startTime = System.currentTimeMillis();
            timerIdStartMap.put(theTimerId, new Double(startTime));
            Object info = null;

            if ((theInfo != null) && (theInfo.length > 0)) {
                info = new StringBuffer();
                StringBuffer sb = (StringBuffer)info;

                for (int i = 0; i < theInfo.length; i++) {
                    if (i != 0) {
                        sb.append(", "); //$NON-NLS-1$
                    }

                    sb.append(theInfo[i]);
                }
            } else {
                info = ""; //$NON-NLS-1$
            }

            print(CoreModelerPlugin.Util.getString("DebuggerImpl.timerStart", new Object[] {theTimerId, info})); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.core.util.Debugger#stop(java.lang.String)
     */
    public void stop( String theContextId ) {
        stop(theContextId, theContextId);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.core.util.Debugger#stop(java.lang.String, java.lang.String)
     */
    public void stop( String theTimerId,
                      String theContextId ) {
        stop(theTimerId, theContextId, null);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.core.util.Debugger#stop(java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void stop( String theTimerId,
                      String theContextId,
                      Object[] theInfo ) {
        ArgCheck.isNotNull(theContextId);

        if (isDebugEnabled(theContextId)) {
            Number startTime = (Number)timerIdStartMap.get(theTimerId);
            Object totalTime = null;
            Object info = null;

            if (startTime == null) {
                totalTime = CoreModelerPlugin.Util.getString("DebuggerImpl.timerNotFound"); //$NON-NLS-1$
            } else {
                timerIdStartMap.remove(theContextId);
                totalTime = new Double(System.currentTimeMillis() - startTime.doubleValue());

                if ((theInfo != null) && (theInfo.length > 0)) {
                    info = new StringBuffer();
                    StringBuffer sb = (StringBuffer)info;

                    for (int i = 0; i < theInfo.length; i++) {
                        if (i != 0) {
                            sb.append(", "); //$NON-NLS-1$
                        }

                        sb.append(theInfo[i]);
                    }
                } else {
                    info = ""; //$NON-NLS-1$
                }
            }

            print(CoreModelerPlugin.Util.getString("DebuggerImpl.timerStop", new Object[] {theTimerId, totalTime, info})); //$NON-NLS-1$
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#trace(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void trace( final String context,
                       final String message ) {
        if (isTraceEnabled(context)) {
            print(format(context, message));
        }
    }

    /**
     * <p>
     * Only intended for use within static methods.
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#trace(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void trace( final Class clazz,
                       final String message ) {
        if (isTraceEnabled(clazz)) {
            print(format(clazz, message));
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#trace(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void trace( final Object object,
                       final String message ) {
        if (isTraceEnabled(object)) {
            print(format(object, message));
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#traceEntered(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void traceEntered( final String context,
                              final String message ) {
        if (isTraceEnabled(context)) {
            printEntered(context, message);
        }
    }

    /**
     * <p>
     * Only intended for use within static methods.
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#traceEntered(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void traceEntered( final Class clazz,
                              final String message ) {
        if (isTraceEnabled(clazz)) {
            printEntered(clazz, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#traceEntered(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void traceEntered( final Object object,
                              final String message ) {
        if (isTraceEnabled(object)) {
            printEntered(object, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#traceExited(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void traceExited( final String context,
                             final String message ) {
        if (isTraceEnabled(context)) {
            printExited(context, message);
        }
    }

    /**
     * <p>
     * Only intended for use within static methods.
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#traceExited(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void traceExited( final Class clazz,
                             final String message ) {
        if (isTraceEnabled(clazz)) {
            printExited(clazz, message);
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.core.util.Debugger#traceExited(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void traceExited( final Object object,
                             final String message ) {
        if (isTraceEnabled(object)) {
            printExited(object, message);
        }
    }

    // ============================================================================================================================
    // Utility Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    String format( final String context,
                   final String message ) {
        return context + MESSAGE_SEPARATOR + message;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    String format( final Class clazz,
                   final String message ) {
        if (clazz != null) {
            return format(clazz.getName(), message);
        }
        return format((String)null, message);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    String format( final Object object,
                   final String message ) {
        if (object != null) {
            return format(object.getClass().getName() + HASHCODE_SEPARATOR + object.hashCode(), message);
        }
        return format((String)null, message);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    String format( String message ) {
        if (printThread) {
            message = Thread.currentThread().toString() + MESSAGE_SEPARATOR + message;
        }
        if (printTimestamp) {
            message = TIMESTAMPER.format(TIMESTAMPER.getCalendar().getTime()) + MESSAGE_SEPARATOR + message;
        }
        return message;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private void print( String message ) {
        PrintStream printer;
        boolean opened = false;
        if (SYSTEM_OUT.equals(output)) {
            printer = System.out;
        } else if (SYSTEM_ERROR.equals(output)) {
            printer = System.err;
        } else {
            try {
                printer = new PrintStream(new FileOutputStream(output, true));
                opened = true;
            } catch (final FileNotFoundException err) {
                printer = System.out;
                CoreModelerPlugin.Util.log(err);
            }
        }
        printer.println(format(message));
        if (opened) {
            printer.close();
        }
    }
}
