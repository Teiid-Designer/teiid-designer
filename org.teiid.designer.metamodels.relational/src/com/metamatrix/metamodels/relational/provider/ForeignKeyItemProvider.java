/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.util.RelationalUtil;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relational.ForeignKey} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class ForeignKeyItemProvider extends RelationshipItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ForeignKeyItemProvider( AdapterFactory adapterFactory ) {
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

            addForeignKeyMultiplicityPropertyDescriptor(object);
            addPrimaryKeyMultiplicityPropertyDescriptor(object);
            addColumnsPropertyDescriptor(object);
            addUniqueKeyPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Foreign Key Multiplicity feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addForeignKeyMultiplicityPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ForeignKey_foreignKeyMultiplicity_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ForeignKey_foreignKeyMultiplicity_feature", "_UI_ForeignKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getForeignKey_ForeignKeyMultiplicity(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Primary Key Multiplicity feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addPrimaryKeyMultiplicityPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ForeignKey_primaryKeyMultiplicity_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ForeignKey_primaryKeyMultiplicity_feature", "_UI_ForeignKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getForeignKey_PrimaryKeyMultiplicity(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * Method used by {@link #addColumnsPropertyDescriptor(Object)} to return the allowable columns.
     * 
     * @param ukey
     * @return
     */
    protected List findAllowableColumns( final ForeignKey fkey ) {
        final Table owner = fkey.getTable();
        return owner.getColumns();
    }

    /**
     * Method used by {@link #addUniqueKeyPropertyDescriptor(Object)} to return the allowable foreign keys.
     * 
     * @param ukey
     * @return
     */
    protected List findAllowableUniqueKeys( final ForeignKey fkey ) {
        final Resource model = fkey.eResource();
        final List results = RelationalUtil.findUniqueKeys(model);
        results.add(0, null); // add 'null' so value can be unset
        return results;
    }

    /**
     * This adds a property descriptor for the Columns feature.
     * 
     * @generated NOT
     */
    protected void addColumnsPropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_ForeignKey_columns_feature"), //$NON-NLS-1$
                                                                             getString("_UI_PropertyDescriptor_description", "_UI_ForeignKey_columns_feature", "_UI_ForeignKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                             RelationalPackage.eINSTANCE.getForeignKey_Columns(),
                                                                             true, null, null, null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((ForeignKey)o).getColumns();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                if (object instanceof ForeignKey) {
                    final ForeignKey fkey = (ForeignKey)object;
                    return findAllowableColumns(fkey);
                }
                return super.getChoiceOfValues(object); // failsafe
            }

        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Columns feature.
     * 
     * @generated
     */
    protected void addColumnsPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ForeignKey_columns_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ForeignKey_columns_feature", "_UI_ForeignKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getForeignKey_Columns(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Unique Key feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addUniqueKeyPropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_ForeignKey_uniqueKey_feature"), //$NON-NLS-1$
                                                                             getString("_UI_PropertyDescriptor_description", "_UI_ForeignKey_uniqueKey_feature", "_UI_ForeignKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                             RelationalPackage.eINSTANCE.getForeignKey_UniqueKey(),
                                                                             true, null, null, null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((ForeignKey)o).getUniqueKey();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                // Find all of the unique keys in this model ...
                final ForeignKey fkey = (ForeignKey)object;
                return findAllowableUniqueKeys(fkey);
            }

        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Unique Key feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUniqueKeyPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ForeignKey_uniqueKey_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ForeignKey_uniqueKey_feature", "_UI_ForeignKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getForeignKey_UniqueKey(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This returns ForeignKey.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/ForeignKey"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        String label = ((ForeignKey)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_ForeignKey_type") : //$NON-NLS-1$
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

        switch (notification.getFeatureID(ForeignKey.class)) {
            case RelationalPackage.FOREIGN_KEY__FOREIGN_KEY_MULTIPLICITY:
            case RelationalPackage.FOREIGN_KEY__PRIMARY_KEY_MULTIPLICITY:
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
