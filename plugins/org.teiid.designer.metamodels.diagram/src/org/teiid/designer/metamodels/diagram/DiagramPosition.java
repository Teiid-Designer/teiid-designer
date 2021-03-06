/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.diagram;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Position</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.diagram.DiagramPosition#getXPosition <em>XPosition</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.diagram.DiagramPosition#getYPosition <em>YPosition</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.diagram.DiagramPosition#getDiagramLink <em>Diagram Link</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramPosition()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface DiagramPosition extends EObject{

    /**
     * Returns the value of the '<em><b>XPosition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>XPosition</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>XPosition</em>' attribute.
     * @see #setXPosition(int)
     * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramPosition_XPosition()
     * @model
     * @generated
     */
    int getXPosition();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.diagram.DiagramPosition#getXPosition <em>XPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>XPosition</em>' attribute.
     * @see #getXPosition()
     * @generated
     */
    void setXPosition(int value);

    /**
     * Returns the value of the '<em><b>YPosition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>YPosition</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>YPosition</em>' attribute.
     * @see #setYPosition(int)
     * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramPosition_YPosition()
     * @model
     * @generated
     */
    int getYPosition();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.diagram.DiagramPosition#getYPosition <em>YPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>YPosition</em>' attribute.
     * @see #getYPosition()
     * @generated
     */
    void setYPosition(int value);

    /**
     * Returns the value of the '<em><b>Diagram Link</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.diagram.DiagramLink#getRoutePoints <em>Route Points</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram Link</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram Link</em>' container reference.
     * @see #setDiagramLink(DiagramLink)
     * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramPosition_DiagramLink()
     * @see org.teiid.designer.metamodels.diagram.DiagramLink#getRoutePoints
     * @model opposite="routePoints"
     * @generated
     */
    DiagramLink getDiagramLink();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.diagram.DiagramPosition#getDiagramLink <em>Diagram Link</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Diagram Link</em>' container reference.
     * @see #getDiagramLink()
     * @generated
     */
    void setDiagramLink(DiagramLink value);

} // DiagramPosition
