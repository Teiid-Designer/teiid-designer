/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Criteria</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.history.HistoryCriteria#getHistoryLog <em>History Log</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.HistoryCriteria#getFromDate <em>From Date</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.HistoryCriteria#getToDate <em>To Date</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.HistoryCriteria#getUser <em>User</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.HistoryCriteria#isIncludeLabels <em>Include Labels</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.HistoryCriteria#isOnlyLabels <em>Only Labels</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryCriteria()
 * @model
 * @generated
 */
public interface HistoryCriteria extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>From Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>From Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>From Date</em>' attribute.
     * @see #setFromDate(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryCriteria_FromDate()
     * @model
     * @generated
     */
    String getFromDate();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryCriteria#getFromDate <em>From Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>From Date</em>' attribute.
     * @see #getFromDate()
     * @generated
     */
    void setFromDate(String value);

    /**
     * Returns the value of the '<em><b>To Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>To Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>To Date</em>' attribute.
     * @see #setToDate(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryCriteria_ToDate()
     * @model
     * @generated
     */
    String getToDate();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryCriteria#getToDate <em>To Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>To Date</em>' attribute.
     * @see #getToDate()
     * @generated
     */
    void setToDate(String value);

    /**
     * Returns the value of the '<em><b>User</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>User</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>User</em>' attribute.
     * @see #setUser(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryCriteria_User()
     * @model
     * @generated
     */
    String getUser();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryCriteria#getUser <em>User</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>User</em>' attribute.
     * @see #getUser()
     * @generated
     */
    void setUser(String value);

    /**
     * Returns the value of the '<em><b>Include Labels</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Include Labels</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Include Labels</em>' attribute.
     * @see #setIncludeLabels(boolean)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryCriteria_IncludeLabels()
     * @model
     * @generated
     */
    boolean isIncludeLabels();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryCriteria#isIncludeLabels <em>Include Labels</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Include Labels</em>' attribute.
     * @see #isIncludeLabels()
     * @generated
     */
    void setIncludeLabels(boolean value);

    /**
     * Returns the value of the '<em><b>Only Labels</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Only Labels</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Only Labels</em>' attribute.
     * @see #setOnlyLabels(boolean)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryCriteria_OnlyLabels()
     * @model
     * @generated
     */
    boolean isOnlyLabels();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryCriteria#isOnlyLabels <em>Only Labels</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Only Labels</em>' attribute.
     * @see #isOnlyLabels()
     * @generated
     */
    void setOnlyLabels(boolean value);

    /**
     * Returns the value of the '<em><b>History Log</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.history.HistoryLog#getHistoryCriteria <em>History Criteria</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>History Log</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>History Log</em>' container reference.
     * @see #setHistoryLog(HistoryLog)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryCriteria_HistoryLog()
     * @see com.metamatrix.metamodels.history.HistoryLog#getHistoryCriteria
     * @model opposite="historyCriteria" required="true"
     * @generated
     */
    HistoryLog getHistoryLog();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryCriteria#getHistoryLog <em>History Log</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>History Log</em>' container reference.
     * @see #getHistoryLog()
     * @generated
     */
    void setHistoryLog(HistoryLog value);

} // HistoryCriteria
