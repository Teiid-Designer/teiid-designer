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
     * The Komodo S-RAMP artifact types.
     */
    enum Type {

        /**
         * The S-RAMP artifact type for a data policy artifact.
         */
        DATA_POLICY("DataPolicy"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a VDB entry.
         */
        ENTRY("Entry"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a import VDB artifact.
         */
        IMPORT_VDB("ImportVdb"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a data permission artifact.
         */
        PERMISSION("Permission"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a schema/model artifact.
         */
        SCHEMA("Schema"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a schema data source artifact.
         */
        SOURCE("Source"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a translator artifact.
         */
        TRANSLATOR("Translator"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a VDB artifact.
         */
        VDB("Vdb"); //$NON-NLS-1$

        private final String name;

        private Type(final String name) {
            this.name = "Teiid" + name; //$NON-NLS-1$
        }

        /**
         * @return the artifact type name (never <code>null</code>)
         */
        public String getName() {
            return this.name;
        }
    }

}
