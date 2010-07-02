/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * @since 4.2
 */
public class VdbUiPlugin extends AbstractUIPlugin implements VdbUiConstants {

    /**
     * The singleton instance of this plug-in
     */
    public static VdbUiPlugin singleton;

    /**
     * @param severity
     * @return The image associated with the supplied severity
     */
    public static Image getImage( final int severity ) {
        switch (severity) {
            case IStatus.ERROR: {
                return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
            }
            case IStatus.WARNING: {
                return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
            }
            case IStatus.INFO: {
                return JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
            }
        }
        return null;
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        singleton = this;
        // Initialize logging/i18n/debugging utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        super.stop(context);
        singleton = null;
    }
}
