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
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComponent;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.ValueType;
import com.metamatrix.metamodels.xml.XmlAttribute;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlNamespace;
import com.metamatrix.metamodels.xml.XmlValueHolder;
import com.metamatrix.metamodels.xsd.XsdUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlAttributeImpl#getValue <em>Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlAttributeImpl#getValueType <em>Value Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlAttributeImpl#getUse <em>Use</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.impl.XmlAttributeImpl#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XmlAttributeImpl extends XmlDocumentNodeImpl implements XmlAttribute {
    /**
     * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected static final String VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
    protected String value = VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getValueType() <em>Value Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValueType()
     * @generated
     * @ordered
     */
    protected static final ValueType VALUE_TYPE_EDEFAULT = ValueType.IGNORED_LITERAL;

    /**
     * The cached value of the '{@link #getValueType() <em>Value Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getValueType()
     * @generated
     * @ordered
     */
    protected ValueType valueType = VALUE_TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getUse() <em>Use</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUse()
     * @generated
     * @ordered
     */
    protected static final XSDAttributeUseCategory USE_EDEFAULT = XSDAttributeUseCategory.OPTIONAL_LITERAL;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlAttributeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlDocumentPackage.eINSTANCE.getXmlAttribute();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValue(String newValue) {
        String oldValue = value;
        value = newValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_ATTRIBUTE__VALUE, oldValue, value));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ValueType getValueType() {
        return valueType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setValueType(ValueType newValueType) {
        ValueType oldValueType = valueType;
        valueType = newValueType == null ? VALUE_TYPE_EDEFAULT : newValueType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_ATTRIBUTE__VALUE_TYPE, oldValueType, valueType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public XSDAttributeUseCategory getUse() {
        final XSDComponent schemaComp = getXsdComponent();
        if (schemaComp != null) {
            return XsdUtil.getUse(schemaComp);
        }
        return USE_EDEFAULT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XSDAttributeUseCategory getUseGen() {
        // TODO: implement this method to return the 'Use' attribute
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlElement getElement() {
        if (eContainerFeatureID != XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT) return null;
        return (XmlElement)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setElement(XmlElement newElement) {
        if (newElement != eContainer || (eContainerFeatureID != XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT && newElement != null)) {
            if (EcoreUtil.isAncestor(this, newElement))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newElement != null)
                msgs = ((InternalEObject)newElement).eInverseAdd(this, XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES, XmlElement.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newElement, XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT, newElement, newElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isValueFixed() {
        final ValueType valueType = getValueType();
        return ValueType.FIXED == valueType.getValue();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isValueFixedGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isValueDefault() {
        final ValueType valueType = getValueType();
        return ValueType.DEFAULT == valueType.getValue();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isValueDefaultGen() {
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
                case XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT, msgs);
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
                case XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT:
                    return eBasicSetContainer(null, XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT, msgs);
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
                case XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT:
                    return eContainer.eInverseRemove(this, XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES, XmlElement.class, msgs);
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
            case XmlDocumentPackage.XML_ATTRIBUTE__BUILD_STATE:
                return getBuildState();
            case XmlDocumentPackage.XML_ATTRIBUTE__NAME:
                return getName();
            case XmlDocumentPackage.XML_ATTRIBUTE__EXCLUDE_FROM_DOCUMENT:
                return isExcludeFromDocument() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_ATTRIBUTE__MIN_OCCURS:
                return new Integer(getMinOccurs());
            case XmlDocumentPackage.XML_ATTRIBUTE__MAX_OCCURS:
                return new Integer(getMaxOccurs());
            case XmlDocumentPackage.XML_ATTRIBUTE__XSD_COMPONENT:
                if (resolve) return getXsdComponent();
                return basicGetXsdComponent();
            case XmlDocumentPackage.XML_ATTRIBUTE__NAMESPACE:
                if (resolve) return getNamespace();
                return basicGetNamespace();
            case XmlDocumentPackage.XML_ATTRIBUTE__VALUE:
                return getValue();
            case XmlDocumentPackage.XML_ATTRIBUTE__VALUE_TYPE:
                return getValueType();
            case XmlDocumentPackage.XML_ATTRIBUTE__USE:
                return getUse();
            case XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT:
                return getElement();
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
            case XmlDocumentPackage.XML_ATTRIBUTE__BUILD_STATE:
                setBuildState((BuildStatus)newValue);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__NAME:
                setName((String)newValue);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)newValue);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__NAMESPACE:
                setNamespace((XmlNamespace)newValue);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__VALUE:
                setValue((String)newValue);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__VALUE_TYPE:
                setValueType((ValueType)newValue);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT:
                setElement((XmlElement)newValue);
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
            case XmlDocumentPackage.XML_ATTRIBUTE__BUILD_STATE:
                setBuildState(BUILD_STATE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(EXCLUDE_FROM_DOCUMENT_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__XSD_COMPONENT:
                setXsdComponent((XSDComponent)null);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__NAMESPACE:
                setNamespace((XmlNamespace)null);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__VALUE:
                setValue(VALUE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__VALUE_TYPE:
                setValueType(VALUE_TYPE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT:
                setElement((XmlElement)null);
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
            case XmlDocumentPackage.XML_ATTRIBUTE__BUILD_STATE:
                return buildState != BUILD_STATE_EDEFAULT;
            case XmlDocumentPackage.XML_ATTRIBUTE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlDocumentPackage.XML_ATTRIBUTE__EXCLUDE_FROM_DOCUMENT:
                return excludeFromDocument != EXCLUDE_FROM_DOCUMENT_EDEFAULT;
            case XmlDocumentPackage.XML_ATTRIBUTE__MIN_OCCURS:
                return getMinOccurs() != MIN_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_ATTRIBUTE__MAX_OCCURS:
                return getMaxOccurs() != MAX_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_ATTRIBUTE__XSD_COMPONENT:
                return xsdComponent != null;
            case XmlDocumentPackage.XML_ATTRIBUTE__NAMESPACE:
                return namespace != null;
            case XmlDocumentPackage.XML_ATTRIBUTE__VALUE:
                return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
            case XmlDocumentPackage.XML_ATTRIBUTE__VALUE_TYPE:
                return valueType != VALUE_TYPE_EDEFAULT;
            case XmlDocumentPackage.XML_ATTRIBUTE__USE:
                return getUse() != USE_EDEFAULT;
            case XmlDocumentPackage.XML_ATTRIBUTE__ELEMENT:
                return getElement() != null;
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
        if (baseClass == XmlValueHolder.class) {
            switch (derivedFeatureID) {
                case XmlDocumentPackage.XML_ATTRIBUTE__VALUE: return XmlDocumentPackage.XML_VALUE_HOLDER__VALUE;
                case XmlDocumentPackage.XML_ATTRIBUTE__VALUE_TYPE: return XmlDocumentPackage.XML_VALUE_HOLDER__VALUE_TYPE;
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
        if (baseClass == XmlValueHolder.class) {
            switch (baseFeatureID) {
                case XmlDocumentPackage.XML_VALUE_HOLDER__VALUE: return XmlDocumentPackage.XML_ATTRIBUTE__VALUE;
                case XmlDocumentPackage.XML_VALUE_HOLDER__VALUE_TYPE: return XmlDocumentPackage.XML_ATTRIBUTE__VALUE_TYPE;
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
        result.append(" (value: "); //$NON-NLS-1$
        result.append(value);
        result.append(", valueType: "); //$NON-NLS-1$
        result.append(valueType);
        result.append(')');
        return result.toString();
    }

} //XmlAttributeImpl
