/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.provider;


import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcPackage;

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

/**
 * This is the item provider adapter for a {@link com.metamatrix.modeler.jdbc.JdbcDriver} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class JdbcDriverItemProvider
    extends ItemProviderAdapter
    implements
        IEditingDomainItemProvider,
        IStructuredItemContentProvider,
        ITreeItemContentProvider,
        IItemLabelProvider,
        IItemPropertySource {
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcDriverItemProvider(AdapterFactory adapterFactory) {
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
            addUrlSyntaxPropertyDescriptor(object);
            addJarFileUrisPropertyDescriptor(object);
            addAvailableDriverClassNamesPropertyDescriptor(object);
            addPreferredDriverClassNamePropertyDescriptor(object);
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
                 getString("_UI_JdbcDriver_name_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcDriver_name_feature", "_UI_JdbcDriver_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcDriver_Name(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Url Syntax feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addUrlSyntaxPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcDriver_urlSyntax_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcDriver_urlSyntax_feature", "_UI_JdbcDriver_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcDriver_UrlSyntax(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Jar File Uris feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addJarFileUrisPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcDriver_jarFileUris_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcDriver_jarFileUris_feature", "_UI_JdbcDriver_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcDriver_JarFileUris(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Available Driver Class Names feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addAvailableDriverClassNamesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcDriver_availableDriverClassNames_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcDriver_availableDriverClassNames_feature", "_UI_JdbcDriver_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcDriver_AvailableDriverClassNames(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Preferred Driver Class Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addPreferredDriverClassNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcDriver_preferredDriverClassName_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcDriver_preferredDriverClassName_feature", "_UI_JdbcDriver_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcDriver_PreferredDriverClassName(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This returns JdbcDriver.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/JdbcDriver"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getText(Object object) {
        String label = ((JdbcDriver)object).getName();
        return label == null || label.trim().length() == 0 ?
            getString("_UI_JdbcDriver_type") : //$NON-NLS-1$
            label; 
    }
    
    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTextGen(Object object) {
        String label = ((JdbcDriver)object).getName();
        return label == null || label.length() == 0 ?
            getString("_UI_JdbcDriver_type") : //$NON-NLS-1$
            getString("_UI_JdbcDriver_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(JdbcDriver.class)) {
            case JdbcPackage.JDBC_DRIVER__NAME:
            case JdbcPackage.JDBC_DRIVER__URL_SYNTAX:
            case JdbcPackage.JDBC_DRIVER__JAR_FILE_URIS:
            case JdbcPackage.JDBC_DRIVER__AVAILABLE_DRIVER_CLASS_NAMES:
            case JdbcPackage.JDBC_DRIVER__PREFERRED_DRIVER_CLASS_NAME:
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
        return JdbcEditPlugin.INSTANCE;
    }

}
