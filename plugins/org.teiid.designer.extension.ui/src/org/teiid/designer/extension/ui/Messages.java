/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    public static String builtInColumnText;
    public static String descriptionColumnText;
    public static String extendedMetamodelUriColumnText;
    public static String namespacePrefixColumnText;
    public static String namespaceUriColumnText;
    public static String versionColumnText;

    public static String cloneMedActionText;
    public static String cloneMedActionToolTip;
    public static String findMedReferencesActionText;
    public static String findMedReferencesActionToolTip;
    public static String openMedActionText;
    public static String openMedActionToolTip;
    public static String registerMedActionText;
    public static String registerMedActionToolTip;
    public static String unregisterMedActionText;
    public static String unregisterMedActionToolTip;

    public static String registryViewToolTip;

    public static String newMedWizardTitle;
    public static String newMedWizardPageTitle;
    public static String newMedWizardNameLabel;
    public static String newMedWizardFolderLabel;
    public static String newMedWizardInitialMsg;
    public static String newMedWizardCreateFileErrorMsg;
    public static String newMedWizardNotModelProjMsg;
    public static String newMedWizardSelectFolderMsg;
    public static String newMedWizardMedNameErrorMsg;

    public static String manageModelExtensionDefnsActionTitle;

    public static String errorOpeningMedEditor;

    public static String medEditorOverviewPageTitle;
    public static String medEditorOverviewPageToolTip;
    public static String overviewPageExtendedMetaclassDescription;
    public static String overviewPageExtendedMetaclassTitle;

    public static String medEditorPropertiesPageTitle;
    public static String medEditorPropertiesPageToolTip;
    public static String propertiesPageExtensionPropertiesDescription;
    public static String propertiesPageExtensionPropertiesTitle;

    public static String medEditorSourcePageTitle;
    public static String medEditorSourcePageToolTip;

    public static String namespacePrefixLabel;
    public static String namespaceUriLabel;
    public static String extendedMetamodelUriLabel;
    public static String resourcePathLabel;
    public static String versionLabel;
    public static String descriptionLabel;

    public static String addButton;
    public static String editButton;
    public static String removeButton;

    static {
        NLS.initializeMessages("org.teiid.designer.extension.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
