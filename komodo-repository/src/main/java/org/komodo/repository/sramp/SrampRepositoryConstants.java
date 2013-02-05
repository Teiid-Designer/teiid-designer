/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.sramp;

import org.komodo.repository.artifact.Artifact.RelationshipType;

/**
 * Constants used when working with the S-RAMP.
 */
public interface SrampRepositoryConstants {

    /**
     * The root node path in the S-RAMP database. Value is {@value}.
     */
    String ROOT_PATH = "/s-ramp"; //$NON-NLS-1$

    /**
     * The root node path in the S-RAMP database for document artifacts. Value is {@value}.
     */
    String DOCUMENT_ARTIFACT_PATH = ROOT_PATH + "/core/Document"; //$NON-NLS-1$

    /**
     * A relationship between a derived artifact and the document artifact. This is created by S-RAMP deriver framework.
     */
    RelationshipType RELATED_DOCUMENT_RELATIONSHIP = new RelationshipType() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.RelationshipType#getId()
         */
        @Override
        public String getId() {
            return "relatedDocument"; //$NON-NLS-1$
        }

    };

    /**
     * The root node path in the S-RAMP database for user-defined artifacts. Value is {@value}.
     */
    String USER_DEFINED_ARTIFACT_PATH = ROOT_PATH + "/ext"; //$NON-NLS-1$

}
