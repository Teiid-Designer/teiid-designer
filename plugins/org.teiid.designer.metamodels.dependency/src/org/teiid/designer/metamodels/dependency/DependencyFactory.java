/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.dependency;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see org.teiid.designer.metamodels.dependency.DependencyPackage
 * @generated
 */
public interface DependencyFactory extends EFactory {
    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    DependencyFactory eINSTANCE = new org.teiid.designer.metamodels.dependency.impl.DependencyFactoryImpl();

    /**
     * Returns a new object of class '<em>Info</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Info</em>'.
     * @generated
     */
    DependencyInfo createDependencyInfo();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    DependencyPackage getDependencyPackage(); // NO_UCD

} // DependencyFactory
