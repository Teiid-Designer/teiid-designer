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

package com.metamatrix.modeler.internal.dqp.ui.wizards;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
public class I18n extends NLS {
    //
    // Shared
    //

    public static String NameHeader;

    public static String StatusHeader;

    //
    // DetailsAreaPanel
    //

    public static String Details;

    //
    // ConnectorImportWizard
    //

    public static String WizardTitle;

    public static String ImportCanceled;

    public static String ImportException;

    //
    // ConnectorTypeSelectionPage
    //

    public static String AllConnectorFileExtensionNames;

    public static String BrowseConnectorFileButton;

    public static String BrowseConnectorFileLabel;

    public static String BrowseConnectorFileToolTip;

    public static String CafFileExtensionName;

    public static String CdkFileExtensionName;

    public static String ConnectorFileBrowseDialogTitle;

    public static String ConnectorFileMissingMsg;

    public static String ConnectorTypeSelectionPageTitle;

    public static String ManyConnectorTypesToImportNoConnectorsMsg;

    public static String ManyConnectorTypesToImportSelectConnectorsMsg;

    public static String NoConnectorTypesToImportSelectConnectorsMsg;

    public static String NoConnectorTypesToImportNoConnectorsMsg;

    public static String OneConnectorTypeToImportNoConnectorsMsg;

    public static String OneConnectorTypeToImportSelectConnectorsMsg;

    //
    // ConnectorSelectionPage
    //

    public static String BindingsAndTypesBeingImportedMsg;

    public static String ConnectorSelectionPageTitle;

    public static String NoTypesNoBindingsBeingImportedMsg;

    public static String TypeHeader;

    //
    // JarSelectionPage
    //

    public static String AllJarsAccountedForMsg;

    public static String ArchiveSelectionPageTitle;

    public static String BrowseJarFile;

    public static String BrowseJarFileToolTip;

    public static String InvalidJarSelection;

    public static String JarExistsInConfigurationPath;

    public static String JarExistsInCaf;

    public static String JarFileBrowseDialogDescription;

    public static String JarFileBrowseDialogTitle;

    public static String MissingJarFilesMsg;

    public static String MultipleJarsSelectedMsg;

    public static String NoJarSelection;

    public static String NoJarsRequiredMsg;

    public static String PathHeader;

    public static String UseExistingJar;

    public static String UseExistingJarToolTip;

    public static String ValidJarSelection;

    // ===========================================================================================================================
    // Class Initializer
    // ===========================================================================================================================

    static {
        // load message values from bundle file
        NLS.initializeMessages("com.metamatrix.modeler.internal.dqp.ui.wizards.i18n", I18n.class); //$NON-NLS-1$
    }
}
