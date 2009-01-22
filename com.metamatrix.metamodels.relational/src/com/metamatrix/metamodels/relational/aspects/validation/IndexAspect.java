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
 * IndexAspect
 */
public class IndexAspect extends RelationalEntityAspect {
    
    /**
     * Construct an instance of IndexAspect.
     * @param entity
     */
    public IndexAspect(MetamodelEntity entity) {
        super(entity);
    }
    
    /**
     * Get all the validation rules for a column.
     */
    @Override
    public ValidationRuleSet getValidationRules() {
        addRule(INDEX_REF_MULTIPLE_TABLES);
        return super.getValidationRules();      
    }
}
