/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

/**
 * <p>
 * This is the interface to the Federate Designer Debug Facility, which provides debugging capabilities for all Federate Designer
 * plug-ins <em>and</em> third-party extensions to Federate Designer extension points. Debug code is enable or disabled via a set
 * of Boolean properties, which are generally associated with particular "contexts", or application processes. These properties
 * can be modified in one of three ways:
 * </p>
 * <ul>
 * <li>Changing the debug settings in a client Federate Designer application's
 * {@link com.metamatrix.internal.ui.preferences.DebugPreferencePage debug preference page},</li>
 * <li>Programatically via the static methods in this interface's sole {@link DebuggerImpl implementation} , or</li>
 * <li>By manually editing the {@link Constants#FILENAME debug properties file} that resides in the
 * {@link CoreModelerPlugin#PLUGIN_ID Core} plug-in's state folder.</li></u>
 * <p>
 * Properties are structured in a hierarchical manner, such that each property (except the
 * {@link Constants#DEBUG_ENABLED_PROPERTY root}) will be defined within a parent "group" property, and will potentially act as a
 * group property for one or more child properties. With this in mind, note that although the actual value of a Boolean value may
 * be set to true, Boolean values are only considered "enabled" if all of their ancestor group properties are also enabled.
 * </p>
 * <p>
 * <strong>Contexts</strong>
 * </p>
 * <p>
 * The term "context" refers to a process, activity, or aspect within an application. An example of a context might be all
 * application code involved in the a repository search process. The execution of debug code associated with a particular context
 * can be enabled or disabled via corresponding Boolean debug properties. In some cases, the behavior of this code can further be
 * controlled by additional non-Boolean debug properties associated with the same context.
 * </p>
 * <p>
 * <strong>Tracing</strong>
 * </p>
 * <p>
 * For more advance lower-level debugging (which is generally also much more output-intensive), a tracing feature is also
 * available. In addition to contexts specific to tracing, this feature offers class-based tracing, which provides the abililty to
 * enable or disable trace code at individual plug-in, package, and class levels.
 * </p>
 * <p>
 * <strong>Options</strong>
 * </p>
 * <p>
 * Several options can be configured that drive how the debug facility itself executes, all of which are related to debug output:
 * </p>
 * <ul>
 * <li>
 * {@link Constants#OUPUT_DESTINATION_PROPERTY Debug output} can be setup to be printed to either the operating system's
 * {@link Constants#SYSTEM_OUT standard output} stream, the {@link Constants#SYSTEM_ERROR standard error} stream, or a writable
 * file.</li>
 * <li>Information for the {@link Constants#PRINT_THREAD_PROPERTY current thread} can optionally be prefixed to each debug
 * message.</li>
 * <li>A {@link Constants#PRINT_TIMESTAMP_PROPERTY timestamp} can optionally be prefixed to each debug message.</li>
 * <li>Trace messages can optionally be {@link Constants#INDENT_MESSAGE_PROPERTY indented} to highlight the hierarchy of method
 * calls (Only applies to class-level tracing).</li></u>
 * <p>
 * There also exists one non-configurable option within a plug-in's installation debug properties file that specifies filters for
 * which class names must be matched against in order for a class to participate in class-level tracing.
 * </p>
 * <p>
 * <strong>Architecture and Configuration</strong>
 * </p>
 * <p>
 * The Debug Facility is driven by {@link Constants#FILENAME properties files} that reside in each plug-in's installation folder.
 * These files follow the same structure as defined by a standard {@link java.util.ResourceBundle resource bundle}. Properties
 * that have special meaning to the Debug Facility are prefixed with a {@link Constants#RESERVED_PREFIX reserved character} that
 * must not be used within the names of any contexts defined by plug-ins. With the exception of the {@link Constants@TRACE_FILTERS_PROPERTY
 * trace filters} property, all reserved properties are defined in the {@link CoreModelerPlugin#PLUGIN_ID Core} plug-in. All
 * property names are specified in a hierarchical manner, such that each name includes the names of its ancestors, in order,
 * separated by a special {@link Constants#SEPARATOR name separation character}. Note, however, that the
 * {@link Constants#DEBUG_ENABLED_PROPERTY root} property that controls the enabling of the entire Debug Facility, is an exception
 * to this rule, and does not appear within other property names.
 * </p>
 * <p>
 * The properties within each of these files are used to discover the available contexts, their default values, their allowable
 * values, and their descriptions. Class-level tracing properties do not require any setup within these files, although
 * descriptions may be provided. Each debug context, option, or group will have up to three properties defined in the properties
 * file.
 * </p>
 * <ul>
 * <li>The actual property, whose value represents the default value of a context if one is not explicitly set.</li>
 * <li>A description property that describes the context or group. This is the only property that may also be specified for traced
 * classes. A description property's name must exactly match the name of the actual property, followed by the reserved
 * {@link Constants#DESCRIPTION_SUFFIX description suffix}.</li>
 * <li>
 * <p>
 * An allowed values property that describes the type of values that a context may contain. An allowed values property's name must
 * exactly match the name of the actual property, followed by the reserved {@link Constants#VALUES_SUFFIX allowed values suffix}.
 * This property's value may be a set of strings, separated by either a {@link Constants#LIST_DELIMITER list delimiter}, which
 * signifies the actual property may contain multiple values, or a {@link Constants#CHOICE_DELIMITER choice delimiter}, which
 * indicates the actual value may contain only one of the string values listed. A {@link Constants#NONE_VALUE special reserved
 * value} may be specified if no values are allowed for the actual property. For sets of choices, two additional,
 * mutually-exclusive reserved values may be specified to indicate that either {@link Debugger#ANY_VALUE any value} or, more
 * specifically, a {@link Constants#FILE_VALUE file name} may be included as a choice. For exammple:
 * </p>
 * <p>
 * <code>contextGroup/contextA$values = value1 | value2 | $file</code>
 * </p>
 * </li></u>
 * <p>
 * Each plug-in may also include a {@link Constants@TRACE_FILTERS_PROPERTY trace filters} property that adds to the global list of
 * filters for which fully-qualified class names must be matched against in order for a class to participate in class-level
 * tracing. Fully-qualified in this case includes the plug-in ID in wich the class is defined. For example,
 * </p>
 * <p>
 * <code>$traceFilters = com.yourcompany.somepluginsuffix/com.yourcompany.somepluginsuffix.somepackage</code>
 * </p>
 * <p>
 * will only allow classes within the plug-in with the ID "com.yourcompany.somepluginsuffix" and defined under the package
 * "com.yourcompany.somepluginsuffix.somepackage" to be traced. Unlike Java, packages are treated as hierarchical, and therefore
 * classes defined in "sub-packages" of the package in this example would also be included.
 * </p>
 * <p>
 * Modifications to properties' default values are stored in a properties file with the same name as the installation file and
 * which resides in the {@link CoreModelerPlugin#PLUGIN_ID Core} plug-in's runtime state folder. This file may be modified
 * directly for server-side and non-GUI applications. Unlike the installation properties file, contexts and class names in this
 * file must include the full hierarchical path back to the {@link Constants#CONTEXTS_PROPERTY contexts property} and
 * {@link Constants#TRACING_PROPERTY tracing property}, respectively. For example, the following entry in an installation debug
 * properties file:
 * </p>
 * <p>
 * <code>pluginActivation = true</code>
 * </p>
 * <p>
 * would appear as the following in the state folder properties file:
 * </p>
 * <p>
 * <code>contexts/pluginActivation = true</code>
 * </p>
 * <p>
 * Note that this file contains nothing that refers to a property's default value, allowable values, or description. Also note
 * that the {@link Constants@TRACE_FILTERS_PROPERTY trace filters} contains the combined list of all trace filters found in all
 * plug-in's installation debug properties files.
 * </p>
 * <p>
 * This interface's {@link DebuggerImpl implementation} is thread-safe.
 * </p>
 * <p>
 * There are actually two implementations of this interface, {@link PluginUtilIMpl} and {@link DebuggerImpl}, but the former is
 * merely a proxy for the latter.
 * </p>
 * 
 * @since 4.0
 */
// hierarchy
// No type means text
// key name = displayed name
// Abstract classes
// DEBUG_PREFIX
// DESCRIPTION_SUFFIX
public interface Debugger {
    // ============================================================================================================================
    // Constants

    interface Constants extends FileUtils.Constants, StringUtil.Constants {
        String FILENAME = FILE_EXTENSION_SEPARATOR + "debug"; //$NON-NLS-1$

        String RESERVED_PREFIX = "$"; //$NON-NLS-1$
        String SEPARATOR = UriUtil.Constants.SEPARATOR;

        char SEPARATOR_CHAR = UriUtil.Constants.SEPARATOR_CHAR;

        String DEBUG_ENABLED_PROPERTY = "debugEnabled"; //$NON-NLS-1$
        String CONTEXTS_PROPERTY = "contexts"; //$NON-NLS-1$
        String TRACING_PROPERTY = "tracing"; //$NON-NLS-1$
        String OPTIONS_PROPERTY = "options"; //$NON-NLS-1$

        String OPTIONS_PREFIX = OPTIONS_PROPERTY + SEPARATOR;
        String CONTEXTS_PREFIX = CONTEXTS_PROPERTY + SEPARATOR;
        String TRACING_PREFIX = TRACING_PROPERTY + SEPARATOR;

        String TRACE_CONTEXTS_PREFIX = TRACING_PREFIX + CONTEXTS_PREFIX;
        String TRACE_CLASSES_PROPERTY = TRACING_PREFIX + "classes"; //$NON-NLS-1$
        String TRACE_CLASSES_PREFIX = TRACE_CLASSES_PROPERTY + SEPARATOR;
        String TRACE_OPTIONS_PREFIX = TRACING_PREFIX + OPTIONS_PREFIX;
        String TRACE_FILTERS_PROPERTY = RESERVED_PREFIX + "traceFilters"; //$NON-NLS-1$

        String OUTPUT_DESTINATION_PROPERTY = OPTIONS_PREFIX + "outputDestination"; //$NON-NLS-1$
        String PRINT_THREAD_PROPERTY = OPTIONS_PREFIX + "printThread"; //$NON-NLS-1$
        String PRINT_TIMESTAMP_PROPERTY = OPTIONS_PREFIX + "printTimestamp"; //$NON-NLS-1$
        String INDENT_MESSAGE_PROPERTY = TRACE_OPTIONS_PREFIX + "indentMessage"; //$NON-NLS-1$

        String DESCRIPTION_SUFFIX = RESERVED_PREFIX + "description"; //$NON-NLS-1$
        String VALUES_SUFFIX = RESERVED_PREFIX + "values"; //$NON-NLS-1$

        /*
         * Mutually exclusive values.
         */
        String NONE_VALUE = RESERVED_PREFIX + "none"; //$NON-NLS-1$ 
        String ANY_VALUE = RESERVED_PREFIX + "any"; //$NON-NLS-1$
        String FILE_VALUE = RESERVED_PREFIX + "file"; //$NON-NLS-1$

        char LIST_DELIMITER = ',';
        char CHOICE_DELIMITER = '|';

        String SYSTEM_OUT = "System.out"; //$NON-NLS-1$
        String SYSTEM_ERROR = "System.err"; //$NON-NLS-1$
    }

    // ============================================================================================================================
    // Methods

    /**
     * <p>
     * Specialized version of {@link #print(String)} that checks if debug is enabled for the specified context before printing the
     * specified message.
     * </p>
     * 
     * @param context A debug context.
     * @param message A message to be printed.
     * @since 4.0
     */
    void debug( String context,
                String message );

    /**
     * <p>
     * Returns the value of the specified debug property.
     * </p>
     * 
     * @param property The debug context property for which a value should be retrieved.
     * @return The specified debug context property's value.
     * @since 4.0
     */
    String getDebugContextProperty( String property );

    /**
     * <p>
     * Returns the value of the specified trace property.
     * </p>
     * 
     * @param property The trace context property for which a value should be retrieved.
     * @return The specified trace context property's value.
     * @since 4.0
     */
    String getTraceContextProperty( String property );

    /**
     * <p>
     * Returns whether debugging is enabled for the specified context. For this method to return true, all of the context's
     * ancestor contexts must also be enabled.
     * </p>
     * 
     * @param context A debug context.
     * @return True if debugging is enabled for the specified context.
     * @since 4.0
     */
    boolean isDebugEnabled( String context );

    /**
     * <p>
     * Returns whether tracing is enabled for the specified context. For this method to return true, all of the context's ancestor
     * contexts must also be enabled.
     * </p>
     * 
     * @param context A trace context.
     * @return True if tracing is enabled for the specified context.
     * @since 4.0
     */
    boolean isTraceEnabled( String context );

    /**
     * <p>
     * Returns whether tracing is enabled for the specified class. For this method to return true, the class' package must also be
     * enabled. Only intended for use within static methods.
     * </p>
     * 
     * @param clazz A class to trace.
     * @return True if debugging is enabled for the class with the specified name.
     * @since 4.0
     */
    boolean isTraceEnabled( Class clazz );

    /**
     * <p>
     * Convenience method that simply calls {@link #isTraceEnabled(Class)}.
     * </p>
     * 
     * @param object An instance of a class to trace.
     * @return True if tracing is enabled for the class of the specified object.
     * @since 4.0
     */
    boolean isTraceEnabled( Object object );

    /**
     * <p>
     * Prints the specified message to the currently set debug output stream, prepending the context. Intended for use when it has
     * already been determined that tracing is enabled for the class in an outer block of code, such as within a surrounding if
     * statement.
     * </p>
     * 
     * @param context A trace context.
     * @param message A message to be printed.
     * @since 4.0
     */
    void print( String context,
                String message );

    /**
     * <p>
     * Prints the specified message to the currently set debug output stream, prepending the class name, if tracing is enabled,
     * but regardless of whether tracing is {@link #isTraceEnabled(Class) enabled} for the specified class. Intended for use when
     * it has already been determined that tracing is enabled for the class in an outer block of code, such as within a
     * surrounding if statement. Only intended for use within static methods.
     * </p>
     * 
     * @param clazz A class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void print( Class clazz,
                String message );

    /**
     * <p>
     * Specialized version of {@link #print(Class, String)} that appends the specified object's hashcode to the class name.
     * </p>
     * 
     * @param object An instance of a class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void print( Object object,
                String message );

    /**
     * <p>
     * Specialized version of {@link #print(String, String)}, intended for use at the beginning of a block of code (e.g., a
     * method), that prepends the specified message with an indicator that the block has been entered. If indentation is enabled,
     * increments the current indent level for the current thread after printing the specified message. Must be used in conjuntion
     * with {@link #printExited(String, String)} for indented entered-exited message pairs to align.
     * </p>
     * 
     * @param context A trace context.
     * @param message A message to be printed.
     * @since 4.0
     */
    void printEntered( String context,
                       String message );

    /**
     * <p>
     * Specialized version of {@link #print(Class, String)}, intended for use at the beginning of a block of code (e.g., a
     * method), that prepends the specified message with an indicator that the block has been entered. If indentation is enabled,
     * increments the current indent level for the current thread after printing the specified message. Must be used in conjuntion
     * with {@link #printExited(Class, String)} for indented entered-exited message pairs to align. Only intended for use within
     * static methods.
     * </p>
     * 
     * @param clazz A class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void printEntered( Class clazz,
                       String message );

    /**
     * <p>
     * Specialized version of {@link #printEntered(Class, String)} that appends the specified object's hashcode to the class name.
     * </p>
     * 
     * @param object An instance of a class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void printEntered( Object object,
                       String message );

    /**
     * <p>
     * <p>
     * Specialized version of {@link #print(String, String)}, intended for use at the end of a block of code (e.g., a method),
     * that prepends the specified message with an indicator that the block has been exited. If indentation is enabled, decrements
     * the current indent level for the current thread before printing the specified message. Must be used in conjuntion with
     * {@link #printEntered(String, String)} for indented entered-exited message pairs to align.
     * </p>
     * <p>
     * Note, it is good practice to place calls to this method within the <code>finally</code> block of a try-finally statement
     * surrounding the code block being debugged, to ensure this method is executed regardless of thrown exceptions or internal
     * return statements within the debugged block of code.
     * </p>
     * 
     * @param context A trace context.
     * @param message A message to be printed.
     * @since 4.0
     */
    void printExited( String context,
                      String message );

    /**
     * <p>
     * <p>
     * Specialized version of {@link #print(Class, String)}, intended for use at the end of a block of code (e.g., a method), that
     * prepends the specified message with an indicator that the block has been exited. If indentation is enabled, decrements the
     * current indent level for the current thread before printing the specified message. Must be used in conjuntion with
     * {@link #printEntered(Class, String)} for indented entered-exited message pairs to align. Only intended for use within
     * static methods.
     * </p>
     * <p>
     * Note, it is good practice to place calls to this method within the <code>finally</code> block of a try-finally statement
     * surrounding the code block being debugged, to ensure this method is executed regardless of thrown exceptions or internal
     * return statements within the debugged block of code.
     * </p>
     * 
     * @param clazz A class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void printExited( Class clazz,
                      String message );

    /**
     * <p>
     * Specialized version of {@link #printExited(Class, String)} that appends the specified object's hashcode to the class name.
     * </p>
     * 
     * @param object An instance of a class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void printExited( Object object,
                      String message );

    /**
     * Marks a beginning point in time for the specified context identifier. A log entry marking the start time and context ID is
     * entered if the context is enabled.
     * 
     * @param theContextId the context identifier
     */
    void start( String theContextId );

    /**
     * Marks a beginning point in time for the specified timer identifier. A log entry marking the start time and timer ID is
     * entered only if the specified context is enabled.
     * 
     * @param theTimerId the timer identifier
     * @param theContextId the context identifier
     */
    void start( String theTimerId,
                String theContextId );

    /**
     * Marks a beginning point in time for the specified timer identifier. A log entry marking the start time and timer ID is
     * entered only if the specified context is enabled. Information objects are added to the message by using their string
     * representations.
     * 
     * @param theTimerId the timer identifier
     * @param theContextId the context identifier
     * @param theInfo the objects that add additional information to the log message (can be <code>null</code>)
     */
    void start( String theTimerId,
                String theContextId,
                Object[] theInfo );

    /**
     * Marks the ending point in time for the specified context identifier. A log entry marking the total time consumed for this
     * context is entered.
     * 
     * @param theContextId the context identifier
     */
    void stop( String theContextId );

    /**
     * Marks the ending point in time for the specified timer identifier. A log entry marking the total time consumed for this
     * timer is entered if the specified context is enabled.
     * 
     * @param theTimerId the timer identifier
     * @param theContextId the specified context
     */
    void stop( String theTimerId,
               String theContextId );

    /**
     * Marks the ending point in time for the specified timer identifier. A log entry marking the total time consumed for this
     * timer is entered if the specified context is enabled. Information objects are added to the message by using their string
     * representations.
     * 
     * @param theTimerId the timer identifier
     * @param theContextId the context identifier
     * @param theInfo the objects that add additional information to the log message
     */
    void stop( String theTimerId,
               String theContextId,
               Object[] theInfo );

    /**
     * <p>
     * Specialized version of {@link #print(String)} that checks if tracing is enabled for the specified context before printing
     * the specified message.
     * </p>
     * 
     * @param context A trace context.
     * @param message A message to be printed.
     * @since 4.0
     */
    void trace( String context,
                String message );

    /**
     * <p>
     * Specialized version of {@link #print(Class, String)} that checks if tracing is enabled for the specified class before
     * printing the specified message. Only intended for use within static methods.
     * </p>
     * 
     * @param clazz A class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void trace( Class clazz,
                String message );

    /**
     * <p>
     * Specialized version of {@link #print(Object, String)} that checks if tracing is enabled for the specified object's class
     * before printing the specified message.
     * </p>
     * 
     * @param object An instance of a class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void trace( Object object,
                String message );

    /**
     * <p>
     * Specialized version of {@link #printEntered(String, String)} that checks if tracing is enabled for the specified context
     * before printing the specified message.
     * </p>
     * 
     * @param context A trace context.
     * @param message A message to be printed.
     * @since 4.0
     */
    void traceEntered( String context,
                       String message );

    /**
     * <p>
     * Specialized version of {@link #printEntered(Class, String)} that checks if tracing is enabled for the specified class
     * before printing the specified message. Only intended for use within static methods.
     * </p>
     * 
     * @param clazz A class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void traceEntered( Class clazz,
                       String message );

    /**
     * <p>
     * Specialized version of {@link #printEntered(Object, String)} that checks if tracing is enabled for the specified object's
     * class before printing the specified message.
     * </p>
     * 
     * @param object An instance of a class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void traceEntered( Object object,
                       String message );

    /**
     * <p>
     * Specialized version of {@link #printExited(String, String)} that checks if tracing is enabled for the specified context
     * before printing the specified message.
     * </p>
     * 
     * @param context A trace context.
     * @param message A message to be printed.
     * @since 4.0
     */
    void traceExited( String context,
                      String message );

    /**
     * <p>
     * Specialized version of {@link #printExited(Class, String)} that checks if tracing is enabled for the specified class before
     * printing the specified message. Only intended for use within static methods.
     * </p>
     * 
     * @param clazz A class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void traceExited( Class clazz,
                      String message );

    /**
     * <p>
     * Specialized version of {@link #printExited(Object, String)} that checks if tracing is enabled for the specified object's
     * class before printing the specified message.
     * </p>
     * 
     * @param object An instance of a class to trace.
     * @param message A message to be printed.
     * @since 4.0
     */
    void traceExited( Object object,
                      String message );
}
