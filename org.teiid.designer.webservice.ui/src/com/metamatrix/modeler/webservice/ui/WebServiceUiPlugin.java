/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui;

import org.osgi.framework.BundleContext;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IWorkbenchPage;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.webservice.ui.util.WebServiceNotificationListener;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.AbstractActionService;
import com.metamatrix.ui.actions.ActionService;

/**
 * The <code>WebServiceUiPlugin</code> is the plugin for the Modeler Web Service UI.
 * 
 * @since 4.2
 */
public final class WebServiceUiPlugin extends AbstractUiPlugin implements
                                                              IInternalUiConstants {
    
    // ===========================================================================================================================
    // Static Variables

    /**
     * The shared instance of this class.
     * 
     * @since 4.2
     */
    private static WebServiceUiPlugin plugin;

    // ===========================================================================================================================
    // Static Methods

    /**
     * Gets the shared instance of this class.
     * 
     * @return the plugin
     * @since 4.2
     */
    public static WebServiceUiPlugin getDefault() {
        return WebServiceUiPlugin.plugin;
    }

    /**
     * Has the benign side-effect of adding a section for the specified object to the dialog settings if that section does not
     * already exist.
     * 
     * @param object
     *            the object whose settings are being requested
     * @return the dialog settings for the specified object
     * @since 4.2
     */
    public static IDialogSettings getDialogSettings(Object object) {
        // Get dialog settings, creating section if necessary
        final IDialogSettings settings = getDefault().getDialogSettings();
        final String name = object.getClass().getName();
        IDialogSettings section = settings.getSection(name);

        if (section == null) {
            section = settings.addNewSection(name);
        }

        return section;
    }
    
    // ===========================================================================================================================
    // Variables

    private WebServiceNotificationListener notificationListener = new WebServiceNotificationListener();
    
    // ===========================================================================================================================
    // Constructors

    /**
     * Constructs a <code>WebserviceUiPlugin</code>.
     * 
     * @since 4.2
     */
    public WebServiceUiPlugin() {
        // Save this instance to be shared via the getDefault() method.
        WebServiceUiPlugin.plugin = this;
    }

    // ===========================================================================================================================
    // Methods

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.2
     */
    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        return new AbstractActionService(this, page) {

            public IAction getDefaultAction(String theActionId) {
                return null;
            }
        };
    }

    /** 
     * @since 4.2
     */
    public ActionService getActionService() {
        return getActionService(getLastValidPage());
    }
    
    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     * @since 4.2
     */
    @Override
    public PluginUtil getPluginUtil() {
        return UTIL;
    }

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        // Initialize logging/i18n/debugging utility
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
        
        ModelUtilities.addNotifyChangedListener(notificationListener);
    }

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        ModelUtilities.removeNotifyChangedListener(notificationListener);
        super.stop(context);
    }
}
