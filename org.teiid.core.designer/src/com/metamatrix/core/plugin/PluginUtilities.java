/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.plugin;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * This class provides static methods to perform the following plugin-related activities:
 * <ul>
 * <li>Instantiate application plugin extension classes, which automatically starts the plugin framework if neccessary,</li>
 * <li>Instantiate other plugin extension classes,</li>
 * <li>Get access to extensions, and</li>
 * <li>Shutdown the plugin framework</li>
 * </ul>
 * Note that if you do not shutdown the plugin framework after instantiating an application, the application will never terminate.
 * 
 * @since 3.1
 * @version 3.1
 * @author <a href="mailto:jverhaeg@metamatrix.com">John P. A. Verhaeg</a>
 */
public abstract class PluginUtilities {

    private static final String PLUGINS_FOLDER = "plugins"; //$NON-NLS-1$
    private static final String BOOT_PLUGIN_FOLDER = "org.eclipse.core.boot"; //$NON-NLS-1$
    private static final String BOOT_JAR = "boot.jar"; //$NON-NLS-1$

    private static final String PROTOCOL = "file"; //$NON-NLS-1$

    private static final String BOOT_LOADER = "org.eclipse.core.boot.BootLoader"; //$NON-NLS-1$

    private static final String TEMPORARY_METADATA_PATH = System.getProperty("java.io.tmpdir") + "metamatrix"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final String STARTUP_METHOD = "startup"; //$NON-NLS-1$

    private static final int BOOT_PLUGIN_FOLDER_NOT_FOUND_CODE = 0;
    private static final String BOOT_PLUGIN_FOLDER_NOT_FOUND_MESSAGE = CoreModelerPlugin.Util.getString("PluginUtilities.Could_not_find___12") + BOOT_PLUGIN_FOLDER + CoreModelerPlugin.Util.getString("PluginUtilities.__folder._13"); //$NON-NLS-1$ //$NON-NLS-2$
    private static final String CONSTRUCTOR_MESSAGE = CoreModelerPlugin.Util.getString("PluginUtilities.Make_sure_the_executable_extension_class_contains_a_public_constructor_14") + //$NON-NLS-1$
                                                      CoreModelerPlugin.Util.getString("PluginUtilities._with_no_arguments._15"); //$NON-NLS-1$

    private static Class bootLoader;
    private static String[] startupArgs = new String[0];

    /**
     * Creates an instance of a class defined by the specified plugin extension, using the value of the specified attribute
     * defined within the specified extension's child element as a fully-qualified class name.
     * 
     * @param extension The extension that defines the class to instantiate.
     * @param element The child element of the extension containing <code>attribute</code>.
     * @param attribute The attribute of <code>element</code> that specifies the fully-qualified class name to instantiate.
     * @return A class instance, or null if either the element or attribute is not found.
     * @throws CoreException If the class instance could not be created for any reason.
     * @since 3.1
     */
    public static Object createExecutableExtension( final IExtension extension,
                                                    final String element,
                                                    final String attribute ) throws CoreException {
        try {
            final IConfigurationElement[] elems = extension.getConfigurationElements();
            for (int ndx = 0, count = elems.length; ndx < count; ++ndx) {
                IConfigurationElement elem = elems[ndx];
                if (elem.getName().equals(element)) {
                    return elem.createExecutableExtension(attribute);
                }
            }
            return null;
        } catch (final CoreException err) {
            if (err.getStatus().getException() instanceof InstantiationException) {
                System.err.println(CONSTRUCTOR_MESSAGE);
            }
            throw err;
        }
    }

    /**
     * Returns the extension identified by the specified fully-qualified ID and contained within the specified list of extensions.
     * 
     * @param id The fully-qualified ID of the extension.
     * @param extensions The array of extensions to be searched.
     * @return An extension, or null if no extension with the specified ID is found in the array.
     * @since 3.1
     */
    public static IExtension getExtension( final String id,
                                           final IExtension[] extensions ) {
        for (int ndx = 0, count = extensions.length; ndx < count; ++ndx) {
            final IExtension extension = extensions[ndx];
            final String uniqueId = extension.getUniqueIdentifier();
            if (uniqueId != null && uniqueId.equals(id)) {
                return extension;
            }
        }
        return null;
    }

    /**
     * Returns all extensions of the extension point identified by the specified fully-qualified ID.
     * 
     * @param id The fully-qualified ID of the extension point.
     * @return The extension point's extensions, or an emply array if no extension point with the specified ID is found.
     * @since 3.1
     */
    public static IExtension[] getExtensions( final String id ) {
        // final IExtensionRegistry registry = Platform.getExtensionRegistry();
        // if (registry != null) {
        // return registry.getExtensions(id);
        // }
        // return new IExtension[0];
        final IExtensionRegistry registry = Platform.getExtensionRegistry();
        if (registry != null) {
            final IExtensionPoint point = registry.getExtensionPoint(id);
            if (point == null) {
                return new IExtension[0];
            }
            return point.getExtensions();
        }
        return new IExtension[0];
    }

    /*
     *  (non-Javadoc)
     * @see org.eclipse.core.runtime.IExtensionRegistry#getConfigurationElementsFor(java.lang.String)
     */
    public static IConfigurationElement[] getConfigurationElementsFor( final String id ) {
        return Platform.getExtensionRegistry().getConfigurationElementsFor(id);
    }

    /**
     * Starts up the plugin framework located at the specified install path, and which uses the specified metadata path to store
     * metadata.
     * 
     * @param installPath The path identifying the folder where the application is installed.
     * @param metadataPath The path identifying the folder under which the Eclipse metadata folder should be created.
     * @return An instance of ApplicationExtension, or null if no extension with the specified ID is found.
     * @throws ClassNotFoundException If the {@link BootLoader} class could not be found in the boot jar file.
     * @throws CoreException If the boot plugin folder could not be found.
     * @throws IllegalAccessException If one of the reflectively accessed BootLoader methods is not public.
     * @throws InvocationTargetException If one of the reflectively called BootLoader methods throws a {@link RuntimeException}.
     * @throws MalformedURLException If installPath contains an invalid path.
     * @throws NoSuchMethodException If one of the reflectively accessed BootLoader or ApplicationExtension methods does not
     *         exist.
     * @since 3.1
     */
    public static void startup( String installPath,
                                String metadataPath )
        throws ClassNotFoundException, CoreException, IllegalAccessException, InvocationTargetException, MalformedURLException,
        NoSuchMethodException {
        ArgCheck.isNotEmpty(installPath,
                            CoreModelerPlugin.Util.getString("PluginUtilities.The_installation_path_must_not_be_empty._18")); //$NON-NLS-1$
        // If it hasn't already been done, get the boot loader needed to instantiate the application extension. The boot loader
        // must be loaded using reflection since it also is defined as a plugin, and the plugin directory is not typically in the
        // classpath.
        if (PluginUtilities.bootLoader == null) {
            if (metadataPath == null) {
                metadataPath = TEMPORARY_METADATA_PATH;
            }
            // Append the plugins folder name to the install path
            final StringBuffer bootFolderPath = new StringBuffer(installPath);
            final char lastChr = bootFolderPath.charAt(bootFolderPath.length() - 1);
            if (lastChr != '/' && lastChr != '\\') {
                bootFolderPath.append('/');
            }
            bootFolderPath.append(PLUGINS_FOLDER);
            // Look for the boot plugin folder under the plugins folder
            final File[] bootPluginFolders = new File(bootFolderPath.toString()).listFiles(new FileFilter() {
                public boolean accept( final File file ) {
                    if (file.isDirectory() && file.getName().startsWith(BOOT_PLUGIN_FOLDER)) {
                        return true;
                    }
                    return false;
                }
            });
            if (bootPluginFolders.length == 0) {
                final IStatus status = new Status(IStatus.ERROR, BOOT_PLUGIN_FOLDER, BOOT_PLUGIN_FOLDER_NOT_FOUND_CODE,
                                                  BOOT_PLUGIN_FOLDER_NOT_FOUND_MESSAGE, null);
                throw new CoreException(status);
            }
            // Append the relative path of the boot.jar file to the first boot plugin folder found
            final String bootJarPath = new File(bootPluginFolders[0], BOOT_JAR).getAbsolutePath().replace(File.separatorChar, '/');
            // Load the BootLoader class from the boot.jar file
            final URL bootUrl = new URL(PROTOCOL, null, bootJarPath);
            final Class bootLoader = new URLClassLoader(new URL[] {bootUrl}, null).loadClass(BOOT_LOADER);
            // Initialize boot loader, again using reflection since the class is defined in a plugin, by passing the metadata
            // directory to the startup method. This initializes the files Eclipse needs in the metadata directory and performs
            // initial processing of the plugins directory (whatever that entails). The plugins directory is apparently located
            // based upon the path used to load the boot.jar.
            final Method startupMeth = bootLoader.getMethod(STARTUP_METHOD, new Class[] {URL.class, String.class, String[].class});
            startupMeth.invoke(bootLoader, new Object[] {null, metadataPath, PluginUtilities.startupArgs});
            // Get access to boot loader's getRunnableMethod for future instantiations of application extensions
            PluginUtilities.bootLoader = bootLoader;
        }
    }
}
