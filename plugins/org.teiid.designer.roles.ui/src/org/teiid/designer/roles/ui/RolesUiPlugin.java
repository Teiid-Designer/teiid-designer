/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles.ui;

import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.ActionService;


/**
 * 
 *
 * @since 8.0
 */
public final class RolesUiPlugin extends AbstractUiPlugin {

    public static final String PLUGIN_ID = RolesUiPlugin.class.getPackage().getName();

    private static final String I18N_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

    static final PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static RolesUiPlugin plugin;

    static RolesUiPlugin getInstance() {
        return plugin;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return UTIL;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
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
}
