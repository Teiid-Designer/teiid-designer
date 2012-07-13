/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.validation.rules;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.designer.core.validation.StructuralFeatureValidationRule;
import org.teiid.designer.core.validation.ValidationContext;

/**
 * OutputDocumentValidationRule
 */
public class OutputDocumentValidationRule implements StructuralFeatureValidationRule {

    /**
     * Construct an instance of OutputDocumentValidationRule.
     * 
     * @param featureID ID of the feature to validate
     */
    public OutputDocumentValidationRule( final int featureID ) {
    }

    /**
     * @see org.teiid.designer.core.validation.StructuralFeatureValidationRule#validate(org.eclipse.emf.ecore.EStructuralFeature,
     *      org.eclipse.emf.ecore.EObject, java.lang.Object, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate( final EStructuralFeature eStructuralFeature,
                          final EObject eObject,
                          final Object value,
                          final ValidationContext context ) {
    }

}
