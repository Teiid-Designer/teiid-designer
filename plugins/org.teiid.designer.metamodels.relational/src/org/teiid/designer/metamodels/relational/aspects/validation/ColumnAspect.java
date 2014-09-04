/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.validation.ValidationRuleSet;


/**
 * ColumnAspect
 *
 * @since 8.0
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
        addRule(EMPTY_COLUMN_NATIVE_TYPE_RULE);
        addRule(CHAR_DATATYPE_LENGTH_RULE);
		return super.getValidationRules();		
	}
}
