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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Log</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.history.HistoryLog#getHistoryCriteria <em>History Criteria</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.HistoryLog#getName <em>Name</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.HistoryLog#getUri <em>Uri</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryLog()
 * @model abstract="true"
 * @generated
 */
public interface HistoryLog extends EObject {

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryLog_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryLog#getName <em>Name</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName( String value );

    /**
     * Returns the value of the '<em><b>Uri</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Uri</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Uri</em>' attribute.
     * @see #setUri(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryLog_Uri()
     * @model
     * @generated
     */
    String getUri();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryLog#getUri <em>Uri</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Uri</em>' attribute.
     * @see #getUri()
     * @generated
     */
    void setUri( String value );

    /**
     * Returns the value of the '<em><b>History Criteria</b></em>' containment reference. It is bidirectional and its opposite is
     * '{@link com.metamatrix.metamodels.history.HistoryCriteria#getHistoryLog <em>History Log</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>History Criteria</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>History Criteria</em>' containment reference.
     * @see #setHistoryCriteria(HistoryCriteria)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryLog_HistoryCriteria()
     * @see com.metamatrix.metamodels.history.HistoryCriteria#getHistoryLog
     * @model opposite="historyLog" containment="true"
     * @generated
     */
    HistoryCriteria getHistoryCriteria();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryLog#getHistoryCriteria <em>History Criteria</em>}'
     * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>History Criteria</em>' containment reference.
     * @see #getHistoryCriteria()
     * @generated
     */
    void setHistoryCriteria( HistoryCriteria value );

} // HistoryLog
