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
         * The S-RAMP artifact type for a VDB data policy.
         */
        DATA_POLICY("DATA_POLICY"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a VDB entry.
         */
        ENTRY("ENTRY"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a VDB data permission.
         */
        PERMISSION("PERMISSION"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a VDB schema/model.
         */
        SCHEMA("SCHEMA"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a VDB translator.
         */
        TRANSLATOR("TRANSLATOR"), //$NON-NLS-1$

        /**
         * The S-RAMP artifact type for a VDB artifact.
         */
        VDB("VDB"); //$NON-NLS-1$

        private final String name;

        private Type(final String name) {
            this.name = "Teiid_" + name; //$NON-NLS-1$
        }

        /**
         * @return the artifact type name (never <code>null</code>)
         */
        public String getName() {
            return this.name;
        }
    }

}
