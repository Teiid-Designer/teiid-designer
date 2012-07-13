/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.runtime;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.relationship.RelationshipAspect;


/**
 * FakeRelationshipAspect.java
 */
public class FakeRelationshipAspect implements RelationshipAspect {

	public String name, typeName, sourceRoleName, targetRoleName;
	public IPath path;
	public Collection sources = Collections.EMPTY_LIST;
	public Collection targets = Collections.EMPTY_LIST;
	public Object uuid, parentUuid, typeUUid;

	@Override
	public Collection getSources(EObject eObject) { return this.sources;	}

	@Override
	public Collection getTargets(EObject eObject) { return this.targets;	}

	@Override
	public Object getType(EObject eObject) {return this.typeUUid;}

	@Override
	public Object getSourceRole(EObject eObject) {return null;}

	@Override
	public Object getTargetRole(EObject eObject) {return null;}

	@Override
	public IStatus isValid(EObject eObject) {	return null;	}

	@Override
	public boolean isRecordType(char recordType) { return (recordType == IndexConstants.SEARCH_RECORD_TYPE.RELATED_OBJECT ); }

	@Override
	public Object getObjectID(EObject eObject) { return this.uuid; }

	@Override
	public Object getParentObjectID(EObject eObject) {	return this.parentUuid;}

	@Override
	public IPath getPath(EObject eObject) {return this.path;}

	@Override
	public MetamodelEntity getMetamodelEntity() { return null;}

	@Override
	public String getID() { return null;	}

	@Override
	public String getName(EObject eObject) { return this.name; }

	@Override
	public String getSourceRoleName(EObject eObject) { return this.sourceRoleName; }

	@Override
	public String getTargetRoleName(EObject eObject) {	return this.targetRoleName; }

	@Override
	public String getTypeName(EObject eObject) {return this.typeName;	}

}
