/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.dependency;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

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
 * @see com.metamatrix.metamodels.dependency.DependencyFactory
 * @generated
 */
public interface DependencyPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "dependency"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/Dependency"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "dependency"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    DependencyPackage eINSTANCE = com.metamatrix.metamodels.dependency.impl.DependencyPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.dependency.impl.DependencyInfoImpl <em>Info</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.dependency.impl.DependencyInfoImpl
     * @see com.metamatrix.metamodels.dependency.impl.DependencyPackageImpl#getDependencyInfo()
     * @generated
     */
    int DEPENDENCY_INFO = 0;

    /**
     * The number of structural features of the the '<em>Info</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DEPENDENCY_INFO_FEATURE_COUNT = 0;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.dependency.DependencyInfo <em>Info</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Info</em>'.
     * @see com.metamatrix.metamodels.dependency.DependencyInfo
     * @generated
     */
    EClass getDependencyInfo();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    DependencyFactory getDependencyFactory();

} //DependencyPackage
