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

package com.metamatrix.metamodels.core;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Import</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.core.ModelImport#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.ModelImport#getPath <em>Path</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.ModelImport#getModelLocation <em>Model Location</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.ModelImport#getUuid <em>Uuid</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.ModelImport#getModelType <em>Model Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.ModelImport#getPrimaryMetamodelUri <em>Primary Metamodel Uri</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.ModelImport#getModel <em>Model</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.core.CorePackage#getModelImport()
 * @model
 * @generated
 */
public interface ModelImport extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelImport_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelImport#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Path</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Path</em>' attribute.
     * @see com.metamatrix.metamodels.core.CorePackage#getModelImport_Path()
     * @model transient="true" changeable="false" volatile="true"
     * @generated
     */
    String getPath();

    /**
     * Returns the value of the '<em><b>Model Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model Location</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Model Location</em>' attribute.
     * @see #setModelLocation(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelImport_ModelLocation()
     * @model
     * @generated
     */
    String getModelLocation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelImport#getModelLocation <em>Model Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Model Location</em>' attribute.
     * @see #getModelLocation()
     * @generated
     */
    void setModelLocation(String value);

    /**
     * Returns the value of the '<em><b>Uuid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Uuid</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Uuid</em>' attribute.
     * @see #setUuid(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelImport_Uuid()
     * @model
     * @generated
     */
    String getUuid();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelImport#getUuid <em>Uuid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Uuid</em>' attribute.
     * @see #getUuid()
     * @generated
     */
    void setUuid(String value);

    /**
     * Returns the value of the '<em><b>Model Type</b></em>' attribute.
     * The default value is <code>"UNKNOWN"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.core.ModelType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Model Type</em>' attribute.
     * @see com.metamatrix.metamodels.core.ModelType
     * @see #setModelType(ModelType)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelImport_ModelType()
     * @model default="UNKNOWN"
     * @generated
     */
    ModelType getModelType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelImport#getModelType <em>Model Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Model Type</em>' attribute.
     * @see com.metamatrix.metamodels.core.ModelType
     * @see #getModelType()
     * @generated
     */
    void setModelType(ModelType value);

    /**
     * Returns the value of the '<em><b>Primary Metamodel Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Primary Metamodel Uri</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Primary Metamodel Uri</em>' attribute.
     * @see #setPrimaryMetamodelUri(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelImport_PrimaryMetamodelUri()
     * @model
     * @generated
     */
    String getPrimaryMetamodelUri();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelImport#getPrimaryMetamodelUri <em>Primary Metamodel Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Primary Metamodel Uri</em>' attribute.
     * @see #getPrimaryMetamodelUri()
     * @generated
     */
    void setPrimaryMetamodelUri(String value);

    /**
     * Returns the value of the '<em><b>Model</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.core.ModelAnnotation#getModelImports <em>Model Imports</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Model</em>' container reference.
     * @see #setModel(ModelAnnotation)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelImport_Model()
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getModelImports
     * @model opposite="modelImports"
     * @generated
     */
    ModelAnnotation getModel();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelImport#getModel <em>Model</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Model</em>' container reference.
     * @see #getModel()
     * @generated
     */
    void setModel(ModelAnnotation value);

} // ModelImport
