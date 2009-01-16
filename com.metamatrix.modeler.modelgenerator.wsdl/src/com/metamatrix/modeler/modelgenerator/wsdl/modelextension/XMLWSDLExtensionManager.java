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

package com.metamatrix.modeler.modelgenerator.wsdl.modelextension;

import org.eclipse.emf.ecore.EcorePackage;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
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
    static final String XML_SOAP_TABLE = "XML SOAP Table"; //$NON-NLS-1$
    static final String XML_SOAP_COLUMN = "XML SOAP Column"; //$NON-NLS-1$

    static final String TABLE_SOAP_ACTION = "SOAPAction"; //$NON-NLS-1$

    public static final Integer SIMPLE_SOAP_ARRAY_MULTIPLE_VALUES = 3;
    public static final Integer COMPLEX_SOAP_ARRAY_MULTIPLE_VALUES = 4;

    private XAttribute soapActionTableAttribute;

    public XMLWSDLExtensionManager() {
        super();
        multipleValueEnumValues.put("SimpleSoapArrayElement", SIMPLE_SOAP_ARRAY_MULTIPLE_VALUES); //$NON-NLS-1$
        multipleValueEnumValues.put("ComplexSoapArrayElement", COMPLEX_SOAP_ARRAY_MULTIPLE_VALUES); //$NON-NLS-1$
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
        }
    }

    public void setSoapAction( Table relTab,
                               String soapAction ) {
        ObjectExtension objectExtension = new ObjectExtension(relTab, theTableXClass, ModelerCore.getModelEditor());
        objectExtension.eDynamicSet(soapActionTableAttribute, soapAction);
    }

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

    public String getTableName() {
        return XML_SOAP_TABLE;
    }
}
