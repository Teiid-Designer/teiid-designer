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

package com.metamatrix.metamodels.relational.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.LogicalRelationshipEnd;
import com.metamatrix.metamodels.relational.MultiplicityKind;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Table;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Logical Relationship End</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.LogicalRelationshipEndImpl#getMultiplicity <em>Multiplicity</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.LogicalRelationshipEndImpl#getTable <em>Table</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.LogicalRelationshipEndImpl#getRelationship <em>Relationship</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LogicalRelationshipEndImpl extends RelationalEntityImpl implements LogicalRelationshipEnd {
    /**
     * The default value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMultiplicity()
     * @generated
     * @ordered
     */
    protected static final MultiplicityKind MULTIPLICITY_EDEFAULT = MultiplicityKind.ONE_LITERAL;

    /**
     * The cached value of the '{@link #getMultiplicity() <em>Multiplicity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMultiplicity()
     * @generated
     * @ordered
     */
    protected MultiplicityKind multiplicity = MULTIPLICITY_EDEFAULT;

    /**
     * The cached value of the '{@link #getTable() <em>Table</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTable()
     * @generated
     * @ordered
     */
    protected Table table = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected LogicalRelationshipEndImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getLogicalRelationshipEnd();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MultiplicityKind getMultiplicity() {
        return multiplicity;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMultiplicity(MultiplicityKind newMultiplicity) {
        MultiplicityKind oldMultiplicity = multiplicity;
        multiplicity = newMultiplicity == null ? MULTIPLICITY_EDEFAULT : newMultiplicity;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.LOGICAL_RELATIONSHIP_END__MULTIPLICITY, oldMultiplicity, multiplicity));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Table getTable() {
        if (table != null && table.eIsProxy()) {
            Table oldTable = table;
            table = (Table)eResolveProxy((InternalEObject)table);
            if (table != oldTable) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE, oldTable, table));
            }
        }
        return table;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Table basicGetTable() {
        return table;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetTable(Table newTable, NotificationChain msgs) {
        Table oldTable = table;
        table = newTable;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE, oldTable, newTable);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTable(Table newTable) {
        if (newTable != table) {
            NotificationChain msgs = null;
            if (table != null)
                msgs = ((InternalEObject)table).eInverseRemove(this, RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS, Table.class, msgs);
            if (newTable != null)
                msgs = ((InternalEObject)newTable).eInverseAdd(this, RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS, Table.class, msgs);
            msgs = basicSetTable(newTable, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE, newTable, newTable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public LogicalRelationship getRelationship() {
        if (eContainerFeatureID != RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP) return null;
        return (LogicalRelationship)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRelationship(LogicalRelationship newRelationship) {
        if (newRelationship != eContainer || (eContainerFeatureID != RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP && newRelationship != null)) {
            if (EcoreUtil.isAncestor(this, newRelationship))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newRelationship != null)
                msgs = ((InternalEObject)newRelationship).eInverseAdd(this, RelationalPackage.LOGICAL_RELATIONSHIP__ENDS, LogicalRelationship.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newRelationship, RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP, newRelationship, newRelationship));
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
                case RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE:
                    if (table != null)
                        msgs = ((InternalEObject)table).eInverseRemove(this, RelationalPackage.TABLE__LOGICAL_RELATIONSHIPS, Table.class, msgs);
                    return basicSetTable((Table)otherEnd, msgs);
                case RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP, msgs);
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
                case RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE:
                    return basicSetTable(null, msgs);
                case RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP:
                    return eBasicSetContainer(null, RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP, msgs);
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
                case RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP:
                    return eContainer.eInverseRemove(this, RelationalPackage.LOGICAL_RELATIONSHIP__ENDS, LogicalRelationship.class, msgs);
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
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__NAME:
                return getName();
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__MULTIPLICITY:
                return getMultiplicity();
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE:
                if (resolve) return getTable();
                return basicGetTable();
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP:
                return getRelationship();
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
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__MULTIPLICITY:
                setMultiplicity((MultiplicityKind)newValue);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE:
                setTable((Table)newValue);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP:
                setRelationship((LogicalRelationship)newValue);
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
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__MULTIPLICITY:
                setMultiplicity(MULTIPLICITY_EDEFAULT);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE:
                setTable((Table)null);
                return;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP:
                setRelationship((LogicalRelationship)null);
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
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__MULTIPLICITY:
                return multiplicity != MULTIPLICITY_EDEFAULT;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__TABLE:
                return table != null;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__RELATIONSHIP:
                return getRelationship() != null;
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
        result.append(" (multiplicity: "); //$NON-NLS-1$
        result.append(multiplicity);
        result.append(')');
        return result.toString();
    }

} //LogicalRelationshipEndImpl
