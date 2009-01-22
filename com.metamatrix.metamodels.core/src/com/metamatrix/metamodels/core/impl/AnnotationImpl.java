/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Annotation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.core.impl.AnnotationImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.impl.AnnotationImpl#getKeywords <em>Keywords</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.impl.AnnotationImpl#getTags <em>Tags</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.impl.AnnotationImpl#getAnnotationContainer <em>Annotation Container</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.impl.AnnotationImpl#getAnnotatedObject <em>Annotated Object</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.core.impl.AnnotationImpl#getExtensionObject <em>Extension Object</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AnnotationImpl extends EObjectImpl implements Annotation {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected String description = DESCRIPTION_EDEFAULT;

    /**
     * The cached value of the '{@link #getKeywords() <em>Keywords</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getKeywords()
     * @generated
     * @ordered
     */
    protected EList keywords = null;

    /**
     * The cached value of the '{@link #getTags() <em>Tags</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTags()
     * @generated
     * @ordered
     */
    protected EMap tags = null;

    /**
     * The cached value of the '{@link #getAnnotatedObject() <em>Annotated Object</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAnnotatedObject()
     * @generated
     * @ordered
     */
    protected EObject annotatedObject = null;

    /**
     * The cached value of the '{@link #getExtensionObject() <em>Extension Object</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExtensionObject()
     * @generated
     * @ordered
     */
    protected EObject extensionObject = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AnnotationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return CorePackage.eINSTANCE.getAnnotation();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDescription(String newDescription) {
        String oldDescription = description;
        description = newDescription;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.ANNOTATION__DESCRIPTION, oldDescription, description));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getKeywords() {
        if (keywords == null) {
            keywords = new EDataTypeUniqueEList(String.class, this, CorePackage.ANNOTATION__KEYWORDS);
        }
        return keywords;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getAnnotatedObject() {
        if (annotatedObject != null && annotatedObject.eIsProxy()) {
            EObject oldAnnotatedObject = annotatedObject;
            annotatedObject = eResolveProxy((InternalEObject)annotatedObject);
            if (annotatedObject != oldAnnotatedObject) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, CorePackage.ANNOTATION__ANNOTATED_OBJECT, oldAnnotatedObject, annotatedObject));
            }
        }
        return annotatedObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetAnnotatedObject() {
        return annotatedObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void setAnnotatedObject(EObject newAnnotatedObject)
    {
        EObject oldAnnotatedObject = annotatedObject;
		// Start customized code
        final AnnotationContainer annContainer = this.getAnnotationContainer();
        if ( annContainer != null && annContainer instanceof InternalAnnotationContainer ) {
            final InternalAnnotationContainer iac = (InternalAnnotationContainer) annContainer;
            iac.removeAnnotation(this);
            annotatedObject = newAnnotatedObject;
            iac.addAnnotation(this);
        } else {
            annotatedObject = newAnnotatedObject;
        }
		// End customized code
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.ANNOTATION__ANNOTATED_OBJECT, oldAnnotatedObject, annotatedObject));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAnnotatedObjectGen(EObject newAnnotatedObject) {
        EObject oldAnnotatedObject = annotatedObject;
        annotatedObject = newAnnotatedObject;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.ANNOTATION__ANNOTATED_OBJECT, oldAnnotatedObject, annotatedObject));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getExtensionObject() {
        return extensionObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetExtensionObject(EObject newExtensionObject, NotificationChain msgs) {
        EObject oldExtensionObject = extensionObject;
        extensionObject = newExtensionObject;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, CorePackage.ANNOTATION__EXTENSION_OBJECT, oldExtensionObject, newExtensionObject);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setExtensionObject(EObject newExtensionObject) {
        if (newExtensionObject != extensionObject) {
            NotificationChain msgs = null;
            if (extensionObject != null)
                msgs = ((InternalEObject)extensionObject).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - CorePackage.ANNOTATION__EXTENSION_OBJECT, null, msgs);
            if (newExtensionObject != null)
                msgs = ((InternalEObject)newExtensionObject).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - CorePackage.ANNOTATION__EXTENSION_OBJECT, null, msgs);
            msgs = basicSetExtensionObject(newExtensionObject, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.ANNOTATION__EXTENSION_OBJECT, newExtensionObject, newExtensionObject));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMap getTags() {
        if (tags == null) {
            tags = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, CorePackage.ANNOTATION__TAGS);
        }
        return tags;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public AnnotationContainer getAnnotationContainer() {
        if (eContainerFeatureID != CorePackage.ANNOTATION__ANNOTATION_CONTAINER) return null;
        return (AnnotationContainer)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAnnotationContainer(AnnotationContainer newAnnotationContainer) {
        if (newAnnotationContainer != eContainer || (eContainerFeatureID != CorePackage.ANNOTATION__ANNOTATION_CONTAINER && newAnnotationContainer != null)) {
            if (EcoreUtil.isAncestor(this, newAnnotationContainer))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newAnnotationContainer != null)
                msgs = ((InternalEObject)newAnnotationContainer).eInverseAdd(this, CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS, AnnotationContainer.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newAnnotationContainer, CorePackage.ANNOTATION__ANNOTATION_CONTAINER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, CorePackage.ANNOTATION__ANNOTATION_CONTAINER, newAnnotationContainer, newAnnotationContainer));
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
                case CorePackage.ANNOTATION__ANNOTATION_CONTAINER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, CorePackage.ANNOTATION__ANNOTATION_CONTAINER, msgs);
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
                case CorePackage.ANNOTATION__TAGS:
                    return ((InternalEList)getTags()).basicRemove(otherEnd, msgs);
                case CorePackage.ANNOTATION__ANNOTATION_CONTAINER:
                    return eBasicSetContainer(null, CorePackage.ANNOTATION__ANNOTATION_CONTAINER, msgs);
                case CorePackage.ANNOTATION__EXTENSION_OBJECT:
                    return basicSetExtensionObject(null, msgs);
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
                case CorePackage.ANNOTATION__ANNOTATION_CONTAINER:
                    return eContainer.eInverseRemove(this, CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS, AnnotationContainer.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * Overridden to update the container's cache.
     * @see org.eclipse.emf.ecore.impl.EObjectImpl#eBasicSetContainer(org.eclipse.emf.ecore.InternalEObject, int, org.eclipse.emf.common.notify.NotificationChain)
     */
    @Override
    public NotificationChain eBasicSetContainer( InternalEObject newContainer, int newContainerFeatureID,
                                                 NotificationChain msgs) {
        final InternalEObject existingContainer = eContainer;
        final NotificationChain result = super.eBasicSetContainer(newContainer, newContainerFeatureID, msgs);
        if ( newContainerFeatureID == CorePackage.ANNOTATION__ANNOTATION_CONTAINER ) {
            if ( existingContainer != null && existingContainer instanceof InternalAnnotationContainer ) {
                ((InternalAnnotationContainer)existingContainer).removeAnnotation(this);
            }
            if ( newContainer != null && newContainer instanceof InternalAnnotationContainer ) {
                ((InternalAnnotationContainer)newContainer).addAnnotation(this);
            }
        }
        return result;
    }




    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.ANNOTATION__DESCRIPTION:
                return getDescription();
            case CorePackage.ANNOTATION__KEYWORDS:
                return getKeywords();
            case CorePackage.ANNOTATION__TAGS:
                return getTags();
            case CorePackage.ANNOTATION__ANNOTATION_CONTAINER:
                return getAnnotationContainer();
            case CorePackage.ANNOTATION__ANNOTATED_OBJECT:
                if (resolve) return getAnnotatedObject();
                return basicGetAnnotatedObject();
            case CorePackage.ANNOTATION__EXTENSION_OBJECT:
                return getExtensionObject();
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
            case CorePackage.ANNOTATION__DESCRIPTION:
                setDescription((String)newValue);
                return;
            case CorePackage.ANNOTATION__KEYWORDS:
                getKeywords().clear();
                getKeywords().addAll((Collection)newValue);
                return;
            case CorePackage.ANNOTATION__TAGS:
                getTags().clear();
                getTags().addAll((Collection)newValue);
                return;
            case CorePackage.ANNOTATION__ANNOTATION_CONTAINER:
                setAnnotationContainer((AnnotationContainer)newValue);
                return;
            case CorePackage.ANNOTATION__ANNOTATED_OBJECT:
                setAnnotatedObject((EObject)newValue);
                return;
            case CorePackage.ANNOTATION__EXTENSION_OBJECT:
                setExtensionObject((EObject)newValue);
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
            case CorePackage.ANNOTATION__DESCRIPTION:
                setDescription(DESCRIPTION_EDEFAULT);
                return;
            case CorePackage.ANNOTATION__KEYWORDS:
                getKeywords().clear();
                return;
            case CorePackage.ANNOTATION__TAGS:
                getTags().clear();
                return;
            case CorePackage.ANNOTATION__ANNOTATION_CONTAINER:
                setAnnotationContainer((AnnotationContainer)null);
                return;
            case CorePackage.ANNOTATION__ANNOTATED_OBJECT:
                setAnnotatedObject((EObject)null);
                return;
            case CorePackage.ANNOTATION__EXTENSION_OBJECT:
                setExtensionObject((EObject)null);
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
            case CorePackage.ANNOTATION__DESCRIPTION:
                return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
            case CorePackage.ANNOTATION__KEYWORDS:
                return keywords != null && !keywords.isEmpty();
            case CorePackage.ANNOTATION__TAGS:
                return tags != null && !tags.isEmpty();
            case CorePackage.ANNOTATION__ANNOTATION_CONTAINER:
                return getAnnotationContainer() != null;
            case CorePackage.ANNOTATION__ANNOTATED_OBJECT:
                return annotatedObject != null;
            case CorePackage.ANNOTATION__EXTENSION_OBJECT:
                return extensionObject != null;
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
        result.append(" (description: "); //$NON-NLS-1$
        result.append(description);
        result.append(", keywords: "); //$NON-NLS-1$
        result.append(keywords);
        result.append(')');
        return result.toString();
    }

} //AnnotationImpl
