/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect;

/**
 * FakeSqlModelAspect
 */
public class FakeSqlModelAspect implements SqlModelAspect {
    
    public String name, fullName, nameInSource, primaryMetamodelUri;
    public IPath path;
    public Object uuid, parentUuid;
    public boolean supportsOrderBy, supportsOuterJoin, supportsWhereAll, supportsDistinct, supportsJoin, isVisible;
    public int maxSetSize, modelType;
    
    public String getPrimaryMetamodelUri(EObject eObject) { return primaryMetamodelUri; }

    public boolean supportsOrderBy(EObject eObject) { return supportsOrderBy; }

    public boolean supportsOuterJoin(EObject eObject) { return supportsOuterJoin; }

    public boolean supportsWhereAll(EObject eObject) { return supportsWhereAll; }

    public boolean supportsDistinct(EObject eObject) { return supportsDistinct; }

    public boolean supportsJoin(EObject eObject) { return supportsJoin; }

    public boolean isVisible(EObject eObject) { return isVisible; }
    
    public int getModelType(EObject eObject) {return modelType;}

    public int getMaxSetSize(EObject eObject) { return maxSetSize; }
    
    public String getName(EObject eObject) { return name; }
    
    public String getFullName(EObject eObject) { return fullName; }    
    
    public String getNameInSource(EObject eObject) { return nameInSource; }
    
    public IPath getPath(EObject eObject) { return path; }
    
    public Object getObjectID(EObject eObject) { return uuid; }
    
    public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.MODEL ); } 

    public boolean isQueryable(EObject eObject) { return true; }
    
    public String getID() { return null; }
    
    public MetamodelEntity getMetamodelEntity() { return null; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }


    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
