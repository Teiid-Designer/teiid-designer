/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository.artifact;

import org.komodo.common.util.StringUtil;

/**
 * Represents a Komodo artifact.
 */
public interface Artifact {

    /**
     * Common artifact relationship types.
     */
    public enum RelationshipType {

        /**
         * A relationship between a data policy and its permissions.
         */
        DATA_POLICY_PERMISSIONS("DataPolicyPermissions"), //$NON-NLS-1$

        /**
         * A relationship between a permission and its data policy.
         */
        PERMISSION_DATA_POLICY("PermissionDataPolicy"), //$NON-NLS-1$

        /**
         * A relationship between a derived artifact and the document artifact. This is created by S-RAMP deriver framework.
         */
        RELATED_DOCUMENT("relatedDocument"), //$NON-NLS-1$

        /**
         * A relationship between a schema/model and its sources.
         */
        SCHEMA_SOURCES("SchemaSources"), //$NON-NLS-1$

        /**
         * A relationship between a source and its schema/model.
         */
        SOURCE_SCHEMA("SourceSchema"); //$NON-NLS-1$

        private final String name;

        private RelationshipType(final String name) {
            this.name = (StringUtil.matches(name, "relatedDocument") ? name : (name + "Relationship")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * @return the relationship type name (never <code>null</code> or empty)
         */
        public String getName() {
            return this.name;
        }
    }

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
