/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.core.CorePackage
 * @generated
 */
public interface CoreFactory extends EFactory {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    CoreFactory eINSTANCE = new com.metamatrix.metamodels.core.impl.CoreFactoryImpl();

    /**
     * Returns a new object of class '<em>Annotation</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Annotation</em>'.
     * @generated
     */
    Annotation createAnnotation();

    /**
     * Returns a new object of class '<em>Annotation Container</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Annotation Container</em>'.
     * @generated
     */
    AnnotationContainer createAnnotationContainer();

    /**
     * Returns a new object of class '<em>Model Annotation</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Model Annotation</em>'.
     * @generated
     */
    ModelAnnotation createModelAnnotation();

    /**
     * Returns a new object of class '<em>Link</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Link</em>'.
     * @generated
     */
    Link createLink();

    /**
     * Returns a new object of class '<em>Link Container</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Link Container</em>'.
     * @generated
     */
    LinkContainer createLinkContainer();

    /**
     * Returns a new object of class '<em>Model Import</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Model Import</em>'.
     * @generated
     */
    ModelImport createModelImport();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    CorePackage getCorePackage();

} // CoreFactory
