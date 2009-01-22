/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
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
import com.metamatrix.core.BundleUtil;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * This class provides logging and resource functionality for a plugin. It is intended to be used by providing in all MetaMatrix
 * plugins a single static final member variable that is initialized to an instance of this class. <code>
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

    private DefaultRunner runner = new DefaultRunner();

    private ILog logger;

    /**
     * The Debugger for the plug-in that instantiated this class.
     * 
     * @since 4.0
     */
    private final Debugger debugger;

    /**
     * Construct an instance of this class by specifying the plugin ID. The name of the resource bundle, which should be of the
     * form "<code>PLUGIN_ID</code> .i18n", and should manifest itself as a properties file named "i18n.properties" in the
     * PLUGIN_ID package. This is so that when run outside of the Eclipse Platform, the properties files for all plugins can be
     * placed on the classpath and are all unique.
     * 
     * @param pluginID the identifier of the plugin for which this utility is being instantiated
     * @param bundle the resource bundle
     */
    public PluginUtilImpl( final String pluginID,
                           final ResourceBundle bundle ) {
        this(pluginID, RESOURCE_FILE_ROOT + '.' + RESOURCE_FILE_ROOT, bundle);
    }

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
        this.debugger = new DebuggerImpl(pluginId);

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
        this.runner = new PlatformRunner();
    }

    /**
     * Ensure we're running minimum required version of JRE
     * 
     * @see com.metamatrix.core.PluginUtil#checkJre(java.lang.String)
     * @since 4.0
     */
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
     * @see com.metamatrix.core.util.Debugger#debug(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void debug( final String context,
                       final String message ) {
        this.debugger.debug(context, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#getDebugContextProperty(java.lang.String)
     * @since 4.0
     */
    public String getDebugContextProperty( final String property ) {
        return this.debugger.getDebugContextProperty(property);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#getTraceContextProperty(java.lang.String)
     * @since 4.0
     */
    public String getTraceContextProperty( final String property ) {
        return this.debugger.getTraceContextProperty(property);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#isDebugEnabled(java.lang.String)
     * @since 4.0
     */
    public boolean isDebugEnabled( final String context ) {
        return this.debugger.isDebugEnabled(context);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#isTraceEnabled(java.lang.String)
     * @since 4.0
     */
    public boolean isTraceEnabled( final String context ) {
        return this.debugger.isTraceEnabled(context);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#isTraceEnabled(java.lang.Class)
     * @since 4.0
     */
    public boolean isTraceEnabled( final Class clazz ) {
        return this.debugger.isTraceEnabled(clazz);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#isTraceEnabled(java.lang.Object)
     * @since 4.0
     */
    public boolean isTraceEnabled( final Object object ) {
        return this.debugger.isTraceEnabled(object);
    }

    /**
     * Logs the given status.
     * <p>
     * If this class is initialized by the Eclipse Platform, then this will forward the request to the
     * {@link org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)}method. In other cases (e.g., JUnit), the status
     * is sent to the {@link SystemLogger}.
     * </p>
     * 
     * @param status the status to log; may not be null
     */
    public void log( final IStatus status ) {
        this.logger.log(status);
    }

    /**
     * Logs the given message with the supplied severity.
     * 
     * @param severity the severity, which corresponds to the {@link IStatus#getSeverity() IStatus severity}.
     * @param message the message to be logged
     */
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
    public void log( final int severity,
                     final Throwable t,
                     final String message ) {
        this.logger.log(new Status(severity, this.pluginId, message, t));
    }

    /**
     * Logs the given object using the object's {@link Object#toString() toString()}method.
     * <p>
     * If this class is initialized by the Eclipse Platform, then this will forward the request to the
     * {@link org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)}method. In other cases (e.g., JUnit), the status
     * is sent to the {@link SystemLogger}.
     * </p>
     * 
     * @param obj the object to log; may not be null
     */
    public void log( final Object obj ) {
        if (obj != null) {
            log(IStatus.WARNING, obj.toString());
        }
    }

    /**
     * Logs the given Throwable.
     * <p>
     * If this class is initialized by the Eclipse Platform, then this will forward the request to the
     * {@link org.eclipse.core.runtime.ILog#log(org.eclipse.core.runtime.IStatus)}method. In other cases (e.g., JUnit), the status
     * is sent to the {@link SystemLogger}.
     * </p>
     * 
     * @param throwable the Throwable to log; may not be null
     */
    public void log( final Throwable throwable ) {
        log(IStatus.ERROR, throwable, throwable.getLocalizedMessage());
    }

    /**
     * @see com.metamatrix.core.util.Debugger#print(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void print( final String context,
                       final String message ) {
        this.debugger.print(context, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#print(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void print( final Class clazz,
                       final String message ) {
        this.debugger.print(clazz, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#print(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void print( final Object object,
                       final String message ) {
        this.debugger.print(object, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#printEntered(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void printEntered( final String context,
                              final String message ) {
        this.debugger.printEntered(context, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#printEntered(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void printEntered( final Class clazz,
                              final String message ) {
        this.debugger.printEntered(clazz, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#printEntered(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void printEntered( final Object object,
                              final String message ) {
        this.debugger.printEntered(object, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#printExited(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void printExited( final String context,
                             final String message ) {
        this.debugger.printExited(context, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#printExited(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void printExited( final Class clazz,
                             final String message ) {
        this.debugger.printExited(clazz, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#printExited(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void printExited( final Object object,
                             final String message ) {
        this.debugger.printExited(object, message);
    }

    /**
     * @see com.metamatrix.core.PluginUtil#run(org.eclipse.core.runtime.ISafeRunnable)
     */
    public void run( ISafeRunnable runnable ) {
        this.runner.run(runnable);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#start(java.lang.String)
     */
    public void start( String theTimerId ) {
        this.debugger.start(theTimerId);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#start(java.lang.String, java.lang.String)
     */
    public void start( String theTimerId,
                       String theContextId ) {
        this.debugger.start(theTimerId, theContextId);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#start(java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void start( String theTimerId,
                       String theContextId,
                       Object[] theInfo ) {
        this.debugger.start(theTimerId, theContextId, theInfo);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#stop(java.lang.String)
     */
    public void stop( String theTimerId ) {
        this.debugger.stop(theTimerId);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#stop(java.lang.String, java.lang.String)
     */
    public void stop( String theTimerId,
                      String theContextId ) {
        this.debugger.stop(theTimerId, theContextId);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#stop(java.lang.String, java.lang.String, java.lang.Object[])
     */
    public void stop( String theTimerId,
                      String theContextId,
                      Object[] theInfo ) {
        this.debugger.stop(theTimerId, theContextId, theInfo);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#trace(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void trace( final String context,
                       final String message ) {
        this.debugger.trace(context, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#trace(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void trace( final Class clazz,
                       final String message ) {
        this.debugger.trace(clazz, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#trace(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void trace( final Object object,
                       final String message ) {
        this.debugger.trace(object, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#traceEntered(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void traceEntered( final String context,
                              final String message ) {
        this.debugger.traceEntered(context, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#traceEntered(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void traceEntered( final Class clazz,
                              final String message ) {
        this.debugger.traceEntered(clazz, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#traceEntered(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void traceEntered( final Object object,
                              final String message ) {
        this.debugger.traceEntered(object, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#traceExited(java.lang.String, java.lang.String)
     * @since 4.0
     */
    public void traceExited( final String context,
                             final String message ) {
        this.debugger.traceExited(context, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#traceExited(java.lang.Class, java.lang.String)
     * @since 4.0
     */
    public void traceExited( final Class clazz,
                             final String message ) {
        this.debugger.traceExited(clazz, message);
    }

    /**
     * @see com.metamatrix.core.util.Debugger#traceExited(java.lang.Object, java.lang.String)
     * @since 4.0
     */
    public void traceExited( final Object object,
                             final String message ) {
        this.debugger.traceExited(object, message);
    }

    /**
     * This class is used by the {@link #run(ISafeRunnable)}method.
     * 
     * @since 4.0
     */
    protected class DefaultRunner {
        public void run( final ISafeRunnable code ) {
            Assertion.isNotNull(code);
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
                public void handleException( Throwable exception ) {
                    // log and then call the code's handle method ...
                    String message = getString("PluginUtilImpl.Error_while_running", getPluginId()); //$NON-NLS-1$
                    PluginUtilImpl.this.log(IStatus.ERROR, message);
                    code.handleException(exception);
                }

                public void run() throws Exception {
                    SafeRunner.run(code);
                }
            };
            // Run the wrapper
            SafeRunner.run(wrapper);
        }
    }
}
