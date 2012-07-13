/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xmlservice.aspects.sql;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.metamodels.xmlservice.XmlInput;
import org.teiid.designer.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import org.teiid.designer.metamodels.xmlservice.XmlServicePackage;



/** 
 * @since 4.2
 */
public class XmlInputAspect extends XmlServiceComponentAspect implements SqlProcedureParameterAspect {

    private final static Object DEFAULT_VALUE = null;
    private final static int LENGTH = 0;
    private final static int SCALE = 0;
    private final static int NULL_TYPE = MetadataConstants.NULL_TYPES.NULLABLE; // nullable
    private final static int PRECISION = 0;
    private final static int RADIX = 0;
    
    private final static String DATATYPE_NAME = DatatypeConstants.BuiltInNames.XML_LITERAL;
    private final static String RUNTIME_TYPE  = DatatypeConstants.RuntimeTypeNames.XML;
    
    protected XmlInputAspect(final MetamodelEntity entity) {
        super(entity);
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public EObject getDatatype(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        XmlInput input = (XmlInput) eObject;
        if (input.getType() != null) {
            return input.getType();
        }
        try {
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
            return dtMgr.getBuiltInDatatype(DATATYPE_NAME);
        } catch(ModelerCoreException e) {
            XmlServiceMetamodelPlugin.Util.log(e);
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
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        XmlInput input = (XmlInput) eObject;
        if (input.getType() != null) {
            final EObject dataType = input.getType();
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(input,true);
            final String dtName = dtMgr.getName(dataType);
            return dtName == null ? "" : dtMgr.getName(dataType); //$NON-NLS-1$
        }
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
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        XmlInput input = (XmlInput) eObject;
        if (input.getType() != null) {
            final EObject datatype = input.getType();
            return datatype == null ? "" : ModelerCore.getDatatypeManager(eObject,true).getRuntimeTypeName(datatype); //$NON-NLS-1$
        }
        return RUNTIME_TYPE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public Object getDefaultValue(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return DEFAULT_VALUE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getNullType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getNullType(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return NULL_TYPE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getLength(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getLength(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return LENGTH;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getPosition(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getPosition(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        XmlInput input = (XmlInput) eObject;
        List inputs = input.getOperation().getInputs();
        // correct from '0' to '1' based position
        return inputs.indexOf(eObject) + 1;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getRadix(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getRadix(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return RADIX;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getScale(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getScale(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return SCALE;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getPrecision(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return PRECISION;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#getType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getType(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return MetadataConstants.PARAMETER_TYPES.IN_PARM;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isOptional(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isOptional(final EObject eObject) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        return false;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.2
     */
    @Override
	public boolean isDatatypeFeature(final EObject eObject,
                                     final EStructuralFeature eFeature) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject); 
        final EObjectImpl eObjectImpl = super.getEObjectImpl(eObject);
        if (eObjectImpl != null) {
            switch (eObjectImpl.eDerivedStructuralFeatureID(eFeature)) {
                case XmlServicePackage.XML_INPUT__TYPE:
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
        return true;
    }
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlProcedureParameterAspect#setDatatype(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public void setDatatype(EObject eObject,
                            EObject datatype) {
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        XmlInput input = (XmlInput) eObject;
        input.setType(datatype);
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
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        final String msg = XmlServiceMetamodelPlugin.Util.getString("XmlInputAspect.setLengthNotSupported"); //$NON-NLS-1$
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
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        final String msg = XmlServiceMetamodelPlugin.Util.getString("XmlInputAspect.setNullTypeNotSupported"); //$NON-NLS-1$
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
        CoreArgCheck.isInstanceOf(XmlInput.class, eObject);
        final String msg = XmlServiceMetamodelPlugin.Util.getString("XmlInputAspect.setDirectionNotSupported"); //$NON-NLS-1$
        throw new UnsupportedOperationException(msg);
    }
}
