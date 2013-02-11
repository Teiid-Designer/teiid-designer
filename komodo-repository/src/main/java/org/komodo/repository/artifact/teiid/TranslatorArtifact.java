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
 * A translator artifact.
 */
public interface TranslatorArtifact extends Artifact {

    /**
     * A relationship between a translator artifact and the source artifacts that reference it.
     */
    public static final RelationshipType SOURCES_RELATIONSHIP = new RelationshipType() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.RelationshipType#getId()
         */
        @Override
        public String getId() {
            return "TranslatorSources"; //$NON-NLS-1$
        }

    };

    /**
     * The artifact type for a translator artifact.
     */
    public static final Type TYPE = new Type() {

        /**
         * {@inheritDoc}
         *
         * @see org.komodo.repository.artifact.Artifact.Type#getId()
         */
        @Override
        public String getId() {
            return "TeiidTranslator"; //$NON-NLS-1$
        }

    };

}
