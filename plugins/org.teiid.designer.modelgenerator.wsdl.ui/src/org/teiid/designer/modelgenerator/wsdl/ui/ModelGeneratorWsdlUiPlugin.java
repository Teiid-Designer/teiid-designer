/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.PluginUtil;
import org.teiid.core.util.PluginUtilImpl;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.AbstractActionService;
import org.teiid.designer.ui.common.actions.ActionService;



/**
 * The <code>ModelGeneratorWsdlUiPlugin</code> is the plugin for the Modeler Web Service UI.
 * @since 4.2
 */
public final class ModelGeneratorWsdlUiPlugin extends AbstractUiPlugin 
                                              implements ModelGeneratorWsdlUiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The shared instance of this class.
     * @since 4.2
     */
    private static ModelGeneratorWsdlUiPlugin plugin;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a <code>WebserviceUiPlugin</code>.
     * @since 4.2
     */
    public ModelGeneratorWsdlUiPlugin() {
        // Save this instance to be shared via the getDefault() method.
        ModelGeneratorWsdlUiPlugin.plugin = this;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the shared instance of this class.
     * @return the plugin
     * @since 4.2
     */
    public static ModelGeneratorWsdlUiPlugin getDefault() {
        return ModelGeneratorWsdlUiPlugin.plugin;
    }
    
    /**
     * Has the benign side-effect of adding a section for the specified object to the dialog settings if that section does not
     * already exist.
     * @param theObject the object whose settings are being requested
     * @return the dialog settings for the specified object
     * @since 4.2
     */
    public static IDialogSettings getDialogSettings(Object theObject) {
        // Get dialog settings, creating section if necessary
        final IDialogSettings settings = getDefault().getDialogSettings();
        final String name = theObject.getClass().getName();
        IDialogSettings section = settings.getSection(name);

        if (section == null) {
            section = settings.addNewSection(name);
        }

        return section;
    }

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);

        // Initialize logging/i18n/debugging utility
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
    }
    
    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return UTIL;
    }
    
    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        return new AbstractActionService(this, page) {
            @Override
			public IAction getDefaultAction(String theActionId) {
                return null;
            }
        };
    }

    public ActionService getActionService() {
        return getActionService(getLastValidPage());
    }
}
