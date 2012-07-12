/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.aspects.relationship;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.relationship.RelationshipAspect;
import org.teiid.designer.metamodels.relationship.Relationship;
import org.teiid.designer.metamodels.relationship.RelationshipRole;
import org.teiid.designer.metamodels.relationship.RelationshipType;


/**
 * RelationAspect
 */
public class RelationAspect
	extends RelationshipEntityAspect
	implements RelationshipAspect {

	protected RelationAspect(MetamodelEntity entity) {
		super(entity);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipAspect#getSources(org.eclipse.emf.ecore.EObject)
	 */
	public Collection getSources(EObject eObject) {
		CoreArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getSources();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipAspect#getTargets(org.eclipse.emf.ecore.EObject)
	 */
	public Collection getTargets(EObject eObject) {
		CoreArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getTargets();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipAspect#getype(org.eclipse.emf.ecore.EObject)
	 */
	public Object getType(EObject eObject) {
		CoreArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getType();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipAspect#getSourceRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getSourceRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getSourceRole();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipAspect#getTargetRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getTargetRole(EObject eObject) {
		CoreArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getTargetRole();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipAspect#isValid(org.eclipse.emf.ecore.EObject)
	 */
	public IStatus isValid(EObject eObject) {
		CoreArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.isValid();
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.relationship.aspect.relationship.RelationshipMetamodelAspect#isRecordType(char)
	 */
	public boolean isRecordType(char recordType) {
		return (recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP || recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.relationship.metamodel.aspect.relationship.RelationshipAspect#getSourceRoleName(org.eclipse.emf.ecore.EObject)
	 */
	public String getSourceRoleName(EObject eObject) {
		RelationshipRole sourceRole = (RelationshipRole) getSourceRole(eObject);
		if(sourceRole != null) {
			return sourceRole.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.relationship.metamodel.aspect.relationship.RelationshipAspect#getTargetRoleName(org.eclipse.emf.ecore.EObject)
	 */
	public String getTargetRoleName(EObject eObject) {
		RelationshipRole targetRole = (RelationshipRole) getTargetRole(eObject);
		if(targetRole != null) {
			return targetRole.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.relationship.metamodel.aspect.relationship.RelationshipAspect#getTypeName(org.eclipse.emf.ecore.EObject)
	 */
	public String getTypeName(EObject eObject) {
		RelationshipType type = (RelationshipType) getType(eObject);
		if(type != null) {
			return type.getName();
		}
		return null;
	}

}
