/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Case Conversion</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.modeler.jdbc.JdbcPackage#getCaseConversion()
 * @model
 * @generated
 */
public final class CaseConversion extends AbstractEnumerator {
    /**
     * The '<em><b>NONE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NONE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int NONE = 0;

    /**
     * The '<em><b>TO UPPERCASE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #TO_UPPERCASE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int TO_UPPERCASE = 1;

    /**
     * The '<em><b>TO LOWERCASE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #TO_LOWERCASE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int TO_LOWERCASE = 2;

    /**
     * The '<em><b>NONE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NONE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NONE
     * @generated
     * @ordered
     */
    public static final CaseConversion NONE_LITERAL = new CaseConversion(NONE, "NONE"); //$NON-NLS-1$

    /**
     * The '<em><b>TO UPPERCASE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>TO UPPERCASE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #TO_UPPERCASE
     * @generated
     * @ordered
     */
    public static final CaseConversion TO_UPPERCASE_LITERAL = new CaseConversion(TO_UPPERCASE, "TO_UPPERCASE"); //$NON-NLS-1$

    /**
     * The '<em><b>TO LOWERCASE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>TO LOWERCASE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #TO_LOWERCASE
     * @generated
     * @ordered
     */
    public static final CaseConversion TO_LOWERCASE_LITERAL = new CaseConversion(TO_LOWERCASE, "TO_LOWERCASE"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Case Conversion</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final CaseConversion[] VALUES_ARRAY =
        new CaseConversion[] {
            NONE_LITERAL,
            TO_UPPERCASE_LITERAL,
            TO_LOWERCASE_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Case Conversion</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Case Conversion</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static CaseConversion getGen(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            CaseConversion result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Case Conversion</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public static CaseConversion get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            CaseConversion result = VALUES_ARRAY[i];
            if (result.toString().equalsIgnoreCase(name)) {
                return result;
            }
        }
        return null;
    }    
    
    /**
     * Returns the '<em><b>Case Conversion</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static CaseConversion get(int value) {
        switch (value) {
            case NONE: return NONE_LITERAL;
            case TO_UPPERCASE: return TO_UPPERCASE_LITERAL;
            case TO_LOWERCASE: return TO_LOWERCASE_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private CaseConversion(int value, String name) {
        super(value, name);
    }

} //CaseConversion
