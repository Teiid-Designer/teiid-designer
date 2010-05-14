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
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.mapping.MappingFactory;
import com.metamatrix.modeler.compare.ComparePackage;
import com.metamatrix.modeler.compare.DifferenceReport;

/**
 * This is the item provider adapter for a {@link com.metamatrix.modeler.compare.DifferenceReport} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class DifferenceReportItemProvider
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
    public DifferenceReportItemProvider(AdapterFactory adapterFactory) {
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

            addTitlePropertyDescriptor(object);
            addTotalAdditionsPropertyDescriptor(object);
            addTotalDeletionsPropertyDescriptor(object);
            addTotalChangesPropertyDescriptor(object);
            addAnalysisTimePropertyDescriptor(object);
            addSourceUriPropertyDescriptor(object);
            addResultUriPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Title feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTitlePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_DifferenceReport_title_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceReport_title_feature", "_UI_DifferenceReport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ComparePackage.eINSTANCE.getDifferenceReport_Title(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Total Additions feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTotalAdditionsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_DifferenceReport_totalAdditions_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceReport_totalAdditions_feature", "_UI_DifferenceReport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ComparePackage.eINSTANCE.getDifferenceReport_TotalAdditions(),
                 true,
                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Total Deletions feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTotalDeletionsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_DifferenceReport_totalDeletions_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceReport_totalDeletions_feature", "_UI_DifferenceReport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ComparePackage.eINSTANCE.getDifferenceReport_TotalDeletions(),
                 true,
                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Total Changes feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTotalChangesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_DifferenceReport_totalChanges_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceReport_totalChanges_feature", "_UI_DifferenceReport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ComparePackage.eINSTANCE.getDifferenceReport_TotalChanges(),
                 true,
                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Analysis Time feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addAnalysisTimePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_DifferenceReport_analysisTime_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceReport_analysisTime_feature", "_UI_DifferenceReport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ComparePackage.eINSTANCE.getDifferenceReport_AnalysisTime(),
                 true,
                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Source Uri feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addSourceUriPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_DifferenceReport_sourceUri_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceReport_sourceUri_feature", "_UI_DifferenceReport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ComparePackage.eINSTANCE.getDifferenceReport_SourceUri(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Result Uri feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addResultUriPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_DifferenceReport_resultUri_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_DifferenceReport_resultUri_feature", "_UI_DifferenceReport_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ComparePackage.eINSTANCE.getDifferenceReport_ResultUri(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Collection getChildrenFeatures(Object object) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(ComparePackage.eINSTANCE.getDifferenceReport_Mapping());
        }
        return childrenFeatures;
    }

    /**
     * This returns DifferenceReport.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/DifferenceReport"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getText(Object object) {
        final DifferenceReport report = (DifferenceReport)object;
        String label = report.getTitle();
        if ( label == null || label.trim().length() == 0 ) {
            label = getString("_UI_DifferenceReport_type"); //$NON-NLS-1$
        }
        return label;
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTextGen(Object object) {
        String label = ((DifferenceReport)object).getTitle();
        return label == null || label.length() == 0 ?
            getString("_UI_DifferenceReport_type") : //$NON-NLS-1$
            getString("_UI_DifferenceReport_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(DifferenceReport.class)) {
            case ComparePackage.DIFFERENCE_REPORT__TITLE:
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_ADDITIONS:
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_DELETIONS:
            case ComparePackage.DIFFERENCE_REPORT__TOTAL_CHANGES:
            case ComparePackage.DIFFERENCE_REPORT__ANALYSIS_TIME:
            case ComparePackage.DIFFERENCE_REPORT__SOURCE_URI:
            case ComparePackage.DIFFERENCE_REPORT__RESULT_URI:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case ComparePackage.DIFFERENCE_REPORT__MAPPING:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
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

        newChildDescriptors.add
            (createChildParameter
                (ComparePackage.eINSTANCE.getDifferenceReport_Mapping(),
                 MappingFactory.eINSTANCE.createMapping()));

        newChildDescriptors.add
            (createChildParameter
                (ComparePackage.eINSTANCE.getDifferenceReport_Mapping(),
                 MappingFactory.eINSTANCE.createMappingRoot()));
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return CompareEditPlugin.INSTANCE;
    }

}
