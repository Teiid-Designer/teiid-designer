
package org.teiid.designer.extension.convertor.mxd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * An extended property definition.
 * 
 * <p>Java class for propertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="propertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.jboss.org/teiiddesigner/ext/2012}displayType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="display" type="{http://www.jboss.org/teiiddesigner/ext/2012}displayType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="allowedValue" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="type" default="string">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="biginteger"/>
 *             &lt;enumeration value="bigdecimal"/>
 *             &lt;enumeration value="blob"/>
 *             &lt;enumeration value="boolean"/>
 *             &lt;enumeration value="byte"/>
 *             &lt;enumeration value="char"/>
 *             &lt;enumeration value="clob"/>
 *             &lt;enumeration value="date"/>
 *             &lt;enumeration value="double"/>
 *             &lt;enumeration value="float"/>
 *             &lt;enumeration value="integer"/>
 *             &lt;enumeration value="long"/>
 *             &lt;enumeration value="object"/>
 *             &lt;enumeration value="short"/>
 *             &lt;enumeration value="string"/>
 *             &lt;enumeration value="time"/>
 *             &lt;enumeration value="timestamp"/>
 *             &lt;enumeration value="xml"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="defaultValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="fixedValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="advanced" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="masked" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "propertyType", namespace = "http://www.jboss.org/teiiddesigner/ext/2012", propOrder = {
    "description",
    "display",
    "allowedValue"
})
public class PropertyType {

    @XmlElement(namespace = "http://www.jboss.org/teiiddesigner/ext/2012")
    protected List<DisplayType> description;
    @XmlElement(namespace = "http://www.jboss.org/teiiddesigner/ext/2012")
    protected List<DisplayType> display;
    @XmlElement(namespace = "http://www.jboss.org/teiiddesigner/ext/2012")
    protected List<String> allowedValue;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "required")
    protected Boolean required;
    @XmlAttribute(name = "defaultValue")
    protected String defaultValue;
    @XmlAttribute(name = "fixedValue")
    protected String fixedValue;
    @XmlAttribute(name = "advanced")
    protected Boolean advanced;
    @XmlAttribute(name = "masked")
    protected Boolean masked;
    @XmlAttribute(name = "index")
    protected Boolean index;

    /**
     * Gets the value of the description property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the description property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DisplayType }
     * 
     * 
     */
    public List<DisplayType> getDescription() {
        if (description == null) {
            description = new ArrayList<DisplayType>();
        }
        return this.description;
    }

    /**
     * Gets the value of the display property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the display property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDisplay().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DisplayType }
     * 
     * 
     */
    public List<DisplayType> getDisplay() {
        if (display == null) {
            display = new ArrayList<DisplayType>();
        }
        return this.display;
    }

    /**
     * Gets the value of the allowedValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allowedValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowedValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAllowedValue() {
        if (allowedValue == null) {
            allowedValue = new ArrayList<String>();
        }
        return this.allowedValue;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        if (type == null) {
            return "string";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the required property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getRequired() {
        if (required == null) {
            return false;
        } else {
            return required;
        }
    }

    /**
     * Sets the value of the required property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequired(Boolean value) {
        this.required = value;
    }

    /**
     * Gets the value of the defaultValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    /**
     * Gets the value of the fixedValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFixedValue() {
        return fixedValue;
    }

    /**
     * Sets the value of the fixedValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFixedValue(String value) {
        this.fixedValue = value;
    }

    /**
     * Gets the value of the advanced property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getAdvanced() {
        if (advanced == null) {
            return false;
        } else {
            return advanced;
        }
    }

    /**
     * Sets the value of the advanced property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAdvanced(Boolean value) {
        this.advanced = value;
    }

    /**
     * Gets the value of the masked property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getMasked() {
        if (masked == null) {
            return false;
        } else {
            return masked;
        }
    }

    /**
     * Sets the value of the masked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMasked(Boolean value) {
        this.masked = value;
    }

    /**
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean getIndex() {
        if (index == null) {
            return true;
        } else {
            return index;
        }
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndex(Boolean value) {
        this.index = value;
    }

}
