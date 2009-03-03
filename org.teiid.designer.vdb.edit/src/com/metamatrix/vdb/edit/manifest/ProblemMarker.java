/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Problem Marker</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getSeverity <em>Severity</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getMessage <em>Message</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getTarget <em>Target</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getTargetUri <em>Target Uri</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getCode <em>Code</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getStackTrace <em>Stack Trace</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getMarked <em>Marked</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getChildren <em>Children</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker()
 * @model
 * @generated
 */
public interface ProblemMarker extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Severity</b></em>' attribute.
     * The literals are from the enumeration {@link com.metamatrix.vdb.edit.manifest.Severity}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Severity</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Severity</em>' attribute.
     * @see com.metamatrix.vdb.edit.manifest.Severity
     * @see #setSeverity(Severity)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_Severity()
     * @model
     * @generated
     */
    Severity getSeverity();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getSeverity <em>Severity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Severity</em>' attribute.
     * @see com.metamatrix.vdb.edit.manifest.Severity
     * @see #getSeverity()
     * @generated
     */
    void setSeverity(Severity value);

    /**
     * Returns the value of the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Message</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Message</em>' attribute.
     * @see #setMessage(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_Message()
     * @model
     * @generated
     */
    String getMessage();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getMessage <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Message</em>' attribute.
     * @see #getMessage()
     * @generated
     */
    void setMessage(String value);

    /**
     * Returns the value of the '<em><b>Target</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Target</em>' attribute.
     * @see #setTarget(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_Target()
     * @model
     * @generated
     */
    String getTarget();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getTarget <em>Target</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target</em>' attribute.
     * @see #getTarget()
     * @generated
     */
    void setTarget(String value);

    /**
     * Returns the value of the '<em><b>Target Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target Uri</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Target Uri</em>' attribute.
     * @see #setTargetUri(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_TargetUri()
     * @model
     * @generated
     */
    String getTargetUri();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getTargetUri <em>Target Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Target Uri</em>' attribute.
     * @see #getTargetUri()
     * @generated
     */
    void setTargetUri(String value);

    /**
     * Returns the value of the '<em><b>Code</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Code</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Code</em>' attribute.
     * @see #setCode(int)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_Code()
     * @model
     * @generated
     */
    int getCode();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getCode <em>Code</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Code</em>' attribute.
     * @see #getCode()
     * @generated
     */
    void setCode(int value);

    /**
     * Returns the value of the '<em><b>Stack Trace</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Stack Trace</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Stack Trace</em>' attribute.
     * @see #setStackTrace(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_StackTrace()
     * @model
     * @generated
     */
    String getStackTrace();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getStackTrace <em>Stack Trace</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Stack Trace</em>' attribute.
     * @see #getStackTrace()
     * @generated
     */
    void setStackTrace(String value);

    /**
     * Returns the value of the '<em><b>Marked</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getMarkers <em>Markers</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Marked</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Marked</em>' container reference.
     * @see #setMarked(ProblemMarkerContainer)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_Marked()
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getMarkers
     * @model opposite="markers" required="true"
     * @generated
     */
    ProblemMarkerContainer getMarked();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getMarked <em>Marked</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Marked</em>' container reference.
     * @see #getMarked()
     * @generated
     */
    void setMarked(ProblemMarkerContainer value);

    /**
     * Returns the value of the '<em><b>Children</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.vdb.edit.manifest.ProblemMarker}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Children</em>' containment reference list.
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_Children()
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getParent
     * @model type="com.metamatrix.vdb.edit.manifest.ProblemMarker" opposite="parent" containment="true"
     * @generated
     */
    EList getChildren();

    /**
     * Returns the value of the '<em><b>Parent</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parent</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parent</em>' container reference.
     * @see #setParent(ProblemMarker)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarker_Parent()
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getChildren
     * @model opposite="children"
     * @generated
     */
    ProblemMarker getParent();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getParent <em>Parent</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parent</em>' container reference.
     * @see #getParent()
     * @generated
     */
    void setParent(ProblemMarker value);

} // ProblemMarker
