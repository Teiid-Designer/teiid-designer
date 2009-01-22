/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapping Class Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClassSet#getMappingClasses <em>Mapping Classes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClassSet#getTarget <em>Target</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClassSet#getInputBinding <em>Input Binding</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClassSet()
 * @model
 * @generated
 */
public interface MappingClassSet extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Mapping Classes</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.MappingClass}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.MappingClass#getMappingClassSet <em>Mapping Class Set</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping Classes</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mapping Classes</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClassSet_MappingClasses()
     * @see com.metamatrix.metamodels.transformation.MappingClass#getMappingClassSet
     * @model type="com.metamatrix.metamodels.transformation.MappingClass" opposite="mappingClassSet" containment="true"
     * @generated
     */
    EList getMappingClasses();

    /**
     * Returns the value of the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Target</em>' reference.
     * @see #setTarget(EObject)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClassSet_Target()
     * @model
     * @generated
     */
    EObject getTarget();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClassSet#getTarget <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target</em>' reference.
     * @see #getTarget()
     * @generated
     */
    void setTarget(EObject value);

    /**
     * Returns the value of the '<em><b>Input Binding</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.InputBinding}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.InputBinding#getMappingClassSet <em>Mapping Class Set</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Binding</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Binding</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClassSet_InputBinding()
     * @see com.metamatrix.metamodels.transformation.InputBinding#getMappingClassSet
     * @model type="com.metamatrix.metamodels.transformation.InputBinding" opposite="mappingClassSet" containment="true"
     * @generated
     */
    EList getInputBinding();

} // MappingClassSet
