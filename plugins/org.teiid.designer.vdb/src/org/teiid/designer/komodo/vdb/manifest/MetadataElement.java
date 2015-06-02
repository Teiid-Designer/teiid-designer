/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb.manifest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "metadata" )
public class MetadataElement implements Serializable {
			
    private static final long serialVersionUID = 1L;
    
    @XmlAttribute( name = "type", required = true )
    private String type;
    
    @XmlValue
    private String schemaText;
    
    /**
     * Used by JAXB when loading a VDB
     */
    public MetadataElement() {
    	
    }
    
    /**
     * Used by JAXB when loading a VDB
     * @param schemaText the schema DDL text
     * @param type the is ddl type (DDL or 
     */
    public MetadataElement(String schemaText, String type) {
    	this.schemaText = schemaText;
    	this.type = type;
    }

	/**
	 * @return the constraint
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @return the sql
	 */
	public String getSchemaText() {
		return this.schemaText;
	}
}