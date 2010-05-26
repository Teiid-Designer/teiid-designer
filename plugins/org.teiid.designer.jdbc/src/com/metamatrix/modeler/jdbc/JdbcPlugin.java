/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.internal.jdbc.JdbcManagerImpl;
import com.metamatrix.modeler.jdbc.metadata.Includes;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor;
import com.metamatrix.modeler.jdbc.metadata.impl.ImportSettingsSelectionVisitor;
import com.metamatrix.modeler.jdbc.metadata.impl.JdbcDatabaseImpl;
import com.metamatrix.modeler.jdbc.metadata.impl.JdbcNodeSelections;

public class JdbcPlugin extends Plugin {

    /**
     * The plug-in identifier of this plugin (value <code>"com.metamatrix.modeler.jdbc"</code>).
     */
    public static final String PLUGIN_ID = "org.teiid.designer.jdbc"; //$NON-NLS-1$

    public static final String PACKAGE_ID = JdbcPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator() {
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
            } catch (final IOException exception) {
                throw new WrappedException(exception);
            }
        }

        public Object getImage( final String key ) {
            try {
                final URL baseUrl = getBaseURL();
                final URL url = new URL(baseUrl + "icons/" + key + ".gif"); //$NON-NLS-1$//$NON-NLS-2$
                final InputStream inputStream = url.openStream();
                inputStream.close();
                return url;
            } catch (final MalformedURLException exception) {
                throw new WrappedException(exception);
            } catch (final IOException exception) {
                throw new MissingResourceException(
                                                   CommonPlugin.INSTANCE.getString("_UI_StringResourceNotFound_exception", new Object[] {key}), //$NON-NLS-1$
                                                   getClass().getName(), key);
            }
        }

        public String getString( final String key ) {
            return Util.getString(key);
        }

        public String getString( final String key,
                                 final boolean translate ) {
            return getString(key);
        }

        public String getString( final String key,
                                 final Object[] substitutions ) {
            return Util.getString(key, substitutions);
        }

        public String getString( final String key,
                                 final Object[] substitutions,
                                 final boolean translate ) {
            return getString(key, substitutions);
        }
    };

    static JdbcPlugin INSTANCE = null;

    public static boolean DEBUG = false;

    /**
     * Starts the manager for the {@link JdbcDriver} instances by loading the instances from the supplied model. This method is safe
     * to call more than once; the method returns whether the manager was actually started.
     * 
     * @param jdbcModelUri the full path to the model file containing the {@link JdbcDriver} instances.
     * @return true if the manager was started, or false if the manager was already started
     * @throws JdbcException if there is an error loading the manager
     * @see #getJdbcDriverManager()
     */
    public static JdbcManager createJdbcManager( final String name ) {
        final JdbcManagerImpl mgr = new JdbcManagerImpl(name);
        mgr.start();
        return mgr;
    }

    /**
     * Method that obtains an existing {@link JdbcImportSettings} object from the supplied {@link JdbcSource source}, or creates a
     * new {@link JdbcImportSettings} object if needed and sets the reference from the {@link JdbcSource source} to the
     * {@link JdbcImportSettings settings}.
     * 
     * @param source the JdbcSource that identifies the JDBC system and in which the node selections are to be recorded; may not be
     *        null
     * @return the {@link JdbcImportSettings} object on the source; never null
     */
    public static JdbcImportSettings ensureNonNullImportSettings( final JdbcSource source ) {
        CoreArgCheck.isNotNull(source);

        // Get (or create) the import settings on the source
        JdbcImportSettings settings = source.getImportSettings();
        if (settings == null) {
            settings = JdbcFactory.eINSTANCE.createJdbcImportSettings();
            source.setImportSettings(settings);
        }
        return settings;
    }

    /**
     * Return the {@link JdbcDatabase} object that provides access to metadata and other information about the JDBC system described
     * by the supplied {@link JdbcSource source} and {@link Connection connection}. Each time this method is called, a new
     * {@link JdbcDatabase} object will be created, so care should be taken not to make duplicates as this is not efficient.
     * 
     * @param source the JdbcSource that identifies the JDBC system; may not be null
     * @param connection the JDBC Connection to the JDBC system; may not be null
     * @return the new JdbcDatabase object
     */
    public static JdbcDatabase getJdbcDatabase( final JdbcSource source,
                                                final Connection connection ) {
        CoreArgCheck.isNotNull(source);
        CoreArgCheck.isNotNull(connection);
        final JdbcNodeSelections selectionsCache = new JdbcNodeSelections();

        // See if there are import settings ...
        final JdbcImportSettings settings = source.getImportSettings();
        if (settings != null) {
            // Populate the selections cache with the what is in the settings
            selectionsCache.initialize(settings);
        }

        // Determine whether there are any selections defined in the settings ...
        final boolean existingSelections = selectionsCache.hasSelectionModes();

        // Then create the JdbcDatabaseImpl (which may modify the selections)
        final JdbcDatabaseImpl result = new JdbcDatabaseImpl(connection, source.getName(), selectionsCache);
        if (!existingSelections) {
            // There are no existing selections, so make the "default" selections
            final IStatus status = result.selectDefaultNodes(source.getUsername());
            if (status.getSeverity() == IStatus.ERROR) {
                Util.log(status);
            }
        }

        // Grab the "include" options off of the JdbcImportSettings ...
        if (settings != null) {
            final Includes includes = result.getIncludes();
            final List includedTableTypes = settings.getIncludedTableTypes();
            final String[] typesToInclude = (String[])includedTableTypes.toArray(new String[includedTableTypes.size()]);
            includes.setIncludedTableTypes(typesToInclude);
            includes.setApproximateIndexes(settings.isIncludeApproximateIndexes());
            includes.setIncludeForeignKeys(settings.isIncludeForeignKeys());
            includes.setIncludeIndexes(settings.isIncludeIndexes());
            includes.setIncludeProcedures(settings.isIncludeProcedures());
            includes.setUniqueIndexesOnly(settings.isIncludeUniqueIndexes());
        }
        return result;
    }

    /**
     * Called by the {@link com.metamatrix.metamodels.transformation.provider.TransformationEditPlugin}
     * 
     * @return the EMF ResourceLocator used when run as a plugin
     */
    public static ResourceLocator getPluginResourceLocator() {
        return RESOURCE_LOCATOR;
    }

    /**
     * Method that records in the {@link JdbcSource source descriptor} the selected {@link JdbcNode nodes} in the supplied
     * {@link JdbcDatabase database tree}. The selections are actually recorded in the {@link JdbcSource#getImportSettings() 
     * source's import settings}. Consequently, if the import settings
     * 
     * @param source the JdbcSource that identifies the JDBC system and in which the node selections are to be recorded; may not be
     *        null
     * @param database the JDBC database object tree whose selections are to be recorded; may not be null
     * @throws JdbcException if there is an error visiting the database
     */
    public static void recordJdbcDatabaseSelections( final JdbcSource source,
                                                     final JdbcDatabase database ) throws JdbcException {
        CoreArgCheck.isNotNull(source);
        CoreArgCheck.isNotNull(database);

        // Get (or create) the import settings on the source
        final JdbcImportSettings settings = ensureNonNullImportSettings(source);

        // Use a visitor to navigate the database tree and record those selected nodes
        final JdbcNodeVisitor visitor = new ImportSettingsSelectionVisitor(settings);
        database.accept(visitor, JdbcNode.DEPTH_INFINITE);
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        INSTANCE = this;
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
    }

}
