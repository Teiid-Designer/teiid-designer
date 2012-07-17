/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel.aspect.core.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.util.UriValidator;



/**
 * Rule to make sure all Namespace URIs are valid. 
 * @since 4.3
 */
public final class InvalidNamespaceUriRule implements ObjectValidationRule {

    /** 
     * @see org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.3
     */
    @Override
	public void validate(EObject theObject,
                         ValidationContext theContext) {
        
        CoreArgCheck.isInstanceOf(ModelAnnotation.class, theObject);
        
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
