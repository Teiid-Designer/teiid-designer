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
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * 
 */
public class ModelExtensionDefinitionWriter {

    private File definitionSchemaFile;

    public ModelExtensionDefinitionWriter( File schemaFile ) {
        this.definitionSchemaFile = schemaFile;
    }

    /**
     * Create a Model Extension Definition template, based on the modelExtension.xsd
     * 
     * @return empty template model extension definition (never <code>null</code>)
     * @throws Exception if the definition file is <code>null</code> or if there is a problem parsing the file
     */
    public InputStream write( ModelExtensionDefinition med ) throws IllegalStateException {
        InputStream inputStream = null;

        try {
            // Create a temp file for the new mxd
            File tempFile = File.createTempFile("MxdTemp", ".mxd"); //$NON-NLS-1$ //$NON-NLS-2$

            FileOutputStream tempOutputStream = new FileOutputStream(tempFile);

            // Create a copy ...
            // NOTE: THIS WORKS IF WE USE THE org.apache.xerces LIBRARY.
            // The problem is that these other implementations don't write out the
            // namespace declarations for the imported nodes
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setValidating(true);

            documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", //$NON-NLS-1$
                                                "http://www.w3.org/2001/XMLSchema"); //$NON-NLS-1$

            documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", //$NON-NLS-1$
                                                new InputSource(new FileInputStream(definitionSchemaFile)));

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            DOMImplementation domImpl = documentBuilder.getDOMImplementation();
            Document document = domImpl.createDocument(ExtensionConstants.Namespaces.NS_MED_VALUE,
                                                       ExtensionConstants.Elements.MODEL_EXTENSION,
                                                       null);

            Element modelExtensionElem = document.getDocumentElement();
            modelExtensionElem.setAttributeNS(ExtensionConstants.Namespaces.NS_KEY,
                                              "xmlns:" + ExtensionConstants.Namespaces.NS_XSI, ExtensionConstants.Namespaces.NS_XSI_VALUE); //$NON-NLS-1$  
            modelExtensionElem.setAttributeNS(ExtensionConstants.Namespaces.NS_KEY,
                                              "xmlns:" + ExtensionConstants.Namespaces.NS_MED, ExtensionConstants.Namespaces.NS_MED_VALUE); //$NON-NLS-1$ 

            String NS_MED_COLON = ExtensionConstants.Namespaces.NS_MED + ":"; //$NON-NLS-1$

            // -----------------------------------------
            // modelExtension element
            // -----------------------------------------
            // Set the modelExtension element attributes
            Attr attr = document.createAttribute(ExtensionConstants.Namespaces.NS_SCHEMALOC);
            attr.setValue(ExtensionConstants.Namespaces.NS_MED_VALUE + " " + ExtensionConstants.SCHEMA_FILENAME); //$NON-NLS-1$
            modelExtensionElem.setAttributeNode(attr);
            attr = document.createAttribute(ExtensionConstants.Attributes.METAMODEL_URI);
            attr.setValue(med.getMetamodelUri());
            modelExtensionElem.setAttributeNode(attr);
            attr = document.createAttribute(ExtensionConstants.Attributes.NAMESPACE_URI);
            attr.setValue(med.getNamespaceUri());
            modelExtensionElem.setAttributeNode(attr);
            attr = document.createAttribute(ExtensionConstants.Attributes.NAMESPACE_PREFIX);
            attr.setValue(med.getNamespacePrefix());
            modelExtensionElem.setAttributeNode(attr);
            attr = document.createAttribute(ExtensionConstants.Attributes.VERSION);
            attr.setValue(String.valueOf(med.getVersion()));
            modelExtensionElem.setAttributeNode(attr);

            // -----------------------------------------
            // Child - description element
            // -----------------------------------------
            Element descriptionElem = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.DESCRIPTION);

            Text descriptionText = document.createTextNode(med.getDescription());
            descriptionElem.appendChild(descriptionText);
            modelExtensionElem.appendChild(descriptionElem);

            // -----------------------------------------
            // Extended metamodel class elements
            // -----------------------------------------
            String[] extendedMetaclasses = med.getExtendedMetaclasses();
            for (int i = 0; i < extendedMetaclasses.length; i++) {
                String extendedMetaclassName = extendedMetaclasses[i];
                // Extended Metaclass Element
                Element extendedMetaclassElem = document.createElement(NS_MED_COLON
                                                                       + ExtensionConstants.Elements.EXTENDED_METACLASS);
                attr = document.createAttribute(ExtensionConstants.Attributes.NAME);
                attr.setValue(extendedMetaclassName);
                extendedMetaclassElem.setAttributeNode(attr);
                modelExtensionElem.appendChild(extendedMetaclassElem);
                Collection<ModelExtensionPropertyDefinition> properties = med.getPropertyDefinitions(extendedMetaclassName);
                for (ModelExtensionPropertyDefinition propDefn : properties) {
                    Element propertyElem = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.PROPERTY);
                    extendedMetaclassElem.appendChild(propertyElem);

                    // Attributes
                    String simpleId = propDefn.getSimpleId();
                    String type = propDefn.getRuntimeType();

                    attr = document.createAttribute(ExtensionConstants.Attributes.NAME);
                    attr.setValue(simpleId);
                    propertyElem.setAttributeNode(attr);
                    attr = document.createAttribute(ExtensionConstants.Attributes.TYPE);
                    attr.setValue(type);
                    propertyElem.setAttributeNode(attr);

                    // Child Elements
                    String displayName = propDefn.getDisplayName();
                    String descrip = propDefn.getDescription();

                    Element descrElement = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.DESCRIPTION);
                    if (descrip != null) {
                        Text descripText = document.createTextNode(descrip);
                        descrElement.appendChild(descripText);
                        propertyElem.appendChild(descrElement);
                    }
                    Element displayElement = document.createElement(NS_MED_COLON + ExtensionConstants.Elements.DISPLAY);
                    if (displayName != null) {
                        Text displayText = document.createTextNode(displayName);
                        displayElement.appendChild(displayText);
                        propertyElem.appendChild(displayElement);
                    }
                }
            }

            // Output it
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(tempOutputStream);
            TransformerFactory tFactory = TransformerFactory.newInstance();

            Transformer transformer = tFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); //$NON-NLS-1$ //$NON-NLS-2$
            transformer.transform(domSource, streamResult);

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

}
