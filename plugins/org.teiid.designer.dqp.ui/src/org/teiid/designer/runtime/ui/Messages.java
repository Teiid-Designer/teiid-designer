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
@SuppressWarnings("javadoc")
public class Messages  extends NLS {

	public static String GenerateDynamicVdbAction_nothingExportedMessage;
	public static String GenerateDynamicVdbAction_nothingExportedTitle;
	
	public static String GenerateDynamicVdbWizard_title;
	public static String GenerateDynamicVdbWizard_validation_versionNotInteger;
	public static String GenerateDynamicVdbWizard_validation_targetLocationUndefined;
	public static String GenerateDynamicVdbWizard_validation_vdbFileNameUndefined;
	public static String GenerateDynamicVdbWizard_validation_vdbMissingXmlExtension;
	
	public static String GenerateDynamicVdbPageOne_title;
	public static String GenerateDynamicVdbPageOne_summaryGroupName;
	public static String GenerateDynamicVdbPageOne_vdb;
	public static String GenerateDynamicVdbPageOne_version;
	public static String GenerateDynamicVdbPageOne_dynamicVdbDefinition;
	public static String GenerateDynamicVdbPageOne_dynamicVdbName;
	public static String GenerateDynamicVdbPageOne_dynamicVdbNameTooltip;
	
	public static String GenerateDynamicVdbPageTwo_title;
	public static String GenerateDynamicVdbPageTwo_location;
	public static String GenerateDynamicVdbPageTwo_browse;
	public static String GenerateDynamicVdbPageTwo_dynamicVdbFileName;
	public static String GenerateDynamicVdbPageTwo_dynamicVdbFileNameToolTip;
	public static String GenerateDynamicVdbPageTwo_fileContents;
	public static String GenerateDynamicVdbPageTwo_exportXmlTitle;
	public static String GenerateDynamicVdbPageTwo_exportXmlTooltip;
	public static String GenerateDynamicVdbPageTwo_exportXmlDialogTitle;
	public static String GenerateDynamicVdbPageTwo_exportXmlErrorMessages;
	
	public static String GenerateArchiveVdbAction_nothingExportedMessage;
	public static String GenerateArchiveVdbAction_nothingExportedTitle;
	
	public static String GenerateArchiveVdbWizard_title;
	public static String GenerateArchiveVdbWizard_validation_versionNotInteger;
	public static String GenerateArchiveVdbWizard_validation_targetLocationUndefined;
	public static String GenerateArchiveVdbWizard_validation_vdbFileNameUndefined;
	public static String GenerateArchiveVdbWizard_validation_vdbMissingVdbExtension;
	
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
    
    static {
        NLS.initializeMessages("org.teiid.designer.runtime.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
