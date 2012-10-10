/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.metamodels.relational.RelationalPackage;

/**
 * @since 8.0
 */
public class JdbcRelationalPlugin extends Plugin implements ModelerJdbcRelationalConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(JdbcRelationalPlugin.class);

    public static boolean DEBUG = false;

    /** Control defining whether relational models are created within a single transaction. */
    public static boolean CREATE_MODEL_IN_TRANSACTION = true;

    private static final JdbcNodeToRelationalMapping JDBC_TO_RELATIONAL_MAPPING = new JdbcNodeToRelationalMappingImpl();

    private static final String MODEL_CONTAINER_NAME = getString("modelContainerName"); //$NON-NLS-1$

    // The shared instance.
    private static JdbcRelationalPlugin plugin;

    private static Container modelCtnr;

    /**
     * Returns the shared instance.
     * 
     * @since 4.0
     */
    public static JdbcRelationalPlugin getDefault() {
        return JdbcRelationalPlugin.plugin;
    }

    /**
     * @since 4.0
     */
    public static Container getModelContainer() throws CoreException {
        if (JdbcRelationalPlugin.modelCtnr == null) {
            JdbcRelationalPlugin.modelCtnr = ModelerCore.createContainer(MODEL_CONTAINER_NAME);
        }
        return JdbcRelationalPlugin.modelCtnr;
    }

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Util.getString(I18N_PREFIX + id);
    }

    /**
     * Return the mapping between the {@link JdbcNode} types and the \ {@link RelationalPackage Relational} metaclasses.
     * 
     * @return the mapping; never null
     */
    public static JdbcNodeToRelationalMapping getJdbcNodeToRelationalMapping() {
        return JDBC_TO_RELATIONAL_MAPPING;
    }

    /**
     * @since 4.0
     */
    public JdbcRelationalPlugin() {
        JdbcRelationalPlugin.plugin = this;
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        ((PluginUtilImpl)org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
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
}
