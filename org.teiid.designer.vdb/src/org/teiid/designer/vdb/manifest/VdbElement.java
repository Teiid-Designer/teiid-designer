/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.manifest;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 */
// Serialize only annotated fields & properties
@XmlAccessorType( XmlAccessType.NONE )
// Map this class to the type of the top-level element, which is defined using an anonymous type
@XmlType( name = "" )
// Associate this class to the root element
@XmlRootElement
public class VdbElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlElement( name = "description" )
    private String description;

    @XmlElement( name = "model", required = true, type = ModelElement.class )
    private List<ModelElement> models;

    @XmlElement( name = "entry", type = EntryElement.class )
    private List<EntryElement> entries;

    /**
     * Used by JAXB
     */
    public VdbElement() {
    }

    /**
     * @param name
     * @param description
     */
    public VdbElement( final String name,
                        final String description ) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return entries
     */
    public List<EntryElement> getEntries() {
        return entries;
    }

    /**
     * @return models
     */
    public List<ModelElement> getModels() {
        return models;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }
}
