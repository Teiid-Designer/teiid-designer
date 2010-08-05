/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.manifest;

import static org.teiid.designer.vdb.Vdb.Xml.PREVIEW;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbEntry;
import org.teiid.designer.vdb.VdbModelEntry;

/**
 * 
 */
// Serialize only annotated fields & properties
@XmlAccessorType( XmlAccessType.NONE )
// Map this class to the type of the top-level element, which is defined using an anonymous type
@XmlType( name = "" )
// Associate this class to the root element
@XmlRootElement( name = "vdb" )
public class VdbElement implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlAttribute( name = "name", required = true )
    private String name;

    @XmlAttribute( name = "version", required = true )
    private int version;

    @XmlElement( name = "description" )
    private String description;

    @XmlElement( name = "property", type = PropertyElement.class )
    private List<PropertyElement> properties;

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
     * @param vdb
     */
    public VdbElement( final Vdb vdb ) {
        name = vdb.getName().removeFileExtension().lastSegment();
        description = vdb.getDescription();
        version = 1;
        for (final VdbEntry entry : vdb.getEntries())
            getEntries().add(new EntryElement(entry));
        for (final VdbModelEntry modelEntry : vdb.getModelEntries())
            getModels().add(new ModelElement(modelEntry));
        getProperties().add(new PropertyElement(PREVIEW, Boolean.toString(vdb.isPreview())));
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
        if (entries == null) entries = new ArrayList<EntryElement>();
        return entries;
    }

    /**
     * @return models
     */
    public List<ModelElement> getModels() {
        if (models == null) models = new ArrayList<ModelElement>();
        return models;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @return The list of properties for this entry; never <code>null</code>
     */
    public List<PropertyElement> getProperties() {
        if (properties == null) properties = new ArrayList<PropertyElement>();
        return properties;
    }

    /**
     * @return version
     */
    public int getVersion() {
        return version;
    }
}
