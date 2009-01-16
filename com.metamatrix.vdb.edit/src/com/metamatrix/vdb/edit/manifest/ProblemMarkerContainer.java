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
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

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
