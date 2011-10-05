/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static org.teiid.designer.extension.ExtensionPlugin.PLUGIN_ID;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.teiid.designer.extension.Messages;
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

    public ModelExtensionDefinitionWriter() throws IllegalStateException {
        try {
            // Model Extension Schema
            final String SCHEMA_FILE = "modelExtension.xsd"; //$NON-NLS-1$
            Bundle bundle = Platform.getBundle(PLUGIN_ID);
            URL url = bundle.getEntry(SCHEMA_FILE);

            if (url == null) {
                throw new IllegalStateException(NLS.bind(Messages.definitionSchemaFileNotFoundInWorkspace, SCHEMA_FILE));
            }

            this.definitionSchemaFile = new File(FileLocator.toFileURL(url).getFile());

            if (!this.definitionSchemaFile.exists()) {
                throw new IllegalStateException(NLS.bind(Messages.definitionSchemaFileNotFoundInFilesystem, SCHEMA_FILE));
            }

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

    // public InputStream write( ModelExtensionDefinition med ) throws IOException {
    //        //CoreArgCheck.isNotNull(med, "ModelExtensionDefinition is null"); //$NON-NLS-1$
    //
    // // Write Med Data out
    // ByteArrayOutputStream bout = new ByteArrayOutputStream(4096);
    //
    //        bout.write("dataOutput".getBytes()); //$NON-NLS-1$
    //
    // bout.close();
    //
    // ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
    //
    // return bin;
    // }

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
            Document document = domImpl.createDocument("http://org.teiid.modelExtension/2011", "modelExtension", null); //$NON-NLS-1$ //$NON-NLS-2$

            Element modelExtensionElem = document.getDocumentElement();
            modelExtensionElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            modelExtensionElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:p", "http://org.teiid.modelExtension/2011"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            // -----------------------------------------
            // modelExtension element
            // -----------------------------------------
            // Set the modelExtension element attributes
            Attr attr = document.createAttribute("xsi:schemaLocation"); //$NON-NLS-1$
            attr.setValue("http://org.teiid.modelExtension/2011 modelExtension.xsd "); //$NON-NLS-1$
            modelExtensionElem.setAttributeNode(attr);
            attr = document.createAttribute("metamodelUri"); //$NON-NLS-1$
            attr.setValue(med.getMetamodelUri());
            modelExtensionElem.setAttributeNode(attr);
            attr = document.createAttribute("namespaceUri"); //$NON-NLS-1$
            attr.setValue(med.getNamespaceUri());
            modelExtensionElem.setAttributeNode(attr);
            attr = document.createAttribute("namespacePrefix"); //$NON-NLS-1$
            attr.setValue(med.getNamespacePrefix());
            modelExtensionElem.setAttributeNode(attr);
            attr = document.createAttribute("version"); //$NON-NLS-1$
            attr.setValue(String.valueOf(med.getVersion()));
            modelExtensionElem.setAttributeNode(attr);

            // -----------------------------------------
            // Child - description element
            // -----------------------------------------
            Element descriptionElem = document.createElement("p:description"); //$NON-NLS-1$

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
                Element extendedMetaclassElem = document.createElement("p:extendedMetaclass"); //$NON-NLS-1$
                attr = document.createAttribute("name"); //$NON-NLS-1$
                attr.setValue(extendedMetaclassName);
                extendedMetaclassElem.setAttributeNode(attr);
                modelExtensionElem.appendChild(extendedMetaclassElem);
                Collection<ModelExtensionPropertyDefinition> properties = med.getPropertyDefinitions(extendedMetaclassName);
                for (ModelExtensionPropertyDefinition propDefn : properties) {
                    Element propertyElem = document.createElement("p:property"); //$NON-NLS-1$
                    extendedMetaclassElem.appendChild(propertyElem);

                    // Attributes
                    String simpleId = propDefn.getSimpleId();
                    String type = propDefn.getRuntimeType();

                    attr = document.createAttribute("name"); //$NON-NLS-1$
                    attr.setValue(simpleId);
                    propertyElem.setAttributeNode(attr);
                    attr = document.createAttribute("type"); //$NON-NLS-1$
                    attr.setValue(type);
                    propertyElem.setAttributeNode(attr);

                    // Child Elements
                    String displayName = propDefn.getDisplayName();
                    String descrip = propDefn.getDescription();

                    Element descrElement = document.createElement("p:description"); //$NON-NLS-1$
                    if (descrip != null) {
                        Text descripText = document.createTextNode(descrip);
                        descrElement.appendChild(descripText);
                        propertyElem.appendChild(descrElement);
                    }
                    Element displayElement = document.createElement("p:display"); //$NON-NLS-1$
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
