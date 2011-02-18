/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Source Names</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.modeler.jdbc.JdbcPackage#getSourceNames()
 * @model
 * @generated
 */
public final class SourceNames extends AbstractEnumerator {
    /**
     * The '<em><b>NONE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #NONE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int NONE = 0;

    /**
     * The '<em><b>UNQUALIFIED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNQUALIFIED_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int UNQUALIFIED = 1;

    /**
     * The '<em><b>FULLY QUALIFIED</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #FULLY_QUALIFIED_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int FULLY_QUALIFIED = 2;

    /**
     * The '<em><b>NONE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>NONE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #NONE
     * @generated
     * @ordered
     */
    public static final SourceNames NONE_LITERAL = new SourceNames(NONE, "NONE"); //$NON-NLS-1$

    /**
     * The '<em><b>UNQUALIFIED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>UNQUALIFIED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #UNQUALIFIED
     * @generated
     * @ordered
     */
    public static final SourceNames UNQUALIFIED_LITERAL = new SourceNames(UNQUALIFIED, "UNQUALIFIED"); //$NON-NLS-1$

    /**
     * The '<em><b>FULLY QUALIFIED</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>FULLY QUALIFIED</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #FULLY_QUALIFIED
     * @generated
     * @ordered
     */
    public static final SourceNames FULLY_QUALIFIED_LITERAL = new SourceNames(FULLY_QUALIFIED, "FULLY_QUALIFIED"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Source Names</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final SourceNames[] VALUES_ARRAY =
        new SourceNames[] {
            NONE_LITERAL,
            UNQUALIFIED_LITERAL,
            FULLY_QUALIFIED_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Source Names</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Source Names</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static SourceNames getGen(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            SourceNames result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Source Names</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public static SourceNames get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            SourceNames result = VALUES_ARRAY[i];
            if (result.toString().equalsIgnoreCase(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Source Names</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static SourceNames get(int value) {
        switch (value) {
            case NONE: return NONE_LITERAL;
            case UNQUALIFIED: return UNQUALIFIED_LITERAL;
            case FULLY_QUALIFIED: return FULLY_QUALIFIED_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private SourceNames(int value, String name) {
        super(value, name);
    }

} //SourceNames
