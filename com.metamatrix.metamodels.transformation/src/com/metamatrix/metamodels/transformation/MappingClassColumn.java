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

package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.ecore.EObject;



/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Mapping Class Column</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClassColumn#getMappingClass <em>Mapping Class</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.MappingClassColumn#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClassColumn()
 * @model
 * @generated
 */
public interface MappingClassColumn extends MappingClassObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' reference.
     * @see #setType(EObject)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClassColumn_Type()
     * @model required="true"
     * @generated
     */
    EObject getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClassColumn#getType <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' reference.
     * @see #getType()
     * @generated
     */
    void setType(EObject value);

    /**
     * Returns the value of the '<em><b>Mapping Class</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.MappingClass#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping Class</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Mapping Class</em>' container reference.
     * @see #setMappingClass(MappingClass)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getMappingClassColumn_MappingClass()
     * @see com.metamatrix.metamodels.transformation.MappingClass#getColumns
     * @model opposite="columns" required="true"
     * @generated
     */
    MappingClass getMappingClass();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.MappingClassColumn#getMappingClass <em>Mapping Class</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mapping Class</em>' container reference.
     * @see #getMappingClass()
     * @generated
     */
    void setMappingClass(MappingClass value);

} // MappingClassColumn
