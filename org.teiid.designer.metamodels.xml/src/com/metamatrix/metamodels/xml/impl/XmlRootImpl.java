/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.xsd.XSDComponent;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.ValueType;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElementHolder;
import com.metamatrix.metamodels.xml.XmlFragment;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlRoot;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlRootImpl#getFragment <em>Fragment</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XmlRootImpl extends XmlElementImpl implements XmlRoot {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlRootImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlRoot();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlFragment getFragment() {
        if (eContainerFeatureID != XmlDocumentPackage.XML_ROOT__FRAGMENT) return null;
        return (XmlFragment)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFragment(XmlFragment newFragment) {
        if (newFragment != eContainer || (eContainerFeatureID != XmlDocumentPackage.XML_ROOT__FRAGMENT && newFragment != null)) {
            if (EcoreUtil.isAncestor(this, newFragment))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newFragment != null)
                msgs = ((InternalEObject)newFragment).eInverseAdd(this, XmlDocumentPackage.XML_FRAGMENT__ROOT, XmlFragment.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newFragment, XmlDocumentPackage.XML_ROOT__FRAGMENT, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_ROOT__FRAGMENT, newFragment, newFragment));
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
                case XmlDocumentPackage.XML_ROOT__DEFAULT_FOR:
                    if (defaultFor != null)
                        msgs = ((InternalEObject)defaultFor).eInverseRemove(this, XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION, XmlChoice.class, msgs);
                    return basicSetDefaultFor((XmlChoice)otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__PARENT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_ROOT__PARENT, msgs);
                case XmlDocumentPackage.XML_ROOT__COMMENTS:
                    return ((InternalEList)getComments()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__PROCESSING_INSTRUCTIONS:
                    return ((InternalEList)getProcessingInstructions()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__ELEMENTS:
                    return ((InternalEList)getElements()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__CONTAINERS:
                    return ((InternalEList)getContainers()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__ATTRIBUTES:
                    return ((InternalEList)getAttributes()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicAdd(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__FRAGMENT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_ROOT__FRAGMENT, msgs);
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
                case XmlDocumentPackage.XML_ROOT__DEFAULT_FOR:
                    return basicSetDefaultFor(null, msgs);
                case XmlDocumentPackage.XML_ROOT__PARENT:
                    return eBasicSetContainer(null, XmlDocumentPackage.XML_ROOT__PARENT, msgs);
                case XmlDocumentPackage.XML_ROOT__COMMENTS:
                    return ((InternalEList)getComments()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__PROCESSING_INSTRUCTIONS:
                    return ((InternalEList)getProcessingInstructions()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__ELEMENTS:
                    return ((InternalEList)getElements()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__CONTAINERS:
                    return ((InternalEList)getContainers()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__ATTRIBUTES:
                    return ((InternalEList)getAttributes()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__DECLARED_NAMESPACES:
                    return ((InternalEList)getDeclaredNamespaces()).basicRemove(otherEnd, msgs);
                case XmlDocumentPackage.XML_ROOT__FRAGMENT:
                    return eBasicSetContainer(null, XmlDocumentPackage.XML_ROOT__FRAGMENT, msgs);
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
                case XmlDocumentPackage.XML_ROOT__PARENT:
                    return eContainer.eInverseRemove(this, XmlDocumentPackage.XML_ELEMENT_HOLDER__ELEMENTS, XmlElementHolder.class, msgs);
                case XmlDocumentPackage.XML_ROOT__FRAGMENT:
                    return eContainer.eInverseRemove(this, XmlDocumentPackage.XML_FRAGMENT__ROOT, XmlFragment.class, msgs);
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
            case XmlDocumentPackage.XML_ROOT__BUILD_STATE:
                return getBuildState();
            case XmlDocumentPackage.XML_ROOT__NAME:
                return getName();
            case XmlDocumentPackage.XML_ROOT__EXCLUDE_FROM_DOCUMENT:
                return isExcludeFromDocument() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_ROOT__MIN_OCCURS:
                return new Integer(getMinOccurs());
            case XmlDocumentPackage.XML_ROOT__MAX_OCCURS:
                return new Integer(getMaxOccurs());
            case XmlDocumentPackage.XML_ROOT__XSD_COMPONENT:
                if (resolve) return getXsdComponent();
                return basicGetXsdComponent();
            case XmlDocumentPackage.XML_ROOT__NAMESPACE:
                if (resolve) return getNamespace();
                return basicGetNamespace();
            case XmlDocumentPackage.XML_ROOT__CHOICE_CRITERIA:
                return getChoiceCriteria();
            case XmlDocumentPackage.XML_ROOT__CHOICE_ORDER:
                return new Integer(getChoiceOrder());
            case XmlDocumentPackage.XML_ROOT__DEFAULT_FOR:
                if (resolve) return getDefaultFor();
                return basicGetDefaultFor();
            case XmlDocumentPackage.XML_ROOT__PARENT:
                return getParent();
            case XmlDocumentPackage.XML_ROOT__COMMENTS:
                return getComments();
            case XmlDocumentPackage.XML_ROOT__PROCESSING_INSTRUCTIONS:
                return getProcessingInstructions();
            case XmlDocumentPackage.XML_ROOT__ELEMENTS:
                return getElements();
            case XmlDocumentPackage.XML_ROOT__CONTAINERS:
                return getContainers();
            case XmlDocumentPackage.XML_ROOT__VALUE:
                return getValue();
            case XmlDocumentPackage.XML_ROOT__VALUE_TYPE:
                return getValueType();
            case XmlDocumentPackage.XML_ROOT__RECURSIVE:
                return isRecursive() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_ROOT__ATTRIBUTES:
                return getAttributes();
            case XmlDocumentPackage.XML_ROOT__DECLARED_NAMESPACES:
                return getDeclaredNamespaces();
            case XmlDocumentPackage.XML_ROOT__FRAGMENT:
                return getFragment();
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
            case XmlDocumentPackage.XML_ROOT__BUILD_STATE:
                setBuildState((BuildStatus)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__NAME:
                setName((String)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_ROOT__XSD_COMPONENT:
                setXsdComponent((XSDComponent)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__NAMESPACE:
                setNamespace((XmlNamespace)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__CHOICE_CRITERIA:
                setChoiceCriteria((String)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__CHOICE_ORDER:
                setChoiceOrder(((Integer)newValue).intValue());
                return;
            case XmlDocumentPackage.XML_ROOT__DEFAULT_FOR:
                setDefaultFor((XmlChoice)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__PARENT:
                setParent((XmlElementHolder)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__COMMENTS:
                getComments().clear();
                getComments().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__PROCESSING_INSTRUCTIONS:
                getProcessingInstructions().clear();
                getProcessingInstructions().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__ELEMENTS:
                getElements().clear();
                getElements().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__CONTAINERS:
                getContainers().clear();
                getContainers().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__VALUE:
                setValue((String)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__VALUE_TYPE:
                setValueType((ValueType)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__RECURSIVE:
                setRecursive(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_ROOT__ATTRIBUTES:
                getAttributes().clear();
                getAttributes().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                getDeclaredNamespaces().addAll((Collection)newValue);
                return;
            case XmlDocumentPackage.XML_ROOT__FRAGMENT:
                setFragment((XmlFragment)newValue);
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
            case XmlDocumentPackage.XML_ROOT__BUILD_STATE:
                setBuildState(BUILD_STATE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ROOT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ROOT__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(EXCLUDE_FROM_DOCUMENT_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ROOT__XSD_COMPONENT:
                setXsdComponent((XSDComponent)null);
                return;
            case XmlDocumentPackage.XML_ROOT__NAMESPACE:
                setNamespace((XmlNamespace)null);
                return;
            case XmlDocumentPackage.XML_ROOT__CHOICE_CRITERIA:
                setChoiceCriteria(CHOICE_CRITERIA_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ROOT__CHOICE_ORDER:
                setChoiceOrder(CHOICE_ORDER_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ROOT__DEFAULT_FOR:
                setDefaultFor((XmlChoice)null);
                return;
            case XmlDocumentPackage.XML_ROOT__PARENT:
                setParent((XmlElementHolder)null);
                return;
            case XmlDocumentPackage.XML_ROOT__COMMENTS:
                getComments().clear();
                return;
            case XmlDocumentPackage.XML_ROOT__PROCESSING_INSTRUCTIONS:
                getProcessingInstructions().clear();
                return;
            case XmlDocumentPackage.XML_ROOT__ELEMENTS:
                getElements().clear();
                return;
            case XmlDocumentPackage.XML_ROOT__CONTAINERS:
                getContainers().clear();
                return;
            case XmlDocumentPackage.XML_ROOT__VALUE:
                setValue(VALUE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ROOT__VALUE_TYPE:
                setValueType(VALUE_TYPE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ROOT__RECURSIVE:
                setRecursive(RECURSIVE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ROOT__ATTRIBUTES:
                getAttributes().clear();
                return;
            case XmlDocumentPackage.XML_ROOT__DECLARED_NAMESPACES:
                getDeclaredNamespaces().clear();
                return;
            case XmlDocumentPackage.XML_ROOT__FRAGMENT:
                setFragment((XmlFragment)null);
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
            case XmlDocumentPackage.XML_ROOT__BUILD_STATE:
                return buildState != BUILD_STATE_EDEFAULT;
            case XmlDocumentPackage.XML_ROOT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlDocumentPackage.XML_ROOT__EXCLUDE_FROM_DOCUMENT:
                return excludeFromDocument != EXCLUDE_FROM_DOCUMENT_EDEFAULT;
            case XmlDocumentPackage.XML_ROOT__MIN_OCCURS:
                return getMinOccurs() != MIN_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_ROOT__MAX_OCCURS:
                return getMaxOccurs() != MAX_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_ROOT__XSD_COMPONENT:
                return xsdComponent != null;
            case XmlDocumentPackage.XML_ROOT__NAMESPACE:
                return namespace != null;
            case XmlDocumentPackage.XML_ROOT__CHOICE_CRITERIA:
                return CHOICE_CRITERIA_EDEFAULT == null ? choiceCriteria != null : !CHOICE_CRITERIA_EDEFAULT.equals(choiceCriteria);
            case XmlDocumentPackage.XML_ROOT__CHOICE_ORDER:
                return choiceOrder != CHOICE_ORDER_EDEFAULT;
            case XmlDocumentPackage.XML_ROOT__DEFAULT_FOR:
                return defaultFor != null;
            case XmlDocumentPackage.XML_ROOT__PARENT:
                return getParent() != null;
            case XmlDocumentPackage.XML_ROOT__COMMENTS:
                return comments != null && !comments.isEmpty();
            case XmlDocumentPackage.XML_ROOT__PROCESSING_INSTRUCTIONS:
                return processingInstructions != null && !processingInstructions.isEmpty();
            case XmlDocumentPackage.XML_ROOT__ELEMENTS:
                return elements != null && !elements.isEmpty();
            case XmlDocumentPackage.XML_ROOT__CONTAINERS:
                return containers != null && !containers.isEmpty();
            case XmlDocumentPackage.XML_ROOT__VALUE:
                return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
            case XmlDocumentPackage.XML_ROOT__VALUE_TYPE:
                return valueType != VALUE_TYPE_EDEFAULT;
            case XmlDocumentPackage.XML_ROOT__RECURSIVE:
                return recursive != RECURSIVE_EDEFAULT;
            case XmlDocumentPackage.XML_ROOT__ATTRIBUTES:
                return attributes != null && !attributes.isEmpty();
            case XmlDocumentPackage.XML_ROOT__DECLARED_NAMESPACES:
                return declaredNamespaces != null && !declaredNamespaces.isEmpty();
            case XmlDocumentPackage.XML_ROOT__FRAGMENT:
                return getFragment() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /** 
     * Overridden to ensure that the minOccurs on a Root element is always 1 or more.
     * @see com.metamatrix.metamodels.xml.XmlDocumentNode#getMinOccurs()
     * @since 4.2
     */
    @Override
    public int getMinOccurs() {
        final int genericMinOccurs = super.getMinOccurs();
        if ( genericMinOccurs < 1 ) {
            return 1;
        }
        return genericMinOccurs;
    }

} //XmlRootImpl
