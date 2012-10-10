/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.ui.common.actions.AbstractActionService;
import org.teiid.designer.ui.common.actions.ActionService;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.graphics.GlobalUiFontManager;


/**
 * The main plugin class to be used in the desktop.
 * 
 * @since 8.0
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
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService( final IWorkbenchPage page ) {
        return new UiActionService(page);
    }

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
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
            @Override
			public void run() {
                registerPluginImage(UI_IDE, Images.CHECKED_CHECKBOX);
                registerPluginImage(UI_IDE, Images.UNCHECKED_CHECKBOX);
                registerPluginImage(UI_IDE, Images.REFRESH);
                registerPluginImage(UI, Images.TASK_ERROR);
                registerPluginImage(UI, Images.TASK_WARNING);
                registerPluginImage(UI, Images.TASK_INFO);
                // Register commonly-used images. Note that unlike the checkbox images above, these are created using the native UI's
                // checkbox image
                registerPluginImage(UiConstants.PLUGIN_ID, UiConstants.Images.CHECKED_CHECKBOX);
                registerPluginImage(UiConstants.PLUGIN_ID, UiConstants.Images.UNCHECKED_CHECKBOX);
            }
        };
        if (Display.getCurrent() == null) {
            Display.getDefault().asyncExec(op);
        } else {
            op.run();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#stop(org.osgi.framework.BundleContext)
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
         * @see org.teiid.designer.ui.common.actions.AbstractActionService#getDefaultAction(java.lang.String)
         */
        @Override
		public IAction getDefaultAction( final String theActionId ) {
            return null;
        }
    }
}
