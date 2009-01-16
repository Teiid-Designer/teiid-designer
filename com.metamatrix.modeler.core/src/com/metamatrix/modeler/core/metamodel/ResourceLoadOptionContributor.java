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
