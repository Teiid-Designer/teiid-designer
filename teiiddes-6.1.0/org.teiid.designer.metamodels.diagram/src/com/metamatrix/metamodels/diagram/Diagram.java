/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.diagram;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Diagram</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.Diagram#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.Diagram#getNotation <em>Notation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.Diagram#getLinkType <em>Link Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramEntity <em>Diagram Entity</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.Diagram#getTarget <em>Target</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramContainer <em>Diagram Container</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramLinks <em>Diagram Links</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagram()
 * @model
 * @generated
 */
public interface Diagram extends PresentationEntity{

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagram_Type()
     * @model
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.Diagram#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(String value);

    /**
     * Returns the value of the '<em><b>Notation</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Notation</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Notation</em>' attribute.
     * @see #setNotation(String)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagram_Notation()
     * @model
     * @generated
     */
    String getNotation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.Diagram#getNotation <em>Notation</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Notation</em>' attribute.
     * @see #getNotation()
     * @generated
     */
    void setNotation(String value);

    /**
     * Returns the value of the '<em><b>Link Type</b></em>' attribute.
     * The default value is <code>"ORTHOGONAL"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.diagram.DiagramLinkType}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Link Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Link Type</em>' attribute.
     * @see com.metamatrix.metamodels.diagram.DiagramLinkType
     * @see #setLinkType(DiagramLinkType)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagram_LinkType()
     * @model default="ORTHOGONAL"
     * @generated
     */
	DiagramLinkType getLinkType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.Diagram#getLinkType <em>Link Type</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Link Type</em>' attribute.
     * @see com.metamatrix.metamodels.diagram.DiagramLinkType
     * @see #getLinkType()
     * @generated
     */
	void setLinkType(DiagramLinkType value);

    /**
     * Returns the value of the '<em><b>Diagram Entity</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.diagram.DiagramEntity}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getDiagram <em>Diagram</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram Entity</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram Entity</em>' containment reference list.
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagram_DiagramEntity()
     * @see com.metamatrix.metamodels.diagram.DiagramEntity#getDiagram
     * @model type="com.metamatrix.metamodels.diagram.DiagramEntity" opposite="diagram" containment="true"
     * @generated
     */
    EList getDiagramEntity();

    /**
     * Returns the value of the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Target</em>' reference.
     * @see #setTarget(EObject)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagram_Target()
     * @model
     * @generated
     */
    EObject getTarget();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.Diagram#getTarget <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target</em>' reference.
     * @see #getTarget()
     * @generated
     */
    void setTarget(EObject value);

    /**
     * Returns the value of the '<em><b>Diagram Container</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.diagram.DiagramContainer#getDiagram <em>Diagram</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram Container</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram Container</em>' container reference.
     * @see #setDiagramContainer(DiagramContainer)
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagram_DiagramContainer()
     * @see com.metamatrix.metamodels.diagram.DiagramContainer#getDiagram
     * @model opposite="diagram"
     * @generated
     */
    DiagramContainer getDiagramContainer();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramContainer <em>Diagram Container</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Diagram Container</em>' container reference.
     * @see #getDiagramContainer()
     * @generated
     */
    void setDiagramContainer(DiagramContainer value);

    /**
     * Returns the value of the '<em><b>Diagram Links</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.diagram.DiagramLink}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.diagram.DiagramLink#getDiagram <em>Diagram</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Diagram Links</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Diagram Links</em>' containment reference list.
     * @see com.metamatrix.metamodels.diagram.DiagramPackage#getDiagram_DiagramLinks()
     * @see com.metamatrix.metamodels.diagram.DiagramLink#getDiagram
     * @model type="com.metamatrix.metamodels.diagram.DiagramLink" opposite="diagram" containment="true"
     * @generated
     */
    EList getDiagramLinks();

} // Diagram
