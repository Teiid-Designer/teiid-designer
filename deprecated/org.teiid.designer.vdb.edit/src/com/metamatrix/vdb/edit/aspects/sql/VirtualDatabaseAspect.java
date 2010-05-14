/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.aspects.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;

/**
 * RelationalEntityAspect
 */
public class VirtualDatabaseAspect extends AbstractMetamodelAspect implements SqlVdbAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;

    protected VirtualDatabaseAspect(MetamodelEntity entity) {
        super();
        super.setID(ASPECT_ID);
        super.setMetamodelEntity(entity);
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getName();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getName();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.VDB_ARCHIVE);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getDescription(org.eclipse.emf.ecore.EObject)
     */
    public String getDescription(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getDescription();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getIdentifier(org.eclipse.emf.ecore.EObject)
     */
    public String getIdentifier(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getIdentifier();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getModelIDs(org.eclipse.emf.ecore.EObject)
     */
    public List getModelIDs(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;
        
        final List modelRefs = entity.getModels();
        final List modelIDs = new ArrayList(modelRefs.size());
        for (Iterator iter = modelRefs.iterator(); iter.hasNext();) {
            final ModelReference modelRef = (ModelReference)iter.next();
            if (modelRef != null && modelRef.getUuid() != null) {
                modelIDs.add(modelRef.getUuid());
            }
        }
        return modelIDs;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getProducerName(org.eclipse.emf.ecore.EObject)
     */
    public String getProducerName(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getProducerName();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getProducerVersion(org.eclipse.emf.ecore.EObject)
     */
    public String getProducerVersion(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getProducerVersion();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getProvider(org.eclipse.emf.ecore.EObject)
     */
    public String getProvider(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getProvider();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getTimeLastChanged(org.eclipse.emf.ecore.EObject)
     */
    public String getTimeLastChanged(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getTimeLastChanged();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getTimeLastProduced(org.eclipse.emf.ecore.EObject)
     */
    public String getTimeLastProduced(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getTimeLastProduced();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlVdbAspect#getVersion(org.eclipse.emf.ecore.EObject)
     */
    public String getVersion(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getVersion();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return entity.getName();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getObjectID(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        String uuidString = entity.getUuid();
        if (uuidString != null) {
            try {
                return IDGenerator.getInstance().stringToObject(uuidString,UUID.PROTOCOL);
            } catch (InvalidIDException e) {
                // Proceed by trying to get the UUID in other ways
            }
        }  
        return super.getObjectID(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public IPath getPath(EObject eObject) {
        ArgCheck.isInstanceOf(VirtualDatabase.class, eObject); 
        VirtualDatabase entity = (VirtualDatabase) eObject;   
        return new Path(entity.getName());
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
