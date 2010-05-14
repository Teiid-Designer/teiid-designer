/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.XmlServiceComponent#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.XmlServiceComponent#getNameInSource <em>Name In Source</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlServiceComponent()
 * @model
 * @generated
 */
public interface XmlServiceComponent extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

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
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlServiceComponent_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xmlservice.XmlServiceComponent#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name In Source</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name In Source</em>' attribute.
     * @see #setNameInSource(String)
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlServiceComponent_NameInSource()
     * @model
     * @generated
     */
    String getNameInSource();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xmlservice.XmlServiceComponent#getNameInSource <em>Name In Source</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name In Source</em>' attribute.
     * @see #getNameInSource()
     * @generated
     */
    void setNameInSource(String value);

} // XmlServiceComponent
