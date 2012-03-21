/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.ui.viewsupport;

import java.util.Properties;

/**
 * This interface provides a means for wizards, actions and dialogs to set simple property values in order to hold state within
 * the application.
 * 
 * The initial need for this context was to enable cheat sheets to obtain some kind of state so subsequent tasks could populate
 * downstream wizards and dialogs with previously defined values.
 * 
 * Example would be a cheat sheet step of "Create Teiid Model Project" followed by a "Import from JDBC". The assumption here would be
 * that a user expected the new project be pre-selected in import wizard.
 * 
 */
public interface IPropertiesContext {
	public static final String KEY_PROJECT_NAME = "projectName"; //$NON-NLS-1$
	public static final String KEY_HAS_SOURCES_FOLDER = "hasSourcesFolder"; //$NON-NLS-1$
	public static final String KEY_HAS_VIEWS_FOLDER = "hasViewsFolder"; //$NON-NLS-1$
	public static final String KEY_HAS_SCHEMA_FOLDER = "hasSchemaFolder"; //$NON-NLS-1$
	public static final String KEY_HAS_WS_FOLDER = "hasWSFolder"; //$NON-NLS-1$
	public static final String KEY_LAST_SOURCE_MODEL_NAME = "lastSourceModelName"; //$NON-NLS-1$
	public static final String KEY_LAST_VIEW_MODEL_NAME = "lastViewModelName"; //$NON-NLS-1$
	public static final String KEY_LAST_CONNECTION_PROFILE_ID = "lastConnectionProfileId"; //$NON-NLS-1$
	public static final String KEY_LAST_VDB_NAME = "lastVdbName"; //$NON-NLS-1$
	public static final String KEY_PREVIEW_TARGET_OBJECT = "previewTargetObject"; //$NON-NLS-1$
	public static final String KEY_PREVIEW_TARGET_MODEL = "previewTargetModel"; //$NON-NLS-1$

	/**
	 * Sets the properties context object
	 * 
	 * @param properties
	 */
	public void setProperties(Properties properties);
}
