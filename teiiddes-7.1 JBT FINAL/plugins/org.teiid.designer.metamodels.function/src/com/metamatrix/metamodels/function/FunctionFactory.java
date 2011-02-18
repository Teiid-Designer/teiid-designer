/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function;

import org.eclipse.emf.ecore.EFactory;
import com.metamatrix.metamodels.function.impl.FunctionFactoryImpl;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.function.FunctionPackage
 * @generated
 */
public interface FunctionFactory extends EFactory {
    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    FunctionFactory eINSTANCE = new FunctionFactoryImpl();

    /**
     * Returns a new object of class '<em>Scalar Function</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Scalar Function</em>'.
     * @generated
     */
    ScalarFunction createScalarFunction();

    /**
     * Returns a new object of class '<em>Parameter</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Parameter</em>'.
     * @generated
     */
    FunctionParameter createFunctionParameter();

    /**
     * Returns a new object of class '<em>Return Parameter</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Return Parameter</em>'.
     * @generated
     */
    ReturnParameter createReturnParameter();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    FunctionPackage getFunctionPackage(); // NO_UCD

} // FunctionFactory
