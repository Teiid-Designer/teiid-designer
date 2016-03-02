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
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metamodels.webservice.WebServiceMetamodelPlugin;


/**
 * This aspect is here because Output is adapted to a SqlColumnSet, and we need an object
 * that is adapted to SqlColumn, and the name has to be "xml" to match up with the output
 * of transformations that return a result set with a single "xml:string" column.
 * @since 8.0
 */
public class SampleMessagesAspect extends WebServiceComponentAspect implements SqlColumnAspect {

    private final static boolean SELECTABLE = true;
    private final static boolean UPDATABLE = false;
    private final static boolean AUTO_INCREMENT = false;
    private final static boolean CASE_SENSITIVE = false;
    private final static boolean SIGNED = false;
    private final static boolean CURRENCY = false;
    private final static boolean FIXED_LENGTH = false;
    private final static boolean TRANSFORMATION_INPUT_PARAMETER = false;
    private final static int SEARCH_TYPE = MetadataConstants.SEARCH_TYPES.UNSEARCHABLE; // not searcheable
    private final static String DEFAULT_VALUE = null;
    private final static Object MIN_VALUE = null;
    private final static Object MAX_VALUE = null;
    private final static int LENGTH = 0;
    private final static int SCALE = 0;
    private final static int NULL_TYPE = MetadataConstants.NULL_TYPES.NULLABLE; // nullable
    private final static String FORMAT = null;
    private final static int PRECISION = 0;
    private final static int CHAR_OCTET_LENGTH = 0;
    private final static int POSITION = 1;
    private final static int RADIX = 0;
    private final static int NULL_VALUES = 0;
    private final static int DISTINCT_VALUES = 0;    
    
    private final static String DATATYPE_NAME = DatatypeConstants.BuiltInNames.XML_LITERAL;
    private final static String RUNTIME_TYPE  = DatatypeConstants.RuntimeTypeNames.XML;
    
    /**
     * @since 4.2
     */
    public SampleMessagesAspect(final MetamodelEntity entity) {
        super(entity);
    }

    
    // --------------------------------------------------------------------------------------------------------
    //                             BEGIN -- Override the behavior of these methods
    // --------------------------------------------------------------------------------------------------------

    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isQueryable(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public boolean isQueryable(final EObject eObject) {
        return true;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#getName(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
    public String getName(EObject eObject) {
        return "xml"; //$NON-NLS-1$   // Matches what is used for XML documents.
    }
    
    // --------------------------------------------------------------------------------------------------------
    //                              END -- Override the behavior of these methods
    // --------------------------------------------------------------------------------------------------------

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getCharOctetLength(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getCharOctetLength(EObject eObject) {
        return CHAR_OCTET_LENGTH;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatype(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public EObject getDatatype(EObject eObject) {
        try {
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
            return dtMgr.getBuiltInDatatype(DATATYPE_NAME);
        } catch (ModelerCoreException e) {
            // ignore
        }
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeName(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getDatatypeName(EObject eObject) {
        return DATATYPE_NAME;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDatatypeObjectID(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getDatatypeObjectID(EObject eObject) {
        EObject datatype = getDatatype(eObject);
        if (datatype == null) {
            return null;
        }
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
        return dtMgr.getUuidString(datatype) == null ? "" : dtMgr.getUuidString(datatype); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDefaultValue(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getDefaultValue(EObject eObject) {
        return DEFAULT_VALUE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getFormat(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getFormat(EObject eObject) {
        return FORMAT;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getLength(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getLength(EObject eObject) {
        return LENGTH;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getMaxValue(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public Object getMaxValue(EObject eObject) {
        return MAX_VALUE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getMinValue(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public Object getMinValue(EObject eObject) {
        return MIN_VALUE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNullType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getNullType(EObject eObject) {
        return NULL_TYPE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getPosition(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getPosition(EObject eObject) {
        return POSITION;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getPrecision(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getPrecision(EObject eObject) {
        return PRECISION;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getRadix(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getRadix(EObject eObject) {
        return RADIX;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getDistinctValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public int getDistinctValues(EObject eObject) {
        return DISTINCT_VALUES;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNullValues(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    @Override
	public int getNullValues(EObject eObject) {
        return NULL_VALUES;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getNativeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getNativeType(EObject eObject) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getRuntimeType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public String getRuntimeType(EObject eObject) {
        return RUNTIME_TYPE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getScale(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getScale(EObject eObject) {
        return SCALE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#getSearchType(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public int getSearchType(EObject eObject) {
        return SEARCH_TYPE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isAutoIncrementable(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isAutoIncrementable(EObject eObject) {
        return AUTO_INCREMENT;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isCaseSensitive(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isCaseSensitive(EObject eObject) {
        return CASE_SENSITIVE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isCurrency(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isCurrency(EObject eObject) {
        return CURRENCY;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isDatatypeFeature(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EStructuralFeature)
     * @since 4.2
     */
    @Override
	public boolean isDatatypeFeature(EObject eObject, EStructuralFeature eFeature) {
        // documents do not have datatype features
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isFixedLength(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isFixedLength(EObject eObject) {
        return FIXED_LENGTH;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isSelectable(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isSelectable(EObject eObject) {
        return SELECTABLE;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isSigned(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isSigned(EObject eObject) {
        return SIGNED;
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isTranformationInputParameter(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isTranformationInputParameter(EObject eObject) {
        return TRANSFORMATION_INPUT_PARAMETER;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#isUpdatable(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public boolean isUpdatable(EObject eObject) {
        return UPDATABLE;
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetDatatype()
     * @since 4.2
     */
    @Override
	public boolean canSetDatatype() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setDatatype(org.eclipse.emf.ecore.EObject,
     *      org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    @Override
	public void setDatatype(EObject eObject, EObject datatype) {
        throw new UnsupportedOperationException(WebServiceMetamodelPlugin.Util.getString("SampleMessagesAspect.setDatatypeNotSupported")); //$NON-NLS-1$
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetLength()
     * @since 4.2
     */
    @Override
	public boolean canSetLength() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setLength(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    @Override
	public void setLength(EObject eObject, int length) {
        throw new UnsupportedOperationException(WebServiceMetamodelPlugin.Util.getString("SampleMessagesAspect.setLengthNotSupported")); //$NON-NLS-1$
    }
    
    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#canSetNullType()
     * @since 4.2
     */
    @Override
	public boolean canSetNullType() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlColumnAspect#setNullType(org.eclipse.emf.ecore.EObject, int)
     * @since 4.2
     */
    @Override
	public void setNullType(EObject eObject, int nullType) {
        throw new UnsupportedOperationException(WebServiceMetamodelPlugin.Util.getString("SampleMessagesAspect.setNullTypeNotSupported")); //$NON-NLS-1$
    }

    /** 
     * @see org.teiid.designer.core.metamodel.aspect.sql.SqlAspect#isRecordType(char)
     * @since 4.2
     */
    @Override
	public boolean isRecordType(char recordType) {
        return recordType == IndexConstants.RECORD_TYPE.COLUMN;
    }

}
