/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Direction Kind</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getDirectionKind()
 * @model
 * @generated
 */
public final class DirectionKind extends AbstractEnumerator {
    /**
     * The '<em><b>IN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #IN_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int IN = 0;

    /**
     * The '<em><b>OUT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #OUT_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int OUT = 1;

    /**
     * The '<em><b>INOUT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INOUT_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int INOUT = 2;

    /**
     * The '<em><b>RETURN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RETURN_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int RETURN = 3;

    /**
     * The '<em><b>UNKNOWN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNKNOWN_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int UNKNOWN = 4;

    /**
     * The '<em><b>IN</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>IN</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #IN
     * @generated
     * @ordered
     */
    public static final DirectionKind IN_LITERAL = new DirectionKind(IN, "IN"); //$NON-NLS-1$

    /**
     * The '<em><b>OUT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>OUT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #OUT
     * @generated
     * @ordered
     */
    public static final DirectionKind OUT_LITERAL = new DirectionKind(OUT, "OUT"); //$NON-NLS-1$

    /**
     * The '<em><b>INOUT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INOUT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INOUT
     * @generated
     * @ordered
     */
    public static final DirectionKind INOUT_LITERAL = new DirectionKind(INOUT, "INOUT"); //$NON-NLS-1$

    /**
     * The '<em><b>RETURN</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>RETURN</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #RETURN
     * @generated
     * @ordered
     */
    public static final DirectionKind RETURN_LITERAL = new DirectionKind(RETURN, "RETURN"); //$NON-NLS-1$

    /**
     * The '<em><b>UNKNOWN</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>UNKNOWN</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #UNKNOWN
     * @generated
     * @ordered
     */
    public static final DirectionKind UNKNOWN_LITERAL = new DirectionKind(UNKNOWN, "UNKNOWN"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Direction Kind</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final DirectionKind[] VALUES_ARRAY =
        new DirectionKind[] {
            IN_LITERAL,
            OUT_LITERAL,
            INOUT_LITERAL,
            RETURN_LITERAL,
            UNKNOWN_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Direction Kind</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Direction Kind</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static DirectionKind get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            DirectionKind result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Direction Kind</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static DirectionKind get(int value) {
        switch (value) {
            case IN: return IN_LITERAL;
            case OUT: return OUT_LITERAL;
            case INOUT: return INOUT_LITERAL;
            case RETURN: return RETURN_LITERAL;
            case UNKNOWN: return UNKNOWN_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private DirectionKind(int value, String name) {
        super(value, name);
    }

} //DirectionKind
