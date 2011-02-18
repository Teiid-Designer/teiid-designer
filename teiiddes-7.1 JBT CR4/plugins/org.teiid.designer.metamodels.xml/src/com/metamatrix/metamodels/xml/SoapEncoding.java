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
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Soap Encoding</b></em>', and utility
 * methods for working with them. <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getSoapEncoding()
 * @model
 * @generated
 */
public final class SoapEncoding extends AbstractEnumerator {
    /**
     * The '<em><b>NONE</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #NONE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int NONE = 0;

    /**
     * The '<em><b>DEFAULT</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #DEFAULT_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int DEFAULT = 1;

    /**
     * The '<em><b>NONE</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NONE</b></em>' literal object isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #NONE
     * @generated
     * @ordered
     */
    public static final SoapEncoding NONE_LITERAL = new SoapEncoding(NONE, "NONE"); //$NON-NLS-1$

    /**
     * The '<em><b>DEFAULT</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DEFAULT</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #DEFAULT
     * @generated
     * @ordered
     */
    public static final SoapEncoding DEFAULT_LITERAL = new SoapEncoding(DEFAULT, "DEFAULT"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Soap Encoding</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private static final SoapEncoding[] VALUES_ARRAY = new SoapEncoding[] {NONE_LITERAL, DEFAULT_LITERAL,};

    /**
     * A public read-only list of all the '<em><b>Soap Encoding</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY)); // NO_UCD

    /**
     * Returns the '<em><b>Soap Encoding</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static SoapEncoding get( String name ) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            SoapEncoding result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Soap Encoding</b></em>' literal with the specified value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static SoapEncoding get( int value ) { // NO_UCD
        switch (value) {
            case NONE:
                return NONE_LITERAL;
            case DEFAULT:
                return DEFAULT_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private SoapEncoding( int value,
                          String name ) {
        super(value, name);
    }

} // SoapEncoding
