/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.xml.XMLReaderWriter;
import com.metamatrix.common.xml.XMLReaderWriterImpl;

public class WorkspaceDefnReaderWriter {
    private static final String UNKNOWN = "Unknown"; //$NON-NLS-1$

    public BasicWorkspaceDefn read( InputStream defStream ) throws IOException {
        BasicWorkspaceDefn workspaceDefn = null;

        try {
            XMLReaderWriter reader = new XMLReaderWriterImpl();
            Document doc = reader.readDocument(defStream);
            Element root = doc.getRootElement();

            workspaceDefn = new BasicWorkspaceDefn();

            if (root != null) {
                // load the Header section
                loadHeaderSection(workspaceDefn, root);

                // add Models section
                loadModelsSection(workspaceDefn, root);
            }

        } catch (JDOMException e) {
            throw new IOException("WorkspaceDefnReaderWriter.Unable_to_read_defn_file");//$NON-NLS-1$
        }
        return workspaceDefn;
    }

    private void loadHeaderSection( BasicWorkspaceDefn workspaceDefn,
                                    Element root ) throws IOException {
        Element headElement = root.getChild(Header.ELEMENT);
        if (headElement == null) {
            throw new IOException("WorkspaceDefnReaderWriter.Invalid_xml_section"); //$NON-NLS-1$
        }

        Properties header = new Properties();
        String createdBy = headElement.getChildText(Header.APPLICATION_CREATED_BY);
        String applicationVersion = headElement.getChildText(Header.APPLICATION_VERSION);
        String systemVersion = headElement.getChildText(Header.SYSTEM_VERSION);
        String userCreatedBy = headElement.getChildText(Header.USER_CREATED_BY);
        String modificationTime = headElement.getChildText(Header.MODIFICATION_TIME);

        header.setProperty(Header.APPLICATION_CREATED_BY, createdBy != null ? createdBy : UNKNOWN);
        header.setProperty(Header.APPLICATION_VERSION, applicationVersion != null ? applicationVersion : UNKNOWN);
        header.setProperty(Header.SYSTEM_VERSION, systemVersion != null ? systemVersion : UNKNOWN);
        header.setProperty(Header.USER_CREATED_BY, userCreatedBy != null ? userCreatedBy : UNKNOWN);
        header.setProperty(Header.MODIFICATION_TIME, modificationTime != null ? modificationTime : UNKNOWN);
        workspaceDefn.setHeaderProperties(header);

        // now place them in the defn too
        workspaceDefn.setCreatedBy(headElement.getChildText(Header.USER_CREATED_BY));

        try {
            workspaceDefn.setDateCreated(new SimpleDateFormat().parse(headElement.getChildText(Header.MODIFICATION_TIME)));
        } catch (ParseException e) {
            workspaceDefn.setDateCreated(Calendar.getInstance().getTime());
        }
    }

    private void loadModelsSection( BasicWorkspaceDefn workspaceDefn,
                                    Element root ) {
        Collection<Element> modelsElements = root.getChildren(Model.ELEMENT);
        for (Element modelElement : modelsElements) {
            workspaceDefn.addModelInfo(loadModel(modelElement));
        }
    }

    private SourceModelInfo loadModel( Element modelElement ) {
        Properties props = getElementProperties(modelElement);

        SourceModelInfo model = new SourceModelInfo(props.getProperty(Model.NAME));
        model.setUuid(props.getProperty(Model.UUID));
        model.setContainerPath(props.getProperty(Model.PATH));
        model.enableMutliSourceBindings(Boolean.parseBoolean(props.getProperty(Model.MULTI_SOURCE_ENABLED)));

        Element cbElement = modelElement.getChild(Model.CONNECTOR_BINDINGS_ELEMENT);
        if (cbElement != null) {
            Collection<Element> bindingElements = cbElement.getChildren(Model.CONNECTOR);
            for (Element bindingElement : bindingElements) {
                model.addConnectorBindingByName(bindingElement.getAttributeValue(Model.CONNECTOR_ATTRIBUTE_NAME));
            }
        }
        return model;
    }

    protected Properties getElementProperties( Element rootElement ) {
        Properties properties = new Properties();
        if (rootElement == null) {
            return properties;
        }
        // obtain any defaults that are defined
        List propertyElements = rootElement.getChildren(Property.ELEMENT);
        if (propertyElements != null) {
            Iterator iterator = propertyElements.iterator();
            for (int i = 1; iterator.hasNext(); i++) {
                Element element = (Element)iterator.next();
                String name = element.getAttributeValue(Property.ATTRIBUTE_NAME);
                String value = element.getAttributeValue(Property.ATTRIBUTE_VALUE);
                if (name != null && name.length() > 0) {
                    properties.setProperty(name, value);
                }
            }
        }
        return properties;
    }

    /**
     * Write the DEF contents into given output stream
     * 
     * @param outstream
     * @param def
     * @param headerProperties
     * @throws IOException
     */
    public void write( OutputStream outstream,
                       BasicWorkspaceDefn def,
                       Properties headerProperties ) throws IOException {

        Element rootElement = new Element(ROOT_ELEMENT);
        Document doc = new Document(rootElement);

        // write the header properties
        rootElement.addContent(createHeaderElement(headerProperties));

        // write the model elements
        Collection<ModelInfo> models = def.getModels();
        for (ModelInfo model : models) {
            rootElement.addContent(createModel(model));
        }

        // write doc to the stream
        new XMLReaderWriterImpl().writeDocument(doc, outstream);
    }

    private Element createHeaderElement( Properties props ) {
        Element headerElement = new Element(Header.ELEMENT);
        headerElement.addContent(new Element(Header.APPLICATION_CREATED_BY).addContent(props.getProperty(Header.APPLICATION_CREATED_BY,
                                                                                                         UNKNOWN)));
        headerElement.addContent(new Element(Header.APPLICATION_VERSION).addContent(props.getProperty(Header.APPLICATION_VERSION,
                                                                                                      UNKNOWN)));
        headerElement.addContent(new Element(Header.USER_CREATED_BY).addContent(props.getProperty(Header.USER_CREATED_BY, UNKNOWN)));
        headerElement.addContent(new Element(Header.SYSTEM_VERSION).addContent(props.getProperty(Header.SYSTEM_VERSION, UNKNOWN)));
        headerElement.addContent(new Element(Header.MODIFICATION_TIME).addContent(props.getProperty(Header.MODIFICATION_TIME,
                                                                                                    UNKNOWN)));
        return headerElement;
    }

    private Element createModel( ModelInfo model ) {
        Element modelElement = new Element(Model.ELEMENT);
        boolean valid = addPropertyElement(modelElement, Model.NAME, model.getName());
        if (valid) {
            addPropertyElement(modelElement, Model.PATH, ((SourceModelInfo)model).getContainerPath());
            addPropertyElement(modelElement, Model.UUID, model.getUUID());
            addPropertyElement(modelElement, Model.MULTI_SOURCE_ENABLED, Boolean.toString(model.isMultiSourceBindingEnabled()));

            List<String> bindings = model.getConnectorBindingNames();
            if (bindings != null && !bindings.isEmpty()) {
                Element cbsElement = new Element(Model.CONNECTOR_BINDINGS_ELEMENT);
                for (String cbName : bindings) {
                    Element connector = new Element(Model.CONNECTOR);
                    connector.setAttribute(Model.CONNECTOR_ATTRIBUTE_NAME, cbName);
                    cbsElement.addContent(connector);
                }
                modelElement.addContent(cbsElement);
            }
        }
        return modelElement;
    }

    private boolean addPropertyElement( Element element,
                                        String name,
                                        String value ) {
        if (element == null || name == null || value == null) {
            return false;
        }
        Element propElement = new Element(Property.ELEMENT);
        propElement.setAttribute(Property.ATTRIBUTE_NAME, name);
        propElement.setAttribute(Property.ATTRIBUTE_VALUE, value);
        element.addContent(propElement);
        return true;
    }

    static final String ROOT_ELEMENT = "WORKSPACE"; //$NON-NLS-1$ 

    public static class Property {
        public static final String ELEMENT = "Property"; //$NON-NLS-1$
        public static final String ATTRIBUTE_NAME = "Name"; //$NON-NLS-1$
        public static final String ATTRIBUTE_VALUE = "Value"; //$NON-NLS-1$
    }

    public static interface Header {
        public static final String ELEMENT = "Header"; //$NON-NLS-1$
        public static final String APPLICATION_CREATED_BY = "ApplicationCreatedBy"; //$NON-NLS-1$
        public static final String APPLICATION_VERSION = "ApplicationVersion"; //$NON-NLS-1$
        public static final String USER_CREATED_BY = "UserCreatedBy"; //$NON-NLS-1$
        public static final String SYSTEM_VERSION = "MetaMatrixSystemVersion"; //$NON-NLS-1$
        public static final String MODIFICATION_TIME = "Time"; //$NON-NLS-1$    
    }

    public static class Model {
        public static final String ELEMENT = "Model"; //$NON-NLS-1$
        public static final String NAME = "Name"; //$NON-NLS-1$
        public static final String UUID = "UUID"; //$NON-NLS-1$
        public static final String PATH = "Path"; //$NON-NLS-1$
        // Optional - Default - physical=false, virtual=true
        public static final String MULTI_SOURCE_ENABLED = "MultiSourceEnabled"; //$NON-NLS-1$

        // Optional - no binding set
        public static final String CONNECTOR_BINDINGS_ELEMENT = "ConnectorBindings"; //$NON-NLS-1$
        public static final String CONNECTOR = "Connector"; //$NON-NLS-1$
        public static final String CONNECTOR_ATTRIBUTE_NAME = "Name"; //$NON-NLS-1$
    }
}
