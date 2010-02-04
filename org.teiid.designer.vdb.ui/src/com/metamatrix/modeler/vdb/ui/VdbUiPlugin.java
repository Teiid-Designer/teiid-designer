/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.AbstractActionService;
import com.metamatrix.ui.actions.ActionService;


/** 
 * @since 4.2
 */
public class VdbUiPlugin extends AbstractUiPlugin implements VdbUiConstants {
    //============================================================================================================================
    // Static Variables

    /**
     * The shared instance of this class.
     * 
     * @since 4.2
     */
    private static VdbUiPlugin plugin;

    //============================================================================================================================
    // Static Methods

    /**
     * Returns the shared instance of this class.
     * 
     * @return
     * @since 4.2
     */
    public static VdbUiPlugin getDefault() {
        return VdbUiPlugin.plugin;
    }

    /**
     * Has the benign side-effect of adding a section for the specified object to the dialog settings if that section does not
     * already exist.
     * 
     * @param object
     * @return The dialog settings for the specified object.
     * @since 4.2
     */
    public static IDialogSettings getDialogSettings(final Object object) {
        // Get dialog settings, creating section if necessary
        final IDialogSettings settings = getDefault().getDialogSettings();
        final String name = object.getClass().getName();
        IDialogSettings section = settings.getSection(name);
        if (section == null) {
            section = settings.addNewSection(name);
        }
        return section;
    }

    //============================================================================================================================
    // Constructors

    /**
     * @since 4.2
     */
    public VdbUiPlugin() {
        // Save this instance to be shared via the getDefault() method.
        VdbUiPlugin.plugin = this;
    }

    //============================================================================================================================
    // Overridden Methods

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        // Initialize logging/i18n/debugging utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }

    //============================================================================================================================
    // Utility Methods

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }
    
    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        return new AbstractActionService(this, page) {
            public IAction getDefaultAction(String theActionId) {
                return null;
            }
        };
    }
}
