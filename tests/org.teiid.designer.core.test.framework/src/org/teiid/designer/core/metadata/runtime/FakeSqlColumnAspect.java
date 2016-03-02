/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metadata.runtime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;


/**
 * FakeSqlColumnAspect
 */
public class FakeSqlColumnAspect implements SqlColumnAspect {
    public String name, fullName, nameInSource, datatypeName, runtimeType, datatypeUUID, format;
    public IPath path;
    public EObject datatype;
    public boolean selectable, updatable, autoIncrementable;
    public boolean caseSensitive, signed, currency, fixedLength, tranformationInputParameter;
    public String defaultValue;
    public Object minValue, maxValue, uuid, parentUuid;
    public int length, scale, precision, charOctetLength, radix, nullType, searchType, position, nullValues, distinctValues;
        
    @Override
	public int getCharOctetLength(EObject eObject) { return charOctetLength; }

    @Override
	public EObject getDatatype(EObject eObject) { return datatype; }
    
    @Override
	public String getDatatypeName(EObject eObject) { return datatypeName; }
    
    @Override
	public String getNativeType(EObject eObject) { return datatypeName; }    
    
    @Override
	public String getRuntimeType(EObject eObject) { return runtimeType; }
    
    @Override
	public String getDatatypeObjectID(EObject eObject) {return datatypeUUID;}
    
    @Override
	public String getDefaultValue(EObject eObject) { return defaultValue; }
    
    @Override
	public int getLength(EObject eObject) { return length; }
    
    @Override
	public Object getMaxValue(EObject eObject) { return maxValue; }
    
    @Override
	public Object getMinValue(EObject eObject) { return minValue; }
    
    @Override
	public String getFormat(EObject eObject) { return format; }        
    
    @Override
	public String getName(EObject eObject) { return name; }
    
    @Override
	public String getFullName(EObject eObject) { return fullName; }    

    @Override
	public String getNameInSource(EObject eObject) { return nameInSource; }
    
    @Override
	public IPath getPath(EObject eObject) { return path; }
    
    @Override
	public Object getObjectID(EObject eObject) { return uuid; }
    
    @Override
	public int getPrecision(EObject eObject) { return precision; }
    
    @Override
	public int getPosition(EObject eObject) {return position;}

    @Override
	public int getScale(EObject eObject) { return scale; }

    @Override
	public int getRadix(EObject eObject) { return radix; }    

    @Override
	public int getSearchType(EObject eObject) { return searchType; }
    
    @Override
	public int getDistinctValues(EObject eObject) { return distinctValues; }

    @Override
	public int getNullValues(EObject eObject) { return nullValues; }

    @Override
	public boolean isAutoIncrementable(EObject eObject) { return autoIncrementable; }
    
    @Override
	public boolean isCaseSensitive(EObject eObject) { return caseSensitive; }
    
    @Override
	public boolean isCurrency(EObject eObject) { return currency; }
    
    @Override
	public boolean isFixedLength(EObject eObject) { return fixedLength; }
    
    @Override
	public int getNullType(EObject eObject) { return nullType; }
    
    @Override
	public boolean isSelectable(EObject eObject) { return selectable; }
    
    @Override
	public boolean isSigned(EObject eObject) { return signed; }
    
    @Override
	public boolean isUpdatable(EObject eObject) { return updatable; }
    
    @Override
	public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.COLUMN ); } 

    @Override
	public boolean isQueryable(EObject eObject) { return true; }
    
    @Override
	public String getID() { return null; }
    
    @Override
	public MetamodelEntity getMetamodelEntity() { return null; }

    @Override
	public Object getParentObjectID(EObject eObject) { return parentUuid; }

    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {}

    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetDatatype()
     * @since 4.2
     */
    @Override
	public boolean canSetDatatype() {
        return true;
    }
    
    @Override
	public void setDatatype(EObject eObject, EObject newValue) { datatype = newValue; }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    @Override
	public boolean canSetLength() {
        return true;
    }
    
    @Override
	public void setLength(EObject eObject, int newValue) { length = newValue; }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
     * @since 4.2
     */
    @Override
	public boolean canSetNullType() {
        return true;
    }
    
    @Override
	public void setNullType(EObject eObject, int newValue) { nullType = newValue; }

    @Override
	public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) { return false; }

    @Override
	public boolean isTranformationInputParameter(EObject eObject) { return tranformationInputParameter; }

}
