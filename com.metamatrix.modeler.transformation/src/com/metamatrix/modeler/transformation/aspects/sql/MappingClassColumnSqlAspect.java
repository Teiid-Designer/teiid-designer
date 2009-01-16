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

package com.metamatrix.modeler.transformation.aspects.sql;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingRoot;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlValueHolder;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.transformation.TransformationPlugin;

/**
 * MappingClassColumnSqlAspect
 */
public class MappingClassColumnSqlAspect extends MappingClassObjectSqlAspect implements SqlColumnAspect {
    /**
     * Construct an instance of MappingClassColumnSqlAspect.
     * 
     */
    public MappingClassColumnSqlAspect(MetamodelEntity entity) {
        super(entity);   
    }
    
    /**
     * @see com.metamatrix.modeler.transformation.aspects.sql.MappingClassObjectSqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.COLUMN);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        return true;
    }
    
    private List getElementsFromMappingClassColumn(MappingClassColumn column) {
    	EObject mc = column.eContainer();
    	ArgCheck.isInstanceOf(MappingClass.class, mc);
    	EObject mcs = ((MappingClass)mc).eContainer();
    	ArgCheck.isInstanceOf(MappingClassSet.class, mcs);
    	EObject target = ((MappingClassSet)mcs).getTarget();
    	ArgCheck.isInstanceOf(XmlDocument.class, target);    	
    	XmlDocument document = (XmlDocument)target;
        Resource documentResource = document.eResource();
        
        // model contents for this resource 
        ModelContents mdlContents = new ModelContents(documentResource);
        Iterator contentIter = mdlContents.getTransformations(document).iterator();

        // get the mapping root associated with the transformation
        while(contentIter.hasNext()) {
            MappingRoot mappingRoot = (MappingRoot) contentIter.next();
            // if there is a mapping root
            if(mappingRoot != null && mappingRoot instanceof TreeMappingRoot) {
                for(Iterator mappingIter = mappingRoot.getNested().iterator();mappingIter.hasNext();) {
                    Mapping nestedMapping = (Mapping) mappingIter.next();
                    // mapping Class columns
                    List inputColumns = nestedMapping.getInputs();
                    // xml elements
                    List outputElements = nestedMapping.getOutputs();
                    if(inputColumns.contains(column)) {
                    	return outputElements;
                    } 
                }
            }
        }
    	return Collections.EMPTY_LIST;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSelectable(EObject eObject) {        
    	return true;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isUpdatable(EObject eObject) {
        return true;
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
    	return !isSelectable(eObject);
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
    	ArgCheck.isInstanceOf(MappingClassColumn.class, eObject);

    	List elements = getElementsFromMappingClassColumn((MappingClassColumn)eObject);
        
    	if (elements.size() != 1) {
    		return null;
    	}
    	
    	XmlValueHolder valueHolder = (XmlValueHolder)elements.get(0);
    	
    	if ((valueHolder.isValueDefault() || valueHolder.isValueFixed()) && valueHolder.getValue() != null) {
    		return valueHolder.getValue();
    	}
    	
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
        return 0;
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
        ArgCheck.isInstanceOf(MappingClassColumn.class, eObject); 
        MappingClassColumn column = (MappingClassColumn) eObject;       

        final EObject dataType = column.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column,true);
        final String dtName = dtMgr.getName(dataType);
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
        ArgCheck.isInstanceOf(MappingClassColumn.class, eObject); 
        MappingClassColumn column = (MappingClassColumn) eObject;       
        column.setType(datatype);
    }
    
    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     */
    public EObject getDatatype(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClassColumn.class, eObject); 
        MappingClassColumn column = (MappingClassColumn) eObject;       

        return column.getType();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeType(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClassColumn.class, eObject); 
        MappingClassColumn column = (MappingClassColumn) eObject;       
        final EObject datatype = column.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column,true);
        final String rtType = dtMgr.getRuntimeTypeName(datatype);
        return rtType == null ? "" : rtType; //$NON-NLS-1$
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeObjectID(EObject eObject) {
        ArgCheck.isInstanceOf(MappingClassColumn.class, eObject); 
        MappingClassColumn column = (MappingClassColumn) eObject;       
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column,true);
        return dtMgr.getUuidString(column.getType() );        
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
		ArgCheck.isInstanceOf(MappingClassColumn.class, eObject); 
		MappingClassColumn column = (MappingClassColumn) eObject;

		MappingClass mappingClass = column.getMappingClass();
		Assertion.isNotNull(mappingClass);
		// correct from '0' to '1' based position
		return mappingClass.getColumns().indexOf(column)+1;		
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
        throw new UnsupportedOperationException(TransformationPlugin.Util.getString("MappingClassColumnSqlAspect.Length_cannot_be_set_on_a_MappingClassColumn_1")); //$NON-NLS-1$
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
        throw new UnsupportedOperationException(TransformationPlugin.Util.getString("MappingClassColumnSqlAspect.NullType_cannot_be_set_on_a_MappingClassColumn_2")); //$NON-NLS-1$
    }
    
    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {
        ArgCheck.isNotNull(sourceObject);
        SqlAspect columnAspect = AspectManager.getSqlAspect(sourceObject);
        ArgCheck.isInstanceOf(SqlColumnAspect.class, columnAspect);
        // get the source column type
        EObject srcType = ((SqlColumnAspect) columnAspect).getDatatype(sourceObject);
        setDatatype(targetObject, srcType);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        ArgCheck.isInstanceOf(MappingClassColumn.class, eObject); 
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case TransformationPackage.MAPPING_CLASS_COLUMN__TYPE:
                    return true;
            }
        }
        return false;
    }

}
