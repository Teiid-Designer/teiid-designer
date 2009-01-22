/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.util.UriValidator;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/**
 * Rule to make sure all Namespace URIs are valid. 
 * @since 4.3
 */
public final class InvalidNamespaceUriRule implements ObjectValidationRule {

    /** 
     * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.3
     */
    public void validate(EObject theObject,
                         ValidationContext theContext) {
        
        ArgCheck.isInstanceOf(ModelAnnotation.class, theObject);
        
        String uri = ((ModelAnnotation)theObject).getNamespaceUri();
        
        try {
            IStatus status = UriValidator.validate(uri);
            
            if (!status.isOK()) {
                ValidationResult result = new ValidationResultImpl(theObject, theObject);
                ValidationProblem problem = new ValidationProblemImpl(status.getCode(), status.getSeverity(), status.getMessage());
                result.addProblem(problem);
                theContext.addResult(result);
            }
        } catch (RuntimeException theException) {
            ValidationResult result = new ValidationResultImpl(theObject, theObject);
            ValidationProblem problem = new ValidationProblemImpl(IStatus.OK, IStatus.ERROR, theException.getLocalizedMessage());
            result.addProblem(problem);
            theContext.addResult(result);
        }
    }

}
