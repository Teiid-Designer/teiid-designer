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
 * ForeignKeyAspect
 *
 * @since 8.0
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
