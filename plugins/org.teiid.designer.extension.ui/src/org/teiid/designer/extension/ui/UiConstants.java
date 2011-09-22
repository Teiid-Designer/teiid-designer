/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.LoggingUtil;

/**
 * Common constants used by this plugin.
 */
public interface UiConstants {

    String PLUGIN_ID = UiConstants.class.getPackage().getName();

    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);

    interface EditorIds {
        String MED_EDITOR = "org.teiid.designer.extension.ui.modelDefinitionEditor"; //$NON-NLS-1$
        String MED_OVERVIEW_PAGE = "org.teiid.designer.extension.ui.overviewPage"; //$NON-NLS-1$
        String MED_PROPERTIES_PAGE = "org.teiid.designer.extension.ui.propertiesPage"; //$NON-NLS-1$
    }

    interface ImageIds {
        String FOLDER = "icons/"; //$NON-NLS-1$
        String CHECK_MARK = FOLDER + "checkmark.gif"; //$NON-NLS-1$
        String MED_EDITOR = FOLDER + "med-editor.png"; //$NON-NLS-1$
        String REGISTERY_MED_UPDATE_ACTION = FOLDER + "registry-med-update.png"; //$NON-NLS-1$
        String REGISTRY_VIEW = FOLDER + "model-extension-registry-view.png"; //$NON-NLS-1$
        String SHOW_REGISTRY_VIEW_ACTION = REGISTRY_VIEW;
    }

    interface ViewIds {
        String REGISTRY_VIEW = "org.teiid.designer.extension.ui.views.modelExtensionRegistryView"; //$NON-NLS-1$
    }
}
