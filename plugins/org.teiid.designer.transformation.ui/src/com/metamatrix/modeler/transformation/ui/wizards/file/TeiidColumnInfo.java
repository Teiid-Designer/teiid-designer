/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;

/**
 * A <code>TeiidColumnInfo</code> defines extension properties for metaclasses within a metamodel.
 */
public class TeiidColumnInfo {
	public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
	public static final String INTEGER_DATATYPE = "integer"; //$NON-NLS-1$
	
	public static final int DEFAULT_WIDTH = 10;
	
    /**
     * The unique column name (never <code>null</code> or empty).
     */
	private String name;
	
	 /**
     * The unique column datatype (never <code>null</code> or empty).
     */
	private String datatype;
	
	 /**
     * The column width value
     */
	private int width = DEFAULT_WIDTH;
	
	
	 /**
     * The unique column datatype (never <code>null</code> or empty).
     */
	private boolean forOrdinality;
	
	 /**
     * The unique column datatype (never <code>null</code> or empty).
     */
	private String defaultValue = StringUtilities.EMPTY_STRING;
	
	 /**
     * The unique column datatype (never <code>null</code> or empty).
     */
	private String xmlPath = StringUtilities.EMPTY_STRING;
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public TeiidColumnInfo(String name) {
		this(name, DEFAULT_DATATYPE);
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidColumnInfo(String name, String datatype) {
		super();
        CoreArgCheck.isNotEmpty(name, "name is null"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(datatype, "datatype is null"); //$NON-NLS-1$
        
		this.name = name;
		this.datatype = datatype;
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidColumnInfo(String name, String datatype, int width) {
		this(name, datatype);
        CoreArgCheck.isPositive(width, "width is zero or less"); //$NON-NLS-1$
        
		this.width = width;
	}
	
	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public TeiidColumnInfo(String name, boolean ordinality, String datatype, String defaultValue, String xmlPath ) {
		this(name, datatype);
        this.forOrdinality = ordinality;
        if( defaultValue == null ) {
        	this.defaultValue = StringUtilities.EMPTY_STRING;
        } else {
        	this.defaultValue = defaultValue;
        }
        if( xmlPath == null ) {
        	this.xmlPath = StringUtilities.EMPTY_STRING;
        } else {
        	this.xmlPath = xmlPath;
        }
	}

	/**
	 * 
	 * @return name the column name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public void setName(String name) {
		CoreArgCheck.isNotNull(name, "name is null"); //$NON-NLS-1$
		this.name = name;
	}

	/**
	 * 
	 * @return datatype the column datatype
	 */
	public String getDatatype() {
		return this.datatype;
	}

	/**
	 * 
	 * @param datatype the column datatype (never <code>null</code> or empty).
	 */
	public void setDatatype(String datatype) {
		CoreArgCheck.isNotNull(datatype, "datatype is null"); //$NON-NLS-1$
		this.datatype = datatype;
	}
	
	/**
	 * 
	 * @return name the column name
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * 
	 * @param name the column name (never <code>null</code> or empty).
	 */
	public void setWidth(int width) {
		CoreArgCheck.isPositive(width, "width is less than 1"); //$NON-NLS-1$
		this.width = width;
	}
	
	/**
	 * 
	 * @return defaultValue the column defaultValue
	 */
	public String getDefaultValue() {
		return this.defaultValue;
	}
	
	/**
	 * 
	 * @param defaultValue the column defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		if( defaultValue == null ) {
        	this.defaultValue = StringUtilities.EMPTY_STRING;
        } else {
        	this.defaultValue = defaultValue;
        }
	}

	/**
	 * 
	 * @param xmlPath the column xmlPath
	 */
	public void setXmlPath(String xmlPath) {
        if( xmlPath == null ) {
        	this.xmlPath = StringUtilities.EMPTY_STRING;
        } else {
        	this.xmlPath = xmlPath;
        }
	}
	
	/**
	 * 
	 * @return xmlPath the column xmlPath
	 */
	public String getXmlPath() {
		return this.xmlPath;
	}
	/**
	 * 
	 * @return forOrdinality the column forOrdinality
	 */
	public boolean getOrdinality() {
		return this.forOrdinality;
	}

	/**
	 * 
	 * @param forOrdinality the column forOrdinality
	 */
	public void setOrdinality(boolean value) {
		this.forOrdinality = value;
	}
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("Teiid Metadata Column Info: "); //$NON-NLS-1$
        text.append("name =").append(getName()); //$NON-NLS-1$
        text.append(", datatype =").append(getDatatype()); //$NON-NLS-1$

        return text.toString();
    }
}
