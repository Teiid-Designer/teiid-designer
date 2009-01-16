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

import java.util.Date;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Virtual Database</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getIdentifier <em>Identifier</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getUuid <em>Uuid</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getDescription <em>Description</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getVersion <em>Version</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProvider <em>Provider</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastChanged <em>Time Last Changed</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastProduced <em>Time Last Produced</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastChangedAsDate <em>Time Last Changed As Date</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastProducedAsDate <em>Time Last Produced As Date</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProducerName <em>Producer Name</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProducerVersion <em>Producer Version</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#isIncludeModelFiles <em>Include Model Files</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getModels <em>Models</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getWsdlOptions <em>Wsdl Options</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getNonModels <em>Non Models</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase()
 * @model
 * @generated
 */
public interface VirtualDatabase extends ProblemMarkerContainer{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

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
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Identifier</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Identifier</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Identifier</em>' attribute.
     * @see #setIdentifier(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_Identifier()
     * @model
     * @generated
     */
    String getIdentifier();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getIdentifier <em>Identifier</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Identifier</em>' attribute.
     * @see #getIdentifier()
     * @generated
     */
    void setIdentifier(String value);

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
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_Uuid()
     * @model
     * @generated
     */
    String getUuid();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getUuid <em>Uuid</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Uuid</em>' attribute.
     * @see #getUuid()
     * @generated
     */
    void setUuid(String value);

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_Description()
     * @model
     * @generated
     */
    String getDescription();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getDescription <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     * @generated
     */
    void setDescription(String value);

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
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_Version()
     * @model
     * @generated
     */
    String getVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
    void setVersion(String value);

    /**
     * Returns the value of the '<em><b>Provider</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Provider</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Provider</em>' attribute.
     * @see #setProvider(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_Provider()
     * @model
     * @generated
     */
    String getProvider();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProvider <em>Provider</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Provider</em>' attribute.
     * @see #getProvider()
     * @generated
     */
    void setProvider(String value);

    /**
     * Returns the value of the '<em><b>Time Last Changed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Time Last Changed</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Time Last Changed</em>' attribute.
     * @see #setTimeLastChanged(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_TimeLastChanged()
     * @model
     * @generated
     */
    String getTimeLastChanged();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastChanged <em>Time Last Changed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time Last Changed</em>' attribute.
     * @see #getTimeLastChanged()
     * @generated
     */
    void setTimeLastChanged(String value);

    /**
     * Returns the value of the '<em><b>Time Last Produced</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Time Last Produced</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Time Last Produced</em>' attribute.
     * @see #setTimeLastProduced(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_TimeLastProduced()
     * @model
     * @generated
     */
    String getTimeLastProduced();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastProduced <em>Time Last Produced</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time Last Produced</em>' attribute.
     * @see #getTimeLastProduced()
     * @generated
     */
    void setTimeLastProduced(String value);

    /**
     * Returns the value of the '<em><b>Time Last Changed As Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Time Last Changed As Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Time Last Changed As Date</em>' attribute.
     * @see #setTimeLastChangedAsDate(Date)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_TimeLastChangedAsDate()
     * @model dataType="com.metamatrix.vdb.edit.manifest.JavaDate" volatile="true"
     * @generated
     */
    Date getTimeLastChangedAsDate();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastChangedAsDate <em>Time Last Changed As Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time Last Changed As Date</em>' attribute.
     * @see #getTimeLastChangedAsDate()
     * @generated
     */
    void setTimeLastChangedAsDate(Date value);

    /**
     * Returns the value of the '<em><b>Time Last Produced As Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Time Last Produced As Date</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Time Last Produced As Date</em>' attribute.
     * @see #setTimeLastProducedAsDate(Date)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_TimeLastProducedAsDate()
     * @model dataType="com.metamatrix.vdb.edit.manifest.JavaDate" volatile="true"
     * @generated
     */
    Date getTimeLastProducedAsDate();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getTimeLastProducedAsDate <em>Time Last Produced As Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Time Last Produced As Date</em>' attribute.
     * @see #getTimeLastProducedAsDate()
     * @generated
     */
    void setTimeLastProducedAsDate(Date value);

    /**
     * Returns the value of the '<em><b>Producer Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Producer Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Producer Name</em>' attribute.
     * @see #setProducerName(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_ProducerName()
     * @model
     * @generated
     */
    String getProducerName();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProducerName <em>Producer Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Producer Name</em>' attribute.
     * @see #getProducerName()
     * @generated
     */
    void setProducerName(String value);

    /**
     * Returns the value of the '<em><b>Producer Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Producer Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Producer Version</em>' attribute.
     * @see #setProducerVersion(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_ProducerVersion()
     * @model
     * @generated
     */
    String getProducerVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getProducerVersion <em>Producer Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Producer Version</em>' attribute.
     * @see #getProducerVersion()
     * @generated
     */
    void setProducerVersion(String value);

    /**
     * Returns the value of the '<em><b>Include Model Files</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Include Model Files</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Include Model Files</em>' attribute.
     * @see #setIncludeModelFiles(boolean)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_IncludeModelFiles()
     * @model
     * @generated
     */
    boolean isIncludeModelFiles();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#isIncludeModelFiles <em>Include Model Files</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Include Model Files</em>' attribute.
     * @see #isIncludeModelFiles()
     * @generated
     */
    void setIncludeModelFiles(boolean value);

    /**
     * Returns the value of the '<em><b>Models</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.vdb.edit.manifest.ModelReference}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.ModelReference#getVirtualDatabase <em>Virtual Database</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Models</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Models</em>' containment reference list.
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_Models()
     * @see com.metamatrix.vdb.edit.manifest.ModelReference#getVirtualDatabase
     * @model type="com.metamatrix.vdb.edit.manifest.ModelReference" opposite="virtualDatabase" containment="true"
     * @generated
     */
    EList getModels();

    /**
     * Returns the value of the '<em><b>Wsdl Options</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.WsdlOptions#getVirtualDatabase <em>Virtual Database</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Wsdl Options</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Wsdl Options</em>' containment reference.
     * @see #setWsdlOptions(WsdlOptions)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_WsdlOptions()
     * @see com.metamatrix.vdb.edit.manifest.WsdlOptions#getVirtualDatabase
     * @model opposite="virtualDatabase" containment="true"
     * @generated
     */
	WsdlOptions getWsdlOptions();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getWsdlOptions <em>Wsdl Options</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Wsdl Options</em>' containment reference.
     * @see #getWsdlOptions()
     * @generated
     */
	void setWsdlOptions(WsdlOptions value);

    /**
     * Returns the value of the '<em><b>Non Models</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.vdb.edit.manifest.NonModelReference}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getVirtualDatabase <em>Virtual Database</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Non Models</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Non Models</em>' containment reference list.
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getVirtualDatabase_NonModels()
     * @see com.metamatrix.vdb.edit.manifest.NonModelReference#getVirtualDatabase
     * @model type="com.metamatrix.vdb.edit.manifest.NonModelReference" opposite="virtualDatabase" containment="true"
     * @generated
     */
    EList getNonModels();

} // VirtualDatabase
