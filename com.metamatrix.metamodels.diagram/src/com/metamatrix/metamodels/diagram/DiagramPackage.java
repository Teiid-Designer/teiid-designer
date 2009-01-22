/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.diagram;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.diagram.DiagramFactory
 * @generated
 */
public interface DiagramPackage extends EPackage{

    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "diagram"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/Diagram"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "diagram"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    DiagramPackage eINSTANCE = com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.diagram.impl.PresentationEntityImpl <em>Presentation Entity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.diagram.impl.PresentationEntityImpl
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl#getPresentationEntity()
     * @generated
     */
    int PRESENTATION_ENTITY = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRESENTATION_ENTITY__NAME = 0;

    /**
     * The number of structural features of the the '<em>Presentation Entity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRESENTATION_ENTITY_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.diagram.impl.AbstractDiagramEntityImpl <em>Abstract Diagram Entity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.diagram.impl.AbstractDiagramEntityImpl
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl#getAbstractDiagramEntity()
     * @generated
     */
    int ABSTRACT_DIAGRAM_ENTITY = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_DIAGRAM_ENTITY__NAME = PRESENTATION_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Alias</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_DIAGRAM_ENTITY__ALIAS = PRESENTATION_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>User String</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_DIAGRAM_ENTITY__USER_STRING = PRESENTATION_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>User Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_DIAGRAM_ENTITY__USER_TYPE = PRESENTATION_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Model Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT = PRESENTATION_ENTITY_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Abstract Diagram Entity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT = PRESENTATION_ENTITY_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.diagram.impl.DiagramEntityImpl <em>Entity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.diagram.impl.DiagramEntityImpl
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl#getDiagramEntity()
     * @generated
     */
    int DIAGRAM_ENTITY = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__NAME = ABSTRACT_DIAGRAM_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Alias</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__ALIAS = ABSTRACT_DIAGRAM_ENTITY__ALIAS;

    /**
     * The feature id for the '<em><b>User String</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__USER_STRING = ABSTRACT_DIAGRAM_ENTITY__USER_STRING;

    /**
     * The feature id for the '<em><b>User Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__USER_TYPE = ABSTRACT_DIAGRAM_ENTITY__USER_TYPE;

    /**
     * The feature id for the '<em><b>Model Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__MODEL_OBJECT = ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT;

    /**
     * The feature id for the '<em><b>XPosition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__XPOSITION = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>YPosition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__YPOSITION = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Height</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__HEIGHT = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Width</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__WIDTH = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Diagram</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY__DIAGRAM = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Entity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_ENTITY_FEATURE_COUNT = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.diagram.impl.DiagramImpl <em>Diagram</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.diagram.impl.DiagramImpl
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl#getDiagram()
     * @generated
     */
    int DIAGRAM = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM__NAME = PRESENTATION_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM__TYPE = PRESENTATION_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Notation</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM__NOTATION = PRESENTATION_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Link Type</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DIAGRAM__LINK_TYPE = PRESENTATION_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Diagram Entity</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM__DIAGRAM_ENTITY = PRESENTATION_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM__TARGET = PRESENTATION_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Diagram Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM__DIAGRAM_CONTAINER = PRESENTATION_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Diagram Links</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM__DIAGRAM_LINKS = PRESENTATION_ENTITY_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the the '<em>Diagram</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_FEATURE_COUNT = PRESENTATION_ENTITY_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.diagram.impl.DiagramContainerImpl <em>Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.diagram.impl.DiagramContainerImpl
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl#getDiagramContainer()
     * @generated
     */
    int DIAGRAM_CONTAINER = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_CONTAINER__NAME = PRESENTATION_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Diagram</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_CONTAINER__DIAGRAM = PRESENTATION_ENTITY_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_CONTAINER_FEATURE_COUNT = PRESENTATION_ENTITY_FEATURE_COUNT + 1;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.diagram.impl.DiagramLinkImpl <em>Link</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.diagram.impl.DiagramLinkImpl
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl#getDiagramLink()
     * @generated
     */
    int DIAGRAM_LINK = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_LINK__NAME = ABSTRACT_DIAGRAM_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Alias</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_LINK__ALIAS = ABSTRACT_DIAGRAM_ENTITY__ALIAS;

    /**
     * The feature id for the '<em><b>User String</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_LINK__USER_STRING = ABSTRACT_DIAGRAM_ENTITY__USER_STRING;

    /**
     * The feature id for the '<em><b>User Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_LINK__USER_TYPE = ABSTRACT_DIAGRAM_ENTITY__USER_TYPE;

    /**
     * The feature id for the '<em><b>Model Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_LINK__MODEL_OBJECT = ABSTRACT_DIAGRAM_ENTITY__MODEL_OBJECT;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DIAGRAM_LINK__TYPE = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Diagram</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_LINK__DIAGRAM = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Route Points</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_LINK__ROUTE_POINTS = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Link</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_LINK_FEATURE_COUNT = ABSTRACT_DIAGRAM_ENTITY_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.diagram.impl.DiagramPositionImpl <em>Position</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPositionImpl
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl#getDiagramPosition()
     * @generated
     */
    int DIAGRAM_POSITION = 6;

    /**
     * The feature id for the '<em><b>XPosition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_POSITION__XPOSITION = 0;

    /**
     * The feature id for the '<em><b>YPosition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_POSITION__YPOSITION = 1;

    /**
     * The feature id for the '<em><b>Diagram Link</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_POSITION__DIAGRAM_LINK = 2;

    /**
     * The number of structural features of the the '<em>Position</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIAGRAM_POSITION_FEATURE_COUNT = 3;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.diagram.DiagramLinkType <em>Link Type</em>}' enum.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.diagram.DiagramLinkType
     * @see com.metamatrix.metamodels.diagram.impl.DiagramPackageImpl#getDiagramLinkType()
     * @generated
     */
	int DIAGRAM_LINK_TYPE = 7;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.diagram.DiagramEntity <em>Entity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Entity</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramEntity
     * @generated
     */
    EClass getDiagramEntity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getXPosition <em>XPosition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>XPosition</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramEntity#getXPosition()
     * @see #getDiagramEntity()
     * @generated
     */
    EAttribute getDiagramEntity_XPosition();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getYPosition <em>YPosition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>YPosition</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramEntity#getYPosition()
     * @see #getDiagramEntity()
     * @generated
     */
    EAttribute getDiagramEntity_YPosition();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getHeight <em>Height</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Height</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramEntity#getHeight()
     * @see #getDiagramEntity()
     * @generated
     */
    EAttribute getDiagramEntity_Height();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getWidth <em>Width</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Width</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramEntity#getWidth()
     * @see #getDiagramEntity()
     * @generated
     */
    EAttribute getDiagramEntity_Width();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.diagram.DiagramEntity#getDiagram <em>Diagram</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Diagram</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramEntity#getDiagram()
     * @see #getDiagramEntity()
     * @generated
     */
    EReference getDiagramEntity_Diagram();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.diagram.PresentationEntity <em>Presentation Entity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Presentation Entity</em>'.
     * @see com.metamatrix.metamodels.diagram.PresentationEntity
     * @generated
     */
    EClass getPresentationEntity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.PresentationEntity#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.diagram.PresentationEntity#getName()
     * @see #getPresentationEntity()
     * @generated
     */
    EAttribute getPresentationEntity_Name();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.diagram.Diagram <em>Diagram</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Diagram</em>'.
     * @see com.metamatrix.metamodels.diagram.Diagram
     * @generated
     */
    EClass getDiagram();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.Diagram#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.metamodels.diagram.Diagram#getType()
     * @see #getDiagram()
     * @generated
     */
    EAttribute getDiagram_Type();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.Diagram#getNotation <em>Notation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Notation</em>'.
     * @see com.metamatrix.metamodels.diagram.Diagram#getNotation()
     * @see #getDiagram()
     * @generated
     */
    EAttribute getDiagram_Notation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.Diagram#getLinkType <em>Link Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Link Type</em>'.
     * @see com.metamatrix.metamodels.diagram.Diagram#getLinkType()
     * @see #getDiagram()
     * @generated
     */
	EAttribute getDiagram_LinkType();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramEntity <em>Diagram Entity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Diagram Entity</em>'.
     * @see com.metamatrix.metamodels.diagram.Diagram#getDiagramEntity()
     * @see #getDiagram()
     * @generated
     */
    EReference getDiagram_DiagramEntity();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.diagram.Diagram#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target</em>'.
     * @see com.metamatrix.metamodels.diagram.Diagram#getTarget()
     * @see #getDiagram()
     * @generated
     */
    EReference getDiagram_Target();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramContainer <em>Diagram Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Diagram Container</em>'.
     * @see com.metamatrix.metamodels.diagram.Diagram#getDiagramContainer()
     * @see #getDiagram()
     * @generated
     */
    EReference getDiagram_DiagramContainer();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.diagram.Diagram#getDiagramLinks <em>Diagram Links</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Diagram Links</em>'.
     * @see com.metamatrix.metamodels.diagram.Diagram#getDiagramLinks()
     * @see #getDiagram()
     * @generated
     */
    EReference getDiagram_DiagramLinks();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.diagram.DiagramContainer <em>Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Container</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramContainer
     * @generated
     */
    EClass getDiagramContainer();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.diagram.DiagramContainer#getDiagram <em>Diagram</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Diagram</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramContainer#getDiagram()
     * @see #getDiagramContainer()
     * @generated
     */
    EReference getDiagramContainer_Diagram();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.diagram.DiagramLink <em>Link</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Link</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramLink
     * @generated
     */
    EClass getDiagramLink();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.DiagramLink#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramLink#getType()
     * @see #getDiagramLink()
     * @generated
     */
	EAttribute getDiagramLink_Type();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.diagram.DiagramLink#getDiagram <em>Diagram</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Diagram</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramLink#getDiagram()
     * @see #getDiagramLink()
     * @generated
     */
    EReference getDiagramLink_Diagram();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.diagram.DiagramLink#getRoutePoints <em>Route Points</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Route Points</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramLink#getRoutePoints()
     * @see #getDiagramLink()
     * @generated
     */
    EReference getDiagramLink_RoutePoints();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity <em>Abstract Diagram Entity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Abstract Diagram Entity</em>'.
     * @see com.metamatrix.metamodels.diagram.AbstractDiagramEntity
     * @generated
     */
    EClass getAbstractDiagramEntity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getAlias <em>Alias</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Alias</em>'.
     * @see com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getAlias()
     * @see #getAbstractDiagramEntity()
     * @generated
     */
    EAttribute getAbstractDiagramEntity_Alias();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getUserString <em>User String</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>User String</em>'.
     * @see com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getUserString()
     * @see #getAbstractDiagramEntity()
     * @generated
     */
    EAttribute getAbstractDiagramEntity_UserString();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getUserType <em>User Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>User Type</em>'.
     * @see com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getUserType()
     * @see #getAbstractDiagramEntity()
     * @generated
     */
    EAttribute getAbstractDiagramEntity_UserType();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getModelObject <em>Model Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Model Object</em>'.
     * @see com.metamatrix.metamodels.diagram.AbstractDiagramEntity#getModelObject()
     * @see #getAbstractDiagramEntity()
     * @generated
     */
    EReference getAbstractDiagramEntity_ModelObject();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.diagram.DiagramPosition <em>Position</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Position</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramPosition
     * @generated
     */
    EClass getDiagramPosition();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.DiagramPosition#getXPosition <em>XPosition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>XPosition</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramPosition#getXPosition()
     * @see #getDiagramPosition()
     * @generated
     */
    EAttribute getDiagramPosition_XPosition();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.diagram.DiagramPosition#getYPosition <em>YPosition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>YPosition</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramPosition#getYPosition()
     * @see #getDiagramPosition()
     * @generated
     */
    EAttribute getDiagramPosition_YPosition();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.diagram.DiagramPosition#getDiagramLink <em>Diagram Link</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Diagram Link</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramPosition#getDiagramLink()
     * @see #getDiagramPosition()
     * @generated
     */
    EReference getDiagramPosition_DiagramLink();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.diagram.DiagramLinkType <em>Link Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Link Type</em>'.
     * @see com.metamatrix.metamodels.diagram.DiagramLinkType
     * @generated
     */
	EEnum getDiagramLinkType();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    DiagramFactory getDiagramFactory();

} //DiagramPackage
