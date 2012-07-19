/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.validation.ValidationRuleSet;


/**
 * FunctionParameterAspect
 *
 * @since 8.0
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
