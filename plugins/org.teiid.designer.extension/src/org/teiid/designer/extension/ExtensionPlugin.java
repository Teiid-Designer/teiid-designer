/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.LoggingUtil;

public class ExtensionPlugin extends Plugin {

    /**
     * The shared instance.
     */
    private static ExtensionPlugin plugin;

    /**
     * The plugin identifier.
     */
    public static final String PLUGIN_ID = "org.teiid.designer.extension"; //$NON-NLS-1$

    public static PluginUtil Util = new LoggingUtil(PLUGIN_ID);

    public static ExtensionPlugin getInstance() {
        return plugin;
    }

    private ModelExtensionAssistantAggregator assistantAggregator;

    private ModelExtensionRegistry registry;

    public ModelExtensionAssistantAggregator getModelExtensionAssistantAggregator() {
        return this.assistantAggregator;
    }

    public ModelExtensionRegistry getRegistry() {
        return this.registry;
    }

    /**
     * @param modelObject the model object being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model object is related to a model extension definition
     * @throws Exception if there is a problem accessing the model object
     */
    public boolean isModelExtensionDefinitionRelated( Object modelObject ) {
        for (String namespacePrefix : this.registry.getAllNamespacePrefixes()) {
            ModelExtensionAssistant assistant = this.registry.getModelExtensionAssistant(namespacePrefix);

            try {
                if (assistant.isModelExtensionDefinitionRelated(modelObject)) {
                    return true;
                }
            } catch (Exception e) {
                Util.log(e);
            }
        }

        return false;
    }

    /**
     * Loads the extension point contributors which indicate the valid extensible metamodel URIs.
     * 
     * @return the set of valid extensible URIs (never <code>null</code>)
     */
    private Set<String> loadExtensibleMetamodelUris() {
        final String EXT_PT = PLUGIN_ID + ".extensibleMetamodelProvider"; //$NON-NLS-1$
        final String METAMODEL_URI_ELEMENT = "definition"; //$NON-NLS-1$
        final String METAMODEL_URI_ATTR = "metamodelUri"; //$NON-NLS-1$

        Set<String> metamodelUris = new HashSet<String>();
        IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXT_PT);

        for (IConfigurationElement configElement : configElements) {
            String sElementName = configElement.getName();

            if (METAMODEL_URI_ELEMENT.equals(sElementName)) {
                String metamodelUri = configElement.getAttribute(METAMODEL_URI_ATTR);

                if ((metamodelUri != null) && (metamodelUri.trim().length() != 0)) {
                    metamodelUris.add(metamodelUri);
                }
            }
        }

        return metamodelUris;
    }

    private void loadRegistry() {
        final String EXT_PT = PLUGIN_ID + ".modelExtensionProvider"; //$NON-NLS-1$
        final String PATH = "path"; //$NON-NLS-1$
        final String CLASS_NAME = "className"; //$NON-NLS-1$

        try {
            IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXT_PT);

            for (IConfigurationElement configElement : configElements) {
                final String pluginId = configElement.getNamespaceIdentifier();

                try {
                    // loop over model definitions
                    String tempPath = configElement.getAttribute(PATH);

                    // make sure path attribute exists
                    if (CoreStringUtil.isEmpty(tempPath)) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.missingDefinitionPath, pluginId, PATH));
                        continue;
                    }

                    // make sure path represents a file in workspace and on the filesystem
                    Bundle bundle = Platform.getBundle(pluginId);
                    final IPath path = new Path(tempPath);
                    URL url = FileLocator.find(bundle, path, null);

                    if (url == null) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.definitionFileNotFoundInWorkspace, path, pluginId));
                        continue;
                    }

                    final File defnFile = new File(FileLocator.toFileURL(url).getFile());

                    if (!defnFile.isFile() || !defnFile.exists()) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.definitionFileNotFoundInFilesystem, path, pluginId));
                        continue;
                    }

                    // must have a model extension assistant
                    Object tempAssistant = null;

                    try {
                        tempAssistant = configElement.createExecutableExtension(CLASS_NAME);
                    } catch (Exception e) {
                        // attribute CLASS_NAME could be missing or a no-arg constructor in was not found
                        Util.log(IStatus.ERROR,
                                 NLS.bind(Messages.problemConstructingModelExtensionAssistantClass,
                                          ModelExtensionAssistant.class.getSimpleName(), pluginId));
                        continue;
                    }

                    final Object assistant = tempAssistant;

                    if ((assistant != null) && !(assistant instanceof ModelExtensionAssistant)) {
                        Util.log(IStatus.ERROR,
                                 NLS.bind(Messages.incorrectModelExtensionAssistantClass, assistant.getClass().getName(), pluginId));
                        continue;
                    }

                    ISafeRunnable runnable = new ISafeRunnable() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see org.eclipse.core.runtime.ISafeRunnable#handleException(java.lang.Throwable)
                         */
                        @Override
                        public void handleException( Throwable e ) {
                            Util.log(IStatus.ERROR, e, NLS.bind(Messages.errorProcessingDefinitionFile, path, pluginId));
                        }

                        /**
                         * {@inheritDoc}
                         * 
                         * @see org.eclipse.core.runtime.ISafeRunnable#run()
                         */
                        @Override
                        public void run() throws Exception {
                            ModelExtensionDefinition definition = getRegistry().addDefinition(new FileInputStream(defnFile),
                                                                                              (ModelExtensionAssistant)assistant);
                            definition.markAsBuiltIn();
                        }
                    };

                    SafeRunner.run(runnable);
                } catch (Exception e) {
                    Util.log(IStatus.ERROR, e, NLS.bind(Messages.errorProcessingModelExtension, pluginId));
                }
            }
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, Messages.errorProcessingExtensionPoint);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;

        // initialize logger first so that other methods can use logger
        ((LoggingUtil)Util).initializePlatformLogger(this);

        try {
            this.registry = new ModelExtensionRegistry();
            this.registry.setMetamodelUris(loadExtensibleMetamodelUris());
            this.assistantAggregator = new ModelExtensionAssistantAggregator(this.registry);

            // load model extension registry
            loadRegistry();
        } catch (Exception e) {
            Util.log(e);
            throw e;
        }
    }

}
