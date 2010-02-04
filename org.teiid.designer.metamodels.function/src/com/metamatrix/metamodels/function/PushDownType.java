/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Push Down Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.function.FunctionPackage#getPushDownType()
 * @model
 * @generated
 */
public final class PushDownType extends AbstractEnumerator {
    /**
     * The '<em><b>REQUIRED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #REQUIRED_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int REQUIRED = 0;

    /**
     * The '<em><b>ALLOWED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ALLOWED_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ALLOWED = 1;

    /**
     * The '<em><b>NOT ALLOWED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NOT_ALLOWED_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int NOT_ALLOWED = 2;

    /**
     * The '<em><b>REQUIRED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>REQUIRED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #REQUIRED
     * @generated
     * @ordered
     */
    public static final PushDownType REQUIRED_LITERAL = new PushDownType(REQUIRED, "REQUIRED"); //$NON-NLS-1$

    /**
     * The '<em><b>ALLOWED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ALLOWED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ALLOWED
     * @generated
     * @ordered
     */
    public static final PushDownType ALLOWED_LITERAL = new PushDownType(ALLOWED, "ALLOWED"); //$NON-NLS-1$

    /**
     * The '<em><b>NOT ALLOWED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NOT ALLOWED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NOT_ALLOWED
     * @generated
     * @ordered
     */
    public static final PushDownType NOT_ALLOWED_LITERAL = new PushDownType(NOT_ALLOWED, "NOT_ALLOWED"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Push Down Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final PushDownType[] VALUES_ARRAY =
        new PushDownType[] {
            REQUIRED_LITERAL,
            ALLOWED_LITERAL,
            NOT_ALLOWED_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Push Down Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Push Down Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static PushDownType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            PushDownType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Push Down Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static PushDownType get(int value) {
        switch (value) {
            case REQUIRED: return REQUIRED_LITERAL;
            case ALLOWED: return ALLOWED_LITERAL;
            case NOT_ALLOWED: return NOT_ALLOWED_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private PushDownType(int value, String name) {
        super(value, name);
    }

} //PushDownType
