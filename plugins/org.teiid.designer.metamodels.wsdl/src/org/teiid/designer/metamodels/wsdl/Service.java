/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Service</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Service#getDefinitions <em>Definitions</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Service#getPorts <em>Ports</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getService()
 * @model
 * @generated
 */
public interface Service extends WsdlNameRequiredEntity, ExtensibleDocumented {

    /**
     * Returns the value of the '<em><b>Definitions</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Definitions#getServices <em>Services</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Definitions</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Definitions</em>' container reference.
     * @see #setDefinitions(Definitions)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getService_Definitions()
     * @see org.teiid.designer.metamodels.wsdl.Definitions#getServices
     * @model opposite="services" required="true"
     * @generated
     */
    Definitions getDefinitions();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Service#getDefinitions <em>Definitions</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Definitions</em>' container reference.
     * @see #getDefinitions()
     * @generated
     */
    void setDefinitions( Definitions value );

    /**
     * Returns the value of the '<em><b>Ports</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.Port}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Port#getService <em>Service</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Ports</em>' containment reference list isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Ports</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getService_Ports()
     * @see org.teiid.designer.metamodels.wsdl.Port#getService
     * @model type="org.teiid.designer.metamodels.wsdl.Port" opposite="service" containment="true"
     * @generated
     */
    EList getPorts();

} // Service
