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
 * ColumnAspect
 */
public class ColumnAspect extends RelationalEntityAspect {

    /**
     * Construct an instance of ColumnAspect.
     * @param entity
     */
    public ColumnAspect(MetamodelEntity entity){
        super(entity);
    }

	/**
	 * Get all the validation rules for a column.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(MISSING_COLUMN_LENGTH_RULE);
        addRule(MISSING_COLUMN_PRECISION_RULE);
		addRule(COLUMN_DATATYPE_RULE);  
		addRule(COLUMN_INTEGER_DATATYPE_RULE);        
        addRule(EMPTY_COLUMN_NATIVE_TYPE_RULE);
		return super.getValidationRules();		
	}
}
