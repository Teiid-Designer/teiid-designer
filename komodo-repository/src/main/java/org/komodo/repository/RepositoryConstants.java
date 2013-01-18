/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.repository;

import org.komodo.common.util.StringUtil;

/**
 * Constants used when working with the S-RAMP database and Teiid artifacts.
 */
public interface RepositoryConstants {

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

        private RelationshipType(String name) {
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
     * Constants related to the S-RAMP database.
     */
    public interface Sramp {

        /**
         * The root node path in the S-RAMP database. Value is {@value}.
         */
        String ROOT_PATH = "/s-ramp"; //$NON-NLS-1$

        /**
         * The root node path in the S-RAMP database for document artifacts. Value is {@value}.
         */
        String DOCUMENT_ARTIFACT_PATH = ROOT_PATH + "/core/Document"; //$NON-NLS-1$

        /**
         * The root node path in the S-RAMP database for user-defined artifacts. Value is {@value}.
         */
        String USER_DEFINED_ARTIFACT_PATH = ROOT_PATH + "/ext"; //$NON-NLS-1$
    }

}
