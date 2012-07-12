/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.PluginUtil;
import org.teiid.core.util.PluginUtilImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.diagram.ui.actions.DiagramActionService;
import org.teiid.designer.diagram.ui.editor.DiagramActionContributor;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.diagram.ui.editor.DiagramEditorUtil;
import org.teiid.designer.diagram.ui.util.AspectManager;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.PreferenceKeyAndDefaultValue;
import org.teiid.designer.ui.common.actions.ActionService;
import org.teiid.designer.ui.editors.AbstractModelEditorPageActionBarContributor;
import org.teiid.designer.ui.editors.ModelEditorPage;


/**
 * @since 4.0
 */
public class DiagramUiPlugin extends AbstractUiPlugin
implements DiagramUiConstants {
    //============================================================================================================================
    // Static Variables

	//The shared instance.
	private static DiagramUiPlugin plugin;

    private static DiagramNotationManager notationManager;
    private static DiagramTypeManager diagramTypeManager;
    private static AspectManager amDiagramAspectManager;
    private static DiagramActionContributor dacDiagramActionContributor;
//    private static DefaultColorPaletteManager colorPaletteManager;

    //============================================================================================================================
    // Static Methods

    /**
     * Returns the shared instance.
     * @since 4.0
     */
    public static DiagramUiPlugin getDefault() {
        return plugin;
    }

    /**
     * @return
     * @since 4.0
     */
    public static DiagramNotationManager getDiagramNotationManager() {
        if ( notationManager == null ) {
            notationManager = new DiagramNotationManager();
        }
        return notationManager;
    }

    /**
     * @return
     * @since 4.0
     */
    public static DiagramTypeManager getDiagramTypeManager() {
        if ( diagramTypeManager == null ) {
            diagramTypeManager = new DiagramTypeManager();
        }
        return diagramTypeManager;
    }

    /**
     * @return
     * @since 4.0
     */
    public static AspectManager getDiagramAspectManager() {
        if ( amDiagramAspectManager == null ) {
            amDiagramAspectManager
                = new AspectManager( ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID );
        }
        return amDiagramAspectManager;
    }

    /**
     * @return
     * @since 4.0
     */
    public static void registerDiagramActionForSelection(ISelectionListener action) {
        ActionService actionService =
            DiagramUiPlugin.getDefault().getActionService(DiagramUiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage());
        actionService.addWorkbenchSelectionListener(action);
    }

    /**
     * @return
     * @since 4.0
     */
    public static void unregisterDiagramActionForSelection(ISelectionListener action) {
        ActionService actionService =
            DiagramUiPlugin.getDefault().getActionService(DiagramUiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage());
        actionService.removeWorkbenchSelectionListener(action);
    }

    /**
     * @return
     * @since 4.0
     */
    public static void updateEditorForPreferences() {
        DiagramEditor[] editors = DiagramEditorUtil.getInitializedDiagramEditors();
        for (int i = 0; i < editors.length; i++) {
            DiagramEditor editor = editors[i];
            editor.updateDiagramPreferences();
        } // endfor
    }

    //============================================================================================================================
    // Constructors

    /**
     * The constructor.
     * @since 4.0
     */
    public DiagramUiPlugin() {
        DiagramUiPlugin.plugin = this;
    }

    //============================================================================================================================
    // AbstractUiPlugin Methods

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

        storeDefaultPreferenceValues();
    }

    //============================================================================================================================
    // AbstractUiPlugin Methods

    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        return new DiagramActionService(page);
    }

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }

	//=========================================================================================================
	// Instance methods
	private void storeDefaultPreferenceValues() {
		//Store default values of preferences.  Needs to be done once.  Does not change current
		//values of preferences if any are already stored.
		IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();
		for (int i = 0; i < PluginConstants.Prefs.PREFERENCES.length; i++) {
			PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore,
					PluginConstants.Prefs.PREFERENCES[i]);
		}
		for (int i = 0; i < PluginConstants.Prefs.Appearance.PREFERENCES.length; i++) {
			PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore,
					PluginConstants.Prefs.Appearance.PREFERENCES[i]);
		}
		for (int i = 0; i < PluginConstants.Prefs.Filter.PREFERENCES.length; i++) {
			PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore,
					PluginConstants.Prefs.Filter.PREFERENCES[i]);
		}
        for (int i = 0; i < PluginConstants.Prefs.Print.PREFERENCES.length; i++) {
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore,
                    PluginConstants.Prefs.Print.PREFERENCES[i]);
        }
		DiagramUiPlugin.getDefault().savePreferences();
	}

    public static AbstractModelEditorPageActionBarContributor getActionBarContributor(ModelEditorPage newPage) {
      if (dacDiagramActionContributor == null) {
          dacDiagramActionContributor = new DiagramActionContributor(newPage);
      } else {
          dacDiagramActionContributor.setEditorPage(newPage);
      }

      return dacDiagramActionContributor;
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void stop(BundleContext theContext) throws Exception {
        cleanUpWindowResources();
        super.stop(theContext);
    }

    private void cleanUpWindowResources() {
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.DEPENDENCY.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.GROUP_BKGRND.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.GROUP_HEADER.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.LOGICAL_GROUP_BKGRND.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.LOGICAL_GROUP_HEADER.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.OUTLINE.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.TEMP_GROUP_BKGRND.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.TEMP_GROUP_HEADER.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.VIRTUAL_GROUP_BKGRND.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.VIRTUAL_GROUP_HEADER.dispose();
    	if( !DiagramUiConstants.Colors.DEPENDENCY.isDisposed() )
    		DiagramUiConstants.Colors.VIRTUAL_RS_GROUP_HEADER.dispose();
    }
}
