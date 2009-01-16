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
