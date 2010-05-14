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
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Index</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.IndexImpl#getFilterCondition <em>Filter Condition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.IndexImpl#isNullable <em>Nullable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.IndexImpl#isAutoUpdate <em>Auto Update</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.IndexImpl#isUnique <em>Unique</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.IndexImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.IndexImpl#getColumns <em>Columns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.IndexImpl#getCatalog <em>Catalog</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IndexImpl extends RelationalEntityImpl implements Index {
    /**
     * The default value of the '{@link #getFilterCondition() <em>Filter Condition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFilterCondition()
     * @generated
     * @ordered
     */
    protected static final String FILTER_CONDITION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFilterCondition() <em>Filter Condition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFilterCondition()
     * @generated
     * @ordered
     */
    protected String filterCondition = FILTER_CONDITION_EDEFAULT;

    /**
     * The default value of the '{@link #isNullable() <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isNullable()
     * @generated
     * @ordered
     */
    protected static final boolean NULLABLE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isNullable() <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isNullable()
     * @generated
     * @ordered
     */
    protected boolean nullable = NULLABLE_EDEFAULT;

    /**
     * The default value of the '{@link #isAutoUpdate() <em>Auto Update</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAutoUpdate()
     * @generated
     * @ordered
     */
    protected static final boolean AUTO_UPDATE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isAutoUpdate() <em>Auto Update</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAutoUpdate()
     * @generated
     * @ordered
     */
    protected boolean autoUpdate = AUTO_UPDATE_EDEFAULT;

    /**
     * The default value of the '{@link #isUnique() <em>Unique</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUnique()
     * @generated
     * @ordered
     */
    protected static final boolean UNIQUE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isUnique() <em>Unique</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUnique()
     * @generated
     * @ordered
     */
    protected boolean unique = UNIQUE_EDEFAULT;

    /**
     * The cached value of the '{@link #getColumns() <em>Columns</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getColumns()
     * @generated
     * @ordered
     */
    protected EList columns = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected IndexImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getIndex();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getFilterCondition() {
        return filterCondition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFilterCondition(String newFilterCondition) {
        String oldFilterCondition = filterCondition;
        filterCondition = newFilterCondition;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.INDEX__FILTER_CONDITION, oldFilterCondition, filterCondition));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNullable(boolean newNullable) {
        boolean oldNullable = nullable;
        nullable = newNullable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.INDEX__NULLABLE, oldNullable, nullable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAutoUpdate(boolean newAutoUpdate) {
        boolean oldAutoUpdate = autoUpdate;
        autoUpdate = newAutoUpdate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.INDEX__AUTO_UPDATE, oldAutoUpdate, autoUpdate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUnique(boolean newUnique) {
        boolean oldUnique = unique;
        unique = newUnique;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.INDEX__UNIQUE, oldUnique, unique));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Schema getSchema() {
        if (eContainerFeatureID != RelationalPackage.INDEX__SCHEMA) return null;
        return (Schema)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSchema(Schema newSchema) {
        if (newSchema != eContainer || (eContainerFeatureID != RelationalPackage.INDEX__SCHEMA && newSchema != null)) {
            if (EcoreUtil.isAncestor(this, newSchema))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSchema != null)
                msgs = ((InternalEObject)newSchema).eInverseAdd(this, RelationalPackage.SCHEMA__INDEXES, Schema.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newSchema, RelationalPackage.INDEX__SCHEMA, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.INDEX__SCHEMA, newSchema, newSchema));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getColumns() {
        if (columns == null) {
            columns = new EObjectWithInverseResolvingEList.ManyInverse(Column.class, this, RelationalPackage.INDEX__COLUMNS, RelationalPackage.COLUMN__INDEXES);
        }
        return columns;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Catalog getCatalog() {
        if (eContainerFeatureID != RelationalPackage.INDEX__CATALOG) return null;
        return (Catalog)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCatalog(Catalog newCatalog) {
        if (newCatalog != eContainer || (eContainerFeatureID != RelationalPackage.INDEX__CATALOG && newCatalog != null)) {
            if (EcoreUtil.isAncestor(this, newCatalog))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newCatalog != null)
                msgs = ((InternalEObject)newCatalog).eInverseAdd(this, RelationalPackage.CATALOG__INDEXES, Catalog.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newCatalog, RelationalPackage.INDEX__CATALOG, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.INDEX__CATALOG, newCatalog, newCatalog));
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
                case RelationalPackage.INDEX__SCHEMA:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.INDEX__SCHEMA, msgs);
                case RelationalPackage.INDEX__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.INDEX__CATALOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.INDEX__CATALOG, msgs);
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
                case RelationalPackage.INDEX__SCHEMA:
                    return eBasicSetContainer(null, RelationalPackage.INDEX__SCHEMA, msgs);
                case RelationalPackage.INDEX__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.INDEX__CATALOG:
                    return eBasicSetContainer(null, RelationalPackage.INDEX__CATALOG, msgs);
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
                case RelationalPackage.INDEX__SCHEMA:
                    return eContainer.eInverseRemove(this, RelationalPackage.SCHEMA__INDEXES, Schema.class, msgs);
                case RelationalPackage.INDEX__CATALOG:
                    return eContainer.eInverseRemove(this, RelationalPackage.CATALOG__INDEXES, Catalog.class, msgs);
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
            case RelationalPackage.INDEX__NAME:
                return getName();
            case RelationalPackage.INDEX__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.INDEX__FILTER_CONDITION:
                return getFilterCondition();
            case RelationalPackage.INDEX__NULLABLE:
                return isNullable() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.INDEX__AUTO_UPDATE:
                return isAutoUpdate() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.INDEX__UNIQUE:
                return isUnique() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.INDEX__SCHEMA:
                return getSchema();
            case RelationalPackage.INDEX__COLUMNS:
                return getColumns();
            case RelationalPackage.INDEX__CATALOG:
                return getCatalog();
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
            case RelationalPackage.INDEX__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.INDEX__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.INDEX__FILTER_CONDITION:
                setFilterCondition((String)newValue);
                return;
            case RelationalPackage.INDEX__NULLABLE:
                setNullable(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.INDEX__AUTO_UPDATE:
                setAutoUpdate(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.INDEX__UNIQUE:
                setUnique(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.INDEX__SCHEMA:
                setSchema((Schema)newValue);
                return;
            case RelationalPackage.INDEX__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case RelationalPackage.INDEX__CATALOG:
                setCatalog((Catalog)newValue);
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
            case RelationalPackage.INDEX__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.INDEX__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.INDEX__FILTER_CONDITION:
                setFilterCondition(FILTER_CONDITION_EDEFAULT);
                return;
            case RelationalPackage.INDEX__NULLABLE:
                setNullable(NULLABLE_EDEFAULT);
                return;
            case RelationalPackage.INDEX__AUTO_UPDATE:
                setAutoUpdate(AUTO_UPDATE_EDEFAULT);
                return;
            case RelationalPackage.INDEX__UNIQUE:
                setUnique(UNIQUE_EDEFAULT);
                return;
            case RelationalPackage.INDEX__SCHEMA:
                setSchema((Schema)null);
                return;
            case RelationalPackage.INDEX__COLUMNS:
                getColumns().clear();
                return;
            case RelationalPackage.INDEX__CATALOG:
                setCatalog((Catalog)null);
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
            case RelationalPackage.INDEX__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.INDEX__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.INDEX__FILTER_CONDITION:
                return FILTER_CONDITION_EDEFAULT == null ? filterCondition != null : !FILTER_CONDITION_EDEFAULT.equals(filterCondition);
            case RelationalPackage.INDEX__NULLABLE:
                return nullable != NULLABLE_EDEFAULT;
            case RelationalPackage.INDEX__AUTO_UPDATE:
                return autoUpdate != AUTO_UPDATE_EDEFAULT;
            case RelationalPackage.INDEX__UNIQUE:
                return unique != UNIQUE_EDEFAULT;
            case RelationalPackage.INDEX__SCHEMA:
                return getSchema() != null;
            case RelationalPackage.INDEX__COLUMNS:
                return columns != null && !columns.isEmpty();
            case RelationalPackage.INDEX__CATALOG:
                return getCatalog() != null;
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
        result.append(" (filterCondition: "); //$NON-NLS-1$
        result.append(filterCondition);
        result.append(", nullable: "); //$NON-NLS-1$
        result.append(nullable);
        result.append(", autoUpdate: "); //$NON-NLS-1$
        result.append(autoUpdate);
        result.append(", unique: "); //$NON-NLS-1$
        result.append(unique);
        result.append(')');
        return result.toString();
    }

} //IndexImpl
