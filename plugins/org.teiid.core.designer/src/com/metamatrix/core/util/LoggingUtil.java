/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.util;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * This class provides an alternative implementation of {@link PluginUtil} that is for logging purposes only.
 * 
 * Plug-ins that wish to utilize Eclipse's {@link NLS} i18n framework can instanciate this class instead of a
 * {@link PluginUtilImpl}
 *
 */
public class LoggingUtil implements PluginUtil {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(PluginUtilImpl.class);

    private static final String JAVA_VERSION = "java.version"; //$NON-NLS-1$

    private static final String VERSION_DELIMITERS = "._- "; //$NON-NLS-1$

    public static final String CONFIG = "config"; //$NON-NLS-1$

    private static final String INVALID_JAVA_VERSION_MESSAGE_ID = "invalidJavaVersionMessage"; //$NON-NLS-1$


    private ILog logger;
    
    private String pluginId;

    /**
     * Construct an instance of this class by specifying the plugin ID.
     * 
     * @param pluginId the identifier of the plugin for which this utility is being instantiated
     * @param bundleName the name of the resource bundle; used for problem reporting purposes only
     * @param bundle the resource bundle
     */
    public LoggingUtil( final String pluginId) {
        super();
        this.pluginId = pluginId;
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
                public void handleException( Throwable exception ) {
                    // log and then call the code's handle method ...
                    String message = getString("PluginUtilImpl.Error_while_running", getPluginId()); //$NON-NLS-1$
                    LoggingUtil.this.log(IStatus.ERROR, message);
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

	@Override
	public String getString(String key, Object... parameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getString(String key, Object parameter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getStringOrKey(String key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean keyExists(String key) {
		throw new UnsupportedOperationException();
	}



}
