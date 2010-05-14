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
 * ModelAnnotationAspect
 */
public class ModelAnnotationAspect extends CoreEntityAspect {

    /**
     * Construct an instance of ModelAnnotationAspect.
     * @param entity
     */
    public ModelAnnotationAspect(MetamodelEntity entity) {
        super(entity);
    }
    
    /**
     * Get all the validation rules for ModelAnnotation.
     */
	@Override
    public ValidationRuleSet getValidationRules() {
		addRule(NULL_PRIMARY_METAMODEL_RULE);
		addRule(NULL_MODEL_TYPE_RULE);
		addRule(MISSING_MODEL_IMPORT_RULE);
		addRule(AMBIGUOUS_MODEL_IMPORTS_RULE);
		addRule(MODEL_ANNOTATION_UUID_RULE);
        addRule(DEPRECATED_METAMODEL_URI_RULE);
        addRule(INVALID_NAMESPACE_URI_RULE);

		return super.getValidationRules();		
	}
}
