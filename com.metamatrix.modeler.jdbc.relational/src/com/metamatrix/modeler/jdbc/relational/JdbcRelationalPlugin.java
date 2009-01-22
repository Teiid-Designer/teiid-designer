/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.metamodels.relational.util.RelationalTypeMappingImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.internal.jdbc.relational.JdbcNodeToRelationalMappingImpl;
import com.metamatrix.modeler.internal.jdbc.relational.ModelerJdbcRelationalConstants;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * JdbcRelationalPlugin
 * 
 * @since 4.0
 */
public class JdbcRelationalPlugin extends Plugin implements ModelerJdbcRelationalConstants {
    // ============================================================================================================================
    // Constants

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcRelationalPlugin.class);

    public static boolean DEBUG = false;

    /** Control defining whether relational models are created within a single transction. */
    public static boolean CREATE_MODEL_IN_TRANSACTION = true;

    private static final JdbcNodeToRelationalMapping JDBC_TO_RELATIONAL_MAPPING = new JdbcNodeToRelationalMappingImpl();

    private static final String MODEL_CONTAINER_NAME = getString("modelContainerName"); //$NON-NLS-1$

    /**
     * Delimiter used by extension/extension point declarations
     */
    public static final String DELIMITER = "."; //$NON-NLS-1$

    /**
     * The identifiers for all ModelerCore extension points
     */
    public static class EXTENSION_POINT {
        /** Extension point for the model validation service implementation */
        public static class MODEL_PROCESSOR {
            public static final String ID = "modelProcessor"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String DRIVER_CLASS_NAME = "driverClass"; //$NON-NLS-1$
                public static final String PROCESSOR_CLASS = "processorClass"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }
    }

    // ============================================================================================================================
    // Static Variables

    // The shared instance.
    private static JdbcRelationalPlugin plugin;

    private static Container modelCtnr;

    // ============================================================================================================================
    // Static Methods

    /**
     * Returns the shared instance.
     * 
     * @since 4.0
     */
    public static JdbcRelationalPlugin getDefault() {
        return JdbcRelationalPlugin.plugin;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public static Container getModelContainer() throws CoreException {
        if (JdbcRelationalPlugin.modelCtnr == null) {
            JdbcRelationalPlugin.modelCtnr = ModelerCore.createContainer(MODEL_CONTAINER_NAME);
        }
        return JdbcRelationalPlugin.modelCtnr;
    }

    // ============================================================================================================================
    // Static Utility Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    // ============================================================================================================================
    // Constructors

    /**
     * Construct an instance of MetaMatrixPlugin.
     * 
     * @since 4.0
     */
    public JdbcRelationalPlugin() {
        JdbcRelationalPlugin.plugin = this;
    }

    // ============================================================================================================================
    // Overridden Methods

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        if (JdbcRelationalPlugin.modelCtnr != null) {
            JdbcRelationalPlugin.modelCtnr.shutdown();
        }
        super.stop(context);
    }

    // ============================================================================================================================
    // Utility Methods

    /**
     * Create a new {@link RelationalModelProcessor Relational model processor} that can transform
     * {@link com.metamatrix.modeler.jdbc.metadata.JdbcDatabase JDBC metadata} into a
     * {@link com.metamatrix.metamodels.relational.RelationalPackage Relational} model.
     * <p>
     * This method attempts to find the model processor that is best suited for the supplied source. It does so by searching for
     * the first <code>com.metamatrix.modeler.jdbc.relational.modelProcessor</code> extension that is defined to work with the
     * JdbcSource's {@link JdbcSource#getDriverClass()}
     * </p>
     * 
     * @param source the JdbcSource; may be null if the default processor should be used
     * @return the new model processor
     */
    public static RelationalModelProcessor createRelationalModelProcessor( final JdbcSource source ) {
        return createRelationalModelProcessor(source, RelationalTypeMappingImpl.getInstance());
    }

    /**
     * Create a new {@link RelationalModelProcessor Relational model processor} that can transform
     * {@link com.metamatrix.modeler.jdbc.metadata.JdbcDatabase JDBC metadata} into a
     * {@link com.metamatrix.metamodels.relational.RelationalPackage Relational} model.
     * <p>
     * This method attempts to find the model processor that is best suited for the supplied source. It does so by searching for
     * the first <code>com.metamatrix.modeler.jdbc.relational.modelProcessor</code> extension that is defined to work with the
     * JdbcSource's {@link JdbcSource#getDriverClass()}
     * </p>
     * 
     * @param source the JdbcSource; may be null if the default processor should be used
     * @param mapping the RelationalTypeMapping that should be used
     * @return the new model processor
     */
    public static RelationalModelProcessor createRelationalModelProcessor( final JdbcSource source,
                                                                           final RelationalTypeMapping mapping ) {
        RelationalModelProcessor processor = null;

        if (source != null) {
            final String driverClassName = source.getDriverClass();

            // Look for extensions ...
            if (driverClassName != null) {
                final IExtension[] extensions = PluginUtilities.getExtensions(JdbcRelationalPlugin.EXTENSION_POINT.MODEL_PROCESSOR.UNIQUE_ID);
                for (int i = 0; i < extensions.length; i++) {
                    final IExtension extension = extensions[i];
                    final IConfigurationElement[] elems = extension.getConfigurationElements();
                    boolean driverMatches = false;
                    Object instance = null;
                    for (int j = 0; j < elems.length; j++) {
                        final IConfigurationElement elem = elems[j];
                        final String elemName = elem.getName();
                        if (elemName == null) {
                            continue;
                        }
                        if (elemName.equals(JdbcRelationalPlugin.EXTENSION_POINT.MODEL_PROCESSOR.ELEMENTS.DRIVER_CLASS_NAME)) {
                            final String className = elem.getAttribute(ModelerCore.EXTENSION_POINT.ASSOCIATION_PROVIDER.ATTRIBUTES.NAME);
                            if (driverClassName.equals(className)) {
                                driverMatches = true;
                            }
                        }
                        if (elemName.equals(JdbcRelationalPlugin.EXTENSION_POINT.MODEL_PROCESSOR.ELEMENTS.PROCESSOR_CLASS)) {
                            final String attribName = JdbcRelationalPlugin.EXTENSION_POINT.MODEL_PROCESSOR.ATTRIBUTES.NAME;
                            try {
                                instance = elem.createExecutableExtension(attribName);
                            } catch (Throwable e) {
                                Util.log(e);
                            }
                        }
                    }
                    // Create the instance if a matching extension was found ...
                    if (driverMatches && instance != null && instance instanceof RelationalModelProcessor) {
                        processor = (RelationalModelProcessor)instance;
                        break;
                    }
                }
            }

        }

        // See if one was found ...
        if (processor == null) {
            // Create the default processor ...
            processor = new RelationalModelProcessorImpl();
        }

        // Attempt to set the type mapping
        if (mapping != null && processor instanceof RelationalModelProcessorImpl) {
            ((RelationalModelProcessorImpl)processor).setTypeMapping(mapping);
        }
        return processor;
    }

    /**
     * Return the mapping between the {@link JdbcNode} types and the \ {@link RelationalPackage Relational} metaclasses.
     * 
     * @return the mapping; never null
     */
    public static JdbcNodeToRelationalMapping getJdbcNodeToRelationalMapping() {
        return JDBC_TO_RELATIONAL_MAPPING;
    }
}
