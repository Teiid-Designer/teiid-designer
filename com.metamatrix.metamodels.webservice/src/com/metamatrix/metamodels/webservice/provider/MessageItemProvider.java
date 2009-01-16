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

package com.metamatrix.metamodels.webservice.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.WebServiceFactory;
import com.metamatrix.metamodels.webservice.WebServiceMetamodelPlugin;
import com.metamatrix.metamodels.webservice.WebServicePackage;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.webservice.Message} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class MessageItemProvider extends WebServiceComponentItemProvider {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MessageItemProvider( AdapterFactory adapterFactory ) {
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

            // web service input/output objects
            addContentElementPropertyDescriptor(object);
            // Defect 18906 - removing simple data type picker/property from the
            // addContentComplexTypePropertyDescriptor(object);
            // addContentSimpleTypePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Content Element feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addContentElementPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(new MessageItemPropertyDescriptor(
                                                                      ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                      getResourceLocator(),
                                                                      getString("_UI_Message_contentElement_feature"), //$NON-NLS-1$
                                                                      getString("_UI_PropertyDescriptor_description", "_UI_Message_contentElement_feature", "_UI_Message_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                      WebServicePackage.eINSTANCE.getMessage_ContentElement(),
                                                                      true, null, null, null));
    }

    /**
     * This adds a property descriptor for the Content Complex Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addContentComplexTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(new MessageItemPropertyDescriptor(
                                                                      ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                      getResourceLocator(),
                                                                      getString("_UI_Message_contentComplexType_feature"), //$NON-NLS-1$
                                                                      getString("_UI_PropertyDescriptor_description", "_UI_Message_contentComplexType_feature", "_UI_Message_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                      WebServicePackage.eINSTANCE.getMessage_ContentComplexType(),
                                                                      true, null, null, null));
    }

    /**
     * This adds a property descriptor for the Content Simple Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addContentSimpleTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Message_contentSimpleType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Message_contentSimpleType_feature", "_UI_Message_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 WebServicePackage.eINSTANCE.getMessage_ContentSimpleType(),
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
            childrenFeatures.add(WebServicePackage.eINSTANCE.getMessage_Samples());
        }
        return childrenFeatures;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getText( Object object ) {
        String label = ((Message)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_Message_type") : //$NON-NLS-1$
        getString("_UI_Message_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(Message.class)) {
            case WebServicePackage.MESSAGE__SAMPLES:
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

        newChildDescriptors.add(createChildParameter(WebServicePackage.eINSTANCE.getMessage_Samples(),
                                                     WebServiceFactory.eINSTANCE.createSampleMessages()));
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return WebServicesEditPlugin.INSTANCE;
    }

    class MessageItemPropertyDescriptor extends ItemPropertyDescriptor {

        public MessageItemPropertyDescriptor( AdapterFactory adapterFactory,
                                              ResourceLocator resourceLocator,
                                              String displayName,
                                              String description,
                                              EStructuralFeature feature,
                                              boolean isSettable,
                                              Object staticImage,
                                              String category,
                                              String[] filterFlags ) {
            super(adapterFactory, resourceLocator, displayName, description, feature, isSettable, staticImage, category,
                  filterFlags);
        }

        @Override
        public Collection getChoiceOfValues( Object object ) {
            return getGlobalElementDeclarations(object);
        }

        /*
         * Walk the workspace looking for all global Element Declarations.
         * Must load any unloaded XSDs.
         */
        protected Collection getGlobalElementDeclarations( final Object object ) {
            final ArrayList result = new ArrayList();
            if (object instanceof EObject) {
                final ResourceSet resourceSet = ((EObject)object).eResource().getResourceSet();
                for (final Iterator it = resourceSet.getResources().iterator(); it.hasNext();) {
                    final Object o = it.next();
                    if (o instanceof Resource) {
                        final Resource resource = (Resource)o;
                        try {
                            // We only care about XSDResources
                            if (resource instanceof XSDResourceImpl) {
                                // If the resource is not loaded, load first
                                if (!resource.isLoaded()) {
                                    resource.load(resourceSet.getLoadOptions());
                                }

                                // Only get the immediate roots as we only care about global Elements
                                final XSDSchema schema = ((XSDResourceImpl)resource).getSchema();
                                if (schema != null) {
                                    final Iterator roots = schema.getContents().iterator();
                                    while (roots.hasNext()) {
                                        final Object element = roots.next();
                                        // Include all global non abstract elements in the result
                                        if (element instanceof XSDElementDeclaration
                                            && !((XSDElementDeclaration)element).isAbstract()) {
                                            result.add(element);
                                        }

                                    } // while
                                }
                            }
                        } catch (IOException ioe) {
                            WebServiceMetamodelPlugin.Util.log(ioe);
                        }
                    }
                }
            }

            return result;
        }

    }

}
