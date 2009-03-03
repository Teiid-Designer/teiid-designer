/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @see com.metamatrix.metamodels.xml.XmlDocumentFactory
 * @generated
 */
public interface XmlDocumentPackage extends EPackage{
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "xml"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/XmlDocument"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "vxmldoc"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    XmlDocumentPackage eINSTANCE = com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.ProcessingInstructionHolder <em>Processing Instruction Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.ProcessingInstructionHolder
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getProcessingInstructionHolder()
     * @generated
     */
    int PROCESSING_INSTRUCTION_HOLDER = 15;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlFragmentImpl <em>Xml Fragment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlFragmentImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlFragment()
     * @generated
     */
    int XML_FRAGMENT = 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlDocumentImpl <em>Xml Document</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlDocument()
     * @generated
     */
    int XML_DOCUMENT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlDocumentEntityImpl <em>Entity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentEntityImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlDocumentEntity()
     * @generated
     */
    int XML_DOCUMENT_ENTITY = 2;

    /**
     * The number of structural features of the the '<em>Entity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT_ENTITY_FEATURE_COUNT = 0;

    /**
     * The feature id for the '<em><b>Comments</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT__COMMENTS = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Processing Instructions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT__PROCESSING_INSTRUCTIONS = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT__NAME = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Root</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT__ROOT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Xml Fragment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_FEATURE_COUNT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Comments</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__COMMENTS = XML_FRAGMENT__COMMENTS;

    /**
     * The feature id for the '<em><b>Processing Instructions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__PROCESSING_INSTRUCTIONS = XML_FRAGMENT__PROCESSING_INSTRUCTIONS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__NAME = XML_FRAGMENT__NAME;

    /**
     * The feature id for the '<em><b>Root</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__ROOT = XML_FRAGMENT__ROOT;

    /**
     * The feature id for the '<em><b>Encoding</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__ENCODING = XML_FRAGMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Formatted</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__FORMATTED = XML_FRAGMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__VERSION = XML_FRAGMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Standalone</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__STANDALONE = XML_FRAGMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Soap Encoding</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT__SOAP_ENCODING = XML_FRAGMENT_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Xml Document</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT_FEATURE_COUNT = XML_FRAGMENT_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl <em>Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentNodeImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlDocumentNode()
     * @generated
     */
    int XML_DOCUMENT_NODE = 5;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT_NODE__BUILD_STATE = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT_NODE__NAME = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_DOCUMENT_NODE__MIN_OCCURS = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_DOCUMENT_NODE__MAX_OCCURS = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT_NODE__XSD_COMPONENT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT_NODE__NAMESPACE = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the the '<em>Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_DOCUMENT_NODE_FEATURE_COUNT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlBaseElementImpl <em>Xml Base Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlBaseElementImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlBaseElement()
     * @generated
     */
    int XML_BASE_ELEMENT = 18;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT__BUILD_STATE = XML_DOCUMENT_NODE__BUILD_STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT__NAME = XML_DOCUMENT_NODE__NAME;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT = XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_BASE_ELEMENT__MIN_OCCURS = XML_DOCUMENT_NODE__MIN_OCCURS;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_BASE_ELEMENT__MAX_OCCURS = XML_DOCUMENT_NODE__MAX_OCCURS;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT__XSD_COMPONENT = XML_DOCUMENT_NODE__XSD_COMPONENT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT__NAMESPACE = XML_DOCUMENT_NODE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT__CHOICE_CRITERIA = XML_DOCUMENT_NODE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_BASE_ELEMENT__CHOICE_ORDER = XML_DOCUMENT_NODE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT__DEFAULT_FOR = XML_DOCUMENT_NODE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT__PARENT = XML_DOCUMENT_NODE_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Xml Base Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BASE_ELEMENT_FEATURE_COUNT = XML_DOCUMENT_NODE_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlElementImpl <em>Xml Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlElementImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlElement()
     * @generated
     */
    int XML_ELEMENT = 3;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__BUILD_STATE = XML_BASE_ELEMENT__BUILD_STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__NAME = XML_BASE_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__EXCLUDE_FROM_DOCUMENT = XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ELEMENT__MIN_OCCURS = XML_BASE_ELEMENT__MIN_OCCURS;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ELEMENT__MAX_OCCURS = XML_BASE_ELEMENT__MAX_OCCURS;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__XSD_COMPONENT = XML_BASE_ELEMENT__XSD_COMPONENT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__NAMESPACE = XML_BASE_ELEMENT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__CHOICE_CRITERIA = XML_BASE_ELEMENT__CHOICE_CRITERIA;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ELEMENT__CHOICE_ORDER = XML_BASE_ELEMENT__CHOICE_ORDER;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__DEFAULT_FOR = XML_BASE_ELEMENT__DEFAULT_FOR;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__PARENT = XML_BASE_ELEMENT__PARENT;

    /**
     * The feature id for the '<em><b>Comments</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__COMMENTS = XML_BASE_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Processing Instructions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__PROCESSING_INSTRUCTIONS = XML_BASE_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__ELEMENTS = XML_BASE_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Containers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__CONTAINERS = XML_BASE_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__VALUE = XML_BASE_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Value Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__VALUE_TYPE = XML_BASE_ELEMENT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Recursive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__RECURSIVE = XML_BASE_ELEMENT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__ATTRIBUTES = XML_BASE_ELEMENT_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT__DECLARED_NAMESPACES = XML_BASE_ELEMENT_FEATURE_COUNT + 8;

    /**
     * The number of structural features of the the '<em>Xml Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT_FEATURE_COUNT = XML_BASE_ELEMENT_FEATURE_COUNT + 9;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlAttributeImpl <em>Xml Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlAttributeImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlAttribute()
     * @generated
     */
    int XML_ATTRIBUTE = 4;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__BUILD_STATE = XML_DOCUMENT_NODE__BUILD_STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__NAME = XML_DOCUMENT_NODE__NAME;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__EXCLUDE_FROM_DOCUMENT = XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ATTRIBUTE__MIN_OCCURS = XML_DOCUMENT_NODE__MIN_OCCURS;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ATTRIBUTE__MAX_OCCURS = XML_DOCUMENT_NODE__MAX_OCCURS;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__XSD_COMPONENT = XML_DOCUMENT_NODE__XSD_COMPONENT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__NAMESPACE = XML_DOCUMENT_NODE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__VALUE = XML_DOCUMENT_NODE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Value Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__VALUE_TYPE = XML_DOCUMENT_NODE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Use</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__USE = XML_DOCUMENT_NODE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Element</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE__ELEMENT = XML_DOCUMENT_NODE_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Xml Attribute</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ATTRIBUTE_FEATURE_COUNT = XML_DOCUMENT_NODE_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlRootImpl <em>Xml Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlRootImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlRoot()
     * @generated
     */
    int XML_ROOT = 6;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__BUILD_STATE = XML_ELEMENT__BUILD_STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__NAME = XML_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__EXCLUDE_FROM_DOCUMENT = XML_ELEMENT__EXCLUDE_FROM_DOCUMENT;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ROOT__MIN_OCCURS = XML_ELEMENT__MIN_OCCURS;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ROOT__MAX_OCCURS = XML_ELEMENT__MAX_OCCURS;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__XSD_COMPONENT = XML_ELEMENT__XSD_COMPONENT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__NAMESPACE = XML_ELEMENT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__CHOICE_CRITERIA = XML_ELEMENT__CHOICE_CRITERIA;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ROOT__CHOICE_ORDER = XML_ELEMENT__CHOICE_ORDER;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__DEFAULT_FOR = XML_ELEMENT__DEFAULT_FOR;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__PARENT = XML_ELEMENT__PARENT;

    /**
     * The feature id for the '<em><b>Comments</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__COMMENTS = XML_ELEMENT__COMMENTS;

    /**
     * The feature id for the '<em><b>Processing Instructions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__PROCESSING_INSTRUCTIONS = XML_ELEMENT__PROCESSING_INSTRUCTIONS;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__ELEMENTS = XML_ELEMENT__ELEMENTS;

    /**
     * The feature id for the '<em><b>Containers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__CONTAINERS = XML_ELEMENT__CONTAINERS;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__VALUE = XML_ELEMENT__VALUE;

    /**
     * The feature id for the '<em><b>Value Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__VALUE_TYPE = XML_ELEMENT__VALUE_TYPE;

    /**
     * The feature id for the '<em><b>Recursive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__RECURSIVE = XML_ELEMENT__RECURSIVE;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__ATTRIBUTES = XML_ELEMENT__ATTRIBUTES;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__DECLARED_NAMESPACES = XML_ELEMENT__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Fragment</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT__FRAGMENT = XML_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Xml Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ROOT_FEATURE_COUNT = XML_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlCommentImpl <em>Xml Comment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlCommentImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlComment()
     * @generated
     */
    int XML_COMMENT = 7;

    /**
     * The feature id for the '<em><b>Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_COMMENT__TEXT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_COMMENT__PARENT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Xml Comment</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_COMMENT_FEATURE_COUNT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlNamespaceImpl <em>Xml Namespace</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlNamespaceImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlNamespace()
     * @generated
     */
    int XML_NAMESPACE = 8;

    /**
     * The feature id for the '<em><b>Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_NAMESPACE__PREFIX = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_NAMESPACE__URI = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Element</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_NAMESPACE__ELEMENT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Xml Namespace</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_NAMESPACE_FEATURE_COUNT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.XmlElementHolder <em>Xml Element Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.XmlElementHolder
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlElementHolder()
     * @generated
     */
    int XML_ELEMENT_HOLDER = 16;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl <em>Xml Container Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlContainerNodeImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlContainerNode()
     * @generated
     */
    int XML_CONTAINER_NODE = 9;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE__ELEMENTS = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Containers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE__CONTAINERS = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE__CHOICE_CRITERIA = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_CONTAINER_NODE__CHOICE_ORDER = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE__DEFAULT_FOR = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE__BUILD_STATE = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_CONTAINER_NODE__MIN_OCCURS = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_CONTAINER_NODE__MAX_OCCURS = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE__XSD_COMPONENT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE__PARENT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 10;

    /**
     * The number of structural features of the the '<em>Xml Container Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_NODE_FEATURE_COUNT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 11;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlSequenceImpl <em>Xml Sequence</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlSequenceImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlSequence()
     * @generated
     */
    int XML_SEQUENCE = 10;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE__ELEMENTS = XML_CONTAINER_NODE__ELEMENTS;

    /**
     * The feature id for the '<em><b>Containers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE__CONTAINERS = XML_CONTAINER_NODE__CONTAINERS;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE__CHOICE_CRITERIA = XML_CONTAINER_NODE__CHOICE_CRITERIA;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_SEQUENCE__CHOICE_ORDER = XML_CONTAINER_NODE__CHOICE_ORDER;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE__DEFAULT_FOR = XML_CONTAINER_NODE__DEFAULT_FOR;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE__BUILD_STATE = XML_CONTAINER_NODE__BUILD_STATE;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE__EXCLUDE_FROM_DOCUMENT = XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_SEQUENCE__MIN_OCCURS = XML_CONTAINER_NODE__MIN_OCCURS;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_SEQUENCE__MAX_OCCURS = XML_CONTAINER_NODE__MAX_OCCURS;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE__XSD_COMPONENT = XML_CONTAINER_NODE__XSD_COMPONENT;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE__PARENT = XML_CONTAINER_NODE__PARENT;

    /**
     * The number of structural features of the the '<em>Xml Sequence</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SEQUENCE_FEATURE_COUNT = XML_CONTAINER_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlAllImpl <em>Xml All</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlAllImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlAll()
     * @generated
     */
    int XML_ALL = 11;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL__ELEMENTS = XML_CONTAINER_NODE__ELEMENTS;

    /**
     * The feature id for the '<em><b>Containers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL__CONTAINERS = XML_CONTAINER_NODE__CONTAINERS;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL__CHOICE_CRITERIA = XML_CONTAINER_NODE__CHOICE_CRITERIA;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ALL__CHOICE_ORDER = XML_CONTAINER_NODE__CHOICE_ORDER;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL__DEFAULT_FOR = XML_CONTAINER_NODE__DEFAULT_FOR;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL__BUILD_STATE = XML_CONTAINER_NODE__BUILD_STATE;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL__EXCLUDE_FROM_DOCUMENT = XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ALL__MIN_OCCURS = XML_CONTAINER_NODE__MIN_OCCURS;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_ALL__MAX_OCCURS = XML_CONTAINER_NODE__MAX_OCCURS;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL__XSD_COMPONENT = XML_CONTAINER_NODE__XSD_COMPONENT;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL__PARENT = XML_CONTAINER_NODE__PARENT;

    /**
     * The number of structural features of the the '<em>Xml All</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ALL_FEATURE_COUNT = XML_CONTAINER_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlChoiceImpl <em>Xml Choice</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlChoiceImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlChoice()
     * @generated
     */
    int XML_CHOICE = 12;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__ELEMENTS = XML_CONTAINER_NODE__ELEMENTS;

    /**
     * The feature id for the '<em><b>Containers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__CONTAINERS = XML_CONTAINER_NODE__CONTAINERS;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__CHOICE_CRITERIA = XML_CONTAINER_NODE__CHOICE_CRITERIA;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_CHOICE__CHOICE_ORDER = XML_CONTAINER_NODE__CHOICE_ORDER;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__DEFAULT_FOR = XML_CONTAINER_NODE__DEFAULT_FOR;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__BUILD_STATE = XML_CONTAINER_NODE__BUILD_STATE;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__EXCLUDE_FROM_DOCUMENT = XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_CHOICE__MIN_OCCURS = XML_CONTAINER_NODE__MIN_OCCURS;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_CHOICE__MAX_OCCURS = XML_CONTAINER_NODE__MAX_OCCURS;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__XSD_COMPONENT = XML_CONTAINER_NODE__XSD_COMPONENT;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__PARENT = XML_CONTAINER_NODE__PARENT;

    /**
     * The feature id for the '<em><b>Default Error Mode</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__DEFAULT_ERROR_MODE = XML_CONTAINER_NODE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Default Option</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE__DEFAULT_OPTION = XML_CONTAINER_NODE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Xml Choice</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CHOICE_FEATURE_COUNT = XML_CONTAINER_NODE_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.XmlCommentHolder <em>Xml Comment Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.XmlCommentHolder
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlCommentHolder()
     * @generated
     */
    int XML_COMMENT_HOLDER = 13;

    /**
     * The feature id for the '<em><b>Comments</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_COMMENT_HOLDER__COMMENTS = 0;

    /**
     * The number of structural features of the the '<em>Xml Comment Holder</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_COMMENT_HOLDER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.ProcessingInstructionImpl <em>Processing Instruction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.ProcessingInstructionImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getProcessingInstruction()
     * @generated
     */
    int PROCESSING_INSTRUCTION = 14;

    /**
     * The feature id for the '<em><b>Raw Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCESSING_INSTRUCTION__RAW_TEXT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Target</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCESSING_INSTRUCTION__TARGET = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCESSING_INSTRUCTION__PARENT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Processing Instruction</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCESSING_INSTRUCTION_FEATURE_COUNT = XML_DOCUMENT_ENTITY_FEATURE_COUNT + 3;


    /**
     * The feature id for the '<em><b>Processing Instructions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCESSING_INSTRUCTION_HOLDER__PROCESSING_INSTRUCTIONS = 0;

    /**
     * The number of structural features of the the '<em>Processing Instruction Holder</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCESSING_INSTRUCTION_HOLDER_FEATURE_COUNT = 1;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT_HOLDER__ELEMENTS = 0;

    /**
     * The number of structural features of the the '<em>Xml Element Holder</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_ELEMENT_HOLDER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.impl.XmlFragmentUseImpl <em>Xml Fragment Use</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.impl.XmlFragmentUseImpl
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlFragmentUse()
     * @generated
     */
    int XML_FRAGMENT_USE = 17;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__BUILD_STATE = XML_BASE_ELEMENT__BUILD_STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__NAME = XML_BASE_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Exclude From Document</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__EXCLUDE_FROM_DOCUMENT = XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT;

    /**
     * The feature id for the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_FRAGMENT_USE__MIN_OCCURS = XML_BASE_ELEMENT__MIN_OCCURS;

    /**
     * The feature id for the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_FRAGMENT_USE__MAX_OCCURS = XML_BASE_ELEMENT__MAX_OCCURS;

    /**
     * The feature id for the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__XSD_COMPONENT = XML_BASE_ELEMENT__XSD_COMPONENT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__NAMESPACE = XML_BASE_ELEMENT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__CHOICE_CRITERIA = XML_BASE_ELEMENT__CHOICE_CRITERIA;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int XML_FRAGMENT_USE__CHOICE_ORDER = XML_BASE_ELEMENT__CHOICE_ORDER;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__DEFAULT_FOR = XML_BASE_ELEMENT__DEFAULT_FOR;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__PARENT = XML_BASE_ELEMENT__PARENT;

    /**
     * The feature id for the '<em><b>Fragment</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE__FRAGMENT = XML_BASE_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Xml Fragment Use</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_FRAGMENT_USE_FEATURE_COUNT = XML_BASE_ELEMENT_FEATURE_COUNT + 1;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.XmlContainerHolder <em>Xml Container Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.XmlContainerHolder
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlContainerHolder()
     * @generated
     */
    int XML_CONTAINER_HOLDER = 19;

    /**
     * The feature id for the '<em><b>Containers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_HOLDER__CONTAINERS = 0;

    /**
     * The number of structural features of the the '<em>Xml Container Holder</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_CONTAINER_HOLDER_FEATURE_COUNT = 1;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.ChoiceOption <em>Choice Option</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.ChoiceOption
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getChoiceOption()
     * @generated
     */
    int CHOICE_OPTION = 20;

    /**
     * The feature id for the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE_OPTION__CHOICE_CRITERIA = 0;

    /**
     * The feature id for the '<em><b>Choice Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int CHOICE_OPTION__CHOICE_ORDER = 1;

    /**
     * The feature id for the '<em><b>Default For</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE_OPTION__DEFAULT_FOR = 2;

    /**
     * The number of structural features of the the '<em>Choice Option</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHOICE_OPTION_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.XmlValueHolder <em>Xml Value Holder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.XmlValueHolder
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlValueHolder()
     * @generated
     */
    int XML_VALUE_HOLDER = 21;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_VALUE_HOLDER__VALUE = 0;

    /**
     * The feature id for the '<em><b>Value Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_VALUE_HOLDER__VALUE_TYPE = 1;

    /**
     * The number of structural features of the the '<em>Xml Value Holder</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_VALUE_HOLDER_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.XmlBuildable <em>Xml Buildable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.XmlBuildable
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getXmlBuildable()
     * @generated
     */
    int XML_BUILDABLE = 22;

    /**
     * The feature id for the '<em><b>Build State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BUILDABLE__BUILD_STATE = 0;

    /**
     * The number of structural features of the the '<em>Xml Buildable</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_BUILDABLE_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.SoapEncoding <em>Soap Encoding</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.SoapEncoding
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getSoapEncoding()
     * @generated
     */
    int SOAP_ENCODING = 23;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.ChoiceErrorMode <em>Choice Error Mode</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.ChoiceErrorMode
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getChoiceErrorMode()
     * @generated
     */
    int CHOICE_ERROR_MODE = 24;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.ValueType <em>Value Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.ValueType
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getValueType()
     * @generated
     */
    int VALUE_TYPE = 25;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.BuildStatus <em>Build Status</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.BuildStatus
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getBuildStatus()
     * @generated
     */
    int BUILD_STATUS = 26;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xml.NormalizationType <em>Normalization Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xml.NormalizationType
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getNormalizationType()
     * @generated
     */
    int NORMALIZATION_TYPE = 27;

    /**
     * The meta object id for the '<em>List</em>' data type.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see java.util.List
     * @see com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl#getList()
     * @generated
     */
	int LIST = 28;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlFragment <em>Xml Fragment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Fragment</em>'.
     * @see com.metamatrix.metamodels.xml.XmlFragment
     * @generated
     */
    EClass getXmlFragment();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlFragment#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.xml.XmlFragment#getName()
     * @see #getXmlFragment()
     * @generated
     */
    EAttribute getXmlFragment_Name();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.xml.XmlFragment#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Root</em>'.
     * @see com.metamatrix.metamodels.xml.XmlFragment#getRoot()
     * @see #getXmlFragment()
     * @generated
     */
    EReference getXmlFragment_Root();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlDocument <em>Xml Document</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Document</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocument
     * @generated
     */
    EClass getXmlDocument();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocument#getEncoding <em>Encoding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Encoding</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocument#getEncoding()
     * @see #getXmlDocument()
     * @generated
     */
    EAttribute getXmlDocument_Encoding();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocument#isFormatted <em>Formatted</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Formatted</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocument#isFormatted()
     * @see #getXmlDocument()
     * @generated
     */
    EAttribute getXmlDocument_Formatted();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocument#getVersion <em>Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Version</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocument#getVersion()
     * @see #getXmlDocument()
     * @generated
     */
    EAttribute getXmlDocument_Version();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocument#isStandalone <em>Standalone</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Standalone</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocument#isStandalone()
     * @see #getXmlDocument()
     * @generated
     */
    EAttribute getXmlDocument_Standalone();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocument#getSoapEncoding <em>Soap Encoding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Soap Encoding</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocument#getSoapEncoding()
     * @see #getXmlDocument()
     * @generated
     */
    EAttribute getXmlDocument_SoapEncoding();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlDocumentEntity <em>Entity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Entity</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocumentEntity
     * @generated
     */
    EClass getXmlDocumentEntity();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlElement <em>Xml Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Element</em>'.
     * @see com.metamatrix.metamodels.xml.XmlElement
     * @generated
     */
    EClass getXmlElement();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlElement#isRecursive <em>Recursive</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Recursive</em>'.
     * @see com.metamatrix.metamodels.xml.XmlElement#isRecursive()
     * @see #getXmlElement()
     * @generated
     */
    EAttribute getXmlElement_Recursive();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.xml.XmlElement#getAttributes <em>Attributes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Attributes</em>'.
     * @see com.metamatrix.metamodels.xml.XmlElement#getAttributes()
     * @see #getXmlElement()
     * @generated
     */
    EReference getXmlElement_Attributes();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.xml.XmlElement#getDeclaredNamespaces <em>Declared Namespaces</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Declared Namespaces</em>'.
     * @see com.metamatrix.metamodels.xml.XmlElement#getDeclaredNamespaces()
     * @see #getXmlElement()
     * @generated
     */
    EReference getXmlElement_DeclaredNamespaces();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlAttribute <em>Xml Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Attribute</em>'.
     * @see com.metamatrix.metamodels.xml.XmlAttribute
     * @generated
     */
    EClass getXmlAttribute();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlAttribute#getUse <em>Use</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Use</em>'.
     * @see com.metamatrix.metamodels.xml.XmlAttribute#getUse()
     * @see #getXmlAttribute()
     * @generated
     */
    EAttribute getXmlAttribute_Use();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xml.XmlAttribute#getElement <em>Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Element</em>'.
     * @see com.metamatrix.metamodels.xml.XmlAttribute#getElement()
     * @see #getXmlAttribute()
     * @generated
     */
    EReference getXmlAttribute_Element();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlDocumentNode <em>Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Node</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode
     * @generated
     */
    EClass getXmlDocumentNode();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode#getName()
     * @see #getXmlDocumentNode()
     * @generated
     */
    EAttribute getXmlDocumentNode_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#isExcludeFromDocument <em>Exclude From Document</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Exclude From Document</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode#isExcludeFromDocument()
     * @see #getXmlDocumentNode()
     * @generated
     */
    EAttribute getXmlDocumentNode_ExcludeFromDocument();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getMinOccurs <em>Min Occurs</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Min Occurs</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode#getMinOccurs()
     * @see #getXmlDocumentNode()
     * @generated
     */
	EAttribute getXmlDocumentNode_MinOccurs();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getMaxOccurs <em>Max Occurs</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Max Occurs</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode#getMaxOccurs()
     * @see #getXmlDocumentNode()
     * @generated
     */
	EAttribute getXmlDocumentNode_MaxOccurs();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getXsdComponent <em>Xsd Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Xsd Component</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode#getXsdComponent()
     * @see #getXmlDocumentNode()
     * @generated
     */
    EReference getXmlDocumentNode_XsdComponent();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getNamespace <em>Namespace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Namespace</em>'.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode#getNamespace()
     * @see #getXmlDocumentNode()
     * @generated
     */
    EReference getXmlDocumentNode_Namespace();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlRoot <em>Xml Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Root</em>'.
     * @see com.metamatrix.metamodels.xml.XmlRoot
     * @generated
     */
    EClass getXmlRoot();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xml.XmlRoot#getFragment <em>Fragment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Fragment</em>'.
     * @see com.metamatrix.metamodels.xml.XmlRoot#getFragment()
     * @see #getXmlRoot()
     * @generated
     */
    EReference getXmlRoot_Fragment();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlComment <em>Xml Comment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Comment</em>'.
     * @see com.metamatrix.metamodels.xml.XmlComment
     * @generated
     */
    EClass getXmlComment();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlComment#getText <em>Text</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Text</em>'.
     * @see com.metamatrix.metamodels.xml.XmlComment#getText()
     * @see #getXmlComment()
     * @generated
     */
    EAttribute getXmlComment_Text();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xml.XmlComment#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see com.metamatrix.metamodels.xml.XmlComment#getParent()
     * @see #getXmlComment()
     * @generated
     */
    EReference getXmlComment_Parent();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlNamespace <em>Xml Namespace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Namespace</em>'.
     * @see com.metamatrix.metamodels.xml.XmlNamespace
     * @generated
     */
    EClass getXmlNamespace();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlNamespace#getPrefix <em>Prefix</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Prefix</em>'.
     * @see com.metamatrix.metamodels.xml.XmlNamespace#getPrefix()
     * @see #getXmlNamespace()
     * @generated
     */
    EAttribute getXmlNamespace_Prefix();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlNamespace#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @see com.metamatrix.metamodels.xml.XmlNamespace#getUri()
     * @see #getXmlNamespace()
     * @generated
     */
    EAttribute getXmlNamespace_Uri();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xml.XmlNamespace#getElement <em>Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Element</em>'.
     * @see com.metamatrix.metamodels.xml.XmlNamespace#getElement()
     * @see #getXmlNamespace()
     * @generated
     */
    EReference getXmlNamespace_Element();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlContainerNode <em>Xml Container Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Container Node</em>'.
     * @see com.metamatrix.metamodels.xml.XmlContainerNode
     * @generated
     */
    EClass getXmlContainerNode();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlContainerNode#isExcludeFromDocument <em>Exclude From Document</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Exclude From Document</em>'.
     * @see com.metamatrix.metamodels.xml.XmlContainerNode#isExcludeFromDocument()
     * @see #getXmlContainerNode()
     * @generated
     */
    EAttribute getXmlContainerNode_ExcludeFromDocument();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getMinOccurs <em>Min Occurs</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Min Occurs</em>'.
     * @see com.metamatrix.metamodels.xml.XmlContainerNode#getMinOccurs()
     * @see #getXmlContainerNode()
     * @generated
     */
	EAttribute getXmlContainerNode_MinOccurs();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getMaxOccurs <em>Max Occurs</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Max Occurs</em>'.
     * @see com.metamatrix.metamodels.xml.XmlContainerNode#getMaxOccurs()
     * @see #getXmlContainerNode()
     * @generated
     */
	EAttribute getXmlContainerNode_MaxOccurs();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getXsdComponent <em>Xsd Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Xsd Component</em>'.
     * @see com.metamatrix.metamodels.xml.XmlContainerNode#getXsdComponent()
     * @see #getXmlContainerNode()
     * @generated
     */
    EReference getXmlContainerNode_XsdComponent();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see com.metamatrix.metamodels.xml.XmlContainerNode#getParent()
     * @see #getXmlContainerNode()
     * @generated
     */
    EReference getXmlContainerNode_Parent();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlSequence <em>Xml Sequence</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Sequence</em>'.
     * @see com.metamatrix.metamodels.xml.XmlSequence
     * @generated
     */
    EClass getXmlSequence();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlAll <em>Xml All</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml All</em>'.
     * @see com.metamatrix.metamodels.xml.XmlAll
     * @generated
     */
    EClass getXmlAll();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlChoice <em>Xml Choice</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Choice</em>'.
     * @see com.metamatrix.metamodels.xml.XmlChoice
     * @generated
     */
    EClass getXmlChoice();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlChoice#getDefaultErrorMode <em>Default Error Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Default Error Mode</em>'.
     * @see com.metamatrix.metamodels.xml.XmlChoice#getDefaultErrorMode()
     * @see #getXmlChoice()
     * @generated
     */
    EAttribute getXmlChoice_DefaultErrorMode();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.xml.XmlChoice#getDefaultOption <em>Default Option</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Default Option</em>'.
     * @see com.metamatrix.metamodels.xml.XmlChoice#getDefaultOption()
     * @see #getXmlChoice()
     * @generated
     */
    EReference getXmlChoice_DefaultOption();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlCommentHolder <em>Xml Comment Holder</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Comment Holder</em>'.
     * @see com.metamatrix.metamodels.xml.XmlCommentHolder
     * @generated
     */
    EClass getXmlCommentHolder();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.xml.XmlCommentHolder#getComments <em>Comments</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Comments</em>'.
     * @see com.metamatrix.metamodels.xml.XmlCommentHolder#getComments()
     * @see #getXmlCommentHolder()
     * @generated
     */
    EReference getXmlCommentHolder_Comments();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.ProcessingInstruction <em>Processing Instruction</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Processing Instruction</em>'.
     * @see com.metamatrix.metamodels.xml.ProcessingInstruction
     * @generated
     */
    EClass getProcessingInstruction();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getRawText <em>Raw Text</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Raw Text</em>'.
     * @see com.metamatrix.metamodels.xml.ProcessingInstruction#getRawText()
     * @see #getProcessingInstruction()
     * @generated
     */
    EAttribute getProcessingInstruction_RawText();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Target</em>'.
     * @see com.metamatrix.metamodels.xml.ProcessingInstruction#getTarget()
     * @see #getProcessingInstruction()
     * @generated
     */
    EAttribute getProcessingInstruction_Target();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xml.ProcessingInstruction#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see com.metamatrix.metamodels.xml.ProcessingInstruction#getParent()
     * @see #getProcessingInstruction()
     * @generated
     */
    EReference getProcessingInstruction_Parent();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.ProcessingInstructionHolder <em>Processing Instruction Holder</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Processing Instruction Holder</em>'.
     * @see com.metamatrix.metamodels.xml.ProcessingInstructionHolder
     * @generated
     */
    EClass getProcessingInstructionHolder();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.xml.ProcessingInstructionHolder#getProcessingInstructions <em>Processing Instructions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Processing Instructions</em>'.
     * @see com.metamatrix.metamodels.xml.ProcessingInstructionHolder#getProcessingInstructions()
     * @see #getProcessingInstructionHolder()
     * @generated
     */
    EReference getProcessingInstructionHolder_ProcessingInstructions();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlElementHolder <em>Xml Element Holder</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Element Holder</em>'.
     * @see com.metamatrix.metamodels.xml.XmlElementHolder
     * @generated
     */
    EClass getXmlElementHolder();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.xml.XmlElementHolder#getElements <em>Elements</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Elements</em>'.
     * @see com.metamatrix.metamodels.xml.XmlElementHolder#getElements()
     * @see #getXmlElementHolder()
     * @generated
     */
    EReference getXmlElementHolder_Elements();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlFragmentUse <em>Xml Fragment Use</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Fragment Use</em>'.
     * @see com.metamatrix.metamodels.xml.XmlFragmentUse
     * @generated
     */
    EClass getXmlFragmentUse();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.xml.XmlFragmentUse#getFragment <em>Fragment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Fragment</em>'.
     * @see com.metamatrix.metamodels.xml.XmlFragmentUse#getFragment()
     * @see #getXmlFragmentUse()
     * @generated
     */
    EReference getXmlFragmentUse_Fragment();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlBaseElement <em>Xml Base Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Base Element</em>'.
     * @see com.metamatrix.metamodels.xml.XmlBaseElement
     * @generated
     */
    EClass getXmlBaseElement();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xml.XmlBaseElement#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see com.metamatrix.metamodels.xml.XmlBaseElement#getParent()
     * @see #getXmlBaseElement()
     * @generated
     */
    EReference getXmlBaseElement_Parent();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlContainerHolder <em>Xml Container Holder</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Container Holder</em>'.
     * @see com.metamatrix.metamodels.xml.XmlContainerHolder
     * @generated
     */
    EClass getXmlContainerHolder();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.xml.XmlContainerHolder#getContainers <em>Containers</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Containers</em>'.
     * @see com.metamatrix.metamodels.xml.XmlContainerHolder#getContainers()
     * @see #getXmlContainerHolder()
     * @generated
     */
    EReference getXmlContainerHolder_Containers();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.ChoiceOption <em>Choice Option</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Choice Option</em>'.
     * @see com.metamatrix.metamodels.xml.ChoiceOption
     * @generated
     */
    EClass getChoiceOption();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.ChoiceOption#getChoiceCriteria <em>Choice Criteria</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Choice Criteria</em>'.
     * @see com.metamatrix.metamodels.xml.ChoiceOption#getChoiceCriteria()
     * @see #getChoiceOption()
     * @generated
     */
    EAttribute getChoiceOption_ChoiceCriteria();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.ChoiceOption#getChoiceOrder <em>Choice Order</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Choice Order</em>'.
     * @see com.metamatrix.metamodels.xml.ChoiceOption#getChoiceOrder()
     * @see #getChoiceOption()
     * @generated
     */
	EAttribute getChoiceOption_ChoiceOrder();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.xml.ChoiceOption#getDefaultFor <em>Default For</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Default For</em>'.
     * @see com.metamatrix.metamodels.xml.ChoiceOption#getDefaultFor()
     * @see #getChoiceOption()
     * @generated
     */
    EReference getChoiceOption_DefaultFor();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlValueHolder <em>Xml Value Holder</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Value Holder</em>'.
     * @see com.metamatrix.metamodels.xml.XmlValueHolder
     * @generated
     */
    EClass getXmlValueHolder();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlValueHolder#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see com.metamatrix.metamodels.xml.XmlValueHolder#getValue()
     * @see #getXmlValueHolder()
     * @generated
     */
    EAttribute getXmlValueHolder_Value();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlValueHolder#getValueType <em>Value Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value Type</em>'.
     * @see com.metamatrix.metamodels.xml.XmlValueHolder#getValueType()
     * @see #getXmlValueHolder()
     * @generated
     */
    EAttribute getXmlValueHolder_ValueType();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xml.XmlBuildable <em>Xml Buildable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Buildable</em>'.
     * @see com.metamatrix.metamodels.xml.XmlBuildable
     * @generated
     */
    EClass getXmlBuildable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xml.XmlBuildable#getBuildState <em>Build State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Build State</em>'.
     * @see com.metamatrix.metamodels.xml.XmlBuildable#getBuildState()
     * @see #getXmlBuildable()
     * @generated
     */
    EAttribute getXmlBuildable_BuildState();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.xml.SoapEncoding <em>Soap Encoding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Soap Encoding</em>'.
     * @see com.metamatrix.metamodels.xml.SoapEncoding
     * @generated
     */
    EEnum getSoapEncoding();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.xml.ChoiceErrorMode <em>Choice Error Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Choice Error Mode</em>'.
     * @see com.metamatrix.metamodels.xml.ChoiceErrorMode
     * @generated
     */
    EEnum getChoiceErrorMode();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.xml.ValueType <em>Value Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Value Type</em>'.
     * @see com.metamatrix.metamodels.xml.ValueType
     * @generated
     */
    EEnum getValueType();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.xml.BuildStatus <em>Build Status</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Build Status</em>'.
     * @see com.metamatrix.metamodels.xml.BuildStatus
     * @generated
     */
    EEnum getBuildStatus();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.xml.NormalizationType <em>Normalization Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Normalization Type</em>'.
     * @see com.metamatrix.metamodels.xml.NormalizationType
     * @generated
     */
    EEnum getNormalizationType();

    /**
     * Returns the meta object for data type '{@link java.util.List <em>List</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for data type '<em>List</em>'.
     * @see java.util.List
     * @model instanceClass="java.util.List"
     * @generated
     */
	EDataType getList();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    XmlDocumentFactory getXmlDocumentFactory();

} //XmlDocumentPackage
