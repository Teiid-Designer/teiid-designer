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

package com.metamatrix.metamodels.history;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Revision</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.history.Revision#getHistoryLog <em>History Log</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.Revision#getBranch <em>Branch</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.Revision#getLabel <em>Label</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.Revision#getNextRevision <em>Next Revision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.Revision#getPreviousRevision <em>Previous Revision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.Revision#getComment <em>Comment</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.Revision#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.history.HistoryPackage#getRevision()
 * @model
 * @generated
 */
public interface Revision extends HistoryLogEntry{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Comment</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Comment</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Comment</em>' attribute.
     * @see #setComment(String)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getRevision_Comment()
     * @model
     * @generated
     */
    String getComment();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.Revision#getComment <em>Comment</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Comment</em>' attribute.
     * @see #getComment()
     * @generated
     */
    void setComment(String value);

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
     * @see com.metamatrix.metamodels.history.HistoryPackage#getRevision_Version()
     * @model
     * @generated
     */
    String getVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.Revision#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
    void setVersion(String value);

    /**
     * Returns the value of the '<em><b>History Log</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.history.RevisionLog#getFirstRevision <em>First Revision</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>History Log</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>History Log</em>' container reference.
     * @see #setHistoryLog(RevisionLog)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getRevision_HistoryLog()
     * @see com.metamatrix.metamodels.history.RevisionLog#getFirstRevision
     * @model opposite="firstRevision" required="true"
     * @generated
     */
    RevisionLog getHistoryLog();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.Revision#getHistoryLog <em>History Log</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>History Log</em>' container reference.
     * @see #getHistoryLog()
     * @generated
     */
    void setHistoryLog(RevisionLog value);

    /**
     * Returns the value of the '<em><b>Branch</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.history.Branch}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.history.Branch#getPreviousRevision <em>Previous Revision</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Branch</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Branch</em>' containment reference list.
     * @see com.metamatrix.metamodels.history.HistoryPackage#getRevision_Branch()
     * @see com.metamatrix.metamodels.history.Branch#getPreviousRevision
     * @model type="com.metamatrix.metamodels.history.Branch" opposite="previousRevision" containment="true"
     * @generated
     */
    EList getBranch();

    /**
     * Returns the value of the '<em><b>Label</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.history.Label}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.history.Label#getRevision <em>Revision</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Label</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Label</em>' containment reference list.
     * @see com.metamatrix.metamodels.history.HistoryPackage#getRevision_Label()
     * @see com.metamatrix.metamodels.history.Label#getRevision
     * @model type="com.metamatrix.metamodels.history.Label" opposite="revision" containment="true"
     * @generated
     */
    EList getLabel();

    /**
     * Returns the value of the '<em><b>Next Revision</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.history.Revision#getPreviousRevision <em>Previous Revision</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Next Revision</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Next Revision</em>' containment reference.
     * @see #setNextRevision(Revision)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getRevision_NextRevision()
     * @see com.metamatrix.metamodels.history.Revision#getPreviousRevision
     * @model opposite="previousRevision" containment="true" required="true"
     * @generated
     */
    Revision getNextRevision();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.Revision#getNextRevision <em>Next Revision</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Next Revision</em>' containment reference.
     * @see #getNextRevision()
     * @generated
     */
    void setNextRevision(Revision value);

    /**
     * Returns the value of the '<em><b>Previous Revision</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.history.Revision#getNextRevision <em>Next Revision</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Previous Revision</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Previous Revision</em>' container reference.
     * @see #setPreviousRevision(Revision)
     * @see com.metamatrix.metamodels.history.HistoryPackage#getRevision_PreviousRevision()
     * @see com.metamatrix.metamodels.history.Revision#getNextRevision
     * @model opposite="nextRevision"
     * @generated
     */
    Revision getPreviousRevision();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.history.Revision#getPreviousRevision <em>Previous Revision</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Previous Revision</em>' container reference.
     * @see #getPreviousRevision()
     * @generated
     */
    void setPreviousRevision(Revision value);

} // Revision
