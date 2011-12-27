/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Port Type</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.PortType#getDefinitions <em>Definitions</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.PortType#getOperations <em>Operations</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getPortType()
 * @model
 * @generated
 */
public interface PortType extends WsdlNameRequiredEntity, ExtensibleAttributesDocumented {

    /**
     * Returns the value of the '<em><b>Definitions</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.wsdl.Definitions#getPortTypes <em>Port Types</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Definitions</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Definitions</em>' container reference.
     * @see #setDefinitions(Definitions)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getPortType_Definitions()
     * @see com.metamatrix.metamodels.wsdl.Definitions#getPortTypes
     * @model opposite="portTypes" required="true"
     * @generated
     */
    Definitions getDefinitions();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.PortType#getDefinitions <em>Definitions</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Definitions</em>' container reference.
     * @see #getDefinitions()
     * @generated
     */
    void setDefinitions( Definitions value );

    /**
     * Returns the value of the '<em><b>Operations</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.wsdl.Operation}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.wsdl.Operation#getPortType <em>Port Type</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operations</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Operations</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getPortType_Operations()
     * @see com.metamatrix.metamodels.wsdl.Operation#getPortType
     * @model type="com.metamatrix.metamodels.wsdl.Operation" opposite="portType" containment="true"
     * @generated
     */
    EList getOperations();

} // PortType
