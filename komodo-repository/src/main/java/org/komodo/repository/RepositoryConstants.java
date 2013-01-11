/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

/**
 * Constants used when working with the S-RAMP database and Teiid artifacts.
 */
public interface RepositoryConstants {

    /**
     * Constants related to the S-RAMP database.
     */
    public interface Sramp {

        /**
         * The root node path in the S-RAMP database. Value is {@value}.
         */
        String ROOT_PATH = "/s-ramp"; //$NON-NLS-1$

        /**
         * The root node path in the S-RAMP database for document artifacts. Value is {@value}.
         */
        String DOCUMENT_ARTIFACT_PATH = ROOT_PATH + "/core/Document"; //$NON-NLS-1$

        /**
         * The root node path in the S-RAMP database for user-defined artifacts. Value is {@value}.
         */
        String USER_DEFINED_ARTIFACT_PATH = ROOT_PATH + "/user"; //$NON-NLS-1$
    }

}
