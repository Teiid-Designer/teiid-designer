/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Use Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapUseType()
 * @model
 * @generated
 */
public final class SoapUseType extends AbstractEnumerator {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The '<em><b>LITERAL</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #LITERAL_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int LITERAL = 0;

    /**
     * The '<em><b>ENCODED</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #ENCODED_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int ENCODED = 1;

    /**
     * The '<em><b>LITERAL</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>LITERAL</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #LITERAL
     * @generated
     * @ordered
     */
	public static final SoapUseType LITERAL_LITERAL = new SoapUseType(LITERAL, "LITERAL"); //$NON-NLS-1$

    /**
     * The '<em><b>ENCODED</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>ENCODED</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #ENCODED
     * @generated
     * @ordered
     */
	public static final SoapUseType ENCODED_LITERAL = new SoapUseType(ENCODED, "ENCODED"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Use Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private static final SoapUseType[] VALUES_ARRAY =
        new SoapUseType[] {
            LITERAL_LITERAL,
            ENCODED_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Use Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Use Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static SoapUseType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            SoapUseType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Use Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static SoapUseType get(int value) {
        switch (value) {
            case LITERAL: return LITERAL_LITERAL;
            case ENCODED: return ENCODED_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private SoapUseType(int value, String name) {
        super(value, name);
    }

} //SoapUseType
