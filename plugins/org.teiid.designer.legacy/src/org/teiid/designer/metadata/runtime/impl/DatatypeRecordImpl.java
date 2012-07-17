/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.impl;

import java.util.List;

import org.teiid.designer.core.index.IndexConstants;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.metadata.runtime.DatatypeRecord;
import org.teiid.designer.metadata.runtime.MetadataConstants;

/**
 * ColumnRecordImpl
 */
public class DatatypeRecordImpl extends AbstractMetadataRecord implements DatatypeRecord {

    /**
     */
    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_JAVA_CLASS_NAME = "java.lang.Object";  //$NON-NLS-1$

    private int length;
    private int precisionLength;
    private int scale;
    private int radix;
    private boolean isSigned;
    private boolean isAutoIncrement;
    private boolean isCaseSensitive;
    private short type;
    private short searchType;
    private short nullType;
    private String javaClassName;
    private String runtimeTypeName;
    private String datatypeID;
    private String basetypeID;
    private String primitiveTypeID;
    private short varietyType;
    private List varietyProps;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public DatatypeRecordImpl() {
    	this(new MetadataRecordDelegate());
        this.javaClassName = DEFAULT_JAVA_CLASS_NAME;        
    }
    
    protected DatatypeRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getName()
     */
    @Override
    public String getName() {
        final String fullName = super.getFullName();
        int indx = fullName.lastIndexOf(DatatypeConstants.URI_REFERENCE_DELIMITER);
        if (indx > -1) {
            return fullName.substring(indx+1);
        }
        indx = fullName.lastIndexOf(IndexConstants.NAME_DELIM_CHAR);
        if (indx > -1) {
            return fullName.substring(indx+1);
        }
        return fullName;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.MetadataRecord#getModelName()
     */
    @Override
    public String getModelName() {
        final String fullName = super.getFullName();
        int indx = fullName.lastIndexOf(DatatypeConstants.URI_REFERENCE_DELIMITER);
        if (indx > -1) {
            return fullName.substring(0, indx);
        }
        indx = fullName.lastIndexOf(IndexConstants.NAME_DELIM_CHAR);
        if (indx > -1) {
            return fullName.substring(0, indx);
        }
        return fullName;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getLength()
     */
    @Override
	public int getLength() {
        return this.length;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getPrecisionLength()
     */
    @Override
	public int getPrecisionLength() {
        return this.precisionLength;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getScale()
     */
    @Override
	public int getScale() {
        return this.scale;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getRadix()
     */
    @Override
	public int getRadix() {
        return this.radix;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#isSigned()
     */
    @Override
	public boolean isSigned() {
        return this.isSigned;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#isAutoIncrement()
     */
    @Override
	public boolean isAutoIncrement() {
        return this.isAutoIncrement;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#isCaseSensitive()
     */
    @Override
	public boolean isCaseSensitive() {
        return this.isCaseSensitive;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getType()
     */
    @Override
	public short getType() {
        return this.type;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#isBuiltin()
     */
    @Override
	public boolean isBuiltin() {
        if ( getType() == MetadataConstants.DATATYPE_TYPES.BASIC ) {
            return true;
        }
        return false;
    }


    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getSearchType()
     */
    @Override
	public short getSearchType() {
        return this.searchType;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getNullType()
     */
    @Override
	public short getNullType() {
        return this.nullType;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getJavaClassName()
     */
    @Override
	public String getJavaClassName() {
        return this.javaClassName;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getRuntimeTypeName()
     */
    @Override
	public String getRuntimeTypeName() {
        return this.runtimeTypeName;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getDatatypeID()
     */
    @Override
	public String getDatatypeID() {
        return this.datatypeID;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getBasetypeID()
     */
    @Override
	public String getBasetypeID() {
        return this.basetypeID;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getBasetypeName()
     */
    @Override
	public String getBasetypeName() {
        if ( this.basetypeID != null ) {
            final int i = getBasetypeID().lastIndexOf(DatatypeConstants.URI_REFERENCE_DELIMITER);
            if ( i != -1 && getBasetypeID().length() > (i+1)) {
                return getBasetypeID().substring(i+1);
            }
        }
        return null;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getPrimitiveTypeID()
     * @since 4.3
     */
    @Override
	public String getPrimitiveTypeID() {
        return this.primitiveTypeID;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getVarietyType()
     */
    @Override
	public short getVarietyType() {
        return this.varietyType;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.DatatypeRecord#getVarietyProps()
     */
    @Override
	public List getVarietyProps() {
        return this.varietyProps;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    public void setBasetypeID(String string) {
        basetypeID = string;
    }

    /**
     * @param string
     */
    public void setPrimitiveTypeID(String string) {
        primitiveTypeID = string;
    }

    /**
     * @param b
     */
    public void setAutoIncrement(boolean b) {
        isAutoIncrement = b;
    }

    /**
     * @param b
     */
    public void setCaseSensitive(boolean b) {
        isCaseSensitive = b;
    }

    /**
     * @param b
     */
    public void setSigned(boolean b) {
        isSigned = b;
    }

    /**
     * @param string
     */
    public void setJavaClassName(String string) {
        javaClassName = string;
    }

    /**
     * @param i
     */
    public void setLength(int i) {
        length = i;
    }

    /**
     * @param s
     */
    public void setNullType(short s) {
        nullType = s;
    }

    /**
     * @param i
     */
    public void setPrecisionLength(int i) {
        precisionLength = i;
    }

    /**
     * @param i
     */
    public void setRadix(int i) {
        radix = i;
    }

    /**
     * @param string
     */
    public void setRuntimeTypeName(String string) {
        runtimeTypeName = string;
    }

    /**
     * @param i
     */
    public void setScale(int i) {
        scale = i;
    }

    /**
     * @param s
     */
    public void setSearchType(short s) {
        searchType = s;
    }

    /**
     * @param s
     */
    public void setType(short s) {
        type = s;
    }

    /**
     * @param string
     */
    public void setDatatypeID(String string) {
        datatypeID = string;
    }

    /**
     * @param list
     */
    public void setVarietyProps(List list) {
        varietyProps = list;
    }

    /**
     * @param s
     */
    public void setVarietyType(short s) {
        varietyType = s;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(getClass().getSimpleName());
        sb.append(" name="); //$NON-NLS-1$
        sb.append(getName());
        sb.append(", basetype name="); //$NON-NLS-1$
        sb.append(getBasetypeName());
        sb.append(", runtimeType="); //$NON-NLS-1$
        sb.append(getRuntimeTypeName());
        sb.append(", javaClassName="); //$NON-NLS-1$
        sb.append(getJavaClassName());
        sb.append(", ObjectID="); //$NON-NLS-1$
        sb.append(getUUID());
        sb.append(", datatypeID="); //$NON-NLS-1$
        sb.append(getDatatypeID());

        return sb.toString();
    }

}