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
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Schema</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.SchemaImpl#getTables <em>Tables</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.SchemaImpl#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.SchemaImpl#getProcedures <em>Procedures</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.SchemaImpl#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.SchemaImpl#getLogicalRelationships <em>Logical Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SchemaImpl extends RelationalEntityImpl implements Schema {
    /**
     * The cached value of the '{@link #getTables() <em>Tables</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTables()
     * @generated
     * @ordered
     */
    protected EList tables = null;

    /**
     * The cached value of the '{@link #getProcedures() <em>Procedures</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProcedures()
     * @generated
     * @ordered
     */
    protected EList procedures = null;

    /**
     * The cached value of the '{@link #getIndexes() <em>Indexes</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIndexes()
     * @generated
     * @ordered
     */
    protected EList indexes = null;

    /**
     * The cached value of the '{@link #getLogicalRelationships() <em>Logical Relationships</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLogicalRelationships()
     * @generated
     * @ordered
     */
    protected EList logicalRelationships = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SchemaImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getSchema();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getTables() {
        if (tables == null) {
            tables = new EObjectContainmentWithInverseEList(Table.class, this, RelationalPackage.SCHEMA__TABLES, RelationalPackage.TABLE__SCHEMA);
        }
        return tables;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Catalog getCatalog() {
        if (eContainerFeatureID != RelationalPackage.SCHEMA__CATALOG) return null;
        return (Catalog)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCatalog(Catalog newCatalog) {
        if (newCatalog != eContainer || (eContainerFeatureID != RelationalPackage.SCHEMA__CATALOG && newCatalog != null)) {
            if (EcoreUtil.isAncestor(this, newCatalog))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newCatalog != null)
                msgs = ((InternalEObject)newCatalog).eInverseAdd(this, RelationalPackage.CATALOG__SCHEMAS, Catalog.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newCatalog, RelationalPackage.SCHEMA__CATALOG, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.SCHEMA__CATALOG, newCatalog, newCatalog));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getProcedures() {
        if (procedures == null) {
            procedures = new EObjectContainmentWithInverseEList(Procedure.class, this, RelationalPackage.SCHEMA__PROCEDURES, RelationalPackage.PROCEDURE__SCHEMA);
        }
        return procedures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getIndexes() {
        if (indexes == null) {
            indexes = new EObjectContainmentWithInverseEList(Index.class, this, RelationalPackage.SCHEMA__INDEXES, RelationalPackage.INDEX__SCHEMA);
        }
        return indexes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getLogicalRelationships() {
        if (logicalRelationships == null) {
            logicalRelationships = new EObjectContainmentWithInverseEList(LogicalRelationship.class, this, RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS, RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA);
        }
        return logicalRelationships;
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
                case RelationalPackage.SCHEMA__TABLES:
                    return ((InternalEList)getTables()).basicAdd(otherEnd, msgs);
                case RelationalPackage.SCHEMA__CATALOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.SCHEMA__CATALOG, msgs);
                case RelationalPackage.SCHEMA__PROCEDURES:
                    return ((InternalEList)getProcedures()).basicAdd(otherEnd, msgs);
                case RelationalPackage.SCHEMA__INDEXES:
                    return ((InternalEList)getIndexes()).basicAdd(otherEnd, msgs);
                case RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS:
                    return ((InternalEList)getLogicalRelationships()).basicAdd(otherEnd, msgs);
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
                case RelationalPackage.SCHEMA__TABLES:
                    return ((InternalEList)getTables()).basicRemove(otherEnd, msgs);
                case RelationalPackage.SCHEMA__CATALOG:
                    return eBasicSetContainer(null, RelationalPackage.SCHEMA__CATALOG, msgs);
                case RelationalPackage.SCHEMA__PROCEDURES:
                    return ((InternalEList)getProcedures()).basicRemove(otherEnd, msgs);
                case RelationalPackage.SCHEMA__INDEXES:
                    return ((InternalEList)getIndexes()).basicRemove(otherEnd, msgs);
                case RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS:
                    return ((InternalEList)getLogicalRelationships()).basicRemove(otherEnd, msgs);
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
                case RelationalPackage.SCHEMA__CATALOG:
                    return eContainer.eInverseRemove(this, RelationalPackage.CATALOG__SCHEMAS, Catalog.class, msgs);
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
            case RelationalPackage.SCHEMA__NAME:
                return getName();
            case RelationalPackage.SCHEMA__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.SCHEMA__TABLES:
                return getTables();
            case RelationalPackage.SCHEMA__CATALOG:
                return getCatalog();
            case RelationalPackage.SCHEMA__PROCEDURES:
                return getProcedures();
            case RelationalPackage.SCHEMA__INDEXES:
                return getIndexes();
            case RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS:
                return getLogicalRelationships();
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
            case RelationalPackage.SCHEMA__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.SCHEMA__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.SCHEMA__TABLES:
                getTables().clear();
                getTables().addAll((Collection)newValue);
                return;
            case RelationalPackage.SCHEMA__CATALOG:
                setCatalog((Catalog)newValue);
                return;
            case RelationalPackage.SCHEMA__PROCEDURES:
                getProcedures().clear();
                getProcedures().addAll((Collection)newValue);
                return;
            case RelationalPackage.SCHEMA__INDEXES:
                getIndexes().clear();
                getIndexes().addAll((Collection)newValue);
                return;
            case RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS:
                getLogicalRelationships().clear();
                getLogicalRelationships().addAll((Collection)newValue);
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
            case RelationalPackage.SCHEMA__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.SCHEMA__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.SCHEMA__TABLES:
                getTables().clear();
                return;
            case RelationalPackage.SCHEMA__CATALOG:
                setCatalog((Catalog)null);
                return;
            case RelationalPackage.SCHEMA__PROCEDURES:
                getProcedures().clear();
                return;
            case RelationalPackage.SCHEMA__INDEXES:
                getIndexes().clear();
                return;
            case RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS:
                getLogicalRelationships().clear();
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
            case RelationalPackage.SCHEMA__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.SCHEMA__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.SCHEMA__TABLES:
                return tables != null && !tables.isEmpty();
            case RelationalPackage.SCHEMA__CATALOG:
                return getCatalog() != null;
            case RelationalPackage.SCHEMA__PROCEDURES:
                return procedures != null && !procedures.isEmpty();
            case RelationalPackage.SCHEMA__INDEXES:
                return indexes != null && !indexes.isEmpty();
            case RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS:
                return logicalRelationships != null && !logicalRelationships.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //SchemaImpl
