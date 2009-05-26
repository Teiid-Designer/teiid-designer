/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.modelextension;

import org.eclipse.emf.ecore.EcorePackage;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.ObjectExtension;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.impl.XMLRequestResponseExtensionManagerImpl;

/**
 * Adds SOAP related metadata extensions to the base XMLRequestResponseExtensions.
 */
public class XMLWSDLExtensionManager extends XMLRequestResponseExtensionManagerImpl {

    static final String MODEL_FILE_NAME = "XMLSOAPConnectorExtensions.xmi"; //$NON-NLS-1$
    static final String PACKAGE_NAME = "XMLSOAPExtension"; //$NON-NLS-1$
    static final String PACKAGE_PREFIX = "xmlsoap"; //$NON-NLS-1$
    static final String PACKAGE_NS_URI = "http://www.metamatrix.com/metamodels/XMLSOAP"; //$NON-NLS-1$
    static final String XML_SOAP_SCHEMA = "XML SOAP Schema"; //$NON-NLS-1$
    static final String XML_SOAP_TABLE = "XML SOAP Table"; //$NON-NLS-1$
    static final String XML_SOAP_COLUMN = "XML SOAP Column"; //$NON-NLS-1$

    static final String TABLE_SOAP_ACTION = "SOAPAction"; //$NON-NLS-1$
    static final String SERVICE_NAME = "ServiceName"; //$NON-NLS-1$
    static final String SERVICE_NAMESPACE = "ServiceNamespace"; //$NON-NLS-1$
    static final String PORT_NAME = "PortName"; //$NON-NLS-1$
    static final String PORT_NAMESPACE = "PortNamespace"; //$NON-NLS-1$

    public static final Integer SIMPLE_SOAP_ARRAY_MULTIPLE_VALUES = 3;
    public static final Integer COMPLEX_SOAP_ARRAY_MULTIPLE_VALUES = 4;

    private XAttribute soapActionTableAttribute;
    private XAttribute serviceNameSchemaAttribute;
    private XAttribute serviceNamespaceSchemaAttribute;
    private XAttribute portNameSchemaAttribute;
    private XAttribute portNamespaceSchemaAttribute;

    public XMLWSDLExtensionManager() {
        super();
        multipleValueEnumValues.put("SimpleSoapArrayElement", SIMPLE_SOAP_ARRAY_MULTIPLE_VALUES); //$NON-NLS-1$
        multipleValueEnumValues.put("ComplexSoapArrayElement", COMPLEX_SOAP_ARRAY_MULTIPLE_VALUES); //$NON-NLS-1$
    }

    @Override
    public void createSchemaExtensions( ExtensionFactory xFactory,
                                        XClass schema ) {
        super.createSchemaExtensions(xFactory, schema);

        serviceNameSchemaAttribute = xFactory.createXAttribute();
        serviceNameSchemaAttribute.setName(SERVICE_NAME);
        serviceNameSchemaAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        schema.getEStructuralFeatures().add(serviceNameSchemaAttribute);

        serviceNamespaceSchemaAttribute = xFactory.createXAttribute();
        serviceNamespaceSchemaAttribute.setName(SERVICE_NAMESPACE);
        serviceNamespaceSchemaAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        schema.getEStructuralFeatures().add(serviceNamespaceSchemaAttribute);

        portNameSchemaAttribute = xFactory.createXAttribute();
        portNameSchemaAttribute.setName(PORT_NAME);
        portNameSchemaAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        schema.getEStructuralFeatures().add(portNameSchemaAttribute);

        portNamespaceSchemaAttribute = xFactory.createXAttribute();
        portNamespaceSchemaAttribute.setName(PORT_NAMESPACE);
        portNamespaceSchemaAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        schema.getEStructuralFeatures().add(portNamespaceSchemaAttribute);
    }

    @Override
    public void createTableExtensions( ExtensionFactory xFactory,
                                       XClass table ) {
        super.createTableExtensions(xFactory, table);
        soapActionTableAttribute = xFactory.createXAttribute();
        soapActionTableAttribute.setName(TABLE_SOAP_ACTION);
        soapActionTableAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        table.getEStructuralFeatures().add(soapActionTableAttribute);
    }

    @Override
    public void assignAttribute( XAttribute attribute ) {
        super.assignAttribute(attribute);
        if (attribute.getName().equals(TABLE_SOAP_ACTION)) {
            soapActionTableAttribute = attribute;
        } else if (attribute.getName().equals(SERVICE_NAME)) {
            serviceNameSchemaAttribute = attribute;
        } else if (attribute.getName().equals(SERVICE_NAMESPACE)) {
            serviceNamespaceSchemaAttribute = attribute;
        } else if (attribute.getName().equals(PORT_NAME)) {
            portNameSchemaAttribute = attribute;
        } else if (attribute.getName().equals(PORT_NAMESPACE)) {
            portNamespaceSchemaAttribute = attribute;
        }
    }

    public void setSoapAction( Table relTab,
                               String soapAction ) {
        ObjectExtension objectExtension = new ObjectExtension(relTab, theTableXClass, ModelerCore.getModelEditor());
        objectExtension.eDynamicSet(soapActionTableAttribute, soapAction);
    }

    public void setServiceName( Schema schema,
                                String serviceName ) {
        ObjectExtension objectExtension = new ObjectExtension(schema, theSchemaXClass, ModelerCore.getModelEditor());
        objectExtension.eDynamicSet(serviceNameSchemaAttribute, serviceName);
    }

    public void setServiceNamespace( Schema schema,
                                     String serviceNamespace ) {
        ObjectExtension objectExtension = new ObjectExtension(schema, theSchemaXClass, ModelerCore.getModelEditor());
        objectExtension.eDynamicSet(serviceNamespaceSchemaAttribute, serviceNamespace);
    }

    public void setPortName( Schema schema,
                             String portName ) {
        ObjectExtension objectExtension = new ObjectExtension(schema, theSchemaXClass, ModelerCore.getModelEditor());
        objectExtension.eDynamicSet(portNameSchemaAttribute, portName);
    }

    public void setPortNamespace( Schema schema,
                                  String portNamespace ) {
        ObjectExtension objectExtension = new ObjectExtension(schema, theSchemaXClass, ModelerCore.getModelEditor());
        objectExtension.eDynamicSet(portNamespaceSchemaAttribute, portNamespace);
    }

    @Override
    public String getColumnName() {
        return XML_SOAP_COLUMN;
    }

    public String getModelFileName() {
        return MODEL_FILE_NAME;
    }

    public String getPackageName() {
        return PACKAGE_NAME;
    }

    public String getPackageNsUri() {
        return PACKAGE_NS_URI;
    }

    public String getPackagePrefix() {
        return PACKAGE_PREFIX;
    }

    @Override
    public String getTableName() {
        return XML_SOAP_TABLE;
    }

    @Override
    public String getSchemaName() {
        return XML_SOAP_SCHEMA;
    }
}
