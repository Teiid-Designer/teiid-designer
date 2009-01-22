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
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.LogicalRelationshipEnd;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Logical Relationship</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.LogicalRelationshipImpl#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.LogicalRelationshipImpl#getSchema <em>Schema</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.LogicalRelationshipImpl#getEnds <em>Ends</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LogicalRelationshipImpl extends RelationshipImpl implements LogicalRelationship {
    /**
     * The cached value of the '{@link #getEnds() <em>Ends</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEnds()
     * @generated
     * @ordered
     */
    protected EList ends = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected LogicalRelationshipImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getLogicalRelationship();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Catalog getCatalog() {
        if (eContainerFeatureID != RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG) return null;
        return (Catalog)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCatalog(Catalog newCatalog) {
        if (newCatalog != eContainer || (eContainerFeatureID != RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG && newCatalog != null)) {
            if (EcoreUtil.isAncestor(this, newCatalog))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newCatalog != null)
                msgs = ((InternalEObject)newCatalog).eInverseAdd(this, RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS, Catalog.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newCatalog, RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG, newCatalog, newCatalog));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Schema getSchema() {
        if (eContainerFeatureID != RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA) return null;
        return (Schema)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSchema(Schema newSchema) {
        if (newSchema != eContainer || (eContainerFeatureID != RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA && newSchema != null)) {
            if (EcoreUtil.isAncestor(this, newSchema))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSchema != null)
                msgs = ((InternalEObject)newSchema).eInverseAdd(this, RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS, Schema.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newSchema, RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA, newSchema, newSchema));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getEnds() {
        if (ends == null) {
            ends = new EObjectContainmentWithInverseEList(LogicalRelationshipEnd.class, this, RelationalPackage.LOGICAL_RELATIONSHIP__ENDS, RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP);
        }
        return ends;
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
                case RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG, msgs);
                case RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA, msgs);
                case RelationalPackage.LOGICAL_RELATIONSHIP__ENDS:
                    return ((InternalEList)getEnds()).basicAdd(otherEnd, msgs);
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
                case RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG:
                    return eBasicSetContainer(null, RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG, msgs);
                case RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA:
                    return eBasicSetContainer(null, RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA, msgs);
                case RelationalPackage.LOGICAL_RELATIONSHIP__ENDS:
                    return ((InternalEList)getEnds()).basicRemove(otherEnd, msgs);
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
                case RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG:
                    return eContainer.eInverseRemove(this, RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS, Catalog.class, msgs);
                case RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA:
                    return eContainer.eInverseRemove(this, RelationalPackage.SCHEMA__LOGICAL_RELATIONSHIPS, Schema.class, msgs);
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
            case RelationalPackage.LOGICAL_RELATIONSHIP__NAME:
                return getName();
            case RelationalPackage.LOGICAL_RELATIONSHIP__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG:
                return getCatalog();
            case RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA:
                return getSchema();
            case RelationalPackage.LOGICAL_RELATIONSHIP__ENDS:
                return getEnds();
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
            case RelationalPackage.LOGICAL_RELATIONSHIP__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG:
                setCatalog((Catalog)newValue);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA:
                setSchema((Schema)newValue);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP__ENDS:
                getEnds().clear();
                getEnds().addAll((Collection)newValue);
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
            case RelationalPackage.LOGICAL_RELATIONSHIP__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG:
                setCatalog((Catalog)null);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA:
                setSchema((Schema)null);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP__ENDS:
                getEnds().clear();
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
            case RelationalPackage.LOGICAL_RELATIONSHIP__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.LOGICAL_RELATIONSHIP__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG:
                return getCatalog() != null;
            case RelationalPackage.LOGICAL_RELATIONSHIP__SCHEMA:
                return getSchema() != null;
            case RelationalPackage.LOGICAL_RELATIONSHIP__ENDS:
                return ends != null && !ends.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //LogicalRelationshipImpl
