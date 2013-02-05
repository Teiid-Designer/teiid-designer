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
 * A data permission artifact.
 */
public interface PermissionArtifact extends Artifact {

    /**
     * A relationship between a permission artifact and its data policy artifact.
     */
    public static final RelationshipType DATA_POLICY_RELATIONSHIP = new RelationshipType() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.RelationshipType#getId()
         */
        @Override
        public String getId() {
            return "PermissionDataPolicy"; //$NON-NLS-1$
        }

    };

    /**
     * The artifact type for a data permission artifact.
     */
    public static final Type TYPE = new Type() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.Type#getId()
         */
        @Override
        public String getId() {
            return "TeiidPermission"; //$NON-NLS-1$
        }

    };

}
