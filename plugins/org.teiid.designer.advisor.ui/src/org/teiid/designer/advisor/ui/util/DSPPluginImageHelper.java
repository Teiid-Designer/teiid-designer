/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.util;

import org.eclipse.swt.graphics.Image;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;

/**
 * 
 */
public class DSPPluginImageHelper implements AdvisorUiConstants.Images {
    // ---------- IMAGES -----------------------------
    public final Image UNCHECKED_BOX_IMAGE = AdvisorUiPlugin.getDefault().getImage(EMPTY_BOX);
    public final Image PROBLEM_BOX_IMAGE = AdvisorUiPlugin.getDefault().getImage(PROBLEM_BOX);
    public final Image CHECKED_BOX_IMAGE = AdvisorUiPlugin.getDefault().getImage(CHECKED_BOX);
    public final Image EMPTY_BOX_IMAGE = AdvisorUiPlugin.getDefault().getImage(EMPTY_GRAY_BOX);
    public final Image WARNING_EMPTY_BOX_IMAGE = AdvisorUiPlugin.getDefault().getImage(WARNING_EMPTY_BOX);
    public final Image WARNING_CHECKED_BOX_IMAGE = AdvisorUiPlugin.getDefault().getImage(WARNING_CHECKED_BOX);
    public final Image WARNING_PROBLEM_BOX_IMAGE = AdvisorUiPlugin.getDefault().getImage(WARNING_PROBLEM_BOX);

    public final Image PREVIEW_WSDL_IMAGE = AdvisorUiPlugin.getDefault().getImage(PREVIEW_WSDL);
    public final Image PREVIEW_WSDL_GRAY_IMAGE = AdvisorUiPlugin.getDefault().getImage(PREVIEW_WSDL_GRAY);
    public final Image PREVIEW_WSDL_ERROR_IMAGE = AdvisorUiPlugin.getDefault().getImage(PREVIEW_WSDL_ERROR);
    public final Image VDB_OK_IMAGE = AdvisorUiPlugin.getDefault().getImage(VDB_OK);
    public final Image VDB_SAVE_REQUIRED_IMAGE = AdvisorUiPlugin.getDefault().getImage(VDB_SAVE_REQUIRED);
    public final Image VDB_ERROR_IMAGE = AdvisorUiPlugin.getDefault().getImage(VDB_ERROR);
    public final Image VDB_REBUILD_VDB_IMAGE = AdvisorUiPlugin.getDefault().getImage(REBUILD_VDB);
    public final Image HELP_IMAGE = AdvisorUiPlugin.getDefault().getImage(HELP_ICON);
    public final Image LIGHTBULB_IMAGE = AdvisorUiPlugin.getDefault().getImage(LIGHTBULB_ICON);
    public final Image NEW_VDB_IMAGE = AdvisorUiPlugin.getDefault().getImage(NEW_VDB);

    public final Image BINDING_IMAGE = AdvisorUiPlugin.getDefault().getImage(CONNECTOR_BINDINGS);
    public final Image IMPORT_XSD_IMAGE = AdvisorUiPlugin.getDefault().getImage(IMPORT_XSD);
    public final Image IMPORT_JDBC_IMAGE = AdvisorUiPlugin.getDefault().getImage(IMPORT_JDBC);
    public final Image NEW_WEB_SERVICE_IMAGE = AdvisorUiPlugin.getDefault().getImage(NEW_WEB_SERVICE);
    public final Image PROBLEMS_VIEW_IMAGE = AdvisorUiPlugin.getDefault().getImage(PROBLEMS_VIEW);
    public final Image NEW_MODEL_IMAGE = AdvisorUiPlugin.getDefault().getImage(NEW_MODEL);
    public final Image FIX_IT_IMAGE = AdvisorUiPlugin.getDefault().getImage(FIX_IT);
    public final Image BUILD_IMAGE = AdvisorUiPlugin.getDefault().getImage(BUILD_ALL);

    public final Image MODEL_PROJECT_IMAGE = AdvisorUiPlugin.getDefault().getImage(MODEL_PROJECT);
    public final Image NEW_MODEL_PROJECT_IMAGE = AdvisorUiPlugin.getDefault().getImage(NEW_PROJECT_ACTION);
    public final Image VDB_PROJECT_IMAGE = AdvisorUiPlugin.getDefault().getImage(VDB_PROJECT);
    
    public final Image IMPORT_WSDL_IMAGE = AdvisorUiPlugin.getDefault().getImage(IMPORT_WSDL);
    public final Image EXPORT_WAR_IMAGE = AdvisorUiPlugin.getDefault().getImage(GENERATE_WAR);
    public final Image EXECUTE_VDB_IMAGE = AdvisorUiPlugin.getDefault().getImage(EXECUTE_VDB_ACTION);
    public final Image EDIT_VDB_IMAGE = AdvisorUiPlugin.getDefault().getImage(EDIT_VDB_ACTION);
    public final Image PREVIEW_DATA_IMAGE = AdvisorUiPlugin.getDefault().getImage(PREVIEW_DATA);
    public final Image OPEN_DATA_SOURCE_EXPLORER_IMAGE = AdvisorUiPlugin.getDefault().getImage(DATA_SOURCE_EXPLORER_VIEW);
    public final Image NEW_CONNECTION_PROFILE_IMAGE = AdvisorUiPlugin.getDefault().getImage(NEW_CONNECTION_PROFILE);
}
