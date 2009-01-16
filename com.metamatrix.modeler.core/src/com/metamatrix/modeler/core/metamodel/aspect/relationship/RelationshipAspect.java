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
