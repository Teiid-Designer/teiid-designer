/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.core.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.core.Annotation} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class AnnotationItemProvider
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
    public AnnotationItemProvider(AdapterFactory adapterFactory) {
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

            addDescriptionPropertyDescriptor(object);
            addKeywordsPropertyDescriptor(object);
            addAnnotatedObjectPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Description feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addDescriptionPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_Annotation_description_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_Annotation_description_feature", "_UI_Annotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getAnnotation_Description(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Keywords feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addKeywordsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_Annotation_keywords_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_Annotation_keywords_feature", "_UI_Annotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getAnnotation_Keywords(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Annotated Object feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    protected void addAnnotatedObjectPropertyDescriptor(Object object)
    {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_Annotation_annotatedObject_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_Annotation_annotatedObject_feature", "_UI_Annotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getAnnotation_AnnotatedObject(),
                 // Start customized code                 
                 false,
                 // End customized code                 
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Annotated Object feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addAnnotatedObjectPropertyDescriptorGen(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_Annotation_annotatedObject_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_Annotation_annotatedObject_feature", "_UI_Annotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 CorePackage.eINSTANCE.getAnnotation_AnnotatedObject(),
                 true,
                 null,
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
            childrenFeatures.add(CorePackage.eINSTANCE.getAnnotation_Tags());
            childrenFeatures.add(CorePackage.eINSTANCE.getAnnotation_ExtensionObject());
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature(Object object, Object child) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This returns Annotation.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/Annotation"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String getText(Object object) {
        String label = ((Annotation)object).getDescription();
        return label == null || label.length() == 0 ?
            getString("_UI_Annotation_type") : //$NON-NLS-1$
            getString("_UI_Annotation_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(Annotation.class)) {
            case CorePackage.ANNOTATION__DESCRIPTION:
            case CorePackage.ANNOTATION__KEYWORDS:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case CorePackage.ANNOTATION__TAGS:
            case CorePackage.ANNOTATION__EXTENSION_OBJECT:
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
                (CorePackage.eINSTANCE.getAnnotation_Tags(),
                 EcoreFactory.eINSTANCE.create(EcorePackage.eINSTANCE.getEStringToStringMapEntry())));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 ExtensionFactory.eINSTANCE.createXClass()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 ExtensionFactory.eINSTANCE.createXPackage()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 ExtensionFactory.eINSTANCE.createXAttribute()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 ExtensionFactory.eINSTANCE.createXEnum()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 ExtensionFactory.eINSTANCE.createXEnumLiteral()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEObject()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEAttribute()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEAnnotation()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEClass()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEDataType()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEEnum()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEEnumLiteral()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEFactory()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEOperation()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEPackage()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEParameter()));

        newChildDescriptors.add
            (createChildParameter
                (CorePackage.eINSTANCE.getAnnotation_ExtensionObject(),
                 EcoreFactory.eINSTANCE.createEReference()));
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
