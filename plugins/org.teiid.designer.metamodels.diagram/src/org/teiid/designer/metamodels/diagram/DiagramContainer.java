/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.diagram;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.diagram.DiagramContainer#getDiagram <em>Diagram</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramContainer()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface DiagramContainer extends PresentationEntity{

    /**
     * Returns the value of the '<em><b>Diagram</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.diagram.Diagram}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.diagram.Diagram#getDiagramContainer <em>Diagram Container</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram</em>' containment reference list.
     * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramContainer_Diagram()
     * @see org.teiid.designer.metamodels.diagram.Diagram#getDiagramContainer
     * @model type="org.teiid.designer.metamodels.diagram.Diagram" opposite="diagramContainer" containment="true"
     * @generated
     */
    EList getDiagram();

} // DiagramContainer
