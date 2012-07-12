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
 * @since 4.2
 */
public class XmlOperationAspect extends XmlServiceComponentAspect {

    /** 
     * @param entity
     * @since 4.2
     */
    protected XmlOperationAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /**
     * Get all the validation rules for relationship entity.
     */
    @Override
    public ValidationRuleSet getValidationRules() {
    	addRule(NAME_RULE);
        addRule(MISSING_REF_RULE);
        return super.getValidationRules();      
    }

}
