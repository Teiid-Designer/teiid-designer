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

package com.metamatrix.modeler.internal.sdt.types;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.impl.XSDSchemaImpl;

import com.metamatrix.modeler.core.ExternalResourceSet;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * XsdExternalResourceSet
 */
public class XsdExternalResourceSet implements ExternalResourceSet {
    
    private static ResourceSet xsdResourceSet = XSDSchemaImpl.getGlobalResourceSet();

    /**
     * Add the URI conversion between the installation specific XMLSchema URI,
     * e.g. "platform:/plugin/org.eclipse.xsd_1.1.1/cache/www.w3.org/2001/XMLSchema.xsd", 
     * and the general XMLSchema URI of "http://www.w3.org/2001/XMLSchema"
     */
    static {
        // Add the conversion for the XMLSchema.xsd resource
        URI logicalURI  = URI.createURI(ModelerCore.XML_SCHEMA_GENERAL_URI);
        URI physicalURI = URI.createURI(ModelerCore.XML_SCHEMA_ECLIPSE_PLATFORM_URI);
        xsdResourceSet.getURIConverter().getURIMap().put(logicalURI,physicalURI);
        
        // Add the conversion for the MagicXMLSchema.xsd resource
        logicalURI  = URI.createURI(ModelerCore.XML_MAGIC_SCHEMA_GENERAL_URI);
        physicalURI = URI.createURI(ModelerCore.XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI);
        xsdResourceSet.getURIConverter().getURIMap().put(logicalURI,physicalURI);
        
        // Add the conversion for the XMLSchema-instance.xsd resource
        logicalURI  = URI.createURI(ModelerCore.XML_SCHEMA_INSTANCE_GENERAL_URI);
        physicalURI = URI.createURI(ModelerCore.XML_SCHEMA_INSTANCE_ECLIPSE_PLATFORM_URI);
        xsdResourceSet.getURIConverter().getURIMap().put(logicalURI,physicalURI);
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.modeler.core.ExternalResourceSet#getResourceSet()
     */
    public ResourceSet getResourceSet() {
        return xsdResourceSet;
    }

}
