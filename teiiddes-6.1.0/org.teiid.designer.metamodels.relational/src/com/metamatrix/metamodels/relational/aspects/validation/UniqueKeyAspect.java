/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.validation;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * UniqueKeyAspect
 */
public abstract class UniqueKeyAspect extends RelationalEntityAspect {
    
    /**
     * Construct an instance of UniqueKeyAspect.
     * @param entity
     */
    public UniqueKeyAspect(MetamodelEntity entity){
        super(entity);
    }
    
	/**
	 * Get all the validation rules for unique key.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(UNIQUE_KEY_COLUMNS_RULE);
		return super.getValidationRules();	
	}
}
