/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.transformation;

import java.util.List;
import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Data Flow Mapping Root</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.transformation.DataFlowMappingRoot#isAllowsOptimization <em>Allows Optimization</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.transformation.DataFlowMappingRoot#getNodes <em>Nodes</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.transformation.DataFlowMappingRoot#getLinks <em>Links</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getDataFlowMappingRoot()
 * @model
 * @generated
 */
public interface DataFlowMappingRoot extends TransformationMappingRoot {

    /**
     * Returns the value of the '<em><b>Allows Optimization</b></em>' attribute. The default value is <code>"false"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Allows Optimization</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Allows Optimization</em>' attribute.
     * @see #setAllowsOptimization(boolean)
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getDataFlowMappingRoot_AllowsOptimization()
     * @model default="false"
     * @generated
     */
    boolean isAllowsOptimization();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.transformation.DataFlowMappingRoot#isAllowsOptimization
     * <em>Allows Optimization</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Allows Optimization</em>' attribute.
     * @see #isAllowsOptimization()
     * @generated
     */
    void setAllowsOptimization( boolean value );

    /**
     * Returns the value of the '<em><b>Nodes</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.transformation.DataFlowNode}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.transformation.DataFlowNode#getOwner <em>Owner</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Nodes</em>' containment reference list isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Nodes</em>' containment reference list.
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getDataFlowMappingRoot_Nodes()
     * @see org.teiid.designer.metamodels.transformation.DataFlowNode#getOwner
     * @model type="org.teiid.designer.metamodels.transformation.DataFlowNode" opposite="owner" containment="true" required="true"
     * @generated
     */
    EList getNodes();

    /**
     * Returns the value of the '<em><b>Links</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.transformation.DataFlowLink}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.transformation.DataFlowLink#getOwner <em>Owner</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Links</em>' containment reference list isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Links</em>' containment reference list.
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getDataFlowMappingRoot_Links()
     * @see org.teiid.designer.metamodels.transformation.DataFlowLink#getOwner
     * @model type="org.teiid.designer.metamodels.transformation.DataFlowLink" opposite="owner" containment="true" required="true"
     * @generated
     */
    EList getLinks();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @model dataType="org.teiid.designer.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getSourceNodes(); // NO_UCD

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @model dataType="org.teiid.designer.metamodels.transformation.List" many="false" parameters=""
     * @generated
     */
    List getTargetNodes(); // NO_UCD

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @model parameters=""
     * @generated
     */
    String getResultantSql(); // NO_UCD

} // DataFlowMappingRoot
