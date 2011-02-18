/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml.provider;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.util.XSDConstants;
import com.metamatrix.metamodels.xml.XmlDocumentFactory;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.xml.XmlElement} object. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class XmlElementItemProvider extends XmlBaseElementItemProvider {

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlElementItemProvider( final AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This adds a property descriptor for the Recursive feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addRecursivePropertyDescriptor( final Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlElement_recursive_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlElement_recursive_feature", "_UI_XmlElement_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlElement_Recursive(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Value feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addValuePropertyDescriptor( final Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlValueHolder_value_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlValueHolder_value_feature", "_UI_XmlValueHolder_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlValueHolder_Value(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Value Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addValueTypePropertyDescriptor( final Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlValueHolder_valueType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlValueHolder_valueType_feature", "_UI_XmlValueHolder_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlValueHolder_ValueType(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that can
     * be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    protected void collectNewChildDescriptors( final Collection newChildDescriptors,
                                               final Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlElement()));

        // We don't want root elements below anything but the fragment/document.
        // newChildDescriptors.add
        // (createChildParameter
        // (XmlDocumentPackage.eINSTANCE.getXmlElementHolder_Elements(),
        // XmlDocumentFactory.eINSTANCE.createXmlRoot()));

        // Not supported in 4.0. Per defect 10240
        // newChildDescriptors.add
        // (createChildParameter
        // (XmlDocumentPackage.eINSTANCE.getXmlElementHolder_Elements(),
        // XmlDocumentFactory.eINSTANCE.createXmlFragmentUse()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlElement_Attributes(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlAttribute()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlElement_DeclaredNamespaces(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlNamespace()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlSequence()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlChoice()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlAll()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlCommentHolder_Comments(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlComment()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getProcessingInstructionHolder_ProcessingInstructions(),
                                                     XmlDocumentFactory.eINSTANCE.createProcessingInstruction()));

    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that can
     * be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void collectNewChildDescriptorsGen( final Collection newChildDescriptors, // NO_UCD
                                                  final Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlCommentHolder_Comments(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlComment()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getProcessingInstructionHolder_ProcessingInstructions(),
                                                     XmlDocumentFactory.eINSTANCE.createProcessingInstruction()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlElement()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlRoot()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlFragmentUse()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlSequence()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlAll()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlChoice()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlElement_Attributes(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlAttribute()));

        newChildDescriptors.add(createChildParameter(XmlDocumentPackage.eINSTANCE.getXmlElement_DeclaredNamespaces(),
                                                     XmlDocumentFactory.eINSTANCE.createXmlNamespace()));
    }

    /**
     * @see org.eclipse.emf.edit.provider.ItemProviderAdapter#createItemPropertyDescriptor(org.eclipse.emf.common.notify.AdapterFactory,
     *      org.eclipse.emf.common.util.ResourceLocator, java.lang.String, java.lang.String,
     *      org.eclipse.emf.ecore.EStructuralFeature, boolean, java.lang.Object, java.lang.String, java.lang.String[])
     * @since 4.3
     */
    @Override
    protected ItemPropertyDescriptor createItemPropertyDescriptor( final AdapterFactory adapterFactory,
                                                                   final ResourceLocator resourceLocator,
                                                                   final String displayName,
                                                                   final String description,
                                                                   final EStructuralFeature feature,
                                                                   final boolean isSettable,
                                                                   final Object staticImage,
                                                                   final String category,
                                                                   final String[] filterFlags ) {
        return new XmlElementPropertyDescriptor(adapterFactory, resourceLocator, displayName, description, feature, isSettable,
                                                staticImage, category, filterFlags);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature( final Object object,
                                                  final Object child ) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Collection getChildrenFeatures( final Object object ) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(XmlDocumentPackage.eINSTANCE.getXmlCommentHolder_Comments());
            childrenFeatures.add(XmlDocumentPackage.eINSTANCE.getProcessingInstructionHolder_ProcessingInstructions());
            childrenFeatures.add(XmlDocumentPackage.eINSTANCE.getXmlEntityHolder_Entities());
            childrenFeatures.add(XmlDocumentPackage.eINSTANCE.getXmlElement_Attributes());
            childrenFeatures.add(XmlDocumentPackage.eINSTANCE.getXmlElement_DeclaredNamespaces());
        }
        return childrenFeatures;
    }

    /**
     * This returns XmlElement.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( final Object object ) {
        return getResourceLocator().getImage("full/obj16/XmlElement"); //$NON-NLS-1$
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public List getPropertyDescriptors( final Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addValuePropertyDescriptor(object);
            addValueTypePropertyDescriptor(object);
            addRecursivePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
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

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( final Object object ) {
        final String label = ((XmlElement)object).getName();
        return label == null || label.trim().length() == 0 ? getString("_UI_XmlElement_type") : //$NON-NLS-1$
        label;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getTextGen( final Object object ) {
        final String label = ((XmlElement)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_XmlElement_type") : //$NON-NLS-1$
        getString("_UI_XmlElement_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating a viewer
     * notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void notifyChanged( final Notification notification ) {
        updateChildren(notification);

        switch (notification.getFeatureID(XmlElement.class)) {
            case XmlDocumentPackage.XML_ELEMENT__VALUE:
            case XmlDocumentPackage.XML_ELEMENT__VALUE_TYPE:
            case XmlDocumentPackage.XML_ELEMENT__RECURSIVE:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case XmlDocumentPackage.XML_ELEMENT__COMMENTS:
            case XmlDocumentPackage.XML_ELEMENT__PROCESSING_INSTRUCTIONS:
            case XmlDocumentPackage.XML_ELEMENT__ENTITIES:
            case XmlDocumentPackage.XML_ELEMENT__ATTRIBUTES:
            case XmlDocumentPackage.XML_ELEMENT__DECLARED_NAMESPACES:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
                return;
        }
        super.notifyChanged(notification);
    }

    // ===========================================================================================================================
    // Inner Class

    protected class XmlElementPropertyDescriptor extends ItemPropertyDescriptor {

        // =======================================================================================================================
        // Constructors (XmlElementPropertyDescriptor)

        /**
         * @since 4.3
         */
        protected XmlElementPropertyDescriptor( final AdapterFactory adapterFactory,
                                                final ResourceLocator resourceLocator,
                                                final String displayName,
                                                final String description,
                                                final EStructuralFeature feature,
                                                final boolean isSettable,
                                                final Object staticImage,
                                                final String category,
                                                final String[] filterFlags ) {
            super(adapterFactory, resourceLocator, displayName, description, feature, isSettable, staticImage, category,
                  filterFlags);
        }

        // =======================================================================================================================
        // Controller Methods (XmlElementPropertyDescriptor)

        /**
         * Always returns false. Subclasses should override to control feature-specific behavior.
         * 
         * @param element An XSD Element that tentatively will appear in the list of choices returned by
         *        {@link #getComboBoxObjects(Object)}.
         * @return True If the specified object should be filtered from the list of choices returned by
         *         {@link #getComboBoxObjects(Object)} (i.e., it shouldn't appear in the list).
         * @since 4.3
         */
        protected boolean filterElement( final XSDElementDeclaration element ) {
            return false;
        }

        /**
         * @see org.eclipse.emf.edit.provider.ItemPropertyDescriptor#getComboBoxObjects(java.lang.Object)
         * @since 4.3
         */
        @Override
        protected Collection getComboBoxObjects( final Object object ) {
            final Collection objs = super.getComboBoxObjects(object);
            if (objs != null && feature == XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_XsdComponent()) for (final Iterator iter = objs.iterator(); iter.hasNext();) {
                final Object obj = iter.next();
                if (obj instanceof XSDElementDeclaration) {
                    final XSDElementDeclaration elem = (XSDElementDeclaration)obj;
                    if (XSDConstants.isSchemaForSchemaNamespace(elem.getTargetNamespace()) || filterElement(elem)) iter.remove();
                }
            }
            return objs;
        }
    }
}
