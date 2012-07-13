/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.modelextension;

import org.eclipse.emf.ecore.EcorePackage;
import org.teiid.designer.metamodels.core.extension.ExtensionFactory;
import org.teiid.designer.metamodels.core.extension.XAttribute;
import org.teiid.designer.metamodels.core.extension.XClass;
import org.teiid.designer.modelgenerator.xml.modelextension.impl.XMLRequestResponseExtensionManagerImpl;


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

    @Override
	public String getModelFileName() {
        return MODEL_FILE_NAME;
    }

    @Override
	public String getPackageName() {
        return PACKAGE_NAME;
    }

    @Override
	public String getPackagePrefix() {
        return PACKAGE_PREFIX;
    }

    @Override
	public String getPackageNsUri() {
        return PACKAGE_NS_URI;
    }

    @Override
    public String getTableName() {
        return XML_HTTP_TABLE;
    }

    @Override
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
