/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.MappingRoot;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapping Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.TransformationMappingRoot#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getTransformationMappingRoot()
 * @model abstract="true"
 * @generated
 */
public interface TransformationMappingRoot extends MappingRoot{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Target</em>' reference.
     * @see #setTarget(EObject)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getTransformationMappingRoot_Target()
     * @model
     * @generated
     */
    EObject getTarget();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.TransformationMappingRoot#getTarget <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target</em>' reference.
     * @see #getTarget()
     * @generated
     */
    void setTarget(EObject value);

} // TransformationMappingRoot
