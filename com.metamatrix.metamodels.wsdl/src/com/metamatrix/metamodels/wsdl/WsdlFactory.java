/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage
 * @generated
 */
public interface WsdlFactory extends EFactory{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	WsdlFactory eINSTANCE = new com.metamatrix.metamodels.wsdl.impl.WsdlFactoryImpl();

    /**
     * Returns a new object of class '<em>Definitions</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Definitions</em>'.
     * @generated
     */
	Definitions createDefinitions();

    /**
     * Returns a new object of class '<em>Documentation</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Documentation</em>'.
     * @generated
     */
	Documentation createDocumentation();

    /**
     * Returns a new object of class '<em>Attribute</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Attribute</em>'.
     * @generated
     */
	Attribute createAttribute();

    /**
     * Returns a new object of class '<em>Message</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Message</em>'.
     * @generated
     */
	Message createMessage();

    /**
     * Returns a new object of class '<em>Port Type</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Port Type</em>'.
     * @generated
     */
	PortType createPortType();

    /**
     * Returns a new object of class '<em>Binding</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Binding</em>'.
     * @generated
     */
	Binding createBinding();

    /**
     * Returns a new object of class '<em>Service</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Service</em>'.
     * @generated
     */
	Service createService();

    /**
     * Returns a new object of class '<em>Import</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Import</em>'.
     * @generated
     */
	Import createImport();

    /**
     * Returns a new object of class '<em>Port</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Port</em>'.
     * @generated
     */
	Port createPort();

    /**
     * Returns a new object of class '<em>Element</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Element</em>'.
     * @generated
     */
	Element createElement();

    /**
     * Returns a new object of class '<em>Types</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Types</em>'.
     * @generated
     */
	Types createTypes();

    /**
     * Returns a new object of class '<em>Message Part</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Message Part</em>'.
     * @generated
     */
	MessagePart createMessagePart();

    /**
     * Returns a new object of class '<em>Operation</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Operation</em>'.
     * @generated
     */
	Operation createOperation();

    /**
     * Returns a new object of class '<em>Input</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Input</em>'.
     * @generated
     */
	Input createInput();

    /**
     * Returns a new object of class '<em>Output</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Output</em>'.
     * @generated
     */
	Output createOutput();

    /**
     * Returns a new object of class '<em>Fault</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Fault</em>'.
     * @generated
     */
	Fault createFault();

    /**
     * Returns a new object of class '<em>Binding Operation</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Binding Operation</em>'.
     * @generated
     */
	BindingOperation createBindingOperation();

    /**
     * Returns a new object of class '<em>Binding Input</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Binding Input</em>'.
     * @generated
     */
	BindingInput createBindingInput();

    /**
     * Returns a new object of class '<em>Binding Output</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Binding Output</em>'.
     * @generated
     */
	BindingOutput createBindingOutput();

    /**
     * Returns a new object of class '<em>Binding Fault</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Binding Fault</em>'.
     * @generated
     */
	BindingFault createBindingFault();

    /**
     * Returns a new object of class '<em>Namespace Declaration</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Namespace Declaration</em>'.
     * @generated
     */
	NamespaceDeclaration createNamespaceDeclaration();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
	WsdlPackage getWsdlPackage();

} //WsdlFactory
