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

package com.metamatrix.modeler.internal.ui.product;

import java.util.Properties;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import com.metamatrix.common.util.PropertiesUtils;
import com.metamatrix.modeler.ui.UiConstants;


/**
 * The <code>ModelerRcpPreferenceInitializer</code> initializes user-assignable preferences.
 * A corresponding properties class is where the prefs can be changed.
 * @since 4.4
 */
public class ModelerRcpPreferenceInitializer extends AbstractPreferenceInitializer
                                             implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private Properties props;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ModelerRcpPreferenceInitializer() {
        final String PROPS_NAME = "preferenceInitializer.properties"; //$NON-NLS-1$

        try {
            this.props = PropertiesUtils.loadAsResource(this.getClass(), PROPS_NAME);
        } catch (Exception theException) {
            Util.log(theException);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private boolean getBoolean(String thePropName,
                               boolean theDefaultValue) {
        return (this.props != null) ? PropertiesUtils.getBooleanProperty(this.props, thePropName, theDefaultValue)
                                    : theDefaultValue;
    }

    private int getInt(String thePropName,
                       int theDefaultValue) {
        return (this.props != null) ? PropertiesUtils.getIntProperty(this.props, thePropName, theDefaultValue)
                                    : theDefaultValue;
    }

    /**
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     * @since 4.3
     */
    @Override
    public void initializeDefaultPreferences() {
        initializedIdePreferences();
        initializeWorkbenchPreferences();
        initializeEclipseEditorPreferences();
        this.props = null;
    }

    private void initializedIdePreferences() {
        /* --------- DESIGN NOTE -------------------------------------------------------------------------------
         * These preferences are found in org.eclipse.ui.internal.ide.IDEPreferenceInitializer. Some have been
         * left out. The IDEPreferenceInitializer was initialized via an extension in the org.eclipse.ui plugin.
         * This contribution was commented out of the org.eclipse.ui plugin in the Data Services Modeler.
         * ----------------------------------------------------------------------------------------------------- */

        IEclipsePreferences prefs = new DefaultScope().getNode(IDEWorkbenchPlugin.getDefault().getBundle().getSymbolicName());

        //
        // assign values from the properties file. Variable names exactly match associated properties key.
        //

        boolean exitPromptOnCloseLastWindow = getBoolean("exitPromptOnCloseLastWindow", true); //$NON-NLS-1$
        boolean refreshWorkspaceOnStartup = getBoolean("refreshWorkspaceOnStartup", false); //$NON-NLS-1$
        boolean saveAllBeforeBuild = getBoolean("saveAllBeforeBuild", false); //$NON-NLS-1$
        int saveInterval = getInt("saveInterval", 5); //$NON-NLS-1$
        boolean welcomeDialog = getBoolean("welcomeDialog", true); //$NON-NLS-1$

        //
        // initialize properties
        //

        prefs.putBoolean(IDEInternalPreferences.EXIT_PROMPT_ON_CLOSE_LAST_WINDOW, exitPromptOnCloseLastWindow);
        prefs.putBoolean(IDEInternalPreferences.REFRESH_WORKSPACE_ON_STARTUP, refreshWorkspaceOnStartup);
        prefs.putBoolean(IDEInternalPreferences.SAVE_ALL_BEFORE_BUILD, saveAllBeforeBuild);
        prefs.putInt(IDEInternalPreferences.SAVE_INTERVAL, saveInterval);
        prefs.putBoolean(IDEInternalPreferences.WELCOME_DIALOG, welcomeDialog);
    }

    private void initializeWorkbenchPreferences() {
        /* --------- DESIGN NOTE -------------------------------------------------------------------------------
         * These preferences are found in org.eclipse.ui.internal.WorkbenchPreferenceInitializer. Some have been
         * left out. The WorkbenchPreferenceInitializer was initialized via an extension in the
         * org.eclipse.ui.workbench plugin. This contribution was commented out of the
         * org.eclipse.ui.workbench plugin.
         * ----------------------------------------------------------------------------------------------------- */

        IEclipsePreferences prefs = new DefaultScope().getNode(WorkbenchPlugin.getDefault().getBundle().getSymbolicName());

        //
        // assign values from the properties file. Variable names exactly match associated properties key.
        //

        boolean colorIcons = getBoolean("colorIcons", true); //$NON-NLS-1$
        int editorTabPosition = getInt("editorTabPosition", SWT.TOP); //$NON-NLS-1$
        int editorTabWidth = getInt("editorTabWidth", 3); //$NON-NLS-1$
        boolean editorListDisplayFullName = getBoolean("editorListDisplayFullName", false); //$NON-NLS-1$
        boolean editorListPullDownAction = getBoolean("editorListPullDownAction", false); //$NON-NLS-1$
        int editorListSortCriteria = getInt("editorListSortCriteria", IPreferenceConstants.EDITORLIST_NAME_SORT); //$NON-NLS-1$
        int recentFiles = getInt("recentFiles", 4); //$NON-NLS-1$
        boolean showMultipleEditorTabs = getBoolean("showMultipleEditorTabs", true); //$NON-NLS-1$
        int viewTabPosition = getInt("viewTabPosition", SWT.TOP); //$NON-NLS-1$

        //
        // initialize properties
        //

        prefs.putBoolean(IPreferenceConstants.COLOR_ICONS, colorIcons);
        prefs.putInt(IWorkbenchPreferenceConstants.EDITOR_TAB_POSITION, editorTabPosition);
        prefs.putInt(IPreferenceConstants.EDITOR_TAB_WIDTH, editorTabWidth);
        prefs.putBoolean(IPreferenceConstants.EDITORLIST_DISPLAY_FULL_NAME, editorListDisplayFullName);
        prefs.putBoolean(IPreferenceConstants.EDITORLIST_PULLDOWN_ACTIVE, editorListPullDownAction);
        prefs.putInt(IPreferenceConstants.EDITORLIST_SORT_CRITERIA, editorListSortCriteria); // 0=name sort, 1=mru
        prefs.putInt(IPreferenceConstants.RECENT_FILES, recentFiles);
        prefs.putBoolean(IWorkbenchPreferenceConstants.SHOW_MULTIPLE_EDITOR_TABS, showMultipleEditorTabs);
		prefs.putInt(IWorkbenchPreferenceConstants.VIEW_TAB_POSITION, viewTabPosition);
    }


    // jh Defect 21085: a new initialize method
    private void initializeEclipseEditorPreferences() {

        IEclipsePreferences prefs = new DefaultScope().getNode( "org.eclipse.ui.editors" ); //$NON-NLS-1$

        //
        // assign values from the properties file. Variable names exactly match associated properties key.
        //
        int tabWidth = getInt("tabWidth", 4); //$NON-NLS-1$
        int undoHistorySize = getInt("undoHistorySize", 25); //$NON-NLS-1$
        int printMarginColumn = getInt("printMarginColumn", 80); //$NON-NLS-1$

        /*
         * jh Defect 22018 (21085 followup): I am commenting out all of the booleans because the
         *     HyperLink one was causing NullPointerExceptions, and the color-related ones
         *     were causing a selected line to be all black.  It had these effects in the
         *     Xsd source editor, not in the SQL Explorer editor.
         */
//        boolean currentLine = getBoolean("currentLine", true); //$NON-NLS-1$
//        boolean overviewRuler = getBoolean("overviewRuler", true); //$NON-NLS-1$
//        boolean hyperlinksEnabled = getBoolean("hyperlinksEnabled", true); //$NON-NLS-1$
//        boolean wideCaret = getBoolean("wideCaret", true); //$NON-NLS-1$
//        boolean selectionForegroundColor = getBoolean("selectionForegroundColor", true); //$NON-NLS-1$
//        boolean selectionBackgroundColor = getBoolean("selectionBackgroundColor", true); //$NON-NLS-1$

        //
        // initialize properties
        //
        prefs.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, tabWidth);
        prefs.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE, undoHistorySize);
        prefs.putInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_PRINT_MARGIN_COLUMN, printMarginColumn);

//        prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_CURRENT_LINE, currentLine);
//        prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_OVERVIEW_RULER, overviewRuler);
//        prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_HYPERLINKS_ENABLED, hyperlinksEnabled);
//        prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_WIDE_CARET, wideCaret);
//        prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR, selectionForegroundColor);
//        prefs.putBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR, selectionBackgroundColor);
    }

}
