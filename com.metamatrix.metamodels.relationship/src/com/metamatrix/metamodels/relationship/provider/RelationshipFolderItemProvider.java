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

package com.metamatrix.metamodels.relationship.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.relationship.RelationshipFactory;
import com.metamatrix.metamodels.relationship.RelationshipFolder;
import com.metamatrix.metamodels.relationship.RelationshipPackage;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relationship.RelationshipFolder} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class RelationshipFolderItemProvider extends RelationshipEntityItemProvider {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationshipFolderItemProvider( AdapterFactory adapterFactory ) {
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

        }
        return itemPropertyDescriptors;
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
            childrenFeatures.add(RelationshipPackage.eINSTANCE.getPlaceholderReferenceContainer_Placeholders());
            childrenFeatures.add(RelationshipPackage.eINSTANCE.getRelationshipContainer_OwnedRelationships());
            childrenFeatures.add(RelationshipPackage.eINSTANCE.getRelationshipFolder_OwnedRelationshipTypes());
            childrenFeatures.add(RelationshipPackage.eINSTANCE.getRelationshipFolder_OwnedRelationshipFolders());
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature( Object object,
                                                  Object child ) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This returns RelationshipFolder.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/RelationshipFolder"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        final RelationshipFolder folder = (RelationshipFolder)object;
        String label = folder.getName();
        if (label == null || label.trim().length() == 0) {
            label = getString("_UI_RelationshipFolder_type"); //$NON-NLS-1$
        }
        return label;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getTextGen( Object object ) {
        String label = ((RelationshipFolder)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_RelationshipFolder_type") : //$NON-NLS-1$
        getString("_UI_RelationshipFolder_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(RelationshipFolder.class)) {
            case RelationshipPackage.RELATIONSHIP_FOLDER__PLACEHOLDERS:
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS:
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES:
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that
     * can be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    protected void collectNewChildDescriptors( Collection newChildDescriptors,
                                               Object object ) {
        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getPlaceholderReferenceContainer_Placeholders(),
                                                     RelationshipFactory.eINSTANCE.createUriReference()));

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getPlaceholderReferenceContainer_Placeholders(),
                                                     RelationshipFactory.eINSTANCE.createFileReference()));

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getRelationshipContainer_OwnedRelationships(),
                                                     RelationshipFactory.eINSTANCE.createRelationship()));

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getRelationshipFolder_OwnedRelationshipTypes(),
                                                     RelationshipFactory.eINSTANCE.createRelationshipType()));

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getRelationshipFolder_OwnedRelationshipFolders(),
                                                     RelationshipFactory.eINSTANCE.createRelationshipFolder()));

        super.collectNewChildDescriptors(newChildDescriptors, object);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that
     * can be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void collectNewChildDescriptorsGen( Collection newChildDescriptors,
                                                  Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getPlaceholderReferenceContainer_Placeholders(),
                                                     RelationshipFactory.eINSTANCE.createUriReference()));

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getPlaceholderReferenceContainer_Placeholders(),
                                                     RelationshipFactory.eINSTANCE.createFileReference()));

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getRelationshipContainer_OwnedRelationships(),
                                                     RelationshipFactory.eINSTANCE.createRelationship()));

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getRelationshipFolder_OwnedRelationshipTypes(),
                                                     RelationshipFactory.eINSTANCE.createRelationshipType()));

        newChildDescriptors.add(createChildParameter(RelationshipPackage.eINSTANCE.getRelationshipFolder_OwnedRelationshipFolders(),
                                                     RelationshipFactory.eINSTANCE.createRelationshipFolder()));
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return RelationshipEditPlugin.INSTANCE;
    }

}
