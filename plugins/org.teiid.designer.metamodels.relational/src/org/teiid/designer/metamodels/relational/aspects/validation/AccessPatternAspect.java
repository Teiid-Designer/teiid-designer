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
 * AccessPatternAspect
 *
 * @since 8.0
 */
public class AccessPatternAspect extends RelationalEntityAspect {
    
    /**
     * Construct an instance of AccessPatternAspect.
     * @param entity
     */
    public AccessPatternAspect(MetamodelEntity entity){
        super(entity);
    }
    
	/**
	 * Get all the validation rules for AccessPattern.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(ACCESS_PTTN_COLUMNS_RULE);
		return super.getValidationRules();		
	}
}
