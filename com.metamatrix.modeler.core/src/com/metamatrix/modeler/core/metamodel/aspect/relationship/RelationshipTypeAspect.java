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
