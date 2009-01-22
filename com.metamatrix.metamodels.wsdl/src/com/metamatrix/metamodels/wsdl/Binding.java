/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import com.metamatrix.metamodels.wsdl.http.HttpBinding;

import com.metamatrix.metamodels.wsdl.soap.SoapBinding;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Binding#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Binding#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Binding#getBindingOperations <em>Binding Operations</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Binding#getSoapBinding <em>Soap Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Binding#getHttpBinding <em>Http Binding</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBinding()
 * @model
 * @generated
 */
public interface Binding extends WsdlNameRequiredEntity, ExtensibleDocumented{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

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
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBinding_Type()
     * @model
     * @generated
     */
	String getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Binding#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
	void setType(String value);

    /**
     * Returns the value of the '<em><b>Definitions</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Definitions#getBindings <em>Bindings</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Definitions</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Definitions</em>' container reference.
     * @see #setDefinitions(Definitions)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBinding_Definitions()
     * @see com.metamatrix.metamodels.wsdl.Definitions#getBindings
     * @model opposite="bindings" required="true"
     * @generated
     */
	Definitions getDefinitions();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Binding#getDefinitions <em>Definitions</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Definitions</em>' container reference.
     * @see #getDefinitions()
     * @generated
     */
	void setDefinitions(Definitions value);

    /**
     * Returns the value of the '<em><b>Binding Operations</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.BindingOperation}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binding Operations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Binding Operations</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBinding_BindingOperations()
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getBinding
     * @model type="com.metamatrix.metamodels.wsdl.BindingOperation" opposite="binding" containment="true"
     * @generated
     */
	EList getBindingOperations();

    /**
     * Returns the value of the '<em><b>Soap Binding</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Soap Binding</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Soap Binding</em>' containment reference.
     * @see #setSoapBinding(SoapBinding)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBinding_SoapBinding()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBinding#getBinding
     * @model opposite="binding" containment="true"
     * @generated
     */
	SoapBinding getSoapBinding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Binding#getSoapBinding <em>Soap Binding</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Soap Binding</em>' containment reference.
     * @see #getSoapBinding()
     * @generated
     */
	void setSoapBinding(SoapBinding value);

    /**
     * Returns the value of the '<em><b>Http Binding</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Http Binding</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Http Binding</em>' containment reference.
     * @see #setHttpBinding(HttpBinding)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBinding_HttpBinding()
     * @see com.metamatrix.metamodels.wsdl.http.HttpBinding#getBinding
     * @model opposite="binding" containment="true"
     * @generated
     */
	HttpBinding getHttpBinding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Binding#getHttpBinding <em>Http Binding</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Http Binding</em>' containment reference.
     * @see #getHttpBinding()
     * @generated
     */
	void setHttpBinding(HttpBinding value);

} // Binding
