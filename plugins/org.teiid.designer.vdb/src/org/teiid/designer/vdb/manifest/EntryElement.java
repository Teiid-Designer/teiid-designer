/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.manifest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.comments.CommentSets;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbIndexedEntry;

/**
 * 
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class EntryElement implements Serializable {

    /**
     */
    public static final String CHECKSUM = "checksum"; //$NON-NLS-1$

    /**
     */
    public static final String INDEX_NAME = "indexName"; //$NON-NLS-1$
    
    private static final long serialVersionUID = 1L;

    private CommentSets comments;

    /**
     * Path of entry
     */
    @XmlAttribute( name = "path" )
    protected String path;

    @XmlElement( name = "description" )
    protected String description;

    @XmlElement( name = "property", type = PropertyElement.class )
    private List<PropertyElement> properties;

    /**
     * Used by JAXB
     */
    public EntryElement() {
    	path = StringConstants.EMPTY_STRING;
    }

    EntryElement( final VdbEntry entry ) {
        path = entry.getPath().toString();
        if( entry.getDescription() != null && !entry.getDescription().isEmpty() ) {
        	description = entry.getDescription();
        }
        
        getProperties().add(new PropertyElement(CHECKSUM, Long.toString(entry.getChecksum())));

        if (entry instanceof VdbIndexedEntry)
            getProperties().add(new PropertyElement(INDEX_NAME, ((VdbIndexedEntry) entry).getIndexName()));
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
     * @return The list of properties for this entry; never <code>null</code>
     */
    public List<PropertyElement> getProperties() {
        if (properties == null) properties = new ArrayList<PropertyElement>();
        return properties;
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
