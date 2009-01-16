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
 * A representation of the literals of the enumeration '<em><b>Multiplicity Kind</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getMultiplicityKind()
 * @model
 * @generated
 */
public final class MultiplicityKind extends AbstractEnumerator {
    /**
     * The '<em><b>ONE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ONE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ONE = 0;

    /**
     * The '<em><b>MANY</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #MANY_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int MANY = 1;

    /**
     * The '<em><b>ZERO TO ONE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ZERO_TO_ONE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ZERO_TO_ONE = 2;

    /**
     * The '<em><b>ZERO TO MANY</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ZERO_TO_MANY_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ZERO_TO_MANY = 3;

    /**
     * The '<em><b>UNSPECIFIED</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #UNSPECIFIED_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int UNSPECIFIED = 4;

    /**
     * The '<em><b>ONE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ONE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ONE
     * @generated
     * @ordered
     */
    public static final MultiplicityKind ONE_LITERAL = new MultiplicityKind(ONE, "ONE"); //$NON-NLS-1$

    /**
     * The '<em><b>MANY</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>MANY</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #MANY
     * @generated
     * @ordered
     */
    public static final MultiplicityKind MANY_LITERAL = new MultiplicityKind(MANY, "MANY"); //$NON-NLS-1$

    /**
     * The '<em><b>ZERO TO ONE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ZERO TO ONE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ZERO_TO_ONE
     * @generated
     * @ordered
     */
    public static final MultiplicityKind ZERO_TO_ONE_LITERAL = new MultiplicityKind(ZERO_TO_ONE, "ZERO_TO_ONE"); //$NON-NLS-1$

    /**
     * The '<em><b>ZERO TO MANY</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ZERO TO MANY</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ZERO_TO_MANY
     * @generated
     * @ordered
     */
    public static final MultiplicityKind ZERO_TO_MANY_LITERAL = new MultiplicityKind(ZERO_TO_MANY, "ZERO_TO_MANY"); //$NON-NLS-1$

    /**
     * The '<em><b>UNSPECIFIED</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>UNSPECIFIED</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #UNSPECIFIED
     * @generated
     * @ordered
     */
	public static final MultiplicityKind UNSPECIFIED_LITERAL = new MultiplicityKind(UNSPECIFIED, "UNSPECIFIED"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Multiplicity Kind</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final MultiplicityKind[] VALUES_ARRAY =
        new MultiplicityKind[] {
            ONE_LITERAL,
            MANY_LITERAL,
            ZERO_TO_ONE_LITERAL,
            ZERO_TO_MANY_LITERAL,
            UNSPECIFIED_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Multiplicity Kind</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Multiplicity Kind</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static MultiplicityKind get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            MultiplicityKind result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Multiplicity Kind</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static MultiplicityKind get(int value) {
        switch (value) {
            case ONE: return ONE_LITERAL;
            case MANY: return MANY_LITERAL;
            case ZERO_TO_ONE: return ZERO_TO_ONE_LITERAL;
            case ZERO_TO_MANY: return ZERO_TO_MANY_LITERAL;
            case UNSPECIFIED: return UNSPECIFIED_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private MultiplicityKind(int value, String name) {
        super(value, name);
    }

} //MultiplicityKind
