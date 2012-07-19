/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Return Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.function.ReturnParameter#getFunction <em>Function</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.function.ReturnParameter#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.function.FunctionPackage#getReturnParameter()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface ReturnParameter extends EObject{
    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see org.teiid.designer.metamodels.function.FunctionPackage#getReturnParameter_Type()
     * @model
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.function.ReturnParameter#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(String value);

    /**
     * Returns the value of the '<em><b>Function</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.function.ScalarFunction#getReturnParameter <em>Return Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Function</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Function</em>' container reference.
     * @see #setFunction(ScalarFunction)
     * @see org.teiid.designer.metamodels.function.FunctionPackage#getReturnParameter_Function()
     * @see org.teiid.designer.metamodels.function.ScalarFunction#getReturnParameter
     * @model opposite="returnParameter" required="true"
     * @generated
     */
    ScalarFunction getFunction();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.function.ReturnParameter#getFunction <em>Function</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Function</em>' container reference.
     * @see #getFunction()
     * @generated
     */
    void setFunction(ScalarFunction value);

} // ReturnParameter
