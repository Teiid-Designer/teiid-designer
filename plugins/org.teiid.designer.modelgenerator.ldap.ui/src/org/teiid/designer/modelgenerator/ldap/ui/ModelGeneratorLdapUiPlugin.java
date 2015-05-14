/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.ldap.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.AbstractActionService;
import org.teiid.designer.ui.common.actions.ActionService;



/**
 * The <code>ModelGeneratorLdapUiPlugin</code> is the plugin for the Modeler LDAP Service UI.
 * @since 8.0
 */
public final class ModelGeneratorLdapUiPlugin extends AbstractUiPlugin implements ModelGeneratorLdapUiConstants {

    private static final String LDAP_BROWSER_UI_PLUGIN_ID = "org.apache.directory.studio.ldapbrowser.ui"; //$NON-NLS-1$

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The shared instance of this class.
     * @since 4.2
     */
    private static ModelGeneratorLdapUiPlugin plugin;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a <code>WebserviceUiPlugin</code>.
     * @since 4.2
     */
    public ModelGeneratorLdapUiPlugin() {
        // Save this instance to be shared via the getDefault() method.
        ModelGeneratorLdapUiPlugin.plugin = this;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the shared instance of this class.
     * @return the plugin
     * @since 4.2
     */
    public static ModelGeneratorLdapUiPlugin getDefault() {
        return ModelGeneratorLdapUiPlugin.plugin;
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

        //
        // Ensure that the browser.ui plugin is fully started before using
        // any of the ldap-related wizards. This is due to
        // https://issues.apache.org/jira/browse/DIRSTUDIO-1046
        //
        Bundle ldapBrowserBundle = Platform.getBundle(LDAP_BROWSER_UI_PLUGIN_ID);
        ldapBrowserBundle.start();
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
}
