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
 * SchemaAspect
 */
public class ModelImportAspect extends CoreEntityAspect {

    /**
     * Construct an instance of ModelImportAspect.
     * @param entity
     */
    public ModelImportAspect(MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for ModelImport.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
		
        addRule(UNRESOLVED_MODEL_IMPORT_RULE);
        addRule(INVALID_MODEL_IMPORT_RULE); //MyDefect : Added for 17511
		return super.getValidationRules();		
	}
}
