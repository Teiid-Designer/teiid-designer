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
 * @since 4.2
 */
public class AnnotationAspect extends CoreEntityAspect {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    public AnnotationAspect( MetamodelEntity theEntity ) {
        super(theEntity);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see com.metamatrix.modeler.core.metamodel.core.aspects.validation.AbstractValidationAspect#getValidationRules()
     * @since 4.2
     */
    @Override
    public ValidationRuleSet getValidationRules() {
        addRule(ANNOTATION_EXTENSION_ATTRIBUTE_DEFAULT_VALUE_RULE);
        addRule(REST_PROPERTIES_RULE);
        return super.getValidationRules();
    }
}
