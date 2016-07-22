/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.runtime.client;

import java.io.File;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.services.PluginResourceLocator;
import org.osgi.framework.BundleContext;
import org.teiid.core.util.ArgCheck;

/**
 * Activator class of this plugin
 */
public class TeiidRuntimePlugin extends Plugin {

    /**
     * ID of this plugin
     */
    public static String PLUGIN_ID;

    /**
     * Target directory
     */
    private static final String TARGET = "target"; //$NON-NLS-1$

    /**
     * sources jar component
     */
    private static final String SOURCES = "sources"; //$NON-NLS-1$

    /**
     * JAR File Extension
     */
    private static final String JAR = "jar"; //$NON-NLS-1$

    /**
     * Logger for logging warnings and errors. Initialised by the plugin starting
     */
    public static ILog LOGGER;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        TeiidRuntimePlugin.LOGGER = this.getLog();
        PLUGIN_ID = context.getBundle().getSymbolicName();
    }

    /**
     * Logs the given message and {@link Throwable}with the supplied severity.
     * 
     * @param severity the severity, which corresponds to the {@link IStatus#getSeverity() IStatus severity}.
     * @param message the message to be logged
     * @param t the exception; may be null
     */
    public static void log( final int severity, final Throwable t, final String message ) {
        ArgCheck.isNotNull(LOGGER);
        LOGGER.log(new Status(severity, PLUGIN_ID, message, t));
    }

    /**
     * Logs the given error message and {@link Throwable} with a prefix for some context.
     *
     * @param context
     * @param e
     * @param message
     */
    public static void logError(String context, Throwable e, Object message) {
        String msg = context + " : " + message; //$NON-NLS-1$
        log(IStatus.ERROR, e, msg);
    }

    /**
     * Logs the given error message and {@link Throwable} with a prefix for some context.
     *
     * @param context
     * @param message
     */
    public static void logError(String context, Object message) {
        String msg = context + " : " + message; //$NON-NLS-1$
        log(IStatus.ERROR, null, msg);
    }

    /**
     * Find the plugin jar based on the given path
     *
     * @param path
     * @return
     */
    private static String findPluginJar(IPath path) {
        if (path == null)
            return null;

        if (!path.getFileExtension().equals(JAR))
            path = path.addFileExtension(JAR);

        String osPath = path.toOSString();
        File jarFile = new File(osPath);
        if (jarFile.exists())
            return osPath;

        return null;
    }

    /**
     * Find a built jar in a 'target' sub-directory of the given path location.
     *
     * This location only really occurs when developing / testing and a built
     * version is available in the maven target directory.
     *
     * @param path
     * @return
     */
    private static String findTargetJar(IPath path) {
        if (path == null)
            return null;

        path = path.append(TARGET);

        File targetDir = new File(path.toOSString());
        if (! targetDir.isDirectory())
            return null;

        for (File file : targetDir.listFiles()) {
            if (! file.getName().endsWith(JAR))
                continue;

            // Ignore sources jar
            if (file.getName().contains(SOURCES))
                continue;

            if (file.getName().startsWith(PLUGIN_ID))
                return file.getAbsolutePath();
        }

        return null;
    }

    /**
     * The location of this plugin from the filesystem
     *
     * @return path of plugin
     */
    public static String getPluginPath() {
        IPath path = PluginResourceLocator.getPluginRootPath(PLUGIN_ID);
        String jarPath = findPluginJar(path);

        if (jarPath == null) {
            /*
             * Should normally exist in installed environment but when developing it will not.
             * Use the backup version of checking the target directory for a build jar of this plugin.
             */
            jarPath = findTargetJar(path);
        }

        return jarPath;
    }
}
