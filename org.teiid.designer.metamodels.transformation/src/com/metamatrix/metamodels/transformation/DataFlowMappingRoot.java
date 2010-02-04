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
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Data Flow Mapping Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#isAllowsOptimization <em>Allows Optimization</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getNodes <em>Nodes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getLinks <em>Links</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowMappingRoot()
 * @model
 * @generated
 */
public interface DataFlowMappingRoot extends TransformationMappingRoot{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Allows Optimization</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Allows Optimization</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Allows Optimization</em>' attribute.
     * @see #setAllowsOptimization(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowMappingRoot_AllowsOptimization()
     * @model default="false"
     * @generated
     */
    boolean isAllowsOptimization();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#isAllowsOptimization <em>Allows Optimization</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Allows Optimization</em>' attribute.
     * @see #isAllowsOptimization()
     * @generated
     */
    void setAllowsOptimization(boolean value);

    /**
     * Returns the value of the '<em><b>Nodes</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.DataFlowNode}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Nodes</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Nodes</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowMappingRoot_Nodes()
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getOwner
     * @model type="com.metamatrix.metamodels.transformation.DataFlowNode" opposite="owner" containment="true" required="true"
     * @generated
     */
    EList getNodes();

    /**
     * Returns the value of the '<em><b>Links</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.DataFlowLink}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Links</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Links</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getDataFlowMappingRoot_Links()
     * @see com.metamatrix.metamodels.transformation.DataFlowLink#getOwner
     * @model type="com.metamatrix.metamodels.transformation.DataFlowLink" opposite="owner" containment="true" required="true"
     * @generated
     */
    EList getLinks();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model dataType="com.metamatrix.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getSourceNodes();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model dataType="com.metamatrix.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getTargetNodes();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    String getResultantSql();

} // DataFlowMappingRoot
