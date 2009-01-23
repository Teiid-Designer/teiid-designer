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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.transformation.AbstractOperationNode;
import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.Expression;
import com.metamatrix.metamodels.transformation.ExpressionOwner;
import com.metamatrix.metamodels.transformation.OperationNodeGroup;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Abstract Operation Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.AbstractOperationNodeImpl#getExpressions <em>Expressions</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.AbstractOperationNodeImpl#getNodeGroup <em>Node Group</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class AbstractOperationNodeImpl extends DataFlowNodeImpl implements AbstractOperationNode {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getExpressions() <em>Expressions</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExpressions()
     * @generated
     * @ordered
     */
    protected EList expressions = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AbstractOperationNodeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getAbstractOperationNode();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getExpressions() {
        if (expressions == null) {
            expressions = new EObjectContainmentWithInverseEList(Expression.class, this, TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS, TransformationPackage.EXPRESSION__OWNER);
        }
        return expressions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OperationNodeGroup getNodeGroup() {
        if (eContainerFeatureID != TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP) return null;
        return (OperationNodeGroup)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNodeGroup(OperationNodeGroup newNodeGroup) {
        if (newNodeGroup != eContainer || (eContainerFeatureID != TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP && newNodeGroup != null)) {
            if (EcoreUtil.isAncestor(this, newNodeGroup))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newNodeGroup != null)
                msgs = ((InternalEObject)newNodeGroup).eInverseAdd(this, TransformationPackage.OPERATION_NODE_GROUP__CONTENTS, OperationNodeGroup.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newNodeGroup, TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP, newNodeGroup, newNodeGroup));
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
                case TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS:
                    return ((InternalEList)getExpressions()).basicAdd(otherEnd, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP, msgs);
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
                case TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER:
                    return eBasicSetContainer(null, TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS:
                    return ((InternalEList)getExpressions()).basicRemove(otherEnd, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP:
                    return eBasicSetContainer(null, TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP, msgs);
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
                case TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER:
                    return eContainer.eInverseRemove(this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES, DataFlowMappingRoot.class, msgs);
                case TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP:
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
            case TransformationPackage.ABSTRACT_OPERATION_NODE__NAME:
                return getName();
            case TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER:
                return getOwner();
            case TransformationPackage.ABSTRACT_OPERATION_NODE__INPUT_LINKS:
                return getInputLinks();
            case TransformationPackage.ABSTRACT_OPERATION_NODE__OUTPUT_LINKS:
                return getOutputLinks();
            case TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS:
                return getExpressions();
            case TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP:
                return getNodeGroup();
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
            case TransformationPackage.ABSTRACT_OPERATION_NODE__NAME:
                setName((String)newValue);
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER:
                setOwner((DataFlowMappingRoot)newValue);
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__INPUT_LINKS:
                getInputLinks().clear();
                getInputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__OUTPUT_LINKS:
                getOutputLinks().clear();
                getOutputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS:
                getExpressions().clear();
                getExpressions().addAll((Collection)newValue);
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP:
                setNodeGroup((OperationNodeGroup)newValue);
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
            case TransformationPackage.ABSTRACT_OPERATION_NODE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER:
                setOwner((DataFlowMappingRoot)null);
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__INPUT_LINKS:
                getInputLinks().clear();
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__OUTPUT_LINKS:
                getOutputLinks().clear();
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS:
                getExpressions().clear();
                return;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP:
                setNodeGroup((OperationNodeGroup)null);
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
            case TransformationPackage.ABSTRACT_OPERATION_NODE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TransformationPackage.ABSTRACT_OPERATION_NODE__OWNER:
                return getOwner() != null;
            case TransformationPackage.ABSTRACT_OPERATION_NODE__INPUT_LINKS:
                return inputLinks != null && !inputLinks.isEmpty();
            case TransformationPackage.ABSTRACT_OPERATION_NODE__OUTPUT_LINKS:
                return outputLinks != null && !outputLinks.isEmpty();
            case TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS:
                return expressions != null && !expressions.isEmpty();
            case TransformationPackage.ABSTRACT_OPERATION_NODE__NODE_GROUP:
                return getNodeGroup() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass) {
        if (baseClass == ExpressionOwner.class) {
            switch (derivedFeatureID) {
                case TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS: return TransformationPackage.EXPRESSION_OWNER__EXPRESSIONS;
                default: return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass) {
        if (baseClass == ExpressionOwner.class) {
            switch (baseFeatureID) {
                case TransformationPackage.EXPRESSION_OWNER__EXPRESSIONS: return TransformationPackage.ABSTRACT_OPERATION_NODE__EXPRESSIONS;
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

} //AbstractOperationNodeImpl
