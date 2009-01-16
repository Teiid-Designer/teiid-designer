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
import com.metamatrix.modeler.modelgenerator.xml.modelextension.impl.BaseXMLRelationalExtensionManagerImpl;

/**
 * The model extension for the XML Relational File Connector. Adds the File Name metadata extension.
 * 
 * @author jdoyle
 */
public class XMLFileExtensionManager extends BaseXMLRelationalExtensionManagerImpl {

    static final String MODEL_FILE_NAME = "XMLFileConnectorExtensions.xmi"; //$NON-NLS-1$
    public static final String PACKAGE_NAME = "XMLFileExtension"; //$NON-NLS-1$
    static final String PACKAGE_PREFIX = "xmlf"; //$NON-NLS-1$
    static final String PACKAGE_NS_URI = "http://www.metamatrix.com/metamodels/XMLFile"; //$NON-NLS-1$
    static final String TABLE_NAME = "XML File Table"; //$NON-NLS-1$
    static final String TABLE_FILE_NAME = "File Name"; //$NON-NLS-1$

    private XAttribute fileNameTableAttribute;

    @Override
    public void createTableExtensions( ExtensionFactory factory,
                                       XClass table ) {
        super.createTableExtensions(factory, table);
        fileNameTableAttribute = factory.createXAttribute();
        fileNameTableAttribute.setName(XMLFileExtensionManager.TABLE_FILE_NAME);
        fileNameTableAttribute.setEType(EcorePackage.eINSTANCE.getEString());
        table.getEStructuralFeatures().add(fileNameTableAttribute);
    }

    @Override
    public void assignAttribute( XAttribute attribute ) {
        if (attribute.getName().equals(getTableName())) {
            fileNameTableAttribute = attribute;
        }
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
        return TABLE_NAME;
    }

    @Override
    public void createColumnExtensions( ExtensionFactory factory,
                                        XClass column ) {

    }

    public String getColumnName() {
        // this may cause problems - want to see if it blows and how to then handle no column extensions (and no table as well)
        return null;
    }
}
