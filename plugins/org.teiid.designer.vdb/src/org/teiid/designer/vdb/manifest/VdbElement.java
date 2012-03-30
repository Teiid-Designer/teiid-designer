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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.teiid.designer.vdb.TranslatorOverride;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbDataRole;
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

    @XmlElement( name = "translator", type = TranslatorElement.class )
    private List<TranslatorElement> translators;

    @XmlElement( name = "data-role", type = DataRoleElement.class )
    private List<DataRoleElement> dataRoles;
    
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
        for (final TranslatorOverride translator : vdb.getTranslators())
            getTranslators().add(new TranslatorElement(translator));
        for (final VdbDataRole dataPolicyEntry : vdb.getDataPolicyEntries())
            getDataPolicies().add(new DataRoleElement(dataPolicyEntry));
        getProperties().add(new PropertyElement(Vdb.Xml.PREVIEW, Boolean.toString(vdb.isPreview())));
        // The Vdb object stores timeout in seconds, but we will persist to Vdb manifest in millis for teiid.
        if( vdb.getQueryTimeout() > 0 ) {
            int timeoutMillis = vdb.getQueryTimeout() * 1000;
            getProperties().add(new PropertyElement(Vdb.Xml.QUERY_TIMEOUT, Integer.toString(timeoutMillis)));
        }
    }

    /**
     * @return entries
     */
    public List<DataRoleElement> getDataPolicies() {
        if (dataRoles == null) dataRoles = new ArrayList<DataRoleElement>();
        return dataRoles;
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
     * @return the translators
     */
    public List<TranslatorElement> getTranslators() {
        if (translators == null) translators = new ArrayList<TranslatorElement>();
        return translators;
    }

    /**
     * @return version
     */
    public int getVersion() {
        return version;
    }
}
