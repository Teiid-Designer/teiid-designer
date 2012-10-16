/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xsd.aspects.sql;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSchema;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.metamodels.core.ModelType;


/**
 * XsdSimpleTypeDefinitionAspect
 *
 * @since 8.0
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
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getName(EObject eObject) {
        CoreArgCheck.isInstanceOf(XSDSchema.class, eObject); 

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
        return CoreStringUtil.Constants.EMPTY_STRING;
//        XSDSchema schema = (XSDSchema)eObject;
//        String targetNamespace = schema.getTargetNamespace();
//        return (targetNamespace == null ? StringUtil.Constants.EMPTY_STRING : targetNamespace);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getNameInSource(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getNameInSource(EObject eObject) {
        return this.getName(eObject);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     */
    @Override
	public boolean isRecordType(char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.MODEL);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isQueryable(final EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#updateObject(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    @Override
	public void updateObject(EObject targetObject, EObject sourceObject) {
        // do nothing
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect#getFullName(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public String getFullName(EObject eObject) {
        return this.getName(eObject);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect#getObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getObjectID(EObject eObject) {
        // defect 18858 - Wrong value getting returned for OID:
        CoreArgCheck.isInstanceOf(XSDSchema.class, eObject); 
        Object object = super.getObjectID(eObject);
        return object;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect#getParentObjectID(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public Object getParentObjectID(EObject eObject) {
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect#getPath(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public IPath getPath(EObject eObject) {
        return new Path(this.getName(eObject));
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#getMaxSetSize(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getMaxSetSize(EObject eObject) {
        return MAX_SET_SIZE_EDEFAULT;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#getModelType(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public int getModelType(EObject eObject) {
        return ModelType.TYPE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#getPrimaryMetamodelUri(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public String getPrimaryMetamodelUri(EObject eObject) {
        return XSDPackage.eNS_URI;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#isVisible(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean isVisible(EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsDistinct(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsDistinct(EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsJoin(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsJoin(EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsOrderBy(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsOrderBy(EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsOuterJoin(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsOuterJoin(EObject eObject) {
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlModelAspect#supportsWhereAll(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public boolean supportsWhereAll(EObject eObject) {
        return false;
    }
    
//    private String getURI(final XSDSchema xsdSchema) {
//        CoreArgCheck.isNotNull(xsdSchema); 
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
