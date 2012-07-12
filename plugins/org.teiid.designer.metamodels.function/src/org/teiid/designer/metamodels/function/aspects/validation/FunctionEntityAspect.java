/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.core.aspects.validation.AbstractValidationAspect;
import org.teiid.designer.core.validation.ValidationRule;
import org.teiid.designer.core.validation.ValidationRuleSet;
import org.teiid.designer.core.validation.rules.StringLengthRule;
import org.teiid.designer.metamodels.function.FunctionPackage;
import org.teiid.designer.metamodels.function.aspects.validation.rules.FunctionEntityNameRule;
import org.teiid.designer.metamodels.function.aspects.validation.rules.FunctionParameterRule;
import org.teiid.designer.metamodels.function.aspects.validation.rules.FunctionParameterUniquenessRule;
import org.teiid.designer.metamodels.function.aspects.validation.rules.ScalarFunctionRule;
import org.teiid.designer.metamodels.function.aspects.validation.rules.ScalarFunctionUniquenessRule;


/**
 * FunctionEntityAspect
 */
public abstract class FunctionEntityAspect extends AbstractValidationAspect {

    public static final int MAX_ENTITY_NAME_LENGTH = 128;

	public static final ValidationRule NAME_RULE = new FunctionEntityNameRule();
	public static final ValidationRule LENGTH_RULE = new StringLengthRule(FunctionEntityAspect.MAX_ENTITY_NAME_LENGTH, FunctionPackage.FUNCTION__NAME);
	public static final ValidationRule FUNCTION_PARAM_RULE = new FunctionParameterRule();
    public static final ValidationRule SCALAR_FUNCTION_RULE = new ScalarFunctionRule();
    public static final ValidationRule FUNCTION_PARAM_UNIQUENESS_RULE = new FunctionParameterUniquenessRule();
    public static final ValidationRule SCALAR_FUNCTION_UNIQUENESS_RULE = new ScalarFunctionUniquenessRule();

    protected FunctionEntityAspect(MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for Function entity.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NAME_RULE);
		addRule(LENGTH_RULE);
		return super.ruleSet;
	}
	
}
