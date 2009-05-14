/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.validation.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.util.XSDResourceImpl;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.validation.ResourceValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;


/** 
 * @since 4.2
 */
public class EmfResourceValidationRule implements ResourceValidationRule {

    /** 
     * @see com.metamatrix.modeler.core.validation.ResourceValidationRule#validate(org.eclipse.emf.ecore.resource.Resource, com.metamatrix.modeler.core.validation.ValidationContext)
     * @since 4.2
     */
    public void validate(final Resource resource, final ValidationContext context) {
	    ArgCheck.isNotNull(resource);
	    ArgCheck.isNotNull(context);
	    // xsd validator generateds markers for XSDDiagnostics
	    if(resource instanceof XSDResourceImpl) {
	        return;
	    }

	    // get to the modelAnnotation object to create the markers on
	    ModelContents contents = new ModelContents(resource);
	    ModelAnnotation annotation = contents.getModelAnnotation();
	    if(annotation != null) {
			final ValidationResult result = new ValidationResultImpl(annotation);					
		    Collection errors = resource.getErrors();
		    // hash set to filter duplicates
		    Collection messages = new HashSet();
		    if(!errors.isEmpty()) {
		        for(final Iterator errIter = errors.iterator(); errIter.hasNext();) {
		            Diagnostic errDiagnostic = (Diagnostic) errIter.next();
		            String diagMessage = errDiagnostic.getMessage();
		            if(!messages.contains(diagMessage)) {
			    		// create validation problem and addit to the result
			    		final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, errDiagnostic.getMessage());
			    		result.addProblem(problem);
			    		messages.add(diagMessage);
		            }
		        }
		    } else {
			    Collection warnings = resource.getWarnings();	        
		        for(final Iterator warnIter = warnings.iterator(); warnIter.hasNext();) {
		            Diagnostic warnDiagnostic = (Diagnostic) warnIter.next();
		            String diagMessage = warnDiagnostic.getMessage();
		            if(!messages.contains(diagMessage)) {
			    		// create validation problem and addit to the result
			    		final ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.WARNING, warnDiagnostic.getMessage());
			    		result.addProblem(problem);
			    		messages.add(diagMessage);
		            }
		        }
		    }
		    if(result.hasProblems()) {
		        context.addResult(result);
		    }
	    }
    }

}
