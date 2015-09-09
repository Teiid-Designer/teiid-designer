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
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.comments.CommentSets;
import org.teiid.designer.core.translators.TranslatorOverrideProperty;
import org.teiid.designer.vdb.TranslatorOverride;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "")
public class TranslatorElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private CommentSets comments;

    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "type", required = true)
    private String type;

    @XmlAttribute(name = "description", required = false)
    private String description;

    @XmlElement(name = "property", required = true, type = PropertyElement.class)
    private List<PropertyElement> properties = new ArrayList<PropertyElement>();

    /**
     * Used by JAXB when loading a VDB
     */
    TranslatorElement() {
        // nothing to do
    }

    TranslatorElement( TranslatorOverride translatorOverride ) {
        this.name = translatorOverride.getName();
        this.type = translatorOverride.getType();
        this.description = translatorOverride.getDescription();

        // process properties
        for (TranslatorOverrideProperty prop : translatorOverride.getOverrideProperties()) {
            if (!StringUtilities.isEmpty(prop.getOverriddenValue())) {
                this.properties.add(new PropertyElement(prop.getDefinition().getId(), prop.getOverriddenValue()));
            }
        }

        // Append any comments to the property elements
        for (PropertyElement propElement : getProperties()) {
            CommentSets propertyComments = translatorOverride.getPropertyComments(propElement.getName());
            propElement.getComments().add(propertyComments);
        }

        getComments().add(translatorOverride.getComments());
    }

    /**
     * @return the translator override description (can be <code>null</code> or empty)
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the translator override identifier (never <code>null</code> or empty)
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the list of overridden translator properties (never <code>null</code>)
     */
    public List<PropertyElement> getProperties() {
        return this.properties;
    }

    /**
     * @return the translator type being overridden (never <code>null</code> or empty)
     */
    public String getType() {
        return this.type;
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
