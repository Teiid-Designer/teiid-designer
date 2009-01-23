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
 * A representation of the model object '<em><b>Problem Marker Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getSeverity <em>Severity</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getMarkers <em>Markers</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarkerContainer()
 * @model abstract="true"
 * @generated
 */
public interface ProblemMarkerContainer extends EObject{
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
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarkerContainer_Severity()
     * @model
     * @generated
     */
    Severity getSeverity();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer#getSeverity <em>Severity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Severity</em>' attribute.
     * @see com.metamatrix.vdb.edit.manifest.Severity
     * @see #getSeverity()
     * @generated
     */
    void setSeverity(Severity value);

    /**
     * Returns the value of the '<em><b>Markers</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.vdb.edit.manifest.ProblemMarker}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ProblemMarker#getMarked <em>Marked</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Markers</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Markers</em>' containment reference list.
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getProblemMarkerContainer_Markers()
     * @see com.metamatrix.vdb.edit.manifest.ProblemMarker#getMarked
     * @model type="com.metamatrix.vdb.edit.manifest.ProblemMarker" opposite="marked" containment="true"
     * @generated
     */
    EList getMarkers();

} // ProblemMarkerContainer
