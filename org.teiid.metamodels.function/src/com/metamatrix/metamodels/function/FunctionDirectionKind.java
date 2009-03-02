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
 * A representation of the literals of the enumeration '<em><b>Direction Kind</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.function.FunctionPackage#getFunctionDirectionKind()
 * @model
 * @generated
 */
public final class FunctionDirectionKind extends AbstractEnumerator {
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
     * The '<em><b>RETURN</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RETURN_LITERAL
     * @model 
     * @generated
     * @ordered
     */
    public static final int RETURN = 1;

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
    public static final FunctionDirectionKind IN_LITERAL = new FunctionDirectionKind(IN, "IN"); //$NON-NLS-1$

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
    public static final FunctionDirectionKind RETURN_LITERAL = new FunctionDirectionKind(RETURN, "RETURN"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Direction Kind</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final FunctionDirectionKind[] VALUES_ARRAY =
        new FunctionDirectionKind[] {
            IN_LITERAL,
            RETURN_LITERAL,
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
    public static FunctionDirectionKind get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            FunctionDirectionKind result = VALUES_ARRAY[i];
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
    public static FunctionDirectionKind get(int value) {
        switch (value) {
            case IN: return IN_LITERAL;
            case RETURN: return RETURN_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private FunctionDirectionKind(int value, String name) {
        super(value, name);
    }

} //FunctionDirectionKind
