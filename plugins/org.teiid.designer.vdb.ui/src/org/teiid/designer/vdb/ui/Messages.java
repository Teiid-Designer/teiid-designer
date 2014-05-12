/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui;

import org.eclipse.osgi.util.NLS;

/**
 *
 */
@SuppressWarnings("javadoc")
public class Messages  extends NLS {

	public static String vdbBuildTaskName;
	public static String vdbFilesBuildProblemMsg;
    public static String vdblFileBuildErrorMsg;
    public static String vdbBuildSubTaskName;
    public static String refactorModelVdbDependencyTitle;
    public static String refactorModelVdbDependencyMessage_noOpenEditors;
    public static String refactorModelVdbDependencyMessage_openEditors;
    public static String fixVdbPath_OpenEditorTitle;
    public static String fixVdbPath_OpenEditorMessage;

	public static String synchronizeVdbLabel;
	public static String extractMissingModelsLabel;
	public static String extractMissingModelsAndSyncLabel;
	public static String migrateXsdFilesFromModelsToOtherLabel;

	public static String modelDetailsPanel_modelDetails;
	public static String modelDetailsPanel_modelDetailsTooltip;
	public static String noSelection;
	public static String modelDetailsPanel_sourceBindingDefinition;
	public static String modelDetailsPanel_sourceBindingDefinitionTooltip;
	public static String modelDetailsPanel_multiSourceLabel;
	public static String modelDetailsPanel_addColumnLabel;
	public static String modelDetailsPanel_sourceNameLabel;
	public static String modelDetailsPanel_translatorNameLabel;
	public static String modelDetailsPanel_jndiNameLabel;
	public static String modelDetailsPanel_multiSourceCheckBoxTooltip;
	public static String modelDetailsPanel_addColumnCheckBoxTooltip;
	public static String modelDetailsPanel_columnAliaslabelTooltip;
	public static String modelDetailsPanel_addButtonTooltip;
	public static String modelDetailsPanel_deleteButtonTooltip;
	public static String modelDetailsPanel_problemsTabLabel;
	public static String modelDetailsPanel_problemsTabTooltip;
	public static String modelDetailsPanel_problemPathLabel;
	public static String modelDetailsPanel_problemDescriptionLabel;
    
    static {
        NLS.initializeMessages("org.teiid.designer.vdb.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
