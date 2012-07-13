/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.aspects.relationship;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.relationship.RelationshipTypeAspect;
import org.teiid.designer.metamodels.relationship.RelationshipRole;
import org.teiid.designer.metamodels.relationship.RelationshipType;


/**
 * RelationTypeAspect.java
 */
public class RelationTypeAspect extends RelationshipEntityAspect implements RelationshipTypeAspect {

	protected RelationTypeAspect(MetamodelEntity entity) {
		super(entity);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipMetamodelAspect#isRecordType(char)
	 */
	@Override
	public boolean isRecordType(char recordType) {
		return (recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_TYPE);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#getConstraint(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getConstraint(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getConstraint();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#getSourceRole(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public Object getSourceRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getSourceRole();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#getStereoType(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getStereoType(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getStereotype();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#getSuperType(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public Object getSuperType(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getSuperType();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#getTargetRole(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public Object getTargetRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.getTargetRole();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#isAbstract(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isAbstract(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isAbstract();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#isDirected(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isDirected(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isDirected();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#isExclusive(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isExclusive(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isExclusive();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipTypeAspect#isUserDefined(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isUserDefined(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipType.class, eObject);
		RelationshipType relationshipType = (RelationshipType) eObject;
		return relationshipType.isUserDefined();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.relationship.metamodel.aspect.relationship.RelationshipTypeAspect#getSourceRoleName(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getSourceRoleName(EObject eObject) {
		RelationshipRole sourceRole = (RelationshipRole) getSourceRole(eObject);
		if(sourceRole != null) {
			return sourceRole.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.relationship.metamodel.aspect.relationship.RelationshipTypeAspect#getTargetRoleName(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getTargetRoleName(EObject eObject) {
		RelationshipRole targetRole = (RelationshipRole) getTargetRole(eObject);
		if(targetRole != null) {
			return targetRole.getName();
		}
		return null;
	}

}
