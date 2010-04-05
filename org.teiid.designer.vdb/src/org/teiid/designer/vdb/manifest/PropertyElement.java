/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.manifest;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "property" )
public class PropertyElement implements Serializable {

    /**
     */
    public static final String BUILT_IN = "builtIn"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlAttribute( name = "value", required = true )
    private String value;

    /**
     * Used by JAXB
     */
    public PropertyElement() {
    }

    /**
     * @param name
     * @param value
     */
    public PropertyElement( final String name,
                            final String value ) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return value
     */
    public String getValue() {
        return value;
    }
}
