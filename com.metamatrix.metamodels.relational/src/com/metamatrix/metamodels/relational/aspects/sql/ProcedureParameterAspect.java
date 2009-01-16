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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * ProcedureParameterAspect
 */
public class ProcedureParameterAspect extends RelationalEntityAspect implements SqlProcedureParameterAspect {
    
    public ProcedureParameterAspect(MetamodelEntity entity) {
        super(entity);   
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeName(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        final EObject dataType = proc.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(proc,true);
        final String dtName = dtMgr.getName(dataType);

        return dtName == null ? "" : dtMgr.getName(dataType); //$NON-NLS-1$
    }
    
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    public EObject getDatatype(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getType();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeType(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        final EObject datatype = proc.getType();
        return datatype == null ? "" : ModelerCore.getDatatypeManager(eObject,true).getRuntimeTypeName(datatype); //$NON-NLS-1$
    }


    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getDefaultValue(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getDefaultValue();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getType(org.eclipse.emf.ecore.EObject)
     */
    public int getType(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        DirectionKind direction = proc.getDirection();

        return convertDirectionKindToMetadataConstant(direction);
        //return direction.getValue();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    public int getLength(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getLength();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    public int getPrecision(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getPrecision();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    public int getRadix(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getRadix();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    public int getPosition(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        List params = proc.getProcedure().getParameters();
        // correct from '0' to '1' based position
        return params.indexOf(eObject) + 1;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    public int getScale(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getScale();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getNullType(org.eclipse.emf.ecore.EObject)
     */
    public int getNullType(EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        NullableType nullType = proc.getNullable();

        return convertNullableTypeToMetadataConstant(nullType);
        //return nullType.getValue();
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER);
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeObjectID(EObject eObject) {
        final EObject datatype = getDatatype(eObject);
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        return dtMgr.getUuidString(datatype);
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isOptional()
     */
    public boolean isOptional(EObject eObject) {
        return false;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject); 
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case RelationalPackage.PROCEDURE_PARAMETER__TYPE:
                    return true;
            }
        }
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetDatatype()
     * @since 4.2
     */
    public boolean canSetDatatype() {
        return true;
    }
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setDatatype(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public void setDatatype(EObject eObject, EObject datatype) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter procParam = (ProcedureParameter) eObject;
        procParam.setType(datatype);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetLength()
     * @since 4.2
     */
    public boolean canSetLength() {
        return true;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    public void setLength(EObject eObject, int length) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter procParam = (ProcedureParameter) eObject;
        procParam.setLength(length);
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetNullType()
     * @since 4.2
     */
    public boolean canSetNullType() {
        return true;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    public void setNullType(EObject eObject, int nullType) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter procParam = (ProcedureParameter) eObject;
        procParam.setNullable(convertMetadataConstantToNullableType(nullType));
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isInputParam()
     * @since 4.3
     */
    public boolean isInputParam(final EObject eObject) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter procParam = (ProcedureParameter) eObject;
        final DirectionKind dir = procParam.getDirection();
        return DirectionKind.IN_LITERAL == dir || DirectionKind.INOUT_LITERAL == dir;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setDirection(org.eclipse.emf.ecore.EObject, int)
     * @since 4.3
     */
    public void setDirection(EObject eObject,
                             int dir) {
        ArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        proc.setDirection(convertMetadataContantToDirectionKind(dir));
    }
}
