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
 * A representation of the model object '<em><b>Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.diagram.DiagramLink#getType <em>Type</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.diagram.DiagramLink#getDiagram <em>Diagram</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.diagram.DiagramLink#getRoutePoints <em>Route Points</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramLink()
 * @model
 * @generated
 */
public interface DiagramLink extends AbstractDiagramEntity{

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * The default value is <code>"ORTHOGONAL"</code>.
     * The literals are from the enumeration {@link org.teiid.designer.metamodels.diagram.DiagramLinkType}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see org.teiid.designer.metamodels.diagram.DiagramLinkType
     * @see #setType(DiagramLinkType)
     * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramLink_Type()
     * @model default="ORTHOGONAL"
     * @generated
     */
	DiagramLinkType getType();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.diagram.DiagramLink#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see org.teiid.designer.metamodels.diagram.DiagramLinkType
     * @see #getType()
     * @generated
     */
	void setType(DiagramLinkType value);

    /**
     * Returns the value of the '<em><b>Diagram</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.diagram.Diagram#getDiagramLinks <em>Diagram Links</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram</em>' container reference.
     * @see #setDiagram(Diagram)
     * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramLink_Diagram()
     * @see org.teiid.designer.metamodels.diagram.Diagram#getDiagramLinks
     * @model opposite="diagramLinks"
     * @generated
     */
    Diagram getDiagram();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.diagram.DiagramLink#getDiagram <em>Diagram</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Diagram</em>' container reference.
     * @see #getDiagram()
     * @generated
     */
    void setDiagram(Diagram value);

    /**
     * Returns the value of the '<em><b>Route Points</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.diagram.DiagramPosition}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.diagram.DiagramPosition#getDiagramLink <em>Diagram Link</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Route Points</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Route Points</em>' containment reference list.
     * @see org.teiid.designer.metamodels.diagram.DiagramPackage#getDiagramLink_RoutePoints()
     * @see org.teiid.designer.metamodels.diagram.DiagramPosition#getDiagramLink
     * @model type="org.teiid.designer.metamodels.diagram.DiagramPosition" opposite="diagramLink" containment="true"
     * @generated
     */
    EList getRoutePoints();

} // DiagramLink
