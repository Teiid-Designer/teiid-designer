/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
