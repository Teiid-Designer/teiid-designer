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
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipRoleAspect;

/**
 * RelationRoleAspect
 */
public class RelationRoleAspect extends RelationshipEntityAspect implements RelationshipRoleAspect {

	protected RelationRoleAspect(MetamodelEntity entity) {
		super(entity);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipMetamodelAspect#isRecordType(char)
	 */
	public boolean isRecordType(char recordType) {
		return (recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_ROLE);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#getStereoType(org.eclipse.emf.ecore.EObject)
	 */
	public String getStereoType(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getStereotype();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#isOrdered(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isOrdered(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isOrdered();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#isUnique(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isUnique(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isUnique();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#isNavigable(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isNavigable(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isNavigable();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#getLowerBound(org.eclipse.emf.ecore.EObject)
	 */
	public int getLowerBound(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getLowerBound();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#getUpperBound(org.eclipse.emf.ecore.EObject)
	 */
	public int getUpperBound(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getUpperBound();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#isSourceRole(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isSourceRole(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isSourceRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#isTargetRole(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isTargetRole(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isTargetRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#getOppositeRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getOppositeRole(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getOppositeRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipRoleAspect#getOverriddenRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getOverriddenRole(EObject eObject) {
		ArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getOverriddenRole();
	}

}
