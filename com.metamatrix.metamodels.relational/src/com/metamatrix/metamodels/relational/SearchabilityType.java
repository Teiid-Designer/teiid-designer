/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.relational;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Searchability Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getSearchabilityType()
 * @model
 * @generated
 */
public final class SearchabilityType extends AbstractEnumerator {
    /**
     * The '<em><b>SEARCHABLE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #SEARCHABLE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int SEARCHABLE = 0;

    /**
     * The '<em><b>ALL EXCEPT LIKE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ALL_EXCEPT_LIKE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int ALL_EXCEPT_LIKE = 1;

    /**
     * The '<em><b>LIKE ONLY</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #LIKE_ONLY_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int LIKE_ONLY = 2;

    /**
     * The '<em><b>UNSEARCHABLE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #UNSEARCHABLE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int UNSEARCHABLE = 3;

    /**
     * The '<em><b>SEARCHABLE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>SEARCHABLE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #SEARCHABLE
     * @generated
     * @ordered
     */
    public static final SearchabilityType SEARCHABLE_LITERAL = new SearchabilityType(SEARCHABLE, "SEARCHABLE"); //$NON-NLS-1$

    /**
     * The '<em><b>ALL EXCEPT LIKE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>ALL EXCEPT LIKE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ALL_EXCEPT_LIKE
     * @generated
     * @ordered
     */
    public static final SearchabilityType ALL_EXCEPT_LIKE_LITERAL = new SearchabilityType(ALL_EXCEPT_LIKE, "ALL_EXCEPT_LIKE"); //$NON-NLS-1$

    /**
     * The '<em><b>LIKE ONLY</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>LIKE ONLY</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #LIKE_ONLY
     * @generated
     * @ordered
     */
    public static final SearchabilityType LIKE_ONLY_LITERAL = new SearchabilityType(LIKE_ONLY, "LIKE_ONLY"); //$NON-NLS-1$

    /**
     * The '<em><b>UNSEARCHABLE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>UNSEARCHABLE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #UNSEARCHABLE
     * @generated
     * @ordered
     */
    public static final SearchabilityType UNSEARCHABLE_LITERAL = new SearchabilityType(UNSEARCHABLE, "UNSEARCHABLE"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Searchability Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final SearchabilityType[] VALUES_ARRAY =
        new SearchabilityType[] {
            SEARCHABLE_LITERAL,
            ALL_EXCEPT_LIKE_LITERAL,
            LIKE_ONLY_LITERAL,
            UNSEARCHABLE_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Searchability Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Searchability Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static SearchabilityType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            SearchabilityType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Searchability Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static SearchabilityType get(int value) {
        switch (value) {
            case SEARCHABLE: return SEARCHABLE_LITERAL;
            case ALL_EXCEPT_LIKE: return ALL_EXCEPT_LIKE_LITERAL;
            case LIKE_ONLY: return LIKE_ONLY_LITERAL;
            case UNSEARCHABLE: return UNSEARCHABLE_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private SearchabilityType(int value, String name) {
        super(value, name);
    }

} //SearchabilityType
