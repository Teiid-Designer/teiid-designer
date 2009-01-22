/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history.provider;


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

import com.metamatrix.metamodels.history.HistoryCriteria;
import com.metamatrix.metamodels.history.HistoryPackage;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.history.HistoryCriteria} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class HistoryCriteriaItemProvider
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
    public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public HistoryCriteriaItemProvider(AdapterFactory adapterFactory) {
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

            addFromDatePropertyDescriptor(object);
            addToDatePropertyDescriptor(object);
            addUserPropertyDescriptor(object);
            addIncludeLabelsPropertyDescriptor(object);
            addOnlyLabelsPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the From Date feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addFromDatePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_HistoryCriteria_fromDate_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_HistoryCriteria_fromDate_feature", "_UI_HistoryCriteria_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 HistoryPackage.eINSTANCE.getHistoryCriteria_FromDate(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the To Date feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addToDatePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_HistoryCriteria_toDate_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_HistoryCriteria_toDate_feature", "_UI_HistoryCriteria_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 HistoryPackage.eINSTANCE.getHistoryCriteria_ToDate(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the User feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addUserPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_HistoryCriteria_user_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_HistoryCriteria_user_feature", "_UI_HistoryCriteria_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 HistoryPackage.eINSTANCE.getHistoryCriteria_User(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Include Labels feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludeLabelsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_HistoryCriteria_includeLabels_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_HistoryCriteria_includeLabels_feature", "_UI_HistoryCriteria_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 HistoryPackage.eINSTANCE.getHistoryCriteria_IncludeLabels(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Only Labels feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addOnlyLabelsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_HistoryCriteria_onlyLabels_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_HistoryCriteria_onlyLabels_feature", "_UI_HistoryCriteria_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 HistoryPackage.eINSTANCE.getHistoryCriteria_OnlyLabels(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This returns HistoryCriteria.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/HistoryCriteria"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText(Object object) {
        String label = ((HistoryCriteria)object).getFromDate();
        return label == null || label.length() == 0 ?
            getString("_UI_HistoryCriteria_type") : //$NON-NLS-1$
            getString("_UI_HistoryCriteria_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(HistoryCriteria.class)) {
            case HistoryPackage.HISTORY_CRITERIA__FROM_DATE:
            case HistoryPackage.HISTORY_CRITERIA__TO_DATE:
            case HistoryPackage.HISTORY_CRITERIA__USER:
            case HistoryPackage.HISTORY_CRITERIA__INCLUDE_LABELS:
            case HistoryPackage.HISTORY_CRITERIA__ONLY_LABELS:
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
        return HistoryEditPlugin.INSTANCE;
    }

}
