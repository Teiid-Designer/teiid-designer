/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;

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

    public String getBasetypeID(EObject eObject) { return basetypeID; }

    public String getPrimitiveTypeID(EObject eObject) { return primitiveTypeID; }
    
    public String getDatatypeID(EObject eObject) { return datatypeID; }
    
    public String getJavaClassName(EObject eObject) { return javaClassName; }
    
    public int getLength(EObject eObject) { return length;}
    
    public short getNullType(EObject eObject) { return nullType; }
    
    public int getPrecisionLength(EObject eObject) { return precisionLength; }
    
    public int getRadix(EObject eObject) { return radix; }
    
    public String getRuntimeTypeName(EObject eObject) { return runtimeTypeName; }
    
    public int getScale(EObject eObject) {return scale; }
    
    public short getSearchType(EObject eObject) { return searchType; }
    
    public short getType(EObject eObject) { return type; }
    
    public List getVarietyProps(EObject eObject) { return varietyProps; }
    
    public short getVarietyType(EObject eObject) { return varietyType; }
    
    public boolean isAutoIncrement(EObject eObject) {  return isAutoIncrement; }
    
    public boolean isCaseSensitive(EObject eObject) { return isCaseSensitive; }
    
    public boolean isSigned(EObject eObject) { return isSigned; }
    
    public String getName(EObject eObject) { return name; }
    
    public String getFullName(EObject eObject) { return fullName; }    
    
    public String getNameInSource(EObject eObject) { return nameInSource; }
    
    public IPath getPath(EObject eObject) { return path; }
    
    public Object getObjectID(EObject eObject) { return uuid; }

    public Object getParentObjectID(EObject eObject) { return parentUuid; }
    
    public String getID() { return null; }
    
    public boolean isRecordType(char recordType) { return (recordType == IndexConstants.RECORD_TYPE.DATATYPE ); } 

    public boolean isQueryable(EObject eObject) { return true; }

    public MetamodelEntity getMetamodelEntity() { return null; }

    public void updateObject(EObject targetObject, EObject sourceObject) { }
    
    public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) { return false; }

    public Object getBasetype(EObject eObject) { return null; }

    public Object getPrimitiveType(EObject eObject) { return null; }

    public Map getEnterpriseExtensionsMap(EObject eObject) { return Collections.EMPTY_MAP; }

    public boolean isBuiltInDatatype(EObject eObject) { return isBuiltInType; }

    public boolean isComplexDatatype(EObject eObject) { return isComplexType; }

    public boolean isSimpleDatatype(EObject eObject) { return isSimpleType; }

    public boolean isURType(EObject eObject) { return isURType; }

    public String getUuidString(EObject eObject) { return (uuid != null ? uuid.toString() : null); }

    public String getDescription(EObject eObject) { return description; }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getRuntimeTypeFixed(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Boolean getRuntimeTypeFixed(EObject eObject) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#isEnterpriseDataType(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean isEnterpriseDataType(EObject type) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#setEnterpriseDataAttributes(org.eclipse.xsd.XSDSimpleTypeDefinition, com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo)
     * @since 4.3
     */
    public void setEnterpriseDataAttributes(XSDSimpleTypeDefinition type, EnterpriseDatatypeInfo edtInfo) {
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#unSetEnterpriseDataAttributes(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public void unSetEnterpriseDataAttributes(XSDSimpleTypeDefinition type) {
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getEnterpriseDatatypeInfo(org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public EnterpriseDatatypeInfo getEnterpriseDatatypeInfo(XSDSimpleTypeDefinition type) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#setBasetype(org.eclipse.xsd.XSDSimpleTypeDefinition, org.eclipse.xsd.XSDSimpleTypeDefinition)
     * @since 4.3
     */
    public void setBasetype(XSDSimpleTypeDefinition simpleType, XSDSimpleTypeDefinition baseType) {
    }

}
