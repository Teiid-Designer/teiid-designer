/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.teiid.designer.runtime.ServerManager;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;

/**
 * The main plugin class to be used in the desktop.
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

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.2.1
     */
    public static PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));
    
    /**
     * The name of the file containing the {{@link #workspaceUuid workspace UUID} for the current workspace: {@value}
     */
    public static final String WORKSPACE_UUID_FILE = "workspace.uuid"; //$NON-NLS-1$

    /**
     * The UUID created and persisted for the current workspace
     */
    public static UUID workspaceUuid;
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
    private ServerManager serverMgr;

    /**
     * @return the server manager
     */
    public ServerManager getServerManager() {
        return this.serverMgr;
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

    private void initializeServerRegistry() throws CoreException {
        this.serverMgr = new ServerManager(DqpPath.getRuntimePath().toFile().getAbsolutePath());

        // restore registry
        final IStatus status = this.serverMgr.restoreState();

        if (!status.isOK()) {
            Util.log(status);
        }

        if (status.getSeverity() == IStatus.ERROR) {
            throw new CoreException(status);
        }
    }
    
    private void initalizeWorkspaceId() throws Exception {
        final File file = getStateLocation().append(WORKSPACE_UUID_FILE).toFile();
        if (file.exists()) {
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            try {
                workspaceUuid = UUID.fromString(reader.readLine());
            } catch (final IOException error) {
            } finally {
                try {
                    reader.close();
                } catch (final IOException ignored) {
                }
            }
        }
        if (workspaceUuid == null) {
            workspaceUuid = UUID.randomUUID();
            final FileWriter writer = new FileWriter(file);
            try {
                writer.write(workspaceUuid.toString());
            } finally {
                try {
                    writer.close();
                } catch (final IOException ignored) {
                }
            }
        }
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

        try {
            initializeServerRegistry();
            
            initalizeWorkspaceId();
        } catch (final Exception e) {
            if (e instanceof CoreException) {
                throw (CoreException)e;
            }

            throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, e.getLocalizedMessage(), e));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        super.stop(context);

        // restore registry
        final IStatus status = this.serverMgr.saveState();

        if (!status.isOK()) {
            Util.log(status);
        }

        if (status.getSeverity() == IStatus.ERROR) {
            throw new CoreException(status);
        }
    }

}
