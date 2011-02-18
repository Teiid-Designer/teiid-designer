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
import org.eclipse.xsd.XSDComponent;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlEntityHolder;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlFragmentUse;
import com.metamatrix.metamodels.xml.XmlNamespace;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Xml Fragment Use</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlFragmentUseImpl#getFragment <em>Fragment</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class XmlFragmentUseImpl extends XmlBaseElementImpl implements XmlFragmentUse {
    /**
     * The cached value of the '{@link #getFragment() <em>Fragment</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getFragment()
     * @generated
     * @ordered
     */
    protected XmlFragment fragment = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected XmlFragmentUseImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlFragment basicGetFragment() {
        return fragment;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer( final NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) switch (eContainerFeatureID) {
            case XmlDocumentPackage.XML_FRAGMENT_USE__PARENT:
                return eContainer.eInverseRemove(this, XmlDocumentPackage.XML_ENTITY_HOLDER__ENTITIES, XmlEntityHolder.class, msgs);
            default:
                return eDynamicBasicRemoveFromContainer(msgs);
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet( final EStructuralFeature eFeature,
                        final boolean resolve ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_FRAGMENT_USE__BUILD_STATE:
                return getBuildState();
            case XmlDocumentPackage.XML_FRAGMENT_USE__NAME:
                return getName();
            case XmlDocumentPackage.XML_FRAGMENT_USE__EXCLUDE_FROM_DOCUMENT:
                return isExcludeFromDocument() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_FRAGMENT_USE__MIN_OCCURS:
                return new Integer(getMinOccurs());
            case XmlDocumentPackage.XML_FRAGMENT_USE__MAX_OCCURS:
                return new Integer(getMaxOccurs());
            case XmlDocumentPackage.XML_FRAGMENT_USE__XSD_COMPONENT:
                if (resolve) return getXsdComponent();
                return basicGetXsdComponent();
            case XmlDocumentPackage.XML_FRAGMENT_USE__NAMESPACE:
                if (resolve) return getNamespace();
                return basicGetNamespace();
            case XmlDocumentPackage.XML_FRAGMENT_USE__CHOICE_CRITERIA:
                return getChoiceCriteria();
            case XmlDocumentPackage.XML_FRAGMENT_USE__CHOICE_ORDER:
                return new Integer(getChoiceOrder());
            case XmlDocumentPackage.XML_FRAGMENT_USE__DEFAULT_FOR:
                if (resolve) return getDefaultFor();
                return basicGetDefaultFor();
            case XmlDocumentPackage.XML_FRAGMENT_USE__PARENT:
                return getParent();
            case XmlDocumentPackage.XML_FRAGMENT_USE__FRAGMENT:
                if (resolve) return getFragment();
                return basicGetFragment();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd( final InternalEObject otherEnd,
                                          final int featureID,
                                          final Class baseClass,
                                          NotificationChain msgs ) {
        if (featureID >= 0) switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
            case XmlDocumentPackage.XML_FRAGMENT_USE__DEFAULT_FOR:
                if (defaultFor != null) msgs = ((InternalEObject)defaultFor).eInverseRemove(this,
                                                                                            XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION,
                                                                                            XmlChoice.class,
                                                                                            msgs);
                return basicSetDefaultFor((XmlChoice)otherEnd, msgs);
            case XmlDocumentPackage.XML_FRAGMENT_USE__PARENT:
                if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_FRAGMENT_USE__PARENT, msgs);
            default:
                return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
        }
        if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove( final InternalEObject otherEnd,
                                             final int featureID,
                                             final Class baseClass,
                                             final NotificationChain msgs ) {
        if (featureID >= 0) switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
            case XmlDocumentPackage.XML_FRAGMENT_USE__DEFAULT_FOR:
                return basicSetDefaultFor(null, msgs);
            case XmlDocumentPackage.XML_FRAGMENT_USE__PARENT:
                return eBasicSetContainer(null, XmlDocumentPackage.XML_FRAGMENT_USE__PARENT, msgs);
            default:
                return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet( final EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_FRAGMENT_USE__BUILD_STATE:
                return buildState != BUILD_STATE_EDEFAULT;
            case XmlDocumentPackage.XML_FRAGMENT_USE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlDocumentPackage.XML_FRAGMENT_USE__EXCLUDE_FROM_DOCUMENT:
                return excludeFromDocument != EXCLUDE_FROM_DOCUMENT_EDEFAULT;
            case XmlDocumentPackage.XML_FRAGMENT_USE__MIN_OCCURS:
                return getMinOccurs() != MIN_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_FRAGMENT_USE__MAX_OCCURS:
                return getMaxOccurs() != MAX_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_FRAGMENT_USE__XSD_COMPONENT:
                return xsdComponent != null;
            case XmlDocumentPackage.XML_FRAGMENT_USE__NAMESPACE:
                return namespace != null;
            case XmlDocumentPackage.XML_FRAGMENT_USE__CHOICE_CRITERIA:
                return CHOICE_CRITERIA_EDEFAULT == null ? choiceCriteria != null : !CHOICE_CRITERIA_EDEFAULT.equals(choiceCriteria);
            case XmlDocumentPackage.XML_FRAGMENT_USE__CHOICE_ORDER:
                return choiceOrder != CHOICE_ORDER_EDEFAULT;
            case XmlDocumentPackage.XML_FRAGMENT_USE__DEFAULT_FOR:
                return defaultFor != null;
            case XmlDocumentPackage.XML_FRAGMENT_USE__PARENT:
                return getParent() != null;
            case XmlDocumentPackage.XML_FRAGMENT_USE__FRAGMENT:
                return fragment != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet( final EStructuralFeature eFeature,
                      final Object newValue ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_FRAGMENT_USE__BUILD_STATE:
                setBuildState((BuildStatus)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__NAME:
                setName((String)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__NAMESPACE:
                setNamespace((XmlNamespace)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__CHOICE_CRITERIA:
                setChoiceCriteria((String)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__CHOICE_ORDER:
                setChoiceOrder(((Integer)newValue).intValue());
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__DEFAULT_FOR:
                setDefaultFor((XmlChoice)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__PARENT:
                setParent((XmlEntityHolder)newValue);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__FRAGMENT:
                setFragment((XmlFragment)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlFragmentUse();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( final EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_FRAGMENT_USE__BUILD_STATE:
                setBuildState(BUILD_STATE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(EXCLUDE_FROM_DOCUMENT_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)null);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__NAMESPACE:
                setNamespace((XmlNamespace)null);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__CHOICE_CRITERIA:
                setChoiceCriteria(CHOICE_CRITERIA_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__CHOICE_ORDER:
                setChoiceOrder(CHOICE_ORDER_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__DEFAULT_FOR:
                setDefaultFor((XmlChoice)null);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__PARENT:
                setParent((XmlEntityHolder)null);
                return;
            case XmlDocumentPackage.XML_FRAGMENT_USE__FRAGMENT:
                setFragment((XmlFragment)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlFragment getFragment() {
        if (fragment != null && fragment.eIsProxy()) {
            final XmlFragment oldFragment = fragment;
            fragment = (XmlFragment)eResolveProxy((InternalEObject)fragment);
            if (fragment != oldFragment) if (eNotificationRequired()) eNotify(new ENotificationImpl(
                                                                                                    this,
                                                                                                    Notification.RESOLVE,
                                                                                                    XmlDocumentPackage.XML_FRAGMENT_USE__FRAGMENT,
                                                                                                    oldFragment, fragment));
        }
        return fragment;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setFragment( final XmlFragment newFragment ) {
        final XmlFragment oldFragment = fragment;
        fragment = newFragment;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   XmlDocumentPackage.XML_FRAGMENT_USE__FRAGMENT, oldFragment,
                                                                   fragment));
    }

} // XmlFragmentUseImpl
