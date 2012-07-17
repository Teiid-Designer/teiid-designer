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
import org.teiid.designer.core.metamodel.aspect.sql.SqlVdbAspect;


/**
 * FakeSqlModelAspect
 */
public class FakeSqlVdbAspect implements SqlVdbAspect {
    
    public String name, fullName, nameInSource, timeLastChanged, timeLastProduced;
    public String version, identifier, description, producerName, producerVersion, provider;
    public List models;
    public IPath path;
    public Object uuid, parentUuid;
        
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
	public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.VDB_ARCHIVE ); } 

    @Override
	public boolean isQueryable(EObject eObject) { return true; }
    
    @Override
	public String getID() { return null; }
    
    @Override
	public MetamodelEntity getMetamodelEntity() { return null; }

    @Override
	public Object getParentObjectID(EObject eObject) { return parentUuid; }

    @Override
	public String getDescription(EObject eObject) { return description; }

    @Override
	public String getIdentifier(EObject eObject) { return identifier; }

    @Override
	public List getModelIDs(EObject eObject) { return models; }

    @Override
	public String getProducerName(EObject eObject) { return producerName; }

    @Override
	public String getProducerVersion(EObject eObject) { return producerVersion; }

    @Override
	public String getProvider(EObject eObject) { return provider; }

    @Override
	public String getTimeLastChanged(EObject eObject) { return timeLastChanged; }

    @Override
	public String getTimeLastProduced(EObject eObject) { return timeLastProduced; }

    @Override
	public String getVersion(EObject eObject) { return version; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
