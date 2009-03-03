/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Type Status</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * <!-- begin-model-doc -->
 * The status of a relationship type defines whether it is valid for instances to exist.  Example values include "Prototype" (i.e., "use with care"), "Standard", "Deprecated" (i.e., "don't use anymore, but some may exist"), or "Invalid" (i.e., "should not be used anymore").
 * <!-- end-model-doc -->
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getRelationshipTypeStatus()
 * @model
 * @generated
 */
public final class RelationshipTypeStatus extends AbstractEnumerator {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The '<em><b>PROTOTYPE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #PROTOTYPE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int PROTOTYPE = 0;

    /**
     * The '<em><b>STANDARD</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #STANDARD_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int STANDARD = 1;

    /**
     * The '<em><b>DEPRECATED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #DEPRECATED_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int DEPRECATED = 2;

    /**
     * The '<em><b>INVALID</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INVALID_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int INVALID = 3;

    /**
     * The '<em><b>PROTOTYPE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>PROTOTYPE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #PROTOTYPE
     * @generated
     * @ordered
     */
    public static final RelationshipTypeStatus PROTOTYPE_LITERAL = new RelationshipTypeStatus(PROTOTYPE, "PROTOTYPE"); //$NON-NLS-1$

    /**
     * The '<em><b>STANDARD</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>STANDARD</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #STANDARD
     * @generated
     * @ordered
     */
    public static final RelationshipTypeStatus STANDARD_LITERAL = new RelationshipTypeStatus(STANDARD, "STANDARD"); //$NON-NLS-1$

    /**
     * The '<em><b>DEPRECATED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DEPRECATED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #DEPRECATED
     * @generated
     * @ordered
     */
    public static final RelationshipTypeStatus DEPRECATED_LITERAL = new RelationshipTypeStatus(DEPRECATED, "DEPRECATED"); //$NON-NLS-1$

    /**
     * The '<em><b>INVALID</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INVALID</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INVALID
     * @generated
     * @ordered
     */
    public static final RelationshipTypeStatus INVALID_LITERAL = new RelationshipTypeStatus(INVALID, "INVALID"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Type Status</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final RelationshipTypeStatus[] VALUES_ARRAY =
        new RelationshipTypeStatus[] {
            PROTOTYPE_LITERAL,
            STANDARD_LITERAL,
            DEPRECATED_LITERAL,
            INVALID_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Type Status</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Type Status</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static RelationshipTypeStatus get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            RelationshipTypeStatus result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Type Status</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static RelationshipTypeStatus get(int value) {
        switch (value) {
            case PROTOTYPE: return PROTOTYPE_LITERAL;
            case STANDARD: return STANDARD_LITERAL;
            case DEPRECATED: return DEPRECATED_LITERAL;
            case INVALID: return INVALID_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private RelationshipTypeStatus(int value, String name) {
        super(value, name);
    }

} //RelationshipTypeStatus
