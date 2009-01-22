/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage
 * @generated
 */
public interface ManifestFactory extends EFactory{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ManifestFactory eINSTANCE = new com.metamatrix.vdb.edit.manifest.impl.ManifestFactoryImpl();

    /**
     * Returns a new object of class '<em>Virtual Database</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Virtual Database</em>'.
     * @generated
     */
    VirtualDatabase createVirtualDatabase();

    /**
     * Returns a new object of class '<em>Model Reference</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Model Reference</em>'.
     * @generated
     */
    ModelReference createModelReference();

    /**
     * Returns a new object of class '<em>Problem Marker</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Problem Marker</em>'.
     * @generated
     */
    ProblemMarker createProblemMarker();

    /**
     * Returns a new object of class '<em>Model Source</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Model Source</em>'.
     * @generated
     */
    ModelSource createModelSource();

    /**
     * Returns a new object of class '<em>Model Source Property</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Model Source Property</em>'.
     * @generated
     */
    ModelSourceProperty createModelSourceProperty();

    /**
     * Returns a new object of class '<em>Wsdl Options</em>'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return a new object of class '<em>Wsdl Options</em>'.
     * @generated
     */
	WsdlOptions createWsdlOptions();

    /**
     * Returns a new object of class '<em>Non Model Reference</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Non Model Reference</em>'.
     * @generated
     */
    NonModelReference createNonModelReference();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ManifestPackage getManifestPackage();

} //ManifestFactory
