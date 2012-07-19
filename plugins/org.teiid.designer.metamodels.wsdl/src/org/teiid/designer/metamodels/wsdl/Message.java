/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Message</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Message#getDefinitions <em>Definitions</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Message#getParts <em>Parts</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getMessage()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface Message extends WsdlNameRequiredEntity, ExtensibleDocumented {

    /**
     * Returns the value of the '<em><b>Definitions</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Definitions#getMessages <em>Messages</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Definitions</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Definitions</em>' container reference.
     * @see #setDefinitions(Definitions)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getMessage_Definitions()
     * @see org.teiid.designer.metamodels.wsdl.Definitions#getMessages
     * @model opposite="messages" required="true"
     * @generated
     */
    Definitions getDefinitions();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Message#getDefinitions <em>Definitions</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Definitions</em>' container reference.
     * @see #getDefinitions()
     * @generated
     */
    void setDefinitions( Definitions value );

    /**
     * Returns the value of the '<em><b>Parts</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.MessagePart}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.MessagePart#getMessage <em>Message</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parts</em>' containment reference list isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Parts</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getMessage_Parts()
     * @see org.teiid.designer.metamodels.wsdl.MessagePart#getMessage
     * @model type="org.teiid.designer.metamodels.wsdl.MessagePart" opposite="message" containment="true"
     * @generated
     */
    EList getParts();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @model dataType="org.teiid.designer.metamodels.wsdl.IStatus" parameters=""
     * @generated
     */
    IStatus isValid(); // NO_UCD

} // Message
