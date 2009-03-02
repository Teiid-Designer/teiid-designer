/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.log.Logger;
import com.metamatrix.core.util.Debugger;

/**
 * See the {@link Debugger}interface for a description of debug-related methods.
 * 
 * @since 4.0
 */
public interface PluginUtil extends
                           Debugger,
                           Logger {
    //============================================================================================================================
    // Utility Methods

    /**
     * <p>
     * Checks whether the version of the currently running JRE is equal to or higher than the specified minimum version.
     * </p>
     * 
     * @param version
     *            The minimum required Java version.
     * @throws CoreException
     *             If the currently running JRE is lower than the specified minimum version.
     * @since 4.0
     */
    void checkJre(String version) throws CoreException;

       
    /**
     * Logs the given status.  
     * @param status the status to log; may not be null
     */
    public void log( IStatus status );

    /**
     * Logs the given object using the object's {@link Object#toString() toString()} method.
     * @param obj the object to log; may not be null
     */
    public void log( Object obj );
    
    /**
     * Logs the given Throwable.
     * @param throwable the Throwable to log; may not be null
     */
    public void log( Throwable t );

    /**
     * Get the string identified by the given key and localized to the current locale.
     * 
     * @param key
     *            the key in the resource file
     * @return the localized String, or "!&lt;key>!" if the string could not be found in the current locale.
     * @exception NullPointerException
     *                if <code>key</code> is <code>null</code>.
     */
    String getString(String key);

    /**
     * Get the string identified by the given key and localized to the current locale, and replace placeholders in the localized
     * string with the string form of the parameters.
     * 
     * @param key
     *            the key in the resource file
     * @param params
     *            the list of parameters that should replace placeholders in the localized string (e.g., "{0}", "{1}", etc.)
     * @return the localized String, or "!&lt;key>!" if the string could not be found in the current locale.
     * @exception NullPointerException
     *                if <code>key</code> is <code>null</code>.
     */
    String getString(String key,
                     List params);

    /**
     * Convenience method that wraps the specified parameter in a Boolean object before calling
     * {@link #getString(String, Object)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     boolean parameter);

    /**
     * Convenience method that wraps the specified parameter in a Byte object before calling {@link #getString(String, Object)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     byte parameter);

    /**
     * Convenience method that wraps the specified parameter in a Character object before calling
     * {@link #getString(String, Object)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     char parameter);

    /**
     * Convenience method that wraps the specified parameter in a Double object before calling {@link #getString(String, Object)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     double parameter);

    /**
     * Convenience method that wraps the specified parameter in a Float object before calling {@link #getString(String, Object)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     float parameter);

    /**
     * Convenience method that wraps the specified parameter in a Integer object before calling
     * {@link #getString(String, Object)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     int parameter);

    /**
     * Convenience method that wraps the specified parameter in a Long object before calling {@link #getString(String, Object)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     long parameter);

    /**
     * Convenience method that adds the specified parameter to a list before calling {@link #getString(String, List)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     Object parameter);

    /**
     * Convenience method that adds the specified parameters to a list before calling {@link #getString(String, List)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     Object parameter1,
                     Object parameter2);

    /**
     * Convenience method that adds the specified parameters to a list before calling {@link #getString(String, List)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     Object parameter1,
                     Object parameter2,
                     Object parameter3);

    /**
     * Gets text substituting values from the given array into the proper place.
     * 
     * @param key
     *            the property key
     * @param parameters
     *            the data placed into the text
     * @return the locale-specific text
     * @since 4.0
     */
    String getString(String key,
                     Object[] parameters);

    /**
     * Convenience method that wraps the specified parameter in a Short object before calling {@link #getString(String, Object)}.
     * 
     * @since 4.0
     */
    String getString(String key,
                     short parameter);

    /**
     * Returns the localized string identified by the specified key if it exists, or the key itself if not.
     * 
     * @param key
     *            the key in the resource file
     * @return the localized string, or they key if not found.
     */
    String getStringOrKey(String key);

    /**
     * Determines if the given key exists in the resource file.
     * 
     * @param key
     *            the key in the resource file
     * @return True if the key exists.
     * @since 4.0
     */
    boolean keyExists(String key);

    /**
     * This method executes the {@link ISafeRunnable}. If this class has not been initialized via the Eclipse platform, this
     * method simply runs the code and calls the exception handler. If, however, this class is running in the Eclipse
     * environment, this method delegates to the
     * {@link org.eclipse.core.runtime.Platform#run(org.eclipse.core.runtime.ISafeRunnable)}
     * 
     * @param runnable
     *            the runnable object to be executed
     */
    void run(ISafeRunnable runnable);
}
