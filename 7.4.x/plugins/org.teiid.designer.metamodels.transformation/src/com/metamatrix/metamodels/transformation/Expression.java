/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Expression</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.transformation.Expression#getValue <em>Value</em>}</li>
 * <li>{@link com.metamatrix.metamodels.transformation.Expression#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getExpression()
 * @model
 * @generated
 */
public interface Expression extends EObject {

    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Value</em>' attribute.
     * @see #setValue(String)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getExpression_Value()
     * @model
     * @generated
     */
    String getValue();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.Expression#getValue <em>Value</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Value</em>' attribute.
     * @see #getValue()
     * @generated
     */
    void setValue( String value );

    /**
     * Returns the value of the '<em><b>Owner</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.transformation.ExpressionOwner#getExpressions <em>Expressions</em>}'. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Owner</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Owner</em>' container reference.
     * @see #setOwner(ExpressionOwner)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getExpression_Owner()
     * @see com.metamatrix.metamodels.transformation.ExpressionOwner#getExpressions
     * @model opposite="expressions"
     * @generated
     */
    ExpressionOwner getOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.Expression#getOwner <em>Owner</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Owner</em>' container reference.
     * @see #getOwner()
     * @generated
     */
    void setOwner( ExpressionOwner value );

} // Expression
