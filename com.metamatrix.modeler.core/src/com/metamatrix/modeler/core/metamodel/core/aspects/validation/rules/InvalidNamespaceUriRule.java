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
