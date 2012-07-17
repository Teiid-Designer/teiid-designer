/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.relationship.RelationshipTypeAspect;


/**
 * FakeRelationshipTypeAspect.java
 */
public class FakeRelationshipTypeAspect implements RelationshipTypeAspect {

	public String name, sourceRoleName, targetRoleName, comment;
	public IPath path;
	public Object uuid, parentUuid, superTypeUUID;

	@Override
	public boolean isDirected(EObject eObject) {return false;}

	@Override
	public boolean isAbstract(EObject eObject) {return false;}

	@Override
	public boolean isExclusive(EObject eObject) {return false;}

	@Override
	public boolean isUserDefined(EObject eObject) {return false;}

	@Override
	public String getStereoType(EObject eObject) {return null;}

	@Override
	public String getConstraint(EObject eObject) {return null;}

	@Override
	public Object getSourceRole(EObject eObject) {return null;}

	@Override
	public Object getTargetRole(EObject eObject) {return null;}

	@Override
	public Object getSuperType(EObject eObject) {return superTypeUUID;}

	@Override
	public boolean isRecordType(char recordType) {return false;}

	@Override
	public Object getObjectID(EObject eObject) {return uuid;}

	@Override
	public Object getParentObjectID(EObject eObject) {return parentUuid;}

	@Override
	public IPath getPath(EObject eObject) {return this.path;}

	@Override
	public MetamodelEntity getMetamodelEntity() {return null;}

	@Override
	public String getID() {return null;}

	public String getComment(EObject eObject) {return comment;}

	@Override
	public String getName(EObject eObject) {return this.name;}

	@Override
	public String getSourceRoleName(EObject eObject) {return this.sourceRoleName;}

	@Override
	public String getTargetRoleName(EObject eObject) {return this.targetRoleName;}

}
