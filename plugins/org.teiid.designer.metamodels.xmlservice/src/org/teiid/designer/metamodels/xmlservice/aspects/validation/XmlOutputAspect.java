/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xmlservice.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.validation.ValidationRuleSet;



/** 
 * XmlOutputAspect
 */
public class XmlOutputAspect extends XmlServiceComponentAspect {

    /** 
     * @param entity
     * @since 4.2
     */
    protected XmlOutputAspect(final MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for relationship entity.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
        addRule(GLOBAL_REF_RULE);
		return super.getValidationRules();		
	}
}
