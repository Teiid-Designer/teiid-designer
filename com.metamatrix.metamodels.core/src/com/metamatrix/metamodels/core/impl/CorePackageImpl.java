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

package com.metamatrix.metamodels.core.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.Datatype;
import com.metamatrix.metamodels.core.Identifiable;
import com.metamatrix.metamodels.core.Link;
import com.metamatrix.metamodels.core.LinkContainer;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class CorePackageImpl extends EPackageImpl implements CorePackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass annotationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass annotationContainerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modelAnnotationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass linkEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass linkContainerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass datatypeEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass identifiableEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modelImportEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum modelTypeEEnum = null;

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
     * @see com.metamatrix.metamodels.core.CorePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private CorePackageImpl() {
        super(eNS_URI, CoreFactory.eINSTANCE);
    }

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
    public static CorePackage init() {
        if (isInited) return (CorePackage)EPackage.Registry.INSTANCE.getEPackage(CorePackage.eNS_URI);

        // Obtain or create and register package
        CorePackageImpl theCorePackage = (CorePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof CorePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new CorePackageImpl());

        isInited = true;

        // Initialize simple dependencies
        ExtensionPackageImpl.init();
        EcorePackageImpl.init();

        // Create package meta-data objects
        theCorePackage.createPackageContents();

        // Initialize created meta-data
        theCorePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theCorePackage.freeze();

        return theCorePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAnnotation() {
        return annotationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getAnnotation_Description() {
        return (EAttribute)annotationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getAnnotation_Keywords() {
        return (EAttribute)annotationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAnnotation_AnnotatedObject() {
        return (EReference)annotationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAnnotation_ExtensionObject() {
        return (EReference)annotationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAnnotation_Tags() {
        return (EReference)annotationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAnnotation_AnnotationContainer() {
        return (EReference)annotationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAnnotationContainer() {
        return annotationContainerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAnnotationContainer_Annotations() {
        return (EReference)annotationContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getModelAnnotation() {
        return modelAnnotationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_Description() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_NameInSource() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_PrimaryMetamodelUri() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_ModelType() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_MaxSetSize() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_Visible() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_SupportsDistinct() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_SupportsJoin() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_SupportsOrderBy() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_SupportsOuterJoin() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_SupportsWhereAll() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelAnnotation_Tags() {
        return (EReference)modelAnnotationEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_NamespaceUri() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_ProducerName() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(13);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelAnnotation_ProducerVersion() {
        return (EAttribute)modelAnnotationEClass.getEStructuralFeatures().get(14);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelAnnotation_ModelImports() {
        return (EReference)modelAnnotationEClass.getEStructuralFeatures().get(15);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelAnnotation_ExtensionPackage() {
        return (EReference)modelAnnotationEClass.getEStructuralFeatures().get(16);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getLink() {
        return linkEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getLink_Name() {
        return (EAttribute)linkEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getLink_Description() {
        return (EAttribute)linkEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getLink_References() {
        return (EAttribute)linkEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLink_LinkedObjects() {
        return (EReference)linkEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLink_LinkContainer() {
        return (EReference)linkEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getLinkContainer() {
        return linkContainerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLinkContainer_Links() {
        return (EReference)linkContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDatatype() {
        return datatypeEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getIdentifiable() {
        return identifiableEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIdentifiable_Uuid() {
        return (EAttribute)identifiableEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getModelImport() {
        return modelImportEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelImport_Name() {
        return (EAttribute)modelImportEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelImport_Path() {
        return (EAttribute)modelImportEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelImport_ModelLocation() {
        return (EAttribute)modelImportEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelImport_Uuid() {
        return (EAttribute)modelImportEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelImport_ModelType() {
        return (EAttribute)modelImportEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelImport_PrimaryMetamodelUri() {
        return (EAttribute)modelImportEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelImport_Model() {
        return (EReference)modelImportEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getModelType() {
        return modelTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CoreFactory getCoreFactory() {
        return (CoreFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

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
        annotationEClass = createEClass(ANNOTATION);
        createEAttribute(annotationEClass, ANNOTATION__DESCRIPTION);
        createEAttribute(annotationEClass, ANNOTATION__KEYWORDS);
        createEReference(annotationEClass, ANNOTATION__TAGS);
        createEReference(annotationEClass, ANNOTATION__ANNOTATION_CONTAINER);
        createEReference(annotationEClass, ANNOTATION__ANNOTATED_OBJECT);
        createEReference(annotationEClass, ANNOTATION__EXTENSION_OBJECT);

        annotationContainerEClass = createEClass(ANNOTATION_CONTAINER);
        createEReference(annotationContainerEClass, ANNOTATION_CONTAINER__ANNOTATIONS);

        modelAnnotationEClass = createEClass(MODEL_ANNOTATION);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__DESCRIPTION);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__NAME_IN_SOURCE);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__PRIMARY_METAMODEL_URI);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__MODEL_TYPE);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__MAX_SET_SIZE);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__VISIBLE);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__SUPPORTS_DISTINCT);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__SUPPORTS_JOIN);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__SUPPORTS_ORDER_BY);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__SUPPORTS_WHERE_ALL);
        createEReference(modelAnnotationEClass, MODEL_ANNOTATION__TAGS);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__NAMESPACE_URI);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__PRODUCER_NAME);
        createEAttribute(modelAnnotationEClass, MODEL_ANNOTATION__PRODUCER_VERSION);
        createEReference(modelAnnotationEClass, MODEL_ANNOTATION__MODEL_IMPORTS);
        createEReference(modelAnnotationEClass, MODEL_ANNOTATION__EXTENSION_PACKAGE);

        linkEClass = createEClass(LINK);
        createEAttribute(linkEClass, LINK__NAME);
        createEAttribute(linkEClass, LINK__DESCRIPTION);
        createEAttribute(linkEClass, LINK__REFERENCES);
        createEReference(linkEClass, LINK__LINKED_OBJECTS);
        createEReference(linkEClass, LINK__LINK_CONTAINER);

        linkContainerEClass = createEClass(LINK_CONTAINER);
        createEReference(linkContainerEClass, LINK_CONTAINER__LINKS);

        datatypeEClass = createEClass(DATATYPE);

        identifiableEClass = createEClass(IDENTIFIABLE);
        createEAttribute(identifiableEClass, IDENTIFIABLE__UUID);

        modelImportEClass = createEClass(MODEL_IMPORT);
        createEAttribute(modelImportEClass, MODEL_IMPORT__NAME);
        createEAttribute(modelImportEClass, MODEL_IMPORT__PATH);
        createEAttribute(modelImportEClass, MODEL_IMPORT__MODEL_LOCATION);
        createEAttribute(modelImportEClass, MODEL_IMPORT__UUID);
        createEAttribute(modelImportEClass, MODEL_IMPORT__MODEL_TYPE);
        createEAttribute(modelImportEClass, MODEL_IMPORT__PRIMARY_METAMODEL_URI);
        createEReference(modelImportEClass, MODEL_IMPORT__MODEL);

        // Create enums
        modelTypeEEnum = createEEnum(MODEL_TYPE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

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
        EcorePackageImpl theEcorePackage = (EcorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
        ExtensionPackageImpl theExtensionPackage = (ExtensionPackageImpl)EPackage.Registry.INSTANCE.getEPackage(ExtensionPackage.eNS_URI);

        // Add supertypes to classes
        datatypeEClass.getESuperTypes().add(this.getIdentifiable());

        // Initialize classes and features; add operations and parameters
        initEClass(annotationEClass, Annotation.class, "Annotation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getAnnotation_Description(), ecorePackage.getEString(), "description", null, 0, 1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getAnnotation_Keywords(), ecorePackage.getEString(), "keywords", null, 0, -1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getAnnotation_Tags(), theEcorePackage.getEStringToStringMapEntry(), null, "tags", null, 0, -1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getAnnotation_AnnotationContainer(), this.getAnnotationContainer(), this.getAnnotationContainer_Annotations(), "annotationContainer", null, 0, 1, Annotation.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getAnnotation_AnnotatedObject(), theEcorePackage.getEObject(), null, "annotatedObject", null, 0, 1, Annotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getAnnotation_ExtensionObject(), theEcorePackage.getEObject(), null, "extensionObject", null, 0, 1, Annotation.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(annotationContainerEClass, AnnotationContainer.class, "AnnotationContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getAnnotationContainer_Annotations(), this.getAnnotation(), this.getAnnotation_AnnotationContainer(), "annotations", null, 0, -1, AnnotationContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(modelAnnotationEClass, ModelAnnotation.class, "ModelAnnotation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getModelAnnotation_Description(), ecorePackage.getEString(), "description", null, 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelAnnotation_NameInSource(), ecorePackage.getEString(), "nameInSource", null, 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelAnnotation_PrimaryMetamodelUri(), ecorePackage.getEString(), "primaryMetamodelUri", null, 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelAnnotation_ModelType(), this.getModelType(), "modelType", "UNKNOWN", 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelAnnotation_MaxSetSize(), ecorePackage.getEInt(), "maxSetSize", "100", 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelAnnotation_Visible(), ecorePackage.getEBoolean(), "visible", "true", 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelAnnotation_SupportsDistinct(), ecorePackage.getEBoolean(), "supportsDistinct", "true", 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelAnnotation_SupportsJoin(), ecorePackage.getEBoolean(), "supportsJoin", "true", 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelAnnotation_SupportsOrderBy(), ecorePackage.getEBoolean(), "supportsOrderBy", "true", 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelAnnotation_SupportsOuterJoin(), ecorePackage.getEBoolean(), "supportsOuterJoin", "true", 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelAnnotation_SupportsWhereAll(), ecorePackage.getEBoolean(), "supportsWhereAll", "true", 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getModelAnnotation_Tags(), theEcorePackage.getEStringToStringMapEntry(), null, "tags", null, 0, -1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelAnnotation_NamespaceUri(), ecorePackage.getEString(), "namespaceUri", null, 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelAnnotation_ProducerName(), ecorePackage.getEString(), "ProducerName", null, 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelAnnotation_ProducerVersion(), ecorePackage.getEString(), "ProducerVersion", null, 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelAnnotation_ModelImports(), this.getModelImport(), this.getModelImport_Model(), "modelImports", null, 0, -1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelAnnotation_ExtensionPackage(), theExtensionPackage.getXPackage(), null, "extensionPackage", null, 0, 1, ModelAnnotation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        EOperation op = addEOperation(modelAnnotationEClass, this.getModelImport(), "findModelImportByPath"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEString(), "path"); //$NON-NLS-1$

        op = addEOperation(modelAnnotationEClass, this.getModelImport(), "findModelImportByUuid"); //$NON-NLS-1$
        addEParameter(op, ecorePackage.getEString(), "uuid"); //$NON-NLS-1$

        initEClass(linkEClass, Link.class, "Link", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getLink_Name(), ecorePackage.getEString(), "name", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getLink_Description(), ecorePackage.getEString(), "description", null, 0, 1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getLink_References(), ecorePackage.getEString(), "references", null, 0, -1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getLink_LinkedObjects(), theEcorePackage.getEObject(), null, "linkedObjects", null, 0, -1, Link.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getLink_LinkContainer(), this.getLinkContainer(), this.getLinkContainer_Links(), "linkContainer", null, 0, 1, Link.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(linkContainerEClass, LinkContainer.class, "LinkContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getLinkContainer_Links(), this.getLink(), this.getLink_LinkContainer(), "links", null, 0, -1, LinkContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(datatypeEClass, Datatype.class, "Datatype", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(identifiableEClass, Identifiable.class, "Identifiable", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getIdentifiable_Uuid(), ecorePackage.getEString(), "uuid", null, 0, 1, Identifiable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(modelImportEClass, ModelImport.class, "ModelImport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getModelImport_Name(), ecorePackage.getEString(), "name", null, 0, 1, ModelImport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelImport_Path(), ecorePackage.getEString(), "path", null, 0, 1, ModelImport.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelImport_ModelLocation(), ecorePackage.getEString(), "modelLocation", null, 0, 1, ModelImport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelImport_Uuid(), ecorePackage.getEString(), "uuid", null, 0, 1, ModelImport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelImport_ModelType(), this.getModelType(), "modelType", "UNKNOWN", 0, 1, ModelImport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelImport_PrimaryMetamodelUri(), ecorePackage.getEString(), "primaryMetamodelUri", null, 0, 1, ModelImport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelImport_Model(), this.getModelAnnotation(), this.getModelAnnotation_ModelImports(), "model", null, 0, 1, ModelImport.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(modelTypeEEnum, ModelType.class, "ModelType"); //$NON-NLS-1$
        addEEnumLiteral(modelTypeEEnum, ModelType.PHYSICAL_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.VIRTUAL_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.TYPE_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.VDB_ARCHIVE_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.UNKNOWN_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.FUNCTION_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.CONFIGURATION_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.METAMODEL_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.EXTENSION_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.LOGICAL_LITERAL);
        addEEnumLiteral(modelTypeEEnum, ModelType.MATERIALIZATION_LITERAL);

        // Create resource
        createResource(eNS_URI);
    }

} //CorePackageImpl
