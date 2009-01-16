/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.diagram.ui.preferences;

import org.eclipse.core.runtime.Preferences;

import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.internal.diagram.ui.PluginConstants;

/**
 * This class contains the Diagram Filter Settings and Groupings
 */
public class FilterSettings implements PluginConstants {

    public static final int DIAGRAM = 0;
    public static final int PACKAGE = 1;
    public static final int GROUP = 2;
    public static final int ATTRIBUTE = 3;
    public static final int OPERATIONS = 4;
    public static final int ASSOCIATIONS = 5;

    // Filter Groupings
    private static final String[][] settings =
        {
            { PluginConstants.Prefs.Filter.DIAGRAM_HIDE_ALL,
              PluginConstants.Prefs.Filter.DIAGRAM_HIDE_DEPENDENCIES,
              PluginConstants.Prefs.Filter.DIAGRAM_HIDE_TRANSFORMATIONS,
              PluginConstants.Prefs.Filter.DIAGRAM_HIDE_NOTES },
            { PluginConstants.Prefs.Filter.PACKAGE_HIDE_STEREOTYPE,
              PluginConstants.Prefs.Filter.PACKAGE_HIDE_LOCATION }, 
            { PluginConstants.Prefs.Filter.GROUP_HIDE_STEREOTYPE, 
              PluginConstants.Prefs.Filter.GROUP_HIDE_LOCATION,
              PluginConstants.Prefs.Filter.GROUP_HIDE_GROUPS,
              PluginConstants.Prefs.Filter.GROUP_HIDE_ATTRIBUTES,
              PluginConstants.Prefs.Filter.GROUP_HIDE_OPERATIONS,
              PluginConstants.Prefs.Filter.GROUP_HIDE_KEYS,
              PluginConstants.Prefs.Filter.GROUP_HIDE_INDEXES },
            { PluginConstants.Prefs.Filter.ATTRIBUTE_HIDE_RETURNTYPE, 
              PluginConstants.Prefs.Filter.ATTRIBUTE_HIDE_VISIBILITY },
            { PluginConstants.Prefs.Filter.OPERATION_HIDE_RETURNTYPE, 
              PluginConstants.Prefs.Filter.OPERATION_HIDE_PARAMETERS,
              PluginConstants.Prefs.Filter.OPERATION_HIDE_VISIBILITY },
            { PluginConstants.Prefs.Filter.ASSOCIATION_HIDE_LABEL, 
              PluginConstants.Prefs.Filter.ASSOCIATION_HIDE_ROLENAMES,
              PluginConstants.Prefs.Filter.ASSOCIATION_HIDE_MULTIPLICITY },
            {  }
    };

    public static boolean getBoolean(String settingID) {
        Preferences pref = DiagramUiPlugin.getDefault().getPluginPreferences();
        return pref.getBoolean(settingID);
    }

    public static boolean getDefaultBoolean(String settingID) {
        Preferences pref = DiagramUiPlugin.getDefault().getPluginPreferences();
        return pref.getDefaultBoolean(settingID);
    }

    public static void setBoolean(String settingID, boolean value) {
        Preferences pref = DiagramUiPlugin.getDefault().getPluginPreferences();
        pref.setValue(settingID, value);
    }

    public static String[] getSettings(int group) {
        return settings[group];
    }

    public static void save() {
        DiagramUiPlugin.getDefault().savePluginPreferences();
    }
}
