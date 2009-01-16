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

import java.util.List;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation Node Group</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.OperationNodeGroup#getContents <em>Contents</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getOperationNodeGroup()
 * @model
 * @generated
 */
public interface OperationNodeGroup extends AbstractOperationNode{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Contents</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.AbstractOperationNode}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.AbstractOperationNode#getNodeGroup <em>Node Group</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Contents</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Contents</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getOperationNodeGroup_Contents()
     * @see com.metamatrix.metamodels.transformation.AbstractOperationNode#getNodeGroup
     * @model type="com.metamatrix.metamodels.transformation.AbstractOperationNode" opposite="nodeGroup" containment="true"
     * @generated
     */
    EList getContents();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model dataType="com.metamatrix.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getAllContents();

} // OperationNodeGroup
