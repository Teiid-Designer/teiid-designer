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
 * A representation of the model object '<em><b>Expression Owner</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.ExpressionOwner#getExpressions <em>Expressions</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getExpressionOwner()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ExpressionOwner extends EObject {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Expressions</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.Expression}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.Expression#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Expressions</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Expressions</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getExpressionOwner_Expressions()
     * @see com.metamatrix.metamodels.transformation.Expression#getOwner
     * @model type="com.metamatrix.metamodels.transformation.Expression" opposite="owner" containment="true" required="true"
     * @generated
     */
    EList getExpressions();

} // ExpressionOwner
