/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.util.Properties;


/**
 * MappingAdapterDescriptor
 */
public interface ExternalResourceDescriptor {
    
    /**
     * Return the identifier of the plugin containing the metamodel extension
     * @return the identifier, or null if no plugin ID exists.
     */
    String getPluginID();
    
    /**
     * Return the identifier of the metamodel extension
     * @return the identifier, or null if no extension ID exists.
     */
    String getExtensionID();
    
    /**
     * Return the name of the external resource
     * @return String or null if no name exists.
     */
    String getResourceName();
    
    /**
     * Return the priority associated with loading the external resource.
     * Before loading any external resources, all ExternalResourceDescriptor
     * instances are ordered by priority.  The external resource with the
     * highest priority value will be loaded first while the resource
     * with the lowest priority value will be loaded last.  If a priority
     * was not set on the descriptor, then 0 is returned by default.
     * @return priority value
     */
    int getPriority();
    
    /**
     * Return the location of the external resource in the form of a URL string.
     * @return URL or null if the URL is malformed or the resource
     * does not exist.
     */
    String getResourceUrl();
    
    /**
     * Return the address of the external resource in the form of a URI string.
     * @return URI or null if no URI exists.
     */
    String getInternalUri();
    
    /**
     * Return the {@link java.util.Properties} associated with this external resource.
     * @return Properties.
     */
    Properties getProperties();
    
}
