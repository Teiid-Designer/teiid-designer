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

package com.metamatrix.metamodels.relationship.impl;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

import com.metamatrix.metamodels.relationship.PlaceholderReference;
import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipContainer;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Folder</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipFolderImpl#getPlaceholders <em>Placeholders</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipFolderImpl#getOwnedRelationships <em>Owned Relationships</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipFolderImpl#getOwnedRelationshipTypes <em>Owned Relationship Types</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipFolderImpl#getOwnedRelationshipFolders <em>Owned Relationship Folders</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipFolderImpl#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationshipFolderImpl extends RelationshipEntityImpl implements RelationshipFolder {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getPlaceholders() <em>Placeholders</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlaceholders()
     * @generated
     * @ordered
     */
    protected EList placeholders = null;

    /**
     * The cached value of the '{@link #getOwnedRelationships() <em>Owned Relationships</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOwnedRelationships()
     * @generated
     * @ordered
     */
    protected EList ownedRelationships = null;

    /**
     * The cached value of the '{@link #getOwnedRelationshipTypes() <em>Owned Relationship Types</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOwnedRelationshipTypes()
     * @generated
     * @ordered
     */
    protected EList ownedRelationshipTypes = null;

    /**
     * The cached value of the '{@link #getOwnedRelationshipFolders() <em>Owned Relationship Folders</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOwnedRelationshipFolders()
     * @generated
     * @ordered
     */
    protected EList ownedRelationshipFolders = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RelationshipFolderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationshipPackage.eINSTANCE.getRelationshipFolder();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getPlaceholders() {
        if (placeholders == null) {
            placeholders = new EObjectContainmentWithInverseEList(PlaceholderReference.class, this, RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS, RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER);
        }
        return placeholders;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getOwnedRelationships() {
        if (ownedRelationships == null) {
            ownedRelationships = new EObjectContainmentWithInverseEList(Relationship.class, this, RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS, RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER);
        }
        return ownedRelationships;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getOwnedRelationshipTypes() {
        if (ownedRelationshipTypes == null) {
            ownedRelationshipTypes = new EObjectContainmentWithInverseEList(RelationshipType.class, this, RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES, RelationshipPackage.RELATIONSHIP_TYPE__OWNER);
        }
        return ownedRelationshipTypes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getOwnedRelationshipFolders() {
        if (ownedRelationshipFolders == null) {
            ownedRelationshipFolders = new EObjectContainmentWithInverseEList(RelationshipFolder.class, this, RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS, RelationshipPackage.RELATIONSHIP_FOLDER__OWNER);
        }
        return ownedRelationshipFolders;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipFolder getOwner() {
        if (eContainerFeatureID != RelationshipPackage.RELATIONSHIP_FOLDER__OWNER) return null;
        return (RelationshipFolder)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOwner(RelationshipFolder newOwner) {
        if (newOwner != eContainer || (eContainerFeatureID != RelationshipPackage.RELATIONSHIP_FOLDER__OWNER && newOwner != null)) {
            if (EcoreUtil.isAncestor(this, newOwner))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOwner != null)
                msgs = ((InternalEObject)newOwner).eInverseAdd(this, RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS, RelationshipFolder.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOwner, RelationshipPackage.RELATIONSHIP_FOLDER__OWNER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_FOLDER__OWNER, newOwner, newOwner));
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
                case RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS:
                    return ((InternalEList)getPlaceholders()).basicAdd(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS:
                    return ((InternalEList)getOwnedRelationships()).basicAdd(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES:
                    return ((InternalEList)getOwnedRelationshipTypes()).basicAdd(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS:
                    return ((InternalEList)getOwnedRelationshipFolders()).basicAdd(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationshipPackage.RELATIONSHIP_FOLDER__OWNER, msgs);
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
                case RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS:
                    return ((InternalEList)getPlaceholders()).basicRemove(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS:
                    return ((InternalEList)getOwnedRelationships()).basicRemove(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES:
                    return ((InternalEList)getOwnedRelationshipTypes()).basicRemove(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS:
                    return ((InternalEList)getOwnedRelationshipFolders()).basicRemove(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNER:
                    return eBasicSetContainer(null, RelationshipPackage.RELATIONSHIP_FOLDER__OWNER, msgs);
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
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNER:
                    return eContainer.eInverseRemove(this, RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS, RelationshipFolder.class, msgs);
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
            case RelationshipPackage.RELATIONSHIP_FOLDER__NAME:
                return getName();
            case RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS:
                return getPlaceholders();
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS:
                return getOwnedRelationships();
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES:
                return getOwnedRelationshipTypes();
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS:
                return getOwnedRelationshipFolders();
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNER:
                return getOwner();
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
            case RelationshipPackage.RELATIONSHIP_FOLDER__NAME:
                setName((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS:
                getPlaceholders().clear();
                getPlaceholders().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS:
                getOwnedRelationships().clear();
                getOwnedRelationships().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES:
                getOwnedRelationshipTypes().clear();
                getOwnedRelationshipTypes().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS:
                getOwnedRelationshipFolders().clear();
                getOwnedRelationshipFolders().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNER:
                setOwner((RelationshipFolder)newValue);
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
            case RelationshipPackage.RELATIONSHIP_FOLDER__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS:
                getPlaceholders().clear();
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS:
                getOwnedRelationships().clear();
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES:
                getOwnedRelationshipTypes().clear();
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS:
                getOwnedRelationshipFolders().clear();
                return;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNER:
                setOwner((RelationshipFolder)null);
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
            case RelationshipPackage.RELATIONSHIP_FOLDER__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS:
                return placeholders != null && !placeholders.isEmpty();
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS:
                return ownedRelationships != null && !ownedRelationships.isEmpty();
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES:
                return ownedRelationshipTypes != null && !ownedRelationshipTypes.isEmpty();
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS:
                return ownedRelationshipFolders != null && !ownedRelationshipFolders.isEmpty();
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNER:
                return getOwner() != null;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class baseClass) {
        if (baseClass == PlaceholderReferenceContainer.class) {
            switch (derivedFeatureID) {
                case RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS: return RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS;
                default: return -1;
            }
        }
        if (baseClass == RelationshipContainer.class) {
            switch (derivedFeatureID) {
                case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS: return RelationshipPackage.RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS;
                default: return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class baseClass) {
        if (baseClass == PlaceholderReferenceContainer.class) {
            switch (baseFeatureID) {
                case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS: return RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS;
                default: return -1;
            }
        }
        if (baseClass == RelationshipContainer.class) {
            switch (baseFeatureID) {
                case RelationshipPackage.RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS: return RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS;
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public IStatus isValid() {
        final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipFolderImpl.Folder_is_valid"); //$NON-NLS-1$
        return new Status(IStatus.OK,RelationshipMetamodelPlugin.PLUGIN_ID,0,msg,null);
    }

} //RelationshipFolderImpl
