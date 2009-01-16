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

package com.metamatrix.modeler.jdbc.impl;

import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcDriverContainer;
import com.metamatrix.modeler.jdbc.JdbcPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Driver</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcDriverImpl#getJdbcDriverContainer <em>Jdbc Driver Container</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcDriverImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcDriverImpl#getUrlSyntax <em>Url Syntax</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcDriverImpl#getJarFileUris <em>Jar File Uris</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcDriverImpl#getAvailableDriverClassNames <em>Available Driver Class Names</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcDriverImpl#getPreferredDriverClassName <em>Preferred Driver Class Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class JdbcDriverImpl extends EObjectImpl implements JdbcDriver {
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
     * The default value of the '{@link #getUrlSyntax() <em>Url Syntax</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUrlSyntax()
     * @generated
     * @ordered
     */
    protected static final String URL_SYNTAX_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUrlSyntax() <em>Url Syntax</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUrlSyntax()
     * @generated
     * @ordered
     */
    protected String urlSyntax = URL_SYNTAX_EDEFAULT;

    /**
     * The cached value of the '{@link #getJarFileUris() <em>Jar File Uris</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getJarFileUris()
     * @generated
     * @ordered
     */
    protected EList jarFileUris = null;

    /**
     * The cached value of the '{@link #getAvailableDriverClassNames() <em>Available Driver Class Names</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAvailableDriverClassNames()
     * @generated
     * @ordered
     */
    protected EList availableDriverClassNames = null;

    /**
     * The default value of the '{@link #getPreferredDriverClassName() <em>Preferred Driver Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPreferredDriverClassName()
     * @generated
     * @ordered
     */
    protected static final String PREFERRED_DRIVER_CLASS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPreferredDriverClassName() <em>Preferred Driver Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPreferredDriverClassName()
     * @generated
     * @ordered
     */
    protected String preferredDriverClassName = PREFERRED_DRIVER_CLASS_NAME_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected JdbcDriverImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JdbcPackage.eINSTANCE.getJdbcDriver();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcDriverContainer getJdbcDriverContainer() {
        if (eContainerFeatureID != JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER) return null;
        return (JdbcDriverContainer)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setJdbcDriverContainer(JdbcDriverContainer newJdbcDriverContainer) {
        if (newJdbcDriverContainer != eContainer || (eContainerFeatureID != JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER && newJdbcDriverContainer != null)) {
            if (EcoreUtil.isAncestor(this, newJdbcDriverContainer))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newJdbcDriverContainer != null)
                msgs = ((InternalEObject)newJdbcDriverContainer).eInverseAdd(this, JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS, JdbcDriverContainer.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newJdbcDriverContainer, JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER, newJdbcDriverContainer, newJdbcDriverContainer));
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
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_DRIVER__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUrlSyntax() {
        return urlSyntax;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUrlSyntax(String newUrlSyntax) {
        String oldUrlSyntax = urlSyntax;
        urlSyntax = newUrlSyntax;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_DRIVER__URL_SYNTAX, oldUrlSyntax, urlSyntax));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getJarFileUris() {
        if (jarFileUris == null) {
            jarFileUris = new EDataTypeUniqueEList(String.class, this, JdbcPackage.JDBC_DRIVER__JAR_FILE_URIS);
        }
        return jarFileUris;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getAvailableDriverClassNames() {
        if (availableDriverClassNames == null) {
            availableDriverClassNames = new EDataTypeUniqueEList(String.class, this, JdbcPackage.JDBC_DRIVER__AVAILABLE_DRIVER_CLASS_NAMES);
        }
        return availableDriverClassNames;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getPreferredDriverClassName() {
        return preferredDriverClassName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPreferredDriverClassName(String newPreferredDriverClassName) {
        String oldPreferredDriverClassName = preferredDriverClassName;
        preferredDriverClassName = newPreferredDriverClassName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_DRIVER__PREFERRED_DRIVER_CLASS_NAME, oldPreferredDriverClassName, preferredDriverClassName));
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
                case JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER, msgs);
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
                case JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER:
                    return eBasicSetContainer(null, JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER, msgs);
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
                case JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER:
                    return eContainer.eInverseRemove(this, JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS, JdbcDriverContainer.class, msgs);
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
            case JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER:
                return getJdbcDriverContainer();
            case JdbcPackage.JDBC_DRIVER__NAME:
                return getName();
            case JdbcPackage.JDBC_DRIVER__URL_SYNTAX:
                return getUrlSyntax();
            case JdbcPackage.JDBC_DRIVER__JAR_FILE_URIS:
                return getJarFileUris();
            case JdbcPackage.JDBC_DRIVER__AVAILABLE_DRIVER_CLASS_NAMES:
                return getAvailableDriverClassNames();
            case JdbcPackage.JDBC_DRIVER__PREFERRED_DRIVER_CLASS_NAME:
                return getPreferredDriverClassName();
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
            case JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER:
                setJdbcDriverContainer((JdbcDriverContainer)newValue);
                return;
            case JdbcPackage.JDBC_DRIVER__NAME:
                setName((String)newValue);
                return;
            case JdbcPackage.JDBC_DRIVER__URL_SYNTAX:
                setUrlSyntax((String)newValue);
                return;
            case JdbcPackage.JDBC_DRIVER__JAR_FILE_URIS:
                getJarFileUris().clear();
                getJarFileUris().addAll((Collection)newValue);
                return;
            case JdbcPackage.JDBC_DRIVER__AVAILABLE_DRIVER_CLASS_NAMES:
                getAvailableDriverClassNames().clear();
                getAvailableDriverClassNames().addAll((Collection)newValue);
                return;
            case JdbcPackage.JDBC_DRIVER__PREFERRED_DRIVER_CLASS_NAME:
                setPreferredDriverClassName((String)newValue);
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
            case JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER:
                setJdbcDriverContainer((JdbcDriverContainer)null);
                return;
            case JdbcPackage.JDBC_DRIVER__NAME:
                setName(NAME_EDEFAULT);
                return;
            case JdbcPackage.JDBC_DRIVER__URL_SYNTAX:
                setUrlSyntax(URL_SYNTAX_EDEFAULT);
                return;
            case JdbcPackage.JDBC_DRIVER__JAR_FILE_URIS:
                getJarFileUris().clear();
                return;
            case JdbcPackage.JDBC_DRIVER__AVAILABLE_DRIVER_CLASS_NAMES:
                getAvailableDriverClassNames().clear();
                return;
            case JdbcPackage.JDBC_DRIVER__PREFERRED_DRIVER_CLASS_NAME:
                setPreferredDriverClassName(PREFERRED_DRIVER_CLASS_NAME_EDEFAULT);
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
            case JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER:
                return getJdbcDriverContainer() != null;
            case JdbcPackage.JDBC_DRIVER__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case JdbcPackage.JDBC_DRIVER__URL_SYNTAX:
                return URL_SYNTAX_EDEFAULT == null ? urlSyntax != null : !URL_SYNTAX_EDEFAULT.equals(urlSyntax);
            case JdbcPackage.JDBC_DRIVER__JAR_FILE_URIS:
                return jarFileUris != null && !jarFileUris.isEmpty();
            case JdbcPackage.JDBC_DRIVER__AVAILABLE_DRIVER_CLASS_NAMES:
                return availableDriverClassNames != null && !availableDriverClassNames.isEmpty();
            case JdbcPackage.JDBC_DRIVER__PREFERRED_DRIVER_CLASS_NAME:
                return PREFERRED_DRIVER_CLASS_NAME_EDEFAULT == null ? preferredDriverClassName != null : !PREFERRED_DRIVER_CLASS_NAME_EDEFAULT.equals(preferredDriverClassName);
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
        result.append(", urlSyntax: "); //$NON-NLS-1$
        result.append(urlSyntax);
        result.append(", jarFileUris: "); //$NON-NLS-1$
        result.append(jarFileUris);
        result.append(", availableDriverClassNames: "); //$NON-NLS-1$
        result.append(availableDriverClassNames);
        result.append(", preferredDriverClassName: "); //$NON-NLS-1$
        result.append(preferredDriverClassName);
        result.append(')');
        return result.toString();
    }

} //JdbcDriverImpl
