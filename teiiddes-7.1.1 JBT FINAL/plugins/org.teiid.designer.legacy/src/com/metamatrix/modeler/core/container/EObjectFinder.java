/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.container;


/**
 * @since 3.1
 */
public interface EObjectFinder {
    //############################################################################################################################
	//# Methods                                                                                                                  #
	//############################################################################################################################
    
    /**
     * Find the object with the specified primary key.
     * @param key the primary key for the object
     * @return Object the object with the matching primary key, or null 
     * if no object with the specified primary
     * key could be found
     */
    Object find(Object key);
    
    /**
     * Find the object key for the given obj.
     * @param the object for which to find the key
     * @return Object the object key for the given object, or null
     * if no key for the given object
     * @author Lance Phillips
     *
     * @since 3.1
     */
    Object findKey(Object object);
}
