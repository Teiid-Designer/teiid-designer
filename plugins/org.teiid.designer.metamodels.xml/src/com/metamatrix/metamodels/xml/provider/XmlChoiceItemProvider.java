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
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.xml.XmlChoice} object. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class XmlChoiceItemProvider extends XmlContainerNodeItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlChoiceItemProvider( AdapterFactory adapterFactory ) {
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

            addDefaultErrorModePropertyDescriptor(object);
            addDefaultOptionPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Default Error Mode feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addDefaultErrorModePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlChoice_defaultErrorMode_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("XmlDocumentNodeItemProvider._UI_PropertyDescriptor_description_ErrorMode"), //$NON-NLS-1$ 
                                                                 // End customized code
                                                                 XmlDocumentPackage.eINSTANCE.getXmlChoice_DefaultErrorMode(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Default Error Mode feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDefaultErrorModePropertyDescriptorGen( Object object ) { // NO_UCD
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlChoice_defaultErrorMode_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlChoice_defaultErrorMode_feature", "_UI_XmlChoice_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlChoice_DefaultErrorMode(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Default Option feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addDefaultOptionPropertyDescriptor( Object object ) {
        // Start customized code
        itemPropertyDescriptors.add(new ItemPropertyDescriptor(
                                                               ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                               getResourceLocator(),
                                                               getString("_UI_XmlChoice_defaultOption_feature"), //$NON-NLS-1$
                                                               getString("XmlDocumentNodeItemProvider._UI_PropertyDescriptor_description_DefaultOption"), //$NON-NLS-1$ 
                                                               XmlDocumentPackage.eINSTANCE.getXmlChoice_DefaultOption(), true,
                                                               null, null, null) {

            /**
             * @see org.eclipse.emf.edit.provider.ItemPropertyDescriptor#getChoiceOfValues(java.lang.Object)
             * @since 4.2
             */
            @Override
            public Collection getChoiceOfValues( Object obj ) {
                final XmlChoice choice = (XmlChoice)obj;
                // Return only those choice options ...
                return choice.getOrderedChoiceOptions();
            }

            /**
             * @see org.eclipse.emf.edit.provider.ItemPropertyDescriptor#getPropertyValue(java.lang.Object)
             * @since 4.2
             */
            @Override
            public Object getPropertyValue( Object arg0 ) {
                return ((XmlChoice)arg0).getDefaultOption();
            }
        });
        // End customized code
    }

    /**
     * This adds a property descriptor for the Default Option feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDefaultOptionPropertyDescriptorGen( Object object ) { // NO_UCD
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlChoice_defaultOption_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlChoice_defaultOption_feature", "_UI_XmlChoice_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlChoice_DefaultOption(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This returns XmlChoice.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/XmlChoice"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getText( Object object ) {
        String label = ((XmlChoice)object).getChoiceCriteria();
        return label == null || label.length() == 0 ? getString("_UI_XmlChoice_type") : //$NON-NLS-1$
        getString("_UI_XmlChoice_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(XmlChoice.class)) {
            case XmlDocumentPackage.XML_CHOICE__DEFAULT_ERROR_MODE:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
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
