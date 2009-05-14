/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipType;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relationship.RelationshipType} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class RelationshipTypeItemProvider extends RelationshipEntityItemProvider {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationshipTypeItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addDirectedPropertyDescriptor(object);
            addExclusivePropertyDescriptor(object);
            addCrossModelPropertyDescriptor(object);
            addAbstractPropertyDescriptor(object);
            // addUserDefinedPropertyDescriptor(object);
            addStatusPropertyDescriptor(object);
            addStereotypePropertyDescriptor(object);
            // addConstraintPropertyDescriptor(object); NOT YET IMPLEMENTED in 4.1
            addOppositeLabelPropertyDescriptor(object);
            addLabelPropertyDescriptor(object);
            // addRelationshipFeaturesPropertyDescriptor(object); NOT YET IMPLEMENTED in 4.1
            addSuperTypePropertyDescriptor(object);
            addSubTypePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List getPropertyDescriptorsGen( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addDirectedPropertyDescriptor(object);
            addExclusivePropertyDescriptor(object);
            addCrossModelPropertyDescriptor(object);
            addAbstractPropertyDescriptor(object);
            addUserDefinedPropertyDescriptor(object);
            addStatusPropertyDescriptor(object);
            addStereotypePropertyDescriptor(object);
            addConstraintPropertyDescriptor(object);
            addLabelPropertyDescriptor(object);
            addOppositeLabelPropertyDescriptor(object);
            addRelationshipFeaturesPropertyDescriptor(object);
            addSuperTypePropertyDescriptor(object);
            addSubTypePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Directed feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addDirectedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_directed_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_directed_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Directed(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_directed_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Directed feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDirectedPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_directed_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_directed_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Directed(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Exclusive feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addExclusivePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_exclusive_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_exclusive_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Exclusive(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_exclusive_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Exclusive feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addExclusivePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_exclusive_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_exclusive_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Exclusive(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Cross Model feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addCrossModelPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_crossModel_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_crossModel_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_CrossModel(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_crossModel_feature_category"), //$NON-NLS-1$
                                                                 // end customized cocde
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Cross Model feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addCrossModelPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_crossModel_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_crossModel_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_CrossModel(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Abstract feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addAbstractPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_abstract_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_abstract_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Abstract(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_abstract_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Abstract feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addAbstractPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_abstract_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_abstract_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Abstract(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the User Defined feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addUserDefinedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_userDefined_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_userDefined_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_UserDefined(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_userDefined_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the User Defined feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUserDefinedPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_userDefined_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_userDefined_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_UserDefined(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Status feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addStatusPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_status_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_status_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Status(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_status_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Status feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addStatusPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_status_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_status_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Status(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Stereotype feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addStereotypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_stereotype_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_stereotype_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Stereotype(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_stereotype_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Stereotype feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addStereotypePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_stereotype_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_stereotype_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Stereotype(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Constraint feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addConstraintPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_constraint_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_constraint_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Constraint(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_constraint_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Constraint feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addConstraintPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_constraint_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_constraint_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Constraint(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Label feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addLabelPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_label_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_label_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Label(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_label_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Label feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addLabelPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_label_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_label_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_Label(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Opposite Label feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addOppositeLabelPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_oppositeLabel_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_oppositeLabel_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_OppositeLabel(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_oppositeLabel_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Opposite Label feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addOppositeLabelPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_oppositeLabel_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_oppositeLabel_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_OppositeLabel(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Relationship Features feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addRelationshipFeaturesPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_relationshipFeatures_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_relationshipFeatures_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_RelationshipFeatures(),
                                                                 true,
                                                                 null,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_relationshipFeatures_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Relationship Features feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addRelationshipFeaturesPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_relationshipFeatures_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_relationshipFeatures_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_RelationshipFeatures(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Super Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSuperTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_superType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_superType_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_SuperType(),
                                                                 false,
                                                                 null,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipType_superType_feature_category"), //$NON-NLS-1$
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Super Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSuperTypePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_superType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_superType_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_SuperType(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Sub Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSubTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_subType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_subType_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_SubType(),
                                                                 false,
                                                                 null,
                                                                 getString("_UI_RelationshipType_subType_feature_category"), //$NON-NLS-1$
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Sub Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSubTypePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipType_subType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipType_subType_feature", "_UI_RelationshipType_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipType_SubType(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Collection getChildrenFeatures( Object object ) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(RelationshipPackage.eINSTANCE.getRelationshipType_Roles());
        }
        return childrenFeatures;
    }

    /**
     * This returns RelationshipType.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/RelationshipType"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        final RelationshipType type = (RelationshipType)object;
        String label = type.getName();
        if (label == null || label.trim().length() == 0) {
            label = getString("_UI_RelationshipType_type"); //$NON-NLS-1$
        }
        return label;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getTextGen( Object object ) {
        String label = ((RelationshipType)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_RelationshipType_type") : //$NON-NLS-1$
        getString("_UI_RelationshipType_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating a viewer
     * notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void notifyChanged( Notification notification ) {
        updateChildren(notification);

        switch (notification.getFeatureID(RelationshipType.class)) {
            case RelationshipPackage.RELATIONSHIP_TYPE__DIRECTED:
            case RelationshipPackage.RELATIONSHIP_TYPE__EXCLUSIVE:
            case RelationshipPackage.RELATIONSHIP_TYPE__CROSS_MODEL:
            case RelationshipPackage.RELATIONSHIP_TYPE__ABSTRACT:
            case RelationshipPackage.RELATIONSHIP_TYPE__USER_DEFINED:
            case RelationshipPackage.RELATIONSHIP_TYPE__STATUS:
            case RelationshipPackage.RELATIONSHIP_TYPE__STEREOTYPE:
            case RelationshipPackage.RELATIONSHIP_TYPE__CONSTRAINT:
            case RelationshipPackage.RELATIONSHIP_TYPE__LABEL:
            case RelationshipPackage.RELATIONSHIP_TYPE__OPPOSITE_LABEL:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case RelationshipPackage.RELATIONSHIP_TYPE__ROLES:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that
     * can be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors( Collection newChildDescriptors,
                                               Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getRelationshipType_Roles(),
                                                     RelationshipFactory.eINSTANCE.createRelationshipRole()));
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return RelationshipEditPlugin.INSTANCE;
    }

}
