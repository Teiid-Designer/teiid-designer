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

package com.metamatrix.vdb.edit.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.Configuration;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConfigurationObjectEditor;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.common.config.util.ConfigUtil;
import com.metamatrix.common.config.xml.XMLConfig_42_HelperImpl;
import com.metamatrix.common.config.xml.XMLConfigurationImportExportUtility;
import com.metamatrix.common.config.xml.XMLHelper;
import com.metamatrix.common.config.xml.XMLHelperUtil;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.common.vdb.api.VDBStreamImpl;
import com.metamatrix.common.xml.XMLReaderWriter;
import com.metamatrix.common.xml.XMLReaderWriterImpl;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.core.util.MetaMatrixProductVersion;
import com.metamatrix.core.util.ObjectConverterUtil;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.internal.def.VDBDefPropertyNames;
import com.metamatrix.vdb.internal.def.VDBDefXMLElementNames;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;
import com.metamatrix.vdb.runtime.BasicVDBDefn;

public class VDBDefnXMLHelper {

    private static final String DEFAULT_USER_CREATED_BY = "VDBWriter"; //$NON-NLS-1$

    private XMLHelper cfgXMLHelper = null;

    protected Element getRoot( File f ) throws Exception {
        FileInputStream in = new FileInputStream(f);
        Document doc = null;
        String fileName = f.getName();
        String absolutePath = f.getAbsolutePath();

        try {
            doc = getXMLReaderWriter().readDocument(in);
        } catch (JDOMException e) {
            System.out.println(absolutePath);
            e.printStackTrace();
            throw new IOException(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Unable_to_read_file", fileName));//$NON-NLS-1$
        } finally {
            in.close();
        }

        return doc.getRootElement();

    }

    protected Element getRoot( char[] defFile ) throws Exception {
        InputStream in = ObjectConverterUtil.convertToInputStream(new String(defFile));
        Document doc = null;

        try {
            doc = getXMLReaderWriter().readDocument(in);
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new IOException(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Unable_to_read_defn_file"));//$NON-NLS-1$
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return doc.getRootElement();

    }

    private XMLReaderWriter getXMLReaderWriter() {
        return new XMLReaderWriterImpl();
    }

    /**
     * This method is used to create a root JDOM Element. This element is for structural organization only and does not represent
     * any real configuration object.
     * 
     * @return a JDOM XML Element
     */
    public Element createRootDocumentElement() {
        return new Element(VDBConstants.VDBElementNames.ELEMENT);
    }

    public Element createVDBInfoElement( VDBDefn defn,
                                         String archiveFileName ) {
        if (defn == null) {
            ArgCheck.isNotNull(defn, VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_VDB_defintion")); //$NON-NLS-1$
        }
        // changed so that the name in the .def file is the same name as the written archivefile

        Element vdbInfoElement = new Element(VDBConstants.VDBElementNames.VDBInfo.ELEMENT);
        String v = null;
        v = defn.getName();
        addPropertyElement(vdbInfoElement, VDBConstants.VDBElementNames.VDBInfo.Properties.NAME, v);
        v = defn.getUUID();
        if (v != null) {
            addPropertyElement(vdbInfoElement, VDBConstants.VDBElementNames.VDBInfo.Properties.GUID, v);
        }
        v = defn.getVersion();
        if (v != null && v.length() > 0) {
            addPropertyElement(vdbInfoElement, VDBConstants.VDBElementNames.VDBInfo.Properties.VERSION, v);
        }

        if (archiveFileName != null) {
            v = archiveFileName;
        } else {
            v = defn.getFileName();
            if (v == null) {
                v = defn.getName();
            }
        }

        addPropertyElement(vdbInfoElement, VDBConstants.VDBElementNames.VDBInfo.Properties.ARCHIVE_NAME, v);

        v = defn.getDescription();
        if (v != null) {
            addPropertyElement(vdbInfoElement, VDBConstants.VDBElementNames.VDBInfo.Properties.DESCRIPTION, v);
        }

        // v = String.valueOf(defn.isActiveStatus());
        // addPropertyElement(vdbInfoElement, VDBConstants.VDBElementNames.VDBInfo.Properties.ISACTIVE, v);

        return vdbInfoElement;
    }

    public Element createVDBModelElement( ModelInfo modelDefn ) {
        if (modelDefn == null) {
            ArgCheck.isNotNull(modelDefn, VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_VDB_model"));//$NON-NLS-1$
        }
        Element vdbModelElement = new Element(VDBConstants.VDBElementNames.Model.ELEMENT);
        addPropertyElement(vdbModelElement, VDBConstants.VDBElementNames.Model.Properties.NAME, modelDefn.getName());
        // addPropertyElement(vdbModelElement, VDBConstants.VDBElementNames.Model.Properties.MODEL_TYPE,
        // modelDefn.getModelTypeName());
        // Boolean isp = new Boolean(modelDefn.isPhysical());
        // addPropertyElement(vdbModelElement, VDBConstants.VDBElementNames.Model.Properties.ISPHYSICAL, isp.toString());
        if (modelDefn.isVisible()) {
            addPropertyElement(vdbModelElement,
                               VDBConstants.VDBElementNames.Model.Properties.VISIBILITY,
                               VDBConstants.Visibility.PUBLIC);
        } else {
            addPropertyElement(vdbModelElement,
                               VDBConstants.VDBElementNames.Model.Properties.VISIBILITY,
                               VDBConstants.Visibility.PRIVATE);
        }
        addPropertyElement(vdbModelElement,
                           VDBConstants.VDBElementNames.Model.Properties.MULTI_SOURCE_ENABLED,
                           new Boolean(modelDefn.isMultiSourceBindingEnabled()).toString());

        /*
         * if (modelDefn.getConnectorBindingName() != null) { addPropertyElement(vdbModelElement,
         * VDBConstants.VDBElementNames.Model.Properties.CONNECTOR_BINDING_NAME, modelDefn.getConnectorBindingName()); } else {
         * addPropertyElement(vdbModelElement, VDBConstants.VDBElementNames.Model.Properties.CONNECTOR_BINDING_NAME, ""); }
         */
        return vdbModelElement;
    }

    protected void addPropertyElement( Element element,
                                       String name,
                                       String value ) {
        if (element == null) {
            ArgCheck.isNotNull(element, VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_elment_property"));//$NON-NLS-1$
        }
        if (name == null) {
            ArgCheck.isNotNull(name, VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_element_name"));//$NON-NLS-1$
        }
        if (value == null) {
            ArgCheck.isNotNull(value, VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_element_value", name));//$NON-NLS-1$
        }
        Element propElement = new Element(VDBConstants.VDBElementNames.Property.ELEMENT);
        propElement.setAttribute(VDBConstants.VDBElementNames.Property.Attributes.NAME, name);
        propElement.setAttribute(VDBConstants.VDBElementNames.Property.Attributes.VALUE, value);
        element.addContent(propElement);
    }

    public VDBDefn createVDBDefn( Element root,
                                  File vdbFile ) throws Exception {
        Element vdbInfoElement = root.getChild(VDBConstants.VDBElementNames.VDBInfo.ELEMENT);
        if (vdbInfoElement == null) {
            throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_xml_section",//$NON-NLS-1$
                                                             VDBConstants.VDBElementNames.VDBInfo.ELEMENT));
        }

        Properties vdbProps = getElementProperties(vdbInfoElement);
        if (vdbProps == null || vdbProps.isEmpty()) {
            throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.No_properties_defined_to_create_defn",//$NON-NLS-1$
                                                             VDBConstants.VDBElementNames.VDBInfo.ELEMENT));
        }

        String vdbName = vdbProps.getProperty(VDBConstants.VDBElementNames.VDBInfo.Properties.NAME);
        if (vdbName == null) {
            Assertion.isNotNull(vdbName, VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_VDB_name"));//$NON-NLS-1$
        }
        BasicVDBDefn defn = null;
        if (vdbFile != null) {
            defn = new BasicVDBDefn(vdbName);
            defn.setVDBStream(new VDBStreamImpl(vdbFile));
        } else {
            defn = new BasicVDBDefn(vdbName);
        }
        return loadVDBDefn(defn, vdbProps);
    }

    public VDBDefn createVDBDefn( VirtualDatabase vdb,
                                  File vdbFile ) throws Exception {
        BasicVDBDefn defn = null;
        if (vdbFile != null) {
            defn = new BasicVDBDefn(vdb.getName());
            defn.setVDBStream(new VDBStreamImpl(vdbFile));
        } else {
            defn = new BasicVDBDefn(vdb.getName());
        }
        if (vdb.getVersion() == null) {
            defn.setVersion("1");//$NON-NLS-1$
        } else {
            defn.setVersion(vdb.getVersion());
        }
        defn.setDescription(vdb.getDescription());
        defn.setUUID(vdb.getUuid());
        defn.setFileName(vdb.getName() + VDBConstants.VDB_ARCHIVE_FILE_EXTENSION);

        defn.setDateCreated(vdb.getTimeLastChangedAsDate());

        return defn;
    }

    public void addVDBDefnInfo( VDBDefn vdbdefn,
                                Element root ) throws Exception {
        BasicVDBDefn defn = (BasicVDBDefn)vdbdefn;
        Element vdbInfoElement = root.getChild(VDBConstants.VDBElementNames.VDBInfo.ELEMENT);
        if (vdbInfoElement == null) {
            throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_xml_section",//$NON-NLS-1$
                                                             VDBConstants.VDBElementNames.VDBInfo.ELEMENT));
        }

        Properties vdbProps = getElementProperties(vdbInfoElement);
        if (vdbProps == null || vdbProps.isEmpty()) {
            throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.No_properties_defined_to_create_defn",//$NON-NLS-1$
                                                             VDBConstants.VDBElementNames.VDBInfo.ELEMENT));
        }
        loadVDBDefn(defn, vdbProps);

    }

    private VDBDefn loadVDBDefn( BasicVDBDefn defn,
                                 Properties vdbProps ) throws Exception {
        String vdbName = vdbProps.getProperty(VDBConstants.VDBElementNames.VDBInfo.Properties.NAME);
        if (vdbName != null && vdbName.length() > 0) {
            defn.setName(vdbName);
        }

        String vdbVersion = vdbProps.getProperty(VDBConstants.VDBElementNames.VDBInfo.Properties.VERSION);
        String vdbArchiveName = vdbProps.getProperty(VDBConstants.VDBElementNames.VDBInfo.Properties.ARCHIVE_NAME);
        String desc = vdbProps.getProperty(VDBConstants.VDBElementNames.VDBInfo.Properties.DESCRIPTION, defn.getName());
        // String active = vdbProps.getProperty(VDBConstants.VDBElementNames.VDBInfo.Properties.ISACTIVE,
        // Boolean.TRUE.toString());
        String guid = vdbProps.getProperty(VDBConstants.VDBElementNames.VDBInfo.Properties.GUID, defn.getName());
        if (vdbVersion != null) {
            defn.setVersion(vdbVersion);
        }
        if (vdbArchiveName != null) {
            defn.setFileName(vdbArchiveName);
        }
        if (defn.getUUID() == null && guid != null) {
            defn.setUUID(guid);
        }
        if (desc != null) {
            defn.setDescription(desc);
        }
        // if (active.equalsIgnoreCase(Boolean.TRUE.toString())) {
        // defn.setStatus(MetadataConstants.VDB_STATUS.ACTIVE);
        // } else {
        // defn.setStatus(MetadataConstants.VDB_STATUS.INACTIVE);
        // }
        return defn;
    }

    /**
     * @param root
     * @param vdbDefn
     * @return
     * @since 4.2
     */
    public VDBDefn addModelInfo42( Element root,
                                   VDBDefn vdbDefn,
                                   ConfigurationModelContainer cmc ) throws Exception {
        // retreive lists of actual configuration object representation elements
        // from each main category element.
        // Get connector binding to connector type mappings, if any.
        XMLConfigurationImportExportUtility importUtil = new XMLConfigurationImportExportUtility();
        ConfigurationObjectEditor editor = new BasicConfigurationObjectEditor(false);

        Map bindingMap = null;
        Map typeMap = null;
        // collectConnectors42(bindingNameToBindingElement, typeNameToConnectorTypeElement, bindingNameToTypeName, root);
        Collection modelsElements = root.getChildren(VDBConstants.VDBElementNames.Model.ELEMENT);
        Collection modelEntries = vdbDefn.getModels();
        Map meMap = new HashMap(modelEntries.size());
        for (Iterator it = modelEntries.iterator(); it.hasNext();) {
            BasicVDBModelDefn e = (BasicVDBModelDefn)it.next();
            meMap.put(e.getName(), e);
        }

        // NOTE: The importing of bindings and types, there doesn't have to be a
        // matching type to a binding (or visa-versa).
        // That validation will be done when the VDBDefn
        // is being processed to verify if the type already exist for the binding
        // being imported.

        Collection bindings = importUtil.importConnectorBindings(root, editor);
        Collection types = importUtil.importComponentTypes(root, editor, true);

        BasicVDBDefn defn = (BasicVDBDefn)vdbDefn;

        // if the cmc is passed in, then the types and bindings will be used when
        // the imported defn may not contain either of those.
        if (cmc != null) {
            typeMap = cmc.getComponentTypes();
        } else {
            typeMap = new HashMap();
        }

        if (types != null && types.size() > 0) {
            for (Iterator itypes = types.iterator(); itypes.hasNext();) {
                ComponentType t = (ComponentType)itypes.next();
                typeMap.put(t.getFullName(), t);
            }
        }
        bindingMap = new HashMap();

        if (cmc != null) {
            Collection cmc_bindings = cmc.getConfiguration().getConnectorBindings();
            if (cmc_bindings != null && cmc_bindings.size() > 0) {
                for (Iterator it = cmc_bindings.iterator(); it.hasNext();) {
                    final ConnectorBinding cb = (ConnectorBinding)it.next();
                    bindingMap.put(cb.getFullName(), cb);
                }

            }
        }

        if (bindings != null && bindings.size() > 0) {
            for (Iterator iBindings = bindings.iterator(); iBindings.hasNext();) {
                ConnectorBinding b = (ConnectorBinding)iBindings.next();
                bindingMap.put(b.getFullName(), b);
            }
        }

        Iterator iterator = modelsElements.iterator();
        // Iterate over models
        while (iterator.hasNext()) {
            Element modelElement = (Element)iterator.next();
            Properties props = getElementProperties(modelElement);
            if (props == null || props.isEmpty()) {
                throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.No_properties_defined_to_create_defn", //$NON-NLS-1$
                                                                 VDBConstants.VDBElementNames.Model.ELEMENT));
            }
            String name = props.getProperty(VDBConstants.VDBElementNames.Model.Properties.NAME);
            if (name == null) {
                throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.No_name_defined",//$NON-NLS-1$
                                                                 VDBConstants.VDBElementNames.Model.Properties.NAME));
            }
            BasicVDBModelDefn m = (BasicVDBModelDefn)meMap.get(name);
            if (m == null) {
                /** Defect 18465 - This method also gets invoked by the Modeler when a VDB is opened. However, we do not want to **/
                /** see this message in the messages tab of the Modeler. **/
                // VdbEditPlugin.Util.log(IStatus.WARNING,
                //                               VdbEditPlugin.Util.getString("VDBDefnXMLHelper.No_model_found_in_VDB", name)); //$NON-NLS-1$
                continue;
            }
            String visibility = props.getProperty(VDBConstants.VDBElementNames.Model.Properties.VISIBILITY);
            if (visibility.equalsIgnoreCase(VDBConstants.Visibility.PUBLIC)) {
                m.setIsVisible(true);
            } else {
                m.setIsVisible(false);
            }

            String multiSourceEnabled = props.getProperty(VDBConstants.VDBElementNames.Model.Properties.MULTI_SOURCE_ENABLED,
                                                          Boolean.FALSE.toString());
            if (multiSourceEnabled != null && multiSourceEnabled.equalsIgnoreCase(Boolean.TRUE.toString())) {
                m.enableMutliSourceBindings(true);
            } else {
                m.enableMutliSourceBindings(false);
            }

            // ConnectorBindings tag within model indicates one or more connector bindings references
            Element connectorBindingsEle = modelElement.getChild(VDBConstants.VDBElementNames.Model.ConnectorBindings.ELEMENT);
            if (connectorBindingsEle != null) {
                Collection connectorBindings = connectorBindingsEle.getChildren(VDBConstants.VDBElementNames.Model.ConnectorBinding.ELEMENT);
                if (connectorBindings != null) {
                    // type and binding are optional to load because
                    // the installation for system models does not contain a binding
                    // but if one is there, so must be the other
                    for (Iterator itr = connectorBindings.iterator(); itr.hasNext();) {
                        Element connectorBindingElement = (Element)itr.next();
                        String bindingName = connectorBindingElement.getAttributeValue(VDBConstants.VDBElementNames.Property.Attributes.NAME);
                        if (bindingName != null && bindingName.length() > 0) {
                            ConnectorBinding binding = (ConnectorBinding)bindingMap.get(bindingName);

                            if (binding != null) {
                                ComponentType cmcT = (ComponentType)typeMap.get(binding.getComponentTypeID().getFullName());
                                if (cmcT != null) {
                                    defn.addConnectorType(cmcT);
                                }

                                defn.addConnectorBinding(m.getName(), binding);
                            }

                        }
                    }
                }
            }
        }
        return defn;
    }

    public boolean containsModelInfo( Element root ) throws Exception {
        Collection modelsElements = root.getChildren(VDBConstants.VDBElementNames.Model.ELEMENT);
        if (modelsElements == null || modelsElements.size() == 0) {
            return false;
        }
        return true;
    }

    public VDBDefn addModelInfo( Element root,
                                 VDBDefn vdbDefn,
                                 ConfigurationModelContainer cmc ) throws Exception {
        // retreive lists of actual configuration object representation elements
        // from each main category element.
        Collection modelsElements = root.getChildren(VDBConstants.VDBElementNames.Model.ELEMENT);
        Collection modelEntries = vdbDefn.getModels();
        Map meMap = new HashMap(modelEntries.size());
        for (Iterator it = modelEntries.iterator(); it.hasNext();) {
            BasicVDBModelDefn e = (BasicVDBModelDefn)it.next();
            meMap.put(e.getName(), e);
        }

        Map types = new HashMap();
        Map bindings = new HashMap();
        BasicVDBDefn defn = (BasicVDBDefn)vdbDefn;
        // Collection models = new ArrayList();
        Iterator iterator = modelsElements.iterator();
        // Iterate over models
        while (iterator.hasNext()) {
            Element modelElement = (Element)iterator.next();
            Properties props = getElementProperties(modelElement);
            if (props == null || props.isEmpty()) {
                throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.No_properties_defined_to_create_defn", //$NON-NLS-1$
                                                                 VDBConstants.VDBElementNames.Model.ELEMENT));
            }
            String name = props.getProperty(VDBConstants.VDBElementNames.Model.Properties.NAME);
            if (name == null) {
                throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.No_name_defined",//$NON-NLS-1$
                                                                 VDBConstants.VDBElementNames.Model.Properties.NAME));
            }
            BasicVDBModelDefn m = (BasicVDBModelDefn)meMap.get(name);
            if (m == null) {
                /** Defect 18465 - This method also gets invoked by the Modeler when a VDB is opened. However, we do not want to **/
                /** see this message in the messages tab of the Modeler. **/
                // VdbEditPlugin.Util.log(IStatus.WARNING,
                //                               VdbEditPlugin.Util.getString("VDBDefnXMLHelper.No_model_found_in_VDB", name)); //$NON-NLS-1$
                continue;
            }
            String visibility = props.getProperty(VDBConstants.VDBElementNames.Model.Properties.VISIBILITY);
            if (visibility.equalsIgnoreCase(VDBConstants.Visibility.PUBLIC)) {
                m.setIsVisible(true);
            } else {
                m.setIsVisible(false);
            }

            defn.addModelInfo(m);

            // NOTE: The importing of bindings and types, there doesn't have to be a
            // matching type to a binding (or visa-versa).
            // That validation will be done when the VDBDefn
            // is being processed to verify if the type already exist for the binding
            // being imported.

            Element typeElement = modelElement.getChild(VDBConstants.VDBElementNames.Model.ComponentType.ELEMENT);
            Element bindingElement = modelElement.getChild(VDBConstants.VDBElementNames.Model.ConnectorBinding.ELEMENT);
            // type and binding are optional to load because
            // the installation for system models does not contain a binding
            // but if one is there, so must the other
            if (typeElement != null) {
                ComponentType type = getConfigXMLHelper().createComponentType(typeElement, getEditor(), null, true);
                // if the connector component type already exist in the configuration
                // model passed in, use it, not the one in the defn file

                if (cmc != null && cmc.getComponentType(type.getFullName()) != null) {
                    type = cmc.getComponentType(type.getFullName());
                }
                if (!types.containsKey(type.getID())) {
                    defn.addConnectorType(type);
                    types.put(type.getID(), type);
                }
            }
            String bindingName = null;
            ConnectorBinding binding = null;
            if (bindingElement != null) {
                binding = getConfigXMLHelper().createConnectorBinding(Configuration.NEXT_STARTUP_ID,
                                                                      bindingElement,
                                                                      getEditor(),
                                                                      null,
                                                                      false);
                // routingUUID = binding.getRoutingUUID();

                // if the connector component type already exist in the configuration
                // model passed in, use it, not the one in the defn file
                if (cmc != null && cmc.getConfiguration().getConnectorBinding(binding.getFullName()) != null) {
                    binding = cmc.getConfiguration().getConnectorBinding(binding.getFullName());
                }
                if (!bindings.containsKey(binding.getFullName())) {
                    defn.addConnectorBinding(binding);
                    bindings.put(binding.getFullName(), binding);
                } else {
                    binding = (ConnectorBinding)bindings.get(binding.getFullName());
                    // routingUUID = binding.getRoutingUUID();
                }

                bindingName = binding.getFullName();
                // System.out.println("BINDING PROPS " +
                // PropertiesUtils.prettyPrint(mdefn.getConnectorBinding().getProperties()));
            } else {
                // this allows the setting of a binding name without loading the binding
                // Example is a system model that binds to a service
                bindingName = props.getProperty(VDBConstants.VDBElementNames.Model.Properties.CONNECTOR_BINDING_NAME);
            }

            if (bindingName != null && bindingName.length() > 0) {
                m.addConnectorBindingByName(bindingName);
            }

            // if (routingUUID != null && routingUUID.length() > 0) {
            // m.addConnectorBindingName(routingUUID);
            // } else if (binding != null) {
            // m.addConnectorBindingUUID(binding.getFullName());
            // }
            // models.add(m);
        }
        // defn.setModelInfos(models);
        return defn;
    }

    /**
     * getElementProperties is used to obtain the property name-value pairs from an element. One or more properties can be defined
     * and they are defined as follows: <Property Name="name" Value="value" /> <Property Name="name" Value="value" />
     * 
     * @return Properties contained in the element
     */
    protected Properties getElementProperties( Element elementProperties ) {
        Properties properties = new Properties();
        if (elementProperties == null) {
            return properties;
        }
        // obtain any defaults that are defined
        List propertyElements = elementProperties.getChildren(VDBConstants.VDBElementNames.Property.ELEMENT);
        if (propertyElements != null) {
            Iterator iterator = propertyElements.iterator();
            for (int i = 1; iterator.hasNext(); i++) {
                Element element = (Element)iterator.next();
                String name = element.getAttributeValue(VDBConstants.VDBElementNames.Property.Attributes.NAME);
                String value = element.getAttributeValue(VDBConstants.VDBElementNames.Property.Attributes.VALUE);
                if (name != null && name.length() > 0) {
                    properties.setProperty(name, value);
                }
            }
        }
        return properties;
    }

    private ConfigurationObjectEditor getEditor() {
        return ConfigUtil.getEditor();
    }

    private XMLHelper getConfigXMLHelper() {
        if (cfgXMLHelper == null) {
            cfgXMLHelper = new XMLConfig_42_HelperImpl();
        }
        return cfgXMLHelper;
    }

    /**
     * VDB export utility to add all connector binding references to a model.
     * 
     * @param connectorBindingNames The collection <String>of connector binding names.
     * @param model the element to which to add connector binding refs.
     * @since 4.2
     */
    public void addConnectorRefs( Collection connectorBindingNames,
                                  Element model ) {
        if (connectorBindingNames != null && connectorBindingNames.size() > 0) {
            Element connectorBindingsElement = new Element(VDBConstants.VDBElementNames.Model.ConnectorBindings.ELEMENT);
            for (Iterator iter = connectorBindingNames.iterator(); iter.hasNext();) {
                Element aConnectorBindingElement = new Element(VDBConstants.VDBElementNames.Model.ConnectorBinding.ELEMENT);
                String name = (String)iter.next();
                aConnectorBindingElement.setAttribute(VDBConstants.VDBElementNames.Property.Attributes.NAME, name);
                connectorBindingsElement.addContent(aConnectorBindingElement);
            }
            model.addContent(connectorBindingsElement);
        }
    }

    public Element addHeaderElement( Element root,
                                     Properties properties ) {

        root.addContent(createHeaderElement(properties));

        return root;

    }

    public static boolean is41Compatible( Element root ) {
        Element headerElement = root.getChild(VDBDefXMLElementNames.Header.ELEMENT);
        if (headerElement == null) {
            // If no header element found, assume it's pre vers 4.2
            return false;
        }

        Properties props = new VDBDefnXMLHelper().getHeaderProperties(headerElement);

        return is41Compatible(props);

    }

    public static boolean is41Compatible( Properties props ) {

        String sVersion = props.getProperty(VDBDefPropertyNames.VDB_EXPORTER_VERSION);

        if (sVersion == null) {
            return is42ConfigurationCompatible(props);
        }
        try {

            try {
                double sv = Double.parseDouble(sVersion);
                if (sv >= VDBDefPropertyNames.VDBEXPORTER_LATEST_VERSION_DBL) {
                    return true;
                }

            } catch (Throwable t) {
            }

            // for compatibility reasons for previously created .DEF files, check
            // the configuration version header property for compatibility
            return is42ConfigurationCompatible(props);

        } catch (Throwable t) {
            return false;
        }
    }

    public static final boolean is42ConfigurationCompatible( Properties props ) {

        try {
            return XMLHelperUtil.is42ConfigurationCompatible(props);

        } catch (Throwable t) {
            return false;
        }

    }

    public final Properties getHeaderProperties( Element element ) {
        Properties props = new Properties();

        // this isnt the header element, the try to get the header element
        if (!element.getName().equals(VDBDefXMLElementNames.Header.ELEMENT)) {
            // String wrongElementName = element.getName();
            element = element.getChild(VDBDefXMLElementNames.Header.ELEMENT);
            if (element == null) {
                // If no header element found, assume it's pre vers 4.2
                return props; // this should cause the compatibility check to use the old version

                //           
                //               final String msg = VdbEditPlugin.Util.getString("VDBDefXMLHelper.Unexpected_header_element", wrongElementName); //$NON-NLS-1$
                //
                // throw new IOException(msg);
            }
        }

        List elements = element.getChildren();
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            final Element e = (Element)it.next();
            props.setProperty(e.getName(), e.getText());
        }
        return props;
    }

    /**
     * <p>
     * This method is used to create a Header JDOM Element from a Properties object.
     * 
     * @param props the properties object that contains the values for the Header
     * @return a JDOM XML Element
     */
    public Element createHeaderElement( Properties props ) {
        if (props == null) {
            props = new Properties();
        }

        Element headerElement = new Element(VDBDefXMLElementNames.Header.ELEMENT);
        String applicationCreatedByContent = props.getProperty(VDBDefPropertyNames.APPLICATION_CREATED_BY);
        String applicationVersionCreatedByContent = props.getProperty(VDBDefPropertyNames.APPLICATION_VERSION_CREATED_BY);
        String userNameContent = props.getProperty(VDBDefPropertyNames.USER_CREATED_BY);

        String vdbVersionContent = VDBDefPropertyNames.VDBEXPORTER_LATEST_VERSION;
        String serverVersionContent = MetaMatrixProductVersion.VERSION_NUMBER;
        String timeContent = DateUtil.getCurrentDateAsString();

        Element configurationVersion = new Element(VDBDefPropertyNames.VDB_EXPORTER_VERSION);
        configurationVersion.addContent(vdbVersionContent);
        headerElement.addContent(configurationVersion);

        if (applicationCreatedByContent != null) {
            Element applicationCreatedBy = new Element(VDBDefPropertyNames.APPLICATION_CREATED_BY);
            applicationCreatedBy.addContent(applicationCreatedByContent);
            headerElement.addContent(applicationCreatedBy);
        }

        if (applicationVersionCreatedByContent != null) {
            Element applicationVersionCreatedBy = new Element(VDBDefPropertyNames.APPLICATION_VERSION_CREATED_BY);
            applicationVersionCreatedBy.addContent(applicationVersionCreatedByContent);
            headerElement.addContent(applicationVersionCreatedBy);
        }

        if (userNameContent == null) {
            userNameContent = DEFAULT_USER_CREATED_BY;
        }

        Element userName = new Element(VDBDefPropertyNames.USER_CREATED_BY);
        userName.addContent(userNameContent);
        headerElement.addContent(userName);

        Element serverVersion = new Element(VDBDefPropertyNames.METAMATRIX_SYSTEM_VERSION);
        serverVersion.addContent(serverVersionContent);
        headerElement.addContent(serverVersion);

        Element time = new Element(VDBDefPropertyNames.TIME);
        time.addContent(timeContent);
        headerElement.addContent(time);

        return headerElement;

    }

    public void updateVDBName( Element root,
                               String newname,
                               String archiveName ) throws Exception {

        Element vdbInfoElement = root.getChild(VDBConstants.VDBElementNames.VDBInfo.ELEMENT);
        if (vdbInfoElement == null) {
            throw new Exception(VdbEditPlugin.Util.getString("VDBDefnXMLHelper.Invalid_xml_section",//$NON-NLS-1$
                                                             VDBConstants.VDBElementNames.VDBInfo.ELEMENT));
        }

        // obtain any defaults that are defined
        List propertyElements = vdbInfoElement.getChildren(VDBConstants.VDBElementNames.Property.ELEMENT);
        if (propertyElements != null) {
            Iterator iterator = propertyElements.iterator();
            for (int i = 1; iterator.hasNext(); i++) {
                Element element = (Element)iterator.next();
                String name = element.getAttributeValue(VDBConstants.VDBElementNames.Property.Attributes.NAME);
                // if the propery is the vdb name, then set the new name
                if (name.equalsIgnoreCase(VDBConstants.VDBElementNames.VDBInfo.Properties.NAME)) {
                    element.setAttribute(VDBConstants.VDBElementNames.Property.Attributes.VALUE, newname);
                } else if (name.equalsIgnoreCase(VDBConstants.VDBElementNames.VDBInfo.Properties.ARCHIVE_NAME)) {
                    element.setAttribute(VDBConstants.VDBElementNames.Property.Attributes.VALUE, archiveName);
                }
            }
        }

    }

    public final Properties getVDBExecutionProperties( Element element ) {
        Properties props = new Properties();

        // this isn't the execution properties element, then try to get the execution properties element
        if (!element.getName().equals(VDBConstants.VDBElementNames.ExecutionProperties.ELEMENT)) {
            element = element.getChild(VDBConstants.VDBElementNames.ExecutionProperties.ELEMENT);
            if (element == null) {
                return props;
            }
        }

        List elements = element.getChildren();
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            final Element e = (Element)it.next();
            props.setProperty(e.getName(), e.getText());
        }
        return props;
    }

    public void updateExecutionProperties( Element root,
                                           Properties executionProps ) throws Exception {

        Element executionPropsElement = root.getChild(VDBConstants.VDBElementNames.ExecutionProperties.ELEMENT);
        if (executionPropsElement == null) {
            executionPropsElement = new Element(VDBConstants.VDBElementNames.ExecutionProperties.ELEMENT);
            root.addContent(2, executionPropsElement);
        }

        // obtain any defaults that are defined
        List propertyElements = executionPropsElement.getChildren(VDBConstants.VDBElementNames.Property.ELEMENT);
        Iterator propsIter = executionProps.keySet().iterator();
        while (propsIter.hasNext()) {
            String propName = (String)propsIter.next();
            String value = executionProps.getProperty(propName);
            Iterator elementIter = propertyElements.iterator();
            boolean elementExist = false;
            while (elementIter.hasNext()) {
                Element element = (Element)elementIter.next();
                String elementName = element.getAttributeValue(VDBConstants.VDBElementNames.Property.Attributes.NAME);
                if (propName.equalsIgnoreCase(elementName)) {
                    element.setAttribute(VDBConstants.VDBElementNames.Property.Attributes.VALUE, value);
                    elementExist = true;
                    break;
                }
            }
            if (!elementExist) {
                this.addPropertyElement(executionPropsElement, propName, value);
            }
        }
    }
}
