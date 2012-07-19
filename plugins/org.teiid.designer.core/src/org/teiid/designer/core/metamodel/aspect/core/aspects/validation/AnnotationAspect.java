/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation;

import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.validation.ValidationRuleSet;


/**
 * @since 8.0
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
     * @see org.teiid.designer.core.metamodel.aspect.core.aspects.validation.AbstractValidationAspect#getValidationRules()
     * @since 4.2
     */
    @Override
    public ValidationRuleSet getValidationRules() {
        addRule(ANNOTATION_EXTENSION_ATTRIBUTE_DEFAULT_VALUE_RULE);
        addRule(REST_PROPERTIES_RULE);
        return super.getValidationRules();
    }
}
