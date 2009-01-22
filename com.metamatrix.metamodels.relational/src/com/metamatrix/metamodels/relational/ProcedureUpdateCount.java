/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Procedure Update Count</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedureUpdateCount()
 * @model
 * @generated
 */
public final class ProcedureUpdateCount extends AbstractEnumerator {
    /**
     * The '<em><b>AUTO</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #AUTO_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int AUTO = 0;

    /**
     * The '<em><b>ZERO</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ZERO_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ZERO = 1;

    /**
     * The '<em><b>ONE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ONE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ONE = 2;

    /**
     * The '<em><b>MULTIPLE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #MULTIPLE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int MULTIPLE = 3;

    /**
     * The '<em><b>AUTO</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>AUTO</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #AUTO
     * @generated
     * @ordered
     */
    public static final ProcedureUpdateCount AUTO_LITERAL = new ProcedureUpdateCount(AUTO, "AUTO"); //$NON-NLS-1$

    /**
     * The '<em><b>ZERO</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ZERO</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ZERO
     * @generated
     * @ordered
     */
    public static final ProcedureUpdateCount ZERO_LITERAL = new ProcedureUpdateCount(ZERO, "ZERO"); //$NON-NLS-1$

    /**
     * The '<em><b>ONE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ONE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ONE
     * @generated
     * @ordered
     */
    public static final ProcedureUpdateCount ONE_LITERAL = new ProcedureUpdateCount(ONE, "ONE"); //$NON-NLS-1$

    /**
     * The '<em><b>MULTIPLE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>MULTIPLE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #MULTIPLE
     * @generated
     * @ordered
     */
    public static final ProcedureUpdateCount MULTIPLE_LITERAL = new ProcedureUpdateCount(MULTIPLE, "MULTIPLE"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Procedure Update Count</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final ProcedureUpdateCount[] VALUES_ARRAY =
        new ProcedureUpdateCount[] {
            AUTO_LITERAL,
            ZERO_LITERAL,
            ONE_LITERAL,
            MULTIPLE_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Procedure Update Count</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Procedure Update Count</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ProcedureUpdateCount get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ProcedureUpdateCount result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Procedure Update Count</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ProcedureUpdateCount get(int value) {
        switch (value) {
            case AUTO: return AUTO_LITERAL;
            case ZERO: return ZERO_LITERAL;
            case ONE: return ONE_LITERAL;
            case MULTIPLE: return MULTIPLE_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private ProcedureUpdateCount(int value, String name) {
        super(value, name);
    }

} //ProcedureUpdateCount
