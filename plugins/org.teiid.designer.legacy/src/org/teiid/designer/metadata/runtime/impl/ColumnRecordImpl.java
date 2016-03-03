/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.impl;

import org.teiid.designer.metadata.runtime.ColumnRecord;

/**
 * ColumnRecordImpl
 *
 * @since 8.0
 */
public class ColumnRecordImpl extends AbstractMetadataRecord implements ColumnRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private String datatypeUUID;
    private boolean selectable;
    private boolean updatable;
    private boolean autoIncrementable;
    private boolean caseSensitive;
    private boolean signed;
    private boolean currency;
    private boolean fixedLength;
    private boolean tranformationInputParameter;
    private int searchType;
    private String defaultValue;
    private Object minValue;
    private Object maxValue;
    private int length;
    private int scale;
    private int nullType;
    private String runtimeTypeName;
    private String nativeType;
    private String format;
    private int precision;
    private int charOctetLength;
    private int position;
    private int radix;
    private int distinctValues;
    private int nullValues;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ColumnRecordImpl() {
    	this(new MetadataRecordDelegate());
    }
    
    protected ColumnRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getCharOctetLength()
     */
    @Override
	public int getCharOctetLength() {
        return charOctetLength;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getRuntimeType()
     */
    @Override
	public String getRuntimeType() {
        return runtimeTypeName;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getDatatypeUUID()
     */
    @Override
	public String getDatatypeUUID() {
        return datatypeUUID;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getDefaultValue()
     */
    @Override
	public String getDefaultValue() {
        return defaultValue;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getLength()
     */
    @Override
	public int getLength() {
        return length;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getMaxValue()
     */
    @Override
	public Object getMaxValue() {
        return maxValue;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getMinValue()
     */
    @Override
	public Object getMinValue() {
        return minValue;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getPrecision()
     */
    @Override
	public int getPrecision() {
        return precision;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getPosition()
     */
    @Override
	public int getPosition() {
        return position;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getScale()
     */
    @Override
	public int getScale() {
        return scale;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getSearchTye()
     */
    @Override
	public int getSearchType() {
        return searchType;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getFormat()
     */
    @Override
	public String getFormat() {
        return format;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#isAutoIncrementable()
     */
    @Override
	public boolean isAutoIncrementable() {
        return autoIncrementable;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#isCaseSensitive()
     */
    @Override
	public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#isCurrency()
     */
    @Override
	public boolean isCurrency() {
        return currency;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#isFixedLength()
     */
    @Override
	public boolean isFixedLength() {
        return fixedLength;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#isTranformationInputParameter()
     * @since 4.2
     */
    @Override
	public boolean isTranformationInputParameter() {
        return tranformationInputParameter;
    }

    /**
     * @return
     */
    @Override
	public int getNullType() {
        return nullType;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#isSelectable()
     */
    @Override
	public boolean isSelectable() {
        return selectable;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#isSigned()
     */
    @Override
	public boolean isSigned() {
        return signed;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#isUpdatable()
     */
    @Override
	public boolean isUpdatable() {
        return updatable;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getRadix()
     */
    @Override
	public int getRadix() {
        return radix;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getNativeType()
     * @since 4.2
     */
    @Override
	public String getNativeType() {
        return nativeType;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getDistinctValues()
     * @since 4.3
     */
    @Override
	public int getDistinctValues() {
        return this.distinctValues;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.ColumnRecord#getNullValues()
     * @since 4.3
     */
    @Override
	public int getNullValues() {
        return this.nullValues;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param b
     */
    public void setAutoIncrementable(boolean b) {
        autoIncrementable = b;
    }

    /**
     * @param b
     */
    public void setCaseSensitive(boolean b) {
        caseSensitive = b;
    }

    /**
     * @param i
     */
    public void setCharOctetLength(int i) {
        charOctetLength = i;
    }

    /**
     * @param b
     */
    public void setCurrency(boolean b) {
        currency = b;
    }

    /**
     * @param string
     */
    public void setRuntimeType(String string) {
        runtimeTypeName = string;
    }

    /**
     * @param string
     */
    public void setDatatypeUUID(String string) {
        datatypeUUID = string;
    }

    /**
     * @param object
     */
    public void setDefaultValue(String object) {
        defaultValue = object;
    }

    /**
     * @param b
     */
    public void setFixedLength(boolean b) {
        fixedLength = b;
    }

    /**
     * @param i
     */
    public void setLength(int i) {
        length = i;
    }

    /**
     * @param i
     */
    public void setNullType(int i) {
        nullType = i;
    }

    /**
     * @param object
     */
    public void setMaxValue(Object object) {
        maxValue = object;
    }

    /**
     * @param object
     */
    public void setMinValue(Object object) {
        minValue = object;
    }

    /**
     * @param i
     */
    public void setPrecision(int i) {
        precision = i;
    }

    /**
     * @param i
     */
    public void setPosition(int i) {
        position = i;
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
    public void setSearchType(int s) {
        searchType = s;
    }

    /**
     * @param b
     */
    public void setSelectable(boolean b) {
        selectable = b;
    }

    /**
     * @param b
     */
    public void setSigned(boolean b) {
        signed = b;
    }

    /**
     * @param b
     */
    public void setUpdatable(boolean b) {
        updatable = b;
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
    public void setFormat(String string) {
        format = string;
    }

    /**
     * @param distinctValues The distinctValues to set.
     * @since 4.3
     */
    public void setDistinctValues(int distinctValues) {
        this.distinctValues = distinctValues;
    }

    /**
     * @param nullValues The nullValues to set.
     * @since 4.3
     */
    public void setNullValues(int nullValues) {
        this.nullValues = nullValues;
    }

    /**
     * @param nativeType The nativeType to set.
     * @since 4.2
     */
    public void setNativeType(String nativeType) {
        this.nativeType = nativeType;
    }

    /**
     * @param b
     */
    public void setTransformationInputParameter(boolean b) {
        this.tranformationInputParameter = b;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(getClass().getSimpleName());
        sb.append(" name="); //$NON-NLS-1$
        sb.append(getName());
        sb.append(", nameInSource="); //$NON-NLS-1$
        sb.append(getNameInSource());
        sb.append(", uuid="); //$NON-NLS-1$
        sb.append(getUUID());
        sb.append(", pathInModel="); //$NON-NLS-1$
        sb.append(getPath());
        return sb.toString();
    }

}