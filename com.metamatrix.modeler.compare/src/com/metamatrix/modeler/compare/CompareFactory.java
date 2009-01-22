/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.modeler.compare.ComparePackage
 * @generated
 */
public interface CompareFactory extends EFactory{
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    CompareFactory eINSTANCE = new com.metamatrix.modeler.compare.impl.CompareFactoryImpl();

    /**
     * Returns a new object of class '<em>Difference Descriptor</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Difference Descriptor</em>'.
     * @generated
     */
    DifferenceDescriptor createDifferenceDescriptor();

    /**
     * Returns a new object of class '<em>Difference Report</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Difference Report</em>'.
     * @generated
     */
    DifferenceReport createDifferenceReport();

    /**
     * Returns a new object of class '<em>Property Difference</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Property Difference</em>'.
     * @generated
     */
    PropertyDifference createPropertyDifference();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ComparePackage getComparePackage();

} //CompareFactory
