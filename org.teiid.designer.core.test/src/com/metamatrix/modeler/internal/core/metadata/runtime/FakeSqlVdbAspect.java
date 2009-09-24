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
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect;

/**
 * FakeSqlModelAspect
 */
public class FakeSqlVdbAspect implements SqlVdbAspect {
    
    public String name, fullName, nameInSource, timeLastChanged, timeLastProduced;
    public String version, identifier, description, producerName, producerVersion, provider;
    public List models;
    public IPath path;
    public Object uuid, parentUuid;
        
    public String getName(EObject eObject) { return name; }
    
    public String getFullName(EObject eObject) { return fullName; }    
    
    public String getNameInSource(EObject eObject) { return nameInSource; }
    
    public IPath getPath(EObject eObject) { return path; }
    
    public Object getObjectID(EObject eObject) { return uuid; }
    
    public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.VDB_ARCHIVE ); } 

    public boolean isQueryable(EObject eObject) { return true; }
    
    public String getID() { return null; }
    
    public MetamodelEntity getMetamodelEntity() { return null; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }

    public String getDescription(EObject eObject) { return description; }

    public String getIdentifier(EObject eObject) { return identifier; }

    public List getModelIDs(EObject eObject) { return models; }

    public String getProducerName(EObject eObject) { return producerName; }

    public String getProducerVersion(EObject eObject) { return producerVersion; }

    public String getProvider(EObject eObject) { return provider; }

    public String getTimeLastChanged(EObject eObject) { return timeLastChanged; }

    public String getTimeLastProduced(EObject eObject) { return timeLastProduced; }

    public String getVersion(EObject eObject) { return version; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
