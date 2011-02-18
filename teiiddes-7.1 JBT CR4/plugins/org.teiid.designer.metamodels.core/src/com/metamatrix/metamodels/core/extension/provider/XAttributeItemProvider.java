/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.provider.EAttributeItemProvider;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XEnum;
import com.metamatrix.metamodels.core.util.CoreUtil;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.core.extension.XAttribute} object. <!-- begin-user-doc
 * --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class XAttributeItemProvider extends EAttributeItemProvider {

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XAttributeItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            // Remove the EAttribute child ...
            CoreUtil.removePropertyDescriptor(this.itemPropertyDescriptors, new EStructuralFeature[] {
                EcorePackage.eINSTANCE.getEStructuralFeature_EContainingClass(), EcorePackage.eINSTANCE.getEAttribute_ID(),
                EcorePackage.eINSTANCE.getEAttribute_EAttributeType(), EcorePackage.eINSTANCE.getEStructuralFeature_Transient(),
                EcorePackage.eINSTANCE.getEStructuralFeature_Volatile(), EcorePackage.eINSTANCE.getETypedElement_Unique()});
        }
        return itemPropertyDescriptors;
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List getPropertyDescriptorsGen( Object object ) { // NO_UCD
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

        }
        return itemPropertyDescriptors;
    }

    protected void removePropertyDescriptor( final EStructuralFeature feature ) { // NO_UCD
        if (feature == null) {
            return;
        }
        final Iterator iter = this.itemPropertyDescriptors.iterator();
        while (iter.hasNext()) {
            final ItemPropertyDescriptor desc = (ItemPropertyDescriptor)iter.next();
            if (feature.equals(desc.getFeature(null))) {
                iter.remove();
            }
        }
    }

    /**
     * This adds a property descriptor for the EType feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    protected void addETypePropertyDescriptor( Object object ) {
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getString("_UI_ETypedElement_eType_feature"), //$NON-NLS-1$
                                                                             getString("_UI_PropertyDescriptor_description", "_UI_ETypedElement_eType_feature", "_UI_ETypedElement_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                             EcorePackage.eINSTANCE.getETypedElement_EType(),
                                                                             true) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((XAttribute)o).getEAttributeType();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                final XAttribute xattribute = (XAttribute)object;
                final List results = new ArrayList();
                results.add(EcorePackage.eINSTANCE.getEBoolean());
                results.add(EcorePackage.eINSTANCE.getEByte());
                results.add(EcorePackage.eINSTANCE.getEChar());
                results.add(EcorePackage.eINSTANCE.getEDouble());
                results.add(EcorePackage.eINSTANCE.getEFloat());
                results.add(EcorePackage.eINSTANCE.getEInt());
                results.add(EcorePackage.eINSTANCE.getELong());
                results.add(EcorePackage.eINSTANCE.getEShort());
                results.add(EcorePackage.eINSTANCE.getEString());

                // Add all enumerations in the XPackage containing the
                // XClass that owns this XAttribute ...
                final Iterator iter = xattribute.getEContainingClass().getEPackage().getEClassifiers().iterator();
                while (iter.hasNext()) {
                    final EClassifier eclassifier = (EClassifier)iter.next();
                    if (eclassifier instanceof XEnum) {
                        results.add(eclassifier);
                    }
                }

                return results;
            }
        };
        itemPropertyDescriptors.add(descriptor);
    }

    /**
     * This returns XAttribute.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public Object getImage( Object object ) {
        return getComposedImage(object, getResourceLocator().getImage("full/obj16/XAttribute")); //$NON-NLS-1$
    }

    /**
     * This returns XAttribute.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Object getImageGen( Object object ) { // NO_UCD
        return overlayImage(object, getResourceLocator().getImage("full/obj16/XAttribute")); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        final XAttribute xattrib = (XAttribute)object;
        String label = xattrib.getName();
        if (label == null || label.length() == 0) {
            label = getString("_UI_XAttribute_type"); //$NON-NLS-1$
        }
        // Add the type information ...
        final EDataType type = xattrib.getEAttributeType();
        if (type != null) {
            label = label + " : " + type.getName(); //$NON-NLS-1$
        }
        return label;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getTextGen( Object object ) { // NO_UCD
        String label = ((XAttribute)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_XAttribute_type") : //$NON-NLS-1$
        getString("_UI_XAttribute_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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
     * @generated NOT
     */
    @Override
    protected void collectNewChildDescriptors( Collection newChildDescriptors,
                                               Object object ) {
        // super.collectNewChildDescriptors(newChildDescriptors, object);
    }

    /**
     * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created under this
     * object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void collectNewChildDescriptorsGen( Collection newChildDescriptors, // NO_UCD
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
        return ExtensionEditPlugin.INSTANCE;
    }

}
