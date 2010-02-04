/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
