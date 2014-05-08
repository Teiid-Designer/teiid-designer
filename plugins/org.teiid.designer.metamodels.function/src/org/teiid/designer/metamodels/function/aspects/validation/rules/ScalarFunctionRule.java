/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.aspects.validation.rules;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.VdbHelper;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.core.validation.rules.CoreValidationRulesUtil;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.function.FunctionPlugin;
import org.teiid.designer.metamodels.function.PushDownType;
import org.teiid.designer.metamodels.function.ReturnParameter;
import org.teiid.designer.metamodels.function.ScalarFunction;
import org.teiid.designer.metamodels.function.aspects.validation.FunctionEntityAspect;
import org.teiid.designer.metamodels.function.extension.FunctionModelExtensionConstants;


/**
 * ScalarFunctionRule
 *
 * @since 8.0
 */
public class ScalarFunctionRule implements ObjectValidationRule {

    /*
     * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     */
    @Override
	public void validate(EObject eObject, ValidationContext context) {
        CoreArgCheck.isInstanceOf(ScalarFunction.class, eObject);

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
        if(CoreStringUtil.isEmpty(invocationClass) && !sFunction.getPushDown().equals(PushDownType.REQUIRED_LITERAL)) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,FunctionPlugin.Util.getString("ScalarFunctionRule.Invocation_class_must_be_specified_on_a_scalar_function._1")); //$NON-NLS-1$
            result.addProblem(problem);
        } else {
            validateJavaIdentifier(invocationClass, FunctionPlugin.Util.getString("ScalarFunctionRule.Invocation_class_3"), true, result); //$NON-NLS-1$
        }

        //  validate invocation method
        String invocationMethod = sFunction.getInvocationMethod();
        if(CoreStringUtil.isEmpty(invocationMethod) && !sFunction.getPushDown().equals(PushDownType.REQUIRED_LITERAL)) {
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,FunctionPlugin.Util.getString("ScalarFunctionRule.Invocation_method_must_be_specified_on_a_scalar_function._2")); //$NON-NLS-1$
            result.addProblem(problem);
        } else {
            validateJavaIdentifier(invocationMethod, FunctionPlugin.Util.getString("ScalarFunctionRule.Invocation_Method_4"), false, result); //$NON-NLS-1$
        }
        
        // validate jarPath property
        validateUdfJarPath(sFunction,result);

        // validate function category
        String category = sFunction.getCategory();
        if (CoreStringUtil.isEmpty(category)) {
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
        final char[] validChars = null;
        CoreValidationRulesUtil.validateStringNameChars(result, name, validChars);
    }

    private final void validateReturnParameter(ReturnParameter rParam, ValidationResult result) {
        // if null already validated by multiplycity rule
        if(rParam != null) {
            String paramType = rParam.getType();
            if(CoreStringUtil.isEmpty(paramType)) {
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
        if(!CoreStringUtil.isEmpty(identifier)) {
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
    
    /**
     * Validate the udfJarPath property.
     * - the path must be set
     * - the specified jar must be located in the workspace project
     * @param scalarFunc the Scalar Function to validate
     * @param result the ValidationResult
     */
    private final void validateUdfJarPath(ScalarFunction scalarFunc, ValidationResult result) {
        String udfJarPath = getUdfJarPath(scalarFunc);

        if(udfJarPath!=null) {
            if (CoreStringUtil.isEmpty(udfJarPath.trim())) {
                String message = FunctionPlugin.Util.getString("ScalarFunctionRule.udfJarPathNotSet"); //$NON-NLS-1$
                ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,message); 
                result.addProblem(problem);
            } else {
                final ModelResource resrc = ModelerCore.getModelWorkspace().findModelResource(scalarFunc);
                IProject project = resrc.getModelProject().getProject();
                IContainer libFolder = VdbHelper.getFolder(project, VdbFolders.UDF.getReadFolder());
                boolean found = VdbHelper.isFileInFolder(libFolder, udfJarPath);
                if(!found) {
                    String message = FunctionPlugin.Util.getString("ScalarFunctionRule.udfJarNotFound",udfJarPath); //$NON-NLS-1$
                    ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR ,message); 
                    result.addProblem(problem);
                }
            }
        }
    }
    
    /**
     * Get the Udf jarPath property from the supplied ScalarFunction
     * @param scalarFunc the supplied ScalarFunction
     * @return the Udf jarPath property value
     */
    private static String getUdfJarPath(final ScalarFunction scalarFunc) {
        String udfJarPath = null;
        ModelObjectExtensionAssistant assistant = (ModelObjectExtensionAssistant)ExtensionPlugin.getInstance().getRegistry().getModelExtensionAssistant(FunctionModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix());
        if(assistant!=null) {
            try {
                udfJarPath = assistant.getPropertyValue(scalarFunc, FunctionModelExtensionConstants.PropertyIds.UDF_JAR_PATH);
            } catch (Exception ex) {
                ModelerCore.Util.log(IStatus.ERROR,ex,FunctionPlugin.Util.getString("FunctionUtil.ErrorGettingJarPath", scalarFunc.getName())); //$NON-NLS-1$
            }
        }
        return udfJarPath;
    }
    
}
