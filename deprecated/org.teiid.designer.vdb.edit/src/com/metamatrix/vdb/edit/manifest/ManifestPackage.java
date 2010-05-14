/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import com.metamatrix.metamodels.core.CorePackage;

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
 * @see com.metamatrix.vdb.edit.manifest.ManifestFactory
 * @model kind="package"
 * @generated
 */
public interface ManifestPackage extends EPackage{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "manifest"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/VirtualDatabase"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "vdb"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ManifestPackage eINSTANCE = com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerContainerImpl <em>Problem Marker Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerContainerImpl
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getProblemMarkerContainer()
     * @generated
     */
    int PROBLEM_MARKER_CONTAINER = 2;

    /**
     * The feature id for the '<em><b>Severity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER_CONTAINER__SEVERITY = 0;

    /**
     * The feature id for the '<em><b>Markers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER_CONTAINER__MARKERS = 1;

    /**
     * The number of structural features of the the '<em>Problem Marker Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER_CONTAINER_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl <em>Virtual Database</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.impl.VirtualDatabaseImpl
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getVirtualDatabase()
     * @generated
     */
    int VIRTUAL_DATABASE = 0;

    /**
     * The feature id for the '<em><b>Severity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__SEVERITY = PROBLEM_MARKER_CONTAINER__SEVERITY;

    /**
     * The feature id for the '<em><b>Markers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__MARKERS = PROBLEM_MARKER_CONTAINER__MARKERS;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__NAME = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Identifier</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__IDENTIFIER = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Uuid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__UUID = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__DESCRIPTION = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__VERSION = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Provider</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__PROVIDER = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Time Last Changed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__TIME_LAST_CHANGED = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Time Last Produced</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__TIME_LAST_PRODUCED = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Time Last Changed As Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__TIME_LAST_CHANGED_AS_DATE = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Time Last Produced As Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__TIME_LAST_PRODUCED_AS_DATE = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Producer Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__PRODUCER_NAME = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Producer Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__PRODUCER_VERSION = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Include Model Files</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__INCLUDE_MODEL_FILES = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 12;

    /**
     * The feature id for the '<em><b>Models</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__MODELS = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 13;

    /**
     * The feature id for the '<em><b>Wsdl Options</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int VIRTUAL_DATABASE__WSDL_OPTIONS = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 14;

    /**
     * The feature id for the '<em><b>Non Models</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE__NON_MODELS = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 15;

    /**
     * The number of structural features of the the '<em>Virtual Database</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIRTUAL_DATABASE_FEATURE_COUNT = PROBLEM_MARKER_CONTAINER_FEATURE_COUNT + 16;

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl <em>Model Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.impl.ModelReferenceImpl
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getModelReference()
     * @generated
     */
    int MODEL_REFERENCE = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__NAME = CorePackage.MODEL_IMPORT__NAME;

    /**
     * The feature id for the '<em><b>Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__PATH = CorePackage.MODEL_IMPORT__PATH;

    /**
     * The feature id for the '<em><b>Model Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__MODEL_LOCATION = CorePackage.MODEL_IMPORT__MODEL_LOCATION;

    /**
     * The feature id for the '<em><b>Uuid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__UUID = CorePackage.MODEL_IMPORT__UUID;

    /**
     * The feature id for the '<em><b>Model Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__MODEL_TYPE = CorePackage.MODEL_IMPORT__MODEL_TYPE;

    /**
     * The feature id for the '<em><b>Primary Metamodel Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__PRIMARY_METAMODEL_URI = CorePackage.MODEL_IMPORT__PRIMARY_METAMODEL_URI;

    /**
     * The feature id for the '<em><b>Model</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__MODEL = CorePackage.MODEL_IMPORT__MODEL;

    /**
     * The feature id for the '<em><b>Severity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__SEVERITY = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Markers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__MARKERS = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__VERSION = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__URI = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Visible</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MODEL_REFERENCE__VISIBLE = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Accessibility</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MODEL_REFERENCE__ACCESSIBILITY = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Time Last Synchronized</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Time Last Synchronized As Date</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MODEL_REFERENCE__TIME_LAST_SYNCHRONIZED_AS_DATE = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Checksum</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MODEL_REFERENCE__CHECKSUM = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Virtual Database</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__VIRTUAL_DATABASE = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Uses</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__USES = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Used By</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__USED_BY = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Model Source</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE__MODEL_SOURCE = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 12;

    /**
     * The number of structural features of the the '<em>Model Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REFERENCE_FEATURE_COUNT = CorePackage.MODEL_IMPORT_FEATURE_COUNT + 13;

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl <em>Problem Marker</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getProblemMarker()
     * @generated
     */
    int PROBLEM_MARKER = 3;

    /**
     * The feature id for the '<em><b>Severity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__SEVERITY = 0;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__MESSAGE = 1;

    /**
     * The feature id for the '<em><b>Target</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__TARGET = 2;

    /**
     * The feature id for the '<em><b>Target Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__TARGET_URI = 3;

    /**
     * The feature id for the '<em><b>Code</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__CODE = 4;

    /**
     * The feature id for the '<em><b>Stack Trace</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__STACK_TRACE = 5;

    /**
     * The feature id for the '<em><b>Marked</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__MARKED = 6;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__CHILDREN = 7;

    /**
     * The feature id for the '<em><b>Parent</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER__PARENT = 8;

    /**
     * The number of structural features of the the '<em>Problem Marker</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROBLEM_MARKER_FEATURE_COUNT = 9;

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.impl.ModelSourceImpl <em>Model Source</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.impl.ModelSourceImpl
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getModelSource()
     * @generated
     */
    int MODEL_SOURCE = 4;

    /**
     * The feature id for the '<em><b>Properties</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_SOURCE__PROPERTIES = 0;

    /**
     * The feature id for the '<em><b>Model</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_SOURCE__MODEL = 1;

    /**
     * The number of structural features of the the '<em>Model Source</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_SOURCE_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.impl.ModelSourcePropertyImpl <em>Model Source Property</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.impl.ModelSourcePropertyImpl
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getModelSourceProperty()
     * @generated
     */
    int MODEL_SOURCE_PROPERTY = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_SOURCE_PROPERTY__NAME = 0;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_SOURCE_PROPERTY__VALUE = 1;

    /**
     * The feature id for the '<em><b>Source</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_SOURCE_PROPERTY__SOURCE = 2;

    /**
     * The number of structural features of the the '<em>Model Source Property</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_SOURCE_PROPERTY_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.impl.WsdlOptionsImpl <em>Wsdl Options</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.impl.WsdlOptionsImpl
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getWsdlOptions()
     * @generated
     */
	int WSDL_OPTIONS = 6;

    /**
     * The feature id for the '<em><b>Target Namespace Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WSDL_OPTIONS__TARGET_NAMESPACE_URI = 0;

    /**
     * The feature id for the '<em><b>Default Namespace Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WSDL_OPTIONS__DEFAULT_NAMESPACE_URI = 1;

    /**
     * The feature id for the '<em><b>Virtual Database</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WSDL_OPTIONS__VIRTUAL_DATABASE = 2;

    /**
     * The number of structural features of the the '<em>Wsdl Options</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WSDL_OPTIONS_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.impl.NonModelReferenceImpl <em>Non Model Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.impl.NonModelReferenceImpl
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getNonModelReference()
     * @generated
     */
    int NON_MODEL_REFERENCE = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NON_MODEL_REFERENCE__NAME = 0;

    /**
     * The feature id for the '<em><b>Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NON_MODEL_REFERENCE__PATH = 1;

    /**
     * The feature id for the '<em><b>Checksum</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NON_MODEL_REFERENCE__CHECKSUM = 2;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NON_MODEL_REFERENCE__PROPERTIES = 3;

    /**
     * The feature id for the '<em><b>Virtual Database</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NON_MODEL_REFERENCE__VIRTUAL_DATABASE = 4;

    /**
     * The number of structural features of the the '<em>Non Model Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int NON_MODEL_REFERENCE_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.Severity <em>Severity</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.Severity
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getSeverity()
     * @generated
     */
    int SEVERITY = 8;


    /**
     * The meta object id for the '{@link com.metamatrix.vdb.edit.manifest.ModelAccessibility <em>Model Accessibility</em>}' enum.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.vdb.edit.manifest.ModelAccessibility
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getModelAccessibility()
     * @generated
     */
	int MODEL_ACCESSIBILITY = 9;

    /**
     * The meta object id for the '<em>Java Date</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.util.Date
     * @see com.metamatrix.vdb.edit.manifest.impl.ManifestPackageImpl#getJavaDate()
     * @generated
     */
    int JAVA_DATE = 10;


    /**
     * Returns the meta object for class '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase <em>Virtual Database</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Virtual Database</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase
     * @generated
     */
    EClass getVirtualDatabase();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getName()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getIdentifier <em>Identifier</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Identifier</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getIdentifier()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_Identifier();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getUuid <em>Uuid</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uuid</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getUuid()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_Uuid();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getDescription()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_Description();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getVersion <em>Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Version</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getVersion()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_Version();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProvider <em>Provider</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Provider</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProvider()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_Provider();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastChanged <em>Time Last Changed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time Last Changed</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastChanged()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_TimeLastChanged();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastProduced <em>Time Last Produced</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time Last Produced</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastProduced()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_TimeLastProduced();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastChangedAsDate <em>Time Last Changed As Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time Last Changed As Date</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastChangedAsDate()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_TimeLastChangedAsDate();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastProducedAsDate <em>Time Last Produced As Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time Last Produced As Date</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastProducedAsDate()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_TimeLastProducedAsDate();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProducerName <em>Producer Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Producer Name</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProducerName()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_ProducerName();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProducerVersion <em>Producer Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Producer Version</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProducerVersion()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_ProducerVersion();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#isIncludeModelFiles <em>Include Model Files</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Include Model Files</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#isIncludeModelFiles()
     * @see #getVirtualDatabase()
     * @generated
     */
    EAttribute getVirtualDatabase_IncludeModelFiles();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getModels <em>Models</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Models</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getModels()
     * @see #getVirtualDatabase()
     * @generated
     */
    EReference getVirtualDatabase_Models();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getWsdlOptions <em>Wsdl Options</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Wsdl Options</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getWsdlOptions()
     * @see #getVirtualDatabase()
     * @generated
     */
	EReference getVirtualDatabase_WsdlOptions();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getNonModels <em>Non Models</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Non Models</em>'.
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getNonModels()
     * @see #getVirtualDatabase()
     * @generated
     */
    EReference getVirtualDatabase_NonModels();

    /**
     * Returns the meta object for class '{@link com.metamatrix.vdb.edit.manifest.ModelReference <em>Model Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Model Reference</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference
     * @generated
     */
    EClass getModelReference();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getVersion <em>Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Version</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getVersion()
     * @see #getModelReference()
     * @generated
     */
    EAttribute getModelReference_Version();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getUri()
     * @see #getModelReference()
     * @generated
     */
    EAttribute getModelReference_Uri();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelReference#isVisible <em>Visible</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Visible</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#isVisible()
     * @see #getModelReference()
     * @generated
     */
	EAttribute getModelReference_Visible();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getAccessibility <em>Accessibility</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Accessibility</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getAccessibility()
     * @see #getModelReference()
     * @generated
     */
	EAttribute getModelReference_Accessibility();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getTimeLastSynchronized <em>Time Last Synchronized</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time Last Synchronized</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getTimeLastSynchronized()
     * @see #getModelReference()
     * @generated
     */
	EAttribute getModelReference_TimeLastSynchronized();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getTimeLastSynchronizedAsDate <em>Time Last Synchronized As Date</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Time Last Synchronized As Date</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getTimeLastSynchronizedAsDate()
     * @see #getModelReference()
     * @generated
     */
	EAttribute getModelReference_TimeLastSynchronizedAsDate();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getChecksum <em>Checksum</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Checksum</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getChecksum()
     * @see #getModelReference()
     * @generated
     */
	EAttribute getModelReference_Checksum();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getVirtualDatabase <em>Virtual Database</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Virtual Database</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getVirtualDatabase()
     * @see #getModelReference()
     * @generated
     */
    EReference getModelReference_VirtualDatabase();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUses <em>Uses</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Uses</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getUses()
     * @see #getModelReference()
     * @generated
     */
    EReference getModelReference_Uses();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getUsedBy <em>Used By</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Used By</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getUsedBy()
     * @see #getModelReference()
     * @generated
     */
    EReference getModelReference_UsedBy();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getModelSource <em>Model Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Model Source</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getModelSource()
     * @see #getModelReference()
     * @generated
     */
    EReference getModelReference_ModelSource();

    /**
     * Returns the meta object for class '{@link com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer <em>Problem Marker Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Problem Marker Container</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer
     * @generated
     */
    EClass getProblemMarkerContainer();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getSeverity <em>Severity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Severity</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getSeverity()
     * @see #getProblemMarkerContainer()
     * @generated
     */
    EAttribute getProblemMarkerContainer_Severity();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getMarkers <em>Markers</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Markers</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getMarkers()
     * @see #getProblemMarkerContainer()
     * @generated
     */
    EReference getProblemMarkerContainer_Markers();

    /**
     * Returns the meta object for class '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker <em>Problem Marker</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Problem Marker</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker
     * @generated
     */
    EClass getProblemMarker();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getSeverity <em>Severity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Severity</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getSeverity()
     * @see #getProblemMarker()
     * @generated
     */
    EAttribute getProblemMarker_Severity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Message</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getMessage()
     * @see #getProblemMarker()
     * @generated
     */
    EAttribute getProblemMarker_Message();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Target</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getTarget()
     * @see #getProblemMarker()
     * @generated
     */
    EAttribute getProblemMarker_Target();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getTargetUri <em>Target Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Target Uri</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getTargetUri()
     * @see #getProblemMarker()
     * @generated
     */
    EAttribute getProblemMarker_TargetUri();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getCode <em>Code</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Code</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getCode()
     * @see #getProblemMarker()
     * @generated
     */
    EAttribute getProblemMarker_Code();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getStackTrace <em>Stack Trace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Stack Trace</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getStackTrace()
     * @see #getProblemMarker()
     * @generated
     */
    EAttribute getProblemMarker_StackTrace();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getMarked <em>Marked</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Marked</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getMarked()
     * @see #getProblemMarker()
     * @generated
     */
    EReference getProblemMarker_Marked();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Children</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getChildren()
     * @see #getProblemMarker()
     * @generated
     */
    EReference getProblemMarker_Children();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Parent</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getParent()
     * @see #getProblemMarker()
     * @generated
     */
    EReference getProblemMarker_Parent();

    /**
     * Returns the meta object for class '{@link com.metamatrix.vdb.edit.manifest.ModelSource <em>Model Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Model Source</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelSource
     * @generated
     */
    EClass getModelSource();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.vdb.edit.manifest.ModelSource#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Properties</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelSource#getProperties()
     * @see #getModelSource()
     * @generated
     */
    EReference getModelSource_Properties();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.vdb.edit.manifest.ModelSource#getModel <em>Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Model</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelSource#getModel()
     * @see #getModelSource()
     * @generated
     */
    EReference getModelSource_Model();

    /**
     * Returns the meta object for class '{@link com.metamatrix.vdb.edit.manifest.ModelSourceProperty <em>Model Source Property</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Model Source Property</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelSourceProperty
     * @generated
     */
    EClass getModelSourceProperty();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelSourceProperty#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelSourceProperty#getName()
     * @see #getModelSourceProperty()
     * @generated
     */
    EAttribute getModelSourceProperty_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.ModelSourceProperty#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelSourceProperty#getValue()
     * @see #getModelSourceProperty()
     * @generated
     */
    EAttribute getModelSourceProperty_Value();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.vdb.edit.manifest.ModelSourceProperty#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Source</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelSourceProperty#getSource()
     * @see #getModelSourceProperty()
     * @generated
     */
    EReference getModelSourceProperty_Source();

    /**
     * Returns the meta object for class '{@link com.metamatrix.vdb.edit.manifest.WsdlOptions <em>Wsdl Options</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Wsdl Options</em>'.
     * @see com.metamatrix.vdb.edit.manifest.WsdlOptions
     * @generated
     */
	EClass getWsdlOptions();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.WsdlOptions#getTargetNamespaceUri <em>Target Namespace Uri</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Target Namespace Uri</em>'.
     * @see com.metamatrix.vdb.edit.manifest.WsdlOptions#getTargetNamespaceUri()
     * @see #getWsdlOptions()
     * @generated
     */
	EAttribute getWsdlOptions_TargetNamespaceUri();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.WsdlOptions#getDefaultNamespaceUri <em>Default Namespace Uri</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Default Namespace Uri</em>'.
     * @see com.metamatrix.vdb.edit.manifest.WsdlOptions#getDefaultNamespaceUri()
     * @see #getWsdlOptions()
     * @generated
     */
	EAttribute getWsdlOptions_DefaultNamespaceUri();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.vdb.edit.manifest.WsdlOptions#getVirtualDatabase <em>Virtual Database</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Virtual Database</em>'.
     * @see com.metamatrix.vdb.edit.manifest.WsdlOptions#getVirtualDatabase()
     * @see #getWsdlOptions()
     * @generated
     */
	EReference getWsdlOptions_VirtualDatabase();

    /**
     * Returns the meta object for class '{@link com.metamatrix.vdb.edit.manifest.NonModelReference <em>Non Model Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Non Model Reference</em>'.
     * @see com.metamatrix.vdb.edit.manifest.NonModelReference
     * @generated
     */
    EClass getNonModelReference();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.vdb.edit.manifest.NonModelReference#getName()
     * @see #getNonModelReference()
     * @generated
     */
    EAttribute getNonModelReference_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getPath <em>Path</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Path</em>'.
     * @see com.metamatrix.vdb.edit.manifest.NonModelReference#getPath()
     * @see #getNonModelReference()
     * @generated
     */
    EAttribute getNonModelReference_Path();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getChecksum <em>Checksum</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Checksum</em>'.
     * @see com.metamatrix.vdb.edit.manifest.NonModelReference#getChecksum()
     * @see #getNonModelReference()
     * @generated
     */
    EAttribute getNonModelReference_Checksum();

    /**
     * Returns the meta object for the map '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Properties</em>'.
     * @see com.metamatrix.vdb.edit.manifest.NonModelReference#getProperties()
     * @see #getNonModelReference()
     * @generated
     */
    EReference getNonModelReference_Properties();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getVirtualDatabase <em>Virtual Database</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Virtual Database</em>'.
     * @see com.metamatrix.vdb.edit.manifest.NonModelReference#getVirtualDatabase()
     * @see #getNonModelReference()
     * @generated
     */
    EReference getNonModelReference_VirtualDatabase();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.vdb.edit.manifest.Severity <em>Severity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Severity</em>'.
     * @see com.metamatrix.vdb.edit.manifest.Severity
     * @generated
     */
    EEnum getSeverity();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.vdb.edit.manifest.ModelAccessibility <em>Model Accessibility</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Model Accessibility</em>'.
     * @see com.metamatrix.vdb.edit.manifest.ModelAccessibility
     * @generated
     */
	EEnum getModelAccessibility();

    /**
     * Returns the meta object for data type '{@link java.util.Date <em>Java Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Java Date</em>'.
     * @see java.util.Date
     * @model instanceClass="java.util.Date"
     * @generated
     */
    EDataType getJavaDate();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ManifestFactory getManifestFactory();

} //ManifestPackage
