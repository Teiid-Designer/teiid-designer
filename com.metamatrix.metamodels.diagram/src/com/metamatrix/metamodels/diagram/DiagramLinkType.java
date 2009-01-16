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

package com.metamatrix.metamodels.diagram;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.AbstractEnumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Link Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramLinkType()
 * @model
 * @generated
 */
public final class DiagramLinkType extends AbstractEnumerator {

    /**
     * The '<em><b>ORTHOGONAL</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #ORTHOGONAL_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int ORTHOGONAL = 0;

    /**
     * The '<em><b>DIRECTED</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #DIRECTED_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int DIRECTED = 1;

    /**
     * The '<em><b>MANUAL</b></em>' literal value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #MANUAL_LITERAL
     * @model
     * @generated
     * @ordered
     */
	public static final int MANUAL = 2;

    /**
     * The '<em><b>ORTHOGONAL</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>ORTHOGONAL</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #ORTHOGONAL
     * @generated
     * @ordered
     */
	public static final DiagramLinkType ORTHOGONAL_LITERAL = new DiagramLinkType(ORTHOGONAL, "ORTHOGONAL"); //$NON-NLS-1$

    /**
     * The '<em><b>DIRECTED</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>DIRECTED</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #DIRECTED
     * @generated
     * @ordered
     */
	public static final DiagramLinkType DIRECTED_LITERAL = new DiagramLinkType(DIRECTED, "DIRECTED"); //$NON-NLS-1$

    /**
     * The '<em><b>MANUAL</b></em>' literal object.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>MANUAL</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @see #MANUAL
     * @generated
     * @ordered
     */
	public static final DiagramLinkType MANUAL_LITERAL = new DiagramLinkType(MANUAL, "MANUAL"); //$NON-NLS-1$

    /**
     * An array of all the '<em><b>Link Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private static final DiagramLinkType[] VALUES_ARRAY =
        new DiagramLinkType[] {
            ORTHOGONAL_LITERAL,
            DIRECTED_LITERAL,
            MANUAL_LITERAL,
        };

    /**
     * A public read-only list of all the '<em><b>Link Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Link Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static DiagramLinkType get(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            DiagramLinkType result = VALUES_ARRAY[i];
            if (result.toString().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Link Type</b></em>' literal with the specified value.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static DiagramLinkType get(int value) {
        switch (value) {
            case ORTHOGONAL: return ORTHOGONAL_LITERAL;
            case DIRECTED: return DIRECTED_LITERAL;
            case MANUAL: return MANUAL_LITERAL;
        }
        return null;	
    }

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private DiagramLinkType(int value, String name) {
        super(value, name);
    }

} //DiagramLinkType
