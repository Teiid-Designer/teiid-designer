/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact.teiid;

import org.komodo.repository.artifact.Artifact;

/**
 * A data policy artifact.
 */
public interface DataPolicyArtifact extends Artifact {

    /**
     * A relationship between a data policy artifact and its permission artifacts.
     */
    public static final RelationshipType PERMISSIONS_RELATIONSHIP = new RelationshipType() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.RelationshipType#getId()
         */
        @Override
        public String getId() {
            return "DataPolicyPermissions"; //$NON-NLS-1$
        }

    };

    /**
     * The artifact type for a data policy artifact.
     */
    public static final Type TYPE = new Type() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.Type#getId()
         */
        @Override
        public String getId() {
            return "TeiidDataPolicy"; //$NON-NLS-1$
        }

    };

}
