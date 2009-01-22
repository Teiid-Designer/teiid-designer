/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.RelationshipTypeStatus;
import com.metamatrix.metamodels.relationship.util.RelationshipTypeManager;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#isDirected <em>Directed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#isExclusive <em>Exclusive</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#isCrossModel <em>Cross Model</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#isAbstract <em>Abstract</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#isUserDefined <em>User Defined</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getStatus <em>Status</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getStereotype <em>Stereotype</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getOppositeLabel <em>Opposite Label</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getRelationshipFeatures <em>Relationship Features</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getSuperType <em>Super Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getSubType <em>Sub Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getRoles <em>Roles</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationshipTypeImpl extends RelationshipEntityImpl implements RelationshipType {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #isDirected() <em>Directed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDirected()
     * @generated
     * @ordered
     */
    protected static final boolean DIRECTED_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isDirected() <em>Directed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDirected()
     * @generated
     * @ordered
     */
    protected boolean directed = DIRECTED_EDEFAULT;

    /**
     * The default value of the '{@link #isExclusive() <em>Exclusive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isExclusive()
     * @generated
     * @ordered
     */
    protected static final boolean EXCLUSIVE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isExclusive() <em>Exclusive</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isExclusive()
     * @generated
     * @ordered
     */
    protected boolean exclusive = EXCLUSIVE_EDEFAULT;

    /**
     * The default value of the '{@link #isCrossModel() <em>Cross Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCrossModel()
     * @generated
     * @ordered
     */
    protected static final boolean CROSS_MODEL_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isCrossModel() <em>Cross Model</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isCrossModel()
     * @generated
     * @ordered
     */
    protected boolean crossModel = CROSS_MODEL_EDEFAULT;

    /**
     * The default value of the '{@link #isAbstract() <em>Abstract</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAbstract()
     * @generated
     * @ordered
     */
    protected static final boolean ABSTRACT_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isAbstract() <em>Abstract</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAbstract()
     * @generated
     * @ordered
     */
    protected boolean abstract_ = ABSTRACT_EDEFAULT;

    /**
     * The default value of the '{@link #isUserDefined() <em>User Defined</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUserDefined()
     * @generated
     * @ordered
     */
    protected static final boolean USER_DEFINED_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isUserDefined() <em>User Defined</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUserDefined()
     * @generated
     * @ordered
     */
    protected boolean userDefined = USER_DEFINED_EDEFAULT;

    /**
     * The default value of the '{@link #getStatus() <em>Status</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStatus()
     * @generated
     * @ordered
     */
    protected static final RelationshipTypeStatus STATUS_EDEFAULT = RelationshipTypeStatus.STANDARD_LITERAL;

    /**
     * The cached value of the '{@link #getStatus() <em>Status</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStatus()
     * @generated
     * @ordered
     */
    protected RelationshipTypeStatus status = STATUS_EDEFAULT;

    /**
     * The default value of the '{@link #getStereotype() <em>Stereotype</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStereotype()
     * @generated
     * @ordered
     */
    protected static final String STEREOTYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getStereotype() <em>Stereotype</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStereotype()
     * @generated
     * @ordered
     */
    protected String stereotype = STEREOTYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getConstraint() <em>Constraint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConstraint()
     * @generated
     * @ordered
     */
    protected static final String CONSTRAINT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getConstraint() <em>Constraint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConstraint()
     * @generated
     * @ordered
     */
    protected String constraint = CONSTRAINT_EDEFAULT;

    /**
     * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected static final String LABEL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected String label = LABEL_EDEFAULT;

    /**
     * The default value of the '{@link #getOppositeLabel() <em>Opposite Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOppositeLabel()
     * @generated
     * @ordered
     */
    protected static final String OPPOSITE_LABEL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getOppositeLabel() <em>Opposite Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOppositeLabel()
     * @generated
     * @ordered
     */
    protected String oppositeLabel = OPPOSITE_LABEL_EDEFAULT;

    /**
     * The cached value of the '{@link #getRelationshipFeatures() <em>Relationship Features</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRelationshipFeatures()
     * @generated
     * @ordered
     */
    protected EList relationshipFeatures = null;

    /**
     * The cached value of the '{@link #getSuperType() <em>Super Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSuperType()
     * @generated
     * @ordered
     */
    protected RelationshipType superType = null;

    /**
     * This is true if the Super Type reference has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean superTypeESet = false;

    /**
     * The cached value of the '{@link #getSubType() <em>Sub Type</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSubType()
     * @generated
     * @ordered
     */
    protected EList subType = null;

    /**
     * The cached value of the '{@link #getRoles() <em>Roles</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRoles()
     * @generated
     * @ordered
     */
    protected EList roles = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RelationshipTypeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationshipPackage.eINSTANCE.getRelationshipType();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isDirected() {
        return directed;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDirected(boolean newDirected) {
        boolean oldDirected = directed;
        directed = newDirected;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__DIRECTED, oldDirected, directed));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isExclusive() {
        return exclusive;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setExclusive(boolean newExclusive) {
        boolean oldExclusive = exclusive;
        exclusive = newExclusive;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__EXCLUSIVE, oldExclusive, exclusive));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isCrossModel() {
        return crossModel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCrossModel(boolean newCrossModel) {
        boolean oldCrossModel = crossModel;
        crossModel = newCrossModel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__CROSS_MODEL, oldCrossModel, crossModel));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAbstract() {
        return abstract_;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAbstract(boolean newAbstract) {
        boolean oldAbstract = abstract_;
        abstract_ = newAbstract;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__ABSTRACT, oldAbstract, abstract_));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isUserDefined() {
        return userDefined;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUserDefined(boolean newUserDefined) {
        boolean oldUserDefined = userDefined;
        userDefined = newUserDefined;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__USER_DEFINED, oldUserDefined, userDefined));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipTypeStatus getStatus() {
        return status;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setStatus(RelationshipTypeStatus newStatus) {
        RelationshipTypeStatus oldStatus = status;
        status = newStatus == null ? STATUS_EDEFAULT : newStatus;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__STATUS, oldStatus, status));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getStereotype() {
        return stereotype;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setStereotype(String newStereotype) {
        String oldStereotype = stereotype;
        stereotype = newStereotype;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__STEREOTYPE, oldStereotype, stereotype));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getConstraint() {
        return constraint;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setConstraint(String newConstraint) {
        String oldConstraint = constraint;
        constraint = newConstraint;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__CONSTRAINT, oldConstraint, constraint));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLabel() {
        return label;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLabel(String newLabel) {
        String oldLabel = label;
        label = newLabel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__LABEL, oldLabel, label));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getOppositeLabel() {
        return oppositeLabel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOppositeLabel(String newOppositeLabel) {
        String oldOppositeLabel = oppositeLabel;
        oppositeLabel = newOppositeLabel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__OPPOSITE_LABEL, oldOppositeLabel, oppositeLabel));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getRelationshipFeatures() {
        if (relationshipFeatures == null) {
            relationshipFeatures = new EObjectResolvingEList(EStructuralFeature.class, this, RelationshipPackage.RELATIONSHIP_TYPE__RELATIONSHIP_FEATURES);
        }
        return relationshipFeatures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipType getSuperType() {
        if (superType != null && superType.eIsProxy()) {
            RelationshipType oldSuperType = superType;
            superType = (RelationshipType)eResolveProxy((InternalEObject)superType);
            if (superType != oldSuperType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE, oldSuperType, superType));
            }
        }
        return superType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipType basicGetSuperType() {
        return superType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetSuperType(RelationshipType newSuperType, NotificationChain msgs) {
        RelationshipType oldSuperType = superType;
        superType = newSuperType;
        boolean oldSuperTypeESet = superTypeESet;
        superTypeESet = true;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE, oldSuperType, newSuperType, !oldSuperTypeESet);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSuperType(RelationshipType newSuperType) {
        if (newSuperType != superType) {
            NotificationChain msgs = null;
            if (superType != null)
                msgs = ((InternalEObject)superType).eInverseRemove(this, RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE, RelationshipType.class, msgs);
            if (newSuperType != null)
                msgs = ((InternalEObject)newSuperType).eInverseAdd(this, RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE, RelationshipType.class, msgs);
            msgs = basicSetSuperType(newSuperType, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else {
            boolean oldSuperTypeESet = superTypeESet;
            superTypeESet = true;
            if (eNotificationRequired())
                eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE, newSuperType, newSuperType, !oldSuperTypeESet));
    	}
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicUnsetSuperType(NotificationChain msgs) {
        RelationshipType oldSuperType = superType;
        superType = null;
        boolean oldSuperTypeESet = superTypeESet;
        superTypeESet = false;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.UNSET, RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE, oldSuperType, null, oldSuperTypeESet);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetSuperType() {
        if (superType != null) {
            NotificationChain msgs = null;
            msgs = ((InternalEObject)superType).eInverseRemove(this, RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE, RelationshipType.class, msgs);
            msgs = basicUnsetSuperType(msgs);
            if (msgs != null) msgs.dispatch();
        }
        else {
            boolean oldSuperTypeESet = superTypeESet;
            superTypeESet = false;
            if (eNotificationRequired())
                eNotify(new ENotificationImpl(this, Notification.UNSET, RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE, null, null, oldSuperTypeESet));
    	}
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetSuperType() {
        return superTypeESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getSubType() {
        if (subType == null) {
            subType = new EObjectWithInverseResolvingEList(RelationshipType.class, this, RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE, RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE);
        }
        return subType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getRoles() {
        if (roles == null) {
            roles = new EObjectContainmentWithInverseEList(RelationshipRole.class, this, RelationshipPackage.RELATIONSHIP_TYPE__ROLES, RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE);
        }
        return roles;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipFolder getOwner() {
        if (eContainerFeatureID != RelationshipPackage.RELATIONSHIP_TYPE__OWNER) return null;
        return (RelationshipFolder)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOwner(RelationshipFolder newOwner) {
        if (newOwner != eContainer || (eContainerFeatureID != RelationshipPackage.RELATIONSHIP_TYPE__OWNER && newOwner != null)) {
            if (EcoreUtil.isAncestor(this, newOwner))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOwner != null)
                msgs = ((InternalEObject)newOwner).eInverseAdd(this, RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES, RelationshipFolder.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOwner, RelationshipPackage.RELATIONSHIP_TYPE__OWNER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_TYPE__OWNER, newOwner, newOwner));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public RelationshipRole getSourceRole() {
        final List roles = this.getRoles();
        final int numRoles = roles.size();
        if ( numRoles < 1 ) {
            return null;
        }
        final RelationshipRole role1 = (RelationshipRole) roles.get(0);
        return role1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipRole getSourceRoleGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public RelationshipRole getTargetRole() {
        final List roles = this.getRoles();
        final int numRoles = roles.size();
        if ( numRoles < 2 ) {
            return null;
        }
        final RelationshipRole role1 = (RelationshipRole) roles.get(1);
        return role1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipRole getTargetRoleGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public List getAllRelationshipFeatures() {
        // Short circuit ...
        if ( this.superType == null ) {
            return this.getRelationshipFeatures();
        }
        final List allFeatures = new ArrayList();
        final List path = new ArrayList();
        doAddRelationshipFeatures(allFeatures,this,path);
        return allFeatures;
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List getAllRelationshipFeaturesGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * Recursive method that prevents processing loops in the supertype hierarchy.
     * @param allFeatures
     * @param startingType
     * @param pathOfSupertypesAlreadyProcessed
     */
    protected void doAddRelationshipFeatures( final List allFeatures, final RelationshipType startingType, 
                                              final List pathOfSupertypesAlreadyProcessed ) {
        if ( startingType == null ) {
            return;
        }
        // Stop if there is a loop in the supertype hierarchy ...
        if ( pathOfSupertypesAlreadyProcessed.contains(startingType) ) {
            return;
        }
        // Add the features of this object to the list ...
        pathOfSupertypesAlreadyProcessed.add(startingType);
        allFeatures.addAll(startingType.getRelationshipFeatures());
        
        // Delegate to the startingType's method ...
        doAddRelationshipFeatures(allFeatures,startingType.getSuperType(),pathOfSupertypesAlreadyProcessed);
    }
    
    protected boolean doDetectCycle( final RelationshipType type, final Set supertypesSeen ) {
        final RelationshipType theSuperType = type.getSuperType();
        if ( theSuperType == null ) {
            return false;       // no cycle
        }
        // Try to add to the set ...
        final boolean added = supertypesSeen.add(theSuperType);
        if ( !added ) {
            return true;
        }
        // Otherwise call recursively ...
        return doDetectCycle(theSuperType,supertypesSeen);
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
    public IStatus isValid() {
		
		String typeName = this.getName();
		// the relationship type should have a name
		if(typeName == null || typeName.trim().length() == 0) {
			return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.The_relationship_type_does_not_have_a_name._1"), null); //$NON-NLS-1$
		}

		// validator to validate the names of 		
		StringNameValidator validator = new StringNameValidator();
		// validate the names of relation ship features
		Iterator featureIter = this.getRelationshipFeatures().iterator();
		while(featureIter.hasNext()) {
			EStructuralFeature feature = (EStructuralFeature) featureIter.next();
			String featureName = feature.getName();
			String message = validator.checkValidName(featureName);
			if(message != null) {
				featureName = featureName != null ? featureName : RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.<<_Relationship_Type_Feature_>>_1"); //$NON-NLS-1$
				String errorMessage = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.The_feature_{0}_on_the_relationship_type_{1}_or_its_super_type,_has_an_invalid_name___{2}._2", featureName, typeName, message); //$NON-NLS-1$
				return new Status(IStatus.ERROR,  RelationshipMetamodelPlugin.PLUGIN_ID, 0, errorMessage, null);
			}
		}
        
        Collection statuses = new LinkedList();

        // Check the supertype hierarchy ...
        if ( this.superType != null ) {
            if ( this.superType.equals(this) ) {
                String errorMessage = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.The_supertype_references_causes_a_cycle_1"); //$NON-NLS-1$
                IStatus status = new Status(IStatus.ERROR,  RelationshipMetamodelPlugin.PLUGIN_ID, 0, errorMessage, null);
                statuses.add(status);
            } else {
                // See if there is a cycle ...
                final Set supertypesSeen = new HashSet();
                // Don't add this in case 'this' is a delegate of a proxy; instead, if there is a cycle
                // this will stop when it gets to the supertype again
                supertypesSeen.add(this.superType);
                final boolean cycle = doDetectCycle(this.superType,supertypesSeen);
                if ( cycle ) {
                    String errorMessage = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.The_supertype_hierarchy_has_a_cycle_2"); //$NON-NLS-1$
                    IStatus status = new Status(IStatus.ERROR,  RelationshipMetamodelPlugin.PLUGIN_ID, 0, errorMessage, null);
                    statuses.add(status);
                }
            }
        } else {
            // Only the Any built-in type doesn't have to have a supertype ...
            final boolean builtInAny = RelationshipTypeManager.getInstance().isBuiltInAnyRelationshipType(this);
            if ( !builtInAny ) {
                String errorMessage = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.MissingSupertype"); //$NON-NLS-1$
                IStatus status = new Status(IStatus.ERROR,  RelationshipMetamodelPlugin.PLUGIN_ID, 0, errorMessage, null);
                statuses.add(status);
            }
        }

		// get all relationship features
		List features = this.getAllRelationshipFeatures();
		// validate the names of the relationship type features
		// against each other ensure there are no duplicates
		Map duplicateMap = validator.getDuplicateNamesMap(features);
		// if there is at least one match, create a status
		if(duplicateMap.size() > 0) {
			Iterator keyIter = duplicateMap.keySet().iterator();
			while(keyIter.hasNext()) {
				EObject eObject = (EObject) keyIter.next();
				final String name = ModelerCore.getModelEditor().getName(eObject);
				Integer count = (Integer)duplicateMap.get(eObject);
				// create a new status and update the list
				IStatus status = new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.The_name_of_feature_{0}_on_the_relationship_type_{1}_or_one_of_its_super_types,_is_the_same_as_the_name_of_{2}_other_features._1", name, typeName, count), null); //$NON-NLS-1$
				statuses.add(status);
			}
		}
        
        // validate the roles
        final EList roles = this.getRoles();
        if(roles.size() != 2){
            //Must have exactly two roles
            String errorMessage = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.Relationship_type_must_have_exactly_two_roles_1"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR,  RelationshipMetamodelPlugin.PLUGIN_ID, 0, errorMessage, null);
            statuses.add(status);
        }else{
            //Both roles must have non-null, non-zero length, unique names
            final RelationshipRole roleA = (RelationshipRole)roles.get(0);
            final String roleAName = roleA.getName();
            if(roleAName == null || roleAName.trim().length() == 0){
                String errorMessage = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.Role_A_must_have_a_name_2"); //$NON-NLS-1$
                IStatus status = new Status(IStatus.ERROR,  RelationshipMetamodelPlugin.PLUGIN_ID, 0, errorMessage, null);
                statuses.add(status);
            }
                
            final RelationshipRole roleB = (RelationshipRole)roles.get(1);
            final String roleBName = roleB.getName();
            if(roleBName == null || roleBName.trim().length() == 0){
                String errorMessage = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.Role_B_must_have_a_name_3"); //$NON-NLS-1$
                IStatus status = new Status(IStatus.ERROR,  RelationshipMetamodelPlugin.PLUGIN_ID, 0, errorMessage, null);
                statuses.add(status);
            }
            
            if(roleAName != null && roleBName != null && roleAName.equalsIgnoreCase(roleBName) ){
                String errorMessage = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeImpl.Role_A_and_Role_B_may_not_have_the_same_case_insignificant_name_4"); //$NON-NLS-1$
                IStatus status = new Status(IStatus.ERROR,  RelationshipMetamodelPlugin.PLUGIN_ID, 0, errorMessage, null);
                statuses.add(status);
            }
        }
            
		
		// return a status using the collected statuses
		if(statuses.isEmpty()) {
			return new Status(IStatus.OK, RelationshipMetamodelPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
		} else if(statuses.size() == 1) {
			return (IStatus) statuses.iterator().next();
		} else {
			MultiStatus multistatus = new MultiStatus(RelationshipMetamodelPlugin.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
			Iterator statusIter = statuses.iterator();
			while(statusIter.hasNext()) {
				multistatus.add((IStatus) statusIter.next());
			}

			return multistatus;
		}		
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
                case RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE:
                    if (superType != null)
                        msgs = ((InternalEObject)superType).eInverseRemove(this, RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE, RelationshipType.class, msgs);
                    return basicSetSuperType((RelationshipType)otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE:
                    return ((InternalEList)getSubType()).basicAdd(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_TYPE__ROLES:
                    return ((InternalEList)getRoles()).basicAdd(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_TYPE__OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationshipPackage.RELATIONSHIP_TYPE__OWNER, msgs);
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
                case RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE:
                    return basicUnsetSuperType(msgs);
                case RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE:
                    return ((InternalEList)getSubType()).basicRemove(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_TYPE__ROLES:
                    return ((InternalEList)getRoles()).basicRemove(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP_TYPE__OWNER:
                    return eBasicSetContainer(null, RelationshipPackage.RELATIONSHIP_TYPE__OWNER, msgs);
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
                case RelationshipPackage.RELATIONSHIP_TYPE__OWNER:
                    return eContainer.eInverseRemove(this, RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES, RelationshipFolder.class, msgs);
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
            case RelationshipPackage.RELATIONSHIP_TYPE__NAME:
                return getName();
            case RelationshipPackage.RELATIONSHIP_TYPE__DIRECTED:
                return isDirected() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.RELATIONSHIP_TYPE__EXCLUSIVE:
                return isExclusive() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.RELATIONSHIP_TYPE__CROSS_MODEL:
                return isCrossModel() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.RELATIONSHIP_TYPE__ABSTRACT:
                return isAbstract() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.RELATIONSHIP_TYPE__USER_DEFINED:
                return isUserDefined() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.RELATIONSHIP_TYPE__STATUS:
                return getStatus();
            case RelationshipPackage.RELATIONSHIP_TYPE__STEREOTYPE:
                return getStereotype();
            case RelationshipPackage.RELATIONSHIP_TYPE__CONSTRAINT:
                return getConstraint();
            case RelationshipPackage.RELATIONSHIP_TYPE__LABEL:
                return getLabel();
            case RelationshipPackage.RELATIONSHIP_TYPE__OPPOSITE_LABEL:
                return getOppositeLabel();
            case RelationshipPackage.RELATIONSHIP_TYPE__RELATIONSHIP_FEATURES:
                return getRelationshipFeatures();
            case RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE:
                if (resolve) return getSuperType();
                return basicGetSuperType();
            case RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE:
                return getSubType();
            case RelationshipPackage.RELATIONSHIP_TYPE__ROLES:
                return getRoles();
            case RelationshipPackage.RELATIONSHIP_TYPE__OWNER:
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
            case RelationshipPackage.RELATIONSHIP_TYPE__NAME:
                setName((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__DIRECTED:
                setDirected(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__EXCLUSIVE:
                setExclusive(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__CROSS_MODEL:
                setCrossModel(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__ABSTRACT:
                setAbstract(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__USER_DEFINED:
                setUserDefined(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__STATUS:
                setStatus((RelationshipTypeStatus)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__STEREOTYPE:
                setStereotype((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__CONSTRAINT:
                setConstraint((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__LABEL:
                setLabel((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__OPPOSITE_LABEL:
                setOppositeLabel((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__RELATIONSHIP_FEATURES:
                getRelationshipFeatures().clear();
                getRelationshipFeatures().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE:
                setSuperType((RelationshipType)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE:
                getSubType().clear();
                getSubType().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__ROLES:
                getRoles().clear();
                getRoles().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__OWNER:
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
            case RelationshipPackage.RELATIONSHIP_TYPE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__DIRECTED:
                setDirected(DIRECTED_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__EXCLUSIVE:
                setExclusive(EXCLUSIVE_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__CROSS_MODEL:
                setCrossModel(CROSS_MODEL_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__ABSTRACT:
                setAbstract(ABSTRACT_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__USER_DEFINED:
                setUserDefined(USER_DEFINED_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__STATUS:
                setStatus(STATUS_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__STEREOTYPE:
                setStereotype(STEREOTYPE_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__CONSTRAINT:
                setConstraint(CONSTRAINT_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__LABEL:
                setLabel(LABEL_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__OPPOSITE_LABEL:
                setOppositeLabel(OPPOSITE_LABEL_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__RELATIONSHIP_FEATURES:
                getRelationshipFeatures().clear();
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE:
                unsetSuperType();
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE:
                getSubType().clear();
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__ROLES:
                getRoles().clear();
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__OWNER:
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
            case RelationshipPackage.RELATIONSHIP_TYPE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationshipPackage.RELATIONSHIP_TYPE__DIRECTED:
                return directed != DIRECTED_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_TYPE__EXCLUSIVE:
                return exclusive != EXCLUSIVE_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_TYPE__CROSS_MODEL:
                return crossModel != CROSS_MODEL_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_TYPE__ABSTRACT:
                return abstract_ != ABSTRACT_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_TYPE__USER_DEFINED:
                return userDefined != USER_DEFINED_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_TYPE__STATUS:
                return status != STATUS_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_TYPE__STEREOTYPE:
                return STEREOTYPE_EDEFAULT == null ? stereotype != null : !STEREOTYPE_EDEFAULT.equals(stereotype);
            case RelationshipPackage.RELATIONSHIP_TYPE__CONSTRAINT:
                return CONSTRAINT_EDEFAULT == null ? constraint != null : !CONSTRAINT_EDEFAULT.equals(constraint);
            case RelationshipPackage.RELATIONSHIP_TYPE__LABEL:
                return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
            case RelationshipPackage.RELATIONSHIP_TYPE__OPPOSITE_LABEL:
                return OPPOSITE_LABEL_EDEFAULT == null ? oppositeLabel != null : !OPPOSITE_LABEL_EDEFAULT.equals(oppositeLabel);
            case RelationshipPackage.RELATIONSHIP_TYPE__RELATIONSHIP_FEATURES:
                return relationshipFeatures != null && !relationshipFeatures.isEmpty();
            case RelationshipPackage.RELATIONSHIP_TYPE__SUPER_TYPE:
                return isSetSuperType();
            case RelationshipPackage.RELATIONSHIP_TYPE__SUB_TYPE:
                return subType != null && !subType.isEmpty();
            case RelationshipPackage.RELATIONSHIP_TYPE__ROLES:
                return roles != null && !roles.isEmpty();
            case RelationshipPackage.RELATIONSHIP_TYPE__OWNER:
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
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (directed: "); //$NON-NLS-1$
        result.append(directed);
        result.append(", exclusive: "); //$NON-NLS-1$
        result.append(exclusive);
        result.append(", crossModel: "); //$NON-NLS-1$
        result.append(crossModel);
        result.append(", abstract: "); //$NON-NLS-1$
        result.append(abstract_);
        result.append(", userDefined: "); //$NON-NLS-1$
        result.append(userDefined);
        result.append(", status: "); //$NON-NLS-1$
        result.append(status);
        result.append(", stereotype: "); //$NON-NLS-1$
        result.append(stereotype);
        result.append(", constraint: "); //$NON-NLS-1$
        result.append(constraint);
        result.append(", label: "); //$NON-NLS-1$
        result.append(label);
        result.append(", oppositeLabel: "); //$NON-NLS-1$
        result.append(oppositeLabel);
        result.append(')');
        return result.toString();
    }

} //RelationshipTypeImpl
