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
 * A schema/model artifact.
 */
public interface SchemaArtifact extends Artifact {

    /**
     * A relationship between a schema/model artifact and its source artifacts.
     */
    public static final RelationshipType SOURCES_RELATIONSHIP = new RelationshipType() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.RelationshipType#getId()
         */
        @Override
        public String getId() {
            return "SchemaSources"; //$NON-NLS-1$
        }

    };

    /**
     * The artifact type for a schema/model artifact.
     */
    public static final Type TYPE = new Type() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.Type#getId()
         */
        @Override
        public String getId() {
            return "TeiidSchema"; //$NON-NLS-1$
        }

    };

}
