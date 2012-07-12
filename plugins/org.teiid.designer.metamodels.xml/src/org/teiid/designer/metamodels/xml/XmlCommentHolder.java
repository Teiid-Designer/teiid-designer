/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Comment Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.xml.XmlCommentHolder#getComments <em>Comments</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlCommentHolder()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface XmlCommentHolder extends EObject{
    /**
     * Returns the value of the '<em><b>Comments</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.xml.XmlComment}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.xml.XmlComment#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Comments</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Comments</em>' containment reference list.
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlCommentHolder_Comments()
     * @see org.teiid.designer.metamodels.xml.XmlComment#getParent
     * @model type="org.teiid.designer.metamodels.xml.XmlComment" opposite="parent" containment="true"
     * @generated
     */
    EList getComments();

} // XmlCommentHolder
