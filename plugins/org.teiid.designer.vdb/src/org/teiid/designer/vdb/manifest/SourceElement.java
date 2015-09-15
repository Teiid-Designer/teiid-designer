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
import org.teiid.designer.vdb.VdbSource;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class SourceElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlAttribute( name = "connection-jndi-name" )
    private String jndiName;

    @XmlAttribute( name = "translator-name", required = true )
    private String translatorName;

    /**
     * Used by JAXB
     */
    public SourceElement() {
    }

    SourceElement( final VdbSource source ) {
        name = source.getName();
        
        // Note: if jndi name is really empty, then the proper value is null so the attribute is not included
        // in the vdb.xml
        
        String tempName = source.getJndiName();
        if( tempName != null ) {
        	tempName = tempName.trim();
        	if( !tempName.isEmpty() ) {
        		jndiName = source.getJndiName();
        	}
        }
        
        translatorName = source.getTranslatorName();
    }

    /**
     * @return jndiName
     */
    public String getJndiName() {
        return jndiName;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return translatorName
     */
    public String getTranslatorName() {
        return translatorName;
    }

    /**
     * @param visitor
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
