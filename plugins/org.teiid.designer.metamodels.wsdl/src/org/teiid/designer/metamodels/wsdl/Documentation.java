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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Documentation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Documentation#getTextContent <em>Text Content</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Documentation#getContents <em>Contents</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.Documentation#getDocumented <em>Documented</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDocumentation()
 * @model
 * @generated
 */
public interface Documentation extends ElementOwner {

    /**
     * Returns the value of the '<em><b>Text Content</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Text Content</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Text Content</em>' attribute.
     * @see #setTextContent(String)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDocumentation_TextContent()
     * @model
     * @generated
     */
    String getTextContent();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Documentation#getTextContent <em>Text Content</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Text Content</em>' attribute.
     * @see #getTextContent()
     * @generated
     */
    void setTextContent( String value );

    /**
     * Returns the value of the '<em><b>Contents</b></em>' containment reference list. The list contents are of type
     * {@link org.eclipse.emf.ecore.EObject}. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Contents</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Contents</em>' containment reference list.
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDocumentation_Contents()
     * @model type="org.eclipse.emf.ecore.EObject" containment="true"
     * @generated
     */
    EList getContents();

    /**
     * Returns the value of the '<em><b>Documented</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Documented#getDocumentation <em>Documentation</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Documented</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Documented</em>' container reference.
     * @see #setDocumented(Documented)
     * @see org.teiid.designer.metamodels.wsdl.WsdlPackage#getDocumentation_Documented()
     * @see org.teiid.designer.metamodels.wsdl.Documented#getDocumentation
     * @model opposite="documentation"
     * @generated
     */
    Documented getDocumented();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.Documentation#getDocumented <em>Documented</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Documented</em>' container reference.
     * @see #getDocumented()
     * @generated
     */
    void setDocumented( Documented value );

} // Documentation
