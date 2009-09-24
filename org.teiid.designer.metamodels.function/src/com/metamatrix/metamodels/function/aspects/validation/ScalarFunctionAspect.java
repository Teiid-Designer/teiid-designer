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
 * ScalarFunctionAspect
 */
public class ScalarFunctionAspect extends FunctionEntityAspect {

    /**
     * Construct an instance of ScalarFunctionAspect.
     * @param entity
     */
    public ScalarFunctionAspect(MetamodelEntity entity){
        super(entity);
    }

	/**
	 * Get all the validation rules for ScalarFunction.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(SCALAR_FUNCTION_RULE);
        addRule(SCALAR_FUNCTION_UNIQUENESS_RULE);
		return super.getValidationRules();
	}
}
