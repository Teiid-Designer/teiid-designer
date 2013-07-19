/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * @since 8.0
 */
public class DesignerSPIPlugin extends Plugin {

    /**
     * This plugin's identifier.
     */
    public static final String PLUGIN_ID = "org.teiid.designer.spi"; //$NON-NLS-1$

    /**
     * The package identifier.
     */
    public static final String PACKAGE_ID = DesignerSPIPlugin.class.getPackage().getName();

    /**
     * The shared instance.
     */
    private static DesignerSPIPlugin plugin;

    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * Logs the given message and {@link Throwable}with the supplied severity.
     *
     * @param severity the severity, which corresponds to the {@link IStatus#getSeverity() IStatus severity}.
     * @param message the message to be logged
     * @param t the exception; may be null
     */
    private static void log(int error, Throwable throwable, String message) {
        plugin.getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, throwable));
    }

    /**
     * Logs the given Throwable.
     * <p>
     * If this class is initialized by the Eclipse Platform, then this will forward the request to the
     * {@link org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)}method.
     * </p>
     *
     * @param throwable the Throwable to log; may not be null
     */
    public static void log(Throwable throwable) {
        log(IStatus.ERROR, throwable, throwable.getLocalizedMessage());
    }
}
