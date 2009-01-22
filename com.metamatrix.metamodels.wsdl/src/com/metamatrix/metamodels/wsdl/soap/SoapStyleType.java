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
 * A representation of the literals of the enumeration '<em><b>Style Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapStyleType()
 * @model
 * @generated
 */
public final class SoapStyleType extends AbstractEnumerator {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The '<em><b>RPC</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #RPC_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int RPC = 0;

    /**
     * The '<em><b>DOCUMENT</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #DOCUMENT_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int DOCUMENT = 1;

    /**
     * The '<em><b>RPC</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>RPC</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #RPC
     * @generated
     * @ordered
     */
	public static final SoapStyleType RPC_LITERAL = new SoapStyleType(RPC, "RPC"); //$NON-NLS-1$

    /**
     * The '<em><b>DOCUMENT</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>DOCUMENT</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #DOCUMENT
     * @generated
     * @ordered
     */
	public static final SoapStyleType DOCUMENT_LITERAL = new SoapStyleType(DOCUMENT, "DOCUMENT"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Style Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private static final SoapStyleType[] VALUES_ARRAY =
        new SoapStyleType[] {
            RPC_LITERAL,
            DOCUMENT_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Style Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Style Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static SoapStyleType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            SoapStyleType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Style Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static SoapStyleType get(int value) {
        switch (value) {
            case RPC: return RPC_LITERAL;
            case DOCUMENT: return DOCUMENT_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private SoapStyleType(int value, String name) {
        super(value, name);
    }

} //SoapStyleType
