/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metadata.runtime;

import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlForeignKeyAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;


/**
 * FakeForeignKeyAspect
 */
public class FakeForeignKeyAspect implements SqlForeignKeyAspect {

    public String name, fullName, nameInSource;
    public IPath path;
    public List columns;
    public Object uniqueKey, uuid, parentUuid;

    @Override
	public Object getUniqueKey(EObject eObject) { return uniqueKey; }

    @Override
	public List getColumns(EObject eObject) { return columns; }

    @Override
	public int getColumnSetType() { return MetadataConstants.COLUMN_SET_TYPES.FOREIGN_KEY; }

    @Override
	public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.FOREIGN_KEY ); }

    @Override
	public boolean isQueryable(EObject eObject) { return true; }

    @Override
	public String getName(EObject eObject) { return name; }

    @Override
	public String getFullName(EObject eObject) { return fullName; }

    @Override
	public String getNameInSource(EObject eObject) { return nameInSource; }

    @Override
	public Object getObjectID(EObject eObject) { return uuid; }

    @Override
	public Object getParentObjectID(EObject eObject) { return parentUuid; }

    @Override
	public String getID() { return null; }

    @Override
	public MetamodelEntity getMetamodelEntity() { return null; }

    @Override
	public IPath getPath(EObject eObject) { return path; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
