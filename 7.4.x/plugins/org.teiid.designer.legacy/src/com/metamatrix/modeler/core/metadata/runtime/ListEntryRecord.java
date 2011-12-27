/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

/**
 * ListEntryRecord
 */
public interface ListEntryRecord {

    /**
     * Get the UUID of the entity
     * @return the UUID of the entity
     */
    String getUUID();
    
    /**
     * Get the position of the entry within the list
     * @return entry's position
     */
    int getPosition();

}
