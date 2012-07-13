/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.sql;

import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspectHelper;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.RelationalPlugin;


/**
 * ProcedureAspect
 */
public class ProcedureAspect extends RelationalEntityAspect implements SqlProcedureAspect {

    public ProcedureAspect(final MetamodelEntity entity) {
        super(entity);   
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isVirtual(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Procedure.class, eObject); 
        Procedure procedure = (Procedure) eObject;       
		try {    
			ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(procedure);
			return (ma != null && ma.getModelType().getValue() == ModelType.VIRTUAL);
		} catch(Exception e) {
			RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
		}

		return false;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#isFunction(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isFunction(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Procedure.class, eObject);
        Procedure proc = (Procedure) eObject;
        return proc.isFunction();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getParameters(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public List getParameters(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Procedure.class, eObject);
        Procedure proc = (Procedure) eObject;
        return proc.getParameters();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getResult(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getResult(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Procedure.class, eObject);
        Procedure proc = (Procedure) eObject;
        return proc.getResult();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#getUpdateCount(org.eclipse.emf.ecore.EObject)
     * @since 5.5.3
     */
    @Override
	public int getUpdateCount(EObject eObject) {
        CoreArgCheck.isInstanceOf(Procedure.class, eObject);
        return ((Procedure)eObject).getUpdateCount().getValue();
    }
    
    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.CALLABLE);
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public boolean isMappable(final EObject eObject, final int mappingType) {
        if(isVirtual(eObject)) {
            return (mappingType == SqlProcedureAspect.MAPPINGS.SQL_TRANSFORM);
        }
        return false;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean canAcceptTransformationSource(EObject target, EObject source) {
        CoreArgCheck.isInstanceOf(Procedure.class, target);
        CoreArgCheck.isNotNull(source);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        if(isVirtual(target)) {
            SqlAspect sourceAspect = SqlAspectHelper.getSqlAspect(source);            
            if(sourceAspect instanceof SqlTableAspect || sourceAspect instanceof SqlProcedureAspect) {
                return true;
            }
        }
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean canBeTransformationSource(EObject source, EObject target) {
        CoreArgCheck.isInstanceOf(Procedure.class, source);
        CoreArgCheck.isNotNull(target);
        // no object should be source of itself
        if(source == target) {
            return false;
        }
        SqlAspect targetAspect = SqlAspectHelper.getSqlAspect(target);            
        if(targetAspect instanceof SqlTableAspect) {
            return ((SqlTableAspect) targetAspect).isVirtual(target);
        } else if(targetAspect instanceof SqlProcedureAspect) {
            return ((SqlProcedureAspect) targetAspect).isVirtual(target);
        }
        return false;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(final EObject targetObject, final EObject sourceObject) {

    }

}
