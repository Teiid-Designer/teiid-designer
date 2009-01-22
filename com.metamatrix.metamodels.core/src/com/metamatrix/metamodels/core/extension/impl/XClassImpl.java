/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>XClass</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.core.extension.impl.XClassImpl#getExtendedClass <em>Extended Class</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XClassImpl extends EClassImpl implements XClass {
    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2000-2008 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getExtendedClass() <em>Extended Class</em>}' reference.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @see #getExtendedClass()
	 * @generated
	 * @ordered
	 */
    protected EClass extendedClass;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    protected XClassImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return ExtensionPackage.Literals.XCLASS;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getExtendedClass() {
		if (extendedClass != null && extendedClass.eIsProxy()) {
			InternalEObject oldExtendedClass = (InternalEObject)extendedClass;
			extendedClass = (EClass)eResolveProxy(oldExtendedClass);
			if (extendedClass != oldExtendedClass) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ExtensionPackage.XCLASS__EXTENDED_CLASS, oldExtendedClass, extendedClass));
			}
		}
		return extendedClass;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass basicGetExtendedClass() {
		return extendedClass;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public void setExtendedClass(EClass newExtendedClass) {
		EClass oldExtendedClass = extendedClass;
		extendedClass = newExtendedClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ExtensionPackage.XCLASS__EXTENDED_CLASS, oldExtendedClass, extendedClass));
	}

    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ExtensionPackage.XCLASS__EXTENDED_CLASS:
				if (resolve) return getExtendedClass();
				return basicGetExtendedClass();
		}
		return super.eGet(featureID, resolve, coreType);
	}

				/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
    public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ExtensionPackage.XCLASS__EXTENDED_CLASS:
				setExtendedClass((EClass)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

				/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
    public void eUnset(int featureID) {
		switch (featureID) {
			case ExtensionPackage.XCLASS__EXTENDED_CLASS:
				setExtendedClass((EClass)null);
				return;
		}
		super.eUnset(featureID);
	}

				/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
    public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ExtensionPackage.XCLASS__EXTENDED_CLASS:
				return extendedClass != null;
		}
		return super.eIsSet(featureID);
	}

} //XClassImpl
