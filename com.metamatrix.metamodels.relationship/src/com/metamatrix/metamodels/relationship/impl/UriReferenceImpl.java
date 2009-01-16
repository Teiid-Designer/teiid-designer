/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.relationship.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.UriReference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Uri Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl#getUri <em>Uri</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl#isResolvable <em>Resolvable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl#getEncoding <em>Encoding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl#getAbstract <em>Abstract</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl#getKeywords <em>Keywords</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl#getRelatedUris <em>Related Uris</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl#getProperties <em>Properties</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UriReferenceImpl extends PlaceholderReferenceImpl implements UriReference {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

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
     * The default value of the '{@link #getUri() <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUri()
     * @generated
     * @ordered
     */
    protected static final String URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUri() <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUri()
     * @generated
     * @ordered
     */
    protected String uri = URI_EDEFAULT;

    /**
     * The default value of the '{@link #isResolvable() <em>Resolvable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isResolvable()
     * @generated
     * @ordered
     */
    protected static final boolean RESOLVABLE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isResolvable() <em>Resolvable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isResolvable()
     * @generated
     * @ordered
     */
    protected boolean resolvable = RESOLVABLE_EDEFAULT;

    /**
     * The default value of the '{@link #getEncoding() <em>Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEncoding()
     * @generated
     * @ordered
     */
    protected static final String ENCODING_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEncoding() <em>Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEncoding()
     * @generated
     * @ordered
     */
    protected String encoding = ENCODING_EDEFAULT;

    /**
     * The default value of the '{@link #getAbstract() <em>Abstract</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAbstract()
     * @generated
     * @ordered
     */
    protected static final String ABSTRACT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAbstract() <em>Abstract</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAbstract()
     * @generated
     * @ordered
     */
    protected String abstract_ = ABSTRACT_EDEFAULT;

    /**
     * The default value of the '{@link #getKeywords() <em>Keywords</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getKeywords()
     * @generated
     * @ordered
     */
    protected static final String KEYWORDS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getKeywords() <em>Keywords</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getKeywords()
     * @generated
     * @ordered
     */
    protected String keywords = KEYWORDS_EDEFAULT;

    /**
     * The cached value of the '{@link #getRelatedUris() <em>Related Uris</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRelatedUris()
     * @generated
     * @ordered
     */
    protected EList relatedUris = null;

    /**
     * The cached value of the '{@link #getProperties() <em>Properties</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProperties()
     * @generated
     * @ordered
     */
    protected EMap properties = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected UriReferenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationshipPackage.eINSTANCE.getUriReference();
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
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.URI_REFERENCE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUri() {
        return uri;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUri(String newUri) {
        String oldUri = uri;
        uri = newUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.URI_REFERENCE__URI, oldUri, uri));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isResolvable() {
        return resolvable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setResolvable(boolean newResolvable) {
        boolean oldResolvable = resolvable;
        resolvable = newResolvable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.URI_REFERENCE__RESOLVABLE, oldResolvable, resolvable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEncoding(String newEncoding) {
        String oldEncoding = encoding;
        encoding = newEncoding;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.URI_REFERENCE__ENCODING, oldEncoding, encoding));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAbstract() {
        return abstract_;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAbstract(String newAbstract) {
        String oldAbstract = abstract_;
        abstract_ = newAbstract;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.URI_REFERENCE__ABSTRACT, oldAbstract, abstract_));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setKeywords(String newKeywords) {
        String oldKeywords = keywords;
        keywords = newKeywords;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.URI_REFERENCE__KEYWORDS, oldKeywords, keywords));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getRelatedUris() {
        if (relatedUris == null) {
            relatedUris = new EDataTypeUniqueEList(String.class, this, RelationshipPackage.URI_REFERENCE__RELATED_URIS);
        }
        return relatedUris;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMap getProperties() {
        if (properties == null) {
            properties = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, RelationshipPackage.URI_REFERENCE__PROPERTIES);
        }
        return properties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getDisplayableName() {
        final String name = this.getName();
        final String uri = this.getUri();
        if ( name != null && name.trim().length() != 0 ) {
            return name + "=" + uri; //$NON-NLS-1$
        }
        return uri;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getDisplayableNameGen() {
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
                case RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER, msgs);
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
                case RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    return eBasicSetContainer(null, RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER, msgs);
                case RelationshipPackage.URI_REFERENCE__PROPERTIES:
                    return ((InternalEList)getProperties()).basicRemove(otherEnd, msgs);
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
                case RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    return eContainer.eInverseRemove(this, RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS, PlaceholderReferenceContainer.class, msgs);
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
            case RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                return getPlaceholderReferenceContainer();
            case RelationshipPackage.URI_REFERENCE__NAME:
                return getName();
            case RelationshipPackage.URI_REFERENCE__URI:
                return getUri();
            case RelationshipPackage.URI_REFERENCE__RESOLVABLE:
                return isResolvable() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.URI_REFERENCE__ENCODING:
                return getEncoding();
            case RelationshipPackage.URI_REFERENCE__ABSTRACT:
                return getAbstract();
            case RelationshipPackage.URI_REFERENCE__KEYWORDS:
                return getKeywords();
            case RelationshipPackage.URI_REFERENCE__RELATED_URIS:
                return getRelatedUris();
            case RelationshipPackage.URI_REFERENCE__PROPERTIES:
                return getProperties();
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
            case RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                setPlaceholderReferenceContainer((PlaceholderReferenceContainer)newValue);
                return;
            case RelationshipPackage.URI_REFERENCE__NAME:
                setName((String)newValue);
                return;
            case RelationshipPackage.URI_REFERENCE__URI:
                setUri((String)newValue);
                return;
            case RelationshipPackage.URI_REFERENCE__RESOLVABLE:
                setResolvable(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.URI_REFERENCE__ENCODING:
                setEncoding((String)newValue);
                return;
            case RelationshipPackage.URI_REFERENCE__ABSTRACT:
                setAbstract((String)newValue);
                return;
            case RelationshipPackage.URI_REFERENCE__KEYWORDS:
                setKeywords((String)newValue);
                return;
            case RelationshipPackage.URI_REFERENCE__RELATED_URIS:
                getRelatedUris().clear();
                getRelatedUris().addAll((Collection)newValue);
                return;
            case RelationshipPackage.URI_REFERENCE__PROPERTIES:
                getProperties().clear();
                getProperties().addAll((Collection)newValue);
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
            case RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                setPlaceholderReferenceContainer((PlaceholderReferenceContainer)null);
                return;
            case RelationshipPackage.URI_REFERENCE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationshipPackage.URI_REFERENCE__URI:
                setUri(URI_EDEFAULT);
                return;
            case RelationshipPackage.URI_REFERENCE__RESOLVABLE:
                setResolvable(RESOLVABLE_EDEFAULT);
                return;
            case RelationshipPackage.URI_REFERENCE__ENCODING:
                setEncoding(ENCODING_EDEFAULT);
                return;
            case RelationshipPackage.URI_REFERENCE__ABSTRACT:
                setAbstract(ABSTRACT_EDEFAULT);
                return;
            case RelationshipPackage.URI_REFERENCE__KEYWORDS:
                setKeywords(KEYWORDS_EDEFAULT);
                return;
            case RelationshipPackage.URI_REFERENCE__RELATED_URIS:
                getRelatedUris().clear();
                return;
            case RelationshipPackage.URI_REFERENCE__PROPERTIES:
                getProperties().clear();
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
            case RelationshipPackage.URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                return getPlaceholderReferenceContainer() != null;
            case RelationshipPackage.URI_REFERENCE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationshipPackage.URI_REFERENCE__URI:
                return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
            case RelationshipPackage.URI_REFERENCE__RESOLVABLE:
                return resolvable != RESOLVABLE_EDEFAULT;
            case RelationshipPackage.URI_REFERENCE__ENCODING:
                return ENCODING_EDEFAULT == null ? encoding != null : !ENCODING_EDEFAULT.equals(encoding);
            case RelationshipPackage.URI_REFERENCE__ABSTRACT:
                return ABSTRACT_EDEFAULT == null ? abstract_ != null : !ABSTRACT_EDEFAULT.equals(abstract_);
            case RelationshipPackage.URI_REFERENCE__KEYWORDS:
                return KEYWORDS_EDEFAULT == null ? keywords != null : !KEYWORDS_EDEFAULT.equals(keywords);
            case RelationshipPackage.URI_REFERENCE__RELATED_URIS:
                return relatedUris != null && !relatedUris.isEmpty();
            case RelationshipPackage.URI_REFERENCE__PROPERTIES:
                return properties != null && !properties.isEmpty();
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
        result.append(", uri: "); //$NON-NLS-1$
        result.append(uri);
        result.append(", resolvable: "); //$NON-NLS-1$
        result.append(resolvable);
        result.append(", encoding: "); //$NON-NLS-1$
        result.append(encoding);
        result.append(", abstract: "); //$NON-NLS-1$
        result.append(abstract_);
        result.append(", keywords: "); //$NON-NLS-1$
        result.append(keywords);
        result.append(", relatedUris: "); //$NON-NLS-1$
        result.append(relatedUris);
        result.append(')');
        return result.toString();
    }

} //UriReferenceImpl
