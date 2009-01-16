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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.extension.XPackage;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Model Annotation</b></em>'. <!-- end-user-doc --> <!--
 * begin-model-doc --> This metaclass is intended to be instantiated with as a root object in a model (generally the first, if
 * possible) to capture additional information about the model. <!-- end-model-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getDescription <em>Description</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getNameInSource <em>Name In Source</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getPrimaryMetamodelUri <em>Primary Metamodel Uri</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getModelType <em>Model Type</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getMaxSetSize <em>Max Set Size</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#isVisible <em>Visible</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsDistinct <em>Supports Distinct</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsJoin <em>Supports Join</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsOrderBy <em>Supports Order By</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsOuterJoin <em>Supports Outer Join</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsWhereAll <em>Supports Where All</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getTags <em>Tags</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getNamespaceUri <em>Namespace Uri</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getProducerName <em>Producer Name</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getProducerVersion <em>Producer Version</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getModelImports <em>Model Imports</em>}</li>
 * <li>{@link com.metamatrix.metamodels.core.ModelAnnotation#getExtensionPackage <em>Extension Package</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation()
 * @model
 * @generated
 */
public interface ModelAnnotation extends EObject {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_Description()
     * @model
     * @generated
     */
    String getDescription();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getDescription <em>Description</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     * @generated
     */
    void setDescription( String value );

    /**
     * Returns the value of the '<em><b>Name In Source</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name In Source</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Name In Source</em>' attribute.
     * @see #setNameInSource(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_NameInSource()
     * @model
     * @generated
     */
    String getNameInSource();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getNameInSource <em>Name In Source</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Name In Source</em>' attribute.
     * @see #getNameInSource()
     * @generated
     */
    void setNameInSource( String value );

    /**
     * Returns the value of the '<em><b>Primary Metamodel Uri</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Primary Metamodel Uri</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Primary Metamodel Uri</em>' attribute.
     * @see #setPrimaryMetamodelUri(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_PrimaryMetamodelUri()
     * @model
     * @generated
     */
    String getPrimaryMetamodelUri();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getPrimaryMetamodelUri
     * <em>Primary Metamodel Uri</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Primary Metamodel Uri</em>' attribute.
     * @see #getPrimaryMetamodelUri()
     * @generated
     */
    void setPrimaryMetamodelUri( String value );

    /**
     * Returns the value of the '<em><b>Model Type</b></em>' attribute. The default value is <code>"UNKNOWN"</code>. The literals
     * are from the enumeration {@link com.metamatrix.metamodels.core.ModelType}. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model Type</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Model Type</em>' attribute.
     * @see com.metamatrix.metamodels.core.ModelType
     * @see #setModelType(ModelType)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_ModelType()
     * @model default="UNKNOWN"
     * @generated
     */
    ModelType getModelType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getModelType <em>Model Type</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Model Type</em>' attribute.
     * @see com.metamatrix.metamodels.core.ModelType
     * @see #getModelType()
     * @generated
     */
    void setModelType( ModelType value );

    /**
     * Returns the value of the '<em><b>Max Set Size</b></em>' attribute. The default value is <code>"100"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Max Set Size</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Max Set Size</em>' attribute.
     * @see #setMaxSetSize(int)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_MaxSetSize()
     * @model default="100"
     * @generated
     */
    int getMaxSetSize();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getMaxSetSize <em>Max Set Size</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Max Set Size</em>' attribute.
     * @see #getMaxSetSize()
     * @generated
     */
    void setMaxSetSize( int value );

    /**
     * Returns the value of the '<em><b>Visible</b></em>' attribute. The default value is <code>"true"</code>. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Visible</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Visible</em>' attribute.
     * @see #setVisible(boolean)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_Visible()
     * @model default="true"
     * @generated
     */
    boolean isVisible();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#isVisible <em>Visible</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Visible</em>' attribute.
     * @see #isVisible()
     * @generated
     */
    void setVisible( boolean value );

    /**
     * Returns the value of the '<em><b>Supports Distinct</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Supports Distinct</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Supports Distinct</em>' attribute.
     * @see #setSupportsDistinct(boolean)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_SupportsDistinct()
     * @model default="true"
     * @generated
     */
    boolean isSupportsDistinct();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsDistinct <em>Supports Distinct</em>}
     * ' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Supports Distinct</em>' attribute.
     * @see #isSupportsDistinct()
     * @generated
     */
    void setSupportsDistinct( boolean value );

    /**
     * Returns the value of the '<em><b>Supports Join</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Supports Join</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Supports Join</em>' attribute.
     * @see #setSupportsJoin(boolean)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_SupportsJoin()
     * @model default="true"
     * @generated
     */
    boolean isSupportsJoin();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsJoin <em>Supports Join</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Supports Join</em>' attribute.
     * @see #isSupportsJoin()
     * @generated
     */
    void setSupportsJoin( boolean value );

    /**
     * Returns the value of the '<em><b>Supports Order By</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Supports Order By</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Supports Order By</em>' attribute.
     * @see #setSupportsOrderBy(boolean)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_SupportsOrderBy()
     * @model default="true"
     * @generated
     */
    boolean isSupportsOrderBy();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsOrderBy <em>Supports Order By</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Supports Order By</em>' attribute.
     * @see #isSupportsOrderBy()
     * @generated
     */
    void setSupportsOrderBy( boolean value );

    /**
     * Returns the value of the '<em><b>Supports Outer Join</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Supports Outer Join</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Supports Outer Join</em>' attribute.
     * @see #setSupportsOuterJoin(boolean)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_SupportsOuterJoin()
     * @model default="true"
     * @generated
     */
    boolean isSupportsOuterJoin();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsOuterJoin
     * <em>Supports Outer Join</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Supports Outer Join</em>' attribute.
     * @see #isSupportsOuterJoin()
     * @generated
     */
    void setSupportsOuterJoin( boolean value );

    /**
     * Returns the value of the '<em><b>Supports Where All</b></em>' attribute. The default value is <code>"true"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Supports Where All</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Supports Where All</em>' attribute.
     * @see #setSupportsWhereAll(boolean)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_SupportsWhereAll()
     * @model default="true"
     * @generated
     */
    boolean isSupportsWhereAll();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsWhereAll
     * <em>Supports Where All</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Supports Where All</em>' attribute.
     * @see #isSupportsWhereAll()
     * @generated
     */
    void setSupportsWhereAll( boolean value );

    /**
     * Returns the value of the '<em><b>Tags</b></em>' map. The key is of type {@link java.lang.String}, and the value is of type
     * {@link java.lang.String}, <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tags</em>' map isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Tags</em>' map.
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_Tags()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String"
     * @generated
     */
    EMap getTags();

    /**
     * Returns the value of the '<em><b>Namespace Uri</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Namespace Uri</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Namespace Uri</em>' attribute.
     * @see #setNamespaceUri(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_NamespaceUri()
     * @model
     * @generated
     */
    String getNamespaceUri();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getNamespaceUri <em>Namespace Uri</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Namespace Uri</em>' attribute.
     * @see #getNamespaceUri()
     * @generated
     */
    void setNamespaceUri( String value );

    /**
     * Returns the value of the '<em><b>Producer Name</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Producer Name</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Producer Name</em>' attribute.
     * @see #setProducerName(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_ProducerName()
     * @model
     * @generated
     */
    String getProducerName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getProducerName <em>Producer Name</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Producer Name</em>' attribute.
     * @see #getProducerName()
     * @generated
     */
    void setProducerName( String value );

    /**
     * Returns the value of the '<em><b>Producer Version</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Producer Version</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Producer Version</em>' attribute.
     * @see #setProducerVersion(String)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_ProducerVersion()
     * @model
     * @generated
     */
    String getProducerVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getProducerVersion <em>Producer Version</em>}'
     * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Producer Version</em>' attribute.
     * @see #getProducerVersion()
     * @generated
     */
    void setProducerVersion( String value );

    /**
     * Returns the value of the '<em><b>Model Imports</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.core.ModelImport}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.core.ModelImport#getModel <em>Model</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Model Imports</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Model Imports</em>' containment reference list.
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_ModelImports()
     * @see com.metamatrix.metamodels.core.ModelImport#getModel
     * @model type="com.metamatrix.metamodels.core.ModelImport" opposite="model" containment="true"
     * @generated
     */
    EList getModelImports();

    /**
     * Returns the value of the '<em><b>Extension Package</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Extension Package</em>' reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Extension Package</em>' reference.
     * @see #setExtensionPackage(XPackage)
     * @see com.metamatrix.metamodels.core.CorePackage#getModelAnnotation_ExtensionPackage()
     * @model
     * @generated
     */
    XPackage getExtensionPackage();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.core.ModelAnnotation#getExtensionPackage
     * <em>Extension Package</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Extension Package</em>' reference.
     * @see #getExtensionPackage()
     * @generated
     */
    void setExtensionPackage( XPackage value );

} // ModelAnnotation
