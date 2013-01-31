/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact;

/**
 * Represents a Komodo artifact.
 */
public interface Artifact {

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

}
