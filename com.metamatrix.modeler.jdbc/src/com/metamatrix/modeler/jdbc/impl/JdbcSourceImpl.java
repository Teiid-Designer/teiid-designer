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
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcPackage;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.JdbcSourceContainer;
import com.metamatrix.modeler.jdbc.JdbcSourceProperty;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Source</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getJdbcDriver <em>Jdbc Driver</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getJdbcSourceContainer <em>Jdbc Source Container</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getImportSettings <em>Import Settings</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getDriverName <em>Driver Name</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getDriverClass <em>Driver Class</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getUsername <em>Username</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcSourceImpl#getUrl <em>Url</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class JdbcSourceImpl extends EObjectImpl implements JdbcSource {
    /**
     * The cached value of the '{@link #getJdbcDriver() <em>Jdbc Driver</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getJdbcDriver()
     * @generated
     * @ordered
     */
    protected JdbcDriver jdbcDriver = null;

    /**
     * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProperties()
     * @generated
     * @ordered
     */
    protected EList properties = null;

    /**
     * The cached value of the '{@link #getImportSettings() <em>Import Settings</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getImportSettings()
     * @generated
     * @ordered
     */
    protected JdbcImportSettings importSettings = null;

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
     * The default value of the '{@link #getDriverName() <em>Driver Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDriverName()
     * @generated
     * @ordered
     */
    protected static final String DRIVER_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDriverName() <em>Driver Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDriverName()
     * @generated
     * @ordered
     */
    protected String driverName = DRIVER_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getDriverClass() <em>Driver Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDriverClass()
     * @generated
     * @ordered
     */
    protected static final String DRIVER_CLASS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDriverClass() <em>Driver Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDriverClass()
     * @generated
     * @ordered
     */
    protected String driverClass = DRIVER_CLASS_EDEFAULT;

    /**
     * The default value of the '{@link #getUsername() <em>Username</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUsername()
     * @generated
     * @ordered
     */
    protected static final String USERNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUsername() <em>Username</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUsername()
     * @generated
     * @ordered
     */
    protected String username = USERNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getUrl() <em>Url</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUrl()
     * @generated
     * @ordered
     */
    protected static final String URL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUrl() <em>Url</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUrl()
     * @generated
     * @ordered
     */
    protected String url = URL_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected JdbcSourceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JdbcPackage.eINSTANCE.getJdbcSource();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcDriver getJdbcDriver() {
        if (jdbcDriver != null && jdbcDriver.eIsProxy()) {
            JdbcDriver oldJdbcDriver = jdbcDriver;
            jdbcDriver = (JdbcDriver)eResolveProxy((InternalEObject)jdbcDriver);
            if (jdbcDriver != oldJdbcDriver) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, JdbcPackage.JDBC_SOURCE__JDBC_DRIVER, oldJdbcDriver, jdbcDriver));
            }
        }
        return jdbcDriver;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcDriver basicGetJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setJdbcDriver(JdbcDriver newJdbcDriver) {
        JdbcDriver oldJdbcDriver = jdbcDriver;
        jdbcDriver = newJdbcDriver;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__JDBC_DRIVER, oldJdbcDriver, jdbcDriver));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getProperties() {
        if (properties == null) {
            properties = new EObjectContainmentWithInverseEList(JdbcSourceProperty.class, this, JdbcPackage.JDBC_SOURCE__PROPERTIES, JdbcPackage.JDBC_SOURCE_PROPERTY__SOURCE);
        }
        return properties;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcSourceContainer getJdbcSourceContainer() {
        if (eContainerFeatureID != JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER) return null;
        return (JdbcSourceContainer)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setJdbcSourceContainer(JdbcSourceContainer newJdbcSourceContainer) {
        if (newJdbcSourceContainer != eContainer || (eContainerFeatureID != JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER && newJdbcSourceContainer != null)) {
            if (EcoreUtil.isAncestor(this, newJdbcSourceContainer))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newJdbcSourceContainer != null)
                msgs = ((InternalEObject)newJdbcSourceContainer).eInverseAdd(this, JdbcPackage.JDBC_SOURCE_CONTAINER__JDBC_SOURCES, JdbcSourceContainer.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newJdbcSourceContainer, JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER, newJdbcSourceContainer, newJdbcSourceContainer));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcImportSettings getImportSettings() {
        return importSettings;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetImportSettings(JdbcImportSettings newImportSettings, NotificationChain msgs) {
        JdbcImportSettings oldImportSettings = importSettings;
        importSettings = newImportSettings;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS, oldImportSettings, newImportSettings);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setImportSettings(JdbcImportSettings newImportSettings) {
        if (newImportSettings != importSettings) {
            NotificationChain msgs = null;
            if (importSettings != null)
                msgs = ((InternalEObject)importSettings).eInverseRemove(this, JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE, JdbcImportSettings.class, msgs);
            if (newImportSettings != null)
                msgs = ((InternalEObject)newImportSettings).eInverseAdd(this, JdbcPackage.JDBC_IMPORT_SETTINGS__SOURCE, JdbcImportSettings.class, msgs);
            msgs = basicSetImportSettings(newImportSettings, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS, newImportSettings, newImportSettings));
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
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDriverName(String newDriverName) {
        String oldDriverName = driverName;
        driverName = newDriverName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__DRIVER_NAME, oldDriverName, driverName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDriverClass() {
        return driverClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDriverClass(String newDriverClass) {
        String oldDriverClass = driverClass;
        driverClass = newDriverClass;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__DRIVER_CLASS, oldDriverClass, driverClass));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUsername() {
        return username;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUsername(String newUsername) {
        String oldUsername = username;
        username = newUsername;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__USERNAME, oldUsername, username));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUrl() {
        return url;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUrl(String newUrl) {
        String oldUrl = url;
        url = newUrl;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, JdbcPackage.JDBC_SOURCE__URL, oldUrl, url));
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
                case JdbcPackage.JDBC_SOURCE__PROPERTIES:
                    return ((InternalEList)getProperties()).basicAdd(otherEnd, msgs);
                case JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER, msgs);
                case JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS:
                    if (importSettings != null)
                        msgs = ((InternalEObject)importSettings).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS, null, msgs);
                    return basicSetImportSettings((JdbcImportSettings)otherEnd, msgs);
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
                case JdbcPackage.JDBC_SOURCE__PROPERTIES:
                    return ((InternalEList)getProperties()).basicRemove(otherEnd, msgs);
                case JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER:
                    return eBasicSetContainer(null, JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER, msgs);
                case JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS:
                    return basicSetImportSettings(null, msgs);
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
                case JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER:
                    return eContainer.eInverseRemove(this, JdbcPackage.JDBC_SOURCE_CONTAINER__JDBC_SOURCES, JdbcSourceContainer.class, msgs);
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
            case JdbcPackage.JDBC_SOURCE__JDBC_DRIVER:
                if (resolve) return getJdbcDriver();
                return basicGetJdbcDriver();
            case JdbcPackage.JDBC_SOURCE__PROPERTIES:
                return getProperties();
            case JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER:
                return getJdbcSourceContainer();
            case JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS:
                return getImportSettings();
            case JdbcPackage.JDBC_SOURCE__NAME:
                return getName();
            case JdbcPackage.JDBC_SOURCE__DRIVER_NAME:
                return getDriverName();
            case JdbcPackage.JDBC_SOURCE__DRIVER_CLASS:
                return getDriverClass();
            case JdbcPackage.JDBC_SOURCE__USERNAME:
                return getUsername();
            case JdbcPackage.JDBC_SOURCE__URL:
                return getUrl();
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
            case JdbcPackage.JDBC_SOURCE__JDBC_DRIVER:
                setJdbcDriver((JdbcDriver)newValue);
                return;
            case JdbcPackage.JDBC_SOURCE__PROPERTIES:
                getProperties().clear();
                getProperties().addAll((Collection)newValue);
                return;
            case JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER:
                setJdbcSourceContainer((JdbcSourceContainer)newValue);
                return;
            case JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS:
                setImportSettings((JdbcImportSettings)newValue);
                return;
            case JdbcPackage.JDBC_SOURCE__NAME:
                setName((String)newValue);
                return;
            case JdbcPackage.JDBC_SOURCE__DRIVER_NAME:
                setDriverName((String)newValue);
                return;
            case JdbcPackage.JDBC_SOURCE__DRIVER_CLASS:
                setDriverClass((String)newValue);
                return;
            case JdbcPackage.JDBC_SOURCE__USERNAME:
                setUsername((String)newValue);
                return;
            case JdbcPackage.JDBC_SOURCE__URL:
                setUrl((String)newValue);
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
            case JdbcPackage.JDBC_SOURCE__JDBC_DRIVER:
                setJdbcDriver((JdbcDriver)null);
                return;
            case JdbcPackage.JDBC_SOURCE__PROPERTIES:
                getProperties().clear();
                return;
            case JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER:
                setJdbcSourceContainer((JdbcSourceContainer)null);
                return;
            case JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS:
                setImportSettings((JdbcImportSettings)null);
                return;
            case JdbcPackage.JDBC_SOURCE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case JdbcPackage.JDBC_SOURCE__DRIVER_NAME:
                setDriverName(DRIVER_NAME_EDEFAULT);
                return;
            case JdbcPackage.JDBC_SOURCE__DRIVER_CLASS:
                setDriverClass(DRIVER_CLASS_EDEFAULT);
                return;
            case JdbcPackage.JDBC_SOURCE__USERNAME:
                setUsername(USERNAME_EDEFAULT);
                return;
            case JdbcPackage.JDBC_SOURCE__URL:
                setUrl(URL_EDEFAULT);
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
            case JdbcPackage.JDBC_SOURCE__JDBC_DRIVER:
                return jdbcDriver != null;
            case JdbcPackage.JDBC_SOURCE__PROPERTIES:
                return properties != null && !properties.isEmpty();
            case JdbcPackage.JDBC_SOURCE__JDBC_SOURCE_CONTAINER:
                return getJdbcSourceContainer() != null;
            case JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS:
                return importSettings != null;
            case JdbcPackage.JDBC_SOURCE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case JdbcPackage.JDBC_SOURCE__DRIVER_NAME:
                return DRIVER_NAME_EDEFAULT == null ? driverName != null : !DRIVER_NAME_EDEFAULT.equals(driverName);
            case JdbcPackage.JDBC_SOURCE__DRIVER_CLASS:
                return DRIVER_CLASS_EDEFAULT == null ? driverClass != null : !DRIVER_CLASS_EDEFAULT.equals(driverClass);
            case JdbcPackage.JDBC_SOURCE__USERNAME:
                return USERNAME_EDEFAULT == null ? username != null : !USERNAME_EDEFAULT.equals(username);
            case JdbcPackage.JDBC_SOURCE__URL:
                return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
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
        result.append(", driverName: "); //$NON-NLS-1$
        result.append(driverName);
        result.append(", driverClass: "); //$NON-NLS-1$
        result.append(driverClass);
        result.append(", username: "); //$NON-NLS-1$
        result.append(username);
        result.append(", url: "); //$NON-NLS-1$
        result.append(url);
        result.append(')');
        return result.toString();
    }

} //JdbcSourceImpl
