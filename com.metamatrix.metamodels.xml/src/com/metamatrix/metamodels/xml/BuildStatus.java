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
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Build Status</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getBuildStatus()
 * @model
 * @generated
 */
public final class BuildStatus extends AbstractEnumerator {
    /**
     * The '<em><b>COMPLETE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #COMPLETE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int COMPLETE = 0;

    /**
     * The '<em><b>INCOMPLETE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #INCOMPLETE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int INCOMPLETE = 1;

    /**
     * The '<em><b>COMPLETE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>COMPLETE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #COMPLETE
     * @generated
     * @ordered
     */
    public static final BuildStatus COMPLETE_LITERAL = new BuildStatus(COMPLETE, "COMPLETE"); //$NON-NLS-1$

    /**
     * The '<em><b>INCOMPLETE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>INCOMPLETE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #INCOMPLETE
     * @generated
     * @ordered
     */
    public static final BuildStatus INCOMPLETE_LITERAL = new BuildStatus(INCOMPLETE, "INCOMPLETE"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Build Status</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final BuildStatus[] VALUES_ARRAY =
        new BuildStatus[] {
            COMPLETE_LITERAL,
            INCOMPLETE_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Build Status</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Build Status</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static BuildStatus get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            BuildStatus result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Build Status</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static BuildStatus get(int value) {
        switch (value) {
            case COMPLETE: return COMPLETE_LITERAL;
            case INCOMPLETE: return INCOMPLETE_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private BuildStatus(int value, String name) {
        super(value, name);
    }

} //BuildStatus
