/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.LogicalRelationshipEnd;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Table</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.TableImpl#isSystem <em>System</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.TableImpl#getCardinality <em>Cardinality</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.TableImpl#isSupportsUpdate <em>Supports Update</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.TableImpl#isMaterialized <em>Materialized</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.TableImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.TableImpl#getAccessPatterns <em>Access Patterns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.TableImpl#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.TableImpl#getLogicalRelationships <em>Logical Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class TableImpl extends ColumnSetImpl implements Table {
    /**
     * The default value of the '{@link #isSystem() <em>System</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSystem()
     * @generated
     * @ordered
     */
    protected static final boolean SYSTEM_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isSystem() <em>System</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSystem()
     * @generated
     * @ordered
     */
    protected boolean system = SYSTEM_EDEFAULT;

    /**
     * The default value of the '{@link #getCardinality() <em>Cardinality</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCardinality()
     * @generated
     * @ordered
     */
    protected static final int CARDINALITY_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getCardinality() <em>Cardinality</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCardinality()
     * @generated
     * @ordered
     */
    protected int cardinality = CARDINALITY_EDEFAULT;

    /**
     * The default value of the '{@link #isSupportsUpdate() <em>Supports Update</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSupportsUpdate()
     * @generated
     * @ordered
     */
    protected static final boolean SUPPORTS_UPDATE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isSupportsUpdate() <em>Supports Update</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSupportsUpdate()
     * @generated
     * @ordered
     */
    protected boolean supportsUpdate = SUPPORTS_UPDATE_EDEFAULT;

    /**
     * The default value of the '{@link #isMaterialized() <em>Materialized</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isMaterialized()
     * @generated
     * @ordered
     */
	protected static final boolean MATERIALIZED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isMaterialized() <em>Materialized</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isMaterialized()
     * @generated
     * @ordered
     */
	protected boolean materialized = MATERIALIZED_EDEFAULT;

    /**
     * The cached value of the '{@link #getAccessPatterns() <em>Access Patterns</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAccessPatterns()
     * @generated
     * @ordered
     */
    protected EList accessPatterns = null;

    /**
     * The cached value of the '{@link #getLogicalRelationships() <em>Logical Relationships</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLogicalRelationships()
     * @generated
     * @ordered
     */
    protected EList logicalRelationships = null;
    
    /**
     * The cached value of the '{@link #getMaterializedTable() <em>Materialized Table</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaterializedTable()
     * @generated
     * @ordered
     */
    protected Table materalizedTable = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TableImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getTable();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSystem() {
        return system;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSystem(boolean newSystem) {
        boolean oldSystem = system;
        system = newSystem;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.TABLE__SYSTEM, oldSystem, system));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getCardinality() {
        return cardinality;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCardinality(int newCardinality) {
        int oldCardinality = cardinality;
        cardinality = newCardinality;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.TABLE__CARDINALITY, oldCardinality, cardinality));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSupportsUpdate() {
        return supportsUpdate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public void setSupportsUpdate(boolean newSupportsUpdate) {
        boolean oldSupportsUpdate = supportsUpdate;
        supportsUpdate = newSupportsUpdate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.TABLE__SUPPORTS_UPDATE, oldSupportsUpdate, supportsUpdate));

        // Start customized code
        
        // If the new setting is different than the current and the new setting is 'not updateable'
        // (Per defects 10556 and 12383)
        // Note: Do not mark the columns as updatable if the table is marked as updateable!
        if ( oldSupportsUpdate != newSupportsUpdate && newSupportsUpdate == false) {
            // then mark all the columns as NOT updateable
            markColumnsAsUpdateable(false);
        }
        // End customized code
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSupportsUpdateGen(boolean newSupportsUpdate) {
        boolean oldSupportsUpdate = supportsUpdate;
        supportsUpdate = newSupportsUpdate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.TABLE__SUPPORTS_UPDATE, oldSupportsUpdate, supportsUpdate));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public boolean isMaterialized() {
        return materialized;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setMaterialized(boolean newMaterialized) {
        boolean oldMaterialized = materialized;
        materialized = newMaterialized;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.TABLE__MATERIALIZED, oldMaterialized, materialized));
    }

    protected void markColumnsAsUpdateable( final boolean newSupportsUpdate ) {
        // Mark the columns as not supporting updates ...
        final Iterator iter = new ArrayList(this.getColumns()).iterator();
        while (iter.hasNext()) {
            final Column column = (Column)iter.next();
            column.setUpdateable(newSupportsUpdate);
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Schema getSchema() {
        if (eContainerFeatureID != RelationalPackage.TABLE__SCHEMA) return null;
        return (Schema)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSchema(Schema newSchema) {
        if (newSchema != eContainer || (eContainerFeatureID != RelationalPackage.TABLE__SCHEMA && newSchema != null)) {
            if (EcoreUtil.isAncestor(this, newSchema))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSchema != null)
                msgs = ((InternalEObject)newSchema).eInverseAdd(this, RelationalPackage.SCHEMA__TABLES, Schema.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newSchema, RelationalPackage.TABLE__SCHEMA, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.TABLE__SCHEMA, newSchema, newSchema));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getAccessPatterns() {
        if (accessPatterns == null) {
            accessPatterns = new EObjectContainmentWithInverseEList(AccessPattern.class, this, RelationalPackage.TABLE__ACCESS_PATTERNS, RelationalPackage.ACCESS_PATTERN__TABLE);
        }
        return accessPatterns;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Catalog getCatalog() {
        if (eContainerFeatureID != RelationalPackage.TABLE__CATALOG) return null;
        return (Catalog)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCatalog(Catalog newCatalog) {
        if (newCatalog != eContainer || (eContainerFeatureID != RelationalPackage.TABLE__CATALOG && newCatalog != null)) {
            if (EcoreUtil.isAncestor(this, newCatalog))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newCatalog != null)
                msgs = ((InternalEObject)newCatalog).eInverseAdd(this, RelationalPackage.CATALOG__TABLES, Catalog.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newCatalog, RelationalPackage.TABLE__CATALOG, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.TABLE__CATALOG, newCatalog, newCatalog));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getLogicalRelationships() {
        if (logicalRelationships == null) {
            logicalRelationships = new EObjectWithInverseResolvingEList(LogicalRelationshipEnd.class, this, RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS, RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE);
        }
        return logicalRelationships;
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Table getMaterializedTable() {
        if (materalizedTable != null && materalizedTable.eIsProxy()) {
            Table oldMateralizedTable = materalizedTable;
            materalizedTable = (Table)eResolveProxy((InternalEObject)materalizedTable);
            if (materalizedTable != oldMateralizedTable) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, RelationalPackage.TABLE__MATERIALIZED_TABLE, oldMateralizedTable, materalizedTable));
            }
        }
        return materalizedTable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetMaterializedTable() {
        return materalizedTable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMaterializedTable(Table newMateralizedTable) {
        Table oldMateralizedTable = materalizedTable;
        materalizedTable = newMateralizedTable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.TABLE__MATERIALIZED_TABLE, oldMateralizedTable, materalizedTable));
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
                case RelationalPackage.TABLE__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.TABLE__SCHEMA:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.TABLE__SCHEMA, msgs);
                case RelationalPackage.TABLE__ACCESS_PATTERNS:
                    return ((InternalEList)getAccessPatterns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.TABLE__CATALOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.TABLE__CATALOG, msgs);
                case RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS:
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
                case RelationalPackage.TABLE__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.TABLE__SCHEMA:
                    return eBasicSetContainer(null, RelationalPackage.TABLE__SCHEMA, msgs);
                case RelationalPackage.TABLE__ACCESS_PATTERNS:
                    return ((InternalEList)getAccessPatterns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.TABLE__CATALOG:
                    return eBasicSetContainer(null, RelationalPackage.TABLE__CATALOG, msgs);
                case RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS:
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
                case RelationalPackage.TABLE__SCHEMA:
                    return eContainer.eInverseRemove(this, RelationalPackage.SCHEMA__TABLES, Schema.class, msgs);
                case RelationalPackage.TABLE__CATALOG:
                    return eContainer.eInverseRemove(this, RelationalPackage.CATALOG__TABLES, Catalog.class, msgs);
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
            case RelationalPackage.TABLE__NAME:
                return getName();
            case RelationalPackage.TABLE__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.TABLE__COLUMNS:
                return getColumns();
            case RelationalPackage.TABLE__SYSTEM:
                return isSystem() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.TABLE__CARDINALITY:
                return new Integer(getCardinality());
            case RelationalPackage.TABLE__SUPPORTS_UPDATE:
                return isSupportsUpdate() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.TABLE__MATERIALIZED:
                return isMaterialized() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.TABLE__SCHEMA:
                return getSchema();
            case RelationalPackage.TABLE__ACCESS_PATTERNS:
                return getAccessPatterns();
            case RelationalPackage.TABLE__CATALOG:
                return getCatalog();
            case RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS:
                return getLogicalRelationships();
            case RelationalPackage.TABLE__MATERIALIZED_TABLE:
                if (resolve) return getMaterializedTable();
                return basicGetMaterializedTable();
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
            case RelationalPackage.TABLE__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.TABLE__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.TABLE__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case RelationalPackage.TABLE__SYSTEM:
                setSystem(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.TABLE__CARDINALITY:
                setCardinality(((Integer)newValue).intValue());
                return;
            case RelationalPackage.TABLE__SUPPORTS_UPDATE:
                setSupportsUpdate(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.TABLE__MATERIALIZED:
                setMaterialized(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.TABLE__SCHEMA:
                setSchema((Schema)newValue);
                return;
            case RelationalPackage.TABLE__ACCESS_PATTERNS:
                getAccessPatterns().clear();
                getAccessPatterns().addAll((Collection)newValue);
                return;
            case RelationalPackage.TABLE__CATALOG:
                setCatalog((Catalog)newValue);
                return;
            case RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS:
                getLogicalRelationships().clear();
                getLogicalRelationships().addAll((Collection)newValue);
                return;
            case RelationalPackage.TABLE__MATERIALIZED_TABLE:
                setMaterializedTable((Table)newValue);
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
            case RelationalPackage.TABLE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.TABLE__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.TABLE__COLUMNS:
                getColumns().clear();
                return;
            case RelationalPackage.TABLE__SYSTEM:
                setSystem(SYSTEM_EDEFAULT);
                return;
            case RelationalPackage.TABLE__CARDINALITY:
                setCardinality(CARDINALITY_EDEFAULT);
                return;
            case RelationalPackage.TABLE__SUPPORTS_UPDATE:
                setSupportsUpdate(SUPPORTS_UPDATE_EDEFAULT);
                return;
            case RelationalPackage.TABLE__MATERIALIZED:
                setMaterialized(MATERIALIZED_EDEFAULT);
                return;
            case RelationalPackage.TABLE__SCHEMA:
                setSchema((Schema)null);
                return;
            case RelationalPackage.TABLE__ACCESS_PATTERNS:
                getAccessPatterns().clear();
                return;
            case RelationalPackage.TABLE__CATALOG:
                setCatalog((Catalog)null);
                return;
            case RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS:
                getLogicalRelationships().clear();
                return;
            case RelationalPackage.TABLE__MATERIALIZED_TABLE:
            	setMaterializedTable((Table)null);
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
            case RelationalPackage.TABLE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.TABLE__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.TABLE__COLUMNS:
                return columns != null && !columns.isEmpty();
            case RelationalPackage.TABLE__SYSTEM:
                return system != SYSTEM_EDEFAULT;
            case RelationalPackage.TABLE__CARDINALITY:
                return cardinality != CARDINALITY_EDEFAULT;
            case RelationalPackage.TABLE__SUPPORTS_UPDATE:
                return supportsUpdate != SUPPORTS_UPDATE_EDEFAULT;
            case RelationalPackage.TABLE__MATERIALIZED:
                return materialized != MATERIALIZED_EDEFAULT;
            case RelationalPackage.TABLE__SCHEMA:
                return getSchema() != null;
            case RelationalPackage.TABLE__ACCESS_PATTERNS:
                return accessPatterns != null && !accessPatterns.isEmpty();
            case RelationalPackage.TABLE__CATALOG:
                return getCatalog() != null;
            case RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS:
                return logicalRelationships != null && !logicalRelationships.isEmpty();
            case RelationalPackage.TABLE__MATERIALIZED_TABLE:
                return materalizedTable != null;
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
        result.append(" (system: "); //$NON-NLS-1$
        result.append(system);
        result.append(", cardinality: "); //$NON-NLS-1$
        result.append(cardinality);
        result.append(", supportsUpdate: "); //$NON-NLS-1$
        result.append(supportsUpdate);
        result.append(", materialized: "); //$NON-NLS-1$
        result.append(materialized);
        result.append(')');
        return result.toString();
    }

} //TableImpl
