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
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.LoggingUtil;


/**
 * Common constants used by this plugin.
 */
public interface UiConstants {

    String PLUGIN_ID = UiConstants.class.getPackage().getName();

    PluginUtil UTIL = new LoggingUtil(PLUGIN_ID);

    interface EditorIds {
        String MED_EDITOR = PLUGIN_ID + ".modelDefinitionEditor"; //$NON-NLS-1$
        String MED_OVERVIEW_PAGE = PLUGIN_ID + ".overviewPage"; //$NON-NLS-1$
        String MED_PROPERTIES_PAGE = PLUGIN_ID + ".propertiesPage"; //$NON-NLS-1$
        String MED_SOURCE_PAGE = PLUGIN_ID + ".sourcePage"; //$NON-NLS-1$
    }
    
    interface ExtensionIds {
        String PROBLEM_MARKER = PLUGIN_ID + ".medMarker"; //$NON-NLS-1$";
    }

    interface ImageIds {
        String FOLDER = "icons/"; //$NON-NLS-1$
        String ADD_DESCRIPTION = FOLDER + "add-metaclass.png"; //$NON-NLS-1$
        String ADD_DISPLAY_NAME = FOLDER + "add-metaclass.png"; //$NON-NLS-1$
        String ADD_METACLASS = FOLDER + "add-metaclass.png"; //$NON-NLS-1$
        String ADD_PROPERTY = FOLDER + "add-metaclass.png"; //$NON-NLS-1$
        String ADD_VALUE = FOLDER + "add-metaclass.png"; //$NON-NLS-1$
        String ATTRIBUTE = FOLDER + "med-attribute.gif"; //$NON-NLS-1$
        String CHECK_MARK = FOLDER + "checkmark.gif"; //$NON-NLS-1$
        String EDIT_DESCRIPTION = FOLDER + "edit-metaclass.png"; //$NON-NLS-1$
        String EDIT_DISPLAY_NAME = FOLDER + "edit-metaclass.png"; //$NON-NLS-1$
        String EDIT_METACLASS = FOLDER + "edit-metaclass.png"; //$NON-NLS-1$
        String EDIT_PROPERTY = FOLDER + "edit-metaclass.png"; //$NON-NLS-1$
        String EDIT_VALUE = FOLDER + "edit-metaclass.png"; //$NON-NLS-1$
        String MED = FOLDER + "med.png"; //$NON-NLS-1$
        String MED_EDITOR = FOLDER + "model-extension-definition-editor.png"; //$NON-NLS-1$
        String METACLASS = FOLDER + "metaclass.png"; //$NON-NLS-1$
        String PROPERTY_DEFINITION = FOLDER + "property-definition.png"; //$NON-NLS-1$
        String REGISTERY_MED_UPDATE_ACTION = FOLDER + "registry-med-update.png"; //$NON-NLS-1$
        String REGISTRY_VIEW = FOLDER + "model-extension-registry-view.png"; //$NON-NLS-1$
        String REMOVE_DESCRIPTION = FOLDER + "remove-metaclass.png"; //$NON-NLS-1$
        String REMOVE_DISPLAY_NAME = FOLDER + "remove-metaclass.png"; //$NON-NLS-1$
        String REMOVE_METACLASS = FOLDER + "remove-metaclass.png"; //$NON-NLS-1$
        String REMOVE_PROPERTY = FOLDER + "remove-metaclass.png"; //$NON-NLS-1$
        String REMOVE_VALUE = FOLDER + "remove-metaclass.png"; //$NON-NLS-1$
        String SHOW_REGISTRY_VIEW_ACTION = REGISTRY_VIEW;
        String UNREGISTER_MED = FOLDER + "unregister-med.png";  //$NON-NLS-1$
    }

    interface Form {
        int COMBO_STYLE = SWT.FLAT | SWT.READ_ONLY | SWT.BORDER;
        int SECTION_STYLE = Section.DESCRIPTION | ExpandableComposite.TITLE_BAR | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT
                | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED;
        int TEXT_STYLE = SWT.BORDER;
        int VIEWER_STYLE = SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER;
    }

    interface ViewIds {
        String REGISTRY_VIEW = PLUGIN_ID + ".views.modelExtensionRegistryView"; //$NON-NLS-1$
    }
}
