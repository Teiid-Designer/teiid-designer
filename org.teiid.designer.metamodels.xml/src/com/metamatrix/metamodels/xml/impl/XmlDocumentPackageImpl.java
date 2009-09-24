/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.impl;

import com.metamatrix.metamodels.xml.BuildStatus;
import java.util.List;

import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.impl.XSDPackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import com.metamatrix.metamodels.xml.ChoiceErrorMode;
import com.metamatrix.metamodels.xml.ChoiceOption;
import com.metamatrix.metamodels.xml.NormalizationType;
import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.ProcessingInstructionHolder;
import com.metamatrix.metamodels.xml.SoapEncoding;
import com.metamatrix.metamodels.xml.ValueType;
import com.metamatrix.metamodels.xml.XmlAll;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlBaseElement;
import com.metamatrix.metamodels.xml.XmlBuildable;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlComment;
import com.metamatrix.metamodels.xml.XmlCommentHolder;
import com.metamatrix.metamodels.xml.XmlContainerHolder;
import com.metamatrix.metamodels.xml.XmlContainerNode;
import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlElementHolder;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlFragmentUse;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.metamodels.xml.XmlSequence;
import com.metamatrix.metamodels.xml.XmlValueHolder;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XmlDocumentPackageImpl extends EPackageImpl implements XmlDocumentPackage {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static XmlDocumentPackage init() {
        if (isInited) return (XmlDocumentPackage)EPackage.Registry.INSTANCE.getEPackage(XmlDocumentPackage.eNS_URI);

        // Obtain or create and register package
        XmlDocumentPackageImpl theXmlDocumentPackage = (XmlDocumentPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof XmlDocumentPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new XmlDocumentPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        XSDPackageImpl.init();

        // Create package meta-data objects
        theXmlDocumentPackage.createPackageContents();

        // Initialize created meta-data
        theXmlDocumentPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theXmlDocumentPackage.freeze();

        return theXmlDocumentPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum choiceErrorModeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass choiceOptionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass processingInstructionEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass processingInstructionHolderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum soapEncodingEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum valueTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum buildStatusEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum normalizationTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EDataType listEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlAllEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlAttributeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlBaseElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlChoiceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlCommentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlCommentHolderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlContainerHolderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlContainerNodeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlDocumentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlDocumentEntityEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlDocumentNodeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlElementHolderEClass = null;
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlFragmentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlFragmentUseEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlNamespaceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlRootEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlSequenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlValueHolderEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlBuildableEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private XmlDocumentPackageImpl() {
        super(eNS_URI, XmlDocumentFactory.eINSTANCE);
    }

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        xmlFragmentEClass = createEClass(XML_FRAGMENT);
        createEAttribute(xmlFragmentEClass, XML_FRAGMENT__NAME);
        createEReference(xmlFragmentEClass, XML_FRAGMENT__ROOT);

        xmlDocumentEClass = createEClass(XML_DOCUMENT);
        createEAttribute(xmlDocumentEClass, XML_DOCUMENT__ENCODING);
        createEAttribute(xmlDocumentEClass, XML_DOCUMENT__FORMATTED);
        createEAttribute(xmlDocumentEClass, XML_DOCUMENT__VERSION);
        createEAttribute(xmlDocumentEClass, XML_DOCUMENT__STANDALONE);
        createEAttribute(xmlDocumentEClass, XML_DOCUMENT__SOAP_ENCODING);

        xmlDocumentEntityEClass = createEClass(XML_DOCUMENT_ENTITY);

        xmlElementEClass = createEClass(XML_ELEMENT);
        createEAttribute(xmlElementEClass, XML_ELEMENT__RECURSIVE);
        createEReference(xmlElementEClass, XML_ELEMENT__ATTRIBUTES);
        createEReference(xmlElementEClass, XML_ELEMENT__DECLARED_NAMESPACES);

        xmlAttributeEClass = createEClass(XML_ATTRIBUTE);
        createEAttribute(xmlAttributeEClass, XML_ATTRIBUTE__USE);
        createEReference(xmlAttributeEClass, XML_ATTRIBUTE__ELEMENT);

        xmlDocumentNodeEClass = createEClass(XML_DOCUMENT_NODE);
        createEAttribute(xmlDocumentNodeEClass, XML_DOCUMENT_NODE__NAME);
        createEAttribute(xmlDocumentNodeEClass, XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT);
        createEAttribute(xmlDocumentNodeEClass, XML_DOCUMENT_NODE__MIN_OCCURS);
        createEAttribute(xmlDocumentNodeEClass, XML_DOCUMENT_NODE__MAX_OCCURS);
        createEReference(xmlDocumentNodeEClass, XML_DOCUMENT_NODE__XSD_COMPONENT);
        createEReference(xmlDocumentNodeEClass, XML_DOCUMENT_NODE__NAMESPACE);

        xmlRootEClass = createEClass(XML_ROOT);
        createEReference(xmlRootEClass, XML_ROOT__FRAGMENT);

        xmlCommentEClass = createEClass(XML_COMMENT);
        createEAttribute(xmlCommentEClass, XML_COMMENT__TEXT);
        createEReference(xmlCommentEClass, XML_COMMENT__PARENT);

        xmlNamespaceEClass = createEClass(XML_NAMESPACE);
        createEAttribute(xmlNamespaceEClass, XML_NAMESPACE__PREFIX);
        createEAttribute(xmlNamespaceEClass, XML_NAMESPACE__URI);
        createEReference(xmlNamespaceEClass, XML_NAMESPACE__ELEMENT);

        xmlContainerNodeEClass = createEClass(XML_CONTAINER_NODE);
        createEAttribute(xmlContainerNodeEClass, XML_CONTAINER_NODE__EXCLUDE_FROM_DOCUMENT);
        createEAttribute(xmlContainerNodeEClass, XML_CONTAINER_NODE__MIN_OCCURS);
        createEAttribute(xmlContainerNodeEClass, XML_CONTAINER_NODE__MAX_OCCURS);
        createEReference(xmlContainerNodeEClass, XML_CONTAINER_NODE__XSD_COMPONENT);
        createEReference(xmlContainerNodeEClass, XML_CONTAINER_NODE__PARENT);

        xmlSequenceEClass = createEClass(XML_SEQUENCE);

        xmlAllEClass = createEClass(XML_ALL);

        xmlChoiceEClass = createEClass(XML_CHOICE);
        createEAttribute(xmlChoiceEClass, XML_CHOICE__DEFAULT_ERROR_MODE);
        createEReference(xmlChoiceEClass, XML_CHOICE__DEFAULT_OPTION);

        xmlCommentHolderEClass = createEClass(XML_COMMENT_HOLDER);
        createEReference(xmlCommentHolderEClass, XML_COMMENT_HOLDER__COMMENTS);

        processingInstructionEClass = createEClass(PROCESSING_INSTRUCTION);
        createEAttribute(processingInstructionEClass, PROCESSING_INSTRUCTION__RAW_TEXT);
        createEAttribute(processingInstructionEClass, PROCESSING_INSTRUCTION__TARGET);
        createEReference(processingInstructionEClass, PROCESSING_INSTRUCTION__PARENT);

        processingInstructionHolderEClass = createEClass(PROCESSING_INSTRUCTION_HOLDER);
        createEReference(processingInstructionHolderEClass, PROCESSING_INSTRUCTION_HOLDER__PROCESSING_INSTRUCTIONS);

        xmlElementHolderEClass = createEClass(XML_ELEMENT_HOLDER);
        createEReference(xmlElementHolderEClass, XML_ELEMENT_HOLDER__ELEMENTS);

        xmlFragmentUseEClass = createEClass(XML_FRAGMENT_USE);
        createEReference(xmlFragmentUseEClass, XML_FRAGMENT_USE__FRAGMENT);

        xmlBaseElementEClass = createEClass(XML_BASE_ELEMENT);
        createEReference(xmlBaseElementEClass, XML_BASE_ELEMENT__PARENT);

        xmlContainerHolderEClass = createEClass(XML_CONTAINER_HOLDER);
        createEReference(xmlContainerHolderEClass, XML_CONTAINER_HOLDER__CONTAINERS);

        choiceOptionEClass = createEClass(CHOICE_OPTION);
        createEAttribute(choiceOptionEClass, CHOICE_OPTION__CHOICE_CRITERIA);
        createEAttribute(choiceOptionEClass, CHOICE_OPTION__CHOICE_ORDER);
        createEReference(choiceOptionEClass, CHOICE_OPTION__DEFAULT_FOR);

        xmlValueHolderEClass = createEClass(XML_VALUE_HOLDER);
        createEAttribute(xmlValueHolderEClass, XML_VALUE_HOLDER__VALUE);
        createEAttribute(xmlValueHolderEClass, XML_VALUE_HOLDER__VALUE_TYPE);

        xmlBuildableEClass = createEClass(XML_BUILDABLE);
        createEAttribute(xmlBuildableEClass, XML_BUILDABLE__BUILD_STATE);

        // Create enums
        soapEncodingEEnum = createEEnum(SOAP_ENCODING);
        choiceErrorModeEEnum = createEEnum(CHOICE_ERROR_MODE);
        valueTypeEEnum = createEEnum(VALUE_TYPE);
        buildStatusEEnum = createEEnum(BUILD_STATUS);
        normalizationTypeEEnum = createEEnum(NORMALIZATION_TYPE);

        // Create data types
        listEDataType = createEDataType(LIST);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getChoiceErrorMode() {
        return choiceErrorModeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getChoiceOption() {
        return choiceOptionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getChoiceOption_ChoiceCriteria() {
        return (EAttribute)choiceOptionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getChoiceOption_ChoiceOrder() {
        return (EAttribute)choiceOptionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getChoiceOption_DefaultFor() {
        return (EReference)choiceOptionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProcessingInstruction() {
        return processingInstructionEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcessingInstruction_Parent() {
        return (EReference)processingInstructionEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcessingInstruction_RawText() {
        return (EAttribute)processingInstructionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcessingInstruction_Target() {
        return (EAttribute)processingInstructionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProcessingInstructionHolder() {
        return processingInstructionHolderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcessingInstructionHolder_ProcessingInstructions() {
        return (EReference)processingInstructionHolderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getSoapEncoding() {
        return soapEncodingEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getValueType() {
        return valueTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getBuildStatus() {
        return buildStatusEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getNormalizationType() {
        return normalizationTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EDataType getList() {
        return listEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlAll() {
        return xmlAllEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlAttribute() {
        return xmlAttributeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlAttribute_Use() {
        return (EAttribute)xmlAttributeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlAttribute_Element() {
        return (EReference)xmlAttributeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlBaseElement() {
        return xmlBaseElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlBaseElement_Parent() {
        return (EReference)xmlBaseElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlChoice() {
        return xmlChoiceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlChoice_DefaultErrorMode() {
        return (EAttribute)xmlChoiceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlChoice_DefaultOption() {
        return (EReference)xmlChoiceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlComment() {
        return xmlCommentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlComment_Parent() {
        return (EReference)xmlCommentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlComment_Text() {
        return (EAttribute)xmlCommentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlCommentHolder() {
        return xmlCommentHolderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlCommentHolder_Comments() {
        return (EReference)xmlCommentHolderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlContainerHolder() {
        return xmlContainerHolderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlContainerHolder_Containers() {
        return (EReference)xmlContainerHolderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlContainerNode() {
        return xmlContainerNodeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlContainerNode_ExcludeFromDocument() {
        return (EAttribute)xmlContainerNodeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getXmlContainerNode_MinOccurs() {
        return (EAttribute)xmlContainerNodeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getXmlContainerNode_MaxOccurs() {
        return (EAttribute)xmlContainerNodeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlContainerNode_Parent() {
        return (EReference)xmlContainerNodeEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlContainerNode_XsdComponent() {
        return (EReference)xmlContainerNodeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlDocument() {
        return xmlDocumentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlDocument_Encoding() {
        return (EAttribute)xmlDocumentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlDocument_Formatted() {
        return (EAttribute)xmlDocumentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlDocument_SoapEncoding() {
        return (EAttribute)xmlDocumentEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlDocument_Standalone() {
        return (EAttribute)xmlDocumentEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlDocument_Version() {
        return (EAttribute)xmlDocumentEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlDocumentEntity() {
        return xmlDocumentEntityEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlDocumentFactory getXmlDocumentFactory() {
        return (XmlDocumentFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlDocumentNode() {
        return xmlDocumentNodeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlDocumentNode_ExcludeFromDocument() {
        return (EAttribute)xmlDocumentNodeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getXmlDocumentNode_MinOccurs() {
        return (EAttribute)xmlDocumentNodeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getXmlDocumentNode_MaxOccurs() {
        return (EAttribute)xmlDocumentNodeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlDocumentNode_Name() {
        return (EAttribute)xmlDocumentNodeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlDocumentNode_Namespace() {
        return (EReference)xmlDocumentNodeEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlDocumentNode_XsdComponent() {
        return (EReference)xmlDocumentNodeEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlElement() {
        return xmlElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlElement_Attributes() {
        return (EReference)xmlElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlElement_DeclaredNamespaces() {
        return (EReference)xmlElementEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlElement_Recursive() {
        return (EAttribute)xmlElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlElementHolder() {
        return xmlElementHolderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlElementHolder_Elements() {
        return (EReference)xmlElementHolderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlFragment() {
        return xmlFragmentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlFragment_Name() {
        return (EAttribute)xmlFragmentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlFragment_Root() {
        return (EReference)xmlFragmentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlFragmentUse() {
        return xmlFragmentUseEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlFragmentUse_Fragment() {
        return (EReference)xmlFragmentUseEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlNamespace() {
        return xmlNamespaceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlNamespace_Element() {
        return (EReference)xmlNamespaceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlNamespace_Prefix() {
        return (EAttribute)xmlNamespaceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlNamespace_Uri() {
        return (EAttribute)xmlNamespaceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlRoot() {
        return xmlRootEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlRoot_Fragment() {
        return (EReference)xmlRootEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlSequence() {
        return xmlSequenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlValueHolder() {
        return xmlValueHolderEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlValueHolder_Value() {
        return (EAttribute)xmlValueHolderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlValueHolder_ValueType() {
        return (EAttribute)xmlValueHolderEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlBuildable() {
        return xmlBuildableEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlBuildable_BuildState() {
        return (EAttribute)xmlBuildableEClass.getEStructuralFeatures().get(0);
    }

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        XSDPackageImpl theXSDPackage = (XSDPackageImpl)EPackage.Registry.INSTANCE.getEPackage(XSDPackage.eNS_URI);

        // Add supertypes to classes
        xmlFragmentEClass.getESuperTypes().add(this.getXmlDocumentEntity());
        xmlFragmentEClass.getESuperTypes().add(this.getXmlCommentHolder());
        xmlFragmentEClass.getESuperTypes().add(this.getProcessingInstructionHolder());
        xmlDocumentEClass.getESuperTypes().add(this.getXmlFragment());
        xmlElementEClass.getESuperTypes().add(this.getXmlBaseElement());
        xmlElementEClass.getESuperTypes().add(this.getXmlCommentHolder());
        xmlElementEClass.getESuperTypes().add(this.getProcessingInstructionHolder());
        xmlElementEClass.getESuperTypes().add(this.getXmlElementHolder());
        xmlElementEClass.getESuperTypes().add(this.getXmlContainerHolder());
        xmlElementEClass.getESuperTypes().add(this.getXmlValueHolder());
        xmlAttributeEClass.getESuperTypes().add(this.getXmlDocumentNode());
        xmlAttributeEClass.getESuperTypes().add(this.getXmlValueHolder());
        xmlDocumentNodeEClass.getESuperTypes().add(this.getXmlDocumentEntity());
        xmlDocumentNodeEClass.getESuperTypes().add(this.getXmlBuildable());
        xmlRootEClass.getESuperTypes().add(this.getXmlElement());
        xmlCommentEClass.getESuperTypes().add(this.getXmlDocumentEntity());
        xmlNamespaceEClass.getESuperTypes().add(this.getXmlDocumentEntity());
        xmlContainerNodeEClass.getESuperTypes().add(this.getXmlDocumentEntity());
        xmlContainerNodeEClass.getESuperTypes().add(this.getXmlElementHolder());
        xmlContainerNodeEClass.getESuperTypes().add(this.getXmlContainerHolder());
        xmlContainerNodeEClass.getESuperTypes().add(this.getChoiceOption());
        xmlContainerNodeEClass.getESuperTypes().add(this.getXmlBuildable());
        xmlSequenceEClass.getESuperTypes().add(this.getXmlContainerNode());
        xmlAllEClass.getESuperTypes().add(this.getXmlContainerNode());
        xmlChoiceEClass.getESuperTypes().add(this.getXmlContainerNode());
        processingInstructionEClass.getESuperTypes().add(this.getXmlDocumentEntity());
        xmlFragmentUseEClass.getESuperTypes().add(this.getXmlBaseElement());
        xmlBaseElementEClass.getESuperTypes().add(this.getXmlDocumentNode());
        xmlBaseElementEClass.getESuperTypes().add(this.getChoiceOption());

        // Initialize classes and features; add operations and parameters
        initEClass(xmlFragmentEClass, XmlFragment.class, "XmlFragment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlFragment_Name(), ecorePackage.getEString(), "name", null, 0, 1, XmlFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlFragment_Root(), this.getXmlRoot(), this.getXmlRoot_Fragment(), "root", null, 1, 1, XmlFragment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlDocumentEClass, XmlDocument.class, "XmlDocument", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlDocument_Encoding(), ecorePackage.getEString(), "encoding", "UTF-8", 0, 1, XmlDocument.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getXmlDocument_Formatted(), ecorePackage.getEBoolean(), "formatted", "false", 0, 1, XmlDocument.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getXmlDocument_Version(), ecorePackage.getEString(), "version", "1.0", 0, 1, XmlDocument.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getXmlDocument_Standalone(), ecorePackage.getEBoolean(), "standalone", "false", 0, 1, XmlDocument.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getXmlDocument_SoapEncoding(), this.getSoapEncoding(), "soapEncoding", "NONE", 0, 1, XmlDocument.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass(xmlDocumentEntityEClass, XmlDocumentEntity.class, "XmlDocumentEntity", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        addEOperation(xmlDocumentEntityEClass, ecorePackage.getEString(), "getPathInDocument"); //$NON-NLS-1$

        addEOperation(xmlDocumentEntityEClass, ecorePackage.getEString(), "getXPath"); //$NON-NLS-1$

        initEClass(xmlElementEClass, XmlElement.class, "XmlElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlElement_Recursive(), ecorePackage.getEBoolean(), "recursive", "false", 0, 1, XmlElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getXmlElement_Attributes(), this.getXmlAttribute(), this.getXmlAttribute_Element(), "attributes", null, 0, -1, XmlElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlElement_DeclaredNamespaces(), this.getXmlNamespace(), this.getXmlNamespace_Element(), "declaredNamespaces", null, 0, -1, XmlElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlAttributeEClass, XmlAttribute.class, "XmlAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlAttribute_Use(), theXSDPackage.getXSDAttributeUseCategory(), "use", null, 0, 1, XmlAttribute.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlAttribute_Element(), this.getXmlElement(), this.getXmlElement_Attributes(), "element", null, 1, 1, XmlAttribute.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlDocumentNodeEClass, XmlDocumentNode.class, "XmlDocumentNode", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlDocumentNode_Name(), ecorePackage.getEString(), "name", null, 0, 1, XmlDocumentNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getXmlDocumentNode_ExcludeFromDocument(), ecorePackage.getEBoolean(), "excludeFromDocument", "false", 0, 1, XmlDocumentNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getXmlDocumentNode_MinOccurs(), ecorePackage.getEInt(), "minOccurs", "1", 0, 1, XmlDocumentNode.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getXmlDocumentNode_MaxOccurs(), ecorePackage.getEInt(), "maxOccurs", "1", 0, 1, XmlDocumentNode.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getXmlDocumentNode_XsdComponent(), theXSDPackage.getXSDComponent(), null, "xsdComponent", null, 0, 1, XmlDocumentNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlDocumentNode_Namespace(), this.getXmlNamespace(), null, "namespace", null, 0, 1, XmlDocumentNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlRootEClass, XmlRoot.class, "XmlRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlRoot_Fragment(), this.getXmlFragment(), this.getXmlFragment_Root(), "fragment", null, 0, 1, XmlRoot.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlCommentEClass, XmlComment.class, "XmlComment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlComment_Text(), ecorePackage.getEString(), "text", null, 0, 1, XmlComment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlComment_Parent(), this.getXmlCommentHolder(), this.getXmlCommentHolder_Comments(), "parent", null, 1, 1, XmlComment.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlNamespaceEClass, XmlNamespace.class, "XmlNamespace", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlNamespace_Prefix(), ecorePackage.getEString(), "prefix", null, 0, 1, XmlNamespace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getXmlNamespace_Uri(), ecorePackage.getEString(), "uri", null, 0, 1, XmlNamespace.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlNamespace_Element(), this.getXmlElement(), this.getXmlElement_DeclaredNamespaces(), "element", null, 1, 1, XmlNamespace.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlContainerNodeEClass, XmlContainerNode.class, "XmlContainerNode", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlContainerNode_ExcludeFromDocument(), ecorePackage.getEBoolean(), "excludeFromDocument", "false", 0, 1, XmlContainerNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getXmlContainerNode_MinOccurs(), ecorePackage.getEInt(), "minOccurs", null, 0, 1, XmlContainerNode.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getXmlContainerNode_MaxOccurs(), ecorePackage.getEInt(), "maxOccurs", null, 0, 1, XmlContainerNode.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlContainerNode_XsdComponent(), theXSDPackage.getXSDComponent(), null, "xsdComponent", null, 0, 1, XmlContainerNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlContainerNode_Parent(), this.getXmlContainerHolder(), this.getXmlContainerHolder_Containers(), "parent", null, 1, 1, XmlContainerNode.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlSequenceEClass, XmlSequence.class, "XmlSequence", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(xmlAllEClass, XmlAll.class, "XmlAll", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(xmlChoiceEClass, XmlChoice.class, "XmlChoice", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlChoice_DefaultErrorMode(), this.getChoiceErrorMode(), "defaultErrorMode", "THROW", 0, 1, XmlChoice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getXmlChoice_DefaultOption(), this.getChoiceOption(), this.getChoiceOption_DefaultFor(), "defaultOption", null, 0, 1, XmlChoice.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(xmlChoiceEClass, this.getList(), "getOrderedChoiceOptions"); //$NON-NLS-1$

        EOperation op = addEOperation(xmlChoiceEClass, null, "setOrderedChoiceOptions"); //$NON-NLS-1$
        addEParameter(op, this.getList(), "options"); //$NON-NLS-1$

        initEClass(xmlCommentHolderEClass, XmlCommentHolder.class, "XmlCommentHolder", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlCommentHolder_Comments(), this.getXmlComment(), this.getXmlComment_Parent(), "comments", null, 0, -1, XmlCommentHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(processingInstructionEClass, ProcessingInstruction.class, "ProcessingInstruction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getProcessingInstruction_RawText(), ecorePackage.getEString(), "rawText", null, 0, 1, ProcessingInstruction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProcessingInstruction_Target(), ecorePackage.getEString(), "target", null, 0, 1, ProcessingInstruction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProcessingInstruction_Parent(), this.getProcessingInstructionHolder(), this.getProcessingInstructionHolder_ProcessingInstructions(), "parent", null, 1, 1, ProcessingInstruction.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(processingInstructionHolderEClass, ProcessingInstructionHolder.class, "ProcessingInstructionHolder", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getProcessingInstructionHolder_ProcessingInstructions(), this.getProcessingInstruction(), this.getProcessingInstruction_Parent(), "processingInstructions", null, 0, -1, ProcessingInstructionHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlElementHolderEClass, XmlElementHolder.class, "XmlElementHolder", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlElementHolder_Elements(), this.getXmlBaseElement(), this.getXmlBaseElement_Parent(), "elements", null, 0, -1, XmlElementHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlFragmentUseEClass, XmlFragmentUse.class, "XmlFragmentUse", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlFragmentUse_Fragment(), this.getXmlFragment(), null, "fragment", null, 1, 1, XmlFragmentUse.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlBaseElementEClass, XmlBaseElement.class, "XmlBaseElement", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlBaseElement_Parent(), this.getXmlElementHolder(), this.getXmlElementHolder_Elements(), "parent", null, 0, 1, XmlBaseElement.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlContainerHolderEClass, XmlContainerHolder.class, "XmlContainerHolder", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlContainerHolder_Containers(), this.getXmlContainerNode(), this.getXmlContainerNode_Parent(), "containers", null, 0, -1, XmlContainerHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(choiceOptionEClass, ChoiceOption.class, "ChoiceOption", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getChoiceOption_ChoiceCriteria(), ecorePackage.getEString(), "choiceCriteria", null, 0, 1, ChoiceOption.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getChoiceOption_ChoiceOrder(), ecorePackage.getEInt(), "choiceOrder", "-1", 0, 1, ChoiceOption.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getChoiceOption_DefaultFor(), this.getXmlChoice(), this.getXmlChoice_DefaultOption(), "defaultFor", null, 0, 1, ChoiceOption.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlValueHolderEClass, XmlValueHolder.class, "XmlValueHolder", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlValueHolder_Value(), ecorePackage.getEString(), "value", null, 0, 1, XmlValueHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getXmlValueHolder_ValueType(), this.getValueType(), "valueType", "IGNORED", 0, 1, XmlValueHolder.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        addEOperation(xmlValueHolderEClass, ecorePackage.getEBoolean(), "isValueFixed"); //$NON-NLS-1$

        addEOperation(xmlValueHolderEClass, ecorePackage.getEBoolean(), "isValueDefault"); //$NON-NLS-1$

        initEClass(xmlBuildableEClass, XmlBuildable.class, "XmlBuildable", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlBuildable_BuildState(), this.getBuildStatus(), "buildState", "COMPLETE", 0, 1, XmlBuildable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        // Initialize enums and add enum literals
        initEEnum(soapEncodingEEnum, SoapEncoding.class, "SoapEncoding"); //$NON-NLS-1$
        addEEnumLiteral(soapEncodingEEnum, SoapEncoding.NONE_LITERAL);
        addEEnumLiteral(soapEncodingEEnum, SoapEncoding.DEFAULT_LITERAL);

        initEEnum(choiceErrorModeEEnum, ChoiceErrorMode.class, "ChoiceErrorMode"); //$NON-NLS-1$
        addEEnumLiteral(choiceErrorModeEEnum, ChoiceErrorMode.THROW_LITERAL);
        addEEnumLiteral(choiceErrorModeEEnum, ChoiceErrorMode.RECORD_LITERAL);
        addEEnumLiteral(choiceErrorModeEEnum, ChoiceErrorMode.DISCARD_LITERAL);

        initEEnum(valueTypeEEnum, ValueType.class, "ValueType"); //$NON-NLS-1$
        addEEnumLiteral(valueTypeEEnum, ValueType.IGNORED_LITERAL);
        addEEnumLiteral(valueTypeEEnum, ValueType.DEFAULT_LITERAL);
        addEEnumLiteral(valueTypeEEnum, ValueType.FIXED_LITERAL);

        initEEnum(buildStatusEEnum, BuildStatus.class, "BuildStatus"); //$NON-NLS-1$
        addEEnumLiteral(buildStatusEEnum, BuildStatus.COMPLETE_LITERAL);
        addEEnumLiteral(buildStatusEEnum, BuildStatus.INCOMPLETE_LITERAL);

        initEEnum(normalizationTypeEEnum, NormalizationType.class, "NormalizationType"); //$NON-NLS-1$
        addEEnumLiteral(normalizationTypeEEnum, NormalizationType.PRESERVE_LITERAL);
        addEEnumLiteral(normalizationTypeEEnum, NormalizationType.REPLACE_LITERAL);
        addEEnumLiteral(normalizationTypeEEnum, NormalizationType.COLLAPSE_LITERAL);

        // Initialize data types
        initEDataType(listEDataType, List.class, "List", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //XmlDocumentPackageImpl
