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
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relationship.RelationshipRole} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class RelationshipRoleItemProvider extends RelationshipEntityItemProvider {
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
    public RelationshipRoleItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addStereotypePropertyDescriptor(object);
            addOrderedPropertyDescriptor(object);
            addUniquePropertyDescriptor(object);
            addNavigablePropertyDescriptor(object);
            addLowerBoundPropertyDescriptor(object);
            addUpperBoundPropertyDescriptor(object);
            addConstraintPropertyDescriptor(object);
            addOppositeRolePropertyDescriptor(object);
            addIncludeTypesPropertyDescriptor(object);
            addExcludeTypesPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Stereotype feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addStereotypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_stereotype_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_stereotype_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Stereotype(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_stereotype_feature_category"), //$NON-NLS-1$;
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
                                                                 getString("_UI_RelationshipRole_stereotype_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_stereotype_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Stereotype(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Ordered feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addOrderedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_ordered_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_ordered_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Ordered(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_ordered_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Ordered feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addOrderedPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_ordered_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_ordered_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Ordered(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Unique feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addUniquePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_unique_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_unique_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Unique(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_unique_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Unique feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUniquePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_unique_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_unique_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Unique(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Navigable feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addNavigablePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_navigable_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_navigable_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Navigable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_navigable_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Navigable feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addNavigablePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_navigable_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_navigable_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Navigable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Lower Bound feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addLowerBoundPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_lowerBound_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_lowerBound_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_LowerBound(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_lowerBound_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Lower Bound feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addLowerBoundPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_lowerBound_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_lowerBound_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_LowerBound(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Upper Bound feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addUpperBoundPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_upperBound_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_upperBound_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_UpperBound(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_upperBound_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Upper Bound feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUpperBoundPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_upperBound_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_upperBound_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_UpperBound(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
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
                                                                 getString("_UI_RelationshipRole_constraint_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_constraint_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Constraint(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_constraint_feature_category"), //$NON-NLS-1$;
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
                                                                 getString("_UI_RelationshipRole_constraint_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_constraint_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_Constraint(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Include Types feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addIncludeTypesPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_includeTypes_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_includeTypes_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_IncludeTypes(),
                                                                 true,
                                                                 null,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_includeTypes_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Include Types feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addIncludeTypesPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_includeTypes_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_includeTypes_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_IncludeTypes(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Exclude Types feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addExcludeTypesPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_excludeTypes_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_excludeTypes_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_ExcludeTypes(),
                                                                 true,
                                                                 null,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_excludeTypes_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Exclude Types feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addExcludeTypesPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_excludeTypes_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_excludeTypes_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_ExcludeTypes(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Opposite Role feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addOppositeRolePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_oppositeRole_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_oppositeRole_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_OppositeRole(),
                                                                 false,
                                                                 null,
                                                                 // start customized code
                                                                 getString("_UI_RelationshipRole_oppositeRole_feature_category"), //$NON-NLS-1$;
                                                                 // end customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Opposite Role feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addOppositeRolePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_RelationshipRole_oppositeRole_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_RelationshipRole_oppositeRole_feature", "_UI_RelationshipRole_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationshipPackage.eINSTANCE.getRelationshipRole_OppositeRole(),
                                                                 false,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This returns RelationshipRole.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public Object getImage( Object object ) {
        if (((RelationshipRole)object).getRelationshipType() != null && ((RelationshipRole)object).isSourceRole()) {
            return getResourceLocator().getImage("full/obj16/RelationshipSourceRole"); //$NON-NLS-1$
        }
        return getResourceLocator().getImage("full/obj16/RelationshipRole"); //$NON-NLS-1$
    }

    /**
     * This returns RelationshipRole.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Object getImageGen( Object object ) {
        return getResourceLocator().getImage("full/obj16/RelationshipRole"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        final RelationshipRole role = (RelationshipRole)object;
        final RelationshipType type = role.getRelationshipType();
        String sourceOrTargetLabel = null;
        if (type.getSourceRole().equals(role)) {
            sourceOrTargetLabel = getString("_UI_RelationshipRole_default_source_label"); //$NON-NLS-1$
        } else {
            sourceOrTargetLabel = getString("_UI_RelationshipRole_default_target_label"); //$NON-NLS-1$
        }
        String label = role.getName();
        if (label == null || label.trim().length() == 0) {
            label = getString("_UI_RelationshipRole_default_name"); //$NON-NLS-1$
        }
        final Object[] params = new Object[] {sourceOrTargetLabel, label};
        final String roleName = RelationshipMetamodelPlugin.Util.getString("_UI_RelationshipRole_label", params); //$NON-NLS-1$
        return roleName;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getTextGen( Object object ) {
        String label = ((RelationshipRole)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_RelationshipRole_type") : //$NON-NLS-1$
        getString("_UI_RelationshipRole_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(RelationshipRole.class)) {
            case RelationshipPackage.RELATIONSHIP_ROLE__STEREOTYPE:
            case RelationshipPackage.RELATIONSHIP_ROLE__ORDERED:
            case RelationshipPackage.RELATIONSHIP_ROLE__UNIQUE:
            case RelationshipPackage.RELATIONSHIP_ROLE__NAVIGABLE:
            case RelationshipPackage.RELATIONSHIP_ROLE__LOWER_BOUND:
            case RelationshipPackage.RELATIONSHIP_ROLE__UPPER_BOUND:
            case RelationshipPackage.RELATIONSHIP_ROLE__CONSTRAINT:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
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
