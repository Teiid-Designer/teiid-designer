/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.aspects.relationship;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipAspect;

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
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipAspect#getSources(org.eclipse.emf.ecore.EObject)
	 */
	public Collection getSources(EObject eObject) {
		ArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getSources();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipAspect#getTargets(org.eclipse.emf.ecore.EObject)
	 */
	public Collection getTargets(EObject eObject) {
		ArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getTargets();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipAspect#getype(org.eclipse.emf.ecore.EObject)
	 */
	public Object getType(EObject eObject) {
		ArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getType();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipAspect#getSourceRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getSourceRole(EObject eObject) {
		ArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getSourceRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipAspect#getTargetRole(org.eclipse.emf.ecore.EObject)
	 */
	public Object getTargetRole(EObject eObject) {
		ArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.getTargetRole();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipAspect#isValid(org.eclipse.emf.ecore.EObject)
	 */
	public IStatus isValid(EObject eObject) {
		ArgCheck.isInstanceOf(Relationship.class, eObject);
		Relationship relationship = (Relationship) eObject;
		return relationship.isValid();
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.relationship.aspect.relationship.RelationshipMetamodelAspect#isRecordType(char)
	 */
	public boolean isRecordType(char recordType) {
		return (recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATIONSHIP || recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.metamodel.aspect.relationship.RelationshipAspect#getSourceRoleName(org.eclipse.emf.ecore.EObject)
	 */
	public String getSourceRoleName(EObject eObject) {
		RelationshipRole sourceRole = (RelationshipRole) getSourceRole(eObject);
		if(sourceRole != null) {
			return sourceRole.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.metamodel.aspect.relationship.RelationshipAspect#getTargetRoleName(org.eclipse.emf.ecore.EObject)
	 */
	public String getTargetRoleName(EObject eObject) {
		RelationshipRole targetRole = (RelationshipRole) getTargetRole(eObject);
		if(targetRole != null) {
			return targetRole.getName();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.relationship.metamodel.aspect.relationship.RelationshipAspect#getTypeName(org.eclipse.emf.ecore.EObject)
	 */
	public String getTypeName(EObject eObject) {
		RelationshipType type = (RelationshipType) getType(eObject);
		if(type != null) {
			return type.getName();
		}
		return null;
	}

}
