/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * StructuralFeatureValidationRule
 */
public interface StructuralFeatureValidationRule extends ValidationRule {
    
    //############################################################################################################################
    //# Methods                                                                                                                  #
    //############################################################################################################################
    
    /**
     * Execute the structural feature validation rule
     * @param eStructuralFeature the {@link org.eclipse.emf.ecore.EStructuralFeature}
     * @param value the EObject value for the feature found by <code>EObject.eGet(EStructuralFeature)</code>
     * @param context the context to be used
     */
    void validate(EStructuralFeature eStructuralFeature, EObject eObject, Object value, ValidationContext context);

}
