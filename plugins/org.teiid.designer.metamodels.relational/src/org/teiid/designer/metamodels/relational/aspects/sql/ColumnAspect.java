/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.AspectManager;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.ColumnSet;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.SearchabilityType;


/**
 * ColumnAspect
 *
 * @since 8.0
 */
public class ColumnAspect extends RelationalEntityAspect implements SqlColumnAspect {
    
    public ColumnAspect(MetamodelEntity entity) {
        super(entity);   
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSelectable(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isSelectable();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isUpdatable(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isUpdateable();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isNullable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getNullType(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;
        NullableType nullType = column.getNullable();

        return convertNullableTypeToMetadataConstant(nullType);
        //return nullType.getValue();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isAutoIncrementable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isAutoIncrementable(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isAutoIncremented();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isCaseSensitive(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isCaseSensitive();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isSigned(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isSigned(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isSigned();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isCurrency(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isCurrency(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isCurrency();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isFixedLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isFixedLength(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.isFixedLength();
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isTranformationInputParameter(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isTranformationInputParameter(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
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
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getSearchTye(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getSearchType(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        SearchabilityType searchType = column.getSearchability();

        return convertSearchabilityTypeToMetadataConstant(searchType);
        //return searchType.getValue();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDefaultValue(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getDefaultValue();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getMinValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getMinValue(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getMinimumValue();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getMaxValue(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Object getMaxValue(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getMaximumValue();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getFormat(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getFormat(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getFormat();
    }    

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getLength(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getScale(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getScale();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDistinctValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public int getDistinctValues(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getDistinctValueCount();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNullValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public int getNullValues(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getNullValueCount();
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
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        column.setType(datatype);
    }
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getDatatype(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        return column.getType();
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNativeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getNativeType(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        return column.getNativeType();
    }
    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDatatypeName(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        final EObject datatype = column.getType();
        if (datatype == null) {
            return CoreStringUtil.Constants.EMPTY_STRING;
        }
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column,true);
        final String dtName = dtMgr.getName(datatype);
        return dtName == null ? CoreStringUtil.Constants.EMPTY_STRING : dtName; 
    }
    
    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getRuntimeType(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        final EObject datatype = column.getType();
        return datatype == null ? 
                            CoreStringUtil.Constants.EMPTY_STRING : 
                            ModelerCore.getDatatypeManager(eObject,true).getRuntimeTypeName(datatype); 
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getDatatypeObjectID(EObject eObject) {
        final EObject datatype = getDatatype(eObject);
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        return dtMgr.getUuidString(datatype);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getPrecision(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getPrecision();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getPosition(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getPosition(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject; 
        ColumnSet columnSet = (ColumnSet) column.eContainer();
        // correct from '0' to '1' based position
        return columnSet.getColumns().indexOf(column)+1;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getCharOctetLength(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getCharOctetLength(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getLength();
    }
    
    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.COLUMN);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getRadix(EObject eObject) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        Column column = (Column) eObject;       
        return column.getRadix();
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    @Override
	public boolean canSetLength() {
        return true;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public void setLength(EObject eObject, int length) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;       
        column.setLength(length);
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
     * @since 4.2
     */
    @Override
	public boolean canSetNullType() {
        return true;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     */
    @Override
	public void setNullType(EObject eObject, int nullType) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
        final Column column = (Column) eObject;     
        column.setNullable(convertMetadataConstantToNullableType(nullType));
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {
        CoreArgCheck.isInstanceOf(Column.class, targetObject);
        CoreArgCheck.isNotNull(sourceObject);
        
        // look up the sqlAspect for the source
        MetamodelAspect aspect = AspectManager.getSqlAspect(sourceObject);       
        CoreArgCheck.isInstanceOf(SqlColumnAspect.class, aspect);
        
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
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    @Override
	public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        CoreArgCheck.isInstanceOf(Column.class, eObject); 
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
