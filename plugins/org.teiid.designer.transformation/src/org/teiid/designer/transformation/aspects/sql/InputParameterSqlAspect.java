/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlAspect;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.metamodels.transformation.TransformationPackage;
import org.teiid.designer.transformation.TransformationPlugin;

/**
 * InputParameterSqlAspect
 *
 * @since 8.0
 */
public class InputParameterSqlAspect extends AbstractTransformationSqlAspect implements SqlColumnAspect {
    
    private static final String INPUT_SET_FULL_NAME = "INPUTS"; //$NON-NLS-1$

    /**
     * Construct an instance of InputParameterSqlAspect.
     * @param entity
     */
    public InputParameterSqlAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.COLUMN);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isQueryable(final EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        return param.getName();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        return INPUT_SET_FULL_NAME + FULL_NAME_DELIMITER + this.getName(eObject);
    }

    /** 
     * @see org.teiid.designer.transformation.aspects.sql.AbstractTransformationSqlAspect#getParentFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    protected String getParentFullName(EObject eObject) {
        return INPUT_SET_FULL_NAME;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getNameInSource(EObject eObject) {
        return getName(eObject);
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSelectable(EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isUpdatable(EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNullType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getNullType(EObject eObject) {
        return 1;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isAutoIncrementable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isAutoIncrementable(EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isCaseSensitive(EObject eObject) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isSigned(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSigned(EObject eObject) {
        return true;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isCurrency(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isCurrency(EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isFixedLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isFixedLength(EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isTranformationInputParameter(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isTranformationInputParameter(EObject eObject) {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getSearchType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getSearchType(EObject eObject) {
        return SearchabilityType.SEARCHABLE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDefaultValue(EObject eObject) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getMinValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getMinValue(EObject eObject) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getMaxValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getMaxValue(EObject eObject) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getFormat(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getFormat(EObject eObject) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getLength(EObject eObject) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getScale(EObject eObject) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getRadix(EObject eObject) {
        return 10;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDistinctValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public int getDistinctValues(EObject eObject) {
        return 0;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNullValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public int getNullValues(EObject eObject) {
        return 0;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNativeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getNativeType(EObject eObject) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDatatypeName(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        final EObject datatype = param.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param,true);
        final String dtName = dtMgr.getName(datatype);
        return dtName == null ? "" : dtName; //$NON-NLS-1$
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetDatatype()
     * @since 4.2
     */
    @Override
	public boolean canSetDatatype() {
        return true;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setDatatype(org.eclipse.emf.ecore.EObject, org.teiid.designer.metamodels.core.Datatype)
     */
    @Override
	public void setDatatype(EObject eObject, EObject datatype) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        param.setType(datatype);
    }
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getDatatype(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        return param.getType();
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getRuntimeType(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        final EObject datatype = param.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param,true);
        return datatype == null ? "" : dtMgr.getRuntimeTypeName(datatype); //$NON-NLS-1$
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDatatypeObjectID(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        final EObject datatype = param.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param,true);
        return dtMgr.getUuidString(datatype); 
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getPrecision(EObject eObject) {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getCharOctetLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getCharOctetLength(EObject eObject) {
        return 0;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getPosition(EObject eObject) {
        return 0;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    @Override
	public boolean canSetLength() {
        return false;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public void setLength(EObject eObject, int length) {
        throw new UnsupportedOperationException(TransformationPlugin.Util.getString("InputParameterSqlAspect.Length_cannot_be_set_on_an_InputParameter_1")); //$NON-NLS-1$
    }

	/** 
	 * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
	 * @since 4.2
	 */
	@Override
	public boolean canSetNullType() {
	    return false;
	}

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public void setNullType(EObject eObject, int nullType) {
        throw new UnsupportedOperationException(TransformationPlugin.Util.getString("InputParameterSqlAspect.NullType_cannot_be_set_on_an_InputParameter_2")); //$NON-NLS-1$
    }

    /*
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {
        CoreArgCheck.isNotNull(sourceObject);
        SqlAspect columnAspect = AspectManager.getSqlAspect(sourceObject);
        CoreArgCheck.isInstanceOf(SqlColumnAspect.class, columnAspect);
        // get the source column type
        EObject srcType = ((SqlColumnAspect) columnAspect).getDatatype(sourceObject);
        setDatatype(targetObject, srcType);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
	public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case TransformationPackage.INPUT_PARAMETER__TYPE:
                    return true;
            }
        }
        return false;
    }

}
