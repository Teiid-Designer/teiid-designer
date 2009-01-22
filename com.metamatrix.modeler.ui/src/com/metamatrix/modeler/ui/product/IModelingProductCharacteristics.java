/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.product;

import com.metamatrix.ui.product.IProductCharacteristics;

public interface IModelingProductCharacteristics extends IProductCharacteristics
{
    public static final String ACTION_NEW_MODEL                         = "NewModel"; //$NON-NLS-1$
    public static final String ACTION_NEW_VDB                           = "NewVDB"; //$NON-NLS-1$
    public static final String ACTION_IMPORT                            = "Import"; //$NON-NLS-1$
    public static final String ACTION_JDBC_IMPORT                       = "ImportJdbc"; //$NON-NLS-1$
    public static final String ACTION_XSD_IMPORT                        = "ImportXsd"; //$NON-NLS-1$
    public static final String ACTION_NEW_XML_DOC_MODEL                 = "NewXmlDocumentModel"; //$NON-NLS-1$
    public static final String ACTION_EXECUTE_VDB                       = "ExecuteVDB"; //$NON-NLS-1$
    public static final String ACTION_REBUILD_VDB                       = "RebuildVDB"; //$NON-NLS-1$
}
