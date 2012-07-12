/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl.mime;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Multipart Related</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.mime.MimeMultipartRelated#getMimeParts <em>Mime Parts</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.mime.MimePackage#getMimeMultipartRelated()
 * @model
 * @generated
 */
public interface MimeMultipartRelated extends MimeElement {

    /**
     * Returns the value of the '<em><b>Mime Parts</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.wsdl.mime.MimePart}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.mime.MimePart#getMimeMultipartRelated <em>Mime Multipart Related</em>}'. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mime Parts</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Mime Parts</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.mime.MimePackage#getMimeMultipartRelated_MimeParts()
     * @see org.teiid.designer.metamodels.wsdl.mime.MimePart#getMimeMultipartRelated
     * @model type="org.teiid.designer.metamodels.wsdl.mime.MimePart" opposite="mimeMultipartRelated" containment="true"
     * @generated
     */
    EList getMimeParts();

} // MimeMultipartRelated
