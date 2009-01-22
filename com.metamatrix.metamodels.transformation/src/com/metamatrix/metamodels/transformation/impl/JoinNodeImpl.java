/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.JoinNode;
import com.metamatrix.metamodels.transformation.JoinType;
import com.metamatrix.metamodels.transformation.OperationNodeGroup;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Join Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.JoinNodeImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class JoinNodeImpl extends OperationNodeImpl implements JoinNode {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected static final JoinType TYPE_EDEFAULT = JoinType.INNER_LITERAL;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected JoinType type = TYPE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected JoinNodeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getJoinNode();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JoinType getType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setType(JoinType newType) {
        JoinType oldType = type;
        type = newType == null ? TYPE_EDEFAULT : newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.JOIN_NODE__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getCriteria() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * Min number of input dataflow nodes to this operation
     * @generated NOT
     */
    @Override
    public int getMinInputs() {
        return 0;
    }

    /**
     * Max number of input dataflow nodes to this operation
     * @generated NOT
     */
    @Override
    public int getMaxInputs() {
        return 2;
    }

    /**
     * Min number of input dataflow nodes to this operation
     * @generated NOT
     */
    @Override
    public int getMinOutputs() {
        return 0;
    }

    /**
     * Max number of output dataflow nodes to this operation
     * @generated NOT
     */
    @Override
    public int getMaxOutputs() {
        return 1;
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
                case TransformationPackage.JOIN_NODE__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.JOIN_NODE__OWNER, msgs);
                case TransformationPackage.JOIN_NODE__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.JOIN_NODE__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.JOIN_NODE__EXPRESSIONS:
                    return ((InternalEList)getExpressions()).basicAdd(otherEnd, msgs);
                case TransformationPackage.JOIN_NODE__NODE_GROUP:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.JOIN_NODE__NODE_GROUP, msgs);
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
                case TransformationPackage.JOIN_NODE__OWNER:
                    return eBasicSetContainer(null, TransformationPackage.JOIN_NODE__OWNER, msgs);
                case TransformationPackage.JOIN_NODE__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.JOIN_NODE__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.JOIN_NODE__EXPRESSIONS:
                    return ((InternalEList)getExpressions()).basicRemove(otherEnd, msgs);
                case TransformationPackage.JOIN_NODE__NODE_GROUP:
                    return eBasicSetContainer(null, TransformationPackage.JOIN_NODE__NODE_GROUP, msgs);
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
                case TransformationPackage.JOIN_NODE__OWNER:
                    return eContainer.eInverseRemove(this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES, DataFlowMappingRoot.class, msgs);
                case TransformationPackage.JOIN_NODE__NODE_GROUP:
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
            case TransformationPackage.JOIN_NODE__NAME:
                return getName();
            case TransformationPackage.JOIN_NODE__OWNER:
                return getOwner();
            case TransformationPackage.JOIN_NODE__INPUT_LINKS:
                return getInputLinks();
            case TransformationPackage.JOIN_NODE__OUTPUT_LINKS:
                return getOutputLinks();
            case TransformationPackage.JOIN_NODE__EXPRESSIONS:
                return getExpressions();
            case TransformationPackage.JOIN_NODE__NODE_GROUP:
                return getNodeGroup();
            case TransformationPackage.JOIN_NODE__TYPE:
                return getType();
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
            case TransformationPackage.JOIN_NODE__NAME:
                setName((String)newValue);
                return;
            case TransformationPackage.JOIN_NODE__OWNER:
                setOwner((DataFlowMappingRoot)newValue);
                return;
            case TransformationPackage.JOIN_NODE__INPUT_LINKS:
                getInputLinks().clear();
                getInputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.JOIN_NODE__OUTPUT_LINKS:
                getOutputLinks().clear();
                getOutputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.JOIN_NODE__EXPRESSIONS:
                getExpressions().clear();
                getExpressions().addAll((Collection)newValue);
                return;
            case TransformationPackage.JOIN_NODE__NODE_GROUP:
                setNodeGroup((OperationNodeGroup)newValue);
                return;
            case TransformationPackage.JOIN_NODE__TYPE:
                setType((JoinType)newValue);
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
            case TransformationPackage.JOIN_NODE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TransformationPackage.JOIN_NODE__OWNER:
                setOwner((DataFlowMappingRoot)null);
                return;
            case TransformationPackage.JOIN_NODE__INPUT_LINKS:
                getInputLinks().clear();
                return;
            case TransformationPackage.JOIN_NODE__OUTPUT_LINKS:
                getOutputLinks().clear();
                return;
            case TransformationPackage.JOIN_NODE__EXPRESSIONS:
                getExpressions().clear();
                return;
            case TransformationPackage.JOIN_NODE__NODE_GROUP:
                setNodeGroup((OperationNodeGroup)null);
                return;
            case TransformationPackage.JOIN_NODE__TYPE:
                setType(TYPE_EDEFAULT);
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
            case TransformationPackage.JOIN_NODE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TransformationPackage.JOIN_NODE__OWNER:
                return getOwner() != null;
            case TransformationPackage.JOIN_NODE__INPUT_LINKS:
                return inputLinks != null && !inputLinks.isEmpty();
            case TransformationPackage.JOIN_NODE__OUTPUT_LINKS:
                return outputLinks != null && !outputLinks.isEmpty();
            case TransformationPackage.JOIN_NODE__EXPRESSIONS:
                return expressions != null && !expressions.isEmpty();
            case TransformationPackage.JOIN_NODE__NODE_GROUP:
                return getNodeGroup() != null;
            case TransformationPackage.JOIN_NODE__TYPE:
                return type != TYPE_EDEFAULT;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (type: "); //$NON-NLS-1$
        result.append(type);
        result.append(')');
        return result.toString();
    }

} //JoinNodeImpl
