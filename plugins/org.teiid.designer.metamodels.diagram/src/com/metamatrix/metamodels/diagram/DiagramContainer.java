/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.diagram;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.DiagramContainer#getDiagram <em>Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramContainer()
 * @model
 * @generated
 */
public interface DiagramContainer extends PresentationEntity{

    /**
     * Returns the value of the '<em><b>Diagram</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.diagram.Diagram}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramContainer <em>Diagram Container</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram</em>' containment reference list.
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagramContainer_Diagram()
     * @see com.metamatrix.metamodels.diagram.Diagram#getDiagramContainer
     * @model type="com.metamatrix.metamodels.diagram.Diagram" opposite="diagramContainer" containment="true"
     * @generated
     */
    EList getDiagram();

} // DiagramContainer
