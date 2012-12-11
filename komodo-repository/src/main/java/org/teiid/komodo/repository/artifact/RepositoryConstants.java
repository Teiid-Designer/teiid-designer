/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository.artifact;

/**
 *
 */
public interface RepositoryConstants {

    public interface Sramp {
        String ROOT_PATH = "/s-ramp/";
    
        String DOCUMENT_ARTIFACT_PATH = ROOT_PATH + "core/Document";
    
        String USER_DEFINED_ARTIFACT_PATH = ROOT_PATH + "user/";
    }

    public interface Teiid {
        String ARTIFACT_TYPE_PREFIX = "Teiid_";
    }
}
