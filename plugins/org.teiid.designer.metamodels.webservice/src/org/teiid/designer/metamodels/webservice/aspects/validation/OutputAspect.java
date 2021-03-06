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
 * OutputAspect
 *
 * @since 8.0
 */
public class OutputAspect extends WebServiceComponentAspect {

    /** 
     * @param entity
     * @since 4.2
     */
    protected OutputAspect(final MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for relationship entity.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
	    addRule(MESSAGE_CONTENT_RULE);
		addRule(OUTPUT_DOC_RULE);
        addRule(GLOBAL_REF_RULE);
        addRule(TARGET_NAMESPACE_RULE);
		return super.getValidationRules();		
	}
}
