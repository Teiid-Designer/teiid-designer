/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Function</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.function.Function#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.Function#getCategory <em>Category</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.Function#getPushDown <em>Push Down</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.function.FunctionPackage#getFunction()
 * @model abstract="true"
 * @generated
 */
public interface Function extends EObject{
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getFunction_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.Function#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Category</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Category</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Category</em>' attribute.
     * @see #setCategory(String)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getFunction_Category()
     * @model
     * @generated
     */
    String getCategory();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.Function#getCategory <em>Category</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Category</em>' attribute.
     * @see #getCategory()
     * @generated
     */
    void setCategory(String value);

    /**
     * Returns the value of the '<em><b>Push Down</b></em>' attribute.
     * The default value is <code>"ALLOWED"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.function.PushDownType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Push Down</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Push Down</em>' attribute.
     * @see com.metamatrix.metamodels.function.PushDownType
     * @see #setPushDown(PushDownType)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getFunction_PushDown()
     * @model default="ALLOWED"
     * @generated
     */
    PushDownType getPushDown();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.Function#getPushDown <em>Push Down</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Push Down</em>' attribute.
     * @see com.metamatrix.metamodels.function.PushDownType
     * @see #getPushDown()
     * @generated
     */
    void setPushDown(PushDownType value);

} // Function
