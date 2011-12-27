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
import org.eclipse.xsd.XSDComponent;
import com.metamatrix.metamodels.xml.BuildStatus;
import com.metamatrix.metamodels.xml.ChoiceOption;
import com.metamatrix.metamodels.xml.XmlBaseElement;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlEntityHolder;
import com.metamatrix.metamodels.xml.XmlHolderEntity;
import com.metamatrix.metamodels.xml.XmlNamespace;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Xml Base Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlBaseElementImpl#getChoiceCriteria <em>Choice Criteria</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlBaseElementImpl#getChoiceOrder <em>Choice Order</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlBaseElementImpl#getDefaultFor <em>Default For</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.impl.XmlBaseElementImpl#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public abstract class XmlBaseElementImpl extends XmlDocumentNodeImpl implements XmlBaseElement {
    /**
     * The default value of the '{@link #getChoiceCriteria() <em>Choice Criteria</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getChoiceCriteria()
     * @generated
     * @ordered
     */
    protected static final String CHOICE_CRITERIA_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getChoiceCriteria() <em>Choice Criteria</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getChoiceCriteria()
     * @generated
     * @ordered
     */
    protected String choiceCriteria = CHOICE_CRITERIA_EDEFAULT;

    /**
     * The default value of the '{@link #getChoiceOrder() <em>Choice Order</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getChoiceOrder()
     * @generated
     * @ordered
     */
    protected static final int CHOICE_ORDER_EDEFAULT = -1;

    /**
     * The cached value of the '{@link #getChoiceOrder() <em>Choice Order</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getChoiceOrder()
     * @generated
     * @ordered
     */
    protected int choiceOrder = CHOICE_ORDER_EDEFAULT;

    /**
     * The cached value of the '{@link #getDefaultFor() <em>Default For</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getDefaultFor()
     * @generated
     * @ordered
     */
    protected XmlChoice defaultFor = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected XmlBaseElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlChoice basicGetDefaultFor() {
        return defaultFor;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetDefaultFor( final XmlChoice newDefaultFor,
                                                 NotificationChain msgs ) {
        final XmlChoice oldDefaultFor = defaultFor;
        defaultFor = newDefaultFor;
        if (eNotificationRequired()) {
            final ENotificationImpl notification = new ENotificationImpl(this, Notification.SET,
                                                                         XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR,
                                                                         oldDefaultFor, newDefaultFor);
            if (msgs == null) msgs = notification;
            else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID( final int derivedFeatureID,
                                         final Class baseClass ) {
        if (baseClass == XmlHolderEntity.class) switch (derivedFeatureID) {
            case XmlDocumentPackage.XML_BASE_ELEMENT__PARENT:
                return XmlDocumentPackage.XML_HOLDER_ENTITY__PARENT;
            default:
                return -1;
        }
        if (baseClass == XmlDocumentNode.class) switch (derivedFeatureID) {
            case XmlDocumentPackage.XML_BASE_ELEMENT__BUILD_STATE:
                return XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE;
            case XmlDocumentPackage.XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                return XmlDocumentPackage.XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT;
            case XmlDocumentPackage.XML_BASE_ELEMENT__MAX_OCCURS:
                return XmlDocumentPackage.XML_DOCUMENT_NODE__MAX_OCCURS;
            case XmlDocumentPackage.XML_BASE_ELEMENT__MIN_OCCURS:
                return XmlDocumentPackage.XML_DOCUMENT_NODE__MIN_OCCURS;
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAME:
                return XmlDocumentPackage.XML_DOCUMENT_NODE__NAME;
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAMESPACE:
                return XmlDocumentPackage.XML_DOCUMENT_NODE__NAMESPACE;
            case XmlDocumentPackage.XML_BASE_ELEMENT__XSD_COMPONENT:
                return XmlDocumentPackage.XML_DOCUMENT_NODE__XSD_COMPONENT;
            default:
                return -1;
        }
        if (baseClass == ChoiceOption.class) switch (derivedFeatureID) {
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_CRITERIA:
                return XmlDocumentPackage.CHOICE_OPTION__CHOICE_CRITERIA;
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_ORDER:
                return XmlDocumentPackage.CHOICE_OPTION__CHOICE_ORDER;
            case XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR:
                return XmlDocumentPackage.CHOICE_OPTION__DEFAULT_FOR;
            default:
                return -1;
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer( final NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) switch (eContainerFeatureID) {
            case XmlDocumentPackage.XML_BASE_ELEMENT__PARENT:
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
    public int eDerivedStructuralFeatureID( final int baseFeatureID,
                                            final Class baseClass ) {
        if (baseClass == XmlHolderEntity.class) switch (baseFeatureID) {
            case XmlDocumentPackage.XML_HOLDER_ENTITY__PARENT:
                return XmlDocumentPackage.XML_BASE_ELEMENT__PARENT;
            default:
                return -1;
        }
        if (baseClass == XmlDocumentNode.class) switch (baseFeatureID) {
            case XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE:
                return XmlDocumentPackage.XML_BASE_ELEMENT__BUILD_STATE;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT:
                return XmlDocumentPackage.XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__MAX_OCCURS:
                return XmlDocumentPackage.XML_BASE_ELEMENT__MAX_OCCURS;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__MIN_OCCURS:
                return XmlDocumentPackage.XML_BASE_ELEMENT__MIN_OCCURS;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAME:
                return XmlDocumentPackage.XML_BASE_ELEMENT__NAME;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAMESPACE:
                return XmlDocumentPackage.XML_BASE_ELEMENT__NAMESPACE;
            case XmlDocumentPackage.XML_DOCUMENT_NODE__XSD_COMPONENT:
                return XmlDocumentPackage.XML_BASE_ELEMENT__XSD_COMPONENT;
            default:
                return -1;
        }
        if (baseClass == ChoiceOption.class) switch (baseFeatureID) {
            case XmlDocumentPackage.CHOICE_OPTION__CHOICE_CRITERIA:
                return XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_CRITERIA;
            case XmlDocumentPackage.CHOICE_OPTION__CHOICE_ORDER:
                return XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_ORDER;
            case XmlDocumentPackage.CHOICE_OPTION__DEFAULT_FOR:
                return XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR;
            default:
                return -1;
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
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
            case XmlDocumentPackage.XML_BASE_ELEMENT__BUILD_STATE:
                return getBuildState();
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAME:
                return getName();
            case XmlDocumentPackage.XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                return isExcludeFromDocument() ? Boolean.TRUE : Boolean.FALSE;
            case XmlDocumentPackage.XML_BASE_ELEMENT__MIN_OCCURS:
                return new Integer(getMinOccurs());
            case XmlDocumentPackage.XML_BASE_ELEMENT__MAX_OCCURS:
                return new Integer(getMaxOccurs());
            case XmlDocumentPackage.XML_BASE_ELEMENT__XSD_COMPONENT:
                if (resolve) return getXsdComponent();
                return basicGetXsdComponent();
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAMESPACE:
                if (resolve) return getNamespace();
                return basicGetNamespace();
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_CRITERIA:
                return getChoiceCriteria();
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_ORDER:
                return new Integer(getChoiceOrder());
            case XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR:
                if (resolve) return getDefaultFor();
                return basicGetDefaultFor();
            case XmlDocumentPackage.XML_BASE_ELEMENT__PARENT:
                return getParent();
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
            case XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR:
                if (defaultFor != null) msgs = ((InternalEObject)defaultFor).eInverseRemove(this,
                                                                                            XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION,
                                                                                            XmlChoice.class,
                                                                                            msgs);
                return basicSetDefaultFor((XmlChoice)otherEnd, msgs);
            case XmlDocumentPackage.XML_BASE_ELEMENT__PARENT:
                if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_BASE_ELEMENT__PARENT, msgs);
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
            case XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR:
                return basicSetDefaultFor(null, msgs);
            case XmlDocumentPackage.XML_BASE_ELEMENT__PARENT:
                return eBasicSetContainer(null, XmlDocumentPackage.XML_BASE_ELEMENT__PARENT, msgs);
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
            case XmlDocumentPackage.XML_BASE_ELEMENT__BUILD_STATE:
                return buildState != BUILD_STATE_EDEFAULT;
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlDocumentPackage.XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                return excludeFromDocument != EXCLUDE_FROM_DOCUMENT_EDEFAULT;
            case XmlDocumentPackage.XML_BASE_ELEMENT__MIN_OCCURS:
                return getMinOccurs() != MIN_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_BASE_ELEMENT__MAX_OCCURS:
                return getMaxOccurs() != MAX_OCCURS_EDEFAULT;
            case XmlDocumentPackage.XML_BASE_ELEMENT__XSD_COMPONENT:
                return xsdComponent != null;
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAMESPACE:
                return namespace != null;
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_CRITERIA:
                return CHOICE_CRITERIA_EDEFAULT == null ? choiceCriteria != null : !CHOICE_CRITERIA_EDEFAULT.equals(choiceCriteria);
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_ORDER:
                return choiceOrder != CHOICE_ORDER_EDEFAULT;
            case XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR:
                return defaultFor != null;
            case XmlDocumentPackage.XML_BASE_ELEMENT__PARENT:
                return getParent() != null;
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
            case XmlDocumentPackage.XML_BASE_ELEMENT__BUILD_STATE:
                setBuildState((BuildStatus)newValue);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAME:
                setName((String)newValue);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(((Boolean)newValue).booleanValue());
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__XSD_COMPONENT:
                setXsdComponent((XSDComponent)newValue);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAMESPACE:
                setNamespace((XmlNamespace)newValue);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_CRITERIA:
                setChoiceCriteria((String)newValue);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_ORDER:
                setChoiceOrder(((Integer)newValue).intValue());
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR:
                setDefaultFor((XmlChoice)newValue);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__PARENT:
                setParent((XmlEntityHolder)newValue);
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
        return XmlDocumentPackage.eINSTANCE.getXmlBaseElement();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( final EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_BASE_ELEMENT__BUILD_STATE:
                setBuildState(BUILD_STATE_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__EXCLUDE_FROM_DOCUMENT:
                setExcludeFromDocument(EXCLUDE_FROM_DOCUMENT_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__XSD_COMPONENT:
                setXsdComponent((XSDComponent)null);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__NAMESPACE:
                setNamespace((XmlNamespace)null);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_CRITERIA:
                setChoiceCriteria(CHOICE_CRITERIA_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_ORDER:
                setChoiceOrder(CHOICE_ORDER_EDEFAULT);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR:
                setDefaultFor((XmlChoice)null);
                return;
            case XmlDocumentPackage.XML_BASE_ELEMENT__PARENT:
                setParent((XmlEntityHolder)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getChoiceCriteria() {
        return choiceCriteria;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public int getChoiceOrder() {
        return choiceOrder;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlChoice getDefaultFor() {
        if (defaultFor != null && defaultFor.eIsProxy()) {
            final XmlChoice oldDefaultFor = defaultFor;
            defaultFor = (XmlChoice)eResolveProxy((InternalEObject)defaultFor);
            if (defaultFor != oldDefaultFor) if (eNotificationRequired()) eNotify(new ENotificationImpl(
                                                                                                        this,
                                                                                                        Notification.RESOLVE,
                                                                                                        XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR,
                                                                                                        oldDefaultFor, defaultFor));
        }
        return defaultFor;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlEntityHolder getParent() {
        if (eContainerFeatureID != XmlDocumentPackage.XML_BASE_ELEMENT__PARENT) return null;
        return (XmlEntityHolder)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setChoiceCriteria( final String newChoiceCriteria ) {
        final String oldChoiceCriteria = choiceCriteria;
        choiceCriteria = newChoiceCriteria;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_CRITERIA,
                                                                   oldChoiceCriteria, choiceCriteria));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setChoiceOrder( final int newChoiceOrder ) {
        final int oldChoiceOrder = choiceOrder;
        choiceOrder = newChoiceOrder;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   XmlDocumentPackage.XML_BASE_ELEMENT__CHOICE_ORDER,
                                                                   oldChoiceOrder, choiceOrder));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setDefaultFor( final XmlChoice newDefaultFor ) {
        if (newDefaultFor != defaultFor) {
            NotificationChain msgs = null;
            if (defaultFor != null) msgs = ((InternalEObject)defaultFor).eInverseRemove(this,
                                                                                        XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION,
                                                                                        XmlChoice.class,
                                                                                        msgs);
            if (newDefaultFor != null) msgs = ((InternalEObject)newDefaultFor).eInverseAdd(this,
                                                                                           XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION,
                                                                                           XmlChoice.class,
                                                                                           msgs);
            msgs = basicSetDefaultFor(newDefaultFor, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          XmlDocumentPackage.XML_BASE_ELEMENT__DEFAULT_FOR,
                                                                          newDefaultFor, newDefaultFor));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setParent( final XmlEntityHolder newParent ) {
        if (newParent != eContainer || (eContainerFeatureID != XmlDocumentPackage.XML_BASE_ELEMENT__PARENT && newParent != null)) {
            if (EcoreUtil.isAncestor(this, newParent)) throw new IllegalArgumentException(
                                                                                          "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newParent != null) msgs = ((InternalEObject)newParent).eInverseAdd(this,
                                                                                   XmlDocumentPackage.XML_ENTITY_HOLDER__ENTITIES,
                                                                                   XmlEntityHolder.class,
                                                                                   msgs);
            msgs = eBasicSetContainer((InternalEObject)newParent, XmlDocumentPackage.XML_BASE_ELEMENT__PARENT, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          XmlDocumentPackage.XML_BASE_ELEMENT__PARENT, newParent,
                                                                          newParent));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        final StringBuffer result = new StringBuffer(super.toString());
        result.append(" (choiceCriteria: "); //$NON-NLS-1$
        result.append(choiceCriteria);
        result.append(", choiceOrder: "); //$NON-NLS-1$
        result.append(choiceOrder);
        result.append(')');
        return result.toString();
    }

} // XmlBaseElementImpl
