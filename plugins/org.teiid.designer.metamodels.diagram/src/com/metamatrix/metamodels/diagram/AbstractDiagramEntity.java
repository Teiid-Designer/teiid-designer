/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.diagram;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Abstract Diagram Entity</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getAlias <em>Alias</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getUserString <em>User String</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getUserType <em>User Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getModelObject <em>Model Object</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.diagram.DiagramPackage#getAbstractDiagramEntity()
 * @model abstract="true"
 * @generated
 */
public interface AbstractDiagramEntity extends PresentationEntity{

    /**
     * Returns the value of the '<em><b>Alias</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Alias</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Alias</em>' attribute.
     * @see #setAlias(String)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getAbstractDiagramEntity_Alias()
     * @model
     * @generated
     */
    String getAlias();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getAlias <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Alias</em>' attribute.
     * @see #getAlias()
     * @generated
     */
    void setAlias(String value);

    /**
     * Returns the value of the '<em><b>User String</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>User String</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>User String</em>' attribute.
     * @see #setUserString(String)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getAbstractDiagramEntity_UserString()
     * @model
     * @generated
     */
    String getUserString();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getUserString <em>User String</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>User String</em>' attribute.
     * @see #getUserString()
     * @generated
     */
    void setUserString(String value);

    /**
     * Returns the value of the '<em><b>User Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>User Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>User Type</em>' attribute.
     * @see #setUserType(String)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getAbstractDiagramEntity_UserType()
     * @model
     * @generated
     */
    String getUserType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getUserType <em>User Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>User Type</em>' attribute.
     * @see #getUserType()
     * @generated
     */
    void setUserType(String value);

    /**
     * Returns the value of the '<em><b>Model Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model Object</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Model Object</em>' reference.
     * @see #setModelObject(EObject)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getAbstractDiagramEntity_ModelObject()
     * @model
     * @generated
     */
    EObject getModelObject();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getModelObject <em>Model Object</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Model Object</em>' reference.
     * @see #getModelObject()
     * @generated
     */
    void setModelObject(EObject value);

} // AbstractDiagramEntity
