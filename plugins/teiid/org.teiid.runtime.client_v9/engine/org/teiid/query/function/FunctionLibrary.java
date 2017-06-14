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

package org.teiid.query.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.teiid.core.CoreConstants;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.Transform;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol.Type;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.udf.IFunctionLibrary;
import org.teiid.metadata.AggregateAttributes;
import org.teiid.metadata.FunctionMethod;
import org.teiid.metadata.FunctionParameter;
import org.teiid.query.function.metadata.FunctionCategoryConstants;
import org.teiid.query.resolver.util.ResolverUtil;
import org.teiid.query.sql.symbol.Constant;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.Function;
import org.teiid.translator.SourceSystemFunctions;



/**
 * The function library is the primary way for the system to find out what
 * functions are available, resolve function signatures, and invoke system
 * and user-defined functions.
 */
public class FunctionLibrary implements IFunctionLibrary<FunctionForm, FunctionDescriptor> {
	
	public static final String MVSTATUS = "mvstatus"; //$NON-NLS-1$

	public static final Set<String> INTERNAL_SCHEMAS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

    static {
        INTERNAL_SCHEMAS.add(CoreConstants.SYSTEM_MODEL);
        INTERNAL_SCHEMAS.add(CoreConstants.SYSTEM_ADMIN_MODEL);
        INTERNAL_SCHEMAS.add(CoreConstants.ODBC_MODEL);
    }

    // Function tree for system functions (never reloaded)
    private FunctionTree systemFunctions;

    // Function tree for user-defined functions
    private FunctionTree[] userFunctions;

    private final ITeiidServerVersion teiidVersion;

    private DataTypeManagerService dataTypeManager;

	/**
	 * Construct the function library.  This should be called only once by the
	 * FunctionLibraryManager.
	 * @param teiidVersion
	 * @param systemFuncs 
	 * @param userFuncs 
	 */
	public FunctionLibrary(ITeiidServerVersion teiidVersion, FunctionTree systemFuncs, FunctionTree... userFuncs) {
        this.teiidVersion = teiidVersion;
        this.systemFunctions = systemFuncs;
       	this.userFunctions = userFuncs;
	}

	/**
     * @return the teiidVersion
     */
    public ITeiidServerVersion getTeiidVersion() {
        return this.teiidVersion;
    }

    public DataTypeManagerService getDataTypeManager() {
        if (dataTypeManager == null)
            dataTypeManager = DataTypeManagerService.getInstance(getTeiidVersion());

        return dataTypeManager;
    }

    public FunctionTree[] getUserFunctions() {
        return userFunctions;
    }

    /**
     * Get all function categories, sorted in alphabetical order
     * @return List of function category names, sorted in alphabetical order
     */
    public List<String> getFunctionCategories() {
        // Remove category duplicates
        TreeSet<String> categories = new TreeSet<String>();
        categories.addAll( systemFunctions.getCategories() );
        if (this.userFunctions != null) {
	        for (FunctionTree tree: this.userFunctions) {
	        	categories.addAll(tree.getCategories());
	        }
        }

        ArrayList<String> categoryList = new ArrayList<String>(categories);
        return categoryList;
    }

    /**
     * Get all functions in a category.
     * @param category Category name
     * @return List of {@link FunctionMethod}s in a category
     */
    public List<FunctionMethod> getFunctionsInCategory(String category) {
        List<FunctionMethod> forms = new ArrayList<FunctionMethod>();
        forms.addAll(systemFunctions.getFunctionsInCategory(category));
        if (this.userFunctions != null) {
            for (FunctionTree tree: this.userFunctions) {
                forms.addAll(tree.getFunctionsInCategory(category));
            }
        }
        return forms;
    }


    @Override
    public List<FunctionForm> getFunctionForms(String category) {
        Set<FunctionMethod> fMethods = systemFunctions.getFunctionsInCategory(category);
        
        if( fMethods.isEmpty() ) { 
        	fMethods = new HashSet<FunctionMethod>();
        } 
        
        for (FunctionTree tree: this.userFunctions) {
            fMethods.addAll(tree.getFunctionsInCategory(category));
        }

        List<FunctionForm> forms = new ArrayList<FunctionForm>();

        if (fMethods != null) {
            for (FunctionMethod fMethod : fMethods) {
                forms.add(new FunctionForm(fMethod));
            }
        }

        return forms;
    }

    @Override
    public FunctionForm findFunctionForm(String name, int numArgs) {
        List<FunctionMethod> functionMethods = systemFunctions.findFunctionMethods(name, numArgs);
        if (functionMethods.size() > 0) {
            return new FunctionForm(functionMethods.get(0));
        }
        if(functionMethods.isEmpty() && this.userFunctions != null) {
            for (FunctionTree tree: this.userFunctions) {
                functionMethods = tree.findFunctionMethods(name, numArgs);
                if (functionMethods.size() > 0) {
                    return new FunctionForm(functionMethods.get(0));
                }
            }
        }

        return null;
    }

    @Override
    public boolean hasFunctionMethod(String name, int numArgs) {
        List<FunctionMethod> methods = systemFunctions.findFunctionMethods(name, numArgs);
        if (!methods.isEmpty()) {
            return true;
        }
        if(this.userFunctions != null) {
            for (FunctionTree tree: this.userFunctions) {
                methods = tree.findFunctionMethods(name, numArgs);
                if (!methods.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public FunctionDescriptor findFunction(FunctionName name, Class[] types) {
        return findFunction(name.text(), types);
    }
    
	/**
	 * Find a function descriptor given a name and the types of the arguments.
	 * This method matches based on case-insensitive function name and
     * an exact match of the number and types of parameter arguments.
     * @param name Name of the function to resolve
     * @param types Array of classes representing the types
     * @return Descriptor if found, null if not found
	 */
    @Override
	public FunctionDescriptor findFunction(String name, Class[] types) {
        // First look in system functions
        FunctionDescriptor descriptor = systemFunctions.getFunction(name, types);

        // If that fails, check the user defined functions
        if(descriptor == null && this.userFunctions != null) {
        	for (FunctionTree tree: this.userFunctions) {
        		descriptor = tree.getFunction(name, types);
        		if (descriptor != null) {
        			break;
        		}
        	}
        }

        return descriptor;
	}

    /**
     * Find a function descriptor given a name and the types of the arguments.
     * This method matches based on case-insensitive function name and
     * an exact match of the number and types of parameter arguments.
     * @param name Name of the function to resolve
     * @param types Array of classes representing the types
     * @return Descriptor if found, null if not found
     */
    public List<FunctionDescriptor> findAllFunctions(String name, Class<?>[] types) {
        // First look in system functions
        FunctionDescriptor descriptor = systemFunctions.getFunction(name, types);

        // If that fails, check the user defined functions
        if(descriptor == null && this.userFunctions != null) {
            List<FunctionDescriptor> result = new LinkedList<FunctionDescriptor>();
            for (FunctionTree tree: this.userFunctions) {
                descriptor = tree.getFunction(name, types);
                if (descriptor != null) {
                    //pushdown function takes presedence 
                    //TODO: there may be multiple translators contributing functions with the same name / types
                    //need "conformed" logic so that the right pushdown can occur
                    if (CoreConstants.SYSTEM_MODEL.equals(descriptor.getSchema()))
                        return Arrays.asList(descriptor);

                    result.add(descriptor);
                }
            }
            return result;
        }
        if (descriptor != null) {
            return Arrays.asList(descriptor);
        }
        return Collections.emptyList();
    }

    public static class ConversionResult {
        public ConversionResult(FunctionMethod method) {
            this.method = method;
        }
        public FunctionMethod method;
        public boolean needsConverion;
    }

	/**
	 * Get the conversions that are needed to call the named function with arguments
	 * of the given type.  In the case of an exact match, the list will contain all nulls.
	 * In other cases the list will contain one or more non-null values where the value
	 * is a conversion function that can be used to convert to the proper types for
	 * executing the function.
     * @param name Name of function
	 * @param returnType
	 * @param args 
	 * @param types Existing types passed to the function
     * @param hasUnknownType
     * @return Null if no conversion could be found, otherwise an array of conversions
     * to apply to each argument.  The list should match 1-to-1 with the parameters.
     * Parameters that do not need a conversion are null; parameters that do are
     * FunctionDescriptors.
	 * @throws Exception
	 */
	public ConversionResult determineNecessaryConversions(String name, Class<?> returnType, Expression[] args, Class<?>[] types, boolean hasUnknownType) throws Exception {
        //First find existing functions with same name and same number of parameters
        final Collection<FunctionMethod> functionMethods = new LinkedList<FunctionMethod>();
        functionMethods.addAll( this.systemFunctions.findFunctionMethods(name, types.length) );
        if (this.userFunctions != null) {
	        for (FunctionTree tree: this.userFunctions) {
	        	functionMethods.addAll( tree.findFunctionMethods(name, types.length) );
	        }
        }
        
        //Score each match, reject any where types can not be converted implicitly       
        //Score of current method (lower score means better match with less converts
        //Current best score (lower score is best.  Higher score results in more implicit conversions
        int bestScore = Integer.MAX_VALUE;
        boolean ambiguous = false;
        FunctionMethod result = null;
        boolean isSystem = false;
        boolean narrowing = false;
                
        outer: for (FunctionMethod nextMethod : functionMethods) {
            int currentScore = 0;
            boolean nextNarrowing = false;
            final List<FunctionParameter> methodTypes = nextMethod.getInputParameters();
            //Holder for current signature with converts where required
            
            //Iterate over the parameters adding conversions where required or failing when
            //no implicit conversion is possible
            for(int i = 0; i < types.length; i++) {
                final String tmpTypeName = methodTypes.get(Math.min(i, methodTypes.size() - 1)).getType();
                Class<?> targetType = getDataTypeManager().getDataTypeClass(tmpTypeName);

                Class<?> sourceType = types[i];
                if (sourceType == null) {
                    currentScore++;
                    continue;
                }
                if (sourceType.isArray() && targetType.isArray()
                    && sourceType.getComponentType().equals(targetType.getComponentType())) {
                    currentScore++;
                    continue;
                }
                if (sourceType.isArray()) {
                    if (isVarArgArrayParam(nextMethod, types, i, targetType)) {
                		//vararg array parameter
                		continue;
                	}
                    //treat the array as object type until proper type handling is added
                	sourceType = DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass();
                }
				try {
					Transform t = getConvertFunctionDescriptor(sourceType, targetType);
					if (t != null) {
		                if (t.isExplicit()) {
		                	if (!(args[i] instanceof Constant) || ResolverUtil.convertConstant(getDataTypeManager().getDataTypeName(sourceType), tmpTypeName, (Constant)args[i]) == null) {
		                		continue outer;
		                	}
		                	nextNarrowing = true;
		                	currentScore++;
		                } else {
		                	currentScore++;
		                }
					}
				} catch (Exception e) {
					continue outer;
				}
            }
            
            //If the method is valid match and it is the current best score, capture those values as current best match
            if (currentScore > bestScore) {
                continue;
            }
            
            if (hasUnknownType) {
            	if (returnType != null) {
            		try {
						Transform t = getConvertFunctionDescriptor(getDataTypeManager().getDataTypeClass(nextMethod.getOutputParameter().getType()), returnType);
						if (t != null) {
							if (t.isExplicit()) {
								//there still may be a common type, but use any other valid conversion over this one
								currentScore += types.length + 1;
								nextNarrowing = true;
							} else {
								currentScore++;
							}
						}
					} catch (Exception e) {
						//there still may be a common type, but use any other valid conversion over this one
						currentScore += (types.length * types.length);
					}
            	}
            }

            if (nextNarrowing && result != null && !narrowing) {
                continue;
            }

            boolean useNext = false;

            if (!nextNarrowing && narrowing) {
                useNext = true;
            }

            boolean isSystemNext = nextMethod.getParent() == null || INTERNAL_SCHEMAS.contains(nextMethod.getParent().getName());
            if ((isSystem && isSystemNext) || (!isSystem && !isSystemNext && result != null)) {
                int partCount = partCount(result.getName());
                int nextPartCount = partCount(nextMethod.getName());
                if (partCount < nextPartCount) {
                    //the current is more specific
                    //this makes us more consistent with the table resolving logic
                    continue outer;
                }
                if (nextPartCount < partCount) {
                    useNext = true;
                }
            } else if (isSystemNext) {
                useNext = true;
            }

            if (currentScore == bestScore && !useNext) {
                ambiguous = true;
                boolean useCurrent = false;
                List<FunctionParameter> bestParams = result.getInputParameters();
                for (int j = 0; j < types.length; j++) {
                    String t1 = bestParams.get(Math.min(j, bestParams.size() - 1)).getType();
                    String t2 = methodTypes.get((Math.min(j, methodTypes.size() - 1))).getType();
                    
                    if (types[j] == null || t1.equals(t2)) {
                        continue;
                    }
                    
                    String commonType = ResolverUtil.getCommonType(teiidVersion, new String[] {t1, t2});
                    
                    if (commonType == null) {
                        continue outer; //still ambiguous
                    }
                    
                    if (commonType.equals(t1)) {
                        if (!useCurrent) {
                            useNext = true;
                        }
                    } else if (commonType.equals(t2)) {
                        if (!useNext) {
                            useCurrent = true;
                        }
                    } else {
                        continue outer;
                    }
                }
                if (useCurrent) {
                    ambiguous = false; //prefer narrower
                } else {
                    String sysName = result.getProperty(FunctionMethod.SYSTEM_NAME, false);
                    String sysNameOther = nextMethod.getProperty(FunctionMethod.SYSTEM_NAME, false);
                    if (sysName != null && sysName.equalsIgnoreCase(sysNameOther)) {
                        ambiguous = false;
                    }
                }
            }

            if (currentScore < bestScore || useNext) {
                ambiguous = false;
                if (currentScore == 0 && isSystemNext) {
                    //this must be an exact match
                    return new ConversionResult(nextMethod);
                }

                bestScore = currentScore;
                result = nextMethod;
                isSystem = isSystemNext;
                narrowing = nextNarrowing;
            }            
        }
        
        if (ambiguous) {
             throw new Exception();
        }

        ConversionResult cr = new ConversionResult(result);
        if (result != null) {
            cr.needsConverion = (bestScore != 0);
        }
        return cr;
	}

	private int partCount(String name) {
        int result = 0;
        int index = 0;
        while (true) {
            index = name.indexOf('.', index+1);
            if (index > 0) {
                result++;
            } else {
                break;
            }
        }
        return result;
    }

	public FunctionDescriptor[] getConverts(FunctionMethod method, Class<?>[] types) {
        final List<FunctionParameter> methodTypes = method.getInputParameters();
        FunctionDescriptor[] result = new FunctionDescriptor[types.length];
        for(int i = 0; i < types.length; i++) {
        	//treat all varags as the same type
            final String tmpTypeName = methodTypes.get(Math.min(i, methodTypes.size() - 1)).getType();
            Class<?> targetType = getDataTypeManager().getDataTypeClass(tmpTypeName);

            Class<?> sourceType = types[i];
            if (sourceType == null) {
                result[i] = findTypedConversionFunction(DataTypeManagerService.DefaultDataTypes.NULL.getTypeClass(), targetType);
            } else if (sourceType != targetType){
            	if (isVarArgArrayParam(method, types, i, targetType)) {
            		//vararg array parameter
            		continue;
            	}
            	result[i] = findTypedConversionFunction(sourceType, targetType);
            }
        }
        return result;
	}

	public boolean isVarArgArrayParam(FunctionMethod method, Class<?>[] types,
			int i, Class<?> targetType) {
		return i == types.length - 1 && method.isVarArgs() && i == method.getInputParameterCount() - 1 
				&& types[i].isArray() && targetType.isAssignableFrom(types[i].getComponentType());
	}
	
	private Transform getConvertFunctionDescriptor(Class<?> sourceType, Class<?> targetType) throws Exception {
        //If exact match no conversion necessary
        if(sourceType.equals(targetType)) {
            return null;
        }
        Transform result = getDataTypeManager().getTransform(sourceType, targetType);
        //Else see if an implicit conversion is possible.
        if(result == null) {
             throw new Exception();
        }

        return result;
	}

    /**
     * Find conversion function and set return type to proper type.   
     * @param sourceType The source type class
     * @param targetType The target type class
     * @return A CONVERT function descriptor or null if not possible
     */
    public FunctionDescriptor findTypedConversionFunction(Class<?> sourceType, Class<?> targetType) {
    	//TODO: should array to string be prohibited?    	
        FunctionDescriptor fd = findFunction(FunctionName.CONVERT, new Class[] {sourceType, DataTypeManagerService.DefaultDataTypes.STRING.getTypeClass()});
        if (fd != null) {
            return copyFunctionChangeReturnType(fd, targetType);
        }
        return null;
    }

	/**
	 * Return a copy of the given FunctionDescriptor with the sepcified return type.
	 * @param fd FunctionDescriptor to be copied.
	 * @param returnType The return type to apply to the copied FunctionDescriptor.
	 * @return The copy of FunctionDescriptor.
	 */
    public FunctionDescriptor copyFunctionChangeReturnType(FunctionDescriptor fd, Class<?> returnType) {
        if(fd != null) {
        	FunctionDescriptor fdImpl = fd;
            FunctionDescriptor copy = fdImpl.clone();
            copy.setReturnType(returnType);
            return copy;
        }
        return fd;
    }
    
    public static boolean isConvert(Function function) {
        Expression[] args = function.getArgs();
        String funcName = function.getName();
        
        return args.length == 2 && (FunctionName.CONVERT.equalsIgnoreCase(funcName) || FunctionName.CAST.equalsIgnoreCase(funcName));
    }
    
    @Override
    public String getFunctionName(FunctionName functionName) {
        if (functionName == null)
            throw new IllegalArgumentException();
        
        return functionName.text();
    }

    /**
     * Return a list of the most general forms of built-in aggregate functions.
     * <br/>count(*) - is not included
     * <br/>textagg - is not included due to its non standard syntax
     * 
     * @param includeAnalytic - true to include analytic functions that must be windowed
     * @return
     */
    public List<FunctionMethod> getBuiltInAggregateFunctions(boolean includeAnalytic) {
        ArrayList<FunctionMethod> result = new ArrayList<FunctionMethod>();
    	if (this.systemFunctions != null) {
	    	FunctionDescriptor stExtent = this.systemFunctions.getFunction(SourceSystemFunctions.ST_EXTENT, 
	    			new Class[] {DataTypeManagerService.DefaultDataTypes.GEOMETRY.getTypeClass()});
	    	result.add(stExtent.getMethod());
    	}
        for (Type type : IAggregateSymbol.Type.values()) {
            AggregateAttributes aa = new AggregateAttributes();
            String returnType = null;
            String[] argTypes = null;
            aa.setAllowsDistinct(true);
            switch (type) {
            case TEXTAGG:
            case USER_DEFINED:
                continue;
            case DENSE_RANK:
            case RANK:
            case ROW_NUMBER:
                if (!includeAnalytic) {
                    continue;
                }
                aa.setAllowsDistinct(false);
                aa.setAnalytic(true);
                returnType = DataTypeManagerService.DefaultDataTypes.INTEGER.getId();
                argTypes = new String[] {};
                break;
            case ANY:
            case SOME:
            case EVERY:
                returnType = DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId();
                argTypes = new String[] {DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId()};
                break;
            case COUNT:
                returnType = DataTypeManagerService.DefaultDataTypes.INTEGER.getId();
                argTypes = new String[] {DataTypeManagerService.DefaultDataTypes.OBJECT.getId()};
                break;
            case MAX:
            case MIN:
            case AVG:
            case SUM:
                returnType = DataTypeManagerService.DefaultDataTypes.OBJECT.getId();
                argTypes = new String[] {DataTypeManagerService.DefaultDataTypes.OBJECT.getId()};
                break;
            case STDDEV_POP:
            case STDDEV_SAMP:
            case VAR_POP:
            case VAR_SAMP:
                returnType = DataTypeManagerService.DefaultDataTypes.DOUBLE.getId();
                argTypes = new String[] {DataTypeManagerService.DefaultDataTypes.DOUBLE.getId()};
                break;
            case STRING_AGG:
                returnType = DataTypeManagerService.DefaultDataTypes.OBJECT.getId();
                argTypes = new String[] {DataTypeManagerService.DefaultDataTypes.OBJECT.getId()};
                aa.setAllowsOrderBy(true);
                break;
            case ARRAY_AGG:
                returnType = DataTypeManagerService.DefaultDataTypes.OBJECT.getId();
                argTypes = new String[] {getDataTypeManager().getDataTypeName(DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeArrayClass())};
                aa.setAllowsOrderBy(true);
                aa.setAllowsDistinct(false);
                break;
            case JSONARRAY_AGG:
                returnType = DataTypeManagerService.DefaultDataTypes.CLOB.getId();
                argTypes = new String[] {DataTypeManagerService.DefaultDataTypes.OBJECT.getId()};
                aa.setAllowsOrderBy(true);
                aa.setAllowsDistinct(false);
                break;
            case XMLAGG:
                returnType = DataTypeManagerService.DefaultDataTypes.XML.getId();
                argTypes = new String[] {DataTypeManagerService.DefaultDataTypes.XML.getId()};
                aa.setAllowsOrderBy(true);
                aa.setAllowsDistinct(false);
                break;
            }
            FunctionMethod fm = FunctionMethod.createFunctionMethod(teiidVersion, type.name(), type.name(), FunctionCategoryConstants.AGGREGATE, returnType, argTypes);
            fm.setAggregateAttributes(aa);
            result.add(fm);
        }
        return result;
    }
}
