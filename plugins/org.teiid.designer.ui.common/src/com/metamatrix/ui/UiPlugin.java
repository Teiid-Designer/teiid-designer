/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui;

import org.eclipse.jface.action.IAction;
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
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.actions.AbstractActionService;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.graphics.GlobalUiColorManager;
import com.metamatrix.ui.graphics.GlobalUiFontManager;
import com.metamatrix.ui.internal.InternalUiConstants;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @since 4.0
 */
public final class UiPlugin extends AbstractUiPlugin implements InternalUiConstants, UiConstants.EclipsePluginIds {

    /**
     * The ID within the {@link JFaceResources#getImageRegistry() JFace image registry} of the checked check box image
     */
    public static final String CHECKED_BOX = "checkedBox"; //$NON-NLS-1$

    /**
     * The ID within the {@link JFaceResources#getImageRegistry() JFace image registry} of the unchecked check box image
     */
    public static final String UNCHECKED_BOX = "uncheckedBox"; //$NON-NLS-1$

    // The shared instance.
    private static UiPlugin plugin;

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
     * Returns the shared instance.
     * 
     * @since 4.0
     */
    public static UiPlugin getDefault() {
        return UiPlugin.plugin;
    }

    /**
     * The constructor.
     * 
     * @since 4.0
     */
    public UiPlugin() {
        UiPlugin.plugin = this;
    }

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService( final IWorkbenchPage page ) {
        return new UiActionService(page);
    }

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);

        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

        // Load Eclipse UI images into registry
        final Runnable op = new Runnable() {
            public void run() {
                registerPluginImage(UI_IDE, Images.CHECKED_CHECKBOX);
                registerPluginImage(UI_IDE, Images.UNCHECKED_CHECKBOX);
                registerPluginImage(UI_IDE, Images.REFRESH);
                registerPluginImage(UI, Images.TASK_ERROR);
                registerPluginImage(UI, Images.TASK_WARNING);
                registerPluginImage(UI, Images.TASK_INFO);
            }
        };
        if (Display.getCurrent() == null) {
            Display.getDefault().asyncExec(op);
        } else {
            op.run();
        }

        // Register commonly-used images. Note that unlike the checkbox images above, these are created using the native UI's
        // checkbox image
        JFaceResources.getImageRegistry().put(UNCHECKED_BOX, createCheckBoxImage(false));
        JFaceResources.getImageRegistry().put(CHECKED_BOX, createCheckBoxImage(true));
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.AbstractUiPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        // This plugin contains the ModelerUiColorManager so it needs to be told to dispose of all it's cached colors
        GlobalUiColorManager.dispose();
        GlobalUiFontManager.dispose();
        super.stop(context);
    }

    /**
     * @since 4.0
     */
    private class UiActionService extends AbstractActionService {

        UiActionService( final IWorkbenchPage page ) {
            super(getDefault(), page);
        }

        /**
         * @see com.metamatrix.ui.actions.AbstractActionService#getDefaultAction(java.lang.String)
         */
        public IAction getDefaultAction( final String theActionId ) {
            return null;
        }
    }
}
