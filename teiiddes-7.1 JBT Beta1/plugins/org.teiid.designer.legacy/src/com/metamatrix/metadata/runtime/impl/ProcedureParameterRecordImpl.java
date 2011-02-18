/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord;

/**
 * ProcedureParameterRecordImpl
 */
public class ProcedureParameterRecordImpl extends AbstractMetadataRecord implements ProcedureParameterRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private String datatypeUUID;
    private String runtimeType;
    private Object defaultValue;
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
     * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#getDefaultValue()
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#getType()
     */
    public short getType() {
        return (short)type;
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#getDatatypeUUID()
     */
    public String getDatatypeUUID() {
        return datatypeUUID;
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#getRuntimeType()
     */
    public String getRuntimeType() {
        return runtimeType;
    }

    /**
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * @return
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @return
     */
    public int getScale() {
        return scale;
    }

    /**
     * @return
     */
    public int getRadix() {
        return radix;
    }

    /**
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return
     */
    public int getNullType() {
        return nullType;
    }

	/*
	 * @see com.metamatrix.modeler.core.metadata.runtime.ProcedureParameterRecord#isOptional()
	 */
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
	public void setDefaultValue(Object object) {
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