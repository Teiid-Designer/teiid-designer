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
 * A representation of the literals of the enumeration '<em><b>Cascade Deletes Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getCascadeDeletesType()
 * @model
 * @generated
 */
public final class CascadeDeletesType extends AbstractEnumerator {
	/**
	 * The '<em><b>ALWAYS</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #ALWAYS_LITERAL
	 * @model 
	 * @generated
	 * @ordered
	 */
	public static final int ALWAYS = 0;

	/**
	 * The '<em><b>NEVER</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NEVER_LITERAL
	 * @model 
	 * @generated
	 * @ordered
	 */
	public static final int NEVER = 1;

	/**
	 * The '<em><b>UNSPECIFIED</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #UNSPECIFIED_LITERAL
	 * @model 
	 * @generated
	 * @ordered
	 */
	public static final int UNSPECIFIED = 2;

	/**
	 * The '<em><b>ALWAYS</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>ALWAYS</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #ALWAYS
	 * @generated
	 * @ordered
	 */
	public static final CascadeDeletesType ALWAYS_LITERAL = new CascadeDeletesType(ALWAYS, "ALWAYS"); //$NON-NLS-1$

	/**
	 * The '<em><b>NEVER</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>NEVER</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #NEVER
	 * @generated
	 * @ordered
	 */
	public static final CascadeDeletesType NEVER_LITERAL = new CascadeDeletesType(NEVER, "NEVER"); //$NON-NLS-1$

	/**
	 * The '<em><b>UNSPECIFIED</b></em>' literal object.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of '<em><b>UNSPECIFIED</b></em>' literal object isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @see #UNSPECIFIED
	 * @generated
	 * @ordered
	 */
	public static final CascadeDeletesType UNSPECIFIED_LITERAL = new CascadeDeletesType(UNSPECIFIED, "UNSPECIFIED"); //$NON-NLS-1$

	/**
	 * An array of all the '<em><b>Cascade Deletes Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final CascadeDeletesType[] VALUES_ARRAY =
		new CascadeDeletesType[] {
			ALWAYS_LITERAL,
			NEVER_LITERAL,
			UNSPECIFIED_LITERAL,
		};

	/**
	 * A public read-only list of all the '<em><b>Cascade Deletes Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Cascade Deletes Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CascadeDeletesType get(String name)
	{
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			CascadeDeletesType result = VALUES_ARRAY[i];
			if (result.toString().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Cascade Deletes Type</b></em>' literal with the specified value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CascadeDeletesType get(int value)
	{
		switch (value) {
			case ALWAYS: return ALWAYS_LITERAL;
			case NEVER: return NEVER_LITERAL;
			case UNSPECIFIED: return UNSPECIFIED_LITERAL;
		}
		return null;	
	}

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private CascadeDeletesType(int value, String name)
	{
		super(value, name);
	}

} //CascadeDeletesType
