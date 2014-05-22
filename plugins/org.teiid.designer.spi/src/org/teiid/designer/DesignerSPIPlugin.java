/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.services.PluginResourceLocator;
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
