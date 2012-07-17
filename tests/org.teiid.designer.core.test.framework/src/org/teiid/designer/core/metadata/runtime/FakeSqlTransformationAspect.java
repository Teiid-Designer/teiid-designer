/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metadata.runtime;

import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.index.IndexingContext;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationInfo;


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
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationTypes(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String[] getTransformationTypes(EObject eObject) {
        return transTypes;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformation(org.eclipse.emf.ecore.EObject, java.lang.String)
     */
    @Override
	public String getTransformation(EObject eObject, String type) {
        if(type.equals(SqlTransformationAspect.Types.SELECT)) {
            return "Select * from MyTable where table.element < 1 and table.element2 > 100";     //$NON-NLS-1$
        }
        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformationInfo(org.eclipse.emf.ecore.EObject, org.teiid.designer.core.validation.ValidationContext, java.lang.String)
     */
    @Override
	public SqlTransformationInfo getTransformationInfo(EObject eObject, IndexingContext context, String type) {
        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getTransformedObject(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getTransformedObject(EObject eObject) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getInputObjects(EObject eObject) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getNestedInputObjects(EObject eObject) {
        return Collections.EMPTY_LIST;
    }
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getNestedOutputObjects(EObject eObject) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedInputsForOutput(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getNestedInputsForOutput(EObject eObject,
                                         EObject output) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getNestedOutputsForInput(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getNestedOutputsForInput(EObject eObject,
                                         EObject input) {
        return Collections.EMPTY_LIST;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#getOutputObjects(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public List getOutputObjects(EObject eObject) {
        return Collections.EMPTY_LIST;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isDeleteAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isDeleteAllowed(EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isInsertAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isInsertAllowed(EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTransformationAspect#isUpdateAllowed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isUpdateAllowed(EObject eObject) {
        return false;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) { 
        return ((recordType == IndexConstants.RECORD_TYPE.SELECT_TRANSFORM) ||
                 (recordType == IndexConstants.RECORD_TYPE.INSERT_TRANSFORM) ||
                 (recordType == IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM) ||
                 (recordType == IndexConstants.RECORD_TYPE.DELETE_TRANSFORM) ||
                 (recordType == IndexConstants.RECORD_TYPE.PROC_TRANSFORM));
    } 

    @Override
	public boolean isQueryable(EObject eObject) { return true; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(EObject eObject) {
        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getFullName(EObject eObject) {
        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getNameInSource(EObject eObject) {
        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getObjectID(EObject eObject) {
        return null;
    }

    @Override
	public Object getParentObjectID(EObject eObject) { return parentUuid; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public IPath getPath(EObject eObject) {
        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspect#getMetamodelEntity()
     */
    @Override
	public MetamodelEntity getMetamodelEntity() {
        return null;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspect#getID()
     */
    @Override
	public String getID() {
        return null;
    }
    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

}
