/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.teiid.designer.runtime.ServerManager;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

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

    // listener for context changes (specifically closed contexts)
    private IChangeListener changeListener = new IChangeListener() {
        public void stateChanged( IChangeNotifier theContext ) {
            handleContextChanged(theContext);
        }
    };

    /**
     * The Teiid server registry.
     */
    private ServerManager serverMgr;

    /**
     * Collection of {@link VdbDefnHelper}s for a given {@link InternalVdbEditingContext}. Important to make sure only one context
     * and one helper is constructed for a given VDB. Made protected for testing purposes. Key=InternalVdbEditingContext,
     * value=VdbDefnHelper
     */
    private Map vdbHelperMap = new HashMap();

    /**
     * @return the server manager
     */
    public ServerManager getServerManager() {
        return this.serverMgr;
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;

        // initialize logger first so that other methods can use logger
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

        try {
            initializeServerRegistry();
        } catch (Exception e) {
            if (e instanceof CoreException) {
                throw (CoreException)e;
            }

            throw new CoreException(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, e.getLocalizedMessage(), e));
        }
    }

    /**
     * Obtains the <code>VdbDefnHelper</code> for the specified <code>InternalVdbEditingContext</code>.
     * 
     * @param theContext the context whose helper is being requested
     * @return the helper
     * @since 4.3
     */
    public VdbDefnHelper getVdbDefnHelper( InternalVdbEditingContext theContext ) {
        VdbDefnHelper result = (VdbDefnHelper)this.vdbHelperMap.get(theContext);

        if (result == null) {
            theContext.addChangeListener(this.changeListener);
            result = new VdbDefnHelper(theContext.getPathToVdb().toFile(), theContext);
            this.vdbHelperMap.put(theContext, result);
        }

        return result;
    }

    /**
     * Cleans up the map of context helpers.
     * 
     * @param theContext the context whose state has changed
     * @since 4.3
     */
    void handleContextChanged( IChangeNotifier theContext ) {
        if (this.vdbHelperMap.get(theContext) != null) {
            // only care if the context is now closed
            if (!((VdbEditingContext)theContext).isOpen()) {
                this.vdbHelperMap.remove(theContext);
                theContext.removeChangeListener(this.changeListener);
            }
        }
    }

    private void initializeServerRegistry() throws CoreException {
        this.serverMgr = new ServerManager(DqpPath.getRuntimePath().toFile().getAbsolutePath());

        // restore registry
        IStatus status = this.serverMgr.restoreState();

        if (!status.isOK()) {
            Util.log(status);
        }

        if (status.getSeverity() == IStatus.ERROR) {
            throw new CoreException(status);
        }
    }

}
