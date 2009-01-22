/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Severity</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getSeverity()
 * @model
 * @generated
 */
public final class Severity extends AbstractEnumerator {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The '<em><b>OK</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OK_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int OK = 0;

    /**
     * The '<em><b>INFO</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INFO_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int INFO = 1;

    /**
     * The '<em><b>WARNING</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #WARNING_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int WARNING = 2;

    /**
     * The '<em><b>ERROR</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ERROR_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ERROR = 3;

    /**
     * The '<em><b>OK</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>OK</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #OK
     * @generated
     * @ordered
     */
    public static final Severity OK_LITERAL = new Severity(OK, "OK"); //$NON-NLS-1$

    /**
     * The '<em><b>INFO</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INFO</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INFO
     * @generated
     * @ordered
     */
    public static final Severity INFO_LITERAL = new Severity(INFO, "INFO"); //$NON-NLS-1$

    /**
     * The '<em><b>WARNING</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>WARNING</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #WARNING
     * @generated
     * @ordered
     */
    public static final Severity WARNING_LITERAL = new Severity(WARNING, "WARNING"); //$NON-NLS-1$

    /**
     * The '<em><b>ERROR</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ERROR</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ERROR
     * @generated
     * @ordered
     */
    public static final Severity ERROR_LITERAL = new Severity(ERROR, "ERROR"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Severity</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final Severity[] VALUES_ARRAY =
        new Severity[] {
            OK_LITERAL,
            INFO_LITERAL,
            WARNING_LITERAL,
            ERROR_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Severity</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Severity</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Severity get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            Severity result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Severity</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static Severity get(int value) {
        switch (value) {
            case OK: return OK_LITERAL;
            case INFO: return INFO_LITERAL;
            case WARNING: return WARNING_LITERAL;
            case ERROR: return ERROR_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private Severity(int value, String name) {
        super(value, name);
    }

} //Severity
