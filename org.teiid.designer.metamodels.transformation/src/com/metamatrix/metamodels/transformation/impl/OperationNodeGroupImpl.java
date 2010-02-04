/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.transformation.AbstractOperationNode;
import com.metamatrix.metamodels.transformation.DataFlowLink;
import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.DataFlowNode;
import com.metamatrix.metamodels.transformation.OperationNode;
import com.metamatrix.metamodels.transformation.OperationNodeGroup;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Operation Node Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.OperationNodeGroupImpl#getContents <em>Contents</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OperationNodeGroupImpl extends AbstractOperationNodeImpl implements OperationNodeGroup {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getContents() <em>Contents</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getContents()
     * @generated
     * @ordered
     */
    protected EList contents = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OperationNodeGroupImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getOperationNodeGroup();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getContents() {
        if (contents == null) {
            contents = new EObjectContainmentWithInverseEList(AbstractOperationNode.class, this, TransformationPackage.OPERATION_NODE_GROUP__CONTENTS, TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP);
        }
        return contents;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public List getAllContents() {
        final List result = new ArrayList();
        addOperationNodesToList(getContents(), result);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public EList getInputLinks() {
        final EList result = new BasicEList();
        
        // Iterate over all operation nodes referenced within this group
        final List allContents = getAllContents();
        for (final Iterator iter = allContents.iterator(); iter.hasNext();) {
            final OperationNode node = (OperationNode)iter.next();
            
            // Any dataflow node that is linked to one of the operation nodes
            // within this group but is external to the group is one of the input
            // links we want to return
            for (final Iterator iter2 = node.getInputLinks().iterator(); iter2.hasNext();) {
                final DataFlowLink inputLink = (DataFlowLink)iter2.next();
                final DataFlowNode inputNode = inputLink.getInputNode();
                if (inputNode != null && !allContents.contains(inputNode)) {
                    result.add(inputLink);
                }
            } // for
        } // for
        
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public EList getOutputLinks() {
        final EList result = new BasicEList();
        
        // Iterate over all operation nodes referenced within this group
        final List allContents = getAllContents();
        for (final Iterator iter = allContents.iterator(); iter.hasNext();) {
            final OperationNode node = (OperationNode)iter.next();
            
            // Any dataflow node that is linked to one of the operation nodes
            // within this group but is external to the group is one of the output
            // links we want to return
            for (final Iterator iter2 = node.getOutputLinks().iterator(); iter2.hasNext();) {
                final DataFlowLink outputLink = (DataFlowLink)iter2.next();
                final DataFlowNode outputNode = outputLink.getOutputNode();
                if (outputNode != null && !allContents.contains(outputNode)) {
                    result.add(outputLink);
                }
            } // for
        } // for
        
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case TransformationPackage.OPERATION_NODE_GROUP__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.OPERATION_NODE_GROUP__OWNER, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__EXPRESSIONS:
                    return ((InternalEList)getExpressions()).basicAdd(otherEnd, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__CONTENTS:
                    return ((InternalEList)getContents()).basicAdd(otherEnd, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case TransformationPackage.OPERATION_NODE_GROUP__OWNER:
                    return eBasicSetContainer(null, TransformationPackage.OPERATION_NODE_GROUP__OWNER, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__EXPRESSIONS:
                    return ((InternalEList)getExpressions()).basicRemove(otherEnd, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP:
                    return eBasicSetContainer(null, TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__CONTENTS:
                    return ((InternalEList)getContents()).basicRemove(otherEnd, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case TransformationPackage.OPERATION_NODE_GROUP__OWNER:
                    return eContainer.eInverseRemove(this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES, DataFlowMappingRoot.class, msgs);
                case TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP:
                    return eContainer.eInverseRemove(this, TransformationPackage.OPERATION_NODE_GROUP__CONTENTS, OperationNodeGroup.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case TransformationPackage.OPERATION_NODE_GROUP__NAME:
                return getName();
            case TransformationPackage.OPERATION_NODE_GROUP__OWNER:
                return getOwner();
            case TransformationPackage.OPERATION_NODE_GROUP__INPUT_LINKS:
                return getInputLinks();
            case TransformationPackage.OPERATION_NODE_GROUP__OUTPUT_LINKS:
                return getOutputLinks();
            case TransformationPackage.OPERATION_NODE_GROUP__EXPRESSIONS:
                return getExpressions();
            case TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP:
                return getNodeGroup();
            case TransformationPackage.OPERATION_NODE_GROUP__CONTENTS:
                return getContents();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case TransformationPackage.OPERATION_NODE_GROUP__NAME:
                setName((String)newValue);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__OWNER:
                setOwner((DataFlowMappingRoot)newValue);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__INPUT_LINKS:
                getInputLinks().clear();
                getInputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__OUTPUT_LINKS:
                getOutputLinks().clear();
                getOutputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__EXPRESSIONS:
                getExpressions().clear();
                getExpressions().addAll((Collection)newValue);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP:
                setNodeGroup((OperationNodeGroup)newValue);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__CONTENTS:
                getContents().clear();
                getContents().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case TransformationPackage.OPERATION_NODE_GROUP__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__OWNER:
                setOwner((DataFlowMappingRoot)null);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__INPUT_LINKS:
                getInputLinks().clear();
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__OUTPUT_LINKS:
                getOutputLinks().clear();
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__EXPRESSIONS:
                getExpressions().clear();
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP:
                setNodeGroup((OperationNodeGroup)null);
                return;
            case TransformationPackage.OPERATION_NODE_GROUP__CONTENTS:
                getContents().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case TransformationPackage.OPERATION_NODE_GROUP__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TransformationPackage.OPERATION_NODE_GROUP__OWNER:
                return getOwner() != null;
            case TransformationPackage.OPERATION_NODE_GROUP__INPUT_LINKS:
                return inputLinks != null && !inputLinks.isEmpty();
            case TransformationPackage.OPERATION_NODE_GROUP__OUTPUT_LINKS:
                return outputLinks != null && !outputLinks.isEmpty();
            case TransformationPackage.OPERATION_NODE_GROUP__EXPRESSIONS:
                return expressions != null && !expressions.isEmpty();
            case TransformationPackage.OPERATION_NODE_GROUP__NODE_GROUP:
                return getNodeGroup() != null;
            case TransformationPackage.OPERATION_NODE_GROUP__CONTENTS:
                return contents != null && !contents.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    private void addOperationNodesToList(final List nodes, final List result) {
        for (final Iterator iter = nodes.iterator(); iter.hasNext();) {
            final DataFlowNode node = (DataFlowNode)iter.next();
            if (node instanceof OperationNode && !result.contains(node)) {
                result.add(node);
            } else if (node instanceof OperationNodeGroup ) {
                OperationNodeGroup nodeGroup = (OperationNodeGroup)node;
                addOperationNodesToList(nodeGroup.getContents(), result);
            }
        } // for
    }

} //OperationNodeGroupImpl
