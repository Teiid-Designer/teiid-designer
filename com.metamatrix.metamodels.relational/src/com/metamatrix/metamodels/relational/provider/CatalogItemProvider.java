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

package com.metamatrix.metamodels.relational.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relational.Catalog} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class CatalogItemProvider extends RelationalEntityItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public CatalogItemProvider( AdapterFactory adapterFactory ) {
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
            childrenFeatures.add(RelationalPackage.eINSTANCE.getCatalog_Schemas());
            childrenFeatures.add(RelationalPackage.eINSTANCE.getCatalog_Procedures());
            childrenFeatures.add(RelationalPackage.eINSTANCE.getCatalog_Indexes());
            childrenFeatures.add(RelationalPackage.eINSTANCE.getCatalog_Tables());
            childrenFeatures.add(RelationalPackage.eINSTANCE.getCatalog_LogicalRelationships());
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
     * This returns Catalog.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/Catalog"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        String label = ((Catalog)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_Catalog_type") : //$NON-NLS-1$
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

        switch (notification.getFeatureID(Catalog.class)) {
            case RelationalPackage.CATALOG__SCHEMAS:
            case RelationalPackage.CATALOG__PROCEDURES:
            case RelationalPackage.CATALOG__INDEXES:
            case RelationalPackage.CATALOG__TABLES:
            case RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS:
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

        newChildDescriptors.add(createChildParameter(RelationalPackage.eINSTANCE.getCatalog_Schemas(),
                                                     RelationalFactory.eINSTANCE.createSchema()));

        newChildDescriptors.add(createChildParameter(RelationalPackage.eINSTANCE.getCatalog_Procedures(),
                                                     RelationalFactory.eINSTANCE.createProcedure()));

        newChildDescriptors.add(createChildParameter(RelationalPackage.eINSTANCE.getCatalog_Indexes(),
                                                     RelationalFactory.eINSTANCE.createIndex()));

        newChildDescriptors.add(createChildParameter(RelationalPackage.eINSTANCE.getCatalog_Tables(),
                                                     RelationalFactory.eINSTANCE.createView()));

        newChildDescriptors.add(createChildParameter(RelationalPackage.eINSTANCE.getCatalog_Tables(),
                                                     RelationalFactory.eINSTANCE.createBaseTable()));

        newChildDescriptors.add(createChildParameter(RelationalPackage.eINSTANCE.getCatalog_LogicalRelationships(),
                                                     RelationalFactory.eINSTANCE.createLogicalRelationship()));
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
