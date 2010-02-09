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

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Operation Node Group</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.transformation.OperationNodeGroup#getContents <em>Contents</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getOperationNodeGroup()
 * @model
 * @generated
 */
public interface OperationNodeGroup extends AbstractOperationNode {

    /**
     * Returns the value of the '<em><b>Contents</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.transformation.AbstractOperationNode}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.transformation.AbstractOperationNode#getNodeGroup <em>Node Group</em>}'. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Contents</em>' reference list isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Contents</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getOperationNodeGroup_Contents()
     * @see com.metamatrix.metamodels.transformation.AbstractOperationNode#getNodeGroup
     * @model type="com.metamatrix.metamodels.transformation.AbstractOperationNode" opposite="nodeGroup" containment="true"
     * @generated
     */
    EList getContents();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @model dataType="com.metamatrix.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getAllContents();

} // OperationNodeGroup
