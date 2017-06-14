/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.core.workspace.ModelWorkspaceNotification;
import org.teiid.designer.core.workspace.ModelWorkspaceNotificationAdapter;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.metamodels.function.extension.FunctionModelExtensionAssistant;
import org.teiid.designer.metamodels.function.extension.FunctionModelExtensionConstants;


/**
 * @since 8.0
 */
public class FunctionPlugin extends Plugin {

    public static final String PLUGIN_ID = "org.teiid.designer.metamodels.function"; //$NON-NLS-1$

    public static final String PACKAGE_ID = FunctionPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator() {
        @Override
		public URL getBaseURL() {
            if (INSTANCE != null) {
                URL baseUrl;
                try {
                    baseUrl = FileLocator.resolve(INSTANCE.getBundle().getEntry("/")); //$NON-NLS-1$
                } catch (final IOException err) {
                    baseUrl = null;
                }
                return baseUrl;
            }
            try {
                final URI uri = URI.createURI(getClass().getResource("plugin.properties").toString()); //$NON-NLS-1$
                final URL baseUrl = new URL(uri.trimSegments(1).toString() + "/"); //$NON-NLS-1$
                return baseUrl;
            } catch (IOException exception) {
                throw new WrappedException(exception);
            }
        }

        @Override
		public Object getImage( String key ) {
            try {
                final URL baseUrl = getBaseURL();
                final URL url = new URL(baseUrl + "icons/" + key + ".gif"); //$NON-NLS-1$//$NON-NLS-2$
                InputStream inputStream = url.openStream();
                inputStream.close();
                return url;
            } catch (MalformedURLException exception) {
                throw new WrappedException(exception);
            } catch (IOException exception) {
                throw new MissingResourceException(
                                                   CommonPlugin.INSTANCE.getString("_UI_StringResourceNotFound_exception", new Object[] {key}), //$NON-NLS-1$
                                                   getClass().getName(), key);
            }
        }

        @Override
		public String getString( String key ) {
            return Util.getString(key);
        }

        @Override
		public String getString( String key,
                                 Object[] substitutions ) {
            return Util.getString(key, substitutions);
        }

        @Override
		public String getString( final String key,
                                 final boolean translate ) {
            return getString(key);
        }

        @Override
		public String getString( final String key,
                                 final Object[] substitutions,
                                 final boolean translate ) {
            return getString(key, substitutions);
        }
    };

    /**
     * 
     * @return the EMF ResourceLocator used when run as a plugin
     */
    public static ResourceLocator getPluginResourceLocator() {
        return RESOURCE_LOCATOR;
    }

    static FunctionPlugin INSTANCE = null;
    
    private ModelWorkspaceNotificationAdapter workspaceListener = null;

    /**
     * Construct an instance of MetaMatrixPlugin.
     */
    public FunctionPlugin() {
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        INSTANCE = this;
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
    
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
     *
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        // unregister workspace listener
        if (!ModelWorkspaceManager.isShutDown() && this.workspaceListener != null) {
            ModelWorkspaceManager.getModelWorkspaceManager().removeNotificationListener(this.workspaceListener);
        }

        super.stop(context);
    }
    
    /**
     * @param notification the notification being handled (never <code>null</code>)
     */
    void handleNewModelEvent(final ModelWorkspaceNotification notification) {
        if (notification.isPostChange()) {
            final IResource model = (IResource)notification.getNotifier();

            if (model != null) {
                final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
                final String prefix = FunctionModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
                final FunctionModelExtensionAssistant assistant = (FunctionModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);

                try {
                    assistant.applyMedIfNecessary(model);
                } catch (Exception e) {
                    Util.log(e);
                }
            }
        }
    }
    
    /**
     * 
     * @param modelObject the emf model object
     * @param propertyID the extension property ID
     * @return the string value of the property. may be null
     */
    public static String getExtensionProperty(EObject modelObject, String propertyID) {
    	String value = null;
    	
        if (modelObject != null) {
            final ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
            final String prefix = FunctionModelExtensionConstants.NAMESPACE_PROVIDER.getNamespacePrefix();
            final FunctionModelExtensionAssistant assistant = (FunctionModelExtensionAssistant)registry.getModelExtensionAssistant(prefix);

            try {
            	value = assistant.getPropertyValue(modelObject, propertyID);
            } catch (Exception e) {
                Util.log(e);
            }
        }
        
        return value;
    }
}
