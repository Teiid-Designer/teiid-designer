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
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.util.RelationalUtil;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relational.UniqueKey} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class UniqueKeyItemProvider extends RelationalEntityItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public UniqueKeyItemProvider( AdapterFactory adapterFactory ) {
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

            addColumnsPropertyDescriptor(object);
            addForeignKeysPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Columns feature.
     */
    protected void addColumnsPropertyDescriptor( Object object ) {
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getString("_UI_UniqueKey_columns_feature"), //$NON-NLS-1$
                                                                             getString("_UI_PropertyDescriptor_description", "_UI_UniqueKey_columns_feature", "_UI_UniqueKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                             RelationalPackage.eINSTANCE.getUniqueKey_Columns(),
                                                                             true) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((UniqueKey)o).getColumns();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                if (object instanceof UniqueKey) {
                    final UniqueKey ukey = (UniqueKey)object;
                    return findAllowableColumns(ukey);
                }
                return super.getChoiceOfValues(object); // failsafe
            }

        };
        itemPropertyDescriptors.add(descriptor);
    }

    /**
     * Method used by {@link #addColumnsPropertyDescriptor(Object)} to return the allowable columns.
     * 
     * @param ukey
     * @return
     */
    protected List findAllowableColumns( final UniqueKey ukey ) {
        final Table owner = ukey.getTable();
        return owner.getColumns();
    }

    /**
     * Method used by {@link #addForeignKeysPropertyDescriptor(Object)} to return the allowable foreign keys.
     * 
     * @param ukey
     * @return
     */
    protected List findAllowableForeignKeys( final UniqueKey ukey ) {
        final Resource model = ukey.eResource();
        final List results = RelationalUtil.findForeignKeys(model);
        return results;
    }

    /**
     * This adds a property descriptor for the Foreign Keys feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addForeignKeysPropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_UniqueKey_foreignKeys_feature"), //$NON-NLS-1$
                                                                             getString("_UI_PropertyDescriptor_description", "_UI_UniqueKey_foreignKeys_feature", "_UI_UniqueKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                             RelationalPackage.eINSTANCE.getUniqueKey_ForeignKeys(),
                                                                             true, null, null, null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((UniqueKey)o).getForeignKeys();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                // Find all of the foreign keys in this model ...
                final UniqueKey ukey = (UniqueKey)object;
                return findAllowableForeignKeys(ukey);
            }

        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Foreign Keys feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addForeignKeysPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_UniqueKey_foreignKeys_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_UniqueKey_foreignKeys_feature", "_UI_UniqueKey_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getUniqueKey_ForeignKeys(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This returns UniqueKey.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/UniqueKey"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        String label = ((UniqueKey)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_UniqueKey_type") : //$NON-NLS-1$
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
