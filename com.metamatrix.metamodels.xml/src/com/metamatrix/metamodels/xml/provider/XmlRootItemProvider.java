/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import com.metamatrix.metamodels.xml.XmlRoot;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.xml.XmlRoot} object. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class XmlRootItemProvider extends XmlElementItemProvider {

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlRootItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * @see org.eclipse.emf.edit.provider.ItemProviderAdapter#createItemPropertyDescriptor(org.eclipse.emf.common.notify.AdapterFactory,
     *      org.eclipse.emf.common.util.ResourceLocator, java.lang.String, java.lang.String,
     *      org.eclipse.emf.ecore.EStructuralFeature, boolean, java.lang.Object, java.lang.String, java.lang.String[])
     * @since 4.3
     */
    @Override
    protected ItemPropertyDescriptor createItemPropertyDescriptor( AdapterFactory adapterFactory,
                                                                   ResourceLocator resourceLocator,
                                                                   String displayName,
                                                                   String description,
                                                                   EStructuralFeature feature,
                                                                   boolean isSettable,
                                                                   Object staticImage,
                                                                   String category,
                                                                   String[] filterFlags ) {
        return new XmlElementPropertyDescriptor(adapterFactory, resourceLocator, displayName, description, feature, isSettable,
                                                staticImage, category, filterFlags) {

            /**
             * @see com.metamatrix.metamodels.xml.provider.XmlElementItemProvider.XmlElementPropertyDescriptor#filterElement(org.eclipse.xsd.XSDElementDeclaration)
             * @since 4.3
             */
            @Override
            protected boolean filterElement( XSDElementDeclaration element ) {
                return !(element.getContainer() instanceof XSDSchema);
            }
        };
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
     * This returns XmlRoot.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/XmlRoot"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        String label = ((XmlRoot)object).getName();
        return label == null || label.trim().length() == 0 ? getString("_UI_XmlRoot_type") : //$NON-NLS-1$
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
        return XmlDocumentEditPlugin.INSTANCE;
    }

}
