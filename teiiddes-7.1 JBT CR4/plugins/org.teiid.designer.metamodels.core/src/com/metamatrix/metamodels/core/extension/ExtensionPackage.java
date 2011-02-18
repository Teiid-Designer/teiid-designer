/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.core.extension.ExtensionFactory
 * @model kind="package"
 * @generated
 */
public interface ExtensionPackage extends EPackage {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String eNAME = "extension"; //$NON-NLS-1$

    /**
     * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/Extension"; //$NON-NLS-1$

    /**
     * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String eNS_PREFIX = "mmext"; //$NON-NLS-1$

    /**
     * The singleton instance of the package. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    ExtensionPackage eINSTANCE = com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.extension.impl.XClassImpl <em>XClass</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see com.metamatrix.metamodels.core.extension.impl.XClassImpl
     * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXClass()
     * @generated
     */
    int XCLASS = 0;

    /**
     * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EANNOTATIONS = EcorePackage.ECLASS__EANNOTATIONS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__NAME = EcorePackage.ECLASS__NAME;

    /**
     * The feature id for the '<em><b>Instance Class Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__INSTANCE_CLASS_NAME = EcorePackage.ECLASS__INSTANCE_CLASS_NAME;

    /**
     * The feature id for the '<em><b>Instance Class</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__INSTANCE_CLASS = EcorePackage.ECLASS__INSTANCE_CLASS;

    /**
     * The feature id for the '<em><b>Default Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__DEFAULT_VALUE = EcorePackage.ECLASS__DEFAULT_VALUE;

    /**
     * The feature id for the '<em><b>Instance Type Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__INSTANCE_TYPE_NAME = EcorePackage.ECLASS__INSTANCE_TYPE_NAME;

    /**
     * The feature id for the '<em><b>EPackage</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EPACKAGE = EcorePackage.ECLASS__EPACKAGE;

    /**
     * The feature id for the '<em><b>EType Parameters</b></em>' containment reference list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__ETYPE_PARAMETERS = EcorePackage.ECLASS__ETYPE_PARAMETERS;

    /**
     * The feature id for the '<em><b>Abstract</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__ABSTRACT = EcorePackage.ECLASS__ABSTRACT;

    /**
     * The feature id for the '<em><b>Interface</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__INTERFACE = EcorePackage.ECLASS__INTERFACE;

    /**
     * The feature id for the '<em><b>ESuper Types</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__ESUPER_TYPES = EcorePackage.ECLASS__ESUPER_TYPES;

    /**
     * The feature id for the '<em><b>EOperations</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EOPERATIONS = EcorePackage.ECLASS__EOPERATIONS;

    /**
     * The feature id for the '<em><b>EAll Attributes</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EALL_ATTRIBUTES = EcorePackage.ECLASS__EALL_ATTRIBUTES;

    /**
     * The feature id for the '<em><b>EAll References</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EALL_REFERENCES = EcorePackage.ECLASS__EALL_REFERENCES;

    /**
     * The feature id for the '<em><b>EReferences</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EREFERENCES = EcorePackage.ECLASS__EREFERENCES;

    /**
     * The feature id for the '<em><b>EAttributes</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EATTRIBUTES = EcorePackage.ECLASS__EATTRIBUTES;

    /**
     * The feature id for the '<em><b>EAll Containments</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EALL_CONTAINMENTS = EcorePackage.ECLASS__EALL_CONTAINMENTS;

    /**
     * The feature id for the '<em><b>EAll Operations</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EALL_OPERATIONS = EcorePackage.ECLASS__EALL_OPERATIONS;

    /**
     * The feature id for the '<em><b>EAll Structural Features</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EALL_STRUCTURAL_FEATURES = EcorePackage.ECLASS__EALL_STRUCTURAL_FEATURES;

    /**
     * The feature id for the '<em><b>EAll Super Types</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EALL_SUPER_TYPES = EcorePackage.ECLASS__EALL_SUPER_TYPES;

    /**
     * The feature id for the '<em><b>EID Attribute</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EID_ATTRIBUTE = EcorePackage.ECLASS__EID_ATTRIBUTE;

    /**
     * The feature id for the '<em><b>EStructural Features</b></em>' containment reference list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__ESTRUCTURAL_FEATURES = EcorePackage.ECLASS__ESTRUCTURAL_FEATURES;

    /**
     * The feature id for the '<em><b>EGeneric Super Types</b></em>' containment reference list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EGENERIC_SUPER_TYPES = EcorePackage.ECLASS__EGENERIC_SUPER_TYPES;

    /**
     * The feature id for the '<em><b>EAll Generic Super Types</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EALL_GENERIC_SUPER_TYPES = EcorePackage.ECLASS__EALL_GENERIC_SUPER_TYPES;

    /**
     * The feature id for the '<em><b>Extended Class</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS__EXTENDED_CLASS = EcorePackage.ECLASS_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>XClass</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XCLASS_FEATURE_COUNT = EcorePackage.ECLASS_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.extension.impl.XPackageImpl <em>XPackage</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see com.metamatrix.metamodels.core.extension.impl.XPackageImpl
     * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXPackage()
     * @generated
     */
    int XPACKAGE = 1;

    /**
     * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE__EANNOTATIONS = EcorePackage.EPACKAGE__EANNOTATIONS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE__NAME = EcorePackage.EPACKAGE__NAME;

    /**
     * The feature id for the '<em><b>Ns URI</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE__NS_URI = EcorePackage.EPACKAGE__NS_URI;

    /**
     * The feature id for the '<em><b>Ns Prefix</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE__NS_PREFIX = EcorePackage.EPACKAGE__NS_PREFIX;

    /**
     * The feature id for the '<em><b>EFactory Instance</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE__EFACTORY_INSTANCE = EcorePackage.EPACKAGE__EFACTORY_INSTANCE;

    /**
     * The feature id for the '<em><b>EClassifiers</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE__ECLASSIFIERS = EcorePackage.EPACKAGE__ECLASSIFIERS;

    /**
     * The feature id for the '<em><b>ESubpackages</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE__ESUBPACKAGES = EcorePackage.EPACKAGE__ESUBPACKAGES;

    /**
     * The feature id for the '<em><b>ESuper Package</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE__ESUPER_PACKAGE = EcorePackage.EPACKAGE__ESUPER_PACKAGE;

    /**
     * The number of structural features of the '<em>XPackage</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XPACKAGE_FEATURE_COUNT = EcorePackage.EPACKAGE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.extension.impl.XAttributeImpl <em>XAttribute</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see com.metamatrix.metamodels.core.extension.impl.XAttributeImpl
     * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXAttribute()
     * @generated
     */
    int XATTRIBUTE = 2;

    /**
     * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__EANNOTATIONS = EcorePackage.EATTRIBUTE__EANNOTATIONS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__NAME = EcorePackage.EATTRIBUTE__NAME;

    /**
     * The feature id for the '<em><b>Ordered</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__ORDERED = EcorePackage.EATTRIBUTE__ORDERED;

    /**
     * The feature id for the '<em><b>Unique</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__UNIQUE = EcorePackage.EATTRIBUTE__UNIQUE;

    /**
     * The feature id for the '<em><b>Lower Bound</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__LOWER_BOUND = EcorePackage.EATTRIBUTE__LOWER_BOUND;

    /**
     * The feature id for the '<em><b>Upper Bound</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__UPPER_BOUND = EcorePackage.EATTRIBUTE__UPPER_BOUND;

    /**
     * The feature id for the '<em><b>Many</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__MANY = EcorePackage.EATTRIBUTE__MANY;

    /**
     * The feature id for the '<em><b>Required</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__REQUIRED = EcorePackage.EATTRIBUTE__REQUIRED;

    /**
     * The feature id for the '<em><b>EType</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__ETYPE = EcorePackage.EATTRIBUTE__ETYPE;

    /**
     * The feature id for the '<em><b>EGeneric Type</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__EGENERIC_TYPE = EcorePackage.EATTRIBUTE__EGENERIC_TYPE;

    /**
     * The feature id for the '<em><b>Changeable</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__CHANGEABLE = EcorePackage.EATTRIBUTE__CHANGEABLE;

    /**
     * The feature id for the '<em><b>Volatile</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__VOLATILE = EcorePackage.EATTRIBUTE__VOLATILE;

    /**
     * The feature id for the '<em><b>Transient</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__TRANSIENT = EcorePackage.EATTRIBUTE__TRANSIENT;

    /**
     * The feature id for the '<em><b>Default Value Literal</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__DEFAULT_VALUE_LITERAL = EcorePackage.EATTRIBUTE__DEFAULT_VALUE_LITERAL;

    /**
     * The feature id for the '<em><b>Default Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__DEFAULT_VALUE = EcorePackage.EATTRIBUTE__DEFAULT_VALUE;

    /**
     * The feature id for the '<em><b>Unsettable</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__UNSETTABLE = EcorePackage.EATTRIBUTE__UNSETTABLE;

    /**
     * The feature id for the '<em><b>Derived</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__DERIVED = EcorePackage.EATTRIBUTE__DERIVED;

    /**
     * The feature id for the '<em><b>EContaining Class</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__ECONTAINING_CLASS = EcorePackage.EATTRIBUTE__ECONTAINING_CLASS;

    /**
     * The feature id for the '<em><b>ID</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__ID = EcorePackage.EATTRIBUTE__ID;

    /**
     * The feature id for the '<em><b>EAttribute Type</b></em>' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE__EATTRIBUTE_TYPE = EcorePackage.EATTRIBUTE__EATTRIBUTE_TYPE;

    /**
     * The number of structural features of the '<em>XAttribute</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XATTRIBUTE_FEATURE_COUNT = EcorePackage.EATTRIBUTE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.extension.impl.XEnumImpl <em>XEnum</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see com.metamatrix.metamodels.core.extension.impl.XEnumImpl
     * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXEnum()
     * @generated
     */
    int XENUM = 3;

    /**
     * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__EANNOTATIONS = EcorePackage.EENUM__EANNOTATIONS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__NAME = EcorePackage.EENUM__NAME;

    /**
     * The feature id for the '<em><b>Instance Class Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__INSTANCE_CLASS_NAME = EcorePackage.EENUM__INSTANCE_CLASS_NAME;

    /**
     * The feature id for the '<em><b>Instance Class</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__INSTANCE_CLASS = EcorePackage.EENUM__INSTANCE_CLASS;

    /**
     * The feature id for the '<em><b>Default Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__DEFAULT_VALUE = EcorePackage.EENUM__DEFAULT_VALUE;

    /**
     * The feature id for the '<em><b>Instance Type Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__INSTANCE_TYPE_NAME = EcorePackage.EENUM__INSTANCE_TYPE_NAME;

    /**
     * The feature id for the '<em><b>EPackage</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__EPACKAGE = EcorePackage.EENUM__EPACKAGE;

    /**
     * The feature id for the '<em><b>EType Parameters</b></em>' containment reference list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__ETYPE_PARAMETERS = EcorePackage.EENUM__ETYPE_PARAMETERS;

    /**
     * The feature id for the '<em><b>Serializable</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__SERIALIZABLE = EcorePackage.EENUM__SERIALIZABLE;

    /**
     * The feature id for the '<em><b>ELiterals</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XENUM__ELITERALS = EcorePackage.EENUM__ELITERALS;

    /**
     * The number of structural features of the '<em>XEnum</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM_FEATURE_COUNT = EcorePackage.EENUM_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.extension.impl.XEnumLiteralImpl <em>XEnum Literal</em>}'
     * class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see com.metamatrix.metamodels.core.extension.impl.XEnumLiteralImpl
     * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXEnumLiteral()
     * @generated
     */
    int XENUM_LITERAL = 4;

    /**
     * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int XENUM_LITERAL__EANNOTATIONS = EcorePackage.EENUM_LITERAL__EANNOTATIONS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM_LITERAL__NAME = EcorePackage.EENUM_LITERAL__NAME;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM_LITERAL__VALUE = EcorePackage.EENUM_LITERAL__VALUE;

    /**
     * The feature id for the '<em><b>Instance</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM_LITERAL__INSTANCE = EcorePackage.EENUM_LITERAL__INSTANCE;

    /**
     * The feature id for the '<em><b>Literal</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM_LITERAL__LITERAL = EcorePackage.EENUM_LITERAL__LITERAL;

    /**
     * The feature id for the '<em><b>EEnum</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM_LITERAL__EENUM = EcorePackage.EENUM_LITERAL__EENUM;

    /**
     * The number of structural features of the '<em>XEnum Literal</em>' class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     * @ordered
     */
    int XENUM_LITERAL_FEATURE_COUNT = EcorePackage.EENUM_LITERAL_FEATURE_COUNT + 0;

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.extension.XClass <em>XClass</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>XClass</em>'.
     * @see com.metamatrix.metamodels.core.extension.XClass
     * @generated
     */
    EClass getXClass();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.core.extension.XClass#getExtendedClass
     * <em>Extended Class</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for the reference '<em>Extended Class</em>'.
     * @see com.metamatrix.metamodels.core.extension.XClass#getExtendedClass()
     * @see #getXClass()
     * @generated
     */
    EReference getXClass_ExtendedClass();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.extension.XPackage <em>XPackage</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>XPackage</em>'.
     * @see com.metamatrix.metamodels.core.extension.XPackage
     * @generated
     */
    EClass getXPackage();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.extension.XAttribute <em>XAttribute</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>XAttribute</em>'.
     * @see com.metamatrix.metamodels.core.extension.XAttribute
     * @generated
     */
    EClass getXAttribute();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.extension.XEnum <em>XEnum</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>XEnum</em>'.
     * @see com.metamatrix.metamodels.core.extension.XEnum
     * @generated
     */
    EClass getXEnum();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.extension.XEnumLiteral <em>XEnum Literal</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>XEnum Literal</em>'.
     * @see com.metamatrix.metamodels.core.extension.XEnumLiteral
     * @generated
     */
    EClass getXEnumLiteral();

    /**
     * Returns the factory that creates the instances of the model. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ExtensionFactory getExtensionFactory();

    /**
     * <!-- begin-user-doc --> Defines literals for the meta objects that represent
     * <ul>
     * <li>each class,</li>
     * <li>each feature of each class,</li>
     * <li>each enum,</li>
     * <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * 
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link com.metamatrix.metamodels.core.extension.impl.XClassImpl <em>XClass</em>}'
         * class. <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @see com.metamatrix.metamodels.core.extension.impl.XClassImpl
         * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXClass()
         * @generated
         */
        EClass XCLASS = eINSTANCE.getXClass();

        /**
         * The meta object literal for the '<em><b>Extended Class</b></em>' reference feature. <!-- begin-user-doc --> <!--
         * end-user-doc -->
         * 
         * @generated
         */
        EReference XCLASS__EXTENDED_CLASS = eINSTANCE.getXClass_ExtendedClass();

        /**
         * The meta object literal for the '{@link com.metamatrix.metamodels.core.extension.impl.XPackageImpl <em>XPackage</em>}'
         * class. <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @see com.metamatrix.metamodels.core.extension.impl.XPackageImpl
         * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXPackage()
         * @generated
         */
        EClass XPACKAGE = eINSTANCE.getXPackage();

        /**
         * The meta object literal for the '{@link com.metamatrix.metamodels.core.extension.impl.XAttributeImpl
         * <em>XAttribute</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @see com.metamatrix.metamodels.core.extension.impl.XAttributeImpl
         * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXAttribute()
         * @generated
         */
        EClass XATTRIBUTE = eINSTANCE.getXAttribute();

        /**
         * The meta object literal for the '{@link com.metamatrix.metamodels.core.extension.impl.XEnumImpl <em>XEnum</em>}' class.
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @see com.metamatrix.metamodels.core.extension.impl.XEnumImpl
         * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXEnum()
         * @generated
         */
        EClass XENUM = eINSTANCE.getXEnum();

        /**
         * The meta object literal for the '{@link com.metamatrix.metamodels.core.extension.impl.XEnumLiteralImpl
         * <em>XEnum Literal</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
         * 
         * @see com.metamatrix.metamodels.core.extension.impl.XEnumLiteralImpl
         * @see com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl#getXEnumLiteral()
         * @generated
         */
        EClass XENUM_LITERAL = eINSTANCE.getXEnumLiteral();

    }

} // ExtensionPackage
