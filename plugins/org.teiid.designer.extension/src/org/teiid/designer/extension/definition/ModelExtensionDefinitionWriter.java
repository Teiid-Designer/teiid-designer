/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.Translation;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * 
 */
public class ModelExtensionDefinitionWriter {

    private static final String NS_MED_COLON = ExtensionConstants.Namespaces.NS_MED + ":"; //$NON-NLS-1$

    /**
     * Create a Model Extension Definition template, based on the modelExtension.xsd.
     * 
     * @param med the model extension definition being written (never <code>null</code>)
     * @return the stream where the definition was written (never <code>null</code>)
     * @throws IllegalStateException if the definition file is <code>null</code> or if there is a problem creating the stream
     */
    public InputStream writeAsStream( ModelExtensionDefinition med ) throws IllegalStateException {
        InputStream inputStream = null;

        try {
            // Create a temp file for the new mxd
            File tempFile = File.createTempFile("MxdTemp", ExtensionConstants.DOT_MED_EXTENSION); //$NON-NLS-1$
            FileOutputStream tempOutputStream = new FileOutputStream(tempFile);
            StreamResult streamResult = new StreamResult(tempOutputStream);
            transform(med, streamResult);
            inputStream = new FileInputStream(tempFile);
        } catch (Exception e) {
            IllegalStateException error = null;

            if (e instanceof IllegalStateException) {
                error = (IllegalStateException)e;
            } else {
                error = new IllegalStateException(e);
            }

            throw error;
        }

        return inputStream;
    }

    /**
     * Create a text representation of the specified model extension definition suitable to be saved in a *.mxd file.
     * 
     * @param med the model extension definition being written (never <code>null</code>)
     * @return a textual representation of the definition (never <code>null</code>)
     * @throws IllegalStateException if the definition file is <code>null</code> or if there is a problem parsing the file
     */
    public String writeAsText( ModelExtensionDefinition med ) throws IllegalStateException {
        StringWriter stringWriter = new StringWriter();
        Result streamResult = new StreamResult(stringWriter);
        transform(med, streamResult);
        return stringWriter.getBuffer().toString();
    }

    /**
     * Transforms the Model Extension Definition into the specified XML result.
     * 
     * @param med the model extension definition being transformed (never <code>null</code>)
     * @throws Exception if the definition file is <code>null</code> or if there is a problem parsing the file
     */
    private void transform( ModelExtensionDefinition med, 
                            Result result ) throws IllegalStateException {
        CoreArgCheck.isNotNull(med, "med is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(result, "result is null"); //$NON-NLS-1$

        try {
            // Create a copy ...
            // NOTE: THIS WORKS IF WE USE THE org.apache.xerces LIBRARY.
            // The problem is that these other implementations don't write out the
            // namespace declarations for the imported nodes
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(false);

            documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", //$NON-NLS-1$
                                                "http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            DOMImplementation domImpl = documentBuilder.getDOMImplementation();
            Document document = domImpl.createDocument(ExtensionConstants.Namespaces.NS_MED_VALUE,
                                                       ExtensionConstants.Elements.MODEL_EXTENSION, null);

            // --------------------------------------------------------------
            // Get the root Element.
            // - Set Attributes and add the Description
            // --------------------------------------------------------------
            Element modelExtensionElem = document.getDocumentElement();
            setModelExtensionElementAttributes(document, modelExtensionElem, med);

            // ------------------------------------------------
            // Create Element for each extended metaclass name
            // ------------------------------------------------
            String[] extendedMetaclassNames = med.getExtendedMetaclasses();
            Element[] extendedMetaclassElems = createExtendedMetaclassElements(document, modelExtensionElem, extendedMetaclassNames);

            // ----------------------------------------------------------------
            // Iterate Extended Metaclass Elements, adding properties for each
            // ----------------------------------------------------------------
            for (int i = 0; i < extendedMetaclassNames.length; i++) {
                String metaclassName = extendedMetaclassNames[i];
                Element metaclassElem = extendedMetaclassElems[i];

                // Create the property elements for this metaclass
                Collection<ModelExtensionPropertyDefinition> properties = med.getPropertyDefinitions(metaclassName);
                createProperyElements(document, metaclassElem, properties);
            }

            // Output the document
            DOMSource domSource = new DOMSource(document);
            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
            transformer.transform(domSource, result);
        } catch (Exception e) {
            IllegalStateException error = null;

            if (e instanceof IllegalStateException) {
                error = (IllegalStateException)e;
            } else {
                error = new IllegalStateException(e);
            }

            throw error;
        }
    }

    /**
     * Set the Attributes and create the description on the ModelExtensionElement
     * 
     * @param document the document being worked on
     * @param extensionElement the model extension element
     * @param med the med to use in configuring the extension element
     */
    private void setModelExtensionElementAttributes( Document document,
                                                     Element modelExtensionElem,
                                                     ModelExtensionDefinition med ) {
        modelExtensionElem.setAttributeNS(ExtensionConstants.Namespaces.NS_KEY,
                                          "xmlns:" + ExtensionConstants.Namespaces.NS_XSI, ExtensionConstants.Namespaces.NS_XSI_VALUE); //$NON-NLS-1$  
        modelExtensionElem.setAttributeNS(ExtensionConstants.Namespaces.NS_KEY,
                                          "xmlns:" + ExtensionConstants.Namespaces.NS_MED, ExtensionConstants.Namespaces.NS_MED_VALUE); //$NON-NLS-1$ 

        // -----------------------------------------
        // modelExtensionElement - Attributes
        // -----------------------------------------
        Attr attr = document.createAttribute(ExtensionConstants.Namespaces.NS_SCHEMALOC);
        attr.setValue(ExtensionConstants.Namespaces.NS_MED_VALUE + " " + ExtensionConstants.SCHEMA_FILENAME); //$NON-NLS-1$
        modelExtensionElem.setAttributeNode(attr);

        // Metamodel URI
        attr = document.createAttribute(ExtensionConstants.Attributes.METAMODEL_URI);
        attr.setValue(med.getMetamodelUri());
        modelExtensionElem.setAttributeNode(attr);

        // Namespace URI
        attr = document.createAttribute(ExtensionConstants.Attributes.NAMESPACE_URI);
        attr.setValue(med.getNamespaceUri());
        modelExtensionElem.setAttributeNode(attr);

        // Namespace Prefix
        attr = document.createAttribute(ExtensionConstants.Attributes.NAMESPACE_PREFIX);
        attr.setValue(med.getNamespacePrefix());
        modelExtensionElem.setAttributeNode(attr);

        // Version
        attr = document.createAttribute(ExtensionConstants.Attributes.VERSION);
        attr.setValue(String.valueOf(med.getVersion()));
        modelExtensionElem.setAttributeNode(attr);

        // -----------------------------------------
        // Description - child element
        // -----------------------------------------
        String medDescription = med.getDescription();
        if (medDescription != null && !medDescription.isEmpty()) {
            Element descriptionElem = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.DESCRIPTION);
            modelExtensionElem.appendChild(descriptionElem);
            Text descriptionText = document.createTextNode(med.getDescription());
            descriptionElem.appendChild(descriptionText);
        }
    }

    /**
     * Create Elements for each extended Metaclass and add to the root modelExtensionElement
     * 
     * @param document the document being worked on
     * @param rootElem the root element (modelExtensionElement)
     * @param extendedMetaclassnames the list of metaclass names being extended
     * @return the list of elements created, which correspond to the supplied names
     */
    private Element[] createExtendedMetaclassElements( Document document,
                                                       Element rootElem,
                                                       String[] extendedMetaclassNames ) {
        Element[] extendedMetaclassElems = new Element[extendedMetaclassNames.length];

        Attr attr = document.createAttribute(ExtensionConstants.Namespaces.NS_SCHEMALOC);

        // For each extended metaclass Name, create an element and append it to the root.
        for (int i = 0; i < extendedMetaclassNames.length; i++) {
            String extendedMetaclassName = extendedMetaclassNames[i];

            // Create the Element
            Element extendedMetaclassElem = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.EXTENDED_METACLASS);
            // Append element to the document root
            rootElem.appendChild(extendedMetaclassElem);

            // Set the Name attribute
            attr = document.createAttribute(ExtensionConstants.Attributes.NAME);
            attr.setValue(extendedMetaclassName);
            extendedMetaclassElem.setAttributeNode(attr);

            // Set element on the returned element array
            extendedMetaclassElems[i] = extendedMetaclassElem;
        }

        return extendedMetaclassElems;
    }

    /**
     * Create the property Elements for the supplied metaclass Element, setting its attributes from the supplied collection of
     * ModelExtensionPropertyDefinitions
     * 
     * @param document the document being worked on
     * @param metaclassElem the metaclass element
     * @param properties the collection of property defns used to create property elems
     */
    private void createProperyElements( Document document,
                                        Element metaclassElem,
                                        Collection<ModelExtensionPropertyDefinition> properties ) {

        // --------------------------------------------------------------------
        // Property Elements
        // Iterate over the collection of ModelExtensionPropertyDefinitions.
        // --------------------------------------------------------------------
        for (ModelExtensionPropertyDefinition propDefn : properties) {
            Attr attr = document.createAttribute(ExtensionConstants.Namespaces.NS_SCHEMALOC);

            // --------------------------------------------------------
            // Create Property Element and append to Metaclass Element
            // --------------------------------------------------------
            Element propertyElem = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.PROPERTY);
            metaclassElem.appendChild(propertyElem);

            // --------------------------------------
            // Set the Property Element Attributes
            // --------------------------------------

            // Name Attribute
            String simpleId = propDefn.getSimpleId();
            attr = document.createAttribute(ExtensionConstants.Attributes.NAME);
            attr.setValue(simpleId);
            propertyElem.setAttributeNode(attr);

            // Type Attribute
            String type = propDefn.getRuntimeType();
            attr = document.createAttribute(ExtensionConstants.Attributes.TYPE);
            attr.setValue(type);
            propertyElem.setAttributeNode(attr);

            // Default Value Attribute
            String defaultValue = propDefn.getDefaultValue();
            attr = document.createAttribute(ExtensionConstants.Attributes.DEFAULT_VALUE);
            attr.setValue(defaultValue);
            propertyElem.setAttributeNode(attr);

            // Fixed Value Attribute
            String fixedValue = propDefn.getFixedValue();
            attr = document.createAttribute(ExtensionConstants.Attributes.FIXED_VALUE);
            attr.setValue(fixedValue);
            propertyElem.setAttributeNode(attr);

            // Required Attribute
            boolean isRequired = propDefn.isRequired();
            attr = document.createAttribute(ExtensionConstants.Attributes.REQUIRED);
            attr.setValue(Boolean.toString(isRequired));
            propertyElem.setAttributeNode(attr);

            // Advanced Attribute
            boolean isAdvanced = propDefn.isAdvanced();
            attr = document.createAttribute(ExtensionConstants.Attributes.ADVANCED);
            attr.setValue(Boolean.toString(isAdvanced));
            propertyElem.setAttributeNode(attr);

            // Masked Attribute
            boolean isMasked = propDefn.isMasked();
            attr = document.createAttribute(ExtensionConstants.Attributes.MASKED);
            attr.setValue(Boolean.toString(isMasked));
            propertyElem.setAttributeNode(attr);

            // Indexed Attribute
            boolean isIndexed = propDefn.shouldBeIndexed();
            attr = document.createAttribute(ExtensionConstants.Attributes.INDEX);
            attr.setValue(Boolean.toString(isIndexed));
            propertyElem.setAttributeNode(attr);

            // ----------------------------------------------
            // Create the Property Element child elements
            // ----------------------------------------------

            // -------------------------------
            // Display Name Elements
            // - can be multiple locales
            // -------------------------------
            Collection<Translation> displayNames = propDefn.getDisplayNames();
            for (Translation displayTranslation : displayNames) {
                String dnLocale = displayTranslation.getLocale().toString();
                String dnName = displayTranslation.getTranslation();

                if (dnLocale != null) {
                    Element displayElement = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.DISPLAY);
                    // Set the displayName text on the element.
                    Text displayText = document.createTextNode(dnName);
                    displayElement.appendChild(displayText);

                    // Set the locale attribute on the display element
                    attr = document.createAttribute(ExtensionConstants.Attributes.LOCALE);
                    attr.setValue(dnLocale);
                    displayElement.setAttributeNode(attr);

                    // append display element child to the property element
                    propertyElem.appendChild(displayElement);
                }
            }

            // -------------------------------
            // Description Elements
            // - can be multiple locales
            // -------------------------------
            Collection<Translation> descriptions = propDefn.getDescriptions();
            for (Translation descriptionTranslation : descriptions) {
                String descriptionLocale = descriptionTranslation.getLocale().toString();
                String descriptionName = descriptionTranslation.getTranslation();

                if (descriptionLocale != null) {
                    Element descrElement = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.DESCRIPTION);

                    // Set the description text on the element.
                    Text descriptionText = document.createTextNode(descriptionName);
                    descrElement.appendChild(descriptionText);

                    // Set the locale attribute on the description element
                    attr = document.createAttribute(ExtensionConstants.Attributes.LOCALE);
                    attr.setValue(descriptionLocale);
                    descrElement.setAttributeNode(attr);

                    // append description element child to the property element
                    propertyElem.appendChild(descrElement);
                }
            }

            // -------------------------------
            // Allowable Value elements
            // -------------------------------
            String[] allowedValues = propDefn.getAllowedValues();
            for (int i = 0; i < allowedValues.length; i++) {
                String allowedValueStr = allowedValues[i];
                Element allowedValueElement = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.ALLOWED_VALUE);
                // Set the allowedValue text on the element. Append the element to the property element.
                Text elemText = document.createTextNode(allowedValueStr);
                allowedValueElement.appendChild(elemText);

                // append allowedValue element child to the property element
                propertyElem.appendChild(allowedValueElement);
            }

        }
    }

}
