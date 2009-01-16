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

package com.metamatrix.modeler.internal.xsd.ui.wizards;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.IImportStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;


/** 
 * @since 4.3
 */
public class XsdImportOperation extends ImportOperation {

    /** 
     * @param theContainerPath
     * @param theSource
     * @param theProvider
     * @param theOverwriteImplementor
     * @since 4.3
     */
    public XsdImportOperation(IPath theContainerPath,
                              Object theSource,
                              IImportStructureProvider theProvider,
                              IOverwriteQuery theOverwriteImplementor) {
        super(theContainerPath, theSource, theProvider, theOverwriteImplementor);
    }

    /** 
     * @param theContainerPath
     * @param theSource
     * @param theProvider
     * @param theOverwriteImplementor
     * @param theFilesToImport
     * @since 4.3
     */
    public XsdImportOperation(IPath theContainerPath,
                              Object theSource,
                              IImportStructureProvider theProvider,
                              IOverwriteQuery theOverwriteImplementor,
                              List theFilesToImport) {
        super(theContainerPath, theSource, theProvider, theOverwriteImplementor, theFilesToImport);
    }

    /** 
     * @param theContainerPath
     * @param theProvider
     * @param theOverwriteImplementor
     * @param theFilesToImport
     * @since 4.3
     */
    public XsdImportOperation(IPath theContainerPath,
                              IImportStructureProvider theProvider,
                              IOverwriteQuery theOverwriteImplementor,
                              List theFilesToImport) {
        super(theContainerPath, theProvider, theOverwriteImplementor, theFilesToImport);
    }

}
