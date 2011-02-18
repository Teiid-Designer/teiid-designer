/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.diagram;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.diagram.DiagramPackage
 * @generated
 */
public interface DiagramFactory extends EFactory {

    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    DiagramFactory eINSTANCE = new com.metamatrix.metamodels.diagram.impl.DiagramFactoryImpl();

    /**
     * Returns a new object of class '<em>Entity</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Entity</em>'.
     * @generated
     */
    DiagramEntity createDiagramEntity();

    /**
     * Returns a new object of class '<em>Diagram</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Diagram</em>'.
     * @generated
     */
    Diagram createDiagram();

    /**
     * Returns a new object of class '<em>Container</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Container</em>'.
     * @generated
     */
    DiagramContainer createDiagramContainer();

    /**
     * Returns a new object of class '<em>Link</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Link</em>'.
     * @generated
     */
    DiagramLink createDiagramLink();

    /**
     * Returns a new object of class '<em>Position</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Position</em>'.
     * @generated
     */
    DiagramPosition createDiagramPosition();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    DiagramPackage getDiagramPackage(); // NO_UCD

} // DiagramFactory
