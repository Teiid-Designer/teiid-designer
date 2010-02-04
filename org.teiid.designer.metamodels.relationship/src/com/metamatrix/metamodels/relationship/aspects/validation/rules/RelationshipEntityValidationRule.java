/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relationship.RelationshipEntity;
import com.metamatrix.modeler.core.validation.ObjectValidationRule;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * RelationshipEntityValidationRule.java
 */
public class RelationshipEntityValidationRule implements ObjectValidationRule {

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext)
	 */
	public void validate(EObject eObject, ValidationContext context) {
		ArgCheck.isInstanceOf(RelationshipEntity.class, eObject);
		// validate the relationship		
		RelationshipEntity relationshipEntity = (RelationshipEntity) eObject;
		// get the status on the relationship		
		IStatus status = relationshipEntity.isValid();
		if(status.getSeverity() == IStatus.OK) {
			return;
		}
		// create a validation resut and add the statuses as problems
		ValidationResult result = new ValidationResultImpl(eObject); 
		IStatus[] statuses = status.getChildren();
		if(statuses.length > 0) {
			for(int i =0; i< statuses.length; i++) {
				IStatus childStatus = statuses[i];
				ValidationProblem problem = new ValidationProblemImpl(childStatus);
				result.addProblem(problem); 	
			}
		} else {
			ValidationProblem problem = new ValidationProblemImpl(status);
			result.addProblem(problem);
		}
		
		context.addResult(result);
	}

}
