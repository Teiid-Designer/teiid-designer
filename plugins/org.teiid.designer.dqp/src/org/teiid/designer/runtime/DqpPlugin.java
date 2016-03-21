/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import java.util.ResourceBundle;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.management.core.IAS7ManagementDetails;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.core.workspace.ModelWorkspaceNotification;
import org.teiid.designer.core.workspace.ModelWorkspaceNotificationAdapter;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionAssistant;
import org.teiid.designer.metamodels.relational.extension.RestModelExtensionConstants;
import org.teiid.designer.runtime.spi.ITeiidServerManager;


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

    /**
     * The source bindings file name
     */
    public static final String SOURCE_BINDINGS_FILE_NAME = "SourceBindings.xml"; //$NON-NLS-1$
    
    private static IPath runtimePath;

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.2.1
     */
    public static PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));


    private ModelWorkspaceNotificationAdapter workspaceListener = null;
    
    
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
     * Provider of the {@link IServer}s collection
     */
    private IServersProvider serversProvider;

    /**
     * Obtains the current plugin preferences values.
     *
     * @return the preferences (never <code>null</code>)
     */
    public IEclipsePreferences getPreferences() {
        return this.getPreferences(PLUGIN_ID);
    }

    /**
     * @return the jboss execution request timeout property from the preferences
     * Note: the preference is saved in seconds while this will convert it into ms since
     *           that is what the {@link IAS7ManagementDetails#PROPERTY_TIMEOUT} requires
     */
    public int getJbossRequestTimeout() {
        IEclipsePreferences preferences = DqpPlugin.getInstance().getPreferences();
        int timeout = preferences.getInt(PreferenceConstants.JBOSS_REQUEST_EXECUTION_TIMEOUT,
                                         PreferenceConstants.JBOSS_REQUEST_EXECUTION_TIMEOUT_SEC_DEFAULT);
        if (timeout < 100) {
            // timeout preference is in seconds and the jboss property is in ms
            timeout = timeout * 1000;
        }
        return timeout;
    }

    /**
     * Obtains the current plugin preferences values for the given plugin id
     *
     * @param pluginId
     *
     * @return the preferences (never <code>null</code>) for the given plugin id
     */
    public IEclipsePreferences getPreferences(String pluginId) {
        return InstanceScope.INSTANCE.getNode(pluginId);
    }

    /**
     * @return the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime path
     *
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
    public ITeiidServerManager getServerManager() {
        return ModelerCore.getTeiidServerManager();
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
        IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(DqpPlugin.getInstance().getBundle().getSymbolicName());

        // initialize the Teiid cleanup enabled preference
        prefs.putBoolean(PreferenceConstants.PREVIEW_ENABLED, PreferenceConstants.PREVIEW_ENABLED_DEFAULT);

        // initialize the Teiid cleanup enabled preference
        prefs.putBoolean(PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED,
                         PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT);

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
        
        // register to receive workspace model events
        this.workspaceListener = new ModelWorkspaceNotificationAdapter() {

            /**
             * {@inheritDoc}
             *
             * @see org.teiid.designer.core.workspace.ModelWorkspaceNotificationAdapter#notifyReloaded(org.teiid.designer.core.workspace.ModelWorkspaceNotification)
             */
            @Override
            public void notifyReloaded(ModelWorkspaceNotification notification) {
                handleNewModelEvent(notification);
            }

            /**
             * {@inheritDoc}
             *
             * @see org.teiid.designer.core.workspace.ModelWorkspaceNotificationAdapter#notifyAdd(org.teiid.designer.core.workspace.ModelWorkspaceNotification)
             */
            @Override
            public void notifyAdd(ModelWorkspaceNotification notification) {
                handleNewModelEvent(notification);
            }
        };
        ModelWorkspaceManager.getModelWorkspaceManager().addNotificationListener(this.workspaceListener);
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
        if (getServersProvider().isInitialised()) {
            // If server is no longer initialised then no need to remove the listeners
            getServersProvider().removeServerStateListener(TeiidParentServerListener.getInstance());
            getServersProvider().removeServerLifecycleListener(TeiidParentServerListener.getInstance());
        }
        
        try {
            if (getServerManager() != null) {
                getServerManager().shutdown(null);
            }
        } finally {
            super.stop(context);
        }
    }
    
    /**
     * @param notification the notification being handled (never <code>null</code>)
     */
    void handleNewModelEvent(final ModelWorkspaceNotification notification) {
        if (notification.isPostChange()) {
            final IResource model = (IResource)notification.getNotifier();

            if (model != null) {
                final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
                final String prefix = RestModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
                final RestModelExtensionAssistant assistant = (RestModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);

                try {
                    assistant.applyMedIfNecessary(model);
                } catch (Exception e) {
                    Util.log(e);
                }
            }
        }
    }

    private static Display getDisplay() {
        return (Display.getCurrent() == null ? Display.getDefault() : Display.getCurrent());
    }

    /**
     * @param operation The operation to be executed in the SWT thread.
     * @param asynchronous True if the operation should be run asynchronously, meaning the calling thread will not be blocked.
     */
    private static void runInSwtThread( final Runnable operation, final boolean asynchronous ) {
        Display display = getDisplay();
        if (Thread.currentThread() != display.getThread()) {
            if (asynchronous) {
                display.asyncExec(operation);
            } else {
                display.syncExec(operation);
            }
        } else {
            operation.run();
        }
    }

    /**
     * Convey the given exception to the user.
     *
     * @param ex
     */
    public static void handleException(final Exception ex) {
        /**
         * TODO
         * Not ideal as we are calling a UI dialog in a technically non-UI plugin.
         * However, it already depends on org.eclipse.ui so free to use this stuff
         * and displaying an error dialog is crucial to the user.
         */

        Util.log(ex);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                String reason = Util.getString("TeiidParentServerListener.initTeiidServerException.reason"); //$NON-NLS-1$

                MultiStatus multiStatus = new MultiStatus(PLUGIN_ID, 0, reason, null);
                Throwable throwable = ex;
                do {
                    Status status = new Status(IStatus.ERROR, PLUGIN_ID, " * " + throwable.getLocalizedMessage()); //$NON-NLS-1$
                    multiStatus.add(status);
                    throwable = throwable.getCause();
                } while (throwable != null);

                StatusManager.getManager().handle(multiStatus, StatusManager.SHOW);
            }
        };

        runInSwtThread(runnable, true);
    }
}
