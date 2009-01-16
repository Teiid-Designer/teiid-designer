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

package com.metamatrix.metamodels.xsd.aspects.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.core.id.IDGenerator;
import com.metamatrix.core.id.InvalidIDException;
import com.metamatrix.core.id.UUID;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.xsd.XsdConstants;
import com.metamatrix.metamodels.xsd.XsdPlugin;
import com.metamatrix.metamodels.xsd.XsdUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.core.types.EnterpriseDatatypeInfo;

/**
 * XsdSimpleTypeDefinitionAspect
 */
public class XsdSimpleTypeDefinitionAspect extends AbstractMetamodelAspect implements SqlDatatypeAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;
    
    public static final URI BUILTIN_DATATYPES_URI = URI.createURI(DatatypeConstants.BUILTIN_DATATYPES_URI);
    public static final String UUID_ATTRIBUTE_NAME  = "UUID"; //$NON-NLS-1$
    public static final String RUNTIME_TYPE_ATTRIBUTE_NAME  = "runtimeDataType"; //$NON-NLS-1$
    public static final String RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME  = "runtimeDataTypeFixed"; //$NON-NLS-1$

    // platform:/plugin/org.eclipse.xsd_1.1.1/cache/www.w3.org/2001/MagicXMLSchema.xsd#//anySimpleType;XSDSimpleTypeDefinition=1
    protected static final String ANY_SIMPLE_TYPE_URI_STRING = ModelerCore.XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI + "#//anySimpleType;XSDSimpleTypeDefinition=1"; //$NON-NLS-1$
    protected static final URI ANY_SIMPLE_TYPE_URI = URI.createURI(ANY_SIMPLE_TYPE_URI_STRING); 
    
    // Map, keyed on datatype name, of MetaMatrix's built-in datatypes as defined in the modeler.sdt plugin.
    private Map mmDatatypeMap; 
    
    // References to the ur-types
    private EObject anySimpleType;

    public XsdSimpleTypeDefinitionAspect(MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
        init();
    }
    
    /**
     * Load a map of MetaMatrix built-in datatype instances
     * to use when retrieving UUID or runtime type information.
     * Since there is only one XsdSimpleTypeDefinitionAspect
     * ever created the overhead of creating the map is acceptable.
     */
    private void init() {
        this.mmDatatypeMap   = new HashMap();
        this.initializeMmDatatypeMap();
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject;   
        return entity.getName();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject;       
        return entity.getName();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getObjectID(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        // If the datatype is a built-in datatype then use the MetaMatrix 
        // instance since the instance found in the org.eclipse.xsd plugin
        // does not have UUID information.  If the datatype is not a
        // built-in datatype then the original EObject reference is used.
        EObject type = this.getMmType(eObject);
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) type;     
        XsdUtil.checkForEnterpriseConversion(entity);

        String uuidString = null;
        if (isEnterpriseDataType(entity)) {
            uuidString = getEnterpriseAttributeValue(entity, UUID_ATTRIBUTE_NAME);
        }
        if (!StringUtil.isEmpty(uuidString)) {
            try {
                return IDGenerator.getInstance().stringToObject(uuidString,UUID.PROTOCOL);
            } catch (InvalidIDException e) {
                // Log error and then proceed by trying to get the UUID in other ways
                final String msg = XsdPlugin.Util.getString("XsdSimpleTypeDefinitionAspect.error_parsing_objectid",uuidString);  //$NON-NLS-1$
                XsdPlugin.Util.log(IStatus.ERROR, e, msg);
            }
        }  
        return super.getObjectID(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getParentObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getParentObjectID(EObject eObject) {
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public IPath getPath(final EObject eObject) {
        return null;
    }    

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject; 
        return this.getURI(entity);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.DATATYPE);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        // If the EObject is a simple datatype but it is not a global datatype then return false
        if ((eObject instanceof XSDSimpleTypeDefinition) && !(eObject.eContainer() instanceof XSDSchema)) {
            return false;
        }                                
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getLength(org.eclipse.emf.ecore.EObject)
     */
    public int getLength(EObject eObject) {
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getPrecisionLength(org.eclipse.emf.ecore.EObject)
     */
    public int getPrecisionLength(EObject eObject) {
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getRadix(org.eclipse.emf.ecore.EObject)
     */
    public int getRadix(EObject eObject) {
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getScale(org.eclipse.emf.ecore.EObject)
     */
    public int getScale(EObject eObject) {
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#isAutoIncrement(org.eclipse.emf.ecore.EObject)
     */
    public boolean isAutoIncrement(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     */
    public boolean isCaseSensitive(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#isSigned(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSigned(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getType(org.eclipse.emf.ecore.EObject)
     */
    public short getType(EObject eObject) {
        return MetadataConstants.DATATYPE_TYPES.USER_DEFINED;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getSearchType(org.eclipse.emf.ecore.EObject)
     */
    public short getSearchType(EObject eObject) {
        return MetadataConstants.SEARCH_TYPES.SEARCHABLE;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getNullType(org.eclipse.emf.ecore.EObject)
     */
    public short getNullType(EObject eObject) {
        return MetadataConstants.NULL_TYPES.NOT_NULL;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getUuidString(org.eclipse.emf.ecore.EObject)
     */
    public String getUuidString(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        // If the datatype is a built-in datatype then use the MetaMatrix 
        // instance since the instance found in the org.eclipse.xsd plugin
        // does not have UUID information.  If the datatype is not a
        // built-in datatype then the original EObject reference is used.
        EObject type = this.getMmType(eObject);
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) type;     
        XsdUtil.checkForEnterpriseConversion(entity);
        String uuid = null;
        if (isEnterpriseDataType(entity)) {
            uuid = getEnterpriseAttributeValue(entity, UUID_ATTRIBUTE_NAME); 
        }
        return uuid;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getJavaClassName(org.eclipse.emf.ecore.EObject)
     */
    public String getJavaClassName(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject;  
        // Retrieve the runtime type name from the appInfo
        String runtimeTypeName = this.getRuntimeTypeName(entity);
        if (runtimeTypeName != null) {
            Class javaClass = DataTypeManager.getDataTypeClass(runtimeTypeName);
            if (javaClass != null) {
                return javaClass.getName();
            }
        }
        // Return Object as the default
        return Object.class.getName();   
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getRuntimeTypeName(org.eclipse.emf.ecore.EObject)
     */
    public String getRuntimeTypeName(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        // If the datatype is a ur-type return a predefined runtime type
        if (this.isURType(eObject)) {
            return DatatypeConstants.RuntimeTypeNames.OBJECT;
        }
        
        // If the datatype is a built-in datatype then use the MetaMatrix 
        // instance since the instance found in the org.eclipse.xsd plugin
        // does not have runtime type information.  If the datatype is not a
        // built-in datatype then the original EObject reference is used.
        EObject type = this.getMmType(eObject);
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) type; 
        XsdUtil.checkForEnterpriseConversion(entity);
        
        String runtimeType = null;
        if (isEnterpriseDataType(entity)) {
            runtimeType = getEnterpriseAttributeValue(entity, RUNTIME_TYPE_ATTRIBUTE_NAME);
        }
        if (runtimeType == null || runtimeType.length() == 0) {
            // Retrieve the runtime type from the basetype ...
            entity = (XSDSimpleTypeDefinition) getBasetype(eObject);
            runtimeType = (entity == null) ? DatatypeConstants.RuntimeTypeNames.OBJECT : getRuntimeTypeName(entity);
        }

        return runtimeType;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getRuntimeTypeFixed(org.eclipse.emf.ecore.EObject)
     */
    public Boolean getRuntimeTypeFixed(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        Boolean fixed = Boolean.FALSE;
        // If the datatype is a ur-type return a predefined runtime type
        if (! this.isURType(eObject)) {        
            // If the datatype is a built-in datatype then use the MetaMatrix 
            // instance since the instance found in the org.eclipse.xsd plugin
            // does not have runtime type information.  If the datatype is not a
            // built-in datatype then the original EObject reference is used.
            EObject type = this.getMmType(eObject);
            XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) type;
            XsdUtil.checkForEnterpriseConversion(entity);
            
            String runtimeTypeFixedValue = null;
            if (isEnterpriseDataType(entity)) {
                runtimeTypeFixedValue = getEnterpriseAttributeValue(entity, RUNTIME_TYPE_ATTRIBUTE_NAME);
            }
            if (runtimeTypeFixedValue == null) {
                // Retrieve the runtime type from the basetype ...
                entity = (XSDSimpleTypeDefinition) getBasetype(eObject);
                if (entity != null) {
                    fixed = getRuntimeTypeFixed(entity);                    
                }
            }
        }
        return fixed;
    }    

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getDatatypeID(org.eclipse.emf.ecore.EObject)
     */
    public String getDatatypeID(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject; 
        return this.getURI(entity);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getBasetypeID(org.eclipse.emf.ecore.EObject)
     */
    public String getBasetypeID(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        
        XSDSimpleTypeDefinition basetype = (XSDSimpleTypeDefinition)getBasetype(eObject);
        if (basetype != null) {
            return this.getURI(basetype);
        }
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getBasetype(org.eclipse.emf.ecore.EObject)
     */
    public Object getBasetype(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition) eObject; 
        XSDSimpleTypeDefinition basetype = null;
        
        // If the datatype is of a list variety then return the itemtype as the basetype
        final XSDVariety variety = type.getVariety();
        if (variety == XSDVariety.LIST_LITERAL) {
            basetype = type.getItemTypeDefinition();
            
        // If the datatype is of a union variety then return "anySimpleType" as the basetype
        } else if (variety == XSDVariety.UNION_LITERAL) {
            basetype = (XSDSimpleTypeDefinition) this.getAnySimpleType();
            
        // If the datatype is of a atomic variety then return it's referenced basetype    
        } else {
            basetype = type.getBaseTypeDefinition();
            if (basetype != null && isPrimitiveType(type) && isPrimitiveType(basetype)) {
                // If the type is a MetaMatrix built-in primitive type 
                // (e.g. "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance#string")
                // and its basetype is the XSD built-in primitive type
                // (e.g. "http://www.w3.org/2001/XMLSchema#string")
                // then return the UR-type of "anySimpleType" as the basetype
                if (DatatypeConstants.BUILTIN_DATATYPES_URI.equals(type.getTargetNamespace())) {
                    basetype = (XSDSimpleTypeDefinition) this.getAnySimpleType();
                }
            }
        }
        
        // If the basetype is anonymous then return "anySimpleType" as the basetype
        if (basetype != null && basetype.getName() == null) {
            basetype = (XSDSimpleTypeDefinition) this.getAnySimpleType();
        }

        return basetype;
    }
    
    private boolean isPrimitiveType(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition) eObject; 
        if ( isBuiltInDatatype(type) && DatatypeConstants.getPrimitivedBuiltInTypeNames().contains(type.getName()) ) {
            return true;
        }
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getPrimitiveType(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public Object getPrimitiveType(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        
        // While the datatype type is not a built-in primitive type ...
        EObject simpleType = eObject;
        EObject baseType   = null;
        while ( simpleType != null ) {
            baseType = (EObject)getBasetype(simpleType);
            if (baseType == null) {
                break;
            }
            if (XSDConstants.isURType((XSDSimpleTypeDefinition)baseType)) {
                return simpleType;
            }
            if (simpleType == baseType) {
                break;    
            }
            simpleType = baseType;
        }
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getPrimitiveTypeID(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public String getPrimitiveTypeID(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        
        XSDSimpleTypeDefinition primitiveType = (XSDSimpleTypeDefinition)getPrimitiveType(eObject);
        if (primitiveType != null) {
            return this.getURI(primitiveType);
        }
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getEnterpriseExtensionsMap(org.eclipse.emf.ecore.EObject)
     */
    public Map getEnterpriseExtensionsMap(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        // If the datatype is a built-in datatype then use the MetaMatrix 
        // instance since the instance found in the org.eclipse.xsd plugin
        // does not have extension map information.  If the datatype is not a
        // built-in datatype then the original EObject reference is used.
        EObject type = this.getMmType(eObject);
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) type; 
        XsdUtil.checkForEnterpriseConversion(entity);
        return getEnterpriseAttributes(entity);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getDescription(org.eclipse.emf.ecore.EObject)
     */
    public String getDescription(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        // If the datatype is a built-in datatype then use the MetaMatrix 
        // instance since the instance found in the org.eclipse.xsd plugin
        // does not have description information.  If the datatype is not a
        // built-in datatype then the original EObject reference is used.
        EObject type = this.getMmType(eObject);
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) type; 
        
        XSDAnnotation annotation = entity.getAnnotation();
        if(annotation != null){
            final Iterator userInfos = annotation.getUserInformation().iterator();
            while(userInfos.hasNext() ){
                final Element userInfo = (Element)userInfos.next();
                final String value = XsdUtil.getChildText(userInfo);
                if (value != null) {
                    return value;
                }
            } 
        }
        return StringUtil.Constants.EMPTY_STRING;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#isBuiltInDatatype(org.eclipse.emf.ecore.EObject)
     */
    public boolean isBuiltInDatatype(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject;
        
        if (XSDConstants.isURType(entity)) {
            return true;
        }
		
		// if the entity is a proxy determine it is a built in data type based on the
		// proxy URI, this is to prevent the resource for the eObject being passed 
		// to be loaded (Defect 13016)
		if(entity.eIsProxy()) {
			final URI eProxyURI = ((InternalEObject)eObject).eProxyURI();
			if(eProxyURI != null) {
				String proxyURIString = eProxyURI.toString();
				if(proxyURIString.startsWith(DatatypeConstants.BUILTIN_DATATYPES_URI) ||
				   proxyURIString.startsWith(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001) ||
				   proxyURIString.startsWith(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10) ||
				   proxyURIString.startsWith(XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999)) {
					return true;
				}
				return false;
			}
		}
        
        final String typeName = entity.getName();
        if ( typeName == null || this.mmDatatypeMap.get(typeName.toLowerCase()) == null ) {
            return false;
        }
        
        final String namespaceUri = entity.getTargetNamespace();
        if ( DatatypeConstants.BUILTIN_DATATYPES_URI.equals(namespaceUri) ) {
            return true;
        }
        if ( XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001.equals(namespaceUri) ) {
            return true;
        }
        if ( XSDConstants.SCHEMA_FOR_SCHEMA_URI_2000_10.equals(namespaceUri) ) {
            return true;
        }
        if ( XSDConstants.SCHEMA_FOR_SCHEMA_URI_1999.equals(namespaceUri) ) {
            return true;
        }
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#isComplexDatatype(org.eclipse.emf.ecore.EObject)
     */
    public boolean isComplexDatatype(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#isXSDSimpleTypeDefinition(org.eclipse.emf.ecore.EObject)
     */
    public boolean isSimpleDatatype(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        return true;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#isURType(org.eclipse.emf.ecore.EObject)
     */
    public boolean isURType(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject; 
        return XSDConstants.isURType(entity);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getVarietyType(org.eclipse.emf.ecore.EObject)
     */
    public short getVarietyType(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject; 
        
        XSDVariety variety = entity.getVariety();
        if (variety.getValue() == XSDVariety.ATOMIC) {
            return MetadataConstants.DATATYPE_VARIETIES.ATOMIC;

        } else if (variety.getValue() == XSDVariety.LIST) {
            return MetadataConstants.DATATYPE_VARIETIES.LIST;

        } else if (variety.getValue() == XSDVariety.UNION) {
            return MetadataConstants.DATATYPE_VARIETIES.UNION;
        }
        return 0;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#getVarietyProps(org.eclipse.emf.ecore.EObject)
     */
    public List getVarietyProps(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        XSDSimpleTypeDefinition entity = (XSDSimpleTypeDefinition) eObject;
         
        XSDVariety variety = entity.getVariety();
        List varietyProps = new ArrayList();
        if (variety.getValue() == XSDVariety.ATOMIC) {
            XSDSimpleTypeDefinition primitiveType = entity.getPrimitiveTypeDefinition();
            if (primitiveType != null) {
                varietyProps.add(this.getURI(primitiveType));
            }

        } else if (variety.getValue() == XSDVariety.LIST) {
            XSDSimpleTypeDefinition itemType = entity.getItemTypeDefinition();
            if (itemType != null) {
                varietyProps.add(this.getURI(itemType));
            }

        } else if (variety.getValue() == XSDVariety.UNION) {
            
            for (Iterator iter = entity.getMemberTypeDefinitions().iterator(); iter.hasNext();) {
                XSDSimpleTypeDefinition memberType = (XSDSimpleTypeDefinition)iter.next();
                if (memberType != null) {
                    varietyProps.add(this.getURI(memberType));
                }
            }
        }
        return varietyProps;
    }

    /*
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {
        // do nothing
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     */
    public boolean isDatatypeFeature(final EObject eObject, final EStructuralFeature eFeature) {
        ArgCheck.isInstanceOf(XSDSimpleTypeDefinition.class, eObject); 
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__MEMBER_TYPE_DEFINITIONS:
                    return true;
                case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__BASE_TYPE_DEFINITION:
                  return true;
                case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__PRIMITIVE_TYPE_DEFINITION:
                  return true;
                case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION__ITEM_TYPE_DEFINITION:
                  return true;
            }
        }
        return false;
    }
        
    /**
     * Check this <code>XSDSimpleTypeDefinition</code> to see if it contains the enterprise datatype
     * attributes 
     * @param type
     * @return success true if this is an enterprise datatype
     */
    public boolean isEnterpriseDataType(final EObject type) {
        ArgCheck.isNotNull(type);
        argCheckIsResolved(type);
        boolean success = false;
        if (type instanceof XSDSimpleTypeDefinition) {
            Map enterpriseAttributes = getEnterpriseAttributes((XSDSimpleTypeDefinition)type);
            if (!enterpriseAttributes.isEmpty()) {
                success = enterpriseAttributes.get(XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME) != null && 
                                 enterpriseAttributes.get(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME) != null &&
                                 enterpriseAttributes.get(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME) != null;
            }
        }
        
        return success;
    }

    /**
     * Set the attribute contents of <code>EnterpriseDatatypeInfo</code> on the 
     * <code>XSDSimpleTypeDefinition</code>. If the schema for the enterprise datatype
     * is not marked as an enterprise schema, it will be now marked with the namespace uri import.
     * 
     * These attrbiutes, which are prefixed with the enterprise namespace prefix, are validated correctly due
     * to the XSD specifications "lax" policy on qualified attrbutes names within complex types:
     * <xs:complexType name="topLevelAttribute">
     *  <xs:complexContent>
     *      <xs:restriction base="xs:attribute">
     *          <xs:sequence>
     *              <xs:element ref="xs:annotation" minOccurs="0"/>
     *              <xs:element name="simpleType" type="xs:localSimpleType" minOccurs="0"/>
     *          </xs:sequence>
     *          <xs:attribute name="ref" use="prohibited"/>
     *          <xs:attribute name="form" use="prohibited"/>
     *          <xs:attribute name="use" use="prohibited"/>
     *          <xs:attribute name="name" type="xs:NCName" use="required"/>
     *          <xs:anyAttribute namespace="##other" processContents="lax"/>  <---- 
     *      </xs:restriction>
     *  </xs:complexContent>
     * </xs:complexType>
     * 
     * @param type
     * @param edtInfo
     */
    public void setEnterpriseDataAttributes(final XSDSimpleTypeDefinition type, final EnterpriseDatatypeInfo edtInfo) {
        ArgCheck.isNotNull(type);
        argCheckIsResolved(type);
        
        if (edtInfo.isValid()) {
            XsdUtil.setAsEnterpriseSchema(type.getSchema());            
            final Element element = type.getElement();
            final Document doc = element.getOwnerDocument();
            boolean isModified = false;
            Attr attribute;
            
            final String uuid = edtInfo.getUuid();
            if ((uuid != null)) { 
                attribute = doc.createAttributeNS(XsdConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005,
                                                                                  XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME);
                attribute.setValue(uuid);
                attribute.setPrefix(XsdConstants.PREFIX_FOR_ENTERPRISE_DATATYPES_URI_2005);
                type.getElement().setAttributeNode(attribute);
                isModified = true;
            }

            final String runtimeType = edtInfo.getRuntimeType();
            if ((runtimeType != null)) {
                attribute = doc.createAttributeNS(XsdConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005,
                                                                                  XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME);
                attribute.setValue(runtimeType);
                attribute.setPrefix(XsdConstants.PREFIX_FOR_ENTERPRISE_DATATYPES_URI_2005);
                type.getElement().setAttributeNode(attribute);
                isModified = true;
            }

            final Boolean runtimeTypeFixed = edtInfo.getRuntimeTypeFixed();
            if ((runtimeTypeFixed != null)) { 
                attribute = doc.createAttributeNS(XsdConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005,
                                                                                  XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME);
                attribute.setValue(runtimeTypeFixed.toString());
                attribute.setPrefix(XsdConstants.PREFIX_FOR_ENTERPRISE_DATATYPES_URI_2005);
                type.getElement().setAttributeNode(attribute);
                isModified = true;
            }
            type.eResource().setModified(isModified);
        }
    }

   /**
    * Remove the enterprise datatype attributes from the given <code>XSDSimpleTypeDefinition</code>.
    * If there are no enterprise datatypes left in the schema, remove the enterprise namespace declaration.
    * @param type
    */    
    public void unSetEnterpriseDataAttributes(final XSDSimpleTypeDefinition type) {
        ArgCheck.isNotNull(type);
        argCheckIsResolved(type);
        
        final Element element = type.getElement();
        element.removeAttributeNS(XsdConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005,
                                                      XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME);
        element.removeAttributeNS(XsdConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005,
                XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME);
        element.removeAttributeNS(XsdConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005,
                XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME);
        if (!containsEnterpriseDatatypes(type.getSchema())) {
            XsdUtil.unsetAsEnterpriseSchema(type.getSchema());
        }
        type.eResource().setModified(true);
    }
    
   /**
    * Obtain the enterprise datatype attributes values from the given
    *  <code>XSDSimpleDefinitionType</code> and wrap them in an 
    *  <code>EnterpriseDatatypeInfo</code>.  
    * @param type
    * @return edtInfo
    */   
   public EnterpriseDatatypeInfo getEnterpriseDatatypeInfo (final XSDSimpleTypeDefinition type) {
       ArgCheck.isNotNull(type);       
       argCheckIsResolved(type);
       
       XsdUtil.checkForEnterpriseConversion(type);       
       EnterpriseDatatypeInfo edtInfo = new EnterpriseDatatypeInfo();
       if (XsdUtil.isEnterpriseSchema(type.getSchema()) && isEnterpriseDataType(type)) {
           final Map attributes = getEnterpriseAttributes(type);       
           edtInfo.setUuid((String) attributes.get(XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME));
           edtInfo.setRuntimeType((String) attributes.get(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME));
           String runtimeTypeFixed = (String) attributes.get(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME);
           edtInfo.setRuntimeTypeFixed(Boolean.valueOf(runtimeTypeFixed));           
       }
       return edtInfo;
   }   

   /**
    *  Non-interface method
    *  Convert a <code>XSDSimpleTypeDefinition</code> to an enterprise datatype if it has existing
    *  enterprise parameters (uuid, runtimetype) in an appinfo tag
    * @param type
    */
   public void convertEnterpriseDatatype (final XSDSimpleTypeDefinition type) {
       ArgCheck.isNotNull(type);
       argCheckIsResolved(type);
       
       if (! isEnterpriseDataType(type)) {
           EnterpriseDatatypeInfo edtInfo = getEnterpriseAttributesFromAppInfo(type);
           if (edtInfo.isValid()) {
               setEnterpriseDataAttributes(type, edtInfo);
               removeEnterpriseAttributesFromAppInfo(type);
               type.eNotify(new ENotificationImpl((InternalEObject) type, Notification.ADD, null, null, type));
           }
       }
   }

   /** 
    * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlDatatypeAspect#setBasetype(org.eclipse.xsd.XSDSimpleTypeDefinition, org.eclipse.xsd.XSDSimpleTypeDefinition)
    * @since 4.3
    */
   public void setBasetype(final XSDSimpleTypeDefinition simpleType, final XSDSimpleTypeDefinition baseType) {
       ArgCheck.isNotNull(simpleType);
       ArgCheck.isNotNull(baseType);
       argCheckIsResolved(simpleType);
       argCheckIsResolved(baseType);
       
       final XSDSchema schema = simpleType.getSchema();
       if (simpleType.eResource() != baseType.eResource()) {
           if (!XSDConstants.isSchemaForSchemaNamespace(baseType.getTargetNamespace()) &&
               !XsdUtil.containsImport(schema, baseType.getTargetNamespace())) {
               XsdUtil.addImport(simpleType, baseType);
           }               
       }
       final String oldReferencedNamespace = simpleType.getBaseTypeDefinition().getTargetNamespace(); 
       simpleType.setBaseTypeDefinition(baseType);

       // if the old basetype's namespace is 
       //  1) not the target namespace
       //  2) no longer referenced in this schema
       // then remove it's declaration (and import, if one exists);
       if (oldReferencedNamespace != null && !oldReferencedNamespace.equals(schema.getTargetNamespace()) &&
               !XsdUtil.containsReferenceToNamespace(schema, oldReferencedNamespace)) {
           XsdUtil.removeNamespaceRef(schema, oldReferencedNamespace);               
           // if the old basetype's namespace is longer referenced in this schema, remove it's import if one exists;
           if (XsdUtil.containsImport(schema, oldReferencedNamespace)) {
               XsdUtil.removeImport(schema, oldReferencedNamespace);
           }                          
       }           
   }
   
   
    // ==================================================================================
    //                         P R I V A T E   M E T H O D S
    // ==================================================================================
   
   private void argCheckIsResolved(EObject e) {
       if (e.eIsProxy()) {
           throw new IllegalArgumentException(XsdPlugin.Util.getString("XsdSimpleTypeDefinitionAspect.Error_EObject_can_not_be_a_proxy", e.toString())); //$NON-NLS-1$
       }
   }
   
    private void initializeMmDatatypeMap() {
        try {
            // Retrieve the MetaMatrix built-in datatype resource from the model container
            Resource resource = null;
            final ResourceSet[] resourceSets = ModelerCore.getExternalResourceSets();
            for (int i = 0; i < resourceSets.length; i++) {
                ResourceSet resourceSet = resourceSets[i];
                resource = resourceSet.getResource(BUILTIN_DATATYPES_URI,false);
                if ( resource != null ) {
                    break;  // Found it!!
                }
            }

            if (resource == null) {
                final Object[] params = new Object[]{BUILTIN_DATATYPES_URI};
                final String msg = XsdPlugin.Util.getString("XsdSimpleTypeDefinitionAspect.Error_obtain_the_built-in_datatypes_resource_from_the_container_using_URI_1",params); //$NON-NLS-1$
                XsdPlugin.Util.log(IStatus.ERROR,msg);
            }
            if (resource != null) {
                // Populate the mmDatatypeMap map with all built-in datatypes
                // keyed by lower case name
                for (Iterator iter = resource.getContents().iterator(); iter.hasNext();) {
                    EObject eObject = (EObject)iter.next();
                    if (eObject != null && eObject instanceof XSDSchema) {
            
                        // Found the schema now gather all the global simple types ...
                        for (Iterator iter2 = eObject.eContents().iterator(); iter2.hasNext();) {
                            eObject = (EObject)iter2.next();
                            if (eObject != null && eObject instanceof XSDSimpleTypeDefinition) {
                                final XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)eObject;
                                final String typeName = type.getName();
                                this.mmDatatypeMap.put(typeName.toLowerCase(),type);
                            }
                        }
                
                    }
                }        
            }
        } catch (Throwable e) {
            final Object[] params = new Object[]{BUILTIN_DATATYPES_URI};
            final String msg = XsdPlugin.Util.getString("XsdSimpleTypeDefinitionAspect.Error_obtain_the_built-in_datatypes_resource_from_the_container_using_URI_1",params); //$NON-NLS-1$
            XsdPlugin.Util.log(IStatus.ERROR,e,msg);
        }
    }
    
    private EObject getAnySimpleType() {
        if (this.anySimpleType == null) {
            this.anySimpleType = XSDSchemaImpl.getGlobalResourceSet().getEObject(ANY_SIMPLE_TYPE_URI,true);
        }
        return this.anySimpleType;
    }
    
    /**
     * If the specified XSDTypeDefinition is a built-in datatype then return 
     * the MetaMatrix built-in datatype as defined in the modeler.sdt plugin. 
     * If specified type is not a built-in datatype then the original EObject
     * reference is returned. 
     * @param type
     * @return
     */
    private EObject getMmType(final EObject eObject) {
        if(this.isURType(eObject) ){
            return eObject;
        }else if (this.isBuiltInDatatype(eObject)) {
            final XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)eObject;
            final String typeName = type.getName();
            final EObject mmdt = (EObject)this.mmDatatypeMap.get(typeName.toLowerCase());
            return mmdt;
        }
        return eObject;
    }
     
   /**
    * Check the given <code>XSDSchema</code> to see if it has any enterprise
    * datatypes. 
    * @param schema
    * @return success true if the schema contains at least one enterprise datatype
    */ 
   private boolean containsEnterpriseDatatypes(final XSDSchema schema) {
       boolean success = false;
       final EList contents = schema.getContents();
       for (final Iterator it = new ArrayList(contents).iterator(); it.hasNext();) {
           final Object o = it.next();
           if (o instanceof XSDSimpleTypeDefinition) {
               final XSDSimpleTypeDefinition sdt = (XSDSimpleTypeDefinition) o;
               if (isEnterpriseDataType(sdt)) {
                   success = true;
                   break;
               }
           }
       }
       return success;
   }
   
   /**
    *  Obtain the old appinfo enterprise tag attributes. 
    * @param type
    */    
   public EnterpriseDatatypeInfo getEnterpriseAttributesFromAppInfo(final XSDSimpleTypeDefinition type) {
       ArgCheck.isNotNull(type);
       EnterpriseDatatypeInfo edtInfo = new EnterpriseDatatypeInfo();
       final XSDAnnotation annotation = type.getAnnotation();
       if (annotation != null) {
           for (final Iterator it = annotation.getApplicationInformation().iterator(); it.hasNext();) {
               final Element appInfo = (Element) it.next();
               if(appInfo.getAttributes() != null && appInfo.getAttributes().getLength() > 0){
                   edtInfo.setUuid(appInfo.getAttribute(XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME));
                   edtInfo.setRuntimeType(appInfo.getAttribute(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME));
                   edtInfo.setRuntimeTypeFixed(Boolean.FALSE);
               }
           }            
       }
       return edtInfo;
   }   
   
   /**
    *  Remove the old appinfo enterprise tag attributes. If this leaves the appinfo tag empty,
    *  remove it as well 
    * @param type
    */    
   private void removeEnterpriseAttributesFromAppInfo(final XSDSimpleTypeDefinition type) {
       ArgCheck.isNotNull(type);
       final XSDAnnotation annotation = type.getAnnotation();
       if (annotation != null) {
           for (final Iterator it = annotation.getApplicationInformation().iterator(); it.hasNext();) {
               final Element appInfo = (Element) it.next();
               if (appInfo != null) {
                   if(appInfo.getAttributes() != null && appInfo.getAttributes().getLength() > 0){
                       appInfo.removeAttribute(XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME);
                       appInfo.removeAttribute(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME);
                       if (appInfo.getAttributes().getLength() == 0) {
                           it.remove();
                       }
                   }
               }
           }
           if (annotation.getApplicationInformation().size() == 0) {
               annotation.getApplicationInformation().clear();
           }
       }
   }    
      
   /**
    *  Obtain the enterprise datatype attribute values from the given 
    *  <code>XSDSimpleDefinitionType</code> and return them in a map.
    * @param type
    * @return results map which contains the enterprise attributes as keys
    */
    private Map getEnterpriseAttributes(final XSDSimpleTypeDefinition type){
        ArgCheck.isNotNull(type);
        final Map results = new HashMap();
        final Element typeElement = type.getElement();
        if (typeElement != null) {
            if ((typeElement.getAttributes() == null) || (typeElement.getAttributes().getLength() == 0)) {
                return Collections.EMPTY_MAP;
            }
    
            results.put(XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME, 
                                getEnterpriseAttributeValue(type, 
                                 XsdSimpleTypeDefinitionAspect.UUID_ATTRIBUTE_NAME));
            results.put(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME, 
                                getEnterpriseAttributeValue(type, 
                                 XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_ATTRIBUTE_NAME));
            results.put(XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME, 
                                getEnterpriseAttributeValue(type, 
                                 XsdSimpleTypeDefinitionAspect.RUNTIME_TYPE_FIXED_ATTRIBUTE_NAME));                
        }
        return results;
    }
    
    /**
     * Helper method to extract the uuid string from the simple datatype definition's enterprise data 
     * @param type
     * @param name
     * @return the value of the uuid
     */
    private String getEnterpriseAttributeValue(final XSDSimpleTypeDefinition type, final String name) {       
        Attr attrNS = type.getElement().getAttributeNodeNS(XsdConstants.SCHEMA_FOR_ENTERPRISE_DATATYPES_URI_2005,
                name);
        return attrNS != null ? attrNS.getValue() : null;
    }    
    
    
    private String getURI(final XSDSimpleTypeDefinition type) {
        ArgCheck.isNotNull(type); 
        XSDSchema xsdSchema = type.getSchema();
        String theTargetNamespace = xsdSchema == null ? type.getTargetNamespace() : xsdSchema.getTargetNamespace();
        if (theTargetNamespace == null) {
            theTargetNamespace = StringUtil.Constants.EMPTY_STRING;
        }
        if (theTargetNamespace.equals(DatatypeConstants.BUILTIN_DATATYPES_URI)) {
            // If this datatype is not one of the MetaMatrix extended built-in types change the
            // target namespace from "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"
            // to "http://www.w3.org/2001/XMLSchema"
            final String typeName = type.getName();
            if (!DatatypeConstants.getMetaMatrixExtendedBuiltInTypeNames().contains(typeName)) {
                theTargetNamespace = ModelerCore.XML_SCHEMA_GENERAL_URI;
            }
        }
        String theName = type.getName();
        if (theName == null) {
            theName = type.getAliasName();
        }
        return theTargetNamespace + "#" + theName; //$NON-NLS-1$
    }
    
}
