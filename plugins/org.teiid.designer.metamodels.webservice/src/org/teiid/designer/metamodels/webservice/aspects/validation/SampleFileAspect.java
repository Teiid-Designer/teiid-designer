/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.validation.ValidationRuleSet;



/** 
 * @since 8.0
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
