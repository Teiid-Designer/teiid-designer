/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import java.io.IOException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.BundleContext;
import org.teiid.core.PluginUtil;
import org.teiid.core.event.IChangeNotifier;
import org.teiid.core.util.PluginUtilImpl;
import org.teiid.designer.runtime.connection.IPasswordProvider;


/**
 * The main plugin class to be used in the desktop.
 *
 * @since 8.0
 */
public class DqpPlugin extends Plugin {

    /**
     * This plugin's identifier.
     */
    public static final String PLUGIN_ID = "org.teiid.designer.dqp"; //$NON-NLS-1$

    /**
     * The package identifier.
     */
    public static final String PACKAGE_ID = DqpPlugin.class.getPackage().getName();

    /**
     * The name of the I18n properties file.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$

    public static final String SOURCE_BINDINGS_FILE_NAME = "SourceBindings.xml"; //$NON-NLS-1$
    
    private static IPath runtimePath;

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.2.1
     */
    public static PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    /**
     * The shared instance.
     */
    private static DqpPlugin plugin;

    /**
     * @return DqpPlugin
     * @since 4.3
     */
    public static DqpPlugin getInstance() {
        return plugin;
    }

    /**
     * The Teiid server registry.
     */
    private TeiidServerManager serverMgr;

    /**
     * Provider of the {@link IServer}s collection
     */
    private IServersProvider serversProvider;
    
    /**
     * The password provider to be used in the server manager's preview manager
     */
    private IPasswordProvider passwordProvider;

    /**
     * Obtains the current plubin preferences values. <strong>This method should be used instead of
     * {@link Plugin#getPluginPreferences()}.</strong>
     * 
     * @return the preferences (never <code>null</code>)
     */
    public IEclipsePreferences getPreferences() {
        return new InstanceScope().getNode(PLUGIN_ID);
    }
    
    /**
     * @return the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public IPath getRuntimePath() {
        if (runtimePath == null) {
            runtimePath = DqpPlugin.getInstance().getStateLocation();
        }

        return (IPath)runtimePath.clone();
    }

    /**
     * @return the server manager
     */
    public TeiidServerManager getServerManager() {
        
        if (serverMgr == null) {
            try {
                initializeServerRegistry();
            } catch (final Exception e) {
                throw new RuntimeException(e.getLocalizedMessage(), e);
            }
        }
        
        return this.serverMgr;
    }

    /**
     * @return <code>true</code> if the server manager has been started
     */
    public boolean isServerManagerStarted() {
        return (this.serverMgr != null);
    }

    /**
     * Get the password provider if one has been set
     * 
     * @return the password provider
     */
    public IPasswordProvider getPasswordProvider() {
        return passwordProvider;
    }
    
    /**
     * @param passwordProvider
     */
    public void setPasswordProvider(IPasswordProvider passwordProvider) {
        this.passwordProvider = passwordProvider;
    }
    
    /**
     * Get the provider of the collection of {@link IServer}s
     * 
     * @return servers provider
     */
    public IServersProvider getServersProvider() {
        if (serversProvider == null)
            serversProvider = new DefaultServersProvider();
        
        return serversProvider;
    }

    /**
     * Cleans up the map of context helpers.
     * 
     * @param theContext the context whose state has changed
     * @since 4.3
     */
    void handleContextChanged( final IChangeNotifier vdb ) {
        // TODO: re-implement
        // if (this.vdbHelperMap.get(vdb) != null) {
        // // only care if the context is now closed
        // if (!(vdb.isOpen()) {
        // this.vdbHelperMap.remove(vdb);
        // theContext.removeChangeListener(this.changeListener);
        // }
        // }
    }

    private void initializeDefaultPreferences() {
        IEclipsePreferences prefs = new DefaultScope().getNode(DqpPlugin.getInstance().getBundle().getSymbolicName());

        // initialize the Teiid cleanup enabled preference
        prefs.putBoolean(PreferenceConstants.PREVIEW_ENABLED, PreferenceConstants.PREVIEW_ENABLED_DEFAULT);

        // initialize the Teiid cleanup enabled preference
        prefs.putBoolean(PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED,
                         PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT);

    }
    
    

    private void initializeServerRegistry() throws CoreException {
        String restoreRegistryPath = getRuntimePath().toFile().getAbsolutePath();
        this.serverMgr = new TeiidServerManager(restoreRegistryPath, passwordProvider, getServersProvider());

        // restore registry
        final IStatus status = this.serverMgr.restoreState();

        if (!status.isOK()) {
            Util.log(status);
        }

        if (status.getSeverity() == IStatus.ERROR) {
            throw new CoreException(status);
        }
    }

    /**
     * Option names can be found in the <code>.debug</code> file and in {@link DebugConstants}.
     * 
     * @param option the option being checked
     * @return <code>true</code> if in debugging mode and the debug option is enabled
     */
    public boolean isDebugOptionEnabled( String option ) {
        return (isDebugging() && Boolean.toString(true).equals(Platform.getDebugOption(option)));
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;

        // initialize logger first so that other methods can use logger
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

        // initialize preferences
        initializeDefaultPreferences();
    }

    /**
     * {@inheritDoc}
     * <p>
     * It is recommended for the UI to call {@link TeiidServerManager#shutdown(org.eclipse.core.runtime.IProgressMonitor)} as there are
     * shutdown tasks that the UI should block on before shutting down.
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        try {
            if (serverMgr != null) {
                this.serverMgr.shutdown(null);
            }
        } finally {
            super.stop(context);
        }
    }

}
