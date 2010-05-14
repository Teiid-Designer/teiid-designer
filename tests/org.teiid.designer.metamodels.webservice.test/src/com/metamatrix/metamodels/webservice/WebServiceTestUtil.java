/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;


/** 
 * @since 4.2
 */
public class WebServiceTestUtil {
    
    private static final WebServiceFactory FACTORY = WebServiceFactory.eINSTANCE;
    
    public static Resource createMinimalWebServiceModel( final URI uri ) {
        final Resource resource = new XMIResourceImpl(uri);
        
        // Create a single web service component ...
        final Interface inter = FACTORY.createInterface();
        
        // Add to the resource ...
        resource.getContents().add(inter);
        
        return resource;
    }

}
