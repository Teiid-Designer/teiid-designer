/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.provider.EPackageItemProvider;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.metamodels.core.util.CoreUtil;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.core.extension.XPackage} object. <!-- begin-user-doc
 * --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class XPackageItemProvider extends EPackageItemProvider {

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XPackageItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
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

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            CoreUtil.removePropertyDescriptor(this.itemPropertyDescriptors, new EStructuralFeature[] {
                EcorePackage.eINSTANCE.getEPackage_EFactoryInstance(), EcorePackage.eINSTANCE.getEPackage_NsPrefix(),
                EcorePackage.eINSTANCE.getEPackage_NsURI()});
        }
        return itemPropertyDescriptors;
    }

    /**
     * This returns XPackage.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return overlayImage(object, getResourceLocator().getImage("full/obj16/XPackage")); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        final XPackage xpkg = (XPackage)object;
        String label = xpkg.getName();
        if (label == null || label.length() == 0) {
            label = getString("_UI_XPackage_type"); //$NON-NLS-1$
        }
        return label;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getTextGen( Object object ) { // NO_UCD
        String label = ((XPackage)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_XPackage_type") : //$NON-NLS-1$
        getString("_UI_XPackage_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        newChildDescriptors.add(createChildParameter(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
                                                     ExtensionFactory.eINSTANCE.createXClass()));

        newChildDescriptors.add(createChildParameter(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
                                                     ExtensionFactory.eINSTANCE.createXEnum()));

        // newChildDescriptors.add
        // (createChildParameter
        // (EcorePackage.eINSTANCE.getEPackage_ESubpackages(),
        // ExtensionFactory.eINSTANCE.createXPackage()));
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

        newChildDescriptors.add(createChildParameter(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
                                                     ExtensionFactory.eINSTANCE.createXClass()));

        newChildDescriptors.add(createChildParameter(EcorePackage.Literals.EPACKAGE__ECLASSIFIERS,
                                                     ExtensionFactory.eINSTANCE.createXEnum()));

        newChildDescriptors.add(createChildParameter(EcorePackage.Literals.EPACKAGE__ESUBPACKAGES,
                                                     ExtensionFactory.eINSTANCE.createXPackage()));
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
