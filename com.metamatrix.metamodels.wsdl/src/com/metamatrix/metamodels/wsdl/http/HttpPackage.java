/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.http;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.http.HttpFactory
 * @generated
 */
public interface HttpPackage extends EPackage{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNAME = "http"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_URI = "http://schemas.xmlsoap.org/wsdl/http/"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_PREFIX = "http"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	HttpPackage eINSTANCE = com.metamatrix.metamodels.wsdl.http.impl.HttpPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.http.impl.HttpAddressImpl <em>Address</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.http.impl.HttpAddressImpl
     * @see com.metamatrix.metamodels.wsdl.http.impl.HttpPackageImpl#getHttpAddress()
     * @generated
     */
	int HTTP_ADDRESS = 0;

    /**
     * The feature id for the '<em><b>Port</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_ADDRESS__PORT = 0;

    /**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_ADDRESS__LOCATION = 1;

    /**
     * The number of structural features of the the '<em>Address</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_ADDRESS_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.http.impl.HttpBindingImpl <em>Binding</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.http.impl.HttpBindingImpl
     * @see com.metamatrix.metamodels.wsdl.http.impl.HttpPackageImpl#getHttpBinding()
     * @generated
     */
	int HTTP_BINDING = 1;

    /**
     * The feature id for the '<em><b>Binding</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_BINDING__BINDING = 0;

    /**
     * The feature id for the '<em><b>Verb</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_BINDING__VERB = 1;

    /**
     * The number of structural features of the the '<em>Binding</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_BINDING_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.http.impl.HttpOperationImpl <em>Operation</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.http.impl.HttpOperationImpl
     * @see com.metamatrix.metamodels.wsdl.http.impl.HttpPackageImpl#getHttpOperation()
     * @generated
     */
	int HTTP_OPERATION = 2;

    /**
     * The feature id for the '<em><b>Binding Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_OPERATION__BINDING_OPERATION = 0;

    /**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_OPERATION__LOCATION = 1;

    /**
     * The number of structural features of the the '<em>Operation</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int HTTP_OPERATION_FEATURE_COUNT = 2;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.http.HttpAddress <em>Address</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Address</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpAddress
     * @generated
     */
	EClass getHttpAddress();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.http.HttpAddress#getLocation <em>Location</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Location</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpAddress#getLocation()
     * @see #getHttpAddress()
     * @generated
     */
	EAttribute getHttpAddress_Location();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.http.HttpAddress#getPort <em>Port</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Port</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpAddress#getPort()
     * @see #getHttpAddress()
     * @generated
     */
	EReference getHttpAddress_Port();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.http.HttpBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpBinding
     * @generated
     */
	EClass getHttpBinding();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getVerb <em>Verb</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Verb</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpBinding#getVerb()
     * @see #getHttpBinding()
     * @generated
     */
	EAttribute getHttpBinding_Verb();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpBinding#getBinding()
     * @see #getHttpBinding()
     * @generated
     */
	EReference getHttpBinding_Binding();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.http.HttpOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpOperation
     * @generated
     */
	EClass getHttpOperation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.http.HttpOperation#getLocation <em>Location</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Location</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpOperation#getLocation()
     * @see #getHttpOperation()
     * @generated
     */
	EAttribute getHttpOperation_Location();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.http.HttpOperation#getBindingOperation <em>Binding Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.http.HttpOperation#getBindingOperation()
     * @see #getHttpOperation()
     * @generated
     */
	EReference getHttpOperation_BindingOperation();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
	HttpFactory getHttpFactory();

} //HttpPackage
