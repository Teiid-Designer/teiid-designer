/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.relationship;

import org.eclipse.emf.ecore.EObject;

/**
 * RelationshipTypeAspect
 */
public interface RelationshipTypeAspect extends RelationshipEntityAspect {

	/**
	 * Check if the relationship type has a direction.
	 * @param eObject The <code>EObject</code> is checked if it is directed
	 * @return boolean indicating if type is directed.
	 */
	boolean isDirected(EObject eObject);

	/**
	 * Check if the relationship type is an abstract type.
	 * @param eObject The <code>EObject</code> is checked if it is abstract
	 * @return boolean indicating if type is abstract.
	 */
	boolean isAbstract(EObject eObject);

	/**
	 * Check if the relationship type is an abstract type.
	 * @param eObject The <code>EObject</code> is checked if it is exclusive
	 * @return boolean indicating if type is exclusive.
	 */
	boolean isExclusive(EObject eObject);

	/**
	 * Check if the relationship type is an abstract type.
	 * @param eObject The <code>EObject</code> is checked if it is userdefined
	 * @return boolean indicating if type is userdefined.
	 */
	boolean isUserDefined(EObject eObject);

	/**
	 * Get stereotype on the type, this may be null.
	 * @param eObject The <code>EObject</code> for which stereotype is obtained 
	 * @return types stereotype if any.
	 */
	String getStereoType(EObject eObject);

	/**
	 * Get constraint on the type, this may be null.
	 * @param eObject The <code>EObject</code> for which constraint is obtained 
	 * @return types constraint if any.
	 */
	String getConstraint(EObject eObject);

	/**
	 * Get a the source relationshiprole <code>EObject</code>s for this relationship type.
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained 
	 * @return a <code>EObject</code> that is the relationshiprole
	 */	
	Object getSourceRole(EObject eObject);
	
	/**
	 * Get a the target relationshiprole <code>EObject</code>s for this relationship type.
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained 
	 * @return a <code>EObject</code> that is the relationshiprole
	 */	
	Object getTargetRole(EObject eObject);

	/**
	 * Get a the relationshiprole name for source to this relationship type.
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained 
	 * @return name of the source role
	 */
	String getSourceRoleName(EObject eObject);

	/**
	 * Get a the relationshiprole name for target to this relationship type.
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained 
	 * @return name of the target type.
	 */
	String getTargetRoleName(EObject eObject);

	/**
	 * Get the superType for this relationship type. 
	 * @param eObject The <code>EObject</code> for which relationshiprole is obtained
	 * @return
	 */
	Object getSuperType(EObject eObject);
}
