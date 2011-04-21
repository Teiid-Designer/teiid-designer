/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.transformation.MappingClass} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class MappingClassItemProvider extends MappingClassObjectItemProvider {

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MappingClassItemProvider( AdapterFactory adapterFactory ) {
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

            addRecursivePropertyDescriptor(object);
            addRecursionAllowedPropertyDescriptor(object);
            addRecursionCriteriaPropertyDescriptor(object);
            addRecursionLimitPropertyDescriptor(object);
            addRecursionLimitErrorModePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Recursive feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addRecursivePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_MappingClass_recursive_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_MappingClass_recursive_feature", "_UI_MappingClass_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getMappingClass_Recursive(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Recursion Allowed feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addRecursionAllowedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_MappingClass_recursionAllowed_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_MappingClass_recursionAllowed_feature", "_UI_MappingClass_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getMappingClass_RecursionAllowed(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Recursion Criteria feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addRecursionCriteriaPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_MappingClass_recursionCriteria_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_MappingClass_recursionCriteria_feature", "_UI_MappingClass_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getMappingClass_RecursionCriteria(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Recursion Limit feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addRecursionLimitPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_MappingClass_recursionLimit_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_MappingClass_recursionLimit_feature", "_UI_MappingClass_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getMappingClass_RecursionLimit(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Recursion Limit Error Mode feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addRecursionLimitErrorModePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_MappingClass_recursionLimitErrorMode_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_MappingClass_recursionLimitErrorMode_feature", "_UI_MappingClass_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getMappingClass_RecursionLimitErrorMode(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
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
            childrenFeatures.add(TransformationPackage.eINSTANCE.getMappingClass_Columns());
            childrenFeatures.add(TransformationPackage.eINSTANCE.getMappingClass_InputSet());
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature( Object object,
                                                  Object child ) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This returns MappingClass.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/MappingClass"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        String label = ((MappingClass)object).getName();
        return label == null || label.trim().length() == 0 ? getString("_UI_MappingClass_type") : //$NON-NLS-1$
        label;
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

        switch (notification.getFeatureID(MappingClass.class)) {
            case TransformationPackage.MAPPING_CLASS__RECURSIVE:
            case TransformationPackage.MAPPING_CLASS__RECURSION_ALLOWED:
            case TransformationPackage.MAPPING_CLASS__RECURSION_CRITERIA:
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT:
            case TransformationPackage.MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case TransformationPackage.MAPPING_CLASS__COLUMNS:
            case TransformationPackage.MAPPING_CLASS__INPUT_SET:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that
     * can be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    protected void collectNewChildDescriptors( Collection newChildDescriptors,
                                               Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getMappingClass_Columns(),
                                                     TransformationFactory.eINSTANCE.createMappingClassColumn()));

        final MappingClass mappingClass = (MappingClass)object;
        if (mappingClass.getInputSet() == null) {
            newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getMappingClass_InputSet(),
                                                         TransformationFactory.eINSTANCE.createInputSet()));
        }

    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that
     * can be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void collectNewChildDescriptorsGen( Collection newChildDescriptors,
                                                  Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getMappingClass_Columns(),
                                                     TransformationFactory.eINSTANCE.createMappingClassColumn()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getMappingClass_InputSet(),
                                                     TransformationFactory.eINSTANCE.createInputSet()));
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return TransformationEditPlugin.INSTANCE;
    }

}
