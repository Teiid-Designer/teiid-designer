/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history.util;

import com.metamatrix.metamodels.history.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.history.HistoryPackage
 * @generated
 */
public class HistoryAdapterFactory extends AdapterFactoryImpl {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static HistoryPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public HistoryAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = HistoryPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected HistorySwitch modelSwitch =
        new HistorySwitch() {
            @Override
            public Object caseHistoryLog(HistoryLog object) {
                return createHistoryLogAdapter();
            }
            @Override
            public Object caseRevision(Revision object) {
                return createRevisionAdapter();
            }
            @Override
            public Object caseLabel(Label object) {
                return createLabelAdapter();
            }
            @Override
            public Object caseHistoryLogEntry(HistoryLogEntry object) {
                return createHistoryLogEntryAdapter();
            }
            @Override
            public Object caseBranch(Branch object) {
                return createBranchAdapter();
            }
            @Override
            public Object caseHistoryCriteria(HistoryCriteria object) {
                return createHistoryCriteriaAdapter();
            }
            @Override
            public Object caseRevisionLog(RevisionLog object) {
                return createRevisionLogAdapter();
            }
            @Override
            public Object caseLabelLog(LabelLog object) {
                return createLabelLogAdapter();
            }
            @Override
            public Object defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return (Adapter)modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.history.HistoryLog <em>Log</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.history.HistoryLog
     * @generated
     */
    public Adapter createHistoryLogAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.history.Revision <em>Revision</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.history.Revision
     * @generated
     */
    public Adapter createRevisionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.history.Label <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.history.Label
     * @generated
     */
    public Adapter createLabelAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.history.HistoryLogEntry <em>Log Entry</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.history.HistoryLogEntry
     * @generated
     */
    public Adapter createHistoryLogEntryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.history.Branch <em>Branch</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.history.Branch
     * @generated
     */
    public Adapter createBranchAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.history.HistoryCriteria <em>Criteria</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.history.HistoryCriteria
     * @generated
     */
    public Adapter createHistoryCriteriaAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.history.RevisionLog <em>Revision Log</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.history.RevisionLog
     * @generated
     */
    public Adapter createRevisionLogAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.history.LabelLog <em>Label Log</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.history.LabelLog
     * @generated
     */
    public Adapter createLabelLogAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //HistoryAdapterFactory
