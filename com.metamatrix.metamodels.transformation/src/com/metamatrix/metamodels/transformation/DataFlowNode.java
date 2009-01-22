/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import java.util.List;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Flow Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowNode#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowNode#getOwner <em>Owner</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowNode#getInputLinks <em>Input Links</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowNode#getOutputLinks <em>Output Links</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowNode()
 * @model
 * @generated
 */
public interface DataFlowNode extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

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
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowNode_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getNodes <em>Nodes</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owner</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Owner</em>' container reference.
     * @see #setOwner(DataFlowMappingRoot)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowNode_Owner()
     * @see com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getNodes
     * @model opposite="nodes"
     * @generated
     */
    DataFlowMappingRoot getOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getOwner <em>Owner</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Owner</em>' container reference.
     * @see #getOwner()
     * @generated
     */
    void setOwner(DataFlowMappingRoot value);

    /**
     * Returns the value of the '<em><b>Input Links</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.DataFlowLink}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOutputNode <em>Output Node</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Links</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Links</em>' reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowNode_InputLinks()
     * @see com.metamatrix.metamodels.transformation.DataFlowLink#getOutputNode
     * @model type="com.metamatrix.metamodels.transformation.DataFlowLink" opposite="outputNode"
     * @generated
     */
    EList getInputLinks();

    /**
     * Returns the value of the '<em><b>Output Links</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.DataFlowLink}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getInputNode <em>Input Node</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Output Links</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Output Links</em>' reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowNode_OutputLinks()
     * @see com.metamatrix.metamodels.transformation.DataFlowLink#getInputNode
     * @model type="com.metamatrix.metamodels.transformation.DataFlowLink" opposite="inputNode"
     * @generated
     */
    EList getOutputLinks();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model dataType="com.metamatrix.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getInputNodes();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model dataType="com.metamatrix.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getOutputNodes();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model dataType="com.metamatrix.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getProjectedSymbols();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    String getSqlString();

} // DataFlowNode
