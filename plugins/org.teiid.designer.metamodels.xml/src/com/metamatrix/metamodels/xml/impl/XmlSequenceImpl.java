/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.xsd.XSDComponent;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlContainerHolder;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlSequence;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Sequence</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class XmlSequenceImpl extends XmlContainerNodeImpl implements XmlSequence {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlSequenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlSequence();
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
                case XmlDocumentPackage.XML_SEQUENCE__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_SEQUENCE__CONTAINERS:
                    return ((InternalEList)getContainers()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_SEQUENCE__DEFAULT_FOR:
                    if (defaultFor != null)
                        msgs = ((InternalEObject)defaultFor).eInverseRemove(this, XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION, XmlChoice.class, msgs);
                    return basicSetDefaultFor((XmlChoice)otherEnd, msgs);
                case XmlDocumentPackage.XML_SEQUENCE__PARENT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_SEQUENCE__PARENT, msgs);
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
                case XmlDocumentPackage.XML_SEQUENCE__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_SEQUENCE__CONTAINERS:
                    return ((InternalEList)getContainers()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_SEQUENCE__DEFAULT_FOR:
                    return basicSetDefaultFor(null, msgs);
                case XmlDocumentPackage.XML_SEQUENCE__PARENT:
                    return eBasicSetContainer(null, XmlDocumentPackage.XML_SEQUENCE__PARENT, msgs);
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
                case XmlDocumentPackage.XML_SEQUENCE__PARENT:
                    return eContainer.eInverseRemove(this, XmlDocumentPackage.XML_CONTAINER_HOLDER__CONTAINERS, XmlContainerHolder.class, msgs);
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
            case XmlDocumentPackage.XML_SEQUENCE__ELEMENTS:
                return getElements();
            case XmlDocumentPackage.XML_SEQUENCE__CONTAINERS:
                return getContainers();
            case XmlDocumentPackage.XML_SEQUENCE__CHOICE_CRITERIA:
                return getChoiceCriteria();
            case XmlDocumentPackage.XML_SEQUENCE__CHOICE_ORDER:
                return new Integer(getChoiceOrder());
            case XmlDocumentPackage.XML_SEQUENCE__DEFAULT_FOR:
                if (resolve) return getDefaultFor();
                return basicGetDefaultFor();
            case XmlDocumentPackage.XML_SEQUENCE__BUILD_STATE:
                return getBuildState();
            case XmlDocumentPackage.XML_SEQUENCE__EXCLUDE_FROM_DOCUMENT:
                return isExcludeFromDocument() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_SEQUENCE__MIN_OCCURS:
                return new Integer(getMinOccurs());
            case XmlDocumentPackage.XML_SEQUENCE__MAX_OCCURS:
                return new Integer(getMaxOccurs());
            case XmlDocumentPackage.XML_SEQUENCE__XSD_COMPONENT:
                if (resolve) return getXsdComponent();
                return basicGetXsdComponent();
            case XmlDocumentPackage.XML_SEQUENCE__PARENT:
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
            case XmlDocumentPackage.XML_SEQUENCE__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__CONTAINERS:
                getContainers().clear();
                getContainers().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__CHOICE_CRITERIA:
                setChoiceCriteria((String)newValue);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__CHOICE_ORDER:
                setChoiceOrder(((Integer)newValue).intValue());
                return;
            case XmlDocumentPackage.XML_SEQUENCE__DEFAULT_FOR:
                setDefaultFor((XmlChoice)newValue);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__BUILD_STATE:
                setBuildState((BuildStatus)newValue);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_SEQUENCE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)newValue);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__PARENT:
                setParent((XmlContainerHolder)newValue);
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
            case XmlDocumentPackage.XML_SEQUENCE__ELEMENTS:
                getElements().clear();
                return;
            case XmlDocumentPackage.XML_SEQUENCE__CONTAINERS:
                getContainers().clear();
                return;
            case XmlDocumentPackage.XML_SEQUENCE__CHOICE_CRITERIA:
                setChoiceCriteria(CHOICE_CRITERIA_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__CHOICE_ORDER:
                setChoiceOrder(CHOICE_ORDER_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__DEFAULT_FOR:
                setDefaultFor((XmlChoice)null);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__BUILD_STATE:
                setBuildState(BUILD_STATE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(EXCLUDE_FROM_DOCUMENT_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)null);
                return;
            case XmlDocumentPackage.XML_SEQUENCE__PARENT:
                setParent((XmlContainerHolder)null);
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
            case XmlDocumentPackage.XML_SEQUENCE__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case XmlDocumentPackage.XML_SEQUENCE__CONTAINERS:
                return containers != null && !containers.isEmpty();
            case XmlDocumentPackage.XML_SEQUENCE__CHOICE_CRITERIA:
                return CHOICE_CRITERIA_EDEFAULT == null ? choiceCriteria != null : !CHOICE_CRITERIA_EDEFAULT.equals(choiceCriteria);
            case XmlDocumentPackage.XML_SEQUENCE__CHOICE_ORDER:
                return choiceOrder != CHOICE_ORDER_EDEFAULT;
            case XmlDocumentPackage.XML_SEQUENCE__DEFAULT_FOR:
                return defaultFor != null;
            case XmlDocumentPackage.XML_SEQUENCE__BUILD_STATE:
                return buildState != BUILD_STATE_EDEFAULT;
            case XmlDocumentPackage.XML_SEQUENCE__EXCLUDE_FROM_DOCUMENT:
                return excludeFromDocument != EXCLUDE_FROM_DOCUMENT_EDEFAULT;
            case XmlDocumentPackage.XML_SEQUENCE__MIN_OCCURS:
                return getMinOccurs() != MIN_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_SEQUENCE__MAX_OCCURS:
                return getMaxOccurs() != MAX_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_SEQUENCE__XSD_COMPONENT:
                return xsdComponent != null;
            case XmlDocumentPackage.XML_SEQUENCE__PARENT:
                return getParent() != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //XmlSequenceImpl
