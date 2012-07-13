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
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;


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
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isVirtual(EObject eObject) { return virtual; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#isFunction(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isFunction(EObject eObject) { return function; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getParameters(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getParameters(EObject eObject) { return parameters; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getResult(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getResult(EObject eObject) { return result; }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getUpdateCount(org.eclipse.emf.ecore.EObject)
     * @since 5.5.3
     */
    @Override
	public int getUpdateCount(EObject eObject) {
        return updateCount;
    }
    
    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) {return (recordType == IndexConstants.RECORD_TYPE.CALLABLE ); }

    @Override
	public boolean isQueryable(EObject eObject) { return true; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(EObject eObject) { return name; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getFullName(EObject eObject) { return fullName; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getNameInSource(EObject eObject) { return nameInSource; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getObjectID(EObject eObject) { return uuid; }

    @Override
	public Object getParentObjectID(EObject eObject) { return parentUuid; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public IPath getPath(EObject eObject) { return path; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspect#getMetamodelEntity()
     */
    @Override
	public MetamodelEntity getMetamodelEntity() { return null; }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspect#getID()
     */
    @Override
	public String getID() { return null; }

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

}
