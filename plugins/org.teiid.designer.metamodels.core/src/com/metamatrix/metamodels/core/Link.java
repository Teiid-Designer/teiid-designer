/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Link</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.Link#getName <em>Name</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Link#getDescription <em>Description</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Link#getReferences <em>References</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Link#getLinkedObjects <em>Linked Objects</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.Link#getLinkContainer <em>Link Container</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.core.CorePackage#getLink()
 * @model
 * @generated
 */
public interface Link extends EObject {

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getLink_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.Link#getName <em>Name</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName( String value );

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getLink_Description()
     * @model
     * @generated
     */
    String getDescription();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.Link#getDescription <em>Description</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     * @generated
     */
    void setDescription( String value );

    /**
     * Returns the value of the '<em><b>References</b></em>' attribute list. The list contents are of type
     * {@link java.lang.String}. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>References</em>' attribute list isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>References</em>' attribute list.
     * @see com.metamatrix.metamodels.core.CorePackage#getLink_References()
     * @model type="java.lang.String"
     * @generated
     */
    EList getReferences();

    /**
     * Returns the value of the '<em><b>Linked Objects</b></em>' reference list. The list contents are of type
     * {@link org.eclipse.emf.ecore.EObject}. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Linked Objects</em>' reference list isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Linked Objects</em>' reference list.
     * @see com.metamatrix.metamodels.core.CorePackage#getLink_LinkedObjects()
     * @model type="org.eclipse.emf.ecore.EObject"
     * @generated
     */
    EList getLinkedObjects();

    /**
     * Returns the value of the '<em><b>Link Container</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.core.LinkContainer#getLinks <em>Links</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Link Container</em>' container reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Link Container</em>' container reference.
     * @see #setLinkContainer(LinkContainer)
     * @see com.metamatrix.metamodels.core.CorePackage#getLink_LinkContainer()
     * @see com.metamatrix.metamodels.core.LinkContainer#getLinks
     * @model opposite="links"
     * @generated
     */
    LinkContainer getLinkContainer();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.Link#getLinkContainer <em>Link Container</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Link Container</em>' container reference.
     * @see #getLinkContainer()
     * @generated
     */
    void setLinkContainer( LinkContainer value );

} // Link
