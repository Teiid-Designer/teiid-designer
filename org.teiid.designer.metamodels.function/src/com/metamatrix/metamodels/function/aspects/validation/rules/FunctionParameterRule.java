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

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.function.FunctionParameter;
import com.metamatrix.metamodels.function.FunctionPlugin;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * FunctionParameterRule
 */
public class FunctionParameterRule implements ObjectValidationRule {

    /*
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     */
    public void validate(EObject eObject, ValidationContext context) {
        ArgCheck.isInstanceOf(FunctionParameter.class, eObject);

        // create a validationResult to add problems to        
        ValidationResult result = new ValidationResultImpl(eObject);

        FunctionParameter param = (FunctionParameter) eObject;
        
        String paramType = param.getType();
        
        if (paramType == null || paramType.length() == 0) {
            final String msg = FunctionPlugin.Util.getString("FunctionParameterRule.Parameter_type_may_not_be_null_or_empty_1"); //$NON-NLS-1$
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR , msg);
            result.addProblem(problem);            

        } else {
            try {
                DatatypeManager manager = context.getDatatypeManager();
                EObject builtInType = manager.getBuiltInDatatype(paramType);
                if(builtInType == null) {
                    ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,FunctionPlugin.Util.getString("FunctionParameterRule.Function_parameter_type_is_not_a_builtinType___1")+paramType); //$NON-NLS-1$
                    result.addProblem(problem);                
                }
            } catch(ModelerCoreException e) {
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,FunctionPlugin.Util.getString("FunctionParameterRule.Error_trying_to_get_builtinType_for_function_parameter_type___2")+paramType); //$NON-NLS-1$
                result.addProblem(problem);            
            }
        }
        // add the result to the context
        context.addResult(result);
    }
}
