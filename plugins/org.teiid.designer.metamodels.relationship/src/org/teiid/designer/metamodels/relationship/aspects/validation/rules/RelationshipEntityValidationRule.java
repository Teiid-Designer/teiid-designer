/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.aspects.validation.rules;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.validation.ObjectValidationRule;
import org.teiid.designer.core.validation.ValidationContext;
import org.teiid.designer.core.validation.ValidationProblem;
import org.teiid.designer.core.validation.ValidationProblemImpl;
import org.teiid.designer.core.validation.ValidationResult;
import org.teiid.designer.core.validation.ValidationResultImpl;
import org.teiid.designer.metamodels.relationship.RelationshipEntity;


/**
 * RelationshipEntityValidationRule.java
 */
public class RelationshipEntityValidationRule implements ObjectValidationRule {

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.validation.ObjectValidationRule#validate(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext)
	 */
	@Override
	public void validate(EObject eObject, ValidationContext context) {
		CoreArgCheck.isInstanceOf(RelationshipEntity.class, eObject);
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
