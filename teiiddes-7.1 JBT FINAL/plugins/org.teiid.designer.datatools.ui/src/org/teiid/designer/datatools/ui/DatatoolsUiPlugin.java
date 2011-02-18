package org.teiid.designer.datatools.ui;

import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;

/**
 * The activator class controls the plug-in life cycle
 */
public class DatatoolsUiPlugin extends AbstractUiPlugin implements DatatoolsUiConstants {
    
	// The shared instance
	private static DatatoolsUiPlugin plugin;
	
	/**
	 * The constructor
	 */
	public DatatoolsUiPlugin() {
	}

    @Override
    protected ActionService createActionService( IWorkbenchPage workbenchPage ) {
        return null;
    }

    @Override
    public PluginUtil getPluginUtil() {
        return UTIL;
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
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        super.stop(context);
    }

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DatatoolsUiPlugin getDefault() {
		return plugin;
	}
}