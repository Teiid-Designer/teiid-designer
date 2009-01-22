/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.embeddedquery.workspace;

import java.util.List;


/** 
 * interface that defines the mapping between the model name and the corresponding 
 * connector binding. 
 */
public interface WorkspaceInfo {

    /**
     * Gets the name of the binding for the given model name, mapping not found returns null 
     * @param modelName
     * @return
     */
    List<String> getBinding(String modelName);
    
    /**
     * Get the metadata interface  
     * @return
     */
    Object getMetadata();
}
