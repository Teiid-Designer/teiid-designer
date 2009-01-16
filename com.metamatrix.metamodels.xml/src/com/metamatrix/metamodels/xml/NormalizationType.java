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

package com.metamatrix.metamodels.xml;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Normalization Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getNormalizationType()
 * @model
 * @generated
 */
public final class NormalizationType extends AbstractEnumerator {
    /**
     * The '<em><b>PRESERVE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>PRESERVE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #PRESERVE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int PRESERVE = 0;

    /**
     * The '<em><b>REPLACE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>REPLACE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #REPLACE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int REPLACE = 1;

    /**
     * The '<em><b>COLLAPSE</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>COLLAPSE</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #COLLAPSE_LITERAL
     * @model
     * @generated
     * @ordered
     */
    public static final int COLLAPSE = 2;

    /**
     * The '<em><b>PRESERVE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #PRESERVE
     * @generated
     * @ordered
     */
    public static final NormalizationType PRESERVE_LITERAL = new NormalizationType(PRESERVE, "PRESERVE"); //$NON-NLS-1$

    /**
     * The '<em><b>REPLACE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #REPLACE
     * @generated
     * @ordered
     */
    public static final NormalizationType REPLACE_LITERAL = new NormalizationType(REPLACE, "REPLACE"); //$NON-NLS-1$

    /**
     * The '<em><b>COLLAPSE</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #COLLAPSE
     * @generated
     * @ordered
     */
    public static final NormalizationType COLLAPSE_LITERAL = new NormalizationType(COLLAPSE, "COLLAPSE"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Normalization Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final NormalizationType[] VALUES_ARRAY =
        new NormalizationType[] {
            PRESERVE_LITERAL,
            REPLACE_LITERAL,
            COLLAPSE_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Normalization Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Normalization Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static NormalizationType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            NormalizationType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Normalization Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static NormalizationType get(int value) {
        switch (value) {
            case PRESERVE: return PRESERVE_LITERAL;
            case REPLACE: return REPLACE_LITERAL;
            case COLLAPSE: return COLLAPSE_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private NormalizationType(int value, String name) {
        super(value, name);
    }

} //NormalizationType
