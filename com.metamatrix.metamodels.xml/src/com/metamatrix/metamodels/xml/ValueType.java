/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Value Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getValueType()
 * @model
 * @generated
 */
public final class ValueType extends AbstractEnumerator {
    /**
     * The '<em><b>IGNORED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #IGNORED_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int IGNORED = 0;

    /**
     * The '<em><b>DEFAULT</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DEFAULT_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int DEFAULT = 1;

    /**
     * The '<em><b>FIXED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FIXED_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int FIXED = 2;

    /**
     * The '<em><b>IGNORED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>IGNORED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #IGNORED
     * @generated
     * @ordered
     */
    public static final ValueType IGNORED_LITERAL = new ValueType(IGNORED, "IGNORED"); //$NON-NLS-1$

    /**
     * The '<em><b>DEFAULT</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DEFAULT</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DEFAULT
     * @generated
     * @ordered
     */
    public static final ValueType DEFAULT_LITERAL = new ValueType(DEFAULT, "DEFAULT"); //$NON-NLS-1$

    /**
     * The '<em><b>FIXED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>FIXED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #FIXED
     * @generated
     * @ordered
     */
    public static final ValueType FIXED_LITERAL = new ValueType(FIXED, "FIXED"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Value Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final ValueType[] VALUES_ARRAY =
        new ValueType[] {
            IGNORED_LITERAL,
            DEFAULT_LITERAL,
            FIXED_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Value Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Value Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ValueType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ValueType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Value Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ValueType get(int value) {
        switch (value) {
            case IGNORED: return IGNORED_LITERAL;
            case DEFAULT: return DEFAULT_LITERAL;
            case FIXED: return FIXED_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private ValueType(int value, String name) {
        super(value, name);
    }

} //ValueType
