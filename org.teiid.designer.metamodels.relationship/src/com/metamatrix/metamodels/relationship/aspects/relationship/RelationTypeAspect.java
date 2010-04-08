/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.aspects.relationship;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.CoreArgCheck;
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
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getConstraint();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getSourceRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getSourceRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getSourceRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getStereoType(org.eclipse.emf.ecore.EObject)
	 */
	public String getStereoType(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getStereotype();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getSuperType(org.eclipse.emf.ecore.EObject)
	 */
	public Object getSuperType(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getSuperType();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#getTargetRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getTargetRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getTargetRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#isAbstract(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isAbstract(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isAbstract();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#isDirected(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isDirected(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isDirected();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#isExclusive(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isExclusive(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isExclusive();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipTypeAspect#isUserDefined(org.eclipse.emf.ecore.EObject)
	 */
	public boolean isUserDefined(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
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
