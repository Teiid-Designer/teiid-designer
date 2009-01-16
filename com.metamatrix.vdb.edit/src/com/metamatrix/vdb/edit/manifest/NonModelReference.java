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

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Non Model Reference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getPath <em>Path</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getChecksum <em>Checksum</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getProperties <em>Properties</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getVirtualDatabase <em>Virtual Database</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getNonModelReference()
 * @model
 * @generated
 */
public interface NonModelReference extends EObject{
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
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getNonModelReference_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getName <em>Name</em>}' attribute.
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
     * @see #setPath(String)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getNonModelReference_Path()
     * @model
     * @generated
     */
    String getPath();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getPath <em>Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Path</em>' attribute.
     * @see #getPath()
     * @generated
     */
    void setPath(String value);

    /**
     * Returns the value of the '<em><b>Checksum</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Checksum</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Checksum</em>' attribute.
     * @see #setChecksum(long)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getNonModelReference_Checksum()
     * @model
     * @generated
     */
    long getChecksum();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getChecksum <em>Checksum</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Checksum</em>' attribute.
     * @see #getChecksum()
     * @generated
     */
    void setChecksum(long value);

    /**
     * Returns the value of the '<em><b>Properties</b></em>' map.
     * The key is of type {@link java.lang.String},
     * and the value is of type {@link java.lang.String},
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Properties</em>' map isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Properties</em>' map.
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getNonModelReference_Properties()
     * @model mapType="org.eclipse.emf.ecore.EStringToStringMapEntry" keyType="java.lang.String" valueType="java.lang.String"
     * @generated
     */
    EMap getProperties();

    /**
     * Returns the value of the '<em><b>Virtual Database</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.vdb.edit.manifest.VirtualDatabase#getNonModels <em>Non Models</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Virtual Database</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Virtual Database</em>' container reference.
     * @see #setVirtualDatabase(VirtualDatabase)
     * @see com.metamatrix.vdb.edit.manifest.ManifestPackage#getNonModelReference_VirtualDatabase()
     * @see com.metamatrix.vdb.edit.manifest.VirtualDatabase#getNonModels
     * @model opposite="nonModels" required="true"
     * @generated
     */
    VirtualDatabase getVirtualDatabase();

    /**
     * Sets the value of the '{@link com.metamatrix.vdb.edit.manifest.NonModelReference#getVirtualDatabase <em>Virtual Database</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Virtual Database</em>' container reference.
     * @see #getVirtualDatabase()
     * @generated
     */
    void setVirtualDatabase(VirtualDatabase value);

} // NonModelReference
