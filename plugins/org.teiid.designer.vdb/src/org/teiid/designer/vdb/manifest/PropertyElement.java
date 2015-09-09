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
import org.teiid.designer.comments.CommentSets;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "property" )
public class PropertyElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private CommentSets comments;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlAttribute( name = "value", required = true )
    private String value;

    /**
     * Used by JAXB
     */
    public PropertyElement() {
    }

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
    
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("PropertyElement : "); //$NON-NLS-1$
        text.append("\n\t name  = ").append(getName()); //$NON-NLS-1$
        text.append("\n\t value = ").append(getValue()); //$NON-NLS-1$

        return text.toString();
	}

	/**
     * @param visitor
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return comments for this element
     */
    public CommentSets getComments() {
        if (this.comments == null)
            this.comments = new CommentSets();

        return this.comments;
    }
}
