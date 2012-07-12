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
