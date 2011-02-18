/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;


/** 
 * @since 4.3
 */
public class XEnumLiteralAspect extends CoreEntityAspect {

    /**
     * Construct an instance of ModelImportAspect.
     * @param entity
     */
    public XEnumLiteralAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * Get all the validation rules for ModelImport.
     */
    @Override
    public ValidationRuleSet getValidationRules() {
        
        addRule(XENUM_LITERAL_NAME_RULE);
        addRule(XENUM_LITERAL_VALUE_RULE);        
        return super.getValidationRules();      
    }
}
