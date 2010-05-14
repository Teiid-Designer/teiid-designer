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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * @since 4.2
 */
public class VdbUiPlugin extends AbstractUIPlugin implements VdbUiConstants {

    /**
     * The ID within the {@link JFaceResources#getImageRegistry() JFace image registry} of the checked check box image
     */
    public static final String CHECKED_BOX = "checkedBox"; //$NON-NLS-1$

    /**
     * The ID within the {@link JFaceResources#getImageRegistry() JFace image registry} of the unchecked check box image
     */
    public static final String UNCHECKED_BOX = "uncheckedBox"; //$NON-NLS-1$

    /**
     * The singleton instance of this plug-in
     */
    public static VdbUiPlugin singleton;

    private static final Image createCheckBoxImage( final boolean checked ) {
        Display display = Display.getCurrent();
        if (display == null) display = Display.getDefault();
        final Shell shell = new Shell(display, SWT.NO_TRIM);
        final Button checkBox = new Button(shell, SWT.CHECK);
        checkBox.setSelection(checked);
        checkBox.pack();
        final Point size = checkBox.getSize();
        shell.setSize(size);
        final Color greenScreen = new Color(display, 255, 255, 254);
        checkBox.setBackground(greenScreen);
        shell.setBackground(greenScreen);
        shell.open();
        final GC gc = new GC(checkBox);
        final Image image = new Image(display, size.x, size.y);
        gc.copyArea(image, 0, 0);
        gc.dispose();
        shell.close();
        final ImageData imageData = image.getImageData();
        imageData.transparentPixel = imageData.palette.getPixel(greenScreen.getRGB());
        return new Image(display, imageData);
    }

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
        // Register commonly-used images
        JFaceResources.getImageRegistry().put(UNCHECKED_BOX, createCheckBoxImage(false));
        JFaceResources.getImageRegistry().put(CHECKED_BOX, createCheckBoxImage(true));
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
