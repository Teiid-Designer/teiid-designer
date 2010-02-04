/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Model Accessibility</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelAccessibility()
 * @model
 * @generated
 */
public final class ModelAccessibility extends AbstractEnumerator {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The '<em><b>PUBLIC</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #PUBLIC_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int PUBLIC = 0;

    /**
     * The '<em><b>PROTECTED</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #PROTECTED_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int PROTECTED = 1;

    /**
     * The '<em><b>PRIVATE</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #PRIVATE_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int PRIVATE = 2;

    /**
     * The '<em><b>PUBLIC</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>PUBLIC</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #PUBLIC
     * @generated
     * @ordered
     */
	public static final ModelAccessibility PUBLIC_LITERAL = new ModelAccessibility(PUBLIC, "PUBLIC"); //$NON-NLS-1$

    /**
     * The '<em><b>PROTECTED</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>PROTECTED</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #PROTECTED
     * @generated
     * @ordered
     */
	public static final ModelAccessibility PROTECTED_LITERAL = new ModelAccessibility(PROTECTED, "PROTECTED"); //$NON-NLS-1$

    /**
     * The '<em><b>PRIVATE</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>PRIVATE</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #PRIVATE
     * @generated
     * @ordered
     */
	public static final ModelAccessibility PRIVATE_LITERAL = new ModelAccessibility(PRIVATE, "PRIVATE"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Model Accessibility</b></em>' enumerators.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private static final ModelAccessibility[] VALUES_ARRAY =
        new ModelAccessibility[] {
            PUBLIC_LITERAL,
            PROTECTED_LITERAL,
            PRIVATE_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Model Accessibility</b></em>' enumerators.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Model Accessibility</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static ModelAccessibility get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ModelAccessibility result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Model Accessibility</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static ModelAccessibility get(int value) {
        switch (value) {
            case PUBLIC: return PUBLIC_LITERAL;
            case PROTECTED: return PROTECTED_LITERAL;
            case PRIVATE: return PRIVATE_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private ModelAccessibility(int value, String name) {
        super(value, name);
    }

} //ModelAccessibility
