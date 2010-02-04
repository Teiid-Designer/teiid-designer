/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.function.FunctionPlugin;
import com.metamatrix.metamodels.function.PushDownType;
import com.metamatrix.metamodels.function.ReturnParameter;
import com.metamatrix.metamodels.function.ScalarFunction;
import com.metamatrix.metamodels.function.aspects.validation.FunctionEntityAspect;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * ScalarFunctionRule
 */
public class ScalarFunctionRule implements ObjectValidationRule {

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(ScalarFunction.class, eObject);

        // create a validationResult to add problems to        
        ValidationResult result = new ValidationResultImpl(eObject);

        ScalarFunction sFunction = (ScalarFunction) eObject;

        // Validate the name ...
        // (Not done with the StringNameRule, since it looks at siblings ...
        validateName(sFunction,result);

        // validate the return parameter
        validateReturnParameter(sFunction.getReturnParameter(), result);

        // validate invocation class 
        String invocationClass = sFunction.getInvocationClass();
        if(StringUtil.isEmpty(invocationClass) && !sFunction.getPushDown().equals(PushDownType.REQUIRED_LITERAL)) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,FunctionPlugin.Util.getString("ScalarFunctionRule.Invocation_class_must_be_specified_on_a_scalar_function._1")); //$NON-NLS-1$
            result.addProblem(problem);
        } else {
            validateJavaIdentifier(invocationClass, FunctionPlugin.Util.getString("ScalarFunctionRule.Invocation_class_3"), true, result); //$NON-NLS-1$
        }

        //  validate invocation method
        String invocationMethod = sFunction.getInvocationMethod();
        if(StringUtil.isEmpty(invocationMethod) && !sFunction.getPushDown().equals(PushDownType.REQUIRED_LITERAL)) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,FunctionPlugin.Util.getString("ScalarFunctionRule.Invocation_method_must_be_specified_on_a_scalar_function._2")); //$NON-NLS-1$
            result.addProblem(problem);
        } else {
            validateJavaIdentifier(invocationMethod, FunctionPlugin.Util.getString("ScalarFunctionRule.Invocation_Method_4"), false, result); //$NON-NLS-1$
        }

        // validate function category
        String category = sFunction.getCategory();
        if (StringUtil.isEmpty(category)) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR , FunctionPlugin.Util.getString("ScalarFunctionRule.Category_can_not_be_null_or_empty")); //$NON-NLS-1$
            result.addProblem(problem);
        } else {
            validateLength(category, FunctionPlugin.Util.getString("ScalarFunctionRule.Category_5"), result); //$NON-NLS-1$
        }

		// validate function description
		try {
			Annotation annotation = ModelerCore.getModelEditor().getAnnotation(sFunction, false);
			if(annotation != null) {
				validateLength(annotation.getDescription(), FunctionPlugin.Util.getString("ScalarFunctionRule.Description_1"), result); //$NON-NLS-1$
			}
		} catch(ModelerCoreException e) {
			ModelerCore.Util.log(IStatus.ERROR,e,FunctionPlugin.Util.getString("ScalarFunctionRule.Error_trying_to_lookup_model_annotation_for_the_function_{0}._2", sFunction.getName())); //$NON-NLS-1$
		}

		// add the result to the context
		context.addResult(result);        
    }

    /**
     * @param sFunction
     * @param result
     */
    private void validateName(ScalarFunction sFunction, ValidationResult result) {
        // Get the name ...
        final String name = sFunction.getName();
        final char[] invalidChars = null;
        CoreValidationRulesUtil.validateStringNameChars(result, name, invalidChars);
    }

    private final void validateReturnParameter(ReturnParameter rParam, ValidationResult result) {
        // if null already validated by multiplycity rule
        if(rParam != null) {
            String paramType = rParam.getType();
            if(StringUtil.isEmpty(paramType)) {
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,FunctionPlugin.Util.getString("ScalarFunctionRule.Type_should_be_specified_on_the_return_parameter_of_a_scalar_function._7")); //$NON-NLS-1$
                result.addProblem(problem);                
            }
            validateJavaIdentifier(paramType, FunctionPlugin.Util.getString("ScalarFunctionRule.Return_Parameter_8"), true, result);              //$NON-NLS-1$
        }
    }

    /**
     * Check that specified string is valid Java identifier.  If not, create problems on the validation result.
     * @param identifier String to check
     * @param strName String to use in exception message
     * @param allowMultiple True if multiple identifiers are allowed, as in a class name
     */
    private final void validateJavaIdentifier(String identifier, String strName, boolean allowMultiple, ValidationResult result) {
        // First check first character
        if(!StringUtil.isEmpty(identifier)) {
            char firstChar = identifier.charAt(0);
            if(! Character.isJavaIdentifierStart(firstChar)) {
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR , strName+FunctionPlugin.Util.getString("ScalarFunctionRule._has_invalid_first_character___10")+firstChar); //$NON-NLS-1$
                result.addProblem(problem);
            }

            // Then check the rest of the characters
            for(int i=1; i<identifier.length(); i++) {
                char ch = identifier.charAt(i);
                if(! Character.isJavaIdentifierPart(ch)) {
                    if(! allowMultiple || ! (ch == '.')) {
                        ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR , strName+FunctionPlugin.Util.getString("ScalarFunctionRule._has_invalid_character___11")+firstChar); //$NON-NLS-1$
                        result.addProblem(problem);
                    }
                }
            }

            if(identifier.charAt(identifier.length()-1) == '.') {
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,strName+FunctionPlugin.Util.getString("ScalarFunctionRule._cannot_end_with_a___.___13")); //$NON-NLS-1$
                result.addProblem(problem);
            }
        }
    }

    /**
     * Check that specified string is no longer than maxLength.  If string is longer, problem gets added
     * to the validation result.
     * @param string String to check for length
     * @param strName Name of string to use in exception message
     */
    private final void validateLength(String string, String strName, ValidationResult result) {
        if(string!= null && string.length() > FunctionEntityAspect.MAX_ENTITY_NAME_LENGTH) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR , strName+FunctionPlugin.Util.getString("ScalarFunctionRule._exceeds_maximum_length_of___9")+FunctionEntityAspect.MAX_ENTITY_NAME_LENGTH); //$NON-NLS-1$
            result.addProblem(problem);
        }
    }

}
