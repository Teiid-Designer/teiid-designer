/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Definitions</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Definitions#getTargetNamespace <em>Target Namespace</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Definitions#getMessages <em>Messages</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Definitions#getPortTypes <em>Port Types</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Definitions#getBindings <em>Bindings</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Definitions#getServices <em>Services</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Definitions#getImports <em>Imports</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Definitions#getTypes <em>Types</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDefinitions()
 * @model
 * @generated
 */
public interface Definitions extends WsdlNameOptionalEntity, ExtensibleDocumented{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Target Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target Namespace</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Target Namespace</em>' attribute.
     * @see #setTargetNamespace(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDefinitions_TargetNamespace()
     * @model
     * @generated
     */
	String getTargetNamespace();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Definitions#getTargetNamespace <em>Target Namespace</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target Namespace</em>' attribute.
     * @see #getTargetNamespace()
     * @generated
     */
	void setTargetNamespace(String value);

    /**
     * Returns the value of the '<em><b>Messages</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.Message}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Message#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Messages</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Messages</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDefinitions_Messages()
     * @see com.metamatrix.metamodels.wsdl.Message#getDefinitions
     * @model type="com.metamatrix.metamodels.wsdl.Message" opposite="definitions" containment="true"
     * @generated
     */
	EList getMessages();

    /**
     * Returns the value of the '<em><b>Port Types</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.PortType}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.PortType#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Port Types</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Port Types</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDefinitions_PortTypes()
     * @see com.metamatrix.metamodels.wsdl.PortType#getDefinitions
     * @model type="com.metamatrix.metamodels.wsdl.PortType" opposite="definitions" containment="true"
     * @generated
     */
	EList getPortTypes();

    /**
     * Returns the value of the '<em><b>Bindings</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.Binding}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Binding#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Bindings</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Bindings</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDefinitions_Bindings()
     * @see com.metamatrix.metamodels.wsdl.Binding#getDefinitions
     * @model type="com.metamatrix.metamodels.wsdl.Binding" opposite="definitions" containment="true"
     * @generated
     */
	EList getBindings();

    /**
     * Returns the value of the '<em><b>Services</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.Service}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Service#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Services</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Services</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDefinitions_Services()
     * @see com.metamatrix.metamodels.wsdl.Service#getDefinitions
     * @model type="com.metamatrix.metamodels.wsdl.Service" opposite="definitions" containment="true"
     * @generated
     */
	EList getServices();

    /**
     * Returns the value of the '<em><b>Imports</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.Import}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Import#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Imports</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Imports</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDefinitions_Imports()
     * @see com.metamatrix.metamodels.wsdl.Import#getDefinitions
     * @model type="com.metamatrix.metamodels.wsdl.Import" opposite="definitions" containment="true"
     * @generated
     */
	EList getImports();

    /**
     * Returns the value of the '<em><b>Types</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Types#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Types</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Types</em>' containment reference.
     * @see #setTypes(Types)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDefinitions_Types()
     * @see com.metamatrix.metamodels.wsdl.Types#getDefinitions
     * @model opposite="definitions" containment="true"
     * @generated
     */
	Types getTypes();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Definitions#getTypes <em>Types</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Types</em>' containment reference.
     * @see #getTypes()
     * @generated
     */
	void setTypes(Types value);

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @model dataType="com.metamatrix.metamodels.wsdl.IStatus" parameters=""
     * @generated
     */
	IStatus isValid();

} // Definitions
