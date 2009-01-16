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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramLink#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramLink#getDiagram <em>Diagram</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramLink#getRoutePoints <em>Route Points</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramLink()
 * @model
 * @generated
 */
public interface DiagramLink extends AbstractDiagramEntity{

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * The default value is <code>"ORTHOGONAL"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.diagram.DiagramLinkType}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see com.metamatrix.metamodels.diagram.DiagramLinkType
     * @see #setType(DiagramLinkType)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramLink_Type()
     * @model default="ORTHOGONAL"
     * @generated
     */
	DiagramLinkType getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramLink#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see com.metamatrix.metamodels.diagram.DiagramLinkType
     * @see #getType()
     * @generated
     */
	void setType(DiagramLinkType value);

    /**
     * Returns the value of the '<em><b>Diagram</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramLinks <em>Diagram Links</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram</em>' container reference.
     * @see #setDiagram(Diagram)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramLink_Diagram()
     * @see com.metamatrix.metamodels.diagram.Diagram#getDiagramLinks
     * @model opposite="diagramLinks"
     * @generated
     */
    Diagram getDiagram();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.DiagramLink#getDiagram <em>Diagram</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Diagram</em>' container reference.
     * @see #getDiagram()
     * @generated
     */
    void setDiagram(Diagram value);

    /**
     * Returns the value of the '<em><b>Route Points</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.diagram.DiagramPosition}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.diagram.DiagramPosition#getDiagramLink <em>Diagram Link</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Route Points</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Route Points</em>' containment reference list.
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramLink_RoutePoints()
     * @see com.metamatrix.metamodels.diagram.DiagramPosition#getDiagramLink
     * @model type="com.metamatrix.metamodels.diagram.DiagramPosition" opposite="diagramLink" containment="true"
     * @generated
     */
    EList getRoutePoints();

} // DiagramLink
