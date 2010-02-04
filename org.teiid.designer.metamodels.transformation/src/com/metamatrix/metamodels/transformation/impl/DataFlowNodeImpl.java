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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.transformation.DataFlowLink;
import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.DataFlowNode;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Data Flow Node</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowNodeImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowNodeImpl#getOwner <em>Owner</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowNodeImpl#getInputLinks <em>Input Links</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.DataFlowNodeImpl#getOutputLinks <em>Output Links</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DataFlowNodeImpl extends EObjectImpl implements DataFlowNode {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The cached value of the '{@link #getInputLinks() <em>Input Links</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInputLinks()
     * @generated
     * @ordered
     */
    protected EList inputLinks = null;

    /**
     * The cached value of the '{@link #getOutputLinks() <em>Output Links</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutputLinks()
     * @generated
     * @ordered
     */
    protected EList outputLinks = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DataFlowNodeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getDataFlowNode();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.DATA_FLOW_NODE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DataFlowMappingRoot getOwner() {
        if (eContainerFeatureID != TransformationPackage.DATA_FLOW_NODE__OWNER) return null;
        return (DataFlowMappingRoot)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOwner(DataFlowMappingRoot newOwner) {
        if (newOwner != eContainer || (eContainerFeatureID != TransformationPackage.DATA_FLOW_NODE__OWNER && newOwner != null)) {
            if (EcoreUtil.isAncestor(this, newOwner))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOwner != null)
                msgs = ((InternalEObject)newOwner).eInverseAdd(this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES, DataFlowMappingRoot.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOwner, TransformationPackage.DATA_FLOW_NODE__OWNER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.DATA_FLOW_NODE__OWNER, newOwner, newOwner));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getInputLinks() {
        if (inputLinks == null) {
            inputLinks = new EObjectWithInverseResolvingEList(DataFlowLink.class, this, TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS, TransformationPackage.DATA_FLOW_LINK__OUTPUT_NODE);
        }
        return inputLinks;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getOutputLinks() {
        if (outputLinks == null) {
            outputLinks = new EObjectWithInverseResolvingEList(DataFlowLink.class, this, TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS, TransformationPackage.DATA_FLOW_LINK__INPUT_NODE);
        }
        return outputLinks;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public List getInputNodes() {
        final List result = new ArrayList();
        for (final Iterator iter = getInputLinks().iterator(); iter.hasNext();) {
            final DataFlowLink link = (DataFlowLink)iter.next();
            final DataFlowNode inputNode = link.getInputNode();
            if (inputNode != null) {
                result.add(inputNode);
            }
        } // for
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public List getOutputNodes() {
        final List result = new ArrayList();
        for (final Iterator iter = getOutputLinks().iterator(); iter.hasNext();) {
            final DataFlowLink link = (DataFlowLink)iter.next();
            final DataFlowNode outputNode = link.getOutputNode();
            if (outputNode != null) {
                result.add(outputNode);
            }
        } // for
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getProjectedSymbols() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getSqlString() {
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
                case TransformationPackage.DATA_FLOW_NODE__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.DATA_FLOW_NODE__OWNER, msgs);
                case TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicAdd(otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicAdd(otherEnd, msgs);
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
                case TransformationPackage.DATA_FLOW_NODE__OWNER:
                    return eBasicSetContainer(null, TransformationPackage.DATA_FLOW_NODE__OWNER, msgs);
                case TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS:
                    return ((InternalEList)getInputLinks()).basicRemove(otherEnd, msgs);
                case TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS:
                    return ((InternalEList)getOutputLinks()).basicRemove(otherEnd, msgs);
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
                case TransformationPackage.DATA_FLOW_NODE__OWNER:
                    return eContainer.eInverseRemove(this, TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES, DataFlowMappingRoot.class, msgs);
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
            case TransformationPackage.DATA_FLOW_NODE__NAME:
                return getName();
            case TransformationPackage.DATA_FLOW_NODE__OWNER:
                return getOwner();
            case TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS:
                return getInputLinks();
            case TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS:
                return getOutputLinks();
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
            case TransformationPackage.DATA_FLOW_NODE__NAME:
                setName((String)newValue);
                return;
            case TransformationPackage.DATA_FLOW_NODE__OWNER:
                setOwner((DataFlowMappingRoot)newValue);
                return;
            case TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS:
                getInputLinks().clear();
                getInputLinks().addAll((Collection)newValue);
                return;
            case TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS:
                getOutputLinks().clear();
                getOutputLinks().addAll((Collection)newValue);
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
            case TransformationPackage.DATA_FLOW_NODE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TransformationPackage.DATA_FLOW_NODE__OWNER:
                setOwner((DataFlowMappingRoot)null);
                return;
            case TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS:
                getInputLinks().clear();
                return;
            case TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS:
                getOutputLinks().clear();
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
            case TransformationPackage.DATA_FLOW_NODE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TransformationPackage.DATA_FLOW_NODE__OWNER:
                return getOwner() != null;
            case TransformationPackage.DATA_FLOW_NODE__INPUT_LINKS:
                return inputLinks != null && !inputLinks.isEmpty();
            case TransformationPackage.DATA_FLOW_NODE__OUTPUT_LINKS:
                return outputLinks != null && !outputLinks.isEmpty();
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
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(')');
        return result.toString();
    }

} //DataFlowNodeImpl
