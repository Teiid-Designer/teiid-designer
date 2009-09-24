/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipAspect;

/**
 * FakeRelationshipAspect.java
 */
public class FakeRelationshipAspect implements RelationshipAspect {

	public String name, typeName, sourceRoleName, targetRoleName;
	public IPath path;
	public Collection sources = Collections.EMPTY_LIST;
	public Collection targets = Collections.EMPTY_LIST;
	public Object uuid, parentUuid, typeUUid;

	public Collection getSources(EObject eObject) { return this.sources;	}

	public Collection getTargets(EObject eObject) { return this.targets;	}

	public Object getType(EObject eObject) {return this.typeUUid;}

	public Object getSourceRole(EObject eObject) {return null;}

	public Object getTargetRole(EObject eObject) {return null;}

	public IStatus isValid(EObject eObject) {	return null;	}

	public boolean isRecordType(char recordType) { return (recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT ); }

	public Object getObjectID(EObject eObject) { return this.uuid; }

	public Object getParentObjectID(EObject eObject) {	return this.parentUuid;}

	public IPath getPath(EObject eObject) {return this.path;}

	public MetamodelEntity getMetamodelEntity() { return null;}

	public String getID() { return null;	}

	public String getName(EObject eObject) { return this.name; }

	public String getSourceRoleName(EObject eObject) { return this.sourceRoleName; }

	public String getTargetRoleName(EObject eObject) {	return this.targetRoleName; }

	public String getTypeName(EObject eObject) {return this.typeName;	}

}
