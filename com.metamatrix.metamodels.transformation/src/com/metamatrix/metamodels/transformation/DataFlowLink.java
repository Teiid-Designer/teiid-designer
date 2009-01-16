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
 * A representation of the model object '<em><b>Data Flow Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOutputNode <em>Output Node</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowLink#getInputNode <em>Input Node</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowLink()
 * @model
 * @generated
 */
public interface DataFlowLink extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Output Node</b></em>' reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getInputLinks <em>Input Links</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Output Node</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Output Node</em>' reference.
     * @see #setOutputNode(DataFlowNode)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowLink_OutputNode()
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getInputLinks
     * @model opposite="inputLinks" required="true"
     * @generated
     */
    DataFlowNode getOutputNode();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOutputNode <em>Output Node</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Output Node</em>' reference.
     * @see #getOutputNode()
     * @generated
     */
    void setOutputNode(DataFlowNode value);

    /**
     * Returns the value of the '<em><b>Input Node</b></em>' reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getOutputLinks <em>Output Links</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Node</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Node</em>' reference.
     * @see #setInputNode(DataFlowNode)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowLink_InputNode()
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getOutputLinks
     * @model opposite="outputLinks" required="true"
     * @generated
     */
    DataFlowNode getInputNode();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getInputNode <em>Input Node</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Input Node</em>' reference.
     * @see #getInputNode()
     * @generated
     */
    void setInputNode(DataFlowNode value);

    /**
     * Returns the value of the '<em><b>Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getLinks <em>Links</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owner</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owner</em>' container reference.
     * @see #setOwner(DataFlowMappingRoot)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowLink_Owner()
     * @see com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getLinks
     * @model opposite="links"
     * @generated
     */
    DataFlowMappingRoot getOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOwner <em>Owner</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Owner</em>' container reference.
     * @see #getOwner()
     * @generated
     */
    void setOwner(DataFlowMappingRoot value);

} // DataFlowLink
