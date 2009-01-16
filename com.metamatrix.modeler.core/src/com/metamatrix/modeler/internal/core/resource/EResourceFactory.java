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

package com.metamatrix.modeler.internal.core.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/**
 * @since 4.3
 */
public class EResourceFactory extends ResourceFactoryImpl {
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    
    /**
     * Construct and instance of the ResourceFactory.
     */
    public EResourceFactory() {
        super();
    }
    
    //==================================================================================
    //                   O V E R R I D D E N   M E T H O D S
    //==================================================================================
    

    /**
     * Returns a newly allocated default resource {@link ResourceImpl#ResourceImpl(URI) implementation}.
     * @param uri the URI.
     * @return a new resource for the URI.
     */
    @Override
    public Resource createResource(URI uri) {
  	    EResourceImpl resource = new EResourceImpl(uri);
        return resource;
    }

}
