/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel;

import org.eclipse.emf.ecore.xmi.XMLResource;


/** 
 * Interface defining the methods for the ModelerCore.EXTENSION_POINT.RESOURCE_LOAD_OPTIONS extension.
 * @since 4.3
 */
public interface ResourceLoadOptionContributor {
    
    /**
     * Add XMLInfo mappings to the specified XMLMap used when loading any
     * xmi resource into a Container.
     * @param xmlMap the XMLMap to add mappings to 
     * @since 4.3
     */
    void addMappings(XMLResource.XMLMap xmlMap);

}
