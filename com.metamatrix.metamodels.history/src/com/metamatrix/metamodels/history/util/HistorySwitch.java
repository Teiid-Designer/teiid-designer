/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.history.util;

import com.metamatrix.metamodels.history.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.history.HistoryPackage
 * @generated
 */
public class HistorySwitch {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static HistoryPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public HistorySwitch() {
        if (modelPackage == null) {
            modelPackage = HistoryPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch(EObject theEObject) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(EClass theEClass, EObject theEObject) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        }
        List eSuperTypes = theEClass.getESuperTypes();
        return
            eSuperTypes.isEmpty() ?
                defaultCase(theEObject) :
                doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case HistoryPackage.HISTORY_LOG: {
                HistoryLog historyLog = (HistoryLog)theEObject;
                Object result = caseHistoryLog(historyLog);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case HistoryPackage.REVISION: {
                Revision revision = (Revision)theEObject;
                Object result = caseRevision(revision);
                if (result == null) result = caseHistoryLogEntry(revision);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case HistoryPackage.LABEL: {
                Label label = (Label)theEObject;
                Object result = caseLabel(label);
                if (result == null) result = caseHistoryLogEntry(label);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case HistoryPackage.HISTORY_LOG_ENTRY: {
                HistoryLogEntry historyLogEntry = (HistoryLogEntry)theEObject;
                Object result = caseHistoryLogEntry(historyLogEntry);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case HistoryPackage.BRANCH: {
                Branch branch = (Branch)theEObject;
                Object result = caseBranch(branch);
                if (result == null) result = caseHistoryLogEntry(branch);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case HistoryPackage.HISTORY_CRITERIA: {
                HistoryCriteria historyCriteria = (HistoryCriteria)theEObject;
                Object result = caseHistoryCriteria(historyCriteria);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case HistoryPackage.REVISION_LOG: {
                RevisionLog revisionLog = (RevisionLog)theEObject;
                Object result = caseRevisionLog(revisionLog);
                if (result == null) result = caseHistoryLog(revisionLog);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case HistoryPackage.LABEL_LOG: {
                LabelLog labelLog = (LabelLog)theEObject;
                Object result = caseLabelLog(labelLog);
                if (result == null) result = caseHistoryLog(labelLog);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Log</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Log</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseHistoryLog(HistoryLog object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Revision</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Revision</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRevision(Revision object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Label</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Label</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseLabel(Label object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Log Entry</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Log Entry</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseHistoryLogEntry(HistoryLogEntry object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Branch</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Branch</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseBranch(Branch object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Criteria</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Criteria</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseHistoryCriteria(HistoryCriteria object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Revision Log</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Revision Log</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRevisionLog(RevisionLog object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Label Log</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Label Log</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseLabelLog(LabelLog object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase(EObject object) {
        return null;
    }

} //HistorySwitch
