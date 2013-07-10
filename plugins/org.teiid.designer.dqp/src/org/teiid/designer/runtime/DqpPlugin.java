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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.event.IChangeNotifier;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.ExtensionRegistryUtils;
import org.teiid.designer.IExtensionRegistryCallback;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.core.workspace.ModelWorkspaceNotification;
import org.teiid.designer.core.workspace.ModelWorkspaceNotificationAdapter;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.runtime.connection.spi.IPasswordProvider;
import org.teiid.designer.runtime.extension.rest.RestModelExtensionAssistant;
import org.teiid.designer.runtime.extension.rest.RestModelExtensionConstants;
import org.teiid.designer.runtime.preview.PreviewManager;
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
     * The Teiid server registry.
     */
    private ITeiidServerManager serverMgr;

    /**
     * Provider of the {@link IServer}s collection
     */
    private IServersProvider serversProvider;
    
    /**
     * The password provider to be used in the server manager's preview manager
     */
    private IPasswordProvider passwordProvider;

    /**
     * Obtains the current plugin preferences values.
     *
     * @return the preferences (never <code>null</code>)
     */
    public IEclipsePreferences getPreferences() {
        return this.getPreferences(PLUGIN_ID);
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
        if (serverMgr == null) {
            
            /*
             * Server manager init requires restoring its state dependent on
             * the server provider which may not have been inited. To ensure
             * that latter is inited avoid initing prior to the workbench initialization 
             */
            if (PlatformUI.getWorkbench().isStarting()) {
                try {
                    throw new Exception("Programming Error: Server Manager should not be instantiated prior to the workbench"); //$NON-NLS-1$
                } catch (Exception ex) {
                    Util.log(ex);
                }
            }
            
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

        if (passwordProvider == null) {
            IExtensionRegistryCallback<IPasswordProvider> callback = new IExtensionRegistryCallback<IPasswordProvider>() {

                @Override
                public String getExtensionPointId() {
                    return IPasswordProvider.PASSWORD_PROVIDER_EXTENSION_POINT_ID;
                }

                @Override
                public String getElementId() {
                    return IPasswordProvider.PASSWORD_PROVIDER_ELEMENT_ID;
                }

                @Override
                public String getAttributeId() {
                    return CLASS_ATTRIBUTE_ID;
                }

                @Override
                public boolean isSingle() {
                    return true;
                }

                @Override
                public void process(IPasswordProvider instance, IConfigurationElement element) {
                    if (passwordProvider != null) {
                        /*
                         * Programming error since the password provider extension should only be
                         * implemented once.
                         */
                        throw new IllegalStateException();
                    }

                    passwordProvider = instance;
                }
            };

            try {
                ExtensionRegistryUtils.createExtensionInstances(callback);
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }

        return passwordProvider;
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

    private void initializeServerRegistry() {
        this.serverMgr = ModelerCore.getTeiidServerManager();
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
        
        // Initialize teiid parent server state listener
        getServersProvider().addServerStateListener(TeiidParentServerListener.getInstance());
        getServersProvider().addServerLifecycleListener(TeiidParentServerListener.getInstance());
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
        getServersProvider().removeServerStateListener(TeiidParentServerListener.getInstance());
        getServersProvider().removeServerLifecycleListener(TeiidParentServerListener.getInstance());
        
        try {
            if (serverMgr != null) {
                this.serverMgr.shutdown(null);
            }

            // shutdown PreviewManager
            PreviewManager.getInstance().shutdown(null);
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

}
