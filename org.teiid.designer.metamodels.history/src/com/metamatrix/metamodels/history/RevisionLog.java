/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Revision Log</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.history.RevisionLog#getFirstRevision <em>First Revision</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.history.HistoryPackage#getRevisionLog()
 * @model
 * @generated
 */
public interface RevisionLog extends HistoryLog{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>First Revision</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.history.Revision#getHistoryLog <em>History Log</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>First Revision</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>First Revision</em>' containment reference.
     * @see #setFirstRevision(Revision)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getRevisionLog_FirstRevision()
     * @see com.metamatrix.metamodels.history.Revision#getHistoryLog
     * @model opposite="historyLog" containment="true" required="true"
     * @generated
     */
    Revision getFirstRevision();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.RevisionLog#getFirstRevision <em>First Revision</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>First Revision</em>' containment reference.
     * @see #getFirstRevision()
     * @generated
     */
    void setFirstRevision(Revision value);

} // RevisionLog
