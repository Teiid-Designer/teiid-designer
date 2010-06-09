package org.teiid.designer.vdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.UUID;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * The activator class controls the plug-in life cycle
 */
public class VdbPlugin extends Plugin {

    /**
     * This plug-in's ID
     */
    public static final String ID = VdbPlugin.class.getPackage().getName();

    private static final String I18N_NAME = ID + ".i18n"; //$NON-NLS-1$

    /**
     * This plug-in's utility for logging and internationalization
     */
    public static final PluginUtil UTIL = new PluginUtilImpl(ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    /**
     * The name of the file containing the {{@link #workspaceUuid workspace UUID} for the current workspace: {@value}
     */
    public static final String WORKSPACE_UUID_FILE = "workspace.uuid"; //$NON-NLS-1$

    /**
     * The UUID created and persisted for the current workspace
     */
    private static UUID workspaceUuid;

    /**
     * The singleton instance of this plug-in
     */
    private static VdbPlugin singleton;

    /**
     * @return the singleton instance of this type
     */
    public static VdbPlugin singleton() {
        return singleton;
    }

    /**
     * @return the UUID of the current workspace
     */
    public static UUID workspaceUuid() {
        return workspaceUuid;
    }

    /**
     * @throws IOException
     */
    public VdbPlugin() throws IOException {
        singleton = this;
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
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
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override public void stop( final BundleContext context ) throws Exception {
        singleton = null;
        super.stop(context);
    }
}
