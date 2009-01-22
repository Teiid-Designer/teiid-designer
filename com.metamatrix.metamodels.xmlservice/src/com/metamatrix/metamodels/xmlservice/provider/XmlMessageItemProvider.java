/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.xmlservice.XmlMessage;
import com.metamatrix.metamodels.xmlservice.XmlServiceMetamodelPlugin;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.xmlservice.XmlMessage} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class XmlMessageItemProvider extends XmlServiceComponentItemProvider {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    protected List virtualPropertyDescriptors = null;

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlMessageItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        // First time init of the descriptor lists
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            virtualPropertyDescriptors = new ArrayList();
            virtualPropertyDescriptors.addAll(itemPropertyDescriptors);
            addContentElementPropertyDescriptor(virtualPropertyDescriptors, object);
        }

        // if virtual return virtual list
        if (isVirtual(object)) {
            return virtualPropertyDescriptors;
        }
        return itemPropertyDescriptors;
    }

    /**
     * Return whether the resource containing this object represents a virtual model. If we cannot determine if the resource is a
     * virtual model then false is returned
     * 
     * @generated NOT
     */
    protected boolean isVirtual( Object object ) {
        if (object instanceof XmlMessage) {
            try {
                ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation((EObject)object);
                return (ma != null && ma.getModelType().getValue() == ModelType.VIRTUAL);
            } catch (Exception e) {
                XmlServiceMetamodelPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        return false;
    }

    /**
     * This adds a property descriptor for the Content Element feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addContentElementPropertyDescriptor( List descriptorList,
                                                        Object object ) {
        descriptorList.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                        getResourceLocator(),
                                                        getString("_UI_XmlMessage_contentElement_feature"), //$NON-NLS-1$
                                                        getString("_UI_PropertyDescriptor_description", "_UI_XmlMessage_contentElement_feature", "_UI_XmlMessage_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                        XmlServicePackage.eINSTANCE.getXmlMessage_ContentElement(),
                                                        true,
                                                        null,
                                                        null,
                                                        null));
    }

    /**
     * This returns XmlMessage.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/XmlMessage"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        String label = ((XmlMessage)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_XmlMessage_type") : //$NON-NLS-1$
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
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return XmlServicesEditPlugin.INSTANCE;
    }

}
