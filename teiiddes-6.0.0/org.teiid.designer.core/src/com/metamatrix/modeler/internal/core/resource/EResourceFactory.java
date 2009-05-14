/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
