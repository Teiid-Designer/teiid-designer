/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

/**
 * A Registry represents a single naming/directory service
 * through which objects can be registered and discovered.
 */
public interface Registry {
    
    /**
     * Look up an object by name in the registry.
     * @param name the name
     * @return the Object registered under that name; may be null if no
     * register entry could be found
     */
    public Object lookup( String name );
    
    /**
     * Register the specified object under the supplied name.
     * @param name the name under which the object is to be registered
     * @param obj the object to be registered
     * @return the object currently registered under the supplied name, or null
     * if there is no object currently registered
     */
    public Object register( String name, Object obj );
    
    /**
     * Unregister the object under the supplied name.
     * @param name the registration name
     * @return the object currently registered under the supplied name, or null
     * if there is no object currently registered
     */
    public Object unregister( String name );
    
    

}
