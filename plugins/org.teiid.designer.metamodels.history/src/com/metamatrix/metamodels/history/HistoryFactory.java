/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.history.HistoryPackage
 * @generated
 */
public interface HistoryFactory extends EFactory {

    /**
     * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    HistoryFactory eINSTANCE = new com.metamatrix.metamodels.history.impl.HistoryFactoryImpl();

    /**
     * Returns a new object of class '<em>Revision</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Revision</em>'.
     * @generated
     */
    Revision createRevision();

    /**
     * Returns a new object of class '<em>Label</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Label</em>'.
     * @generated
     */
    Label createLabel();

    /**
     * Returns a new object of class '<em>Branch</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Branch</em>'.
     * @generated
     */
    Branch createBranch();

    /**
     * Returns a new object of class '<em>Criteria</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Criteria</em>'.
     * @generated
     */
    HistoryCriteria createHistoryCriteria();

    /**
     * Returns a new object of class '<em>Revision Log</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Revision Log</em>'.
     * @generated
     */
    RevisionLog createRevisionLog();

    /**
     * Returns a new object of class '<em>Label Log</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return a new object of class '<em>Label Log</em>'.
     * @generated
     */
    LabelLog createLabelLog();

    /**
     * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the package supported by this factory.
     * @generated
     */
    HistoryPackage getHistoryPackage(); // NO_UCD

} // HistoryFactory
