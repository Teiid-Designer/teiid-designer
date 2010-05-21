/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;

/**
 * The runtime id used to uniquely identify a Model. 
 */
public interface ModelID extends MetadataID {
/**
 * Returns the version.
 * @return String 
 */
    public String getVersion();
}

