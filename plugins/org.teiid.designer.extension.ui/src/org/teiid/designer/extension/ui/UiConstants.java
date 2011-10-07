/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui;

import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

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
        String ADD_DESCRIPTION = FOLDER + "add-description.png"; //$NON-NLS-1$
        String ADD_DISPLAY_NAME = FOLDER + "add-display-name.png"; //$NON-NLS-1$
        String ADD_METACLASS = FOLDER + "add-metaclass.png"; //$NON-NLS-1$
        String ADD_PROPERTY = FOLDER + "add-property.png"; //$NON-NLS-1$
        String ADD_VALUE = FOLDER + "add-value.png"; //$NON-NLS-1$
        String CHECK_MARK = FOLDER + "checkmark.gif"; //$NON-NLS-1$
        String EDIT_DESCRIPTION = FOLDER + "edit-description.png"; //$NON-NLS-1$
        String EDIT_DISPLAY_NAME = FOLDER + "edit-display-name.png"; //$NON-NLS-1$
        String EDIT_METACLASS = FOLDER + "edit-metaclass.png"; //$NON-NLS-1$
        String EDIT_PROPERTY = FOLDER + "edit-property.png"; //$NON-NLS-1$
        String EDIT_VALUE = FOLDER + "edit-value.png"; //$NON-NLS-1$
        String MED_EDITOR = FOLDER + "med-editor.png"; //$NON-NLS-1$
        String REGISTERY_MED_UPDATE_ACTION = FOLDER + "registry-med-update.png"; //$NON-NLS-1$
        String REGISTRY_VIEW = FOLDER + "model-extension-registry-view.png"; //$NON-NLS-1$
        String REMOVE_DESCRIPTION = FOLDER + "remove-description.png"; //$NON-NLS-1$
        String REMOVE_DISPLAY_NAME = FOLDER + "remove-display-name.png"; //$NON-NLS-1$
        String REMOVE_METACLASS = FOLDER + "remove-metaclass.png"; //$NON-NLS-1$
        String REMOVE_PROPERTY = FOLDER + "remove-property.png"; //$NON-NLS-1$
        String REMOVE_VALUE = FOLDER + "remove-value.png"; //$NON-NLS-1$
        String SHOW_REGISTRY_VIEW_ACTION = REGISTRY_VIEW;
        String TOGGLE_DEFAULT_VALUE = FOLDER + "toggle-default-value.png"; //$NON-NLS-1$
    }

    interface Form {
        int COMBO_STYLE = SWT.FLAT | SWT.READ_ONLY | SWT.BORDER;
        int SECTION_STYLE = Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT
                | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED;
        int TEXT_STYLE = SWT.BORDER;
        int VIEWER_STYLE = SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER;
    }

    interface ViewIds {
        String REGISTRY_VIEW = "org.teiid.designer.extension.ui.views.modelExtensionRegistryView"; //$NON-NLS-1$
    }
}
