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
import javax.xml.bind.annotation.XmlType;
import org.eclipse.core.runtime.IPath;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class EntryElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "path" )
    private String path;

    @XmlElement( name = "description" )
    private String description;

    @XmlElement( name = "property", type = PropertyElement.class )
    private List<PropertyElement> properties;

    /**
     * Used by JAXB
     */
    public EntryElement() {
    }

    /**
     * @param name
     * @param description
     */
    public EntryElement( final IPath name,
                         final String description ) {
        this.path = name.toString();
        this.description = description;
    }

    /**
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return properties
     */
    public List<PropertyElement> getProperties() {
        return properties;
    }
}
