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

package com.metamatrix.modeler.compare;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.MappingPackage;

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
 * @see com.metamatrix.modeler.compare.CompareFactory
 * @generated
 */
public interface ComparePackage extends EPackage{
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "compare"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/Compare"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "mmcompare"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ComparePackage eINSTANCE = com.metamatrix.modeler.compare.impl.ComparePackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.modeler.compare.impl.DifferenceDescriptorImpl <em>Difference Descriptor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.modeler.compare.impl.DifferenceDescriptorImpl
     * @see com.metamatrix.modeler.compare.impl.ComparePackageImpl#getDifferenceDescriptor()
     * @generated
     */
    int DIFFERENCE_DESCRIPTOR = 0;

    /**
     * The feature id for the '<em><b>Mapper</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_DESCRIPTOR__MAPPER = MappingPackage.MAPPING_HELPER__MAPPER;

    /**
     * The feature id for the '<em><b>Helped Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_DESCRIPTOR__HELPED_OBJECT = MappingPackage.MAPPING_HELPER__HELPED_OBJECT;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_DESCRIPTOR__NESTED_IN = MappingPackage.MAPPING_HELPER__NESTED_IN;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_DESCRIPTOR__NESTED = MappingPackage.MAPPING_HELPER__NESTED;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_DESCRIPTOR__TYPE = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Skip</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_DESCRIPTOR__SKIP = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Property Differences</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Difference Descriptor</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_DESCRIPTOR_FEATURE_COUNT = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.modeler.compare.impl.DifferenceReportImpl <em>Difference Report</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.modeler.compare.impl.DifferenceReportImpl
     * @see com.metamatrix.modeler.compare.impl.ComparePackageImpl#getDifferenceReport()
     * @generated
     */
    int DIFFERENCE_REPORT = 1;

    /**
     * The feature id for the '<em><b>Title</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT__TITLE = 0;

    /**
     * The feature id for the '<em><b>Total Additions</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT__TOTAL_ADDITIONS = 1;

    /**
     * The feature id for the '<em><b>Total Deletions</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT__TOTAL_DELETIONS = 2;

    /**
     * The feature id for the '<em><b>Total Changes</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT__TOTAL_CHANGES = 3;

    /**
     * The feature id for the '<em><b>Analysis Time</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT__ANALYSIS_TIME = 4;

    /**
     * The feature id for the '<em><b>Source Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT__SOURCE_URI = 5;

    /**
     * The feature id for the '<em><b>Result Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT__RESULT_URI = 6;

    /**
     * The feature id for the '<em><b>Mapping</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT__MAPPING = 7;

    /**
     * The number of structural features of the the '<em>Difference Report</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DIFFERENCE_REPORT_FEATURE_COUNT = 8;

    /**
     * The meta object id for the '{@link com.metamatrix.modeler.compare.impl.PropertyDifferenceImpl <em>Property Difference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.modeler.compare.impl.PropertyDifferenceImpl
     * @see com.metamatrix.modeler.compare.impl.ComparePackageImpl#getPropertyDifference()
     * @generated
     */
    int PROPERTY_DIFFERENCE = 2;

    /**
     * The feature id for the '<em><b>New Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROPERTY_DIFFERENCE__NEW_VALUE = 0;

    /**
     * The feature id for the '<em><b>Old Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROPERTY_DIFFERENCE__OLD_VALUE = 1;

    /**
     * The feature id for the '<em><b>Skip</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROPERTY_DIFFERENCE__SKIP = 2;

    /**
     * The feature id for the '<em><b>Affected Feature</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROPERTY_DIFFERENCE__AFFECTED_FEATURE = 3;

    /**
     * The feature id for the '<em><b>Descriptor</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROPERTY_DIFFERENCE__DESCRIPTOR = 4;

    /**
     * The number of structural features of the the '<em>Property Difference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROPERTY_DIFFERENCE_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link com.metamatrix.modeler.compare.DifferenceType <em>Difference Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.modeler.compare.DifferenceType
     * @see com.metamatrix.modeler.compare.impl.ComparePackageImpl#getDifferenceType()
     * @generated
     */
    int DIFFERENCE_TYPE = 3;

    /**
     * The meta object id for the '<em>Any Type</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.lang.Object
     * @see com.metamatrix.modeler.compare.impl.ComparePackageImpl#getAnyType()
     * @generated
     */
    int ANY_TYPE = 4;


    /**
     * Returns the meta object for class '{@link com.metamatrix.modeler.compare.DifferenceDescriptor <em>Difference Descriptor</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Difference Descriptor</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceDescriptor
     * @generated
     */
    EClass getDifferenceDescriptor();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceDescriptor#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceDescriptor#getType()
     * @see #getDifferenceDescriptor()
     * @generated
     */
    EAttribute getDifferenceDescriptor_Type();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceDescriptor#isSkip <em>Skip</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Skip</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceDescriptor#isSkip()
     * @see #getDifferenceDescriptor()
     * @generated
     */
    EAttribute getDifferenceDescriptor_Skip();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.modeler.compare.DifferenceDescriptor#getPropertyDifferences <em>Property Differences</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Property Differences</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceDescriptor#getPropertyDifferences()
     * @see #getDifferenceDescriptor()
     * @generated
     */
    EReference getDifferenceDescriptor_PropertyDifferences();

    /**
     * Returns the meta object for class '{@link com.metamatrix.modeler.compare.DifferenceReport <em>Difference Report</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Difference Report</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport
     * @generated
     */
    EClass getDifferenceReport();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceReport#getTitle <em>Title</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Title</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport#getTitle()
     * @see #getDifferenceReport()
     * @generated
     */
    EAttribute getDifferenceReport_Title();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalAdditions <em>Total Additions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Total Additions</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport#getTotalAdditions()
     * @see #getDifferenceReport()
     * @generated
     */
    EAttribute getDifferenceReport_TotalAdditions();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalDeletions <em>Total Deletions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Total Deletions</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport#getTotalDeletions()
     * @see #getDifferenceReport()
     * @generated
     */
    EAttribute getDifferenceReport_TotalDeletions();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceReport#getTotalChanges <em>Total Changes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Total Changes</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport#getTotalChanges()
     * @see #getDifferenceReport()
     * @generated
     */
    EAttribute getDifferenceReport_TotalChanges();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceReport#getAnalysisTime <em>Analysis Time</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Analysis Time</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport#getAnalysisTime()
     * @see #getDifferenceReport()
     * @generated
     */
    EAttribute getDifferenceReport_AnalysisTime();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceReport#getSourceUri <em>Source Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Source Uri</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport#getSourceUri()
     * @see #getDifferenceReport()
     * @generated
     */
    EAttribute getDifferenceReport_SourceUri();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.DifferenceReport#getResultUri <em>Result Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Result Uri</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport#getResultUri()
     * @see #getDifferenceReport()
     * @generated
     */
    EAttribute getDifferenceReport_ResultUri();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.modeler.compare.DifferenceReport#getMapping <em>Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Mapping</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceReport#getMapping()
     * @see #getDifferenceReport()
     * @generated
     */
    EReference getDifferenceReport_Mapping();

    /**
     * Returns the meta object for class '{@link com.metamatrix.modeler.compare.PropertyDifference <em>Property Difference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Property Difference</em>'.
     * @see com.metamatrix.modeler.compare.PropertyDifference
     * @generated
     */
    EClass getPropertyDifference();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.PropertyDifference#getNewValue <em>New Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>New Value</em>'.
     * @see com.metamatrix.modeler.compare.PropertyDifference#getNewValue()
     * @see #getPropertyDifference()
     * @generated
     */
    EAttribute getPropertyDifference_NewValue();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.PropertyDifference#getOldValue <em>Old Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Old Value</em>'.
     * @see com.metamatrix.modeler.compare.PropertyDifference#getOldValue()
     * @see #getPropertyDifference()
     * @generated
     */
    EAttribute getPropertyDifference_OldValue();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.modeler.compare.PropertyDifference#isSkip <em>Skip</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Skip</em>'.
     * @see com.metamatrix.modeler.compare.PropertyDifference#isSkip()
     * @see #getPropertyDifference()
     * @generated
     */
    EAttribute getPropertyDifference_Skip();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.modeler.compare.PropertyDifference#getAffectedFeature <em>Affected Feature</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Affected Feature</em>'.
     * @see com.metamatrix.modeler.compare.PropertyDifference#getAffectedFeature()
     * @see #getPropertyDifference()
     * @generated
     */
    EReference getPropertyDifference_AffectedFeature();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.modeler.compare.PropertyDifference#getDescriptor <em>Descriptor</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Descriptor</em>'.
     * @see com.metamatrix.modeler.compare.PropertyDifference#getDescriptor()
     * @see #getPropertyDifference()
     * @generated
     */
    EReference getPropertyDifference_Descriptor();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.modeler.compare.DifferenceType <em>Difference Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Difference Type</em>'.
     * @see com.metamatrix.modeler.compare.DifferenceType
     * @generated
     */
    EEnum getDifferenceType();

    /**
     * Returns the meta object for data type '{@link java.lang.Object <em>Any Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Any Type</em>'.
     * @see java.lang.Object
     * @model instanceClass="java.lang.Object"
     * @generated
     */
    EDataType getAnyType();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    CompareFactory getCompareFactory();

} //ComparePackage
