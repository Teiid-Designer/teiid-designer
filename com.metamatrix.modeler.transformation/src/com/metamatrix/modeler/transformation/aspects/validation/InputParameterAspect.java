/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRule;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;
import com.metamatrix.modeler.core.validation.rules.StringLengthRule;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;

/**
 * InputParameterAspect
 */
public class InputParameterAspect extends TransformationAspect {
	
	public static final ValidationRule NAME_RULE = new StringNameRule(TransformationPackage.INPUT_PARAMETER__NAME);
	public static final ValidationRule LENGTH_RULE = new StringLengthRule(TransformationPackage.INPUT_PARAMETER__NAME);

	public InputParameterAspect(final MetamodelEntity entity) {
		super(entity);
	}

	/**
	 * Get validation rules for InputParameterAspect
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NAME_RULE);
		addRule(LENGTH_RULE);
		addRule(BINDING_RULE);
		return super.getValidationRules();
	}
	
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect#shouldValidate(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean shouldValidate(final EObject eObject, final ValidationContext context) {
        ArgCheck.isInstanceOf(InputParameter.class, eObject);
        if(!context.shouldIgnore(eObject)) {
            InputParameter inputParameter = (InputParameter) eObject;
	        EObject inputSet = inputParameter.getInputSet();
	        if(inputSet != null) {
	            ValidationAspect validAspect = AspectManager.getValidationAspect(inputSet);
	            if(validAspect != null) {
	                boolean shouldValidate = validAspect.shouldValidate(inputSet, context);
	                if(!shouldValidate) {
	    	            context.addObjectToIgnore(eObject, true);
	                }
	                return shouldValidate;
	            }
	        }
	        return true;
        }
        return false;
    }	
}
