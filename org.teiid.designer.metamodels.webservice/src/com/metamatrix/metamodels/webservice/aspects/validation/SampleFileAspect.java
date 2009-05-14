/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.aspects.validation;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;


/** 
 * @since 4.2
 */
public class SampleFileAspect extends WebServiceComponentAspect {

    /** 
     * @param entity
     * @since 4.2
     */
    public SampleFileAspect(MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for relationship entity.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(FILE_URL_RULE);
		return super.getValidationRules();		
	}    
}
