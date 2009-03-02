/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.provider.MappingHelperItemProvider;
import com.metamatrix.modeler.compare.CompareFactory;
import com.metamatrix.modeler.compare.ComparePackage;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceType;

/**
 * This is the item provider adapter for a {@link com.metamatrix.modeler.compare.DifferenceDescriptor} object. <!-- begin-user-doc
 * --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class DifferenceDescriptorItemProvider extends MappingHelperItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public DifferenceDescriptorItemProvider( AdapterFactory adapterFactory ) {
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

            addTypePropertyDescriptor(object);
            addSkipPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_DifferenceDescriptor_type_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceDescriptor_type_feature", "_UI_DifferenceDescriptor_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 ComparePackage.eINSTANCE.getDifferenceDescriptor_Type(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Skip feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSkipPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_DifferenceDescriptor_skip_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceDescriptor_skip_feature", "_UI_DifferenceDescriptor_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 ComparePackage.eINSTANCE.getDifferenceDescriptor_Skip(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
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
            childrenFeatures.add(ComparePackage.eINSTANCE.getDifferenceDescriptor_PropertyDifferences());
        }
        return childrenFeatures;
    }

    /**
     * This returns DifferenceDescriptor.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/DifferenceDescriptor"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getText( Object object ) {
        DifferenceType labelValue = ((DifferenceDescriptor)object).getType();
        String label = labelValue == null ? null : labelValue.toString();
        return label == null || label.length() == 0 ? getString("_UI_DifferenceDescriptor_type") : //$NON-NLS-1$
        getString("_UI_DifferenceDescriptor_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(DifferenceDescriptor.class)) {
            case ComparePackage.DIFFERENCE_DESCRIPTOR__TYPE:
            case ComparePackage.DIFFERENCE_DESCRIPTOR__SKIP:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES:
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

        newChildDescriptors.add(createChildParameter(MappingPackage.eINSTANCE.getMappingHelper_Nested(),
                                                     CompareFactory.eINSTANCE.createDifferenceDescriptor()));

        newChildDescriptors.add(createChildParameter(ComparePackage.eINSTANCE.getDifferenceDescriptor_PropertyDifferences(),
                                                     CompareFactory.eINSTANCE.createPropertyDifference()));
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return CompareEditPlugin.INSTANCE;
    }

}
