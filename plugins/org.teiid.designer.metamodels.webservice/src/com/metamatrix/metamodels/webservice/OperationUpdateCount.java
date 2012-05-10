/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Operation Update Count</b></em>', and
 * utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getOperationUpdateCount()
 * @model
 * @generated
 */
public final class OperationUpdateCount extends AbstractEnumerator {
    /**
     * The '<em><b>AUTO</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #AUTO_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int AUTO = 0;

    /**
     * The '<em><b>ZERO</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #ZERO_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ZERO = 1;

    /**
     * The '<em><b>ONE</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #ONE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ONE = 2;

    /**
     * The '<em><b>MULTIPLE</b></em>' literal value. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #MULTIPLE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int MULTIPLE = 3;

    /**
     * The '<em><b>AUTO</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>AUTO</b></em>' literal object isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #AUTO
     * @generated
     * @ordered
     */
    public static final OperationUpdateCount AUTO_LITERAL = new OperationUpdateCount(AUTO, "AUTO"); //$NON-NLS-1$

    /**
     * The '<em><b>ZERO</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ZERO</b></em>' literal object isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #ZERO
     * @generated
     * @ordered
     */
    public static final OperationUpdateCount ZERO_LITERAL = new OperationUpdateCount(ZERO, "ZERO"); //$NON-NLS-1$

    /**
     * The '<em><b>ONE</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ONE</b></em>' literal object isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #ONE
     * @generated
     * @ordered
     */
    public static final OperationUpdateCount ONE_LITERAL = new OperationUpdateCount(ONE, "ONE"); //$NON-NLS-1$

    /**
     * The '<em><b>MULTIPLE</b></em>' literal object. <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>MULTIPLE</b></em>' literal object isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @see #MULTIPLE
     * @generated
     * @ordered
     */
    public static final OperationUpdateCount MULTIPLE_LITERAL = new OperationUpdateCount(MULTIPLE, "MULTIPLE"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Operation Update Count</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private static final OperationUpdateCount[] VALUES_ARRAY = new OperationUpdateCount[] {AUTO_LITERAL, ZERO_LITERAL,
        ONE_LITERAL, MULTIPLE_LITERAL,};

    /**
     * A public read-only list of all the '<em><b>Operation Update Count</b></em>' enumerators. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY)); // NO_UCD

    /**
     * Returns the '<em><b>Operation Update Count</b></em>' literal with the specified name. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    public static OperationUpdateCount get( String name ) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            OperationUpdateCount result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Operation Update Count</b></em>' literal with the specified value. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     */
    public static OperationUpdateCount get( int value ) { // NO_UCD
        switch (value) {
            case AUTO:
                return AUTO_LITERAL;
            case ZERO:
                return ZERO_LITERAL;
            case ONE:
                return ONE_LITERAL;
            case MULTIPLE:
                return MULTIPLE_LITERAL;
        }
        return null;
    }

    /**
     * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private OperationUpdateCount( int value,
                                  String name ) {
        super(value, name);
    }

} // OperationUpdateCount
