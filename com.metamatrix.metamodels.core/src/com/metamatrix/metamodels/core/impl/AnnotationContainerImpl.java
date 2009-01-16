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

package com.metamatrix.metamodels.core.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CorePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Container</b></em>'.
 * @implements InternalAnnotationContainer
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.core.impl.AnnotationContainerImpl#getAnnotations <em>Annotations</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AnnotationContainerImpl extends EObjectImpl implements AnnotationContainer, InternalAnnotationContainer {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    private Map annotationByTarget = new HashMap();
    
    /**
     * The cached value of the '{@link #getAnnotations() <em>Annotations</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAnnotations()
     * @generated
     * @ordered
     */
    protected EList annotations = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected AnnotationContainerImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return CorePackage.eINSTANCE.getAnnotationContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getAnnotations() {
        if (annotations == null) {
            annotations = new EObjectContainmentWithInverseEList(Annotation.class, this, CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS, CorePackage.ANNOTATION__ANNOTATION_CONTAINER);
        }
        return annotations;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
    {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
					// Start customized code
                    addAnnotation((Annotation)otherEnd);
					// End customized code
                    return ((InternalEList)getAnnotations()).basicAdd(otherEnd, msgs);
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
    public NotificationChain eInverseAddGen(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
                    return ((InternalEList)getAnnotations()).basicAdd(otherEnd, msgs);
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
     * @generated NOT
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
    {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
					// Start customized code
                    removeAnnotation((Annotation)otherEnd);
					// End customized code
                    return ((InternalEList)getAnnotations()).basicRemove(otherEnd, msgs);
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
    public NotificationChain eInverseRemoveGen(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
                    return ((InternalEList)getAnnotations()).basicRemove(otherEnd, msgs);
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
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
                return getAnnotations();
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
            case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
                getAnnotations().clear();
                getAnnotations().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public void eUnset(EStructuralFeature eFeature)
    {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
                getAnnotations().clear();
				// Start customized code
                this.annotationByTarget.clear();
				// End customized code
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void eUnsetGen(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
                getAnnotations().clear();
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
            case CorePackage.ANNOTATION_CONTAINER__ANNOTATIONS:
                return annotations != null && !annotations.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

    public Annotation findAnnotation( final EObject annotatedObject ) {
        final Annotation result = (Annotation) this.annotationByTarget.get(annotatedObject);
        return result;
    }
    
    protected void doUpdateExistingAnnotation( final Annotation annotation, final EObject newTarget, final EObject oldTarget ) {
        if ( oldTarget != null ) {
            // Remove the caching by the old target ...
            this.annotationByTarget.remove(oldTarget);
        }
        if ( newTarget != null ) {
            this.annotationByTarget.put(newTarget,annotation);
        }
    }

    /**
     * @param annotation
     */
    public void addAnnotation(final Annotation annotation) {
        if ( annotation != null ) {
            this.annotationByTarget.put(annotation.getAnnotatedObject(),annotation);
        }
    }

    /**
     * @param annotation
     */
    public void removeAnnotation(Annotation annotation) {
        if ( annotation != null ) {
            this.annotationByTarget.remove(annotation.getAnnotatedObject());
        }
    }

} //AnnotationContainerImpl
