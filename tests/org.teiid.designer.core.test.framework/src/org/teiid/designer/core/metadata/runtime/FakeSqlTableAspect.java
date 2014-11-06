/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metadata.runtime;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;


/**
 * FakeSqlTableAspect
 */
public class FakeSqlTableAspect implements SqlTableAspect {
    public String name, fullName, nameInSource;
    public IPath path;
    public boolean virtual, supportsUpdate, system, materialized;
    public List columns, foreignKeys, indexes, uniqueKeys, accessPatterns;
    public Object primaryKey, uuid, parentUuid;
    public int cardinality, tableType;

    @Override
	public Collection getAccessPatterns(EObject eObject) { return accessPatterns; }

    @Override
	public int getCardinality(EObject eObject) { return cardinality; }

    @Override
	public List getColumns(EObject eObject) { return columns; }

    @Override
	public Collection getForeignKeys(EObject eObject) { return foreignKeys; }

    @Override
	public Collection getIndexes(EObject eObject) { return indexes; }

    @Override
	public String getName(EObject eObject) { return name; }

    @Override
	public String getFullName(EObject eObject) { return fullName; }    

    @Override
	public String getNameInSource(EObject eObject) { return nameInSource; }

    @Override
	public IPath getPath(EObject eObject) { return path; }

    @Override
	public Object getObjectID(EObject eObject) { return uuid; }

    @Override
	public Object getPrimaryKey(EObject eObject) { return primaryKey; }

    @Override
	public Collection getUniqueKeys(EObject eObject) { return uniqueKeys; }

    @Override
	public boolean isVirtual(EObject eObject) { return virtual; }

    @Override
	public boolean isMaterialized(EObject eObject) { return materialized; }
    
    @Override
	public String getMaterializedTableId(EObject eObject) { return null; } 

    @Override
	public boolean isSystem(EObject eObject) { return system; }

    @Override
	public boolean supportsUpdate(EObject eObject) { return supportsUpdate; }

    @Override
	public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.TABLE ); } 

    @Override
	public boolean isQueryable(EObject eObject) { return true; }

    @Override
	public String getID() { return null; }

    @Override
	public MetamodelEntity getMetamodelEntity() { return null; }

    @Override
	public Object getParentObjectID(EObject eObject) { return parentUuid; }

    @Override
	public int getTableType(EObject eObject) { return tableType; }

    @Override
	public int getColumnSetType() { return MetadataConstants.COLUMN_SET_TYPES.TABLE; }

    @Override
	public boolean isMappable(EObject eObject, int mappingType) { return false; }

    @Override
	public boolean canAcceptTransformationSource(EObject target, EObject source) { return false; }

    @Override
	public boolean canBeTransformationSource(EObject source, EObject target) { return false; }
    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#setSupportsUpdate(org.eclipse.emf.ecore.EObject, boolean)
     */
    @Override
	public void setSupportsUpdate(EObject eObject, boolean supportsUpdate) {
        this.supportsUpdate = supportsUpdate;
    }

	@Override
	public boolean isGlobalTempTable(EObject eObject) {
		return false;
	}

}
