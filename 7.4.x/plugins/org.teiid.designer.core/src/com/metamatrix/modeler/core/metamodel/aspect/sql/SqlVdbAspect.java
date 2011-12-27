/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import java.util.List;
import org.eclipse.emf.ecore.EObject;

/**
 * SqlVdbAspect is used to get the different properties on a VDB archive for runtime metadata.
 */
public interface SqlVdbAspect extends SqlAspect {
    
    /**
     * Return the version of the VDB archive
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getVersion(EObject eObject);
    
    /**
     * Return the identifier for the VDB archive
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getIdentifier(EObject eObject);
    
    /**
     * Return the description for the VDB archive
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getDescription(EObject eObject);
    
    /**
     * Return the name of the VDB archive producer
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getProducerName(EObject eObject);
    
    /**
     * Return the version of the VDB archive producer
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getProducerVersion(EObject eObject);
    
    /**
     * Return the name of the provider
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getProvider(EObject eObject);
    
    /**
     * Return the time the VDB archive was last changed
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getTimeLastChanged(EObject eObject);
    
    /**
     * Return the time the VDB archive was last re-indexed
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    String getTimeLastProduced(EObject eObject);
    
    /**
     * Return the list of model identifiers for the VDB archive
     * @param eObject The <code>EObject</code> to be checked  
     * @return
     */
    List getModelIDs(EObject eObject);

}
