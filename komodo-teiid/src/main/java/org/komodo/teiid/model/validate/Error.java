/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.validate;

import org.komodo.common.validate.Status;
import org.komodo.common.validate.Status.Severity;

/**
 * Validation errors for Teiid model objects.
 */
public enum Error {
    // @formatter:off

    //
    // Translator
    //

    /**
     * Indicates the translator name is <code>null</code> or empty.
     */
    EMPTY_TRANSLATOR_NAME(Severity.ERROR, 1000),

    /**
     * Indicates the translator name is not empty but not valid.
     */
    INVALID_TRANSLATOR_NAME(Severity.ERROR, 1010),

    /**
     * Indicates the translator type is <code>null</code> or empty.
     */
    EMPTY_TRANSLATOR_TYPE(Severity.ERROR, 1020),

    /**
     * Indicates the translator type is not empty but not valid.
     */
    INVALID_TRANSLATOR_TYPE(Severity.ERROR, 1030),

    /**
     * Indicates the translator has no properties.
     */
    NO_TRANSLATOR_PROPERTIES(Severity.ERROR, 1040),

    /**
     * Indicates a translator property name is <code>null</code> or empty.
     */
    EMPTY_TRANSLATOR_PROPERTY_NAME(Severity.ERROR, 1050),

    /**
     * Indicates a translator property name is not empty but not valid.
     */
    INVALID_TRANSLATOR_PROPERTY_NAME(Severity.ERROR, 1060),

    /**
     * Indicates a translator property value is <code>null</code> or empty.
     */
    EMPTY_TRANSLATOR_PROPERTY_VALUE(Severity.ERROR, 1070),

    /**
     * Indicates a translator property value is not empty but not valid.
     */
    INVALID_TRANSLATOR_PROPERTY_VALUE(Severity.ERROR, 1080),

    //
    // Source
    //

    /**
     * Indicates the source name is <code>null</code> or empty.
     */
    EMPTY_SOURCE_NAME(Severity.ERROR, 2000),

    /**
     * Indicates the source name is not empty but not valid.
     */
    INVALID_SOURCE_NAME(Severity.ERROR, 2010),

    /**
     * Indicates the source's translator name is <code>null</code> or empty.
     */
    EMPTY_SOURCE_TRANSLATOR_NAME(Severity.ERROR, 2020),

    /**
     * Indicates the source's translator name is not empty but not valid.
     */
    INVALID_SOURCE_TRANSLATOR_NAME(Severity.ERROR, 2030),

    /**
     * Indicates the source's JNDI name is not empty but not valid.
     */
    INVALID_SOURCE_JNDI_NAME(Severity.ERROR, 2040),

    //
    // Import VDB
    //

    /**
     * Indicates the VDB import name is <code>null</code> or empty.
     */
    EMPTY_IMPORT_VDB_NAME(Severity.ERROR, 3000),

    /**
     * Indicates the VDB name is not empty but not valid.
     */
    INVALID_IMPORT_VDB_NAME(Severity.ERROR, 3010),

    /**
     * Indicates the VDB import version is not valid.
     */
    INVALID_IMPORT_VDB_VERSION(Severity.ERROR, 3020),

    //
    // Schema
    //

    /**
     * Indicates the schema name is <code>null</code> or empty.
     */
    EMPTY_SCHEMA_NAME(Severity.ERROR, 4000),

    /**
     * Indicates the schema name is not empty but not valid.
     */
    INVALID_SCHEMA_NAME(Severity.ERROR, 4010),

    /**
     * Indicates the schema type is <code>null</code> or empty.
     */
    EMPTY_SCHEMA_TYPE(Severity.ERROR, 4020),

    /**
     * Indicates the schema type is not empty but not valid.
     */
    INVALID_SCHEMA_TYPE(Severity.ERROR, 4030),

    /**
     * Indicates the schema's metadata is not valid.
     */
    INVALID_SCHEMA_METADATA(Severity.ERROR, 4040),

    /**
     * Indicates the schema's metadata type is <code>null</code> or empty.
     */
    EMPTY_SCHEMA_METADATA_TYPE(Severity.ERROR, 4050),

    /**
     * Indicates the schema's metadata type not empty but not valid.
     */
    INVALID_SCHEMA_METADATA_TYPE(Severity.ERROR, 4060),

    /**
     * Indicates a schema property name is <code>null</code> or empty.
     */
    EMPTY_SCHEMA_PROPERTY_NAME(Severity.ERROR, 4070),

    /**
     * Indicates a schema property name is not empty but not valid.
     */
    INVALID_SCHEMA_PROPERTY_NAME(Severity.ERROR, 4080),

    /**
     * Indicates a schema property value is <code>null</code> or empty.
     */
    EMPTY_SCHEMA_PROPERTY_VALUE(Severity.ERROR, 4090),

    /**
     * Indicates a schema property value is not empty but not valid.
     */
    INVALID_SCHEMA_PROPERTY_VALUE(Severity.ERROR, 4100),

    //
    // Entry
    //

    /**
     * Indicates the entry path is <code>null</code> or empty.
     */
    EMPTY_ENTRY_PATH(Severity.ERROR, 5000),

    /**
     * Indicates the entry path is not empty but not valid.
     */
    INVALID_ENTRY_PATH(Severity.ERROR, 5010),

    /**
     * Indicates an entry property name is <code>null</code> or empty.
     */
    EMPTY_ENTRY_PROPERTY_NAME(Severity.ERROR, 5050),

    /**
     * Indicates an entry property name is not empty but not valid.
     */
    INVALID_ENTRY_PROPERTY_NAME(Severity.ERROR, 5060),

    /**
     * Indicates an entry property value is <code>null</code> or empty.
     */
    EMPTY_ENTRY_PROPERTY_VALUE(Severity.ERROR, 5070),

    /**
     * Indicates an entry property value is not empty but not valid.
     */
    INVALID_ENTRY_PROPERTY_VALUE(Severity.ERROR, 5080),

    //
    // Data Policy
    //

    /**
     * Indicates the data policy name is <code>null</code> or empty.
     */
    EMPTY_DATA_POLICY_NAME(Severity.ERROR, 6000),

    /**
     * Indicates the data policy name is not empty but not valid.
     */
    INVALID_DATA_POLICY_NAME(Severity.ERROR, 6010),

    /**
     * Indicates the data policy role name is not valid.
     */
    INVALID_DATA_POLICY_ROLE_NAME(Severity.ERROR, 6020),

    /**
     * Indicates the data policy does not have any permissions.
     */
    NO_DATA_POLICY_PERMISSIONS(Severity.ERROR, 6030),

    //
    // VDB
    //

    /**
     * Indicates the VDB name is <code>null</code> or empty.
     */
    EMPTY_VDB_NAME(Severity.ERROR, 7000),

    /**
     * Indicates the VDB name is not empty but not valid.
     */
    INVALID_VDB_NAME(Severity.ERROR, 7010),

    /**
     * Indicates the VDB version is not valid.
     */
    INVALID_VDB_VERSION(Severity.ERROR, 7020),

    //
    // Permission
    //

    /**
     * Indicates the data permission resource name is <code>null</code> or empty.
     */
    EMPTY_PERMISSION_RESOURCE_NAME(Severity.ERROR, 8000),

    /**
     * Indicates the data permission resource name is not empty but not valid.
     */
    INVALID_PERMISSION_RESOURCE_NAME(Severity.ERROR, 8010);
    // @formatter:on

    final Severity severity;
    final int code;

    private Error(final Severity severity,
                  final int code) {
        this.severity = severity;
        this.code = code;
    }

    /**
     * @return the validation status created from this error (never <code>null</code>)
     */
    public Status createStatus() {
        return new Status(this.severity, this.code);
    }

}