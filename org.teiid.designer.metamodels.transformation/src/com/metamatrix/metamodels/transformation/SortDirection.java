/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Sort Direction</b></em>', and utility
 * methods for working with them. <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSortDirection()
 * @model
 * @generated
 */
public final class SortDirection extends AbstractEnumerator {

    /**
     * The '<em><b>ASCENDING</b></em>' literal value. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ASCENDING</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #ASCENDING_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ASCENDING = 0;

    /**
     * The '<em><b>DESCENDING</b></em>' literal value. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>DESCENDING</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #DESCENDING_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int DESCENDING = 1;

    /**
     * The '<em><b>ASCENDING</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #ASCENDING
     * @generated
     * @ordered
     */
    public static final SortDirection ASCENDING_LITERAL = new SortDirection(ASCENDING, "ASCENDING"); //$NON-NLS-1$

    /**
     * The '<em><b>DESCENDING</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #DESCENDING
     * @generated
     * @ordered
     */
    public static final SortDirection DESCENDING_LITERAL = new SortDirection(DESCENDING, "DESCENDING"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Sort Direction</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private static final SortDirection[] VALUES_ARRAY = new SortDirection[] {ASCENDING_LITERAL, DESCENDING_LITERAL,};

    /**
     * A public read-only list of all the '<em><b>Sort Direction</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY)); // NO_UCD

    /**
     * Returns the '<em><b>Sort Direction</b></em>' literal with the specified name. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static SortDirection get( String name ) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            SortDirection result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Sort Direction</b></em>' literal with the specified value. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     */
    public static SortDirection get( int value ) { // NO_UCD
        switch (value) {
            case ASCENDING:
                return ASCENDING_LITERAL;
            case DESCENDING:
                return DESCENDING_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private SortDirection( int value,
                           String name ) {
        super(value, name);
    }

} // SortDirection
