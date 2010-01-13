/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.query.function.FunctionForm;
import com.metamatrix.query.function.FunctionLibrary;
import com.metamatrix.query.function.FunctionLibraryManager;
import com.metamatrix.query.internal.ui.builder.util.BuilderUtils;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.Function;

/**
 * FunctionEditorModel
 */
public class FunctionEditorModel extends AbstractLanguageObjectEditorModel {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final String PREFIX = I18nUtil.getPropertyPrefix(FunctionEditorModel.class);

    private static final String NONE = Util.getString(PREFIX + "none"); //$NON-NLS-1$

    public static final String CATEGORY = "CATEGORY"; //$NON-NLS-1$
    public static final String SELECTED_FUNCTION = "SELECTED_FUNCTION"; //$NON-NLS-1$

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private List argNames; // the current function's argument names
    private List argValues; // the current function's argument values

    private String[] categories;

    private String category;
    private String[] functions; // collection of function names valid for current category
    private FunctionForm[] functionForms;

    private static String defaultCategory;

    /** The function library. All information about functions is obtained from here. */
    private FunctionLibrary funcLib;

    private FunctionForm selectedFunctionForm;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public FunctionEditorModel() {
        super(Function.class);
        getCategories();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#clear()
     */
    @Override
    public void clear() {
        argNames = null;
        argValues = null;
        selectedFunctionForm = null;
        super.clear();
    }

    private FunctionForm findFunctionForm( String theFunctionName ) {
        FunctionForm result = null;

        if (functionForms != null) {
            for (int i = 0; i < functionForms.length; i++) {
                if (functionForms[i].getDisplayString().equals(theFunctionName)) {
                    result = functionForms[i];
                    break;
                }
            }
        }

        return result;
    }

    public String[] getCategories() {
        // not sure if users can dynamically add categories.
        // make this construct categories each time this method is called
        categories = null;
        funcLib = FunctionLibraryManager.getFunctionLibrary();
        List list = funcLib.getFunctionCategories();

        if ((list != null) && !list.isEmpty()) {
            Object[] temp = list.toArray();

            if ((temp != null) && (temp.length > 0)) {
                categories = new String[temp.length];

                for (int i = 0; i < categories.length; i++) {
                    categories[i] = temp[i].toString();
                }
            }
        }

        // no categories found. shouldn't happen.
        if (categories == null) {
            categories = new String[1];
            categories[0] = NONE;
        }

        defaultCategory = categories[0]; // set default to first category

        return categories;
    }

    public String getCategory() {
        return category;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    /**
     * Gets the current value.
     * 
     * @return the current <code>Function</code>
     * @throws IllegalStateException if the current value is not complete
     */
    public Function getFunction() {
        return (Function)getLanguageObject();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#getLanguageObject()
     */
    @Override
    public LanguageObject getLanguageObject() {
        // return null if not complete
        if (!isComplete()) {
            return null;
        }

        int numArgs = argValues.size();
        Expression[] args = new Expression[numArgs];

        for (int i = 0; i < numArgs; i++) {
            args[i] = (Expression)argValues.get(i);
        }

        return new Function(selectedFunctionForm.getName(), args);
    }

    /**
     * Gets the appropriate value for the function argument trying to use the given value.
     * 
     * @param theFunctionName the function name
     * @param theArgName the argument name
     * @param theProposedValue the proposed value
     */
    private Object getFunctionArgValue( String theFunctionName,
                                        String theArgName,
                                        Object theProposedValue ) {
        Object result = theProposedValue;

        if (BuilderUtils.isConversionTypeArg(theFunctionName, theArgName)) {
            if ((theProposedValue instanceof Constant) && BuilderUtils.isConversionTypeConstant(theProposedValue)) {
                result = theProposedValue;
            } else {
                result = BuilderUtils.createConversionTypeConstant();
            }
        }

        return result;
    }

    public String[] getFunctions() {
        return functions;
    }

    public List getFunctionArgNames() {
        return argNames;
    }

    public String getFunctionDescription() {
        return (selectedFunctionForm == null) ? null : selectedFunctionForm.getDescription();
    }

    public String getFunctionName() {
        return (selectedFunctionForm == null) ? null : selectedFunctionForm.getDisplayString();
    }

    public List getFunctionArgValues() {
        return argValues;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#isComplete()
     */
    @Override
    public boolean isComplete() {
        return (selectedFunctionForm != null);
    }

    public boolean isValid() {
        boolean result = isComplete();

        if (result) {
            // make sure argNames have values
            for (int size = argValues.size(), i = 0; i < size; i++) {
                if (argValues.get(i) == null) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    public void setCategory( String theCategory ) {
        // only set category if not null and valid
        if ((theCategory != null) && Arrays.asList(categories).contains(theCategory)) {
            boolean changeCategory = true;

            if ((category != null) && category.equals(theCategory)) {
                changeCategory = false;
            }

            if (changeCategory) {
                category = theCategory;

                // get corresponding functions for category
                functions = null;
                functionForms = null;
                selectedFunctionForm = null;
                List forms = funcLib.getFunctionForms(category);

                if ((forms != null) && !forms.isEmpty()) {
                    int size = forms.size();
                    functionForms = new FunctionForm[size];
                    functions = new String[size];

                    for (int i = 0; i < size; i++) {
                        functionForms[i] = (FunctionForm)forms.get(i);
                        functions[i] = functionForms[i].getDisplayString();
                    }
                } else {
                    functionForms = new FunctionForm[1];
                    functions = new String[1];
                    functions[0] = NONE;
                }
                fireModelChanged(CATEGORY);
            }
        }
    }

    private void setFunction( Function theFunction ) {
        notifyListeners = false;

        if (theFunction == null) {
            clear();
        } else {
            Expression[] newArgValues = theFunction.getArgs();
            FunctionForm functionForm = funcLib.findFunctionForm(theFunction.getName(), newArgValues.length);

            setCategory(functionForm.getCategory());
            setFunctionName(functionForm.getDisplayString());

            // set current arg values
            argValues = Arrays.asList(newArgValues);
        }

        notifyListeners = true;
        fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
    }

    /**
     * Sets the function argument at the given index.
     * 
     * @param theValue
     * @param theIndex
     * @throws IllegalArgumentException if the argument value array is null
     * @throws ArrayIndexOutOfBoundsException if the index is invalid
     */
    public void setFunctionArgValue( Expression theValue,
                                     int theIndex ) {
        ArgCheck.isNotNull(argValues);
        argValues.set(theIndex, theValue);
    }

    public void setFunctionName( String theName ) {
        FunctionForm functionForm = findFunctionForm(theName);
        ArgCheck.isNotNull(functionForm);

        if ((selectedFunctionForm == null) || !selectedFunctionForm.equals(functionForm)) {
            selectedFunctionForm = functionForm;

            // set new function arguments
            List prevArgValues = argValues;
            int prevNumArgs = (prevArgValues == null) ? 0 : prevArgValues.size();
            argNames = selectedFunctionForm.getArgNames();
            argValues = new ArrayList(argNames.size());

            // reuse prior arg values if possible
            String functionName = selectedFunctionForm.getName();

            for (int numArgs = argNames.size(), i = 0; i < numArgs; i++) {
                // set value
                String argName = (String)argNames.get(i);
                Object value = null;

                if (i < prevNumArgs) {
                    Object arg = prevArgValues.get(i);

                    if (!BuilderUtils.isConversionTypeConstant(arg) && !BuilderUtils.isConversionTypeArg(functionName, argName)) {
                        // keep value when both old/new argNames are not conversion type constants
                        value = getFunctionArgValue(functionName, argName, arg);
                    } else if (BuilderUtils.isConversionTypeConstant(arg)
                               && BuilderUtils.isConversionTypeArg(functionName, argName)) {
                        // keep value when both old/new argNames are conversion type constants
                        value = getFunctionArgValue(functionName, argName, arg);
                    }
                }

                // if value is not set yet get undefined value or conversion type constant
                if (value == null) {
                    value = getFunctionArgValue(functionName, argName, null);
                }

                argValues.add(value);
            }
            fireModelChanged(SELECTED_FUNCTION);
        }
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLangObj ) {
        super.setLanguageObject(theLangObj);
        setFunction((Function)theLangObj);
    }

}
