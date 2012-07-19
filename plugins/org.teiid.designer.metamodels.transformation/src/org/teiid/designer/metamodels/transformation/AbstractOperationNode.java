/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.transformation;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Abstract Operation Node</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.transformation.AbstractOperationNode#getNodeGroup <em>Node Group</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getAbstractOperationNode()
 * @model abstract="true"
 * @generated
 *
 * @since 8.0
 */
public interface AbstractOperationNode extends DataFlowNode, ExpressionOwner {

    /**
     * Returns the value of the '<em><b>Node Group</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.transformation.OperationNodeGroup#getContents <em>Contents</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Node Group</em>' reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Node Group</em>' container reference.
     * @see #setNodeGroup(OperationNodeGroup)
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getAbstractOperationNode_NodeGroup()
     * @see org.teiid.designer.metamodels.transformation.OperationNodeGroup#getContents
     * @model opposite="contents"
     * @generated
     */
    OperationNodeGroup getNodeGroup();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.transformation.AbstractOperationNode#getNodeGroup
     * <em>Node Group</em>}' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Node Group</em>' container reference.
     * @see #getNodeGroup()
     * @generated
     */
    void setNodeGroup( OperationNodeGroup value );

} // AbstractOperationNode
