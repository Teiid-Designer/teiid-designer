/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.teiid.designer.metamodels.relational.LogicalRelationshipEnd;
import org.teiid.designer.metamodels.relational.RelationalPackage;

/**
 * This is the item provider adapter for a {@link org.teiid.designer.metamodels.relational.LogicalRelationshipEnd} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 *
 * @since 8.0
 */
public class LogicalRelationshipEndItemProvider extends RelationalEntityItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public LogicalRelationshipEndItemProvider( AdapterFactory adapterFactory ) {
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

            addMultiplicityPropertyDescriptor(object);
            addTablePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Multiplicity feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addMultiplicityPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_LogicalRelationshipEnd_multiplicity_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_LogicalRelationshipEnd_multiplicity_feature", "_UI_LogicalRelationshipEnd_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getLogicalRelationshipEnd_Multiplicity(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Table feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addTablePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_LogicalRelationshipEnd_table_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_LogicalRelationshipEnd_table_feature", "_UI_LogicalRelationshipEnd_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getLogicalRelationshipEnd_Table(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This returns LogicalRelationshipEnd.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/LogicalRelationshipEnd"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getText( Object object ) {
        String label = ((LogicalRelationshipEnd)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_LogicalRelationshipEnd_type") : //$NON-NLS-1$
        getString("_UI_LogicalRelationshipEnd_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(LogicalRelationshipEnd.class)) {
            case RelationalPackage.LOGICAL_RELATIONSHIP_END__MULTIPLICITY:
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
        return RelationalEditPlugin.INSTANCE;
    }

}
