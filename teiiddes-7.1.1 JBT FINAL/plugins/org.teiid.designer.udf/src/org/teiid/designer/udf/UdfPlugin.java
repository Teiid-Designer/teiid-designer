/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf;

import java.util.ResourceBundle;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * 
 */
public final class UdfPlugin extends Plugin {

    public static final String PLUGIN_ID = UdfPlugin.class.getPackage().getName();

    private static final String I18N_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

    static final PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static UdfPlugin plugin;

    /**
     * @param msgKey the properties file key
     * @param params the optional message data parameters
     * @return the error status object with the localized message
     * @since 6.0.0
     */
    public static IStatus createErrorStatus( String msgKey,
                                             String... params ) {
        String msg = (params == null) ? UTIL.getString(msgKey) : UTIL.getString(msgKey, (Object[])params);
        return new Status(IStatus.ERROR, PLUGIN_ID, msg);
    }

    public static UdfPlugin getInstance() {
        return UdfPlugin.plugin;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
        // Initialize the use of user-defined functions
        UdfManager.INSTANCE.initialize();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        super.stop(context);
        UdfManager.INSTANCE.shutdown();
    }
}
