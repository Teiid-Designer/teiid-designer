/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
