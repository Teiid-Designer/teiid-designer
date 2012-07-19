/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl;

import org.teiid.designer.metamodels.wsdl.soap.SoapFault;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Binding Fault</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.BindingFault#getBindingOperation <em>Binding Operation</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.BindingFault#getSoapFault <em>Soap Fault</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBindingFault()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface BindingFault extends ExtensibleDocumented, WsdlNameRequiredEntity {

    /**
     * Returns the value of the '<em><b>Binding Operation</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.BindingOperation#getBindingFaults <em>Binding Faults</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Binding Operation</em>' container reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Binding Operation</em>' container reference.
     * @see #setBindingOperation(BindingOperation)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBindingFault_BindingOperation()
     * @see org.teiid.designer.metamodels.wsdl.BindingOperation#getBindingFaults
     * @model opposite="bindingFaults" required="true"
     * @generated
     */
    BindingOperation getBindingOperation();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.BindingFault#getBindingOperation <em>Binding Operation</em>}'
     * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Binding Operation</em>' container reference.
     * @see #getBindingOperation()
     * @generated
     */
    void setBindingOperation( BindingOperation value );

    /**
     * Returns the value of the '<em><b>Soap Fault</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.soap.SoapFault#getBindingFault <em>Binding Fault</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Soap Fault</em>' containment reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Soap Fault</em>' containment reference.
     * @see #setSoapFault(SoapFault)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getBindingFault_SoapFault()
     * @see org.teiid.designer.metamodels.wsdl.soap.SoapFault#getBindingFault
     * @model opposite="bindingFault" containment="true" required="true"
     * @generated
     */
    SoapFault getSoapFault();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.BindingFault#getSoapFault <em>Soap Fault</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Soap Fault</em>' containment reference.
     * @see #getSoapFault()
     * @generated
     */
    void setSoapFault( SoapFault value );

} // BindingFault
