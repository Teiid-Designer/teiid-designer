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
