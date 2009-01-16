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
