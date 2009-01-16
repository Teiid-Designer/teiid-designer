/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.relational;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Nullable Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getNullableType()
 * @model
 * @generated
 */
public final class NullableType extends AbstractEnumerator {
    /**
     * The '<em><b>NO NULLS</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NO_NULLS_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int NO_NULLS = 0;

    /**
     * The '<em><b>NULLABLE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NULLABLE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int NULLABLE = 1;

    /**
     * The '<em><b>NULLABLE UNKNOWN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NULLABLE_UNKNOWN_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int NULLABLE_UNKNOWN = 2;

    /**
     * The '<em><b>NO NULLS</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NO NULLS</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NO_NULLS
     * @generated
     * @ordered
     */
    public static final NullableType NO_NULLS_LITERAL = new NullableType(NO_NULLS, "NO_NULLS"); //$NON-NLS-1$

    /**
     * The '<em><b>NULLABLE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NULLABLE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NULLABLE
     * @generated
     * @ordered
     */
    public static final NullableType NULLABLE_LITERAL = new NullableType(NULLABLE, "NULLABLE"); //$NON-NLS-1$

    /**
     * The '<em><b>NULLABLE UNKNOWN</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NULLABLE UNKNOWN</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NULLABLE_UNKNOWN
     * @generated
     * @ordered
     */
    public static final NullableType NULLABLE_UNKNOWN_LITERAL = new NullableType(NULLABLE_UNKNOWN, "NULLABLE_UNKNOWN"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Nullable Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final NullableType[] VALUES_ARRAY =
        new NullableType[] {
            NO_NULLS_LITERAL,
            NULLABLE_LITERAL,
            NULLABLE_UNKNOWN_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Nullable Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Nullable Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static NullableType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            NullableType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Nullable Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static NullableType get(int value) {
        switch (value) {
            case NO_NULLS: return NO_NULLS_LITERAL;
            case NULLABLE: return NULLABLE_LITERAL;
            case NULLABLE_UNKNOWN: return NULLABLE_UNKNOWN_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private NullableType(int value, String name) {
        super(value, name);
    }

} //NullableType
