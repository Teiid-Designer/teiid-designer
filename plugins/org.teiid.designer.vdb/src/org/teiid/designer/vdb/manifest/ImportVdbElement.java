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
import org.teiid.designer.vdb.VdbImportVdbEntry;

/**
 * @since 8.0
 */
@XmlAccessorType( XmlAccessType.NONE )
@XmlType( name = "" )
public class ImportVdbElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private CommentSets comments;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlAttribute( name = "version", required = true )
    private String version = "1";

    @XmlAttribute( name = "import-data-policies" )
    private boolean importDataPolicies = true;

    /**
     * Used by JAXB
     */
    public ImportVdbElement() {
    }

    /**
     * Used to save a model entry
     * 
     * @param entry
     */
    ImportVdbElement( final VdbImportVdbEntry entry ) {
        name = entry.getName();
        version = entry.getVersion();
        importDataPolicies = entry.isImportDataPolicies();

        getComments().add(entry.getComments());
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return version
     */
    public String getVersion() {
    	return version;
    }

    /**
     * @return importDataPolicies
     */
    public boolean isImportDataPolicies() {
    	return importDataPolicies;
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
