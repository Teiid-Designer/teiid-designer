/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.xml.ProcessingInstruction;
import com.metamatrix.metamodels.xml.ProcessingInstructionHolder;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Processing Instruction</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.ProcessingInstructionImpl#getRawText <em>Raw Text</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.ProcessingInstructionImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.ProcessingInstructionImpl#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProcessingInstructionImpl extends XmlDocumentEntityImpl implements ProcessingInstruction {
    /**
     * The default value of the '{@link #getRawText() <em>Raw Text</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRawText()
     * @generated
     * @ordered
     */
    protected static final String RAW_TEXT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRawText() <em>Raw Text</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRawText()
     * @generated
     * @ordered
     */
    protected String rawText = RAW_TEXT_EDEFAULT;

    /**
     * The default value of the '{@link #getTarget() <em>Target</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTarget()
     * @generated
     * @ordered
     */
    protected static final String TARGET_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTarget() <em>Target</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTarget()
     * @generated
     * @ordered
     */
    protected String target = TARGET_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProcessingInstructionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getProcessingInstruction();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getRawText() {
        return rawText;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRawText(String newRawText) {
        String oldRawText = rawText;
        rawText = newRawText;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.PROCESSING_INSTRUCTION__RAW_TEXT, oldRawText, rawText));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTarget() {
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTarget(String newTarget) {
        String oldTarget = target;
        target = newTarget;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.PROCESSING_INSTRUCTION__TARGET, oldTarget, target));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ProcessingInstructionHolder getParent() {
        if (eContainerFeatureID != XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT) return null;
        return (ProcessingInstructionHolder)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setParent(ProcessingInstructionHolder newParent) {
        if (newParent != eContainer || (eContainerFeatureID != XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT && newParent != null)) {
            if (EcoreUtil.isAncestor(this, newParent))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newParent != null)
                msgs = ((InternalEObject)newParent).eInverseAdd(this, XmlDocumentPackage.PROCESSING_INSTRUCTION_HOLDER__PROCESSING_INSTRUCTIONS, ProcessingInstructionHolder.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newParent, XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT, newParent, newParent));
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
                case XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT, msgs);
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
                case XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT:
                    return eBasicSetContainer(null, XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT, msgs);
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
                case XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT:
                    return eContainer.eInverseRemove(this, XmlDocumentPackage.PROCESSING_INSTRUCTION_HOLDER__PROCESSING_INSTRUCTIONS, ProcessingInstructionHolder.class, msgs);
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
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__RAW_TEXT:
                return getRawText();
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__TARGET:
                return getTarget();
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT:
                return getParent();
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
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__RAW_TEXT:
                setRawText((String)newValue);
                return;
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__TARGET:
                setTarget((String)newValue);
                return;
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT:
                setParent((ProcessingInstructionHolder)newValue);
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
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__RAW_TEXT:
                setRawText(RAW_TEXT_EDEFAULT);
                return;
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__TARGET:
                setTarget(TARGET_EDEFAULT);
                return;
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT:
                setParent((ProcessingInstructionHolder)null);
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
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__RAW_TEXT:
                return RAW_TEXT_EDEFAULT == null ? rawText != null : !RAW_TEXT_EDEFAULT.equals(rawText);
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__TARGET:
                return TARGET_EDEFAULT == null ? target != null : !TARGET_EDEFAULT.equals(target);
            case XmlDocumentPackage.PROCESSING_INSTRUCTION__PARENT:
                return getParent() != null;
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
        result.append(" (rawText: "); //$NON-NLS-1$
        result.append(rawText);
        result.append(", target: "); //$NON-NLS-1$
        result.append(target);
        result.append(')');
        return result.toString();
    }

} //ProcessingInstructionImpl
