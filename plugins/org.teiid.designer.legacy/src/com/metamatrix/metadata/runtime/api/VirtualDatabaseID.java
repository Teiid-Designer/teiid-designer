/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;


/**
 * <p>The VirtualDatabaseID should uniquely identify a  VirutalDatabase.  This is accomplished when instantiating the class by passing the full name of the VirtualDatabase and the version.  These 2 arguments uniquely identify a Virtual Database.</p> 
 */
public interface VirtualDatabaseID extends MetadataID {

/**
 * Returns the version.
 * @return String 
 */
    public String getVersion();

}

