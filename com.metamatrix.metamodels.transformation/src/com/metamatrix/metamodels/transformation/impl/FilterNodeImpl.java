/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.transformation.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.FilterNode;
import com.metamatrix.metamodels.transformation.OperationNodeGroup;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Filter Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class FilterNodeImpl extends OperationNodeImpl implements FilterNode {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected FilterNodeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getFilterNode();
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
        return 1;
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
                case TransformationPackage.FILTER_NODE__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.FILTER_NODE__OWNER, msgs);
                case TransformationPackage.FILTER_NODE__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.FILTER_NODE__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.FILTER_NODE__EXPRESSIONS:
                    return ((InternalEList)getExpressions()).basicAdd(otherEnd, msgs);
                case TransformationPackage.FILTER_NODE__NODE_GROUP:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.FILTER_NODE__NODE_GROUP, msgs);
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
                case TransformationPackage.FILTER_NODE__OWNER:
                    return eBasicSetContainer(null, TransformationPackage.FILTER_NODE__OWNER, msgs);
                case TransformationPackage.FILTER_NODE__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.FILTER_NODE__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.FILTER_NODE__EXPRESSIONS:
                    return ((InternalEList)getExpressions()).basicRemove(otherEnd, msgs);
                case TransformationPackage.FILTER_NODE__NODE_GROUP:
                    return eBasicSetContainer(null, TransformationPackage.FILTER_NODE__NODE_GROUP, msgs);
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
                case TransformationPackage.FILTER_NODE__OWNER:
                    return eContainer.eInverseRemove(this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES, DataFlowMappingRoot.class, msgs);
                case TransformationPackage.FILTER_NODE__NODE_GROUP:
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
            case TransformationPackage.FILTER_NODE__NAME:
                return getName();
            case TransformationPackage.FILTER_NODE__OWNER:
                return getOwner();
            case TransformationPackage.FILTER_NODE__INPUT_LINKS:
                return getInputLinks();
            case TransformationPackage.FILTER_NODE__OUTPUT_LINKS:
                return getOutputLinks();
            case TransformationPackage.FILTER_NODE__EXPRESSIONS:
                return getExpressions();
            case TransformationPackage.FILTER_NODE__NODE_GROUP:
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
            case TransformationPackage.FILTER_NODE__NAME:
                setName((String)newValue);
                return;
            case TransformationPackage.FILTER_NODE__OWNER:
                setOwner((DataFlowMappingRoot)newValue);
                return;
            case TransformationPackage.FILTER_NODE__INPUT_LINKS:
                getInputLinks().clear();
                getInputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.FILTER_NODE__OUTPUT_LINKS:
                getOutputLinks().clear();
                getOutputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.FILTER_NODE__EXPRESSIONS:
                getExpressions().clear();
                getExpressions().addAll((Collection)newValue);
                return;
            case TransformationPackage.FILTER_NODE__NODE_GROUP:
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
            case TransformationPackage.FILTER_NODE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TransformationPackage.FILTER_NODE__OWNER:
                setOwner((DataFlowMappingRoot)null);
                return;
            case TransformationPackage.FILTER_NODE__INPUT_LINKS:
                getInputLinks().clear();
                return;
            case TransformationPackage.FILTER_NODE__OUTPUT_LINKS:
                getOutputLinks().clear();
                return;
            case TransformationPackage.FILTER_NODE__EXPRESSIONS:
                getExpressions().clear();
                return;
            case TransformationPackage.FILTER_NODE__NODE_GROUP:
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
            case TransformationPackage.FILTER_NODE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TransformationPackage.FILTER_NODE__OWNER:
                return getOwner() != null;
            case TransformationPackage.FILTER_NODE__INPUT_LINKS:
                return inputLinks != null && !inputLinks.isEmpty();
            case TransformationPackage.FILTER_NODE__OUTPUT_LINKS:
                return outputLinks != null && !outputLinks.isEmpty();
            case TransformationPackage.FILTER_NODE__EXPRESSIONS:
                return expressions != null && !expressions.isEmpty();
            case TransformationPackage.FILTER_NODE__NODE_GROUP:
                return getNodeGroup() != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //FilterNodeImpl
