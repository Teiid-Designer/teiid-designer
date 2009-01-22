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
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipContainer;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.RelationshipTypeStatus;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relationship</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipImpl#getOwnedRelationships <em>Owned Relationships</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipImpl#getFeatureValues <em>Feature Values</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipImpl#getTargets <em>Targets</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipImpl#getSources <em>Sources</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.RelationshipImpl#getRelationshipContainer <em>Relationship Container</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RelationshipImpl extends RelationshipEntityImpl implements Relationship {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

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
     * The cached value of the '{@link #getFeatureValues() <em>Feature Values</em>}' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFeatureValues()
     * @generated
     * @ordered
     */
    protected EMap featureValues = null;

    /**
     * The cached value of the '{@link #getTargets() <em>Targets</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTargets()
     * @generated
     * @ordered
     */
    protected EList targets = null;

    /**
     * The cached value of the '{@link #getSources() <em>Sources</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSources()
     * @generated
     * @ordered
     */
    protected EList sources = null;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected RelationshipType type = null;

    /**
     * This is true if the Type reference has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean typeESet = false;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RelationshipImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationshipPackage.eINSTANCE.getRelationship();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getOwnedRelationships() {
        if (ownedRelationships == null) {
            ownedRelationships = new EObjectContainmentWithInverseEList(Relationship.class, this, RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS, RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER);
        }
        return ownedRelationships;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMap getFeatureValues() {
        if (featureValues == null) {
            featureValues = new EcoreEMap(EcorePackage.eINSTANCE.getEStringToStringMapEntry(), EStringToStringMapEntryImpl.class, this, RelationshipPackage.RELATIONSHIP__FEATURE_VALUES);
        }
        return featureValues;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getTargets() {
        if (targets == null) {
            targets = new EObjectResolvingEList(EObject.class, this, RelationshipPackage.RELATIONSHIP__TARGETS);
        }
        return targets;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getSources() {
        if (sources == null) {
            sources = new EObjectResolvingEList(EObject.class, this, RelationshipPackage.RELATIONSHIP__SOURCES);
        }
        return sources;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipType getType() {
        if (type != null && type.eIsProxy()) {
            RelationshipType oldType = type;
            type = (RelationshipType)eResolveProxy((InternalEObject)type);
            if (type != oldType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, RelationshipPackage.RELATIONSHIP__TYPE, oldType, type));
            }
        }
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipType basicGetType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setType(RelationshipType newType) {
        RelationshipType oldType = type;
        type = newType;
        boolean oldTypeESet = typeESet;
        typeESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP__TYPE, oldType, type, !oldTypeESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetType() {
        RelationshipType oldType = type;
        boolean oldTypeESet = typeESet;
        type = null;
        typeESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, RelationshipPackage.RELATIONSHIP__TYPE, oldType, null, oldTypeESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetType() {
        return typeESet;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationshipContainer getRelationshipContainer() {
        if (eContainerFeatureID != RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER) return null;
        return (RelationshipContainer)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRelationshipContainer(RelationshipContainer newRelationshipContainer) {
        if (newRelationshipContainer != eContainer || (eContainerFeatureID != RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER && newRelationshipContainer != null)) {
            if (EcoreUtil.isAncestor(this, newRelationshipContainer))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newRelationshipContainer != null)
                msgs = ((InternalEObject)newRelationshipContainer).eInverseAdd(this, RelationshipPackage.RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS, RelationshipContainer.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newRelationshipContainer, RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER, newRelationshipContainer, newRelationshipContainer));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public RelationshipRole getSourceRole()
    {
        if ( this.type != null ) {
            return this.type.getSourceRole();
        }
        return null;
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
    public RelationshipRole getTargetRole()
    {
        if ( this.type != null ) {
            return this.type.getTargetRole();
        }
        return null;
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
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS:
                    return ((InternalEList)getOwnedRelationships()).basicAdd(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER, msgs);
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
     * @generated NOT
     */
    @Override
    public IStatus isValid() {

    	String relationName = this.getName();
		// the relationship should have a name    	
    	if(relationName == null) {
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_relationship_does_not_have_a_name._1"); //$NON-NLS-1$
			return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);    		
    	}
        // validate relationshiptype
        RelationshipType relationType = this.getType();
        List statuses = new ArrayList();
        if(relationType != null) {
        	// type name
        	String typeName = relationType.getName() != null ? 
                              relationType.getName() : 
                              RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.<<_Relationship_Type_>>_1"); //$NON-NLS-1$

        	// the relationship has reference a type that is valid 
			RelationshipTypeStatus typeStatus = relationType.getStatus();
			if(typeStatus.getValue() == RelationshipTypeStatus.INVALID) {
                final Object[] params = new Object[]{relationName, typeName};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_relationship_{0}_references_an_invalid_type_{1}._3", params); //$NON-NLS-1$
				return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);				
			} else if(typeStatus.getValue() == RelationshipTypeStatus.PROTOTYPE) {
                final Object[] params = new Object[]{relationName, typeName};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_relationship_{0}_references_an_proto_type_{1}._5",params ); //$NON-NLS-1$
                statuses.add(new Status(IStatus.WARNING, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null));
			} else if(typeStatus.getValue() == RelationshipTypeStatus.DEPRECATED) {
                final Object[] params = new Object[]{relationName, typeName};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_relationship_{0}_references_an_deprecated_type_{1}._4",params ); //$NON-NLS-1$
                statuses.add(new Status(IStatus.WARNING, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null));
            }

			// The relationship cannot reference an abstract relationship type
        	if(relationType.isAbstract()) {
                final Object[] params = new Object[]{relationName, typeName};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_relationship_{0}_references_an_abstarct_type_{1}._1", params);//$NON-NLS-1$
        		return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null); 
        	}

			/*
			 * If the relationship type does not support cross model relationships,
			 * then make sure the targets and sources of the relationship belong to the same model. 
			 */
        	if(!relationType.isCrossModel()) {
				String modelUri = null;
				boolean sameModel = true;
				// check if each of the sources belong to the same model
        		Iterator srcIter = this.getSources().iterator();
        		while(srcIter.hasNext()) {
        			EObject srcObj = (EObject) srcIter.next();
        			Resource srcResource = srcObj.eResource();
					String srcUri = srcResource.getURI().toString();
					if(modelUri != null) {
						if(!srcUri.equalsIgnoreCase(modelUri)) {
							sameModel = false;
						}
					} else {
						modelUri = srcUri;
					}
        		}

				// if all sources belong to the same model
				if(sameModel) {
					// check if each of the targets belong to the same model
					// as the sources
					Iterator tgtIter = this.getTargets().iterator();
					while(srcIter.hasNext()) {
						EObject tgtObj = (EObject) tgtIter.next();
						Resource tgtResource = tgtObj.eResource();
						String tgtUri = tgtResource.getURI().toString();
						if(modelUri != null) {
							if(!tgtUri.equalsIgnoreCase(modelUri)) {
								sameModel = false;
							}
						} else {
							modelUri = tgtUri;
						}
					}
				}

				if(!sameModel) {
                    final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_type_{0}_on_the_relationship_{1}_does_not_support_a_crossmodel_relationship,_one_or_more_sources/targets_belong_to_differrent_models._5", typeName, relationName); //$NON-NLS-1$
                    return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);
				}
        	}
        } else {
            // relationship is null
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.nullType", new Object[] {relationName}); //$NON-NLS-1$
            return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);              
        }

		RelationshipRole sourceRole = this.getSourceRole();
		if(sourceRole != null) {
			// validate the sources against the role's lower bound 
			int lowerBound = sourceRole.getLowerBound();
			if(lowerBound != -1 && this.getSources().size() < lowerBound) {

                final Object[] params = new Object[]{ sourceRole.getName(), new Integer(lowerBound)};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_number_of_items_in_the_{0}_table_is_less_than_the_lowerbound_of_{1}._10",params); //$NON-NLS-1$

				return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);
			}
			// validate the sources against the role's upper bound
			int upperBound = sourceRole.getUpperBound();
			if(upperBound != -1 && this.getSources().size() > upperBound) {

                  final Object[] params = new Object[]{ sourceRole.getName(), new Integer(upperBound)};
                  final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_number_of_items_in_the_{0}_table_is_greater_than_the_upperbound_of_{1}._11",params); //$NON-NLS-1$
				return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);
			}
            
            // validate the source participants in the role ...
            final Iterator iter = this.getSources().iterator();
            while (iter.hasNext()) {
                final EObject participant = (EObject)iter.next();
                // Check whether the participant is equivalent to this relationship ...
                if ( participant.equals(this) ) {
                    final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.A_relationship_may_not_have_itself_as_a_source"); //$NON-NLS-1$
                    final IStatus newStatus = new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null); 
                    statuses.add(newStatus);
                } else {
                    final IStatus participantStatus = sourceRole.isValidParticipant(participant);
                    if ( participantStatus.getSeverity() != IStatus.OK ) {
                        statuses.add(participantStatus);
                    }
                }
            }
		}

		RelationshipRole targetRole = this.getTargetRole();
		if(targetRole != null) {
			// validate the targets against the role's lower bound 
			int lowerBound = targetRole.getLowerBound();
			if(lowerBound != -1 && this.getTargets().size() < lowerBound) {

                final Object[] params = new Object[]{ targetRole.getName(), new Integer(lowerBound)};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_number_of_items_in_the_{0}_table_is_less_than_the_lowerbound_of_{1}._10",params); //$NON-NLS-1$
				return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null);
			}
			// validate the targets against the role's upper bound
			int upperBound = targetRole.getUpperBound();
			if(upperBound != -1 && this.getTargets().size() > upperBound) {

                final Object[] params = new Object[]{ targetRole.getName(), new Integer(upperBound)};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.The_number_of_items_in_the_{0}_table_is_greater_than_the_upperbound_of_{1}._11",params); //$NON-NLS-1$
				return new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null); 
			}
            
            // validate the target participants in the role ...
            final Iterator iter = this.getTargets().iterator();
            while (iter.hasNext()) {
                final EObject participant = (EObject)iter.next();
                // Check whether the participant is equivalent to this relationship ...
                if ( participant.equals(this) ) {
                    final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipImpl.A_relationship_may_not_have_itself_as_a_target"); //$NON-NLS-1$
                    final IStatus newStatus = new Status(IStatus.ERROR, RelationshipMetamodelPlugin.PLUGIN_ID, 0, msg, null); 
                    statuses.add(newStatus);
                } else {
                    final IStatus participantStatus = targetRole.isValidParticipant(participant);
                    if ( participantStatus.getSeverity() != IStatus.OK ) {
                        statuses.add(participantStatus);
                    }
                }
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
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS:
                    return ((InternalEList)getOwnedRelationships()).basicRemove(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP__FEATURE_VALUES:
                    return ((InternalEList)getFeatureValues()).basicRemove(otherEnd, msgs);
                case RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER:
                    return eBasicSetContainer(null, RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER, msgs);
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
                case RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER:
                    return eContainer.eInverseRemove(this, RelationshipPackage.RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS, RelationshipContainer.class, msgs);
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
            case RelationshipPackage.RELATIONSHIP__NAME:
                return getName();
            case RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS:
                return getOwnedRelationships();
            case RelationshipPackage.RELATIONSHIP__FEATURE_VALUES:
                return getFeatureValues();
            case RelationshipPackage.RELATIONSHIP__TARGETS:
                return getTargets();
            case RelationshipPackage.RELATIONSHIP__SOURCES:
                return getSources();
            case RelationshipPackage.RELATIONSHIP__TYPE:
                if (resolve) return getType();
                return basicGetType();
            case RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER:
                return getRelationshipContainer();
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
            case RelationshipPackage.RELATIONSHIP__NAME:
                setName((String)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS:
                getOwnedRelationships().clear();
                getOwnedRelationships().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP__FEATURE_VALUES:
                getFeatureValues().clear();
                getFeatureValues().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP__TARGETS:
                getTargets().clear();
                getTargets().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP__SOURCES:
                getSources().clear();
                getSources().addAll((Collection)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP__TYPE:
                setType((RelationshipType)newValue);
                return;
            case RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER:
                setRelationshipContainer((RelationshipContainer)newValue);
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
            case RelationshipPackage.RELATIONSHIP__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS:
                getOwnedRelationships().clear();
                return;
            case RelationshipPackage.RELATIONSHIP__FEATURE_VALUES:
                getFeatureValues().clear();
                return;
            case RelationshipPackage.RELATIONSHIP__TARGETS:
                getTargets().clear();
                return;
            case RelationshipPackage.RELATIONSHIP__SOURCES:
                getSources().clear();
                return;
            case RelationshipPackage.RELATIONSHIP__TYPE:
                unsetType();
                return;
            case RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER:
                setRelationshipContainer((RelationshipContainer)null);
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
            case RelationshipPackage.RELATIONSHIP__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS:
                return ownedRelationships != null && !ownedRelationships.isEmpty();
            case RelationshipPackage.RELATIONSHIP__FEATURE_VALUES:
                return featureValues != null && !featureValues.isEmpty();
            case RelationshipPackage.RELATIONSHIP__TARGETS:
                return targets != null && !targets.isEmpty();
            case RelationshipPackage.RELATIONSHIP__SOURCES:
                return sources != null && !sources.isEmpty();
            case RelationshipPackage.RELATIONSHIP__TYPE:
                return isSetType();
            case RelationshipPackage.RELATIONSHIP__RELATIONSHIP_CONTAINER:
                return getRelationshipContainer() != null;
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
        if (baseClass == RelationshipContainer.class) {
            switch (derivedFeatureID) {
                case RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS: return RelationshipPackage.RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS;
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
        if (baseClass == RelationshipContainer.class) {
            switch (baseFeatureID) {
                case RelationshipPackage.RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS: return RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS;
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

} //RelationshipImpl
