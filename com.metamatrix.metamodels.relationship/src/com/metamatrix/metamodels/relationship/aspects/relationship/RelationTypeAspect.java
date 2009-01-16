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

package com.metamatrix.metamodels.relationship.aspects.relationship;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipTypeAspect;

/**
 * RelationTypeAspect.java
 */
public class RelationTypeAspect extends RelationshipEntityAspect implements RelationshipTypeAspect {

	protected RelationTypeAspect(MetamodelEntity entity) {
		super(entity);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipMetamodelAspect#isRecordType(char)
	 */
	public boolean isRecordType(char recordType) {
		return (recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_TYPE);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getConstraint(org.eclipse.emf.ecore.EObject)
	 */
	public String getConstraint(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getConstraint();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getSourceRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getSourceRole(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getSourceRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getStereoType(org.eclipse.emf.ecore.EObject)
	 */
	public String getStereoType(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getStereotype();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getSuperType(org.eclipse.emf.ecore.EObject)
	 */
	public Object getSuperType(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getSuperType();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getTargetRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getTargetRole(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getTargetRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#isAbstract(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isAbstract(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isAbstract();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#isDirected(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isDirected(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isDirected();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#isExclusive(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isExclusive(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isExclusive();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#isUserDefined(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isUserDefined(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isUserDefined();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.metamodel.aspect.relationship.RelationshipTypeAspect#getSourceRoleName(org.eclipse.emf.ecore.EObject)
	 */
	public String getSourceRoleName(EObject eObject) {
		RelationshipRole sourceRole = (RelationshipRole) getSourceRole(eObject);
		if(sourceRole != null) {
			return sourceRole.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.metamodel.aspect.relationship.RelationshipTypeAspect#getTargetRoleName(org.eclipse.emf.ecore.EObject)
	 */
	public String getTargetRoleName(EObject eObject) {
		RelationshipRole targetRole = (RelationshipRole) getTargetRole(eObject);
		if(targetRole != null) {
			return targetRole.getName();
		}
		return null;
	}

}
