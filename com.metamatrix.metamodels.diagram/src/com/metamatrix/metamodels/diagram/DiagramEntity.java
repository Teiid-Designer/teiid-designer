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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entity</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramEntity#getXPosition <em>XPosition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramEntity#getYPosition <em>YPosition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramEntity#getHeight <em>Height</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramEntity#getWidth <em>Width</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramEntity#getDiagram <em>Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramEntity()
 * @model
 * @generated
 */
public interface DiagramEntity extends AbstractDiagramEntity{

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
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramEntity_XPosition()
     * @model
     * @generated
     */
    int getXPosition();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getXPosition <em>XPosition</em>}' attribute.
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
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramEntity_YPosition()
     * @model
     * @generated
     */
    int getYPosition();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getYPosition <em>YPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>YPosition</em>' attribute.
     * @see #getYPosition()
     * @generated
     */
    void setYPosition(int value);

    /**
     * Returns the value of the '<em><b>Height</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Height</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Height</em>' attribute.
     * @see #setHeight(int)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramEntity_Height()
     * @model
     * @generated
     */
    int getHeight();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getHeight <em>Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Height</em>' attribute.
     * @see #getHeight()
     * @generated
     */
    void setHeight(int value);

    /**
     * Returns the value of the '<em><b>Width</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Width</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Width</em>' attribute.
     * @see #setWidth(int)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramEntity_Width()
     * @model
     * @generated
     */
    int getWidth();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getWidth <em>Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Width</em>' attribute.
     * @see #getWidth()
     * @generated
     */
    void setWidth(int value);

    /**
     * Returns the value of the '<em><b>Diagram</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramEntity <em>Diagram Entity</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram</em>' container reference.
     * @see #setDiagram(Diagram)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramEntity_Diagram()
     * @see com.metamatrix.metamodels.diagram.Diagram#getDiagramEntity
     * @model opposite="diagramEntity"
     * @generated
     */
    Diagram getDiagram();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getDiagram <em>Diagram</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Diagram</em>' container reference.
     * @see #getDiagram()
     * @generated
     */
    void setDiagram(Diagram value);

} // DiagramEntity
