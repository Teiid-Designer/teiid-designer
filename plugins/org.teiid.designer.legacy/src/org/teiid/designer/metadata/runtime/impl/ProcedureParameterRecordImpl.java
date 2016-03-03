/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.impl;

import org.teiid.designer.metadata.runtime.ProcedureParameterRecord;

/**
 * ProcedureParameterRecordImpl
 *
 * @since 8.0
 */
public class ProcedureParameterRecordImpl extends AbstractMetadataRecord implements ProcedureParameterRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private String datatypeUUID;
    private String runtimeType;
    private String defaultValue;
    private int type;
    private int length;
    private int scale;
    private int radix;
    private int precision;
    private int nullType;
    private int position;
    private boolean optional;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ProcedureParameterRecordImpl() {
    	this(new MetadataRecordDelegate());	
    }
    
    protected ProcedureParameterRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureParameterRecord#getDefaultValue()
     */
    @Override
	public String getDefaultValue() {
        return defaultValue;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureParameterRecord#getType()
     */
    @Override
	public short getType() {
        return (short)type;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureParameterRecord#getDatatypeUUID()
     */
    @Override
	public String getDatatypeUUID() {
        return datatypeUUID;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureParameterRecord#getRuntimeType()
     */
    @Override
	public String getRuntimeType() {
        return runtimeType;
    }

    /**
     * @return
     */
    @Override
	public int getLength() {
        return length;
    }

    /**
     * @return
     */
    @Override
	public int getPrecision() {
        return precision;
    }

    /**
     * @return
     */
    @Override
	public int getScale() {
        return scale;
    }

    /**
     * @return
     */
    @Override
	public int getRadix() {
        return radix;
    }

    /**
     * @return
     */
    @Override
	public int getPosition() {
        return position;
    }

    /**
     * @return
     */
    @Override
	public int getNullType() {
        return nullType;
    }

	/*
	 * @see org.teiid.designer.metadata.runtime.ProcedureParameterRecord#isOptional()
	 */
	@Override
	public boolean isOptional() {
		return optional;
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
	public void setPrecision(int i) {
		precision = i;
	}

	/**
	 * @param i
	 */
	public void setScale(int i) {
		scale = i;
	}

	/**
	 * @param i
	 */
	public void setRadix(int i) {
		radix = i;
	}

    /**
     * @param i
     */
    public void setNullType(int i) {
        nullType = i;
    }

	/**
	 * @param i
	 */
	public void setPosition(int i) {
		position = i;
	}

	/**
	 * @param string
	 */
	public void setRuntimeType(String string) {
		runtimeType = string;
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
	 * @param i
	 */
	public void setType(int i) {
		type = i;
	}

    /**
     * @param b
     */
    public void setOptional(boolean b) {
        optional = b;
    }

}