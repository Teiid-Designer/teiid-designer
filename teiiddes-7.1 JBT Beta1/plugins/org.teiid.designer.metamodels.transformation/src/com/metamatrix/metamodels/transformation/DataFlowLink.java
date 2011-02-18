/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Data Flow Link</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOutputNode <em>Output Node</em>}</li>
 * <li>{@link com.metamatrix.metamodels.transformation.DataFlowLink#getInputNode <em>Input Node</em>}</li>
 * <li>{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowLink()
 * @model
 * @generated
 */
public interface DataFlowLink extends EObject {

    /**
     * Returns the value of the '<em><b>Output Node</b></em>' reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.transformation.DataFlowNode#getInputLinks <em>Input Links</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Output Node</em>' reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Output Node</em>' reference.
     * @see #setOutputNode(DataFlowNode)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowLink_OutputNode()
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getInputLinks
     * @model opposite="inputLinks" required="true"
     * @generated
     */
    DataFlowNode getOutputNode();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOutputNode <em>Output Node</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Output Node</em>' reference.
     * @see #getOutputNode()
     * @generated
     */
    void setOutputNode( DataFlowNode value );

    /**
     * Returns the value of the '<em><b>Input Node</b></em>' reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.transformation.DataFlowNode#getOutputLinks <em>Output Links</em>}'. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Input Node</em>' reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Input Node</em>' reference.
     * @see #setInputNode(DataFlowNode)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowLink_InputNode()
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getOutputLinks
     * @model opposite="outputLinks" required="true"
     * @generated
     */
    DataFlowNode getInputNode();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getInputNode <em>Input Node</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Input Node</em>' reference.
     * @see #getInputNode()
     * @generated
     */
    void setInputNode( DataFlowNode value );

    /**
     * Returns the value of the '<em><b>Owner</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getLinks <em>Links</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Owner</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Owner</em>' container reference.
     * @see #setOwner(DataFlowMappingRoot)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowLink_Owner()
     * @see com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getLinks
     * @model opposite="links"
     * @generated
     */
    DataFlowMappingRoot getOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOwner <em>Owner</em>}' container
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Owner</em>' container reference.
     * @see #getOwner()
     * @generated
     */
    void setOwner( DataFlowMappingRoot value );

} // DataFlowLink
