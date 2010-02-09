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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Source Node</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.transformation.SourceNode#getSource <em>Source</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSourceNode()
 * @model
 * @generated
 */
public interface SourceNode extends DataFlowNode {

    /**
     * Returns the value of the '<em><b>Source</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Source</em>' reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Source</em>' reference.
     * @see #setSource(EObject)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSourceNode_Source()
     * @model
     * @generated
     */
    EObject getSource();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SourceNode#getSource <em>Source</em>}' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Source</em>' reference.
     * @see #getSource()
     * @generated
     */
    void setSource( EObject value );

} // SourceNode
