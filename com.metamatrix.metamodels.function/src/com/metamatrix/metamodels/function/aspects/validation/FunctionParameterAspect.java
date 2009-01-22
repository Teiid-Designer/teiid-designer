/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.aspects.validation;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * FunctionParameterAspect
 */
public class FunctionParameterAspect extends FunctionEntityAspect {

    /**
     * Construct an instance of FunctionParameterAspect.
     * @param entity
     */
    public FunctionParameterAspect(MetamodelEntity entity){
        super(entity);
    }

	/**
	 * Get all the validation rules for FunctionParameter.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(FUNCTION_PARAM_RULE);
        addRule(FUNCTION_PARAM_UNIQUENESS_RULE);
		return super.getValidationRules();
	}
}
