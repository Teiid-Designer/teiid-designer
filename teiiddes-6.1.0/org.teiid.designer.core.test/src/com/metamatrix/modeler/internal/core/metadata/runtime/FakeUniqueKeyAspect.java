/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlUniqueKeyAspect;

/**
 * FakeUniqueKeyAspect
 */
public class FakeUniqueKeyAspect implements SqlUniqueKeyAspect {

    public String name, fullName, nameInSource;
    public IPath path;
    public List columns, foreignKeys;
    public Object uniqueKey, uuid, parentUuid;

    public List getForeignKeys(EObject eObject) { return foreignKeys; }

    public List getColumns(EObject eObject) { return columns; }

    public int getColumnSetType() { return MetadataConstants.COLUMN_SET_TYPES.FOREIGN_KEY; }

    public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.FOREIGN_KEY ); }

    public boolean isQueryable(EObject eObject) { return true; }

    public String getName(EObject eObject) { return name; }

    public String getFullName(EObject eObject) { return fullName; }

    public String getNameInSource(EObject eObject) { return nameInSource; }

    public Object getObjectID(EObject eObject) { return uuid; }

    public String getID() { return null; }

    public MetamodelEntity getMetamodelEntity() { return null; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }

    public IPath getPath(EObject eObject) { return path; }
    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
