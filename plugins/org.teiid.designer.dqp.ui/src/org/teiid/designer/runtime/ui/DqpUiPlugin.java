/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.ActionService;


/**
 * The main plugin class to be used in the desktop.
 *
 * @since 8.0
 */
public class DqpUiPlugin extends AbstractUiPlugin implements DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(DqpUiPlugin.class);

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString( String key ) {
        ResourceBundle bundle = DqpUiPlugin.getDefault().getResourceBundle();
        try {
            return (bundle != null) ? bundle.getString(key) : key;
        } catch (MissingResourceException e) {
            return key;
        }
    }

    private static String getString( String theKey ) {
        return UTIL.getStringOrKey(PREFIX + theKey);
    }

    /**
     * Used in non-Eclipse environments to identify the install location of the <code>modeler.transformation</code> plugin.
     * <strong>To be used for testing purposes only.</strong>
     * 
     * @since 6.0.0
     */
    public String testInstallPath;

    // The shared instance.
    private static DqpUiPlugin plugin;

    /**
     * Returns the shared instance.
     */
    public static DqpUiPlugin getDefault() {
        return plugin;
    }

    public static void showErrorDialog( Shell shell,
                                        Exception error ) {
        MessageDialog.openError(shell, getString("errorDialogTitle"), error.getMessage()); //$NON-NLS-1$
    }

    // Resource bundle.
    private ResourceBundle resourceBundle;

    /**
     * The constructor.
     */
    public DqpUiPlugin() {
        plugin = this;
    }


    @Override
    protected ActionService createActionService( IWorkbenchPage workbenchPage ) {
        return null;
    }

    public Image getAnImage( String key ) {
        return getOrCreateImage(key);
    }

    private Image getOrCreateImage( String key ) {
        Image image = getImageRegistry().get(key);
        if (image == null) {
            ImageDescriptor d = getImageDescriptor(key);

            // make sure we still need to put in registry (above call
            // seems to be registering the image):
            image = getImageRegistry().get(key);
            if (image == null) {
                image = d.createImage();
                getImageRegistry().put(key, image);
            } // endif
        }
        return image;
    }

    @Override
    public PluginUtil getPluginUtil() {
        return UTIL;
    }

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        // Initialize logging/i18n/debugging utility
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
    }
}
