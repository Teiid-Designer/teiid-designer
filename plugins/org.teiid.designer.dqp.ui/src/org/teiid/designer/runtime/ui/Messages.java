/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
@SuppressWarnings( "javadoc" )
public class Messages extends NLS {

    public static String GenerateDynamicVdbAction_nothingExportedMessage;
    public static String GenerateDynamicVdbAction_nothingExportedTitle;
    public static String GenerateArchiveVdbAction_exceptionTitle;

    public static String GenerateVdbWizard_validation_versionNotInteger;
    public static String GenerateVdbWizard_validation_targetLocationUndefined;
    public static String GenerateVdbWizard_validation_vdbFileNameUndefined;
    public static String GenerateVdbWizard_validation_targetLocationNotExist;
    public static String GenerateVdbWizard_validation_targetFileAlreadyExists;

    public static String GenerateDynamicVdbWizard_title;
    public static String GenerateDynamicVdbWizard_validation_vdbMissingXmlExtension;
    public static String GenerateDynamicVdbWizard_validation_noDynamicVdbGenerated;
    public static String GenerateDynamicVdbWizard_exportLocationAlreadyExists;
    public static String GenerateDynamicVdbWizard_exportLocationFailedToCreateFile;

    public static String GenerateDynamicVdbPageOne_title;
    public static String GenerateDynamicVdbPageOne_summaryGroupName;
    public static String GenerateDynamicVdbPageOne_vdb;
    public static String GenerateDynamicVdbPageOne_vdbName;
    public static String GenerateDynamicVdbPageOne_version;
    public static String GenerateDynamicVdbPageOne_dynamicVdbDefinition;
    public static String GenerateDynamicVdbPageOne_dynamicVdbName;
    public static String GenerateDynamicVdbPageOne_dynamicVdbNameTooltip;
    public static String GenerateDynamicVdbPageOne_dynamicVdbDestination;
    public static String GenerateDynamicVdbPageOne_options;
    public static String GenerateDynamicVdbPageOne_location;
    public static String GenerateDynamicVdbPageOne_browse;
    public static String GenerateDynamicVdbPageOne_dynamicVdbFileName;
    public static String GenerateDynamicVdbPageOne_dynamicVdbFileNameToolTip;
    public static String GenerateDynamicVdbPageOne_excludeSourceDdlMetadata;
    public static String GenerateDynamicVdbPageOne_suppressDefaultAttributesOption;
    public static String GenerateDynamicVdbPageOne_suppressDefaultAttributesOptionTooltip;
    public static String GenerateDynamicVdbPageOne_overwriteFilesOptionLabel;
    public static String GenerateDynamicVdbPageOne_overwriteVDBOptionTooltip;

    public static String GenerateDynamicVdbPageTwo_title;
    public static String GenerateDynamicVdbPageTwo_fileContents;
    public static String GenerateDynamicVdbPageTwo_exportXmlTitle;
    public static String GenerateDynamicVdbPageTwo_exportXmlTooltip;
    public static String GenerateDynamicVdbPageTwo_exportXmlLabel;
    public static String GenerateDynamicVdbPageTwo_exportXmlDialogTitle;
    public static String GenerateDynamicVdbPageTwo_exportXmlErrorMessages;
    public static String GenerateDynamicVdbPageTwo_clickGenerateToCreateVdb;
    public static String GenerateDynamicVdbPageTwo_clickFinishToSaveVdb;

    public static String GenerateArchiveVdbAction_nothingExportedMessage;
    public static String GenerateArchiveVdbAction_nothingExportedTitle;
    public static String GenerateDynamicVdbAction_exceptionTitle;

    public static String GenerateArchiveVdbWizard_title;
    public static String GenerateArchiveVdbWizard_validation_vdbMissingVdbExtension;
    public static String GenerateArchiveVdbWizard_validation_noArchiveVdbGenerated;
    public static String GenerateArchiveVdbWizard_cancelJobName;

    public static String GenerateArchiveVdbPageOne_title;
    public static String GenerateArchiveVdbPageOne_dynamicVdbFile;
    public static String GenerateArchiveVdbPageOne_vdbName;
    public static String GenerateArchiveVdbPageOne_vdbXmlContents;

    public static String GenerateArchiveVdbPageTwo_title;
    public static String GenerateArchiveVdbPageTwo_vdbDetails;
    public static String GenerateArchiveVdbPageTwo_originalVdbName;
    public static String GenerateArchiveVdbPageTwo_location;
    public static String GenerateArchiveVdbPageTwo_browse;
    public static String GenerateArchiveVdbPageTwo_version;
    public static String GenerateArchiveVdbPageTwo_archiveVdbName;
    public static String GenerateArchiveVdbPageTwo_archiveVdbNameTooltip;
    public static String GenerateArchiveVdbPageTwo_vdbArchiveFileName;
    public static String GenerateArchiveVdbPageTwo_vdbArchiveFileNameTooltip;
    public static String GenerateArchiveVdbPageTwo_sourceModels;
    public static String GenerateArchiveVdbPageTwo_viewModels;
    public static String GenerateArchiveVdbPageTwo_ddlAsDescriptionOptionLabel;
    public static String GenerateArchiveVdbPageTwo_ddlAsDescriptionOptionTooltip;
    public static String GenerateArchiveVdbPageTwo_overwriteFilesOptionLabel;
    public static String GenerateArchiveVdbPageTwo_overwriteVDBAndModelsOptionTooltip;
    public static String GenerateArchiveVdbPageTwo_clickFinishToSaveVdbAndModels;

    public static String GenerateVdbButton_Title;
    public static String GenerateVdbButton_Tooltip;

    public static String GenerateVdbWizard_ConvertJobName;
    
    public static String PreviewDataInputDialog_previewDynamicVdbXmlTabLabel;
    public static String PreviewDataInputDialog_sqlQueryLabel;
    public static String PreviewDataInputDialog_previewSqlLabel;
    public static String PreviewDataInputDialog_title;
    public static String PreviewDataInputDialog_initialMessage;
    public static String PreviewDataInputDialog_previewXMLLabel;
    
    static {
        NLS.initializeMessages("org.teiid.designer.runtime.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
