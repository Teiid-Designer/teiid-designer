/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationInfo;

/**
 * FakeSqlTransformationAspect
 */
public class FakeSqlTransformationAspect implements SqlTransformationAspect {
    
    public String name, fullName, nameInSource;
    public IPath path;
    public String transTypes[] = {SqlTransformationAspect.Types.DELETE, SqlTransformationAspect.Types.INSERT, SqlTransformationAspect.Types.MAPPING,
                                SqlTransformationAspect.Types.PROCEDURE, SqlTransformationAspect.Types.SELECT, SqlTransformationAspect.Types.UPDATE};
    public Object uuid, parentUuid;
    

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationTypes(org.eclipse.emf.ecore.EObject)
     */
    public String[] getTransformationTypes(EObject eObject) {
        return transTypes;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformation(org.eclipse.emf.ecore.EObject, java.lang.String)
     */
    public String getTransformation(EObject eObject, String type) {
        if(type.equals(SqlTransformationAspect.Types.SELECT)) {
            return "Select * from MyTable where table.element < 1 and table.element2 > 100";     //$NON-NLS-1$
        }
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationInfo(org.eclipse.emf.ecore.EObject, com.metamatrix.modeler.core.validation.ValidationContext, java.lang.String)
     */
    public SqlTransformationInfo getTransformationInfo(EObject eObject, IndexingContext context, String type) {
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformedObject(org.eclipse.emf.ecore.EObject)
     */
    public Object getTransformedObject(EObject eObject) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getInputObjects(EObject eObject) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedInputObjects(EObject eObject) {
        return Collections.EMPTY_LIST;
    }
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedOutputObjects(EObject eObject) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputsForOutput(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedInputsForOutput(EObject eObject,
                                         EObject output) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputsForInput(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getNestedOutputsForInput(EObject eObject,
                                         EObject input) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#getOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public List getOutputObjects(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isDeleteAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isDeleteAllowed(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isInsertAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isInsertAllowed(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTransformationAspect#isUpdateAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isUpdateAllowed(EObject eObject) {
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) { 
        return ((recordType == IndexConstants.RECORD_TYPE.SELECT_TRANSFORM) ||
                 (recordType == IndexConstants.RECORD_TYPE.INSERT_TRANSFORM) ||
                 (recordType == IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM) ||
                 (recordType == IndexConstants.RECORD_TYPE.DELETE_TRANSFORM) ||
                 (recordType == IndexConstants.RECORD_TYPE.PROC_TRANSFORM));
    } 

    public boolean isQueryable(EObject eObject) { return true; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    public String getFullName(EObject eObject) {
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    public Object getObjectID(EObject eObject) {
        return null;
    }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    public IPath getPath(EObject eObject) {
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect#getMetamodelEntity()
     */
    public MetamodelEntity getMetamodelEntity() {
        return null;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect#getID()
     */
    public String getID() {
        return null;
    }
    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
