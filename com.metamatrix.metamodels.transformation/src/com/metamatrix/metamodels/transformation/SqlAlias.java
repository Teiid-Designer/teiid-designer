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
 * A representation of the model object '<em><b>Sql Alias</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlAlias#getAlias <em>Alias</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlAlias#getAliasedObject <em>Aliased Object</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlAlias#getSqlTransformation <em>Sql Transformation</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlAlias()
 * @model
 * @generated
 */
public interface SqlAlias extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Alias</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Alias</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Alias</em>' attribute.
     * @see #setAlias(String)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlAlias_Alias()
     * @model
     * @generated
     */
    String getAlias();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlAlias#getAlias <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Alias</em>' attribute.
     * @see #getAlias()
     * @generated
     */
    void setAlias(String value);

    /**
     * Returns the value of the '<em><b>Aliased Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Aliased Object</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Aliased Object</em>' reference.
     * @see #setAliasedObject(EObject)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlAlias_AliasedObject()
     * @model required="true"
     * @generated
     */
    EObject getAliasedObject();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlAlias#getAliasedObject <em>Aliased Object</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Aliased Object</em>' reference.
     * @see #getAliasedObject()
     * @generated
     */
    void setAliasedObject(EObject value);

    /**
     * Returns the value of the '<em><b>Sql Transformation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getAliases <em>Aliases</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sql Transformation</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Sql Transformation</em>' container reference.
     * @see #setSqlTransformation(SqlTransformation)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlAlias_SqlTransformation()
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#getAliases
     * @model opposite="aliases"
     * @generated
     */
    SqlTransformation getSqlTransformation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlAlias#getSqlTransformation <em>Sql Transformation</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Sql Transformation</em>' container reference.
     * @see #getSqlTransformation()
     * @generated
     */
    void setSqlTransformation(SqlTransformation value);

} // SqlAlias
