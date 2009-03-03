/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ColumnSet;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * ColumnAspect
 */
public class ColumnAspect extends RelationalEntityAspect implements SqlColumnAspect {
    
    public ColumnAspect(MetamodelEntity entity) {
        super(entity);   
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSelectable(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isSelectable();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isUpdatable(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isUpdateable();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isNullable(org.eclipse.emf.ecore.EObject)
     */
    public int getNullType(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;
        NullableType nullType = column.getNullable();

        return convertNullableTypeToMetadataConstant(nullType);
        //return nullType.getValue();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isAutoIncrementable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isAutoIncrementable(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isAutoIncremented();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCaseSensitive(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isCaseSensitive();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSigned(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSigned(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isSigned();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isCurrency(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCurrency(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isCurrency();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isFixedLength(org.eclipse.emf.ecore.EObject)
     */
    public boolean isFixedLength(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isFixedLength();
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isTranformationInputParameter(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean isTranformationInputParameter(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        
        // First, only process virtual models ...
        Resource eResource = column.eResource();
        if (eResource instanceof EmfResource) {
            EmfResource emfResource = (EmfResource)eResource;
            ModelAnnotation modelAnnot = emfResource.getModelAnnotation();
            if (modelAnnot != null && modelAnnot.getModelType() == ModelType.VIRTUAL_LITERAL) {
        		ColumnAspect columnAspect = (ColumnAspect)AspectManager.getSqlAspect(eObject);
            	if (!columnAspect.isSelectable(eObject) && !isAutoIncrementable(eObject)) {
            		return true;
            	}
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getSearchTye(org.eclipse.emf.ecore.EObject)
     */
    public int getSearchType(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        SearchabilityType searchType = column.getSearchability();

        return convertSearchabilityTypeToMetadataConstant(searchType);
        //return searchType.getValue();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getDefaultValue(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getDefaultValue();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getMinValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getMinValue(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getMinimumValue();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getMaxValue(org.eclipse.emf.ecore.EObject)
     */
    public Object getMaxValue(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getMaximumValue();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getFormat(org.eclipse.emf.ecore.EObject)
     */
    public String getFormat(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getFormat();
    }    

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    public int getLength(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getLength();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    public int getScale(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getScale();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDistinctValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public int getDistinctValues(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getDistinctValueCount();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNullValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public int getNullValues(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getNullValueCount();
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
        ArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        column.setType(datatype);
    }
    
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    public EObject getDatatype(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        return column.getType();
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getNativeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getNativeType(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        return column.getNativeType();
    }
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeName(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        final EObject datatype = column.getType();
        if (datatype == null) {
            return StringUtil.Constants.EMPTY_STRING;
        }
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column,true);
        final String dtName = dtMgr.getName(datatype);
        return dtName == null ? StringUtil.Constants.EMPTY_STRING : dtName; 
    }
    
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeType(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        final EObject datatype = column.getType();
        return datatype == null ? 
                            StringUtil.Constants.EMPTY_STRING : 
                            ModelerCore.getDatatypeManager(eObject,true).getRuntimeTypeName(datatype); 
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeObjectID(EObject eObject) {
        final EObject datatype = getDatatype(eObject);
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        return dtMgr.getUuidString(datatype);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    public int getPrecision(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getPrecision();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    public int getPosition(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject; 
        ColumnSet columnSet = (ColumnSet) column.eContainer();
        // correct from '0' to '1' based position
        return columnSet.getColumns().indexOf(column)+1;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getCharOctetLength(org.eclipse.emf.ecore.EObject)
     */
    public int getCharOctetLength(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getLength();
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.COLUMN);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    public int getRadix(EObject eObject) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getRadix();
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    public boolean canSetLength() {
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     */
    public void setLength(EObject eObject, int length) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        column.setLength(length);
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
     * @since 4.2
     */
    public boolean canSetNullType() {
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     */
    public void setNullType(EObject eObject, int nullType) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;     
        column.setNullable(convertMetadataConstantToNullableType(nullType));
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {
        ArgCheck.isInstanceOf(Column.class, targetObject);
        ArgCheck.isNotNull(sourceObject);
        
        // look up the sqlAspect for the source
        MetamodelAspect aspect = AspectManager.getSqlAspect(sourceObject);       
        ArgCheck.isInstanceOf(SqlColumnAspect.class, aspect);
        
        final Column tgtColumn = (Column) targetObject;
        final SqlColumnAspect srcColumnAspect = (SqlColumnAspect) aspect;

        // set all the properties by looking up the sql aspect        
        tgtColumn.setAutoIncremented(srcColumnAspect.isAutoIncrementable(sourceObject));
        tgtColumn.setCaseSensitive(srcColumnAspect.isCaseSensitive(sourceObject));
        tgtColumn.setCurrency(srcColumnAspect.isCurrency(sourceObject));
        tgtColumn.setFixedLength(srcColumnAspect.isFixedLength(sourceObject));
        tgtColumn.setFormat(srcColumnAspect.getFormat(sourceObject));
        tgtColumn.setLength(srcColumnAspect.getLength(sourceObject));
        tgtColumn.setPrecision(srcColumnAspect.getPrecision(sourceObject));
        tgtColumn.setUpdateable(srcColumnAspect.isUpdatable(sourceObject));
        tgtColumn.setSigned(srcColumnAspect.isSigned(sourceObject));
        tgtColumn.setSelectable(srcColumnAspect.isSelectable(sourceObject));
        tgtColumn.setRadix(srcColumnAspect.getRadix(sourceObject));
        tgtColumn.setPrecision(srcColumnAspect.getPrecision(sourceObject));
        tgtColumn.setScale(srcColumnAspect.getScale(sourceObject));
        tgtColumn.setType(srcColumnAspect.getDatatype(sourceObject));

        tgtColumn.setMaximumValue(getString(srcColumnAspect.getMaxValue(sourceObject)));
        tgtColumn.setMinimumValue(getString(srcColumnAspect.getMinValue(sourceObject)));        
        tgtColumn.setDefaultValue(getString(srcColumnAspect.getDefaultValue(sourceObject)));
        // set the searchtype for the target
        int searchType = srcColumnAspect.getSearchType(sourceObject);
        tgtColumn.setSearchability(convertMetadataConstantToSearchabilityType(searchType));
        // set the nulltype for the target
        int nullType = srcColumnAspect.getNullType(sourceObject);
        tgtColumn.setNullable(convertMetadataConstantToNullableType(nullType));
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        ArgCheck.isInstanceOf(Column.class, eObject); 
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case RelationalPackage.COLUMN__TYPE:
                    return true;
            }
        }
        return false;
    }

    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================
    
    private String getString(Object obj) {
        if(obj != null) {
            return obj.toString();    
        }
        return null;
    }

}
