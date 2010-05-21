/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

import java.util.List;

/**
 * ModelRecord
 */
public interface VdbRecord extends MetadataRecord {
    
    /**
     * Return the version of the VDB archive
     * @return
     */
    String getVersion();
    
    /**
     * Return the identifier for the VDB archive
     * @return
     */
    String getIdentifier();
    
    /**
     * Return the description for the VDB archive
     * @return
     */
    String getDescription();
    
    /**
     * Return the name of the VDB archive producer
     * @return
     */
    String getProducerName();
    
    /**
     * Return the version of the VDB archive producer
     * @return
     */
    String getProducerVersion();
    
    /**
     * Return the name of the provider
     * @return
     */
    String getProvider();
    
    /**
     * Return the time the VDB archive was last changed
     * @return
     */
    String getTimeLastChanged();
    
    /**
     * Return the time the VDB archive was last re-indexed
     * @return
     */
    String getTimeLastProduced();
    
    /**
     * Return the list of model identifiers for the VDB archive
     * @return
     */
    List getModelIDs();

}
