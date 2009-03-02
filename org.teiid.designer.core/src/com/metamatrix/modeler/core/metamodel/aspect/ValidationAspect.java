/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationRuleSet;

/**
 * ValidationAspect
 */
public interface ValidationAspect extends MetamodelAspect {

    ValidationRuleSet getValidationRules();

    void updateContext(final EObject eObject, final ValidationContext context);

    boolean shouldValidate(final EObject eObject, final ValidationContext context);
}
