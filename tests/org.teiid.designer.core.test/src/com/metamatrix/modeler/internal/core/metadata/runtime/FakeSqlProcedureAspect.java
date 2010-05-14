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
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;

/**
 * FakeSqlProcedureAspect
 */
public class FakeSqlProcedureAspect implements SqlProcedureAspect {

    public String name, fullName, nameInSource;
    public IPath path;
    public boolean virtual, function;
    public List parameters;
    public Object result, uuid, parentUuid;
    public int updateCount;

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    public boolean isVirtual(EObject eObject) { return virtual; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isFunction(org.eclipse.emf.ecore.EObject)
     */
    public boolean isFunction(EObject eObject) { return function; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getParameters(org.eclipse.emf.ecore.EObject)
     */
    public List getParameters(EObject eObject) { return parameters; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getResult(org.eclipse.emf.ecore.EObject)
     */
    public Object getResult(EObject eObject) { return result; }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getUpdateCount(org.eclipse.emf.ecore.EObject)
     * @since 5.5.3
     */
    public int getUpdateCount(EObject eObject) {
        return updateCount;
    }
    
    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {return (recordType == IndexConstants.RECORD_TYPE.CALLABLE ); }

    public boolean isQueryable(EObject eObject) { return true; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) { return name; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    public String getFullName(EObject eObject) { return fullName; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) { return nameInSource; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    public Object getObjectID(EObject eObject) { return uuid; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    public IPath getPath(EObject eObject) { return path; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect#getMetamodelEntity()
     */
    public MetamodelEntity getMetamodelEntity() { return null; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect#getID()
     */
    public String getID() { return null; }

    public boolean isMappable(EObject eObject, int mappingType) { return false; }
    
    public boolean canAcceptTransformationSource(EObject target, EObject source) { return false; }

    public boolean canBeTransformationSource(EObject source, EObject target) { return false; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
