/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.core.log.Logger;

/**
 * @since 4.0
 */
public interface PluginUtil extends Logger {
    // ============================================================================================================================
    // Utility Methods

    /**
     * <p>
     * Checks whether the version of the currently running JRE is equal to or higher than the specified minimum version.
     * </p>
     * 
     * @param version The minimum required Java version.
     * @throws CoreException If the currently running JRE is lower than the specified minimum version.
     * @since 4.0
     */
    void checkJre( String version ) throws CoreException;

    /**
     * Logs the given status.
     * 
     * @param status the status to log; may not be null
     */
    public void log( IStatus status );

    /**
     * Logs the given object using the object's {@link Object#toString() toString()} method.
     * 
     * @param obj the object to log; may not be null
     */
    public void log( Object obj );

    /**
     * Logs the given Throwable.
     * 
     * @param throwable the Throwable to log; may not be null
     */
    public void log( Throwable t );

    /**
     * Returns the localized string identified by the specified key if it exists, or the key itself if not.
     * 
     * @param key the key in the resource file
     * @return the localized string, or they key if not found.
     */
    String getStringOrKey( String key );

    /**
     * Determines if the given key exists in the resource file.
     * 
     * @param key the key in the resource file
     * @return True if the key exists.
     * @since 4.0
     */
    boolean keyExists( String key );

    /**
     * This method executes the {@link ISafeRunnable}. If this class has not been initialized via the Eclipse platform, this
     * method simply runs the code and calls the exception handler. If, however, this class is running in the Eclipse environment,
     * this method delegates to the {@link org.eclipse.core.runtime.Platform#run(org.eclipse.core.runtime.ISafeRunnable)}
     * 
     * @param runnable the runnable object to be executed
     */
    void run( ISafeRunnable runnable );

    /**
     * @param key
     * @param parameters
     * @return
     */
    String getString( String key,
                      Object... parameters );

    /**
     * @param key
     * @param parameter
     * @return
     */
    String getString( String key,
                      Object parameter );

}
