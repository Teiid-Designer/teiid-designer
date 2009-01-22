/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.xmlservice.XmlInput} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class XmlInputItemProvider extends XmlMessageItemProvider {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlInputItemProvider( AdapterFactory adapterFactory ) {
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

            addTypePropertyDescriptor(object);
        }
        if (isVirtual(object)) {
            return virtualPropertyDescriptors;
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addTypePropertyDescriptor( Object object ) {
        ItemPropertyDescriptor descr = createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                    getResourceLocator(),
                                                                    getString("_UI_XmlInput_type_feature"), //$NON-NLS-1$
                                                                    getString("_UI_PropertyDescriptor_description", "_UI_XmlInput_type_feature", "_UI_XmlInput_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                    XmlServicePackage.eINSTANCE.getXmlInput_Type(),
                                                                    true,
                                                                    null,
                                                                    null,
                                                                    null);
        itemPropertyDescriptors.add(descr);
        virtualPropertyDescriptors.add(descr);
    }

    /**
     * This returns XmlInput.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/XmlInput"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        // Start customized code
        final XmlInput param = (XmlInput)object;
        String label = param.getName();
        if (label == null || label.length() == 0) {
            label = getString("_UI_XmlInput_type"); //$NON-NLS-1$
        }
        // Add the datatype ...
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param, true);
        final EObject dt = param.getType();
        final String dtName = dtMgr.getName(dt);
        if (dt != null && dtName != null && dtName.trim().length() != 0) {
            label = label + " : " + dtName; //$NON-NLS-1$
        }
        return label;
        // End customized code
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
