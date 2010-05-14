/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.core.extension.ExtensionPackage
 * @generated
 */
public interface ExtensionFactory extends EFactory {

    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    ExtensionFactory eINSTANCE = new com.metamatrix.metamodels.core.extension.impl.ExtensionFactoryImpl();

    /**
     * Returns a new object of class '<em>XClass</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>XClass</em>'.
     * @generated
     */
    XClass createXClass();

    /**
     * Returns a new object of class '<em>XPackage</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>XPackage</em>'.
     * @generated
     */
    XPackage createXPackage();

    /**
     * Returns a new object of class '<em>XAttribute</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>XAttribute</em>'.
     * @generated
     */
    XAttribute createXAttribute();

    /**
     * Returns a new object of class '<em>XEnum</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>XEnum</em>'.
     * @generated
     */
    XEnum createXEnum();

    /**
     * Returns a new object of class '<em>XEnum Literal</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>XEnum Literal</em>'.
     * @generated
     */
    XEnumLiteral createXEnumLiteral();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    ExtensionPackage getExtensionPackage(); // NO_UCD

} // ExtensionFactory
