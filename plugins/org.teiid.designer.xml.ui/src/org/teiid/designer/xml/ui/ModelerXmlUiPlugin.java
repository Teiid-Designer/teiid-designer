/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.ui;

import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.PluginUtil;
import org.teiid.core.CoreConstants.Debug;
import org.teiid.core.util.PluginUtilImpl;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.ActionService;
import org.teiid.designer.xml.ui.actions.ModelerXmlActionService;


/**
 * ModelerXmlUiPlugin is the plugin class for MetaBase Modeler's xml document technology.
 *
 * @since 8.0
 */
public class ModelerXmlUiPlugin extends AbstractUiPlugin implements Debug, ModelerXmlUiConstants {

    // The shared instance.
    private static ModelerXmlUiPlugin plugin;

    /**
     * Returns the shared instance.
     * 
     * @since 4.0
     */
    public static ModelerXmlUiPlugin getDefault() {
        return ModelerXmlUiPlugin.plugin;
    }

    /**
     * Construct an instance of XmlUiPlugin.
     */
    public ModelerXmlUiPlugin() {
        ModelerXmlUiPlugin.plugin = this;
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);

        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

    }

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return new ModelerXmlActionService(page);
    }

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }

}
