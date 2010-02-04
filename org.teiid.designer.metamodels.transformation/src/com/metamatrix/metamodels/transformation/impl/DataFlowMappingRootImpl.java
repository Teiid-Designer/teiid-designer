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
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.MappingPackage;
import com.metamatrix.metamodels.transformation.DataFlowLink;
import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.DataFlowNode;
import com.metamatrix.metamodels.transformation.OperationNodeGroup;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Flow Mapping Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowMappingRootImpl#isAllowsOptimization <em>Allows Optimization</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowMappingRootImpl#getNodes <em>Nodes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowMappingRootImpl#getLinks <em>Links</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataFlowMappingRootImpl extends TransformationMappingRootImpl implements DataFlowMappingRoot {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #isAllowsOptimization() <em>Allows Optimization</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAllowsOptimization()
     * @generated
     * @ordered
     */
    protected static final boolean ALLOWS_OPTIMIZATION_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isAllowsOptimization() <em>Allows Optimization</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAllowsOptimization()
     * @generated
     * @ordered
     */
    protected boolean allowsOptimization = ALLOWS_OPTIMIZATION_EDEFAULT;

    /**
     * The cached value of the '{@link #getNodes() <em>Nodes</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNodes()
     * @generated
     * @ordered
     */
    protected EList nodes = null;

    /**
     * The cached value of the '{@link #getLinks() <em>Links</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLinks()
     * @generated
     * @ordered
     */
    protected EList links = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DataFlowMappingRootImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getDataFlowMappingRoot();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAllowsOptimization() {
        return allowsOptimization;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAllowsOptimization(boolean newAllowsOptimization) {
        boolean oldAllowsOptimization = allowsOptimization;
        allowsOptimization = newAllowsOptimization;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.DATA_FLOW_MAPPING_ROOT__ALLOWS_OPTIMIZATION, oldAllowsOptimization, allowsOptimization));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getNodes() {
        if (nodes == null) {
            nodes = new EObjectContainmentWithInverseEList(DataFlowNode.class, this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES, TransformationPackage.DATA_FLOW_NODE__OWNER);
        }
        return nodes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getLinks() {
        if (links == null) {
            links = new EObjectContainmentWithInverseEList(DataFlowLink.class, this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS, TransformationPackage.DATA_FLOW_LINK__OWNER);
        }
        return links;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public List getSourceNodes() {
        final List result = new ArrayList();
        addNodeTypeToList(getNodes(), TransformationPackage.SOURCE_NODE, result);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public List getTargetNodes() {
        final List result = new ArrayList();
        addNodeTypeToList(getNodes(), TransformationPackage.TARGET_NODE, result);
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getResultantSql() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
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
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__HELPER:
                    if (helper != null)
                        msgs = ((InternalEObject)helper).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TransformationPackage.DATA_FLOW_MAPPING_ROOT__HELPER, null, msgs);
                    return basicSetHelper((MappingHelper)otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED:
                    return ((InternalEList)getNested()).basicAdd(otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN, msgs);
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES:
                    return ((InternalEList)getNodes()).basicAdd(otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS:
                    return ((InternalEList)getLinks()).basicAdd(otherEnd, msgs);
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
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__HELPER:
                    return basicSetHelper(null, msgs);
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED:
                    return ((InternalEList)getNested()).basicRemove(otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN:
                    return eBasicSetContainer(null, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN, msgs);
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES:
                    return ((InternalEList)getNodes()).basicRemove(otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS:
                    return ((InternalEList)getLinks()).basicRemove(otherEnd, msgs);
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
                case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING__NESTED, Mapping.class, msgs);
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
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__HELPER:
                return getHelper();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED:
                return getNested();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN:
                return getNestedIn();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__INPUTS:
                return getInputs();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__OUTPUTS:
                return getOutputs();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TYPE_MAPPING:
                if (resolve) return getTypeMapping();
                return basicGetTypeMapping();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__OUTPUT_READ_ONLY:
                return isOutputReadOnly() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TOP_TO_BOTTOM:
                return isTopToBottom() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__COMMAND_STACK:
                return getCommandStack();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TARGET:
                if (resolve) return getTarget();
                return basicGetTarget();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__ALLOWS_OPTIMIZATION:
                return isAllowsOptimization() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES:
                return getNodes();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS:
                return getLinks();
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
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__HELPER:
                setHelper((MappingHelper)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED:
                getNested().clear();
                getNested().addAll((Collection)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN:
                setNestedIn((Mapping)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__INPUTS:
                getInputs().clear();
                getInputs().addAll((Collection)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__OUTPUTS:
                getOutputs().clear();
                getOutputs().addAll((Collection)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TYPE_MAPPING:
                setTypeMapping((Mapping)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__OUTPUT_READ_ONLY:
                setOutputReadOnly(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TOP_TO_BOTTOM:
                setTopToBottom(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__COMMAND_STACK:
                setCommandStack((String)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TARGET:
                setTarget((EObject)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__ALLOWS_OPTIMIZATION:
                setAllowsOptimization(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES:
                getNodes().clear();
                getNodes().addAll((Collection)newValue);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS:
                getLinks().clear();
                getLinks().addAll((Collection)newValue);
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
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__HELPER:
                setHelper((MappingHelper)null);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED:
                getNested().clear();
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN:
                setNestedIn((Mapping)null);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__INPUTS:
                getInputs().clear();
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__OUTPUTS:
                getOutputs().clear();
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TYPE_MAPPING:
                setTypeMapping((Mapping)null);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__OUTPUT_READ_ONLY:
                setOutputReadOnly(OUTPUT_READ_ONLY_EDEFAULT);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TOP_TO_BOTTOM:
                setTopToBottom(TOP_TO_BOTTOM_EDEFAULT);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__COMMAND_STACK:
                setCommandStack(COMMAND_STACK_EDEFAULT);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TARGET:
                setTarget((EObject)null);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__ALLOWS_OPTIMIZATION:
                setAllowsOptimization(ALLOWS_OPTIMIZATION_EDEFAULT);
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES:
                getNodes().clear();
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS:
                getLinks().clear();
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
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__HELPER:
                return helper != null;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED:
                return nested != null && !nested.isEmpty();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NESTED_IN:
                return getNestedIn() != null;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__INPUTS:
                return inputs != null && !inputs.isEmpty();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__OUTPUTS:
                return outputs != null && !outputs.isEmpty();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TYPE_MAPPING:
                return typeMapping != null;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__OUTPUT_READ_ONLY:
                return outputReadOnly != OUTPUT_READ_ONLY_EDEFAULT;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TOP_TO_BOTTOM:
                return topToBottom != TOP_TO_BOTTOM_EDEFAULT;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__COMMAND_STACK:
                return COMMAND_STACK_EDEFAULT == null ? commandStack != null : !COMMAND_STACK_EDEFAULT.equals(commandStack);
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__TARGET:
                return target != null;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__ALLOWS_OPTIMIZATION:
                return allowsOptimization != ALLOWS_OPTIMIZATION_EDEFAULT;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES:
                return nodes != null && !nodes.isEmpty();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS:
                return links != null && !links.isEmpty();
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
        result.append(" (allowsOptimization: "); //$NON-NLS-1$
        result.append(allowsOptimization);
        result.append(')');
        return result.toString();
    }

    protected void addNodeTypeToList(final List nodes, final int theClassifierID, final List result) {
        for (final Iterator iter = nodes.iterator(); iter.hasNext();) {
            final DataFlowNode node = (DataFlowNode)iter.next();
            if (node != null) {
                final int classifierID = node.eClass().getClassifierID();
                if (classifierID == theClassifierID) {
                    result.add(node);
                }
                if (classifierID == TransformationPackage.OPERATION_NODE_GROUP) {
                    OperationNodeGroup operationNodeGroup = (OperationNodeGroup)node;
                    addNodeTypeToList(operationNodeGroup.getContents(), theClassifierID, result);
                }
            } 
        } // for
    }

} //DataFlowMappingRootImpl
