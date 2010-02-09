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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Log Entry</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.history.HistoryLogEntry#getTimestamp <em>Timestamp</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.HistoryLogEntry#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryLogEntry()
 * @model abstract="true"
 * @generated
 */
public interface HistoryLogEntry extends EObject {

    /**
     * Returns the value of the '<em><b>Timestamp</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Timestamp</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Timestamp</em>' attribute.
     * @see #setTimestamp(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryLogEntry_Timestamp()
     * @model
     * @generated
     */
    String getTimestamp();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryLogEntry#getTimestamp <em>Timestamp</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Timestamp</em>' attribute.
     * @see #getTimestamp()
     * @generated
     */
    void setTimestamp( String value );

    /**
     * Returns the value of the '<em><b>User</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>User</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>User</em>' attribute.
     * @see #setUser(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getHistoryLogEntry_User()
     * @model
     * @generated
     */
    String getUser();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.HistoryLogEntry#getUser <em>User</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>User</em>' attribute.
     * @see #getUser()
     * @generated
     */
    void setUser( String value );

} // HistoryLogEntry
