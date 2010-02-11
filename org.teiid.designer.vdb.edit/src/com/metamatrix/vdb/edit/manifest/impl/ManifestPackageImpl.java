/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.impl;

import java.util.Date;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl;
import com.metamatrix.metamodels.core.impl.CorePackageImpl;
import com.metamatrix.modeler.core.validation.ProblemMarker;
import com.metamatrix.modeler.core.validation.ProblemMarkerContainer;
import com.metamatrix.modeler.core.validation.Severity;
import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ModelAccessibility;
import com.metamatrix.vdb.edit.manifest.ModelReference;
import com.metamatrix.vdb.edit.manifest.ModelSource;
import com.metamatrix.vdb.edit.manifest.ModelSourceProperty;
import com.metamatrix.vdb.edit.manifest.NonModelReference;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ManifestPackageImpl extends EPackageImpl implements ManifestPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass virtualDatabaseEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modelReferenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass problemMarkerContainerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass problemMarkerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modelSourceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modelSourcePropertyEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass wsdlOptionsEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass nonModelReferenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum severityEEnum = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EEnum modelAccessibilityEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType javaDateEDataType = null;

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
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ManifestPackageImpl() {
        super(eNS_URI, ManifestFactory.eINSTANCE);
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
    public static ManifestPackage init() {
        if (isInited) return (ManifestPackage)EPackage.Registry.INSTANCE.getEPackage(ManifestPackage.eNS_URI);

        // Obtain or create and register package
        ManifestPackageImpl theManifestPackage = (ManifestPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ManifestPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ManifestPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackageImpl.init();
        CorePackageImpl.init();
        ExtensionPackageImpl.init();

        // Create package meta-data objects
        theManifestPackage.createPackageContents();

        // Initialize created meta-data
        theManifestPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theManifestPackage.freeze();

        return theManifestPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getVirtualDatabase() {
        return virtualDatabaseEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_Name() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_Identifier() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_Uuid() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_Description() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_Version() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_Provider() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_TimeLastChanged() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_TimeLastProduced() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_TimeLastChangedAsDate() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_TimeLastProducedAsDate() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_ProducerName() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_ProducerVersion() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getVirtualDatabase_IncludeModelFiles() {
        return (EAttribute)virtualDatabaseEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getVirtualDatabase_Models() {
        return (EReference)virtualDatabaseEClass.getEStructuralFeatures().get(13);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getVirtualDatabase_WsdlOptions() {
        return (EReference)virtualDatabaseEClass.getEStructuralFeatures().get(14);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getVirtualDatabase_NonModels() {
        return (EReference)virtualDatabaseEClass.getEStructuralFeatures().get(15);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getModelReference() {
        return modelReferenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelReference_Version() {
        return (EAttribute)modelReferenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelReference_Uri() {
        return (EAttribute)modelReferenceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getModelReference_Visible() {
        return (EAttribute)modelReferenceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getModelReference_Accessibility() {
        return (EAttribute)modelReferenceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getModelReference_TimeLastSynchronized() {
        return (EAttribute)modelReferenceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getModelReference_TimeLastSynchronizedAsDate() {
        return (EAttribute)modelReferenceEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getModelReference_Checksum() {
        return (EAttribute)modelReferenceEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelReference_VirtualDatabase() {
        return (EReference)modelReferenceEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelReference_Uses() {
        return (EReference)modelReferenceEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelReference_UsedBy() {
        return (EReference)modelReferenceEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelReference_ModelSource() {
        return (EReference)modelReferenceEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProblemMarkerContainer() {
        return problemMarkerContainerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProblemMarkerContainer_Severity() {
        return (EAttribute)problemMarkerContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProblemMarkerContainer_Markers() {
        return (EReference)problemMarkerContainerEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProblemMarker() {
        return problemMarkerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProblemMarker_Severity() {
        return (EAttribute)problemMarkerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProblemMarker_Message() {
        return (EAttribute)problemMarkerEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProblemMarker_Target() {
        return (EAttribute)problemMarkerEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProblemMarker_TargetUri() {
        return (EAttribute)problemMarkerEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProblemMarker_Code() {
        return (EAttribute)problemMarkerEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProblemMarker_StackTrace() {
        return (EAttribute)problemMarkerEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProblemMarker_Marked() {
        return (EReference)problemMarkerEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProblemMarker_Children() {
        return (EReference)problemMarkerEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProblemMarker_Parent() {
        return (EReference)problemMarkerEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getModelSource() {
        return modelSourceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelSource_Properties() {
        return (EReference)modelSourceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelSource_Model() {
        return (EReference)modelSourceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getModelSourceProperty() {
        return modelSourcePropertyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelSourceProperty_Name() {
        return (EAttribute)modelSourcePropertyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelSourceProperty_Value() {
        return (EAttribute)modelSourcePropertyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelSourceProperty_Source() {
        return (EReference)modelSourcePropertyEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getWsdlOptions() {
        return wsdlOptionsEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getWsdlOptions_TargetNamespaceUri() {
        return (EAttribute)wsdlOptionsEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getWsdlOptions_DefaultNamespaceUri() {
        return (EAttribute)wsdlOptionsEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getWsdlOptions_VirtualDatabase() {
        return (EReference)wsdlOptionsEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getNonModelReference() {
        return nonModelReferenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getNonModelReference_Name() {
        return (EAttribute)nonModelReferenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getNonModelReference_Path() {
        return (EAttribute)nonModelReferenceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getNonModelReference_Checksum() {
        return (EAttribute)nonModelReferenceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getNonModelReference_Properties() {
        return (EReference)nonModelReferenceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getNonModelReference_VirtualDatabase() {
        return (EReference)nonModelReferenceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getSeverity() {
        return severityEEnum;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EEnum getModelAccessibility() {
        return modelAccessibilityEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getJavaDate() {
        return javaDateEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ManifestFactory getManifestFactory() {
        return (ManifestFactory)getEFactoryInstance();
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
        virtualDatabaseEClass = createEClass(VIRTUAL_DATABASE);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__NAME);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__IDENTIFIER);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__UUID);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__DESCRIPTION);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__VERSION);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__PROVIDER);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__TIME_LAST_CHANGED);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__TIME_LAST_PRODUCED);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__TIME_LAST_CHANGED_AS_DATE);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__TIME_LAST_PRODUCED_AS_DATE);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__PRODUCER_NAME);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__PRODUCER_VERSION);
        createEAttribute(virtualDatabaseEClass, VIRTUAL_DATABASE__INCLUDE_MODEL_FILES);
        createEReference(virtualDatabaseEClass, VIRTUAL_DATABASE__MODELS);
        createEReference(virtualDatabaseEClass, VIRTUAL_DATABASE__WSDL_OPTIONS);
        createEReference(virtualDatabaseEClass, VIRTUAL_DATABASE__NON_MODELS);

        modelReferenceEClass = createEClass(MODEL_REFERENCE);
        createEAttribute(modelReferenceEClass, MODEL_REFERENCE__VERSION);
        createEAttribute(modelReferenceEClass, MODEL_REFERENCE__URI);
        createEAttribute(modelReferenceEClass, MODEL_REFERENCE__VISIBLE);
        createEAttribute(modelReferenceEClass, MODEL_REFERENCE__ACCESSIBILITY);
        createEAttribute(modelReferenceEClass, MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED);
        createEAttribute(modelReferenceEClass, MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED_AS_DATE);
        createEAttribute(modelReferenceEClass, MODEL_REFERENCE__CHECKSUM);
        createEReference(modelReferenceEClass, MODEL_REFERENCE__VIRTUAL_DATABASE);
        createEReference(modelReferenceEClass, MODEL_REFERENCE__USES);
        createEReference(modelReferenceEClass, MODEL_REFERENCE__USED_BY);
        createEReference(modelReferenceEClass, MODEL_REFERENCE__MODEL_SOURCE);

        problemMarkerContainerEClass = createEClass(PROBLEM_MARKER_CONTAINER);
        createEAttribute(problemMarkerContainerEClass, PROBLEM_MARKER_CONTAINER__SEVERITY);
        createEReference(problemMarkerContainerEClass, PROBLEM_MARKER_CONTAINER__MARKERS);

        problemMarkerEClass = createEClass(PROBLEM_MARKER);
        createEAttribute(problemMarkerEClass, PROBLEM_MARKER__SEVERITY);
        createEAttribute(problemMarkerEClass, PROBLEM_MARKER__MESSAGE);
        createEAttribute(problemMarkerEClass, PROBLEM_MARKER__TARGET);
        createEAttribute(problemMarkerEClass, PROBLEM_MARKER__TARGET_URI);
        createEAttribute(problemMarkerEClass, PROBLEM_MARKER__CODE);
        createEAttribute(problemMarkerEClass, PROBLEM_MARKER__STACK_TRACE);
        createEReference(problemMarkerEClass, PROBLEM_MARKER__MARKED);
        createEReference(problemMarkerEClass, PROBLEM_MARKER__CHILDREN);
        createEReference(problemMarkerEClass, PROBLEM_MARKER__PARENT);

        modelSourceEClass = createEClass(MODEL_SOURCE);
        createEReference(modelSourceEClass, MODEL_SOURCE__PROPERTIES);
        createEReference(modelSourceEClass, MODEL_SOURCE__MODEL);

        modelSourcePropertyEClass = createEClass(MODEL_SOURCE_PROPERTY);
        createEAttribute(modelSourcePropertyEClass, MODEL_SOURCE_PROPERTY__NAME);
        createEAttribute(modelSourcePropertyEClass, MODEL_SOURCE_PROPERTY__VALUE);
        createEReference(modelSourcePropertyEClass, MODEL_SOURCE_PROPERTY__SOURCE);

        wsdlOptionsEClass = createEClass(WSDL_OPTIONS);
        createEAttribute(wsdlOptionsEClass, WSDL_OPTIONS__TARGET_NAMESPACE_URI);
        createEAttribute(wsdlOptionsEClass, WSDL_OPTIONS__DEFAULT_NAMESPACE_URI);
        createEReference(wsdlOptionsEClass, WSDL_OPTIONS__VIRTUAL_DATABASE);

        nonModelReferenceEClass = createEClass(NON_MODEL_REFERENCE);
        createEAttribute(nonModelReferenceEClass, NON_MODEL_REFERENCE__NAME);
        createEAttribute(nonModelReferenceEClass, NON_MODEL_REFERENCE__PATH);
        createEAttribute(nonModelReferenceEClass, NON_MODEL_REFERENCE__CHECKSUM);
        createEReference(nonModelReferenceEClass, NON_MODEL_REFERENCE__PROPERTIES);
        createEReference(nonModelReferenceEClass, NON_MODEL_REFERENCE__VIRTUAL_DATABASE);

        // Create enums
        severityEEnum = createEEnum(SEVERITY);
        modelAccessibilityEEnum = createEEnum(MODEL_ACCESSIBILITY);

        // Create data types
        javaDateEDataType = createEDataType(JAVA_DATE);
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
        CorePackageImpl theCorePackage = (CorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(CorePackage.eNS_URI);
        EcorePackageImpl theEcorePackage = (EcorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

        // Add supertypes to classes
        virtualDatabaseEClass.getESuperTypes().add(this.getProblemMarkerContainer());
        modelReferenceEClass.getESuperTypes().add(theCorePackage.getModelImport());
        modelReferenceEClass.getESuperTypes().add(this.getProblemMarkerContainer());

        // Initialize classes and features; add operations and parameters
        initEClass(virtualDatabaseEClass, VirtualDatabase.class, "VirtualDatabase", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_Name(), ecorePackage.getEString(), "name", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_Identifier(), ecorePackage.getEString(), "identifier", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_Uuid(), ecorePackage.getEString(), "uuid", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_Description(), ecorePackage.getEString(), "description", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_Version(), ecorePackage.getEString(), "version", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_Provider(), ecorePackage.getEString(), "provider", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_TimeLastChanged(), ecorePackage.getEString(), "timeLastChanged", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_TimeLastProduced(), ecorePackage.getEString(), "timeLastProduced", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_TimeLastChangedAsDate(), this.getJavaDate(), "timeLastChangedAsDate", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_TimeLastProducedAsDate(), this.getJavaDate(), "timeLastProducedAsDate", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_ProducerName(), ecorePackage.getEString(), "producerName", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_ProducerVersion(), ecorePackage.getEString(), "producerVersion", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getVirtualDatabase_IncludeModelFiles(), ecorePackage.getEBoolean(), "includeModelFiles", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getVirtualDatabase_Models(), this.getModelReference(), this.getModelReference_VirtualDatabase(), "models", null, 0, -1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getVirtualDatabase_WsdlOptions(), this.getWsdlOptions(), this.getWsdlOptions_VirtualDatabase(), "wsdlOptions", null, 0, 1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getVirtualDatabase_NonModels(), this.getNonModelReference(), this.getNonModelReference_VirtualDatabase(), "nonModels", null, 0, -1, VirtualDatabase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(modelReferenceEClass, ModelReference.class, "ModelReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getModelReference_Version(), ecorePackage.getEString(), "version", null, 0, 1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelReference_Uri(), ecorePackage.getEString(), "uri", null, 0, 1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelReference_Visible(), ecorePackage.getEBoolean(), "visible", "true", 0, 1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelReference_Accessibility(), this.getModelAccessibility(), "accessibility", "PUBLIC", 0, 1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getModelReference_TimeLastSynchronized(), ecorePackage.getEString(), "timeLastSynchronized", null, 0, 1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelReference_TimeLastSynchronizedAsDate(), this.getJavaDate(), "timeLastSynchronizedAsDate", null, 0, 1, ModelReference.class, !IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelReference_Checksum(), ecorePackage.getELong(), "checksum", null, 0, 1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelReference_VirtualDatabase(), this.getVirtualDatabase(), this.getVirtualDatabase_Models(), "virtualDatabase", null, 1, 1, ModelReference.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelReference_Uses(), this.getModelReference(), this.getModelReference_UsedBy(), "uses", null, 0, -1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelReference_UsedBy(), this.getModelReference(), this.getModelReference_Uses(), "usedBy", null, 0, -1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelReference_ModelSource(), this.getModelSource(), this.getModelSource_Model(), "modelSource", null, 0, 1, ModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(problemMarkerContainerEClass, ProblemMarkerContainer.class, "ProblemMarkerContainer", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getProblemMarkerContainer_Severity(), this.getSeverity(), "severity", null, 0, 1, ProblemMarkerContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProblemMarkerContainer_Markers(), this.getProblemMarker(), this.getProblemMarker_Marked(), "markers", null, 0, -1, ProblemMarkerContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(problemMarkerEClass, ProblemMarker.class, "ProblemMarker", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getProblemMarker_Severity(), this.getSeverity(), "severity", null, 0, 1, ProblemMarker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProblemMarker_Message(), ecorePackage.getEString(), "message", null, 0, 1, ProblemMarker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProblemMarker_Target(), ecorePackage.getEString(), "target", null, 0, 1, ProblemMarker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProblemMarker_TargetUri(), ecorePackage.getEString(), "targetUri", null, 0, 1, ProblemMarker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProblemMarker_Code(), ecorePackage.getEInt(), "code", null, 0, 1, ProblemMarker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProblemMarker_StackTrace(), ecorePackage.getEString(), "stackTrace", null, 0, 1, ProblemMarker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProblemMarker_Marked(), this.getProblemMarkerContainer(), this.getProblemMarkerContainer_Markers(), "marked", null, 1, 1, ProblemMarker.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProblemMarker_Children(), this.getProblemMarker(), this.getProblemMarker_Parent(), "children", null, 0, -1, ProblemMarker.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProblemMarker_Parent(), this.getProblemMarker(), this.getProblemMarker_Children(), "parent", null, 0, 1, ProblemMarker.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(modelSourceEClass, ModelSource.class, "ModelSource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getModelSource_Properties(), this.getModelSourceProperty(), this.getModelSourceProperty_Source(), "properties", null, 0, -1, ModelSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelSource_Model(), this.getModelReference(), this.getModelReference_ModelSource(), "model", null, 1, 1, ModelSource.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(modelSourcePropertyEClass, ModelSourceProperty.class, "ModelSourceProperty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getModelSourceProperty_Name(), ecorePackage.getEString(), "name", null, 0, 1, ModelSourceProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getModelSourceProperty_Value(), ecorePackage.getEString(), "value", null, 0, 1, ModelSourceProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getModelSourceProperty_Source(), this.getModelSource(), this.getModelSource_Properties(), "source", null, 1, 1, ModelSourceProperty.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(wsdlOptionsEClass, WsdlOptions.class, "WsdlOptions", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getWsdlOptions_TargetNamespaceUri(), ecorePackage.getEString(), "targetNamespaceUri", null, 0, 1, WsdlOptions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getWsdlOptions_DefaultNamespaceUri(), ecorePackage.getEString(), "defaultNamespaceUri", null, 0, 1, WsdlOptions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getWsdlOptions_VirtualDatabase(), this.getVirtualDatabase(), this.getVirtualDatabase_WsdlOptions(), "virtualDatabase", null, 1, 1, WsdlOptions.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(nonModelReferenceEClass, NonModelReference.class, "NonModelReference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getNonModelReference_Name(), ecorePackage.getEString(), "name", null, 0, 1, NonModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getNonModelReference_Path(), ecorePackage.getEString(), "path", null, 0, 1, NonModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getNonModelReference_Checksum(), ecorePackage.getELong(), "checksum", null, 0, 1, NonModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getNonModelReference_Properties(), theEcorePackage.getEStringToStringMapEntry(), null, "properties", null, 0, -1, NonModelReference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getNonModelReference_VirtualDatabase(), this.getVirtualDatabase(), this.getVirtualDatabase_NonModels(), "virtualDatabase", null, 1, 1, NonModelReference.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(severityEEnum, Severity.class, "Severity"); //$NON-NLS-1$
        addEEnumLiteral(severityEEnum, Severity.OK_LITERAL);
        addEEnumLiteral(severityEEnum, Severity.INFO_LITERAL);
        addEEnumLiteral(severityEEnum, Severity.WARNING_LITERAL);
        addEEnumLiteral(severityEEnum, Severity.ERROR_LITERAL);

        initEEnum(modelAccessibilityEEnum, ModelAccessibility.class, "ModelAccessibility"); //$NON-NLS-1$
        addEEnumLiteral(modelAccessibilityEEnum, ModelAccessibility.PUBLIC_LITERAL);
        addEEnumLiteral(modelAccessibilityEEnum, ModelAccessibility.PROTECTED_LITERAL);
        addEEnumLiteral(modelAccessibilityEEnum, ModelAccessibility.PRIVATE_LITERAL);

        // Initialize data types
        initEDataType(javaDateEDataType, Date.class, "JavaDate", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //ManifestPackageImpl
