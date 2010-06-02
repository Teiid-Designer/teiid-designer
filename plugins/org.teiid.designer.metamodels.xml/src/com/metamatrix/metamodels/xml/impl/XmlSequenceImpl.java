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
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlEntityHolder;
import com.metamatrix.metamodels.xml.XmlSequence;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Xml Sequence</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class XmlSequenceImpl extends XmlContainerNodeImpl implements XmlSequence {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected XmlSequenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer( final NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) switch (eContainerFeatureID) {
            case XmlDocumentPackage.XML_SEQUENCE__PARENT:
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
            case XmlDocumentPackage.XML_SEQUENCE__ENTITIES:
                return getEntities();
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
            case XmlDocumentPackage.XML_SEQUENCE__ENTITIES:
                return ((InternalEList)getEntities()).basicAdd(otherEnd, msgs);
            case XmlDocumentPackage.XML_SEQUENCE__DEFAULT_FOR:
                if (defaultFor != null) msgs = ((InternalEObject)defaultFor).eInverseRemove(this,
                                                                                            XmlDocumentPackage.XML_CHOICE__DEFAULT_OPTION,
                                                                                            XmlChoice.class,
                                                                                            msgs);
                return basicSetDefaultFor((XmlChoice)otherEnd, msgs);
            case XmlDocumentPackage.XML_SEQUENCE__PARENT:
                if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                return eBasicSetContainer(otherEnd, XmlDocumentPackage.XML_SEQUENCE__PARENT, msgs);
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
            case XmlDocumentPackage.XML_SEQUENCE__ENTITIES:
                return ((InternalEList)getEntities()).basicRemove(otherEnd, msgs);
            case XmlDocumentPackage.XML_SEQUENCE__DEFAULT_FOR:
                return basicSetDefaultFor(null, msgs);
            case XmlDocumentPackage.XML_SEQUENCE__PARENT:
                return eBasicSetContainer(null, XmlDocumentPackage.XML_SEQUENCE__PARENT, msgs);
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
            case XmlDocumentPackage.XML_SEQUENCE__ENTITIES:
                return entities != null && !entities.isEmpty();
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

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet( final EStructuralFeature eFeature,
                      final Object newValue ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_SEQUENCE__ENTITIES:
                getEntities().clear();
                getEntities().addAll((Collection)newValue);
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
        return XmlDocumentPackage.eINSTANCE.getXmlSequence();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( final EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlDocumentPackage.XML_SEQUENCE__ENTITIES:
                getEntities().clear();
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
                setParent((XmlEntityHolder)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

} // XmlSequenceImpl
