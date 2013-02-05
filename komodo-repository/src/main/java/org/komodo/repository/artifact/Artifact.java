/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact;

import org.komodo.teiid.model.Describable;
import org.komodo.teiid.model.Propertied;

/**
 * Represents a Komodo artifact.
 */
public interface Artifact extends Describable, Propertied {

    /**
     * Names of default properties for use in queries.
     */
    public interface Property {

        /**
         * The 'created by' property name. Value is {@value}.
         */
        String CREATED_BY = "createdBy"; //$NON-NLS-1$

        /**
         * The 'created timestamp' property name. Value is {@value}.
         */
        String CREATED_TIMESTAMP = "createdTimestamp"; //$NON-NLS-1$

        /**
         * The 'description' property name. Value is {@value}.
         */
        String DESCRIPTION = "description"; //$NON-NLS-1$

        /**
         * The 'last modified by' property name. Value is {@value}.
         */
        String LAST_MODIFIED_BY = "lastModifiedBy"; //$NON-NLS-1$

        /**
         * The 'last modified timestamp' property name. Value is {@value}.
         */
        String LAST_MODIFIED_TIMESTAMP = "lastModifiedTimestamp"; //$NON-NLS-1$

        /**
         * The 'name' property name. Value is {@value}.
         */
        String NAME = "name"; //$NON-NLS-1$

        /**
         * The 'UUID' property name. Value is {@value}.
         */
        String UUID = "uuid"; //$NON-NLS-1$

        /**
         * The 'version' property name. Value is {@value}.
         */
        String VERSION = "version"; //$NON-NLS-1$
    }

    /**
     * A Komodo S-RAMP artifact relationship identifier.
     */
    public interface RelationshipType {

        /**
         * @return a unique relationship identifier (cannot be <code>null</code> or empty)
         */
        String getId();
    }

    /**
     * A Komodo S-RAMP artifact type identifier.
     */
    public interface Type {

        /**
         * @return a unique artifact type identifier (cannot be <code>null</code> or empty)
         */
        String getId();
    }

    /**
     * @return the artifact name (can be <code>null</code> or empty)
     */
    String getArtifactName();

    /**
     * @return the artifact type (never <code>null</code>)
     */
    Type getArtifactType();

    /**
     * @return the artifact UUID (never <code>null</code> or empty)
     */
    String getArtifactUuid();

    /**
     * @return the artifact version (never <code>null</code> or empty)
     */
    String getArtifactVersion();

}
