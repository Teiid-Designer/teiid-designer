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
import org.teiid.designer.core.metamodel.aspect.relationship.RelationshipRoleAspect;
import org.teiid.designer.metamodels.relationship.RelationshipRole;


/**
 * RelationRoleAspect
 */
public class RelationRoleAspect extends RelationshipEntityAspect implements RelationshipRoleAspect {

	protected RelationRoleAspect(MetamodelEntity entity) {
		super(entity);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipMetamodelAspect#isRecordType(char)
	 */
	@Override
	public boolean isRecordType(char recordType) {
		return (recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP_ROLE);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#getStereoType(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getStereoType(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getStereotype();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#isOrdered(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isOrdered(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isOrdered();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#isUnique(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isUnique(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isUnique();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#isNavigable(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isNavigable(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isNavigable();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#getLowerBound(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public int getLowerBound(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getLowerBound();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#getUpperBound(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public int getUpperBound(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getUpperBound();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#isSourceRole(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isSourceRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isSourceRole();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#isTargetRole(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public boolean isTargetRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.isTargetRole();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#getOppositeRole(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public Object getOppositeRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getOppositeRole();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipRoleAspect#getOverriddenRole(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public Object getOverriddenRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(RelationshipRole.class, eObject);
		RelationshipRole relationshipRole = (RelationshipRole) eObject;
		return relationshipRole.getOverriddenRole();
	}

}
