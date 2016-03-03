/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.sql;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.RelationalPackage;


/**
 * ProcedureParameterAspect
 *
 * @since 8.0
 */
public class ProcedureParameterAspect extends RelationalEntityAspect implements SqlProcedureParameterAspect {
    
    public ProcedureParameterAspect(MetamodelEntity entity) {
        super(entity);   
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDatatypeName(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        final EObject dataType = proc.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(proc,true);
        final String dtName = dtMgr.getName(dataType);

        return dtName == null ? "" : dtMgr.getName(dataType); //$NON-NLS-1$
    }
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getDatatype(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getType();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getRuntimeType(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        final EObject datatype = proc.getType();
        return datatype == null ? "" : ModelerCore.getDatatypeManager(eObject,true).getRuntimeTypeName(datatype); //$NON-NLS-1$
    }


    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDefaultValue(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getDefaultValue();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getType(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        DirectionKind direction = proc.getDirection();

        return convertDirectionKindToMetadataConstant(direction);
        //return direction.getValue();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getLength(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getLength();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getPrecision(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getPrecision();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getRadix(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getRadix();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getPosition(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        List params = proc.getProcedure().getParameters();
        // correct from '0' to '1' based position
        return params.indexOf(eObject) + 1;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getScale(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        return proc.getScale();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getNullType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getNullType(EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        NullableType nullType = proc.getNullable();

        return convertNullableTypeToMetadataConstant(nullType);
        //return nullType.getValue();
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER);
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDatatypeObjectID(EObject eObject) {
        final EObject datatype = getDatatype(eObject);
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        return dtMgr.getUuidString(datatype);
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isOptional()
     */
    @Override
	public boolean isOptional(EObject eObject) {
        return false;
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {

    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
	public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject); 
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
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetDatatype()
     * @since 4.2
     */
    @Override
	public boolean canSetDatatype() {
        return true;
    }
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setDatatype(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public void setDatatype(EObject eObject, EObject datatype) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter procParam = (ProcedureParameter) eObject;
        procParam.setType(datatype);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetLength()
     * @since 4.2
     */
    @Override
	public boolean canSetLength() {
        return true;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    @Override
	public void setLength(EObject eObject, int length) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter procParam = (ProcedureParameter) eObject;
        procParam.setLength(length);
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetNullType()
     * @since 4.2
     */
    @Override
	public boolean canSetNullType() {
        return true;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    @Override
	public void setNullType(EObject eObject, int nullType) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter procParam = (ProcedureParameter) eObject;
        procParam.setNullable(convertMetadataConstantToNullableType(nullType));
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isInputParam()
     * @since 4.3
     */
    @Override
	public boolean isInputParam(final EObject eObject) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter procParam = (ProcedureParameter) eObject;
        final DirectionKind dir = procParam.getDirection();
        return DirectionKind.IN_LITERAL == dir || DirectionKind.INOUT_LITERAL == dir;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setDirection(org.eclipse.emf.ecore.EObject, int)
     * @since 4.3
     */
    @Override
	public void setDirection(EObject eObject,
                             int dir) {
        CoreArgCheck.isInstanceOf(ProcedureParameter.class, eObject);
        ProcedureParameter proc = (ProcedureParameter) eObject;
        proc.setDirection(convertMetadataContantToDirectionKind(dir));
    }
}
