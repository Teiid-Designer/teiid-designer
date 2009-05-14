/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation;

import org.eclipse.emf.ecore.EObject;

/**
 * @since 4.0
 */
public interface ObjectValidationRule extends ValidationRule {
    
    /**
     * Execute the object validation rule
     * @param eObject the target of the validation rule
     * @param context the context to be used
     */
    void validate(EObject eObject, ValidationContext context);
    
}
