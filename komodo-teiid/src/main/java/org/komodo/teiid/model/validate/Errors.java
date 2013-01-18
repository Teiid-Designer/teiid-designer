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
 * Teiid model object validation errors.
 */
public interface Errors {
    // @formatter:off

    //
    // Translator
    //

    /**
     * Indicates the translator name is <code>null</code> or empty.
     */
    Status EMPTY_TRANSLATOR_NAME = new Status(Severity.ERROR, 1000);

    /**
     * Indicates the translator name is not empty but not valid.
     */
    Status INVALID_TRANSLATOR_NAME = new Status(Severity.ERROR, 1010);

    /**
     * Indicates the translator type is <code>null</code> or empty.
     */
    Status EMPTY_TRANSLATOR_TYPE = new Status(Severity.ERROR, 1020);

    /**
     * Indicates the translator type is not empty but not valid.
     */
    Status INVALID_TRANSLATOR_TYPE = new Status(Severity.ERROR, 1030);

    /**
     * Indicates the translator has no properties.
     */
    Status NO_TRANSLATOR_PROPERTIES = new Status(Severity.ERROR, 1040);

    /**
     * Indicates a translator property name is <code>null</code> or empty.
     */
    Status EMPTY_TRANSLATOR_PROPERTY_NAME = new Status(Severity.ERROR, 1050);

    /**
     * Indicates a translator property name is not empty but not valid.
     */
    Status INVALID_TRANSLATOR_PROPERTY_NAME = new Status(Severity.ERROR, 1060);

    /**
     * Indicates a translator property value is <code>null</code> or empty.
     */
    Status EMPTY_TRANSLATOR_PROPERTY_VALUE = new Status(Severity.ERROR, 1070);

    /**
     * Indicates a translator property value is not empty but not valid.
     */
    Status INVALID_TRANSLATOR_PROPERTY_VALUE = new Status(Severity.ERROR, 1080);

    //
    // Source
    //

    /**
     * Indicates the source name is <code>null</code> or empty.
     */
    Status EMPTY_SOURCE_NAME = new Status(Severity.ERROR, 2000);

    /**
     * Indicates the source name is not empty but not valid.
     */
    Status INVALID_SOURCE_NAME = new Status(Severity.ERROR, 2010);

    /**
     * Indicates the source's translator name is <code>null</code> or empty.
     */
    Status EMPTY_SOURCE_TRANSLATOR_NAME = new Status(Severity.ERROR, 2020);

    /**
     * Indicates the source's translator name is not empty but not valid.
     */
    Status INVALID_SOURCE_TRANSLATOR_NAME = new Status(Severity.ERROR, 2030);

    /**
     * Indicates the source's JNDI name is not empty but not valid.
     */
    Status INVALID_SOURCE_JNDI_NAME = new Status(Severity.ERROR, 2040);

    //
    // Import VDB
    //

    /**
     * Indicates the VDB import name is <code>null</code> or empty.
     */
    Status EMPTY_IMPORT_VDB_NAME = new Status(Severity.ERROR, 3000);

    /**
     * Indicates the VDB name is not empty but not valid.
     */
    Status INVALID_IMPORT_VDB_NAME = new Status(Severity.ERROR, 3010);

    /**
     * Indicates the VDB import version is not valid.
     */
    Status INVALID_IMPORT_VDB_VERSION = new Status(Severity.ERROR, 3020);

    //
    // Schema
    //

    /**
     * Indicates the schema's source name is <code>null</code> or empty.
     */
    Status EMPTY_SCHEMA_SOURCE_NAME = new Status(Severity.ERROR, 4000);

    /**
     * Indicates the schema's source name is not empty but not valid.
     */
    Status INVALID_SCHEMA_SOURCE_NAME = new Status(Severity.ERROR, 4010);

    /**
     * Indicates the schema's source name is <code>null</code> or empty.
     */
    Status EMPTY_SCHEMA_SOURCE_TRANSLATOR_NAME = new Status(Severity.ERROR, 4020);

    /**
     * Indicates the schema's source name is not empty but not valid.
     */
    Status INVALID_SCHEMA_SOURCE_TRANSLATOR_NAME = new Status(Severity.ERROR, 4030);

    /**
     * Indicates the schema's metadata is not valid.
     */
    Status INVALID_SCHEMA_METADATA = new Status(Severity.ERROR, 4040);

    /**
     * Indicates the schema's metadata type is <code>null</code> or empty.
     */
    Status EMPTY_SCHEMA_METADATA_TYPE = new Status(Severity.ERROR, 4050);

    /**
     * Indicates the schema's metadata type not empty but not valid.
     */
    Status INVALID_SCHEMA_METADATA_TYPE = new Status(Severity.ERROR, 4060);

    /**
     * Indicates a schema property name is <code>null</code> or empty.
     */
    Status EMPTY_SCHEMA_PROPERTY_NAME = new Status(Severity.ERROR, 4070);

    /**
     * Indicates a schema property name is not empty but not valid.
     */
    Status INVALID_SCHEMA_PROPERTY_NAME = new Status(Severity.ERROR, 4080);

    /**
     * Indicates a schema property value is <code>null</code> or empty.
     */
    Status EMPTY_SCHEMA_PROPERTY_VALUE = new Status(Severity.ERROR, 4090);

    /**
     * Indicates a schema property value is not empty but not valid.
     */
    Status INVALID_SCHEMA_PROPERTY_VALUE = new Status(Severity.ERROR, 4100);

    /**
     * Indicates the schema name is <code>null</code> or empty.
     */
    Status EMPTY_SCHEMA_NAME = new Status(Severity.ERROR, 4110);

    /**
     * Indicates the schema name is not empty but not valid.
     */
    Status INVALID_SCHEMA_NAME = new Status(Severity.ERROR, 4120);

    /**
     * Indicates the schema type is <code>null</code> or empty.
     */
    Status EMPTY_SCHEMA_TYPE = new Status(Severity.ERROR, 4130);

    /**
     * Indicates the schema type is not empty but not valid.
     */
    Status INVALID_SCHEMA_TYPE = new Status(Severity.ERROR, 4140);

    //
    // Entry
    //

    /**
     * Indicates the entry path is <code>null</code> or empty.
     */
    Status EMPTY_ENTRY_PATH = new Status(Severity.ERROR, 5000);

    /**
     * Indicates the entry path is not empty but not valid.
     */
    Status INVALID_ENTRY_PATH = new Status(Severity.ERROR, 5010);

    /**
     * Indicates an entry property name is <code>null</code> or empty.
     */
    Status EMPTY_ENTRY_PROPERTY_NAME = new Status(Severity.ERROR, 5050);

    /**
     * Indicates an entry property name is not empty but not valid.
     */
    Status INVALID_ENTRY_PROPERTY_NAME = new Status(Severity.ERROR, 5060);

    /**
     * Indicates an entry property value is <code>null</code> or empty.
     */
    Status EMPTY_ENTRY_PROPERTY_VALUE = new Status(Severity.ERROR, 5070);

    /**
     * Indicates an entry property value is not empty but not valid.
     */
    Status INVALID_ENTRY_PROPERTY_VALUE = new Status(Severity.ERROR, 5080);

    //
    // Data Policy
    //

    /**
     * Indicates the data policy name is <code>null</code> or empty.
     */
    Status EMPTY_DATA_POLICY_NAME = new Status(Severity.ERROR, 6000);

    /**
     * Indicates the data policy name is not empty but not valid.
     */
    Status INVALID_DATA_POLICY_NAME = new Status(Severity.ERROR, 6010);

    /**
     * Indicates the data policy does not have any permissions.
     */
    Status NO_DATA_POLICY_PERMISSIONS = new Status(Severity.ERROR, 6020);

    // @formatter:on
}
