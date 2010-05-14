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
public class XPackageAspect extends CoreEntityAspect {

    /**
     * Construct an instance of ModelImportAspect.
     * @param entity
     */
    public XPackageAspect(MetamodelEntity entity) {
        super(entity);
    }

	/**
	 * Get all the validation rules for ModelImport.
	 */
	@Override
    public ValidationRuleSet getValidationRules() {
        addRule(XPACKAGE_NAME_RULE);
        addRule(XCLASS_UNIQUE_EXTENDED_CLASS_IN_XPACKAGE_RULE);
        addRule(XCLASS_UNIQUE_NAME_IN_XPACKAGE_RULE);
		return super.getValidationRules();		
	}
}
