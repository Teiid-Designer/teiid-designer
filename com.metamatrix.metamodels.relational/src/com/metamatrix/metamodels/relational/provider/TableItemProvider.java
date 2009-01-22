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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.util.RelationalUtil;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relational.Table} object. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class TableItemProvider extends ColumnSetItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public TableItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        // Repopulate the list of property descriptors every call
        if (itemPropertyDescriptors != null) {
            this.itemPropertyDescriptors.clear();
            this.itemPropertyDescriptors = null;
        }
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addSystemPropertyDescriptor(object);
            addCardinalityPropertyDescriptor(object);
            addSupportsUpdatePropertyDescriptor(object);
            addMaterializedPropertyDescriptor(object);
            addLogicalRelationshipsPropertyDescriptor(object);
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

            addSystemPropertyDescriptor(object);
            addCardinalityPropertyDescriptor(object);
            addSupportsUpdatePropertyDescriptor(object);
            addMaterializedPropertyDescriptor(object);
            addLogicalRelationshipsPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the System feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSystemPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_system_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("TableItemProvider._UI_PropertyDescriptor_description_System"), //$NON-NLS-1$ 
                                                                 // End customized code
                                                                 RelationalPackage.eINSTANCE.getTable_System(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the System feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSystemPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_system_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Table_system_feature", "_UI_Table_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getTable_System(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Cardinality feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addCardinalityPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_cardinality_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("TableItemProvider._UI_PropertyDescriptor_description_Cardinality"), //$NON-NLS-1$ 
                                                                 // End customized code
                                                                 RelationalPackage.eINSTANCE.getTable_Cardinality(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Cardinality feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addCardinalityPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_cardinality_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Table_cardinality_feature", "_UI_Table_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getTable_Cardinality(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Supports Update feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSupportsUpdatePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_supportsUpdate_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("TableItemProvider._UI_PropertyDescriptor_description_SupportsUpdate"), //$NON-NLS-1$ 
                                                                 // End customized code
                                                                 RelationalPackage.eINSTANCE.getTable_SupportsUpdate(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Supports Update feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSupportsUpdatePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_supportsUpdate_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Table_supportsUpdate_feature", "_UI_Table_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getTable_SupportsUpdate(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Materialized feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addMaterializedPropertyDescriptor( Object object ) {
        // If the Table is contained within a physical model then "materialized" cannot be set
        boolean isSettable = isVirtual(object);

        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_materialized_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("TableItemProvider._UI_PropertyDescriptor_description_Materialized"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 RelationalPackage.eINSTANCE.getTable_Materialized(),
                                                                 isSettable,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Materialized feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addMaterializedPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_materialized_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Table_materialized_feature", "_UI_Table_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getTable_Materialized(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Logical Relationships feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addLogicalRelationshipsPropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_Table_logicalRelationships_feature"), //$NON-NLS-1$
                                                                             getString("TableItemProvider._UI_PropertyDescriptor_description_LogicalRelationships"), //$NON-NLS-1$ 
                                                                             RelationalPackage.eINSTANCE.getTable_LogicalRelationships(),
                                                                             true, null, null, null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((Table)o).getLogicalRelationships();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                final Table table = (Table)object;
                final Resource model = table.eResource();
                final List results = RelationalUtil.findLogicalRelationshipEnds(model);
                return results;
            }
        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Logical Relationships feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addLogicalRelationshipsPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Table_logicalRelationships_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Table_logicalRelationships_feature", "_UI_Table_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getTable_LogicalRelationships(),
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
            childrenFeatures.add(RelationalPackage.eINSTANCE.getTable_AccessPatterns());
        }
        return childrenFeatures;
    }

    /**
     * This returns Table.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/Table"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        String label = ((Table)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_Table_type") : //$NON-NLS-1$
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

        switch (notification.getFeatureID(Table.class)) {
            case RelationalPackage.TABLE__SYSTEM:
            case RelationalPackage.TABLE__CARDINALITY:
            case RelationalPackage.TABLE__SUPPORTS_UPDATE:
            case RelationalPackage.TABLE__MATERIALIZED:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case RelationalPackage.TABLE__ACCESS_PATTERNS:
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

        newChildDescriptors.add(createChildParameter(RelationalPackage.eINSTANCE.getTable_AccessPatterns(),
                                                     RelationalFactory.eINSTANCE.createAccessPattern()));
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

    /**
     * Return whether the resource containing this object represents a virtual model. If we cannot determine if the resource is a
     * virtual model then false is returned
     * 
     * @generated NOT
     */
    protected boolean isVirtual( Object object ) {
        if (object instanceof Table) {
            Table table = (Table)object;
            try {
                ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation(table);
                return (ma != null && ma.getModelType().getValue() == ModelType.VIRTUAL);
            } catch (Exception e) {
                RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        return false;
    }

}
