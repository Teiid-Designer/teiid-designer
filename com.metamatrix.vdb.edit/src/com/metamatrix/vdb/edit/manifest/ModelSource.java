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
 * A representation of the model object '<em><b>Model Source</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelSource#getProperties <em>Properties</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.ModelSource#getModel <em>Model</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelSource()
 * @model
 * @generated
 */
public interface ModelSource extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.vdb.edit.manifest.ModelSourceProperty}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ModelSourceProperty#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Properties</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Properties</em>' containment reference list.
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelSource_Properties()
     * @see com.metamatrix.vdb.edit.manifest.ModelSourceProperty#getSource
     * @model type="com.metamatrix.vdb.edit.manifest.ModelSourceProperty" opposite="source" containment="true"
     * @generated
     */
    EList getProperties();

    /**
     * Returns the value of the '<em><b>Model</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getModelSource <em>Model Source</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Model</em>' container reference.
     * @see #setModel(ModelReference)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getModelSource_Model()
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getModelSource
     * @model opposite="modelSource" required="true"
     * @generated
     */
    ModelReference getModel();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.ModelSource#getModel <em>Model</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Model</em>' container reference.
     * @see #getModel()
     * @generated
     */
    void setModel(ModelReference value);

} // ModelSource
