/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogListener;
import org.teiid.core.designer.BundleUtil;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.PluginUtil;


/**
 * This class provides logging and resource functionality for a plugin. It is intended to be used by providing in all Teiid
 * Designer plugins a single static final member variable that is initialized to an instance of this class. <code>
 *        public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID);
 * </code> </p>
 * <p>
 * The only other code required in the {@link Plugin}subclass is a call to the {@link #initializePlatformLogger(Plugin)}so that a
 * new {@link LogListener}can be added to this object's log that sends errors to the Eclipse error log. This is done with the
 * following statement: <code>
 *        ((PluginUtilImpl)Util).initializePlatformLogger(this);
 * </code>
 * </p>
 * <p>
 * If this is done correctly, then any component with a particular plugin can make use of that plugin's logger and access resource
 * bundles <i>regardless of whether or not the plugin is instantiated and activated by the Eclipse Platform </i>. This means that
 * the same client code will work if run, for example, in a JUnit test application or when running within the Eclipse Workbench.
 * </p>
 * <p>
 * By default, when this class is instantiated, the <code>log</code> methods write the log entry to the {@link System#out standard
 * output stream}in a format that is both human readable and easily parsed into a spreadsheet (i.e., message fragments are
 * separated by various delimiters). However, when the {@link #initializePlatformLogger(Plugin)}method is called (by a Plugin's
 * {@link Plugin#start()}method), this class sends the log entries to the {@link Plugin#getLog() Plugin's logger}.
 * </p>
 * <p>
 * Resources (accessed through {@link ResourceBundle}) are expected to be located in the package of the {@link Plugin}subclass
 * (which generally matches the plugin ID), and localized strings should be located in a file beginning with <code>i18n</code> and
 * having the appropriate language, country and variant. Thus, {@link ResourceBundle}will attempt to locate such files in the
 * following order:
 * </p>
 * <p>
 * &lt;pluginpackage>.i18n_&lt;language1>_&lt;country1>_&lt;variant1>.class <BR>
 * &lt;pluginpackage>.i18n_&lt;language1>_&lt;country1>.class <BR>
 * &lt;pluginpackage>.i18n_&lt;language1>.class <BR>
 * &lt;pluginpackage>.i18n_&lt;language2>_&lt;country2>_&lt;variant2>.class <BR>
 * &lt;pluginpackage>.i18n_&lt;language2>_&lt;country2>.class <BR>
 * &lt;pluginpackage>.i18n_&lt;language2>.class <BR>
 * &lt;pluginpackage>.i18n.class <BR>
 * &lt;pluginpackage>.i18n_&lt;language1>_&lt;country1>_&lt;variant1>.properties <BR>
 * &lt;pluginpackage>.i18n_&lt;language1>_&lt;country1>.properties <BR>
 * &lt;pluginpackage>.i18n_&lt;language1>.properties <BR>
 * &lt;pluginpackage>.i18n_&lt;language2>_&lt;country2>_&lt;variant2>.properties <BR>
 * &lt;pluginpackage>.i18n_&lt;language2>_&lt;country2>.properties <BR>
 * &lt;pluginpackage>.i18n_&lt;language2>.properties <BR>
 * &lt;pluginpackage>.i18n.properties
 * </p>
 *
 * @since 8.0
 */
public class PluginUtilImpl extends BundleUtil implements PluginUtil {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(PluginUtilImpl.class);

    private static final String JAVA_VERSION = "java.version"; //$NON-NLS-1$

    private static final String VERSION_DELIMITERS = "._- "; //$NON-NLS-1$

    public static final String RESOURCE_FILE_ROOT = "i18n"; //$NON-NLS-1$

    public static final String CONFIG = "config"; //$NON-NLS-1$

    private static final String INVALID_JAVA_VERSION_MESSAGE_ID = "invalidJavaVersionMessage"; //$NON-NLS-1$

    private static final String PRODUCT_PROPERTIES = "product.properties"; //$NON-NLS-1$

    /**
     * Indicates if an attempt to load the product properties has been made.
     * 
     * @since 5.0.2
     */
    private static boolean productPropsLoaded;

    private ILog logger;

    /**
     * Construct an instance of this class by specifying the plugin ID.
     * 
     * @param pluginId the identifier of the plugin for which this utility is being instantiated
     * @param bundleName the name of the resource bundle; used for problem reporting purposes only
     * @param bundle the resource bundle
     */
    public PluginUtilImpl( final String pluginId,
                           final String bundleName,
                           final ResourceBundle bundle ) {
        // Note that the bundle has to be passed in. When running in Eclipse, if another plugin
        // that requires this plugin is instantiating this class, then this class is loaded
        // by this plugin's class loader (and not that of the calling plugin). Therefore, if
        // this class were to get the bundle, this plugin's class loader would be used to find
        // the bundles in other plugins, which of course does not work.
        super(pluginId, bundleName, bundle);

        // see if a product properties file exists which is used for overriding values
        if (!PluginUtilImpl.productPropsLoaded) {
            if (Platform.isRunning() && Platform.getInstallLocation() != null) {
                try {
                    File file = getConfigDirectory();
                    file = new File(file, PRODUCT_PROPERTIES);

                    if (file.exists()) {
                        BundleUtil.productProps = new PropertyResourceBundle(new FileInputStream(file));
                        PluginUtilImpl.productPropsLoaded = true;
                    }
                } catch (Exception theException) {
                    log(theException);
                    BundleUtil.productProps = null;
                }
            }
        }
    }

    public File getConfigDirectory() {
        if (Platform.isRunning() && Platform.getInstallLocation() != null) {
            final URL url = Platform.getInstallLocation().getURL();
            if (url != null) {
                try {
                    final File parentDir = new File(FileLocator.toFileURL(url).getFile()).getParentFile();
                    return new File(parentDir, CONFIG);
                } catch (IOException err) {
                    // Just return null;
                }
            }
        }

        return null;
    }

    /**
     * This method should be called by a {@link Plugin}subclass in its {@link Plugin#start() start()}method as follows: <code>
     *        Util.initializePlatformLogger(this);
     * </code>
     * 
     * @param plugin the {@link Plugin}whose {@link Plugin#getLog() log}should be used
     */
    public void initializePlatformLogger( final Plugin plugin ) {
        this.logger = plugin.getLog();
    }

    /**
     * Ensure we're running minimum required version of JRE
     * 
     * @see org.teiid.core.designer.PluginUtil#checkJre(java.lang.String)
     * @since 4.0
     */
    @Override
	public void checkJre( final String version ) throws CoreException {
        final String ver = System.getProperty(JAVA_VERSION);
        final StringTokenizer verIter = new StringTokenizer(ver, VERSION_DELIMITERS);
        boolean invalid = false;
        for (final StringTokenizer minVerIter = new StringTokenizer(version, VERSION_DELIMITERS); minVerIter.hasMoreTokens();) {
            if (!verIter.hasMoreTokens()) {
                invalid = true;
                break;
            }
            final int lvl = Integer.parseInt(verIter.nextToken());
            final int minLvl = Integer.parseInt(minVerIter.nextToken());
            if (lvl > minLvl) {
                return;
            }
            if (lvl < minLvl) {
                invalid = true;
                break;
            }
        }
        if (invalid) {
            final String plugin = Platform.getBundle(this.pluginId).getSymbolicName();
            final String msg = CoreModelerPlugin.Util.getString(I18N_PREFIX + INVALID_JAVA_VERSION_MESSAGE_ID,
                                                                plugin,
                                                                version,
                                                                ver);
            final Status status = new Status(IStatus.ERROR, CoreModelerPlugin.PLUGIN_ID, 0, msg, null);
            throw new CoreException(status);
        }
    }

    /**
     * Logs the given status.
     * <p>
     * If this class is initialized by the Eclipse Platform, then this will forward the request to the
     * {@link org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)}method.
     * </p>
     * 
     * @param status the status to log; may not be null
     */
    @Override
	public void log( final IStatus status ) {
        this.logger.log(status);
    }

    /**
     * Logs the given message with the supplied severity.
     * 
     * @param severity the severity, which corresponds to the {@link IStatus#getSeverity() IStatus severity}.
     * @param message the message to be logged
     */
    @Override
	public void log( final int severity,
                     final String message ) {
        this.logger.log(new Status(severity, this.pluginId, message));
    }

    /**
     * Logs the given message and {@link Throwable}with the supplied severity.
     * 
     * @param severity the severity, which corresponds to the {@link IStatus#getSeverity() IStatus severity}.
     * @param message the message to be logged
     * @param t the exception; may be null
     */
    @Override
	public void log( final int severity,
                     final Throwable t,
                     final String message ) {
        this.logger.log(new Status(severity, this.pluginId, message, t));
    }

    /**
     * Logs the given object using the object's {@link Object#toString() toString()}method.
     * <p>
     * If this class is initialized by the Eclipse Platform, then this will forward the request to the
     * {@link org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)}method.
     * </p>
     * 
     * @param obj the object to log; may not be null
     */
    @Override
	public void log( final Object obj ) {
        if (obj != null) {
            log(IStatus.WARNING, obj.toString());
        }
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
    @Override
	public void log( final Throwable throwable ) {
        log(IStatus.ERROR, throwable, throwable.getLocalizedMessage());
    }

    /**
     * This class is used by the {@link #run(ISafeRunnable)}method.
     * 
     * @since 4.0
     */
    protected class DefaultRunner {
        public void run( final ISafeRunnable code ) {
            CoreArgCheck.isNotNull(code);
            try {
                code.run();
            } catch (Exception e) {
                handleException(code, e);
            } catch (LinkageError e) {
                handleException(code, e);
            }
        }

        private void handleException( ISafeRunnable code,
                                      Throwable e ) {
            if (!(e instanceof OperationCanceledException)) {
                // try to figure out which plugin caused the problem. Derive this from the class
                // of the code arg. Attribute to the Runtime plugin if we can't figure it out.
                final String pluginId = getPluginId();
                String message = getString("PluginUtilImpl.Error_while_running", pluginId); //$NON-NLS-1$
                IStatus status = null;
                if (e instanceof CoreException) {
                    status = new MultiStatus(pluginId, Platform.PLUGIN_ERROR, message, e);
                    ((MultiStatus)status).merge(((CoreException)e).getStatus());
                } else {
                    status = new Status(IStatus.ERROR, pluginId, Platform.PLUGIN_ERROR, message, e);
                }
                log(status);
            }
            code.handleException(e);
        }
    }

    String getPluginId() {
        return pluginId;
    }

    /**
     * This class is used to delegate the {@link #run(ISafeRunnable)}method to
     * {@link Platform#run(org.eclipse.core.runtime.ISafeRunnable) the Platform}
     */
    protected class PlatformRunner extends DefaultRunner {
        @Override
        public void run( final ISafeRunnable code ) {
            // Wrap the runnable so we can log the exception
            final ISafeRunnable wrapper = new ISafeRunnable() {
                @Override
				public void handleException( Throwable exception ) {
                    // log and then call the code's handle method ...
                    String message = getString("PluginUtilImpl.Error_while_running", getPluginId()); //$NON-NLS-1$
                    PluginUtilImpl.this.log(IStatus.ERROR, message);
                    code.handleException(exception);
                }

                @Override
				public void run() throws Exception {
                    SafeRunner.run(code);
                }
            };
            // Run the wrapper
            SafeRunner.run(wrapper);
        }
    }

    /**
     * Convenience method that adds the specified parameter to a list before calling {@link #getString(String, Object[])}. Insures
     * proper casting before calling the generalized signature.
     * 
     * @since 2.1
     */
    @Override
	public String getString( final String key,
                             final Object parameter ) {
        if (parameter != null) {
            if (parameter.getClass().isArray()) {
                return getString(key, (Object[])parameter);
            }
            if (parameter instanceof List) {
                return getString(key, (List)parameter);
            }
        }
        return getString(key, new Object[] {parameter});
    }

}
