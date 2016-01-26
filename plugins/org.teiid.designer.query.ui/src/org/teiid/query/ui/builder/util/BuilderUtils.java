/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.util;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.query.IQueryFactory;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.query.sql.ISQLStringVisitor;
import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IConstant;
import org.teiid.designer.query.sql.symbol.IFunction;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.query.ui.UiConstants;


/**
 * BuilderUtils
 *
 * @since 8.0
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

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public static final Color COLOR_HIGHLIGHT = GlobalUiColorManager.getColor(new RGB(204, 204, 255));

    /** Text used to indicate something has not be defined or has not value. */
    public static final String UNDEFINED = ISQLStringVisitor.UNDEFINED;
    
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Don't allow construction. */
    private BuilderUtils() {
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a <code>Constant</code> that is used for conversion type functions.
     * 
     * @return the conversion type constant
     */
    public static IConstant createConversionTypeConstant() {
        IQueryService queryService = ModelerCore.getTeiidQueryService();
        IQueryFactory factory = queryService.createQueryFactory();
        
        IDataTypeManagerService dataTypeManagerService = ModelerCore.getTeiidDataTypeManagerService();
        return factory.createConstant(dataTypeManagerService.getDefaultDataType(DataTypeName.STRING));
    }

    /**
     * Gets the <code>LanguageObject</code> used within the builders. In most instances the input parameter is returned. However,
     * when the input parameter is an implicit function conversion, the first argument is returned. If the first argument is also
     * an implicit function, this method recurses until it doesn't find one.
     * 
     * @param theLangObj the object whose builder object is being requested
     * @return the <code>LanguageObject</code> to use within the builders
     * @throws IllegalArgumentException if parameter is null
     */
    public static ILanguageObject getBuilderLanguageObject( ILanguageObject theLangObj ) {
        CoreArgCheck.isNotNull(theLangObj);

        ILanguageObject result = theLangObj;

        if (theLangObj != null && theLangObj instanceof IFunction && ((IFunction)theLangObj).isImplicit()) {
            // according to Alex, all implicit functions are conversions and
            // the first argument is what is being converted
            result = getBuilderLanguageObject(((IFunction)theLangObj).getArgs()[0]);
        }

        return result;
    }

    public static int getTextLimit( String theTextType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return service.getDataTypeLimit(theTextType);
    }

    /**
     * Gets the data type of the given constant.
     * 
     * @return the data type
     */
    public static String getType( IConstant theConstant ) {
        Class typeClass = theConstant.getType();
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return service.getDataTypeName(typeClass);
    }

    /**
     * Gets all data types
     * 
     * @return set of all data types
     */
    public static Collection<String> getAllTypes() {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return service.getAllDataTypeNames();
    }
    
    /**
     * Get the invalid types
     * 
     * @return set of invalid data types
     */
    public static Collection<String>getInvalidTypes() {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return Collections.singleton(service.getDefaultDataType(DataTypeName.NULL));
    }
    
    /**
     * Gets the <code>Class</code> associated with the given type.
     * 
     * @return the data type or <code>null</code> if invalid type
     */
    public static Class getTypeClass( String theType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return (isValidType(theType)) ? service.getDataTypeClass(theType) : null;
    }

    public static String getValidChars( String theTextType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return service.getDataTypeValidChars(theTextType);
    }

    /**
     * Indicates if the given type is a <code>boolean</code> type.
     * 
     * @return <code>true</code> if the type is non-<code>null</code> and a boolean type; <code>false</code> otherwise.
     */
    public static boolean isBooleanType( String theType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return (theType == null) ? false : theType.equals(service.getDefaultDataType(DataTypeName.BOOLEAN));
    }

    /**
     * Indicates if the {@link org.teiid.query.sql.symbol.Constant} currently displayed in the editor is a conversion type.
     * 
     * @return <code>true</code> if conversion type; <code>false</code> otherwise.
     */
    public static boolean isConversionType( IConstant theConstant ) {
        boolean result = false;

        // conversion types are string constants with their value equal to one of the constant
        // string values for type defined in DataTypeManager.DefaultDataTypes
        if ((theConstant != null) && theConstant.getType().equals(String.class) && isValidType((String)theConstant.getValue())) {

            result = true;
        }

        return result;
    }

    /**
     * Indicates if the given function argument is a conversion type constant. Conversion type constants are used to identify the
     * result type of a conversion.
     * 
     * @return <code>true</code> if the function argument is a conversion type constant; <code>false</code> otherwise.
     */
    public static boolean isConversionTypeArg( String theFunctionName,
                                               String theArgName ) {
        boolean result = false;

        if ((theFunctionName.equals("CAST") || theFunctionName.equals("CONVERT")) && //$NON-NLS-1$ //$NON-NLS-2$
            theArgName.equals("TARGET")) { //$NON-NLS-1$
            result = true;
        }

        return result;
    }

    /**
     * Indicates if the given object is a conversion type constant. Conversion type constants are used to identify the result type
     * of a conversion. Conversion type constants are constants whose value is one of the data type values.
     * 
     * @return <code>true</code> if the type is a conversion type constant; <code>false</code> otherwise.
     */
    public static boolean isConversionTypeConstant( Object theObject ) {
        boolean result = (theObject instanceof IConstant);

        if (result) {
            IConstant constant = (IConstant)theObject;
            result = (constant.getType().equals(String.class) && isValidType((String)constant.getValue()));
        }

        return result;
    }

    /**
     * Indicates if the given type is a <code>Date</code> type.
     * 
     * @return <code>true</code> if the type is non-<code>null</code> and a date type; <code>false</code> otherwise.
     */
    public static boolean isDateType( String theType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return (theType == null) ? false : theType.equals(service.getDefaultDataType(DataTypeName.DATE));
    }

    /**
     * Indicates if the given type is a <code>Number</code> type.
     * 
     * @return <code>true</code> if the type is non-<code>null</code> and a number type; <code>false</code> otherwise.
     */
    public static boolean isNumberType( String theType ) {
        if (theType == null)
            return false;
        
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();

        return theType.equals(service.getDefaultDataType(DataTypeName.BIGDECIMAL))
               || theType.equals(service.getDefaultDataType(DataTypeName.BIGINTEGER))
               || theType.equals(service.getDefaultDataType(DataTypeName.BYTE))
               || theType.equals(service.getDefaultDataType(DataTypeName.DOUBLE))
               || theType.equals(service.getDefaultDataType(DataTypeName.FLOAT))
               || theType.equals(service.getDefaultDataType(DataTypeName.INTEGER))
               || theType.equals(service.getDefaultDataType(DataTypeName.LONG))
               || theType.equals(service.getDefaultDataType(DataTypeName.SHORT));
    }

    /**
     * Indicates if the given type is a <code>null</code> type.
     * 
     * @return <code>true</code> if the type is non-<code>null</code> and a null type; <code>false</code> otherwise.
     */
    public static boolean isNullType( String theType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return (theType == null) ? false : theType.equals(service.getDefaultDataType(DataTypeName.NULL));
    }

    /**
     * Indicates if the given type is a <code>String</code> type.
     * 
     * @return <code>true</code> if the type is non-<code>null</code> and a string type; <code>false</code> otherwise.
     */
    public static boolean isStringType( String theType ) {
        if (theType == null)
            return false;
        
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return isNumberType(theType)
                || theType.equals(service.getDefaultDataType(DataTypeName.CHAR))
                || theType.equals(service.getDefaultDataType(DataTypeName.STRING));
    }

    /**
     * Indicates if the given type is a <code>Time</code> type.
     * 
     * @return <code>true</code> if the type is non-<code>null</code> and a time type; <code>false</code> otherwise.
     */
    public static boolean isTimeType( String theType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return (theType == null) ? false : theType.equals(service.getDefaultDataType(DataTypeName.TIME));
    }

    /**
     * Indicates if the given type is a <code>Timestamp</code> type.
     * 
     * @return <code>true</code> if the type is non-<code>null</code> and a time type; <code>false</code> otherwise.
     */
    public static boolean isTimestampType( String theType ) {
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        return (theType == null) ? false : theType.equals(service.getDefaultDataType(DataTypeName.TIMESTAMP));
    }

    /**
     * Indicates if the given type is valid.
     * 
     * @return <code>true</code> if the type is non-<code>null</code> and a valid type; <code>false</code> otherwise.
     */
    public static boolean isValidType( String theType ) {
        if (theType == null)
            return false;
        
        IDataTypeManagerService service = ModelerCore.getTeiidDataTypeManagerService();
        
        for (DataTypeName dataTypeName : DataTypeName.values()) {
        	if( !AnnotationUtils.isApplicable(dataTypeName, ModelerCore.getTeiidServerVersion())) continue;
        	
            if (theType.equals(service.getDefaultDataType(dataTypeName)))
                return true;
        }
        
        return false;
    }

}
