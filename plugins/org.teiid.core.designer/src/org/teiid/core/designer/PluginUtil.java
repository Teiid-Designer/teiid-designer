/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;

/**
 * @since 8.0
 */
public interface PluginUtil {
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
    void log( IStatus status );

    void log( int severity,
              String message );

    void log( int severity,
              Throwable error,
              java.lang.String message );

    /**
     * Logs the given object using the object's {@link Object#toString() toString()} method.
     * 
     * @param obj the object to log; may not be null
     */
    void log( Object obj );

    /**
     * Logs the given Throwable.
     * 
     * @param throwable the Throwable to log; may not be null
     */
    void log( Throwable t );

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
