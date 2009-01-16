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

package com.metamatrix.modeler.modelgenerator.xml.modelextension;

import org.eclipse.emf.ecore.EcorePackage;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.modeler.modelgenerator.xml.modelextension.impl.XMLRequestResponseExtensionManagerImpl;

public class XMLHTTPExtensionManager extends XMLRequestResponseExtensionManagerImpl {

    public XMLHTTPExtensionManager() {
        super();
    }

    static final String MODEL_FILE_NAME = "XMLHTTPConnectorExtensions.xmi"; //$NON-NLS-1$
    static final String PACKAGE_NAME = "XMLHTTPExtension"; //$NON-NLS-1$
    static final String PACKAGE_PREFIX = "xmlhttp"; //$NON-NLS-1$
    static final String PACKAGE_NS_URI = "http://www.metamatrix.com/metamodels/XMLHTTP"; //$NON-NLS-1$
    static final String XML_HTTP_TABLE = "XML HTTP Table"; //$NON-NLS-1$
    static final String XML_HTTP_COLUMN = "XML HTTP Column"; //$NON-NLS-1$

    static final String TABLE_SERVLET_CALL_PATH = "ServletCallPathforURL"; //$NON-NLS-1$

    private XAttribute servletCallPathforURLTableAttribute;

    public String getModelFileName() {
        return MODEL_FILE_NAME;
    }

    public String getPackageName() {
        return PACKAGE_NAME;
    }

    public String getPackagePrefix() {
        return PACKAGE_PREFIX;
    }

    public String getPackageNsUri() {
        return PACKAGE_NS_URI;
    }

    public String getTableName() {
        return XML_HTTP_TABLE;
    }

    public String getColumnName() {
        return XML_HTTP_COLUMN;
    }

    @Override
    public void createTableExtensions( ExtensionFactory xFactory,
                                       XClass table ) {
        super.createTableExtensions(xFactory, table);
        servletCallPathforURLTableAttribute = xFactory.createXAttribute();
        servletCallPathforURLTableAttribute.setName(XMLHTTPExtensionManager.TABLE_SERVLET_CALL_PATH);
        servletCallPathforURLTableAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        table.getEStructuralFeatures().add(servletCallPathforURLTableAttribute);
    }

    @Override
    public void createColumnExtensions( ExtensionFactory xFactory,
                                        XClass column ) {
        super.createColumnExtensions(xFactory, column);
    }

    @Override
    public void assignAttribute( XAttribute attribute ) {
        super.assignAttribute(attribute);
        if (attribute.getName().equals(TABLE_SERVLET_CALL_PATH)) {
            servletCallPathforURLTableAttribute = attribute;
        }
    }
}
