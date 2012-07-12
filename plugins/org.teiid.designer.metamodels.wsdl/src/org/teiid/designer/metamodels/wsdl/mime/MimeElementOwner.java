/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl.mime;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Element Owner</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.mime.MimeElementOwner#getMimeElements <em>Mime Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.mime.MimePackage#getMimeElementOwner()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface MimeElementOwner extends EObject {

    /**
     * Returns the value of the '<em><b>Mime Elements</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.mime.MimeElement}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.mime.MimeElement#getMimeElementOwner <em>Mime Element Owner</em>}'. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mime Elements</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Mime Elements</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.mime.MimePackage#getMimeElementOwner_MimeElements()
     * @see org.teiid.designer.metamodels.wsdl.mime.MimeElement#getMimeElementOwner
     * @model type="org.teiid.designer.metamodels.wsdl.mime.MimeElement" opposite="mimeElementOwner" containment="true"
     * @generated
     */
    EList getMimeElements();

} // MimeElementOwner
