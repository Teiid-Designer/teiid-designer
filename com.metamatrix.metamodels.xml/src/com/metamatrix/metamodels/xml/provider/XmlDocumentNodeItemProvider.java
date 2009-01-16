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

package com.metamatrix.metamodels.xml.provider;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.xml.XmlDocumentEntity;
import com.metamatrix.metamodels.xml.XmlDocumentNode;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlElement;
import com.metamatrix.metamodels.xml.XmlRoot;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.xml.XmlDocumentNode} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class XmlDocumentNodeItemProvider extends XmlDocumentEntityItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XmlDocumentNodeItemProvider( AdapterFactory adapterFactory ) {
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

            addBuildStatePropertyDescriptor(object);
            addNamePropertyDescriptor(object);
            addExcludeFromDocumentPropertyDescriptor(object);
            addMinOccursPropertyDescriptor(object);
            addMaxOccursPropertyDescriptor(object);
            addXsdComponentPropertyDescriptor(object);
            addNamespacePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Build State feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addBuildStatePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlBuildable_buildState_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlBuildable_buildState_feature", "_UI_XmlBuildable_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlBuildable_BuildState(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Name feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addNamePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(new ItemPropertyDescriptor(
                                                               ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                               getString("_UI_XmlDocumentNode_name_feature"), //$NON-NLS-1$
                                                               //				 getString("_UI_PropertyDescriptor_description", "_UI_XmlDocumentNode_name_feature", "_UI_XmlDocumentNode_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                               // MyDefect:15019
                                                               getString("XmlDocumentNodeItemProvider._UI_PropertyDescriptor_description_Name"), //$NON-NLS-1$ 
                                                               XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_Name(), true,
                                                               ItemPropertyDescriptor.GENERIC_VALUE_IMAGE));
    }

    /**
     * This adds a property descriptor for the Name feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addNamePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_name_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlDocumentNode_name_feature", "_UI_XmlDocumentNode_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_Name(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Exclude From Document feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addExcludeFromDocumentPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_excludeFromDocument_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("XmlDocumentNodeItemProvider._UI_PropertyDescriptor_description_ExcludeFromDocument"), //$NON-NLS-1$ 
                                                                 // End customized code
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_ExcludeFromDocument(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Exclude From Document feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addExcludeFromDocumentPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_excludeFromDocument_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlDocumentNode_excludeFromDocument_feature", "_UI_XmlDocumentNode_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_ExcludeFromDocument(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Min Occurs feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addMinOccursPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_minOccurs_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("XmlDocumentNodeItemProvider._UI_PropertyDescriptor_description_MinOccurs"), //$NON-NLS-1$ 
                                                                 // End customized code
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_MinOccurs(),
                                                                 false,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Min Occurs feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addMinOccursPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_minOccurs_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlDocumentNode_minOccurs_feature", "_UI_XmlDocumentNode_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_MinOccurs(),
                                                                 false,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Max Occurs feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addMaxOccursPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_maxOccurs_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("XmlDocumentNodeItemProvider._UI_PropertyDescriptor_description_MaxOccurs"), //$NON-NLS-1$ 
                                                                 // End customized code
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_MaxOccurs(),
                                                                 false,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Max Occurs feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addMaxOccursPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_maxOccurs_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlDocumentNode_maxOccurs_feature", "_UI_XmlDocumentNode_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_MaxOccurs(),
                                                                 false,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Xsd Component feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addXsdComponentPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_xsdComponent_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("XmlDocumentNodeItemProvider._UI_PropertyDescriptor_description_XsdComponent"), //$NON-NLS-1$ 
                                                                 // End customized code
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_XsdComponent(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Xsd Component feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addXsdComponentPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_xsdComponent_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlDocumentNode_xsdComponent_feature", "_UI_XmlDocumentNode_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_XsdComponent(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Namespace feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addNamespacePropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_XmlDocumentNode_namespace_feature"), //$NON-NLS-1$
                                                                             getString("XmlDocumentNodeItemProvider._UI_PropertyDescriptor_description_Namespace"), //$NON-NLS-1$ 
                                                                             XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_Namespace(),
                                                                             true, null, null, null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((XmlDocumentNode)o).getNamespace();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                if (object instanceof XmlDocumentNode) {
                    final XmlDocumentNode docNode = (XmlDocumentNode)object;
                    return findAllowableNamespaces(docNode);
                }
                return super.getChoiceOfValues(object); // failsafe
            }
        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Namespace feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addNamespacePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XmlDocumentNode_namespace_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XmlDocumentNode_namespace_feature", "_UI_XmlDocumentNode_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 XmlDocumentPackage.eINSTANCE.getXmlDocumentNode_Namespace(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * Method used by {@link #addUniqueKeyPropertyDescriptor(Object)} to return the allowable foreign keys.
     * 
     * @param ukey
     * @return
     */
    protected List findAllowableNamespaces( final XmlDocumentNode docNode ) {
        XmlDocumentEntity node = docNode;
        final List results = new LinkedList();
        while (node != null) {
            if (node instanceof XmlElement) {
                final XmlElement element = (XmlElement)node;
                // Get all of the available namespaces on this node ...
                final List declaredNS = element.getDeclaredNamespaces();
                if (declaredNS != null && declaredNS.size() != 0) {
                    results.addAll(declaredNS);
                }
            }
            if (docNode instanceof XmlRoot) {
                node = null;
            } else {
                // Set to the parent node ...
                final EObject parent = node.eContainer();
                if (parent instanceof XmlDocumentEntity) {
                    node = (XmlDocumentEntity)parent;
                } else {
                    node = null;
                }
            }
        }
        results.add(0, null); // add 'null' so value can be unset to be the 'default' namespace (see defect 11339)
        return results;
    }

    /**
     * This returns XmlDocumentNode.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/XmlDocumentNode"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        String label = ((XmlDocumentNode)object).getName();
        return label == null || label.trim().length() == 0 ? getString("_UI_XmlDocumentNode_type") : //$NON-NLS-1$
        label;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getTextGen( Object object ) {
        String label = ((XmlDocumentNode)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_XmlDocumentNode_type") : //$NON-NLS-1$
        getString("_UI_XmlDocumentNode_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(XmlDocumentNode.class)) {
            case XmlDocumentPackage.XML_DOCUMENT_NODE__BUILD_STATE:
            case XmlDocumentPackage.XML_DOCUMENT_NODE__NAME:
            case XmlDocumentPackage.XML_DOCUMENT_NODE__EXCLUDE_FROM_DOCUMENT:
            case XmlDocumentPackage.XML_DOCUMENT_NODE__MIN_OCCURS:
            case XmlDocumentPackage.XML_DOCUMENT_NODE__MAX_OCCURS:
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
