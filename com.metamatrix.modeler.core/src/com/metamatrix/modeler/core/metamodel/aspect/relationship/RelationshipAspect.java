/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.relationship;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

/**
 * RelationshipAspect
 */
public interface RelationshipAspect extends RelationshipEntityAspect {

	/**
	 * Get a list of <code>EObject</code>s objects for the source to the relationship.
	 * @param eObject The <code>EObject</code> for which sources are obtained 
	 * @return a collection of <code>EObject</code>s
	 */
	Collection getSources(EObject eObject);

	/**
	 * Get a list of <code>EObject</code>s objects for the source to the relationship.
	 * @param eObject The <code>EObject</code> for which sources are obtained 
	 * @return a collection of <code>EObject</code>s
	 */
	Collection getTargets(EObject eObject);

	/**
	 * Get a the relationshiptype <code>EObject</code>s for this relationship.
	 * @param eObject The <code>EObject</code> for which relationshiptype is obtained 
	 * @return a <code>EObject</code> that is the relationshiptype
	 */
	Object getType(EObject eObject);

	/**
	 * Get a the name of the relationshiptype for this relationship.
	 * @param eObject The <code>EObject</code> for which relationshiptype is obtained 
	 * @return the name of the relationshiptype
	 */
	String getTypeName(EObject eObject);

	/**
	 * Get a the relationshiprole <code>EObject</code>s for source to this relationship.
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained 
	 * @return a <code>EObject</code> that is the relationshiprole
	 */
	Object getSourceRole(EObject eObject);
	
	/**
	 * Get a the relationshiprole <code>EObject</code>s for target to this relationship.
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained 
	 * @return a <code>EObject</code> that is the relationshiprole
	 */
	Object getTargetRole(EObject eObject);

	/**
	 * Get a the relationshiprole name for source to this relationship.
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained 
	 * @return name of the relationshiprole
	 */
	String getSourceRoleName(EObject eObject);

	/**
	 * Get a the relationshiprole name for target to this relationship.
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained 
	 * @return name of the relationshiprole
	 */
	String getTargetRoleName(EObject eObject);

	/**
	 * Get a the status on this relationship.
	 * @param eObject The <code>EObject</code> for which status is obtained 
	 * @return a <code>IStatus</code> that is status on the relationship
	 */
	IStatus isValid(EObject eObject);
}
