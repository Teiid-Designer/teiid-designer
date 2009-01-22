/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Join Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getJoinType()
 * @model
 * @generated
 */
public final class JoinType extends AbstractEnumerator {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The '<em><b>INNER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INNER</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INNER_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int INNER = 0;

    /**
     * The '<em><b>LEFT OUTER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>LEFT OUTER</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #LEFT_OUTER_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int LEFT_OUTER = 1;

    /**
     * The '<em><b>RIGHT OUTER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>RIGHT OUTER</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #RIGHT_OUTER_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int RIGHT_OUTER = 2;

    /**
     * The '<em><b>FULL OUTER</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>FULL OUTER</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #FULL_OUTER_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int FULL_OUTER = 3;

    /**
     * The '<em><b>CROSS</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CROSS</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CROSS_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int CROSS = 4;

    /**
     * The '<em><b>INNER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INNER
     * @generated
     * @ordered
     */
    public static final JoinType INNER_LITERAL = new JoinType(INNER, "INNER"); //$NON-NLS-1$

    /**
     * The '<em><b>LEFT OUTER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #LEFT_OUTER
     * @generated
     * @ordered
     */
    public static final JoinType LEFT_OUTER_LITERAL = new JoinType(LEFT_OUTER, "LEFT_OUTER"); //$NON-NLS-1$

    /**
     * The '<em><b>RIGHT OUTER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RIGHT_OUTER
     * @generated
     * @ordered
     */
    public static final JoinType RIGHT_OUTER_LITERAL = new JoinType(RIGHT_OUTER, "RIGHT_OUTER"); //$NON-NLS-1$

    /**
     * The '<em><b>FULL OUTER</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FULL_OUTER
     * @generated
     * @ordered
     */
    public static final JoinType FULL_OUTER_LITERAL = new JoinType(FULL_OUTER, "FULL_OUTER"); //$NON-NLS-1$

    /**
     * The '<em><b>CROSS</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CROSS
     * @generated
     * @ordered
     */
    public static final JoinType CROSS_LITERAL = new JoinType(CROSS, "CROSS"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Join Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final JoinType[] VALUES_ARRAY =
        new JoinType[] {
            INNER_LITERAL,
            LEFT_OUTER_LITERAL,
            RIGHT_OUTER_LITERAL,
            FULL_OUTER_LITERAL,
            CROSS_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Join Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Join Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static JoinType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            JoinType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Join Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static JoinType get(int value) {
        switch (value) {
            case INNER: return INNER_LITERAL;
            case LEFT_OUTER: return LEFT_OUTER_LITERAL;
            case RIGHT_OUTER: return RIGHT_OUTER_LITERAL;
            case FULL_OUTER: return FULL_OUTER_LITERAL;
            case CROSS: return CROSS_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private JoinType(int value, String name) {
        super(value, name);
    }

} //JoinType
