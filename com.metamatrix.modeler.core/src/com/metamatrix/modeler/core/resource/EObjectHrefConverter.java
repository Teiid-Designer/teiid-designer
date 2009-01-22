/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * A converter to normalize EObject URI instances of the form resourceURI#fragment. 
 */
public interface EObjectHrefConverter {
    
    /**
     * Return the physical URI for the specified logical EObject URI.
     * If no mapping exists in the converter for the specified logical
     * URI then null is returned.
     * @param logicalUri the logical URI to lookup; may not be null
     * @return the physical URI mapping.
     */
    URI getPhysicalURI(URI logicalURI);
    
    /**
     * Return the physical URI for the specified EObject.
     * @param eObject the EObject instance to use; may not be null
     * @return the physical URI.
     */
    URI getPhysicalURI(EObject eObject);
    
    /**
     * Return the logical URI for the specified physical EObject URI.
     * If no mapping exists in the converter for the specified physical
     * URI then null is returned.
     * @param physicalUri the physical URI to lookup; may not be null
     * @return the logical URI mapping.
     */
    URI getLogicalURI(URI physicalURI);
    
    /**
     * Return the logical URI for the specified EObject. If no logical
     * mapping exists in the converter for the specified EObject then
     * then null is returned.
     * @param eObject the EObject instance to use; may not be null
     * @return the logical URI mapping.
     */
    URI getLogicalURI(EObject eObject);

}
