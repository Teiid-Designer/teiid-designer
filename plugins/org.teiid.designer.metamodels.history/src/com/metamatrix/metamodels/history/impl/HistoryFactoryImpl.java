/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import com.metamatrix.metamodels.history.Branch;
import com.metamatrix.metamodels.history.HistoryCriteria;
import com.metamatrix.metamodels.history.HistoryFactory;
import com.metamatrix.metamodels.history.HistoryPackage;
import com.metamatrix.metamodels.history.Label;
import com.metamatrix.metamodels.history.LabelLog;
import com.metamatrix.metamodels.history.Revision;
import com.metamatrix.metamodels.history.RevisionLog;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class HistoryFactoryImpl extends EFactoryImpl implements HistoryFactory {

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public HistoryFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EObject create( EClass eClass ) {
        switch (eClass.getClassifierID()) {
            case HistoryPackage.REVISION:
                return createRevision();
            case HistoryPackage.LABEL:
                return createLabel();
            case HistoryPackage.BRANCH:
                return createBranch();
            case HistoryPackage.HISTORY_CRITERIA:
                return createHistoryCriteria();
            case HistoryPackage.REVISION_LOG:
                return createRevisionLog();
            case HistoryPackage.LABEL_LOG:
                return createLabelLog();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Revision createRevision() {
        RevisionImpl revision = new RevisionImpl();
        return revision;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Label createLabel() {
        LabelImpl label = new LabelImpl();
        return label;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Branch createBranch() {
        BranchImpl branch = new BranchImpl();
        return branch;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public HistoryCriteria createHistoryCriteria() {
        HistoryCriteriaImpl historyCriteria = new HistoryCriteriaImpl();
        return historyCriteria;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RevisionLog createRevisionLog() {
        RevisionLogImpl revisionLog = new RevisionLogImpl();
        return revisionLog;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public LabelLog createLabelLog() {
        LabelLogImpl labelLog = new LabelLogImpl();
        return labelLog;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public HistoryPackage getHistoryPackage() {
        return (HistoryPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static HistoryPackage getPackage() { // NO_UCD
        return HistoryPackage.eINSTANCE;
    }

} // HistoryFactoryImpl
