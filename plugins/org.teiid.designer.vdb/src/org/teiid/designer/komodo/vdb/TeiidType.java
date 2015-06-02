/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb;

import java.util.ArrayList;
import java.util.List;

/**
 * @author blafond
 *
 */
public enum TeiidType {

    /**
     * Schema
     */
    SCHEMA,

    /**
     * Teiid
     */
    TEIID,

    /**
     * Vdb
     */
    VDB,

    /**
     * Vdb Entry
     */
    VDB_ENTRY,

    /**
     * Vdb Import
     */
    VDB_IMPORT,
    
    /**
     * Vdb Model Source
     */
    VDB_MODEL_METADATA,

    /**
     * Vdb Model Source
     */
    VDB_MODEL_SOURCE,

    /**
     * Vdb Translator
     */
    VDB_TRANSLATOR,

    /**
     * Vdb Condition
     */
    VDB_CONDITION,

    /**
     * Vdb Data Role
     */
    VDB_DATA_ROLE,

    /**
     * Vdb Make
     */
    VDB_MASK,

    /**
     * Vdb Permission
     */
    VDB_PERMISSION,

    /**
     * Access Pattern
     */
    ACCESS_PATTERN,

    /**
     * Column
     */
    COLUMN,

    /**
     * Foreign Key
     */
    FOREIGN_KEY,

    /**
     * Pushdown Function
     */
    PUSHDOWN_FUNCTION,

    /**
     * UDF
     */
    USER_DEFINED_FUNCTION,

    /**
     * Index
     */
    INDEX,

    /**
     * Model
     */
    MODEL,

    /**
     * Paremeter
     */
    PARAMETER,

    /**
     * Primary Key
     */
    PRIMARY_KEY,

    /**
     * Stored Procedure
     */
    STORED_PROCEDURE,

    /**
     * Virtual Procedure
     */
    VIRTUAL_PROCEDURE,

    /**
     * Data type Result Set
     */
    DATA_TYPE_RESULT_SET,

    /**
     * Tabular Result Set Column
     */
    RESULT_SET_COLUMN,

    /**
     * Tabular Result Set
     */
    TABULAR_RESULT_SET,

    /**
     * Statement Option
     */
    STATEMENT_OPTION,

    /**
     * Table
     */
    TABLE,

    /**
     * Unique Constraint
     */
    UNIQUE_CONSTRAINT,

    /**
     * View
     */
    VIEW,

    /**
     * Unknown Type
     */
    UNKNOWN;

    /**
     * @return actual type
     */
    public String getType() {
        StringBuffer sb = new StringBuffer();
        for (String s : name().split("_")) {
            sb.append(Character.toUpperCase(s.charAt(0)));
            if (s.length() > 1) {
                sb.append(s.substring(1, s.length()).toLowerCase());
            }
        }

        return sb.toString();
    }

    /** (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return getType();
    }

    /**
     * @param kType the string definition of a type
     * @return the {@link TeiidType} of the given string definition
     */
    public static TeiidType getTeiidType(String kType) {
        if (kType == null)
            return TeiidType.UNKNOWN;

        for (TeiidType value : values()) {
            if (value.getType().equalsIgnoreCase(kType))
                return value;
        }

        return TeiidType.UNKNOWN;
    }

    /**
     * @return all the string definitions of the types
     */
    public static List<String> getTypes() {
        List<String> names = new ArrayList<String>();
        for (TeiidType kType : values()) {
            names.add(kType.getType());
        }

        return names;
    }
}