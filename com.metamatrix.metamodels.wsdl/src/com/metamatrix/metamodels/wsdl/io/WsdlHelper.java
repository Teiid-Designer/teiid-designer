/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

package com.metamatrix.metamodels.wsdl.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.xmi.impl.XMLHelperImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import com.metamatrix.common.protocol.URLHelper;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.xml.SAXBuilderHelper;
import com.metamatrix.metamodels.wsdl.io.WsdlConstants.Xsd;

/**
 * @since 4.2
 */
public class WsdlHelper extends XMLHelperImpl implements WsdlConstants, Xsd {

    /**
     * Converts url based wsdl imports and includes into temporary files and returns them in an ArrayList. Also, converts paths
     * for imports with locations that are relative to absolute paths.
     * 
     * @param file
     * @param absPath
     * @param fileArray - ArrayList containing WSDL file instances
     * @param addTotArray - boolean indicating whether wsdl files should be added to the ArrayList
     * @return fileArray - ArrayList containing WSDL file instances
     */
    public static ArrayList convertImportsToAbsolutePaths( final File wsdlFile,
                                                           final String absPath,
                                                           ArrayList fileArray,
                                                           Map urlMap,
                                                           boolean addToArray ) throws JDOMException, IOException {

        URI absoluteUri = URI.create(absPath);
        SAXBuilder builder = SAXBuilderHelper.createSAXBuilder(false);
        // Create JDom document object for iterating the wsdl contents
        Document wsdlDocument = builder.build(wsdlFile);
        // First look for WSDL imports/includes
        Element root = wsdlDocument.getRootElement();
        List wsdlElements = root.getContent();
        Iterator iter = wsdlElements.iterator();

        while (iter.hasNext()) {
            Object importObject = iter.next();
            if (!(importObject instanceof Element)) {
                continue;
            }
            Element importElement = (Element)importObject;
            if (!(importElement.getName().equals(IMPORT) && !importElement.getName().equals(INCLUDE))) {
                continue;
            }

            String location = importElement.getAttributeValue(IMPORT_LOCATION);

            if (location == null) {// There is no location set for this import.
                continue;
            }

            URI uri = java.net.URI.create(location);
            // Resolve the absolute path of the wsdl against the imported wsdl's path.
            // If the imported wsdl's path is already absolute, the imported wsdl's absolute path
            // will be used.
            final URI resolvedUri = absoluteUri.resolve(uri);
            final File file = URLHelper.createFileFromUrl(resolvedUri.toURL(), StringUtil.createFileName(uri.getPath()), ".wsdl"); //$NON-NLS-1$ 
            final String path = "file:/" + file.getAbsolutePath(); //$NON-NLS-1$
            importElement.setAttribute(IMPORT_LOCATION, path);
            urlMap.put(file.getAbsolutePath(), resolvedUri.toString());
            convertImportsToAbsolutePaths(file, resolvedUri.toString(), fileArray, urlMap, false);
        }

        // Now look for types element to get any schemas
        wsdlElements = root.getContent();
        iter = wsdlElements.iterator();
        while (iter.hasNext()) {
            Object typesObject = iter.next();
            if (!(typesObject instanceof Element)) {
                continue;
            }
            Element typesElement = (Element)typesObject;
            if (!(typesElement.getName().equals(TYPES))) {
                continue;
            }

            // Now look for schema elements in types
            List schemaElements = typesElement.getContent();
            iter = schemaElements.iterator();
            while (iter.hasNext()) {
                Object schemaObject = iter.next();
                if (!(schemaObject instanceof Element)) {
                    continue;
                }
                Element schemaElement = (Element)schemaObject;
                if (!(schemaElement.getName().equals(SCHEMA))) {
                    continue;
                }

                // Now look for import elements in schemas
                List importElements = schemaElement.getContent();
                iter = importElements.iterator();
                while (iter.hasNext()) {
                    Object importObject = iter.next();
                    if (!(importObject instanceof Element)) {
                        continue;
                    }
                    Element importElement = (Element)importObject;
                    if (!(importElement.getName().equals(IMPORT) && !importElement.getName().equals(INCLUDE))) {
                        continue;
                    }

                    String schemaLocation = importElement.getAttributeValue(XSDConstants.SCHEMALOCATION_ATTRIBUTE);
                    if (schemaLocation == null) {
                        continue; // There is no location attribute for this import.
                    }

                    java.net.URI uri = java.net.URI.create(schemaLocation);
                    uri = java.net.URI.create(schemaLocation);
                    URI newUri = null;

                    // Resolve the absolute path of the wsdl against the schema path.
                    // If the schema path is already absolute, the schemas absolute path
                    // will be used.
                    newUri = absoluteUri.resolve(uri);
                    importElement.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, newUri.toString());
                }
            }
        }

        // Just add root wsdl to the array
        if (addToArray) {
            fileArray.add(wsdlFile);
        }

        // Write the JDom document
        writeDocument(wsdlDocument, wsdlFile);

        return fileArray;
    }

    /**
     * This method writes a DOM document to a file.
     * 
     * @param doc
     * @param file File to write to.
     * @throws JDOMException
     * @throws IOException
     */
    private static void writeDocument( final Document doc,
                                       final File file ) throws IOException {
        XMLOutputter out = new XMLOutputter();
        java.io.FileWriter writer = new java.io.FileWriter(file);
        out.output(doc, writer);
        writer.flush();
        writer.close();
    }
}
