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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.util.RelationshipUtil;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Role</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#getStereotype <em>Stereotype</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#isOrdered <em>Ordered</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#isUnique <em>Unique</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#isNavigable <em>Navigable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#getLowerBound <em>Lower Bound</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#getUpperBound <em>Upper Bound</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#getRelationshipType <em>Relationship Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#getOppositeRole <em>Opposite Role</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#getIncludeTypes <em>Include Types</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl#getExcludeTypes <em>Exclude Types</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationshipRoleImpl extends RelationshipEntityImpl implements RelationshipRole {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

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
     * The default value of the '{@link #isOrdered() <em>Ordered</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isOrdered()
     * @generated
     * @ordered
     */
    protected static final boolean ORDERED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isOrdered() <em>Ordered</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isOrdered()
     * @generated
     * @ordered
     */
    protected boolean ordered = ORDERED_EDEFAULT;

    /**
     * The default value of the '{@link #isUnique() <em>Unique</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isUnique()
     * @generated
     * @ordered
     */
    protected static final boolean UNIQUE_EDEFAULT = true;

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
     * The default value of the '{@link #isNavigable() <em>Navigable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isNavigable()
     * @generated
     * @ordered
     */
    protected static final boolean NAVIGABLE_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isNavigable() <em>Navigable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isNavigable()
     * @generated
     * @ordered
     */
    protected boolean navigable = NAVIGABLE_EDEFAULT;

    /**
     * The default value of the '{@link #getLowerBound() <em>Lower Bound</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLowerBound()
     * @generated
     * @ordered
     */
    protected static final int LOWER_BOUND_EDEFAULT = 1;

    /**
     * The cached value of the '{@link #getLowerBound() <em>Lower Bound</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLowerBound()
     * @generated
     * @ordered
     */
    protected int lowerBound = LOWER_BOUND_EDEFAULT;

    /**
     * The default value of the '{@link #getUpperBound() <em>Upper Bound</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpperBound()
     * @generated
     * @ordered
     */
    protected static final int UPPER_BOUND_EDEFAULT = -1;

    /**
     * The cached value of the '{@link #getUpperBound() <em>Upper Bound</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpperBound()
     * @generated
     * @ordered
     */
    protected int upperBound = UPPER_BOUND_EDEFAULT;

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
     * The cached value of the '{@link #getIncludeTypes() <em>Include Types</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIncludeTypes()
     * @generated
     * @ordered
     */
    protected EList includeTypes = null;

    /**
     * The cached value of the '{@link #getExcludeTypes() <em>Exclude Types</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getExcludeTypes()
     * @generated
     * @ordered
     */
    protected EList excludeTypes = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RelationshipRoleImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationshipPackage.eINSTANCE.getRelationshipRole();
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
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_ROLE__STEREOTYPE, oldStereotype, stereotype));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isOrdered() {
        return ordered;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOrdered(boolean newOrdered) {
        boolean oldOrdered = ordered;
        ordered = newOrdered;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_ROLE__ORDERED, oldOrdered, ordered));
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
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_ROLE__UNIQUE, oldUnique, unique));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isNavigable() {
        return navigable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNavigable(boolean newNavigable) {
        boolean oldNavigable = navigable;
        navigable = newNavigable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_ROLE__NAVIGABLE, oldNavigable, navigable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLowerBound(int newLowerBound) {
        int oldLowerBound = lowerBound;
        lowerBound = newLowerBound;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_ROLE__LOWER_BOUND, oldLowerBound, lowerBound));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getUpperBound() {
        return upperBound;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUpperBound(int newUpperBound) {
        int oldUpperBound = upperBound;
        upperBound = newUpperBound;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_ROLE__UPPER_BOUND, oldUpperBound, upperBound));
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
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_ROLE__CONSTRAINT, oldConstraint, constraint));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipType getRelationshipType() {
        if (eContainerFeatureID != RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE) return null;
        return (RelationshipType)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRelationshipType(RelationshipType newRelationshipType) {
        if (newRelationshipType != eContainer || (eContainerFeatureID != RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE && newRelationshipType != null)) {
            if (EcoreUtil.isAncestor(this, newRelationshipType))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newRelationshipType != null)
                msgs = ((InternalEObject)newRelationshipType).eInverseAdd(this, RelationshipPackage.RELATIONSHIP_TYPE__ROLES, RelationshipType.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newRelationshipType, RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE, newRelationshipType, newRelationshipType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getIncludeTypes() {
        if (includeTypes == null) {
            includeTypes = new EObjectResolvingEList(EClass.class, this, RelationshipPackage.RELATIONSHIP_ROLE__INCLUDE_TYPES);
        }
        return includeTypes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getExcludeTypes() {
        if (excludeTypes == null) {
            excludeTypes = new EObjectResolvingEList(EClass.class, this, RelationshipPackage.RELATIONSHIP_ROLE__EXCLUDE_TYPES);
        }
        return excludeTypes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public RelationshipRole getOppositeRole() {
        final RelationshipType type = this.getRelationshipType();
        final List roles = type.getRoles();
        final int numRoles = roles.size();
        if ( numRoles < 2 ) {
            return null;
        }
        final RelationshipRole role1 = (RelationshipRole) roles.get(0);
        final RelationshipRole role2 = (RelationshipRole) roles.get(1);
        if ( role1.equals(this) ) {
            return role2;
        }
        return role1;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipRole getOppositeRoleGen() {
        // TODO: implement this method to return the 'Opposite Role' reference
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isSourceRole() {
        final RelationshipType type = this.getRelationshipType();
        final RelationshipRole sourceRole = type.getSourceRole();
        
        if ( this.equals(sourceRole) ) {
            return true;
        }
        return false;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSourceRoleGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isTargetRole() {
        final RelationshipType type = this.getRelationshipType();
        final RelationshipRole targetRole = type.getTargetRole();
        if ( this.equals(targetRole) ) {
            return true;
        }
        return false;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isTargetRoleGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public IStatus isValidParticipant(final EObject participant) {
        ArgCheck.isNotNull(participant);
        if ( participant.eIsProxy() ) {
            EcoreUtil.resolve(participant,participant);
        }
        return isValidParticipant(participant.eClass());
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public IStatus isValidParticipantGen(EObject participant) {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public IStatus isValidParticipant(final EClassifier participantType) {
        ArgCheck.isNotNull(participantType);
        if ( participantType instanceof EClass ) {
            String invalidReason = null;
            
            final EClass type = (EClass)participantType;
            // Check the includes ...
            final List includedTypes = this.getIncludeTypes();
            if ( includedTypes.size() != 0 ) {
                // Allowed by included types if one of the supertype is an ancestor of the type ...
                if ( !includedTypes.contains(type) && !RelationshipUtil.isAncestor(type,includedTypes) ) {
                    // There is at least one included type, and they aren't supertypes of 'classifier'
                    final Object[] params = new Object[]{getClassifierLabel(type),this.getName()};
                    invalidReason = RelationshipMetamodelPlugin.Util.getString("RelationshipRoleImpl.Instances_of_{0}_are_not_included_by_the_{1}_role_1",params); //$NON-NLS-1$
                }
            }
            if ( invalidReason == null ) {
                // Allowed by the included list, so check the excluded ...
                final List excludedTypes = this.getExcludeTypes();
                if ( excludedTypes.size() != 0 ) {
                    // Allowed by included types if one of the supertype is an ancestor of the type ...
                    if ( excludedTypes.contains(type) ) {
                        final Object[] params = new Object[]{getClassifierLabel(type),this.getName()};
                        invalidReason = RelationshipMetamodelPlugin.Util.getString("RelationshipRoleImpl.Instances_of_{0}_are_not_excluded_by_the_{1}_role_2",params); //$NON-NLS-1$
                    } else if ( RelationshipUtil.isAncestor(type, excludedTypes) ) {
                        final Object[] params = new Object[]{getClassifierLabel(type),this.getName()};
                        invalidReason = RelationshipMetamodelPlugin.Util.getString("RelationshipRoleImpl.{0}_is_a_subtype_of_a_metaclass_excluded_by_the_{1}_role_3",params); //$NON-NLS-1$
                    }
                }
            }
            if ( invalidReason != null ) {
                return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, invalidReason, null);
            }
            final Object[] params = new Object[]{getClassifierLabel(type),this.getName()};
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipRoleImpl.Instances_of_{0}_are_allowed_as_participants_in_the_{1}_role_4",params); //$NON-NLS-1$
            return new Status(IStatus.OK, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);
        }
        final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipRoleImpl.Only_model_objects_are_allowed_participants_5"); //$NON-NLS-1$
        return new Status(IStatus.OK, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public IStatus isValidParticipantGen(EClassifier participantType) {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    protected String getClassifierLabel( final EClass classifier ) {
        return ModelerCore.getMetamodelRegistry().getMetaClassLabel(classifier);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isAllowed(final EClassifier classifier)
    {
        final IStatus status = isValidParticipant(classifier);
        return status.isOK();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAllowedGen(EClassifier type) {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isAllowed(EObject particpant) {
        final IStatus status = isValidParticipant(particpant);
        return status.isOK();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAllowedGen(EObject particpant) {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public RelationshipRole getOverriddenRole() {
        // Get the supertype ...
        final RelationshipType type = this.getRelationshipType();
        if ( type != null ) {
            final RelationshipType supertype = this.getRelationshipType();
            if ( supertype != null ) {
                // Is this a source or target role?
                if ( this.isSourceRole() ) {
                    return supertype.getSourceRole();
                }
                return supertype.getTargetRole();
            }
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipRole getOverriddenRoleGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
    public IStatus isValid() {

		// get the bound of the role
		int lowerBound = getLowerBound();
		int upperBound = getUpperBound();
		// upper bound if set cannot be lower than the lower bound
		if(upperBound != -1 && upperBound < lowerBound) {
            final Object[] params = new Object[]{getName()};
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipRoleImpl.Upperbound_{0}_cannot_exceed_lowerbound",params); //$NON-NLS-1$
			return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);
		}
		
		Collection statuses = new ArrayList();

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
                case RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE, msgs);
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
                case RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE:
                    return eBasicSetContainer(null, RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE, msgs);
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
                case RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE:
                    return eContainer.eInverseRemove(this, RelationshipPackage.RELATIONSHIP_TYPE__ROLES, RelationshipType.class, msgs);
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
            case RelationshipPackage.RELATIONSHIP_ROLE__NAME:
                return getName();
            case RelationshipPackage.RELATIONSHIP_ROLE__STEREOTYPE:
                return getStereotype();
            case RelationshipPackage.RELATIONSHIP_ROLE__ORDERED:
                return isOrdered() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.RELATIONSHIP_ROLE__UNIQUE:
                return isUnique() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.RELATIONSHIP_ROLE__NAVIGABLE:
                return isNavigable() ? Boolean.TRUE : Boolean.FALSE;
            case RelationshipPackage.RELATIONSHIP_ROLE__LOWER_BOUND:
                return new Integer(getLowerBound());
            case RelationshipPackage.RELATIONSHIP_ROLE__UPPER_BOUND:
                return new Integer(getUpperBound());
            case RelationshipPackage.RELATIONSHIP_ROLE__CONSTRAINT:
                return getConstraint();
            case RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE:
                return getRelationshipType();
            case RelationshipPackage.RELATIONSHIP_ROLE__OPPOSITE_ROLE:
                return getOppositeRole();
            case RelationshipPackage.RELATIONSHIP_ROLE__INCLUDE_TYPES:
                return getIncludeTypes();
            case RelationshipPackage.RELATIONSHIP_ROLE__EXCLUDE_TYPES:
                return getExcludeTypes();
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
            case RelationshipPackage.RELATIONSHIP_ROLE__NAME:
                setName((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__STEREOTYPE:
                setStereotype((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__ORDERED:
                setOrdered(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__UNIQUE:
                setUnique(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__NAVIGABLE:
                setNavigable(((Boolean)newValue).booleanValue());
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__LOWER_BOUND:
                setLowerBound(((Integer)newValue).intValue());
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__UPPER_BOUND:
                setUpperBound(((Integer)newValue).intValue());
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__CONSTRAINT:
                setConstraint((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE:
                setRelationshipType((RelationshipType)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__INCLUDE_TYPES:
                getIncludeTypes().clear();
                getIncludeTypes().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__EXCLUDE_TYPES:
                getExcludeTypes().clear();
                getExcludeTypes().addAll((Collection)newValue);
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
            case RelationshipPackage.RELATIONSHIP_ROLE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__STEREOTYPE:
                setStereotype(STEREOTYPE_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__ORDERED:
                setOrdered(ORDERED_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__UNIQUE:
                setUnique(UNIQUE_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__NAVIGABLE:
                setNavigable(NAVIGABLE_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__LOWER_BOUND:
                setLowerBound(LOWER_BOUND_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__UPPER_BOUND:
                setUpperBound(UPPER_BOUND_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__CONSTRAINT:
                setConstraint(CONSTRAINT_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE:
                setRelationshipType((RelationshipType)null);
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__INCLUDE_TYPES:
                getIncludeTypes().clear();
                return;
            case RelationshipPackage.RELATIONSHIP_ROLE__EXCLUDE_TYPES:
                getExcludeTypes().clear();
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
            case RelationshipPackage.RELATIONSHIP_ROLE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationshipPackage.RELATIONSHIP_ROLE__STEREOTYPE:
                return STEREOTYPE_EDEFAULT == null ? stereotype != null : !STEREOTYPE_EDEFAULT.equals(stereotype);
            case RelationshipPackage.RELATIONSHIP_ROLE__ORDERED:
                return ordered != ORDERED_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_ROLE__UNIQUE:
                return unique != UNIQUE_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_ROLE__NAVIGABLE:
                return navigable != NAVIGABLE_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_ROLE__LOWER_BOUND:
                return lowerBound != LOWER_BOUND_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_ROLE__UPPER_BOUND:
                return upperBound != UPPER_BOUND_EDEFAULT;
            case RelationshipPackage.RELATIONSHIP_ROLE__CONSTRAINT:
                return CONSTRAINT_EDEFAULT == null ? constraint != null : !CONSTRAINT_EDEFAULT.equals(constraint);
            case RelationshipPackage.RELATIONSHIP_ROLE__RELATIONSHIP_TYPE:
                return getRelationshipType() != null;
            case RelationshipPackage.RELATIONSHIP_ROLE__OPPOSITE_ROLE:
                return getOppositeRole() != null;
            case RelationshipPackage.RELATIONSHIP_ROLE__INCLUDE_TYPES:
                return includeTypes != null && !includeTypes.isEmpty();
            case RelationshipPackage.RELATIONSHIP_ROLE__EXCLUDE_TYPES:
                return excludeTypes != null && !excludeTypes.isEmpty();
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
        result.append(" (stereotype: "); //$NON-NLS-1$
        result.append(stereotype);
        result.append(", ordered: "); //$NON-NLS-1$
        result.append(ordered);
        result.append(", unique: "); //$NON-NLS-1$
        result.append(unique);
        result.append(", navigable: "); //$NON-NLS-1$
        result.append(navigable);
        result.append(", lowerBound: "); //$NON-NLS-1$
        result.append(lowerBound);
        result.append(", upperBound: "); //$NON-NLS-1$
        result.append(upperBound);
        result.append(", constraint: "); //$NON-NLS-1$
        result.append(constraint);
        result.append(')');
        return result.toString();
    }

} //RelationshipRoleImpl
