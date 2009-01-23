/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.MimeElement#getMimeElementOwner <em>Mime Element Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeElement()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface MimeElement extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Mime Element Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.mime.MimeElementOwner#getMimeElements <em>Mime Elements</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mime Element Owner</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Mime Element Owner</em>' container reference.
     * @see #setMimeElementOwner(MimeElementOwner)
     * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeElement_MimeElementOwner()
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElementOwner#getMimeElements
     * @model opposite="mimeElements" required="true"
     * @generated
     */
	MimeElementOwner getMimeElementOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.mime.MimeElement#getMimeElementOwner <em>Mime Element Owner</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mime Element Owner</em>' container reference.
     * @see #getMimeElementOwner()
     * @generated
     */
	void setMimeElementOwner(MimeElementOwner value);

} // MimeElement
