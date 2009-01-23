/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sql Alias</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlAliasImpl#getAlias <em>Alias</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlAliasImpl#getAliasedObject <em>Aliased Object</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.SqlAliasImpl#getSqlTransformation <em>Sql Transformation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SqlAliasImpl extends EObjectImpl implements SqlAlias {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getAlias() <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAlias()
     * @generated
     * @ordered
     */
    protected static final String ALIAS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAlias() <em>Alias</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAlias()
     * @generated
     * @ordered
     */
    protected String alias = ALIAS_EDEFAULT;

    /**
     * The cached value of the '{@link #getAliasedObject() <em>Aliased Object</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAliasedObject()
     * @generated
     * @ordered
     */
    protected EObject aliasedObject = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SqlAliasImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getSqlAlias();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAlias() {
        return alias;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAlias(String newAlias) {
        String oldAlias = alias;
        alias = newAlias;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_ALIAS__ALIAS, oldAlias, alias));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getAliasedObject() {
        if (aliasedObject != null && aliasedObject.eIsProxy()) {
            EObject oldAliasedObject = aliasedObject;
            aliasedObject = eResolveProxy((InternalEObject)aliasedObject);
            if (aliasedObject != oldAliasedObject) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TransformationPackage.SQL_ALIAS__ALIASED_OBJECT, oldAliasedObject, aliasedObject));
            }
        }
        return aliasedObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetAliasedObject() {
        return aliasedObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAliasedObject(EObject newAliasedObject) {
        EObject oldAliasedObject = aliasedObject;
        aliasedObject = newAliasedObject;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_ALIAS__ALIASED_OBJECT, oldAliasedObject, aliasedObject));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SqlTransformation getSqlTransformation() {
        if (eContainerFeatureID != TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION) return null;
        return (SqlTransformation)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSqlTransformation(SqlTransformation newSqlTransformation) {
        if (newSqlTransformation != eContainer || (eContainerFeatureID != TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION && newSqlTransformation != null)) {
            if (EcoreUtil.isAncestor(this, newSqlTransformation))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSqlTransformation != null)
                msgs = ((InternalEObject)newSqlTransformation).eInverseAdd(this, TransformationPackage.SQL_TRANSFORMATION__ALIASES, SqlTransformation.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newSqlTransformation, TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION, newSqlTransformation, newSqlTransformation));
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
                case TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION, msgs);
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
                case TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION:
                    return eBasicSetContainer(null, TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION, msgs);
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
                case TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION:
                    return eContainer.eInverseRemove(this, TransformationPackage.SQL_TRANSFORMATION__ALIASES, SqlTransformation.class, msgs);
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
            case TransformationPackage.SQL_ALIAS__ALIAS:
                return getAlias();
            case TransformationPackage.SQL_ALIAS__ALIASED_OBJECT:
                if (resolve) return getAliasedObject();
                return basicGetAliasedObject();
            case TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION:
                return getSqlTransformation();
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
            case TransformationPackage.SQL_ALIAS__ALIAS:
                setAlias((String)newValue);
                return;
            case TransformationPackage.SQL_ALIAS__ALIASED_OBJECT:
                setAliasedObject((EObject)newValue);
                return;
            case TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION:
                setSqlTransformation((SqlTransformation)newValue);
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
            case TransformationPackage.SQL_ALIAS__ALIAS:
                setAlias(ALIAS_EDEFAULT);
                return;
            case TransformationPackage.SQL_ALIAS__ALIASED_OBJECT:
                setAliasedObject((EObject)null);
                return;
            case TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION:
                setSqlTransformation((SqlTransformation)null);
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
            case TransformationPackage.SQL_ALIAS__ALIAS:
                return ALIAS_EDEFAULT == null ? alias != null : !ALIAS_EDEFAULT.equals(alias);
            case TransformationPackage.SQL_ALIAS__ALIASED_OBJECT:
                return aliasedObject != null;
            case TransformationPackage.SQL_ALIAS__SQL_TRANSFORMATION:
                return getSqlTransformation() != null;
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
        result.append(" (alias: "); //$NON-NLS-1$
        result.append(alias);
        result.append(')');
        return result.toString();
    }

} //SqlAliasImpl
