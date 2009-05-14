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
 * A representation of the model object '<em><b>Branch</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.history.Branch#getPreviousRevision <em>Previous Revision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.Branch#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.history.HistoryPackage#getBranch()
 * @model
 * @generated
 */
public interface Branch extends HistoryLogEntry{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see #setVersion(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getBranch_Version()
     * @model
     * @generated
     */
    String getVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.Branch#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
    void setVersion(String value);

    /**
     * Returns the value of the '<em><b>Previous Revision</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.history.Revision#getBranch <em>Branch</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Previous Revision</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Previous Revision</em>' container reference.
     * @see #setPreviousRevision(Revision)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getBranch_PreviousRevision()
     * @see com.metamatrix.metamodels.history.Revision#getBranch
     * @model opposite="branch" required="true"
     * @generated
     */
    Revision getPreviousRevision();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.Branch#getPreviousRevision <em>Previous Revision</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Previous Revision</em>' container reference.
     * @see #getPreviousRevision()
     * @generated
     */
    void setPreviousRevision(Revision value);

} // Branch
