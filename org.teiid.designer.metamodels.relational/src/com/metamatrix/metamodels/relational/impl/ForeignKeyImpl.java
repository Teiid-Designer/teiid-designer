/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.CascadeDeletesType;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.MultiplicityKind;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.UniqueKey;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Foreign Key</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ForeignKeyImpl#getForeignKeyMultiplicity <em>Foreign Key Multiplicity</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ForeignKeyImpl#getPrimaryKeyMultiplicity <em>Primary Key Multiplicity</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ForeignKeyImpl#getColumns <em>Columns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ForeignKeyImpl#getUniqueKey <em>Unique Key</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ForeignKeyImpl#getTable <em>Table</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ForeignKeyImpl extends RelationshipImpl implements
                                                    ForeignKey {

    /**
     * The default value of the '{@link #getForeignKeyMultiplicity() <em>Foreign Key Multiplicity</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getForeignKeyMultiplicity()
     * @generated
     * @ordered
     */
    protected static final MultiplicityKind FOREIGN_KEY_MULTIPLICITY_EDEFAULT = MultiplicityKind.ZERO_TO_MANY_LITERAL;

    /**
     * The cached value of the '{@link #getForeignKeyMultiplicity() <em>Foreign Key Multiplicity</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getForeignKeyMultiplicity()
     * @generated 
     * @ordered
     */
    protected MultiplicityKind foreignKeyMultiplicity = FOREIGN_KEY_MULTIPLICITY_EDEFAULT;

    /**
     * The default value of the '{@link #getPrimaryKeyMultiplicity() <em>Primary Key Multiplicity</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getPrimaryKeyMultiplicity()
     * @generated
     * @ordered
     */
    protected static final MultiplicityKind PRIMARY_KEY_MULTIPLICITY_EDEFAULT = MultiplicityKind.ONE_LITERAL;

    /**
     * The cached value of the '{@link #getPrimaryKeyMultiplicity() <em>Primary Key Multiplicity</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getPrimaryKeyMultiplicity()
     * @generated @ordered
     */
    protected MultiplicityKind primaryKeyMultiplicity = PRIMARY_KEY_MULTIPLICITY_EDEFAULT;

    /**
     * The default value of the '{@link #getCascadeDeletes() <em>Cascade Deletes</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getCascadeDeletes()
     * @generated @ordered
     */
    protected static final CascadeDeletesType CASCADE_DELETES_EDEFAULT = CascadeDeletesType.UNSPECIFIED_LITERAL;

    /**
     * The cached value of the '{@link #getCascadeDeletes() <em>Cascade Deletes</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getCascadeDeletes()
     * @generated @ordered
     */
    protected CascadeDeletesType cascadeDeletes = CASCADE_DELETES_EDEFAULT;

    /**
     * The cached value of the '{@link #getColumns() <em>Columns</em>}' reference list. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getColumns()
     * @generated @ordered
     */
    protected EList columns = null;

    /**
     * The cached value of the '{@link #getUniqueKey() <em>Unique Key</em>}' reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getUniqueKey()
     * @generated @ordered
     */
    protected UniqueKey uniqueKey = null;

    /**
     * This is true if the Unique Key reference has been set. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated @ordered
     */
    protected boolean uniqueKeyESet = false;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected ForeignKeyImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getForeignKey();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public MultiplicityKind getForeignKeyMultiplicity() {
        return foreignKeyMultiplicity;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setForeignKeyMultiplicity(MultiplicityKind newForeignKeyMultiplicity) {
        MultiplicityKind oldForeignKeyMultiplicity = foreignKeyMultiplicity;
        foreignKeyMultiplicity = newForeignKeyMultiplicity == null ? FOREIGN_KEY_MULTIPLICITY_EDEFAULT : newForeignKeyMultiplicity;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.FOREIGN_KEY__FOREIGN_KEY_MULTIPLICITY, oldForeignKeyMultiplicity, foreignKeyMultiplicity));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public MultiplicityKind getPrimaryKeyMultiplicity() {
        return primaryKeyMultiplicity;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setPrimaryKeyMultiplicity(MultiplicityKind newPrimaryKeyMultiplicity) {
        MultiplicityKind oldPrimaryKeyMultiplicity = primaryKeyMultiplicity;
        primaryKeyMultiplicity = newPrimaryKeyMultiplicity == null ? PRIMARY_KEY_MULTIPLICITY_EDEFAULT : newPrimaryKeyMultiplicity;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.FOREIGN_KEY__PRIMARY_KEY_MULTIPLICITY, oldPrimaryKeyMultiplicity, primaryKeyMultiplicity));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EList getColumns() {
        if (columns == null) {
            columns = new EObjectWithInverseResolvingEList.ManyInverse(Column.class, this, RelationalPackage.FOREIGN_KEY__COLUMNS, RelationalPackage.COLUMN__FOREIGN_KEYS);
        }
        return columns;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public UniqueKey getUniqueKey() {
        if (uniqueKey != null && uniqueKey.eIsProxy()) {
            UniqueKey oldUniqueKey = uniqueKey;
            uniqueKey = (UniqueKey)eResolveProxy((InternalEObject)uniqueKey);
            if (uniqueKey != oldUniqueKey) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, RelationalPackage.FOREIGN_KEY__UNIQUE_KEY, oldUniqueKey, uniqueKey));
            }
        }
        return uniqueKey;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public UniqueKey basicGetUniqueKey() {
        return uniqueKey;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetUniqueKey(UniqueKey newUniqueKey, NotificationChain msgs) {
        UniqueKey oldUniqueKey = uniqueKey;
        uniqueKey = newUniqueKey;
        boolean oldUniqueKeyESet = uniqueKeyESet;
        uniqueKeyESet = true;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, RelationalPackage.FOREIGN_KEY__UNIQUE_KEY, oldUniqueKey, newUniqueKey, !oldUniqueKeyESet);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setUniqueKey(UniqueKey newUniqueKey) {
        if (newUniqueKey != uniqueKey) {
            NotificationChain msgs = null;
            if (uniqueKey != null)
                msgs = ((InternalEObject)uniqueKey).eInverseRemove(this, RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS, UniqueKey.class, msgs);
            if (newUniqueKey != null)
                msgs = ((InternalEObject)newUniqueKey).eInverseAdd(this, RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS, UniqueKey.class, msgs);
            msgs = basicSetUniqueKey(newUniqueKey, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else {
            boolean oldUniqueKeyESet = uniqueKeyESet;
            uniqueKeyESet = true;
            if (eNotificationRequired())
                eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.FOREIGN_KEY__UNIQUE_KEY, newUniqueKey, newUniqueKey, !oldUniqueKeyESet));
    	}
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicUnsetUniqueKey(NotificationChain msgs) {
        UniqueKey oldUniqueKey = uniqueKey;
        uniqueKey = null;
        boolean oldUniqueKeyESet = uniqueKeyESet;
        uniqueKeyESet = false;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.UNSET, RelationalPackage.FOREIGN_KEY__UNIQUE_KEY, oldUniqueKey, null, oldUniqueKeyESet);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void unsetUniqueKey() {
        if (uniqueKey != null) {
            NotificationChain msgs = null;
            msgs = ((InternalEObject)uniqueKey).eInverseRemove(this, RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS, UniqueKey.class, msgs);
            msgs = basicUnsetUniqueKey(msgs);
            if (msgs != null) msgs.dispatch();
        }
        else {
            boolean oldUniqueKeyESet = uniqueKeyESet;
            uniqueKeyESet = false;
            if (eNotificationRequired())
                eNotify(new ENotificationImpl(this, Notification.UNSET, RelationalPackage.FOREIGN_KEY__UNIQUE_KEY, null, null, oldUniqueKeyESet));
    	}
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetUniqueKey() {
        return uniqueKeyESet;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public BaseTable getTable() {
        if (eContainerFeatureID != RelationalPackage.FOREIGN_KEY__TABLE) return null;
        return (BaseTable)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setTable(BaseTable newTable) {
        if (newTable != eContainer || (eContainerFeatureID != RelationalPackage.FOREIGN_KEY__TABLE && newTable != null)) {
            if (EcoreUtil.isAncestor(this, newTable))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newTable != null)
                msgs = ((InternalEObject)newTable).eInverseAdd(this, RelationalPackage.BASE_TABLE__FOREIGN_KEYS, BaseTable.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newTable, RelationalPackage.FOREIGN_KEY__TABLE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.FOREIGN_KEY__TABLE, newTable, newTable));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationalPackage.FOREIGN_KEY__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.FOREIGN_KEY__UNIQUE_KEY:
                    if (uniqueKey != null)
                        msgs = ((InternalEObject)uniqueKey).eInverseRemove(this, RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS, UniqueKey.class, msgs);
                    return basicSetUniqueKey((UniqueKey)otherEnd, msgs);
                case RelationalPackage.FOREIGN_KEY__TABLE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.FOREIGN_KEY__TABLE, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationalPackage.FOREIGN_KEY__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.FOREIGN_KEY__UNIQUE_KEY:
                    return basicUnsetUniqueKey(msgs);
                case RelationalPackage.FOREIGN_KEY__TABLE:
                    return eBasicSetContainer(null, RelationalPackage.FOREIGN_KEY__TABLE, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case RelationalPackage.FOREIGN_KEY__TABLE:
                    return eContainer.eInverseRemove(this, RelationalPackage.BASE_TABLE__FOREIGN_KEYS, BaseTable.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.FOREIGN_KEY__NAME:
                return getName();
            case RelationalPackage.FOREIGN_KEY__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.FOREIGN_KEY__FOREIGN_KEY_MULTIPLICITY:
                return getForeignKeyMultiplicity();
            case RelationalPackage.FOREIGN_KEY__PRIMARY_KEY_MULTIPLICITY:
                return getPrimaryKeyMultiplicity();
            case RelationalPackage.FOREIGN_KEY__COLUMNS:
                return getColumns();
            case RelationalPackage.FOREIGN_KEY__UNIQUE_KEY:
                if (resolve) return getUniqueKey();
                return basicGetUniqueKey();
            case RelationalPackage.FOREIGN_KEY__TABLE:
                return getTable();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.FOREIGN_KEY__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.FOREIGN_KEY__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.FOREIGN_KEY__FOREIGN_KEY_MULTIPLICITY:
                setForeignKeyMultiplicity((MultiplicityKind)newValue);
                return;
            case RelationalPackage.FOREIGN_KEY__PRIMARY_KEY_MULTIPLICITY:
                setPrimaryKeyMultiplicity((MultiplicityKind)newValue);
                return;
            case RelationalPackage.FOREIGN_KEY__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case RelationalPackage.FOREIGN_KEY__UNIQUE_KEY:
                setUniqueKey((UniqueKey)newValue);
                return;
            case RelationalPackage.FOREIGN_KEY__TABLE:
                setTable((BaseTable)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.FOREIGN_KEY__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.FOREIGN_KEY__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.FOREIGN_KEY__FOREIGN_KEY_MULTIPLICITY:
                setForeignKeyMultiplicity(FOREIGN_KEY_MULTIPLICITY_EDEFAULT);
                return;
            case RelationalPackage.FOREIGN_KEY__PRIMARY_KEY_MULTIPLICITY:
                setPrimaryKeyMultiplicity(PRIMARY_KEY_MULTIPLICITY_EDEFAULT);
                return;
            case RelationalPackage.FOREIGN_KEY__COLUMNS:
                getColumns().clear();
                return;
            case RelationalPackage.FOREIGN_KEY__UNIQUE_KEY:
                unsetUniqueKey();
                return;
            case RelationalPackage.FOREIGN_KEY__TABLE:
                setTable((BaseTable)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.FOREIGN_KEY__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.FOREIGN_KEY__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.FOREIGN_KEY__FOREIGN_KEY_MULTIPLICITY:
                return foreignKeyMultiplicity != FOREIGN_KEY_MULTIPLICITY_EDEFAULT;
            case RelationalPackage.FOREIGN_KEY__PRIMARY_KEY_MULTIPLICITY:
                return primaryKeyMultiplicity != PRIMARY_KEY_MULTIPLICITY_EDEFAULT;
            case RelationalPackage.FOREIGN_KEY__COLUMNS:
                return columns != null && !columns.isEmpty();
            case RelationalPackage.FOREIGN_KEY__UNIQUE_KEY:
                return isSetUniqueKey();
            case RelationalPackage.FOREIGN_KEY__TABLE:
                return getTable() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (foreignKeyMultiplicity: "); //$NON-NLS-1$
        result.append(foreignKeyMultiplicity);
        result.append(", primaryKeyMultiplicity: "); //$NON-NLS-1$
        result.append(primaryKeyMultiplicity);
        result.append(')');
        return result.toString();
    }

} //ForeignKeyImpl
