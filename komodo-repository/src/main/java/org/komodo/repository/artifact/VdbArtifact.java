/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact;

/**
 * A VDB artifact.
 */
public class VdbArtifact implements Artifact {

    /**
     * A relationship between a VDB artifact and its derived artifacts.
     */
    public static final RelationshipType DERIVED_RELATIONSHIP = new RelationshipType() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.RelationshipType#getId()
         */
        @Override
        public String getId() {
            return "VdbDerivedArtifacts"; //$NON-NLS-1$
        }

    };

    /**
     * The S-RAMP artifact type for a VDB artifact.
     */
    public static final Type TYPE = new Type() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.Type#getId()
         */
        @Override
        public String getId() {
            return "TeiidVdb"; //$NON-NLS-1$
        }

    };

}
