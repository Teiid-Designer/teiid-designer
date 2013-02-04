/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.ui.properties.extension;

import org.eclipse.osgi.util.NLS;


/**
 * @since 8.0
 */
public class Messages extends NLS {

    public static String errorCreatingPropertyDescriptors;
    public static String errorObtainingPropertyFromAssistant;
    public static String modelExtensionAssistantNotFound;
	public static String modelExtensionPropertyCategory;
    public static String openEditorDialogMessage;
	public static String openEditorDialogTitle;
	public static String propertyDefinitiontNotFound;
	public static String unexpectedPropertySourceId;
	public static String unexpectedPropertyValueType;
	public static String valueIsNotAnAllowedValue;
	public static String workspaceFileNotFound;
	
    public static String selectedFileNotAJarDialogTitle;
    public static String selectedFileNotAJarDialogMessage;

    public static String chooseFileFromWorkspaceDialogUdfTitle;
    public static String chooseFileFromWorkspaceDialogUdfMessage;
    public static String chooseFileFromWorkspaceDialogFileTitle;
    public static String chooseFileFromWorkspaceDialogFileMessage;

    public static String chooseUdfFromWorkspaceRadioText;
    public static String chooseUdfFromFileSystemRadioText;
    public static String chooseFileFromWorkspaceRadioText;
    public static String chooseFileFromFileSystemRadioText;
    public static String copyToWorkspaceCheckboxText;
    public static String workspaceOrFileSystemDialogUdfTitle;
    public static String workspaceOrFileSystemDialogFileTitle;
    public static String workspaceOrFileSystemDialogUdfMessage;
    public static String workspaceOrFileSystemDialogFileMessage;
    public static String workspaceOptionIsDisabledMessage;
    	
    static {
        NLS.initializeMessages("org.teiid.designer.ui.properties.extension.messages", Messages.class); //$NON-NLS-1$
    }
}
