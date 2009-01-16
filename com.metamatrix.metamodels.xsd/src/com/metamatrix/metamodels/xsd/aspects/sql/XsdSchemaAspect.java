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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect;
import com.metamatrix.modeler.core.types.DatatypeConstants;

/**
 * XsdSimpleTypeDefinitionAspect
 */
public class XsdSchemaAspect extends AbstractMetamodelAspect implements SqlModelAspect {

    public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID;
    
    private static final int MAX_SET_SIZE_EDEFAULT = 100;


    protected XsdSchemaAspect(MetamodelEntity entity) {
        super.setMetamodelEntity(entity);
        super.setID(ASPECT_ID);
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName(EObject eObject) {
        ArgCheck.isInstanceOf(XSDSchema.class, eObject); 

        Resource resource = eObject.eResource();
        if (resource != null && resource.getURI() != null) {
            final URI resourceURI  = resource.getURI();
            if (resourceURI.isFile()) {
                String extension = resourceURI.fileExtension();
                String modelNameWithExtension = resourceURI.lastSegment();
                if (extension != null && extension.length() > 0) {
                    int endIndex = modelNameWithExtension.indexOf(extension) - 1;
                    return (endIndex > 0 ? modelNameWithExtension.substring(0, endIndex) : modelNameWithExtension);
                }
            } else {
                final String uriString = URI.decode(resourceURI.toString());
                if (DatatypeConstants.BUILTIN_DATATYPES_URI.equals(uriString)) {
                    return DatatypeConstants.DATATYPES_MODEL_FILE_NAME_WITHOUT_EXTENSION;
                }
                if (ModelerCore.XML_SCHEMA_GENERAL_URI.equals(uriString)) {
                    return DatatypeConstants.DATATYPES_MODEL_FILE_NAME_WITHOUT_EXTENSION;
                }
                
            }
        }
        return StringUtil.Constants.EMPTY_STRING;
//        XSDSchema schema = (XSDSchema)eObject;
//        String targetNamespace = schema.getTargetNamespace();
//        return (targetNamespace == null ? StringUtil.Constants.EMPTY_STRING : targetNamespace);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    public String getNameInSource(EObject eObject) {
        return this.getName(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.MODEL);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    public boolean isQueryable(final EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void updateObject(EObject targetObject, EObject sourceObject) {
        // do nothing
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        return this.getName(eObject);
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getObjectID(EObject eObject) {
        // defect 18858 - Wrong value getting returned for OID:
        ArgCheck.isInstanceOf(XSDSchema.class, eObject); 
        Object object = super.getObjectID(eObject);
        return object;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getParentObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getParentObjectID(EObject eObject) {
        return null;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.AbstractMetamodelAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public IPath getPath(EObject eObject) {
        return new Path(this.getName(eObject));
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#getMaxSetSize(org.eclipse.emf.ecore.EObject)
     */
    public int getMaxSetSize(EObject eObject) {
        return MAX_SET_SIZE_EDEFAULT;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#getModelType(org.eclipse.emf.ecore.EObject)
     */
    public int getModelType(EObject eObject) {
        return ModelType.TYPE;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#getPrimaryMetamodelUri(org.eclipse.emf.ecore.EObject)
     */
    public String getPrimaryMetamodelUri(EObject eObject) {
        return XSDPackage.eNS_URI;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#isVisible(org.eclipse.emf.ecore.EObject)
     */
    public boolean isVisible(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsDistinct(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsDistinct(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsJoin(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsJoin(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsOrderBy(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsOrderBy(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsOuterJoin(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsOuterJoin(EObject eObject) {
        return false;
    }

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect#supportsWhereAll(org.eclipse.emf.ecore.EObject)
     */
    public boolean supportsWhereAll(EObject eObject) {
        return false;
    }
    
//    private String getURI(final XSDSchema xsdSchema) {
//        ArgCheck.isNotNull(xsdSchema); 
//        String theTargetNamespace = xsdSchema.getTargetNamespace();
//        if (theTargetNamespace == null) {
//            theTargetNamespace = StringUtil.Constants.EMPTY_STRING;
//        }
//        if (theTargetNamespace.equals(DatatypeConstants.BUILTIN_DATATYPES_URI)) {
//            theTargetNamespace = ModelerCore.XML_SCHEMA_GENERAL_URI;
//        }
//        return theTargetNamespace; //$NON-NLS-1$
//    }

}
