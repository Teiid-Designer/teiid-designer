/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.query.function.source;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.metadata.FunctionMethod;
import org.teiid.metadata.FunctionMethod.Determinism;
import org.teiid.metadata.FunctionMethod.PushDown;
import org.teiid.metadata.FunctionParameter;
import org.teiid.metadata.MetadataFactory;
import org.teiid.query.function.FunctionMethods;
import org.teiid.query.function.GeometryFunctionMethods;
import org.teiid.query.function.JSONFunctionMethods;
import org.teiid.query.function.SystemFunctionMethods;
import org.teiid.query.function.TeiidFunction;
import org.teiid.query.function.UDFSource;
import org.teiid.query.function.metadata.FunctionCategoryConstants;
import org.teiid.runtime.client.Messages;
import org.teiid.translator.SourceSystemFunctions;


/**
 * This metadata source has metadata for the hard-coded system functions.  All
 * system functions are described by this metadata.
 */
public class SystemSource extends UDFSource implements FunctionCategoryConstants {

    /** The name of the invocation class for all of the system functions. */
    private static final String FUNCTION_CLASS = FunctionMethods.class.getName(); 
    private static final String XML_FUNCTION_CLASS = XMLSystemFunctions.class.getName(); 
    private static final String SECURITY_FUNCTION_CLASS = SecuritySystemFunctions.class.getName();

    private final ITeiidServerVersion teiidVersion;
    private final DataTypeManagerService dataTypeManager;
    
    /**
     * Construct a source of system metadata.
     *
     * @param teiidVersion
     * @param allowEnvFunction
     * @param classLoader 
     */
    public SystemSource(ITeiidServerVersion teiidVersion, boolean allowEnvFunction, ClassLoader classLoader) {
    	super(new ArrayList<FunctionMethod>(), classLoader);
        this.teiidVersion = teiidVersion;
        this.dataTypeManager = DataTypeManagerService.getInstance(teiidVersion);

		// +, -, *, /
        addArithmeticFunction(SourceSystemFunctions.ADD_OP, Messages.getString(Messages.SystemSource.Add_description), "plus", Messages.getString(Messages.SystemSource.Add_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addArithmeticFunction(SourceSystemFunctions.SUBTRACT_OP, Messages.getString(Messages.SystemSource.Subtract_description), "minus", Messages.getString(Messages.SystemSource.Subtract_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addArithmeticFunction(SourceSystemFunctions.MULTIPLY_OP, Messages.getString(Messages.SystemSource.Multiply_description), "multiply", Messages.getString(Messages.SystemSource.Multiply_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addArithmeticFunction(SourceSystemFunctions.DIVIDE_OP, Messages.getString(Messages.SystemSource.Divide_description), "divide", Messages.getString(Messages.SystemSource.Divide_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addArithmeticFunction(SourceSystemFunctions.MOD, Messages.getString(Messages.SystemSource.Mod_description), "mod", Messages.getString(Messages.SystemSource.Mod_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        
        // numeric
        addAbsFunction();
        addRandFunction();
        addPowerFunction();
        addRoundFunction();
        addSignFunction();
        addSqrtFunction();        
		addDoubleFunction(SourceSystemFunctions.ACOS, Messages.getString(Messages.SystemSource.Acos_description)); //$NON-NLS-1$ 
		addDoubleFunction(SourceSystemFunctions.ASIN, Messages.getString(Messages.SystemSource.Asin_description)); //$NON-NLS-1$ 
		addDoubleFunction(SourceSystemFunctions.ATAN, Messages.getString(Messages.SystemSource.Atan_description)); //$NON-NLS-1$ 
		addAtan2Function(SourceSystemFunctions.ATAN2, Messages.getString(Messages.SystemSource.Atan2_description)); //$NON-NLS-1$ 
		addDoubleFunction(SourceSystemFunctions.COS, Messages.getString(Messages.SystemSource.Cos_description)); //$NON-NLS-1$ 
		addDoubleFunction(SourceSystemFunctions.COT, Messages.getString(Messages.SystemSource.Cot_description)); //$NON-NLS-1$ 
		addDoubleFunction(SourceSystemFunctions.DEGREES, Messages.getString(Messages.SystemSource.Degrees_description)); //$NON-NLS-1$ 
		addPiFunction(SourceSystemFunctions.PI, Messages.getString(Messages.SystemSource.Pi_description)); //$NON-NLS-1$ 
		addDoubleFunction(SourceSystemFunctions.RADIANS, Messages.getString(Messages.SystemSource.Radians_description)); //$NON-NLS-1$ 
		addDoubleFunction(SourceSystemFunctions.SIN, Messages.getString(Messages.SystemSource.Sin_description)); //$NON-NLS-1$ 
		addDoubleFunction(SourceSystemFunctions.TAN, Messages.getString(Messages.SystemSource.Tan_description)); //$NON-NLS-1$ 
        addDoubleFunction(SourceSystemFunctions.LOG, Messages.getString(Messages.SystemSource.Log_description)); //$NON-NLS-1$ 
        addDoubleFunction(SourceSystemFunctions.LOG10, Messages.getString(Messages.SystemSource.Log10_description)); //$NON-NLS-1$ 
        addDoubleFunction(SourceSystemFunctions.CEILING, Messages.getString(Messages.SystemSource.Ceiling_description)); //$NON-NLS-1$ 
        addDoubleFunction(SourceSystemFunctions.EXP, Messages.getString(Messages.SystemSource.Exp_description)); //$NON-NLS-1$ 
        addDoubleFunction(SourceSystemFunctions.FLOOR, Messages.getString(Messages.SystemSource.Floor_description)); //$NON-NLS-1$ 
        
        // bit
        addBitFunction(SourceSystemFunctions.BITAND, Messages.getString(Messages.SystemSource.Bitand_description), "bitand", 2, Messages.getString(Messages.SystemSource.Bitand_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addBitFunction(SourceSystemFunctions.BITOR, Messages.getString(Messages.SystemSource.Bitor_description), "bitor", 2, Messages.getString(Messages.SystemSource.Bitor_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addBitFunction(SourceSystemFunctions.BITXOR, Messages.getString(Messages.SystemSource.Bitxor_description), "bitxor", 2, Messages.getString(Messages.SystemSource.Bitxor_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addBitFunction(SourceSystemFunctions.BITNOT, Messages.getString(Messages.SystemSource.Bitnot_description), "bitnot", 1, Messages.getString(Messages.SystemSource.Bitnot_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

        // date
        addConstantDateFunction(SourceSystemFunctions.CURDATE, Messages.getString(Messages.SystemSource.Curdate_description), "currentDate", DataTypeManagerService.DefaultDataTypes.DATE); //$NON-NLS-1$ //$NON-NLS-2$ 
        addConstantDateFunction(SourceSystemFunctions.CURTIME, Messages.getString(Messages.SystemSource.Curtime_description), "currentTime", DataTypeManagerService.DefaultDataTypes.TIME); //$NON-NLS-1$ //$NON-NLS-2$ 
        addConstantDateFunction(SourceSystemFunctions.NOW, Messages.getString(Messages.SystemSource.Now_description), "currentTimestamp", DataTypeManagerService.DefaultDataTypes.TIMESTAMP); //$NON-NLS-1$ //$NON-NLS-2$ 
        addDateFunction(SourceSystemFunctions.DAYNAME, "dayName", Messages.getString(Messages.SystemSource.Dayname_result_d_description), Messages.getString(Messages.SystemSource.Dayname_result_ts_description), DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addDateFunction(SourceSystemFunctions.DAYOFMONTH, "dayOfMonth", Messages.getString(Messages.SystemSource.Dayofmonth_result_d_description), Messages.getString(Messages.SystemSource.Dayofmonth_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addDateFunction(SourceSystemFunctions.DAYOFWEEK, "dayOfWeek", Messages.getString(Messages.SystemSource.Dayofweek_result_d_description), Messages.getString(Messages.SystemSource.Dayofweek_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addDateFunction(SourceSystemFunctions.DAYOFYEAR, "dayOfYear", Messages.getString(Messages.SystemSource.Dayofyear_result_d_description), Messages.getString(Messages.SystemSource.Dayofyear_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addDateFunction(SourceSystemFunctions.MONTH, "month", Messages.getString(Messages.SystemSource.Month_result_d_description), Messages.getString(Messages.SystemSource.Month_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addDateFunction(SourceSystemFunctions.MONTHNAME, "monthName", Messages.getString(Messages.SystemSource.Monthname_result_d_description), Messages.getString(Messages.SystemSource.Monthname_result_ts_description), DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addDateFunction(SourceSystemFunctions.WEEK, "week", Messages.getString(Messages.SystemSource.Week_result_d_description), Messages.getString(Messages.SystemSource.Week_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addDateFunction(SourceSystemFunctions.YEAR, "year", Messages.getString(Messages.SystemSource.Year_result_d_description), Messages.getString(Messages.SystemSource.Year_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addTimeFunction(SourceSystemFunctions.HOUR, "hour", Messages.getString(Messages.SystemSource.Hour_result_t_description), Messages.getString(Messages.SystemSource.Hour_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addTimeFunction(SourceSystemFunctions.MINUTE, "minute", Messages.getString(Messages.SystemSource.Minute_result_t_description), Messages.getString(Messages.SystemSource.Minute_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
        addTimeFunction(SourceSystemFunctions.SECOND, "second", Messages.getString(Messages.SystemSource.Second_result_t_description), Messages.getString(Messages.SystemSource.Second_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		addQuarterFunction(SourceSystemFunctions.QUARTER, "quarter", Messages.getString(Messages.SystemSource.Quarter_result_d_description), Messages.getString(Messages.SystemSource.Quarter_result_ts_description), DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		addTimestampAddFunction();
        addTimestampDiffFunction();
        addTimeZoneFunctions();
        addTimestampCreateFunction();
        addUnixTimeFunctions();
		                  
        // string
        addStringFunction(SourceSystemFunctions.LENGTH, Messages.getString(Messages.SystemSource.Length_result), "length", DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$ //$NON-NLS-2$ 
        addStringFunction(SourceSystemFunctions.UCASE, Messages.getString(Messages.SystemSource.Ucase_result), "upperCase", DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$ //$NON-NLS-2$ 
        addStringFunction(SourceSystemFunctions.LCASE, Messages.getString(Messages.SystemSource.Lcase_result), "lowerCase", DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$ //$NON-NLS-2$ 
		addStringFunction("lower", Messages.getString(Messages.SystemSource.Lower_result), "lowerCase", DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		addStringFunction("upper", Messages.getString(Messages.SystemSource.Upper_result), "upperCase", DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        addStringFunction(SourceSystemFunctions.LTRIM, Messages.getString(Messages.SystemSource.Left_result), "leftTrim", DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$ //$NON-NLS-2$ 
        addStringFunction(SourceSystemFunctions.RTRIM, Messages.getString(Messages.SystemSource.Right_result), "rightTrim", DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$ //$NON-NLS-2$ 
        addConcatFunction();    
        addSubstringFunction(); 
        addLeftRightFunctions();
        addLocateFunction();
        addReplaceFunction();
        addAsciiFunction();
        addCharFunction();
        addInitCapFunction();
        addLpadFunction();
        addRpadFunction();
        addTranslateFunction();
        addRepeatFunction();
		addSpaceFunction();
		addInsertFunction();

		// Since 8.0.0
		addEndsWithFunction();
		
        // clob
        addClobFunction(SourceSystemFunctions.UCASE, Messages.getString(Messages.SystemSource.UcaseClob_result), "upperCase", DataTypeManagerService.DefaultDataTypes.CLOB); //$NON-NLS-1$ //$NON-NLS-2$ 
        addClobFunction(SourceSystemFunctions.LCASE, Messages.getString(Messages.SystemSource.LcaseClob_result), "lowerCase", DataTypeManagerService.DefaultDataTypes.CLOB); //$NON-NLS-1$ //$NON-NLS-2$ 
        addClobFunction("lower", Messages.getString(Messages.SystemSource.LowerClob_result), "lowerCase", DataTypeManagerService.DefaultDataTypes.CLOB); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        addClobFunction("upper", Messages.getString(Messages.SystemSource.UpperClob_result), "upperCase", DataTypeManagerService.DefaultDataTypes.CLOB); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
		// Removed 8.0.0
		addToCharsFunction();
        addToBytesFunction();

        // conversion
        addConversionFunctions();   
        
        // miscellaneous functions
        addContextFunctions(); 
        addRowLimitFunctions();                        
        addRowLimitExceptionFunctions();                        
        addDecodeFunctions();
        addLookupFunctions();
        addUserFunction();
        addCurrentDatabaseFunction();
        if (allowEnvFunction) {
        	addEnvFunction();
        }
        addSessionIdFunction();
        addCommandPayloadFunctions();
		addIfNullFunctions();
        
		// format 
		addFormatTimestampFunction();  
		addFormatNumberFunctions();
		
		// parse
		addParseTimestampFunction();
		addParseNumberFunctions();
        
        // xml functions
        addXpathValueFunction();
        addXslTransformFunction();
        addXmlConcat();
        addXmlComment();
        addXmlPi();
        addJsonToXml();
        
        addSecurityFunctions();

        for (String type : dataTypeManager.getAllDataTypeNames()) {
        	if (!dataTypeManager.isNonComparable(type)) {
        		addTypedNullIfFunction(type);
        	}
        	addTypedCoalesceFunction(type);
        }
        
        addUnescape();
        addUuidFunction();
        addArrayGet();
        addArrayLength();
        addTrimFunction();

		// Since 8.0.0
        addFunctions(JSONFunctionMethods.class);
        addFunctions(SystemFunctionMethods.class);
        addFunctions(FunctionMethods.class);

        // Since 8.10
        addFunctions(GeometryFunctionMethods.class);

        // Added here in Teiid 8.10 but the class has existed long before this
        if (teiidVersion.isGreaterThanOrEqualTo(Version.TEIID_8_10.get()))
            addFunctions(XMLSystemFunctions.class);
    }

    /**
     * Taken from TypeFacility
     *
     * Convert a primitive class to the corresponding object class
     *
     * @param clazz
     * @return
     */
    private Class<?> convertPrimitiveToObject(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        }
        if      ( clazz == Boolean.TYPE   ) clazz = Boolean.class;
        else if ( clazz == Character.TYPE ) clazz = Character.class;
        else if ( clazz == Byte.TYPE      ) clazz = Byte.class;
        else if ( clazz == Short.TYPE     ) clazz = Short.class;
        else if ( clazz == Integer.TYPE   ) clazz = Integer.class;
        else if ( clazz == Long.TYPE      ) clazz = Long.class;
        else if ( clazz == Float.TYPE     ) clazz = Float.class;
        else if ( clazz == Double.TYPE    ) clazz = Double.class;
        return clazz;
    }

    private void addFunctions(Class<?> clazz) {
        if(! AnnotationUtils.isApplicable(clazz, teiidVersion))
            return;
        
		Method[] methods = clazz.getMethods();
		//need a consistent order for tests
        Arrays.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(Method arg0, Method arg1) {
                return arg0.toGenericString().compareTo(arg1.toGenericString());
            }
        });
		for (Method method : methods) {
			TeiidFunction f = method.getAnnotation(TeiidFunction.class);
			if (f == null) {
				continue;
			}
			String name = f.name();
			if (name.isEmpty()) {
				name = method.getName();
			}
			addFunction(method, f, name);
            if (!f.alias().isEmpty()) {
                addFunction(method, f, f.alias());
            }
		}
	}

    private FunctionMethod addFunction(Method method, TeiidFunction f, String name) {
        FunctionMethod func = MetadataFactory.createFunctionFromMethod(teiidVersion, name, method);
        Messages.SystemSource descKey = Messages.SystemSource.safeValueOf(name + "_description"); //$NON-NLS-1$
        Messages.SystemSource resultKey = Messages.SystemSource.safeValueOf(name + "_result"); //$NON-NLS-1$

        func.setDescription(Messages.getString(descKey));
        func.setCategory(f.category());
        for (int i = 0; i < func.getInputParameterCount(); i++) {
            Messages.SystemSource paramKey = Messages.SystemSource.safeValueOf(name + "_param" + (i +1)); //$NON-NLS-1$
            func.getInputParameters().get(i).setDescription(Messages.getString(paramKey));
        }
        func.getOutputParameter().setDescription(Messages.getString(resultKey));
        if (f.nullOnNull()) {
            func.setNullOnNull(true);
        }
        func.setDeterminism(f.determinism());
        func.setPushdown(f.pushdown());
        functions.add(func);
        return func;
    }

    private void addTrimFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.TRIM, Messages.getString(Messages.SystemSource.trim_description), STRING, FUNCTION_CLASS, SourceSystemFunctions.TRIM,//$NON-NLS-1$ 
                new FunctionParameter[] {
            		new FunctionParameter(teiidVersion, "spec", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.trim_arg1)),//$NON-NLS-1$ //$NON-NLS-2$
            		new FunctionParameter(teiidVersion, "trimChar", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.trim_arg2)),//$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.trim_arg3)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.trim_result)) ) );   //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addArrayLength() {
    	functions.add(new FunctionMethod(SourceSystemFunctions.ARRAY_LENGTH, Messages.getString(Messages.SystemSource.array_length_description), MISCELLANEOUS, PushDown.CAN_PUSHDOWN, FUNCTION_CLASS, SourceSystemFunctions.ARRAY_LENGTH, //$NON-NLS-1$ 
                Arrays.asList( 
                    new FunctionParameter(teiidVersion, "array", DataTypeManagerService.DefaultDataTypes.OBJECT, Messages.getString(Messages.SystemSource.array_param1))), //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.array_length_result)), true, Determinism.DETERMINISTIC ) );       //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addArrayGet() {
    	functions.add(new FunctionMethod(SourceSystemFunctions.ARRAY_GET, Messages.getString(Messages.SystemSource.array_get_description), MISCELLANEOUS, PushDown.CAN_PUSHDOWN, FUNCTION_CLASS, SourceSystemFunctions.ARRAY_GET, //$NON-NLS-1$ 
                Arrays.asList( 
                    new FunctionParameter(teiidVersion, "array", DataTypeManagerService.DefaultDataTypes.OBJECT, Messages.getString(Messages.SystemSource.array_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "index", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.array_get_param2))), //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.OBJECT, Messages.getString(Messages.SystemSource.array_get_result)), true, Determinism.DETERMINISTIC ) );       //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addUnescape() {
    	functions.add(new FunctionMethod(SourceSystemFunctions.UNESCAPE, Messages.getString(Messages.SystemSource.unescape_description), STRING, PushDown.CANNOT_PUSHDOWN, FUNCTION_CLASS, SourceSystemFunctions.UNESCAPE, //$NON-NLS-1$ 
                Arrays.asList( 
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.unescape_param1))), //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.unescape_result)), true, Determinism.DETERMINISTIC ) );       //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addSecurityFunctions() {
        functions.add(new FunctionMethod("hasRole", Messages.getString(Messages.SystemSource.hasRole_description), SECURITY, PushDown.CANNOT_PUSHDOWN, SECURITY_FUNCTION_CLASS, "hasRole", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                        Arrays.asList( 
                                            new FunctionParameter(teiidVersion, "roleType", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.hasRole_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                                            new FunctionParameter(teiidVersion, "roleName", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.hasRole_param2))), //$NON-NLS-1$ //$NON-NLS-2$ 
                                        new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.BOOLEAN, Messages.getString(Messages.SystemSource.hasRole_result)), true, Determinism.USER_DETERMINISTIC ) );       //$NON-NLS-1$ //$NON-NLS-2$
        
        functions.add(new FunctionMethod("hasRole", Messages.getString(Messages.SystemSource.hasRole_description), SECURITY, PushDown.CANNOT_PUSHDOWN, SECURITY_FUNCTION_CLASS, "hasRole", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                Arrays.asList( 
                    new FunctionParameter(teiidVersion, "roleName", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.hasRole_param2))), //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.BOOLEAN, Messages.getString(Messages.SystemSource.hasRole_result)), true, Determinism.USER_DETERMINISTIC ) );       //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addFormatNumberFunctions() {
		addFormatNumberFunction(SourceSystemFunctions.FORMATINTEGER, Messages.getString(Messages.SystemSource.Formatinteger_description), "format", "integer", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Formatinteger_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addFormatNumberFunction(SourceSystemFunctions.FORMATLONG, Messages.getString(Messages.SystemSource.Formatlong_description), "format", "long", DataTypeManagerService.DefaultDataTypes.LONG, Messages.getString(Messages.SystemSource.Formatlong_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addFormatNumberFunction(SourceSystemFunctions.FORMATDOUBLE, Messages.getString(Messages.SystemSource.Formatdouble_description), "format", "double", DataTypeManagerService.DefaultDataTypes.DOUBLE, Messages.getString(Messages.SystemSource.Formatdouble_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addFormatNumberFunction(SourceSystemFunctions.FORMATFLOAT, Messages.getString(Messages.SystemSource.Formatfloat_description), "format", "float", DataTypeManagerService.DefaultDataTypes.FLOAT, Messages.getString(Messages.SystemSource.Formatfloat_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addFormatNumberFunction(SourceSystemFunctions.FORMATBIGINTEGER, Messages.getString(Messages.SystemSource.Formatbiginteger_description), "format", "biginteger", DataTypeManagerService.DefaultDataTypes.BIG_INTEGER, Messages.getString(Messages.SystemSource.Formatbiginteger_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addFormatNumberFunction(SourceSystemFunctions.FORMATBIGDECIMAL, Messages.getString(Messages.SystemSource.Formatbigdecimal_description), "format", "bigdecimal", DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL, Messages.getString(Messages.SystemSource.Formatbigdecimal_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
	}
	
	private void addParseNumberFunctions() {
		addParseNumberFunction(SourceSystemFunctions.PARSEINTEGER, Messages.getString(Messages.SystemSource.Parseinteger_description), "parseInteger", "integer", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Parseinteger_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addParseNumberFunction(SourceSystemFunctions.PARSELONG, Messages.getString(Messages.SystemSource.Parselong_description), "parseLong", "long", DataTypeManagerService.DefaultDataTypes.LONG, Messages.getString(Messages.SystemSource.Parselong_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addParseNumberFunction(SourceSystemFunctions.PARSEDOUBLE, Messages.getString(Messages.SystemSource.Parsedouble_description), "parseDouble", "double", DataTypeManagerService.DefaultDataTypes.DOUBLE, Messages.getString(Messages.SystemSource.Parsedouble_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addParseNumberFunction(SourceSystemFunctions.PARSEFLOAT, Messages.getString(Messages.SystemSource.Parsefloat_description), "parseFloat", "float", DataTypeManagerService.DefaultDataTypes.FLOAT, Messages.getString(Messages.SystemSource.Parsefloat_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addParseNumberFunction(SourceSystemFunctions.PARSEBIGINTEGER, Messages.getString(Messages.SystemSource.Parsebiginteger_description), "parseBigInteger", "biginteger", DataTypeManagerService.DefaultDataTypes.BIG_INTEGER, Messages.getString(Messages.SystemSource.Parsebiginteger_result_description)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
		addParseNumberFunction(SourceSystemFunctions.PARSEBIGDECIMAL, Messages.getString(Messages.SystemSource.Parsebigdecimal_description), "parseBigDecimal", "bigdecimal", DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL, Messages.getString(Messages.SystemSource.Parsebigdecimal_result_description));	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
	}
	
    private void addArithmeticFunction(String functionName, String description, String methodName, String resultsDescription) {
        addTypedArithmeticFunction(functionName, description, methodName, resultsDescription, DataTypeManagerService.DefaultDataTypes.INTEGER);
        addTypedArithmeticFunction(functionName, description, methodName, resultsDescription, DataTypeManagerService.DefaultDataTypes.LONG);
        addTypedArithmeticFunction(functionName, description, methodName, resultsDescription, DataTypeManagerService.DefaultDataTypes.FLOAT);
        addTypedArithmeticFunction(functionName, description, methodName, resultsDescription, DataTypeManagerService.DefaultDataTypes.DOUBLE);
        addTypedArithmeticFunction(functionName, description, methodName, resultsDescription, DataTypeManagerService.DefaultDataTypes.BIG_INTEGER);
        addTypedArithmeticFunction(functionName, description, methodName, resultsDescription, DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL);
    }

    private void addTypedArithmeticFunction(String functionName, String description, String methodName, String resultsDescription, DefaultDataTypes type) {
        addTypedArithmeticFunction(functionName, description, methodName, resultsDescription, type.getId());
    }

    private void addTypedArithmeticFunction(String functionName, String description, String methodName, String resultsDescription, String type) {
        functions.add(
            new FunctionMethod(functionName, description, NUMERIC, FUNCTION_CLASS, methodName,
                new FunctionParameter[] { 
                    new FunctionParameter(teiidVersion, "op1", type, Messages.getString(Messages.SystemSource.Arith_left_op)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "op2", type, Messages.getString(Messages.SystemSource.Arith_right_op)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", type, resultsDescription) ) );                 //$NON-NLS-1$
    }
    
    private void addAbsFunction() {
        addTypedAbsFunction(DataTypeManagerService.DefaultDataTypes.INTEGER);
        addTypedAbsFunction(DataTypeManagerService.DefaultDataTypes.LONG);
        addTypedAbsFunction(DataTypeManagerService.DefaultDataTypes.FLOAT);
        addTypedAbsFunction(DataTypeManagerService.DefaultDataTypes.DOUBLE);
        addTypedAbsFunction(DataTypeManagerService.DefaultDataTypes.BIG_INTEGER);
        addTypedAbsFunction(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL);
    }

    private void addTypedAbsFunction(DefaultDataTypes type) {
        addTypedAbsFunction(type.getId());
    }

    private void addTypedAbsFunction(String type) {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.ABS, Messages.getString(Messages.SystemSource.Abs_description), NUMERIC, FUNCTION_CLASS, "abs", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] { 
                    new FunctionParameter(teiidVersion, "number", type, Messages.getString(Messages.SystemSource.Abs_arg)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", type, Messages.getString(Messages.SystemSource.Abs_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addRandFunction() {
        // With Seed
        FunctionMethod rand = new FunctionMethod(SourceSystemFunctions.RAND, Messages.getString(Messages.SystemSource.Rand_description), NUMERIC, FUNCTION_CLASS, "rand", //$NON-NLS-1$ //$NON-NLS-2$ 
                                          new FunctionParameter[] {new FunctionParameter(teiidVersion, "seed", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Rand_arg)) }, //$NON-NLS-1$ //$NON-NLS-2$
                                          new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DOUBLE, Messages.getString(Messages.SystemSource.Rand_result_description)) );                 //$NON-NLS-1$ //$NON-NLS-2$
        rand.setNullOnNull(false);
        rand.setDeterminism(Determinism.NONDETERMINISTIC);
        functions.add(rand);
        // Without Seed
        rand = new FunctionMethod(SourceSystemFunctions.RAND, Messages.getString(Messages.SystemSource.Rand_description), NUMERIC, FUNCTION_CLASS, "rand", //$NON-NLS-1$ //$NON-NLS-2$ 
                                          new FunctionParameter[] {}, 
                                          new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DOUBLE, Messages.getString(Messages.SystemSource.Rand_result_description)) ); //$NON-NLS-1$ //$NON-NLS-2$
        rand.setDeterminism(Determinism.NONDETERMINISTIC);
        functions.add(rand);
    }
    
    private void addUuidFunction() {
        FunctionMethod rand = new FunctionMethod(SourceSystemFunctions.UUID, Messages.getString(Messages.SystemSource.uuid_description), MISCELLANEOUS, FUNCTION_CLASS, "uuid", //$NON-NLS-1$ //$NON-NLS-2$ 
                                          new FunctionParameter[] {},
                                          new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.uuid_result_description)) );                 //$NON-NLS-1$ //$NON-NLS-2$
        rand.setDeterminism(Determinism.NONDETERMINISTIC);
        functions.add(rand);
    }

	private void addDoubleFunction(String name, String description) {
		functions.add(
			new FunctionMethod(name, description, NUMERIC, FUNCTION_CLASS, name,
				new FunctionParameter[] { 
					new FunctionParameter(teiidVersion, "number", DataTypeManagerService.DefaultDataTypes.DOUBLE, Messages.getString(Messages.SystemSource.Double_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DOUBLE, description) ) );                 //$NON-NLS-1$
		functions.add(
				new FunctionMethod(name, description, NUMERIC, FUNCTION_CLASS, name,
					new FunctionParameter[] { 
						new FunctionParameter(teiidVersion, "number", DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL, Messages.getString(Messages.SystemSource.Double_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DOUBLE, description) ) );                 //$NON-NLS-1$
	}

	private void addAtan2Function(String name, String description) {
		functions.add(
			new FunctionMethod(name, description, NUMERIC, FUNCTION_CLASS, name,
				new FunctionParameter[] { 
					new FunctionParameter(teiidVersion, "number1", DataTypeManagerService.DefaultDataTypes.DOUBLE, Messages.getString(Messages.SystemSource.Atan_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "number2", DataTypeManagerService.DefaultDataTypes.DOUBLE, Messages.getString(Messages.SystemSource.Atan_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DOUBLE, description) ) );                 //$NON-NLS-1$
		functions.add(
				new FunctionMethod(name, description, NUMERIC, FUNCTION_CLASS, name,
					new FunctionParameter[] { 
						new FunctionParameter(teiidVersion, "number1", DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL, Messages.getString(Messages.SystemSource.Atan_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
						new FunctionParameter(teiidVersion, "number2", DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL, Messages.getString(Messages.SystemSource.Atan_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DOUBLE, description) ) );                 //$NON-NLS-1$
	}

	private void addPiFunction(String name, String description) {
		functions.add(
			new FunctionMethod(name, description, NUMERIC, FUNCTION_CLASS, name,
				new FunctionParameter[] { },
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DOUBLE, description) ) );                 //$NON-NLS-1$
	}
			
    private void addPowerFunction() {
        addTypedPowerFunction(DataTypeManagerService.DefaultDataTypes.DOUBLE, DataTypeManagerService.DefaultDataTypes.DOUBLE);
        addTypedPowerFunction(DataTypeManagerService.DefaultDataTypes.BIG_INTEGER, DataTypeManagerService.DefaultDataTypes.INTEGER);        
        addTypedPowerFunction(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL, DataTypeManagerService.DefaultDataTypes.INTEGER);
    }

    private void addTypedPowerFunction(DefaultDataTypes baseType, DefaultDataTypes powerType) {
        addTypedPowerFunction(baseType.getId(), powerType.getId());
    }

    private void addTypedPowerFunction(String baseType, String powerType) { 
        functions.add(
            new FunctionMethod(SourceSystemFunctions.POWER, Messages.getString(Messages.SystemSource.Power_description), NUMERIC, FUNCTION_CLASS, "power", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] { 
                    new FunctionParameter(teiidVersion, "base", baseType, Messages.getString(Messages.SystemSource.Power_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "power", powerType, Messages.getString(Messages.SystemSource.Power_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", baseType, Messages.getString(Messages.SystemSource.Power_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    } 

    private void addRoundFunction() {
        addTypedRoundFunction(DataTypeManagerService.DefaultDataTypes.INTEGER);
        addTypedRoundFunction(DataTypeManagerService.DefaultDataTypes.FLOAT);
        addTypedRoundFunction(DataTypeManagerService.DefaultDataTypes.DOUBLE);
        addTypedRoundFunction(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL);        
    }

    private void addTypedRoundFunction(DefaultDataTypes roundType) {
        addTypedRoundFunction(roundType.getId());
    }

    private void addTypedRoundFunction(String roundType) {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.ROUND, Messages.getString(Messages.SystemSource.Round_description), NUMERIC, FUNCTION_CLASS, "round", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] { 
                    new FunctionParameter(teiidVersion, "number", roundType, Messages.getString(Messages.SystemSource.Round_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "places", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Round_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", roundType, Messages.getString(Messages.SystemSource.Round_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    } 

    private void addSignFunction() {
        addTypedSignFunction(DataTypeManagerService.DefaultDataTypes.INTEGER);
        addTypedSignFunction(DataTypeManagerService.DefaultDataTypes.LONG);
        addTypedSignFunction(DataTypeManagerService.DefaultDataTypes.FLOAT);
        addTypedSignFunction(DataTypeManagerService.DefaultDataTypes.DOUBLE);
        addTypedSignFunction(DataTypeManagerService.DefaultDataTypes.BIG_INTEGER);
        addTypedSignFunction(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL);
    }

    private void addTypedSignFunction(DefaultDataTypes type) {
        addTypedSignFunction(type.getId());
    }

    private void addTypedSignFunction(String type) {        
        functions.add(
            new FunctionMethod(SourceSystemFunctions.SIGN, Messages.getString(Messages.SystemSource.Sign_description), NUMERIC, FUNCTION_CLASS, "sign", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] { 
                    new FunctionParameter(teiidVersion, "number", type, Messages.getString(Messages.SystemSource.Sign_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Sign_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    } 
    
    private void addSqrtFunction() {
        addTypedSqrtFunction(DataTypeManagerService.DefaultDataTypes.LONG);
        addTypedSqrtFunction(DataTypeManagerService.DefaultDataTypes.DOUBLE);
        addTypedSqrtFunction(DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL);
    }

    private void addTypedSqrtFunction(DefaultDataTypes type) {
        addTypedSqrtFunction(type.getId());
    }

    private void addTypedSqrtFunction(String type) {    
        functions.add(
            new FunctionMethod(SourceSystemFunctions.SQRT, Messages.getString(Messages.SystemSource.Sqrt_description), NUMERIC, FUNCTION_CLASS, "sqrt", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] { 
                    new FunctionParameter(teiidVersion, "number", type, Messages.getString(Messages.SystemSource.Sqrt_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DOUBLE, Messages.getString(Messages.SystemSource.Sqrt_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    } 

    /**
     * Date functions a marked as command deterministic, since we prefer pre-evaluation rather than row-by-row
     * evaluation.
     */
    private void addConstantDateFunction(String name, String description, String methodName, DefaultDataTypes returnType) {
        addConstantDateFunction(name, description, methodName, returnType.getId());
    }

    /**
     * Date functions a marked as command deterministic, since we prefer pre-evaluation rather than row-by-row
     * evaluation.
     */
    private void addConstantDateFunction(String name, String description, String methodName, String returnType) {
        FunctionMethod method = new FunctionMethod(name, description, DATETIME, FUNCTION_CLASS, methodName,
                new FunctionParameter[] {},
                new FunctionParameter(teiidVersion, "result", returnType, description));                 //$NON-NLS-1$
        method.setDeterminism(Determinism.COMMAND_DETERMINISTIC);
        functions.add(method);
    }

    private void addDateFunction(String name, String methodName, String dateDesc, String timestampDesc, DefaultDataTypes returnType) {
        addDateFunction(name, methodName, dateDesc, timestampDesc, returnType.getId());
    }

    private void addDateFunction(String name, String methodName, String dateDesc, String timestampDesc, String returnType) {
        functions.add(
            new FunctionMethod(name, dateDesc, DATETIME, FUNCTION_CLASS, methodName,
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "date", DataTypeManagerService.DefaultDataTypes.DATE, dateDesc) }, //$NON-NLS-1$
                new FunctionParameter(teiidVersion, "result", returnType, dateDesc) ) );                 //$NON-NLS-1$
        functions.add(
            new FunctionMethod(name, timestampDesc, DATETIME, FUNCTION_CLASS, methodName,
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, timestampDesc) }, //$NON-NLS-1$
                new FunctionParameter(teiidVersion, "result", returnType, timestampDesc) ) );                 //$NON-NLS-1$
    }

    private void addQuarterFunction(String name, String methodName, String dateDesc, String timestampDesc, DefaultDataTypes returnType) {
        addQuarterFunction(name, methodName, dateDesc, timestampDesc, returnType.getId());
    }

	private void addQuarterFunction(String name, String methodName, String dateDesc, String timestampDesc, String returnType) {
		functions.add(
			new FunctionMethod(name, dateDesc, DATETIME, FUNCTION_CLASS, methodName,
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "date", DataTypeManagerService.DefaultDataTypes.DATE, dateDesc) }, //$NON-NLS-1$
				new FunctionParameter(teiidVersion, "result", returnType, dateDesc) ) );                 //$NON-NLS-1$
		functions.add(
			new FunctionMethod(name, timestampDesc, DATETIME, FUNCTION_CLASS, methodName,
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, timestampDesc) }, //$NON-NLS-1$
				new FunctionParameter(teiidVersion, "result", returnType, timestampDesc) ) );                 //$NON-NLS-1$
	}

	private void addTimestampAddFunction() {
		functions.add(
			createSyntheticMethod(SourceSystemFunctions.TIMESTAMPADD, Messages.getString(Messages.SystemSource.Timestampadd_d_description), DATETIME, null, null, new FunctionParameter[] { //$NON-NLS-1$
				new FunctionParameter(teiidVersion, "interval", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Timestampadd_d_arg1)),  //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "count", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Timestampadd_d_arg2)),  //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.DATE, Messages.getString(Messages.SystemSource.Timestampadd_d_arg3))}, //$NON-NLS-1$ //$NON-NLS-2$ 
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DATE, Messages.getString(Messages.SystemSource.Timestampadd_d_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
		functions.add(
			createSyntheticMethod(SourceSystemFunctions.TIMESTAMPADD, Messages.getString(Messages.SystemSource.Timestampadd_t_description), DATETIME, null, null, new FunctionParameter[] { //$NON-NLS-1$
				new FunctionParameter(teiidVersion, "interval", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Timestampadd_t_arg1)),  //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "count", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Timestampadd_t_arg2)),  //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.TIME, Messages.getString(Messages.SystemSource.Timestampadd_t_arg3))}, //$NON-NLS-1$ //$NON-NLS-2$  
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.TIME, Messages.getString(Messages.SystemSource.Timestampadd_t_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
		functions.add(
			new FunctionMethod(SourceSystemFunctions.TIMESTAMPADD, Messages.getString(Messages.SystemSource.Timestampadd_ts_description), DATETIME, FUNCTION_CLASS, "timestampAdd", //$NON-NLS-1$ //$NON-NLS-2$ 
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "interval", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Timestampadd_ts_arg1)),  //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "count", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Timestampadd_ts_arg2)),  //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.Timestampadd_ts_arg3))}, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.Timestampadd_ts_result)) ) );			                //$NON-NLS-1$ //$NON-NLS-2$
	}

    private void addTimestampDiffFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.TIMESTAMPDIFF, Messages.getString(Messages.SystemSource.Timestampdiff_ts_description), DATETIME, FUNCTION_CLASS, "timestampDiff", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "interval", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Timestampdiff_ts_arg1)),  //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "timestamp1", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.Timestampdiff_ts_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "timestamp2", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.Timestampdiff_ts_arg3))}, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.LONG, Messages.getString(Messages.SystemSource.Timestampdiff_ts_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addTimestampCreateFunction() {
        functions.add(
              new FunctionMethod(SourceSystemFunctions.TIMESTAMPCREATE, Messages.getString(Messages.SystemSource.TimestampCreate_description), DATETIME, FUNCTION_CLASS, "timestampCreate", //$NON-NLS-1$ //$NON-NLS-2$ 
                  new FunctionParameter[] {
                      new FunctionParameter(teiidVersion, "date", DataTypeManagerService.DefaultDataTypes.DATE, Messages.getString(Messages.SystemSource.TimestampCreate_arg1)),  //$NON-NLS-1$ //$NON-NLS-2$
                      new FunctionParameter(teiidVersion, "time", DataTypeManagerService.DefaultDataTypes.TIME, Messages.getString(Messages.SystemSource.TimestampCreate_arg2))}, //$NON-NLS-1$ //$NON-NLS-2$
                  new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.TimestampCreate_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addTimeFunction(String name, String methodName, String timeDesc, String timestampDesc, DefaultDataTypes returnType) {
        addTimeFunction(name, methodName, timeDesc, timestampDesc, returnType.getId());
    }

    private void addTimeFunction(String name, String methodName, String timeDesc, String timestampDesc, String returnType) {
        functions.add(
            new FunctionMethod(name, timeDesc, DATETIME, FUNCTION_CLASS, methodName,
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "time", DataTypeManagerService.DefaultDataTypes.TIME, timeDesc) }, //$NON-NLS-1$
                new FunctionParameter(teiidVersion, "result", returnType, timeDesc) ) );                 //$NON-NLS-1$
        functions.add(
            new FunctionMethod(name, timestampDesc, DATETIME, FUNCTION_CLASS, methodName,
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, timestampDesc) }, //$NON-NLS-1$
                new FunctionParameter(teiidVersion, "result", returnType, timestampDesc) ) );                 //$NON-NLS-1$
    }

    private void addStringFunction(String name, String description, String methodName, DefaultDataTypes returnType) {
        addStringFunction(name, description, methodName, returnType.getId());
    }

    private void addStringFunction(String name, String description, String methodName, String returnType) {
        functions.add(
            new FunctionMethod(name, description, STRING, FUNCTION_CLASS, methodName,
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Stringfunc_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", returnType, description) ) );                 //$NON-NLS-1$
    }

    private void addClobFunction(String name, String description, String methodName, DefaultDataTypes returnType) {
        addClobFunction(name, description, methodName, returnType.getId());
    }

    private void addClobFunction(String name, String description, String methodName, String returnType) {
        functions.add(
            new FunctionMethod(name, description, STRING, PushDown.MUST_PUSHDOWN, FUNCTION_CLASS, methodName,
                Arrays.asList(
                    new FunctionParameter(teiidVersion, "clob", DataTypeManagerService.DefaultDataTypes.CLOB, Messages.getString(Messages.SystemSource.Clobfunc_arg1)) ), //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", returnType, description), true, Determinism.DETERMINISTIC ) );                 //$NON-NLS-1$
    }

    private void addConcatFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.CONCAT, Messages.getString(Messages.SystemSource.Concat_description), STRING, FUNCTION_CLASS, "concat", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string1", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concat_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "string2", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concat_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concat_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(
            new FunctionMethod("||", Messages.getString(Messages.SystemSource.Concatop_description), STRING, FUNCTION_CLASS, "concat", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string1", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concatop_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "string2", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concatop_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concatop_result_description)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
        
        FunctionMethod concat2 = new FunctionMethod(SourceSystemFunctions.CONCAT2, Messages.getString(Messages.SystemSource.Concat_description), STRING, FUNCTION_CLASS, "concat2", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
            		new FunctionParameter(teiidVersion, "string1", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concat_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
            		new FunctionParameter(teiidVersion, "string2", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concat_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
            	new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Concat_result_description)) );                 //$NON-NLS-1$ //$NON-NLS-2$
        concat2.setNullOnNull(false);
        functions.add(concat2);                         
    }

    private void addSubstringFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.SUBSTRING, Messages.getString(Messages.SystemSource.Substring_description), STRING, FUNCTION_CLASS, "substring", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Substring_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "index", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Substring_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "length", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Substring_arg3)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Substring_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(
            new FunctionMethod(SourceSystemFunctions.SUBSTRING, Messages.getString(Messages.SystemSource.Susbstring2_description), STRING, FUNCTION_CLASS, "substring", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Substring2_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "index", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Substring2_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Substring2_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addLeftRightFunctions() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.LEFT, Messages.getString(Messages.SystemSource.Left_description), STRING, FUNCTION_CLASS, "left", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Left_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "length", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Left_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Left2_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(
            new FunctionMethod(SourceSystemFunctions.RIGHT, Messages.getString(Messages.SystemSource.Right_description), STRING, FUNCTION_CLASS, "right", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Right_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "length", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Right_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Right2_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    }
           
    private void addLocateFunction() {
        FunctionMethod func =
            new FunctionMethod(SourceSystemFunctions.LOCATE, Messages.getString(Messages.SystemSource.Locate_description), STRING, FUNCTION_CLASS, "locate", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "substring", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Locate_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Locate_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "index", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Locate_arg3)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Locate_result)) );                 //$NON-NLS-1$ //$NON-NLS-2$
        func.setNullOnNull(false);
        functions.add(func);
        functions.add(
            new FunctionMethod(SourceSystemFunctions.LOCATE, Messages.getString(Messages.SystemSource.Locate2_description), STRING, FUNCTION_CLASS, "locate", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "substring", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Locate2_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Locate2_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Locate2_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addReplaceFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.REPLACE, Messages.getString(Messages.SystemSource.Replace_description), STRING, FUNCTION_CLASS, "replace", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Replace_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "substring", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Replace_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "replacement", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Replace_arg3)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Replace_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    }

	@Since(Version.TEIID_8_0)
    private void addEndsWithFunction() {
	    if (teiidVersion.getMinimumVersion().isLessThan(Version.TEIID_8_0.get()))
            return;

        FunctionMethod f =
            new FunctionMethod(SourceSystemFunctions.ENDSWITH, Messages.getString(Messages.SystemSource.endswith_description), STRING, FUNCTION_CLASS, "endsWith", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "substring", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.endswith_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.endswith_arg2))}, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.endswith_result)) );                 //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(f);
    }    

	private void addRepeatFunction() {
		functions.add(
			new FunctionMethod(SourceSystemFunctions.REPEAT, Messages.getString(Messages.SystemSource.Repeat_description), STRING, FUNCTION_CLASS, "repeat", //$NON-NLS-1$ //$NON-NLS-2$ 
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Repeat_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "count", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Repeat_arg2))}, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Repeat_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addSpaceFunction() {
		functions.add(
			createSyntheticMethod(IFunctionLibrary.FunctionName.SPACE.text(), Messages.getString(Messages.SystemSource.Space_description), STRING, null, null, new FunctionParameter[] { //$NON-NLS-1$
				new FunctionParameter(teiidVersion, "count", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Space_arg1))}, //$NON-NLS-1$ //$NON-NLS-2$ 
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Space_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addInsertFunction() {
		functions.add(
			new FunctionMethod(SourceSystemFunctions.INSERT, Messages.getString(Messages.SystemSource.Insert_description), STRING, FUNCTION_CLASS, "insert", //$NON-NLS-1$ //$NON-NLS-2$ 
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "str1", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Insert_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "start", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Insert_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "length", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Insert_arg3)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "str2", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Insert_arg4)) }, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Insert_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Removed(Version.TEIID_8_0)
	private void addToCharsFunction() {
	    if (teiidVersion.getMinimumVersion().isGreaterThanOrEqualTo(Version.TEIID_8_0.get()))
	        return;

		functions.add(
			new FunctionMethod("to_chars", Messages.getString(Messages.SystemSource.encode_description), CONVERSION, FUNCTION_CLASS, "toChars", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$  
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "value", DataTypeManagerService.DefaultDataTypes.BLOB, Messages.getString(Messages.SystemSource.encode_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "encoding", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.encode_arg2))}, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.CLOB, Messages.getString(Messages.SystemSource.encode_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Removed(Version.TEIID_8_0)
	private void addToBytesFunction() {
	    if (teiidVersion.getMinimumVersion().isGreaterThanOrEqualTo(Version.TEIID_8_0.get()))
            return;

		functions.add(
			new FunctionMethod("to_bytes", Messages.getString(Messages.SystemSource.decode_description), CONVERSION, FUNCTION_CLASS, "toBytes", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$  
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "value", DataTypeManagerService.DefaultDataTypes.CLOB, Messages.getString(Messages.SystemSource.decode_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "encoding", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.decode_arg2))}, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.BLOB, Messages.getString(Messages.SystemSource.decode_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
	}

    private void addAsciiFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.ASCII, Messages.getString(Messages.SystemSource.Ascii_description), STRING, FUNCTION_CLASS, "ascii", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Ascii_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Ascii_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(
            new FunctionMethod(SourceSystemFunctions.ASCII, Messages.getString(Messages.SystemSource.Ascii2_description), STRING, FUNCTION_CLASS, "ascii", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "char", DataTypeManagerService.DefaultDataTypes.CHAR, Messages.getString(Messages.SystemSource.Ascii2_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Ascii2_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }

	private void addCharFunction() {
		functions.add(
			new FunctionMethod(SourceSystemFunctions.CHAR, Messages.getString(Messages.SystemSource.Char_description), STRING, FUNCTION_CLASS, "chr", //$NON-NLS-1$ //$NON-NLS-2$ 
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "code", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Char_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.CHAR, Messages.getString(Messages.SystemSource.Char_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(
                new FunctionMethod("chr", Messages.getString(Messages.SystemSource.Chr_description), STRING, FUNCTION_CLASS, "chr", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    new FunctionParameter[] {
                        new FunctionParameter(teiidVersion, "code", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Chr_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.CHAR, Messages.getString(Messages.SystemSource.Chr_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
    private void addInitCapFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.INITCAP, Messages.getString(Messages.SystemSource.Initcap_description), STRING, FUNCTION_CLASS, "initCap", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Initcap_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Initcap_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addLpadFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.LPAD, Messages.getString(Messages.SystemSource.Lpad_description), STRING, FUNCTION_CLASS, "lpad", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Lpad_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "length", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Lpad_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Lpad_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(
            new FunctionMethod(SourceSystemFunctions.LPAD, Messages.getString(Messages.SystemSource.Lpad3_description), STRING, FUNCTION_CLASS, "lpad", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Lpad3_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "length", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Lpad3_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "char", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Lpad3_arg3)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Lpad3_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addRpadFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.RPAD, Messages.getString(Messages.SystemSource.Rpad1_description), STRING, FUNCTION_CLASS, "rpad", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Rpad1_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "length", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Rpad1_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Rpad1_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(
            new FunctionMethod(SourceSystemFunctions.RPAD, Messages.getString(Messages.SystemSource.Rpad3_description), STRING, FUNCTION_CLASS, "rpad", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Rpad3_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "length", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Rpad3_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "char", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Rpad3_arg3)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Rpad3_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addTranslateFunction() {
        functions.add(
            new FunctionMethod(SourceSystemFunctions.TRANSLATE, Messages.getString(Messages.SystemSource.Translate_description), STRING, FUNCTION_CLASS, "translate", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "string", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Translate_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "source", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Translate_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "destination", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Translate_arg3)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Translate_result)) ) ); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addConversionFunctions() {
    	for (String type : dataTypeManager.getAllDataTypeNames()) {
            addTypedConversionFunction(SourceSystemFunctions.CONVERT, type); 
            addTypedConversionFunction("cast", type); //$NON-NLS-1$
    	}
    }
    
    private void addTypedConversionFunction(String name, String sourceType) {
        functions.add(
            new FunctionMethod(name, Messages.getString(Messages.SystemSource.Convert_description, sourceType), CONVERSION, FUNCTION_CLASS, "convert", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "value", sourceType, Messages.getString(Messages.SystemSource.Convert_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "target", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Convert_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.OBJECT, Messages.getString(Messages.SystemSource.Convert_result)) ) );                 //$NON-NLS-1$ //$NON-NLS-2$
    }    

    private void addContextFunctions() {
    	for (String contextType : dataTypeManager.getAllDataTypeNames()) {
    		for (String exprType : dataTypeManager.getAllDataTypeNames()) {
                addTypedContextFunction(contextType, exprType);
        	}
    	}
    }
    
    private void addTypedContextFunction(String contextType, String exprType) {
        functions.add(
            new FunctionMethod("context", Messages.getString(Messages.SystemSource.Context_description), MISCELLANEOUS, FUNCTION_CLASS, "context", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "context", contextType, Messages.getString(Messages.SystemSource.Context_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "element", exprType, Messages.getString(Messages.SystemSource.Context_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", exprType, Messages.getString(Messages.SystemSource.Context_result)) ) );                     //$NON-NLS-1$ //$NON-NLS-2$
    } 
    
    private void addRowLimitFunctions() {
    	for (String exprType : dataTypeManager.getAllDataTypeNames()) {
            functions.add(
                    new FunctionMethod("rowlimit", Messages.getString(Messages.SystemSource.Rowlimit_description), MISCELLANEOUS, FUNCTION_CLASS, "rowlimit", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        new FunctionParameter[] {
                            new FunctionParameter(teiidVersion, "element", exprType, Messages.getString(Messages.SystemSource.Rowlimit_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                        new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Rowlimit_result)) ) );                     //$NON-NLS-1$ //$NON-NLS-2$
    	}
    }
    
    private void addRowLimitExceptionFunctions() {
    	for (String exprType : dataTypeManager.getAllDataTypeNames()) {
            functions.add(
                    new FunctionMethod("rowlimitexception", Messages.getString(Messages.SystemSource.RowlimitException_description), MISCELLANEOUS, FUNCTION_CLASS, "rowlimitexception", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        new FunctionParameter[] {
                            new FunctionParameter(teiidVersion, "element", exprType, Messages.getString(Messages.SystemSource.Rowlimit_arg1)) }, //$NON-NLS-1$ //$NON-NLS-2$
                        new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Rowlimit_result)) ) );                     //$NON-NLS-1$ //$NON-NLS-2$
    	}
    }    
    
    private void addDecodeFunctions(){
        addDecodeFunction("decodeInteger", DataTypeManagerService.DefaultDataTypes.INTEGER); //$NON-NLS-1$
        addDecodeFunction("decodeString", DataTypeManagerService.DefaultDataTypes.STRING); //$NON-NLS-1$
    }

    private void addDecodeFunction(String functionName, DefaultDataTypes resultType) {
        addDecodeFunction(functionName, resultType.getId());
    }

    private void addDecodeFunction(String functionName, String resultType) {
        functions.add(
        	createSyntheticMethod(functionName, Messages.getString(Messages.SystemSource.Decode1_description), MISCELLANEOUS, null, null, new FunctionParameter[] {  //$NON-NLS-1$
			    new FunctionParameter(teiidVersion, "input", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Decode1_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
			    new FunctionParameter(teiidVersion, "decodeString", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Decode1_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", resultType, Messages.getString(Messages.SystemSource.Decode1_result) ) ) );    //$NON-NLS-1$ //$NON-NLS-2$
                     
        functions.add(
        	createSyntheticMethod(functionName, Messages.getString(Messages.SystemSource.Decode2_description), MISCELLANEOUS, null, null, new FunctionParameter[] {  //$NON-NLS-1$
			    new FunctionParameter(teiidVersion, "input", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Decode2_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
			    new FunctionParameter(teiidVersion, "decodeString", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Decode2_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
			    new FunctionParameter(teiidVersion, "delimiter", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Decode2_arg3)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", resultType, Messages.getString(Messages.SystemSource.Decode2_result) ) ) );    //$NON-NLS-1$ //$NON-NLS-2$

    }

    private void addLookupFunctions() {
    	for (String keyValueType : dataTypeManager.getAllDataTypeNames()) {
            functions.add(
                    new FunctionMethod("lookup", Messages.getString(Messages.SystemSource.Lookup_description), MISCELLANEOUS, PushDown.CANNOT_PUSHDOWN, FUNCTION_CLASS, "lookup", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        Arrays.asList(
                            new FunctionParameter(teiidVersion, "codetable", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Lookup_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                            new FunctionParameter(teiidVersion, "returnelement", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Lookup_arg2)), //$NON-NLS-1$ //$NON-NLS-2$
                            new FunctionParameter(teiidVersion, "keyelement", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Lookup_arg3)), //$NON-NLS-1$ //$NON-NLS-2$
                            new FunctionParameter(teiidVersion, "keyvalue", keyValueType, Messages.getString(Messages.SystemSource.Lookup_arg4)) //$NON-NLS-1$ //$NON-NLS-2$
                             ),
                        new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.OBJECT, Messages.getString(Messages.SystemSource.Lookup_result)), false, Determinism.VDB_DETERMINISTIC ) );                     //$NON-NLS-1$ //$NON-NLS-2$
    	}
    }

    private void addUserFunction() {
        functions.add(
            new FunctionMethod("user", Messages.getString(Messages.SystemSource.User_description), MISCELLANEOUS, PushDown.CANNOT_PUSHDOWN, FUNCTION_CLASS, "user", null, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.User_result)), true, Determinism.USER_DETERMINISTIC) );                     //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addCurrentDatabaseFunction() {
        functions.add(
            new FunctionMethod("current_database", Messages.getString(Messages.SystemSource.current_database_description), MISCELLANEOUS, PushDown.CANNOT_PUSHDOWN, FUNCTION_CLASS, "current_database", null, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.current_database_result)), true, Determinism.VDB_DETERMINISTIC) );                     //$NON-NLS-1$ //$NON-NLS-2$
    }    
    
    private void addEnvFunction() {
        functions.add(
            new FunctionMethod("env", Messages.getString(Messages.SystemSource.Env_description), MISCELLANEOUS, PushDown.CANNOT_PUSHDOWN, FUNCTION_CLASS, "env", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                Arrays.asList(
                    new FunctionParameter(teiidVersion, "variablename", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Env_varname)) //$NON-NLS-1$ //$NON-NLS-2$
                     ),
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Env_result)), true, Determinism.DETERMINISTIC ) );                     //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addSessionIdFunction() {
        functions.add(
            new FunctionMethod(IFunctionLibrary.FunctionName.SESSION_ID.text(), Messages.getString(Messages.SystemSource.session_id_description), MISCELLANEOUS, PushDown.CANNOT_PUSHDOWN, FUNCTION_CLASS, "session_id", null, //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.session_id_result)), true, Determinism.SESSION_DETERMINISTIC) );                     //$NON-NLS-1$ //$NON-NLS-2$
    }    
    
    private void addCommandPayloadFunctions() {
        functions.add(
            new FunctionMethod("commandpayload", Messages.getString(Messages.SystemSource.CommandPayload_desc0), MISCELLANEOUS, PushDown.CANNOT_PUSHDOWN, FUNCTION_CLASS, "commandPayload", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 null,
                 new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.CommandPayload_result)), true, Determinism.COMMAND_DETERMINISTIC ) );                     //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(
            new FunctionMethod("commandpayload", Messages.getString(Messages.SystemSource.CommandPayload_desc1), MISCELLANEOUS, PushDown.CANNOT_PUSHDOWN, FUNCTION_CLASS, "commandPayload", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 Arrays.asList(
                     new FunctionParameter(teiidVersion, "property", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.CommandPayload_property)) //$NON-NLS-1$ //$NON-NLS-2$
                      ),
                 new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.CommandPayload_result)), true, Determinism.COMMAND_DETERMINISTIC ) );                     //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addIfNullFunctions() {
    	for (String type : dataTypeManager.getAllDataTypeNames()) {
            addNvlFunction(type);
            addIfNullFunction(type);
    	}
    }
	
    private void addNvlFunction(String valueType) {
        FunctionMethod nvl = 
            new FunctionMethod("nvl", Messages.getString(Messages.SystemSource.Nvl_description), MISCELLANEOUS, FUNCTION_CLASS, "ifnull", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                new FunctionParameter[] {
                    new FunctionParameter(teiidVersion, "value", valueType, Messages.getString(Messages.SystemSource.Nvl_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "valueIfNull", valueType, Messages.getString(Messages.SystemSource.Nvl_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", valueType, Messages.getString(Messages.SystemSource.Nvl_result)) ); //$NON-NLS-1$ //$NON-NLS-2$
        nvl.setNullOnNull(false);
        functions.add(nvl); 
    }

	private void addIfNullFunction(String valueType) {
		FunctionMethod nvl = 
			new FunctionMethod("ifnull", Messages.getString(Messages.SystemSource.Ifnull_description), MISCELLANEOUS, FUNCTION_CLASS, "ifnull", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				new FunctionParameter[] {
					new FunctionParameter(teiidVersion, "value", valueType, Messages.getString(Messages.SystemSource.Ifnull_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "valueIfNull", valueType, Messages.getString(Messages.SystemSource.Ifnull_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", valueType, Messages.getString(Messages.SystemSource.Ifnull_result)) ); //$NON-NLS-1$ //$NON-NLS-2$
        nvl.setNullOnNull(false);
        functions.add(nvl); 
	}
			
	private void addFormatTimestampFunction() {
		functions.add(
			new FunctionMethod(SourceSystemFunctions.FORMATTIMESTAMP, Messages.getString(Messages.SystemSource.Formattimestamp_description),CONVERSION, FUNCTION_CLASS, "format", //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter[] { 
					new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.Formattimestamp_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "format", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Formattimestamp_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Formattimestamp_result_description)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
		functions.add(
				createSyntheticMethod(IFunctionLibrary.FunctionName.FORMATDATE.text(), Messages.getString(Messages.SystemSource.Formatdate_description),CONVERSION, null, null, new FunctionParameter[] {  //$NON-NLS-1$
					new FunctionParameter(teiidVersion, "date", DataTypeManagerService.DefaultDataTypes.DATE, Messages.getString(Messages.SystemSource.Formatdate_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "format", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Formatdate_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Formatdate_result_description)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
		functions.add(
				createSyntheticMethod(IFunctionLibrary.FunctionName.FORMATTIME.text(), Messages.getString(Messages.SystemSource.Formattime_description),CONVERSION, null, null, new FunctionParameter[] {  //$NON-NLS-1$
					new FunctionParameter(teiidVersion, "time", DataTypeManagerService.DefaultDataTypes.TIME, Messages.getString(Messages.SystemSource.Formattime_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "format", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Formattime_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Formattime_result_description)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
	}
					
	private void addParseTimestampFunction() {
		functions.add(
			new FunctionMethod(SourceSystemFunctions.PARSETIMESTAMP, Messages.getString(Messages.SystemSource.Parsetimestamp_description),CONVERSION, FUNCTION_CLASS, "parseTimestamp", //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter[] { 
					new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Parsetimestamp_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "format", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Parsetimestamp_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.Parsetimestamp_result_description)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
		functions.add(
				createSyntheticMethod(IFunctionLibrary.FunctionName.PARSETIME.text(), Messages.getString(Messages.SystemSource.Parsetime_description),CONVERSION, null, null, new FunctionParameter[] {  //$NON-NLS-1$
					new FunctionParameter(teiidVersion, "time", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Parsetime_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "format", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Parsetime_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.TIME, Messages.getString(Messages.SystemSource.Parsetime_result_description)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
		functions.add(
				createSyntheticMethod(IFunctionLibrary.FunctionName.PARSEDATE.text(), Messages.getString(Messages.SystemSource.Parsedate_description),CONVERSION, null, null, new FunctionParameter[] {  //$NON-NLS-1$
					new FunctionParameter(teiidVersion, "date", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Parsedate_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "format", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Parsedate_arg2)) }, //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.DATE, Messages.getString(Messages.SystemSource.Parsedate_result_description)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addFormatNumberFunction(String functionName, String description, String methodName, String inputParam, DefaultDataTypes dataType, String resultDesc) {
	    addFormatNumberFunction(functionName, description, methodName, inputParam, dataType.getId(), resultDesc);
	}

	private void addFormatNumberFunction(String functionName, String description, String methodName, String inputParam, String dataType,  String resultDesc) {
		functions.add(
			new FunctionMethod(functionName, description, CONVERSION, FUNCTION_CLASS, methodName,
				new FunctionParameter[] { 
					new FunctionParameter(teiidVersion, inputParam, dataType, Messages.getString(Messages.SystemSource.Formatnumber_arg1)), //$NON-NLS-1$
					new FunctionParameter(teiidVersion, "format", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Formatnumber_arg2))}, //$NON-NLS-1$ //$NON-NLS-2$
				new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, resultDesc) ) );       //$NON-NLS-1$
	}

	private void addParseNumberFunction(String functionName, String description, String methodName, String inputParam, DefaultDataTypes dataType,  String resultDesc) {
	    addParseNumberFunction(functionName, description, methodName, inputParam, dataType.getId(), resultDesc);
	}

	private void addParseNumberFunction(String functionName, String description, String methodName, String inputParam, String dataType,  String resultDesc) {
			functions.add(
				new FunctionMethod(functionName, description, CONVERSION, FUNCTION_CLASS, methodName,
					new FunctionParameter[] { 
						new FunctionParameter(teiidVersion, inputParam, DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Parsenumber_arg1)), //$NON-NLS-1$
						new FunctionParameter(teiidVersion, "format", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.Parsenumber_arg2))}, //$NON-NLS-1$ //$NON-NLS-2$
					new FunctionParameter(teiidVersion, "result", dataType, resultDesc) ) );       //$NON-NLS-1$
	}
    
    private void addBitFunction(String functionName, String description, String methodName, int parameters, String resultDescription) {
        FunctionParameter[] paramArray = null;
        if (parameters == 1) {
            paramArray = new FunctionParameter[] {
                new FunctionParameter(teiidVersion, "integer", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Bitfunc_arg1)) //$NON-NLS-1$ //$NON-NLS-2$
            };
        } else if (parameters == 2) {
            paramArray = new FunctionParameter[] {
                new FunctionParameter(teiidVersion, "integer1", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Bitfunc2_arg1)), //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "integer2", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.Bitfunc2_arg2)) //$NON-NLS-1$ //$NON-NLS-2$
            };
        }
        functions.add(
            new FunctionMethod(functionName, description, NUMERIC, FUNCTION_CLASS, methodName,
                paramArray,
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.INTEGER, resultDescription) //$NON-NLS-1$
            )
        );
    }
    
    private void addXpathValueFunction() {
        functions.add(new FunctionMethod(SourceSystemFunctions.XPATHVALUE, Messages.getString(Messages.SystemSource.xpathvalue_description), XML, XML_FUNCTION_CLASS, "xpathValue", //$NON-NLS-1$ //$NON-NLS-2$ 
                            new FunctionParameter[] { 
                                new FunctionParameter(teiidVersion, "document", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpath_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                                new FunctionParameter(teiidVersion, "xpath", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpath_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                            new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpathvalue_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$

        functions.add(new FunctionMethod(SourceSystemFunctions.XPATHVALUE, Messages.getString(Messages.SystemSource.xpathvalue_description), XML, XML_FUNCTION_CLASS, "xpathValue", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] { 
                    new FunctionParameter(teiidVersion, "document", DataTypeManagerService.DefaultDataTypes.CLOB, Messages.getString(Messages.SystemSource.xpath_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "xpath", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpath_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpathvalue_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$

        functions.add(new FunctionMethod(SourceSystemFunctions.XPATHVALUE, Messages.getString(Messages.SystemSource.xpathvalue_description), XML, XML_FUNCTION_CLASS, "xpathValue", //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter[] { 
                    new FunctionParameter(teiidVersion, "document", DataTypeManagerService.DefaultDataTypes.BLOB, Messages.getString(Messages.SystemSource.xpath_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "xpath", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpath_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpathvalue_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
        
        functions.add(new FunctionMethod(SourceSystemFunctions.XPATHVALUE, Messages.getString(Messages.SystemSource.xpathvalue_description), XML, XML_FUNCTION_CLASS, "xpathValue", //$NON-NLS-1$ //$NON-NLS-2$ 
                                         new FunctionParameter[] { 
                                             new FunctionParameter(teiidVersion, "document", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.xpath_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                                             new FunctionParameter(teiidVersion, "xpath", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpath_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                                         new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xpathvalue_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addXslTransformFunction() {
    	for (DefaultDataTypes type1 : Arrays.asList(DataTypeManagerService.DefaultDataTypes.STRING, DataTypeManagerService.DefaultDataTypes.XML, DataTypeManagerService.DefaultDataTypes.CLOB)) {
    		for (DefaultDataTypes type2 : Arrays.asList(DataTypeManagerService.DefaultDataTypes.STRING, DataTypeManagerService.DefaultDataTypes.XML, DataTypeManagerService.DefaultDataTypes.CLOB)) {
    	        functions.add(new FunctionMethod(SourceSystemFunctions.XSLTRANSFORM, Messages.getString(Messages.SystemSource.xsltransform_description), XML, XML_FUNCTION_CLASS, "xslTransform", //$NON-NLS-1$ //$NON-NLS-2$  
                        new FunctionParameter[] { 
                            new FunctionParameter(teiidVersion, "document", type1, Messages.getString(Messages.SystemSource.xsltransform_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                            new FunctionParameter(teiidVersion, "xsl", type2, Messages.getString(Messages.SystemSource.xsltransform_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                        new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.CLOB, Messages.getString(Messages.SystemSource.xsltransform_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
    		}
    	}
    }
    
    private void addXmlComment() {
        functions.add(new FunctionMethod(SourceSystemFunctions.XMLCOMMENT, Messages.getString(Messages.SystemSource.xmlcomment_description), XML, XML_FUNCTION_CLASS, "xmlComment", //$NON-NLS-1$ //$NON-NLS-2$  
                            new FunctionParameter[] { 
                                new FunctionParameter(teiidVersion, "value", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xmlcomment_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                            new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.xmlcomment_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
    }

    private void addXmlPi() {
        functions.add(new FunctionMethod(SourceSystemFunctions.XMLPI, Messages.getString(Messages.SystemSource.xmlpi_description), XML, XML_FUNCTION_CLASS, "xmlPi", //$NON-NLS-1$ //$NON-NLS-2$  
                            new FunctionParameter[] { 
					            new FunctionParameter(teiidVersion, "name", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xmlpi_param1))}, //$NON-NLS-1$ //$NON-NLS-2$
                            new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.xmlpi_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
        
        functions.add(new FunctionMethod(SourceSystemFunctions.XMLPI, Messages.getString(Messages.SystemSource.xmlpi_description), XML, XML_FUNCTION_CLASS, "xmlPi", //$NON-NLS-1$ //$NON-NLS-2$  
                new FunctionParameter[] { 
		            new FunctionParameter(teiidVersion, "name", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xmlpi_param1)), //$NON-NLS-1$ //$NON-NLS-2$
		            new FunctionParameter(teiidVersion, "value", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.xmlpi_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.xmlpi_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addJsonToXml() {
        functions.add(new FunctionMethod(SourceSystemFunctions.JSONTOXML, Messages.getString(Messages.SystemSource.jsonToXml_description), XML, XML_FUNCTION_CLASS, "jsonToXml", //$NON-NLS-1$ //$NON-NLS-2$  
                new FunctionParameter[] { 
        	new FunctionParameter(teiidVersion, "rootElementName", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.jsonToXml_param1)), //$NON-NLS-1$ //$NON-NLS-2$
        	new FunctionParameter(teiidVersion, "json", DataTypeManagerService.DefaultDataTypes.CLOB, Messages.getString(Messages.SystemSource.jsonToXml_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.jsonToXml_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
        functions.add(new FunctionMethod(SourceSystemFunctions.JSONTOXML, Messages.getString(Messages.SystemSource.jsonToXml_description), XML, XML_FUNCTION_CLASS, "jsonToXml", //$NON-NLS-1$ //$NON-NLS-2$  
                new FunctionParameter[] { 
        	new FunctionParameter(teiidVersion, "rootElementName", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.jsonToXml_param1)), //$NON-NLS-1$ //$NON-NLS-2$
        	new FunctionParameter(teiidVersion, "json", DataTypeManagerService.DefaultDataTypes.BLOB, Messages.getString(Messages.SystemSource.jsonToXml_param2))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.jsonToXml_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addXmlConcat() {
        functions.add(new FunctionMethod(SourceSystemFunctions.XMLCONCAT, Messages.getString(Messages.SystemSource.xmlconcat_description), XML, PushDown.CAN_PUSHDOWN, XML_FUNCTION_CLASS, "xmlConcat", //$NON-NLS-1$ //$NON-NLS-2$  
                            Arrays.asList( 
                                new FunctionParameter(teiidVersion, "param1", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.xmlconcat_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                                new FunctionParameter(teiidVersion, "param2", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.xmlconcat_param2), true)), //$NON-NLS-1$ //$NON-NLS-2$ 
                            new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.XML, Messages.getString(Messages.SystemSource.xmlconcat_result)), false, Determinism.DETERMINISTIC ) );       //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addTimeZoneFunctions() {
        functions.add(new FunctionMethod(SourceSystemFunctions.MODIFYTIMEZONE, Messages.getString(Messages.SystemSource.modifyTimeZone_description), DATETIME, FUNCTION_CLASS, "modifyTimeZone", //$NON-NLS-1$ //$NON-NLS-2$ 
                            new FunctionParameter[] { 
                                new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.modifyTimeZone_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                                new FunctionParameter(teiidVersion, "startTimeZone", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.modifyTimeZone_param2)), //$NON-NLS-1$ //$NON-NLS-2$
                                new FunctionParameter(teiidVersion, "endTimeZone", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.modifyTimeZone_param3))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                            new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.modifyTimeZone_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$

        functions.add(new FunctionMethod(SourceSystemFunctions.MODIFYTIMEZONE, Messages.getString(Messages.SystemSource.modifyTimeZone_description), DATETIME, FUNCTION_CLASS, "modifyTimeZone", //$NON-NLS-1$ //$NON-NLS-2$ 
                                         new FunctionParameter[] { 
                                             new FunctionParameter(teiidVersion, "timestamp", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.modifyTimeZone_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                                             new FunctionParameter(teiidVersion, "endTimeZone", DataTypeManagerService.DefaultDataTypes.STRING, Messages.getString(Messages.SystemSource.modifyTimeZone_param3))}, //$NON-NLS-1$ //$NON-NLS-2$ 
                                         new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.modifyTimeZone_result)) ) );       //$NON-NLS-1$ //$NON-NLS-2$

    }
    
    private void addUnixTimeFunctions() {
    	functions.add(new FunctionMethod(IFunctionLibrary.FunctionName.FROM_UNIXTIME.text(), Messages.getString(Messages.SystemSource.from_unixtime_description), DATETIME, PushDown.SYNTHETIC, null, null, //$NON-NLS-1$ 
    			Arrays.asList(
    				new FunctionParameter(teiidVersion, "unix_timestamp", DataTypeManagerService.DefaultDataTypes.INTEGER, Messages.getString(Messages.SystemSource.from_unixtime_param1)) //$NON-NLS-1$ //$NON-NLS-2$
    			),
                new FunctionParameter(teiidVersion, "result", DataTypeManagerService.DefaultDataTypes.TIMESTAMP, Messages.getString(Messages.SystemSource.from_unixtime_result)), true, Determinism.DETERMINISTIC )); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addTypedNullIfFunction(String type) {
        functions.add(
            new FunctionMethod(IFunctionLibrary.FunctionName.NULLIF.text(), Messages.getString(Messages.SystemSource.nullif_description), MISCELLANEOUS, PushDown.SYNTHETIC, null, null, //$NON-NLS-1$ 
                Arrays.asList( 
                    new FunctionParameter(teiidVersion, "op1", type, Messages.getString(Messages.SystemSource.nullif_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "op2", type, Messages.getString(Messages.SystemSource.nullif_param1)) ), //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", type, Messages.getString(Messages.SystemSource.nullif_result)), false, Determinism.DETERMINISTIC)); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    private void addTypedCoalesceFunction(String type) {
        functions.add(
            new FunctionMethod(IFunctionLibrary.FunctionName.COALESCE.text(), Messages.getString(Messages.SystemSource.coalesce_description), MISCELLANEOUS, PushDown.CAN_PUSHDOWN, FUNCTION_CLASS, "coalesce", //$NON-NLS-1$ //$NON-NLS-2$
                Arrays.asList( 
                    new FunctionParameter(teiidVersion, "op1", type, Messages.getString(Messages.SystemSource.coalesce_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "op2", type, Messages.getString(Messages.SystemSource.coalesce_param1)), //$NON-NLS-1$ //$NON-NLS-2$
                    new FunctionParameter(teiidVersion, "op3", type, Messages.getString(Messages.SystemSource.coalesce_param1), true) ), //$NON-NLS-1$ //$NON-NLS-2$
                new FunctionParameter(teiidVersion, "result", type, Messages.getString(Messages.SystemSource.coalesce_result)), false, Determinism.DETERMINISTIC)); //$NON-NLS-1$ //$NON-NLS-2$
    }
		
    /**
     * Get all function signatures for this metadata source.
     * @return Unordered collection of {@link FunctionMethod}s
     */
    public Collection<org.teiid.metadata.FunctionMethod> getFunctionMethods() {
        return this.functions;
	}
    
    public static FunctionMethod createSyntheticMethod(String name, String description, String category, 
            String invocationClass, String invocationMethod, FunctionParameter[] inputParams, 
            FunctionParameter outputParam) {
    	return new FunctionMethod(name, description, category, PushDown.SYNTHETIC, invocationClass, invocationMethod, inputParams!=null?Arrays.asList(inputParams):null, outputParam, false,Determinism.NONDETERMINISTIC);
    }
}
