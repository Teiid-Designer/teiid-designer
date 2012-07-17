/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.validation.rules;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.validation.ResourceValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;



/** 
 * @since 4.2
 */
public class EmfResourceValidationRule implements ResourceValidationRule {

    /** 
     * @see org.teiid.designer.core.validation.ResourceValidationRule#validate(org.eclipse.emf.ecore.resource.Resource, org.teiid.designer.core.validation.ValidationContext)
     * @since 4.2
     */
    @Override
	public void validate(final Resource resource, final ValidationContext context) {
	    CoreArgCheck.isNotNull(resource);
	    CoreArgCheck.isNotNull(context);
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
