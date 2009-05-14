/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipTypeAspect;

/**
 * FakeRelationshipTypeAspect.java
 */
public class FakeRelationshipTypeAspect implements RelationshipTypeAspect {

	public String name, sourceRoleName, targetRoleName, comment;
	public IPath path;
	public Object uuid, parentUuid, superTypeUUID;

	public boolean isDirected(EObject eObject) {return false;}

	public boolean isAbstract(EObject eObject) {return false;}

	public boolean isExclusive(EObject eObject) {return false;}

	public boolean isUserDefined(EObject eObject) {return false;}

	public String getStereoType(EObject eObject) {return null;}

	public String getConstraint(EObject eObject) {return null;}

	public Object getSourceRole(EObject eObject) {return null;}

	public Object getTargetRole(EObject eObject) {return null;}

	public Object getSuperType(EObject eObject) {return superTypeUUID;}

	public boolean isRecordType(char recordType) {return false;}

	public Object getObjectID(EObject eObject) {return uuid;}

	public Object getParentObjectID(EObject eObject) {return parentUuid;}

	public IPath getPath(EObject eObject) {return this.path;}

	public MetamodelEntity getMetamodelEntity() {return null;}

	public String getID() {return null;}

	public String getComment(EObject eObject) {return comment;}

	public String getName(EObject eObject) {return this.name;}

	public String getSourceRoleName(EObject eObject) {return this.sourceRoleName;}

	public String getTargetRoleName(EObject eObject) {return this.targetRoleName;}

}
