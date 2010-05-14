/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;

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

    public Collection getAccessPatterns(EObject eObject) { return accessPatterns; }

    public int getCardinality(EObject eObject) { return cardinality; }

    public List getColumns(EObject eObject) { return columns; }

    public Collection getForeignKeys(EObject eObject) { return foreignKeys; }

    public Collection getIndexes(EObject eObject) { return indexes; }

    public String getName(EObject eObject) { return name; }

    public String getFullName(EObject eObject) { return fullName; }    

    public String getNameInSource(EObject eObject) { return nameInSource; }

    public IPath getPath(EObject eObject) { return path; }

    public Object getObjectID(EObject eObject) { return uuid; }

    public Object getPrimaryKey(EObject eObject) { return primaryKey; }

    public Collection getUniqueKeys(EObject eObject) { return uniqueKeys; }

    public boolean isVirtual(EObject eObject) { return virtual; }

    public boolean isMaterialized(EObject eObject) { return materialized; }

    public boolean isSystem(EObject eObject) { return system; }

    public boolean supportsUpdate(EObject eObject) { return supportsUpdate; }

    public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.TABLE ); } 

    public boolean isQueryable(EObject eObject) { return true; }

    public String getID() { return null; }

    public MetamodelEntity getMetamodelEntity() { return null; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }

    public int getTableType(EObject eObject) { return tableType; }

    public int getColumnSetType() { return MetadataConstants.COLUMN_SET_TYPES.TABLE; }

    public boolean isMappable(EObject eObject, int mappingType) { return false; }

    public boolean canAcceptTransformationSource(EObject target, EObject source) { return false; }

    public boolean canBeTransformationSource(EObject source, EObject target) { return false; }
    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#setSupportsUpdate(org.eclipse.emf.ecore.EObject, boolean)
     */
    public void setSupportsUpdate(EObject eObject, boolean supportsUpdate) {
        this.supportsUpdate = supportsUpdate;
    }

}
