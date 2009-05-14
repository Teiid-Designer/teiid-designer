/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelImport;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.core.ModelImport} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelImportItemProvider
    extends ItemProviderAdapter
    implements
        IEditingDomainItemProvider,
        IStructuredItemContentProvider,
        ITreeItemContentProvider,
        IItemLabelProvider,
        IItemPropertySource {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelImportItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public List getPropertyDescriptors(Object object) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addNamePropertyDescriptor(object);
            addPathPropertyDescriptor(object);
            addModelLocationPropertyDescriptor(object);
            addUuidPropertyDescriptor(object);
            addModelTypePropertyDescriptor(object);
            addPrimaryMetamodelUriPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ModelImport_name_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ModelImport_name_feature", "_UI_ModelImport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getModelImport_Name(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Path feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addPathPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ModelImport_path_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ModelImport_path_feature", "_UI_ModelImport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getModelImport_Path(),
                 false,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Model Location feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addModelLocationPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ModelImport_modelLocation_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ModelImport_modelLocation_feature", "_UI_ModelImport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getModelImport_ModelLocation(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Uuid feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addUuidPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ModelImport_uuid_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ModelImport_uuid_feature", "_UI_ModelImport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getModelImport_Uuid(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Model Type feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addModelTypePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ModelImport_modelType_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ModelImport_modelType_feature", "_UI_ModelImport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getModelImport_ModelType(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Primary Metamodel Uri feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addPrimaryMetamodelUriPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ModelImport_primaryMetamodelUri_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ModelImport_primaryMetamodelUri_feature", "_UI_ModelImport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getModelImport_PrimaryMetamodelUri(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This returns ModelImport.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/ModelImport"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getText(Object object) {
    	// Start customized code
        final ModelImport mdlImport = (ModelImport)object;
        final String path = mdlImport.getPath();
        String label = path;
        if ( label == null || label.trim().length() == 0 ) {
            label = getString("_UI_ModelImport_type"); //$NON-NLS-1$
        }
        return label;
    	// End customized code
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTextGen(Object object) {
        String label = ((ModelImport)object).getName();
        return label == null || label.length() == 0 ?
            getString("_UI_ModelImport_type") : //$NON-NLS-1$
            getString("_UI_ModelImport_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void notifyChanged(Notification notification) {
        updateChildren(notification);

        switch (notification.getFeatureID(ModelImport.class)) {
            case CorePackage.MODEL_IMPORT__NAME:
            case CorePackage.MODEL_IMPORT__PATH:
            case CorePackage.MODEL_IMPORT__MODEL_LOCATION:
            case CorePackage.MODEL_IMPORT__UUID:
            case CorePackage.MODEL_IMPORT__MODEL_TYPE:
            case CorePackage.MODEL_IMPORT__PRIMARY_METAMODEL_URI:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s
     * describing all of the children that can be created under this object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors(Collection newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return CoreEditPlugin.INSTANCE;
    }

}
