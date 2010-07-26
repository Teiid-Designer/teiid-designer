/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;

/**
 * The main plugin class to be used in the desktop.
 */
public class DqpUiPlugin extends AbstractUiPlugin implements DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(DqpUiPlugin.class);

    /**
     * Used in non-Eclipse environments to identify the install location of the <code>modeler.transformation</code> plugin.
     * <strong>To be used for testing purposes only.</strong>
     * 
     * @since 6.0.0
     */
    public String testInstallPath;

    private static String getString( String theKey ) {
        return UTIL.getStringOrKey(PREFIX + theKey);
    }

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

    // The Vdb Editor Util instance for this plugin
    private IVdbEditorUtil vdbEditorUtil;

    /**
     * The constructor.
     */
    public DqpUiPlugin() {
        plugin = this;
    }

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

    /**
     * Returns the plugin's resource bundle,
     */
    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    @Override
    protected ActionService createActionService( IWorkbenchPage workbenchPage ) {
        return null;
    }

    @Override
    public PluginUtil getPluginUtil() {
        return UTIL;
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

    public IVdbEditorUtil getVdbEditorUtil() {
        if (vdbEditorUtil == null) {

            // look for any extensions to VdbEditorUtil

            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID,
                                                                                               ExtensionPoints.VdbEditorUtil.ID);

            // get the all extensions to the MappingClassStrategies extension point
            IExtension[] extensions = extensionPoint.getExtensions();

            // if there is an extension, use it
            if (extensions.length > 0) {
                IConfigurationElement[] elements = extensions[0].getConfigurationElements();
                Object extension = null;
                for (int j = 0; j < elements.length; j++) {
                    try {

                        extension = elements[j].createExecutableExtension(ExtensionPoints.VdbEditorUtil.CLASSNAME);

                        if (extension instanceof IVdbEditorUtil) {
                            this.vdbEditorUtil = (IVdbEditorUtil)extension;
                        }
                    } catch (Exception theException) {
                        UTIL.log(theException);
                    }
                }
            }

            // if no extension was found, implement the default
            if (vdbEditorUtil == null) {
                vdbEditorUtil = new DefaultVdbEditorUtil();
            }

        }
        return vdbEditorUtil;
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

    @Override
    public void stop( BundleContext theContext ) throws Exception {
        super.stop(theContext);
    }
}
