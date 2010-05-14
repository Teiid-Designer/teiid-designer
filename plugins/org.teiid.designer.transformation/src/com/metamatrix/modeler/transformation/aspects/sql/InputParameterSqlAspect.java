/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.transformation.TransformationPlugin;
import com.metamatrix.query.sql.ProcedureReservedWords;

/**
 * InputParameterSqlAspect
 */
public class InputParameterSqlAspect extends AbstractTransformationSqlAspect implements SqlColumnAspect {
    
    private static final String INPUT_SET_FULL_NAME = ProcedureReservedWords.INPUT;

    /**
     * Construct an instance of InputParameterSqlAspect.
     * @param entity
     */
    public InputParameterSqlAspect(MetamodelEntity entity) {
        super(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.COLUMN);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        return param.getName();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        return INPUT_SET_FULL_NAME + FULL_NAME_DELIMITER + this.getName(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.transformation.aspects.sql.AbstractTransformationSqlAspect#getParentFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    protected String getParentFullName(EObject eObject) {
        return INPUT_SET_FULL_NAME;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        return getName(eObject);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSelectable(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isUpdatable(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNullType(org.eclipse.emf.ecore.EObject)
     */
    public int getNullType(EObject eObject) {
        return 1;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isAutoIncrementable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isAutoIncrementable(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCaseSensitive(EObject eObject) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSigned(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSigned(EObject eObject) {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isCurrency(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCurrency(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isFixedLength(org.eclipse.emf.ecore.EObject)
     */
    public boolean isFixedLength(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isTranformationInputParameter(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isTranformationInputParameter(EObject eObject) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getSearchType(org.eclipse.emf.ecore.EObject)
     */
    public int getSearchType(EObject eObject) {
        return SearchabilityType.SEARCHABLE;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getDefaultValue(EObject eObject) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getMinValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getMinValue(EObject eObject) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getMaxValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getMaxValue(EObject eObject) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getFormat(org.eclipse.emf.ecore.EObject)
     */
    public String getFormat(EObject eObject) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    public int getLength(EObject eObject) {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    public int getScale(EObject eObject) {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    public int getRadix(EObject eObject) {
        return 10;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDistinctValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public int getDistinctValues(EObject eObject) {
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNullValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public int getNullValues(EObject eObject) {
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNativeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getNativeType(EObject eObject) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeName(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        final EObject datatype = param.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param,true);
        final String dtName = dtMgr.getName(datatype);
        return dtName == null ? "" : dtName; //$NON-NLS-1$
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetDatatype()
     * @since 4.2
     */
    public boolean canSetDatatype() {
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setDatatype(org.eclipse.emf.ecore.EObject, com.metamatrix.metamodels.core.Datatype)
     */
    public void setDatatype(EObject eObject, EObject datatype) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        param.setType(datatype);
    }
    
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    public EObject getDatatype(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        return param.getType();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeType(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        final EObject datatype = param.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param,true);
        return datatype == null ? "" : dtMgr.getRuntimeTypeName(datatype); //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeObjectID(EObject eObject) {
        CoreArgCheck.isInstanceOf(InputParameter.class, eObject); 
        InputParameter param = (InputParameter) eObject;       
        final EObject datatype = param.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param,true);
        return dtMgr.getUuidString(datatype); 
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    public int getPrecision(EObject eObject) {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getCharOctetLength(org.eclipse.emf.ecore.EObject)
     */
    public int getCharOctetLength(EObject eObject) {
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    public int getPosition(EObject eObject) {
        return 0;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    public boolean canSetLength() {
        return false;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     */
    public void setLength(EObject eObject, int length) {
        throw new UnsupportedOperationException(TransformationPlugin.Util.getString("InputParameterSqlAspect.Length_cannot_be_set_on_an_InputParameter_1")); //$NON-NLS-1$
    }

	/** 
	 * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
	 * @since 4.2
	 */
	public boolean canSetNullType() {
	    return false;
	}

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     */
    public void setNullType(EObject eObject, int nullType) {
        throw new UnsupportedOperationException(TransformationPlugin.Util.getString("InputParameterSqlAspect.NullType_cannot_be_set_on_an_InputParameter_2")); //$NON-NLS-1$
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {
        CoreArgCheck.isNotNull(sourceObject);
        SqlAspect columnAspect = AspectManager.getSqlAspect(sourceObject);
        CoreArgCheck.isInstanceOf(SqlColumnAspect.class, columnAspect);
        // get the source column type
        EObject srcType = ((SqlColumnAspect) columnAspect).getDatatype(sourceObject);
        setDatatype(targetObject, srcType);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
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
