/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.relational.aspects.sql;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;

/**
 * ProcedureAspect
 */
public class ProcedureAspect extends RelationalEntityAspect implements SqlProcedureAspect {

    public ProcedureAspect(final MetamodelEntity entity) {
        super(entity);   
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isVirtual(org.eclipse.emf.ecore.EObject)
     */
    public boolean isVirtual(final EObject eObject) {
        ArgCheck.isInstanceOf(Procedure.class, eObject); 
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isFunction(org.eclipse.emf.ecore.EObject)
     */
    public boolean isFunction(final EObject eObject) {
        ArgCheck.isInstanceOf(Procedure.class, eObject);
        Procedure proc = (Procedure) eObject;
        return proc.isFunction();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getParameters(org.eclipse.emf.ecore.EObject)
     */
    public List getParameters(final EObject eObject) {
        ArgCheck.isInstanceOf(Procedure.class, eObject);
        Procedure proc = (Procedure) eObject;
        return proc.getParameters();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getResult(org.eclipse.emf.ecore.EObject)
     */
    public Object getResult(final EObject eObject) {
        ArgCheck.isInstanceOf(Procedure.class, eObject);
        Procedure proc = (Procedure) eObject;
        return proc.getResult();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#getUpdateCount(org.eclipse.emf.ecore.EObject)
     * @since 5.5.3
     */
    public int getUpdateCount(EObject eObject) {
        ArgCheck.isInstanceOf(Procedure.class, eObject);
        return ((Procedure)eObject).getUpdateCount().getValue();
    }
    
    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.CALLABLE);
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect#isMappable(org.eclipse.emf.ecore.EObject, int)
     */
    public boolean isMappable(final EObject eObject, final int mappingType) {
        if(isVirtual(eObject)) {
            return (mappingType == SqlProcedureAspect.MAPPINGS.SQL_TRANSFORM);
        }
        return false;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canAcceptTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canAcceptTransformationSource(EObject target, EObject source) {
        ArgCheck.isInstanceOf(Procedure.class, target);
        ArgCheck.isNotNull(source);
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect#canBeTransformationSource(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean canBeTransformationSource(EObject source, EObject target) {
        ArgCheck.isInstanceOf(Procedure.class, source);
        ArgCheck.isNotNull(target);
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
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(final EObject targetObject, final EObject sourceObject) {

    }

}
