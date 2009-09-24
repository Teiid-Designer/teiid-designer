/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.transformation.DataFlowLink;
import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.DataFlowNode;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Flow Link</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowLinkImpl#getOutputNode <em>Output Node</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowLinkImpl#getInputNode <em>Input Node</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowLinkImpl#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataFlowLinkImpl extends EObjectImpl implements DataFlowLink {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getOutputNode() <em>Output Node</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutputNode()
     * @generated
     * @ordered
     */
    protected DataFlowNode outputNode = null;

    /**
     * The cached value of the '{@link #getInputNode() <em>Input Node</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInputNode()
     * @generated
     * @ordered
     */
    protected DataFlowNode inputNode = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DataFlowLinkImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getDataFlowLink();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DataFlowNode getOutputNode() {
        if (outputNode != null && outputNode.eIsProxy()) {
            DataFlowNode oldOutputNode = outputNode;
            outputNode = (DataFlowNode)eResolveProxy((InternalEObject)outputNode);
            if (outputNode != oldOutputNode) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE, oldOutputNode, outputNode));
            }
        }
        return outputNode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DataFlowNode basicGetOutputNode() {
        return outputNode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetOutputNode(DataFlowNode newOutputNode, NotificationChain msgs) {
        DataFlowNode oldOutputNode = outputNode;
        outputNode = newOutputNode;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE, oldOutputNode, newOutputNode);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOutputNode(DataFlowNode newOutputNode) {
        if (newOutputNode != outputNode) {
            NotificationChain msgs = null;
            if (outputNode != null)
                msgs = ((InternalEObject)outputNode).eInverseRemove(this, TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS, DataFlowNode.class, msgs);
            if (newOutputNode != null)
                msgs = ((InternalEObject)newOutputNode).eInverseAdd(this, TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS, DataFlowNode.class, msgs);
            msgs = basicSetOutputNode(newOutputNode, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE, newOutputNode, newOutputNode));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DataFlowNode getInputNode() {
        if (inputNode != null && inputNode.eIsProxy()) {
            DataFlowNode oldInputNode = inputNode;
            inputNode = (DataFlowNode)eResolveProxy((InternalEObject)inputNode);
            if (inputNode != oldInputNode) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TransformationPackage.DATA_FLOW_LINK__INPUT_NODE, oldInputNode, inputNode));
            }
        }
        return inputNode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DataFlowNode basicGetInputNode() {
        return inputNode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetInputNode(DataFlowNode newInputNode, NotificationChain msgs) {
        DataFlowNode oldInputNode = inputNode;
        inputNode = newInputNode;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, TransformationPackage.DATA_FLOW_LINK__INPUT_NODE, oldInputNode, newInputNode);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInputNode(DataFlowNode newInputNode) {
        if (newInputNode != inputNode) {
            NotificationChain msgs = null;
            if (inputNode != null)
                msgs = ((InternalEObject)inputNode).eInverseRemove(this, TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS, DataFlowNode.class, msgs);
            if (newInputNode != null)
                msgs = ((InternalEObject)newInputNode).eInverseAdd(this, TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS, DataFlowNode.class, msgs);
            msgs = basicSetInputNode(newInputNode, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.DATA_FLOW_LINK__INPUT_NODE, newInputNode, newInputNode));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DataFlowMappingRoot getOwner() {
        if (eContainerFeatureID != TransformationPackage.DATA_FLOW_LINK__OWNER) return null;
        return (DataFlowMappingRoot)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOwner(DataFlowMappingRoot newOwner) {
        if (newOwner != eContainer || (eContainerFeatureID != TransformationPackage.DATA_FLOW_LINK__OWNER && newOwner != null)) {
            if (EcoreUtil.isAncestor(this, newOwner))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOwner != null)
                msgs = ((InternalEObject)newOwner).eInverseAdd(this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS, DataFlowMappingRoot.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOwner, TransformationPackage.DATA_FLOW_LINK__OWNER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.DATA_FLOW_LINK__OWNER, newOwner, newOwner));
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
                case TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE:
                    if (outputNode != null)
                        msgs = ((InternalEObject)outputNode).eInverseRemove(this, TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS, DataFlowNode.class, msgs);
                    return basicSetOutputNode((DataFlowNode)otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_LINK__INPUT_NODE:
                    if (inputNode != null)
                        msgs = ((InternalEObject)inputNode).eInverseRemove(this, TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS, DataFlowNode.class, msgs);
                    return basicSetInputNode((DataFlowNode)otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_LINK__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.DATA_FLOW_LINK__OWNER, msgs);
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
                case TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE:
                    return basicSetOutputNode(null, msgs);
                case TransformationPackage.DATA_FLOW_LINK__INPUT_NODE:
                    return basicSetInputNode(null, msgs);
                case TransformationPackage.DATA_FLOW_LINK__OWNER:
                    return eBasicSetContainer(null, TransformationPackage.DATA_FLOW_LINK__OWNER, msgs);
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
                case TransformationPackage.DATA_FLOW_LINK__OWNER:
                    return eContainer.eInverseRemove(this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS, DataFlowMappingRoot.class, msgs);
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
            case TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE:
                if (resolve) return getOutputNode();
                return basicGetOutputNode();
            case TransformationPackage.DATA_FLOW_LINK__INPUT_NODE:
                if (resolve) return getInputNode();
                return basicGetInputNode();
            case TransformationPackage.DATA_FLOW_LINK__OWNER:
                return getOwner();
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
            case TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE:
                setOutputNode((DataFlowNode)newValue);
                return;
            case TransformationPackage.DATA_FLOW_LINK__INPUT_NODE:
                setInputNode((DataFlowNode)newValue);
                return;
            case TransformationPackage.DATA_FLOW_LINK__OWNER:
                setOwner((DataFlowMappingRoot)newValue);
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
            case TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE:
                setOutputNode((DataFlowNode)null);
                return;
            case TransformationPackage.DATA_FLOW_LINK__INPUT_NODE:
                setInputNode((DataFlowNode)null);
                return;
            case TransformationPackage.DATA_FLOW_LINK__OWNER:
                setOwner((DataFlowMappingRoot)null);
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
            case TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE:
                return outputNode != null;
            case TransformationPackage.DATA_FLOW_LINK__INPUT_NODE:
                return inputNode != null;
            case TransformationPackage.DATA_FLOW_LINK__OWNER:
                return getOwner() != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //DataFlowLinkImpl
