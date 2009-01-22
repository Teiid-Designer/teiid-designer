/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.common.types.DataTypeManager.DefaultDataTypes;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.Function;
import com.metamatrix.query.sql.visitor.SQLStringVisitor;
import com.metamatrix.query.ui.UiConstants;
import com.metamatrix.ui.graphics.GlobalUiColorManager;

/**
 * BuilderUtils
 */
public final class BuilderUtils implements UiConstants {
    
    /**
     * Constants used in the query.ui plugin's .debug file
     */
    public interface LoggingConstants {
        /** Logging prefix. */
        String PREFIX = "builders_"; //$NON-NLS-1$

        /** Constant for debug-level logging. */
        String DEBUG = PREFIX + "debug"; //$NON-NLS-1$

        /** Constant for event logging. */
        String EVENTS = PREFIX + "events"; //$NON-NLS-1$

        /** Constant for trace-level logging. */
        String TRACING = PREFIX + "tracing"; //$NON-NLS-1$
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public static final Color COLOR_HIGHLIGHT = GlobalUiColorManager.getColor(new RGB(204, 204, 255));

    public static final String[] ALL_TYPES = new String[] {DefaultDataTypes.BIG_DECIMAL,
                                                           DefaultDataTypes.BIG_INTEGER,
                                                           DefaultDataTypes.BOOLEAN,
                                                           DefaultDataTypes.BYTE,
                                                           DefaultDataTypes.CHAR,
                                                           DefaultDataTypes.DATE,
                                                           DefaultDataTypes.DOUBLE,
                                                           DefaultDataTypes.FLOAT,
                                                           DefaultDataTypes.INTEGER,
                                                           DefaultDataTypes.LONG,
                                                           DefaultDataTypes.SHORT,
                                                           DefaultDataTypes.STRING,
                                                           DefaultDataTypes.TIME,
                                                           DefaultDataTypes.TIMESTAMP,
                                                           DefaultDataTypes.NULL};
                                                           
    public static final List ALL_TYPES_LIST = Collections.unmodifiableList(Arrays.asList(ALL_TYPES));

    public static final String[] BOOLEAN_TYPES = new String[] {DefaultDataTypes.BOOLEAN};

    public static final List BOOLEAN_TYPES_LIST = Collections.unmodifiableList(Arrays.asList(BOOLEAN_TYPES));

    public static final String[] DATE_TYPES = new String[] {DefaultDataTypes.DATE,
                                                            DefaultDataTypes.TIME,
                                                            DefaultDataTypes.TIMESTAMP };

    public static final List DATE_TYPES_LIST = Collections.unmodifiableList(Arrays.asList(DATE_TYPES));

    public static final String[] INVALID_CONVERSION_ARG_TYPES = new String[] {DefaultDataTypes.NULL};

    public static final List INVALID_CONVERSION_ARG_TYPES_LIST = Collections.unmodifiableList(Arrays.asList(INVALID_CONVERSION_ARG_TYPES));

    public static final String[] NULL_TYPES = new String[] {DefaultDataTypes.NULL};

    public static final List NULL_TYPES_LIST = Collections.unmodifiableList(Arrays.asList(NULL_TYPES));

    public static final String[] NUMBER_TYPES = new String[] {DefaultDataTypes.BIG_DECIMAL,
                                                              DefaultDataTypes.BIG_INTEGER,
                                                              DefaultDataTypes.BYTE,
                                                              DefaultDataTypes.DOUBLE,
                                                              DefaultDataTypes.FLOAT,
                                                              DefaultDataTypes.INTEGER,
                                                              DefaultDataTypes.LONG,
                                                              DefaultDataTypes.SHORT };
                                          
    public static final List NUMBER_TYPES_LIST = Collections.unmodifiableList(Arrays.asList(NUMBER_TYPES));

    public static final String[] STRING_TYPES = new String[] {DefaultDataTypes.BIG_DECIMAL,
                                                              DefaultDataTypes.BIG_INTEGER,
                                                              DefaultDataTypes.BYTE,
                                                              DefaultDataTypes.CHAR,
                                                              DefaultDataTypes.DOUBLE,
                                                              DefaultDataTypes.FLOAT,
                                                              DefaultDataTypes.INTEGER,
                                                              DefaultDataTypes.LONG,
                                                              DefaultDataTypes.SHORT,
                                                              DefaultDataTypes.STRING };
 
    public static final List STRING_TYPES_LIST = Collections.unmodifiableList(Arrays.asList(STRING_TYPES));
    
    /** Keep in same order as STRING_TYPES. */
    private static final Map TEXT_LIMITS; // STRING

    /** Text used to indicate something has not be defined or has not value. */
    public static final String UNDEFINED = SQLStringVisitor.getSQLString(null);
    
    /** Keep in same order as STRING_TYPES. */
    private static final Map VALID_CHARS;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        TEXT_LIMITS = new HashMap();
        TEXT_LIMITS.put(STRING_TYPES[0], new Integer(30)); // BIG_DECIMAL
        TEXT_LIMITS.put(STRING_TYPES[1], new Integer(30)); // BIG_INTEGER
        TEXT_LIMITS.put(STRING_TYPES[2], new Integer(3)); // BYTE
        TEXT_LIMITS.put(STRING_TYPES[3], new Integer(1)); // CHAR
        TEXT_LIMITS.put(STRING_TYPES[4], new Integer(30)); // DOUBLE
        TEXT_LIMITS.put(STRING_TYPES[5], new Integer(30)); // FLOAT
        TEXT_LIMITS.put(STRING_TYPES[6], new Integer(10)); // INTEGER
        TEXT_LIMITS.put(STRING_TYPES[7], new Integer(19)); // LONG
        TEXT_LIMITS.put(STRING_TYPES[8], new Integer(5)); // SHORT
        TEXT_LIMITS.put(STRING_TYPES[9], new Integer(256)); // STRING

        VALID_CHARS = new HashMap();
        VALID_CHARS.put(STRING_TYPES[0], "0123456789-."); // BIG_DECIMAL //$NON-NLS-1$
        VALID_CHARS.put(STRING_TYPES[1], "0123456789-"); // BIG_INTEGER //$NON-NLS-1$
        VALID_CHARS.put(STRING_TYPES[2], "0123456789-"); // BYTE //$NON-NLS-1$
        VALID_CHARS.put(STRING_TYPES[3], null); // CHAR
        VALID_CHARS.put(STRING_TYPES[4], "0123456789-.E"); // DOUBLE //$NON-NLS-1$
        VALID_CHARS.put(STRING_TYPES[5], "0123456789-."); // FLOAT //$NON-NLS-1$
        VALID_CHARS.put(STRING_TYPES[6], "0123456789-"); // INTEGER //$NON-NLS-1$
        VALID_CHARS.put(STRING_TYPES[7], "0123456789-"); // LONG //$NON-NLS-1$
        VALID_CHARS.put(STRING_TYPES[8], "0123456789-"); // SHORT //$NON-NLS-1$
        VALID_CHARS.put(STRING_TYPES[9], null); // STRING
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Don't allow construction. */
    private BuilderUtils() {}
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Creates a <code>Constant</code> that is used for conversion type functions.
     * @return the conversion type constant
     */
    public static Constant createConversionTypeConstant() {
        return new Constant(DefaultDataTypes.STRING);
    }
    
    /**
     * Gets the <code>LanguageObject</code> used within the builders. In most instances the input
     * parameter is returned. However, when the input parameter is an implicit function conversion,
     * the first argument is returned. If the first argument is also an implicit function, this method
     * recurses until it doesn't find one. 
     * @param theLangObj the object whose builder object is being requested
     * @return the <code>LanguageObject</code> to use within the builders
     * @throws IllegalArgumentException if parameter is null
     */
    public static LanguageObject getBuilderLanguageObject(LanguageObject theLangObj) {
        ArgCheck.isNotNull(theLangObj);
        
        LanguageObject result = theLangObj;
        
        if ((theLangObj instanceof Function) && ((Function)theLangObj).isImplicit()) {
            // according to Alex, all implicit functions are conversions and
            // the first argument is what is being converted
            result = getBuilderLanguageObject(((Function)theLangObj).getArgs()[0]);
        }
        
        return result;
    }
    
    public static int getTextLimit(String theTextType) {
        int result = -1;
        
        if (TEXT_LIMITS.containsKey(theTextType)) {
            Integer limit = (Integer)TEXT_LIMITS.get(theTextType);
            
            if (limit != null) {
                result = limit.intValue();
            }
        }
        
        return result;
    }
    /**
     * Gets the data type of the given constant.
     * @return the data type
     */
    public static String getType(Constant theConstant) {
        Class typeClass = theConstant.getType();
        return DataTypeManager.getDataTypeName(typeClass);
    }
    
    /**
     * Gets the <code>Class</code> associated with the given type.
     * @return the data type or <code>null</code> if invalid type
     */
    public static Class getTypeClass(String theType) {
        return (isValidType(theType)) ? DataTypeManager.getDataTypeClass(theType)
                                      : null;
    }
    
    public static String getValidChars(String theTextType) {
        return (String)VALID_CHARS.get(theTextType);
    }
    
    /**
     * Indicates if the given type is a <code>boolean</code> type.
     * @return <code>true</code> if the type is non-<code>null</code> and a boolean type; 
     * <code>false</code> otherwise.
     */
    public static boolean isBooleanType(String theType) {
        return (theType == null) ? false
                                 : BOOLEAN_TYPES_LIST.contains(theType);
    }

    /** 
     * Indicates if the {@link com.metamatrix.query.sql.symbol.Constant} currently displayed in the
     * editor is a conversion type.
     * @return <code>true</code> if conversion type; <code>false</code> otherwise.
     */
    public static boolean isConversionType(Constant theConstant) {
        boolean result = false;
        
        // conversion types are string constants with their value equal to one of the constant
        // string values for type defined in DataTypeManager.DefaultDataTypes
        if ((theConstant != null) &&
            theConstant.getType().equals(String.class) && 
            isValidType((String)theConstant.getValue())) {

            result = true;
        }

        return result;
    }
    
    /**
     * Indicates if the given function argument is a conversion type constant. Conversion type constants are 
     * used to identify the result type of a conversion.
     * @return <code>true</code> if the function argument is a conversion type constant; 
     * <code>false</code> otherwise.
     */
    public static boolean isConversionTypeArg(String theFunctionName,
                                              String theArgName) {
        boolean result = false;
        
        if ((theFunctionName.equals("CAST") || theFunctionName.equals("CONVERT")) && //$NON-NLS-1$ //$NON-NLS-2$
            theArgName.equals("TARGET")) { //$NON-NLS-1$
            result = true;
        }

        return result;
    }
    
    /**
     * Indicates if the given object is a conversion type constant. Conversion type constants are used to 
     * identify the result type of a conversion. Conversion type constants are constants whose value is one
     * of the data type values.
     * @return <code>true</code> if the type is a conversion type constant; <code>false</code> otherwise.
     */
    public static boolean isConversionTypeConstant(Object theObject) {
        boolean result = (theObject instanceof Constant);
        
        if (result) {
            Constant constant = (Constant)theObject;
            result = (constant.getType().equals(String.class) && isValidType((String)constant.getValue()));
        }

        return result;
    }

    /**
     * Indicates if the given type is a <code>Date</code> type.
     * @return <code>true</code> if the type is non-<code>null</code> and a date type; 
     * <code>false</code> otherwise.
     */
    public static boolean isDateType(String theType) {
        return (theType == null) ? false
                                 : theType.equals(DefaultDataTypes.DATE);
    }
    
    /**
     * Indicates if debug logging has been activated.
     * @return <code>true</code> if debug logging is activated; <code>false</code> otherwise.
     */
    public static boolean isDebugLogging() {
        return Util.isDebugEnabled(LoggingConstants.DEBUG);
    }
    
    /**
     * Indicates if event logging has been activated.
     * @return <code>true</code> if event logging is activated; <code>false</code> otherwise.
     */
    public static boolean isEventLogging() {
        return Util.isDebugEnabled(LoggingConstants.EVENTS);
    }
    
    /**
     * Indicates if the given type is a <code>Number</code> type.
     * @return <code>true</code> if the type is non-<code>null</code> and a number type; 
     * <code>false</code> otherwise.
     */
    public static boolean isNumberType(String theType) {
        return (theType == null) ? false
                                 : NUMBER_TYPES_LIST.contains(theType);
    }
    
    /**
     * Indicates if the given type is a <code>null</code> type.
     * @return <code>true</code> if the type is non-<code>null</code> and a null type; 
     * <code>false</code> otherwise.
     */
    public static boolean isNullType(String theType) {
        return (theType == null) ? false
                                 : NULL_TYPES_LIST.contains(theType);
    }

    /**
     * Indicates if the given type is a <code>String</code> type.
     * @return <code>true</code> if the type is non-<code>null</code> and a string type; 
     * <code>false</code> otherwise.
     */
    public static boolean isStringType(String theType) {
        return (theType == null) ? false
                                 : STRING_TYPES_LIST.contains(theType);
    }

    /**
     * Indicates if the given type is a <code>Time</code> type.
     * @return <code>true</code> if the type is non-<code>null</code> and a time type; 
     * <code>false</code> otherwise.
     */
    public static boolean isTimeType(String theType) {
        return (theType == null) ? false
                                 : theType.equals(DefaultDataTypes.TIME);
    }

    /**
     * Indicates if the given type is a <code>Timestamp</code> type.
     * @return <code>true</code> if the type is non-<code>null</code> and a time type; 
     * <code>false</code> otherwise.
     */
    public static boolean isTimestampType(String theType) {
        return (theType == null) ? false
                                 : theType.equals(DefaultDataTypes.TIMESTAMP);
    }
    
    /**
     * Indicates if trace logging has been activated.
     * @return <code>true</code> if trace logging is activated; <code>false</code> otherwise.
     */
    public static boolean isTraceLogging() {
        return Util.isDebugEnabled(LoggingConstants.TRACING);
    }
    
    /**
     * Indicates if the given type is valid.
     * @return <code>true</code> if the type is non-<code>null</code> and a valid type; 
     * <code>false</code> otherwise.
     */
    public static boolean isValidType(String theType) {
        return (theType == null) ? false
                                 : ALL_TYPES_LIST.contains(theType);
    }
    
}
