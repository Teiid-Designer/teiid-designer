/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

/**
 * 
 */
public final class ModelType {

    /**
     * The '<em><b>PHYSICAL</b></em>' literal value.>
     */
    public static final int PHYSICAL = 0;

    /**
     * The '<em><b>VIRTUAL</b></em>' literal value.
     */
    public static final int VIRTUAL = 1;

    /**
     * The '<em><b>TYPE</b></em>' literal value.
     */
    public static final int TYPE = 2;

    /**
     * The '<em><b>VDB ARCHIVE</b></em>' literal value.
     */
    public static final int VDB_ARCHIVE = 3;

    /**
     * The '<em><b>UNKNOWN</b></em>' literal value.ed
     */
    public static final int UNKNOWN = 4;

    /**
     * The '<em><b>FUNCTION</b></em>' literal value.
     */
    public static final int FUNCTION = 5;

    /**
     * The '<em><b>CONFIGURATION</b></em>' literal value.
     */
    public static final int CONFIGURATION = 6;

    /**
     * The '<em><b>METAMODEL</b></em>' literal value.
     */
    public static final int METAMODEL = 7;

    /**
     * The '<em><b>EXTENSION</b></em>' literal value.d
     */
    public static final int EXTENSION = 8;

    /**
     * The '<em><b>LOGICAL</b></em>' literal value.
     */
    public static final int LOGICAL = 9;

    /**
     * The '<em><b>MATERIALIZATION</b></em>' literal value.
     */
    public static final int MATERIALIZATION = 10;

    public static final String[] MODEL_NAMES = {"PHYSICAL", //$NON-NLS-1$
        "VIRTUAL", //$NON-NLS-1$
        "TYPE", //$NON-NLS-1$
        "VDB_ARCHIVE", //$NON-NLS-1$
        "UNKNOWN", //$NON-NLS-1$
        "FUNCTION", //$NON-NLS-1$
        "CONFIGURATION", //$NON-NLS-1$
        "METAMODEL", //$NON-NLS-1$
        "EXTENSION", //$NON-NLS-1$
        "LOGICAL", //$NON-NLS-1$
        "MATERIALIZATION" //$NON-NLS-1$
    };

    public static int parseString( String s ) {
        for (int i = PHYSICAL; i <= MATERIALIZATION; i++) {
            if (MODEL_NAMES[i].equals(s)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unknown model type"); //$NON-NLS-1$
    }

    public static String getString( int modelType ) {
        if (modelType < 0 || modelType >= MODEL_NAMES.length) {
            throw new IllegalArgumentException("Unknown model type"); //$NON-NLS-1$
        }
        return MODEL_NAMES[modelType];
    }
}
