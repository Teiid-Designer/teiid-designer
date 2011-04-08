/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.extension.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.LoggingUtil;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;


public class ExtensionUiPlugin  extends AbstractUiPlugin implements ExtensionUiConstants {

    // The shared instance.
    private static ExtensionUiPlugin plugin;

    /**
     * Returns the shared instance.
     */
    public static ExtensionUiPlugin getDefault() {
        return plugin;
    }

    /**
     * The constructor.
     */
    public ExtensionUiPlugin() {
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
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        // Initialize logging/i18n/debugging utility
        ((LoggingUtil)UTIL).initializePlatformLogger(this);

    }

    @Override
    public void stop( BundleContext context ) throws Exception {
        super.stop(context);
    }
}