/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metadata.runtime;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect;
import org.teiid.designer.core.types.EnterpriseDatatypeInfo;


/**
 * FakeSqlColumnAspect
 */
public class FakeSqlDatatypeAspect implements SqlDatatypeAspect {
    public IPath path;
    public int length, precisionLength, scale, radix;
    public boolean isSigned, isAutoIncrement, isCaseSensitive, isBuiltInType, isURType, isSimpleType, isComplexType;
    public short type, searchType, nullType, varietyType;
    public String name, fullName, nameInSource, javaClassName, runtimeTypeName, datatypeID, basetypeID, primitiveTypeID, description;
    public List varietyProps;
    public Object uuid, parentUuid;

    @Override
	public String getBasetypeID(EObject eObject) { return basetypeID; }

    @Override
	public String getPrimitiveTypeID(EObject eObject) { return primitiveTypeID; }
    
    @Override
	public String getDatatypeID(EObject eObject) { return datatypeID; }
    
    @Override
	public String getJavaClassName(EObject eObject) { return javaClassName; }
    
    @Override
	public int getLength(EObject eObject) { return length;}
    
    @Override
	public short getNullType(EObject eObject) { return nullType; }
    
    @Override
	public int getPrecisionLength(EObject eObject) { return precisionLength; }
    
    @Override
	public int getRadix(EObject eObject) { return radix; }
    
    @Override
	public String getRuntimeTypeName(EObject eObject) { return runtimeTypeName; }
    
    @Override
	public int getScale(EObject eObject) {return scale; }
    
    @Override
	public short getSearchType(EObject eObject) { return searchType; }
    
    @Override
	public short getType(EObject eObject) { return type; }
    
    @Override
	public List getVarietyProps(EObject eObject) { return varietyProps; }
    
    @Override
	public short getVarietyType(EObject eObject) { return varietyType; }
    
    @Override
	public boolean isAutoIncrement(EObject eObject) {  return isAutoIncrement; }
    
    @Override
	public boolean isCaseSensitive(EObject eObject) { return isCaseSensitive; }
    
    @Override
	public boolean isSigned(EObject eObject) { return isSigned; }
    
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
	public Object getParentObjectID(EObject eObject) { return parentUuid; }
    
    @Override
	public String getID() { return null; }
    
    @Override
	public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.DATATYPE ); } 

    @Override
	public boolean isQueryable(EObject eObject) { return true; }

    @Override
	public MetamodelEntity getMetamodelEntity() { return null; }

    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) { }
    
    @Override
	public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) { return false; }

    @Override
	public Object getBasetype(EObject eObject) { return null; }

    @Override
	public Object getPrimitiveType(EObject eObject) { return null; }

    @Override
	public Map getEnterpriseExtensionsMap(EObject eObject) { return Collections.EMPTY_MAP; }

    @Override
	public boolean isBuiltInDatatype(EObject eObject) { return isBuiltInType; }

    @Override
	public boolean isComplexDatatype(EObject eObject) { return isComplexType; }

    @Override
	public boolean isSimpleDatatype(EObject eObject) { return isSimpleType; }

    @Override
	public boolean isURType(EObject eObject) { return isURType; }

    @Override
	public String getUuidString(EObject eObject) { return (uuid != null ? uuid.toString() : null); }

    @Override
	public String getDescription(EObject eObject) { return description; }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect#getRuntimeTypeFixed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public Boolean getRuntimeTypeFixed(EObject eObject) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect#isEnterpriseDataType(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isEnterpriseDataType(EObject type) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect#setEnterpriseDataAttributes(org.eclipse.xsd.XSDSimpleTypeDefinition, org.teiid.designer.core.types.EnterpriseDatatypeInfo)
     * @since 4.3
     */
    @Override
	public void setEnterpriseDataAttributes(XSDSimpleTypeDefinition type, EnterpriseDatatypeInfo edtInfo) {
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect#unSetEnterpriseDataAttributes(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    @Override
	public void unSetEnterpriseDataAttributes(XSDSimpleTypeDefinition type) {
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect#getEnterpriseDatatypeInfo(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    @Override
	public EnterpriseDatatypeInfo getEnterpriseDatatypeInfo(XSDSimpleTypeDefinition type) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlDatatypeAspect#setBasetype(org.eclipse.xsd.XSDSimpleTypeDefinition, org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    @Override
	public void setBasetype(XSDSimpleTypeDefinition simpleType, XSDSimpleTypeDefinition baseType) {
    }

}
