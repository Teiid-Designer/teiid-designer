/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.provider;


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

import com.metamatrix.vdb.edit.manifest.ManifestFactory;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ProblemMarker;
import com.metamatrix.vdb.edit.manifest.Severity;

/**
 * This is the item provider adapter for a {@link com.metamatrix.vdb.edit.manifest.ProblemMarker} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class ProblemMarkerItemProvider
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
    public ProblemMarkerItemProvider(AdapterFactory adapterFactory) {
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

            addSeverityPropertyDescriptor(object);
            addMessagePropertyDescriptor(object);
            addTargetPropertyDescriptor(object);
            addTargetUriPropertyDescriptor(object);
            addCodePropertyDescriptor(object);
            addStackTracePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Severity feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addSeverityPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ProblemMarker_severity_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ProblemMarker_severity_feature", "_UI_ProblemMarker_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ManifestPackage.eINSTANCE.getProblemMarker_Severity(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Message feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addMessagePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ProblemMarker_message_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ProblemMarker_message_feature", "_UI_ProblemMarker_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ManifestPackage.eINSTANCE.getProblemMarker_Message(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Target feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTargetPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ProblemMarker_target_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ProblemMarker_target_feature", "_UI_ProblemMarker_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ManifestPackage.eINSTANCE.getProblemMarker_Target(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Target Uri feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addTargetUriPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ProblemMarker_targetUri_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ProblemMarker_targetUri_feature", "_UI_ProblemMarker_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ManifestPackage.eINSTANCE.getProblemMarker_TargetUri(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Code feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addCodePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ProblemMarker_code_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ProblemMarker_code_feature", "_UI_ProblemMarker_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ManifestPackage.eINSTANCE.getProblemMarker_Code(),
                 true,
                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Stack Trace feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addStackTracePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_ProblemMarker_stackTrace_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_ProblemMarker_stackTrace_feature", "_UI_ProblemMarker_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 ManifestPackage.eINSTANCE.getProblemMarker_StackTrace(),
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
            childrenFeatures.add(ManifestPackage.eINSTANCE.getProblemMarker_Children());
        }
        return childrenFeatures;
    }

    /**
     * This returns ProblemMarker.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public Object getImage(Object object) {
        final ProblemMarker marker = (ProblemMarker)object;
        final int severityValue = marker.getSeverity().getValue();        
        switch( severityValue ) {
            case Severity.ERROR :
                return getResourceLocator().getImage("full/obj16/ProblemMarker_Error"); //$NON-NLS-1$
            case Severity.INFO :
                return getResourceLocator().getImage("full/obj16/ProblemMarker_Info"); //$NON-NLS-1$
            case Severity.WARNING :
                return getResourceLocator().getImage("full/obj16/ProblemMarker_Warning"); //$NON-NLS-1$
        }
        return getResourceLocator().getImage("full/obj16/ProblemMarker"); //$NON-NLS-1$
    }

    /**
     * This returns ProblemMarker.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getImageGen(Object object) {
        return getResourceLocator().getImage("full/obj16/ProblemMarker"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getText(Object object) {
        final ProblemMarker marker = (ProblemMarker)object;
        final String label = marker.getMessage();
        return label == null || label.trim().length() == 0 
            ?
            getString("_UI_ProblemMarker_type")     //$NON-NLS-1$ 
            : 
            label;
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTextGen(Object object) {
        Severity labelValue = ((ProblemMarker)object).getSeverity();
        String label = labelValue == null ? null : labelValue.toString();
        return label == null || label.length() == 0 ?
            getString("_UI_ProblemMarker_type") : //$NON-NLS-1$
            getString("_UI_ProblemMarker_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(ProblemMarker.class)) {
            case ManifestPackage.PROBLEM_MARKER__SEVERITY:
            case ManifestPackage.PROBLEM_MARKER__MESSAGE:
            case ManifestPackage.PROBLEM_MARKER__TARGET:
            case ManifestPackage.PROBLEM_MARKER__TARGET_URI:
            case ManifestPackage.PROBLEM_MARKER__CODE:
            case ManifestPackage.PROBLEM_MARKER__STACK_TRACE:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case ManifestPackage.PROBLEM_MARKER__CHILDREN:
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
                (ManifestPackage.eINSTANCE.getProblemMarker_Children(),
                 ManifestFactory.eINSTANCE.createProblemMarker()));
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return ManifestEditPlugin.INSTANCE;
    }

}
