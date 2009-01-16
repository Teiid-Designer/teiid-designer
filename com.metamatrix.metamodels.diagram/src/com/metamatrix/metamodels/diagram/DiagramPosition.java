/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.diagram;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Position</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramPosition#getXPosition <em>XPosition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramPosition#getYPosition <em>YPosition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramPosition#getDiagramLink <em>Diagram Link</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramPosition()
 * @model
 * @generated
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
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramPosition_XPosition()
     * @model
     * @generated
     */
    int getXPosition();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramPosition#getXPosition <em>XPosition</em>}' attribute.
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
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramPosition_YPosition()
     * @model
     * @generated
     */
    int getYPosition();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramPosition#getYPosition <em>YPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>YPosition</em>' attribute.
     * @see #getYPosition()
     * @generated
     */
    void setYPosition(int value);

    /**
     * Returns the value of the '<em><b>Diagram Link</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.diagram.DiagramLink#getRoutePoints <em>Route Points</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram Link</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram Link</em>' container reference.
     * @see #setDiagramLink(DiagramLink)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramPosition_DiagramLink()
     * @see com.metamatrix.metamodels.diagram.DiagramLink#getRoutePoints
     * @model opposite="routePoints"
     * @generated
     */
    DiagramLink getDiagramLink();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramPosition#getDiagramLink <em>Diagram Link</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Diagram Link</em>' container reference.
     * @see #getDiagramLink()
     * @generated
     */
    void setDiagramLink(DiagramLink value);

} // DiagramPosition
