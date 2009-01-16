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

package com.metamatrix.metamodels.wsdl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @see com.metamatrix.metamodels.wsdl.WsdlFactory
 * @generated
 */
public interface WsdlPackage extends EPackage{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNAME = "wsdl"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_URI = "http://schemas.xmlsoap.org/wsdl/"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_PREFIX = "wsdl"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	WsdlPackage eINSTANCE = com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity <em>Name Optional Entity</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getWsdlNameOptionalEntity()
     * @generated
     */
	int WSDL_NAME_OPTIONAL_ENTITY = 17;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WSDL_NAME_OPTIONAL_ENTITY__NAME = 0;

    /**
     * The number of structural features of the the '<em>Name Optional Entity</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl <em>Definitions</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.DefinitionsImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getDefinitions()
     * @generated
     */
	int DEFINITIONS = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__NAME = WSDL_NAME_OPTIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__DOCUMENTATION = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__DECLARED_NAMESPACES = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__ELEMENTS = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Target Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__TARGET_NAMESPACE = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Messages</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__MESSAGES = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Port Types</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__PORT_TYPES = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Bindings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__BINDINGS = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Services</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__SERVICES = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Imports</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__IMPORTS = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Types</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS__TYPES = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 9;

    /**
     * The number of structural features of the the '<em>Definitions</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DEFINITIONS_FEATURE_COUNT = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 10;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner <em>Namespace Declaration Owner</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getNamespaceDeclarationOwner()
     * @generated
     */
	int NAMESPACE_DECLARATION_OWNER = 30;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES = 0;

    /**
     * The number of structural features of the the '<em>Namespace Declaration Owner</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int NAMESPACE_DECLARATION_OWNER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.ElementOwner <em>Element Owner</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.ElementOwner
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getElementOwner()
     * @generated
     */
	int ELEMENT_OWNER = 13;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT_OWNER__DECLARED_NAMESPACES = NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT_OWNER__DOCUMENTATION = NAMESPACE_DECLARATION_OWNER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT_OWNER__ELEMENTS = NAMESPACE_DECLARATION_OWNER_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Element Owner</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT_OWNER_FEATURE_COUNT = NAMESPACE_DECLARATION_OWNER_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.DocumentationImpl <em>Documentation</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.DocumentationImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getDocumentation()
     * @generated
     */
	int DOCUMENTATION = 1;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTATION__DECLARED_NAMESPACES = ELEMENT_OWNER__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTATION__DOCUMENTATION = ELEMENT_OWNER__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTATION__ELEMENTS = ELEMENT_OWNER__ELEMENTS;

    /**
     * The feature id for the '<em><b>Text Content</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTATION__TEXT_CONTENT = ELEMENT_OWNER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Contents</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTATION__CONTENTS = ELEMENT_OWNER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Documented</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTATION__DOCUMENTED = ELEMENT_OWNER_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Documentation</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTATION_FEATURE_COUNT = ELEMENT_OWNER_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.Documented <em>Documented</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.Documented
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getDocumented()
     * @generated
     */
	int DOCUMENTED = 2;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTED__DOCUMENTATION = 0;

    /**
     * The number of structural features of the the '<em>Documented</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DOCUMENTED_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented <em>Extensible Attributes Documented</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getExtensibleAttributesDocumented()
     * @generated
     */
	int EXTENSIBLE_ATTRIBUTES_DOCUMENTED = 3;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int EXTENSIBLE_ATTRIBUTES_DOCUMENTED__DOCUMENTATION = DOCUMENTED__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int EXTENSIBLE_ATTRIBUTES_DOCUMENTED__DECLARED_NAMESPACES = DOCUMENTED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int EXTENSIBLE_ATTRIBUTES_DOCUMENTED__ATTRIBUTES = DOCUMENTED_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Extensible Attributes Documented</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT = DOCUMENTED_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.AttributeImpl <em>Attribute</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.AttributeImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getAttribute()
     * @generated
     */
	int ATTRIBUTE = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE__NAME = 0;

    /**
     * The feature id for the '<em><b>Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE__PREFIX = 1;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE__VALUE = 2;

    /**
     * The feature id for the '<em><b>Namespace Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE__NAMESPACE_URI = 3;

    /**
     * The feature id for the '<em><b>Attribute Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE__ATTRIBUTE_OWNER = 4;

    /**
     * The number of structural features of the the '<em>Attribute</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity <em>Name Required Entity</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getWsdlNameRequiredEntity()
     * @generated
     */
	int WSDL_NAME_REQUIRED_ENTITY = 14;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WSDL_NAME_REQUIRED_ENTITY__NAME = 0;

    /**
     * The number of structural features of the the '<em>Name Required Entity</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.MessageImpl <em>Message</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.MessageImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getMessage()
     * @generated
     */
	int MESSAGE = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__NAME = WSDL_NAME_REQUIRED_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__DOCUMENTATION = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__DECLARED_NAMESPACES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__ELEMENTS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Definitions</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__DEFINITIONS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Parts</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__PARTS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Message</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_FEATURE_COUNT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.PortTypeImpl <em>Port Type</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.PortTypeImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getPortType()
     * @generated
     */
	int PORT_TYPE = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT_TYPE__NAME = WSDL_NAME_REQUIRED_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT_TYPE__DOCUMENTATION = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT_TYPE__DECLARED_NAMESPACES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT_TYPE__ATTRIBUTES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Definitions</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT_TYPE__DEFINITIONS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Operations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT_TYPE__OPERATIONS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Port Type</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT_TYPE_FEATURE_COUNT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.BindingImpl <em>Binding</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.BindingImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getBinding()
     * @generated
     */
	int BINDING = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__NAME = WSDL_NAME_REQUIRED_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__DOCUMENTATION = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__DECLARED_NAMESPACES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__ELEMENTS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__TYPE = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Definitions</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__DEFINITIONS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Binding Operations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__BINDING_OPERATIONS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Soap Binding</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__SOAP_BINDING = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Http Binding</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING__HTTP_BINDING = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the the '<em>Binding</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_FEATURE_COUNT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 8;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.ServiceImpl <em>Service</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.ServiceImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getService()
     * @generated
     */
	int SERVICE = 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SERVICE__NAME = WSDL_NAME_REQUIRED_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SERVICE__DOCUMENTATION = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SERVICE__DECLARED_NAMESPACES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SERVICE__ELEMENTS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Definitions</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SERVICE__DEFINITIONS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Ports</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SERVICE__PORTS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Service</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SERVICE_FEATURE_COUNT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.ImportImpl <em>Import</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.ImportImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getImport()
     * @generated
     */
	int IMPORT = 9;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int IMPORT__DOCUMENTATION = EXTENSIBLE_ATTRIBUTES_DOCUMENTED__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int IMPORT__DECLARED_NAMESPACES = EXTENSIBLE_ATTRIBUTES_DOCUMENTED__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int IMPORT__ATTRIBUTES = EXTENSIBLE_ATTRIBUTES_DOCUMENTED__ATTRIBUTES;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int IMPORT__NAMESPACE = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int IMPORT__LOCATION = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Definitions</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int IMPORT__DEFINITIONS = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Import</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int IMPORT_FEATURE_COUNT = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.PortImpl <em>Port</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.PortImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getPort()
     * @generated
     */
	int PORT = 10;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT__NAME = WSDL_NAME_REQUIRED_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT__DOCUMENTATION = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT__DECLARED_NAMESPACES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT__ELEMENTS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Binding</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT__BINDING = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Service</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT__SERVICE = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Soap Address</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT__SOAP_ADDRESS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Http Address</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT__HTTP_ADDRESS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the the '<em>Port</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PORT_FEATURE_COUNT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.AttributeOwner <em>Attribute Owner</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.AttributeOwner
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getAttributeOwner()
     * @generated
     */
	int ATTRIBUTE_OWNER = 12;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE_OWNER__DECLARED_NAMESPACES = NAMESPACE_DECLARATION_OWNER__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE_OWNER__ATTRIBUTES = NAMESPACE_DECLARATION_OWNER_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Attribute Owner</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ATTRIBUTE_OWNER_FEATURE_COUNT = NAMESPACE_DECLARATION_OWNER_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.ElementImpl <em>Element</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.ElementImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getElement()
     * @generated
     */
	int ELEMENT = 11;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__DECLARED_NAMESPACES = ATTRIBUTE_OWNER__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__ATTRIBUTES = ATTRIBUTE_OWNER__ATTRIBUTES;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__DOCUMENTATION = ATTRIBUTE_OWNER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__ELEMENTS = ATTRIBUTE_OWNER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__NAME = ATTRIBUTE_OWNER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__PREFIX = ATTRIBUTE_OWNER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Text Content</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__TEXT_CONTENT = ATTRIBUTE_OWNER_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Namespace Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__NAMESPACE_URI = ATTRIBUTE_OWNER_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Element Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT__ELEMENT_OWNER = ATTRIBUTE_OWNER_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the the '<em>Element</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int ELEMENT_FEATURE_COUNT = ATTRIBUTE_OWNER_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.ExtensibleDocumented <em>Extensible Documented</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.ExtensibleDocumented
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getExtensibleDocumented()
     * @generated
     */
	int EXTENSIBLE_DOCUMENTED = 23;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int EXTENSIBLE_DOCUMENTED__DOCUMENTATION = DOCUMENTED__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int EXTENSIBLE_DOCUMENTED__DECLARED_NAMESPACES = DOCUMENTED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int EXTENSIBLE_DOCUMENTED__ELEMENTS = DOCUMENTED_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Extensible Documented</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int EXTENSIBLE_DOCUMENTED_FEATURE_COUNT = DOCUMENTED_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.TypesImpl <em>Types</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.TypesImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getTypes()
     * @generated
     */
	int TYPES = 15;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int TYPES__DOCUMENTATION = EXTENSIBLE_DOCUMENTED__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int TYPES__DECLARED_NAMESPACES = EXTENSIBLE_DOCUMENTED__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int TYPES__ELEMENTS = EXTENSIBLE_DOCUMENTED__ELEMENTS;

    /**
     * The feature id for the '<em><b>Definitions</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int TYPES__DEFINITIONS = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Schemas</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int TYPES__SCHEMAS = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Types</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int TYPES_FEATURE_COUNT = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.MessagePartImpl <em>Message Part</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.MessagePartImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getMessagePart()
     * @generated
     */
	int MESSAGE_PART = 16;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_PART__DOCUMENTATION = EXTENSIBLE_ATTRIBUTES_DOCUMENTED__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_PART__DECLARED_NAMESPACES = EXTENSIBLE_ATTRIBUTES_DOCUMENTED__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_PART__ATTRIBUTES = EXTENSIBLE_ATTRIBUTES_DOCUMENTED__ATTRIBUTES;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_PART__NAME = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_PART__TYPE = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Element</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_PART__ELEMENT = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Message</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_PART__MESSAGE = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Message Part</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_PART_FEATURE_COUNT = EXTENSIBLE_ATTRIBUTES_DOCUMENTED_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.OperationImpl <em>Operation</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.OperationImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getOperation()
     * @generated
     */
	int OPERATION = 18;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__NAME = WSDL_NAME_REQUIRED_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__DOCUMENTATION = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__DECLARED_NAMESPACES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__ELEMENTS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Parameter Order</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__PARAMETER_ORDER = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Port Type</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__PORT_TYPE = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Input</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__INPUT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Output</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__OUTPUT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Faults</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__FAULTS = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the the '<em>Operation</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION_FEATURE_COUNT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 8;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.ParamType <em>Param Type</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.ParamType
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getParamType()
     * @generated
     */
	int PARAM_TYPE = 22;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PARAM_TYPE__NAME = WSDL_NAME_OPTIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PARAM_TYPE__DOCUMENTATION = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PARAM_TYPE__DECLARED_NAMESPACES = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PARAM_TYPE__ATTRIBUTES = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PARAM_TYPE__MESSAGE = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Param Type</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int PARAM_TYPE_FEATURE_COUNT = WSDL_NAME_OPTIONAL_ENTITY_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.InputImpl <em>Input</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.InputImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getInput()
     * @generated
     */
	int INPUT = 19;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__NAME = PARAM_TYPE__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__DOCUMENTATION = PARAM_TYPE__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__DECLARED_NAMESPACES = PARAM_TYPE__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__ATTRIBUTES = PARAM_TYPE__ATTRIBUTES;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__MESSAGE = PARAM_TYPE__MESSAGE;

    /**
     * The feature id for the '<em><b>Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__OPERATION = PARAM_TYPE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Input</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT_FEATURE_COUNT = PARAM_TYPE_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.OutputImpl <em>Output</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.OutputImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getOutput()
     * @generated
     */
	int OUTPUT = 20;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__NAME = PARAM_TYPE__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__DOCUMENTATION = PARAM_TYPE__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__DECLARED_NAMESPACES = PARAM_TYPE__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__ATTRIBUTES = PARAM_TYPE__ATTRIBUTES;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__MESSAGE = PARAM_TYPE__MESSAGE;

    /**
     * The feature id for the '<em><b>Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__OPERATION = PARAM_TYPE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Output</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT_FEATURE_COUNT = PARAM_TYPE_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.FaultImpl <em>Fault</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.FaultImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getFault()
     * @generated
     */
	int FAULT = 21;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int FAULT__NAME = WSDL_NAME_REQUIRED_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int FAULT__DOCUMENTATION = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int FAULT__DECLARED_NAMESPACES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int FAULT__ATTRIBUTES = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int FAULT__MESSAGE = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int FAULT__OPERATION = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Fault</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int FAULT_FEATURE_COUNT = WSDL_NAME_REQUIRED_ENTITY_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl <em>Binding Operation</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.BindingOperationImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getBindingOperation()
     * @generated
     */
	int BINDING_OPERATION = 24;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__DOCUMENTATION = EXTENSIBLE_DOCUMENTED__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__DECLARED_NAMESPACES = EXTENSIBLE_DOCUMENTED__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__ELEMENTS = EXTENSIBLE_DOCUMENTED__ELEMENTS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__NAME = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Binding</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__BINDING = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Binding Input</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__BINDING_INPUT = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Binding Faults</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__BINDING_FAULTS = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Binding Output</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__BINDING_OUTPUT = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Soap Operation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__SOAP_OPERATION = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Http Operation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION__HTTP_OPERATION = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the the '<em>Binding Operation</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OPERATION_FEATURE_COUNT = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.BindingParam <em>Binding Param</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.BindingParam
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getBindingParam()
     * @generated
     */
	int BINDING_PARAM = 28;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_PARAM__DOCUMENTATION = EXTENSIBLE_DOCUMENTED__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_PARAM__DECLARED_NAMESPACES = EXTENSIBLE_DOCUMENTED__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_PARAM__ELEMENTS = EXTENSIBLE_DOCUMENTED__ELEMENTS;

    /**
     * The feature id for the '<em><b>Mime Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_PARAM__MIME_ELEMENTS = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_PARAM__NAME = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Soap Header</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_PARAM__SOAP_HEADER = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Soap Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_PARAM__SOAP_BODY = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Binding Param</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_PARAM_FEATURE_COUNT = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.BindingInputImpl <em>Binding Input</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.BindingInputImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getBindingInput()
     * @generated
     */
	int BINDING_INPUT = 25;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT__DOCUMENTATION = BINDING_PARAM__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT__DECLARED_NAMESPACES = BINDING_PARAM__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT__ELEMENTS = BINDING_PARAM__ELEMENTS;

    /**
     * The feature id for the '<em><b>Mime Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT__MIME_ELEMENTS = BINDING_PARAM__MIME_ELEMENTS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT__NAME = BINDING_PARAM__NAME;

    /**
     * The feature id for the '<em><b>Soap Header</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT__SOAP_HEADER = BINDING_PARAM__SOAP_HEADER;

    /**
     * The feature id for the '<em><b>Soap Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT__SOAP_BODY = BINDING_PARAM__SOAP_BODY;

    /**
     * The feature id for the '<em><b>Binding Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT__BINDING_OPERATION = BINDING_PARAM_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Binding Input</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_INPUT_FEATURE_COUNT = BINDING_PARAM_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.BindingOutputImpl <em>Binding Output</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.BindingOutputImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getBindingOutput()
     * @generated
     */
	int BINDING_OUTPUT = 26;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT__DOCUMENTATION = BINDING_PARAM__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT__DECLARED_NAMESPACES = BINDING_PARAM__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT__ELEMENTS = BINDING_PARAM__ELEMENTS;

    /**
     * The feature id for the '<em><b>Mime Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT__MIME_ELEMENTS = BINDING_PARAM__MIME_ELEMENTS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT__NAME = BINDING_PARAM__NAME;

    /**
     * The feature id for the '<em><b>Soap Header</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT__SOAP_HEADER = BINDING_PARAM__SOAP_HEADER;

    /**
     * The feature id for the '<em><b>Soap Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT__SOAP_BODY = BINDING_PARAM__SOAP_BODY;

    /**
     * The feature id for the '<em><b>Binding Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT__BINDING_OPERATION = BINDING_PARAM_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Binding Output</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_OUTPUT_FEATURE_COUNT = BINDING_PARAM_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.BindingFaultImpl <em>Binding Fault</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.BindingFaultImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getBindingFault()
     * @generated
     */
	int BINDING_FAULT = 27;

    /**
     * The feature id for the '<em><b>Documentation</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_FAULT__DOCUMENTATION = EXTENSIBLE_DOCUMENTED__DOCUMENTATION;

    /**
     * The feature id for the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_FAULT__DECLARED_NAMESPACES = EXTENSIBLE_DOCUMENTED__DECLARED_NAMESPACES;

    /**
     * The feature id for the '<em><b>Elements</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_FAULT__ELEMENTS = EXTENSIBLE_DOCUMENTED__ELEMENTS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_FAULT__NAME = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Binding Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_FAULT__BINDING_OPERATION = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Soap Fault</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_FAULT__SOAP_FAULT = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Binding Fault</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BINDING_FAULT_FEATURE_COUNT = EXTENSIBLE_DOCUMENTED_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.impl.NamespaceDeclarationImpl <em>Namespace Declaration</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.impl.NamespaceDeclarationImpl
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getNamespaceDeclaration()
     * @generated
     */
	int NAMESPACE_DECLARATION = 29;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int NAMESPACE_DECLARATION__URI = 0;

    /**
     * The feature id for the '<em><b>Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int NAMESPACE_DECLARATION__PREFIX = 1;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int NAMESPACE_DECLARATION__OWNER = 2;

    /**
     * The number of structural features of the the '<em>Namespace Declaration</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int NAMESPACE_DECLARATION_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '<em>IStatus</em>' data type.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see org.eclipse.core.runtime.IStatus
     * @see com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl#getIStatus()
     * @generated
     */
	int ISTATUS = 31;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Definitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Definitions</em>'.
     * @see com.metamatrix.metamodels.wsdl.Definitions
     * @generated
     */
	EClass getDefinitions();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Definitions#getTargetNamespace <em>Target Namespace</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Target Namespace</em>'.
     * @see com.metamatrix.metamodels.wsdl.Definitions#getTargetNamespace()
     * @see #getDefinitions()
     * @generated
     */
	EAttribute getDefinitions_TargetNamespace();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Definitions#getMessages <em>Messages</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Messages</em>'.
     * @see com.metamatrix.metamodels.wsdl.Definitions#getMessages()
     * @see #getDefinitions()
     * @generated
     */
	EReference getDefinitions_Messages();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Definitions#getPortTypes <em>Port Types</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Port Types</em>'.
     * @see com.metamatrix.metamodels.wsdl.Definitions#getPortTypes()
     * @see #getDefinitions()
     * @generated
     */
	EReference getDefinitions_PortTypes();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Definitions#getBindings <em>Bindings</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Bindings</em>'.
     * @see com.metamatrix.metamodels.wsdl.Definitions#getBindings()
     * @see #getDefinitions()
     * @generated
     */
	EReference getDefinitions_Bindings();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Definitions#getServices <em>Services</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Services</em>'.
     * @see com.metamatrix.metamodels.wsdl.Definitions#getServices()
     * @see #getDefinitions()
     * @generated
     */
	EReference getDefinitions_Services();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Definitions#getImports <em>Imports</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Imports</em>'.
     * @see com.metamatrix.metamodels.wsdl.Definitions#getImports()
     * @see #getDefinitions()
     * @generated
     */
	EReference getDefinitions_Imports();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.Definitions#getTypes <em>Types</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Types</em>'.
     * @see com.metamatrix.metamodels.wsdl.Definitions#getTypes()
     * @see #getDefinitions()
     * @generated
     */
	EReference getDefinitions_Types();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Documentation <em>Documentation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Documentation</em>'.
     * @see com.metamatrix.metamodels.wsdl.Documentation
     * @generated
     */
	EClass getDocumentation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Documentation#getTextContent <em>Text Content</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Text Content</em>'.
     * @see com.metamatrix.metamodels.wsdl.Documentation#getTextContent()
     * @see #getDocumentation()
     * @generated
     */
	EAttribute getDocumentation_TextContent();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Documentation#getContents <em>Contents</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Contents</em>'.
     * @see com.metamatrix.metamodels.wsdl.Documentation#getContents()
     * @see #getDocumentation()
     * @generated
     */
	EReference getDocumentation_Contents();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Documentation#getDocumented <em>Documented</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Documented</em>'.
     * @see com.metamatrix.metamodels.wsdl.Documentation#getDocumented()
     * @see #getDocumentation()
     * @generated
     */
	EReference getDocumentation_Documented();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Documented <em>Documented</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Documented</em>'.
     * @see com.metamatrix.metamodels.wsdl.Documented
     * @generated
     */
	EClass getDocumented();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.Documented#getDocumentation <em>Documentation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Documentation</em>'.
     * @see com.metamatrix.metamodels.wsdl.Documented#getDocumentation()
     * @see #getDocumented()
     * @generated
     */
	EReference getDocumented_Documentation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented <em>Extensible Attributes Documented</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Extensible Attributes Documented</em>'.
     * @see com.metamatrix.metamodels.wsdl.ExtensibleAttributesDocumented
     * @generated
     */
	EClass getExtensibleAttributesDocumented();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Attribute <em>Attribute</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Attribute</em>'.
     * @see com.metamatrix.metamodels.wsdl.Attribute
     * @generated
     */
	EClass getAttribute();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Attribute#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.wsdl.Attribute#getName()
     * @see #getAttribute()
     * @generated
     */
	EAttribute getAttribute_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Attribute#getPrefix <em>Prefix</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Prefix</em>'.
     * @see com.metamatrix.metamodels.wsdl.Attribute#getPrefix()
     * @see #getAttribute()
     * @generated
     */
	EAttribute getAttribute_Prefix();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Attribute#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see com.metamatrix.metamodels.wsdl.Attribute#getValue()
     * @see #getAttribute()
     * @generated
     */
	EAttribute getAttribute_Value();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Attribute#getNamespaceUri <em>Namespace Uri</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Namespace Uri</em>'.
     * @see com.metamatrix.metamodels.wsdl.Attribute#getNamespaceUri()
     * @see #getAttribute()
     * @generated
     */
	EAttribute getAttribute_NamespaceUri();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Attribute#getAttributeOwner <em>Attribute Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Attribute Owner</em>'.
     * @see com.metamatrix.metamodels.wsdl.Attribute#getAttributeOwner()
     * @see #getAttribute()
     * @generated
     */
	EReference getAttribute_AttributeOwner();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Message <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Message</em>'.
     * @see com.metamatrix.metamodels.wsdl.Message
     * @generated
     */
	EClass getMessage();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Message#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Definitions</em>'.
     * @see com.metamatrix.metamodels.wsdl.Message#getDefinitions()
     * @see #getMessage()
     * @generated
     */
	EReference getMessage_Definitions();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Message#getParts <em>Parts</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Parts</em>'.
     * @see com.metamatrix.metamodels.wsdl.Message#getParts()
     * @see #getMessage()
     * @generated
     */
	EReference getMessage_Parts();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.PortType <em>Port Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Port Type</em>'.
     * @see com.metamatrix.metamodels.wsdl.PortType
     * @generated
     */
	EClass getPortType();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.PortType#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Definitions</em>'.
     * @see com.metamatrix.metamodels.wsdl.PortType#getDefinitions()
     * @see #getPortType()
     * @generated
     */
	EReference getPortType_Definitions();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.PortType#getOperations <em>Operations</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Operations</em>'.
     * @see com.metamatrix.metamodels.wsdl.PortType#getOperations()
     * @see #getPortType()
     * @generated
     */
	EReference getPortType_Operations();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Binding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.Binding
     * @generated
     */
	EClass getBinding();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Binding#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.metamodels.wsdl.Binding#getType()
     * @see #getBinding()
     * @generated
     */
	EAttribute getBinding_Type();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Binding#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Definitions</em>'.
     * @see com.metamatrix.metamodels.wsdl.Binding#getDefinitions()
     * @see #getBinding()
     * @generated
     */
	EReference getBinding_Definitions();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Binding#getBindingOperations <em>Binding Operations</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Binding Operations</em>'.
     * @see com.metamatrix.metamodels.wsdl.Binding#getBindingOperations()
     * @see #getBinding()
     * @generated
     */
	EReference getBinding_BindingOperations();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.Binding#getSoapBinding <em>Soap Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Soap Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.Binding#getSoapBinding()
     * @see #getBinding()
     * @generated
     */
	EReference getBinding_SoapBinding();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.Binding#getHttpBinding <em>Http Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Http Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.Binding#getHttpBinding()
     * @see #getBinding()
     * @generated
     */
	EReference getBinding_HttpBinding();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Service <em>Service</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Service</em>'.
     * @see com.metamatrix.metamodels.wsdl.Service
     * @generated
     */
	EClass getService();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Service#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Definitions</em>'.
     * @see com.metamatrix.metamodels.wsdl.Service#getDefinitions()
     * @see #getService()
     * @generated
     */
	EReference getService_Definitions();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Service#getPorts <em>Ports</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Ports</em>'.
     * @see com.metamatrix.metamodels.wsdl.Service#getPorts()
     * @see #getService()
     * @generated
     */
	EReference getService_Ports();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Import <em>Import</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Import</em>'.
     * @see com.metamatrix.metamodels.wsdl.Import
     * @generated
     */
	EClass getImport();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Import#getNamespace <em>Namespace</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Namespace</em>'.
     * @see com.metamatrix.metamodels.wsdl.Import#getNamespace()
     * @see #getImport()
     * @generated
     */
	EAttribute getImport_Namespace();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Import#getLocation <em>Location</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Location</em>'.
     * @see com.metamatrix.metamodels.wsdl.Import#getLocation()
     * @see #getImport()
     * @generated
     */
	EAttribute getImport_Location();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Import#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Definitions</em>'.
     * @see com.metamatrix.metamodels.wsdl.Import#getDefinitions()
     * @see #getImport()
     * @generated
     */
	EReference getImport_Definitions();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Port <em>Port</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Port</em>'.
     * @see com.metamatrix.metamodels.wsdl.Port
     * @generated
     */
	EClass getPort();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Port#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.Port#getBinding()
     * @see #getPort()
     * @generated
     */
	EAttribute getPort_Binding();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Port#getService <em>Service</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Service</em>'.
     * @see com.metamatrix.metamodels.wsdl.Port#getService()
     * @see #getPort()
     * @generated
     */
	EReference getPort_Service();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.Port#getSoapAddress <em>Soap Address</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Soap Address</em>'.
     * @see com.metamatrix.metamodels.wsdl.Port#getSoapAddress()
     * @see #getPort()
     * @generated
     */
	EReference getPort_SoapAddress();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.Port#getHttpAddress <em>Http Address</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Http Address</em>'.
     * @see com.metamatrix.metamodels.wsdl.Port#getHttpAddress()
     * @see #getPort()
     * @generated
     */
	EReference getPort_HttpAddress();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Element <em>Element</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Element</em>'.
     * @see com.metamatrix.metamodels.wsdl.Element
     * @generated
     */
	EClass getElement();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Element#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.wsdl.Element#getName()
     * @see #getElement()
     * @generated
     */
	EAttribute getElement_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Element#getPrefix <em>Prefix</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Prefix</em>'.
     * @see com.metamatrix.metamodels.wsdl.Element#getPrefix()
     * @see #getElement()
     * @generated
     */
	EAttribute getElement_Prefix();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Element#getTextContent <em>Text Content</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Text Content</em>'.
     * @see com.metamatrix.metamodels.wsdl.Element#getTextContent()
     * @see #getElement()
     * @generated
     */
	EAttribute getElement_TextContent();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Element#getNamespaceUri <em>Namespace Uri</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Namespace Uri</em>'.
     * @see com.metamatrix.metamodels.wsdl.Element#getNamespaceUri()
     * @see #getElement()
     * @generated
     */
	EAttribute getElement_NamespaceUri();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Element#getElementOwner <em>Element Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Element Owner</em>'.
     * @see com.metamatrix.metamodels.wsdl.Element#getElementOwner()
     * @see #getElement()
     * @generated
     */
	EReference getElement_ElementOwner();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.AttributeOwner <em>Attribute Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Attribute Owner</em>'.
     * @see com.metamatrix.metamodels.wsdl.AttributeOwner
     * @generated
     */
	EClass getAttributeOwner();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.AttributeOwner#getAttributes <em>Attributes</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Attributes</em>'.
     * @see com.metamatrix.metamodels.wsdl.AttributeOwner#getAttributes()
     * @see #getAttributeOwner()
     * @generated
     */
	EReference getAttributeOwner_Attributes();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.ElementOwner <em>Element Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Element Owner</em>'.
     * @see com.metamatrix.metamodels.wsdl.ElementOwner
     * @generated
     */
	EClass getElementOwner();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.ElementOwner#getElements <em>Elements</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Elements</em>'.
     * @see com.metamatrix.metamodels.wsdl.ElementOwner#getElements()
     * @see #getElementOwner()
     * @generated
     */
	EReference getElementOwner_Elements();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity <em>Name Required Entity</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Name Required Entity</em>'.
     * @see com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity
     * @generated
     */
	EClass getWsdlNameRequiredEntity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.wsdl.WsdlNameRequiredEntity#getName()
     * @see #getWsdlNameRequiredEntity()
     * @generated
     */
	EAttribute getWsdlNameRequiredEntity_Name();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Types <em>Types</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Types</em>'.
     * @see com.metamatrix.metamodels.wsdl.Types
     * @generated
     */
	EClass getTypes();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Types#getDefinitions <em>Definitions</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Definitions</em>'.
     * @see com.metamatrix.metamodels.wsdl.Types#getDefinitions()
     * @see #getTypes()
     * @generated
     */
	EReference getTypes_Definitions();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Types#getSchemas <em>Schemas</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Schemas</em>'.
     * @see com.metamatrix.metamodels.wsdl.Types#getSchemas()
     * @see #getTypes()
     * @generated
     */
	EReference getTypes_Schemas();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.MessagePart <em>Message Part</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Message Part</em>'.
     * @see com.metamatrix.metamodels.wsdl.MessagePart
     * @generated
     */
	EClass getMessagePart();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.MessagePart#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.metamodels.wsdl.MessagePart#getType()
     * @see #getMessagePart()
     * @generated
     */
	EAttribute getMessagePart_Type();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.MessagePart#getElement <em>Element</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Element</em>'.
     * @see com.metamatrix.metamodels.wsdl.MessagePart#getElement()
     * @see #getMessagePart()
     * @generated
     */
	EAttribute getMessagePart_Element();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.MessagePart#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Message</em>'.
     * @see com.metamatrix.metamodels.wsdl.MessagePart#getMessage()
     * @see #getMessagePart()
     * @generated
     */
	EReference getMessagePart_Message();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity <em>Name Optional Entity</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Name Optional Entity</em>'.
     * @see com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity
     * @generated
     */
	EClass getWsdlNameOptionalEntity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.wsdl.WsdlNameOptionalEntity#getName()
     * @see #getWsdlNameOptionalEntity()
     * @generated
     */
	EAttribute getWsdlNameOptionalEntity_Name();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Operation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.Operation
     * @generated
     */
	EClass getOperation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Operation#getParameterOrder <em>Parameter Order</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Parameter Order</em>'.
     * @see com.metamatrix.metamodels.wsdl.Operation#getParameterOrder()
     * @see #getOperation()
     * @generated
     */
	EAttribute getOperation_ParameterOrder();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Operation#getPortType <em>Port Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Port Type</em>'.
     * @see com.metamatrix.metamodels.wsdl.Operation#getPortType()
     * @see #getOperation()
     * @generated
     */
	EReference getOperation_PortType();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.Operation#getInput <em>Input</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Input</em>'.
     * @see com.metamatrix.metamodels.wsdl.Operation#getInput()
     * @see #getOperation()
     * @generated
     */
	EReference getOperation_Input();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.Operation#getOutput <em>Output</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Output</em>'.
     * @see com.metamatrix.metamodels.wsdl.Operation#getOutput()
     * @see #getOperation()
     * @generated
     */
	EReference getOperation_Output();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.Operation#getFaults <em>Faults</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Faults</em>'.
     * @see com.metamatrix.metamodels.wsdl.Operation#getFaults()
     * @see #getOperation()
     * @generated
     */
	EReference getOperation_Faults();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Input <em>Input</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Input</em>'.
     * @see com.metamatrix.metamodels.wsdl.Input
     * @generated
     */
	EClass getInput();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Input#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.Input#getOperation()
     * @see #getInput()
     * @generated
     */
	EReference getInput_Operation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Output <em>Output</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Output</em>'.
     * @see com.metamatrix.metamodels.wsdl.Output
     * @generated
     */
	EClass getOutput();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Output#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.Output#getOperation()
     * @see #getOutput()
     * @generated
     */
	EReference getOutput_Operation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.Fault <em>Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Fault</em>'.
     * @see com.metamatrix.metamodels.wsdl.Fault
     * @generated
     */
	EClass getFault();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.Fault#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Message</em>'.
     * @see com.metamatrix.metamodels.wsdl.Fault#getMessage()
     * @see #getFault()
     * @generated
     */
	EAttribute getFault_Message();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.Fault#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.Fault#getOperation()
     * @see #getFault()
     * @generated
     */
	EReference getFault_Operation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.ParamType <em>Param Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Param Type</em>'.
     * @see com.metamatrix.metamodels.wsdl.ParamType
     * @generated
     */
	EClass getParamType();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.ParamType#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Message</em>'.
     * @see com.metamatrix.metamodels.wsdl.ParamType#getMessage()
     * @see #getParamType()
     * @generated
     */
	EAttribute getParamType_Message();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.ExtensibleDocumented <em>Extensible Documented</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Extensible Documented</em>'.
     * @see com.metamatrix.metamodels.wsdl.ExtensibleDocumented
     * @generated
     */
	EClass getExtensibleDocumented();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.BindingOperation <em>Binding Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Binding Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOperation
     * @generated
     */
	EClass getBindingOperation();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getBinding()
     * @see #getBindingOperation()
     * @generated
     */
	EReference getBindingOperation_Binding();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getBindingInput <em>Binding Input</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Binding Input</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getBindingInput()
     * @see #getBindingOperation()
     * @generated
     */
	EReference getBindingOperation_BindingInput();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getBindingFaults <em>Binding Faults</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Binding Faults</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getBindingFaults()
     * @see #getBindingOperation()
     * @generated
     */
	EReference getBindingOperation_BindingFaults();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getBindingOutput <em>Binding Output</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Binding Output</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getBindingOutput()
     * @see #getBindingOperation()
     * @generated
     */
	EReference getBindingOperation_BindingOutput();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getSoapOperation <em>Soap Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Soap Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getSoapOperation()
     * @see #getBindingOperation()
     * @generated
     */
	EReference getBindingOperation_SoapOperation();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getHttpOperation <em>Http Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Http Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getHttpOperation()
     * @see #getBindingOperation()
     * @generated
     */
	EReference getBindingOperation_HttpOperation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.BindingInput <em>Binding Input</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Binding Input</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingInput
     * @generated
     */
	EClass getBindingInput();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.BindingInput#getBindingOperation <em>Binding Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingInput#getBindingOperation()
     * @see #getBindingInput()
     * @generated
     */
	EReference getBindingInput_BindingOperation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.BindingOutput <em>Binding Output</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Binding Output</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOutput
     * @generated
     */
	EClass getBindingOutput();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.BindingOutput#getBindingOperation <em>Binding Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingOutput#getBindingOperation()
     * @see #getBindingOutput()
     * @generated
     */
	EReference getBindingOutput_BindingOperation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.BindingFault <em>Binding Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Binding Fault</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingFault
     * @generated
     */
	EClass getBindingFault();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.BindingFault#getBindingOperation <em>Binding Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingFault#getBindingOperation()
     * @see #getBindingFault()
     * @generated
     */
	EReference getBindingFault_BindingOperation();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.BindingFault#getSoapFault <em>Soap Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Soap Fault</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingFault#getSoapFault()
     * @see #getBindingFault()
     * @generated
     */
	EReference getBindingFault_SoapFault();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.BindingParam <em>Binding Param</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Binding Param</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingParam
     * @generated
     */
	EClass getBindingParam();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapHeader <em>Soap Header</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Soap Header</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingParam#getSoapHeader()
     * @see #getBindingParam()
     * @generated
     */
	EReference getBindingParam_SoapHeader();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapBody <em>Soap Body</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Soap Body</em>'.
     * @see com.metamatrix.metamodels.wsdl.BindingParam#getSoapBody()
     * @see #getBindingParam()
     * @generated
     */
	EReference getBindingParam_SoapBody();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration <em>Namespace Declaration</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Namespace Declaration</em>'.
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclaration
     * @generated
     */
	EClass getNamespaceDeclaration();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getUri()
     * @see #getNamespaceDeclaration()
     * @generated
     */
	EAttribute getNamespaceDeclaration_Uri();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getPrefix <em>Prefix</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Prefix</em>'.
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getPrefix()
     * @see #getNamespaceDeclaration()
     * @generated
     */
	EAttribute getNamespaceDeclaration_Prefix();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Owner</em>'.
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getOwner()
     * @see #getNamespaceDeclaration()
     * @generated
     */
	EReference getNamespaceDeclaration_Owner();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner <em>Namespace Declaration Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Namespace Declaration Owner</em>'.
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner
     * @generated
     */
	EClass getNamespaceDeclarationOwner();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner#getDeclaredNamespaces <em>Declared Namespaces</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Declared Namespaces</em>'.
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner#getDeclaredNamespaces()
     * @see #getNamespaceDeclarationOwner()
     * @generated
     */
	EReference getNamespaceDeclarationOwner_DeclaredNamespaces();

    /**
     * Returns the meta object for data type '{@link org.eclipse.core.runtime.IStatus <em>IStatus</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for data type '<em>IStatus</em>'.
     * @see org.eclipse.core.runtime.IStatus
     * @model instanceClass="org.eclipse.core.runtime.IStatus"
     * @generated
     */
	EDataType getIStatus();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
	WsdlFactory getWsdlFactory();

} //WsdlPackage
