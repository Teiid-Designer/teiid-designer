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
 * ForeignKeyAspect
 */
public class ForeignKeyAspect extends RelationalEntityAspect {
    
    /**
     * Construct an instance of ForeignKeyAspect.
     * @param entity
     */
    public ForeignKeyAspect(MetamodelEntity entity){
        super(entity);
    }
    
	/**
	 * Get all the validation rules for ForeignKey.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(FOREIGN_KEY_COLUMNS_RULE);
		return super.getValidationRules();		
	}
}
