/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * A <code>TeiidColumnInfo</code> defines extension properties for metaclasses within a metamodel.
 */
public class TeiidColumnInfo {
	public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
	
    /**
     * The unique column name (never <code>null</code> or empty).
     */
	private String name;
	
	 /**
     * The unique column datatype (never <code>null</code> or empty).
     */
	private String datatype;
	
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
