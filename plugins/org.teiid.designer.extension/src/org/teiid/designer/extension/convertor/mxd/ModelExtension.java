
package org.teiid.designer.extension.convertor.mxd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="modelType" maxOccurs="2" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="PHYSICAL"/>
 *               &lt;enumeration value="VIRTUAL"/>
 *               &lt;enumeration value="FUNCTION"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="extendedMetaclass" type="{http://www.jboss.org/teiiddesigner/ext/2012}metaclassType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="namespacePrefix" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="namespaceUri" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="metamodelUri" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="version" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description",
    "modelType",
    "extendedMetaclass"
})
@XmlRootElement(name = "modelExtension", namespace = "http://www.jboss.org/teiiddesigner/ext/2012")
public class ModelExtension {

    @XmlElement(namespace = "http://www.jboss.org/teiiddesigner/ext/2012")
    protected String description;
    @XmlElement(namespace = "http://www.jboss.org/teiiddesigner/ext/2012")
    protected List<String> modelType;
    @XmlElement(namespace = "http://www.jboss.org/teiiddesigner/ext/2012", required = true)
    protected List<MetaclassType> extendedMetaclass;
    @XmlAttribute(name = "namespacePrefix", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String namespacePrefix;
    @XmlAttribute(name = "namespaceUri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String namespaceUri;
    @XmlAttribute(name = "metamodelUri", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String metamodelUri;
    @XmlAttribute(name = "version")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger version;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the modelType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the modelType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModelType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getModelType() {
        if (modelType == null) {
            modelType = new ArrayList<String>();
        }
        return this.modelType;
    }

    /**
     * Gets the value of the extendedMetaclass property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the extendedMetaclass property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExtendedMetaclass().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MetaclassType }
     * 
     * 
     */
    public List<MetaclassType> getExtendedMetaclass() {
        if (extendedMetaclass == null) {
            extendedMetaclass = new ArrayList<MetaclassType>();
        }
        return this.extendedMetaclass;
    }

    /**
     * Gets the value of the namespacePrefix property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    /**
     * Sets the value of the namespacePrefix property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamespacePrefix(String value) {
        this.namespacePrefix = value;
    }

    /**
     * Gets the value of the namespaceUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * Sets the value of the namespaceUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamespaceUri(String value) {
        this.namespaceUri = value;
    }

    /**
     * Gets the value of the metamodelUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetamodelUri() {
        return metamodelUri;
    }

    /**
     * Sets the value of the metamodelUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetamodelUri(String value) {
        this.metamodelUri = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getVersion() {
        if (version == null) {
            return new BigInteger("1");
        } else {
            return version;
        }
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setVersion(BigInteger value) {
        this.version = value;
    }

}
