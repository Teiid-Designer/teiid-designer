/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;

/**
 * FakeSqlColumnAspect
 */
public class FakeSqlColumnAspect implements SqlColumnAspect {
    public String name, fullName, nameInSource, datatypeName, runtimeType, datatypeUUID, format;
    public IPath path;
    public EObject datatype;
    public boolean selectable, updatable, autoIncrementable;
    public boolean caseSensitive, signed, currency, fixedLength, tranformationInputParameter;
    public Object defaultValue, minValue, maxValue, uuid, parentUuid;
    public int length, scale, precision, charOctetLength, radix, nullType, searchType, position, nullValues, distinctValues;
        
    public int getCharOctetLength(EObject eObject) { return charOctetLength; }

    public EObject getDatatype(EObject eObject) { return datatype; }
    
    public String getDatatypeName(EObject eObject) { return datatypeName; }
    
    public String getNativeType(EObject eObject) { return datatypeName; }    
    
    public String getRuntimeType(EObject eObject) { return runtimeType; }
    
    public String getDatatypeObjectID(EObject eObject) {return datatypeUUID;}
    
    public Object getDefaultValue(EObject eObject) { return defaultValue; }
    
    public int getLength(EObject eObject) { return length; }
    
    public Object getMaxValue(EObject eObject) { return maxValue; }
    
    public Object getMinValue(EObject eObject) { return minValue; }
    
    public String getFormat(EObject eObject) { return format; }        
    
    public String getName(EObject eObject) { return name; }
    
    public String getFullName(EObject eObject) { return fullName; }    

    public String getNameInSource(EObject eObject) { return nameInSource; }
    
    public IPath getPath(EObject eObject) { return path; }
    
    public Object getObjectID(EObject eObject) { return uuid; }
    
    public int getPrecision(EObject eObject) { return precision; }
    
    public int getPosition(EObject eObject) {return position;}

    public int getScale(EObject eObject) { return scale; }

    public int getRadix(EObject eObject) { return radix; }    

    public int getSearchType(EObject eObject) { return searchType; }
    
    public int getDistinctValues(EObject eObject) { return distinctValues; }

    public int getNullValues(EObject eObject) { return nullValues; }

    public boolean isAutoIncrementable(EObject eObject) { return autoIncrementable; }
    
    public boolean isCaseSensitive(EObject eObject) { return caseSensitive; }
    
    public boolean isCurrency(EObject eObject) { return currency; }
    
    public boolean isFixedLength(EObject eObject) { return fixedLength; }
    
    public int getNullType(EObject eObject) { return nullType; }
    
    public boolean isSelectable(EObject eObject) { return selectable; }
    
    public boolean isSigned(EObject eObject) { return signed; }
    
    public boolean isUpdatable(EObject eObject) { return updatable; }
    
    public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.COLUMN ); } 

    public boolean isQueryable(EObject eObject) { return true; }
    
    public String getID() { return null; }
    
    public MetamodelEntity getMetamodelEntity() { return null; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }

    public void updateObject(EObject targetObject, EObject sourceObject) {}

    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetDatatype()
     * @since 4.2
     */
    public boolean canSetDatatype() {
        return true;
    }
    
    public void setDatatype(EObject eObject, EObject newValue) { datatype = newValue; }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    public boolean canSetLength() {
        return true;
    }
    
    public void setLength(EObject eObject, int newValue) { length = newValue; }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
     * @since 4.2
     */
    public boolean canSetNullType() {
        return true;
    }
    
    public void setNullType(EObject eObject, int newValue) { nullType = newValue; }

    public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) { return false; }

    public boolean isTranformationInputParameter(EObject eObject) { return tranformationInputParameter; }

}
