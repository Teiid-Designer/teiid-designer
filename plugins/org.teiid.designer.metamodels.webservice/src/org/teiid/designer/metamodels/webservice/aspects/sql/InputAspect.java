/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.webservice.aspects.sql;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.webservice.Input;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;
import org.teiid.designer.metamodels.webservice.WebServicePackage;



/** 
 * @since 8.0
 */
public class InputAspect extends WebServiceComponentAspect implements SqlProcedureParameterAspect {

    private final static String DEFAULT_VALUE = null;
    private final static int LENGTH = 0;
    private final static int SCALE = 0;
    private final static int NULL_TYPE = MetadataConstants.NULL_TYPES.NULLABLE; // nullable
    private final static int PRECISION = 0;
    private final static int POSITION = 0;
    private final static int RADIX = 0;
    
    private final static String DATATYPE_NAME = DatatypeConstants.BuiltInNames.XML_LITERAL;
    private final static String RUNTIME_TYPE  = DatatypeConstants.RuntimeTypeNames.XML;
    
    protected InputAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public EObject getDatatype(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        try {
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
            return dtMgr.getBuiltInDatatype(DATATYPE_NAME);
        } catch(ModelerCoreException e) {
            WebServiceMetamodelPlugin.Util.log(e);
            // ignore
        }
        return null;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getDatatypeName(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return DATATYPE_NAME;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getDatatypeObjectID(final EObject eObject) {
        EObject datatype = getDatatype(eObject);
        if(datatype == null) {
            return null;
        }
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        return dtMgr.getUuidString(datatype) == null ? "" : dtMgr.getUuidString(datatype); //$NON-NLS-1$
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getRuntimeType(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return RUNTIME_TYPE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getDefaultValue(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return DEFAULT_VALUE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getNullType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getNullType(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return NULL_TYPE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getLength(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getLength(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return LENGTH;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getPosition(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getPosition(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return POSITION;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getRadix(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getRadix(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return RADIX;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getScale(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getScale(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return SCALE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getPrecision(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return PRECISION;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getType(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return MetadataConstants.PARAMETER_TYPES.IN_PARM;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isOptional(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isOptional(final EObject eObject) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.2
     */
    @Override
	public boolean isDatatypeFeature(final EObject eObject,
                                     final EStructuralFeature eFeature) {
        CoreArgCheck.isInstanceOf(Input.class, eObject); 
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case WebServicePackage.INPUT__CONTENT_SIMPLE_TYPE:
                    return true;
            }
        }
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     * @since 4.2
     */
    @Override
	public boolean isRecordType(final char recordType) {
        return (recordType == IndexConstants.RECORD_TYPE.CALLABLE_PARAMETER);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetDatatype()
     * @since 4.2
     */
    @Override
	public boolean canSetDatatype() {
        return false;
    }
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setDatatype(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public void setDatatype(EObject eObject,
                            EObject datatype) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        final String msg = WebServiceMetamodelPlugin.Util.getString("InputAspect.setDatatypeNotSupported"); //$NON-NLS-1$
        throw new UnsupportedOperationException(msg);
    }
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetLength()
     * @since 4.2
     */
    @Override
	public boolean canSetLength() {
        return false;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    @Override
	public void setLength(EObject eObject, int length) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        final String msg = WebServiceMetamodelPlugin.Util.getString("InputAspect.setLengthNotSupported"); //$NON-NLS-1$
        throw new UnsupportedOperationException(msg);
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#canSetNullType()
     * @since 4.2
     */
    @Override
	public boolean canSetNullType() {
        return false;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    @Override
	public void setNullType(EObject eObject, int nullType) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        final String msg = WebServiceMetamodelPlugin.Util.getString("InputAspect.setNullTypeNotSupported"); //$NON-NLS-1$
        throw new UnsupportedOperationException(msg);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isInputParam(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public boolean isInputParam(EObject eObject) {
        return true;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setDirection(org.eclipse.emf.ecore.EObject, int)
     * @since 4.3
     */
    @Override
	public void setDirection(EObject eObject,
                             int dir) {
        CoreArgCheck.isInstanceOf(Input.class, eObject);
        final String msg = WebServiceMetamodelPlugin.Util.getString("InputAspect.setDirectionNotSupported"); //$NON-NLS-1$
        throw new UnsupportedOperationException(msg);
    }
}
