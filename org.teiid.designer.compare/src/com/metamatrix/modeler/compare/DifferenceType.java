/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Difference Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.modeler.compare.ComparePackage#getDifferenceType()
 * @model
 * @generated
 */
public final class DifferenceType extends AbstractEnumerator {
    /**
     * The '<em><b>NO CHANGE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NO_CHANGE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int NO_CHANGE = 0;

    /**
     * The '<em><b>ADDITION</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ADDITION_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ADDITION = 1;

    /**
     * The '<em><b>DELETION</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DELETION_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int DELETION = 2;

    /**
     * The '<em><b>CHANGE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CHANGE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int CHANGE = 3;

    /**
     * The '<em><b>CHANGE BELOW</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CHANGE_BELOW_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int CHANGE_BELOW = 4;

    /**
     * The '<em><b>NO CHANGE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NO CHANGE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NO_CHANGE
     * @generated
     * @ordered
     */
    public static final DifferenceType NO_CHANGE_LITERAL = new DifferenceType(NO_CHANGE, "NO_CHANGE"); //$NON-NLS-1$

    /**
     * The '<em><b>ADDITION</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ADDITION</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ADDITION
     * @generated
     * @ordered
     */
    public static final DifferenceType ADDITION_LITERAL = new DifferenceType(ADDITION, "ADDITION"); //$NON-NLS-1$

    /**
     * The '<em><b>DELETION</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DELETION</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DELETION
     * @generated
     * @ordered
     */
    public static final DifferenceType DELETION_LITERAL = new DifferenceType(DELETION, "DELETION"); //$NON-NLS-1$

    /**
     * The '<em><b>CHANGE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CHANGE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CHANGE
     * @generated
     * @ordered
     */
    public static final DifferenceType CHANGE_LITERAL = new DifferenceType(CHANGE, "CHANGE"); //$NON-NLS-1$

    /**
     * The '<em><b>CHANGE BELOW</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>CHANGE BELOW</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CHANGE_BELOW
     * @generated
     * @ordered
     */
    public static final DifferenceType CHANGE_BELOW_LITERAL = new DifferenceType(CHANGE_BELOW, "CHANGE_BELOW"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Difference Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final DifferenceType[] VALUES_ARRAY =
        new DifferenceType[] {
            NO_CHANGE_LITERAL,
            ADDITION_LITERAL,
            DELETION_LITERAL,
            CHANGE_LITERAL,
            CHANGE_BELOW_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Difference Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Difference Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static DifferenceType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            DifferenceType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Difference Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static DifferenceType get(int value) {
        switch (value) {
            case NO_CHANGE: return NO_CHANGE_LITERAL;
            case ADDITION: return ADDITION_LITERAL;
            case DELETION: return DELETION_LITERAL;
            case CHANGE: return CHANGE_LITERAL;
            case CHANGE_BELOW: return CHANGE_BELOW_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private DifferenceType(int value, String name) {
        super(value, name);
    }

} //DifferenceType
